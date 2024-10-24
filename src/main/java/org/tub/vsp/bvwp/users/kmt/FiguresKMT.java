package org.tub.vsp.bvwp.users.kmt;

import org.tub.vsp.bvwp.BvwpUtils;
import org.tub.vsp.bvwp.data.Headers;
import org.tub.vsp.bvwp.data.type.Einstufung;
import tech.tablesaw.api.Table;
import tech.tablesaw.plotly.components.Axis;
import tech.tablesaw.plotly.components.Axis.Type;
import tech.tablesaw.plotly.components.Figure;
import tech.tablesaw.plotly.components.Layout;
import tech.tablesaw.plotly.components.Marker;
import tech.tablesaw.plotly.traces.ScatterTrace;
import tech.tablesaw.plotly.traces.ScatterTrace.Mode;
import tech.tablesaw.plotly.traces.Trace;

class FiguresKMT {

  private static final String legendFormat = "%30s";

  // Do not instanciate
  FiguresKMT() {}

  private static Trace getPriorityTrace(
      Table table, Einstufung priority, String xName, String yName, String color) {
    Table tableVbe = BvwpUtils.extractPriorityTable(table, priority.name());
    return ScatterTrace.builder(tableVbe.numberColumn(xName), tableVbe.numberColumn(yName))
        .text(tableVbe.stringColumn(Headers.PROJECT_NAME).asObjectArray())
        .name(String.format(legendFormat, yName + "_" + priority.name()))
        .marker(Marker.builder().color(color).build())
        .build();
  }

  // #########################

  static Figure createFigureCostByPriority(int plotWidth, Table table, String xName) {
    Figure figure3;
    String yName = Headers.INVCOST_ORIG;
    String y3Name = Headers.INVCOST_ORIG;
    String y2Name = Headers.INVCOST_ORIG;

    double maxX = table.numberColumn(xName).max();
    Axis xAxis =
        Axis.builder()
            .type(Type.LINEAR)
            .title(xName)
            .range(0., maxX)
            //                             .autoRange( Axis.AutoRange.REVERSED );
            .build();

    Axis yAxis =
        Axis.builder()
            .type(Type.LOG)
            //                             .range( 1.1*table.numberColumn( y2Name ).min(),4. )
            .title(yName)
            .build();
    Layout layout = Layout.builder(yName).xAxis(xAxis).yAxis(yAxis).width(plotWidth).build();
    Trace trace =
        ScatterTrace.builder(table.numberColumn(xName), table.numberColumn(yName))
            .text(table.stringColumn(Headers.PROJECT_NAME).asObjectArray())
            .name(String.format(legendFormat, yName))
            .build();

    final Trace traceWb = getPriorityTrace(table, Einstufung.WB, xName, y2Name, "cyan");
    final Trace traceWbp = getPriorityTrace(table, Einstufung.WBP, xName, y2Name, "yellow");
    final Trace traceVb = getPriorityTrace(table, Einstufung.VB, xName, y2Name, "orange");
    final Trace traceVbe = getPriorityTrace(table, Einstufung.VBE, xName, y2Name, "red");

    Trace trace3 =
        ScatterTrace.builder(table.numberColumn(xName), table.numberColumn(y3Name))
            .text(table.stringColumn(Headers.PROJECT_NAME).asObjectArray())
            .name(String.format(legendFormat, y3Name))
            .marker(Marker.builder().color("gray").build())
            .build();

    //            double[] xx = new double[]{1., 200.};
    //            Trace trace1 = ScatterTrace.builder( xx, xx )
    //                                       .mode( ScatterTrace.Mode.LINE )
    //                                       .build();

    figure3 = new Figure(layout, trace, trace3, traceWb, traceWbp, traceVb, traceVbe);
    return figure3;
  }

  static Figure createFigureNkvByPriority(Axis xAxis, int plotWidth, Table table, String xName) {
    Figure figure2;
    String yName = Headers.NKV_ORIG_EN;
    //		String y3Name = Headers.NKV_CO2_680_EN;
    String y2Name = Headers.NKV_EL03_CARBON215_INVCOSTTUD;

    Axis yAxis =
        Axis.builder()
            //			     .type( Axis.Type.LOG ) // wirft NKV < 0 raus!
            //                             .range( 1.1*table.numberColumn( y2Name ).min(),4. )
            .title(yName)
            .build();
    Layout layout = Layout.builder(yName).xAxis(xAxis).yAxis(yAxis).width(plotWidth).build();
    Trace trace =
        ScatterTrace.builder(table.numberColumn(xName), table.numberColumn(yName))
            .text(table.stringColumn(Headers.PROJECT_NAME).asObjectArray())
            .name(String.format(legendFormat, String.format("%30s", yName)))
            .build();

    final Trace traceWb = getPriorityTrace(table, Einstufung.WB, xName, y2Name, "cyan");
    final Trace traceWbp = getPriorityTrace(table, Einstufung.WBP, xName, y2Name, "yellow");
    final Trace traceVb = getPriorityTrace(table, Einstufung.VB, xName, y2Name, "orange");
    final Trace traceVbe = getPriorityTrace(table, Einstufung.VBE, xName, y2Name, "red");

    //		Trace trace3 = ScatterTrace.builder( table.numberColumn( xName ), table.numberColumn( y3Name
    // ) )
    //					   .text( table.stringColumn( Headers.PROJECT_NAME ).asObjectArray() )
    //					   .name( String.format( legendFormat, y3Name ) )
    //					   .marker( Marker.builder().color( "gray" ).build() )
    //					   .build();

    double[] xx = new double[] {0., 1.1 * table.numberColumn(xName).max()};
    double[] yy = new double[] {1., 1.};
    Trace trace4 = ScatterTrace.builder(xx, yy).mode(ScatterTrace.Mode.LINE).build();

    figure2 =
        new Figure(
            layout, trace
            //				, trace3
            , traceWb, traceWbp, traceVb, traceVbe, trace4);
    return figure2;
  }

