package org.tub.vsp.bvwp.data.container.base.rail;

public class RailPhysicalEffectDataContainer {
    private RailEmissionsDataContainer emissionsDataContainer;

    public RailEmissionsDataContainer getEmissionsDataContainer() {
        return emissionsDataContainer;
    }

    public RailPhysicalEffectDataContainer setEmissionsDataContainer(RailEmissionsDataContainer emissionsDataContainer) {
        this.emissionsDataContainer = emissionsDataContainer;
        return this;
    }
}
