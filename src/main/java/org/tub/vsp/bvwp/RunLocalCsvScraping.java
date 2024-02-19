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
import tech.tablesaw.plotly.components.Axis.Type;
import tech.tablesaw.plotly.components.Figure;
import tech.tablesaw.plotly.components.Layout;
import tech.tablesaw.plotly.components.Marker;
import tech.tablesaw.plotly.display.Browser;
import tech.tablesaw.plotly.traces.ScatterTrace;
import tech.tablesaw.plotly.traces.ScatterTrace.Mode;
import tech.tablesaw.plotly.traces.Trace;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.List;

import static org.tub.vsp.bvwp.scraping.StreetScraper.projectString;

public class RunLocalCsvScraping {
    private static final Logger logger = LogManager.getLogger(RunLocalCsvScraping.class);
    private static final int plotWidth = 1700;

    public static void main(String[] args) throws IOException{
        logger.warn( "(vermutl. weitgehend gelöst) Teilweise werden die Hauptprojekte bewertet und nicht die " +
            "Teilprojekte (A20); teilweise werden die Teilprojekte " +
            "bewertet aber nicht das Hauptprojekt (A2).  Müssen aufpassen, dass nichts unter den Tisch fällt." );
        logger.warn( "Bei https://www.bvwp-projekte.de/strasse/A559-G10-NW/A559-G10-NW.html hat evtl. die Veränderung " +
            "Betriebsleistung PV falsches VZ.  Nutzen (positiv) dann wieder richtig." );
        logger.warn( "Wieso geht bei der https://www.bvwp-projekte.de/strasse/A14-G20-ST-BB/A14-G20-ST-BB.html der " +
            "Nutzen mit impl und co2Price sogar nach oben?" );
        logger.warn( "===========" );

        String positivListe = projectString( "BW", "A5" )
            + projectString( "BW", "A6" )
            + projectString( "BY", "A003" )
            + projectString( "BY", "A008" ) // Ausbau München - Traunstein (- Salzburg)
            + projectString( "BY", "A009" )
            + projectString( "BY", "A092" )
            + projectString( "BY", "A094" )
            + projectString( "BY", "A099" )
            + projectString( "HB", "A27" )
            + projectString( "HE", "A3" )
            + projectString( "HE", "A5" )
            + projectString( "HE", "A45" )
            + projectString( "HE", "A60" )
            + projectString( "HE", "A67" )
            + projectString( "HE", "A67" )
            + projectString( "NI", "A2" )
            + projectString( "NI", "A7" )
            + projectString( "NI", "A27" )
            + projectString( "NI", "A30" )
            + projectString( "NW", "A1" )
            + projectString( "NW", "A2" )
            + projectString( "NW", "A3" )
            + projectString( "NW", "A4" )
            + projectString( "NW", "A30" )
            + projectString( "NW", "A40" )
            + projectString( "NW", "A42" )
            + projectString( "NW", "A43" )
            + projectString( "NW", "A45" )
            + projectString( "NW", "A52" )
            + projectString( "NW", "A57" )
            + projectString( "NW", "A59" )
            + projectString( "NW", "A559" )
            + projectString( "RP", "A1" )
            + projectString( "RP", "A1" )
            + "(abcdef)"; // um das letzte "|" abzufangen!


        StreetScraper scraper = new StreetScraper();

        logger.info( "Starting scraping" );

        // yyyy man könnte (sollte?) den table in den StreetAnalysisDataContainer mit hinein geben, und die Werte gleich dort eintragen.  kai, feb'24

        List<StreetAnalysisDataContainer> allStreetBaseData = scraper
            .extractAllLocalBaseData( "./data/street/all", "A", positivListe )
            .stream()
            .map( StreetAnalysisDataContainer::new )
            .toList();

        logger.info( "Writing csv" );
        StreetCsvWriter csvWriter = new StreetCsvWriter( "output/street_data.csv" );
        Table table = csvWriter.writeCsv( allStreetBaseData );

//        table = table.where( table.numberColumn( Headers.NKV_INDUZ_CO2 ).isLessThan( 1.) );

        String xName;
        Axis.AxisBuilder xAxisBuilder = Axis.builder();
        {
            xName = Headers.B_CO2_NEU;
            xAxisBuilder.type( Type.LINEAR );
        }
//        {
//            xName = Headers.NKV_INDUZ_CO2;
//            xAxisBuilder
//                             //                         .type( Axis.Type.LOG )
//                             .autoRange( Axis.AutoRange.REVERSED );
//        }
        table = table.sortDescendingOn( xName );
        Axis xAxis = xAxisBuilder.title( xName ).build();
        final int plotWidth = 1700;

//        Figure figurePkwKm = createFigurePkwKm( xAxis, table, xName );
        Figure figureNkv = createFigureNkv( xAxis, plotWidth, table, xName );
//        Figure figure3 = createFigure3( xAxis, plotWidth, table, xName );
//        Figure figureCO2 = createFigureCO2( xAxis, plotWidth, table, xName );

        Figure figureNkvChange = createFigureNkvChange(plotWidth, table, xName );

        String page = MultiPlotExample.pageTop + System.lineSeparator() +
            figureNkv.asJavascript( "plot1" ) + System.lineSeparator() +
//                                      figurePkwKm.asJavascript( "plot2" ) + System.lineSeparator() +
//                                      figure3.asJavascript( "plot3" ) + System.lineSeparator() +
//                                      figureCO2.asJavascript( "plot4" ) + System.lineSeparator() +
            figureNkvChange.asJavascript("plot5") + System.lineSeparator()+
            MultiPlotExample.pageBottom;

        File outputFile = Paths.get("multiplot.html" ).toFile();

        try ( FileWriter fileWriter = new FileWriter(outputFile)) {
            fileWriter.write(page);
        }

        new Browser().browse(outputFile);

    }



