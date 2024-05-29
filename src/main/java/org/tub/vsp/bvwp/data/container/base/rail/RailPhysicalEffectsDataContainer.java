package org.tub.vsp.bvwp.data.container.base.rail;

import java.util.Objects;

public class RailPhysicalEffectsDataContainer{
    private RailEmissionsDataContainer emissionsDataContainer;

    public RailEmissionsDataContainer getEmissionsDataContainer() {
        return emissionsDataContainer;
    }

    public RailPhysicalEffectsDataContainer setEmissionsDataContainer( RailEmissionsDataContainer emissionsDataContainer ) {
        this.emissionsDataContainer = emissionsDataContainer;
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        RailPhysicalEffectsDataContainer that = (RailPhysicalEffectsDataContainer) o;

        return Objects.equals(emissionsDataContainer, that.emissionsDataContainer);
    }

    @Override
    public int hashCode() {
        return emissionsDataContainer != null ? emissionsDataContainer.hashCode() : 0;
    }
}
