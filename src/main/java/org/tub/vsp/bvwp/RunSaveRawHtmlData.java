package org.tub.vsp.bvwp;

import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.tub.vsp.bvwp.scraping.RailScraper;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class RunSaveRawHtmlData {
    private static final Logger logger = LogManager.getLogger(RunSaveRawHtmlData.class);

    public static void main(String[] args) throws IOException {
//        StreetScraper scraper = new StreetScraper();
//        List<String> projectUrls = scraper.getProjectUrls();

        List<String> projectUrls = new RailScraper().getProjectUrls();

        for (String projectUrl : projectUrls) {
            logger.info("Downloading and saving html for {}", projectUrl);
            downloadAndSaveHtml(projectUrl);
        }
    }

    private static void downloadAndSaveHtml(String projectUrl) throws IOException {
        final Connection.Response response = Jsoup.connect(projectUrl).execute();
        final Document doc = response.parse();

        final File f = new File("data/rail/all/" + projectUrl.substring(projectUrl.lastIndexOf("/") + 1));
        FileUtils.writeStringToFile(f, doc.outerHtml(), StandardCharsets.UTF_8);
    }
}
