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

public class CostBenefitMapper implements DocumentMapper<CostBenefitAnalysisDataContainer> {
    private static final Logger logger = LogManager.getLogger(CostBenefitMapper.class);

    @Override
    public CostBenefitAnalysisDataContainer mapDocument(Document document) {
        CostBenefitAnalysisDataContainer result = new CostBenefitAnalysisDataContainer();

        Optional<Element> costBenefitTable = getTableByKey(document, "table.table_webprins", this::isCostBenefitTable);
        Optional<Element> costTable = getTableByKey(document, "table.table_kosten", this::isCostTable);

        costBenefitTable.ifPresent(element -> result.setNb(extractSimpleBenefit(element, "NB"))
                                                    .setNw(extractSimpleBenefit(element, "NW"))
                                                    .setNs(extractSimpleBenefit(element, "NS"))
                                                    .setNrz(extractSimpleBenefit(element, "NRZ"))
                                                    .setNtz(extractSimpleBenefit(element, "NTZ"))
                                                    .setNi(extractSimpleBenefit(element, "NI"))
                                                    .setNl(extractSimpleBenefit(element, "NL"))
                                                    .setNg(extractSimpleBenefit(element, "NG"))
                                                    .setNt(extractSimpleBenefit(element, "NT"))
                                                    .setNz(extractSimpleBenefit(element, "NZ"))
                                                    .setNa(extractEmissionsBenefit(element)));

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

        return Optional.of(list.getFirst());
    }

    private boolean isCostBenefitTable(Element element) {
        return element.select("tr")
                      .get(1)
                      .text()
                      .contains("Veränderung der Betriebskosten");
    }

    private boolean isCostTable(Element element) {
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

    private Benefit extractSimpleBenefit(Element table, String key, int columnIndex) {
        return JSoupUtils.firstRowWithKeyInCol(table, key, columnIndex)
                         .map(this::extractBenefitFromRow)
                         .orElse(null);
    }

    private Benefit extractBenefitFromRow(Element e) {
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
            return null;
        }
        return new Benefit(annualBenefits, overallBenefits);
    }

    private Map<Emission, Benefit> extractEmissionsBenefit(Element table) {
        return Emission.STRING_IDENTIFIER_BY_EMISSION_WITH_LIFE_CYCLE_CO2
                .entrySet()
                .stream()
                .map(e -> Map.entry(e.getKey(), extractSimpleBenefit(table, e.getValue(), 0)))
                .collect(Collectors.toMap(Map.Entry::getKey,
                        Map.Entry::getValue));
    }


}
