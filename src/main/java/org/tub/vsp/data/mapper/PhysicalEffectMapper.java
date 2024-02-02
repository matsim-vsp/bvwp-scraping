package org.tub.vsp.data.mapper;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.tub.vsp.JSoupUtils;
import org.tub.vsp.data.container.base.PhysicalEffectDataContainer;

import java.text.ParseException;
import java.util.Optional;

public class PhysicalEffectMapper {
    private static final Logger logger = LogManager.getLogger(PhysicalEffectMapper.class);

    public PhysicalEffectDataContainer mapDocument(Document document) {
        PhysicalEffectDataContainer physicalEffectDataContainer =
                new PhysicalEffectDataContainer().setEmissionsDataContainer(new EmissionsMapper().mapDocument(document));

        Optional<Element> table = JSoupUtils.getTableByClassAndContainedText(document, "table.table_wirkung_strasse",
                "Verkehrswirkungen im Planfall");

        if (table.isEmpty()) {
            return physicalEffectDataContainer;
        }

        JSoupUtils.getFirstRowIndexWithText(table.get(), "Veränderung der Reisezeit im PV")
                  .ifPresent(i -> physicalEffectDataContainer.setTravelTimes(extractEffect(table.get(), i)));

        JSoupUtils.getFirstRowIndexWithText(table.get(), "Veränderung der Betriebsleistung im Personenverkehr")
                  .ifPresent(i -> physicalEffectDataContainer.setVehicleKilometers(extractEffect(table.get(), i)));

        return physicalEffectDataContainer;
    }

    private PhysicalEffectDataContainer.Effect extractEffect(Element table, int firsRow) {
        try {
            Double overall = JSoupUtils.parseDouble(JSoupUtils.getTextFromRowAndCol(table, firsRow, 1));
            Double induced = JSoupUtils.parseDouble(JSoupUtils.getTextFromRowAndCol(table, firsRow + 2, 1));
            Double shifted = JSoupUtils.parseDouble(JSoupUtils.getTextFromRowAndCol(table, firsRow + 3, 1));
            return new PhysicalEffectDataContainer.Effect(overall, induced, shifted);
        } catch (ParseException e) {
            logger.warn("Could not parse effect value from {}", table);
            return null;
        }
    }
}
