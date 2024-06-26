package org.tub.vsp.bvwp.data.container.base.street;

import org.tub.vsp.bvwp.data.type.Emission;

import java.util.Objects;

public class StreetPhysicalEffectDataContainer {
    private StreetEmissionsDataContainer emissionsDataContainer;
    private Effect travelTimes;
    private Effect vehicleKilometers;
    private Effect vehicleHours;

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

    public Effect getTravelTimes() {
        return travelTimes;
    }

    public void setTravelTimes(Effect travelTimes) {
        this.travelTimes = travelTimes;
    }

    public Effect getVehicleKilometers() {
        return vehicleKilometers;
    }

    public void setVehicleKilometers(Effect vehicleKilometers) {
        this.vehicleKilometers = vehicleKilometers;
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
        if (!Objects.equals(travelTimes, that.travelTimes)) {
            return false;
        }
        return Objects.equals(vehicleKilometers, that.vehicleKilometers);
    }

    @Override
    public int hashCode() {
        int result = emissionsDataContainer != null ? emissionsDataContainer.hashCode() : 0;
        result = 31 * result + (travelTimes != null ? travelTimes.hashCode() : 0);
        result = 31 * result + (vehicleKilometers != null ? vehicleKilometers.hashCode() : 0);
        return result;
    }
    public void setVehicleHours( Effect vehicleHours ){
        this.vehicleHours = vehicleHours;
    }
    public Effect getVehicleHours(){
        return vehicleHours;
    }

    public static final record Effect(Double overall, Double induced, Double shifted) {

    }
}
