package org.tub.vsp.bvwp;

import org.tub.vsp.bvwp.computation.ComputationKN;
import org.tub.vsp.bvwp.data.Headers;
import tech.tablesaw.api.Table;
import tech.tablesaw.plotly.components.Axis;
import tech.tablesaw.plotly.components.Axis.Type;
import tech.tablesaw.plotly.components.Figure;
import tech.tablesaw.plotly.components.Layout;
import tech.tablesaw.plotly.components.Marker;
import tech.tablesaw.plotly.traces.ScatterTrace;
import tech.tablesaw.plotly.traces.ScatterTrace.Mode;
import tech.tablesaw.plotly.traces.Trace;

class Figures {
	private static final int plotWidth = 2000;
	private static final String legendFormat = "%30s";
	private final Table table;
	private final Axis xAxis;
	private final String xName;
	Figures( Table table, Axis xAxis, String xName ){
		this.table = table;
		this.xAxis = xAxis;
		this.xName = xName;
	}
	Figure createFigureCost( ){
		Figure figure3;
		String yName = Headers.COST_OVERALL;
		String y3Name = Headers.COST_OVERALL;
		String y2Name = Headers.COST_OVERALL;

		Axis yAxis = Axis.builder()
				 .type( Axis.Type.LOG )
				 //                             .range( 1.1*table.numberColumn( y2Name ).min(),4. )
				 .title( yName )
				 .build();
		Layout layout = Layout.builder( yName )
				      .xAxis( xAxis )
				      .yAxis( yAxis )
				      .width( plotWidth )
				      .build();
		Trace trace = ScatterTrace.builder( table.numberColumn( xName ), table.numberColumn( yName ) )
					  .text( table.stringColumn( Headers.PROJECT_NAME ).asObjectArray() )
					  .name( String.format( legendFormat, yName ) )
					  .build();

		final Trace traceWb = getTraceWb( table, xName, y2Name );
		final Trace traceWbp = getTraceWbp( table, xName, y2Name );
		final Trace traceVb = getTraceVb( table, xName, y2Name );
		final Trace traceVbe = getTraceVbe( table, xName, y2Name );

		Trace trace3 = ScatterTrace.builder( table.numberColumn( xName ), table.numberColumn( y3Name ) )
					   .text( table.stringColumn( Headers.PROJECT_NAME ).asObjectArray() )
					   .name( String.format( legendFormat, y3Name ) )
					   .marker( Marker.builder().color( "gray" ).build() )
					   .build();

		//            double[] xx = new double[]{1., 200.};
		//            Trace trace1 = ScatterTrace.builder( xx, xx )
		//                                       .mode( ScatterTrace.Mode.LINE )
		//                                       .build();

		figure3 = new Figure( layout, trace, trace3, traceWb, traceWbp, traceVb, traceVbe );
		return figure3;
	}
	Figure createFigureNkv( ){
		Figure figure2;
		String yName = Headers.NKV_NO_CHANGE;
		String y3Name = Headers.NKV_CO2;
		String y2Name = Headers.NKV_INDUZ_CO2;

		Axis yAxis = Axis.builder()
//			     .type( Axis.Type.LOG ) // wirft NKV < 0 raus!
				 //                             .range( 1.1*table.numberColumn( y2Name ).min(),4. )
				 .title( yName )
				 .build();
		Layout layout = Layout.builder( yName )
				      .xAxis( xAxis )
				      .yAxis( yAxis )
				      .width( plotWidth )
				      .build();
		Trace trace = ScatterTrace.builder( table.numberColumn( xName ), table.numberColumn( yName ) )
					  .text( table.stringColumn( Headers.PROJECT_NAME ).asObjectArray() )
					  .name( String.format( legendFormat, String.format( "%30s" , yName ) ) )
					  .build();

		final Trace traceWb = getTraceWb( table, xName, y2Name );
		final Trace traceWbp = getTraceWbp( table, xName, y2Name );
		final Trace traceVb = getTraceVb( table, xName, y2Name );
		final Trace traceVbe = getTraceVbe( table, xName, y2Name );


		Trace trace3 = ScatterTrace.builder( table.numberColumn( xName ), table.numberColumn( y3Name ) )
					   .text( table.stringColumn( Headers.PROJECT_NAME ).asObjectArray() )
					   .name( String.format( legendFormat, y3Name ) )
					   .marker( Marker.builder().color( "gray" ).build() )
					   .build();

		double[] xx = new double[]{0., 1.1* table.numberColumn( xName ).max() };
		double[] yy = new double[]{1., 1.};
		Trace trace4 = ScatterTrace.builder( xx, yy )
					   .mode( ScatterTrace.Mode.LINE )
					   .build();

		figure2 = new Figure( layout, trace, trace3, traceWb,traceWbp,  traceVb, traceVbe, trace4 );
		return figure2;
	}
	Figure createFigurePkwKm( ){
		Figure figure;
		String yName = Headers.PKWKM_INDUZ;
		String y2Name = Headers.PKWKM_ALL_NEU;

		Axis yAxis = Axis.builder()
				 .title( yName )
//				 .type( Axis.Type.LOG ) // sonst "0" nicht darstellbar
				 .build();
		Layout layout = Layout.builder( "plot" ).xAxis( xAxis ).yAxis( yAxis )
				      .width( plotWidth )
				      .build();

		Trace trace = ScatterTrace.builder( table.numberColumn( xName ), table.numberColumn( yName ) )
					  .name( String.format( legendFormat, yName ) )
					  .text( table.stringColumn( Headers.PROJECT_NAME ).asObjectArray() )
					  .build();

		final Trace traceWb = getTraceWb( table, xName, y2Name );
		final Trace traceWbp = getTraceWbp( table, xName, y2Name );
		final Trace traceVb = getTraceVb( table, xName, y2Name );
		final Trace traceVbe = getTraceVbe( table, xName, y2Name );

		//            double[] xx = new double[]{1., 200.};
		//            Trace trace1 = ScatterTrace.builder( xx, xx )
		//                                       .mode( ScatterTrace.Mode.LINE )
		//                                       .build();

		figure = new Figure( layout, trace, traceWb, traceWbp, traceVb, traceVbe );
		return figure;
	}
	public Figure createFigureElasticities(){
		String yName = "elasticity_old";
		String y2Name = "elasticity_new";

		table.addColumns( table.numberColumn( Headers.PKWKM_ALL )
				       .divide( ComputationKN.FZKM_AB )
				       .multiply( ComputationKN.LANE_KM_AB )
				       .divide( table.numberColumn( Headers.ADDITIONAL_LANE_KM ) ).setName("elasticity_old"),
				table.numberColumn( Headers.PKWKM_ALL_NEU )
				     .divide( ComputationKN.FZKM_AB )
				     .multiply( ComputationKN.LANE_KM_AB )
				     .divide( table.numberColumn( Headers.ADDITIONAL_LANE_KM ) ).setName("elasticity_new" )
				);

		Axis yAxis = Axis.builder().title( yName )
//				 .type( Axis.Type.LOG )
				 .range( -0.3, 0.8 )
				 .build();

		Layout layout = Layout.builder( "plot" ).xAxis( xAxis ).yAxis( yAxis ).width( plotWidth ).build();

		Trace trace = ScatterTrace.builder( table.numberColumn( xName ), table.numberColumn( yName ) )
					  .name( String.format( legendFormat, yName ) )
					  .text( table.stringColumn( Headers.PROJECT_NAME ).asObjectArray() )
					  .build();

		Trace trace1 = ScatterTrace.builder( new double[]{2,700}, new double[]{ 0.3, 0.3 } )
					   .mode( ScatterTrace.Mode.LINE )
					   .build();
		Trace trace2 = ScatterTrace.builder( new double[]{2,700}, new double[]{ 0.6, 0.6 } )
					   .mode( ScatterTrace.Mode.LINE )
					   .build();

//		final Trace traceWb = getTraceWb( table, xName, y2Name );
//		final Trace traceWbp = getTraceWbp( table, xName, y2Name );
//		final Trace traceVb = getTraceVb( table, xName, y2Name );
//		final Trace traceVbe = getTraceVbe( table, xName, y2Name );


		return new Figure( layout, trace,
//				traceWb, traceWbp, traceVb, traceVbe
				trace1, trace2 );
	}
	public Figure createFigureFzkm(){
		String yName = Headers.PKWKM_ALL;
		String y2Name = Headers.PKWKM_ALL;

		Axis yAxis = Axis.builder().title( yName )
//				 .type( Axis.Type.LOG )
				 .range( 0.,400. )
				 .build();

		Layout layout = Layout.builder( "plot" ).xAxis( xAxis ).yAxis( yAxis ).width( plotWidth ).build();

		Trace trace = ScatterTrace.builder( table.numberColumn( xName ), table.numberColumn( yName ) )
					  .name( String.format( legendFormat, yName ) )
					  .text( table.stringColumn( Headers.PROJECT_NAME ).asObjectArray() )
					  .build();

		final Trace traceWb = getTraceWb( table, xName, y2Name );
		final Trace traceWbp = getTraceWbp( table, xName, y2Name );
		final Trace traceVb = getTraceVb( table, xName, y2Name );
		final Trace traceVbe = getTraceVbe( table, xName, y2Name );

		Trace trace1 = ScatterTrace.builder( new double[]{0,700}, new double[]{ 0 , 700./ComputationKN.LANE_KM_AB * ComputationKN.FZKM_AB*0.3} )
					   .mode( ScatterTrace.Mode.LINE )
					   .build();
		Trace trace2 = ScatterTrace.builder( new double[]{0,700}, new double[]{ 0 , 700./ComputationKN.LANE_KM_AB * ComputationKN.FZKM_AB*0.6} )
					   .mode( ScatterTrace.Mode.LINE )
					   .build();

		return new Figure( layout, trace,
//				traceWb, traceWbp, traceVb, traceVbe,
				trace1, trace2 );
	}

