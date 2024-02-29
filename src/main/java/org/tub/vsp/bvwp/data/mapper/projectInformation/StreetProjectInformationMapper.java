package org.tub.vsp.bvwp.data.mapper.projectInformation;

import org.jsoup.nodes.Document;
import org.tub.vsp.bvwp.JSoupUtils;
import org.tub.vsp.bvwp.data.container.base.street.StreetProjectInformationDataContainer;
import org.tub.vsp.bvwp.data.type.Bautyp;
import org.tub.vsp.bvwp.data.type.Priority;

import java.text.ParseException;

import static org.tub.vsp.bvwp.data.mapper.projectInformation.ProjectInformationMapperUtils.extractInformation;

public class StreetProjectInformationMapper {
    public StreetProjectInformationDataContainer mapDocument(Document document) {
        StreetProjectInformationDataContainer projectInformation = new StreetProjectInformationDataContainer();

        String projectNumber = extractInformation(document, 0, "Projektnummer");
        String street = extractInformation(document, 0, "Straße");
        String length = extractInformation(document, 0, "Länge");
        if (length != null) {
            length = length.replace(" km", "");
        }
        String bautyp = extractInformation(document, 0, "Bautyp(en), Bauziel(e)");

        String priority = extractInformation(document, 1, "Dringlichkeitseinstufung");

        try {
            return projectInformation.setProjectNumber(projectNumber)
                                     .setStreet(street)
                                     .setLength(JSoupUtils.parseDouble(length))
                                     .setBautyp(Bautyp.getFromString(bautyp))
                                     .setPriority(Priority.getFromString(priority));
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
    }
}
