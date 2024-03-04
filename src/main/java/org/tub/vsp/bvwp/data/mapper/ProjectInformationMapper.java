package org.tub.vsp.bvwp.data.mapper;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jsoup.nodes.Document;
import org.tub.vsp.bvwp.JSoupUtils;
import org.tub.vsp.bvwp.data.container.base.street.StreetProjectInformationDataContainer;
import org.tub.vsp.bvwp.data.type.Bautyp;
import org.tub.vsp.bvwp.data.type.Priority;

import java.text.ParseException;
import java.util.Optional;

public class ProjectInformationMapper {
    private static final Logger logger = LogManager.getLogger(ProjectInformationMapper.class);

    public StreetProjectInformationDataContainer mapDocument( Document document ) {
        StreetProjectInformationDataContainer projectInformation = new StreetProjectInformationDataContainer();

        String projectNumber = extractInformation(document, 0, "Projektnummer");
        String street = extractInformation(document, 0, "Straße");
        String length = extractInformation( document, 0, "Länge" );
        if ( length!= null ) {
            length = length.replace( " km", "" );
        }
        String bautyp = extractInformation( document, 0, "Bautyp(en), Bauziel(e)" );

        String verkehrsbelastungPlanfall = extractInformation( document, 0, "im Planfall 2030" );
        if ( verkehrsbelastungPlanfall!=null ) {
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

        String priority = extractInformation(document, 1, "Dringlichkeitseinstufung");

        if ( projectNumber.startsWith( "A008-G010" ) ) {
            // NKV ist im Hauptprojekt, aber Einstufungen sind in den Teilprojekten.  3 davon VBE, 1 WBP --> VBE.

            priority = Priority.VBE.name();
            logger.warn( "priority=" + priority );
        }

        if ( priority.equalsIgnoreCase( "siehe Teilprojekte" ) ) {
            logger.warn("project=" + projectNumber ) ;
        }

        try{
            return projectInformation.setProjectNumber(projectNumber)
                                     .setStreet(street)
                                     .setLength( JSoupUtils.parseDouble( length ) )
                                     .setBautyp( Bautyp.getFromString( bautyp ) )
                                     .setPriority( Priority.getFromString(priority ) )
                                     .setVerkehrsbelastungPlanfall( JSoupUtils.parseDouble( verkehrsbelastungPlanfall ) )
                            ;
        } catch( ParseException e ){
            throw new RuntimeException( e );
        }
    }

    //mapping information from the grunddaten table. There are two tables with the same class, so we need to specify
    // the index
    public static String extractInformation(Document document, int tableIndex, String key) {
        Optional<String> info = document.select("table.table_grunddaten")
                                        .get(tableIndex)
                                        .select("tr")
                                        .stream()
                                        .filter(r -> r.text()
                                                      .contains(key))
                                        .findFirst()
                                        .map(r -> r.child(1)
                                                   .text());
        if (info.isEmpty()) {
            logger.warn("Could not find information for key {} in table {}", key, tableIndex);
        }
        return info.orElse(null);
    }
}