	Figure createFigureCO2( ){
		String yName = Headers.B_CO2_NEU;
		String y2Name = Headers.B_CO2_NEU;

		Axis yAxis = Axis.builder().title( yName ).type( Axis.Type.LOG ).build();

		Layout layout = Layout.builder( "plot" ).xAxis( xAxis ).yAxis( yAxis ).width( plotWidth ).build();

		Trace trace = ScatterTrace.builder( table.numberColumn( xName ), table.numberColumn( yName ) )
					  .name( String.format( legendFormat, yName ) )
					  .text( table.stringColumn( Headers.PROJECT_NAME ).asObjectArray() )
					  .build();

		final Trace traceWb = getTraceWb( table, xName, y2Name );
		final Trace traceWbp = getTraceWbp( table, xName, y2Name );
		final Trace traceVb = getTraceVb( table, xName, y2Name );
		final Trace traceVbe = getTraceVbe( table, xName, y2Name );


		return new Figure( layout, trace, traceWb, traceWbp, traceVb, traceVbe );
	}
	private static Trace getTraceVbe( Table table, String xName, String y2Name ){
		Table tableVbe = table.where( table.stringColumn( Headers.PRIORITY ).isEqualTo( "VBE" ) );
		Trace traceVbe = ScatterTrace.builder( tableVbe.numberColumn( xName ), tableVbe.numberColumn( y2Name ) )
					     .text( tableVbe.stringColumn( Headers.PROJECT_NAME ).asObjectArray() )
					     .name( String.format( legendFormat, y2Name ) )
					     .marker( Marker.builder().color( "red" ).build() )
					     .build();
		return traceVbe;
	}
	private static Trace getTraceVb( Table table, String xName, String y2Name ){
		Table tableVb = table.where( table.stringColumn( Headers.PRIORITY ).isEqualTo( "VB" ) );
		Trace traceVb = ScatterTrace.builder( tableVb.numberColumn( xName ), tableVb.numberColumn( y2Name ) )
					    .text( tableVb.stringColumn( Headers.PROJECT_NAME ).asObjectArray() )
					    .name( String.format( legendFormat, y2Name ) )
					    .marker( Marker.builder().color( "orange" ).build() )
					    .build();
		return traceVb;
	}
	private static Trace getTraceWb( Table table, String xName, String y2Name ){
		Table tableWb = table.where( table.stringColumn( Headers.PRIORITY ).isEqualTo( "WB" ) );
		Trace traceWb = ScatterTrace.builder( tableWb.numberColumn( xName ), tableWb.numberColumn( y2Name ) )
					    .text( tableWb.stringColumn( Headers.PROJECT_NAME ).asObjectArray() )
					    .name( String.format( legendFormat, y2Name ) )
					    .marker( Marker.builder().color( "cyan" ).build() )
					    .build();
		return traceWb;
	}
	private static Trace getTraceWbp( Table table, String xName, String y2Name ){
		Table tableWb = table.where( table.stringColumn( Headers.PRIORITY ).isEqualTo( "WBP" ) );
		Trace traceWb = ScatterTrace.builder( tableWb.numberColumn( xName ), tableWb.numberColumn( y2Name ) )
					    .text( tableWb.stringColumn( Headers.PROJECT_NAME ).asObjectArray() )
					    .name( String.format( legendFormat, y2Name ) )
					    .marker( Marker.builder().color( "yellow" ).build() )
					    .build();
		return traceWb;
	}


