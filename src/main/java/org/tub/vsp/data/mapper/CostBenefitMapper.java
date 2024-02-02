package org.tub.vsp.data.mapper;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.tub.vsp.JSoupUtils;
import org.tub.vsp.data.container.CostBenefitAnalysisDataContainer;
import org.tub.vsp.data.type.Benefit;
import org.tub.vsp.data.type.Cost;
import org.tub.vsp.data.type.Emission;

import java.text.ParseException;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class CostBenefitMapper {
    private static final Logger logger = LogManager.getLogger(CostBenefitMapper.class);

    public CostBenefitAnalysisDataContainer mapDocument(Document document) {
        CostBenefitAnalysisDataContainer result = new CostBenefitAnalysisDataContainer();

        Optional<Element> benefit = getTableByKey(document, "table.table_webprins", CostBenefitMapper::isBenefitTable);
        Optional<Element> costTable = getTableByKey(document, "table.table_kosten", CostBenefitMapper::isCostTable);

        //We only scrape the cumulated values
        benefit.ifPresent(element -> result.setNb(extractSimpleBenefit(element, "NB"))
                                           .setNbOperations(extractSimpleBenefit(element, "Betriebsführungskosten (Betrieb)", 0))
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

        costTable.ifPresent(element -> result.setCost(extractCosts(element)));

        return result;
    }

    private Optional<Element> getTableByKey(Document document, String key, Predicate<Element> predicate) {
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

//        return Optional.of(list.getFirst());
        return Optional.of(list.get(0));
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

    private Cost extractCosts(Element table) {
        Double costs;
        Double overallCosts;
        try {
            costs = JSoupUtils.parseDouble(JSoupUtils.getTextFromRowAndCol(table, 3, 1));
            overallCosts = JSoupUtils.parseDouble(JSoupUtils.getTextFromRowAndCol(table, 3, 2));
        } catch (ParseException e) {
            logger.warn("Could not parse benefit value from {}", table);
            return null;
        }

        return new Cost(costs, overallCosts);
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
                         .flatMap(this::extractBenefitFromRow);
    }

    private Optional<Benefit> extractBenefitFromRow(Element e) {
        Double annualBenefits;
        Double overallBenefits;
        try {
            annualBenefits = JSoupUtils.parseDouble(e.select("td")
                                                     .get(2)
                                                     .text());
            overallBenefits = JSoupUtils.parseDouble(e.select("td")
                                                      .get(3)
                                                      .text());
        } catch (ParseException ex) {
            logger.warn("Could not parse benefit value from {}", e);
            return Optional.empty();
        }
        return Optional.of(new Benefit(annualBenefits, overallBenefits));
    }

    private Map<Emission, Benefit> extractEmissionsBenefit(Element table) {
        return Emission.STRING_IDENTIFIER_BY_EMISSION_WITHOUT_CO2_OVERALL
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
