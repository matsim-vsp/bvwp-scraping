package org.tub.vsp.bvwp;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.tub.vsp.bvwp.data.container.base.street.StreetBaseDataContainer;
import org.tub.vsp.bvwp.io.JsonIo;
import org.tub.vsp.bvwp.scraping.StreetScraper;

import java.util.List;

public class RunJsonScraping {
    private static final Logger logger = LogManager.getLogger(RunJsonScraping.class);

    public static void main(String[] args) {
        StreetScraper scraper = new StreetScraper();

        logger.info("Starting scraping");
        List<StreetBaseDataContainer> allStreetBaseData = scraper.extractAllRemoteBaseData();

        JsonIo jsonIo = new JsonIo();
        logger.info("Writing json");
        jsonIo.writeJson(allStreetBaseData, "output/street_data.json");
    }
}
