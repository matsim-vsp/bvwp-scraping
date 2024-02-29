package org.tub.vsp.bvwp.data.container.base.rail;

import org.tub.vsp.bvwp.data.type.Emission;

import java.util.Map;

public record RailEmissionsDataContainer(Map<Emission, Double> emissions) {
}
