package org.tub.vsp.bvwp.data.mapper;

import org.jsoup.nodes.Document;
import org.tub.vsp.bvwp.JSoupUtils;
import org.tub.vsp.bvwp.data.container.base.RailProjectInformationDataContainer;
import org.tub.vsp.bvwp.data.type.Priority;

import java.text.ParseException;

import static org.tub.vsp.bvwp.data.mapper.ProjectInformationMapperUtils.extractInformation;

public class RailProjectInformationMapper {
    public static RailProjectInformationDataContainer mapDocument(Document document) {
        RailProjectInformationDataContainer projectInformation = new RailProjectInformationDataContainer();

        String projectNumber = extractInformation(document, 0, "Projektnummer");
        String titel = extractInformation(document, 0, "Maßnahmetitel");
        String length = extractInformation(document, 0, "Länge");
        if (length != null) {
            length = length.replace(" km", "");
        }

        String priority = extractInformation(document, 1, "Dringlichkeitseinstufung");

        try {
            return projectInformation.setProjectNumber(projectNumber)
                                     .setTitle(titel)
                                     .setLength(JSoupUtils.parseDouble(length))
                                     .setPriority(Priority.getFromString(priority));
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
    }
}
