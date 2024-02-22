package org.tub.vsp.bvwp.data.container.base;

import org.tub.vsp.bvwp.data.type.Benefit;

import java.util.Objects;

public class RailBenefitPassengerTrafficDataContainer {
    private Benefit nbPkw;
    private Benefit nbSpv;
    private Benefit nbLuft;

    private Benefit naPkw;
    private Benefit naSpv;
    private Benefit naLuft;

    private Benefit nsPkw;
    private Benefit nsSpv;

    private Benefit nrzVerbVerkehr;
    private Benefit nrzInduzVerkehr;
    private Benefit nrzVerlagerungPkwSpv;
    private Benefit nrzVerlagerungLuftSpv;

    private Benefit niInduzVerkehr;
    private Benefit niVerlagerungPkwSpv;
    private Benefit niVerlagerungLuftSpv;

    private Benefit overallBenefit;

    public Benefit getNbPkw() {
        return nbPkw;
    }

    public RailBenefitPassengerTrafficDataContainer setNbPkw(Benefit nbPkw) {
        this.nbPkw = nbPkw;
        return this;
    }

    public Benefit getNbSpv() {
        return nbSpv;
    }

    public RailBenefitPassengerTrafficDataContainer setNbSpv(Benefit nbSpv) {
        this.nbSpv = nbSpv;
        return this;
    }

    public Benefit getNbLuft() {
        return nbLuft;
    }

    public RailBenefitPassengerTrafficDataContainer setNbLuft(Benefit nbLuft) {
        this.nbLuft = nbLuft;
        return this;
    }

    public Benefit getNaPkw() {
        return naPkw;
    }

    public RailBenefitPassengerTrafficDataContainer setNaPkw(Benefit naPkw) {
        this.naPkw = naPkw;
        return this;
    }

    public Benefit getNaSpv() {
        return naSpv;
    }

    public RailBenefitPassengerTrafficDataContainer setNaSpv(Benefit naSpv) {
        this.naSpv = naSpv;
        return this;
    }

    public Benefit getNaLuft() {
        return naLuft;
    }

    public RailBenefitPassengerTrafficDataContainer setNaLuft(Benefit naLuft) {
        this.naLuft = naLuft;
        return this;
    }

    public Benefit getNsPkw() {
        return nsPkw;
    }

    public RailBenefitPassengerTrafficDataContainer setNsPkw(Benefit nsPkw) {
        this.nsPkw = nsPkw;
        return this;
    }

    public Benefit getNsSpv() {
        return nsSpv;
    }

    public RailBenefitPassengerTrafficDataContainer setNsSpv(Benefit nsSpv) {
        this.nsSpv = nsSpv;
        return this;
    }

    public Benefit getNrzVerbVerkehr() {
        return nrzVerbVerkehr;
    }

    public RailBenefitPassengerTrafficDataContainer setNrzVerbVerkehr(Benefit nrzVerbVerkehr) {
        this.nrzVerbVerkehr = nrzVerbVerkehr;
        return this;
    }

    public Benefit getNrzInduzVerkehr() {
        return nrzInduzVerkehr;
    }

    public RailBenefitPassengerTrafficDataContainer setNrzInduzVerkehr(Benefit nrzInduzVerkehr) {
        this.nrzInduzVerkehr = nrzInduzVerkehr;
        return this;
    }

    public Benefit getNrzVerlagerungPkwSpv() {
        return nrzVerlagerungPkwSpv;
    }

    public RailBenefitPassengerTrafficDataContainer setNrzVerlagerungPkwSpv(Benefit nrzVerlagerungPkwSpv) {
        this.nrzVerlagerungPkwSpv = nrzVerlagerungPkwSpv;
        return this;
    }

    public Benefit getNrzVerlagerungLuftSpv() {
        return nrzVerlagerungLuftSpv;
    }

    public RailBenefitPassengerTrafficDataContainer setNrzVerlagerungLuftSpv(Benefit nrzVerlagerungLuftSpv) {
        this.nrzVerlagerungLuftSpv = nrzVerlagerungLuftSpv;
        return this;
    }

    public Benefit getNiInduzVerkehr() {
        return niInduzVerkehr;
    }

    public RailBenefitPassengerTrafficDataContainer setNiInduzVerkehr(Benefit niInduzVerkehr) {
        this.niInduzVerkehr = niInduzVerkehr;
        return this;
    }

    public Benefit getNiVerlagerungPkwSpv() {
        return niVerlagerungPkwSpv;
    }

    public RailBenefitPassengerTrafficDataContainer setNiVerlagerungPkwSpv(Benefit niVerlagerungPkwSpv) {
        this.niVerlagerungPkwSpv = niVerlagerungPkwSpv;
        return this;
    }

