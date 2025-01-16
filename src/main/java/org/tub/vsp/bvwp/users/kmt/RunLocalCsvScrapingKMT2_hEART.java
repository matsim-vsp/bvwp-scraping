package org.tub.vsp.bvwp.users.kmt;

import static org.tub.vsp.bvwp.data.Headers.*;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Paths;
import java.text.NumberFormat;
import java.util.*;
import org.apache.commons.math3.util.Pair;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.tub.vsp.bvwp.BvwpUtils;
import org.tub.vsp.bvwp.Gbl;
import org.tub.vsp.bvwp.data.HeadersKN;
import org.tub.vsp.bvwp.data.container.analysis.StreetAnalysisDataContainer;
import org.tub.vsp.bvwp.data.type.Einstufung;
import org.tub.vsp.bvwp.io.StreetCsvWriter;
import org.tub.vsp.bvwp.plot.MultiPlotUtils;
import org.tub.vsp.bvwp.scraping.StreetScraper;
import tech.tablesaw.api.*;
import tech.tablesaw.plotly.components.Figure;
import tech.tablesaw.plotly.display.Browser;

public class RunLocalCsvScrapingKMT2_hEART {
    private static final Logger logger = LogManager.getLogger( RunLocalCsvScrapingKMT2_hEART.class );


    /**
     * Ich habe mir diese Methode von KN genommen und stutze die nun zurecht, um mri den ganzen umbau zu sparen.
     * Hier sind es immerhin schon die 213 BAB Projekte und die Plots sind grundsätzlich in der KN-Version da.
     *
     * Werde aber kräftig hier Dinge rauswerfen.
     *
     * Fokus hier ist auf der Analyse für hEART-Paper --> Elastizitäten.
     * @param args
     * @throws IOException
     */
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


        String filePath = "../shared-svn/";
        Map<String, Double> constructionCostsByProject = BvwpUtils.getConstructionCostsFromTudFile(filePath );

        final String regexToMatch = "(A.*)|(B288_A524-G20-NW.html)"; // dies führt, mit prefix="" (!), zu den gleichen 213 BAB Projekten wie bei Richard.
//        final String regexToMatch = "(A...B.*)|(A....B.*)";
        
        StringBuilder strb = new StringBuilder();
        strb.append("A20-G10-SH.html"); // gibt es nochmal mit A20-G10-SH-NI.  Muss man beide zusammenzählen?  kai, feb'24
//                    strb.append("A57-G10-NW.html")) // sehr hohes DTV für 4 Spuren.  ??  kai, mar'24
//                    strb.append("A81-G50-BW.html")) // sehr hohes DTV für 4 Spuren.  ??  kai, mar'24
        strb.append("|A61-G10-RP-T2-RP.html"); // benefits and costs for T1 and T2 are same; there are no revised investment costs from TUD for T2
        strb.append("|A3-G30-HE-T05-HE.html"); // benefits and costs for T04 and T05 are same; there are no revised investment costs from TUD for T05
        strb.append("|A3-G30-HE-T08-HE.html"); // benefits and costs for T06 and T08 are same; there are no revised investment costs from TUD for T08
        strb.append("|A40-G30-NW-T4-NW.html"); // dto
        strb.append("|A003-G061-BY.html"); // dto
        strb.append("|A860_B31-G20-BW-T2-BW.html"); // Exkludiert, da NKA von T1 genutzt UND Teilprojekt selber BStr ist
        strb.append("|A860_B31-G20-BW-T3-BW.html"); // Exkludiert, da NKA von T1 genutzt UND Teilprojekt selber BStr ist
        strb.append("|A860_B31-G20-BW-T4-BW.html"); // Exkludiert, da NKA von T1 genutzt UND Teilprojekt selber BStr ist
        strb.append("|A860_B31-G20-BW-T5-BW.html"); // BStr, da Teilprojekt einzeln bewertet UND NKA für das Teilprojekt vorliegt yyyy müsste man für BStr reinnehmen!
        final String regexToExclude = strb.toString();

        logger.info( "Starting scraping" );
        // yyyy man könnte (sollte?) den table in den StreetAnalysisDataContainer mit hinein geben, und die Werte gleich dort eintragen.  kai, feb'24
        List<StreetAnalysisDataContainer> allStreetBaseData = new StreetScraper()
                                                                              .extractAllLocalBaseData( "./data/street/all", "", regexToMatch, regexToExclude )
                                                                              .stream()
                                                                              .map(streetBaseDataContainer -> new StreetAnalysisDataContainer(
                                                                                              streetBaseDataContainer,
                                                                                              constructionCostsByProject.get(streetBaseDataContainer.getProjectInformation().getProjectNumber())
                                                                              ))
                                                                              .toList();

