package org.tub.vsp.bvwp.data.mapper.costBenefit;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.tub.vsp.bvwp.data.LocalFileAccessor;
import org.tub.vsp.bvwp.data.container.base.rail.RailBenefitFreightDataContainer;
import org.tub.vsp.bvwp.data.container.base.rail.RailBenefitPassengerDataContainer;
import org.tub.vsp.bvwp.data.container.base.rail.RailCostBenefitAnalysisDataContainer;
import org.tub.vsp.bvwp.data.type.Benefit;
import org.tub.vsp.bvwp.data.type.InvestmentCosts;

import java.io.IOException;


class RailInvestmentCostsBenefitMapperTest{
    @Test
    void testPassengerBenefits_rrx() throws IOException {
        RailCostBenefitAnalysisDataContainer railCostBenefit =
                RailCostBenefitMapper.mapDocument(LocalFileAccessor.getLocalDocument("rrx_v02.html"));

        RailBenefitPassengerDataContainer pb = railCostBenefit.getPassengerBenefits();
        Assertions.assertEquals(new Benefit(33305., 734.4), pb.getNbPkw());
        Assertions.assertEquals(new Benefit(-22178., -489.), pb.getNbSpv());
        Assertions.assertEquals(new Benefit(2676., 59.), pb.getNbLuft());

        Assertions.assertEquals(new Benefit(2654., 58.5), pb.getNaPkw());
        Assertions.assertEquals(new Benefit(-2083., -45.9), pb.getNaSpv());
        Assertions.assertEquals(new Benefit(788., -45.9), pb.getNaLuft());

        Assertions.assertEquals(new Benefit(4296., 94.7), pb.getNsPkw());
        Assertions.assertEquals(new Benefit(-746., -16.5), pb.getNsSpv());

        Assertions.assertEquals(new Benefit(28906., 637.4), pb.getNrzVerbVerkehr());
        Assertions.assertEquals(new Benefit(-8664., -191.), pb.getNrzInduzVerkehr());
        Assertions.assertEquals(new Benefit(-11776., -259.7), pb.getNrzVerlagerungPkwSpv());
        Assertions.assertEquals(new Benefit(-1612., -35.5), pb.getNrzVerlagerungLuftSpv());

        Assertions.assertEquals(new Benefit(13246., 292.1), pb.getNiInduzVerkehr());
        Assertions.assertEquals(new Benefit(24780., 546.4), pb.getNiVerlagerungPkwSpv());
        Assertions.assertEquals(new Benefit(402., 8.9), pb.getNiVerlagerungLuftSpv());

    }

    @Test
    void testFreightBenefits_hh_ha() throws IOException {
        RailCostBenefitAnalysisDataContainer railCostBenefit =
                RailCostBenefitMapper.mapDocument(LocalFileAccessor.getLocalDocument("2-003-v01.html"));

        RailBenefitFreightDataContainer fb = railCostBenefit.getFreightBenefits();

        Assertions.assertEquals(new Benefit(80425., 1599.6), fb.getNbLkw());
        Assertions.assertEquals(new Benefit(-13615., -270.8), fb.getNbSchiene());
        Assertions.assertEquals(new Benefit(612., 12.2), fb.getNbSchiff());

        Assertions.assertEquals(new Benefit(9453., 188.), fb.getNaLkw());
        Assertions.assertEquals(new Benefit(-1997., -39.7), fb.getNaSchiene());
        Assertions.assertEquals(new Benefit(72., 1.4), fb.getNaSchiff());

        Assertions.assertEquals(new Benefit(2844., 56.6), fb.getNsLkw());
        Assertions.assertEquals(new Benefit(-537., -10.7), fb.getNsSchiene());
        Assertions.assertEquals(new Benefit(2., 0.), fb.getNsSchiff());

        Assertions.assertEquals(new Benefit(3373., 67.1), fb.getNtzVerbVerkehr());
        Assertions.assertEquals(new Benefit(-5160., -102.6), fb.getNtzLkwSchiene());
        Assertions.assertEquals(new Benefit(95., 1.9), fb.getNtzSchiffSchiene());

        Assertions.assertEquals(new Benefit(-3907., -77.7), fb.getNiLkwSchiene());
        Assertions.assertEquals(new Benefit(-248., -4.9), fb.getNiSchiffSchiene());

        Assertions.assertEquals(new Benefit(2767., 55.), fb.getNzVerbVerkehr());
    }

    @Test
    void testOverallCost_hh_ha() throws IOException {
        RailCostBenefitAnalysisDataContainer railCostBenefit =
                RailCostBenefitMapper.mapDocument(LocalFileAccessor.getLocalDocument("2-003-v01.html"));

        Assertions.assertEquals(new InvestmentCosts(2360.7, 2036.5), railCostBenefit.getCost() );
    }

    @Test
    void testOverallBenefit_hh_ha() throws IOException {
        RailCostBenefitAnalysisDataContainer railCostBenefit =
                RailCostBenefitMapper.mapDocument(LocalFileAccessor.getLocalDocument("2-003-v01.html"));

        Assertions.assertEquals(new Benefit(-1340.0, -26.6), railCostBenefit.getNl());
        Assertions.assertEquals(new Benefit(221149.0, 4398.6), railCostBenefit.getOverallBenefit());
    }

    @Test
    void testOverallCost_rrx() throws IOException {
        RailCostBenefitAnalysisDataContainer railCostBenefit =
                RailCostBenefitMapper.mapDocument(LocalFileAccessor.getLocalDocument("rrx_v02.html"));

        Assertions.assertEquals(new Benefit(-49.0, -1.1), railCostBenefit.getNl());
        Assertions.assertEquals(new Benefit(62258.0, 1372.7), railCostBenefit.getOverallBenefit());
    }
}
