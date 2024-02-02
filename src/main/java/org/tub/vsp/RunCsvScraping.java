package org.tub.vsp;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.tub.vsp.data.container.StreetBaseDataContainer;
import org.tub.vsp.io.StreetCsvWriter;
import org.tub.vsp.scraping.StreetScraper;

import java.util.List;

public class RunCsvScraping {
    private static final Logger logger = LogManager.getLogger(RunCsvScraping.class);

    public static void main(String[] args) {
        StreetScraper scraper = new StreetScraper();

        logger.info("Starting scraping");
        List<StreetBaseDataContainer> allStreetBaseData = scraper.extractAllBaseData();

        logger.info("Writing csv");
        StreetCsvWriter csvWriter = new StreetCsvWriter("output/street_data.csv");
        csvWriter.writeCsv(allStreetBaseData);
    }
}
