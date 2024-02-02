package org.tub.vsp.data.container.base;

import org.tub.vsp.data.type.Emission;

import java.util.Objects;

public class PhysicalEffectDataContainer {
    private EmissionsDataContainer emissionsDataContainer;
    private Effect travelTimes;
    private Effect vehicleKilometers;

    public EmissionsDataContainer getEmissionsDataContainer() {
        return emissionsDataContainer;
    }

    public Double getEmission(Emission emission) {
        return emissionsDataContainer.emissions()
                                     .get(emission);
    }

    public PhysicalEffectDataContainer setEmissionsDataContainer(EmissionsDataContainer emissionsDataContainer) {
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

        PhysicalEffectDataContainer that = (PhysicalEffectDataContainer) o;

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

    public static final record Effect(double overall, double induced, double shifted) {

    }
}
