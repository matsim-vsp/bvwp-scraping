package org.tub.vsp.bvwp.users.kn;

import org.tub.vsp.bvwp.computation.ComputationKN;
import org.tub.vsp.bvwp.data.Headers;
import tech.tablesaw.api.DoubleColumn;
import tech.tablesaw.api.Table;
import tech.tablesaw.plotly.components.Axis;
import tech.tablesaw.plotly.components.Figure;
import tech.tablesaw.plotly.components.Layout;
import tech.tablesaw.plotly.components.Marker;
import tech.tablesaw.plotly.traces.ScatterTrace;
import tech.tablesaw.plotly.traces.Trace;

class FiguresKN{
	private static final int plotWidth = 2000;
	private static final String legendFormat = "%30s";
	private final Table table;
	private final Axis xAxis;
	private final String xName;
	FiguresKN( Table table ){
		Axis.AxisBuilder xAxisBuilder = Axis.builder();
		// ---------------------------------------------------------------
        {
//		xName = Headers.B_CO2_NEU;
//		xName = Headers.COST_OVERALL;
		xName = Headers.VERKEHRSBELASTUNG_2030;
        }
		// ---------------------------------------------------------------
		// ---------------------------------------------------------------
//		{
//			xName = Headers.NKV_NO_CHANGE;
//			xName = Headers.NKV_INDUZ_CO2;
//            xName = Headers.NKV_DIFF;
//            xName = Headers.ADDITIONAL_LANE_KM;
//			xAxisBuilder
//					.type( Axis.Type.LOG )
////                            .autoRange( Axis.AutoRange.REVERSED )
//                            .range(0,1)
//			;
//		}
		// ---------------------------------------------------------------

		switch( xName ){
			case Headers.NKV_NO_CHANGE
//					     , Headers.VERKEHRSBELASTUNG_2030
					-> xAxisBuilder.type( Axis.Type.LOG );
		}


		table = table.sortAscendingOn( xName );
		this.xAxis = xAxisBuilder.title( xName ).build();

		DoubleColumn newColumn = DoubleColumn.create( Headers.VERKEHRSBELASTUNG_2030 );
		for( Double value : table.doubleColumn( Headers.VERKEHRSBELASTUNG_2030 ) ){
			newColumn.append( value - 500. + 1000. * Math.random() ); // randomize so that they are not on top of each other in plotly
		}
		table.removeColumns( Headers.VERKEHRSBELASTUNG_2030 );
		table.addColumns( newColumn );

		this.table = table;
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

		final Trace traceWb = getTraceCyan( table, xName, y2Name );
		final Trace traceWbp = getTraceMagenta( table, xName, y2Name );
		final Trace traceVb = getTraceOrange( table, xName, y2Name );
		final Trace traceVbe = getTraceRed( table, xName, y2Name );

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
		String yName = Headers.NKV_INDUZ_CO2;
//		String y3Name = Headers.NKV_CO2;
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

		final Trace traceCyan = getTraceCyan( table, xName, y2Name );
		final Trace traceMagenta = getTraceMagenta( table, xName, y2Name );
		final Trace traceOrange = getTraceOrange( table, xName, y2Name );
		final Trace traceRed = getTraceRed( table, xName, y2Name );


//		Trace trace3 = ScatterTrace.builder( table.numberColumn( xName ), table.numberColumn( y3Name ) )
//					   .text( table.stringColumn( Headers.PROJECT_NAME ).asObjectArray() )
//					   .name( String.format( legendFormat, y3Name ) )
//					   .marker( Marker.builder().color( "gray" ).build() )
//					   .build();

		double[] xx = new double[]{0., 1.1* table.numberColumn( xName ).max() };
		double[] yy = new double[]{1., 1.};
		Trace trace4 = ScatterTrace.builder( xx, yy )
					   .mode( ScatterTrace.Mode.LINE )
					   .build();

		figure2 = new Figure( layout
//				, trace
//				, trace3
				, traceCyan,traceMagenta
//				,  traceOrange, traceRed
				, trace4 );
		return figure2;
	}
	Figure createFigurePkwKm( ){
		Figure figure;
		String yName = Headers.PKWKM_INDUZ;
		String y2Name = Headers.PKWKM_ALL_NEU;

		Axis yAxis = Axis.builder()
				 .title( yName )
				 .type( Axis.Type.LOG ) // sonst "0" nicht darstellbar
				 .build();
		Layout layout = Layout.builder( "plot" ).xAxis( xAxis ).yAxis( yAxis )
				      .width( plotWidth )
				      .build();

		Trace trace = ScatterTrace.builder( table.numberColumn( xName ), table.numberColumn( yName ) )
					  .name( String.format( legendFormat, yName ) )
					  .text( table.stringColumn( Headers.PROJECT_NAME ).asObjectArray() )
					  .build();

		final Trace traceWb = getTraceCyan( table, xName, y2Name );
		final Trace traceWbp = getTraceMagenta( table, xName, y2Name );
		final Trace traceVb = getTraceOrange( table, xName, y2Name );
		final Trace traceVbe = getTraceRed( table, xName, y2Name );

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

		double xMin = table.numberColumn( xName ).min();
		double xMax = table.numberColumn( xName ).max();

		Axis yAxis = Axis.builder().title( yName )
//				 .type( Axis.Type.LOG )
//				 .range( -0.3, 0.8 )
				 .build();

		Layout layout = Layout.builder( "plot" ).xAxis( xAxis ).yAxis( yAxis ).width( plotWidth ).build();

		Trace trace = ScatterTrace.builder( table.numberColumn( xName ), table.numberColumn( yName ) )
					  .name( String.format( legendFormat, yName ) )
					  .text( table.stringColumn( Headers.PROJECT_NAME ).asObjectArray() )
					  .build();

		Trace trace1 = ScatterTrace.builder( new double[]{xMin,xMax}, new double[]{ 0.2, 0.2 } )
					   .mode( ScatterTrace.Mode.LINE )
					   .build();
		Trace trace2 = ScatterTrace.builder( new double[]{xMin, xMax}, new double[]{ 0.6, 0.6 } )
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
		String y2Name = Headers.PKWKM_ALL;
		String yName = Headers.PKWKM_ALL_NEU;

		Axis yAxis = Axis.builder().title( yName )
				 .type( Axis.Type.LOG )
//				 .range( 0.,400. )
				 .build();

		Layout layout = Layout.builder( "plot" ).xAxis( xAxis ).yAxis( yAxis ).width( plotWidth ).build();

		Trace trace = ScatterTrace.builder( table.numberColumn( xName ), table.numberColumn( yName ) )
					  .name( String.format( legendFormat, yName ) )
					  .text( table.stringColumn( Headers.PROJECT_NAME ).asObjectArray() )
					  .build();

		final Trace traceWb = getTraceCyan( table, xName, y2Name );
		final Trace traceWbp = getTraceMagenta( table, xName, y2Name );
		final Trace traceVb = getTraceOrange( table, xName, y2Name );
		final Trace traceVbe = getTraceRed( table, xName, y2Name );

//		Trace trace1 = ScatterTrace.builder( new double[]{1,700}, new double[]{ 1 , 700./ComputationKN.LANE_KM_AB * ComputationKN.FZKM_AB*0.3} )
//					   .mode( ScatterTrace.Mode.LINE )
//					   .build();
//		Trace trace2 = ScatterTrace.builder( new double[]{1,700}, new double[]{ 1 , 700./ComputationKN.LANE_KM_AB * ComputationKN.FZKM_AB*0.6} )
//					   .mode( ScatterTrace.Mode.LINE )
//					   .build();

		return new Figure( layout, trace
				,traceWb, traceWbp, traceVb, traceVbe
//				,trace1, trace2
		);
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

		final Trace traceWb = getTraceCyan( table, xName, y2Name );
		final Trace traceWbp = getTraceMagenta( table, xName, y2Name );
		final Trace traceVb = getTraceOrange( table, xName, y2Name );
		final Trace traceVbe = getTraceRed( table, xName, y2Name );


		return new Figure( layout, trace, traceWb, traceWbp, traceVb, traceVbe );
	}
	public Figure createFigureDtv(){
		String yName = Headers.VERKEHRSBELASTUNG_2030;
		String y2Name = Headers.VERKEHRSBELASTUNG_2030;

		Axis yAxis = Axis.builder().title( yName ).type( Axis.Type.LOG ).build();

		Layout layout = Layout.builder( "plot" ).xAxis( xAxis ).yAxis( yAxis ).width( plotWidth ).build();

		Trace trace = ScatterTrace.builder( table.numberColumn( xName ), table.numberColumn( yName ) )
					  .name( String.format( legendFormat, yName ) )
					  .text( table.stringColumn( Headers.PROJECT_NAME ).asObjectArray() )
					  .build();

		final Trace traceWb = getTraceCyan( table, xName, y2Name );
		final Trace traceWbp = getTraceMagenta( table, xName, y2Name );
		final Trace traceVb = getTraceOrange( table, xName, y2Name );
		final Trace traceVbe = getTraceRed( table, xName, y2Name );


		return new Figure( layout, trace
				, traceWb
				,traceWbp
				, traceVb
				, traceVbe
		);
	}
	private static Trace getTraceRed( Table table, String xName, String y2Name ){
		Table tableVbe = table.where( table.stringColumn( Headers.BAUTYP ).isIn( "EW8" ) );
		Trace traceVbe = ScatterTrace.builder( tableVbe.numberColumn( xName ), tableVbe.numberColumn( y2Name ) )
					     .text( tableVbe.stringColumn( Headers.PROJECT_NAME ).asObjectArray() )
					     .name( String.format( legendFormat, y2Name ) )
					     .marker( Marker.builder().color( "red" ).build() )
					     .build();
		return traceVbe;
	}
	private static Trace getTraceOrange( Table table, String xName, String y2Name ){
		Table tableVb = table.where( table.stringColumn( Headers.BAUTYP ).isIn( "EW6" ) );
		Trace traceVb = ScatterTrace.builder( tableVb.numberColumn( xName ), tableVb.numberColumn( y2Name ) )
					    .text( tableVb.stringColumn( Headers.PROJECT_NAME ).asObjectArray() )
					    .name( String.format( legendFormat, y2Name ) )
					    .marker( Marker.builder().color( "orange" ).build() )
					    .build();
		return traceVb;
	}
	private static Trace getTraceMagenta( Table table, String xName, String y2Name ){
		Table tableWb = table.where( table.stringColumn( Headers.BAUTYP ).containsString( "NB" ) );
		Trace traceWb = ScatterTrace.builder( tableWb.numberColumn( xName ), tableWb.numberColumn( y2Name ) )
					    .text( tableWb.stringColumn( Headers.PROJECT_NAME ).asObjectArray() )
					    .name( String.format( legendFormat, y2Name ) )
					    .marker( Marker.builder().color( "magenta" ).build() )
					    .build();
		return traceWb;
	}
	private static Trace getTraceCyan( Table table, String xName, String y2Name ){
		Table tableWb = table.where( table.stringColumn( Headers.BAUTYP ).containsString( "KNOTENPUNKT" ) ) ;
		Trace traceWb = ScatterTrace.builder( tableWb.numberColumn( xName ), tableWb.numberColumn( y2Name ) )
					    .text( tableWb.stringColumn( Headers.PROJECT_NAME ).asObjectArray() )
					    .name( String.format( legendFormat, y2Name ) )
					    .marker( Marker.builder().color( "cyan" ).build() )
					    .build();
		return traceWb;
	}
}
