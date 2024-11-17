package org.tub.vsp.bvwp.users.kn;

import org.apache.commons.math3.util.Pair;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.tub.vsp.bvwp.BvwpUtils;
import org.tub.vsp.bvwp.data.HeadersKN;
import org.tub.vsp.bvwp.data.container.analysis.StreetAnalysisDataContainer;
import org.tub.vsp.bvwp.data.type.Einstufung;
import org.tub.vsp.bvwp.io.StreetCsvWriter;
import org.tub.vsp.bvwp.plot.MultiPlotUtils;
import org.tub.vsp.bvwp.scraping.StreetScraper;
import tech.tablesaw.aggregate.AggregateFunctions;
import tech.tablesaw.api.DoubleColumn;
import tech.tablesaw.api.NumberColumn;
import tech.tablesaw.api.Row;
import tech.tablesaw.api.Table;
import tech.tablesaw.columns.Column;
import tech.tablesaw.plotly.components.Axis;
import tech.tablesaw.plotly.components.Figure;
import tech.tablesaw.plotly.components.Layout;
import tech.tablesaw.plotly.components.Marker;
import tech.tablesaw.plotly.display.Browser;
import tech.tablesaw.plotly.traces.BarTrace;

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


        String filePath = "../../shared-svn/";
        Map<String, Double> constructionCostsByProject = BvwpUtils.getConstructionCostsFromTudFile(filePath );

        final String regexToExclude = "(A...B.*)|(A....B.*)"; // Bundesstrassen, die von Autobahnen ausgehen.

        logger.info( "Starting scraping" );
        // yyyy man könnte (sollte?) den table in den StreetAnalysisDataContainer mit hinein geben, und die Werte gleich dort eintragen.  kai, feb'24
        List<StreetAnalysisDataContainer> allStreetBaseData = new StreetScraper()
                                                                              .extractAllLocalBaseData( "./data/street/all", "A", ".*", regexToExclude )
                                                                              .stream()
                                                                              .map(streetBaseDataContainer -> new StreetAnalysisDataContainer(
                                                                                              streetBaseDataContainer,
                                                                                              constructionCostsByProject.get(streetBaseDataContainer.getProjectInformation().getProjectNumber())
                                                                              ))
                                                                              .toList();

        logger.info( "Writing csv and generating table:" );
        Table table = new StreetCsvWriter( "output/street_data.csv" ).writeCsv( allStreetBaseData );

        final String PLUS_110_PCT = "plus110pct";
        table.addColumns( table.numberColumn( INVCOST_SUM_ORIG ).multiply( 2.1 ).setName( PLUS_110_PCT ) );

        // ===
        final String NKV_ORIG_CAPPED5 = addCap( 5, table, HeadersKN.NKV_ORIG );
        Figures1KN figures1 = new Figures1KN( table, NKV_ORIG_CAPPED5 );
        Figures2KN figures2 = new Figures2KN( table );

        List<Pair<String,Figure>> figures = new ArrayList<>();

        figures.add( Pair.create( createHeader1( "Abc" ), null ) );
        String str;

        str = HeadersKN.NKV_ORIG;
        figures.add( Pair.create( createHeader2( str ), null ));
        figures.add( Pair.create( createHeader2( "" ), barChartFigureAnzahlProjekte( table, str ) ) ) ;
//        figures.add( Pair.create( createHeader2( "" ), barChartFigureInvKosten( table, str, INVCOST_SUM_ORIG ) ) ) ;
        // (yyyy man müsste eigentlich hier und folgend die haushaltsrelevanten Kosten nehmen!)

        str = HeadersKN.NKV_CARBON700;
        figures.add( Pair.create( createHeader2( str ), null ));
        figures.add( Pair.create( createHeader2( "" ), barChartFigureAnzahlProjekte( table, str ) ) ) ;
//        figures.add( Pair.create( createHeader2( "" ), barChartFigureInvKosten( table, str, INVCOST_SUM_ORIG ) ) ) ;

        str = HeadersKN.NKV_CARBON700_EMOB;
        figures.add( Pair.create( createHeader2( str ), null ));
        figures.add( Pair.create( createHeader2( "" ), barChartFigureAnzahlProjekte( table, str ) ) ) ;
