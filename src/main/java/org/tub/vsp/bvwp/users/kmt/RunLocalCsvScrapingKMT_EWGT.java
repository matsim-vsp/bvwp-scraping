package org.tub.vsp.bvwp.users.kmt;

import java.util.*;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.tub.vsp.bvwp.BvwpUtils;
import org.tub.vsp.bvwp.data.Headers;
import org.tub.vsp.bvwp.data.container.analysis.StreetAnalysisDataContainer;
import org.tub.vsp.bvwp.data.type.Einstufung;
import org.tub.vsp.bvwp.io.StreetCsvWriter;
import org.tub.vsp.bvwp.plot.MultiPlotUtils;
import org.tub.vsp.bvwp.scraping.StreetScraper;
import org.tub.vsp.bvwp.Gbl;
import tech.tablesaw.api.Row;
import tech.tablesaw.api.Table;
import tech.tablesaw.plotly.components.Axis;
import tech.tablesaw.plotly.components.Axis.Type;
import tech.tablesaw.plotly.components.Figure;
import tech.tablesaw.plotly.display.Browser;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Paths;
import java.text.NumberFormat;

import static tech.tablesaw.aggregate.AggregateFunctions.*;

public class RunLocalCsvScrapingKMT_EWGT {
  private static final Logger logger = LogManager.getLogger(RunLocalCsvScrapingKMT_EWGT.class);

  static final int plotWidth = 1400;

  /**
   * Fokus hier ist auf der Analyse für EWGT2024-Paper --> CO2-Preis und Investititonskosten.
   * @param args
   * @throws IOException
   */
  public static void main(String[] args) throws IOException {
    Locale.setDefault(Locale.US);

    logger.warn(
        "(vermutl. weitgehend gelöst) Teilweise werden die Hauptprojekte bewertet und nicht"
            + "Teilprojekte (A20); teilweise werden die Teilprojekte "
            + "bewertet aber nicht das Hauptprojekt (A2).  "
            + "Müssen aufpassen, dass nichts unter den Tisch fällt.");
    logger.warn(
        "Bei https://www.bvwp-projekte.de/strasse/A559-G10-NW/A559-G10-NW.html "
            + "hat evtl. die Veränderung "
            + "Betriebsleistung PV falsches VZ.  Nutzen (positiv) dann wieder richtig.");
    logger.warn(
        "Wieso geht bei https://www.bvwp-projekte.de/strasse/A14-G20-ST-BB/A14-G20-ST-BB.html"
            + " der Nutzen mit impl und co2Price sogar nach oben?");
    logger.warn("===========");

    StreetScraper scraper = new StreetScraper();

    logger.info("Starting scraping");

    String filePath = "../shared-svn/";
    Map<String, Double> constructionCostsByProject = BvwpUtils.getConstructionCostsFromTudFile(filePath);

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


    table.addColumns(
        table
            .numberColumn(Headers.NKV_ORIG_EN)
            .subtract(table.numberColumn(Headers.NKV_EL03_CARBON215_INVCOSTTUD))
            .setName(Headers.NKV_EL03_DIFF)
    );

    { // Plotting and table preparation
      String xNameKMT;
      Axis.AxisBuilder xAxisBuilder = Axis.builder();
      {
        xNameKMT = Headers.CO2_COST_EL03;
        xAxisBuilder.type(Type.LINEAR);
      }

      table = table.sortDescendingOn(xNameKMT);
      Axis xAxis = xAxisBuilder.title(xNameKMT).build();

      kmtPlots_old(xAxis, table, xNameKMT);
      kmtPlots_Co2values(table);
    }

    calculationsAndTableWriting(table);
  }

