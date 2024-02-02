package org.tub.vsp.data.mapper;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.tub.vsp.JSoupUtils;
import org.tub.vsp.data.container.base.EmissionsDataContainer;
import org.tub.vsp.data.type.Emission;

import java.text.ParseException;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

public class EmissionsMapper {
    private static final Logger logger = LogManager.getLogger(EmissionsMapper.class);

    public EmissionsDataContainer mapDocument(Document document) {
        EmissionsDataContainer emissions = EmissionsDataContainer.empty();

        Optional<Element> table = JSoupUtils.getTableByClassAndContainedText(document, "table.table_wirkung_strasse",
                "Veränderung der Abgasemissionen");
        if (table.isEmpty()) {
            return emissions;
        }

        //Creating the map for the emissions data container
        Map<Emission, Double> collect =
                Emission.STRING_IDENTIFIER_BY_EMISSION_WITHOUT_CO2_OVERALL
                        .entrySet()
                        .stream()
                        .map(e -> getEmissionDoubleEntry(e, table.get()))
                        .filter(Optional::isPresent)
                        .map(Optional::get)
                        .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

        Optional<Element> envTable = JSoupUtils.getTableByClassAndContainedText(document, "table.table_webprins",
                "Äquivalenten aus Lebenszyklusemissionen");

        if (envTable.isEmpty()) {
            logger.warn("Could not find table with entry {}.", "Äquivalenten aus Lebenszyklusemissionen");
            return emissions;
        }

        collect.put(Emission.CO2_OVERALL_EQUIVALENTS, getCO2Overall(envTable.get()));

        return new EmissionsDataContainer(collect);
    }

    //get emission value for one specific emission
    private Optional<Map.Entry<Emission, Double>> getEmissionDoubleEntry(Map.Entry<Emission, String> e, Element table) {
        Optional<Double> v;
        try {
            v = mapEmissionFromRow(table, e.getValue());
        } catch (ParseException ex) {
            logger.warn("Could not parse emission value for {}", e.getKey());
            return Optional.empty();
        }
        return v.map(d -> Map.entry(e.getKey(), d));
    }

    private Optional<Double> mapEmissionFromRow(Element table, String key) throws ParseException {
        Optional<Element> row = JSoupUtils.firstRowWithKeyContainedInCol(table, key, 0);
        if (row.isEmpty()) {
            logger.warn("Could not find row with key {}.", key);
            return Optional.empty();
        }

        return Optional.of(JSoupUtils.parseDouble(row.get()
                                                     .child(3)
                                                     .text()));
    }

    //mapping the CO2 overall equivalents value
    private Double getCO2Overall(Element table) {
        return JSoupUtils.firstRowWithKeyContainedInCol(table, Emission.STRING_IDENTIFIER_CO2_OVERALL_EQUIVALENTS, 1)
                         .map(r -> {
                             try {
                                 return JSoupUtils.parseDouble(r.child(2)
                                                                .text());
                             } catch (ParseException e) {
                                 throw new RuntimeException(e);
                             }
                         })
                         .orElse(null);
    }
}
