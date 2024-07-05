package org.tub.vsp.bvwp.scraping;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jsoup.nodes.Document;
import org.tub.vsp.bvwp.data.container.base.street.StreetBaseDataContainer;
import org.tub.vsp.bvwp.data.mapper.costBenefit.StreetCostBenefitMapper;
import org.tub.vsp.bvwp.data.mapper.physicalEffect.StreetPhysicalEffectMapper;
import org.tub.vsp.bvwp.data.mapper.projectInformation.ProjectInformationMapperUtils;
import org.tub.vsp.bvwp.data.mapper.projectInformation.StreetProjectInformationMapper;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class StreetScraper extends AbstractScraper<StreetBaseDataContainer> {
    private static final Logger logger = LogManager.getLogger(StreetScraper.class);

    @Override
    protected String getBaseUrl() {
        return "https://www.bvwp-projekte.de/strasse/";
    }

    @Override
    public List<String> getProjectUrls() throws IOException {
        List<String> result = super.getProjectUrls();
//        result.removeIf(link -> !link.startsWith("A"));
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

    public List<StreetBaseDataContainer> extractAllLocalBaseData(String path, String prefix, String regexToMatch, String regexToExclude ) {

        List<File> files = Arrays.stream(Objects.requireNonNull(new File(path).listFiles())).toList();
        return files.stream()
                    .filter(file -> file.getName().startsWith(prefix))
                    .filter(file -> file.getName().matches(regexToMatch))
                    .filter(file -> !file.getName().matches("A20-G10-SH.html")) // gibt es nochmal mit A20-G10-SH-NI.  Muss man beide zusammenzählen?  kai, feb'24
//                    .filter(file -> !file.getName().matches("A57-G10-NW.html")) // sehr hohes DTV für 4 Spuren.  ??  kai, mar'24
//                    .filter(file -> !file.getName().matches("A81-G50-BW.html")) // sehr hohes DTV für 4 Spuren.  ??  kai, mar'24
                    .filter(file -> !file.getName().matches("A61-G10-RP-T2-RP.html")) // benefits and costs for T1 and T2 are same; there are no revised investment costs from TUD for T2
                    .filter(file -> !file.getName().matches("A3-G30-HE-T05-HE.html")) // benefits and costs for T04 and T05 are same; there are no revised investment costs from TUD for T05
                    .filter(file -> !file.getName().matches("A3-G30-HE-T08-HE.html")) // benefits and costs for T06 and T06 are same; there are no revised investment costs from TUD for T08
                    .filter(file -> !file.getName().matches("A40-G30-NW-T4-NW.html")) // dto
                    .filter(file -> !file.getName().matches("A003-G061-BY.html")) // dto
                    .filter(file -> !file.getName().matches( regexToExclude ))
                    .map(this::extractLocalBaseData)
                    .filter(Optional::isPresent)
                    .map(Optional::get)
                    .sorted(Comparator.comparing(StreetBaseDataContainer::getUrl))
                    .toList();
    }

    @Override
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
        boolean sieheHauptprojekt = ProjectInformationMapperUtils.extractInformation(doc, 2, "Nutzen-Kosten-Verhältnis").contains("siehe Hauptprojekt");
        String extractedInformation = ProjectInformationMapperUtils.extractInformation(doc, 2, "Nutzen-Kosten-Verhältnis");
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
