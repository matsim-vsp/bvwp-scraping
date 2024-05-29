package org.tub.vsp.bvwp.data.mapper.projectInformation;

import org.jsoup.nodes.Document;
import org.tub.vsp.bvwp.JSoupUtils;
import org.tub.vsp.bvwp.data.container.base.rail.RailProjectInformationDataContainer;
import org.tub.vsp.bvwp.data.type.Einstufung;

import java.text.ParseException;

public class RailProjectInformationMapper {
    public static RailProjectInformationDataContainer mapDocument(Document document) {
        RailProjectInformationDataContainer projectInformation = new RailProjectInformationDataContainer();

        String projectNumber = ProjectInformationMapperUtils.extractInformation(document, 0, "Projektnummer");
        String titel = ProjectInformationMapperUtils.extractInformation(document, 0, "Maßnahmetitel");
        String length = ProjectInformationMapperUtils.extractInformation(document, 0, "Länge");
        if (length != null) {
            length = length.replace(" km", "");
        }

        String priority = ProjectInformationMapperUtils.extractInformation(document, 1, "Dringlichkeitseinstufung");

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
