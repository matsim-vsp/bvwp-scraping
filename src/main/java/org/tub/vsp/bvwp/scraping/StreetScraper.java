package org.tub.vsp.bvwp.scraping;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.tub.vsp.bvwp.data.container.base.street.StreetBaseDataContainer;
import org.tub.vsp.bvwp.data.mapper.costBenefit.StreetCostBenefitMapper;
import org.tub.vsp.bvwp.data.mapper.physicalEffect.StreetPhysicalEffectMapper;
import org.tub.vsp.bvwp.data.mapper.projectInformation.ProjectInformationMapperUtils;
import org.tub.vsp.bvwp.data.mapper.projectInformation.StreetProjectInformationMapper;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class StreetScraper extends Scraper {
    private static final Logger logger = LogManager.getLogger(StreetScraper.class);

    @Override
    protected String getBaseUrl() {
        return "https://www.bvwp-projekte.de/strasse/";
    }

    @Override
    public List<String> getProjectUrls() throws IOException {
        List<String> result = super.getProjectUrls();
        result.removeIf(link -> !link.startsWith("A"));
        return result;
    }

    public List<StreetBaseDataContainer> extractAllRemoteBaseData() {
        List<String> projectUrls;
        try {
            projectUrls = getProjectUrls();
        } catch (IOException e) {
            logger.error("Could not get project urls");
            throw new RuntimeException(e);
        }

        return projectUrls.stream()
                          .map(this::extractRemoteBaseData)
                          .filter(Optional::isPresent)
                          .map(Optional::get)
                          .toList();
    }

    public static String projectString(String bundesland, String road) {
        return "(" + road + "-.*-" + bundesland + ".html" + ")|";
    }

    public List<StreetBaseDataContainer> extractAllLocalBaseData(String path, String prefix, String regexToMatch) {

        List<File> files = Arrays.stream(Objects.requireNonNull(new File(path).listFiles())).toList();
        return files.stream()
                    .filter(file -> file.getName().startsWith(prefix))
                    .filter(file -> file.getName().matches(regexToMatch))
                    .filter(file -> !file.getName()
                                         .matches("A20-G10-SH.html")) // gibt es nochmal mit A20-G10-SH-NI.  Muss man
                    // beide zusammenzählen?  kai, feb'23
                    .filter(file -> !file.getName().matches("A...B.*")) // Ortsumgehungen, die an AB angrenzen.
                    .map(this::extractLocalBaseData)
                    .filter(Optional::isPresent)
                    .map(Optional::get)
                    .sorted(Comparator.comparing(StreetBaseDataContainer::getUrl))
                    .toList();
    }

    private Optional<StreetBaseDataContainer> extractRemoteBaseData(String projectUrl) {
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

    private Optional<StreetBaseDataContainer> extractLocalBaseData(File file) {
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
                                                  .setProjectInformation(StreetProjectInformationMapper.mapDocument(doc))
                                                  .setPhysicalEffect(StreetPhysicalEffectMapper.mapDocument(doc))
                                                  .setCostBenefitAnalysis(StreetCostBenefitMapper.mapDocument(doc)));
    }

    private boolean checkIfProjectIsScrapable(Document doc) {
        boolean sieheHauptprojekt = ProjectInformationMapperUtils.extractInformation(doc, 2, "Nutzen-Kosten-Verh" +
                                                                         "ältnis")
                                                                 .contains("siehe Hauptprojekt");

        String extractedInformation = ProjectInformationMapperUtils.extractInformation(doc, 2, "Nutzen-Kosten-Verh" +
                "ältnis");
        boolean sieheTeilprojekt = extractedInformation.contains("siehe Teilprojekt");
        logger.warn("extractedInformation=" + extractedInformation + "; sieheTeilprojekt=" + sieheTeilprojekt);


//        boolean isNoPartialProject = !doc.select("div.right").select("h1").text().contains("Teilprojekt");
//        boolean isNoPartialProject = true;
        // (in some places, the Teilprojekte contain the necessary information)

        //main projects are alwyas scraped
        //partial projects are only scraped if they hold detailed information
        return !sieheTeilprojekt && !sieheHauptprojekt;
    }
}
