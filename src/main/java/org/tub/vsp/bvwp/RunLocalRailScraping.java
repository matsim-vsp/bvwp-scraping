package org.tub.vsp.bvwp;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.tub.vsp.bvwp.data.container.analysis.RailAnalysisDataContainer;
import org.tub.vsp.bvwp.io.RailCsvWriter;
import org.tub.vsp.bvwp.scraping.RailScraper;

import java.util.List;

public class RunLocalRailScraping {
    private static final Logger logger = LogManager.getLogger(RunLocalRailScraping.class);

    public static void main(String[] args) {
        RailScraper scraper = new RailScraper();

        logger.info("Starting scraping");
        List<RailAnalysisDataContainer> allRailData = scraper.extractAllLocalBaseData("./data/rail/all", "", "^2.*", "")
                                                             .stream()
                                                             .map(RailAnalysisDataContainer::new)
                                                             .toList();

        logger.info("Writing csv");
        RailCsvWriter csvWriter = new RailCsvWriter();

        //TODO
    }
}
