package org.tub.vsp.bvwp.data.container.analysis;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.tub.vsp.bvwp.data.container.base.rail.RailBaseDataContainer;

public class RailAnalysisDataContainer {
    Logger logger = LogManager.getLogger(RailAnalysisDataContainer.class);

    private final RailBaseDataContainer baseDataContainer;

    public RailAnalysisDataContainer(RailBaseDataContainer baseDataContainer) {
        this.baseDataContainer = baseDataContainer;
    }

    //add analysis stuff here...
}
