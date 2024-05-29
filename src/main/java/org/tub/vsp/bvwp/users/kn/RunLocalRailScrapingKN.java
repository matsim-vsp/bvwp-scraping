package org.tub.vsp.bvwp.users.kn;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.tub.vsp.bvwp.data.container.analysis.RailAnalysisDataContainer;
import org.tub.vsp.bvwp.io.RailCsvWriter;
import org.tub.vsp.bvwp.scraping.RailScraper;
import tech.tablesaw.api.Table;

import java.util.List;

public class RunLocalRailScrapingKN{
    private static final Logger logger = LogManager.getLogger( RunLocalRailScrapingKN.class );

    public static void main(String[] args) {
        RailScraper scraper = new RailScraper();

        logger.info("Starting scraping");
        List<RailAnalysisDataContainer> allRailData = scraper.extractAllLocalBaseData("./data/rail/all", "", "^2.*", "")
                                                             .stream()
                                                             .map(RailAnalysisDataContainer::new)
                                                             .toList();

        logger.info("Writing csv");
        RailCsvWriter csvWriter = new RailCsvWriter("output/rail_data.csv");
        Table table = csvWriter.writeCsv( allRailData );

        //TODO
    }
}
