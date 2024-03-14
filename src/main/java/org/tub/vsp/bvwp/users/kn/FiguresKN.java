package org.tub.vsp.bvwp.users.kn;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.tub.vsp.bvwp.computation.ComputationKN;
import org.tub.vsp.bvwp.data.Headers;
import org.tub.vsp.bvwp.data.type.Priority;
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
import java.util.Locale;

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
		xName = Headers.COST_OVERALL;
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
			case Headers.NKV_ORIG
//					     , Headers.VERKEHRSBELASTUNG_2030
					-> xAxisBuilder.type( Axis.Type.LOG );
		}

//		xAxisBuilder.type( Axis.Type.LOG );


//        table = table.where( table.numberColumn( Headers.NKV_INDUZ_CO2 ).isLessThan( 2.) );

		// ============================================
		table.addColumns(
				table.numberColumn( Headers.NKV_INDUZ_CO2_CONSTRUCTION ).subtract( table.numberColumn( Headers.NKV_ORIG ) ).setName( Headers.NKV_DIFF )
				,table.numberColumn( Headers.ADDTL_PKWKM_NEU ).subtract( table.numberColumn( Headers.ADDTL_PKWKM_ORIG ) ).setName( Headers.ADDTL_PKWKM_DIFF )
				);
		// ============================================
		// ============================================
		final double cap=5.;
		{
			DoubleColumn newColumn = DoubleColumn.create( Headers.NKV_INDUZ_CO2_CONSTRUCTION_CAPPED5 );
			for( Double number : table.doubleColumn( Headers.NKV_INDUZ_CO2_CONSTRUCTION ) ){
				number = Math.min( number,cap - Math.random()*0.1 + 0.05 );
				newColumn.append( number );
			}
			table.addColumns( newColumn );
		}
		// ============================================
		// ============================================
		{
			DoubleColumn newColumn = DoubleColumn.create( Headers.NKV_INDUZ_CO2215_CONSTRUCTION_CAPPED5 );
			for( Double number : table.doubleColumn( Headers.NKV_INDUZ_CO2215_CONSTRUCTION ) ){
				number = Math.min( number, cap - Math.random()*0.1 + 0.05 );
				newColumn.append( number );
			}
			table.addColumns( newColumn );
		}
		// ============================================
		// ============================================
		{
			DoubleColumn newColumn = DoubleColumn.create( Headers.NKV_ORIG_CAPPED5 );
			for( Double number : table.doubleColumn( Headers.NKV_ORIG ) ){
				number = Math.min( number, cap - Math.random()*0.1 + 0.05);
				newColumn.append( number );
			}
			table.addColumns( newColumn );
		}
		// ============================================
		// ===========================
		{
			DoubleColumn newColumn = DoubleColumn.create( Headers.VERKEHRSBELASTUNG_PLANFALL );
			for( Double value : table.doubleColumn( Headers.VERKEHRSBELASTUNG_PLANFALL ) ){
				newColumn.append( value + 2000. * Math.random() ); // randomize so that they are not on top of each other in plotly.  Deliberately not centered to that values are > 0.
			}
			table.removeColumns( Headers.VERKEHRSBELASTUNG_PLANFALL );
			table.addColumns( newColumn );
		}
		// ===========================
		// ===========================
		{
			DoubleColumn column = DoubleColumn.create( Headers.PRIO_AS_NUMBER );
			final double factor = 5.;
			for( String prio : table.stringColumn( Headers.EINSTUFUNG ) ){
				switch( Priority.valueOf( prio ) ){
					case VBE -> {
						column.append( 4. * factor );
					}
					case VB -> {
						column.append( 3. * factor );
					}
					case WBP -> {
						column.append( 2. * factor );
					}
					case WB -> {
						column.append( 1. * factor );
					}
					case UNDEFINED -> {
						column.append( 2. );
					}
					default -> throw new IllegalStateException( "Unexpected value: " + prio );
				}
			}
			table.addColumns( column );
		}
		// ===========================



		table = table.sortAscendingOn( xName );
		this.xAxis = xAxisBuilder.title( xName ).build();

		this.table = table;

		nkvCappedMax = table.doubleColumn( Headers.NKV_ORIG_CAPPED5 ).max() + 0.2 ;
		nkvMin = table.doubleColumn( Headers.NKV_INDUZ_CO2215_CONSTRUCTION_CAPPED5 ).min();

		NumberFormat nf = NumberFormat.getInstance( Locale.GERMAN );

		for( Column<?> column : table.columns() ){
			if ( column instanceof NumericColumn ) {
				((NumericColumn) column).setPrintFormatter( nf, "" );
			}
		}

		new CsvWriter().write( table, CsvWriteOptions.builder( new File( "output.tsv" ) ).separator( '\t' ).usePrintFormatters( true ).build() );

		System.exit(-1);
	}
	Figure createFigureCostVsLanekm(){
//		String xName = Headers.ADDTL_LANE_KM;
//		Axis xAxis = Axis.builder().title(xName).type( Axis.Type.LOG ).build();

		Figure figure3;
		String yName = Headers.COST_OVERALL;
//		String y3Name = Headers.COST_OVERALL;
		String y2Name = Headers.COST_OVERALL;

		Axis yAxis = Axis.builder()
				 .type( Axis.Type.LOG )
				 //                             .range( 1.1*table.numberColumn( y2Name ).min(),4. )
				 .title( yName )
				 .build();
		Layout layout = Layout.builder( "construction cost SIMTO lane-km, but quite varied:" )
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

		figure3 = new Figure( layout
//				, trace, trace3
				, traceWb, traceWbp, traceVb, traceVbe );
		return figure3;
	}
	Figure createFigureNkvVsLanekm(){
//		String xName = Headers.ADDTL_LANE_KM;
//		Axis xAxis = Axis.builder().title(xName).type( Axis.Type.LOG ).build();

		Figure figure2;
		String yName = Headers.NKV_DIFF;
//		String y3Name = Headers.NKV_CO2;
		String y2Name = Headers.NKV_DIFF;

		Axis yAxis = Axis.builder()
//			     .type( Axis.Type.LOG ) // wirft NKV < 0 raus!
				 //                             .range( 1.1*table.numberColumn( y2Name ).min(),4. )
				 .title( yName )
				 .build();
		Layout layout = Layout.builder( "since (co2_cost SIMTO lane-km) and (cost SIMTO lane-km), the division of the two does not depend on lane-km:" )
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

		figure2 = new Figure( layout
//				, trace
//				, trace3
				, traceCyan,traceMagenta
				,  traceOrange, traceRed
//				, trace4
				);
		return figure2;
	}
	Figure createFigurePkwKm( ){
		Figure figure;
		String yName = Headers.ADDTL_PKWKM_INDUZ;
		String y2Name = Headers.ADDTL_PKWKM_NEU;

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
//		String xName = Headers.ADDTL_LANE_KM;
//		Axis xAxis = Axis.builder().title(xName).type( Axis.Type.LOG ).build();

		String yName = "elasticity_old";
		String y2Name = "elasticity_new";

		table.addColumns( table.numberColumn( Headers.ADDTL_PKWKM_ORIG )
				       .divide( ComputationKN.FZKM_AB )
				       .multiply( ComputationKN.LANE_KM_AB )
				       .divide( table.numberColumn( Headers.ADDTL_LANE_KM ) ).setName("elasticity_old" ),
				table.numberColumn( Headers.ADDTL_PKWKM_NEU )
				     .divide( ComputationKN.FZKM_AB )
				     .multiply( ComputationKN.LANE_KM_AB )
				     .divide( table.numberColumn( Headers.ADDTL_LANE_KM ) ).setName("elasticity_new" )
				);

		double xMin = table.numberColumn( xName ).min();
		double xMax = table.numberColumn( xName ).max();

		Axis yAxis = Axis.builder().title( yName )
//				 .type( Axis.Type.LOG )
//				 .range( -0.3, 0.8 )
				 .build();

		Layout layout = Layout.builder( "elasticities are indep of lane-km:" ).xAxis( xAxis ).yAxis( yAxis ).width( plotWidth ).build();

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
		String y2Name = Headers.ADDTL_PKWKM_NEU;
		String yName = Headers.ADDTL_PKWKM_ORIG;

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

	Figure createFigureCO2VsLanekm(){
//		String xName = Headers.ADDTL_LANE_KM;
//		Axis xAxis = Axis.builder().title(xName).type( Axis.Type.LOG ).build();

		String yName = Headers.CO2_COST_ORIG;
		String y2Name = Headers.CO2_COST_NEU;

		Axis yAxis = Axis.builder().title( yName ).type( Axis.Type.LOG ).build();

		Layout layout = Layout.builder( "CO2 costs SIMTO addtl lane-km:" ).xAxis( xAxis ).yAxis( yAxis ).width( plotWidth ).build();

		Trace trace = ScatterTrace.builder( table.numberColumn( xName ), table.numberColumn( yName ) )
					  .name( String.format( legendFormat, yName ) )
					  .text( table.stringColumn( Headers.PROJECT_NAME ).asObjectArray() )
					  .build();

		final Trace traceWb = getTraceCyan( table, xName, y2Name );
		final Trace traceWbp = getTraceMagenta( table, xName, y2Name );
		final Trace traceVb = getTraceOrange( table, xName, y2Name );
		final Trace traceVbe = getTraceRed( table, xName, y2Name );


		return new Figure( layout
				, trace
				, traceWb, traceWbp, traceVb, traceVbe );
	}
	public Figure createFigureDtv(){
		String yName = Headers.VERKEHRSBELASTUNG_PLANFALL;
		String y2Name = Headers.VERKEHRSBELASTUNG_PLANFALL;

		Axis yAxis = Axis.builder().title( yName ).type( Axis.Type.LOG ).build();

		Layout layout = Layout.builder( "plot" ).xAxis( xAxis ).yAxis( yAxis ).width( plotWidth ).build();

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
	public Figure createFigureFzkmNewVsLanekm(){
//		String xName = Headers.ADDTL_LANE_KM;
//		Axis xAxis = Axis.builder().title(xName).type( Axis.Type.LOG ).build();

		String yName = Headers.ADDTL_PKWKM_ORIG;
		String y2Name = Headers.ADDTL_PKWKM_NEU;

		Axis yAxis = Axis.builder().title( y2Name ).type( Axis.Type.LOG ).build();

		Layout layout = Layout.builder( "addtl_veh-km are PROPTO addtl_lane-km:" ).xAxis( xAxis ).yAxis( yAxis ).width( plotWidth ).build();

		Trace trace = ScatterTrace.builder( table.numberColumn( xName ), table.numberColumn( yName ) )
					  .name( String.format( legendFormat, yName ) )
					  .text( table.stringColumn( Headers.PROJECT_NAME ).asObjectArray() )
					  .build();

		final Trace traceWb = getTraceCyan( table, xName, y2Name );
		final Trace traceWbp = getTraceMagenta( table, xName, y2Name );
		final Trace traceVb = getTraceOrange( table, xName, y2Name );
		final Trace traceVbe = getTraceRed( table, xName, y2Name );

		log.warn( table.where( table.stringColumn( Headers.PROJECT_NAME ).startsWith( "A008" ) ) );
		log.warn("here");

		return new Figure( layout
				, trace
				, traceWb
				,traceWbp
				, traceVb
				, traceVbe
		);
	}
	public Figure createFigureFzkmDiffVsLanekm(){
//		String xName = Headers.ADDTL_LANE_KM;
//		Axis xAxis = Axis.builder().title(xName).type( Axis.Type.LOG ).build();

		String y2Name = Headers.ADDTL_PKWKM_DIFF;

		Axis yAxis = Axis.builder().title( y2Name ).type( Axis.Type.LOG ).build();

		Layout layout = Layout.builder( "addtl_veh-km_diff are SIM addtl_lane-km:" ).xAxis( xAxis ).yAxis( yAxis ).width( plotWidth ).build();

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


	public Figure createFigureNkvVsDtv(){
		String xName = Headers.VERKEHRSBELASTUNG_PLANFALL;
		Axis xAxis = Axis.builder().title(xName).build();

		Table table2 = table.sortAscendingOn( xName ); // cannot remember why this is necessary

		String yName = Headers.NKV_INDUZ_CO2_CONSTRUCTION_CAPPED5;
		Axis yAxis = Axis.builder().title( yName ).build(); // cannot use a a logarithmic y axis since it removes nkv < 1

		String title = "nkv_nb normally low; nkv_knotenpunkt normally high; nkv_ew depends on dtv";
		Layout layout = Layout.builder( title ).xAxis( xAxis ).yAxis( yAxis ).width( plotWidth ).build();

		// the nkv=1 line:
		double[] xx = new double[]{0., 1.1* table2.numberColumn( xName ).max() };
		double[] yy = new double[]{1., 1.};
		Trace trace4 = ScatterTrace.builder( xx, yy ).mode( ScatterTrace.Mode.LINE ).build();

		final Trace traceCyan = getTraceCyan( table2, xName, yName );
		final Trace traceMagenta = getTraceMagenta( table2, xName, yName );
		final Trace traceOrange = getTraceOrange( table2, xName, yName );
		final Trace traceRed = getTraceRed( table2, xName, yName );

		return new Figure( layout, traceCyan, traceMagenta, traceOrange, traceRed, trace4 );
	}
	public Figure createFigureCostVsNkvNew(){
		String xName = Headers.NKV_INDUZ_CO2215_CONSTRUCTION_CAPPED5;

		Axis xAxis = Axis.builder().title(xName).autoRange( Axis.AutoRange.REVERSED )
				 .showZeroLine( false )
				 .zeroLineWidth( 0 )
				 .zeroLineColor( "lightgray" )
				 .range( nkvCappedMax, nkvMin )
				 .build();

		Table table2 = table.sortDescendingOn( xName ); // cannot remember why this is necessary

		String yName = Headers.COST_OVERALL_INCREASED;
		Axis yAxis = Axis.builder().title( yName )
				 .build();

//		String title = "WB meistens NKV<1; Knotenpunkt alle NKV>1; bei Erweiterung (EW) haengt NKV von der Verkehrsmenge ab; bei Neubau (NB) meist hohes NKV wenn Lueckenschluss, sonst niedrig bis < 1";
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
	public Figure createFigureCO2VsNkvNew(){
		String xName = Headers.NKV_INDUZ_CO2215_CONSTRUCTION_CAPPED5;

		Axis xAxis = Axis.builder().title(xName).autoRange( Axis.AutoRange.REVERSED )
				 .showZeroLine( false )
				 .zeroLineWidth( 0 )
				 .zeroLineColor( "lightgray" )
				 .range( nkvCappedMax, nkvMin )
				 .build();

		Table table2 = table.sortDescendingOn( xName ); // cannot remember why this is necessary

		String yName = Headers.CO2_COST_NEU;
		Axis yAxis = Axis.builder().title( yName )
				 .build();

//		String title = "WB meistens NKV<1; Knotenpunkt alle NKV>1; bei Erweiterung (EW) haengt NKV von der Verkehrsmenge ab; bei Neubau (NB) meist hohes NKV wenn Lueckenschluss, sonst niedrig bis < 1";
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
	public Figure createFigureCostVsNkvOld(){
		String xName = Headers.NKV_ORIG_CAPPED5;

		Axis xAxis = Axis.builder().title(xName).autoRange( Axis.AutoRange.REVERSED )
//				 .visible( false )
//				 .showLine( false )
				 .showZeroLine( false )
				 .zeroLineWidth( 0 )
				 .zeroLineColor( "lightgray" )
				 .range( nkvCappedMax, nkvMin )
				 .build();

		Table table2 = table.sortDescendingOn( xName ); // cannot remember why this is necessary

		String yName = Headers.COST_OVERALL;
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
	// "BubblePlot" handles categorical variables --> could convert BAUTYP into the categories that are of interest.
	// There is also ScatterPlot3D

	// one can combine the four methods below by setting the marker color etc. to a Column instead of to a single value.  See examples in BubblePlot.
	// (however, in the end this does not work that much differently from what follows here except that it loops over the categorical column instead of repeating the code)
	private static Trace getTraceRed( Table table, String xName, String y2Name ){
		Table tableVbe = table.where( table.stringColumn( Headers.BAUTYP ).isIn( "EW8" ) );
		final String nameInLegend = "EW8";
		final String color = "red";
		final Trace traceVbe = getTrace( xName, y2Name, tableVbe, nameInLegend, color );
		return traceVbe;
	}
	private static Trace getTrace( String xName, String y2Name, Table tableVbe, String nameInLegend, String color ){
		Trace traceVbe = ScatterTrace.builder( tableVbe.numberColumn( xName ), tableVbe.numberColumn( y2Name ) )
					     .text( tableVbe.stringColumn( Headers.PROJECT_NAME ).asObjectArray() )
					     .name( String.format( legendFormat, nameInLegend ) )
					     .marker( Marker.builder().color( color ).size( tableVbe.doubleColumn( Headers.PRIO_AS_NUMBER ) ).sizeMode( Marker.SizeMode.DIAMETER ).build() )
					     .build();
		return traceVbe;
	}
	private static Trace getTraceOrange( Table table, String xName, String y2Name ){
		Table tableVb = table.where( table.stringColumn( Headers.BAUTYP ).isIn( "EW6","EW6_EW8" ) );
		final Trace traceVb = getTrace( xName, y2Name, tableVb, "EW6", "orange" );
		return traceVb;
	}
	private static Trace getTraceMagenta( Table table, String xName, String y2Name ){
		Table tableWb = table.where( table.stringColumn( Headers.BAUTYP ).containsString( "NB" ) );
		final Trace traceWb = getTrace( xName, y2Name, tableWb, "Neubau", "magenta" );
		return traceWb;
	}
	private static Trace getTraceCyan( Table table, String xName, String y2Name ){
		Table tableWb = table.where( table.stringColumn( Headers.BAUTYP ).containsString( "KNOTENPUNKT" ) ) ;
		final Trace traceWb = getTrace( xName, y2Name, tableWb, "Knotenpunkt", "cyan" );
		return traceWb;
	}
}
