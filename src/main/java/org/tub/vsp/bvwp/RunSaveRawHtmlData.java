package org.tub.vsp.bvwp;

import org.apache.commons.io.FileUtils;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.tub.vsp.bvwp.scraping.StreetScraper;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class RunSaveRawHtmlData {
    public static void main(String[] args) throws IOException {
        StreetScraper scraper = new StreetScraper();
        List<String> projectUrls = scraper.getProjectUrls();

        for (String projectUrl : projectUrls) {
            downloadAndSaveHtml(projectUrl);
        }
    }

    private static void downloadAndSaveHtml(String projectUrl) throws IOException {
        final Connection.Response response = Jsoup.connect(projectUrl).execute();
        final Document doc = response.parse();

        final File f = new File("data/street/all/" + projectUrl.substring(projectUrl.lastIndexOf("/") + 1));
        FileUtils.writeStringToFile(f, doc.outerHtml(), StandardCharsets.UTF_8);
    }
}