  private static void calculationsAndTableWriting(Table table) {
    // === Some calculations

    Comparator<Row> comparator =
        (o1, o2) -> {
          Einstufung p1 = Einstufung.valueOf(o1.getString(Headers.EINSTUFUNG));
          Einstufung p2 = Einstufung.valueOf(o2.getString(Headers.EINSTUFUNG));
          return p1.compareTo(p2);
        };

    final Table tbl = table.sortOn(comparator);
    NumberFormat format = NumberFormat.getCompactNumberInstance();
    format.setMaximumFractionDigits(0);
    tbl.numberColumn(Headers.CO2_COST_EL03).setPrintFormatter(format, "n/a");

    // Projekte, die bereits vor Änderung NKV <1 haben
    Table tableBaseKl1 = tbl.where(tbl.numberColumn( Headers.NKV_ORIG_EN ).isLessThan(1. ) );
    Table tableIndCo2kl1 = tbl.where(tbl.numberColumn(Headers.NKV_EL03_CARBON215_INVCOSTTUD).isLessThan(1.));

    { // -- von KN
      System.out.println(BvwpUtils.SEPARATOR_AUSGABE);
      System.out.println("NKV Original auf Gesamttabelle");
      System.out.println(tbl.summarize( Headers.NKV_ORIG_EN, count, mean, stdDev, min, max ).by(Headers.EINSTUFUNG ) );
      System.out.println(tbl.summarize( Headers.NKV_ORIG_EN, count, mean, stdDev, min, max ).apply() );
      System.out.println(System.lineSeparator() + "Davon NKV < 1: nach Modifikation.");
      System.out.println(tableIndCo2kl1.summarize(Headers.NKV_EL03_CARBON215_INVCOSTTUD, count, mean, stdDev, min, max).by(Headers.EINSTUFUNG));
      System.out.println(tableIndCo2kl1.summarize(Headers.NKV_EL03_CARBON215_INVCOSTTUD, count, mean, stdDev, min, max).apply());
      System.out.println(BvwpUtils.SEPARATOR_AUSGABE);
      System.out.println(tbl.summarize(Headers.INVCOST_BARWERT_ORIG, sum, mean, stdDev, min, max ).by(Headers.EINSTUFUNG ) );
      System.out.println(tbl.summarize(Headers.INVCOST_BARWERT_ORIG, sum, mean, stdDev, min, max ).apply() );
      System.out.println(System.lineSeparator() + "Davon NKV < 1:");
      System.out.println(tableIndCo2kl1.summarize(Headers.INVCOST_BARWERT_ORIG, sum, mean, stdDev, min, max ).by(Headers.EINSTUFUNG ) );
      System.out.println(tableIndCo2kl1.summarize(Headers.INVCOST_BARWERT_ORIG, sum, mean, stdDev, min, max ).apply() );
      System.out.println(BvwpUtils.SEPARATOR_AUSGABE);
      System.out.println(tbl.summarize(Headers.CO2_COST_EL03, sum, mean, stdDev, min, max).by(Headers.EINSTUFUNG));
      System.out.println(System.lineSeparator() + "Davon NKV < 1:");
      System.out.println(tableIndCo2kl1.summarize(Headers.CO2_COST_EL03, sum, mean, stdDev, min, max).by(Headers.EINSTUFUNG));
    }


    List<String> headersKMT = new LinkedList<>();
    {
      // KMT
      System.out.println(BvwpUtils.SEPARATOR_AUSGABE);
      System.out.println("### KMT ###");
      System.out.println(BvwpUtils.SEPARATOR_AUSGABE);


      headersKMT.add(Headers.NKV_ORIG_EN);
      headersKMT.add(Headers.NKV_CO2_700_EN);
      headersKMT.add(Headers.NKV_CO2_2000_EN);
      headersKMT.add(Headers.NKV_INVCOSTTUD_EN);
      headersKMT.add(Headers.NKV_INVCOST150_EN);
      headersKMT.add(Headers.NKV_INVCOST200_EN);
      headersKMT.add(Headers.NKV_CO2_700_INVCOSTTUD_EN);
      headersKMT.add(Headers.NKV_CO2_700_INVCOST150_EN);
      headersKMT.add(Headers.NKV_CO2_700_INVCOST200_EN);
      headersKMT.add(Headers.NKV_CO2_2000_INVCOSTTUD_EN);
      headersKMT.add(Headers.NKV_CO2_2000_INVCOST150_EN);
      headersKMT.add(Headers.NKV_CO2_2000_INVCOST200_EN);
      }

    TableCalculationsKMT.printNkvTablesKMT(tbl, headersKMT);
  }

