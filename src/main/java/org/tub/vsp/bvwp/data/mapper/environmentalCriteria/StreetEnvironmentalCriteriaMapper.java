package org.tub.vsp.bvwp.data.mapper.environmentalCriteria;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.tub.vsp.bvwp.JSoupUtils;
import org.tub.vsp.bvwp.data.container.base.street.StreetEnvironmentalDataContainer;
import org.tub.vsp.bvwp.data.type.EnvironmentalCriteria;

import java.util.ArrayList;
import java.util.List;

public class StreetEnvironmentalCriteriaMapper {
    public static final Logger logger = LogManager.getLogger(StreetEnvironmentalCriteriaMapper.class);

    public static StreetEnvironmentalDataContainer mapDocument(Document document) {
        StreetEnvironmentalDataContainer result = new StreetEnvironmentalDataContainer();

        boolean exclude = document.select("p").stream().anyMatch(p -> p.text()
                                                                       .contains("Die umweltfachliche Beurteilung von Knotenpunkten erfolgt in Form" +
                                                                               " einer qualitativen Einschätzung."));

        if (exclude) {
            logger.warn("Excluding environmental criteria for this project");
            return null;
        }

        Element envTable = JSoupUtils.getTableByCssKeyAndPredicate(document, "table.table_webprins",
                                             StreetEnvironmentalCriteriaMapper::isEnvironmentalCriteriaTable)
                                     .orElseThrow();

        result.setNaturschutzVorrangflaechen21(extractFlatEnvironmentalCriteria(envTable, "2.1"))
              .setNatura2000Gebiete22(extractWithSubEnvironmentalCriteria(envTable, "2.2", 2))
              .setUnzerschnitteneKernraeume23(extractFlatEnvironmentalCriteria(envTable, "2.3"))
              .setUnzerschnitteneGrossraeume24(extractWithSubEnvironmentalCriteria(envTable, "2.4", 4))
              .setFlaechenInanspruchnahme25(extractFlatEnvironmentalCriteria(envTable, "2.5"))
              .setUeberschwemmungsgebiete26(extractFlatEnvironmentalCriteria(envTable, "2.6"))
              .setWasserschutzgebiete27(extractFlatEnvironmentalCriteria(envTable, "2.7"))
              .setVerkehrsarmeRaeume28(extractFlatEnvironmentalCriteria(envTable, "2.8"))
              .setKulturLandschaftsschutz29(extractFlatEnvironmentalCriteria(envTable, "2.9"));

        return result;
    }

    private static EnvironmentalCriteria extractFlatEnvironmentalCriteria(Element table, String number) {
        Element row = JSoupUtils.firstRowWithKeyInCol(table, number, 0).orElseThrow();

        Double absolut = JSoupUtils.parseDoubleOrElseThrow(row.select("td").get(2).text());
        Double betroffen = JSoupUtils.parseDoubleOrElseThrow(row.select("td").get(4).text());
        EnvironmentalCriteria.Description description = new EnvironmentalCriteria.Description(absolut, betroffen);

        EnvironmentalCriteria.UmweltBewertung bewertung = handleBewertung(row.select("td").get(6).text());
        return new EnvironmentalCriteria(List.of(description), bewertung);
    }

    private static EnvironmentalCriteria extractWithSubEnvironmentalCriteria(Element table, String number, int rows) {
        int headRowIndex = JSoupUtils.getFirstRowIndexWithTextInCol(table, number, 0).orElseThrow();
        EnvironmentalCriteria.UmweltBewertung umweltBewertung = handleBewertung(JSoupUtils.getTextFromRowAndCol(table, headRowIndex, 6));

        List<EnvironmentalCriteria.Description> descriptions = new ArrayList<>();
        for (int i = 1; i <= rows; i++) {
            Double absolut = JSoupUtils.parseDoubleOrElseThrow(JSoupUtils.getTextFromRowAndCol(table, headRowIndex + i, 2));
            Double betroffen = JSoupUtils.parseDoubleOrElseThrow(JSoupUtils.getTextFromRowAndCol(table, headRowIndex + i, 4));
            EnvironmentalCriteria.Description description = new EnvironmentalCriteria.Description(absolut, betroffen);
            descriptions.add(description);
        }

        return new EnvironmentalCriteria(descriptions, umweltBewertung);
    }

    private static EnvironmentalCriteria.UmweltBewertung handleBewertung(String bewertung) {
        if (bewertung.equals("-") || bewertung.isBlank()) {
            return null;
        }
        return EnvironmentalCriteria.UmweltBewertung.valueOf(bewertung.toUpperCase());
    }

    private static boolean isEnvironmentalCriteriaTable(Element element) {
        return element.select("th").stream().map(Element::text).limit(4).toList().equals(List.of("Nr.", "Kriterium", "Beschreibung", "Bewertung"));
    }
}
