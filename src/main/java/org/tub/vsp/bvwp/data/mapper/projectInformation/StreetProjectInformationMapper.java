package org.tub.vsp.bvwp.data.mapper.projectInformation;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.util.Assert;
import org.jsoup.nodes.Document;
import org.tub.vsp.bvwp.JSoupUtils;
import org.tub.vsp.bvwp.data.container.base.street.StreetProjectInformationDataContainer;
import org.tub.vsp.bvwp.data.type.Bautyp;
import org.tub.vsp.bvwp.data.type.Einstufung;

import java.text.ParseException;

import static org.tub.vsp.bvwp.data.mapper.projectInformation.ProjectInformationMapperUtils.extractInformation;

public class StreetProjectInformationMapper {
    private static final Logger logger = LogManager.getLogger(StreetProjectInformationDataContainer.class);

    public static StreetProjectInformationDataContainer mapDocument(Document document) {
        StreetProjectInformationDataContainer projectInformation = new StreetProjectInformationDataContainer();

        String projectNumber = extractInformation(document, 0, "Projektnummer");
        String street = extractInformation(document, 0, "Straße");
        String length = extractInformation(document, 0, "Länge");
        if (length != null) {
            length = length.replace(" km", "");
        }
        String bautyp = extractInformation(document, 0, "Bautyp(en), Bauziel(e)");

        String einstufung = extractInformation(document, 1, "Dringlichkeitseinstufung");

        String verkehrsbelastungPlanfall = extractInformation( document, 0, "im Planfall 2030" );
        if ( verkehrsbelastungPlanfall != null ) {
            verkehrsbelastungPlanfall = verkehrsbelastungPlanfall.replace( " Kfz/24h", "" );
        } else {
            // ( other format for Knotenpunkte )
            verkehrsbelastungPlanfall = extractInformation( document, 0, "/ Planfall 2030" );
            verkehrsbelastungPlanfall = verkehrsbelastungPlanfall.replaceAll( "^.* / ", "" );
            verkehrsbelastungPlanfall = verkehrsbelastungPlanfall.replace( " Kfz/24h", "" );
        }
        if ( verkehrsbelastungPlanfall==null ) {
            verkehrsbelastungPlanfall = "0.";
        }

//        logger.warn( ConsoleColors.TEXT_RED + "project=" + projectNumber + "; verkehrsbelastungPlanfall=" + verkehrsbelastungPlanfall + ConsoleColors.TEXT_BLACK ) ;

        if ( projectNumber.contains( "A008-G010-BY" ) ){
            logger.warn( "projectNumber=" + projectNumber + "; einstufung=" + einstufung );
            logger.warn( "Project has NKV in main project but the Einstufungen VBE, VBE, VBE, WBP in the subprojects.  Setting the main project to VBE." );
            einstufung = Einstufung.VBE.name();
        }
        if ( projectNumber.contains( "A21-G20-SH-NI" ) ) {
            logger.warn( "projectNumber=" + projectNumber + "; einstufung=" + einstufung );
            logger.warn( "Project has NKV in main project but the Einstufungen VB, WBP, WBP, WBP in the subprojects.  Setting the main project to VB (!)." );
            einstufung = Einstufung.VB.name();
        }
        if ( projectNumber.contains( "A98-G110-BW" ) ) {
            logger.warn( "projectNumber=" + projectNumber + "; einstufung=" + einstufung );
            logger.warn( "Project has NKV in main project but the Einstufungen VB, WBP in the subprojects.  Setting the main project to VB." );
            einstufung = Einstufung.VB.name();
        }
        if ( projectNumber.contains( "A006-G015-BY" ) ) {
            logger.warn( "projectNumber=" + projectNumber + "; einstufung=" + einstufung );
            logger.warn( "Project has NKV in main project but the Einstufungen VB, WBP in the subprojects.  Setting the main project to VB." );
            einstufung = Einstufung.VB.name();
        }

        try {
            return projectInformation.setProjectNumber(projectNumber)
                                     .setStreet(street)
                                     .setLength(JSoupUtils.parseDouble(length))
                                     .setBautyp(Bautyp.getFromString(bautyp))
                                     .setEinstufung( Einstufung.getFromString(einstufung ) )
                                     .setVerkehrsbelastungPlanfall( JSoupUtils.parseDouble( verkehrsbelastungPlanfall ) );
        } catch (ParseException e) {
            logger.error( "projectNumber=" + projectNumber );
            throw new RuntimeException(e);
        }
    }
}