  static Figure createFigureNkv(Axis xAxis, int plotWidth, Table table, String xName) {
    Figure figure2;
    String yName = Headers.NKV_ORIG_EN;
    String y3Name = Headers.NKV_CO2_700_EN;
    //        String y2Name = Headers.NKV_INDUZ_CO2;

    Axis yAxis =
        Axis.builder()
            .type(Type.LINEAR)
            .range(
                Double.min(
                    1.1 * table.numberColumn(y3Name).min(), 1.1 * table.numberColumn(yName).min()),
                20.)
            .title(yName)
            .build();
    Layout layout = Layout.builder(yName).xAxis(xAxis).yAxis(yAxis).width(plotWidth).build();
    Trace trace =
        ScatterTrace.builder(table.numberColumn(xName), table.numberColumn(yName))
            .text(table.stringColumn(Headers.PROJECT_NAME).asObjectArray())
            .name(yName)
            .build();

    Trace trace3 =
        ScatterTrace.builder(table.numberColumn(xName), table.numberColumn(y3Name))
            .text(table.stringColumn(Headers.PROJECT_NAME).asObjectArray())
            .name(y3Name)
            .marker(Marker.builder().color("gray").build())
            .build();

    double[] xx = new double[] {0., 1.1 * table.numberColumn(xName).max()};
    double[] yy = new double[] {1., 1.};
    Trace trace4 =
        ScatterTrace.builder(xx, yy)
            .mode(Mode.LINE)
            .marker(Marker.builder().color("red").build())
            .build();

    figure2 = new Figure(layout, trace, trace3, trace4);
    return figure2;
  }

  static Figure createFigureCO2(Axis xAxis, int plotWidth, Table table, String xName) {
    String yName = Headers.CO2_COST_EL03;
    String y2Name = Headers.CO2_COST_EL03;

    Axis yAxis =
        Axis.builder()
            .title(yName)
            //                         .type( Axis.Type.LOG )
            .build();
    Layout layout = Layout.builder("plot").xAxis(xAxis).yAxis(yAxis).width(plotWidth).build();

    Trace trace =
        ScatterTrace.builder(table.numberColumn(xName), table.numberColumn(yName))
            .name(yName)
            .text(table.stringColumn(Headers.PROJECT_NAME).asObjectArray())
            .build();
    Trace trace2 =
        ScatterTrace.builder(table.numberColumn(xName), table.numberColumn(y2Name))
            .name(y2Name)
            .text(table.stringColumn(Headers.PROJECT_NAME).asObjectArray())
            .marker(Marker.builder().color("red").build())
            .build();

    return new Figure(layout, trace, trace2);
  }

  static Figure createFigureNkvChange(int plotWidth, Table table, String xName, String yName) {

    //        String xName = Headers.NKV_NO_CHANGE;
    //        String yName = Headers.NKV_CO2;
    double maxX = 20.;
    double maxY = 20.;

    Axis xAxis =
        Axis.builder()
            .type(Type.LINEAR)
            .title(xName)
            .range(0., maxX)
            //                             .autoRange( Axis.AutoRange.REVERSED );
            .build();

    table = table.sortDescendingOn(xName);

    Axis yAxis =
        Axis.builder()
            .type(Type.LINEAR)
            .range(Double.min(0., 1.1 * table.numberColumn(yName).min()), maxY)
            //                             .range( 1.1*table.numberColumn( y2Name ).min(),4. )
            .title(yName)
            .build();

    Layout layout =
        Layout.builder(yName + " over " + xName).xAxis(xAxis).yAxis(yAxis).width(plotWidth).build();

    Trace cbrOverCbrTrace =
        ScatterTrace.builder(table.numberColumn(xName), table.numberColumn(yName))
            .text(table.stringColumn(Headers.PROJECT_NAME).asObjectArray())
            .name(yName)
            .marker(Marker.builder().color("blue").build())
            .build();

    //        double[] xx = new double[]{0., 1.1* table.numberColumn( xName ).max() };
    //        double[] yy = new double[]{0., 1.1* table.numberColumn( xName ).max()};
    double[] xx = new double[] {0., maxX};
    double[] yy = new double[] {0., maxY};
    double[] xy1 = new double[] {1., 1.};

    Trace diagonale =
        ScatterTrace.builder(xx, yy)
            .name(xName + " = " + yName)
            .mode(Mode.LINE)
            .marker(Marker.builder().color("gray").build())
            .build();

    Trace horizontalCbr1 =
        ScatterTrace.builder(xx, xy1)
            .name(yName + " = 1")
            .mode(Mode.LINE)
            .marker(Marker.builder().color("gray").build())
            .build();

    Trace verticalCbr1 =
        ScatterTrace.builder(xy1, yy)
            .name(xName + " = 1")
            .mode(Mode.LINE)
            .marker(Marker.builder().color("gray").build())
            .build();

    return new Figure(layout, cbrOverCbrTrace, diagonale, horizontalCbr1, verticalCbr1);
  }

