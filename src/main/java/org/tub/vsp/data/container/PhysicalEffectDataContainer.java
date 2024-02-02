package org.tub.vsp.data.container;

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

        return Objects.equals(emissionsDataContainer, that.emissionsDataContainer);
    }

    @Override
    public int hashCode() {
        return emissionsDataContainer != null ? emissionsDataContainer.hashCode() : 0;
    }

    public static final record Effect(double overall, double induced, double shifted) {

    }
}
