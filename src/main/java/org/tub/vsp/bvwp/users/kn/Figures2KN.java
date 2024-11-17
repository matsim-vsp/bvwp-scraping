package org.tub.vsp.bvwp.users.kn;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.tub.vsp.bvwp.data.Headers;
import org.tub.vsp.bvwp.data.HeadersKN;
import tech.tablesaw.api.DoubleColumn;
import tech.tablesaw.api.Table;
import tech.tablesaw.io.csv.CsvWriteOptions;
import tech.tablesaw.io.csv.CsvWriter;
import tech.tablesaw.plotly.components.Axis;
import tech.tablesaw.plotly.components.Figure;
import tech.tablesaw.plotly.components.Layout;
import tech.tablesaw.plotly.traces.ScatterTrace;
import tech.tablesaw.plotly.traces.Trace;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static org.tub.vsp.bvwp.data.Headers.*;

class Figures2KN extends Figures1KN {
	private static final Logger log = LogManager.getLogger(Figures2KN.class );
	Figures2KN( Table table ){
		super( table, null);
	}
	// ========================================================================================
	// ========================================================================================
	public Figure nkv_vs_dtv( String whichNKV ){
		String xName = VERKEHRSBELASTUNG_PLANFALL;
		Axis xAxis = Axis.builder().title(xName ).titleFont( defaultFont ).build();

		String yName = Headers.addCap( 20, table, whichNKV );

		Table table2 = table.sortAscendingOn( xName ); // cannot remember why this is necessary

		Axis yAxis = Figures1KN.axisBuilder().title( whichNKV ).build(); // cannot use a a logarithmic y axis since it removes nkv < 1

		Layout layout = Layout.builder( "" ).xAxis( xAxis ).yAxis( yAxis ).width( plotWidth ).build();

		List<Trace> traces = new ArrayList<>();
		{
			// the nkv=1 line:
			double[] xx = new double[]{0., 1.1 * table2.numberColumn( xName ).max()};
			double[] yy = new double[]{1., 1.};
			traces.add( ScatterTrace.builder( xx, yy ).mode( ScatterTrace.Mode.LINE ).build() );
		}
		{
			traces.addAll( getTraceCyan( table2, xName, yName ) );
			traces.addAll( getTraceMagenta( table2, xName, yName ) );
			traces.addAll( getTraceOrange( table2, xName, yName ) );
			traces.addAll( getTraceRed( table2, xName, yName ) );
		}

		table2.addColumns( table2.doubleColumn( xName ).power( 2 ).setName( "power2" ) );

		switch ( whichNKV ) {
			case HeadersKN.NKV_ORIG -> {
				traces.add( ScatterTrace.builder( new double[]{60_000., 100_000.}, new double[]{0., 10.} ).mode( ScatterTrace.Mode.LINE ).name( "line to guide the eye" ).build() );
				traces.add( ScatterTrace.builder( new double[]{ 100_000., 140_000. }, new double[]{0., 10.} ).mode( ScatterTrace.Mode.LINE ).name( "line to guide they eye" ).build() );
			}
			case NKV_ELTTIME_CARBON700_INVCOSTTUD -> {
				traces.add( ScatterTrace.builder( new double[]{70_000., 110_000.}, new double[]{0., 10.} ).mode( ScatterTrace.Mode.LINE ).name( "line to guide the eye" ).build() );
				traces.add( ScatterTrace.builder( new double[]{ 110_000., 150_000. }, new double[]{0., 10.} ).mode( ScatterTrace.Mode.LINE ).name( "line to guide they eye" ).build() );
			}
			case NKV_ELTTIME_CARBON2000_EMOB_INVCOSTTUD -> {
				traces.add( ScatterTrace.builder( new double[]{70_000., 100_000.}, new double[]{0., 10.} ).mode( ScatterTrace.Mode.LINE ).name( "line to guide the eye" ).build() );
				traces.add( ScatterTrace.builder( new double[]{ 110_000., 140_000. }, new double[]{0., 10.} ).mode( ScatterTrace.Mode.LINE ).name( "line to guide they eye" ).build() );
			}
			default -> throw new IllegalStateException( "Unexpected value: " + whichNKV );
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
		}
		return new Figure( layout , traces.toArray(new Trace[]{}) );
	}
	// ========================================================================================
	// ========================================================================================
	public Figure invcosttud_vs_nkvEl03Cprice215Invcosttud( int cap ){
		String xName = Headers.cappedOf( cap, NKV_EL03_CARBON215_INVCOSTTUD );
		return investmentCost( cap, xName, INVCOST_TUD );
	}
	// ========================================================================================
	// ========================================================================================
	public Figure cumulativeCostTud_vs_nkvEl03Cprice215InvcostTud( int cap ){
		String xName = Headers.cappedOf( cap, NKV_EL03_CARBON215_INVCOSTTUD );
		return cumulativeInvestmentCostTud( cap, xName );
	}
	// ========================================================================================
	// ========================================================================================
	public Figure invcost50_vs_NkvEl03Cprice700InvcostTud(){
		String xName = NKV_EL03_CARBON700_INVCOSTTUD_CAPPED5;
		String yName = INVCOST_TUD;

		Table table2 = table.sortDescendingOn( xName ); // cannot remember why this is necessary

		Axis xAxis = Axis.builder().title(xName).titleFont( defaultFont ).autoRange( Axis.AutoRange.REVERSED )
				 .showZeroLine( false )
				 .zeroLineWidth( 0 )
				 .zeroLineColor( "lightgray" )
				 .range( nkvCappedMax, nkvMin )
				 .build();

		Axis yAxis = Axis.builder().title( yName ).titleFont( defaultFont ).build();

		Layout layout = Layout.builder( "" ).xAxis( xAxis ).yAxis( yAxis ).width( plotWidth ).build();

		List<Trace> traces = new ArrayList<>( getTracesByColor( table2, xName, yName  ) );

		traces.add( this.vertialNkvOneLine( yName ) );

		return new Figure( layout, traces.toArray(new Trace[]{} ) );
	}
	// ========================================================================================
	// ========================================================================================
	public Figure cumcost50_vs_nkvEl03Cprice700InvcostTud(){

		String xName = NKV_EL03_CARBON700_INVCOSTTUD_CAPPED5;

		Axis xAxis = Axis.builder().title(xName).titleFont( defaultFont ).autoRange( Axis.AutoRange.REVERSED )
				 .showZeroLine( false )
				 .zeroLineWidth( 0 )
				 .zeroLineColor( "lightgray" )
				 .range( nkvCappedMax, nkvMin )
				 .build();

		Table table2 = Table.create( table.stringColumn( PROJECT_NAME ), table.doubleColumn( INVCOST_TUD ), table.doubleColumn( xName ) );

		table2 = table2.sortDescendingOn( xName ); // necessary to get cumulative cost right

		DoubleColumn cumulativeCost = DoubleColumn.create( "cumulative_cost" );
		{
			double sum = 0.;
			for( Double cost : table2.doubleColumn( INVCOST_TUD ) ){
				sum += cost;
				cumulativeCost.append( sum );
			}
		}

		new CsvWriter().write( table2, CsvWriteOptions.builder( new File( "cumCosts.tsv" ) ).separator( '\t' ).usePrintFormatters( true ).build() );

		String yName = "cumulative_cost";
		Axis yAxis = Axis.builder().title( yName )
				 .titleFont( defaultFont ).build();

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

		String xName = Headers.addCap( 5, table, HeadersKN.NKV_ORIG );

		Axis xAxis = Axis.builder().title(xName).titleFont( defaultFont ).autoRange( Axis.AutoRange.REVERSED )
				 .showZeroLine( false )
				 .zeroLineWidth( 0 )
				 .zeroLineColor( "lightgray" )
				 .range( nkvCappedMax, nkvMin )
				 .build();

		Table table2 = Table.create( table.stringColumn( PROJECT_NAME ), table.doubleColumn( INVCOST_BARWERT_ORIG ), table.doubleColumn( xName ) ).sortDescendingOn( xName );

		DoubleColumn cumulativeCost = DoubleColumn.create( "cumulative_cost" );
		{
			double sum = 0.;
			for( Double cost : table2.doubleColumn( INVCOST_BARWERT_ORIG ) ){
				sum += cost;
				cumulativeCost.append( sum );
			}
		}

		new CsvWriter().write( table2, CsvWriteOptions.builder( new File( "cumCostsOrig.tsv" ) ).separator( '\t' ).usePrintFormatters( true ).build() );

		Axis yAxis = Axis.builder().titleFont( defaultFont ).title( "Kumulierte Investitionskosten (orig) [Mio]" ).build();

		Layout layout = Layout.builder( "" ).xAxis( xAxis ).yAxis( yAxis ).width( plotWidth ).build();

		List<Trace> traces = new ArrayList<>();

		var nameInLegend = "cumulative cost";
		traces.add( ScatterTrace.builder( table2.doubleColumn( xName ), cumulativeCost ).mode( ScatterTrace.Mode.LINE ).showLegend( true ).name( String.format( legendFormat, nameInLegend ) ).build() );

		return new Figure( layout, traces.toArray(new Trace[]{} ) );
	}
	// ========================================================================================
	// ========================================================================================
	public Figure cost_VS_nkvOrig(){
		String xName = Headers.addCap( 5, table, HeadersKN.NKV_ORIG );

		Axis xAxis = Axis.builder().titleFont( defaultFont ).title(xName).autoRange( Axis.AutoRange.REVERSED )
//				 .visible( false )
//				 .showLine( false )
				 .showZeroLine( false )
				 .zeroLineWidth( 0 )
				 .zeroLineColor( "lightgray" )
				 .range( nkvCappedMax, nkvMin )
				 .build();

		Table table2 = table.sortDescendingOn( xName ); // cannot remember why this is necessary

		String yName = INVCOST_BARWERT_ORIG;
		Axis yAxis = Axis.builder().titleFont( defaultFont ).title( yName )
//				 .showZeroLine( false )
//				 .showLine( false )
//				 .visible( false )
//				 .zeroLineWidth( 100 )
//				 .type( Axis.Type.LOG )
				 .build();

		String title = "";
		Layout layout = Layout.builder( title ).xAxis( xAxis ).yAxis( yAxis ).width( plotWidth ).build();

		List<Trace> traces = new ArrayList<>( getTracesByColor( table2, xName, yName ) );

		// the nkv=1 line:
		double[] xx = new double[]{1., 1.};
		double[] yy = new double[]{0., 1.1* table2.numberColumn( yName ).max() };
		traces.add( ScatterTrace.builder( xx, yy ).mode( ScatterTrace.Mode.LINE ).name("NKV=1").build() );

		return new Figure( layout, traces.toArray( new Trace[]{} ) );
	}
	// ========================================================================================
	// ========================================================================================
	Figure carbon_vs_nkvEl03Cprice215Invcost50Capped5(){
		String xName = NKV_EL03_CARBON215_INVCOSTTUD_CAPPED5;

		Axis xAxis = Axis.builder().title(xName).titleFont( defaultFont ).autoRange( Axis.AutoRange.REVERSED )
				 .showZeroLine( false )
				 .zeroLineWidth( 0 )
				 .zeroLineColor( "lightgray" )
				 .range( nkvCappedMax, nkvMin )
				 .build();


		String yName = B_CO2_ORIG;
		String y2Name = CO2_COST_EL03;

		Axis yAxis = Axis.builder().title( yName ).titleFont( defaultFont ).build();

		String title = "";
		if ( ADDTL_LANE_KM.equals( xName ) ) {
			title = "CO2 costs SIMTO addtl lane-km:";
		}

		Layout layout = Layout.builder( title ).xAxis( xAxis ).yAxis( yAxis ).width( plotWidth ).build();

		List<Trace> traces = new ArrayList<>( getTracesByColor( table, xName, y2Name ) );

		traces.add( vertialNkvOneLine( y2Name ) );

		return new Figure( layout, traces.toArray( new Trace[]{} ) );
	}
	// ========================================================================================
	// ========================================================================================
	public List<Figure> getFigures( int cap, String xNameOrig ){
		List<Figure> figures = new ArrayList<>();
//		String xName = Headers.cappedOf( cap, xNameOrig );
		figures.add( investmentCost( cap, xNameOrig, INVCOST_TUD ) );
		figures.add( carbon( cap, xNameOrig ) );
//		figures.add( cumulativeInvestmentCostTud( cap, xName ) );
		figures.add( cumBenefitVsCumCost( xNameOrig ) );
		figures.add( cumBenefitVsCumCarbon( xNameOrig ));
		return figures;
	}
	// ========================================================================================
	// ========================================================================================
	public Figure nkvNew_vs_nkvOrig( int cap, String yName ){
		String xName = HeadersKN.NKV_ORIG;
		String x2Name = Headers.addCap( cap, table, HeadersKN.NKV_ORIG );
		Axis.AxisBuilder xAxisBuilder = Axis.builder()
						    .autoRange( Axis.AutoRange.REVERSED )
						    .zeroLineWidth( 0 ).zeroLineColor( "white" );
//		xAxisBuilder.range( 0, 50 );

		Table table2 = table.sortDescendingOn( x2Name ); // cannot remember why this is necessary

		String y2Name = Headers.addCap( cap, table2, yName );

		Axis yAxis = Axis.builder().titleFont( defaultFont ).title( yName ).zeroLineWidth( 0 ).zeroLineColor( "white" )
//				 .range(-5,50)
				 .build();

		Layout layout = Layout.builder( "" ).margin( defaultMargin ).xAxis( xAxisBuilder.title( xName ).titleFont( defaultFont ).build() ).yAxis( yAxis ).width( plotWidth ).build();

		List<Trace> traces = new ArrayList<>( getTracesByColor( table2, x2Name, y2Name ) );
		traces.add( diagonalLine2( table2, x2Name, y2Name ) );
		traces.add( horizontalNkvOneLine( table2, x2Name ) );
		traces.add( vertialNkvOneLine( table2, y2Name ) );
		return new Figure( layout, traces.toArray( new Trace[]{} ) );
	}

