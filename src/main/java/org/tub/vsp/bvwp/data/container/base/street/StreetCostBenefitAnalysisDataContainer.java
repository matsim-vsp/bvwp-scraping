package org.tub.vsp.bvwp.data.container.base.street;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.tub.vsp.bvwp.data.type.Benefit;
import org.tub.vsp.bvwp.data.type.InvestmentCosts;
import org.tub.vsp.bvwp.data.type.Durations;
import org.tub.vsp.bvwp.data.type.Emission;

import java.util.Map;
import java.util.Objects;
import java.util.Optional;

public class StreetCostBenefitAnalysisDataContainer {
    private static final Logger logger = LogManager.getLogger(StreetCostBenefitAnalysisDataContainer.class);

    private Benefit nb;
    private Benefit nbOperations;
    private Benefit nbPersonnel;
    private Benefit nbVehicle;
    private Benefit nw;
    private Benefit ns;
    private Benefit nrz;
    private Benefit ntz;
    private Benefit ni;
    private Benefit nl;
    private Benefit ng;
    private Map<Emission, Benefit> na;
    private Benefit nt;
    private Benefit nz;
    private Benefit overallBenefit;
    private InvestmentCosts investmentCosts;

    private Durations durations;

    public Benefit getNb() {
        return nb;
    }

    public StreetCostBenefitAnalysisDataContainer setNb(Benefit nb) {
        this.nb = nb;
        return this;
    }

    public Benefit getNbOperations() {
        return nbOperations;
    }

    public StreetCostBenefitAnalysisDataContainer setNbOperations(Benefit nbOperations) {
        this.nbOperations = nbOperations;
        return this;
    }

    public Benefit getNw() {
        return nw;
    }

    public StreetCostBenefitAnalysisDataContainer setNw(Benefit nw) {
        this.nw = nw;
        return this;
    }

    public Benefit getNs() {
        return ns;
    }

    public StreetCostBenefitAnalysisDataContainer setNs(Benefit ns) {
        this.ns = ns;
        return this;
    }

    public Benefit getNrz() {
        return nrz;
    }

    public StreetCostBenefitAnalysisDataContainer setNrz(Benefit nrz) {
        this.nrz = nrz;
        return this;
    }

    public Benefit getNtz() {
        return ntz;
    }

    public StreetCostBenefitAnalysisDataContainer setNtz(Benefit ntz) {
        this.ntz = ntz;
        return this;
    }

    public Benefit getNi() {
        return ni;
    }

    public StreetCostBenefitAnalysisDataContainer setNi(Benefit ni) {
        this.ni = ni;
        return this;
    }

    public Benefit getNl() {
        return nl;
    }

    public StreetCostBenefitAnalysisDataContainer setNl(Benefit nl) {
        this.nl = nl;
        return this;
    }

    public Benefit getNg() {
        return ng;
    }

    public StreetCostBenefitAnalysisDataContainer setNg(Benefit ng) {
        this.ng = ng;
        return this;
    }

    public Map<Emission, Benefit> getNa() {
        return na;
    }

    public StreetCostBenefitAnalysisDataContainer setNa(Map<Emission, Benefit> na) {
        this.na = na;
        return this;
    }

    public Benefit getNaCumulated() {
        if (na == null) {
            return new Benefit();
        }

        return na.values()
                 .stream()
                 .reduce(new Benefit(0., 0.), Benefit::add);
    }

    public Benefit getNt() {
        return nt;
    }

    public StreetCostBenefitAnalysisDataContainer setNt(Benefit nt) {
        this.nt = nt;
        return this;
    }

    public Benefit getNz() {
        return nz;
    }

    public StreetCostBenefitAnalysisDataContainer setNz(Benefit nz) {
        this.nz = nz;
        return this;
    }

    public InvestmentCosts getInvCost() {
        return investmentCosts;
    }

    public StreetCostBenefitAnalysisDataContainer setCost( InvestmentCosts investmentCosts ) {
        this.investmentCosts = investmentCosts;
        return this;
    }

