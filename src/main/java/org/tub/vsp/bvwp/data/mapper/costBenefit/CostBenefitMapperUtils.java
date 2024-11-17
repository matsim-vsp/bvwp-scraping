package org.tub.vsp.bvwp.data.mapper.costBenefit;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jsoup.nodes.Element;
import org.tub.vsp.bvwp.JSoupUtils;
import org.tub.vsp.bvwp.data.type.Benefit;
import org.tub.vsp.bvwp.data.type.InvestmentCosts;
import org.tub.vsp.bvwp.data.type.Durations;

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

    public static InvestmentCosts extractCosts( Element table ) {
        Double costs;
        Double overallCosts;
        try {
            costs = JSoupUtils.parseDouble(JSoupUtils.getTextFromRowAndCol(table, 3, 1));
            overallCosts = JSoupUtils.parseDouble(JSoupUtils.getTextFromRowAndCol(table, 3, 2));
        } catch (ParseException e) {
            logger.warn("Could not parse benefit value from {}", table);
            return null;
        }

        return new InvestmentCosts(costs, overallCosts);
    }

    public static Durations extractDurations(Element table) {
        double planning;
        double construction;
        double maintenance;
        try {
            planning = parseNumberWithSuffix(JSoupUtils.getTextFromRowAndCol(table, 1, 1), "Monate") / 12.;
            construction = parseNumberWithSuffix(JSoupUtils.getTextFromRowAndCol(table, 2, 1), "Monate") / 12.;
            maintenance = parseNumberWithSuffix(JSoupUtils.getTextFromRowAndCol(table, 3, 1), "Jahre");
        } catch (ParseException e) {
            logger.warn("Could not parse duration value from {}", table);
            return null;
        }

        return new Durations(planning, construction, maintenance);
    }

    private static Double parseNumberWithSuffix(String content, String suffix) throws ParseException {
        assert content.endsWith(suffix);
        return JSoupUtils.parseDouble(content.split(" ")[0]);
    }
}
