package org.tub.vsp.data.mapper;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.tub.vsp.data.LocalFileAccessor;
import org.tub.vsp.data.container.base.ProjectInformationDataContainer;
import org.tub.vsp.data.type.Severity;

import java.io.IOException;

class ProjectInformationMapperTest {
    @Test
    void testMapping() throws IOException {
        ProjectInformationMapper mapper = new ProjectInformationMapper();
        ProjectInformationDataContainer mappingResult = mapper.mapDocument(LocalFileAccessor.getLocalDocument("a20" +
                ".html"));

        Assertions.assertEquals("A20-G10-NI-SH", mappingResult.getProjectNumber());
        Assertions.assertEquals("A 20", mappingResult.getStreet());
        Assertions.assertEquals(Severity.VB, mappingResult.getSeverity());
    }
}