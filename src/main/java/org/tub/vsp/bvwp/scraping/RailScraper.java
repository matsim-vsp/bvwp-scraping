package org.tub.vsp.bvwp.scraping;

public class RailScraper extends Scraper {
    @Override
    protected String getBaseUrl() {
        return "https://www.bvwp-projekte.de/schiene/";
    }
}