  static Figure createFigure3(Axis xAxis, int plotWidth, Table table, String xName){
      Figure figure3;
      String yName = Headers.COST_OVERALL;
      String y3Name = Headers.COST_OVERALL;
      String y2Name = Headers.COST_OVERALL;

      Axis yAxis = Axis.builder()
//                         .type( Axis.Type.LOG )
//                             .range( 1.1*table.numberColumn( y2Name ).min(),4. )
          .title( yName )
          .build();
      Layout layout = Layout.builder( yName )
          .xAxis( xAxis )
          .yAxis( yAxis )
          .width( plotWidth )
          .build();
      Trace trace = ScatterTrace.builder( table.numberColumn( xName ), table.numberColumn( yName ) )
          .text( table.stringColumn( Headers.PROJECT_NAME ).asObjectArray() )
          .name( yName )
          .build();
      Trace trace2 = ScatterTrace.builder( table.numberColumn( xName ), table.numberColumn( y2Name ) )
          .text( table.stringColumn( Headers.PROJECT_NAME ).asObjectArray() )
          .name( y2Name )
          .marker( Marker.builder().color( "red" ).build() )
          .build();
      Trace trace3 = ScatterTrace.builder( table.numberColumn( xName ), table.numberColumn( y3Name ) )
          .text( table.stringColumn( Headers.PROJECT_NAME ).asObjectArray() )
          .name( y3Name )
          .marker( Marker.builder().color( "gray" ).build() )
          .build();

//            double[] xx = new double[]{1., 200.};
//            Trace trace1 = ScatterTrace.builder( xx, xx )
//                                       .mode( ScatterTrace.Mode.LINE )
//                                       .build();

      figure3 = new Figure( layout, trace, trace3, trace2 );
      return figure3;
  }