//        figures.add( Pair.create( createHeader2( "" ), barChartFigureInvKosten( table, str, INVCOST_SUM_ORIG ) ) ) ;

        str = HeadersKN.NKV_ELTTIME;
        figures.add( Pair.create( createHeader2( str ), null ));
        figures.add( Pair.create( createHeader2( "" ), barChartFigureAnzahlProjekte( table, str ) ) ) ;
//        figures.add( Pair.create( createHeader2( "" ), barChartFigureInvKosten( table, str, INVCOST_SUM_ORIG ) ) ) ;

        str = NKV_INVCOSTTUD;
        figures.add( Pair.create( createHeader2( str ), null ));
        figures.add( Pair.create( createHeader2( "" ), barChartFigureAnzahlProjekte( table, str ) ) ) ;
//        figures.add( Pair.create( createHeader2( "" ), barChartFigureInvKosten( table, str, PLUS_110_PCT ) ) ) ;

        // ### combination of ELTTIME and CARBON700:

        str = NKV_ELTTIME_CARBON700;
        figures.add( Pair.create( createHeader2( str ), null ));
//        figures.add( Pair.create( createHeader2( "" ), barChartFigureAnzahlProjekte( table, str ) ) ) ;
        figures.add( Pair.create( createHeader2( "" ), barChartFigureInvKosten( table, str, INVCOST_SUM_ORIG ) ) ) ;


        // ### BMDV:

        str = NKV_INVCOSTTUD_CARBON700_EMOB;
        figures.add( Pair.create( createHeader2( str ), null ));
        figures.add( Pair.create( createHeader2( "" ), barChartFigureAnzahlProjekte( table, str ) ) ) ;
        figures.add( Pair.create( createHeader2( "" ), barChartFigureInvKosten( table, str, PLUS_110_PCT ) ) ) ;

        // ### Sensitivities of BMDV:

        // BMDV + mehr induzierter Strassenverkehr:
        str = NKV_ELTTIME_CARBON700_EMOB_INVCOSTTUD;
        figures.add( Pair.create( createHeader2( str ), null ));
//        figures.add( Pair.create( createHeader2( "" ), barChartFigureAnzahlProjekte( table, str ) ) ) ;
        figures.add( Pair.create( createHeader2( "" ), barChartFigureInvKosten( table, str, PLUS_110_PCT ) ) ) ;

        // BMDV + weniger Verkehrsnachfrage
        str = NKV_ELTTIME_CARBON700_EMOB_INVCOSTTUD;
        figures.add( Pair.create( createHeader2( str ), null ));
