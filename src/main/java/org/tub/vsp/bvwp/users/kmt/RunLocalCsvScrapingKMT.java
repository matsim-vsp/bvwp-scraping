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
import tech.tablesaw.api.DoubleColumn;
import tech.tablesaw.api.Row;
import tech.tablesaw.api.Table;
import tech.tablesaw.io.csv.CsvWriteOptions;
import tech.tablesaw.io.csv.CsvWriter;
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

public class RunLocalCsvScrapingKMT {
  private static final Logger logger = LogManager.getLogger(RunLocalCsvScrapingKMT.class);

  static final int plotWidth = 1400;


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

    {
      // KMT
      System.out.println(BvwpUtils.SEPARATOR_AUSGABE);
      System.out.println("### KMT ###");
      System.out.println(BvwpUtils.SEPARATOR_AUSGABE);

      List<String> headersKMT = new LinkedList<>();
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

      // General Todos for all three analysis:
      // Todo: Zeile hinzufügen zu eingsparten Projektanzahl. --> alles in einer Tabelle
      // zusammenfassen??
      // Todo: ! Zeile einfügen, welceh die relative Abweichung zum Base-Case anzeigt... (x%
      // Projekte / Costen sind "raus")

      // Todo: Eigentlich müsste man aus dem folgenden eine Funktion machen können, mit wenigen
      // übergabe-Infos. Weil inhaltlich ist es 3x das gleiche.
      { // Projektanzahl
        Table nkvBelow1_count = Table.create("Nu of Projects with BCR < 1 ");
        nkvBelow1_count.addColumns(DoubleColumn.create("# of all Projects", new double[] {tbl.rowCount()}));

        // Erstelle eine Spalte für jeden "Fall"
        for (String s : headersKMT) {
          Table tblBelow1 = tbl.where(tbl.numberColumn(s).isLessThan(1.));
          nkvBelow1_count.addColumns(DoubleColumn.create(s, new double[] {tblBelow1.rowCount()}));
        }
        System.out.println(nkvBelow1_count.print());

        var options =
            CsvWriteOptions.builder("output/NKV_below_1_projects.csv").separator(';').build();
        new CsvWriter().write(nkvBelow1_count, options);
      }

      System.out.println(BvwpUtils.SEPARATOR_AUSGABE);

      { // Gesparte Projektlänge - km
        Table nkvBelow1_length =
                Table.create("Projects with BCR < 1 -- saved project length (km)");
        nkvBelow1_length.addColumns(
                DoubleColumn.create(
                        "project length of all projects (km)",
                        (double) tbl.summarize(Headers.LENGTH, sum).apply().get(0, 0)));

        // Erstelle eine Spalte für jeden "Fall"
        for (String s : headersKMT) {
          Table tblBelow1 = tbl.where(tbl.numberColumn(s).isLessThan(1.));
          nkvBelow1_length.addColumns(
                  DoubleColumn.create(s, (double) tblBelow1.summarize(Headers.LENGTH, sum).apply().get(0, 0)));
        }
        System.out.println(nkvBelow1_length.print());

        var options = CsvWriteOptions.builder("output/NKV_below_1_projectLengthSaved.csv").separator(';').build();
        new CsvWriter().write(nkvBelow1_length, options);
      }

      System.out.println(BvwpUtils.SEPARATOR_AUSGABE);

      { // Gesparte (zusätzliche( Fahrsteifenlänge - km
        Table nkvBelow1_length =
                Table.create("Projects with BCR < 1 -- saved add. lane length (km) -- estimated from project length");
        nkvBelow1_length.addColumns(
                DoubleColumn.create(
                        "add. lane length of all projects (km)",
                        (double) tbl.summarize(Headers.ADDTL_LANE_KM, sum).apply().get(0, 0)));

        // Erstelle eine Spalte für jeden "Fall"
        for (String s : headersKMT) {
          Table tblBelow1 = tbl.where(tbl.numberColumn(s).isLessThan(1.));
          nkvBelow1_length.addColumns(
                  DoubleColumn.create(s, (double) tblBelow1.summarize(Headers.ADDTL_LANE_KM, sum).apply().get(0, 0)));
        }
        System.out.println(nkvBelow1_length.print());

        var options = CsvWriteOptions.builder("output/NKV_below_1_addLaneLengthSaved.csv").separator(';').build();
        new CsvWriter().write(nkvBelow1_length, options);
      }

      System.out.println(BvwpUtils.SEPARATOR_AUSGABE);

      { // Gesparte zusätzliche vkm PERSONENverkehr
        Table nkvBelow1_PkwKm =
                Table.create("Projects with BCR < 1 -- saved add. vkm PERSONENverkehr (Mio PKW-km/a)");
        nkvBelow1_PkwKm.addColumns(
                DoubleColumn.create(
                        "add. PKW-km/a of all projects",
                        (double) tbl.summarize(Headers.ADDTL_PKWKM_ORIG, sum).apply().get(0, 0)));

        // Erstelle eine Spalte für jeden "Fall"
        for (String s : headersKMT) {
          Table tblBelow1 = tbl.where(tbl.numberColumn(s).isLessThan(1.));
          nkvBelow1_PkwKm.addColumns(
                  DoubleColumn.create(s, (double) tblBelow1.summarize(Headers.ADDTL_PKWKM_ORIG, sum).apply().get(0, 0)));
        }
        System.out.println(nkvBelow1_PkwKm.print());

        var options = CsvWriteOptions.builder("output/NKV_below_1_addPkwKmhSaved.csv").separator(';').build();
        new CsvWriter().write(nkvBelow1_PkwKm, options);
      }

      System.out.println(BvwpUtils.SEPARATOR_AUSGABE);

      { // Gesparte zusätzliche vkm Güterverkehr
        Table nkvBelow1_LkwKm =
                Table.create("Projects with BCR < 1 -- saved add. vkm GÜTERverkehr (Mio LKW-km/a)");
        nkvBelow1_LkwKm.addColumns(
                DoubleColumn.create(
                        "add. LKW-km/a of all projects",
                        (double) tbl.summarize(Headers.ADDTL_LKWKM_ORIG, sum).apply().get(0, 0)));

        // Erstelle eine Spalte für jeden "Fall"
        for (String s : headersKMT) {
          Table tblBelow1 = tbl.where(tbl.numberColumn(s).isLessThan(1.));
          nkvBelow1_LkwKm.addColumns(
                  DoubleColumn.create(s, (double) tblBelow1.summarize(Headers.ADDTL_LKWKM_ORIG, sum).apply().get(0, 0)));
        }
        System.out.println(nkvBelow1_LkwKm.print());

        var options = CsvWriteOptions.builder("output/NKV_below_1_addLkwKmhSaved.csv").separator(';').build();
        new CsvWriter().write(nkvBelow1_LkwKm, options);
      }

      System.out.println(BvwpUtils.SEPARATOR_AUSGABE);

      { // Gesparte Investitionskosten - Barwert der Kosten in Mio EUR
        Table nkvBelow1_costs =
            Table.create("Projects with BCR < 1 -- saved Investment Costs - Barwert (Mio EUR)");
        nkvBelow1_costs.addColumns(
            DoubleColumn.create(
                "Investment costs of all projects (Mio EUR)",
                (double) tbl.summarize(Headers.INVCOST_BARWERT_ORIG, sum ).apply().get(0, 0 ) ) );

        // Erstelle eine Spalte für jeden "Fall"
        for (String s : headersKMT) {
          Table tblBelow1 = tbl.where(tbl.numberColumn(s).isLessThan(1.));
          nkvBelow1_costs.addColumns(
              DoubleColumn.create(s, (double) tblBelow1.summarize(Headers.INVCOST_BARWERT_ORIG, sum ).apply().get(0, 0 ) ) );
        }
        System.out.println(nkvBelow1_costs.print());

        var options = CsvWriteOptions.builder("output/NKV_below_1_costsSaved.csv").separator(';').build();
        new CsvWriter().write(nkvBelow1_costs, options);
      }

      System.out.println(BvwpUtils.SEPARATOR_AUSGABE);

      { // Gesparte CO2 - Emissionen: Aus Verkehr und Lebenszyklusemissionen (t/a)
        Table nkvBelow1_co2safed = Table.create(
                "Projects with BCR < 1 -- safed CO2 emissions -- direct and lifecycle of infrastructure (t/a");
        nkvBelow1_co2safed.addColumns(DoubleColumn.create(
                "CO2 Emissions of all projects (t/a)",
                (double) tbl.summarize(Headers.CO_2_EQUIVALENTS_EMISSIONS, sum).apply().get(0, 0)));

        // Erstelle eine Spalte für jeden "Fall"
        for (String s : headersKMT) {
          Table tblBelow1 = tbl.where(tbl.numberColumn(s).isLessThan(1.));
          nkvBelow1_co2safed.addColumns(DoubleColumn.create(
                  s,
                  (double) tblBelow1
                          .summarize(Headers.CO_2_EQUIVALENTS_EMISSIONS, sum)
                          .apply()
                          .get(0, 0)));
        }
        System.out.println(nkvBelow1_co2safed.print());

        var options =
            CsvWriteOptions.builder("output/NKV_below_1_co2Safed.csv").separator(';').build();
        new CsvWriter().write(nkvBelow1_co2safed, options);
      }
    }
  }

  private static void kmtPlots_old(Axis xAxis, Table table, String xNameKMT)
      throws IOException {
    Figure figureNkv = FiguresKMT.createFigureNkv(xAxis, RunLocalCsvScrapingKMT.plotWidth, table, xNameKMT);
    Figure figureCostByPriority = FiguresKMT.createFigureCostByPriority(RunLocalCsvScrapingKMT.plotWidth, table, Headers.INVCOST_BARWERT_ORIG );
    Figure figureNkvByPriority = FiguresKMT.createFigureNkvByPriority(xAxis, RunLocalCsvScrapingKMT.plotWidth, table, Headers.INVCOST_BARWERT_ORIG );
    Figure figureCO2Benefit = FiguresKMT.createFigureCO2(xAxis, RunLocalCsvScrapingKMT.plotWidth, table, xNameKMT);
    Figure figureNkvChangeCo2_680 = FiguresKMT.createFigureNkvChange(RunLocalCsvScrapingKMT.plotWidth, table, Headers.NKV_ORIG_EN, Headers.NKV_CO2_700_EN );
    Figure figureNkvChangeInduz_2000 = FiguresKMT.createFigureNkvChange(RunLocalCsvScrapingKMT.plotWidth, table, Headers.NKV_ORIG_EN, Headers.NKV_CO2_2000_EN );

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
    Figure figureNkvChangeCo2_700 = FiguresKMT.createFigureNkvChange(RunLocalCsvScrapingKMT.plotWidth, table, Headers.NKV_ORIG_EN, Headers.NKV_CO2_700_EN );
    Figure figureNkvChangeInduz_2000 = FiguresKMT.createFigureNkvChange(RunLocalCsvScrapingKMT.plotWidth, table, Headers.NKV_ORIG_EN, Headers.NKV_CO2_2000_EN );

    Figure figureNkvChange_InvCostTud = FiguresKMT.createFigureNkvChange(RunLocalCsvScrapingKMT.plotWidth, table, Headers.NKV_ORIG_EN, Headers.NKV_INVCOSTTUD_EN );
    Figure figureNkvChange_InvCost150 = FiguresKMT.createFigureNkvChange(RunLocalCsvScrapingKMT.plotWidth, table, Headers.NKV_ORIG_EN, Headers.NKV_INVCOST150_EN );
    Figure figureNkvChange_InvCost200 = FiguresKMT.createFigureNkvChange(RunLocalCsvScrapingKMT.plotWidth, table, Headers.NKV_ORIG_EN, Headers.NKV_INVCOST200_EN );
    Figure figureNkvChange_Co2_700_InvCostTud = FiguresKMT.createFigureNkvChange(RunLocalCsvScrapingKMT.plotWidth, table, Headers.NKV_ORIG_EN, Headers.NKV_CO2_700_INVCOSTTUD_EN );
    Figure figureNkvChange_Co2_700_InvCost150 = FiguresKMT.createFigureNkvChange(RunLocalCsvScrapingKMT.plotWidth, table, Headers.NKV_ORIG_EN, Headers.NKV_CO2_700_INVCOST150_EN );
    Figure figureNkvChange_Co2_700_InvCost200 = FiguresKMT.createFigureNkvChange(RunLocalCsvScrapingKMT.plotWidth, table, Headers.NKV_ORIG_EN, Headers.NKV_CO2_700_INVCOST200_EN );
    Figure figureNkvChange_Co2_2000_InvCostTud = FiguresKMT.createFigureNkvChange(RunLocalCsvScrapingKMT.plotWidth, table, Headers.NKV_ORIG_EN, Headers.NKV_CO2_2000_INVCOSTTUD_EN );
    Figure figureNkvChange_Co2_2000_InvCost150 = FiguresKMT.createFigureNkvChange(RunLocalCsvScrapingKMT.plotWidth, table, Headers.NKV_ORIG_EN, Headers.NKV_CO2_2000_INVCOST150_EN );
    Figure figureNkvChange_Co2_2000_InvCost200 = FiguresKMT.createFigureNkvChange(RunLocalCsvScrapingKMT.plotWidth, table, Headers.NKV_ORIG_EN, Headers.NKV_CO2_2000_INVCOST200_EN );

    Figure figureNkvChange_InvCost150_200 =
        FiguresKMT.createFigureNkvChange(
                table,
                Headers.NKV_INVCOST150_EN,
            Headers.NKV_INVCOST200_EN);
    Figure figureNkvChange_Co2_700_InvCost150_200 =
        FiguresKMT.createFigureNkvChange(
                table,
                Headers.NKV_CO2_700_INVCOST150_EN,
            Headers.NKV_CO2_700_INVCOST200_EN);
    Figure figureNkvChange_Co2_2000_InvCost150_200 =
        FiguresKMT.createFigureNkvChange(
                table,
                Headers.NKV_CO2_2000_INVCOST150_EN,
            Headers.NKV_CO2_2000_INVCOST200_EN);

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
