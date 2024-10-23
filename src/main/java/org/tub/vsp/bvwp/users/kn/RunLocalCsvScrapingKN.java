package org.tub.vsp.bvwp.users.kn;

import org.apache.commons.math3.util.Pair;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.tub.vsp.bvwp.BvwpUtils;
import org.tub.vsp.bvwp.data.container.analysis.StreetAnalysisDataContainer;
import org.tub.vsp.bvwp.io.StreetCsvWriter;
import org.tub.vsp.bvwp.plot.MultiPlotUtils;
import org.tub.vsp.bvwp.scraping.StreetScraper;
import tech.tablesaw.api.DoubleColumn;
import tech.tablesaw.api.NumberColumn;
import tech.tablesaw.api.Table;
import tech.tablesaw.columns.Column;
import tech.tablesaw.io.csv.CsvWriteOptions;
import tech.tablesaw.io.csv.CsvWriter;
import tech.tablesaw.plotly.components.Figure;
import tech.tablesaw.plotly.display.Browser;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Paths;
import java.text.NumberFormat;
import java.util.*;

import static org.tub.vsp.bvwp.data.Headers.*;

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

        String filePath = "../../shared-svn/";
        Map<String, Double> constructionCostsByProject = BvwpUtils.getConstructionCostsFromTudFile(filePath );

        final String regexToExclude = "(A...B.*)|(A....B.*)"; // Bundesstrassen, die von Autobahnen ausgehen.

        // yyyy man könnte (sollte?) den table in den StreetAnalysisDataContainer mit hinein geben, und die Werte gleich dort eintragen.  kai, feb'24
        List<StreetAnalysisDataContainer> allStreetBaseData = scraper
                                                                              .extractAllLocalBaseData( "./data/street/all", "A", ".*", regexToExclude )
                                                                              .stream()
                                                                              .map(streetBaseDataContainer -> new StreetAnalysisDataContainer(
                                                                                              streetBaseDataContainer,
                                                                                              constructionCostsByProject.get(streetBaseDataContainer.getProjectInformation().getProjectNumber())
                                                                              ))
                                                                              .toList();

        logger.info( "Writing csv" );
        StreetCsvWriter csvWriter = new StreetCsvWriter( "output/street_data.csv" );
        Table table = csvWriter.writeCsv( allStreetBaseData );

        // ===
        final String NKV_ORIG_CAPPED5 = addCap( 5, table, NKV_ORIG);
        Figures1KN figures1 = new Figures1KN( table, NKV_ORIG_CAPPED5 );
        Figures2KN figures2 = new Figures2KN( table );

        List<Pair<String,Figure>> figures = new ArrayList<>();

        figures.add(Pair.create( createHeader1( "New plots" ), null ) );

        // N pro CO2:
        table.addColumns( table.doubleColumn( NKV_ORIG )
                               .multiply( table.doubleColumn( INVCOST_ORIG ) )
                               .divide( table.doubleColumn( CO2_ORIG ) )
                               .setName( NProCo2_ORIG )
                        );

        table.addColumns( table.doubleColumn( NKV_ELTTIME_CARBON2000_EMOB_INVCOSTTUD )
                               .multiply( table.doubleColumn( INVCOST_TUD ) )
                               .divide( table.doubleColumn( CO2_ELTTIME ) )
                               .setName( NProCo2_ELTTIME_CARBON2000_EMOB_INVCOSTTUD )
                        );

        // === weniger Verkehr:
        table.addColumns( table.doubleColumn( NKV_ELTTIME_CARBON700_EMOB_INVCOSTTUD )
                               .subtract(
                                               table.doubleColumn( VERKEHRSBELASTUNG_PLANFALL )
                                                    .multiply( 0.2 ) // 20% less traffic
                                                    .multiply( 10./30000 ) // this is roughly the slope of NKV(new) vs DTV
                                        ).setName( NKV_ELTTIME_CARBON700_EMOB_INVCOSTTUD_20pctLessTraffic )
                        );
        table.addColumns( table.doubleColumn( NKV_ELTTIME_CARBON700_EMOB_INVCOSTTUD )
                               .subtract(
                                               table.doubleColumn( VERKEHRSBELASTUNG_PLANFALL )
                                                    .multiply( 0.1 ) // 20% less traffic
                                                    .multiply( 10./30000 ) // this is roughly the slope of NKV(new) vs DTV
                                        ).setName( NKV_ELTTIME_CARBON700_EMOB_INVCOSTTUD_10pctLessTraffic )
                        );

