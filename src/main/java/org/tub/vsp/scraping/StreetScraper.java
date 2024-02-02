package org.tub.vsp.scraping;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.tub.vsp.data.container.base.StreetBaseDataContainer;
import org.tub.vsp.data.mapper.CostBenefitMapper;
import org.tub.vsp.data.mapper.PhysicalEffectMapper;
import org.tub.vsp.data.mapper.ProjectInformationMapper;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

public class StreetScraper extends Scraper {
    private static final Logger logger = LogManager.getLogger(StreetScraper.class);

    private final ProjectInformationMapper projectInformationMapper = new ProjectInformationMapper();
    private final PhysicalEffectMapper physicalEffectMapper = new PhysicalEffectMapper();
    private final CostBenefitMapper costBenefitMapper = new CostBenefitMapper();

    @Override
    protected String getBaseUrl() {
        return "https://www.bvwp-projekte.de/strasse/";
    }

    public List<StreetBaseDataContainer> extractAllBaseData() {
        List<String> projectUrls;
        try {
            projectUrls = getProjectUrls();
        } catch (IOException e) {
            logger.error("Could not get project urls");
            throw new RuntimeException(e);
        }

        return projectUrls.stream()
                          .map(this::extractBaseDataFromUrl)
                          .filter(Optional::isPresent)
                          .map(Optional::get)
                          .toList();
    }

    private Optional<StreetBaseDataContainer> extractBaseDataFromUrl(String projectUrl) {
        logger.info("Scraping project from {}", projectUrl);

        Document doc;
        try {
            doc = Jsoup.connect(projectUrl)
                       .get();
        } catch (IOException e) {
            logger.warn("Could not connect to {}", projectUrl);
            logger.warn("Skipping project.");
            return Optional.empty();
        }

        return extractBaseData(doc, projectUrl);
    }

    public Optional<StreetBaseDataContainer> extractBaseData(Document doc) {
        return extractBaseData(doc, null);
    }

    public Optional<StreetBaseDataContainer> extractBaseData(Document doc, String url) {
        if (!checkIfProjectIsScrapable(doc)) {
            logger.info("Skipping project because it is a subproject.");
            return Optional.empty();
        }

        StreetBaseDataContainer streetBaseDataContainer = new StreetBaseDataContainer();
        return Optional.of(streetBaseDataContainer.setUrl(url)
                                                  .setProjectInformation(projectInformationMapper.mapDocument(doc))
                                                  .setPhysicalEffect(physicalEffectMapper.mapDocument(doc))
                                                  .setCostBenefitAnalysis(costBenefitMapper.mapDocument(doc)));
    }

    private boolean checkIfProjectIsScrapable(Document doc) {
        boolean holdsDetailedInformation = !ProjectInformationMapper.extractInformation(doc, 2, "Nutzen-Kosten-Verh" +
                                                                            "ältnis")
                                                                    .contains("siehe Hauptprojekt");

        boolean isNoPartialProject = !doc.select("div.right")
                                         .select("h1")
                                         .text()
                                         .contains("Teilprojekt");

        //main projects are alwyas scraped
        //partial projects are only scraped if they hold detailed information
        return isNoPartialProject || holdsDetailedInformation;
    }
}
