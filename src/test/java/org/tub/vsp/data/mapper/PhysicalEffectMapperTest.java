package org.tub.vsp.data.mapper;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.tub.vsp.data.LocalFileAccessor;
import org.tub.vsp.data.container.base.PhysicalEffectDataContainer;

import java.io.IOException;

class PhysicalEffectMapperTest {
    @Test
    void test() throws IOException {
        PhysicalEffectDataContainer physicalEffectDataContainer =
                new PhysicalEffectMapper().mapDocument(LocalFileAccessor.getLocalDocument("a20.html"));

        Assertions.assertEquals(physicalEffectDataContainer.getVehicleKilometers(),
                new PhysicalEffectDataContainer.Effect(131.53, 143.95, 9.75));
        Assertions.assertEquals(physicalEffectDataContainer.getTravelTimes(),
                new PhysicalEffectDataContainer.Effect(-18.56, 1.99, 0.2));

        //emissions are tested in EmissionsMapperTest
    }

}