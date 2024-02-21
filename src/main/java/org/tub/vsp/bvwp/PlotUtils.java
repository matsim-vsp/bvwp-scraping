package org.tub.vsp.bvwp;

import org.tub.vsp.bvwp.data.Headers;
import tech.tablesaw.api.Table;
import tech.tablesaw.plotly.components.Axis;
import tech.tablesaw.plotly.components.Figure;
import tech.tablesaw.plotly.components.Layout;
import tech.tablesaw.plotly.components.Marker;
import tech.tablesaw.plotly.traces.ScatterTrace;
import tech.tablesaw.plotly.traces.Trace;

class PlotUtils{
	private static final int plotWidth = 2000;
	private static final String legendFormat = "%30s";
	private PlotUtils(){} // do not instantiate
	static Figure createFigureCost( Axis xAxis, Table table, String xName ){
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
	static Figure createFigureNkv( Axis xAxis, Table table, String xName ){
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
	static Figure createFigurePkwKm( Axis xAxis, Table table, String xName ){
		Figure figure;
		String yName = Headers.PKWKM_INDUZ;
		String y2Name = Headers.PKWKM_INDUZ_NEU;

		Axis yAxis = Axis.builder()
				 .title( yName )
				 .type( Axis.Type.LOG )
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
	static Figure createFigureCO2( Axis xAxis, Table table, String xName ){
		String yName = Headers.B_CO2_NEU;
		String y2Name = Headers.B_CO2_NEU;

		Axis yAxis = Axis.builder()
				 .title( yName )
				 .type( Axis.Type.LOG )
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
}