//        figures.add( Pair.create( createDefaultKey( figures ), figures2.nProCo2_vs_nkv( NProCo2_ELTTIME_CARBON2000ptpr0_EMOB_INVCOSTTUD, NKV_ELTTIME_CARBON2000ptpr0_EMOB_INVCOSTTUD ) ) );

        figures.add( Pair.create( createHeader2( "CO2 vs Nutzen_pro_CO2" ), figures2.carbonOrig( Integer.MAX_VALUE, NProCo2_ORIG ) ) );

//        figures.add( Pair.create( createHeader2( "CO2 vs Nutzen_pro_CO2" ), figures2.carbonOrig( Integer.MAX_VALUE, NProCo2_ELTTIME_CARBON2000ptpr0_EMOB_INVCOSTTUD ) ) );

        figures.add( Pair.create( createHeader2( "CO2 vs Nutzen_pro_CO2" ), figures2.carbon( Integer.MAX_VALUE, NProCo2_ELTTIME_CARBON2000_EMOB_INVCOSTTUD ) ) );

        figures.add( Pair.create( createHeader2( "CO2 vs Nutzen_pro_CO2" ), figures2.carbonWithEmob( Integer.MAX_VALUE, NProCo2_ELTTIME_CARBON2000_EMOB_INVCOSTTUD ) ) );

        figures.add( Pair.create( createHeader2( "Inv.Kosten vs Nutzen_pro_CO2") , figures2.investmentCostTud( Integer.MAX_VALUE, NProCo2_ELTTIME_CARBON2000_EMOB_INVCOSTTUD ) ) );




        // Induzierter Strassenmehrverkehr:
        figures.add( Pair.create( createHeader1( "Induzierter Strassenmehrverkehr (aus Elastizität 0,3):" ), figures2.fzkmEl03_vs_fzkmOrig() ) );
//        figures.add( figures2.fzkmFromEl03Delta_vs_fzkmOrig() );
        figures.add( Pair.create( createHeader1( "Induzierter Strassenmehrverkehr (aus konstantem Reisezeitbudget):" ), figures2.fzkmFromTtime_vs_fzkmOrig() ) );
//        figures.add( figures2.fzkmFromTtimeDelta_vs_fzkmOrig() );


        // Abhängigkeit von Verkehrsnachfrage:
        figures.add( Pair.create( createHeader1( "Abhängigkeit NKV von Verkehrsmenge:" ), figures2.nkv_vs_dtv( NKV_ORIG ) ) );
        figures.add( Pair.create( createHeader1( "Abhängigkeit NKV von Verkehrsmenge:" ), figures2.nkv_vs_dtv( NKV_ELTTIME_CARBON2000_EMOB_INVCOSTTUD ) ) );