	// ========================================================================================
	// ========================================================================================
	public Figure nProCo2_vs_nkv( String xName, String yName ){
		Axis.AxisBuilder xAxisBuilder = Axis.builder().autoRange( Axis.AutoRange.REVERSED );

		Axis.AxisBuilder yAxisBuilder = Axis.builder().titleFont( defaultFont ).title( yName );

		Table table2 = table.sortDescendingOn( xName ); // cannot remember why this is necessary

		Layout layout = Layout.builder( "" ).margin( defaultMargin ).xAxis( xAxisBuilder.title( xName ).titleFont( defaultFont ).build() ).yAxis( yAxisBuilder.build() ).width( plotWidth ).build();

		List<Trace> traces = new ArrayList<>( getTracesByColor( table2, xName, yName ) );
//		traces.add( diagonalLine2( table, x2Name, y2Name ) );
//		traces.add( horizontalNkvOneLine( table, x2Name ) );
		return new Figure( layout, traces.toArray( new Trace[]{} ) );
	}

	// ========================================================================================
	// ========================================================================================
	public Figure carbon_vs_invcostTud(){
		String xName = INVCOST_TUD;
		Axis.AxisBuilder xAxisBuilder = Axis.builder();

		Table table2 = table.sortDescendingOn( xName ); // cannot remember why this is necessary

		String yName = CO2_ELTTIME;
		Axis yAxis = Axis.builder().titleFont( defaultFont ).title( yName ).build();

		Layout layout = Layout.builder( "CO2-Kosten vs Investitionskosten" ).margin( defaultMargin ).xAxis( xAxisBuilder.titleFont( defaultFont ).title( xName ).build() ).yAxis( yAxis ).width( plotWidth ).build();

		List<Trace> traces = new ArrayList<>( getTracesByColor( table2, xName, yName ));
//		traces.add( ScatterTrace.builder( new double[]{0., 4000.}, new double[]{0., 1900.} ).mode( ScatterTrace.Mode.LINE ).name( "line to guide the eye" ).build() );
		return new Figure( layout, traces.toArray( new Trace[]{} ) );
	}

