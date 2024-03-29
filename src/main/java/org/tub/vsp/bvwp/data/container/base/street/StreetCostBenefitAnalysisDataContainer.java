package org.tub.vsp.bvwp.data.container.base.street;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.tub.vsp.bvwp.data.type.Benefit;
import org.tub.vsp.bvwp.data.type.Cost;
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
    private Cost cost;

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

    public Benefit getNaCumulated() {
        if (na == null) {
            return new Benefit();
        }

        return na.values()
                 .stream()
                 .reduce(new Benefit(0., 0.), Benefit::add);
    }

    public StreetCostBenefitAnalysisDataContainer setNa(Map<Emission, Benefit> na) {
        this.na = na;
        return this;
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

    public Cost getCost() {
        return cost;
    }

    public StreetCostBenefitAnalysisDataContainer setCost(Cost cost) {
        this.cost = cost;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        StreetCostBenefitAnalysisDataContainer that = (StreetCostBenefitAnalysisDataContainer) o;

        if (!Objects.equals(nb, that.nb)) {
            return false;
        }
        if (!Objects.equals(nw, that.nw)) {
            return false;
        }
        if (!Objects.equals(ns, that.ns)) {
            return false;
        }
        if (!Objects.equals(nrz, that.nrz)) {
            return false;
        }
        if (!Objects.equals(ntz, that.ntz)) {
            return false;
        }
        if (!Objects.equals(ni, that.ni)) {
            return false;
        }
        if (!Objects.equals(nl, that.nl)) {
            return false;
        }
        if (!Objects.equals(ng, that.ng)) {
            return false;
        }
        if (!Objects.equals(na, that.na)) {
            return false;
        }
        if (!Objects.equals(nt, that.nt)) {
            return false;
        }
        if (!Objects.equals(nz, that.nz)) {
            return false;
        }
        return Objects.equals(cost, that.cost);
    }

    @Override
    public int hashCode() {
        int result = nb != null ? nb.hashCode() : 0;
        result = 31 * result + (nw != null ? nw.hashCode() : 0);
        result = 31 * result + (ns != null ? ns.hashCode() : 0);
        result = 31 * result + (nrz != null ? nrz.hashCode() : 0);
        result = 31 * result + (ntz != null ? ntz.hashCode() : 0);
        result = 31 * result + (ni != null ? ni.hashCode() : 0);
        result = 31 * result + (nl != null ? nl.hashCode() : 0);
        result = 31 * result + (ng != null ? ng.hashCode() : 0);
        result = 31 * result + (na != null ? na.hashCode() : 0);
        result = 31 * result + (nt != null ? nt.hashCode() : 0);
        result = 31 * result + (nz != null ? nz.hashCode() : 0);
        result = 31 * result + (cost != null ? cost.hashCode() : 0);
        return result;
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
}
