package org.tub.vsp.bvwp.data.container.base.street;

import org.tub.vsp.bvwp.data.type.Emission;

import java.util.Objects;

public class StreetPhysicalEffectDataContainer {
    private StreetEmissionsDataContainer emissionsDataContainer;
    private PEffect pVehicleHours; //Personenverkehr
    private PEffect pVehicleKilometers; //Personenverkehr
    private PEffect lVehicleHours; //Güterverkehr (Lkw)
    private Double lVehicleKilometers; //Güterverkehr (Lkw)

    public StreetEmissionsDataContainer getEmissionsDataContainer() {
        return emissionsDataContainer;
    }

    public Double getKfzEmission(Emission emission) {
        return emissionsDataContainer.emissions().get(emission).kfz();
    }

    public StreetPhysicalEffectDataContainer setEmissionsDataContainer(StreetEmissionsDataContainer emissionsDataContainer) {
        this.emissionsDataContainer = emissionsDataContainer;
        return this;
    }

    public PEffect getPTravelTimes() {
        return pVehicleHours;
    }

    public void setPVehicleHours(PEffect pVehicleHours) {
        this.pVehicleHours = pVehicleHours;
    }

    public PEffect getPVehicleKilometers() {
        return pVehicleKilometers;
    }

    public void setPVehicleKilometers( PEffect pVehicleKilometers ) {
        this.pVehicleKilometers = pVehicleKilometers;
    }
    public Double getLVehicleKilometers() {
        return lVehicleKilometers;
    }

    public void setLVehicleKilometers( Double lVehicleKilometers ) {
        this.lVehicleKilometers = lVehicleKilometers;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        StreetPhysicalEffectDataContainer that = (StreetPhysicalEffectDataContainer) o;

        if (!Objects.equals(emissionsDataContainer, that.emissionsDataContainer)) {
            return false;
        }
        if (!Objects.equals(pVehicleHours, that.pVehicleHours)) {
            return false;
        }
        return Objects.equals( pVehicleKilometers, that.pVehicleKilometers );
    }

    @Override
    public int hashCode() {
        int result = emissionsDataContainer != null ? emissionsDataContainer.hashCode() : 0;
        result = 31 * result + (pVehicleHours != null ? pVehicleHours.hashCode() : 0);
        result = 31 * result + (pVehicleKilometers != null ? pVehicleKilometers.hashCode() : 0);
        return result;
    }
    public void setlVehicleHours(PEffect lVehicleHours){
        this.lVehicleHours = lVehicleHours;
    }
    public PEffect getlVehicleHours(){
        return lVehicleHours;
    }

    public record PEffect(Double overall, Double induced, Double shifted) {

    }
}