	// ========================================================================================
	// ========================================================================================
	public Figure nco2v_vs_vs_nkvElttimeCarbon700Invcosttud( int cap ){
		String xName = Headers.cappedOf( cap, NKV_ELTTIME_CARBON700_INVCOSTTUD );
		Axis.AxisBuilder xAxisBuilder = Axis.builder().titleFont( defaultFont );

		if ( cap ==Integer.MAX_VALUE ) {
			xName = NKV_ELTTIME_CARBON700_INVCOSTTUD;
			xAxisBuilder.autoRange( Axis.AutoRange.REVERSED );
		} else {
			xAxisBuilder.range( nkvCappedMax, nkvMin );
		}

		Table table2 = Table.create( table.stringColumn( PROJECT_NAME )
				, table.stringColumn( BAUTYP )
				, table.numberColumn( EINSTUFUNG_AS_NUMBER )
				, table.doubleColumn( NKV_ELTTIME_CARBON700_INVCOSTTUD )
				, table.doubleColumn( xName )
				, table.doubleColumn( INVCOST_TUD )
				, table.doubleColumn( CO2_COST_EL03 ) // should be ELTTIME!!
					   );

		final String N_CO2_V = "N-CO2-V";
		table2.addColumns( table2.doubleColumn( NKV_ELTTIME_CARBON700_INVCOSTTUD )
					 .multiply( table2.doubleColumn( INVCOST_TUD ) )
					 .divide( table2.doubleColumn( CO2_COST_EL03 ) ).setName( N_CO2_V )
				 ) ;


		String yName = N_CO2_V;
		Axis yAxis = Axis.builder().title( yName ).titleFont( defaultFont ).build();

		Layout layout = Layout.builder( "" ).xAxis( xAxisBuilder.title( xName ).build() ).yAxis( yAxis ).width( plotWidth ).build();

		List<Trace> traces = new ArrayList<>( getTracesByColor( table2, xName, yName ) );

		// the nkv=1 line:
		double[] xx = new double[]{1., 1.};
		double[] yy = new double[]{0., 1.1* table2.numberColumn( yName ).max() };
		traces.add( ScatterTrace.builder( xx, yy ).mode( ScatterTrace.Mode.LINE ).name("NKV=1").build() );

		return new Figure( layout, traces.toArray( new Trace[]{} ) );
	}