  static Figure createFigureNkv(Axis xAxis, int plotWidth, Table table, String xName){
      Figure figure2;
      String yName = Headers.NKV_NO_CHANGE;
      String y3Name = Headers.NKV_CO2;
//        String y2Name = Headers.NKV_INDUZ_CO2;

      Axis yAxis = Axis.builder()
          .type( Type.LINEAR )
          .range( Double.min(
                  1.1*table.numberColumn(y3Name).min(), 1.1*table.numberColumn( yName ).min()),
              20. )
          .title( yName )
          .build();
      Layout layout = Layout.builder( yName )
          .xAxis( xAxis )
          .yAxis( yAxis )
          .width( plotWidth )
          .build();
      Trace trace = ScatterTrace.builder( table.numberColumn( xName ), table.numberColumn( yName ) )
          .text( table.stringColumn( Headers.PROJECT_NAME ).asObjectArray() )
          .name( yName )
          .build();
//        Trace trace2 = ScatterTrace.builder( table.numberColumn( xName ), table.numberColumn( y2Name ) )
//                                   .text( table.stringColumn( Headers.PROJECT_NAME ).asObjectArray() )
//                                   .name( y2Name )
//                                   .marker( Marker.builder().color( "red" ).build() )
//                                   .build();
      Trace trace3 = ScatterTrace.builder( table.numberColumn( xName ), table.numberColumn( y3Name ) )
          .text( table.stringColumn( Headers.PROJECT_NAME ).asObjectArray() )
          .name( y3Name )
          .marker( Marker.builder().color( "gray" ).build() )
          .build();

      double[] xx = new double[]{0., 1.1* table.numberColumn( xName ).max() };
      double[] yy = new double[]{1., 1.};
      Trace trace4 = ScatterTrace.builder( xx, yy )
          .mode( Mode.LINE )
          .marker(Marker.builder().color( "red" ).build())
          .build();

//        figure2 = new Figure( layout, trace, trace3, trace2, trace4 );
      figure2 = new Figure( layout, trace, trace3, trace4 );
      return figure2;
  }

