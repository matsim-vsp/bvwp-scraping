package org.tub.vsp.bvwp.data.mapper;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jsoup.nodes.Document;
import org.tub.vsp.bvwp.JSoupUtils;
import org.tub.vsp.bvwp.data.type.Priority;
import org.tub.vsp.bvwp.data.container.base.ProjectInformationDataContainer;

import java.text.ParseException;
import java.util.Optional;

public class ProjectInformationMapper {
    private static final Logger logger = LogManager.getLogger(ProjectInformationMapper.class);

    public ProjectInformationDataContainer mapDocument(Document document) {
        ProjectInformationDataContainer projectInformation = new ProjectInformationDataContainer();

        String projectNumber = extractInformation(document, 0, "Projektnummer");
        String street = extractInformation(document, 0, "Straße");
        String length = extractInformation( document, 0, "Länge" );
        if ( length!= null ) {
            length = length.replace( " km", "" );
        }
        String bautyp = extractInformation( document, 0, "Bautyp(en), Bauziel(e)" );

        String verkehrsbelastung2030 = extractInformation( document, 0, "im Planfall 2030" );
        if ( verkehrsbelastung2030!=null ) {
            verkehrsbelastung2030 = verkehrsbelastung2030.replace( " Kfz/24h", "" );
        }

        String priority = extractInformation(document, 1, "Dringlichkeitseinstufung");

        try{
            return projectInformation.setProjectNumber(projectNumber)
                                     .setStreet(street)
                                     .setLength( JSoupUtils.parseDouble( length ) )
                                     .setBautyp( bautyp )
                                     .setPriority( Priority.getFromString(priority ) )
                                     .setVerkehrsbelastung2030( JSoupUtils.parseDouble( verkehrsbelastung2030 ))
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
