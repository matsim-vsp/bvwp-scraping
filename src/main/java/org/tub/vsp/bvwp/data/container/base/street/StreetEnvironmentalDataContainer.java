package org.tub.vsp.bvwp.data.container.base.street;

import org.tub.vsp.bvwp.data.type.EnvironmentalCriteria;

import java.util.Objects;

public class StreetEnvironmentalDataContainer {
    private EnvironmentalCriteria naturschutzVorrangflaechen21;
    private EnvironmentalCriteria natura2000Gebiete22;
    private EnvironmentalCriteria unzerschnitteneKernraeume23;
    private EnvironmentalCriteria unzerschnitteneGrossraeume24;
    private EnvironmentalCriteria flaechenInanspruchnahme25;
    private EnvironmentalCriteria ueberschwemmungsgebiete26;
    private EnvironmentalCriteria wasserschutzgebiete27;
    private EnvironmentalCriteria verkehrsarmeRaeume28;
    private EnvironmentalCriteria kulturLandschaftsschutz29;

    public EnvironmentalCriteria getNaturschutzVorrangflaechen21() {
        return naturschutzVorrangflaechen21;
    }

    public StreetEnvironmentalDataContainer setNaturschutzVorrangflaechen21(EnvironmentalCriteria naturschutzVorrangflaechen21) {
        this.naturschutzVorrangflaechen21 = naturschutzVorrangflaechen21;
        return this;
    }

    public EnvironmentalCriteria getNatura2000Gebiete22() {
        return natura2000Gebiete22;
    }

    public StreetEnvironmentalDataContainer setNatura2000Gebiete22(EnvironmentalCriteria natura2000Gebiete22) {
        this.natura2000Gebiete22 = natura2000Gebiete22;
        return this;
    }

    public EnvironmentalCriteria getUnzerschnitteneKernraeume23() {
        return unzerschnitteneKernraeume23;
    }

    public StreetEnvironmentalDataContainer setUnzerschnitteneKernraeume23(EnvironmentalCriteria unzerschnitteneKernraeume23) {
        this.unzerschnitteneKernraeume23 = unzerschnitteneKernraeume23;
        return this;
    }

    public EnvironmentalCriteria getUnzerschnitteneGrossraeume24() {
        return unzerschnitteneGrossraeume24;
    }

    public StreetEnvironmentalDataContainer setUnzerschnitteneGrossraeume24(EnvironmentalCriteria unzerschnitteneGrossraeume24) {
        this.unzerschnitteneGrossraeume24 = unzerschnitteneGrossraeume24;
        return this;
    }

    public EnvironmentalCriteria getFlaechenInanspruchnahme25() {
        return flaechenInanspruchnahme25;
    }

    public StreetEnvironmentalDataContainer setFlaechenInanspruchnahme25(EnvironmentalCriteria flaechenInanspruchnahme25) {
        this.flaechenInanspruchnahme25 = flaechenInanspruchnahme25;
        return this;
    }

    public EnvironmentalCriteria getUeberschwemmungsgebiete26() {
        return ueberschwemmungsgebiete26;
    }

    public StreetEnvironmentalDataContainer setUeberschwemmungsgebiete26(EnvironmentalCriteria ueberschwemmungsgebiete26) {
        this.ueberschwemmungsgebiete26 = ueberschwemmungsgebiete26;
        return this;
    }

    public EnvironmentalCriteria getWasserschutzgebiete27() {
        return wasserschutzgebiete27;
    }

    public StreetEnvironmentalDataContainer setWasserschutzgebiete27(EnvironmentalCriteria wasserschutzgebiete27) {
        this.wasserschutzgebiete27 = wasserschutzgebiete27;
        return this;
    }

    public EnvironmentalCriteria getVerkehrsarmeRaeume28() {
        return verkehrsarmeRaeume28;
    }

    public StreetEnvironmentalDataContainer setVerkehrsarmeRaeume28(EnvironmentalCriteria verkehrsarmeRaeume28) {
        this.verkehrsarmeRaeume28 = verkehrsarmeRaeume28;
        return this;
    }

    public EnvironmentalCriteria getKulturLandschaftsschutz29() {
        return kulturLandschaftsschutz29;
    }

    public StreetEnvironmentalDataContainer setKulturLandschaftsschutz29(EnvironmentalCriteria kulturLandschaftsschutz29) {
        this.kulturLandschaftsschutz29 = kulturLandschaftsschutz29;
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

        StreetEnvironmentalDataContainer that = (StreetEnvironmentalDataContainer) o;
        return Objects.equals(naturschutzVorrangflaechen21, that.naturschutzVorrangflaechen21) && Objects.equals(natura2000Gebiete22,
                that.natura2000Gebiete22) && Objects.equals(unzerschnitteneKernraeume23, that.unzerschnitteneKernraeume23) && Objects.equals(unzerschnitteneGrossraeume24, that.unzerschnitteneGrossraeume24) && Objects.equals(flaechenInanspruchnahme25, that.flaechenInanspruchnahme25) && Objects.equals(ueberschwemmungsgebiete26, that.ueberschwemmungsgebiete26) && Objects.equals(wasserschutzgebiete27, that.wasserschutzgebiete27) && Objects.equals(verkehrsarmeRaeume28, that.verkehrsarmeRaeume28) && Objects.equals(kulturLandschaftsschutz29, that.kulturLandschaftsschutz29);
    }

    @Override
    public int hashCode() {
        int result = Objects.hashCode(naturschutzVorrangflaechen21);
        result = 31 * result + Objects.hashCode(natura2000Gebiete22);
        result = 31 * result + Objects.hashCode(unzerschnitteneKernraeume23);
        result = 31 * result + Objects.hashCode(unzerschnitteneGrossraeume24);
        result = 31 * result + Objects.hashCode(flaechenInanspruchnahme25);
        result = 31 * result + Objects.hashCode(ueberschwemmungsgebiete26);
        result = 31 * result + Objects.hashCode(wasserschutzgebiete27);
        result = 31 * result + Objects.hashCode(verkehrsarmeRaeume28);
        result = 31 * result + Objects.hashCode(kulturLandschaftsschutz29);
        return result;
    }
}