//        figures.add( Pair.create( createHeader2( "" ), barChartFigureAnzahlProjekte( table, str ) ) ) ;
        figures.add( Pair.create( createHeader2( "" ), barChartFigureInvKosten( table, str, PLUS_110_PCT ) ) ) ;

        // BMDV + CO2Preis2000:

        // BMDV + weniger eMob:

        // BMDV + weniger eMob zusammen mit mehr induziertem Strassenverkehr:







        figures.add(Pair.create( createHeader1( "New plots" ), null ) );

        // N pro CO2:
        table.addColumns( table.doubleColumn( HeadersKN.NKV_ORIG )
                               .multiply( table.doubleColumn( INVCOST_BARWERT_ORIG ) )
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

        figures.add( Pair.create( createHeader2( "Inv.Kosten vs Nutzen_pro_CO2") , figures2.investmentCost( Integer.MAX_VALUE, NProCo2_ELTTIME_CARBON2000_EMOB_INVCOSTTUD, INVCOST_TUD ) ) );


        // Induzierter Strassenmehrverkehr:
        figures.add( Pair.create( createHeader1( "Induzierter Strassenmehrverkehr (aus Elastizität 0,3):" ), figures2.fzkmEl03_vs_fzkmOrig() ) );
//        figures.add( figures2.fzkmFromEl03Delta_vs_fzkmOrig() );
        figures.add( Pair.create( createHeader1( "Induzierter Strassenmehrverkehr (aus konstantem Reisezeitbudget):" ), figures2.fzkmFromTtime_vs_fzkmOrig() ) );
//        figures.add( figures2.fzkmFromTtimeDelta_vs_fzkmOrig() );


        // Abhängigkeit von Verkehrsnachfrage:
        figures.add( Pair.create( createHeader1( "Abhängigkeit NKV von Verkehrsmenge:" ), figures2.nkv_vs_dtv( HeadersKN.NKV_ORIG ) ) );
        figures.add( Pair.create( createHeader1( "Abhängigkeit NKV von Verkehrsmenge:" ), figures2.nkv_vs_dtv( NKV_ELTTIME_CARBON2000_EMOB_INVCOSTTUD ) ) );
//        figures.add( figures2.nkvNeu_vs_dtv( NKV_ELTTIME_CARBON700TPR0_INVCOSTTUD ) );

        // ===
        // ===
        final int cap = 5;
        {
            Map<String, String> nkvs = new LinkedHashMap<>();
            {
                nkvs.put( "... Investitionskosten+:", NKV_INVCOSTTUD );
                nkvs.put( "... CO2-Kosten+:", HeadersKN.NKV_CARBON700 );
                nkvs.put( "... CO2-Kosten+ &  eMob+:", HeadersKN.NKV_CARBON700_EMOB );
                nkvs.put( "... CO2-Kosten+ & eMob+ & Inv.Kosten+:", NKV_ELTTIME_CARBON700_EMOB_INVCOSTTUD );
                // ---
                nkvs.put( "... induz. Strassenmehrverkehr+:", HeadersKN.NKV_ELTTIME );
//                nkvs.put( "... Kombination induz. Strassenmehrverkehr + erh. CO2-Kosten:", NKV_ELTTIME_CARBON700 );
                nkvs.put( "... induz. Str.mehrverkehr+, CO2-Preis+, E-Mob+:", NKV_ELTTIME_CARBON700_EMOB );
                nkvs.put( "... induz. Str.mehrverkehr+, CO2-Preis+, E-Mob+, Inv.Kosten+:", NKV_ELTTIME_CARBON700_EMOB_INVCOSTTUD );
//                nkvs.put( "... zusätzlich veränderte Investitionskosten:", NKV_ELTTIME_CARBON700_INVCOSTTUD );
//                nkvs.put( "... zusätzlich CO2-Preis jetzt auf 2000:", NKV_ELTTIME_CARBON2000_EMOB_INVCOSTTUD );
            }

            // ---

            figures.add( Pair.create( createHeader1( "Veränderung NKV durch ..." ), null ) );
            for( Map.Entry<String, String> entry : nkvs.entrySet() ){
                figures.add( Pair.create( createHeader2( entry.getKey() ), figures2.nkvNew_vs_nkvOrig( cap, entry.getValue() ) ) );
            }

            // ---

            figures.add( Pair.create( createHeader1( "Inv.Kosten vs. NKV mit ... " ), null ) );
            figures.add( Pair.create( createHeader2( "... originalem NKV:" ), figures2.investmentCost( cap, HeadersKN.NKV_ORIG, INVCOST_BARWERT_ORIG ) ) );
            figures.add( Pair.create( createHeader2( "... originalem NKV:" ), figures2.investmentCost( cap, HeadersKN.NKV_ORIG, INVCOST_TUD ) ) );
            for( Map.Entry<String, String> entry : nkvs.entrySet() ){
                figures.add( Pair.create( createHeader2( entry.getKey() ), figures2.investmentCost( cap, entry.getValue(), INVCOST_TUD ) ) );
            }

            // ---

            figures.add( Pair.create( createHeader1( "CO2 vs. NKV mit ... " ), null ) );
            figures.add( Pair.create( createHeader2( "... originalem NKV:" ), figures2.carbonWithEmob( cap, HeadersKN.NKV_ORIG ) ) );
            for( Map.Entry<String, String> entry : nkvs.entrySet() ){
                figures.add( Pair.create( createHeader2( entry.getKey() ), figures2.carbonWithEmob( cap, entry.getValue() ) ) );
            }
        }

        figures.add( Pair.create( createHeader2( "cumulative ..." ), figures2.cumBenefitVsCumCost( NKV_ELTTIME_CARBON700_EMOB_INVCOSTTUD ) ) );

        figures.add( Pair.create( createHeader2( "cumulative ..." ), figures2.cumBenefitVsCumCost( NKV_ELTTIME_CARBON2000_EMOB_INVCOSTTUD ) ) );

        // ===
        figures.add( Pair.create( createHeader1( "Further material ..." ), null ) );

        // Änlichkeit zwischen carbon cost und Investitionskosten:
        figures.add( Pair.create( createHeader1( "Ähnlichkeit CO2-Kosten und Investitionskosten:" ), figures2.carbon_vs_invcostTud() ) );

        // ###  nicht verwendet:

        // changes in investment cost:
        figures.add( Pair.create( createDefaultKey( figures ), figures2.invcost_tud_vs_orig() ) );


        // ===
        // ===

        // an example of how to format a column:
//        {
//            NumberFormat format1 = NumberFormat.getCompactNumberInstance();
//        format1.setMaximumFractionDigits( 5 );
//        format1.setMinimumFractionDigits( 5 );
//            table.numberColumn( Headers.CO2_COST_EL03 ).setPrintFormatter( format0, "n/a" );
//        }

        {
            Comparator<Row> einstufComparator = Comparator.comparing( o -> Einstufung.valueOf( o.getString( EINSTUFUNG ) ) );

//            System.out.println( BvwpUtils.SEPARATOR_AT_START );
//            System.out.println( table.summarize( Headers.NKV_ORIG, count ).apply() );
//            System.out.println( BvwpUtils.SEPARATOR_AT_START );

//            {
//                System.out.println( BvwpUtils.SEPARATOR );
//
//                final String name = NKV_ORIG;
//                Table table2 = table.where( table.numberColumn( name ).isLessThan( 1. ) );
//
//                System.out.println( System.lineSeparator() + "Bei Verwendung von " + name + " müssen folgende nachbewertet werden:" );
//                System.out.println( table2.summarize( Headers.NKV_ORIG, count ).apply() );
//
//                System.out.println( BvwpUtils.SEPARATOR );
//            }
//            {
//                System.out.println( BvwpUtils.SEPARATOR );
//
//                final String name = NKV_INVCOST38;
//                Table table2 = table.where( table.numberColumn( name ).isLessThan( 1. ) );
//
//                System.out.println( System.lineSeparator() + "Bei Verwendung von " + name + " müssen folgende nachbewertet werden:" );
//                System.out.println( table2.summarize( Headers.NKV_ORIG, count ).apply() );
//
//                System.out.println( BvwpUtils.SEPARATOR );
//            }
//            {
//                System.out.println( BvwpUtils.SEPARATOR );
//
//                final String name = NKV_INVCOST82;
//                Table table2 = table.where( table.numberColumn( name ).isLessThan( 1. ) );
//
//                System.out.println( System.lineSeparator() + "Bei Verwendung von " + name + " müssen folgende nachbewertet werden:" );
//                System.out.println( table2.summarize( Headers.NKV_ORIG, count ).apply() );
//
//                System.out.println( table2.where( table2.stringColumn( EINSTUFUNG ).isEqualTo( Einstufung.VBE.name() ) ) );
//
//                System.out.println( BvwpUtils.SEPARATOR );
//            }
            {
                final String name = HeadersKN.NKV_CARBON700 ;
                Table table2 = table.where( table.numberColumn( name ).isLessThan( 1. ) );

//                System.out.println( BvwpUtils.SEPARATOR_AT_START );
//                System.out.println( System.lineSeparator() + "Bei Verwendung von " + name + " müssen folgende nachbewertet werden:" );
//                System.out.println( table2.summarize( Headers.NKV_ORIG, count ).apply() );
//                System.out.println( BvwpUtils.SEPARATOR_AT_START );
            }
            {
                NumberFormat format1 = NumberFormat.getNumberInstance( Locale.GERMAN );
                format1.setMaximumFractionDigits( 1 );
                format1.setMinimumFractionDigits( 1 );

                final DoubleColumn multiplied = table.numberColumn( B_CO2_ORIG ).multiply( 4.49 );
                multiplied.setPrintFormatter( format1,"n/a" );
                final DoubleColumn bCo2Revised = table.numberColumn( B_OVERALL_ORIG ).add( table.numberColumn( B_CO2_ORIG ).multiply( 4.49 ) ).setName( "b_overall_revised" );
                bCo2Revised.setPrintFormatter( format1,"n/a" );
                Table table2 = Table.create( table.stringColumn( LINK )
                                , table.numberColumn( B_OVERALL_ORIG )
                                , table.numberColumn( B_CO2_ORIG )
                                , multiplied
                                , bCo2Revised
                                , table.numberColumn( INVCOST_BARWERT_ORIG )
                                , table.numberColumn( HeadersKN.NKV_ORIG )
                                , table.numberColumn( HeadersKN.NKV_CARBON700 )
                                , bCo2Revised.divide( table.numberColumn( INVCOST_BARWERT_ORIG ) ).setName( "NKV revised" )
                                           );
                Table table3 = table2.sortAscendingOn( "NKV revised" );
//                logger.info( "\n" + table3.print(55*2) );
            }

//            System.exit(-1);
            {
                final String name = HeadersKN.NKV_CARBON700_EMOB ;
                Table table2 = table.where( table.numberColumn( name ).isLessThan( 1. ) );

//                System.out.println( BvwpUtils.SEPARATOR_AT_START );
//                System.out.println( System.lineSeparator() + "Bei Verwendung von " + name + " müssen folgende nachbewertet werden:" );
//                System.out.println( table2.summarize( Headers.NKV_ORIG, count ).apply() );
//                System.out.println( BvwpUtils.SEPARATOR_AT_START );
            }


//            System.out.println( BvwpUtils.SEPARATOR_AT_START );
//            System.out.println( BvwpUtils.SEPARATOR_AT_START );
//            System.out.println( table.summarize( Headers.NKV_ORIG, count ).by( EINSTUFUNG ).sortOn( einstufComparator ) );
//            System.out.println( BvwpUtils.SEPARATOR_AT_START );
            {
//                System.out.println( BvwpUtils.SEPARATOR_AT_START );

                final String name = NKV_CARBON700_EMOB_INVCOST80;
                Table table2 = table.where( table.numberColumn( name ).isLessThan( 1. ) );

//                System.out.println( System.lineSeparator() + "Bei Verwendung von " + name + " müssen folgende nachbewertet werden:" );
//                System.out.println( table2.summarize( Headers.NKV_ORIG, count ).by( EINSTUFUNG ).sortOn( einstufComparator ) );
//
//                System.out.println( BvwpUtils.SEPARATOR_AT_END );
            }
            {
//                System.out.println( BvwpUtils.SEPARATOR_AT_START );

                Table table2 = table.where( table.stringColumn( PROJECT_NAME ).containsString( "A20" ) ) ;
//                System.out.println( table2.print() );

//                System.out.println( BvwpUtils.SEPARATOR_AT_END );
            }

            // falls wir per Kategorien sortieren wollen:
//            System.out.println( table2.summarize( Headers.NKV_ORIG, count ).by( Headers.EINSTUFUNG ).sortOn( einstufComparator ).print() );


//            System.out.println( BvwpUtils.SEPARATOR );
//            System.out.println( table.summarize( Headers.INVCOST_ORIG, sum, mean, stdDev, min, max ).by( Headers.EINSTUFUNG ) );
//            System.out.println( System.lineSeparator() + "Davon müssen folgende nachbewertet werden:" );
//            System.out.println( table2.summarize( Headers.INVCOST_ORIG, sum, mean, stdDev, min, max ).by( Headers.EINSTUFUNG ) );

//        System.out.println(BvwpUtils.SEPARATOR);
//        System.out.println( table.summarize( Headers.CO2_COST_EL03, sum, mean, stdDev, min, max ).by(Headers.EINSTUFUNG ) );
//        System.out.println( System.lineSeparator() + "Davon müssen folgende nachbewertet werden:");
//        System.out.println( table2.summarize( Headers.CO2_COST_EL03, sum, mean, stdDev, min, max ).by(Headers.EINSTUFUNG ) );
        }
        // ===



//        Table table2 = table.where( table.stringColumn( PROJECT_NAME ).startsWith( "A20-" )
//                             .or( table.stringColumn( PROJECT_NAME ).startsWith( "A008-G010" ) )
//                             .or( table.stringColumn( PROJECT_NAME ).startsWith( "A39-" ) )
//                                  );
//
//        Table table3 = table.where( table.stringColumn( PROJECT_NAME ).startsWith( "A14-" )
//                                  );
//
//        Table table4 = table.where( table.stringColumn( PROJECT_NAME ).startsWith( "A59-G80" )
////                             .or( table.stringColumn( PROJECT_NAME ).startsWith( "A661-G30" ) )
//                                         .or( table.stringColumn( PROJECT_NAME ).startsWith( "A099-G030" ) )
////                                         .or( table.stringColumn( PROJECT_NAME ).startsWith( "A5-G20" ) )
////                                         .or( table.stringColumn( PROJECT_NAME ).startsWith( "A3-G70" ) )
//                                         .or( table.stringColumn( PROJECT_NAME ).startsWith( "A8-G40" ) )
//                                         .or( table.stringColumn( PROJECT_NAME ).startsWith( "A67-G10" ) )
//                                  );
//
//        Table combined = table2.append( table3 ).append( table4 );
//
//        final Table table2b = createVariousNKVs( table2 );
//        final Table table3b = createVariousNKVs( table3 );
//        final Table table4b = createVariousNKVs( table4 );
//        final Table combinedB = createVariousNKVs( combined );
//
//        new CsvWriter().write( combinedB, CsvWriteOptions.builder( "/dev/stdout" ).separator( ';' ).usePrintFormatters( true ).build() );
//
//        new CsvWriter().write( combinedB, CsvWriteOptions.builder( "table.csv" ).separator( ';' ).usePrintFormatters( true ).build() );
//
//        final Table all = createVariousNKVs( table );
//        new CsvWriter().write( all, CsvWriteOptions.builder( "all.csv" ).separator( ';' ).usePrintFormatters( true ).build() );
//
//        System.exit(-1);
//
//        System.out.println();
//
//        System.out.println( table2b );
//        System.out.println();
//        System.out.println( table3b );
//        System.out.println();
//        System.out.println( table4b );


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


        // ===
        // ===
        // (Plotting is at the end so I can use System.exit(...) to stop earlier.)

        {
            File outputFile = Paths.get( "multiplot.html" ).toFile();
            try( FileWriter fileWriter = new FileWriter( outputFile ) ){
                fileWriter.write( MultiPlotUtils.createPageV2( figures ) );
            }
            new Browser().browse( outputFile );
        }

        // ===


    }
    private static Figure barChartFigureInvKosten( Table table, String whichNkv, String welcheHhrelKosten ){

//        // figure out which investment costs are used within the NKV:
//        InvKosten whichInvCost = null;
//        for( InvKosten value : InvKosten.values() ){
//            if ( whichNkv.contains( value.name() ) ) {
//                whichInvCost = value;
//            }
//        }
//        if ( whichNkv.equals( HeadersKN.NKV_ORIG ) ) {
//            whichInvCost = InvKosten.InvestitionsKostenOrig;
//        }
//        if( whichNkv.contains( "Inv.kosten+" ) ) {
//            whichInvCost = InvKosten.InvestitionsKostenPlus83pct;
//        }
//        if ( whichInvCost==null ) {
//            logger.info( "Type of investment cost not in key; use orig. Barwert and hoping for the best; whichInvCost=" + whichInvCost + "; whichNkv=" + whichNkv );
//            whichInvCost = InvKosten.InvestitionsKostenOrig;
//        }
//
//        DoubleColumn investmentCostColumn;
//        switch( whichInvCost ) {
//            case InvestitionsKostenOrig -> {
//                investmentCostColumn = table.doubleColumn( INVCOST_SUM_ORIG ); // nominelle Baukosten zum Zeitpunkt BVWP
//            }
//            case InvestitionsKostenPlus83pct -> {
//                investmentCostColumn = table.doubleColumn( INVCOST_SUM_ORIG ).multiply( 2.11 );  // Baukostensteigerung in nominal terms!
//            }
//            default -> throw new IllegalStateException( "Unexpected value: " + whichInvCost );
//        }
//
//
        Table table2 = Table.create( table.column( PROJECT_NAME ), table.column( whichNkv ), table.column( welcheHhrelKosten ), table.column( EINSTUFUNG ), table.column(EINSTUFUNG_AS_NUMBER) )
                            .sortDescendingOn( EINSTUFUNG_AS_NUMBER );
        //noinspection ReassignedVariable
        table = null;

        String whichInvCostMrd = welcheHhrelKosten + " [Mrd Eu]";
        table2.addColumns( table2.numberColumn( welcheHhrelKosten ).divide( 1000 ).setName( whichInvCostMrd ) );

        Axis yAxis = Axis.builder().title( whichInvCostMrd ).build();
        Layout layout = Layout.builder().barMode( Layout.BarMode.STACK ).yAxis( yAxis ).title( whichNkv ).build();

        List<BarTrace> traces = new ArrayList<>();
        {
            final Table tTmp = table2.where( table2.numberColumn( whichNkv ).isLessThan( 0.95 ) );
            System.out.println( tTmp.print(55));
            Table t3 = tTmp
                             .summarize( whichInvCostMrd, AggregateFunctions.sum ).by( EINSTUFUNG );

            traces.add( generateBarTrace( t3, "Sum [", whichInvCostMrd, "NKV<0.95", "red" ) );
        }
        {
            Table t3 = table2.where( table2.numberColumn( whichNkv ).isBetweenInclusive( 0.95, 2.-Double.MIN_VALUE ) )
                             .summarize( whichInvCostMrd, AggregateFunctions.sum ).by( EINSTUFUNG );

            traces.add( generateBarTrace( t3, "Sum [", whichInvCostMrd, "0.95<=NKV<2", "orange" ) );
        }
        {
            Table t3 = table2.where( table2.numberColumn( whichNkv ).isBetweenInclusive( 2., 3.-Double.MIN_VALUE ) )
                             .summarize( whichInvCostMrd, AggregateFunctions.sum ).by( EINSTUFUNG );

            traces.add( generateBarTrace( t3, "Sum [", whichInvCostMrd, "2<=NKV<3", "yellow" ) );
        }
        {
            Table t3 = table2.where( table2.numberColumn( whichNkv ).isBetweenInclusive( 3., 4.-Double.MIN_VALUE ) )
                             .summarize( whichInvCostMrd, AggregateFunctions.sum ).by( EINSTUFUNG );

            traces.add( generateBarTrace( t3, "Sum [", whichInvCostMrd, "3<=NKV<4", "AAAAFF" ) );
        }
        {
            Table t3 = table2.where( table2.numberColumn( whichNkv ).isBetweenInclusive( 4., 5.-Double.MIN_VALUE ) )
                             .summarize( whichInvCostMrd, AggregateFunctions.sum ).by( EINSTUFUNG );

            traces.add( generateBarTrace( t3, "Sum [", whichInvCostMrd, "4<=NKV<5", "8888FF" ) );
        }
        {
            Table t3 = table2.where( table2.numberColumn( whichNkv ).isGreaterThanOrEqualTo(5. ) )
                             .summarize( whichInvCostMrd, AggregateFunctions.sum ).by( EINSTUFUNG );

            traces.add( generateBarTrace( t3, "Sum [", whichInvCostMrd, "5<=NKV", "6666FF" ) );
        }

        final Figure figure = new Figure( layout, traces.toArray( new BarTrace[0] ) );
        return figure;
    }
    private static BarTrace generateBarTrace( Table t3, String x, String whichColumn, String name, String color ){
        return BarTrace.builder( t3.categoricalColumn( EINSTUFUNG ), t3.numberColumn( x + whichColumn + "]" ) )
                       .orientation( BarTrace.Orientation.VERTICAL )
                       .name( name )
                       .marker( Marker.builder().color( color ).build() )
                       .build();
    }
    private static Figure barChartFigureAnzahlProjekte( Table table, String whichNkv ){
        Table table2 = Table.create( table.column( PROJECT_NAME ), table.column( whichNkv ), table.column( EINSTUFUNG ), table.column(EINSTUFUNG_AS_NUMBER) )
                            .sortDescendingOn( EINSTUFUNG_AS_NUMBER );
        System.out.println( table2.print() );
        table = null;

        Axis yAxis = Axis.builder().title( "Anzahl Projekte" ).build();
        Layout layout = Layout.builder().barMode( Layout.BarMode.RELATIVE ).yAxis( yAxis ).title( whichNkv ).build();

        List<BarTrace> traces = new ArrayList<>();
        {
            Table t3 = table2.where( table.numberColumn( whichNkv ).isLessThan( 0.95 ) )
                             .summarize( whichNkv, AggregateFunctions.count ).by( EINSTUFUNG );

            traces.add( generateBarTrace( t3, "Count [", whichNkv, "NKV<0.95", "red" ) );
        }
        {
            Table t3 = table2.where( table2.numberColumn( whichNkv ).isBetweenInclusive( 1., 2.-Double.MIN_VALUE ) )
                             .summarize( whichNkv, AggregateFunctions.count ).by( EINSTUFUNG );

            traces.add( generateBarTrace( t3, "Count [", whichNkv, "0.95<=NKV<2", "orange" ) );
        }
        {
            Table t3 = table2.where( table2.numberColumn( whichNkv ).isBetweenInclusive( 2., 3.-Double.MIN_VALUE ) )
                             .summarize( whichNkv, AggregateFunctions.count ).by( EINSTUFUNG );

            traces.add( generateBarTrace( t3, "Count [", whichNkv, "2<=NKV<3", "yellow" ) );
        }
        {
            Table t3 = table2.where( table2.numberColumn( whichNkv ).isBetweenInclusive( 3., 4.-Double.MIN_VALUE ) )
                             .summarize( whichNkv, AggregateFunctions.count ).by( EINSTUFUNG );

            traces.add( generateBarTrace( t3, "Count [", whichNkv, "3<=NKV<4", "AAAAFF" ) );
        }
        {
            Table t3 = table2.where( table2.numberColumn( whichNkv ).isBetweenInclusive( 4., 5.-Double.MIN_VALUE ) )
                             .summarize( whichNkv, AggregateFunctions.count ).by( EINSTUFUNG );

            traces.add( generateBarTrace( t3, "Count [", whichNkv, "4<=NKV<5", "8888FF" ) );
        }
        {
            Table t3 = table2.where( table2.numberColumn( whichNkv ).isGreaterThanOrEqualTo(5. ) )
                             .summarize( whichNkv, AggregateFunctions.count ).by( EINSTUFUNG );

            traces.add( generateBarTrace( t3, "Count [", whichNkv, "5<=NKV", "6666FF" ) );
        }

        final Figure figure = new Figure( layout, traces.toArray( new BarTrace[0] ) );
        return figure;
    }
    private static Table createVariousNKVs( Table table2 ){
        Table table3 = Table.create( table2.column( PROJECT_NAME )
                        , table2.column( BAUTYP )
                        , table2.column( INVCOST_BARWERT_ORIG )
                        , table2.column( INVCOST_TUD )
                        , table2.column( HeadersKN.NKV_ORIG )
                        , table2.column( NKV_INVCOSTTUD )
                        , table2.column( HeadersKN.NKV_ELTTIME )
                        , table2.column( HeadersKN.NKV_CARBON700 )
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
