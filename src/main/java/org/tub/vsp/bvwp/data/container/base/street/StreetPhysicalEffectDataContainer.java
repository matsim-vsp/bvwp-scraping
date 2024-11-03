package org.tub.vsp.bvwp.data.container.base.street;

import org.tub.vsp.bvwp.data.type.Emission;

import java.util.Objects;

public class StreetPhysicalEffectDataContainer {
    private StreetEmissionsDataContainer emissionsDataContainer;
    private PEffect travelTimes;
    private PEffect pVehicleKilometers;
    private PEffect vehicleHours;
    private Double lVehicleKilometers;

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

    public PEffect getTravelTimes() {
        return travelTimes;
    }

    public void setTravelTimes( PEffect travelTimes ) {
        this.travelTimes = travelTimes;
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
        if (!Objects.equals(travelTimes, that.travelTimes)) {
            return false;
        }
        return Objects.equals( pVehicleKilometers, that.pVehicleKilometers );
    }

    @Override
    public int hashCode() {
        int result = emissionsDataContainer != null ? emissionsDataContainer.hashCode() : 0;
        result = 31 * result + (travelTimes != null ? travelTimes.hashCode() : 0);
        result = 31 * result + (pVehicleKilometers != null ? pVehicleKilometers.hashCode() : 0);
        return result;
    }
    public void setVehicleHours( PEffect vehicleHours ){
        this.vehicleHours = vehicleHours;
    }
    public PEffect getVehicleHours(){
        return vehicleHours;
    }

    public static final record PEffect(Double overall, Double induced, Double shifted) {

    }
}
