package org.tub.vsp;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.tub.vsp.data.container.analysis.StreetAnalysisDataContainer;
import org.tub.vsp.io.StreetCsvWriter;
import org.tub.vsp.scraping.StreetScraper;

import java.util.List;

public class RunCsvScraping {
    private static final Logger logger = LogManager.getLogger(RunCsvScraping.class);

    public static void main(String[] args) {
        StreetScraper scraper = new StreetScraper();

        logger.info("Starting scraping");
        List<StreetAnalysisDataContainer> allStreetBaseData = scraper.extractAllBaseData()
                                                                     .stream()
                                                                     .map(StreetAnalysisDataContainer::new)
                                                                     .toList();

        logger.info("Writing csv");
        StreetCsvWriter csvWriter = new StreetCsvWriter("output/street_data.csv");
        csvWriter.writeCsv(allStreetBaseData);
    }
}
