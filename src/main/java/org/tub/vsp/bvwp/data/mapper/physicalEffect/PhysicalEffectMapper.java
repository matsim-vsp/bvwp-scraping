package org.tub.vsp.bvwp.data.mapper.physicalEffect;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.tub.vsp.bvwp.JSoupUtils;
import org.tub.vsp.bvwp.data.container.base.street.StreetPhysicalEffectDataContainer;
import org.tub.vsp.bvwp.data.mapper.physicalEffect.emissions.StreetEmissionsMapper;

import java.util.Optional;

public class PhysicalEffectMapper {
    private static final Logger logger = LogManager.getLogger(PhysicalEffectMapper.class);

    public StreetPhysicalEffectDataContainer mapDocument(Document document) {
        StreetPhysicalEffectDataContainer physicalEffectDataContainer =
                new StreetPhysicalEffectDataContainer().setEmissionsDataContainer(StreetEmissionsMapper.mapDocument(document));

        Optional<Element> table = JSoupUtils.getTableByKeyAndContainedText(document, "table.table_wirkung_strasse",
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

    private Optional<Integer> getFirstRowIndexWithTextAfter(Element table, String key, int afterRow) {
        for (int i = afterRow; i < table.select("tr").size(); i++) {
            if (JSoupUtils.getTextFromRowAndCol(table, i, 0).contains(key)) {
                return Optional.of(i);
            }
        }
        return Optional.empty();
    }

    private StreetPhysicalEffectDataContainer.Effect extractEffect(Element table, int firsRow) {
        Double overall = JSoupUtils.parseDoubleOrElseNull(JSoupUtils.getTextFromRowAndCol(table, firsRow, 1));
        Double induced = getFirstRowIndexWithTextAfter(table, "induziertem Verkehr", firsRow)
                .map(row -> JSoupUtils.getTextFromRowAndCol(table, row, 1))
                .map(JSoupUtils::parseDoubleOrElseNull).orElse(null);
        Double shifted = getFirstRowIndexWithTextAfter(table, "verlagertem Verkehr", firsRow)
                .map(row -> JSoupUtils.getTextFromRowAndCol(table, row, 1))
                .map(JSoupUtils::parseDoubleOrElseNull).orElse(null);
        return new StreetPhysicalEffectDataContainer.Effect(overall, induced, shifted);
    }
}
