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


        final String regexToExclude = "(A...B.*)|(A....B.*)"; // Bundesstrassen, die von Autobahnen ausgehen.

        // yyyy man könnte (sollte?) den table in den StreetAnalysisDataContainer mit hinein geben, und die Werte gleich dort eintragen.  kai, feb'24
        List<StreetAnalysisDataContainer> allStreetBaseData = scraper
                                                                              .extractAllLocalBaseData( "./data/street/all", "A", ".*", regexToExclude )
                                                                              .stream()
                                                                              .map( streetBaseDataContainer -> new StreetAnalysisDataContainer( streetBaseDataContainer, constructionCostFactor ) )
                                                                              .toList();

        logger.info( "Writing csv" );
        StreetCsvWriter csvWriter = new StreetCsvWriter( "output/street_data.csv" );
        Table table = csvWriter.writeCsv( allStreetBaseData );

        table.addColumns( table.numberColumn( Headers.INVCOST_ORIG ).multiply( constructionCostFactor ).setName( Headers.INVCOST50 ) );

        // ===
        Figures1KN figures1 = new Figures1KN( table );
        Figures2KN figures2 = new Figures2KN( table );

        List<Figure> plots1 = new ArrayList<>();
        List<Figure> plots2 = new ArrayList<>();

        plots1.add( figures1.nkv_el03() );
        plots1.add( figures1.nkv_carbon700() );
        plots1.add( figures1.nkv_el03_carbon700() );

//        plots1.add( figures1.elasticities() );
//        plots1.add( figures1.fzkmDiff() );
//        plots1.add( figures1.carbon() );
//        plots1.add( figures1.invcost() );
//        plots1.add( figures1.nkv_el03_diff() );
//        plots1.add( figures1.dtv() );
//        plots1.add( figures1.fzkmNew() );

        plots2.add( figures2.nkvVsDtv() );

        plots2.add( figures2.cost_VS_nkvOrig() );

        plots2.add( figures2.costOrigVsCumulativeCostOrig() );

        plots2.add( figures2.invcost50_vs_nkvEl03Cprice215Invcost50Capped5() );
        plots2.add( figures2.cumulativeCost50_vs_nkvEl03Cprice215Invcost50Capped5() );
        plots2.add( figures2.carbon_vs_nkvEl03Cprice215Invcost50Capped5() );

        plots2.add( figures2.invcost50_vs_NkvEl03Cprice600Invcost50() );
        plots2.add( figures2.cumulativeCost50_vs_nkvEl03Cprice600Invcost50Capped5() );

        // ===



        String page = MultiPlotUtils.pageTop() + System.lineSeparator();
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
        table.numberColumn( Headers.CO2_COST_EL03 ).setPrintFormatter( format, "n/a" );

        Table table2 = table.where( table.numberColumn( Headers.NKV_EL03_CARBON215_INVCOST50 ).isLessThan( 1. ) );

        System.out.println(BvwpUtils.SEPARATOR);
        System.out.println( table.summarize( Headers.NKV_ORIG, count ).by(Headers.EINSTUFUNG ).print() );
        System.out.println( System.lineSeparator() + "Davon müssen folgende nachbewertet werden:");
        System.out.println( table2.summarize( Headers.NKV_ORIG, count ).by(Headers.EINSTUFUNG ) );

        System.out.println(BvwpUtils.SEPARATOR);
        System.out.println( table.summarize( Headers.INVCOST_ORIG, sum, mean, stdDev, min, max ).by(Headers.EINSTUFUNG ) );
        System.out.println( System.lineSeparator() + "Davon müssen folgende nachbewertet werden:");
        System.out.println( table2.summarize( Headers.INVCOST_ORIG, sum, mean, stdDev, min, max ).by(Headers.EINSTUFUNG ) );

        System.out.println(BvwpUtils.SEPARATOR);
        System.out.println( table.summarize( Headers.CO2_COST_EL03, sum, mean, stdDev, min, max ).by(Headers.EINSTUFUNG ) );
        System.out.println( System.lineSeparator() + "Davon müssen folgende nachbewertet werden:");
        System.out.println( table2.summarize( Headers.CO2_COST_EL03, sum, mean, stdDev, min, max ).by(Headers.EINSTUFUNG ) );
    }

}