  private static void kmtPlots_old(Axis xAxis, Table table, String xNameKMT)
      throws IOException {
    Figure figureNkv = FiguresKMT.createFigureNkv(xAxis, RunLocalCsvScrapingKMT_EWGT.plotWidth, table, xNameKMT);
    Figure figureCostByPriority = FiguresKMT.createFigureCostByPriority(RunLocalCsvScrapingKMT_EWGT.plotWidth, table, Headers.INVCOST_BARWERT_ORIG );
    Figure figureNkvByPriority = FiguresKMT.createFigureNkvByPriority(xAxis, RunLocalCsvScrapingKMT_EWGT.plotWidth, table, Headers.INVCOST_BARWERT_ORIG );
    Figure figureCO2Benefit = FiguresKMT.createFigureCO2(xAxis, RunLocalCsvScrapingKMT_EWGT.plotWidth, table, xNameKMT);
    Figure figureNkvChangeCo2_680 = FiguresKMT.createFigureNkvChange(RunLocalCsvScrapingKMT_EWGT.plotWidth, table, Headers.NKV_ORIG_EN, Headers.NKV_CO2_700_EN );
    Figure figureNkvChangeInduz_2000 = FiguresKMT.createFigureNkvChange(RunLocalCsvScrapingKMT_EWGT.plotWidth, table, Headers.NKV_ORIG_EN, Headers.NKV_CO2_2000_EN );

    String pageKMT =
        MultiPlotUtils.pageTop()
            + System.lineSeparator()
            + figureNkv.asJavascript("plot1")
            + System.lineSeparator()
            + figureCostByPriority.asJavascript("plot2")
            + System.lineSeparator()
            + figureNkvByPriority.asJavascript("plot3")
            + System.lineSeparator()
            + figureCO2Benefit.asJavascript("plot4")
            + System.lineSeparator()
            + figureNkvChangeCo2_680.asJavascript("plot5")
            + System.lineSeparator()
            + figureNkvChangeInduz_2000.asJavascript("plot6")
            + System.lineSeparator()
            + MultiPlotUtils.pageBottom;

    File outputFileKMT = Paths.get("multiplotKMT.html").toFile();

    try (FileWriter fileWriter = new FileWriter(outputFileKMT)) {
      fileWriter.write(pageKMT);
    }

    new Browser().browse(outputFileKMT);
  }

