package org.tub.vsp.bvwp.data.container.base.street;

import org.tub.vsp.bvwp.data.type.Emission;

import java.util.Objects;

public class StreetPhysicalEffectDataContainer {
    private StreetEmissionsDataContainer emissionsDataContainer;
    private PhysicalEffect pvVehicleHours; //Personenverkehr (Pkw) (Fzg-h)
    private PhysicalEffect pvVehicleKilometers; //Personenverkehr (Pkw) Fzg-km
    private PhysicalEffect pvPersonHours; //Personenverkehr Personen-h
    private PhysicalEffect gvVehicleHours; //Güterverkehr (Lkw) Fzg-h
    private Double gvVehicleKilometers; //Güterverkehr (Lkw) Fzg-km

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

    public PhysicalEffect getPvVehicleHours() {
        return pvVehicleHours;
    }

    public void setPvVehicleHours(PhysicalEffect pvVehicleHours) {
        this.pvVehicleHours = pvVehicleHours;
    }

    public PhysicalEffect getPvVehicleKilometers() {
        return pvVehicleKilometers;
    }

    public void setPvVehicleKilometers(PhysicalEffect pvVehicleKilometers ) {
        this.pvVehicleKilometers = pvVehicleKilometers;
    }

    public PhysicalEffect getPvPersonHours() {
        return pvPersonHours;
    }

    public void setPvPersonHours(PhysicalEffect pvPersonHours) {
        this.pvPersonHours = pvPersonHours;
    }

    public Double getGvVehicleKilometers() {
        return gvVehicleKilometers;
    }

    public void setGvVehicleKilometers(Double gvVehicleKilometers ) {
        this.gvVehicleKilometers = gvVehicleKilometers;
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
        if (!Objects.equals(pvVehicleHours, that.pvVehicleHours)) {
            return false;
        }
        return Objects.equals(pvVehicleKilometers, that.pvVehicleKilometers);
    }

    @Override
    public int hashCode() {
        int result = emissionsDataContainer != null ? emissionsDataContainer.hashCode() : 0;
        result = 31 * result + (pvVehicleHours != null ? pvVehicleHours.hashCode() : 0);
        result = 31 * result + (pvVehicleKilometers != null ? pvVehicleKilometers.hashCode() : 0);
        return result;
    }
    public void setGvVehicleHours(PhysicalEffect gvVehicleHours){
        this.gvVehicleHours = gvVehicleHours;
    }
    public PhysicalEffect getGvVehicleHours(){
        return gvVehicleHours;
    }


    public record PhysicalEffect(Double overall, Double induced, Double shifted) {

    }
}