    private static Figure createFigure3( Axis xAxis, int plotWidth, Table table, String xName ){
        Figure figure3;
        String yName = Headers.COST_OVERALL;
        String y3Name = Headers.COST_OVERALL;
        String y2Name = Headers.COST_OVERALL;

        Axis yAxis = Axis.builder()
//                         .type( Axis.Type.LOG )
//                             .range( 1.1*table.numberColumn( y2Name ).min(),4. )
            .title( yName )
            .build();
        Layout layout = Layout.builder( yName )
            .xAxis( xAxis )
            .yAxis( yAxis )
            .width( plotWidth )
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
        return figure3;
    }
    private static Figure createFigureNkv( Axis xAxis, int plotWidth, Table table, String xName ){
        Figure figure2;
        String yName = Headers.NKV_NO_CHANGE;
        String y3Name = Headers.NKV_CO2;
//        String y2Name = Headers.NKV_INDUZ_CO2;

        Axis yAxis = Axis.builder()
            .type( Type.LINEAR )
            .range( Double.min(
                    1.1*table.numberColumn(y3Name).min(), 1.1*table.numberColumn( yName ).min()),
                20. )
            .title( yName )
            .build();
        Layout layout = Layout.builder( yName )
            .xAxis( xAxis )
            .yAxis( yAxis )
            .width( plotWidth )
            .build();
        Trace trace = ScatterTrace.builder( table.numberColumn( xName ), table.numberColumn( yName ) )
            .text( table.stringColumn( Headers.PROJECT_NAME ).asObjectArray() )
            .name( yName )
            .build();
//        Trace trace2 = ScatterTrace.builder( table.numberColumn( xName ), table.numberColumn( y2Name ) )
//                                   .text( table.stringColumn( Headers.PROJECT_NAME ).asObjectArray() )
//                                   .name( y2Name )
//                                   .marker( Marker.builder().color( "red" ).build() )
//                                   .build();
        Trace trace3 = ScatterTrace.builder( table.numberColumn( xName ), table.numberColumn( y3Name ) )
            .text( table.stringColumn( Headers.PROJECT_NAME ).asObjectArray() )
            .name( y3Name )
            .marker( Marker.builder().color( "gray" ).build() )
            .build();

        double[] xx = new double[]{0., 1.1* table.numberColumn( xName ).max() };
        double[] yy = new double[]{1., 1.};
        Trace trace4 = ScatterTrace.builder( xx, yy )
            .mode( Mode.LINE )
            .marker(Marker.builder().color( "red" ).build())
            .build();

//        figure2 = new Figure( layout, trace, trace3, trace2, trace4 );
        figure2 = new Figure( layout, trace, trace3, trace4 );
        return figure2;
    }
    private static Figure createFigurePkwKm( Axis xAxis, Table table, String xName ){
        Figure figure;
        String yName = Headers.PKWKM_INDUZ;
        String y2Name = Headers.PKWKM_INDUZ_NEU;

        Axis yAxis = Axis.builder()
            .title( yName )
            .type( Axis.Type.LOG )
            .build();
        Layout layout = Layout.builder( "plot" ).xAxis( xAxis ).yAxis( yAxis )
            .width( plotWidth )
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
        return figure;
    }
    private static Figure createFigureCO2( Axis xAxis, int plotWidth, Table table, String xName ){
        String yName = Headers.B_CO2_NEU;
        String y2Name = Headers.B_CO2_NEU;

        Axis yAxis = Axis.builder()
            .title( yName )
//                         .type( Axis.Type.LOG )
            .build();
        Layout layout = Layout.builder( "plot" ).xAxis( xAxis ).yAxis( yAxis )
            .width( plotWidth )
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

        return new Figure( layout, trace, trace2 );
    }

