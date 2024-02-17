package org.tub.vsp.bvwp;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.tub.vsp.bvwp.data.Headers;
import org.tub.vsp.bvwp.data.container.analysis.StreetAnalysisDataContainer;
import org.tub.vsp.bvwp.io.StreetCsvWriter;
import org.tub.vsp.bvwp.plot.MultiPlotExample;
import org.tub.vsp.bvwp.scraping.StreetScraper;
import tech.tablesaw.api.*;
import tech.tablesaw.plotly.components.Axis;
import tech.tablesaw.plotly.components.Figure;
import tech.tablesaw.plotly.components.Layout;
import tech.tablesaw.plotly.components.Marker;
import tech.tablesaw.plotly.display.Browser;
import tech.tablesaw.plotly.traces.ScatterTrace;
import tech.tablesaw.plotly.traces.Trace;
import tech.tablesaw.selection.Selection;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

public class RunLocalCsvScraping {
    private static final Logger logger = LogManager.getLogger(RunLocalCsvScraping.class);

    public static void main(String[] args) throws IOException{
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

        logger.info("Writing csv");
        StreetCsvWriter csvWriter = new StreetCsvWriter("output/street_data.csv");
        Table table = csvWriter.writeCsv(allStreetBaseData);

        table = table.where( table.numberColumn( Headers.NKV_INDUZ_CO2 ).isLessThan( 1.) );

        table = table.sortDescendingOn( Headers.CO2_NEU );
        String xName = Headers.CO2_NEU;
        Axis xAxis = Axis.builder().title( xName )
//                         .type( Axis.Type.LOG )
                         .build();

        Figure figure;
        {
            String yName = Headers.PKWKM_INDUZ;
            String y2Name = Headers.PKWKM_INDUZ_NEU;

            Axis yAxis = Axis.builder()
//                         .type( Axis.Type.LOG )
                             .title( yName )
                             .build();
            Layout layout = Layout.builder( "plot" ).xAxis( xAxis ).yAxis( yAxis )
                                  .width( 2000 )
                                  .build();


            Trace trace = ScatterTrace.builder( table.numberColumn( xName ), table.numberColumn( yName ) )
                                      .name( yName )
                                      .text( table.stringColumn( Headers.PROJECT_NAME ).asObjectArray() )
                                      .build();
            Trace trace2 = ScatterTrace.builder( table.numberColumn( xName ), table.numberColumn( y2Name ) )
                                       .name( y2Name )
                                       .text( table.stringColumn( Headers.PROJECT_NAME ).asObjectArray() )
                                       .marker( Marker.builder().color( "red" ).build() )
                                       .build();

//            double[] xx = new double[]{1., 200.};
//            Trace trace1 = ScatterTrace.builder( xx, xx )
//                                       .mode( ScatterTrace.Mode.LINE )
//                                       .build();

            figure = new Figure( layout, trace, trace2 );
        }


        Figure figure2;
        {
            String yName = Headers.NKV_NO_CHANGE;
            String y3Name = Headers.NKV_CO2;
            String y2Name = Headers.NKV_INDUZ_CO2;

            Axis yAxis = Axis.builder()
//                         .type( Axis.Type.LOG )
                             .range( 1.1*table.numberColumn( y2Name ).min(),4. )
                             .title( yName )
                             .build();
            Layout layout = Layout.builder( "plot" )
                                  .xAxis( xAxis )
                                  .yAxis( yAxis )
                                  .width( 2000 )
                                  .build();
            Trace trace = ScatterTrace.builder( table.numberColumn( xName ), table.numberColumn( yName ) )
                                      .text( table.stringColumn( Headers.PROJECT_NAME ).asObjectArray() )
                                      .name( yName )
                                      .build();
            Trace trace2 = ScatterTrace.builder( table.numberColumn( xName ), table.numberColumn( y2Name ) )
                                       .text( table.stringColumn( Headers.PROJECT_NAME ).asObjectArray() )
                                       .name( y2Name )
                                       .marker( Marker.builder().color( "red" ).build() )
                                       .build();
            Trace trace3 = ScatterTrace.builder( table.numberColumn( xName ), table.numberColumn( y3Name ) )
                                       .text( table.stringColumn( Headers.PROJECT_NAME ).asObjectArray() )
                                       .name( y3Name )
                                       .marker( Marker.builder().color( "gray" ).build() )
                                       .build();

            double[] xx = new double[]{0., 1.1*table.numberColumn( xName ).max() };
            double[] yy = new double[]{1., 1.};
            Trace trace4 = ScatterTrace.builder( xx, yy )
                                       .mode( ScatterTrace.Mode.LINE )
                                       .build();

            figure2 = new Figure( layout, trace, trace3, trace2, trace4 );
        }

        Figure figure3;
        {
            String yName = Headers.COST_OVERALL;
            String y3Name = Headers.COST_OVERALL;
            String y2Name = Headers.COST_OVERALL;

            Axis yAxis = Axis.builder()
//                         .type( Axis.Type.LOG )
//                             .range( 1.1*table.numberColumn( y2Name ).min(),4. )
                             .title( yName )
                             .build();
            Layout layout = Layout.builder( "plot" )
                                  .xAxis( xAxis )
                                  .yAxis( yAxis )
                                  .width( 2000 )
                                  .build();
            Trace trace = ScatterTrace.builder( table.numberColumn( xName ), table.numberColumn( yName ) )
                                      .text( table.stringColumn( Headers.PROJECT_NAME ).asObjectArray() )
                                      .name( yName )
                                      .build();
            Trace trace2 = ScatterTrace.builder( table.numberColumn( xName ), table.numberColumn( y2Name ) )
                                       .text( table.stringColumn( Headers.PROJECT_NAME ).asObjectArray() )
                                       .name( y2Name )
                                       .marker( Marker.builder().color( "red" ).build() )
                                       .build();
            Trace trace3 = ScatterTrace.builder( table.numberColumn( xName ), table.numberColumn( y3Name ) )
                                       .text( table.stringColumn( Headers.PROJECT_NAME ).asObjectArray() )
                                       .name( y3Name )
                                       .marker( Marker.builder().color( "gray" ).build() )
                                       .build();

//            double[] xx = new double[]{1., 200.};
//            Trace trace1 = ScatterTrace.builder( xx, xx )
//                                       .mode( ScatterTrace.Mode.LINE )
//                                       .build();

            figure3 = new Figure( layout, trace, trace3, trace2 );
        }

        String page = MultiPlotExample.pageTop + System.lineSeparator() +
                                      figure2.asJavascript( "plot1" ) + System.lineSeparator() +
                                      figure.asJavascript( "plot2" ) + System.lineSeparator() +
                                      figure3.asJavascript( "plot3" ) + System.lineSeparator() +
                                      MultiPlotExample.pageBottom;

        File outputFile = Paths.get("multiplot.html" ).toFile();

        try ( FileWriter fileWriter = new FileWriter(outputFile)) {
            fileWriter.write(page);
        }

        new Browser().browse(outputFile );

    }
}
