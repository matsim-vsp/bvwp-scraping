package org.tub.vsp.bvwp.data.mapper.physicalEffect.emissions;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.tub.vsp.bvwp.JSoupUtils;
import org.tub.vsp.bvwp.data.container.base.rail.RailEmissionsDataContainer;
import org.tub.vsp.bvwp.data.type.Emission;

import java.text.ParseException;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

public class RailEmissionsMapper {
    private static final Logger logger = LogManager.getLogger(RailEmissionsMapper.class);

    public static RailEmissionsDataContainer mapDocument(Document document) {
        Optional<Element> table = JSoupUtils.getTableByKeyAndContainedText(document, "table.table_webprins",
                "Veränderung der Abgasemissionen");
        if (table.isEmpty()) {
            return RailEmissionsDataContainer.empty();
        }

        Map<Emission, Double> collect = Emission.RAIL_STRING_IDENTIFIER_BY_EMISSION
                .entrySet().stream()
                .map(e -> extractEmission(e, table.get()))
                .filter(Optional::isPresent)
                .map(Optional::get)
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

        return new RailEmissionsDataContainer(collect);
    }

    private static Optional<Map.Entry<Emission, Double>> extractEmission(Map.Entry<Emission, String> e, Element table) {
        Optional<String> value =
                JSoupUtils.getTextFromFirstRowWithKeyContainedInCol(table, e.getValue(), 0, 1);
        return value.map(v -> {
            try {
                return Map.entry(e.getKey(), JSoupUtils.parseDouble(v));
            } catch (ParseException ex) {
                logger.warn("Could not parse emission value for {}",
                        e.getKey());
                return null;
            }
        });
    }
}
