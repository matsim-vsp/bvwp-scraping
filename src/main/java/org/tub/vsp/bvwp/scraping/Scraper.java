package org.tub.vsp.bvwp.scraping;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public abstract class Scraper {

    private static final Logger logger = LogManager.getLogger(Scraper.class);

    protected abstract String getBaseUrl();

    public List<String> getProjectUrls() throws IOException {
        logger.info("Scraping projects from {}", getBaseUrl());

        Document doc = Jsoup.connect(getBaseUrl()).get();
        Elements links = doc.select("a[href]");
        List<String> projectUrls = new ArrayList<>();

        for (Element link : links) {
            projectUrls.add(link.attr("href"));
        }

        projectUrls.remove("index.html");
        projectUrls.remove("../glossar.html");
        projectUrls.remove("../impressum.html");
        projectUrls.remove("../datenschutz.html");
        projectUrls.remove("../hinweise.html");

        projectUrls.removeIf(link -> link.endsWith(".pdf"));

        //Todo: Sollte dieses Aufräumen nicht irgendwo in den Streetscraper? Denn das ist ja die **straßen**spezifische Filterung, KMT Feb24
        //A = Autobahn; B = Bundesstraße -> Diese sollen behalten werden.
        projectUrls.removeIf( link -> (!link.startsWith( "A" ) ));
//        projectUrls.removeIf( link -> (!link.startsWith( "A" ) && !link.startsWith( "B" )));

        logger.info("Found {} projects", projectUrls.size());
        logger.info(projectUrls);

        return projectUrls.stream()
                          .map(link -> getBaseUrl() + link)
                          .toList();
    }


}
