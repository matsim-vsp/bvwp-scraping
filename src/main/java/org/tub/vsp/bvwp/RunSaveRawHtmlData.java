package org.tub.vsp.bvwp;

import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.tub.vsp.bvwp.scraping.RailScraper;
import org.tub.vsp.bvwp.scraping.StreetScraper;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class RunSaveRawHtmlData {
    private static final Logger logger = LogManager.getLogger(RunSaveRawHtmlData.class);

    private enum Mode { ROAD, RAIL }

    private static final Mode mode = Mode.ROAD;

    public static void main(String[] args) throws IOException {
        List<String> projectUrls;
        switch( mode ) {
            case ROAD -> {
                projectUrls = new StreetScraper().getProjectUrls();
            }
            case RAIL -> {
                projectUrls = new RailScraper().getProjectUrls();
            }
            default -> throw new IllegalStateException( "Unexpected value: " + mode );
        }

        for (String projectUrl : projectUrls) {
            logger.info("Downloading and saving html for {}", projectUrl);
            downloadAndSaveHtml(projectUrl);
        }
    }

    private static void downloadAndSaveHtml(String projectUrl) throws IOException {
        final Connection.Response response = Jsoup.connect(projectUrl).execute();
        final Document doc = response.parse();

        final File file;
        switch( mode ) {
            case ROAD -> {
                file = new File("data/street/all" + projectUrl.substring(projectUrl.lastIndexOf("/") + 1));
            }
            case RAIL -> {
                file = new File("data/rail/all/" + projectUrl.substring(projectUrl.lastIndexOf("/") + 1));
            }
            default -> throw new IllegalStateException( "Unexpected value: " + mode );
        }

        FileUtils.writeStringToFile(file, doc.outerHtml(), StandardCharsets.UTF_8);
    }
}
