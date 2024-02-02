package org.tub.vsp.data.mapper;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.tub.vsp.data.LocalFileAccessor;
import org.tub.vsp.data.container.base.EmissionsDataContainer;
import org.tub.vsp.data.type.Emission;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

class EmissionsMapperTest {
    @Test
    void testEmissionsMapping() throws IOException {
        EmissionsDataContainer emissionsDataContainer =
                new EmissionsMapper().mapDocument(LocalFileAccessor.getLocalDocument("a20.html"));

        Map<Emission, Double> expected = new HashMap<>();
        expected.put(Emission.NOX, 66.85);
        expected.put(Emission.CO, 1305.85);
        expected.put(Emission.CO2, 48689.94);
        expected.put(Emission.HC, 11.08);
        expected.put(Emission.PM, 2.86);
        expected.put(Emission.SO2, 0.63);
        expected.put(Emission.CO2_OVERALL_EQUIVALENTS, 90786.067);

        Assertions.assertEquals(expected, emissionsDataContainer.emissions());
    }
}