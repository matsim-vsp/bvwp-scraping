package org.tub.vsp;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.text.NumberFormat;
import java.text.ParseException;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

public class JSoupUtils {
    private static final Logger logger = LogManager.getLogger(JSoupUtils.class);

    public static String getTextFromRowAndCol(Element table, int rowIndex, int colIndex) {
        return table.select("tr")
                    .get(rowIndex)
                    .select("td")
                    .get(colIndex)
                    .text();
    }

    public static Optional<Element> firstRowWithKeyContainedInCol(Element table, String key, int colIndex) {
        return table.select("tr")
                    .stream()
                    .filter(r -> r.child(colIndex)
                                  .text()
                                  .contains(key))
                    .findFirst();
    }

    public static Optional<Element> firstRowWithKeyInCol(Element table, String key, int colIndex) {
        return table.select("tr")
                    .stream()
                    .filter(r -> r.child(colIndex)
                                  .text()
                                  .equals(key))
                    .findFirst();
    }

    public static Double parseDouble(String s) throws ParseException {
        return NumberFormat.getInstance(Locale.GERMANY)
                           .parse(s)
                           .doubleValue();
    }

    public static Optional<Element> getTableByClassAndContainedText(Document document, String cssClass,
                                                                    String textToContain) {
        List<Element> list = document.select(cssClass)
                                     .stream()
                                     .filter(e -> anyRowContainsText(e, textToContain))
                                     .toList();

        if (list.isEmpty()) {
            logger.warn("Could not find any fitting table.");
            return Optional.empty();
        } else if (list.size() > 1) {
            logger.warn("Found more than one fitting table.");
            return Optional.empty();
        }

//        return Optional.of(list.getFirst());
        return Optional.of(list.get(0));
    }

    private static boolean anyRowContainsText(Element element, String textToContain) {
        return element.select("tr")
                      .stream()
                      .anyMatch(r -> r.text()
                                      .contains(textToContain));
    }
}
