package org.tub.vsp.bvwp;

import static tech.tablesaw.aggregate.AggregateFunctions.count;
import static tech.tablesaw.aggregate.AggregateFunctions.max;
import static tech.tablesaw.aggregate.AggregateFunctions.mean;
import static tech.tablesaw.aggregate.AggregateFunctions.min;
import static tech.tablesaw.aggregate.AggregateFunctions.stdDev;
import static tech.tablesaw.aggregate.AggregateFunctions.sum;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Paths;
import java.text.NumberFormat;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.tub.vsp.bvwp.data.Headers;
import org.tub.vsp.bvwp.data.container.analysis.StreetAnalysisDataContainer;
import org.tub.vsp.bvwp.data.type.Priority;
import org.tub.vsp.bvwp.io.StreetCsvWriter;
import org.tub.vsp.bvwp.plot.MultiPlotExample;
import org.tub.vsp.bvwp.scraping.StreetScraper;
import tech.tablesaw.api.DoubleColumn;
import tech.tablesaw.api.Row;
import tech.tablesaw.api.Table;
import tech.tablesaw.plotly.components.Axis;
import tech.tablesaw.plotly.components.Axis.Type;
import tech.tablesaw.plotly.components.Figure;
import tech.tablesaw.plotly.display.Browser;

public class RunLocalCsvScraping {
    private static final Logger logger = LogManager.getLogger(RunLocalCsvScraping.class );
    public static final String SEPARATOR = System.lineSeparator() + "===========================================";

