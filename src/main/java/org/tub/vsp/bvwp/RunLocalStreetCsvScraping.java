package org.tub.vsp.bvwp;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.tub.vsp.bvwp.data.container.analysis.StreetAnalysisDataContainer;
import org.tub.vsp.bvwp.io.StreetCsvWriter;
import org.tub.vsp.bvwp.scraping.StreetScraper;

import java.util.List;

public class RunLocalStreetCsvScraping {
    private static final Logger logger = LogManager.getLogger(RunLocalStreetCsvScraping.class);

    public static void main(String[] args) {
        logger.warn("(vermutl. weitgehend gelöst) Teilweise werden die Hauptprojekte bewertet und nicht die " +
                "Teilprojekte (A20); teilweise werden die Teilprojekte " +
                "bewertet aber nicht das Hauptprojekt (A2).  Müssen aufpassen, dass nichts unter den Tisch fällt.");
        logger.warn("Bei https://www.bvwp-projekte.de/strasse/A559-G10-NW/A559-G10-NW.html hat evtl. die Veränderung " +
                "Betriebsleistung PV falsches VZ.  Nutzen (positiv) dann wieder richtig.");
        logger.warn("Wieso geht bei der https://www.bvwp-projekte.de/strasse/A14-G20-ST-BB/A14-G20-ST-BB.html der " +
                "Nutzen mit impl und co2Price sogar nach oben?");


        StreetScraper scraper = new StreetScraper();

        logger.info("Starting scraping");
        List<StreetAnalysisDataContainer> allStreetData = scraper.extractAllLocalBaseData("./data/street", "A", ".*", "" )
                                                                 .stream()
                                                                 .map(streetBaseDataContainer -> new StreetAnalysisDataContainer(streetBaseDataContainer, 0.) )
                                                                 .toList();

        logger.info("Writing csv");
        StreetCsvWriter csvWriter = new StreetCsvWriter("output/street_data.csv");
        csvWriter.writeCsv(allStreetData);
    }
}