//        figures.add( figures2.nkvNeu_vs_dtv( NKV_ELTTIME_CARBON700TPR0_INVCOSTTUD ) );

        // ===
        // ===

        Map<String,String> nkvs = new LinkedHashMap<>();

        nkvs.put( "... veränderte Investitionskosten:", NKV_INVCOSTTUD );
        nkvs.put( "... veränderten induz. Strassenmehrverkehr:", NKV_ELTTIME );
        nkvs.put( "... erhöhte CO2-Kosten:", NKV_CARBON700 );
        nkvs.put( "... Kombination induz. Strassenmehrverkehr + erh. CO2-Kosten:", NKV_ELTTIME_CARBON700 );
        nkvs.put( "... zusätzlich veränderte Investitionskosten:", NKV_ELTTIME_CARBON700_INVCOSTTUD );
        nkvs.put( "... zusätzlich Berücksichtigung E-Mobilität:", NKV_ELTTIME_CARBON700_EMOB_INVCOSTTUD );
        nkvs.put( "... zusätzlich CO2-Preis jetzt auf 2000:", NKV_ELTTIME_CARBON2000_EMOB_INVCOSTTUD );

        figures.add( Pair.create( createHeader1( "Veränderung NKV durch ..." ), null ) );
        for( Map.Entry<String, String> entry : nkvs.entrySet() ){
            figures.add( Pair.create( createHeader2( entry.getKey() ), figures2.nkvNew_vs_nkvOrig( 5, entry.getValue() ) ) );
        }

        // ---

        figures.add( Pair.create( createHeader1( "Inv.Kosten vs. NKV mit ... " ), null ) );
        figures.add( Pair.create( createHeader2( "... originalem NKV:" ), figures2.investmentCostTud( 5, NKV_ORIG ) ) );
        for( Map.Entry<String, String> entry : nkvs.entrySet() ){
            figures.add( Pair.create( createHeader2( entry.getKey() ), figures2.investmentCostTud( 5, entry.getValue() ) ) );
        }

        // ---

        figures.add( Pair.create( createHeader1( "CO2 vs. NKV mit ... " ), null ) );
        figures.add( Pair.create( createHeader2( "... originalem NKV:" ), figures2.carbonWithEmob( 5, NKV_ORIG ) ) );
        for( Map.Entry<String, String> entry : nkvs.entrySet() ){
            figures.add( Pair.create( createHeader2( entry.getKey() ), figures2.carbonWithEmob( 5, entry.getValue() ) ) );
        }

        figures.add( Pair.create( createHeader2( "cumulative ..." ), figures2.cumBenefitVsCumCost( NKV_ELTTIME_CARBON700_EMOB_INVCOSTTUD ) ) );

        figures.add( Pair.create( createHeader2( "cumulative ..." ), figures2.cumBenefitVsCumCost( NKV_ELTTIME_CARBON2000_EMOB_INVCOSTTUD ) ) );

        figures.add( Pair.create( createHeader1( "Further material ..." ), null ) );


//        figures.add( Pair.create( createHeader1( "Inv.Kosten vs NKV mit veränderten/erhöhten Investitionskosten/induziertem Verkehr/CO2-Kosten/E-Mobilität:"),
//                        figures2.getFigures( 5, NKV_ELTTIME_CARBON700ptpr0_EMOB_INVCOSTTUD ).get( 0 ) );
////        addHeaderPlusMultipleFigures( figures, createHeader1( "XXX vs. NKV mit veränderten/erhöhten Investitionskosten/induziertem Verkehr/CO2-Kosten/E-Mobilität:" ),
////                        figures2.getFigures( 10, NKV_ELTTIME_CARBON700ptpr0_EMOB_INVCOSTTUD ) );
//
//        // ... plus CO2 Preis 2000:
//        figures.add( Pair.create( createHeader1( "Inv.Kosten vs NKV mit veränderten/erhöhten Investitionskosten/induziertem Verkehr/CO2-Kosten++/E-Mobilität:"),
//                        figures2.getFigures( 5, NKV_ELTTIME_CARBON2000ptpr0_EMOB_INVCOSTTUD ).get( 0 ) );


        // Änlichkeit zwischen carbon cost und Investitionskosten:
        figures.add( Pair.create( createHeader1( "Ähnlichkeit CO2-Kosten und Investitionskosten:" ), figures2.carbon_vs_invcostTud() ) );

        // ###  nicht verwendet:

        // changes in investment cost:
        figures.add( Pair.create( createDefaultKey( figures ), figures2.invcost_tud_vs_orig() ) );






//        figures.add( figures2.cost_VS_nkvOrig() );

//        figures.add( figures2.costOrigVsCumulativeCostOrig() );


//        figures.addAll( figures2.getFigures( 5, NKV_ORIG ) );
//        figures.addAll( figures2.getFigures( 5, NKV_ELTTIME_CARBON215_INVCOSTTUD ) );

