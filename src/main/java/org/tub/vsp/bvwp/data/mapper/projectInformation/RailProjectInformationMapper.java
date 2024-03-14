package org.tub.vsp.bvwp.data.mapper.projectInformation;

import org.jsoup.nodes.Document;
import org.tub.vsp.bvwp.JSoupUtils;
import org.tub.vsp.bvwp.data.container.base.rail.RailProjectInformationDataContainer;
import org.tub.vsp.bvwp.data.type.Einstufung;

import java.text.ParseException;

import static org.tub.vsp.bvwp.data.mapper.projectInformation.ProjectInformationMapperUtils.extractInformation;

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
                                     .setPriority( Einstufung.getFromString(priority ) );
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
    }
}
