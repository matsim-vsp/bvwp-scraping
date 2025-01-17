package org.tub.vsp.bvwp.users.kmt;

import org.tub.vsp.bvwp.BvwpUtils;
import org.tub.vsp.bvwp.data.Headers;
import tech.tablesaw.api.DoubleColumn;
import tech.tablesaw.api.Table;
import tech.tablesaw.io.csv.CsvWriteOptions;
import tech.tablesaw.io.csv.CsvWriter;

import java.util.List;

import static tech.tablesaw.aggregate.AggregateFunctions.sum;

public class TableCalculationsKMT {
  /**
   * Gibt einige Tabellen aus, die für die KMT-Analyse relevant sind. Fokus ist auf den Tabellen mit
   * KNV <
   *
   * @param tbl Tabelle mit den Daten
   * @param headers Tabellenüberschriften, für die die Analyse gemacht werden soll.
   */
  static void printNkvTablesKMT(Table tbl, List<String> headers) {
    // KMT
    // General Todos for all three analysis:
    // Todo: Zeile hinzufügen zu eingsparten Projektanzahl. --> alles in einer Tabelle
    // zusammenfassen??
    // Todo: ! Zeile einfügen, welceh die relative Abweichung zum Base-Case anzeigt... (x%
    // Projekte / Costen sind "raus")

    // Todo: Eigentlich müsste man aus dem folgenden eine Funktion machen können, mit wenigen
    // übergabe-Infos. Weil inhaltlich ist es 3x das gleiche.
    { // Projektanzahl
      Table nkvBelow1_count = Table.create("No. of Projects with BCR < 1 ");
      nkvBelow1_count.addColumns(
          DoubleColumn.create("# of all Projects", new double[] {tbl.rowCount()}));

      // Erstelle eine Spalte für jeden "Fall"
      for (String s : headers) {
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
      Table nkvBelow1_length = Table.create("Projects with BCR < 1 -- saved project length (km)");
      nkvBelow1_length.addColumns(
          DoubleColumn.create(
              "project length of all projects (km)",
              (double) tbl.summarize(Headers.LENGTH, sum).apply().get(0, 0)));

      // Erstelle eine Spalte für jeden "Fall"
      for (String s : headers) {
        Table tblBelow1 = tbl.where(tbl.numberColumn(s).isLessThan(1.));
        nkvBelow1_length.addColumns(
            DoubleColumn.create(
                s, (double) tblBelow1.summarize(Headers.LENGTH, sum).apply().get(0, 0)));
      }
      System.out.println(nkvBelow1_length.print());

      var options =
          CsvWriteOptions.builder("output/NKV_below_1_projectLengthSaved.csv")
              .separator(';')
              .build();
      new CsvWriter().write(nkvBelow1_length, options);
    }

    System.out.println(BvwpUtils.SEPARATOR_AUSGABE);

    { // Gesparte (zusätzliche( Fahrsteifenlänge - km
      Table nkvBelow1_length =
          Table.create(
              "Projects with BCR < 1 -- saved add. lane length (km) -- estimated from project length");
      nkvBelow1_length.addColumns(
          DoubleColumn.create(
              "add. lane length of all projects (km)",
              (double) tbl.summarize(Headers.ADDTL_LANE_KM, sum).apply().get(0, 0)));

      // Erstelle eine Spalte für jeden "Fall"
      for (String s : headers) {
        Table tblBelow1 = tbl.where(tbl.numberColumn(s).isLessThan(1.));
        nkvBelow1_length.addColumns(
            DoubleColumn.create(
                s, (double) tblBelow1.summarize(Headers.ADDTL_LANE_KM, sum).apply().get(0, 0)));
      }
      System.out.println(nkvBelow1_length.print());

      var options =
          CsvWriteOptions.builder("output/NKV_below_1_addLaneLengthSaved.csv")
              .separator(';')
              .build();
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
      for (String s : headers) {
        Table tblBelow1 = tbl.where(tbl.numberColumn(s).isLessThan(1.));
        nkvBelow1_PkwKm.addColumns(
            DoubleColumn.create(
                s, (double) tblBelow1.summarize(Headers.ADDTL_PKWKM_ORIG, sum).apply().get(0, 0)));
      }
      System.out.println(nkvBelow1_PkwKm.print());

      var options =
          CsvWriteOptions.builder("output/NKV_below_1_addPkwKmhSaved.csv").separator(';').build();
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
      for (String s : headers) {
        Table tblBelow1 = tbl.where(tbl.numberColumn(s).isLessThan(1.));
        nkvBelow1_LkwKm.addColumns(
            DoubleColumn.create(
                s, (double) tblBelow1.summarize(Headers.ADDTL_LKWKM_ORIG, sum).apply().get(0, 0)));
      }
      System.out.println(nkvBelow1_LkwKm.print());

      var options =
          CsvWriteOptions.builder("output/NKV_below_1_addLkwKmhSaved.csv").separator(';').build();
      new CsvWriter().write(nkvBelow1_LkwKm, options);
    }

    System.out.println(BvwpUtils.SEPARATOR_AUSGABE);

    { // Gesparte Investitionskosten - Barwert der Kosten in Mio EUR
      Table nkvBelow1_costs =
          Table.create("Projects with BCR < 1 -- saved Investment Costs - Barwert (Mio EUR)");
      nkvBelow1_costs.addColumns(
          DoubleColumn.create(
              "Investment costs of all projects (Mio EUR)",
              (double) tbl.summarize(Headers.INVCOST_BARWERT_ORIG, sum).apply().get(0, 0)));

      // Erstelle eine Spalte für jeden "Fall"
      for (String s : headers) {
        Table tblBelow1 = tbl.where(tbl.numberColumn(s).isLessThan(1.));
        nkvBelow1_costs.addColumns(
            DoubleColumn.create(
                s,
                (double) tblBelow1.summarize(Headers.INVCOST_BARWERT_ORIG, sum).apply().get(0, 0)));
      }
      System.out.println(nkvBelow1_costs.print());

      var options =
          CsvWriteOptions.builder("output/NKV_below_1_costsSaved.csv").separator(';').build();
      new CsvWriter().write(nkvBelow1_costs, options);
    }

    System.out.println(BvwpUtils.SEPARATOR_AUSGABE);

    { // Gesparte CO2 - Emissionen: Aus Verkehr und Lebenszyklusemissionen (t/a)
      Table nkvBelow1_co2safed =
          Table.create(
              "Projects with BCR < 1 -- safed CO2 emissions -- direct and lifecycle of infrastructure (t/a");
      nkvBelow1_co2safed.addColumns(
          DoubleColumn.create(
              "CO2 Emissions of all projects (t/a)",
              (double) tbl.summarize(Headers.CO_2_EQUIVALENTS_EMISSIONS, sum).apply().get(0, 0)));

      // Erstelle eine Spalte für jeden "Fall"
      for (String s : headers) {
        Table tblBelow1 = tbl.where(tbl.numberColumn(s).isLessThan(1.));
        nkvBelow1_co2safed.addColumns(
            DoubleColumn.create(
                s,
                (double)
                    tblBelow1
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