//        figures.add( figures2.invcosttud_vs_nkvEl03Cprice215Invcosttud( 5) );
//        figures.add( figures2.cumulativeCostTud_vs_nkvEl03Cprice215InvcostTud(5 ) );
//        figures.add( figures2.cumulativeCostTud_vs_nkvEl03Cprice215InvcostTud(Integer.MAX_VALUE ) );
//        figures.add( figures2.invcosttud_vs_nkvEl03Cprice215Invcosttud( Integer.MAX_VALUE) );
//
//        figures.add( figures2.invcosttud_vs_nkvElttimeCarbon700Invcosttud(5) );
////        figures.add( figures2.invcost50_vs_NkvEl03Cprice700InvcostTud() );
////        figures.add( figures2.cumcost50_vs_nkvEl03Cprice700InvcostTud() );
//        figures.add( figures2.invcosttud_vs_nkvElttimeCarbon2000Invcosttud() );
//
//        figures.add( figures2.carbon_vs_nkvEl03Cprice215Invcost50Capped5() );
//
//        figures.add( figures2.nco2v_vs_vs_nkvElttimeCarbon700Invcosttud(5 ) );


        // ===

        {
            File outputFile = Paths.get( "multiplot.html" ).toFile();
            try( FileWriter fileWriter = new FileWriter( outputFile ) ){
                fileWriter.write( MultiPlotUtils.createPageV2( figures ) );
            }
            new Browser().browse( outputFile );
        }

//        System.exit(-1);

        // ===

        Table table2 = table.where( table.stringColumn( PROJECT_NAME ).startsWith( "A20-" )
                             .or( table.stringColumn( PROJECT_NAME ).startsWith( "A008-G010" ) )
                             .or( table.stringColumn( PROJECT_NAME ).startsWith( "A39-" ) )
                                  );

        Table table3 = table.where( table.stringColumn( PROJECT_NAME ).startsWith( "A14-" )
                                  );

        Table table4 = table.where( table.stringColumn( PROJECT_NAME ).startsWith( "A59-G80" )
//                             .or( table.stringColumn( PROJECT_NAME ).startsWith( "A661-G30" ) )
                                         .or( table.stringColumn( PROJECT_NAME ).startsWith( "A099-G030" ) )
//                                         .or( table.stringColumn( PROJECT_NAME ).startsWith( "A5-G20" ) )
//                                         .or( table.stringColumn( PROJECT_NAME ).startsWith( "A3-G70" ) )
                                         .or( table.stringColumn( PROJECT_NAME ).startsWith( "A8-G40" ) )
                                         .or( table.stringColumn( PROJECT_NAME ).startsWith( "A67-G10" ) )
                                  );

        Table combined = table2.append( table3 ).append( table4 );

        final Table table2b = createVariousNKVs( table2 );
        final Table table3b = createVariousNKVs( table3 );
        final Table table4b = createVariousNKVs( table4 );
        final Table combinedB = createVariousNKVs( combined );

        new CsvWriter().write( combinedB, CsvWriteOptions.builder( "/dev/stdout" ).separator( ';' ).usePrintFormatters( true ).build() );

        new CsvWriter().write( combinedB, CsvWriteOptions.builder( "table.csv" ).separator( ';' ).usePrintFormatters( true ).build() );

        final Table all = createVariousNKVs( table );
        new CsvWriter().write( all, CsvWriteOptions.builder( "all.csv" ).separator( ';' ).usePrintFormatters( true ).build() );

        System.exit(-1);

        System.out.println();

        System.out.println( table2b );
        System.out.println();
        System.out.println( table3b );
        System.out.println();
        System.out.println( table4b );


        //        {
//            File tableFile = Paths.get( "table.html" ).toFile();
//            try( FileWriter fileWriter = new FileWriter( tableFile ) ){
//                System.out.println("we are here 10");
//                final String html = table3.write().toString( "csv" );
//                System.out.println("we are here 20");
//                System.out.println( html );
//                System.out.println("we are here 30");
//                fileWriter.write( html );
//            }
//            new Browser().browse( tableFile );
//        }

//        System.out.println( table3.write().toString("csv") );

//        newTable.write().csv( "a20.csv" );



//        Comparator<Row> comparator = ( o1, o2 ) -> {
//	    Einstufung p1 = Einstufung.valueOf( o1.getString( Headers.EINSTUFUNG ) );
//	    Einstufung p2 = Einstufung.valueOf( o2.getString( Headers.EINSTUFUNG ) );
//	    return p1.compareTo( p2 );
//	};
//        table = table.sortOn( comparator );
//        NumberFormat format = NumberFormat.getCompactNumberInstance();
//        format.setMaximumFractionDigits( 0 );
//        table.numberColumn( Headers.CO2_COST_EL03 ).setPrintFormatter( format, "n/a" );

