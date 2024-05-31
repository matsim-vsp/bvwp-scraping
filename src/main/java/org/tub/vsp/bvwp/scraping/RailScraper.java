package org.tub.vsp.bvwp.scraping;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jsoup.nodes.Document;
import org.tub.vsp.bvwp.JSoupUtils;
import org.tub.vsp.bvwp.data.container.base.rail.RailBaseDataContainer;
import org.tub.vsp.bvwp.data.mapper.costBenefit.RailCostBenefitMapper;
import org.tub.vsp.bvwp.data.mapper.physicalEffect.RailPhysicalEffectMapper;
import org.tub.vsp.bvwp.data.mapper.projectInformation.RailProjectInformationMapper;

import java.io.File;
import java.util.*;

public class RailScraper extends AbstractScraper<RailBaseDataContainer> {
    private static final Logger logger = LogManager.getLogger(RailScraper.class);

    @Override
    protected String getBaseUrl() {
        return "https://www.bvwp-projekte.de/schiene/";
    }

    @Override
    public Optional<RailBaseDataContainer> extractBaseData(Document doc, String url) {
        if (!isProjectScrapable(doc)) {
            logger.info("Project not scrapable: {}", url);
            return Optional.empty();
        }

        logger.info("Scraping rail project from {}", url);

        RailBaseDataContainer railBaseDataContainer = new RailBaseDataContainer();
        return Optional.of(railBaseDataContainer.setUrl(url)
                                                .setProjectInformation(RailProjectInformationMapper.mapDocument(doc))
                                                .setPhysicalEffect(RailPhysicalEffectMapper.mapDocument(doc))
                                                .setCostBenefitAnalysis(RailCostBenefitMapper.mapDocument(doc)));
    }

    public List<RailBaseDataContainer> extractAllLocalBaseData(String projectFolder, String prefix, String regexToMatch, String regexToExclude) {

        List<File> files = Arrays.stream(Objects.requireNonNull(new File(projectFolder).listFiles())).toList();
        return files.stream()
                    .filter(file -> file.getName().startsWith(prefix))
                    .filter(file -> file.getName().matches(regexToMatch))
                    .filter(file -> !file.getName().matches(regexToExclude))
                    .map(this::extractLocalBaseData)
                    .filter(Optional::isPresent)
                    .map(Optional::get)
                    .sorted(Comparator.comparing(RailBaseDataContainer::getUrl))
                    .toList();
    }

    private boolean isProjectScrapable(Document doc) {
        //if there is no emissions table, there is no further information to scrape (e.g. https://www.bvwp-projekte.de/schiene/2-034-V01/2-034-V01.html)
        return JSoupUtils.getTableByKeyAndContainedText(doc, "table.table_webprins",
                "Veränderung der Abgasemissionen").isPresent();
    }
}
