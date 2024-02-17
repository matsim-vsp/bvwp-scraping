package org.tub.vsp.bvwp.io;

import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.tub.vsp.bvwp.data.container.base.StreetBaseDataContainer;
import org.tub.vsp.bvwp.data.container.analysis.StreetAnalysisDataContainer;

import java.io.IOException;
import java.util.List;

public class StreetCsvWriterTest {
    @Test
    void testWriteCsv() throws IOException {
        JsonIo jsonIo = new JsonIo();
        String filePath = "src/test/resources/testData/referenceData/a20.json";
        StreetBaseDataContainer deserializedContainer = jsonIo.readJson(filePath, StreetBaseDataContainer.class );

        StreetCsvWriter csvWriter = new StreetCsvWriter("output/a20.csv");
        csvWriter.writeCsv(List.of(new StreetAnalysisDataContainer(deserializedContainer)));

        Assertions.assertTrue(
                FileUtils.contentEqualsIgnoreEOL(FileUtils.getFile("src/test/resources/testData/referenceData/a20.csv"),
                        FileUtils.getFile("output/a20.csv"), "UTF-8"));
    }
}