    public Benefit getNiVerlagerungLuftSpv() {
        return niVerlagerungLuftSpv;
    }

    public RailBenefitPassengerTrafficDataContainer setNiVerlagerungLuftSpv(Benefit niVerlagerungLuftSpv) {
        this.niVerlagerungLuftSpv = niVerlagerungLuftSpv;
        return this;
    }

    public Benefit getOverallBenefit() {
        return overallBenefit;
    }

    public RailBenefitPassengerTrafficDataContainer setOverallBenefit(Benefit overallBenefit) {
        this.overallBenefit = overallBenefit;
        return this;
    }

    private Benefit nb() {
        return nbPkw.add(nbSpv).add(nbLuft);
    }

    private Benefit na() {
        return naPkw.add(naSpv).add(naLuft);
    }

    private Benefit ns() {
        return nsPkw.add(nsSpv);
    }

    private Benefit nrz() {
        return nrzVerbVerkehr.add(nrzInduzVerkehr).add(nrzVerlagerungPkwSpv).add(nrzVerlagerungLuftSpv);
    }

    private Benefit ni() {
        return niInduzVerkehr.add(niVerlagerungPkwSpv).add(niVerlagerungLuftSpv);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        RailBenefitPassengerTrafficDataContainer that = (RailBenefitPassengerTrafficDataContainer) o;

        if (!Objects.equals(nbPkw, that.nbPkw)) {
            return false;
        }
        if (!Objects.equals(nbSpv, that.nbSpv)) {
            return false;
        }
        if (!Objects.equals(nbLuft, that.nbLuft)) {
            return false;
        }
        if (!Objects.equals(naPkw, that.naPkw)) {
            return false;
        }
        if (!Objects.equals(naSpv, that.naSpv)) {
            return false;
        }
        if (!Objects.equals(naLuft, that.naLuft)) {
            return false;
        }
        if (!Objects.equals(nsPkw, that.nsPkw)) {
            return false;
        }
        if (!Objects.equals(nsSpv, that.nsSpv)) {
            return false;
        }
        if (!Objects.equals(nrzVerbVerkehr, that.nrzVerbVerkehr)) {
            return false;
        }
        if (!Objects.equals(nrzInduzVerkehr, that.nrzInduzVerkehr)) {
            return false;
        }
        if (!Objects.equals(nrzVerlagerungPkwSpv, that.nrzVerlagerungPkwSpv)) {
            return false;
        }
        if (!Objects.equals(nrzVerlagerungLuftSpv, that.nrzVerlagerungLuftSpv)) {
            return false;
        }
        if (!Objects.equals(niInduzVerkehr, that.niInduzVerkehr)) {
            return false;
        }
        if (!Objects.equals(niVerlagerungPkwSpv, that.niVerlagerungPkwSpv)) {
            return false;
        }
        if (!Objects.equals(niVerlagerungLuftSpv, that.niVerlagerungLuftSpv)) {
            return false;
        }
        return Objects.equals(overallBenefit, that.overallBenefit);
    }

    @Override
    public int hashCode() {
        int result = nbPkw != null ? nbPkw.hashCode() : 0;
        result = 31 * result + (nbSpv != null ? nbSpv.hashCode() : 0);
        result = 31 * result + (nbLuft != null ? nbLuft.hashCode() : 0);
        result = 31 * result + (naPkw != null ? naPkw.hashCode() : 0);
        result = 31 * result + (naSpv != null ? naSpv.hashCode() : 0);
        result = 31 * result + (naLuft != null ? naLuft.hashCode() : 0);
        result = 31 * result + (nsPkw != null ? nsPkw.hashCode() : 0);
        result = 31 * result + (nsSpv != null ? nsSpv.hashCode() : 0);
        result = 31 * result + (nrzVerbVerkehr != null ? nrzVerbVerkehr.hashCode() : 0);
        result = 31 * result + (nrzInduzVerkehr != null ? nrzInduzVerkehr.hashCode() : 0);
        result = 31 * result + (nrzVerlagerungPkwSpv != null ? nrzVerlagerungPkwSpv.hashCode() : 0);
        result = 31 * result + (nrzVerlagerungLuftSpv != null ? nrzVerlagerungLuftSpv.hashCode() : 0);
        result = 31 * result + (niInduzVerkehr != null ? niInduzVerkehr.hashCode() : 0);
        result = 31 * result + (niVerlagerungPkwSpv != null ? niVerlagerungPkwSpv.hashCode() : 0);
        result = 31 * result + (niVerlagerungLuftSpv != null ? niVerlagerungLuftSpv.hashCode() : 0);
        result = 31 * result + (overallBenefit != null ? overallBenefit.hashCode() : 0);
        return result;
    }
}
