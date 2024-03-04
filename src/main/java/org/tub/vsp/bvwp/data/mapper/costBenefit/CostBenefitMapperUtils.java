package org.tub.vsp.bvwp.data.mapper.costBenefit;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jsoup.nodes.Element;
import org.tub.vsp.bvwp.JSoupUtils;
import org.tub.vsp.bvwp.data.type.Benefit;
import org.tub.vsp.bvwp.data.type.Cost;

import java.text.ParseException;
import java.util.Optional;

public class CostBenefitMapperUtils {
    private static final Logger logger = LogManager.getLogger(CostBenefitMapperUtils.class);

    public static Optional<Benefit> extractBenefitFromRow(Element e) {
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

    public static Cost extractCosts(Element table) {
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
}
