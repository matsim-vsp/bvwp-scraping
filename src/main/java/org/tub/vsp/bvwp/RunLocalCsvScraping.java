package org.tub.vsp.bvwp;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.tub.vsp.bvwp.data.Headers;
import org.tub.vsp.bvwp.data.container.analysis.StreetAnalysisDataContainer;
import org.tub.vsp.bvwp.io.StreetCsvWriter;
import org.tub.vsp.bvwp.plot.MultiPlotExample;
import org.tub.vsp.bvwp.scraping.StreetScraper;
import tech.tablesaw.api.Table;
import tech.tablesaw.plotly.components.Axis;
import tech.tablesaw.plotly.components.Figure;
import tech.tablesaw.plotly.display.Browser;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.List;

public class RunLocalCsvScraping {
    private static final Logger logger = LogManager.getLogger(RunLocalCsvScraping.class);

    public static void main(String[] args) throws IOException{
        logger.warn( "(vermutl. weitgehend gelöst) Teilweise werden die Hauptprojekte bewertet und nicht die " +
                                     "Teilprojekte (A20); teilweise werden die Teilprojekte " +
                                     "bewertet aber nicht das Hauptprojekt (A2).  Müssen aufpassen, dass nichts unter den Tisch fällt." );
        logger.warn( "Bei https://www.bvwp-projekte.de/strasse/A559-G10-NW/A559-G10-NW.html hat evtl. die Veränderung " +
                                     "Betriebsleistung PV falsches VZ.  Nutzen (positiv) dann wieder richtig." );
        logger.warn( "Wieso geht bei der https://www.bvwp-projekte.de/strasse/A14-G20-ST-BB/A14-G20-ST-BB.html der " +
                                     "Nutzen mit impl und co2Price sogar nach oben?" );
        logger.warn( "===========" );

        String positivListe = BvwpUtils.getPositivListe();

        StreetScraper scraper = new StreetScraper();

        logger.info( "Starting scraping" );

        // yyyy man könnte (sollte?) den table in den StreetAnalysisDataContainer mit hinein geben, und die Werte gleich dort eintragen.  kai, feb'24

        List<StreetAnalysisDataContainer> allStreetBaseData = scraper
                                                                              .extractAllLocalBaseData( "./data/street/all", "A", ".*" )
                                                                              .stream()
                                                                              .map( StreetAnalysisDataContainer::new )
                                                                              .toList();

        logger.info( "Writing csv" );
        StreetCsvWriter csvWriter = new StreetCsvWriter( "output/street_data.csv" );
        Table table = csvWriter.writeCsv( allStreetBaseData );

//        table = table.where( table.numberColumn( Headers.NKV_INDUZ_CO2 ).isLessThan( 1.) );

        String xName;
        Axis.AxisBuilder xAxisBuilder = Axis.builder();
//        {
//            xName = Headers.B_CO2_NEU;
//            xAxisBuilder.type( Axis.Type.LOG );
//        }
        {
            xName = Headers.NKV_NO_CHANGE;
            xAxisBuilder
                            .type( Axis.Type.LOG )
                            .autoRange( Axis.AutoRange.REVERSED );
        }
        table = table.sortDescendingOn( xName );
        Axis xAxis = xAxisBuilder.title( xName ).build();

        Figure figure = PlotUtils.createFigurePkwKm( xAxis, table, xName );
        Figure figure2 = PlotUtils.createFigureNkv( xAxis, table, xName );
        Figure figure3 = PlotUtils.createFigureCost( xAxis, table, xName );
        Figure figure4 = PlotUtils.createFigureCO2( xAxis, table, xName );

        String page = MultiPlotExample.pageTop + System.lineSeparator() +
                                      figure2.asJavascript( "plot1" ) + System.lineSeparator() +
                                      figure.asJavascript( "plot2" ) + System.lineSeparator() +
                                      figure3.asJavascript( "plot3" ) + System.lineSeparator() +
                                      figure4.asJavascript( "plot4" ) + System.lineSeparator() +
                                      MultiPlotExample.pageBottom;

        File outputFile = Paths.get("multiplot.html" ).toFile();

        try ( FileWriter fileWriter = new FileWriter(outputFile)) {
            fileWriter.write(page);
        }

        new Browser().browse(outputFile );

    }

}
