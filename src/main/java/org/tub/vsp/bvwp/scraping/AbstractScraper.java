package org.tub.vsp.bvwp.scraping;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public abstract class AbstractScraper<D> {

    private static final Logger logger = LogManager.getLogger(AbstractScraper.class);

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

        logger.info("Found {} projects", projectUrls.size());
        logger.info(projectUrls);

        return projectUrls.stream()
                          .map(link -> getBaseUrl() + link)
                          .toList();
    }

    protected Optional<D> extractRemoteBaseData(String url) {
        logger.info("Scraping project from {}", url);

        Document doc;
        try {
            doc = Jsoup.connect(url).get();
        } catch (IOException e) {
            logger.warn("Could not connect to {}", url);
            logger.warn("Skipping project.");
            return Optional.empty();
        }

        return extractBaseData(doc, url);
    }

    protected Optional<D> extractLocalBaseData(File file) {
        logger.info("Scraping project from file {}", file);

        Document doc;
        try {
            doc = Jsoup.parse(file, "UTF-8");
        } catch (IOException e) {
            logger.warn("Could not connect to {}", file);
            logger.warn("Skipping project.");
            return Optional.empty();
        }

        String name = file.getName();
        return extractBaseData(doc, getBaseUrl() + name.substring(0, name.length() - 5) + "/" + name);
    }

    public Optional<D> extractBaseData(Document doc) {
        return extractBaseData(doc, null);
    }

    public abstract Optional<D> extractBaseData(Document doc, String url);
}
