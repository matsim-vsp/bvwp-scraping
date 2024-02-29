package org.tub.vsp.bvwp.data.mapper;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.tub.vsp.bvwp.data.LocalFileAccessor;
import org.tub.vsp.bvwp.data.container.base.street.StreetProjectInformationDataContainer;
import org.tub.vsp.bvwp.data.mapper.projectInformation.StreetProjectInformationMapper;
import org.tub.vsp.bvwp.data.type.Priority;

import java.io.IOException;

class StreetProjectInformationMapperTest {
    @Test
    void testMapping() throws IOException {
        StreetProjectInformationMapper mapper = new StreetProjectInformationMapper();
        StreetProjectInformationDataContainer mappingResult = mapper.mapDocument(LocalFileAccessor.getLocalDocument(
                "a20" +
                        ".html"));

        Assertions.assertEquals("A20-G10-NI-SH", mappingResult.getProjectNumber());
        Assertions.assertEquals("A 20", mappingResult.getStreet());
        Assertions.assertEquals(Priority.VB, mappingResult.getPriority());
    }
}
