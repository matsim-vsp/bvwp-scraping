package org.tub.vsp.bvwp.users.kmt;

import java.util.Map;
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
import tech.tablesaw.plotly.components.Axis;
import tech.tablesaw.plotly.components.Axis.Type;
import tech.tablesaw.plotly.components.Figure;
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
          0.
//          constructionCostsByProject.get(streetBaseDataContainer.getProjectInformation().getProjectNumber())
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
    table = table.sortOn(comparator);
    NumberFormat format = NumberFormat.getCompactNumberInstance();
    format.setMaximumFractionDigits(0);
    table.numberColumn(Headers.CO2_COST_EL03 ).setPrintFormatter(format, "n/a" );

    //Projekte, die bereits vor Änderung NKV <1 haben
    Table tableBaseKl1 = table.where(table.numberColumn(Headers.NKV_ORIG ).isLessThan(1. ) );
    Table tableCo2_680_Kl1 = table.where(table.numberColumn(Headers.NKV_CO2_700_EN ).isLessThan(1. ) );
    Table tableCo2_2000_Kl1 = table.where(table.numberColumn(Headers.NKV_CO2_2000_EN).isLessThan(1.));
    Table tableIndCo2kl1 = table.where(
        table.numberColumn(Headers.NKV_EL03_CARBON215_INVCOSTTUD ).isLessThan(1. ) );

    { //-- von KN
        System.out.println(BvwpUtils.SEPARATOR);
        System.out.println(table.summarize(Headers.NKV_ORIG, count, mean, stdDev, min, max )
            .by(Headers.EINSTUFUNG ) );
        System.out.println(System.lineSeparator() + "Davon NKV < 1:");
        System.out.println(
            tableIndCo2kl1.summarize(Headers.NKV_EL03_CARBON215_INVCOSTTUD, count, mean, stdDev, min, max )
                .by(Headers.EINSTUFUNG ) );

        System.out.println(BvwpUtils.SEPARATOR);
        System.out.println(
            table.summarize(Headers.INVCOST_ORIG, sum, mean, stdDev, min, max )
                .by(Headers.EINSTUFUNG ) );
        System.out.println(System.lineSeparator() + "Davon NKV < 1:");
        System.out.println(
            tableIndCo2kl1.summarize(Headers.INVCOST_ORIG, sum, mean, stdDev, min, max )
                .by(Headers.EINSTUFUNG ) );

        System.out.println(BvwpUtils.SEPARATOR);
        System.out.println(
            table.summarize(
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
        System.out.println("All projects");
        System.out.println(table.summarize(Headers.NKV_ORIG, count ).apply() );

        System.out.println(BvwpUtils.SEPARATOR);
        System.out.println(BvwpUtils.SEPARATOR);
        Table kmtTable = Table.create("Projects with BCR < 1");
        kmtTable.addColumns(DoubleColumn.create("#Projects"
            , new double[]{table.rowCount()
        }
        ));
        kmtTable.addColumns(DoubleColumn.create("Base"
            , new double[]{tableBaseKl1.rowCount()}
        ));
        kmtTable.addColumns(DoubleColumn.create("CO2_680"
            , new double[]{tableCo2_680_Kl1.rowCount()}
        ));
        kmtTable.addColumns(DoubleColumn.create("Co2_2000"
            , new double[]{tableCo2_2000_Kl1.rowCount()}
        ));
//            kmtTable.addColumns(DoubleColumn.create("CO2_Induced"
//                , new double[]{tableIndCo2kl1.rowCount()}
//            ));
        System.out.println(kmtTable.print());
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
    Figure figureNkv = FiguresKMT.createFigureNkv(xAxis, plotWidth, table, xNameKMT);
    Figure figureCostByPriority = FiguresKMT.createFigureCostByPriority(plotWidth, table, Headers.INVCOST_ORIG );
    Figure figureNkvByPriority = FiguresKMT.createFigureNkvByPriority(xAxis, plotWidth, table, Headers.INVCOST_ORIG );
    Figure figureCO2Benefit = FiguresKMT.createFigureCO2(xAxis, plotWidth, table, xNameKMT);
    Figure figureNkvChangeCo2_680 = FiguresKMT.createFigureNkvChange(plotWidth, table, Headers.NKV_ORIG, Headers.NKV_CO2_700_EN );
    Figure figureNkvChangeInduz_2000 = FiguresKMT.createFigureNkvChange(plotWidth, table, Headers.NKV_ORIG, Headers.NKV_CO2_2000_EN );


    String page = MultiPlotUtils.pageTop() + System.lineSeparator() +
        figureNkv.asJavascript("plot1") + System.lineSeparator() +
//                FiguresKMT.createTextFigure("Neuer Abschnitt").asJavascript("plot2") + System.lineSeparator() + //Test um mal eine Trennung zu erzeugen... vlt doch anders machen
        figureCostByPriority.asJavascript("plot2") + System.lineSeparator() +
        figureNkvByPriority.asJavascript("plot3")+System.lineSeparator() +
        figureCO2Benefit.asJavascript("plot4") + System.lineSeparator() +
        figureNkvChangeCo2_680.asJavascript("plot5") + System.lineSeparator() +
        figureNkvChangeInduz_2000.asJavascript("plot6") + System.lineSeparator() +
//                figureNkvChangeInduzCo2.asJavascript("plot7") + System.lineSeparator() +
        MultiPlotUtils.pageBottom;

    File outputFile = Paths.get("EWGT_CO2-Values.html").toFile();

    try (FileWriter fileWriter = new FileWriter(outputFile)) {
      fileWriter.write(page);
    }

    new Browser().browse(outputFile);
  }

}
