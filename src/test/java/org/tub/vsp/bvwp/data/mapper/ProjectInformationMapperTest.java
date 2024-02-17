package org.tub.vsp.bvwp.data.mapper;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.tub.vsp.bvwp.data.type.Priority;
import org.tub.vsp.bvwp.data.LocalFileAccessor;
import org.tub.vsp.bvwp.data.container.base.ProjectInformationDataContainer;

import java.io.IOException;

class ProjectInformationMapperTest {
    @Test
    void testMapping() throws IOException {
        ProjectInformationMapper mapper = new ProjectInformationMapper();
        ProjectInformationDataContainer mappingResult = mapper.mapDocument(LocalFileAccessor.getLocalDocument("a20" +
                ".html"));

        Assertions.assertEquals("A20-G10-NI-SH", mappingResult.getProjectNumber());
        Assertions.assertEquals("A 20", mappingResult.getStreet());
        Assertions.assertEquals( Priority.VB, mappingResult.getPriority() );
    }
}
