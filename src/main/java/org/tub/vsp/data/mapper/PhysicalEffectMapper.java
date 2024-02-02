package org.tub.vsp.data.mapper;

import org.jsoup.nodes.Document;
import org.tub.vsp.data.container.PhysicalEffectDataContainer;

public class PhysicalEffectMapper {
    public PhysicalEffectDataContainer mapDocument(Document document) {
        return new PhysicalEffectDataContainer().setEmissionsDataContainer(new EmissionsMapper().mapDocument(document));
    }
}
