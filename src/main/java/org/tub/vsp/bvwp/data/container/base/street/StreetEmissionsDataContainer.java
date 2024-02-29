package org.tub.vsp.bvwp.data.container.base.street;

import org.tub.vsp.bvwp.data.type.Emission;
import org.tub.vsp.bvwp.data.type.VehicleEmissions;

import java.util.Map;

public record StreetEmissionsDataContainer(Map<Emission, VehicleEmissions> emissions, Double co2Overall) {
    public static StreetEmissionsDataContainer empty() {
        return new StreetEmissionsDataContainer(Map.of(), null);
    }
}
