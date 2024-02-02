package org.tub.vsp.data.container.base;

import org.tub.vsp.data.type.Emission;

import java.util.Map;
import java.util.Objects;

public record EmissionsDataContainer(Map<Emission, Double> emissions) {

    public static EmissionsDataContainer empty() {
        return new EmissionsDataContainer(Map.of());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        EmissionsDataContainer that = (EmissionsDataContainer) o;

        return Objects.equals(emissions, that.emissions);
    }

    @Override
    public int hashCode() {
        return emissions != null ? emissions.hashCode() : 0;
    }
}
