package org.tub.vsp.data.container;

import java.util.Objects;

public class PhysicalEffectDataContainer {
    //TODO add other
    private EmissionsDataContainer emissionsDataContainer;

    public EmissionsDataContainer getEmissionsDataContainer() {
        return emissionsDataContainer;
    }

    public PhysicalEffectDataContainer setEmissionsDataContainer(EmissionsDataContainer emissionsDataContainer) {
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

        PhysicalEffectDataContainer that = (PhysicalEffectDataContainer) o;

        return Objects.equals(emissionsDataContainer, that.emissionsDataContainer);
    }

    @Override
    public int hashCode() {
        return emissionsDataContainer != null ? emissionsDataContainer.hashCode() : 0;
    }
}
