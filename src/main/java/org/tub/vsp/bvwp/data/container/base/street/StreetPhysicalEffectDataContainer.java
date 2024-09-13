package org.tub.vsp.bvwp.data.container.base.street;

import org.tub.vsp.bvwp.data.type.Emission;

import java.util.Objects;

public class StreetPhysicalEffectDataContainer {
    private StreetEmissionsDataContainer emissionsDataContainer;
    private Effect travelTimesPV;
    private Effect vehicleKilometersPV; //Personenverkehr
    private Effect vehicleHoursPV; //Personenverkehr
    //KMT braucht für das EWGT Paper auch die ckm im GV um Gesamtsumme bilden zu können.
    private Double vehicleKilometersGV; //Güterverkehr

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

    public Effect getTravelTimesPV() {
        return travelTimesPV;
    }

    public void setTravelTimesPV(Effect travelTimesPV) {
        this.travelTimesPV = travelTimesPV;
    }

    public Effect getVehicleKilometersPV() {
        return vehicleKilometersPV;
    }

    public Double getVehicleKilometersGV() {
        return vehicleKilometersGV;
    }

    public void setVehicleKilometersPV(Effect vehicleKilometersPV) {
        this.vehicleKilometersPV = vehicleKilometersPV;
    }

    public void setVehicleKilometersGV(Double vehicleKilometersGV) {
        this.vehicleKilometersGV = vehicleKilometersGV;
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
        if (!Objects.equals(travelTimesPV, that.travelTimesPV)) {
            return false;
        }
        if (!Objects.equals(vehicleKilometersGV, that.vehicleKilometersGV)) {
            return false;
        }
        return Objects.equals(vehicleKilometersPV, that.vehicleKilometersPV);
    }

    @Override
    public int hashCode() {
        int result = emissionsDataContainer != null ? emissionsDataContainer.hashCode() : 0;
        result = 31 * result + (travelTimesPV != null ? travelTimesPV.hashCode() : 0);
        result = 31 * result + (vehicleKilometersPV != null ? vehicleKilometersPV.hashCode() : 0);
        return result;
    }
    public void setVehicleHoursPV(Effect vehicleHoursPV){
        this.vehicleHoursPV = vehicleHoursPV;
    }
    public Effect getVehicleHoursPV(){
        return vehicleHoursPV;
    }

    public static final record Effect(Double overall, Double induced, Double shifted) {

    }
}
