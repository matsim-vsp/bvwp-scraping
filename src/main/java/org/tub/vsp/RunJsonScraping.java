package org.tub.vsp;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.tub.vsp.data.container.StreetBaseDataContainer;
import org.tub.vsp.io.JsonIo;
import org.tub.vsp.scraping.StreetScraper;

import java.util.List;

public class RunJsonScraping {
    private static final Logger logger = LogManager.getLogger(RunJsonScraping.class);

    public static void main(String[] args) {
        StreetScraper scraper = new StreetScraper();

        logger.info("Starting scraping");
        List<StreetBaseDataContainer> allStreetBaseData = scraper.extractAllBaseData();

        JsonIo jsonIo = new JsonIo();
        logger.info("Writing json");
        jsonIo.writeJson(allStreetBaseData, "output/street_data.json");
    }
}
