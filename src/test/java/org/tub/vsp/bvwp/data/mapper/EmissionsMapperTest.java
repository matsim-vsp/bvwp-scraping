package org.tub.vsp.bvwp.data.mapper;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.tub.vsp.bvwp.data.LocalFileAccessor;
import org.tub.vsp.bvwp.data.container.base.street.StreetEmissionsDataContainer;
import org.tub.vsp.bvwp.data.mapper.physicalEffect.emissions.EmissionsMapper;
import org.tub.vsp.bvwp.data.type.Emission;
import org.tub.vsp.bvwp.data.type.VehicleEmissions;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

class EmissionsMapperTest {
    @Test
    void testEmissionsMapping() throws IOException {
        StreetEmissionsDataContainer emissionsDataContainer =
                new EmissionsMapper().mapDocument(LocalFileAccessor.getLocalDocument("a20.html"));

        Map<Emission, VehicleEmissions> expected = new HashMap<>();
        expected.put(Emission.NOX, new VehicleEmissions(103.65, -36.81, 66.85));
        expected.put(Emission.CO, new VehicleEmissions(1353.13, -47.28, 1305.85));
        expected.put(Emission.CO2, new VehicleEmissions(54773.28, -6083.34, 48689.94));
        expected.put(Emission.HC, new VehicleEmissions(12.54, -1.46, 11.08));
        expected.put(Emission.PM, new VehicleEmissions(3., -0.13, 2.86));
        expected.put(Emission.SO2, new VehicleEmissions(0.66, -.03, 0.63));

        Assertions.assertEquals(expected, emissionsDataContainer.emissions());
        Assertions.assertEquals(90786.067, emissionsDataContainer.co2Overall());
    }
}