  private static void kmtPlots_Co2values(Table table) throws IOException {
    Figure figureNkvChangeCo2_700 = FiguresKMT.createFigureNkvChange(RunLocalCsvScrapingKMT_EWGT.plotWidth, table, Headers.NKV_ORIG_EN, Headers.NKV_CO2_700_EN );
    Figure figureNkvChangeInduz_2000 = FiguresKMT.createFigureNkvChange(RunLocalCsvScrapingKMT_EWGT.plotWidth, table, Headers.NKV_ORIG_EN, Headers.NKV_CO2_2000_EN );

    Figure figureNkvChange_InvCostTud = FiguresKMT.createFigureNkvChange(RunLocalCsvScrapingKMT_EWGT.plotWidth, table, Headers.NKV_ORIG_EN, Headers.NKV_INVCOSTTUD_EN );
    Figure figureNkvChange_InvCost150 = FiguresKMT.createFigureNkvChange(RunLocalCsvScrapingKMT_EWGT.plotWidth, table, Headers.NKV_ORIG_EN, Headers.NKV_INVCOST150_EN );
    Figure figureNkvChange_InvCost200 = FiguresKMT.createFigureNkvChange(RunLocalCsvScrapingKMT_EWGT.plotWidth, table, Headers.NKV_ORIG_EN, Headers.NKV_INVCOST200_EN );
    Figure figureNkvChange_Co2_700_InvCostTud = FiguresKMT.createFigureNkvChange(RunLocalCsvScrapingKMT_EWGT.plotWidth, table, Headers.NKV_ORIG_EN, Headers.NKV_CO2_700_INVCOSTTUD_EN );
    Figure figureNkvChange_Co2_700_InvCost150 = FiguresKMT.createFigureNkvChange(RunLocalCsvScrapingKMT_EWGT.plotWidth, table, Headers.NKV_ORIG_EN, Headers.NKV_CO2_700_INVCOST150_EN );
    Figure figureNkvChange_Co2_700_InvCost200 = FiguresKMT.createFigureNkvChange(RunLocalCsvScrapingKMT_EWGT.plotWidth, table, Headers.NKV_ORIG_EN, Headers.NKV_CO2_700_INVCOST200_EN );
    Figure figureNkvChange_Co2_2000_InvCostTud = FiguresKMT.createFigureNkvChange(RunLocalCsvScrapingKMT_EWGT.plotWidth, table, Headers.NKV_ORIG_EN, Headers.NKV_CO2_2000_INVCOSTTUD_EN );
    Figure figureNkvChange_Co2_2000_InvCost150 = FiguresKMT.createFigureNkvChange(RunLocalCsvScrapingKMT_EWGT.plotWidth, table, Headers.NKV_ORIG_EN, Headers.NKV_CO2_2000_INVCOST150_EN );
    Figure figureNkvChange_Co2_2000_InvCost200 = FiguresKMT.createFigureNkvChange(RunLocalCsvScrapingKMT_EWGT.plotWidth, table, Headers.NKV_ORIG_EN, Headers.NKV_CO2_2000_INVCOST200_EN );

    Figure figureNkvChange_InvCost150_200 =
        FiguresKMT.createFigureNkvChange(
                table,
                Headers.NKV_INVCOST150_EN,
            Headers.NKV_INVCOST200_EN, 8);
    Figure figureNkvChange_Co2_700_InvCost150_200 =
        FiguresKMT.createFigureNkvChange(
                table,
                Headers.NKV_CO2_700_INVCOST150_EN,
            Headers.NKV_CO2_700_INVCOST200_EN, 8);
    Figure figureNkvChange_Co2_2000_InvCost150_200 =
        FiguresKMT.createFigureNkvChange(
                table,
                Headers.NKV_CO2_2000_INVCOST150_EN,
            Headers.NKV_CO2_2000_INVCOST200_EN, 8);

    String page =
        MultiPlotUtils.pageTop()
            + System.lineSeparator()
            + figureNkvChangeCo2_700.asJavascript("plot1")
            + System.lineSeparator()
            + figureNkvChangeInduz_2000.asJavascript("plot2")
            + System.lineSeparator()
            + figureNkvChange_InvCostTud.asJavascript("plot3")
            + System.lineSeparator()
            + figureNkvChange_InvCost150.asJavascript("plot4")
            + System.lineSeparator()
            + figureNkvChange_InvCost200.asJavascript("plot5")
            + System.lineSeparator()
            + figureNkvChange_Co2_700_InvCostTud.asJavascript("plot6")
            + System.lineSeparator()
            + figureNkvChange_Co2_700_InvCost150.asJavascript("plot7")
            + System.lineSeparator()
            + figureNkvChange_Co2_700_InvCost200.asJavascript("plot8")
            + System.lineSeparator()
            + figureNkvChange_Co2_2000_InvCostTud.asJavascript("plot9")
            + System.lineSeparator()
            + figureNkvChange_Co2_2000_InvCost150.asJavascript("plot10")
            + System.lineSeparator()
            + figureNkvChange_Co2_2000_InvCost200.asJavascript("plot11")
            + System.lineSeparator()
            +

            // Plot, der beide Inv Kostenveränderungen für alle Projekte enthält
            figureNkvChange_InvCost150_200.asJavascript("plotA")
            + System.lineSeparator()
            + figureNkvChange_Co2_700_InvCost150_200.asJavascript("plotB")
            + System.lineSeparator()
            + figureNkvChange_Co2_2000_InvCost150_200.asJavascript("plotC")
            + System.lineSeparator()
            + MultiPlotUtils.pageBottom;

    File outputFile = Paths.get("EWGT_CO2-Values.html").toFile();

    try (FileWriter fileWriter = new FileWriter(outputFile)) {
      fileWriter.write(page);
    }

    new Browser().browse(outputFile);
  }
}