	// ========================================================================================
	// ========================================================================================
	public Figure invcosttud_vs_nkvElttimeCarbon2000Invcosttud(){
		String xName = Headers.capped5Of( NKV_ELTTIME_CARBON2000_INVCOSTTUD );
		String y2Name = INVCOST_TUD;

		Axis xAxis = Axis.builder().title( xName ).titleFont( defaultFont ).autoRange( Axis.AutoRange.REVERSED )
//				 .range( nkvCappedMax, nkvMin )
								   .build();

		Axis yAxis = Axis.builder().title( y2Name ).titleFont( defaultFont ).build();

		Layout layout = Layout.builder("").xAxis( xAxis ).yAxis( yAxis ).width( plotWidth ).build();

		List<Trace> traces = new ArrayList<>( getTracesByColor( table, xName, y2Name  ) );

		traces.add( vertialNkvOneLine( y2Name ) );

		return new Figure( layout, traces.toArray(new Trace[]{} ) );
	}

	// ========================================================================================
	// ========================================================================================
	private ScatterTrace vertialNkvOneLine( String y2Name ){
		return vertialNkvOneLine( table, y2Name );
	}
	// ################################################################
	// ################################################################
	Figure invcost_tud_vs_orig(){
		String xName = INVCOST_BARWERT_ORIG;

		String yName = INVCOST_TUD;
//		String y3Name = Headers.COST_OVERALL;
		String y2Name = INVCOST_TUD;

		Axis xAxis = Axis.builder()
				 .title( xName )
//				 .type( Axis.Type.LOG)
				 .titleFont( defaultFont ).build();

		Axis yAxis = Axis.builder()
//				 .type( Axis.Type.LOG )
				 //                             .range( 1.1*table.numberColumn( y2Name ).min(),4. )
				 .title( yName )
				 .titleFont( defaultFont ).build();
		Layout layout = Layout.builder( "Investitionskosten neu vs Investitionskosten lt. PRINS" )
				      .xAxis( xAxis )
				      .yAxis( yAxis )
				      .width( plotWidth )
				      .build();

		List<Trace> traces = new ArrayList<>( getTracesByColor( table, xName, y2Name ) );

//		Trace trace = ScatterTrace.builder( table.numberColumn( xName ), table.numberColumn( yName ) )
//					  .text( table.stringColumn( Headers.PROJECT_NAME ).asObjectArray() )
//					  .name( String.format( legendFormat, yName ) )
//					  .build();

		{
			double[] xx = new double[]{ table.numberColumn( xName ).min(), table.numberColumn( xName ).max() };
			double[] yy = new double[]{ table.numberColumn( xName ).min(), table.numberColumn( xName ).max() };
			traces.add( ScatterTrace.builder( xx, yy )
						   .mode( ScatterTrace.Mode.LINE )
						   .build() );
		}
		Figure figure3 = new Figure( layout, traces.toArray( new Trace[0] ) );
		return figure3;
	}
	public Figure fzkmFromTtime_vs_fzkmOrig(){
		String xName = ADDTL_PKWKM_ORIG;

		String yName = ADDTL_PKWKM_FROM_TTIME;
		String y2Name = yName;

		Axis xAxis = Axis.builder().title( xName ).titleFont( defaultFont ).build();

		Axis yAxis = Axis.builder().title( yName ).titleFont( defaultFont ).build();

		Layout layout = Layout.builder().xAxis( xAxis ).yAxis( yAxis ).width( plotWidth ).build();

		List<Trace> traces = new ArrayList<>();
		traces.addAll( getTracesByColor( table, xName, y2Name ) );
		traces.add( diagonalLine2( table, xName, y2Name ) );
		return new Figure( layout, traces.toArray(new Trace[]{} ) );
	}
	public Figure fzkmEl03_vs_fzkmOrig(){
		String xName = ADDTL_PKWKM_ORIG;

		String yName = ADDTL_PKWKM_EL03;
		String y2Name = yName;

		Axis xAxis = Axis.builder().title( xName ).titleFont( defaultFont ).build();

		Axis yAxis = Axis.builder().title( yName ).titleFont( defaultFont ).build();

		Layout layout = Layout.builder().xAxis( xAxis ).yAxis( yAxis ).width( plotWidth ).build();

		List<Trace> traces = new ArrayList<>();
		traces.addAll( getTracesByColor( table, xName, y2Name ) );
		traces.add( diagonalLine2( table, xName, y2Name ) );
		return new Figure( layout, traces.toArray(new Trace[]{} ) );
	}
	public Figure fzkmFromTtimeDelta_vs_fzkmOrig(){
		String x2Name = ADDTL_PKWKM_ORIG;

		String yName = ADDTL_PKWKM_FROM_TTIME_DIFF;
		String y2Name = ADDTL_PKWKM_FROM_TTIME_DIFF;

		Table table2 = Table.create( table.column( PROJECT_NAME )
				, table.stringColumn( BAUTYP )
				, table.column( EINSTUFUNG_AS_NUMBER )
				, table.doubleColumn( x2Name )
				, table.doubleColumn( ADDTL_PKWKM_FROM_TTIME )
				, table.doubleColumn( ADDTL_PKWKM_FROM_TTIME ).subtract( table.doubleColumn( x2Name ) ).setName( y2Name )
					   );

		Axis xAxis = Axis.builder().title( x2Name ).titleFont( defaultFont ).build();

		Axis yAxis = Axis.builder().title( yName ).titleFont( defaultFont ).build();

		Layout layout = Layout.builder().xAxis( xAxis ).yAxis( yAxis ).width( plotWidth ).build();

		List<Trace> traces = new ArrayList<>();
		traces.addAll( getTracesByColor( table2, x2Name, y2Name ) );
		return new Figure( layout, traces.toArray(new Trace[]{} ) );
	}
	public Figure fzkmFromEl03Delta_vs_fzkmOrig(){
		String xName = ADDTL_PKWKM_ORIG;

		String yName = ADDTL_PKWKM_EL03_DIFF;
		String y2Name = yName;

		Table table2 = Table.create( table.column( PROJECT_NAME )
				, table.stringColumn( BAUTYP )
				, table.column( EINSTUFUNG_AS_NUMBER )
				, table.doubleColumn( xName )
				, table.doubleColumn( ADDTL_PKWKM_EL03 )
				, table.doubleColumn( ADDTL_PKWKM_EL03 ).subtract( table.doubleColumn( xName ) ).setName( y2Name )
					   );

		Axis xAxis = Axis.builder().title( xName ).titleFont( defaultFont ).build();

		Axis yAxis = Axis.builder().title( yName ).titleFont( defaultFont ).build();

		Layout layout = Layout.builder().xAxis( xAxis ).yAxis( yAxis ).width( plotWidth ).build();

		List<Trace> traces = new ArrayList<>();
		traces.addAll( getTracesByColor( table2, xName, y2Name ) );
		return new Figure( layout, traces.toArray(new Trace[]{} ) );
	}
	public Figure fzkmFromTtimeSum_vs_fzkmOrig(){
		String xName = ADDTL_PKWKM_ORIG;

		final String ADDTL_PKWKM_FROM_TTIME_PLUS_ORIG = "additional pkwkm from ttime plus orig";

		table.addColumns(  table.numberColumn( ADDTL_PKWKM_FROM_TTIME ).add( table.numberColumn( ADDTL_PKWKM_ORIG ) ).setName( ADDTL_PKWKM_FROM_TTIME_PLUS_ORIG ) );

		String y2Name = ADDTL_PKWKM_FROM_TTIME_PLUS_ORIG;

		Axis xAxis = Axis.builder().title( xName ).titleFont( defaultFont ).build();

		Axis yAxis = Axis.builder().title( y2Name ).titleFont( defaultFont ).build();

		Layout layout = Layout.builder().xAxis( xAxis ).yAxis( yAxis ).width( plotWidth ).build();

		List<Trace> traces = new ArrayList<>();
		traces.addAll( getTracesByColor( table, xName, y2Name ) );
		return new Figure( layout, traces.toArray(new Trace[]{} ) );
	}
	Figure investmentCost( int cap, String xName, String yName ){
		Axis.AxisBuilder xAxisBuilder = Axis.builder().zeroLineWidth( 0 ).zeroLineColor( "white" );

		if ( cap ==Integer.MAX_VALUE ) {
			xAxisBuilder.autoRange( Axis.AutoRange.REVERSED );
		} else {
			xAxisBuilder.range( nkvCappedMax, nkvMin );
		}

		Table table2 = table.sortDescendingOn( xName ); // cannot remember why this is necessary

		Axis yAxis = Axis.builder().title( yName ).titleFont( defaultFont ).build();

		Layout layout = Layout.builder( "" ).margin( defaultMargin ).xAxis( xAxisBuilder.title( xName ).titleFont( defaultFont ).build() ).yAxis( yAxis ).width( plotWidth ).build();

		List<Trace> traces = new ArrayList<>( getTracesByColor( table2, xName, yName ));
		if ( xName.contains( "NKV" ) ){
			traces.add( vertialNkvOneLine( table2, yName ) );
		}
		return new Figure( layout, traces.toArray( new Trace[]{} ) );
	}
	Figure carbonOrig( int cap, String xName ){
		Axis.AxisBuilder xAxisBuilder = Axis.builder().zeroLineWidth( 0 ).zeroLineColor( "white" );

		if ( xName.contains( "Nutzen_pro" )){
			xAxisBuilder.range( 6000, 0 );
		} else if ( cap ==Integer.MAX_VALUE ) {
			xAxisBuilder.autoRange( Axis.AutoRange.REVERSED );
		} else {
			xAxisBuilder.range( nkvCappedMax, nkvMin );
		}

		Table table2 = table.sortDescendingOn( xName ); // cannot remember why this is necessary

		String yName = CO2_ORIG;
		Axis yAxis = Axis.builder().title( yName ).titleFont( defaultFont ).build();

		Layout layout = Layout.builder( "" ).margin( defaultMargin ).xAxis( xAxisBuilder.title( xName ).titleFont( defaultFont ).build() ).yAxis( yAxis ).width( plotWidth ).build();

		List<Trace> traces = new ArrayList<>( getTracesByColor( table2, xName, yName ));
		if ( xName.contains( "NKV" ) ){
			traces.add( vertialNkvOneLine( table2, yName ) );
		}
		return new Figure( layout, traces.toArray( new Trace[]{} ) );
	}
	Figure carbon( int cap, String xName ){
		Axis.AxisBuilder xAxisBuilder = Axis.builder().zeroLineWidth( 0 ).zeroLineColor( "white" );

		if ( cap ==Integer.MAX_VALUE ) {
			xAxisBuilder.autoRange( Axis.AutoRange.REVERSED );
		} else {
			xAxisBuilder.range( nkvCappedMax, nkvMin );
		}

		Table table2 = table.sortDescendingOn( xName ); // cannot remember why this is necessary

		String yName = CO2_ELTTIME;
		Axis yAxis = Axis.builder().title( yName ).titleFont( defaultFont ).build();

		Layout layout = Layout.builder( "" ).margin( defaultMargin ).xAxis( xAxisBuilder.title( xName ).titleFont( defaultFont ).build() ).yAxis( yAxis ).width( plotWidth ).build();

		List<Trace> traces = new ArrayList<>( getTracesByColor( table2, xName, yName ));
		if ( xName.contains( "NKV" ) ){
			traces.add( vertialNkvOneLine( table2, yName ) );
		}
		return new Figure( layout, traces.toArray( new Trace[]{} ) );
	}
	Figure carbonWithEmob( int cap, String xName ){
		Axis.AxisBuilder xAxisBuilder = Axis.builder().zeroLineWidth( 0 ).zeroLineColor( "white" );

		if ( cap ==Integer.MAX_VALUE ) {
			xAxisBuilder.autoRange( Axis.AutoRange.REVERSED );
		} else {
			xAxisBuilder.range( nkvCappedMax, nkvMin );
		}

		Table table2 = table.sortDescendingOn( xName ); // cannot remember why this is necessary

		String yName = CO2_ELTTIME_EMOB;
		Axis yAxis = Axis.builder().title( yName ).titleFont( defaultFont ).build();

		Layout layout = Layout.builder( "" ).margin( defaultMargin ).xAxis( xAxisBuilder.title( xName ).titleFont( defaultFont ).build() ).yAxis( yAxis ).width( plotWidth ).build();

		List<Trace> traces = new ArrayList<>( getTracesByColor( table2, xName, yName ));
		if ( xName.contains( "NKV" ) ){
			traces.add( vertialNkvOneLine( table2, yName ) );
		}
		return new Figure( layout, traces.toArray( new Trace[]{} ) );
	}
	private Figure cumulativeInvestmentCostTud( int cap, String xName ){
		Axis.AxisBuilder xAxisBuilder = Axis.builder();
		if ( cap ==Integer.MAX_VALUE ) {
			xAxisBuilder.autoRange( Axis.AutoRange.REVERSED );
		} else {
			xAxisBuilder.range( nkvCappedMax, nkvMin );
		}

		String columnToCumulate = INVCOST_TUD;
		String yName = "cumulative_" + columnToCumulate;

		final Table table2 = createCumulatedTable( xName, columnToCumulate, yName, table );

//		new CsvWriter().write( table2, CsvWriteOptions.builder( new File( "cumCosts.tsv" ) ).separator( '\t' ).usePrintFormatters( true ).build() );

		Axis xAxis = xAxisBuilder.title( xName ).titleFont( defaultFont ).build();
		Axis yAxis = Axis.builder().title( yName ).titleFont( defaultFont ).build();
		Layout layout = Layout.builder( "Kumulierte Investitionskosten:" ).margin( defaultMargin ).xAxis( xAxis ).yAxis( yAxis ).width( plotWidth ).build();

		List<Trace> traces = new ArrayList<>();

		var nameInLegend = "cumulative cost";
		traces.add( ScatterTrace.builder( table2.doubleColumn( xName ), table2.doubleColumn(yName) ).mode( ScatterTrace.Mode.LINE ).showLegend( true ).name( String.format( legendFormat, nameInLegend ) ).build() );

		traces.add( vertialNkvOneLine( table2, yName ) );

		return new Figure( layout, traces.toArray( new Trace[]{} ) );
	}
	private Figure cumBenefitVsCumCarbon( String nkvToUseOrig ){
		String nkvToUse = Headers.cappedOf( 30, nkvToUseOrig );
		Headers.addCap( 30, table, nkvToUseOrig );

		Axis.AxisBuilder xAxisBuilder = Axis.builder().titleFont( defaultFont );

		final String BENEFIT = "benefit";

		// add benefits row:
		Table tableTmp = Table.create( table.stringColumn( PROJECT_NAME ), table.doubleColumn( nkvToUse ), table.doubleColumn( CO2_ELTTIME ) );
		tableTmp.addColumns(  table.doubleColumn( nkvToUse ).multiply( table.doubleColumn( INVCOST_TUD ) ).setName( BENEFIT ) );

		tableTmp = tableTmp.sortDescendingOn( nkvToUse ); // necessary to get cumulations right

		System.out.println( tableTmp.print() );

		// add cumulative benefits row:
		String yName = "cumulative_" + BENEFIT;
		{
			DoubleColumn cumulativeCost = DoubleColumn.create( yName );
			double sum = 0.;
			for( Double cost : tableTmp.doubleColumn( BENEFIT ) ){
				sum += cost;
				cumulativeCost.append( sum );
			}
			tableTmp.addColumns( cumulativeCost );

		}
		// add cumulative costs row:
		String xName = "cumulative_" + CO2_ELTTIME;
		{
			DoubleColumn cumulativeCost = DoubleColumn.create( xName );
			double sum = 0.;
			for( Double cost : tableTmp.doubleColumn( CO2_ELTTIME ) ){
				sum += cost;
				cumulativeCost.append( sum );
			}
			tableTmp.addColumns( cumulativeCost );
		}

//		new CsvWriter().write( table2, CsvWriteOptions.builder( new File( "cumCosts.tsv" ) ).separator( '\t' ).usePrintFormatters( true ).build() );

		Axis xAxis = xAxisBuilder.title( xName ).build();
		Axis yAxis = Axis.builder().titleFont( defaultFont ).title( yName ).build();
		Layout layout = Layout.builder( "Kumulierte Nutzen vs kumulierter CO2-Ausstoss:" ).margin( defaultMargin ).xAxis( xAxis ).yAxis( yAxis ).width( plotWidth ).build();

		List<Trace> traces = new ArrayList<>();

		var nameInLegend = "cumulative cost";
		traces.add( ScatterTrace.builder( tableTmp.doubleColumn( xName ), tableTmp.doubleColumn(yName) ).mode( ScatterTrace.Mode.LINE ).showLegend( true ).name( String.format( legendFormat, nameInLegend ) ).build() );

		return new Figure( layout, traces.toArray( new Trace[]{} ) );
	}
	Figure cumBenefitVsCumCost( String nkvToUseOrig ){
		String nkvToUse = Headers.cappedOf( 30, nkvToUseOrig );
		Headers.addCap( 30, table, nkvToUseOrig );

		Axis.AxisBuilder xAxisBuilder = Axis.builder().titleFont( defaultFont );

		final String BENEFIT = "benefit";

		// add benefits row:
		Table tableTmp = Table.create( table.stringColumn( PROJECT_NAME ), table.doubleColumn( nkvToUse ), table.doubleColumn( INVCOST_TUD ) );
		tableTmp.addColumns(  table.doubleColumn( nkvToUse ).multiply( table.doubleColumn( INVCOST_TUD ) ).setName( BENEFIT ) );

		tableTmp = tableTmp.sortDescendingOn( nkvToUse ); // necessary to get cumulations right

//		System.out.println( tableTmp.print() );

		// add cumulative benefits row:
		String yName = "cumulative_" + BENEFIT;
		{
			DoubleColumn cumulativeCost = DoubleColumn.create( yName );
			double sum = 0.;
			for( Double cost : tableTmp.doubleColumn( BENEFIT ) ){
				sum += cost;
				cumulativeCost.append( sum );
			}
			tableTmp.addColumns( cumulativeCost );

		}
		// add cumulative costs row:
		String xName = "cumulative_" + INVCOST_TUD;
		{
			DoubleColumn cumulativeCost = DoubleColumn.create( xName );
			double sum = 0.;
			for( Double cost : tableTmp.doubleColumn( INVCOST_TUD ) ){
				sum += cost;
				cumulativeCost.append( sum );
			}
			tableTmp.addColumns( cumulativeCost );
		}

//		new CsvWriter().write( table2, CsvWriteOptions.builder( new File( "cumCosts.tsv" ) ).separator( '\t' ).usePrintFormatters( true ).build() );

		Axis xAxis = xAxisBuilder.title( xName ).build();
		Axis yAxis = Axis.builder().title( yName ).build();
		Layout layout = Layout.builder( "Kumulierter Nutzen vs kumulierte Investitionskosten:" ).margin(defaultMargin).xAxis( xAxis ).yAxis( yAxis ).width( plotWidth ).build();

		List<Trace> traces = new ArrayList<>();

		var nameInLegend = "cumulative cost";
		traces.add( ScatterTrace.builder( tableTmp.doubleColumn( xName ), tableTmp.doubleColumn(yName) ).mode( ScatterTrace.Mode.LINE ).showLegend( true ).name( String.format( legendFormat, nameInLegend ) ).build() );

		return new Figure( layout, traces.toArray( new Trace[]{} ) );
	}
	private static Table createCumulatedTable( String xName, String columnToCumulate, String yName, Table inputTable ){
		Table table2 = Table.create( inputTable.stringColumn( PROJECT_NAME ), inputTable.doubleColumn( columnToCumulate ), inputTable.doubleColumn( xName ) );
		table2 = table2.sortDescendingOn( xName ); // necessary to get cumulative cost right
		DoubleColumn cumulativeCost = DoubleColumn.create( yName );
		{
			double sum = 0.;
			for( Double cost : table2.doubleColumn( columnToCumulate ) ){
				sum += cost;
				cumulativeCost.append( sum );
			}
			table2.addColumns( cumulativeCost );
		}
		return table2;
	}


}
