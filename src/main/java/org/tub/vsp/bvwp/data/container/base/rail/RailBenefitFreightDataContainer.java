package org.tub.vsp.bvwp.data.container.base.rail;

import org.tub.vsp.bvwp.data.type.Benefit;

import java.util.Objects;

public class RailBenefitFreightDataContainer {
    private Benefit nbLkw;
    private Benefit nbSchiene;
    private Benefit nbSchiff;

    private Benefit naLkw;
    private Benefit naSchiene;
    private Benefit naSchiff;

    private Benefit nsLkw;
    private Benefit nsSchiene;
    private Benefit nsSchiff;

    private Benefit ntzVerbVerkehr;
    private Benefit ntzLkwSchiene;
    private Benefit ntzSchiffSchiene;

    private Benefit niLkwSchiene;
    private Benefit niSchiffSchiene;

    private Benefit nzVerbVerkehr;

    private Benefit overallBenefit;

    public Benefit getNbLkw() {
        return nbLkw;
    }

    public RailBenefitFreightDataContainer setNbLkw(Benefit nbLkw) {
        this.nbLkw = nbLkw;
        return this;
    }

    public Benefit getNbSchiene() {
        return nbSchiene;
    }

    public RailBenefitFreightDataContainer setNbSchiene(Benefit nbSchiene) {
        this.nbSchiene = nbSchiene;
        return this;
    }

    public Benefit getNbSchiff() {
        return nbSchiff;
    }

    public RailBenefitFreightDataContainer setNbSchiff(Benefit nbSchiff) {
        this.nbSchiff = nbSchiff;
        return this;
    }

    public Benefit getNaLkw() {
        return naLkw;
    }

    public RailBenefitFreightDataContainer setNaLkw(Benefit naLkw) {
        this.naLkw = naLkw;
        return this;
    }

    public Benefit getNaSchiene() {
        return naSchiene;
    }

    public RailBenefitFreightDataContainer setNaSchiene(Benefit naSchiene) {
        this.naSchiene = naSchiene;
        return this;
    }

    public Benefit getNaSchiff() {
        return naSchiff;
    }

    public RailBenefitFreightDataContainer setNaSchiff(Benefit naSchiff) {
        this.naSchiff = naSchiff;
        return this;
    }

    public Benefit getNsLkw() {
        return nsLkw;
    }

    public RailBenefitFreightDataContainer setNsLkw(Benefit nsLkw) {
        this.nsLkw = nsLkw;
        return this;
    }

    public Benefit getNsSchiene() {
        return nsSchiene;
    }

    public RailBenefitFreightDataContainer setNsSchiene(Benefit nsSchiene) {
        this.nsSchiene = nsSchiene;
        return this;
    }

    public Benefit getNsSchiff() {
        return nsSchiff;
    }

    public RailBenefitFreightDataContainer setNsSchiff(Benefit nsSchiff) {
        this.nsSchiff = nsSchiff;
        return this;
    }

    public Benefit getNtzVerbVerkehr() {
        return ntzVerbVerkehr;
    }

    public RailBenefitFreightDataContainer setNtzVerbVerkehr(Benefit ntzVerbVerkehr) {
        this.ntzVerbVerkehr = ntzVerbVerkehr;
        return this;
    }

    public Benefit getNtzLkwSchiene() {
        return ntzLkwSchiene;
    }

    public RailBenefitFreightDataContainer setNtzLkwSchiene(Benefit ntzLkwSchiene) {
        this.ntzLkwSchiene = ntzLkwSchiene;
        return this;
    }

    public Benefit getNtzSchiffSchiene() {
        return ntzSchiffSchiene;
    }

    public RailBenefitFreightDataContainer setNtzSchiffSchiene(Benefit ntzSchiffSchiene) {
        this.ntzSchiffSchiene = ntzSchiffSchiene;
        return this;
    }

    public Benefit getNiLkwSchiene() {
        return niLkwSchiene;
    }

    public RailBenefitFreightDataContainer setNiLkwSchiene(Benefit niLkwSchiene) {
        this.niLkwSchiene = niLkwSchiene;
        return this;
    }

