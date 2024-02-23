package org.tub.vsp.bvwp.data.container.base;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.tub.vsp.bvwp.data.type.Benefit;
import org.tub.vsp.bvwp.data.type.Cost;

public class RailCostBenefitAnalysisDataContainer {
    private static final Logger logger = LogManager.getLogger(RailCostBenefitAnalysisDataContainer.class);

    private RailBenefitPassengerDataContainer passengerBenefits;
    private RailBenefitFreightDataContainer freightBenefits;

    private Benefit overallBenefit;
    private Cost cost;

    public RailBenefitPassengerDataContainer getPassengerBenefits() {
        return passengerBenefits;
    }

    public RailCostBenefitAnalysisDataContainer setPassengerBenefits(RailBenefitPassengerDataContainer passengerBenefits) {
        this.passengerBenefits = passengerBenefits;
        return this;
    }

    public RailBenefitFreightDataContainer getFreightBenefits() {
        return freightBenefits;
    }

    public RailCostBenefitAnalysisDataContainer setFreightBenefits(RailBenefitFreightDataContainer freightBenefits) {
        this.freightBenefits = freightBenefits;
        return this;
    }

    public Benefit getOverallBenefit() {
        return overallBenefit;
    }

    public RailCostBenefitAnalysisDataContainer setOverallBenefit(Benefit overallBenefit) {
        this.overallBenefit = overallBenefit;
        return this;
    }

    public Cost getCost() {
        return cost;
    }

    public RailCostBenefitAnalysisDataContainer setCost(Cost cost) {
        this.cost = cost;
        return this;
    }
}