  static Figure createFigurePkwKm_KMT(Axis xAxis, Table table, String xName){
      Figure figure;
      String yName = Headers.PKWKM_INDUZ;
      String y2Name = Headers.PKWKM_INDUZ_NEU;

      Axis yAxis = Axis.builder()
          .title( yName )
          .type( Type.LOG )
          .build();
      Layout layout = Layout.builder( "plot" ).xAxis( xAxis ).yAxis( yAxis )
          .width( plotWidth )
          .build();


      Trace trace = ScatterTrace.builder( table.numberColumn( xName ), table.numberColumn( yName ) )
          .name( yName )
          .text( table.stringColumn( Headers.PROJECT_NAME ).asObjectArray() )
          .build();
      Trace trace2 = ScatterTrace.builder( table.numberColumn( xName ), table.numberColumn( y2Name ) )
          .name( y2Name )
          .text( table.stringColumn( Headers.PROJECT_NAME ).asObjectArray() )
          .marker( Marker.builder().color( "red" ).build() )
          .build();

//            double[] xx = new double[]{1., 200.};
//            Trace trace1 = ScatterTrace.builder( xx, xx )
//                                       .mode( ScatterTrace.Mode.LINE )
//                                       .build();

      figure = new Figure( layout, trace, trace2 );
      return figure;
  }

  static Figure createFigureCO2(Axis xAxis, int plotWidth, Table table, String xName){
      String yName = Headers.B_CO2_NEU;
      String y2Name = Headers.B_CO2_NEU;

      Axis yAxis = Axis.builder()
          .title( yName )
//                         .type( Axis.Type.LOG )
          .build();
      Layout layout = Layout.builder( "plot" ).xAxis( xAxis ).yAxis( yAxis )
          .width( plotWidth )
          .build();


      Trace trace = ScatterTrace.builder( table.numberColumn( xName ), table.numberColumn( yName ) )
          .name( yName )
          .text( table.stringColumn( Headers.PROJECT_NAME ).asObjectArray() )
          .build();
      Trace trace2 = ScatterTrace.builder( table.numberColumn( xName ), table.numberColumn( y2Name ) )
          .name( y2Name )
          .text( table.stringColumn( Headers.PROJECT_NAME ).asObjectArray() )
          .marker( Marker.builder().color( "red" ).build() )
          .build();

//            double[] xx = new double[]{1., 200.};
//            Trace trace1 = ScatterTrace.builder( xx, xx )
//                                       .mode( ScatterTrace.Mode.LINE )
//                                       .build();

      return new Figure( layout, trace, trace2 );
  }

  static Figure createFigureNkvChange(int plotWidth, Table table, String xName, String yName) {

//        String xName = Headers.NKV_NO_CHANGE;
//        String yName = Headers.NKV_CO2;
      double maxX = 20.;
      double maxY = 20.;

      Axis xAxis = Axis.builder()
          .type( Type.LINEAR )
          .title( xName )
          .range(0. ,maxX)
//                             .autoRange( Axis.AutoRange.REVERSED );
          .build();

      table = table.sortDescendingOn( xName );

      Axis yAxis = Axis.builder()
          .type( Type.LINEAR )
          .range(Double.min(0., 1.1*table.numberColumn(yName).min()) ,maxY)
//                             .range( 1.1*table.numberColumn( y2Name ).min(),4. )
          .title( yName )
          .build();

      Layout layout = Layout.builder( xName )
          .xAxis( xAxis )
          .yAxis( yAxis )
          .width( plotWidth )
          .build();

      Trace cbrOverCbrTrace = ScatterTrace.builder( table.numberColumn( xName ), table.numberColumn( yName ) )
          .text( table.stringColumn( Headers.PROJECT_NAME ).asObjectArray() )
          .name( yName )
          .marker( Marker.builder().color( "blue" ).build() )
          .build();

//        double[] xx = new double[]{0., 1.1* table.numberColumn( xName ).max() };
//        double[] yy = new double[]{0., 1.1* table.numberColumn( xName ).max()};
      double[] xx = new double[]{0., maxX };
      double[] yy = new double[]{0., maxY};
      double[] xy1 = new double[]{1., 1.};

      Trace diagonale = ScatterTrace.builder( xx, yy )
          .name(xName + " = " + yName)
          .mode( Mode.LINE )
          .build();

      Trace horizontalCbr1 = ScatterTrace.builder( xx, xy1 )
          .name(yName + " = 1")
          .mode( Mode.LINE )
          .marker( Marker.builder().color( "gray" ).build() )
          .build();

      Trace verticalCbr1 = ScatterTrace.builder( xy1, yy )
          .name(xName + " = 1")
          .mode( Mode.LINE )
          .marker( Marker.builder().color( "gray" ).build() )
          .build();


      return new Figure( layout, cbrOverCbrTrace, diagonale, horizontalCbr1, verticalCbr1 );
  }
}