        logger.info( "Writing csv and generating table:" );
        Table table = new StreetCsvWriter( "output/street_data.csv" ).writeCsv( allStreetBaseData );

        Gbl.assertTrue( table.rowCount()==213, "wrong number of (BAB) projects; should be 213 but is "+table.rowCount() );

        final String PLUS_110_PCT = "plus110pct";
        table.addColumns( table.numberColumn( INVCOST_SUM_ORIG ).multiply( 2.1 ).setName( PLUS_110_PCT ) );

        // ===

        for( Einstufung einstufung : Einstufung.values() ){
            if ( einstufung==Einstufung.KB ) continue;
            Row row = table.appendRow();
            row.setString( PROJECT_NAME, "dummy" );
            row.setString( EINSTUFUNG, einstufung.name() );
        }

        // ===
//        plotsKNsFigures(table, PLUS_110_PCT);

        plotKMTforHeartFigures(table);

    }

    private static void plotKMTforHeartFigures(Table table) throws IOException {
        Figures2KN figures2 = new Figures2KN(table);

        List<Pair<String, List<Figure>>> figures = new ArrayList<>();
        Figure fig;

        figures.add( Pair.create( Figures2KN.createHeader1( "Plots für hEART-Paper 2025" ), null ) );

        //#####
        figures.add( Pair.create( Figures2KN.createHeader1( "Aus Elastizitäten" ), null ) );

        fig = figures2.fzkmEl_vs_fzkm(ADDTL_PKWKM_ORIG, ADDTL_PKWKM_EL06 );
        figures.add( Pair.create( Figures2KN.createHeader2( "Elastizität 0.6"), Collections.singletonList( fig )));

        fig = figures2.fzkmEl_vs_fzkm(ADDTL_PKWKM_ORIG, ADDTL_PKWKM_EL03_HALF);
        figures.add( Pair.create( Figures2KN.createHeader2( "Elastizität 0.3, halbiert"), Collections.singletonList( fig )));

        fig = figures2.fzkmEl_vs_fzkm(ADDTL_PKWKM_ORIG, ADDTL_PKWKM_EL0306_HALF);
        figures.add( Pair.create( Figures2KN.createHeader2( "'reduziert': Elastizität 0.3 oder 0.6 ja nach Bautyp; halbiert"), Collections.singletonList( fig )));


        //####
        figures.add( Pair.create( Figures2KN.createHeader1( "Aus Reisezeitgewinnen" ), null ) );

        fig = figures2.fzkmEl_vs_fzkm(ADDTL_PKWKM_ORIG, ADDTL_PKWKM_FROM_TTIME_29_HALF);
        figures.add( Pair.create( Figures2KN.createHeader2( "zusätzlich aus Fahreitgewinnen, 29km/h, halbiert"), Collections.singletonList( fig )));

        fig = figures2.fzkmEl_vs_fzkm(ADDTL_PKWKM_ORIG, ADDTL_PKWKM_FROM_TTIME_29_HALF_InklBVWP);
        figures.add( Pair.create( Figures2KN.createHeader2( "BVWP plus 0.5 Fahreitgewinnen, 29km/h, "), Collections.singletonList( fig )));


        //####
        figures.add( Pair.create( Figures2KN.createHeader1( "Kombiniert" ), null ) );

        fig = FiguresKMT.createFigureElaChange(table, ADDTL_PKWKM_ORIG, ADDTL_PKWKM_EL06, ADDTL_PKWKM_EL0306_HALF, ADDTL_PKWKM_FROM_TTIME_29_HALF_InklBVWP, "additional mio vkm/a");
        figures.add( Pair.create( Figures2KN.createHeader2( "Kombiniert die verschiedenen Ansätze"), Collections.singletonList( fig )));


        plotFigures("multiplot_hEART.html", figures);


    }



    private static void plotsKNsFigures(Table table, String PLUS_110_PCT) throws IOException {
        final String NKV_ORIG_CAPPED5 = addCap( 5, table, HeadersKN.NKV_ORIG );
        Figures1KN figures1 = new Figures1KN(table, NKV_ORIG_CAPPED5 );
        Figures2KN figures2 = new Figures2KN(table);

        List<Pair<String, List<Figure>>> figures = new ArrayList<>();

        figures.add( Pair.create( Figures2KN.createHeader1( "Abc" ), null ) );
        String str;

        str = HeadersKN.NKV_ORIG;
        figures.add( Pair.create( Figures2KN.createHeader2( str ), Arrays.asList( Figures2KN.barChartFigureAnzahlProjekte(table, str ), Figures2KN.barChartFigureInvKosten(table, str, PLUS_110_PCT) ) ) );

        str = HeadersKN.NKV_CARBON700;
        figures.add( Pair.create( Figures2KN.createHeader2( str ), Arrays.asList( Figures2KN.barChartFigureAnzahlProjekte(table, str ), Figures2KN.barChartFigureInvKosten(table, str, PLUS_110_PCT) ) ) );

        str = HeadersKN.NKV_CARBON700_EMOB;
        figures.add( Pair.create( Figures2KN.createHeader2( str ), null ));
        figures.add( Pair.create( Figures2KN.createHeader2( "" ), Collections.singletonList( Figures2KN.barChartFigureAnzahlProjekte(table, str ) ) ) );
//        figures.add( Pair.create( createHeader2( "" ), barChartFigureInvKosten( table, str, INVCOST_SUM_ORIG ) ) ) ;

        str = HeadersKN.NKV_ELTTIME;
        figures.add( Pair.create( Figures2KN.createHeader2( str ), null ));
        figures.add( Pair.create( Figures2KN.createHeader2( "" ), Collections.singletonList( Figures2KN.barChartFigureAnzahlProjekte(table, str ) ) ) );
//        figures.add( Pair.create( createHeader2( "" ), barChartFigureInvKosten( table, str, INVCOST_SUM_ORIG ) ) ) ;

        str = NKV_INVCOSTTUD;
        figures.add( Pair.create( Figures2KN.createHeader2( str ), null ));
        figures.add( Pair.create( Figures2KN.createHeader2( "" ), Collections.singletonList( Figures2KN.barChartFigureAnzahlProjekte(table, str ) ) ) );
//        figures.add( Pair.create( createHeader2( "" ), barChartFigureInvKosten( table, str, PLUS_110_PCT ) ) ) ;

        // ### combination of ELTTIME and CARBON700:

        str = NKV_ELTTIME_CARBON700;
        figures.add( Pair.create( Figures2KN.createHeader2( str ), Arrays.asList( Figures2KN.barChartFigureAnzahlProjekte(table, str ), Figures2KN.barChartFigureInvKosten(table, str, PLUS_110_PCT) ) ) );


        // ### BMDV:

        str = NKV_INVCOSTTUD_CARBON700_EMOB;
        figures.add( Pair.create( Figures2KN.createHeader2( str ), Arrays.asList( Figures2KN.barChartFigureAnzahlProjekte(table, str ), Figures2KN.barChartFigureInvKosten(table, str, PLUS_110_PCT) ) ) );

        // ### Sensitivities of BMDV:

        // BMDV + mehr induzierter Strassenverkehr:
        str = NKV_ELTTIME_CARBON700_EMOB_INVCOSTTUD;
        figures.add( Pair.create( Figures2KN.createHeader2( str ), null ));
//        figures.add( Pair.create( createHeader2( "" ), barChartFigureAnzahlProjekte( table, str ) ) ) ;
        figures.add( Pair.create( Figures2KN.createHeader2( "" ), Collections.singletonList( Figures2KN.barChartFigureInvKosten(table, str, PLUS_110_PCT) ) ) );

        // BMDV + weniger Verkehrsnachfrage

        // BMDV + CO2Preis2000:

        // BMDV + weniger eMob:

        // BMDV + weniger eMob zusammen mit mehr induziertem Strassenverkehr:


        figures.add(Pair.create( Figures2KN.createHeader1( "New plots" ), null ) );

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

        figures.add( Pair.create( Figures2KN.createHeader2( "CO2 vs Nutzen_pro_CO2" ), Collections.singletonList( figures2.carbonOrig( Integer.MAX_VALUE, NProCo2_ORIG ) ) ) );

//        figures.add( Pair.create( createHeader2( "CO2 vs Nutzen_pro_CO2" ), figures2.carbonOrig( Integer.MAX_VALUE, NProCo2_ELTTIME_CARBON2000ptpr0_EMOB_INVCOSTTUD ) ) );

        figures.add( Pair.create( Figures2KN.createHeader2( "CO2 vs Nutzen_pro_CO2" ), Collections.singletonList( figures2.carbon( Integer.MAX_VALUE, NProCo2_ELTTIME_CARBON2000_EMOB_INVCOSTTUD ) ) ) );

        figures.add( Pair.create( Figures2KN.createHeader2( "CO2 vs Nutzen_pro_CO2" ), Collections.singletonList( figures2.carbonWithEmob( Integer.MAX_VALUE, NProCo2_ELTTIME_CARBON2000_EMOB_INVCOSTTUD ) ) ) );

        figures.add( Pair.create( Figures2KN.createHeader2( "Inv.Kosten vs Nutzen_pro_CO2") , Collections.singletonList( figures2.investmentCost( Integer.MAX_VALUE, NProCo2_ELTTIME_CARBON2000_EMOB_INVCOSTTUD, INVCOST_TUD ) ) ) );


        // Induzierter Strassenmehrverkehr:
        figures.add( Pair.create( Figures2KN.createHeader1( "Induzierter Strassenmehrverkehr (aus Elastizität 0,3):" ), Collections.singletonList( figures2.fzkmEl03_vs_fzkmOrig() ) ) );
//        figures.add( figures2.fzkmFromEl03Delta_vs_fzkmOrig() );
        figures.add( Pair.create( Figures2KN.createHeader1( "Induzierter Strassenmehrverkehr (aus konstantem Reisezeitbudget):" ), Collections.singletonList( figures2.fzkmFromTtime_vs_fzkmOrig() ) ) );
//        figures.add( figures2.fzkmFromTtimeDelta_vs_fzkmOrig() );


        // Abhängigkeit von Verkehrsnachfrage:
        figures.add( Pair.create( Figures2KN.createHeader1( "Abhängigkeit NKV von Verkehrsmenge:" ), Collections.singletonList( figures2.nkv_vs_dtv( HeadersKN.NKV_ORIG ) ) ) );
        figures.add( Pair.create( Figures2KN.createHeader1( "Abhängigkeit NKV von Verkehrsmenge:" ), Collections.singletonList( figures2.nkv_vs_dtv( NKV_ELTTIME_CARBON2000_EMOB_INVCOSTTUD ) ) ) );
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

            figures.add( Pair.create( Figures2KN.createHeader1( "Veränderung NKV durch ..." ), null ) );
            for( Map.Entry<String, String> entry : nkvs.entrySet() ){
                figures.add( Pair.create( Figures2KN.createHeader2( entry.getKey() ), Collections.singletonList( figures2.nkvNew_vs_nkvOrig( cap, entry.getValue() ) ) ) );
            }

            // ---

            figures.add( Pair.create( Figures2KN.createHeader1( "Inv.Kosten vs. NKV mit ... " ), null ) );
            figures.add( Pair.create( Figures2KN.createHeader2( "... originalem NKV:" ), Collections.singletonList( figures2.investmentCost( cap, HeadersKN.NKV_ORIG, INVCOST_BARWERT_ORIG ) ) ) );
            figures.add( Pair.create( Figures2KN.createHeader2( "... originalem NKV:" ), Collections.singletonList( figures2.investmentCost( cap, HeadersKN.NKV_ORIG, INVCOST_TUD ) ) ) );
            for( Map.Entry<String, String> entry : nkvs.entrySet() ){
                figures.add( Pair.create( Figures2KN.createHeader2( entry.getKey() ), Collections.singletonList( figures2.investmentCost( cap, entry.getValue(), INVCOST_TUD ) ) ) );
            }

            // ---

            figures.add( Pair.create( Figures2KN.createHeader1( "CO2 vs. NKV mit ... " ), null ) );
            figures.add( Pair.create( Figures2KN.createHeader2( "... originalem NKV:" ), Collections.singletonList( figures2.carbonWithEmob( cap, HeadersKN.NKV_ORIG ) ) ) );
            for( Map.Entry<String, String> entry : nkvs.entrySet() ){
                figures.add( Pair.create( Figures2KN.createHeader2( entry.getKey() ), Collections.singletonList( figures2.carbonWithEmob( cap, entry.getValue() ) ) ) );
            }
        }

        figures.add( Pair.create( Figures2KN.createHeader2( "cumulative ..." ), Collections.singletonList( figures2.cumBenefitVsCumCost( NKV_ELTTIME_CARBON700_EMOB_INVCOSTTUD ) ) ) );

        figures.add( Pair.create( Figures2KN.createHeader2( "cumulative ..." ), Collections.singletonList( figures2.cumBenefitVsCumCost( NKV_ELTTIME_CARBON2000_EMOB_INVCOSTTUD ) ) ) );

        // ===
        figures.add( Pair.create( Figures2KN.createHeader1( "Further material ..." ), null ) );

        // Änlichkeit zwischen carbon cost und Investitionskosten:
        figures.add( Pair.create( Figures2KN.createHeader1( "Ähnlichkeit CO2-Kosten und Investitionskosten:" ), Collections.singletonList( figures2.carbon_vs_invcostTud() ) ) );

        // ###  nicht verwendet:

        // changes in investment cost:
        figures.add( Pair.create( "", Collections.singletonList( figures2.invcost_tud_vs_orig() ) ) );


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


        plotFigures("multiplot.html", figures);

        // ===
    }

    private static void plotFigures(String outputFileString, List<Pair<String, List<Figure>>> figures) throws IOException {
        File outputFile = Paths.get( outputFileString ).toFile();
        try( FileWriter fileWriter = new FileWriter(outputFile) ){
            fileWriter.write( MultiPlotUtils.createPageV2(figures) );
        }
        new Browser().browse(outputFile);
    }

}
