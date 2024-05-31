package org.tub.vsp.bvwp;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.tub.vsp.bvwp.data.container.analysis.RailAnalysisDataContainer;
import org.tub.vsp.bvwp.io.RailTableCreator;
import org.tub.vsp.bvwp.scraping.RailScraper;
import tech.tablesaw.api.Table;

import java.io.IOException;
import java.util.List;

public class RunLocalRailScraping {
    private static final Logger logger = LogManager.getLogger(RunLocalRailScraping.class);

    public static void main(String[] args) throws IOException {
        RailScraper scraper = new RailScraper();

        logger.info("Starting scraping");
        List<RailAnalysisDataContainer> allRailData = scraper.extractAllLocalBaseData("./data/rail/all", "", "^2.*", "")
                                                             .stream()
                                                             //there is something wrong with the project number of 2-009-V03
                                                             .filter(d -> !d.getProjectInformation().getProjectNumber().equals("2-009-V03"))
                                                             .map(RailAnalysisDataContainer::new)
                                                             .toList();

        logger.info("Writing csv");
        RailTableCreator tableCreator = new RailTableCreator();
        Table table = tableCreator.computeTable(allRailData);
        table.write().csv("out.csv");
    }
}