  /**
   * Für NKV vergleich-Plot mit 2 Y-Achsen.
   *
   * @param plotWidth
   * @param table
   * @param xName
   * @param yName
   * @param yName2
   * @return
   */
  static Figure createFigureNkvChange(
      int plotWidth, Table table, String xName, String yName, String yName2) {

    //        String xName = Headers.NKV_NO_CHANGE;
    //        String yName = Headers.NKV_CO2;
    double maxX = 20.;
    double maxY = 20.;

    Axis xAxis =
        Axis.builder()
            .type(Type.LINEAR)
            .title(xName)
            .range(0., maxX)
            //                             .autoRange( Axis.AutoRange.REVERSED );
            .build();

    table = table.sortDescendingOn(xName);

    Axis yAxis =
        Axis.builder()
            .type(Type.LINEAR)
            .range(Double.min(0., 1.1 * table.numberColumn(yName).min()), maxY)
            //                             .range( 1.1*table.numberColumn( y2Name ).min(),4. )
            .title(yName)
            .build();

    Axis yAxis2 =
        Axis.builder()
            .type(Type.LINEAR)
            .range(Double.min(0., 1.1 * table.numberColumn(yName2).min()), maxY)
            //                             .range( 1.1*table.numberColumn( y2Name ).min(),4. )
            .title(yName2)
            .build();

    Layout layout =
        Layout.builder(yName + " and " + yName2 + " over " + xName)
            .xAxis(xAxis)
            .yAxis(yAxis)
            .yAxis2(yAxis2)
            .width(plotWidth)
            .build();

    Trace cbrOverCbrTrace1 =
        ScatterTrace.builder(table.numberColumn(xName), table.numberColumn(yName))
            .text(table.stringColumn(Headers.PROJECT_NAME).asObjectArray())
            .name(yName)
            .marker(Marker.builder().color("blue").build())
            .build();

    Trace cbrOverCbrTrace2 =
        ScatterTrace.builder(table.numberColumn(xName), table.numberColumn(yName2))
            .text(table.stringColumn(Headers.PROJECT_NAME).asObjectArray())
            .name(yName2)
            .marker(Marker.builder().color("red").build())
            .build();

    //        double[] xx = new double[]{0., 1.1* table.numberColumn( xName ).max() };
    //        double[] yy = new double[]{0., 1.1* table.numberColumn( xName ).max()};
    double[] xx = new double[] {0., maxX};
    double[] yy = new double[] {0., maxY};
    double[] xy1 = new double[] {1., 1.};

    Trace diagonale =
        ScatterTrace.builder(xx, yy)
            .name(xName + " = " + yName)
            .mode(Mode.LINE)
            .marker(Marker.builder().color("orange").build())
            .build();

    Trace horizontalCbr1 =
        ScatterTrace.builder(xx, xy1)
            .name(yName + " = 1")
            .mode(Mode.LINE)
            .marker(Marker.builder().color("gray").build())
            .build();

    Trace verticalCbr1 =
        ScatterTrace.builder(xy1, yy)
            .name(xName + " = 1")
            .mode(Mode.LINE)
            .marker(Marker.builder().color("gray").build())
            .build();

    return new Figure(
        layout, cbrOverCbrTrace1, cbrOverCbrTrace2, diagonale, horizontalCbr1, verticalCbr1);
  }

  /**
   * Idee um eine Trennung zu erzeugen... Noch nicht optimal
   *
   * @param title
   * @return
   */
  static Figure createTextFigure(String title) {

    Layout layout =
        Layout.builder(title)
            .yAxis(Axis.builder().visible(false).build())
            .xAxis(Axis.builder().visible(false).build())
            .width(1)
            .height(1)
            .build();

    return new Figure(layout);
  }
}