    public Benefit getOverallBenefit() {
        Benefit cumulatedOverallBenefit = Optional.ofNullable(nb)
                                                  .orElse(new Benefit())
                                                  .add(nw)
                                                  .add(ns)
                                                  .add(nrz)
                                                  .add(ntz)
                                                  .add(ni)
                                                  .add(nl)
                                                  .add(ng)
                                                  .add(nt)
                                                  .add(nz)
                                                  .add(getNaCumulated());

        if (!cumulatedOverallBenefit.equalsWithPrecision(overallBenefit, 0)) {
            logger.warn("Different overall benefits. Cumulated overall benefit: {} | Scraped overall benefit: {}",
                    cumulatedOverallBenefit, overallBenefit);
        }
        return overallBenefit;
    }

    public StreetCostBenefitAnalysisDataContainer setOverallBenefit(Benefit overallBenefit) {
        this.overallBenefit = overallBenefit;
        return this;
    }

    public Benefit getCo2EquivalentBenefit() {
        Benefit emissions = Optional.ofNullable(this.na)
                                    .map(m -> m.get(Emission.CO2))
                                    .orElse(new Benefit());
        Benefit lifecycle = Optional.ofNullable(this.nl)
                                    .orElse(new Benefit());
        return emissions.add(lifecycle);
    }

    public Durations getDurations() {
        return durations;
    }

    public StreetCostBenefitAnalysisDataContainer setDurations(Durations durations) {
        this.durations = durations;
        return this;
    }

    public Benefit getNbPersonnel() {
        return nbPersonnel;
    }

    public StreetCostBenefitAnalysisDataContainer setNbPersonnel(Benefit nbPersonnel) {
        this.nbPersonnel = nbPersonnel;
        return this;
    }

    public Benefit getNbVehicle() {
        return nbVehicle;
    }

    public StreetCostBenefitAnalysisDataContainer setNbVehicle(Benefit nbVehicle) {
        this.nbVehicle = nbVehicle;
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

        StreetCostBenefitAnalysisDataContainer that = (StreetCostBenefitAnalysisDataContainer) o;
        return Objects.equals(nb, that.nb) && Objects.equals(nbOperations, that.nbOperations) && Objects.equals(nbPersonnel, that.nbPersonnel) && Objects.equals(nbVehicle, that.nbVehicle) && Objects.equals(nw, that.nw) && Objects.equals(ns, that.ns) && Objects.equals(nrz, that.nrz) && Objects.equals(ntz, that.ntz) && Objects.equals(ni, that.ni) && Objects.equals(nl, that.nl) && Objects.equals(ng, that.ng) && Objects.equals(na, that.na) && Objects.equals(nt, that.nt) && Objects.equals(nz, that.nz) && Objects.equals(overallBenefit, that.overallBenefit) && Objects.equals(
                        investmentCosts, that.investmentCosts ) && Objects.equals(durations, that.durations );
    }

    @Override
    public int hashCode() {
        int result = Objects.hashCode(nb);
        result = 31 * result + Objects.hashCode(nbOperations);
        result = 31 * result + Objects.hashCode(nbPersonnel);
        result = 31 * result + Objects.hashCode(nbVehicle);
        result = 31 * result + Objects.hashCode(nw);
        result = 31 * result + Objects.hashCode(ns);
        result = 31 * result + Objects.hashCode(nrz);
        result = 31 * result + Objects.hashCode(ntz);
        result = 31 * result + Objects.hashCode(ni);
        result = 31 * result + Objects.hashCode(nl);
        result = 31 * result + Objects.hashCode(ng);
        result = 31 * result + Objects.hashCode(na);
        result = 31 * result + Objects.hashCode(nt);
        result = 31 * result + Objects.hashCode(nz);
        result = 31 * result + Objects.hashCode(overallBenefit);
        result = 31 * result + Objects.hashCode( investmentCosts );
        result = 31 * result + Objects.hashCode(durations);
        return result;
    }
}
