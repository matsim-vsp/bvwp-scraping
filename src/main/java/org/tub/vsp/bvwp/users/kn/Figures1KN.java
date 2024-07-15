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
import tech.tablesaw.plotly.components.*;
import tech.tablesaw.plotly.traces.ScatterTrace;
import tech.tablesaw.plotly.traces.Trace;

import java.io.File;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import static org.tub.vsp.bvwp.data.Headers.*;

class Figures1KN{
	private static final Logger log = LogManager.getLogger( Figures1KN.class );
	static final int plotWidth = 2000;
	static final String legendFormat = "%30s";
	static final String dotSizeString = "largest = VB-E; large = VB; small = WB*; tiny = WB";
	final Table table;
	private final Axis xAxis;
	private final String xName;
	final double nkvCappedMax;
	final double nkvMin;
	private static boolean init = true ;
	Margin defaultMargin;

	static final Font defaultFont = Font.builder().size( 20 ).family( Font.Family.VERDANA ).build();
	Figures1KN( Table table, String xName ){
		this.xName = xName;
		Axis.AxisBuilder xAxisBuilder = Axis.builder();

		if ( xName!=null && xName.contains( "NKV" ) ) {
			xAxisBuilder.autoRange( Axis.AutoRange.REVERSED );
		}

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
//			table.addColumns(
//					table.numberColumn( NKV_EL03_CARBON215_INVCOSTTUD ).subtract( table.numberColumn( NKV_ORIG ) ).setName( NKV_EL03_DIFF )
//					, table.numberColumn( ADDTL_PKWKM_EL03 ).subtract( table.numberColumn( ADDTL_PKWKM_ORIG ) ).setName( ADDTL_PKWKM_EL03_DIFF )
//					);
			// ===========================
			// ===========================
//			Headers.addCap5( table, NKV_EL03_CARBON215_INVCOSTTUD );
//			Headers.addCap5( table, NKV_EL03_CARBON700ptpr0_INVCOSTTUD );
			Headers.addCap5( table, NKV_ELTTIME_CARBON215_INVCOSTTUD );
			Headers.addCap5( table, NKV_ELTTIME_CARBON700ptpr0_INVCOSTTUD );
			Headers.addCap5( table, NKV_ORIG );
//			Headers.addCap5( table, NKV_EL03 );
//			Headers.addCap5( table, NKV_EL03_CARBON700ptpr0 );
			Headers.addCap5( table, NKV_CARBON700ptpr0 );
			Headers.addCap5( table, NKV_ELTTIME_CARBON2000_INVCOSTTUD );

//			Headers.addCap( 10, table, NKV_EL03_CARBON215_INVCOSTTUD );
			Headers.addCap( 10, table, NKV_ELTTIME_CARBON215_INVCOSTTUD );
			// ===========================
			// ===========================
			{
				DoubleColumn newColumn = DoubleColumn.create( VERKEHRSBELASTUNG_PLANFALL );
				for( Double value : table.doubleColumn( VERKEHRSBELASTUNG_PLANFALL ) ){
					newColumn.append( value + 2000. * Math.random() );
					// (randomize so that they are not on top of each other in plotly.  Deliberately not centered to that values are > 0.)
				}
				table.removeColumns( VERKEHRSBELASTUNG_PLANFALL );
				table.addColumns( newColumn );
			}
			// ===========================
			// ===========================
			{
				DoubleColumn column = DoubleColumn.create( EINSTUFUNG_AS_NUMBER );
				final double factor = 8.;
				final double offset = 6.;
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
							column.append( offset );
//						throw new IllegalStateException( "Unexpected value: " + prio );
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
			table = table.sortAscendingOn( this.xName );
		}

		this.xAxis = xAxisBuilder.title( this.xName )
//					 .type( Axis.Type.LOG )
					 .build();

		this.table = table;

		final String NKV_ORIG_CAPPED5 = Headers.addCap( 5, table, NKV_ORIG );
		nkvCappedMax = table.doubleColumn( NKV_ORIG_CAPPED5 ).max() + 0.2 ;
		nkvMin = table.doubleColumn( NKV_ELTTIME_CARBON700ptpr0_INVCOSTTUD ).min();

		this.defaultMargin = Margin.builder()
//						.autoExpand( true )
				      .padding( 0 )
				      .top( 25 ) // even smaller number swallows the title
				      .bottom( 100 )
				      .build();
		;


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
//		Trace trace = ScatterTrace.builder( table.numberColumn( xName ), table.numberColumn( yName ) )
//					  .text( table.stringColumn( Headers.PROJECT_NAME ).asObjectArray() )
//					  .name( String.format( legendFormat, String.format( "%30s" , yName ) ) )
//					  .build();

		List<Trace> traces = new ArrayList<>();
		traces.addAll( getTraceCyan( table, xName, y2Name ) );
		traces.addAll( getTraceMagenta( table, xName, y2Name ) );
		traces.addAll( getTraceOrange( table, xName, y2Name ) );
		traces.addAll( getTraceRed( table, xName, y2Name ) );


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

		Figure figure2 = new Figure( layout, traces.toArray(new Trace[]{}) );
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

		List<Trace> traces = new ArrayList<>();
		traces.addAll( getTraceCyan( table, xName, y2Name ) );
		traces.addAll( getTraceMagenta( table, xName, y2Name ) );
		traces.addAll( getTraceOrange( table, xName, y2Name ) );
		traces.addAll( getTraceRed( table, xName, y2Name ) );


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

		Figure figure2 = new Figure( layout, traces.toArray(new Trace[]{}) );
		return figure2;
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

		List<Trace> traces = new ArrayList<>( getTracesByColor( table, xName, y2Name ) );

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
//		Trace trace = ScatterTrace.builder( table.numberColumn( xName ), table.numberColumn( yName ) )
//					  .text( table.stringColumn( Headers.PROJECT_NAME ).asObjectArray() )
//					  .name( String.format( legendFormat, String.format( "%30s" , yName ) ) )
//					  .build();

		List<Trace> traces = new ArrayList<>( getTracesByColor( table, xName, y2Name ) );

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

		Figure figure = new Figure( layout, traces.toArray(traces.toArray( new Trace[0] ) ) );
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

		List<Trace> traces = new ArrayList<>( getTracesByColor( table, xName, y2Name ) );

		Trace trace = ScatterTrace.builder( table.numberColumn( xName ), table.numberColumn( yName ) )
					  .name( String.format( legendFormat, yName ) )
					  .text( table.stringColumn( PROJECT_NAME ).asObjectArray() )
					  .build();

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
		String yName = CO2_COST_ORIG;
		String y2Name = CO2_COST_EL03;

		Axis yAxis = Axis.builder().title( yName ).type( Axis.Type.LOG ).build();

		String title = "";
		if ( ADDTL_LANE_KM.equals( xName ) ) {
			title = "CO2 costs SIMTO addtl lane-km:";
		}

		Layout layout = Layout.builder( title ).xAxis( xAxis ).yAxis( yAxis ).width( plotWidth ).build();

		List<Trace> traces = new ArrayList<>( getTracesByColor( table, xName, y2Name ));
		return new Figure( layout, traces.toArray(new Trace[]{}) );
	}
	// ========================================================================================
	// ========================================================================================
	public Figure dtv(){
		String yName = VERKEHRSBELASTUNG_PLANFALL;
		String y2Name = VERKEHRSBELASTUNG_PLANFALL;

		Axis yAxis = Axis.builder().title( yName ).type( Axis.Type.LOG ).build();

		Layout layout = Layout.builder( "" ).xAxis( xAxis ).yAxis( yAxis ).width( plotWidth ).build();

//		Trace trace = ScatterTrace.builder( table.numberColumn( xName ), table.numberColumn( yName ) )
//					  .name( String.format( legendFormat, yName ) )
//					  .text( table.stringColumn( Headers.PROJECT_NAME ).asObjectArray() )
//					  .build();

		List<Trace> traces = new ArrayList<>( getTracesByColor( table, xName, y2Name ) );

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

		List<Trace> traces = new ArrayList<>( getTracesByColor( table, xName, y2Name ) );

		traces.add( ScatterTrace.builder( table.numberColumn( xName ), table.numberColumn( yName ) )
					  .name( String.format( legendFormat, yName ) )
					  .text( table.stringColumn( PROJECT_NAME ).asObjectArray() )
					  .build() );

//		log.warn( table.where( table.stringColumn( Headers.PROJECT_NAME ).startsWith( "A008" ) ) );
//		log.warn("here");

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

		List<Trace> traces = new ArrayList<>( getTracesByColor( table, xName, y2Name ) );

//		Trace trace = ScatterTrace.builder( table.numberColumn( xName ), table.numberColumn( yName ) )
//					  .name( String.format( legendFormat, yName ) )
//					  .text( table.stringColumn( Headers.PROJECT_NAME ).asObjectArray() )
//					  .build();

		return new Figure( layout, traces.toArray(new Trace[]{} ) );

	}
	public Figure invCostTud(){
		String y2Name = INVCOST_TUD;

		Axis yAxis = Axis.builder().title( y2Name ).type( Axis.Type.LOG ).build();

		String title = "";
		if ( ADDTL_LANE_KM.equals( xName ) ){
			title = "addtl_veh-km_diff are SIM addtl_lane-km:";
		}

		Layout layout = Layout.builder( title ).xAxis( xAxis ).yAxis( yAxis ).width( plotWidth ).build();

		List<Trace> traces = new ArrayList<>( getTracesByColor( table, xName, y2Name ) );

		return new Figure( layout, traces.toArray(new Trace[]{} ) );
	}