    public static void main(String[] args) throws IOException {
        Locale.setDefault(Locale.US);

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

        logger.info("Starting scraping");

        // yyyy man könnte (sollte?) den table in den StreetAnalysisDataContainer mit hinein geben, und die Werte gleich dort eintragen.  kai, feb'24

        List<StreetAnalysisDataContainer> allStreetBaseData = scraper
            .extractAllLocalBaseData("./data/street/all", "A", ".*")
            .stream()
            .map(StreetAnalysisDataContainer::new)
            .toList();

        logger.info("Writing csv");
        StreetCsvWriter csvWriter = new StreetCsvWriter("output/street_data.csv");
        Table table = csvWriter.writeCsv(allStreetBaseData);

//        table = table.where( table.numberColumn( Headers.NKV_INDUZ_CO2 ).isLessThan( 2.) );

        table.addColumns(table.numberColumn(Headers.NKV_NO_CHANGE)
            .subtract(table.numberColumn(Headers.NKV_INDUZ_CO2)).setName(
                Headers.NKV_DIFF));

//        final Table newTable = table.selectColumns( "nkvDiff", Headers.COST_OVERALL );
//        LinearModel winsModel = OLS.fit( Formula.lhs("nkvDiff" ), newTable.smile().toDataFrame() );
//        System.out.println( winsModel );
//        System.exit(-1);
        // ===

        { //KN
            String xName;
            Axis.AxisBuilder xAxisBuilder = Axis.builder();
            // ---------------------------------------------------------------
//        {
//            xName = Headers.B_CO2_NEU;
//            xAxisBuilder.type( Axis.Type.LOG );
//        }
            // ---------------------------------------------------------------
            // ---------------------------------------------------------------
            {
//            xName = Headers.NKV_NO_CHANGE;
//            xName = NKV_DIFF;
                xName = Headers.ADDITIONAL_LANE_KM;
                xAxisBuilder
                    .type(Axis.Type.LOG)
//                            .autoRange( Axis.AutoRange.REVERSED )
                ;
            }
            // ---------------------------------------------------------------
            table = table.sortDescendingOn(xName);
            Axis xAxis = xAxisBuilder.title(xName).build();

            Figures figures = new Figures(table, xAxis, xName);
            Figure figure = figures.createFigurePkwKm();
            Figure figure2 = figures.createFigureNkv();
            Figure figure3 = figures.createFigureCost();
            Figure figure4 = figures.createFigureCO2();
            Figure figure5 = figures.createFigureElasticities();
            Figure figure6 = figures.createFigureFzkm();

            String page = MultiPlotExample.pageTop + System.lineSeparator() +
                figure2.asJavascript("plot1") + System.lineSeparator() +
                figure.asJavascript("plot2") + System.lineSeparator() +
                figure3.asJavascript("plot3") + System.lineSeparator() +
                figure4.asJavascript("plot4") + System.lineSeparator() +
                figure5.asJavascript("plot5") + System.lineSeparator() +
                figure6.asJavascript("plot6") + System.lineSeparator() +
                MultiPlotExample.pageBottom;

            File outputFile = Paths.get("multiplot.html").toFile();

            try (FileWriter fileWriter = new FileWriter(outputFile)) {
                fileWriter.write(page);
            }

            new Browser().browse(outputFile);
        }

        { //KMT
            String xNameKMT;
            Axis.AxisBuilder xAxisBuilder = Axis.builder();
            {
                xNameKMT = Headers.B_CO2_NEU;
                xAxisBuilder.type(Type.LINEAR);
            }
//        {
//            xNameKMT = Headers.NKV_INDUZ_CO2;
//            xAxisBuilder
//                             //                         .type( Axis.Type.LOG )
//                             .autoRange( Axis.AutoRange.REVERSED );
//        }
            table = table.sortDescendingOn(xNameKMT);
            Axis xAxis = xAxisBuilder.title(xNameKMT).build();
            final int plotWidth = 1400;

            Figure figureNkv = Figures.createFigureNkv(xAxis, plotWidth, table, xNameKMT);
            Figure figureCO2 = Figures.createFigureCO2(xAxis, plotWidth, table, xNameKMT);
            Figure figureNkvChangeCo2 = Figures.createFigureNkvChange(plotWidth, table,
                Headers.NKV_NO_CHANGE, Headers.NKV_CO2);
            Figure figureNkvChangeInduz = Figures.createFigureNkvChange(plotWidth, table,
                Headers.NKV_NO_CHANGE, Headers.NKV_INDUZ);
            Figure figureNkvChangeInduzCo2 = Figures.createFigureNkvChange(plotWidth, table,
                Headers.NKV_NO_CHANGE, Headers.NKV_INDUZ_CO2);

            String pageKMT = MultiPlotExample.pageTop + System.lineSeparator() +
                figureNkv.asJavascript("plot1") + System.lineSeparator() +
                figureCO2.asJavascript("plot4") + System.lineSeparator() +
                figureNkvChangeCo2.asJavascript("plot5") + System.lineSeparator() +
                figureNkvChangeInduz.asJavascript("plot5b") + System.lineSeparator() +
                figureNkvChangeInduzCo2.asJavascript("plot5c") + System.lineSeparator() +
                MultiPlotExample.pageBottom;

            File outputFileKMT = Paths.get("multiplotKMT.html").toFile();

            try (FileWriter fileWriter = new FileWriter(outputFileKMT)) {
                fileWriter.write(pageKMT);
            }

            new Browser().browse(outputFileKMT);
        }


        // === Some calculations

        Comparator<Row> comparator = (o1, o2) -> {
            Priority p1 = Priority.valueOf(o1.getString(Headers.PRIORITY));
            Priority p2 = Priority.valueOf(o2.getString(Headers.PRIORITY));
            return p1.compareTo(p2);
        };
        table = table.sortOn(comparator);
        NumberFormat format = NumberFormat.getCompactNumberInstance();
        format.setMaximumFractionDigits(0);
        table.numberColumn(Headers.B_CO2_NEU).setPrintFormatter(format, "n/a");

        //Projekte, die bereits vor Änderung NKV <1 haben
        Table tableBaseKl1 = table.where(table.numberColumn(Headers.NKV_NO_CHANGE).isLessThan(1.));
        Table tableCo2Kl1 = table.where(table.numberColumn(Headers.NKV_CO2).isLessThan(1.));
        Table tableIndKl1 = table.where(table.numberColumn(Headers.NKV_INDUZ).isLessThan(1.));
        Table tableIndCo2kl1 = table.where(
            table.numberColumn(Headers.NKV_INDUZ_CO2).isLessThan(1.));

        { //-- KN
            System.out.println(SEPARATOR);
            System.out.println(table.summarize(Headers.NKV_NO_CHANGE, count, mean, stdDev, min, max)
                .by(Headers.PRIORITY));
            System.out.println(System.lineSeparator() + "Davon NKV < 1:");
            System.out.println(
                tableIndCo2kl1.summarize(Headers.NKV_INDUZ_CO2, count, mean, stdDev, min, max)
                    .by(Headers.PRIORITY));

            System.out.println(SEPARATOR);
            System.out.println(
                table.summarize(Headers.COST_OVERALL, sum, mean, stdDev, min, max)
                    .by(Headers.PRIORITY));
            System.out.println(System.lineSeparator() + "Davon NKV < 1:");
            System.out.println(
                tableIndCo2kl1.summarize(Headers.COST_OVERALL, sum, mean, stdDev, min, max)
                    .by(Headers.PRIORITY));

            System.out.println(SEPARATOR);
            System.out.println(
                table.summarize(
                    Headers.B_CO2_NEU, sum, mean, stdDev, min, max).by(Headers.PRIORITY));
            System.out.println(System.lineSeparator() + "Davon NKV < 1:");
            System.out.println(
                tableIndCo2kl1.summarize(Headers.B_CO2_NEU, sum, mean, stdDev, min, max)
                    .by(Headers.PRIORITY));
        }

        {
            //KMT
            System.out.println(SEPARATOR);
            System.out.println("### KMT ###");
            System.out.println(SEPARATOR);
            System.out.println("All projects");
            System.out.println(table.summarize(Headers.NKV_NO_CHANGE, count).apply());
            System.out.println(System.lineSeparator() + "Davon NKV < 1:");
            System.out.println(tableBaseKl1.summarize(Headers.NKV_NO_CHANGE, count).apply());
            System.out.println(System.lineSeparator() + "Mit CO2 Price und Änderung NKV < 1:");
            System.out.println(tableIndCo2kl1.summarize(Headers.NKV_NO_CHANGE, count).apply());

            System.out.println(SEPARATOR);
            System.out.println(SEPARATOR);
            Table kmtTable = Table.create("Projects with BCR < 1");
            kmtTable.addColumns(DoubleColumn.create("#Projects"
                , new double[]{table.rowCount()}
            ));
            kmtTable.addColumns(DoubleColumn.create("Base"
                , new double[]{tableBaseKl1.rowCount()}
            ));
            kmtTable.addColumns(DoubleColumn.create("CO2_only"
                , new double[]{tableCo2Kl1.rowCount()}
            ));
            kmtTable.addColumns(DoubleColumn.create("induced_only"
                , new double[]{tableIndKl1.rowCount()}
            ));
            kmtTable.addColumns(DoubleColumn.create("CO2_Induced"
                , new double[]{tableIndCo2kl1.rowCount()}
            ));
            System.out.println(kmtTable.print());
        }
    }

}
