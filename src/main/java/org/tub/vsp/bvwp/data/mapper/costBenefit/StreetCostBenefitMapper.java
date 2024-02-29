package org.tub.vsp.bvwp.data.mapper.costBenefit;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.tub.vsp.bvwp.JSoupUtils;
import org.tub.vsp.bvwp.data.container.base.street.StreetCostBenefitAnalysisDataContainer;
import org.tub.vsp.bvwp.data.type.Benefit;
import org.tub.vsp.bvwp.data.type.Emission;

import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

public class StreetCostBenefitMapper {
    private static final Logger logger = LogManager.getLogger(StreetCostBenefitMapper.class);

    public StreetCostBenefitAnalysisDataContainer mapDocument(Document document) {
        StreetCostBenefitAnalysisDataContainer result = new StreetCostBenefitAnalysisDataContainer();

        Optional<Element> benefit = JSoupUtils.getTableByCssKeyAndPredicate(document, "table.table_webprins",
                StreetCostBenefitMapper::isBenefitTable);
        Optional<Element> costTable = JSoupUtils.getTableByCssKeyAndPredicate(document, "table.table_kosten",
                StreetCostBenefitMapper::isCostTable);

        //We only scrape the cumulated values
        benefit.ifPresent(element -> result.setNb(extractSimpleBenefit(element, "NB"))
                                           .setNbVehicle(extractSimpleBenefit(element, "Fahrzeugvorhaltekosten", 0))
                                           .setNbPersonnel(extractSimpleBenefit(element, "Betriebsführungskosten " +
                                                   "(Personal)", 0))
                                           .setNbOperations(extractSimpleBenefit(element, "Betriebsführungskosten " +
                                                   "(Betrieb)", 0))
                                           .setNw(extractSimpleBenefit(element, "NW"))
                                           .setNs(extractSimpleBenefit(element, "NS"))
                                           .setNrz(extractSimpleBenefit(element, "NRZ"))
                                           .setNtz(extractSimpleBenefit(element, "NTZ"))
                                           .setNi(extractSimpleBenefit(element, "NI"))
                                           .setNl(extractSimpleBenefit(element, "NL"))
                                           .setNg(extractSimpleBenefit(element, "NG"))
                                           .setNt(extractSimpleBenefit(element, "NT"))
                                           .setNz(extractSimpleBenefit(element, "NZ"))
                                           //Only for emissions we scrape the individual values
                                           .setNa(extractEmissionsBenefit(element))
                                           .setOverallBenefit(extractSimpleBenefit(element, "Gesamtnutzen", 0)));

        costTable.ifPresent(element -> result.setCost(CostBenefitMapperUtils.extractCosts(element)));

        return result;
    }

    //The table with "Veränderung der Betriebskosten" in its second row corresponds to the benefit table
    private static boolean isBenefitTable(Element element) {
        return element.select("tr")
                      .get(1)
                      .text()
                      .contains("Veränderung der Betriebskosten");
    }

    //The table with "Summe bewertungsrelevanter Investitionskosten" in its third row corresponds to the cost table
    private static boolean isCostTable(Element element) {
        if (element.select("tr")
                   .size() < 4) {
            return false;
        }
        return element.select("tr")
                      .get(3)
                      .text()
                      .contains("Summe bewertungsrelevanter Investitionskosten");
    }

    private Benefit extractSimpleBenefit(Element table, String key) {
        return extractSimpleBenefit(table, key, 1);
    }

    private Benefit extractSimpleBenefit(Element table, String key, int keyColumnIndex) {
        Optional<Benefit> optionalBenefit = extractSimpleBenefitOptional(table, key, keyColumnIndex);
        if (optionalBenefit.isEmpty()) {
            logger.warn("Could not find cost benefit for key {}.", key);
            return null;
        }
        return optionalBenefit.get();
    }

    private Optional<Benefit> extractSimpleBenefitOptional(Element table, String key, int keyColumnIndex) {
        return JSoupUtils.firstRowWithKeyInCol(table, key, keyColumnIndex)
                         .flatMap(CostBenefitMapperUtils::extractBenefitFromRow);
    }

    private Map<Emission, Benefit> extractEmissionsBenefit(Element table) {
        return Emission.STREET_STRING_IDENTIFIER_BY_EMISSION
                .entrySet()
                .stream()
                .map(e -> Map.entry(e.getKey(), extractSimpleBenefitOptional(table, e.getValue(), 0)))
                .filter(e -> e.getValue()
                              .isPresent())
                .collect(Collectors.toMap(Map.Entry::getKey,
                        e -> e.getValue()
                              .get()));
    }


}
