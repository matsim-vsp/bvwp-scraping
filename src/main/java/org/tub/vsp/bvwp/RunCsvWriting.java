package org.tub.vsp.bvwp;

import org.tub.vsp.bvwp.data.container.analysis.StreetAnalysisDataContainer;
import org.tub.vsp.bvwp.io.StreetCsvWriter;
import org.tub.vsp.bvwp.scraping.StreetScraper;

import java.util.List;

public class RunCsvWriting {
    public static void main(String[] args) {
        List<StreetAnalysisDataContainer> allStreetBaseData =
                new StreetScraper().extractAllLocalBaseData("./data/street/all", "A", ".*")
                                   .stream()
                                   .map( streetBaseDataContainer -> new StreetAnalysisDataContainer( streetBaseDataContainer, 0.6, 1. ) )
                                   .toList();

        StreetCsvWriter csvWriter = new StreetCsvWriter("output/street_data.csv");
        csvWriter.writeCsv(allStreetBaseData);
    }
}
