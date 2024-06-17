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
import tech.tablesaw.api.DoubleColumn;
import tech.tablesaw.api.Row;
import tech.tablesaw.api.Table;
import tech.tablesaw.io.Destination;
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
    private static final Logger logger = LogManager.getLogger(RunLocalCsvScrapingKMT.class );

    public static void main(String[] args) throws IOException{
        Locale.setDefault( Locale.US );

        logger.warn(
            "(vermutl. weitgehend gelöst) Teilweise werden die Hauptprojekte bewertet und nicht" +
                "Teilprojekte (A20); teilweise werden die Teilprojekte " +
                "bewertet aber nicht das Hauptprojekt (A2).  "
                + "Müssen aufpassen, dass nichts unter den Tisch fällt.");
        logger.warn(
            "Bei https://www.bvwp-projekte.de/strasse/A559-G10-NW/A559-G10-NW.html "
                + "hat evtl. die Veränderung "
                + "Betriebsleistung PV falsches VZ.  Nutzen (positiv) dann wieder richtig.");
        logger.warn(
            "Wieso geht bei https://www.bvwp-projekte.de/strasse/A14-G20-ST-BB/A14-G20-ST-BB.html"
                + " der Nutzen mit impl und co2Price sogar nach oben?");
        logger.warn("===========");

        String positivListe = BvwpUtils.getPositivListe();

        StreetScraper scraper = new StreetScraper();

        logger.info( "Starting scraping" );

        String filePath = "../shared-svn/";
        Map<String, Double> constructionCostsByProject = BvwpUtils.getConstructionCostsFromTudFile(filePath );

        final String regexToExclude = "(A...B.*)|(A....B.*)"; // Bundesstrassen, die von Autobahnen ausgehen.

        // yyyy man könnte (sollte?) den table in den StreetAnalysisDataContainer mit hinein geben, und die Werte gleich dort eintragen.  kai, feb'24

        List<StreetAnalysisDataContainer> allStreetBaseData = scraper
            .extractAllLocalBaseData("./data/street/all", "A", ".*", "")
            .stream()
			.map(streetBaseDataContainer -> new StreetAnalysisDataContainer(streetBaseDataContainer,
//          0.
          constructionCostsByProject.get(streetBaseDataContainer.getProjectInformation().getProjectNumber())
      ))
            .toList();

        logger.info("Writing csv");
        StreetCsvWriter csvWriter = new StreetCsvWriter("output/street_data.csv");
        Table table = csvWriter.writeCsv(allStreetBaseData);


        table.addColumns(table.numberColumn(Headers.NKV_ORIG )
                              .subtract(table.numberColumn(Headers.NKV_EL03_CARBON215_INVCOSTTUD ) ).setName(
                Headers.NKV_EL03_DIFF ) );

        { //Plotting and table preparation
            String xNameKMT;
            Axis.AxisBuilder xAxisBuilder = Axis.builder();
            {
                xNameKMT = Headers.CO2_COST_EL03;
                xAxisBuilder.type(Type.LINEAR);
            }

            table = table.sortDescendingOn(xNameKMT);
            Axis xAxis = xAxisBuilder.title(xNameKMT).build();
            final int plotWidth = 1400;

          kmtPlots_old(xAxis, plotWidth, table, xNameKMT);
          kmtPlots_Co2values(xAxis, plotWidth, table, xNameKMT);
        }

      calculationsAndTableWriting(table);
    }

  private static void calculationsAndTableWriting(Table table) {
    // === Some calculations

    Comparator<Row> comparator = (o1, o2) -> {
        Einstufung p1 = Einstufung.valueOf(o1.getString(Headers.EINSTUFUNG ) );
        Einstufung p2 = Einstufung.valueOf(o2.getString(Headers.EINSTUFUNG ) );
        return p1.compareTo(p2);
    };

    final Table tbl = table.sortOn(comparator);
    NumberFormat format = NumberFormat.getCompactNumberInstance();
    format.setMaximumFractionDigits(0);
    tbl.numberColumn(Headers.CO2_COST_EL03 ).setPrintFormatter(format, "n/a" );

    //Projekte, die bereits vor Änderung NKV <1 haben
    Table tableBaseKl1 = tbl.where(tbl.numberColumn(Headers.NKV_ORIG ).isLessThan(1. ) );
    Table tableIndCo2kl1 = tbl.where(tbl.numberColumn(Headers.NKV_EL03_CARBON215_INVCOSTTUD ).isLessThan(1. ) );

    { //-- von KN
        System.out.println(BvwpUtils.SEPARATOR);
        System.out.println("NKV Original auf Gesamttabelle");
        System.out.println(tbl.summarize(Headers.NKV_ORIG, count, mean, stdDev, min, max ).by(Headers.EINSTUFUNG ) );
        System.out.println(tbl.summarize(Headers.NKV_ORIG, count, mean, stdDev, min, max ).apply());
        System.out.println(System.lineSeparator() + "Davon NKV < 1: nach Modifikation.");
        System.out.println(tableIndCo2kl1.summarize(Headers.NKV_EL03_CARBON215_INVCOSTTUD, count, mean, stdDev, min, max ).by(Headers.EINSTUFUNG ) );
        System.out.println(tableIndCo2kl1.summarize(Headers.NKV_EL03_CARBON215_INVCOSTTUD, count, mean, stdDev, min, max ).apply() );

        System.out.println(BvwpUtils.SEPARATOR);
        System.out.println(tbl.summarize(Headers.INVCOST_ORIG, sum, mean, stdDev, min, max ).by(Headers.EINSTUFUNG ) );
        System.out.println(tbl.summarize(Headers.INVCOST_ORIG, sum, mean, stdDev, min, max ).apply());
        System.out.println(System.lineSeparator() + "Davon NKV < 1:");
        System.out.println(tableIndCo2kl1.summarize(Headers.INVCOST_ORIG, sum, mean, stdDev, min, max ).by(Headers.EINSTUFUNG ) );
        System.out.println(tableIndCo2kl1.summarize(Headers.INVCOST_ORIG, sum, mean, stdDev, min, max ).apply());
        System.out.println(BvwpUtils.SEPARATOR);
        System.out.println(
            tbl.summarize(
                Headers.CO2_COST_EL03, sum, mean, stdDev, min, max ).by(Headers.EINSTUFUNG ) );
        System.out.println(System.lineSeparator() + "Davon NKV < 1:");
        System.out.println(
            tableIndCo2kl1.summarize(Headers.CO2_COST_EL03, sum, mean, stdDev, min, max )
                .by(Headers.EINSTUFUNG ) );
    }

    {
        //KMT
        System.out.println(BvwpUtils.SEPARATOR);
        System.out.println("### KMT ###");
        System.out.println(BvwpUtils.SEPARATOR);

        List<String> headersKMT = new LinkedList<>();
        headersKMT.add(Headers.NKV_ORIG);
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

        //General Todos for all three analysis:
        //Todo: Zeile hinzufügen zu eingsparten Projektanzahl. --> alles in einer Tabelle zusammenfassen??
        //Todo: ! Zeile einfügen, welceh die relative Abweichung zum Base-Case anzeigt... (x% Projekte / Costen sind "raus")

        //Todo: Eigentlich müsste man aus dem folgenden eine Funktion machen können, mit wenigen übergabe-Infos. Weil inhaltlich ist es 3x das gleiche.
        { //Projektanzahl
            Table nkvBelow1_count = Table.create("Nu of Projects with BCR < 1 ");
            nkvBelow1_count.addColumns(DoubleColumn.create("# of all Projects"
                    , new double[]{tbl.rowCount()
                    }
            ));

            //Erstelle eine Spalte für jeden "Fall"
            for (String s : headersKMT) {
                Table tblBelow1 = tbl.where(tbl.numberColumn(s).isLessThan(1.));
                nkvBelow1_count.addColumns(DoubleColumn.create(s, new double[]{tblBelow1.rowCount()}));
            }
            System.out.println(nkvBelow1_count.print());

            var options = CsvWriteOptions.builder("output/NKV_below_1_projects.csv").separator(';').build();
            new CsvWriter().write(nkvBelow1_count, options);
        }

        System.out.println(BvwpUtils.SEPARATOR);

        { // Gesparte Investitionskosten - Barwert der Kosten in Mio EUR
            Table nkvBelow1_costs = Table.create("Projects with BCR < 1 -- safed Investment Costs - Barwert (Mio EUR)");
            nkvBelow1_costs.addColumns(DoubleColumn.create("Investment costs of all projects (Mio EUR)"
                    , (double) tbl.summarize(Headers.INVCOST_ORIG, sum).apply().get(0,0))
            );

            //Erstelle eine Spalte für jeden "Fall"
            for (String s : headersKMT) {
                Table tblBelow1 = tbl.where(tbl.numberColumn(s).isLessThan(1.));
                nkvBelow1_costs.addColumns(DoubleColumn.create(s, new double[]{(double) tblBelow1.summarize(Headers.INVCOST_ORIG, sum).apply().get(0,0)}));
            }
            System.out.println(nkvBelow1_costs.print());

            var options = CsvWriteOptions.builder("output/NKV_below_1_costsSafed.csv").separator(';').build();
            new CsvWriter().write(nkvBelow1_costs, options);
        }

        System.out.println(BvwpUtils.SEPARATOR);

        { // Gesparte CO2 - Emissionen: Aus Verkehr und Lebenszyklusemissionen (t/a)
            Table nkvBelow1_co2safed = Table.create("Projects with BCR < 1 -- safed CO2 emissions -- direct and lifecycle of infrastructure (t/a");
            nkvBelow1_co2safed.addColumns(DoubleColumn.create("CO2 Emissions of all projects (t/a)"
                    , (double) tbl.summarize(Headers.CO_2_EQUIVALENTS_EMISSIONS, sum).apply().get(0,0))
            );

            //Erstelle eine Spalte für jeden "Fall"
            for (String s : headersKMT) {
                Table tblBelow1 = tbl.where(tbl.numberColumn(s).isLessThan(1.));
                nkvBelow1_co2safed.addColumns(DoubleColumn.create(s, (double) tblBelow1.summarize(Headers.CO_2_EQUIVALENTS_EMISSIONS, sum).apply().get(0,0)));
            }
            System.out.println(nkvBelow1_co2safed.print());

            var options = CsvWriteOptions.builder("output/NKV_below_1_co2Safed.csv").separator(';').build();
            new CsvWriter().write(nkvBelow1_co2safed, options);
        }

    }
  }

  private static void kmtPlots_old(Axis xAxis, int plotWidth, Table table, String xNameKMT)
      throws IOException {
    Figure figureNkv = FiguresKMT.createFigureNkv(xAxis, plotWidth, table, xNameKMT);
    Figure figureCostByPriority = FiguresKMT.createFigureCostByPriority(plotWidth, table, Headers.INVCOST_ORIG );
    Figure figureNkvByPriority = FiguresKMT.createFigureNkvByPriority(xAxis, plotWidth, table, Headers.INVCOST_ORIG );
    Figure figureCO2Benefit = FiguresKMT.createFigureCO2(xAxis, plotWidth, table, xNameKMT);
    Figure figureNkvChangeCo2_680 = FiguresKMT.createFigureNkvChange(plotWidth, table,
        Headers.NKV_ORIG, Headers.NKV_CO2_700_EN );
    Figure figureNkvChangeInduz_2000 = FiguresKMT.createFigureNkvChange(plotWidth, table,
        Headers.NKV_ORIG, Headers.NKV_CO2_2000_EN );
//            Figure figureNkvChangeInduzCo2 = Figures.createFigureNkvChange(plotWidth, table,
//                Headers.NKV_NO_CHANGE, Headers.NKV_INDUZ_CO2);

    String pageKMT = MultiPlotUtils.pageTop() + System.lineSeparator() +
        figureNkv.asJavascript("plot1") + System.lineSeparator() +
//                FiguresKMT.createTextFigure("Neuer Abschnitt").asJavascript("plot2") + System.lineSeparator() + //Test um mal eine Trennung zu erzeugen... vlt doch anders machen
        figureCostByPriority.asJavascript("plot2") + System.lineSeparator() +
        figureNkvByPriority.asJavascript("plot3")+System.lineSeparator() +
        figureCO2Benefit.asJavascript("plot4") + System.lineSeparator() +
        figureNkvChangeCo2_680.asJavascript("plot5") + System.lineSeparator() +
        figureNkvChangeInduz_2000.asJavascript("plot6") + System.lineSeparator() +
//                figureNkvChangeInduzCo2.asJavascript("plot7") + System.lineSeparator() +
        MultiPlotUtils.pageBottom;

    File outputFileKMT = Paths.get("multiplotKMT.html").toFile();

    try (FileWriter fileWriter = new FileWriter(outputFileKMT)) {
        fileWriter.write(pageKMT);
    }

    new Browser().browse(outputFileKMT);
  }

  private static void kmtPlots_Co2values(Axis xAxis, int plotWidth, Table table, String xNameKMT)
      throws IOException {
    Figure figureNkvChangeCo2_700 = FiguresKMT.createFigureNkvChange(plotWidth, table, Headers.NKV_ORIG, Headers.NKV_CO2_700_EN );
    Figure figureNkvChangeInduz_2000 = FiguresKMT.createFigureNkvChange(plotWidth, table, Headers.NKV_ORIG, Headers.NKV_CO2_2000_EN );

    Figure figureNkvChange_InvCostTud = FiguresKMT.createFigureNkvChange(plotWidth, table, Headers.NKV_ORIG, Headers.NKV_INVCOSTTUD_EN );
    Figure figureNkvChange_InvCost150 = FiguresKMT.createFigureNkvChange(plotWidth, table, Headers.NKV_ORIG, Headers.NKV_INVCOST150_EN);
    Figure figureNkvChange_InvCost200 = FiguresKMT.createFigureNkvChange(plotWidth, table, Headers.NKV_ORIG, Headers.NKV_INVCOST200_EN );
    Figure figureNkvChange_Co2_700_InvCostTud = FiguresKMT.createFigureNkvChange(plotWidth, table, Headers.NKV_ORIG, Headers.NKV_CO2_700_INVCOSTTUD_EN);
    Figure figureNkvChange_Co2_700_InvCost150 = FiguresKMT.createFigureNkvChange(plotWidth, table, Headers.NKV_ORIG, Headers.NKV_CO2_700_INVCOST150_EN);
    Figure figureNkvChange_Co2_700_InvCost200 = FiguresKMT.createFigureNkvChange(plotWidth, table, Headers.NKV_ORIG, Headers.NKV_CO2_700_INVCOST200_EN );
    Figure figureNkvChange_Co2_2000_InvCostTud = FiguresKMT.createFigureNkvChange(plotWidth, table, Headers.NKV_ORIG, Headers.NKV_CO2_2000_INVCOSTTUD_EN );
    Figure figureNkvChange_Co2_2000_InvCost150 = FiguresKMT.createFigureNkvChange(plotWidth, table, Headers.NKV_ORIG, Headers.NKV_CO2_2000_INVCOST150_EN);
    Figure figureNkvChange_Co2_2000_InvCost200 = FiguresKMT.createFigureNkvChange(plotWidth, table, Headers.NKV_ORIG, Headers.NKV_CO2_2000_INVCOST200_EN);

      Figure figureNkvChange_InvCost150_200 = FiguresKMT.createFigureNkvChange(plotWidth, table, Headers.NKV_ORIG, Headers.NKV_INVCOST150_EN, Headers.NKV_INVCOST200_EN);
    Figure figureNkvChange_Co2_700_InvCost150_200 = FiguresKMT.createFigureNkvChange(plotWidth, table, Headers.NKV_ORIG, Headers.NKV_CO2_700_INVCOST150_EN, Headers.NKV_CO2_700_INVCOST200_EN);
      Figure figureNkvChange_Co2_2000_InvCost150_200 = FiguresKMT.createFigureNkvChange(plotWidth, table, Headers.NKV_ORIG, Headers.NKV_CO2_2000_INVCOST150_EN, Headers.NKV_CO2_2000_INVCOST200_EN);

      String page = MultiPlotUtils.pageTop() + System.lineSeparator() +
        figureNkvChangeCo2_700.asJavascript("plot1") + System.lineSeparator() +
        figureNkvChangeInduz_2000.asJavascript("plot2") + System.lineSeparator() +

       figureNkvChange_InvCostTud .asJavascript("plot3") + System.lineSeparator() +
        figureNkvChange_InvCost150.asJavascript("plot4") + System.lineSeparator() +
        figureNkvChange_InvCost200.asJavascript("plot5") + System.lineSeparator() +

        figureNkvChange_Co2_700_InvCostTud.asJavascript("plot6") + System.lineSeparator() +
        figureNkvChange_Co2_700_InvCost150.asJavascript("plot7") + System.lineSeparator() +
        figureNkvChange_Co2_700_InvCost200.asJavascript("plot8") + System.lineSeparator() +

        figureNkvChange_Co2_2000_InvCostTud.asJavascript("plot9") + System.lineSeparator() +
        figureNkvChange_Co2_2000_InvCost150.asJavascript("plot10") + System.lineSeparator() +
        figureNkvChange_Co2_2000_InvCost200.asJavascript("plot11") + System.lineSeparator() +

        //Plot, der beide Inv Kostenveränderungen für alle Projekte enthält
        figureNkvChange_InvCost150_200.asJavascript("plotA") + System.lineSeparator() +
        figureNkvChange_Co2_700_InvCost150_200.asJavascript("plotB") + System.lineSeparator() +
        figureNkvChange_Co2_2000_InvCost150_200.asJavascript("plotC") + System.lineSeparator() +
        MultiPlotUtils.pageBottom;

    File outputFile = Paths.get("EWGT_CO2-Values.html").toFile();

    try (FileWriter fileWriter = new FileWriter(outputFile)) {
      fileWriter.write(page);
    }

    new Browser().browse(outputFile);
  }

}
