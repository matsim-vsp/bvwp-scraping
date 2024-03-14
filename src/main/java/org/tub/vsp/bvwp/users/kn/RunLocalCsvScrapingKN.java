package org.tub.vsp.bvwp.users.kn;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.tub.vsp.bvwp.BvwpUtils;
import org.tub.vsp.bvwp.data.Headers;
import org.tub.vsp.bvwp.data.container.analysis.StreetAnalysisDataContainer;
import org.tub.vsp.bvwp.data.type.Einstufung;
import org.tub.vsp.bvwp.io.StreetCsvWriter;
import org.tub.vsp.bvwp.plot.MultiPlotUtils;
import org.tub.vsp.bvwp.scraping.StreetScraper;
import tech.tablesaw.api.Row;
import tech.tablesaw.api.Table;
import tech.tablesaw.plotly.display.Browser;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Paths;
import java.text.NumberFormat;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

import static tech.tablesaw.aggregate.AggregateFunctions.*;

public class RunLocalCsvScrapingKN{
    private static final Logger logger = LogManager.getLogger( RunLocalCsvScrapingKN.class );

    public static void main(String[] args) throws IOException{
        Locale.setDefault( Locale.US );

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

        final double constructionCostFactor = 1.5;


        // yyyy man könnte (sollte?) den table in den StreetAnalysisDataContainer mit hinein geben, und die Werte gleich dort eintragen.  kai, feb'24
        List<StreetAnalysisDataContainer> allStreetBaseData = scraper
                                                                              .extractAllLocalBaseData( "./data/street/all", "A", ".*" )
                                                                              .stream()
                                                                              .map( streetBaseDataContainer -> new StreetAnalysisDataContainer( streetBaseDataContainer, constructionCostFactor ) )
                                                                              .toList();

        logger.info( "Writing csv" );
        StreetCsvWriter csvWriter = new StreetCsvWriter( "output/street_data.csv" );
        Table table = csvWriter.writeCsv( allStreetBaseData );

        table.addColumns( table.numberColumn( Headers.COST_OVERALL ).multiply( constructionCostFactor ).setName( Headers.COST_OVERALL_INCREASED ) );

//        final Table newTable = table.selectColumns( "nkvDiff", Headers.COST_OVERALL );
//        LinearModel winsModel = OLS.fit( Formula.lhs("nkvDiff" ), newTable.smile().toDataFrame() );
//        System.out.println( winsModel );
//        System.exit(-1);

        // ===


        FiguresKN figures = new FiguresKN( table );

        String page = MultiPlotUtils.pageTop + System.lineSeparator() +
                                      figures.createFigureFzkmNew().asJavascript( "plot1" ) + System.lineSeparator() +
                                      figures.createFigureElasticities().asJavascript( "plot2" ) + System.lineSeparator() +
                                      figures.createFigureFzkmDiff().asJavascript( "plot3" ) + System.lineSeparator() +
                                      figures.createFigureCO2().asJavascript( "plot4" ) + System.lineSeparator() +
                                      figures.createFigureCostVsLanekm().asJavascript( "plot5" ) + System.lineSeparator() +
                                      figures.createFigureNkvVsLanekm().asJavascript( "plot6" ) + System.lineSeparator() +
                                      figures.createFigureDtv().asJavascript( "plot7" ) + System.lineSeparator() +
                                      figures.createFigureFzkmNew().asJavascript( "plot8" ) + System.lineSeparator() +
                                      figures.createFigureNkvVsDtv().asJavascript( "plotA" ) + System.lineSeparator() +
                                      figures.createFigureCostVsNkvOld().asJavascript( "plotB" ) + System.lineSeparator() +
                                      figures.createFigureCostVsNkvNew().asJavascript( "plotC" ) + System.lineSeparator() +
                                      figures.createFigureCO2VsNkvNew().asJavascript( "plotD" ) + System.lineSeparator() +
                                      MultiPlotUtils.pageBottom;

        File outputFile = Paths.get("multiplot.html" ).toFile();

        try ( FileWriter fileWriter = new FileWriter(outputFile)) {
            fileWriter.write(page);
        }

        new Browser().browse(outputFile );

        // ===

        Comparator<Row> comparator = ( o1, o2 ) -> {
	    Einstufung p1 = Einstufung.valueOf( o1.getString( Headers.EINSTUFUNG ) );
	    Einstufung p2 = Einstufung.valueOf( o2.getString( Headers.EINSTUFUNG ) );
	    return p1.compareTo( p2 );
	};
        table = table.sortOn( comparator );
        NumberFormat format = NumberFormat.getCompactNumberInstance();
        format.setMaximumFractionDigits( 0 );
        table.numberColumn( Headers.CO2_COST_NEU ).setPrintFormatter( format, "n/a" );

        Table table2 = table.where( table.numberColumn( Headers.NKV_EL03_CO2_CONSTRUCTION ).isLessThan( 1. ) );

        System.out.println(BvwpUtils.SEPARATOR);
        System.out.println( table.summarize( Headers.NKV_ORIG, count ).by(Headers.EINSTUFUNG ).print() );
        System.out.println( System.lineSeparator() + "Davon müssen folgende nachbewertet werden:");
        System.out.println( table2.summarize( Headers.NKV_ORIG, count ).by(Headers.EINSTUFUNG ) );

        System.out.println(BvwpUtils.SEPARATOR);
        System.out.println( table.summarize( Headers.COST_OVERALL, sum, mean, stdDev, min, max ).by(Headers.EINSTUFUNG ) );
        System.out.println( System.lineSeparator() + "Davon müssen folgende nachbewertet werden:");
        System.out.println( table2.summarize( Headers.COST_OVERALL, sum, mean, stdDev, min, max ).by(Headers.EINSTUFUNG ) );

        System.out.println(BvwpUtils.SEPARATOR);
        System.out.println( table.summarize( Headers.CO2_COST_NEU, sum, mean, stdDev, min, max ).by(Headers.EINSTUFUNG ) );
        System.out.println( System.lineSeparator() + "Davon müssen folgende nachbewertet werden:");
        System.out.println( table2.summarize( Headers.CO2_COST_NEU, sum, mean, stdDev, min, max ).by(Headers.EINSTUFUNG ) );
    }

}
