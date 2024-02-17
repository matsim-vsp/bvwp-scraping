package org.tub.vsp.bvwp.data.container.base;

import org.tub.vsp.bvwp.data.type.Emission;
import org.tub.vsp.bvwp.data.type.VehicleEmissions;

import java.util.Map;

public record EmissionsDataContainer(Map<Emission, VehicleEmissions> emissions, Double co2Overall) {
    public static EmissionsDataContainer empty() {
        return new EmissionsDataContainer(Map.of(), null);
    }
}