//        Table table2 = table.where( table.numberColumn( Headers.NKV_EL03_CARBON215_INVCOSTTUD ).isLessThan( 1. ) );
//
//        System.out.println(BvwpUtils.SEPARATOR);
//        System.out.println( table.summarize( Headers.NKV_ORIG, count ).by(Headers.EINSTUFUNG ).print() );
//        System.out.println( System.lineSeparator() + "Davon müssen folgende nachbewertet werden:");
//        System.out.println( table2.summarize( Headers.NKV_ORIG, count ).by(Headers.EINSTUFUNG ) );
//
//        System.out.println(BvwpUtils.SEPARATOR);
//        System.out.println( table.summarize( Headers.INVCOST_ORIG, sum, mean, stdDev, min, max ).by(Headers.EINSTUFUNG ) );
//        System.out.println( System.lineSeparator() + "Davon müssen folgende nachbewertet werden:");
//        System.out.println( table2.summarize( Headers.INVCOST_ORIG, sum, mean, stdDev, min, max ).by(Headers.EINSTUFUNG ) );
//
//        System.out.println(BvwpUtils.SEPARATOR);
//        System.out.println( table.summarize( Headers.CO2_COST_EL03, sum, mean, stdDev, min, max ).by(Headers.EINSTUFUNG ) );
//        System.out.println( System.lineSeparator() + "Davon müssen folgende nachbewertet werden:");
//        System.out.println( table2.summarize( Headers.CO2_COST_EL03, sum, mean, stdDev, min, max ).by(Headers.EINSTUFUNG ) );
    }
    private static Table createVariousNKVs( Table table2 ){
        Table table3 = Table.create( table2.column( PROJECT_NAME )
                        , table2.column( BAUTYP )
                        , table2.column( INVCOST_ORIG )
                        , table2.column( INVCOST_TUD )
                        , table2.column( NKV_ORIG )
                        , table2.column( NKV_INVCOSTTUD )
                        , table2.column( NKV_ELTTIME )
                        , table2.column( NKV_CARBON700 )
                        , table2.column( NKV_ELTTIME_CARBON700_INVCOSTTUD )
                        , table2.column( NKV_ELTTIME_CARBON700_EMOB_INVCOSTTUD )
                        , table2.column( NKV_ELTTIME_CARBON2000_EMOB_INVCOSTTUD )
                        , table2.column( NKV_ELTTIME_CARBON700_EMOB_INVCOSTTUD_10pctLessTraffic )
                        , table2.column( NKV_ELTTIME_CARBON700_EMOB_INVCOSTTUD_20pctLessTraffic )
//                        ,table2.numberColumn( Headers.B_OVERALL )
////                        , table2.numberColumn( Headers.NKV_EL03_CARBON215_INVCOSTTUD )
//                        , table2.numberColumn( Headers.NKV_ELTTIME_CARBON700TPR0_INVCOSTTUD )
                                   );

        for( Column<?> column : table3.columns() ){
            if ( column.name().startsWith( "NKV" ) ) {
                NumberFormat format = NumberFormat.getNumberInstance( Locale.GERMAN );
                format.setMaximumFractionDigits( 1 );
                format.setMinimumFractionDigits( 1 );
                ((DoubleColumn) column).setPrintFormatter( format, "n/a" );
            } else if ( column instanceof NumberColumn ){
                NumberFormat format = NumberFormat.getNumberInstance( Locale.GERMAN );
//                format.setMaximumFractionDigits( 1 );
//                format.setMinimumFractionDigits( 1 );
                ((DoubleColumn) column).setPrintFormatter( format, "n/a" );
            }
        }

        return table3;
    }
    static String createHeader1( String str ) {
        return "<h1>" + str + "</h1>";
    }
    static String createHeader2( String str ) {
        return "<h2>" + str + "</h2>";
    }
    static void addHeaderPlusMultipleFigures( List<Pair<String,Figure>> figuresMap, String str, List<Figure> figuresList ) {
        figuresMap.add( Pair.create( str, figuresList.removeFirst() ) );
        for( Figure figure : figuresList ){
            figuresMap.add( Pair.create( createDefaultKey( figuresMap ), figure ) );
        }
    }
    private static String createDefaultKey( List<Pair<String, Figure>> figures ){
        return "<p>Plot Nr. " + figures.size() + ":</p>";
    }

}
