package org.tub.vsp.bvwp.users.kn;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.tub.vsp.bvwp.computation.ComputationKN;
import org.tub.vsp.bvwp.data.Headers;
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
import tech.tablesaw.plotly.components.Marker;
import tech.tablesaw.plotly.traces.ScatterTrace;
import tech.tablesaw.plotly.traces.Trace;

import java.io.File;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import static org.tub.vsp.bvwp.data.Headers.*;

class FiguresKN{
	private static final Logger log = LogManager.getLogger( FiguresKN.class );
	private static final int plotWidth = 2000;
	private static final String legendFormat = "%30s";
	private final Table table;
	private final Axis xAxis;
	private final String xName;
	private final double nkvCappedMax;
	private final double nkvMin;
	FiguresKN( Table table ){
		Axis.AxisBuilder xAxisBuilder = Axis.builder();
		// ---------------------------------------------------------------
        {
//		xName = Headers.B_CO2_NEU;
		xName = COST_OVERALL;
//		xName = Headers.VERKEHRSBELASTUNG_PLANFALL;
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
			case NKV_ORIG
//					     , Headers.VERKEHRSBELASTUNG_2030
					-> xAxisBuilder.type( Axis.Type.LOG );
		}

//		xAxisBuilder.type( Axis.Type.LOG );


//        table = table.where( table.numberColumn( Headers.NKV_INDUZ_CO2 ).isLessThan( 2.) );

		// ===========================
		table.addColumns(
				table.numberColumn( NKV_EL03_CARBON215_INVCOST50 ).subtract( table.numberColumn( NKV_ORIG ) ).setName( NKV_EL03_DIFF )
				,table.numberColumn( ADDTL_PKWKM_EL03 ).subtract( table.numberColumn( ADDTL_PKWKM_ORIG ) ).setName( ADDTL_PKWKM_EL03_DIFF )
				);
		// ===========================
		// ===========================
		final double cap=5.;
		{
			DoubleColumn newColumn = DoubleColumn.create( NKV_EL03_CARBON215_INVCOST50_CAPPED5 );
			for( Double number : table.doubleColumn( NKV_EL03_CARBON215_INVCOST50 ) ){
				number = Math.min( number,cap - Math.random()*0.1 + 0.05 );
				newColumn.append( number );
			}
			table.addColumns( newColumn );
		}
		// ===========================
		// ===========================
		{
			DoubleColumn newColumn = DoubleColumn.create( NKV_EL03_CARBON215_INVCOST50_CAPPED10 );
			for( Double number : table.doubleColumn( NKV_EL03_CARBON215_INVCOST50 ) ){
				number = Math.min( number,10 - Math.random()*0.1 + 0.05 );
				newColumn.append( number );
			}
			table.addColumns( newColumn );
		}
		// ===========================
		// ===========================
		{
			DoubleColumn newColumn = DoubleColumn.create( NKV_ORIG_CAPPED5 );
			for( Double number : table.doubleColumn( NKV_ORIG ) ){
				number = Math.min( number, cap - Math.random()*0.1 + 0.05);
				newColumn.append( number );
			}
			table.addColumns( newColumn );
		}
		// ===========================
		// ===========================
		{
			DoubleColumn newColumn = DoubleColumn.create( VERKEHRSBELASTUNG_PLANFALL );
			for( Double value : table.doubleColumn( VERKEHRSBELASTUNG_PLANFALL ) ){
				newColumn.append( value + 2000. * Math.random() ); // randomize so that they are not on top of each other in plotly.  Deliberately not centered to that values are > 0.
			}
			table.removeColumns( VERKEHRSBELASTUNG_PLANFALL );
			table.addColumns( newColumn );
		}
		// ===========================
		// ===========================
		{
			DoubleColumn column = DoubleColumn.create( EINSTUFUNG_AS_NUMBER );
			final double factor = 8.;
			final double offset = 3.;
			for( String prio : table.stringColumn( EINSTUFUNG ) ){
				switch( Einstufung.valueOf( prio ) ){
					case VBE -> {
						column.append( 3. * factor + offset );
					}
					case VB -> {
						column.append( 2. * factor + offset );
					}
					case WBP -> {
						column.append( 1. * factor + offset );
					}
					case WB -> {
						column.append( offset );
					}
//					case UNDEFINED -> {
//						column.append( 2. );
//					}
					default -> {
						column.append( 2. );
//						throw new IllegalStateException( "Unexpected value: " + prio );
					}
				}
			}
			table.addColumns( column );
		}
		// ===========================



		table = table.sortAscendingOn( xName );

		this.xAxis = xAxisBuilder.title( xName ).type( Axis.Type.LOG ).build();

		this.table = table;

		nkvCappedMax = table.doubleColumn( NKV_ORIG_CAPPED5 ).max() + 0.2 ;
		nkvMin = table.doubleColumn( NKV_EL03_CARBON215_INVCOST50_CAPPED5 ).min();

		NumberFormat nf = NumberFormat.getInstance( Locale.GERMAN );

		for( Column<?> column : table.columns() ){
			if ( column instanceof NumericColumn ) {
				((NumericColumn) column).setPrintFormatter( nf, "" );
			}
		}

		new CsvWriter().write( table, CsvWriteOptions.builder( new File( "output.tsv" ) ).separator( '\t' ).usePrintFormatters( true ).build() );

	}
	// ################################################################
	// ################################################################
	Figure createFigureCostVsLanekm(){
		String yName = COST_OVERALL;
//		String y3Name = Headers.COST_OVERALL;
		String y2Name = COST_OVERALL;

		String title = "";
		if ( ADDTL_LANE_KM.equals( xName ) ) {
			title = "construction cost SIMTO lane-km, but quite varied:";
		}

		Axis yAxis = Axis.builder()
				 .type( Axis.Type.LOG )
				 //                             .range( 1.1*table.numberColumn( y2Name ).min(),4. )
				 .title( yName )
				 .build();
		Layout layout = Layout.builder( title )
				      .xAxis( xAxis )
				      .yAxis( yAxis )
				      .width( plotWidth )
				      .build();
//		Trace trace = ScatterTrace.builder( table.numberColumn( xName ), table.numberColumn( yName ) )
//					  .text( table.stringColumn( Headers.PROJECT_NAME ).asObjectArray() )
//					  .name( String.format( legendFormat, yName ) )
//					  .build();

		final Trace traceWb = getTraceCyan( table, xName, y2Name );
		final Trace traceWbp = getTraceMagenta( table, xName, y2Name );
		final Trace traceVb = getTraceOrange( table, xName, y2Name );
		final Trace traceVbe = getTraceRed( table, xName, y2Name );

//		final Trace trace3 = getTrace( xName, y3Name, table, y3Name, "gray" );

		//            double[] xx = new double[]{1., 200.};
		//            Trace trace1 = ScatterTrace.builder( xx, xx )
		//                                       .mode( ScatterTrace.Mode.LINE )
		//                                       .build();

		Figure figure3 = new Figure( layout
//				, trace, trace3
				, traceWb, traceWbp, traceVb, traceVbe );
		return figure3;
	}
	// ========================================================================================
	// ========================================================================================
	Figure createFigureNkvVsLanekm(){
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
//		Trace trace = ScatterTrace.builder( table.numberColumn( xName ), table.numberColumn( yName ) )
//					  .text( table.stringColumn( Headers.PROJECT_NAME ).asObjectArray() )
//					  .name( String.format( legendFormat, String.format( "%30s" , yName ) ) )
//					  .build();

		final Trace traceCyan = getTraceCyan( table, xName, y2Name );
		final Trace traceMagenta = getTraceMagenta( table, xName, y2Name );
		final Trace traceOrange = getTraceOrange( table, xName, y2Name );
		final Trace traceRed = getTraceRed( table, xName, y2Name );


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

		Figure figure2 = new Figure( layout
//				, trace
//				, trace3
				, traceCyan, traceMagenta
				, traceOrange, traceRed
//				, trace4
		);
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

		Trace trace = ScatterTrace.builder( table.numberColumn( xName ), table.numberColumn( yName ) )
					  .name( String.format( legendFormat, yName ) )
					  .text( table.stringColumn( PROJECT_NAME ).asObjectArray() )
					  .build();

		final Trace traceWb = getTraceCyan( table, xName, y2Name );
		final Trace traceWbp = getTraceMagenta( table, xName, y2Name );
		final Trace traceVb = getTraceOrange( table, xName, y2Name );
		final Trace traceVbe = getTraceRed( table, xName, y2Name );

		//            double[] xx = new double[]{1., 200.};
		//            Trace trace1 = ScatterTrace.builder( xx, xx )
		//                                       .mode( ScatterTrace.Mode.LINE )
		//                                       .build();

		Figure figure = new Figure( layout, trace, traceWb, traceWbp, traceVb, traceVbe );
		return figure;
	}
	// ========================================================================================
	// ========================================================================================
	public Figure createFigureElasticities(){
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
	public Figure createFigureFzkm(){
		String y2Name = ADDTL_PKWKM_EL03;
		String yName = ADDTL_PKWKM_ORIG;

		Axis yAxis = Axis.builder().title( yName )
				 .type( Axis.Type.LOG )
//				 .range( 0.,400. )
				 .build();

		Layout layout = Layout.builder( "" ).xAxis( xAxis ).yAxis( yAxis ).width( plotWidth ).build();

		Trace trace = ScatterTrace.builder( table.numberColumn( xName ), table.numberColumn( yName ) )
					  .name( String.format( legendFormat, yName ) )
					  .text( table.stringColumn( PROJECT_NAME ).asObjectArray() )
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

	// ========================================================================================
	// ========================================================================================
	Figure createFigureCO2(){
		String yName = CO2_COST_ORIG;
		String y2Name = CO2_COST_NEU;

		Axis yAxis = Axis.builder().title( yName ).type( Axis.Type.LOG ).build();

		String title = "";
		if ( ADDTL_LANE_KM.equals( xName ) ) {
			title = "CO2 costs SIMTO addtl lane-km:";
		}

		Layout layout = Layout.builder( title ).xAxis( xAxis ).yAxis( yAxis ).width( plotWidth ).build();

		Trace trace = ScatterTrace.builder( table.numberColumn( xName ), table.numberColumn( yName ) )
					  .name( String.format( legendFormat, yName ) )
					  .text( table.stringColumn( PROJECT_NAME ).asObjectArray() )
					  .build();

		final Trace traceWb = getTraceCyan( table, xName, y2Name );
		final Trace traceWbp = getTraceMagenta( table, xName, y2Name );
		final Trace traceVb = getTraceOrange( table, xName, y2Name );
		final Trace traceVbe = getTraceRed( table, xName, y2Name );


		return new Figure( layout
				, trace
				, traceWb, traceWbp, traceVb, traceVbe );
	}
	// ========================================================================================
	// ========================================================================================
	Figure co2_vs_nkv_new(){
		String xName = NKV_EL03_CARBON215_INVCOST50_CAPPED5;

		Axis xAxis = Axis.builder().title(xName).autoRange( Axis.AutoRange.REVERSED )
				 .showZeroLine( false )
				 .zeroLineWidth( 0 )
				 .zeroLineColor( "lightgray" )
				 .range( nkvCappedMax, nkvMin )
				 .build();


		String yName = CO2_COST_ORIG;
		String y2Name = CO2_COST_NEU;

		Axis yAxis = Axis.builder().title( yName ).build();

		String title = "";
		if ( ADDTL_LANE_KM.equals( xName ) ) {
			title = "CO2 costs SIMTO addtl lane-km:";
		}

		Layout layout = Layout.builder( title ).xAxis( xAxis ).yAxis( yAxis ).width( plotWidth ).build();

		Trace trace = ScatterTrace.builder( table.numberColumn( xName ), table.numberColumn( yName ) )
					  .name( String.format( legendFormat, yName ) )
					  .text( table.stringColumn( PROJECT_NAME ).asObjectArray() )
					  .build();

		final Trace traceWb = getTraceCyan( table, xName, y2Name );
		final Trace traceWbp = getTraceMagenta( table, xName, y2Name );
		final Trace traceVb = getTraceOrange( table, xName, y2Name );
		final Trace traceVbe = getTraceRed( table, xName, y2Name );


		return new Figure( layout
				, trace
				, traceWb, traceWbp, traceVb, traceVbe );
	}
	// ========================================================================================
	// ========================================================================================
	public Figure createFigureDtv(){
		String yName = VERKEHRSBELASTUNG_PLANFALL;
		String y2Name = VERKEHRSBELASTUNG_PLANFALL;

		Axis yAxis = Axis.builder().title( yName ).type( Axis.Type.LOG ).build();

		Layout layout = Layout.builder( "" ).xAxis( xAxis ).yAxis( yAxis ).width( plotWidth ).build();

//		Trace trace = ScatterTrace.builder( table.numberColumn( xName ), table.numberColumn( yName ) )
//					  .name( String.format( legendFormat, yName ) )
//					  .text( table.stringColumn( Headers.PROJECT_NAME ).asObjectArray() )
//					  .build();

		final Trace traceWb = getTraceCyan( table, xName, y2Name );
		final Trace traceWbp = getTraceMagenta( table, xName, y2Name );
		final Trace traceVb = getTraceOrange( table, xName, y2Name );
		final Trace traceVbe = getTraceRed( table, xName, y2Name );


		return new Figure( layout
//				, trace
				, traceWb
				,traceWbp
				, traceVb
				, traceVbe
		);
	}
	public Figure createFigureFzkmNew(){
		String yName = ADDTL_PKWKM_ORIG;
		String y2Name = ADDTL_PKWKM_EL03;

		Axis yAxis = Axis.builder().title( y2Name ).type( Axis.Type.LOG ).build();

		String title = "";
		if ( ADDTL_LANE_KM.equals( xName ) ){
			title = "addtl_veh-km are PROPTO addtl_lane-km:";
		}

		Layout layout = Layout.builder( title ).xAxis( xAxis ).yAxis( yAxis ).width( plotWidth ).build();

		Trace trace = ScatterTrace.builder( table.numberColumn( xName ), table.numberColumn( yName ) )
					  .name( String.format( legendFormat, yName ) )
					  .text( table.stringColumn( PROJECT_NAME ).asObjectArray() )
					  .build();

		final Trace traceWb = getTraceCyan( table, xName, y2Name );
		final Trace traceWbp = getTraceMagenta( table, xName, y2Name );
		final Trace traceVb = getTraceOrange( table, xName, y2Name );
		final Trace traceVbe = getTraceRed( table, xName, y2Name );

//		log.warn( table.where( table.stringColumn( Headers.PROJECT_NAME ).startsWith( "A008" ) ) );
//		log.warn("here");

		return new Figure( layout
				, trace
				, traceWb
				,traceWbp
				, traceVb
				, traceVbe
		);
	}
	// ========================================================================================
	// ========================================================================================
	public Figure createFigureFzkmDiff(){
		String y2Name = ADDTL_PKWKM_EL03_DIFF;

		Axis yAxis = Axis.builder().title( y2Name ).type( Axis.Type.LOG ).build();

		String title = "";
		if ( ADDTL_LANE_KM.equals( xName ) ){
			title = "addtl_veh-km_diff are SIM addtl_lane-km:";
		}

		Layout layout = Layout.builder( title ).xAxis( xAxis ).yAxis( yAxis ).width( plotWidth ).build();

//		Trace trace = ScatterTrace.builder( table.numberColumn( xName ), table.numberColumn( yName ) )
//					  .name( String.format( legendFormat, yName ) )
//					  .text( table.stringColumn( Headers.PROJECT_NAME ).asObjectArray() )
//					  .build();

		final Trace traceWb = getTraceCyan( table, xName, y2Name );
		final Trace traceWbp = getTraceMagenta( table, xName, y2Name );
		final Trace traceVb = getTraceOrange( table, xName, y2Name );
		final Trace traceVbe = getTraceRed( table, xName, y2Name );

		return new Figure( layout
//				, trace
				, traceWb
				,traceWbp
				, traceVb
				, traceVbe
		);
	}


	// ========================================================================================
	// ========================================================================================
	public Figure createFigureNkvVsDtv(){
		String xName = VERKEHRSBELASTUNG_PLANFALL;
		Axis xAxis = Axis.builder().title(xName).build();

		Table table2 = table.sortAscendingOn( xName ); // cannot remember why this is necessary

		String yName = NKV_EL03_CARBON215_INVCOST50_CAPPED10;
		Axis yAxis = Axis.builder().title( yName ).build(); // cannot use a a logarithmic y axis since it removes nkv < 1

		String title = "nkv_nb normally low; nkv_knotenpunkt normally high; nkv_ew depends on dtv";
		Layout layout = Layout.builder( title ).xAxis( xAxis ).yAxis( yAxis ).width( plotWidth ).build();

		List<Trace> traces = new ArrayList<>();
		{
			// the nkv=1 line:
			double[] xx = new double[]{0., 1.1 * table2.numberColumn( xName ).max()};
			double[] yy = new double[]{1., 1.};
			traces.add( ScatterTrace.builder( xx, yy ).mode( ScatterTrace.Mode.LINE ).build() );
		}
		{
//		final Trace traceCyan = getTraceCyan( table2, xName, yName );
//		final Trace traceMagenta = getTraceMagenta( table2, xName, yName );
			traces.add( getTraceOrange( table2, xName, yName ) );
			traces.add( getTraceRed( table2, xName, yName ) );
		}

		table2.addColumns( table2.doubleColumn( xName ).power( 2 ).setName( "power2" ) );

		{
//			Table tableVb = table2
//							 .where( table -> table.stringColumn( BAUTYP ).isIn( "EW6", "EW6_EW8" ) )
//							 .where( table -> table.doubleColumn( xName ).isGreaterThanOrEqualTo( 60000. ) )
//							 .dropWhere( table -> table.stringColumn( PROJECT_NAME ).containsString( "A57-G10-NW" ) )
//							 .dropWhere( table -> table.stringColumn( PROJECT_NAME ).containsString( "A81-G50-BW" ) )
//							 .dropWhere( table -> table.stringColumn( PROJECT_NAME ).containsString( "A40-G70-NW-T3" ) )
//							 .dropWhere( table -> table.stringColumn( PROJECT_NAME ).containsString( "A1-G30-NW" ) )
//							 .dropWhere( table -> table.stringColumn( PROJECT_NAME ).containsString( "A61-G60-NW" ) )
//							 .dropWhere( table -> table.stringColumn( PROJECT_NAME ).containsString( "A66-G10-HE-T1" ) )
//							 .sortAscendingOn( xName );
//			LinearModel regression = OLS.fit( Formula.lhs( NKV_EL03_CARBON215_INVCOST50 ),
//					tableVb.selectColumns( xName, NKV_EL03_CARBON215_INVCOST50
////							, "power2"
//							     ).smile().toDataFrame() );
//			log.info( regression );
//			var column = DoubleColumn.create( "regression" );
//			for( double fittedValue : regression.fittedValues() ){
//				column.append( fittedValue );
//			}
//			rTrace = ScatterTrace.builder( tableVb.doubleColumn( xName ), column ).mode( ScatterTrace.Mode.LINE ).build();
			double[] xx = new double[]{ 60_000., 120_000. };
			double[] yy = new double[]{0., 10.};
			traces.add( ScatterTrace.builder( xx, yy ).mode( ScatterTrace.Mode.LINE ).build() );
		}
		{
//			Table tableEW8 = table.where( table.stringColumn( BAUTYP ).containsString( "EW8" ) );
//			LinearModel regression = OLS.fit( Formula.lhs( NKV_EL03_CARBON215_INVCOST50 ),
//					tableEW8.selectColumns( xName, NKV_EL03_CARBON215_INVCOST50 ).smile().toDataFrame() );
//			log.info( regression );
//			var column = DoubleColumn.create( "regression" );
//			for( double fittedValue : regression.fittedValues() ){
//				column.append( fittedValue );
//			}
//			traces.add( ScatterTrace.builder( tableEW8.doubleColumn( xName ), column ).mode( ScatterTrace.Mode.LINE ).build() );
			double[] xx = new double[]{ 100_000., 160_000. };
			double[] yy = new double[]{0., 10.};
			traces.add( ScatterTrace.builder( xx, yy ).mode( ScatterTrace.Mode.LINE ).build() );
		}
		return new Figure( layout , traces.toArray(new Trace[]{}) );
	}
	// ========================================================================================
	// ========================================================================================
	public Figure cost_increased_VsNkv_El03_CO2_215_Baukosten_50(){
		String xName = NKV_EL03_CARBON215_INVCOST50_CAPPED5;

		Axis xAxis = Axis.builder().title(xName).autoRange( Axis.AutoRange.REVERSED )
				 .showZeroLine( false )
				 .zeroLineWidth( 0 )
				 .zeroLineColor( "lightgray" )
				 .range( nkvCappedMax, nkvMin )
				 .build();

		Table table2 = table.sortDescendingOn( xName ); // cannot remember why this is necessary

		String yName = COST_OVERALL_INCREASED;
		Axis yAxis = Axis.builder().title( yName )
				 .build();

//		String title = "WB meistens NKV<1; Knotenpunkt alle NKV>1; bei Erweiterung (EW) haengt NKV von der Verkehrsmenge ab; bei Neubau (NB) meist hohes NKV wenn Lueckenschluss, sonst niedrig bis < 1";
		String title = "";
		Layout layout = Layout.builder( title ).xAxis( xAxis ).yAxis( yAxis ).width( plotWidth ).build();

		List<Trace> traces = new ArrayList<>();

		// the nkv=1 line:
		double[] xx = new double[]{1., 1.};
		double[] yy = new double[]{0., 1.1* table2.numberColumn( yName ).max() };
		traces.add( ScatterTrace.builder( xx, yy ).mode( ScatterTrace.Mode.LINE ).name("NKV=1").build() );

		traces.add( getTraceCyan( table2, xName, yName ) );
		traces.add( getTraceMagenta( table2, xName, yName ) );
		traces.add( getTraceOrange( table2, xName, yName ) );
		traces.add( getTraceRed( table2, xName, yName ) );

		return new Figure( layout, traces.toArray(new Trace[]{} ) );
	}
	// ========================================================================================
	// ========================================================================================
	public Figure cumulativeCost50VsNkv_el03_carbon2015_invcost50_capped5(){

		String xName = NKV_EL03_CARBON215_INVCOST50_CAPPED5;

		Axis xAxis = Axis.builder().title(xName).autoRange( Axis.AutoRange.REVERSED )
				 .showZeroLine( false )
				 .zeroLineWidth( 0 )
				 .zeroLineColor( "lightgray" )
				 .range( nkvCappedMax, nkvMin )
				 .build();

		Table table2 = table.sortDescendingOn( xName ); // cannot remember why this is necessary

		DoubleColumn cumulativeCost = DoubleColumn.create( "cumulative_cost" );
		{
			double sum = 0.;
			for( Double cost : table2.doubleColumn( COST_OVERALL_INCREASED ) ){
				sum += cost;
				cumulativeCost.append( sum );
			}
		}

		String yName = "cumulative_cost";
		Axis yAxis = Axis.builder().title( yName )
				 .build();

//		String title = "WB meistens NKV<1; Knotenpunkt alle NKV>1; bei Erweiterung (EW) haengt NKV von der Verkehrsmenge ab; bei Neubau (NB) meist hohes NKV wenn Lueckenschluss, sonst niedrig bis < 1";
		String title = "";
		Layout layout = Layout.builder( title ).xAxis( xAxis ).yAxis( yAxis ).width( plotWidth ).build();

		List<Trace> traces = new ArrayList<>();

		var nameInLegend = "cumulative cost";
		traces.add( ScatterTrace.builder( table2.doubleColumn( xName ), cumulativeCost ).mode( ScatterTrace.Mode.LINE ).showLegend( true ).name( String.format( legendFormat, nameInLegend ) ).build() );

		return new Figure( layout, traces.toArray(new Trace[]{} ) );
	}
	// ========================================================================================
	// ========================================================================================
	public Figure costOrigVsCumulativeCostOrig(){

		String xName = NKV_ORIG_CAPPED5;

		Axis xAxis = Axis.builder().title(xName).autoRange( Axis.AutoRange.REVERSED )
				 .showZeroLine( false )
				 .zeroLineWidth( 0 )
				 .zeroLineColor( "lightgray" )
				 .range( nkvCappedMax, nkvMin )
				 .build();

		Table table2 = table.sortDescendingOn( xName ); // cannot remember why this is necessary

		DoubleColumn cumulativeCost = DoubleColumn.create( "cumulative_cost" );
		{
			double sum = 0.;
			for( Double cost : table2.doubleColumn( COST_OVERALL ) ){
				sum += cost;
				cumulativeCost.append( sum );
			}
		}

		String yName = "cumulative_cost";
		Axis yAxis = Axis.builder().title( "Kumulative Investitionskosten (orig) [Mio]" )
				 .build();

//		String title = "WB meistens NKV<1; Knotenpunkt alle NKV>1; bei Erweiterung (EW) haengt NKV von der Verkehrsmenge ab; bei Neubau (NB) meist hohes NKV wenn Lueckenschluss, sonst niedrig bis < 1";
		String title = "";
		Layout layout = Layout.builder( title ).xAxis( xAxis ).yAxis( yAxis ).width( plotWidth ).build();

		List<Trace> traces = new ArrayList<>();

		var nameInLegend = "cumulative cost";
		traces.add( ScatterTrace.builder( table2.doubleColumn( xName ), cumulativeCost ).mode( ScatterTrace.Mode.LINE ).showLegend( true ).name( String.format( legendFormat, nameInLegend ) ).build() );

		return new Figure( layout, traces.toArray(new Trace[]{} ) );
	}
	// ========================================================================================
	// ========================================================================================
	public Figure createFigureCostVsNkvOld(){
		String xName = NKV_ORIG_CAPPED5;

		Axis xAxis = Axis.builder().title(xName).autoRange( Axis.AutoRange.REVERSED )
//				 .visible( false )
//				 .showLine( false )
				 .showZeroLine( false )
				 .zeroLineWidth( 0 )
				 .zeroLineColor( "lightgray" )
				 .range( nkvCappedMax, nkvMin )
				 .build();

		Table table2 = table.sortDescendingOn( xName ); // cannot remember why this is necessary

		String yName = COST_OVERALL;
		Axis yAxis = Axis.builder().title( yName )
//				 .showZeroLine( false )
//				 .showLine( false )
//				 .visible( false )
//				 .zeroLineWidth( 100 )
//				 .type( Axis.Type.LOG )
				 .build();

		String title = "";
		Layout layout = Layout.builder( title ).xAxis( xAxis ).yAxis( yAxis ).width( plotWidth ).build();

		// the nkv=1 line:
		double[] xx = new double[]{1., 1.};
		double[] yy = new double[]{0., 1.1* table2.numberColumn( yName ).max() };
		Trace trace4 = ScatterTrace.builder( xx, yy ).mode( ScatterTrace.Mode.LINE ).name("NKV=1").build();

		final Trace traceCyan = getTraceCyan( table2, xName, yName );
		final Trace traceMagenta = getTraceMagenta( table2, xName, yName );
		final Trace traceOrange = getTraceOrange( table2, xName, yName );
		final Trace traceRed = getTraceRed( table2, xName, yName );

		return new Figure( layout, traceCyan, traceMagenta, traceOrange, traceRed
				, trace4
		);
	}
	// ========================================================================================
	// ========================================================================================

	// "BubblePlot" handles categorical variables --> could convert BAUTYP into the categories that are of interest.
	// There is also ScatterPlot3D

	// one can combine the four methods below by setting the marker color etc. to a Column instead of to a single value.  See examples in BubblePlot.
	// (however, in the end this does not work that much differently from what follows here except that it loops over the categorical column instead of repeating the code)

	private static Trace getTraceRed( Table table, String xName, String y2Name ){
		Table tableVbe = table.where( table.stringColumn( BAUTYP ).isIn( "EW8" ) );
		final String nameInLegend = "EW8";
		final String color = "red";
		final Trace traceVbe = getTrace( xName, y2Name, tableVbe, nameInLegend, color );
		return traceVbe;
	}
	private static Trace getTrace( String xName, String y2Name, Table tableVbe, String nameInLegend, String color ){
		Trace traceVbe = ScatterTrace.builder( tableVbe.numberColumn( xName ), tableVbe.numberColumn( y2Name ) )
					     .text( tableVbe.stringColumn( PROJECT_NAME ).asObjectArray() )
					     .name( String.format( legendFormat, nameInLegend ) )
					     .marker( Marker.builder().color( color )
							    .size(20)
							    .size( tableVbe.doubleColumn( Headers.EINSTUFUNG_AS_NUMBER ) ).sizeMode( Marker.SizeMode.DIAMETER )
							    .build() )
					     .build();
		return traceVbe;
	}
	private static Trace getTraceOrange( Table table, String xName, String y2Name ){
		Table tableVb = table.where( table.stringColumn( BAUTYP ).isIn( "EW6","EW6_EW8" ) );
		final Trace traceVb = getTrace( xName, y2Name, tableVb, "EW6", "orange" );
		return traceVb;
	}
	private static Trace getTraceMagenta( Table table, String xName, String y2Name ){
		Table tableWb = table.where( table.stringColumn( BAUTYP ).containsString( "NB" ) );
		final Trace traceWb = getTrace( xName, y2Name, tableWb, "Neubau", "magenta" );
		return traceWb;
	}
	private static Trace getTraceCyan( Table table, String xName, String y2Name ){
		Table tableWb = table.where( table.stringColumn( BAUTYP ).containsString( "KNOTENPUNKT" ) ) ;
		final Trace traceWb = getTrace( xName, y2Name, tableWb, "Knotenpunkt", "cyan" );
		return traceWb;
	}
}