    private static Figure createFigureNkvChange(int plotWidth, Table table, String xName) {

        String xNameLocal = Headers.NKV_NO_CHANGE;
        String yName = Headers.NKV_CO2;
        double maxX = 20.;
        double maxY = 20.;

        Axis xAxis = Axis.builder()
            .type( Type.LINEAR )
            .title( xNameLocal )
            .range(0. ,maxX)
//                             .autoRange( Axis.AutoRange.REVERSED );
            .build();

        table = table.sortDescendingOn( xNameLocal );

        Axis yAxis = Axis.builder()
            .type( Type.LINEAR )
            .range(0. ,maxY)
//                             .range( 1.1*table.numberColumn( y2Name ).min(),4. )
            .title( yName )
            .build();

        Layout layout = Layout.builder( xNameLocal )
            .xAxis( xAxis )
            .yAxis( yAxis )
            .width( plotWidth )
            .build();

        Trace cbrOverCbrTrace = ScatterTrace.builder( table.numberColumn( xNameLocal ), table.numberColumn( yName ) )
            .text( table.stringColumn( Headers.PROJECT_NAME ).asObjectArray() )
            .name( yName )
            .marker( Marker.builder().color( "blue" ).build() )
            .build();

//        double[] xx = new double[]{0., 1.1* table.numberColumn( xNameLocal ).max() };
//        double[] yy = new double[]{0., 1.1* table.numberColumn( xNameLocal ).max()};
        double[] xx = new double[]{0., maxX };
        double[] yy = new double[]{0., maxY};
        double[] xy1 = new double[]{1., 1.};

        Trace diagonale = ScatterTrace.builder( xx, yy )
            .name(xName + " = " + yName)
            .mode( Mode.LINE )
            .build();

        Trace horizontalCbr1 = ScatterTrace.builder( xx, xy1 )
            .name(yName + " = 1")
            .mode( Mode.LINE )
            .marker( Marker.builder().color( "gray" ).build() )
            .build();

        Trace verticalCbr1 = ScatterTrace.builder( xy1, yy )
            .name(xName + " = 1")
            .mode( Mode.LINE )
            .marker( Marker.builder().color( "gray" ).build() )
            .build();


        return new Figure( layout, cbrOverCbrTrace, diagonale, horizontalCbr1, verticalCbr1 );
    }


}
