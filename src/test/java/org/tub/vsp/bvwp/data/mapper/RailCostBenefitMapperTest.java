package org.tub.vsp.bvwp.data.mapper;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.tub.vsp.bvwp.data.LocalFileAccessor;
import org.tub.vsp.bvwp.data.container.base.RailBenefitPassengerDataContainer;
import org.tub.vsp.bvwp.data.container.base.RailCostBenefitAnalysisDataContainer;
import org.tub.vsp.bvwp.data.type.Benefit;

import java.io.IOException;


class RailCostBenefitMapperTest {
    @Test
    void test() throws IOException {
        RailCostBenefitAnalysisDataContainer railProjectInformationDataContainer =
                RailCostBenefitMapper.mapDocument(LocalFileAccessor.getLocalDocument("rrx_v02.html"));

        RailBenefitPassengerDataContainer pb = railProjectInformationDataContainer.getPassengerBenefits();
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

}