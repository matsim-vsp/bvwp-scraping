package org.tub.vsp.bvwp;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.text.NumberFormat;
import java.text.ParseException;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.function.Predicate;

public class JSoupUtils {
    private static final Logger logger = LogManager.getLogger(JSoupUtils.class);

    public static Optional<Integer> getFirstRowIndexWithText(Element table, String text) {
        int counter = 0;
        for (Element tr : table.select("tr")) {
            if (tr.text().contains(text)) {
                return Optional.of(counter);
            }
            counter++;
        }
        return Optional.empty();
    }

    public static Optional<Integer> getFirstRowIndexWithTextInCol(Element table, String text, int colIndex) {
        int counter = 0;
        for (Element tr : table.select("tr")) {
            if (tr.child(colIndex).text().contains(text)) {
                return Optional.of(counter);
            }
            counter++;
        }
        return Optional.empty();
    }

    public static String getTextFromRowAndCol(Element table, int rowIndex, int colIndex) {
        return table.select("tr")
                    .get(rowIndex)
                    .select("td")
                    .get(colIndex)
                    .text();
    }

    public static Optional<String> getTextFromFirstRowWithKeyContainedInCol(Element table, String key,
                                                                            int keyColIndex, int valueColIndex) {
        return firstRowWithKeyContainedInCol(table, key, keyColIndex)
                .map(r -> r.child(valueColIndex).text());
    }

    public static Optional<Element> firstRowWithKeyContainedInCol(Element table, String key, int colIndex) {
        return table.select("tr")
                    .stream()
                    .filter(r -> r.child(colIndex).text().contains(key))
                    .findFirst();
    }

    public static Optional<Element> firstRowWithKeyMatchesInCol(Element table, String key, int colIndex) {
        return table.select("tr")
                    .stream()
                    .filter(r -> r.child(colIndex).text().equals(key))
                    .findFirst();
    }

    public static Optional<Element> firstRowWithKeyInCol(Element table, String key, int colIndex) {
        return table.select("tr")
                    .stream()
                    .filter(r -> r.child(colIndex).text().equals(key))
                    .findFirst();
    }

    public static Double parseDouble(String s) throws ParseException {
        if ("-".equals(s) || s == null) {
            return 0.;
        }
        return NumberFormat.getInstance(Locale.GERMANY)
                           .parse(s)
                           .doubleValue();
    }

    public static Double parseDoubleOrElseNull(String s) {
        try {
            return parseDouble(s);
        } catch (ParseException e) {
            return null;
        }
    }

    public static Double parseDoubleOrElseThrow(String s) {
        try {
            return parseDouble(s);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
    }

    public static Optional<Element> getTableByKeyAndContainedText(Document document, String cssClass,
                                                                  String textToContain) {
        return getTableByCssKeyAndPredicate(document, cssClass, (e) -> anyRowContainsText(e, textToContain));
    }

    public static Optional<Element> getTableByCssKeyAndPredicate(Document document, String key,
                                                                 Predicate<Element> predicate) {
        List<Element> list = document.select(key)
                                     .stream()
                                     .filter(predicate)
                                     .toList();

        if (list.isEmpty()) {
            logger.warn("Could not find any element with key {}.", key);
            return Optional.empty();
        } else if (list.size() > 1) {
            logger.warn("Found more than one element with key {}.", key);
            return Optional.empty();
        }

        return Optional.of(list.getFirst());
    }

    private static boolean anyRowContainsText(Element element, String textToContain) {
        return element.select("tr")
                      .stream()
                      .anyMatch(r -> r.text().contains(textToContain));
    }
}
