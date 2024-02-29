package org.tub.vsp.bvwp.data.mapper.projectInformation;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.tub.vsp.bvwp.JSoupUtils;

import java.util.Optional;

public class ProjectInformationMapperUtils {
    private static final Logger logger = LogManager.getLogger(RailProjectInformationMapper.class);

    //mapping information from the grunddaten table. There are two tables with the same class, so we need to specify
    //the index
    public static String extractInformation(Document document, int tableIndex, String key) {
        Element element = document.select("table.table_grunddaten").get(tableIndex);
        Optional<String> info = JSoupUtils.getTextFromFirstRowWithKeyContainedInCol(element, key, 0, 1);
        if (info.isEmpty()) {
            logger.warn("Could not find information for key {} in table {}", key, tableIndex);
        }
        return info.orElse(null);
    }
}