    public Benefit getNiSchiffSchiene() {
        return niSchiffSchiene;
    }

    public RailBenefitFreightDataContainer setNiSchiffSchiene(Benefit niSchiffSchiene) {
        this.niSchiffSchiene = niSchiffSchiene;
        return this;
    }

    public Benefit getNzVerbVerkehr() {
        return nzVerbVerkehr;
    }

    public RailBenefitFreightDataContainer setNzVerbVerkehr(Benefit nzVerbVerkehr) {
        this.nzVerbVerkehr = nzVerbVerkehr;
        return this;
    }

    public Benefit getOverallBenefit() {
        return overallBenefit;
    }

    public RailBenefitFreightDataContainer setOverallBenefit(Benefit overallBenefit) {
        this.overallBenefit = overallBenefit;
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

        RailBenefitFreightDataContainer that = (RailBenefitFreightDataContainer) o;

        if (!Objects.equals(nbLkw, that.nbLkw)) {
            return false;
        }
        if (!Objects.equals(nbSchiene, that.nbSchiene)) {
            return false;
        }
        if (!Objects.equals(nbSchiff, that.nbSchiff)) {
            return false;
        }
        if (!Objects.equals(naLkw, that.naLkw)) {
            return false;
        }
        if (!Objects.equals(naSchiene, that.naSchiene)) {
            return false;
        }
        if (!Objects.equals(naSchiff, that.naSchiff)) {
            return false;
        }
        if (!Objects.equals(nsLkw, that.nsLkw)) {
            return false;
        }
        if (!Objects.equals(nsSchiene, that.nsSchiene)) {
            return false;
        }
        if (!Objects.equals(nsSchiff, that.nsSchiff)) {
            return false;
        }
        if (!Objects.equals(ntzVerbVerkehr, that.ntzVerbVerkehr)) {
            return false;
        }
        if (!Objects.equals(ntzLkwSchiene, that.ntzLkwSchiene)) {
            return false;
        }
        if (!Objects.equals(ntzSchiffSchiene, that.ntzSchiffSchiene)) {
            return false;
        }
        if (!Objects.equals(niLkwSchiene, that.niLkwSchiene)) {
            return false;
        }
        if (!Objects.equals(niSchiffSchiene, that.niSchiffSchiene)) {
            return false;
        }
        if (!Objects.equals(nzVerbVerkehr, that.nzVerbVerkehr)) {
            return false;
        }
        return Objects.equals(overallBenefit, that.overallBenefit);
    }

    @Override
    public int hashCode() {
        int result = nbLkw != null ? nbLkw.hashCode() : 0;
        result = 31 * result + (nbSchiene != null ? nbSchiene.hashCode() : 0);
        result = 31 * result + (nbSchiff != null ? nbSchiff.hashCode() : 0);
        result = 31 * result + (naLkw != null ? naLkw.hashCode() : 0);
        result = 31 * result + (naSchiene != null ? naSchiene.hashCode() : 0);
        result = 31 * result + (naSchiff != null ? naSchiff.hashCode() : 0);
        result = 31 * result + (nsLkw != null ? nsLkw.hashCode() : 0);
        result = 31 * result + (nsSchiene != null ? nsSchiene.hashCode() : 0);
        result = 31 * result + (nsSchiff != null ? nsSchiff.hashCode() : 0);
        result = 31 * result + (ntzVerbVerkehr != null ? ntzVerbVerkehr.hashCode() : 0);
        result = 31 * result + (ntzLkwSchiene != null ? ntzLkwSchiene.hashCode() : 0);
        result = 31 * result + (ntzSchiffSchiene != null ? ntzSchiffSchiene.hashCode() : 0);
        result = 31 * result + (niLkwSchiene != null ? niLkwSchiene.hashCode() : 0);
        result = 31 * result + (niSchiffSchiene != null ? niSchiffSchiene.hashCode() : 0);
        result = 31 * result + (nzVerbVerkehr != null ? nzVerbVerkehr.hashCode() : 0);
        result = 31 * result + (overallBenefit != null ? overallBenefit.hashCode() : 0);
        return result;
    }
}
