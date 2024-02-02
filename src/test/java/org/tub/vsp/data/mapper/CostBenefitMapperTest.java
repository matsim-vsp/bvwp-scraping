package org.tub.vsp.data.mapper;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.tub.vsp.data.LocalFileAccessor;
import org.tub.vsp.data.container.base.CostBenefitAnalysisDataContainer;
import org.tub.vsp.data.type.Benefit;
import org.tub.vsp.data.type.Cost;

import java.io.IOException;

class CostBenefitMapperTest {
    @Test
    void testMapper() throws IOException {
        CostBenefitMapper costBenefitMapper = new CostBenefitMapper();
        CostBenefitAnalysisDataContainer result =
                costBenefitMapper.mapDocument(LocalFileAccessor.getLocalDocument("a20.html"));

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
        Assertions.assertEquals(new Cost(3145.75, 2737.176), result.getCost());
    }
}