package org.tub.vsp.bvwp.data.mapper.physicalEffect.emissions;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.tub.vsp.bvwp.data.LocalFileAccessor;
import org.tub.vsp.bvwp.data.container.base.rail.RailEmissionsDataContainer;
import org.tub.vsp.bvwp.data.type.Emission;

import java.io.IOException;
import java.util.Map;

public class RailEmissionsMapperTest {

    @Test
    void test() throws IOException {
        RailEmissionsDataContainer emissions =
                RailEmissionsMapper.mapDocument(LocalFileAccessor.getLocalDocument("2-003-v01.html"));

        Map<Emission, Double> expected = Map.of(
                Emission.NOX, -65.,
                Emission.CO, -69.,
                Emission.CO2, -74699.,
                Emission.HC, 6.,
                Emission.PM, -1.,
                Emission.SO2, -9.
        );

        Assertions.assertEquals(expected, emissions.emissions());
    }
}
