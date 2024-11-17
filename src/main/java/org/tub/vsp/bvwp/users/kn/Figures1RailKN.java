package org.tub.vsp.bvwp.users.kn;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.tub.vsp.bvwp.computation.ComputationKN;
import org.tub.vsp.bvwp.data.Headers;
import org.tub.vsp.bvwp.data.HeadersKN;
import org.tub.vsp.bvwp.data.type.Einstufung;
import tech.tablesaw.api.DoubleColumn;
import tech.tablesaw.api.NumericColumn;
import tech.tablesaw.api.Table;
import tech.tablesaw.columns.Column;
import tech.tablesaw.io.csv.CsvWriteOptions;
import tech.tablesaw.io.csv.CsvWriter;
import tech.tablesaw.plotly.components.Axis;
import tech.tablesaw.plotly.components.Figure;
import tech.tablesaw.plotly.components.Layout;
import tech.tablesaw.plotly.traces.ScatterTrace;
import tech.tablesaw.plotly.traces.Trace;

import java.io.File;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import static org.tub.vsp.bvwp.data.Headers.*;
import static org.tub.vsp.bvwp.users.kn.Figures1KN.*;

class Figures1RailKN{
	private static final Logger log = LogManager.getLogger( Figures1RailKN.class );
	static final int plotWidth = 2000;
	static final String legendFormat = "%30s";
	static final String dotSizeString = "largest = VB-E; large = VB; small = WB*; tiny = WB";
	final Table table;
	private final Axis xAxis;
	private final String xName;
	final double nkvCappedMax;
	final double nkvMin;
	private static boolean init = true ;
	Figures1RailKN( Table table, String xName ){
		this.xName = xName;
		Axis.AxisBuilder xAxisBuilder = Axis.builder();
		// ---------------------------------------------------------------
        {
//		xName = Headers.B_CO2_NEU;
//		xName = INVCOST_ORIG;
//		xName = Headers.VERKEHRSBELASTUNG_PLANFALL;
        }
		// ---------------------------------------------------------------
		// ---------------------------------------------------------------
//		{
//			this.xName = NKV_ORIG_CAPPED5;
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

//		switch( xName ){
//			case NKV_ORIG
//					     , Headers.VERKEHRSBELASTUNG_2030
//					-> xAxisBuilder.type( Axis.Type.LOG );
				// wirft NKV<0 raus, was bei "orig" nicht passiert, aber dann doch, wenn man andere NKVs auf die y-Achse
				// plottet, und dort nun auch logscale verwenden sollte, um etwas zu sehen.
//		}

//		xAxisBuilder.type( Axis.Type.LOG );


		if ( init ){
			init = false;

			// ===========================
			table.addColumns(
//					table.numberColumn( NKV_EL03_CARBON215_INVCOSTTUD ).subtract( table.numberColumn( NKV_ORIG ) ).setName( NKV_EL03_DIFF )
//					, table.numberColumn( ADDTL_PKWKM_EL03 ).subtract( table.numberColumn( ADDTL_PKWKM_ORIG ) ).setName( ADDTL_PKWKM_EL03_DIFF )
					);
			// ===========================
			// ===========================
//			Headers.addCap5( table, NKV_EL03_CARBON215_INVCOSTTUD );
//			Headers.addCap5( table, NKV_EL03_CARBON700tpr0_INVCOSTTUD );
//			Headers.addCap5( table, NKV_ELTTIME_CARBON215_INVCOSTTUD );
//			Headers.addCap5( table, NKV_ELTTIME_CARBON700TPR0_INVCOSTTUD );
			Headers.addCap5( table, HeadersKN.NKV_ORIG );
//			Headers.addCap5( table, NKV_EL03 );
//			Headers.addCap5( table, NKV_EL03_CARBON700tpr0 );
			Headers.addCap5( table, HeadersKN.NKV_CARBON700 );
//			Headers.addCap5( table, NKV_ELTTIME_CARBON2000_INVCOSTTUD );

//			Headers.addCap( 10, table, NKV_EL03_CARBON215_INVCOSTTUD );
			// ===========================
			// ===========================
			{
				DoubleColumn column = DoubleColumn.create( EINSTUFUNG_AS_NUMBER );
				final double factor = 8.;
				final double offset = 8.;
				for( String prio : table.stringColumn( EINSTUFUNG ) ){
					switch( Einstufung.valueOf( prio ) ){
						case VBE -> {
							column.append( 4. * factor + offset );
						}
						case VB -> {
							column.append( 3. * factor + offset );
						}
						case WBP -> {
							column.append( 2. * factor + offset );
						}
						case WB -> {
							column.append( 1. * factor + offset );
						}
						case KB -> {
							column.append( offset );
						}
//					case UNDEFINED -> {
//						column.append( 2. );
//					}
						default -> {
//							column.append( 2. );
						throw new IllegalStateException( "Unexpected value: " + prio );
						}
					}
				}
				table.addColumns( column );
			}
			// ===========================

			NumberFormat nf = NumberFormat.getInstance( Locale.GERMAN );
			for( Column<?> column : table.columns() ){
				if ( column instanceof NumericColumn ) {
					((NumericColumn<?>) column).setPrintFormatter( nf, "" );
				}
			}

			new CsvWriter().write( table, CsvWriteOptions.builder( new File( "output.tsv" ) ).separator( '\t' ).usePrintFormatters( true ).build() );

		}

		if ( this.xName != null ){
			if ( this.xName.contains( "NKV" ) ) {
				xAxisBuilder.autoRange( Axis.AutoRange.REVERSED );
			} else{
			}
		}

		this.xAxis = xAxisBuilder.title( this.xName )
//					 .type( Axis.Type.LOG )
					 .build();

		this.table = table;

		final String NKV_ORIG_CAPPED5 = Headers.addCap( 5, table, HeadersKN.NKV_ORIG );
		nkvCappedMax = table.doubleColumn( NKV_ORIG_CAPPED5 ).max() + 0.2 ;
//		nkvMin = table.doubleColumn( NKV_EL03_CARBON215_INVCOSTTUD_CAPPED5 ).min();
		nkvMin = table.doubleColumn( HeadersKN.NKV_CARBON700 ).min();



	}
	// ========================================================================================
	// ========================================================================================
	Figure nkv_el03_diff(){
		String yName = NKV_EL03_DIFF;
//		String y3Name = Headers.NKV_CO2;
		String y2Name = NKV_EL03_DIFF;

		Axis yAxis = Axis.builder()
//			     .type( Axis.Type.LOG ) // wirft NKV < 0 raus!
				 //                             .range( 1.1*table.numberColumn( y2Name ).min(),4. )
				 .title( yName )
				 .build();

		String title = "";
		if ( ADDTL_LANE_KM.equals( xName ) ){
			title = "since (co2_cost SIMTO lane-km) and (cost SIMTO lane-km), the division of the two does not depend on lane-km:";
		}
		Layout layout = Layout.builder( title )
				      .xAxis( xAxis )
				      .yAxis( yAxis )
				      .width( plotWidth )
				      .build();

		List<Trace> traces = new ArrayList<>( getTracesByColor( table, xName, y2Name ) );

//		Trace trace = ScatterTrace.builder( table.numberColumn( xName ), table.numberColumn( yName ) )
//					  .text( table.stringColumn( Headers.PROJECT_NAME ).asObjectArray() )
//					  .name( String.format( legendFormat, String.format( "%30s" , yName ) ) )
//					  .build();

//		Trace trace3 = ScatterTrace.builder( table.numberColumn( xName ), table.numberColumn( y3Name ) )
//					   .text( table.stringColumn( Headers.PROJECT_NAME ).asObjectArray() )
//					   .name( String.format( legendFormat, y3Name ) )
//					   .marker( Marker.builder().color( "gray" ).build() )
//					   .build();

//		double[] xx = new double[]{0., 1.1* table.numberColumn( xName ).max() };
//		double[] yy = new double[]{1., 1.};
//		Trace trace4 = ScatterTrace.builder( xx, yy )
//					   .mode( ScatterTrace.Mode.LINE )
//					   .build();

		Figure figure2 = new Figure( layout, traces.toArray(new Trace[]{} ) );
		return figure2;
	}
	// ========================================================================================
	// ========================================================================================
	Figure nkv_el03(){
		String yName = NKV_EL03_CAPPED5;
		String y2Name = NKV_EL03_CAPPED5;

		Axis yAxis = Axis.builder()
//			     .type( Axis.Type.LOG ) // wirft NKV < 0 raus!
				 //                             .range( 1.1*table.numberColumn( y2Name ).min(),4. )
				 .title( yName )
				 .build();

		Layout layout = Layout.builder( "" ).xAxis( xAxis ).yAxis( yAxis ).width( plotWidth ).build();

		List<Trace> traces = new ArrayList<>( getTracesByColor( table, xName, y2Name ) );

//		Trace trace = ScatterTrace.builder( table.numberColumn( xName ), table.numberColumn( yName ) )
//					  .text( table.stringColumn( Headers.PROJECT_NAME ).asObjectArray() )
//					  .name( String.format( legendFormat, String.format( "%30s" , yName ) ) )
//					  .build();

//		Trace trace3 = ScatterTrace.builder( table.numberColumn( xName ), table.numberColumn( y3Name ) )
//					   .text( table.stringColumn( Headers.PROJECT_NAME ).asObjectArray() )
//					   .name( String.format( legendFormat, y3Name ) )
//					   .marker( Marker.builder().color( "gray" ).build() )
//					   .build();

//		double[] xx = new double[]{0., 1.1* table.numberColumn( xName ).max() };
//		double[] yy = new double[]{1., 1.};
//		Trace trace4 = ScatterTrace.builder( xx, yy )
//					   .mode( ScatterTrace.Mode.LINE )
//					   .build();

		Figure figure2 = new Figure( layout, traces.toArray(new Trace[]{} ) );
		return figure2;
	}
	// ========================================================================================
	// ========================================================================================
	Figure nkv_orig(){
		final String NKV_ORIG_CAPPED5 = Headers.addCap( 5, table, HeadersKN.NKV_ORIG );

		String yName = NKV_ORIG_CAPPED5;
		String y2Name = NKV_ORIG_CAPPED5;

		Axis yAxis = Axis.builder()
//			     .type( Axis.Type.LOG ) // wirft NKV < 0 raus!
				 //                             .range( 1.1*table.numberColumn( y2Name ).min(),4. )
				 .title( yName )
				 .build();

		Layout layout = Layout.builder( "" ).xAxis( xAxis ).yAxis( yAxis ).width( plotWidth ).build();

		List<Trace> traces = new ArrayList<>();

		traces.addAll( getTraces( xName, y2Name, table, "null", "cyan" ) );

		Figure figure2 = new Figure( layout, traces.toArray(new Trace[0]) );
		return figure2;
	}
	// ========================================================================================
	// ========================================================================================
	Figure invCost_orig(){
		String yName = INVCOST_BARWERT_ORIG;
		String y2Name = yName;

		Axis yAxis = Axis.builder()
//			     .type( Axis.Type.LOG ) // wirft NKV < 0 raus!
				 //                             .range( 1.1*table.numberColumn( y2Name ).min(),4. )
				 .title( yName )
				 .build();

		Layout layout = Layout.builder( "" ).xAxis( xAxis ).yAxis( yAxis ).width( plotWidth ).build();

		List<Trace> traces = new ArrayList<>();

		final List<Trace> traceWb = getTraces( xName, y2Name, table, "null", "cyan" );
		traces.addAll( traceWb );

		Figure figure2 = new Figure( layout, traces.toArray(new Trace[0]) );
		return figure2;
	}
	// ========================================================================================
	// ========================================================================================
	Figure invCost_orig_cumulative(){
		String yName = INVCOST_BARWERT_ORIG;
		String y2Name = yName;

		Table table2 = Table.create( table.stringColumn( PROJECT_NAME ), table.doubleColumn( INVCOST_BARWERT_ORIG ), table.doubleColumn( xName ) ).sortDescendingOn( xName );

		table2 = table2.sortDescendingOn( xName ); // necessary to get cumulative cost right

		DoubleColumn cumulativeCost = DoubleColumn.create( "cumulative_cost" );
		{
			double sum = 0.;
			for( Double cost : table2.doubleColumn( INVCOST_BARWERT_ORIG ) ){
				sum += cost;
				cumulativeCost.append( sum );
			}
		}

		Axis yAxis = Axis.builder().title( "Kumulierte Investitionskosten (orig) [Mio]" ).build();

		Layout layout = Layout.builder( "" ).xAxis( xAxis ).yAxis( yAxis ).width( plotWidth ).build();

		List<Trace> traces = new ArrayList<>();

		var nameInLegend = "cumulative cost";
		traces.add( ScatterTrace.builder( table2.doubleColumn( xName ), cumulativeCost ).mode( ScatterTrace.Mode.LINE ).showLegend( true ).name( String.format( legendFormat, nameInLegend ) ).build() );

		return new Figure( layout, traces.toArray(new Trace[]{} ) );
	}
	// ========================================================================================
	// ========================================================================================
	Figure nkv_carbon700(){
		String yName = NKV_CARBON700_CAPPED5;
		String y2Name = NKV_CARBON700_CAPPED5;

		Axis yAxis = Axis.builder()
//			     .type( Axis.Type.LOG ) // wirft NKV < 0 raus!
				 //                             .range( 1.1*table.numberColumn( y2Name ).min(),4. )
				 .title( yName )
				 .build();

		Layout layout = Layout.builder( "" ).xAxis( xAxis ).yAxis( yAxis ).width( plotWidth ).build();

		List<Trace> traces = new ArrayList<>();

		final List<Trace> traceWb = getTraces( xName, y2Name, table, "null", "cyan" );
		traces.addAll( traceWb );

		Figure figure2 = new Figure( layout, traces.toArray(new Trace[0]) );
		return figure2;
	}
	// ========================================================================================
	// ========================================================================================
	Figure nkv_el03_carbon700(){
		String yName = NKV_EL03_CARBON700ptpr0;
		String y2Name = NKV_EL03_CARBON700_CAPPED5;

		Axis yAxis = Axis.builder()
//			     .type( Axis.Type.LOG ) // wirft NKV < 0 raus!
				 //                             .range( 1.1*table.numberColumn( y2Name ).min(),4. )
				 .title( yName )
				 .build();

		String title = "";
		if ( ADDTL_LANE_KM.equals( xName ) ){
			title = "since (co2_cost SIMTO lane-km) and (cost SIMTO lane-km), the division of the two does not depend on lane-km:";
		}
		Layout layout = Layout.builder( title )
				      .xAxis( xAxis )
				      .yAxis( yAxis )
				      .width( plotWidth )
				      .build();

		List<Trace> traces = new ArrayList<>( getTracesByColor( table, xName, y2Name ) );

//		Trace trace = ScatterTrace.builder( table.numberColumn( xName ), table.numberColumn( yName ) )
//					  .text( table.stringColumn( Headers.PROJECT_NAME ).asObjectArray() )
//					  .name( String.format( legendFormat, String.format( "%30s" , yName ) ) )
//					  .build();

//		Trace trace3 = ScatterTrace.builder( table.numberColumn( xName ), table.numberColumn( y3Name ) )
//					   .text( table.stringColumn( Headers.PROJECT_NAME ).asObjectArray() )
//					   .name( String.format( legendFormat, y3Name ) )
//					   .marker( Marker.builder().color( "gray" ).build() )
//					   .build();

//		double[] xx = new double[]{0., 1.1* table.numberColumn( xName ).max() };
//		double[] yy = new double[]{1., 1.};
//		Trace trace4 = ScatterTrace.builder( xx, yy )
//					   .mode( ScatterTrace.Mode.LINE )
//					   .build();

		Figure figure2 = new Figure( layout, traces.toArray(new Trace[]{} ) );
		return figure2;
	}
	// ========================================================================================
	// ========================================================================================
	Figure createFigurePkwKm( ){
		String yName = ADDTL_PKWKM_INDUZ_ORIG;
		String y2Name = ADDTL_PKWKM_EL03;

		Axis yAxis = Axis.builder()
				 .title( yName )
				 .type( Axis.Type.LOG ) // sonst "0" nicht darstellbar
				 .build();
		Layout layout = Layout.builder( "" ).xAxis( xAxis ).yAxis( yAxis )
				      .width( plotWidth )
				      .build();

		List<Trace> traces = new ArrayList<>( getTracesByColor( table, xName, y2Name ) );

		traces.add( ScatterTrace.builder( table.numberColumn( xName ), table.numberColumn( yName ) )
					  .name( String.format( legendFormat, yName ) )
					  .text( table.stringColumn( PROJECT_NAME ).asObjectArray() )
					  .build() );
		//            double[] xx = new double[]{1., 200.};
		//            Trace trace1 = ScatterTrace.builder( xx, xx )
		//                                       .mode( ScatterTrace.Mode.LINE )
		//                                       .build();

		Figure figure = new Figure( layout, traces.toArray(new Trace[]{} ) );
		return figure;
	}
	// ========================================================================================
	// ========================================================================================
	public Figure elasticities(){
		String yName = "elasticity_old";
		String y2Name = "elasticity_new";

		table.addColumns( table.numberColumn( ADDTL_PKWKM_ORIG )
				       .divide( ComputationKN.FZKM_AB )
				       .multiply( ComputationKN.LANE_KM_AB )
				       .divide( table.numberColumn( ADDTL_LANE_KM ) ).setName("elasticity_old" ),
				table.numberColumn( ADDTL_PKWKM_EL03 )
				     .divide( ComputationKN.FZKM_AB )
				     .multiply( ComputationKN.LANE_KM_AB )
				     .divide( table.numberColumn( ADDTL_LANE_KM ) ).setName("elasticity_new" )
				);

		double xMin = table.numberColumn( xName ).min();
		double xMax = table.numberColumn( xName ).max();

		Axis yAxis = Axis.builder().title( yName )
//				 .type( Axis.Type.LOG )
//				 .range( -0.3, 0.8 )
				 .build();

		String title = "";
		if ( ADDTL_LANE_KM.equals( xName ) ){
			title = "elasticities are indep of lane-km:";
		}
		Layout layout = Layout.builder( title ).xAxis( xAxis ).yAxis( yAxis ).width( plotWidth ).build();

		Trace trace = ScatterTrace.builder( table.numberColumn( xName ), table.numberColumn( yName ) )
					  .name( String.format( legendFormat, yName ) )
					  .text( table.stringColumn( PROJECT_NAME ).asObjectArray() )
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
	// ========================================================================================
	// ========================================================================================
	public Figure fzkm(){
		String y2Name = ADDTL_PKWKM_EL03;
		String yName = ADDTL_PKWKM_ORIG;

		Axis yAxis = Axis.builder().title( yName )
				 .type( Axis.Type.LOG )
//				 .range( 0.,400. )
				 .build();

		Layout layout = Layout.builder( "" ).xAxis( xAxis ).yAxis( yAxis ).width( plotWidth ).build();

		List<Trace> traces = getTracesByColor( table, xName, y2Name );

		traces.add( ScatterTrace.builder( table.numberColumn( xName ), table.numberColumn( yName ) )
					  .name( String.format( legendFormat, yName ) )
					  .text( table.stringColumn( PROJECT_NAME ).asObjectArray() )
					  .build() );

//		Trace trace1 = ScatterTrace.builder( new double[]{1,700}, new double[]{ 1 , 700./ComputationKN.LANE_KM_AB * ComputationKN.FZKM_AB*0.3} )
//					   .mode( ScatterTrace.Mode.LINE )
//					   .build();
//		Trace trace2 = ScatterTrace.builder( new double[]{1,700}, new double[]{ 1 , 700./ComputationKN.LANE_KM_AB * ComputationKN.FZKM_AB*0.6} )
//					   .mode( ScatterTrace.Mode.LINE )
//					   .build();

		return new Figure( layout, traces.toArray(new Trace[]{} ) );
	}

	// ========================================================================================
	// ========================================================================================
	Figure carbon(){
		String yName = B_CO2_ORIG;
		String y2Name = CO2_COST_EL03;

		Axis yAxis = Axis.builder().title( yName ).type( Axis.Type.LOG ).build();

		String title = "";
		if ( ADDTL_LANE_KM.equals( xName ) ) {
			title = "CO2 costs SIMTO addtl lane-km:";
		}

		Layout layout = Layout.builder( title ).xAxis( xAxis ).yAxis( yAxis ).width( plotWidth ).build();

		List<Trace> traces = getTracesByColor( table, xName, y2Name );

		traces.add( ScatterTrace.builder( table.numberColumn( xName ), table.numberColumn( yName ) )
					  .name( String.format( legendFormat, yName ) )
					  .text( table.stringColumn( PROJECT_NAME ).asObjectArray() )
					  .build() );

		return new Figure( layout, traces.toArray(new Trace[]{} ) );
	}
	// ========================================================================================
	// ========================================================================================
	public Figure dtv(){
		String yName = VERKEHRSBELASTUNG_PLANFALL;
		String y2Name = VERKEHRSBELASTUNG_PLANFALL;

		Axis yAxis = Axis.builder().title( yName ).type( Axis.Type.LOG ).build();

		Layout layout = Layout.builder( "" ).xAxis( xAxis ).yAxis( yAxis ).width( plotWidth ).build();

		List<Trace> traces = getTracesByColor( table, xName, y2Name );

//		Trace trace = ScatterTrace.builder( table.numberColumn( xName ), table.numberColumn( yName ) )
//					  .name( String.format( legendFormat, yName ) )
//					  .text( table.stringColumn( Headers.PROJECT_NAME ).asObjectArray() )
//					  .build();

		return new Figure( layout, traces.toArray(new Trace[]{} ) );
	}
	public Figure fzkmNew(){
		String yName = ADDTL_PKWKM_ORIG;
		String y2Name = ADDTL_PKWKM_EL03;

		Axis yAxis = Axis.builder().title( y2Name ).type( Axis.Type.LOG ).build();

		String title = "";
		if ( ADDTL_LANE_KM.equals( xName ) ){
			title = "addtl_veh-km are PROPTO addtl_lane-km:";
		}

		Layout layout = Layout.builder( title ).xAxis( xAxis ).yAxis( yAxis ).width( plotWidth ).build();

		List<Trace> traces = getTracesByColor( table, xName, y2Name );

		traces.add( ScatterTrace.builder( table.numberColumn( xName ), table.numberColumn( yName ) )
					  .name( String.format( legendFormat, yName ) )
					  .text( table.stringColumn( PROJECT_NAME ).asObjectArray() )
					  .build() );

		return new Figure( layout, traces.toArray(new Trace[]{} ) );
	}
	// ========================================================================================
	// ========================================================================================
	public Figure fzkmDiff(){
		String y2Name = ADDTL_PKWKM_EL03_DIFF;

		Axis yAxis = Axis.builder().title( y2Name ).type( Axis.Type.LOG ).build();

		String title = "";
		if ( ADDTL_LANE_KM.equals( xName ) ){
			title = "addtl_veh-km_diff are SIM addtl_lane-km:";
		}

		Layout layout = Layout.builder( title ).xAxis( xAxis ).yAxis( yAxis ).width( plotWidth ).build();

		List<Trace> traces = getTracesByColor( table, xName, y2Name );

//		Trace trace = ScatterTrace.builder( table.numberColumn( xName ), table.numberColumn( yName ) )
//					  .name( String.format( legendFormat, yName ) )
//					  .text( table.stringColumn( Headers.PROJECT_NAME ).asObjectArray() )
//					  .build();

		return new Figure( layout, traces.toArray(new Trace[]{} ) );

	}


	// ========================================================================================
}
