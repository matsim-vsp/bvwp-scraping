package org.tub.vsp.bvwp.io;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.tub.vsp.bvwp.data.LocalFileAccessor;
import org.tub.vsp.bvwp.data.container.base.rail.RailBaseDataContainer;
import org.tub.vsp.bvwp.scraping.RailScraper;

import java.io.IOException;

public class RailCsvWriterTest {

    @Test
    void testWriteCsv() throws IOException {
        RailScraper railScraper = new RailScraper();
        RailBaseDataContainer railBaseData = railScraper.extractBaseData(LocalFileAccessor.getLocalDocument(
                "rrx_v02.html")).orElseThrow();

        Assertions.assertNotNull(railBaseData);

        //TODO add test when csv writer is implemented

//        StreetCsvWriter csvWriter = new StreetCsvWriter("output/a20.csv");
//        csvWriter.writeCsv(List.of(new StreetAnalysisDataContainer(streetBaseData, 123456.789)));
//
//        Assertions.assertTrue(
//                FileUtils.contentEqualsIgnoreEOL(FileUtils.getFile("src/test/resources/testData/referenceData/a20.csv"),
//                        FileUtils.getFile("output/a20.csv"), "UTF-8"));
    }
}
