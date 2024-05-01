package org.tub.vsp.bvwp.io;

import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.tub.vsp.bvwp.data.LocalFileAccessor;
import org.tub.vsp.bvwp.data.container.analysis.StreetAnalysisDataContainer;
import org.tub.vsp.bvwp.data.container.base.street.StreetBaseDataContainer;
import org.tub.vsp.bvwp.scraping.StreetScraper;

import java.io.IOException;
import java.util.List;

public class StreetCsvWriterTest {

    @Test
    void testWriteCsv() throws IOException {
        StreetScraper streetScraper = new StreetScraper();
        StreetBaseDataContainer streetBaseData = streetScraper.extractBaseData(LocalFileAccessor.getLocalDocument(
                "a20.html")).orElseThrow();

        StreetCsvWriter csvWriter = new StreetCsvWriter("output/a20.csv");
        csvWriter.writeCsv(List.of(new StreetAnalysisDataContainer(streetBaseData, 123456.789) ) );

        Assertions.assertTrue(
                FileUtils.contentEqualsIgnoreEOL(FileUtils.getFile("src/test/resources/testData/referenceData/a20.csv"),
                        FileUtils.getFile("output/a20.csv"), "UTF-8"));
    }
}
