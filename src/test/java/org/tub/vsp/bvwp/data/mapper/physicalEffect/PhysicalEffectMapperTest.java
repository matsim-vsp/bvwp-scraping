package org.tub.vsp.bvwp.data.mapper.physicalEffect;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.tub.vsp.bvwp.data.LocalFileAccessor;
import org.tub.vsp.bvwp.data.container.base.street.StreetPhysicalEffectDataContainer;
import org.tub.vsp.bvwp.data.type.Emission;
import org.tub.vsp.bvwp.data.type.VehicleEmissions;

import java.io.IOException;
import java.util.Map;

class PhysicalEffectMapperTest {
    @Test
    void test() throws IOException {
        StreetPhysicalEffectDataContainer physicalEffectDataContainer =
                new PhysicalEffectMapper().mapDocument(LocalFileAccessor.getLocalDocument("a20.html"));

        Assertions.assertEquals(physicalEffectDataContainer.getVehicleKilometers(),
                new StreetPhysicalEffectDataContainer.Effect(131.53, 143.95, 9.75));
        Assertions.assertEquals(physicalEffectDataContainer.getTravelTimes(),
                new StreetPhysicalEffectDataContainer.Effect(-18.56, 1.99, 0.2));

        //emissions are tested in EmissionsMapperTest
    }

    @Test
    void testKnotenpunkte() throws IOException {
        StreetPhysicalEffectDataContainer physicalEffectDataContainer =
                new PhysicalEffectMapper().mapDocument(LocalFileAccessor.getLocalDocument("A003-G20-HE-T2-HE.html"));

        Assertions.assertEquals(physicalEffectDataContainer.getVehicleKilometers(),
                new StreetPhysicalEffectDataContainer.Effect(0.0, null, 0.0));

        Assertions.assertEquals(physicalEffectDataContainer.getTravelTimes(),
                new StreetPhysicalEffectDataContainer.Effect(-0.61, null, 0.0));

        Map<Emission, VehicleEmissions> emissions = physicalEffectDataContainer.getEmissionsDataContainer().emissions();
        Assertions.assertEquals(emissions.size(), 6);
        Assertions.assertEquals(emissions.get(Emission.NOX), new VehicleEmissions(-1.09, -7.48, -8.57));
        Assertions.assertEquals(emissions.get(Emission.CO), new VehicleEmissions(-2.55, -1.12, -3.66));
        Assertions.assertEquals(emissions.get(Emission.CO2), new VehicleEmissions(-1635.09, -4497.28, -6132.37));
        Assertions.assertEquals(emissions.get(Emission.HC), new VehicleEmissions(-0.07, -0.30, -0.36));
        Assertions.assertEquals(emissions.get(Emission.PM), new VehicleEmissions(-0.02, -0.06, -0.08));
        Assertions.assertEquals(emissions.get(Emission.SO2), new VehicleEmissions(-0.01, -0.02, -0.03));

        Assertions.assertNull(physicalEffectDataContainer.getEmissionsDataContainer().co2Overall());
    }

}
