package org.tub.vsp.bvwp;

import org.tub.vsp.bvwp.data.container.analysis.StreetAnalysisDataContainer;
import org.tub.vsp.bvwp.io.StreetCsvWriter;
import org.tub.vsp.bvwp.scraping.StreetScraper;

import java.util.List;

public class RunCsvWriting {
    public static void main(String[] args) {
//        String filePath = "../../shared-svn/";
//        Map<String, Double> constructionCostsByProject = BvwpUtils.getConstructionCostsFromTudFile(filePath );

        List<StreetAnalysisDataContainer> allStreetBaseData =
                new StreetScraper().extractAllLocalBaseData("./data/street/all", "A", ".*", "")
                                   .stream()
                                   .map(s -> new StreetAnalysisDataContainer(s, 0))
                                   .toList();

        StreetCsvWriter csvWriter = new StreetCsvWriter("output/street_data.csv");
        csvWriter.writeCsv(allStreetBaseData);
    }
}
