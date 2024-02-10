package org.tub.vsp;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.tub.vsp.data.Headers;
import org.tub.vsp.data.container.analysis.StreetAnalysisDataContainer;
import org.tub.vsp.io.StreetCsvWriter;
import org.tub.vsp.scraping.StreetScraper;
import tech.tablesaw.api.*;
import tech.tablesaw.plotly.Plot;
import tech.tablesaw.plotly.components.Axis;
import tech.tablesaw.plotly.components.Figure;
import tech.tablesaw.plotly.components.Layout;
import tech.tablesaw.plotly.traces.ScatterTrace;
import tech.tablesaw.plotly.traces.Trace;

import java.util.Arrays;
import java.util.List;

public class RunLocalCsvScraping {
    private static final Logger logger = LogManager.getLogger(RunLocalCsvScraping.class);

    public static void main(String[] args) {
        logger.warn("(vermutl. weitgehend gelöst) Teilweise werden die Hauptprojekte bewertet und nicht die " +
                "Teilprojekte (A20); teilweise werden die Teilprojekte " +
                "bewertet aber nicht das Hauptprojekt (A2).  Müssen aufpassen, dass nichts unter den Tisch fällt.");
        logger.warn("Bei https://www.bvwp-projekte.de/strasse/A559-G10-NW/A559-G10-NW.html hat evtl. die Veränderung " +
                "Betriebsleistung PV falsches VZ.  Nutzen (positiv) dann wieder richtig.");
        logger.warn("Wieso geht bei der https://www.bvwp-projekte.de/strasse/A14-G20-ST-BB/A14-G20-ST-BB.html der " +
                "Nutzen mit impl und co2Price sogar nach oben?");
        logger.warn("===========");

        StreetScraper scraper = new StreetScraper();

        logger.info("Starting scraping");
        List<StreetAnalysisDataContainer> allStreetBaseData = scraper.extractAllLocalBaseData("./data/street/all", "A" )
                                                                     .stream()
                                                                     .map(StreetAnalysisDataContainer::new)
                                                                     .toList();

//        String title = "Title";
//        Table table = Table.create();
//        String xcol = "xcol";
//        String ycol = "ycol";
//
//        for( StreetAnalysisDataContainer analysisDataContainer : allStreetBaseData ){
//            logger.info( "we are looking at " + analysisDataContainer.getStreetBaseDataContainer().getProjectInformation().getProjectNumber() ) ;
//            for( Map.Entry<String, Double> entry : analysisDataContainer.getColumns().entrySet() ){
//                column = table.columns()
//            }
//
//            Row row = table.appendRow();
//            row.setString( "name", analysisDataContainer.getStreetBaseDataContainer(). );
//            row.setDouble(  );
//        }
//
//
//        DoubleColumn xColumn = DoubleColumn.create( "dataset" );
//        DoubleColumn yColumn = DoubleColumn.create( "dataset2" );
//
//        Layout layout = Layout.builder( "title", xColumn.title(), yColumn.title() ).build();
//        ScatterTrace trace = ScatterTrace.builder( table.numberColumn( xcol ), table.numberColumn( ycol ) ).build();
//        new Figure( layout, trace );

        logger.info("Writing csv");
        StreetCsvWriter csvWriter = new StreetCsvWriter("output/street_data.csv");
        Table table = csvWriter.writeCsv(allStreetBaseData);

//        Table wines = Table.read().csv("test_wines.csv");
//
//        Table champagne =
//                        wines.where(
//                                        wines.stringColumn("wine type").isEqualTo("Champagne & Sparkling")
//                                             .and(wines.stringColumn("region").isEqualTo("California")));
//        Plot.show(
//                        ScatterPlot.create("Champagne prices by vintage",
//                                        champagne, "mean retail", "year") );

        String xName = Headers.PKWKM_INDUZ;
        String yName = Headers.PKWKM_INDUZ_NEU;

        Axis xAxis = Axis.builder()
//                         .type( Axis.Type.LOG )
                         .title( xName )
                         .build();
        Axis yAxis = Axis.builder()
//                         .type( Axis.Type.LOG )
                         .title( yName )
                         .build();
        Layout layout = Layout.builder( "plot")
                              .xAxis( xAxis )
                              .yAxis( yAxis )
                              .build();
        Trace trace = ScatterTrace.builder( table.numberColumn( xName ), table.numberColumn( yName ) )
                                         .build();

        double[] xx = new double[]{ 1., 200.};

        Trace trace1 = ScatterTrace.builder( xx, xx )
                .mode(ScatterTrace.Mode.LINE)
                .build();

        Plot.show( new Figure( layout, trace, trace1 ) );


    }
}
