package org.tub.vsp.bvwp.data.mapper.costBenefit;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.tub.vsp.bvwp.data.LocalFileAccessor;
import org.tub.vsp.bvwp.data.container.base.street.StreetCostBenefitAnalysisDataContainer;
import org.tub.vsp.bvwp.data.type.Benefit;
import org.tub.vsp.bvwp.data.type.InvestmentCosts;
import org.tub.vsp.bvwp.data.type.Durations;
import org.tub.vsp.bvwp.data.type.Emission;

import java.io.IOException;

class StreetInvestmentCostsBenefitMapperTest{
    @Test
    void testMapper() throws IOException {
        StreetCostBenefitAnalysisDataContainer result = StreetCostBenefitMapper.mapDocument(LocalFileAccessor.getLocalDocument("a20.html"));

        Assertions.assertEquals(new Benefit(40.55, 1005.26), result.getNb());
        Assertions.assertEquals(new Benefit(-31.675, -785.233), result.getNbOperations());
        Assertions.assertEquals(new Benefit(-5.294, -131.248), result.getNw());
        Assertions.assertEquals(new Benefit(24.694, 612.174), result.getNs());
        Assertions.assertEquals(new Benefit(103.081, 2555.429), result.getNrz());
        Assertions.assertEquals(new Benefit(10.159, 251.841), result.getNtz());
        Assertions.assertEquals(new Benefit(41.365, 1025.464), result.getNi());
        Assertions.assertEquals(new Benefit(-6.104, -151.319), result.getNl());
        Assertions.assertEquals(new Benefit(-16.059, -398.107), result.getNg());
        Assertions.assertEquals(new Benefit(0.136, 3.363), result.getNt());
        Assertions.assertEquals(new Benefit(29.997, 743.646), result.getNz());
        Assertions.assertEquals(new InvestmentCosts(3145.75, 2737.176), result.getInvCost() );

        Assertions.assertEquals(new Durations(120. / 12., 48. / 12., 42.), result.getDurations());
    }

    @Test
    void testNulls() throws IOException {
        StreetCostBenefitAnalysisDataContainer result = StreetCostBenefitMapper.mapDocument(LocalFileAccessor.getLocalDocument("a2.html"));

        Assertions.assertNotNull(result.getNa());
        Assertions.assertEquals(result.getNa().get(Emission.CO2), new Benefit(0.0, 0.0));
        Assertions.assertEquals(result.getNa().get(Emission.CO), new Benefit(0.0, 0.0));
        Assertions.assertEquals(result.getNa().get(Emission.NOX), new Benefit(0.0, 0.0));
        Assertions.assertEquals(result.getNa().get(Emission.HC), new Benefit(0.0, 0.0));
        Assertions.assertEquals(result.getNa().get(Emission.SO2), new Benefit(0.0, 0.0));
        Assertions.assertEquals(result.getNa().get(Emission.PM), new Benefit(0.0, 0.0));

        Assertions.assertEquals(new Durations(90. / 12., 56. / 12., 31.), result.getDurations());
    }
}
