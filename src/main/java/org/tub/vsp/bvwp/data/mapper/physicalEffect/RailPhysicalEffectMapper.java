package org.tub.vsp.bvwp.data.mapper.physicalEffect;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jsoup.nodes.Document;
import org.tub.vsp.bvwp.data.container.base.rail.RailPhysicalEffectsDataContainer;
import org.tub.vsp.bvwp.data.mapper.physicalEffect.emissions.RailEmissionsMapper;

public class RailPhysicalEffectMapper {
    private static final Logger logger = LogManager.getLogger(RailPhysicalEffectMapper.class);

    public static RailPhysicalEffectsDataContainer mapDocument( Document document ) {
        final RailPhysicalEffectsDataContainer physicalDataContainer = new RailPhysicalEffectsDataContainer();
        physicalDataContainer.setEmissionsDataContainer(RailEmissionsMapper.mapDocument(document ) );
        return physicalDataContainer;
    }
}
