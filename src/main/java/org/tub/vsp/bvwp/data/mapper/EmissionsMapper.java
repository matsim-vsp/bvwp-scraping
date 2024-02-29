package org.tub.vsp.bvwp.data.mapper;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.tub.vsp.bvwp.JSoupUtils;
import org.tub.vsp.bvwp.data.container.base.EmissionsDataContainer;
import org.tub.vsp.bvwp.data.type.Emission;
import org.tub.vsp.bvwp.data.type.VehicleEmissions;

import java.text.ParseException;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

public class EmissionsMapper {
    public static final String STRING_IDENTIFIER_CO2_OVERALL_EQUIVALENTS = "Äquivalenten aus Lebenszyklusemissionen";

    private static final Logger logger = LogManager.getLogger(EmissionsMapper.class);

    public EmissionsDataContainer mapDocument(Document document) {

        Optional<Element> table = JSoupUtils.getTableByKeyAndContainedText(document, "table.table_wirkung_strasse",
                "Veränderung der Abgasemissionen");
        if (table.isEmpty()) {
            return EmissionsDataContainer.empty();
        }

        //Creating the map for the emissions data container
        Map<Emission, VehicleEmissions> collect =
                Emission.STRING_IDENTIFIER_BY_EMISSION
                        .entrySet()
                        .stream()
                        .map(e -> getEmissionDoubleEntry(e, table.get()))
                        .filter(Optional::isPresent)
                        .map(Optional::get)
                        .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

        Optional<Element> envTable = JSoupUtils.getTableByKeyAndContainedText(document, "table.table_webprins",
                "Äquivalenten aus Lebenszyklusemissionen");

        if (envTable.isEmpty()) {
            logger.warn("Could not find table with entry {}.", "Äquivalenten aus Lebenszyklusemissionen");
            return new EmissionsDataContainer(collect, null);
        }

        return new EmissionsDataContainer(collect, getCO2Overall(envTable.get()));
    }

    //get emission value for one specific emission
    private Optional<Map.Entry<Emission, VehicleEmissions>> getEmissionDoubleEntry(Map.Entry<Emission, String> e,
                                                                                   Element table) {
        Optional<VehicleEmissions> v;
        try {
            v = mapEmissionFromRow(table, e.getValue());
        } catch (ParseException ex) {
            logger.warn("Could not parse emission value for {}", e.getKey());
            return Optional.empty();
        }
        return v.map(d -> Map.entry(e.getKey(), d));
    }

    private Optional<VehicleEmissions> mapEmissionFromRow(Element table, String key) throws ParseException {
        Optional<Element> row = JSoupUtils.firstRowWithKeyContainedInCol(table, key, 0);
        if (row.isEmpty()) {
            logger.warn("Could not find row with key {}.", key);
            return Optional.empty();
        }

        Double pkw = JSoupUtils.parseDouble(row.get().child(1).text());
        Double lkw = JSoupUtils.parseDouble(row.get().child(2).text());
        Double kfz = JSoupUtils.parseDouble(row.get().child(3).text());
        VehicleEmissions vehicleEmissions = new VehicleEmissions(pkw, lkw, kfz);

        return Optional.of(vehicleEmissions);
    }

    //mapping the CO2 overall equivalents value
    private Double getCO2Overall(Element table) {
        return JSoupUtils.firstRowWithKeyContainedInCol(table, STRING_IDENTIFIER_CO2_OVERALL_EQUIVALENTS, 1)
                         .map(r -> {
                             try {
                                 return JSoupUtils.parseDouble(r.child(2).text());
                             } catch (ParseException e) {
                                 throw new RuntimeException(e);
                             }
                         })
                         .orElse(null);
    }
}