	// ========================================================================================
	// ========================================================================================

	// "BubblePlot" handles categorical variables --> could convert BAUTYP into the categories that are of interest.
	// There is also ScatterPlot3D

	// one can combine the four methods below by setting the marker color etc. to a Column instead of to a single value.  See examples in BubblePlot.
	// (however, in the end this does not work that much differently from what follows here except that it loops over the categorical column instead of repeating the code)

	static List<Trace> getTraceRed( Table table, String xName, String y2Name ){
		Table tableVbe = table.where( table.stringColumn( BAUTYP ).isIn( "EW8" ) );
		final String nameInLegend = "EW8";
		final String color = "red";
		return getTraces( xName, y2Name, tableVbe, nameInLegend, color );
	}
	static List<Trace> getTraces( String xName, String y2Name, Table table, String nameInLegend, String color ){

		Table tableA20 = table.where( table.stringColumn( PROJECT_NAME ).startsWith( "A20-" ) );
		Table tableA14 = table.where( table.stringColumn( PROJECT_NAME ).startsWith( "A14-" ) );
		Table tableA39 = table.where( table.stringColumn( PROJECT_NAME ).startsWith( "A39-" ) );
		Table tableA008 = table.where( table.stringColumn( PROJECT_NAME ).startsWith( "A008-" ) );
//
		Table tableTmp = table.dropWhere( table.stringColumn( PROJECT_NAME ).startsWith( "A20-" )
							      .or( table.stringColumn( PROJECT_NAME ).startsWith( "A008-" ) )
							      .or( table.stringColumn( PROJECT_NAME ).startsWith( "A14-" ) )
							      .or( table.stringColumn( PROJECT_NAME ).startsWith( "A39-" ) )
						    );

		List<Trace> traces = new ArrayList<>();

		traces.add( ScatterTrace.builder( tableTmp.numberColumn( xName ), tableTmp.numberColumn( y2Name ) )
					.text( tableTmp.stringColumn( PROJECT_NAME ).asObjectArray() )
					.name( String.format( legendFormat, nameInLegend ) )
					.marker( Marker.builder().color( color )
//						       .size( 20 )
						       .size( tableTmp.doubleColumn( EINSTUFUNG_AS_NUMBER ) )
						       .sizeMode( Marker.SizeMode.DIAMETER ).build() )
					.build() );
		traces.add( ScatterTrace.builder( tableA20.numberColumn( xName ), tableA20.numberColumn( y2Name ) )
					.text( tableA20.stringColumn( PROJECT_NAME ).asObjectArray() )
					.name( String.format( legendFormat, nameInLegend ) )
					.marker( Marker.builder().color( color )
						       .size( 40 )
//						       .size( tableA20.doubleColumn( EINSTUFUNG_AS_NUMBER ) )
						       .sizeMode( Marker.SizeMode.DIAMETER )
						       .symbol( Symbol.TRIANGLE_NW).build() )
					.build() );
		traces.add( ScatterTrace.builder( tableA14.numberColumn( xName ), tableA14.numberColumn( y2Name ) )
					.text( tableA14.stringColumn( PROJECT_NAME ).asObjectArray() )
					.name( String.format( legendFormat, nameInLegend ) )
					.marker( Marker.builder().color( color )
						       .size( 40 )
//						       .size( tableA14.doubleColumn( EINSTUFUNG_AS_NUMBER ) )
						       .sizeMode( Marker.SizeMode.DIAMETER )
						       .symbol( Symbol.TRIANGLE_RIGHT).build() )
					.build() );
		traces.add( ScatterTrace.builder( tableA39.numberColumn( xName ), tableA14.numberColumn( y2Name ) )
					.text( tableA39.stringColumn( PROJECT_NAME ).asObjectArray() )
					.name( String.format( legendFormat, nameInLegend ) )
					.marker( Marker.builder().color( color )
						       .size( 40 )
//						       .size( tableA39.doubleColumn( EINSTUFUNG_AS_NUMBER ) )
						       .sizeMode( Marker.SizeMode.DIAMETER )
						       .symbol( Symbol.TRIANGLE_LEFT).build() )
					.build() );
		traces.add( ScatterTrace.builder( tableA008.numberColumn( xName ), tableA008.numberColumn( y2Name ) )
					.text( tableA008.stringColumn( PROJECT_NAME ).asObjectArray() )
					.name( String.format( legendFormat, nameInLegend ) )
					.marker( Marker.builder().color( color )
						       .size( 40 )
//						       .size( tableA008.doubleColumn( EINSTUFUNG_AS_NUMBER ) )
						       .sizeMode( Marker.SizeMode.DIAMETER )
						       .symbol( Symbol.TRIANGLE_SE).build() )
					.build() );

		return traces;
	}
	static List<Trace> getTraceOrange( Table table, String xName, String y2Name ){
		Table tableVb = table.where( table.stringColumn( BAUTYP ).isIn( "EW6","EW6_EW8" ) );
		return getTraces( xName, y2Name, tableVb, "EW6", "orange" );
	}
	static List<Trace> getTraceMagenta( Table table, String xName, String y2Name ){
		Table tableWb = table.where( table.stringColumn( BAUTYP ).containsString( "NB" ) );
		return getTraces( xName, y2Name, tableWb, "Neubau", "magenta" );
	}
	static List<Trace> getTraceCyan( Table table, String xName, String y2Name ){
		Table tableWb = table.where( table.stringColumn( BAUTYP ).containsString( "KNOTENPUNKT" ) ) ;
		return getTraces( xName, y2Name, tableWb, "Knotenpunkt", "cyan" );
	}
	static List<Trace> getTracesByColor( Table table, String xName, String y2Name ){
		List<Trace> list = new ArrayList<>();
		list.addAll( getTraceRed( table, xName, y2Name ) );
		list.addAll( getTraceMagenta( table, xName, y2Name ) );
		list.addAll( getTraceCyan( table, xName, y2Name ) );
		list.addAll( getTraceOrange( table, xName, y2Name ) );
		return list;
	}
	static ScatterTrace diagonalLine( Table table, String xName, String y2Name ){
		double xMin = table.numberColumn( xName ).min();
		double yMin = table.numberColumn( y2Name ).min();
		double overallMin = Math.min( xMin, yMin);

		{
			var column = table.doubleColumn( xName );
			for ( int ii=0; ii<column.size(); ii++ ) {
				if ( Double.isInfinite( column.getDouble( ii ) ) ) {
					column.set( ii, Double.NaN );
				}
			}
		}
		{
			var column = table.doubleColumn( y2Name );
			for ( int ii=0; ii<column.size(); ii++ ) {
				if ( Double.isInfinite( column.getDouble( ii ) ) ) {
					column.setMissing( ii );
				}
			}
		}

		double xMax = table.numberColumn( xName ).max();
		double yMax = table.numberColumn( y2Name ).max();
		double overallMax = Math.max( xMax, yMax );

		return ScatterTrace.builder( new double[]{overallMin, overallMax}, new double[]{overallMin, overallMax} ).mode( ScatterTrace.Mode.LINE ).name( "diagonalLine" ).build();
	}
	static ScatterTrace diagonalLine2( Table table, String xName, String y2Name ){
		double xMin = table.numberColumn( xName ).min();
		double yMin = table.numberColumn( y2Name ).min();
		double overallMin = Math.max( xMin, yMin);

		{
			var column = table.doubleColumn( xName );
			for ( int ii=0; ii<column.size(); ii++ ) {
				if ( Double.isInfinite( column.getDouble( ii ) ) ) {
					column.set( ii, Double.NaN );
				}
			}
		}
		{
			var column = table.doubleColumn( y2Name );
			for ( int ii=0; ii<column.size(); ii++ ) {
				if ( Double.isInfinite( column.getDouble( ii ) ) ) {
					column.setMissing( ii );
				}
			}
		}

		double xMax = table.numberColumn( xName ).max();
		double yMax = table.numberColumn( y2Name ).max();
		double overallMax = Math.min( xMax, yMax );

		return ScatterTrace.builder( new double[]{overallMin, overallMax}, new double[]{overallMin, overallMax} ).mode( ScatterTrace.Mode.LINE ).name( "diagonalLine" ).build();
	}
	static ScatterTrace vertialNkvOneLine( Table table, String y2Name ){
		return ScatterTrace.builder( new double[]{1., 1.}, new double[]{0., 1.1 * table.numberColumn( y2Name ).max()} ).mode( ScatterTrace.Mode.LINE ).name( "NKV=1" ).build();
	}
	static ScatterTrace horizontalNkvOneLine( Table table, String x2Name ){
		return ScatterTrace.builder(  new double[]{0., 1.1 * table.numberColumn( x2Name ).max()}, new double[]{1., 1.} ).mode( ScatterTrace.Mode.LINE ).name( "NKV=1" ).build();
	}
	static Axis.AxisBuilder axisBuilder() {
		return Axis.builder().titleFont( defaultFont );
	}
	static Layout.LayoutBuilder layoutBuilder() {
		return Layout.builder().width( plotWidth ).titleFont( defaultFont );
	}
}
