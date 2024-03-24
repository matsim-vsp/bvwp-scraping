package org.tub.vsp.bvwp.data.mapper.projectInformation;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.tub.vsp.bvwp.data.LocalFileAccessor;
import org.tub.vsp.bvwp.data.container.base.rail.RailProjectInformationDataContainer;
import org.tub.vsp.bvwp.data.type.Einstufung;

import java.io.IOException;

class RailProjectInformationMapperTest {

    @Test
    void testMapping() throws IOException {
        RailProjectInformationDataContainer railProjectInformationDataContainer =
                RailProjectInformationMapper.mapDocument(LocalFileAccessor.getLocalDocument("alpha_e.html"));

        Assertions.assertEquals("2-003-v02", railProjectInformationDataContainer.getProjectNumber());
        Assertions.assertEquals("\"optimiertes Alpha\" (Bezeichnung noch offen)",
                railProjectInformationDataContainer.getTitle());
        Assertions.assertEquals(188.10, railProjectInformationDataContainer.getLength());
//        Assertions.assertEquals( Einstufung.UNDEFINED, railProjectInformationDataContainer.getPriority() );
    }

}
