package org.tub.vsp.bvwp.data.container.base;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.tub.vsp.bvwp.data.type.Benefit;
import org.tub.vsp.bvwp.data.type.Cost;

public class RailCostBenefitAnalysisDataContainer {
    private static final Logger logger = LogManager.getLogger(RailCostBenefitAnalysisDataContainer.class);

    private RailBenefitPassengerTrafficDataContainer passengerBenefits;
    private RailBenefitFreightTrafficDataContainer freightBenefits;

    private Benefit overallBenefit;
    private Cost cost;
}
