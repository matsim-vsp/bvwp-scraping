package org.tub.vsp.bvwp.data.container.base.rail;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.tub.vsp.bvwp.data.type.Benefit;
import org.tub.vsp.bvwp.data.type.Cost;

import java.util.Objects;

public class RailCostBenefitAnalysisDataContainer {
    private static final Logger logger = LogManager.getLogger(RailCostBenefitAnalysisDataContainer.class);

    private RailBenefitPassengerDataContainer passengerBenefits;
    private RailBenefitFreightDataContainer freightBenefits;

    private Benefit nl;

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

    public Benefit getNl() {
        return nl;
    }

    public RailCostBenefitAnalysisDataContainer setNl(Benefit nl) {
        this.nl = nl;
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        RailCostBenefitAnalysisDataContainer that = (RailCostBenefitAnalysisDataContainer) o;

        if (!Objects.equals(passengerBenefits, that.passengerBenefits)) {
            return false;
        }
        if (!Objects.equals(freightBenefits, that.freightBenefits)) {
            return false;
        }
        if (!Objects.equals(nl, that.nl)) {
            return false;
        }
        if (!Objects.equals(overallBenefit, that.overallBenefit)) {
            return false;
        }
        return Objects.equals(cost, that.cost);
    }

    @Override
    public int hashCode() {
        int result = passengerBenefits != null ? passengerBenefits.hashCode() : 0;
        result = 31 * result + (freightBenefits != null ? freightBenefits.hashCode() : 0);
        result = 31 * result + (nl != null ? nl.hashCode() : 0);
        result = 31 * result + (overallBenefit != null ? overallBenefit.hashCode() : 0);
        result = 31 * result + (cost != null ? cost.hashCode() : 0);
        return result;
    }
}
