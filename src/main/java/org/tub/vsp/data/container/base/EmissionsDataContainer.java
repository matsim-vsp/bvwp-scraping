package org.tub.vsp.data.container.base;

import org.tub.vsp.data.type.Emission;
import org.tub.vsp.data.type.VehicleEmissions;

import java.util.Map;

public record EmissionsDataContainer(Map<Emission, VehicleEmissions> emissions, Double co2Overall) {
    public static EmissionsDataContainer empty() {
        return new EmissionsDataContainer(Map.of(), null);
    }
}
