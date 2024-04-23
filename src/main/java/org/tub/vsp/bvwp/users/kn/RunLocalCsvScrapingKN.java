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
import tech.tablesaw.plotly.components.Figure;
import tech.tablesaw.plotly.display.Browser;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Paths;
import java.text.NumberFormat;
import java.util.ArrayList;
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
                                                                              .map(streetBaseDataContainer -> new StreetAnalysisDataContainer(streetBaseDataContainer, constructionCostFactor, 0.))
                                                                              .toList();

        logger.info( "Writing csv" );
        StreetCsvWriter csvWriter = new StreetCsvWriter( "output/street_data.csv" );
        Table table = csvWriter.writeCsv( allStreetBaseData );

        table.addColumns( table.numberColumn( Headers.COST_OVERALL ).multiply( constructionCostFactor ).setName( Headers.COST_OVERALL_INCREASED ) );

        // ===
        FiguresKN figures = new FiguresKN( table );

        List<Figure> plots1 = new ArrayList<>();
        List<Figure> plots2 = new ArrayList<>();

        plots1.add( figures.createFigureNkvVsDtv() );
        plots1.add( figures.createFigureElasticities() );
        plots1.add( figures.createFigureFzkmDiff() );
        plots1.add( figures.createFigureCO2() );
        plots1.add( figures.createFigureCostVsLanekm() );
        plots1.add( figures.createFigureNkvVsLanekm() );
        plots1.add( figures.createFigureDtv() );
        plots1.add( figures.createFigureFzkmNew() );

        plots2.add( figures.createFigureFzkmNew() );
        plots2.add( figures.createFigureCostVsNkvOld() );
        plots2.add( figures.costOrigVsCumulativeCostOrig() );
        plots2.add( figures.cost_increased_VsNkv_El03_CO2_215_Baukosten_50() );
        plots2.add( figures.cumulativeCost50VsNkv_el03_carbon2015_invcost50_capped5() );
        plots2.add( figures.co2_vs_nkv_new() );
        // ===



        String page = MultiPlotUtils.pageTop + System.lineSeparator();
        for( int ii=0; ii<plots1.size(); ii++ ) {
            page += plots1.get(ii).asJavascript( "plot" + (ii+1) ) + System.lineSeparator() ;
        }
        for( int ii=0; ii<plots2.size(); ii++ ) {
            final char c = (char) (ii + 65); // generate A, B, ... to be backwards compatible with what we had so far.  kai, mar'24
            logger.warn( "c=" + c );
            page += plots2.get(ii ).asJavascript( "plot" + c ) + System.lineSeparator() ;
        }
        page += MultiPlotUtils.pageBottom;

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

        Table table2 = table.where( table.numberColumn( Headers.NKV_EL03_CARBON215_INVCOST50 ).isLessThan( 1. ) );

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
