package org.tub.vsp.io;

import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.tub.vsp.data.container.StreetBaseDataContainer;

import java.io.IOException;
import java.util.List;

public class StreetCsvWriterTest {
    @Test
    void testWriteCsv() throws IOException {
        JsonIo jsonIo = new JsonIo();
        String filePath = "src/test/resources/testData/referenceData/a20.json";
        StreetBaseDataContainer deserializedContainer = jsonIo.readJson(filePath, StreetBaseDataContainer.class);

        StreetCsvWriter csvWriter = new StreetCsvWriter("output/a20.csv");
        csvWriter.writeCsv(List.of(deserializedContainer));

        Assertions.assertTrue(
                FileUtils.contentEquals(FileUtils.getFile("src/test/resources/testData/referenceData/a20.csv"),
                        FileUtils.getFile("output/a20.csv")));
    }
}
