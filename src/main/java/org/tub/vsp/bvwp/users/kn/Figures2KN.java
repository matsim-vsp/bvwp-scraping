package org.tub.vsp.bvwp.users.kn;

import org.tub.vsp.bvwp.data.Headers;
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
	Figures2KN( Table table ){
		super( table );
	}
	// ========================================================================================
	// ========================================================================================
	public Figure nkvVsDtv(){
		String xName = VERKEHRSBELASTUNG_PLANFALL;
		Axis xAxis = Axis.builder().title(xName ).build();

		Table table2 = table.sortAscendingOn( xName ); // cannot remember why this is necessary

		String yName = NKV_EL03_CARBON215_INVCOSTTUD_CAPPED10;
		Axis yAxis = Axis.builder().title( yName ).build(); // cannot use a a logarithmic y axis since it removes nkv < 1

		String title = "";
		if ( xName.equals( VERKEHRSBELASTUNG_PLANFALL ) ){
			title = "nkv_nb normally low; nkv_knotenpunkt often high; nkv_ew depends on dtv. " + dotSizeString;
		}
		Layout layout = Layout.builder( title ).xAxis( xAxis ).yAxis( yAxis ).width( plotWidth ).build();

		List<Trace> traces = new ArrayList<>();
		{
			// the nkv=1 line:
			double[] xx = new double[]{0., 1.1 * table2.numberColumn( xName ).max()};
			double[] yy = new double[]{1., 1.};
			traces.add( ScatterTrace.builder( xx, yy ).mode( ScatterTrace.Mode.LINE ).build() );
		}
		{
			traces.add( getTraceCyan( table2, xName, yName ) );
			traces.add( getTraceMagenta( table2, xName, yName ) );
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
			double[] xx = new double[]{ 60_000., 100_000. };
			double[] yy = new double[]{0., 10.};
			traces.add( ScatterTrace.builder( xx, yy ).mode( ScatterTrace.Mode.LINE ).name( "line to guide the eye" ).build() );
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
			double[] xx = new double[]{ 100_000., 140_000. };
			double[] yy = new double[]{0., 10.};
			traces.add( ScatterTrace.builder( xx, yy ).mode( ScatterTrace.Mode.LINE ).name( "line to guide they eye" ).build() );
		}
		return new Figure( layout , traces.toArray(new Trace[]{}) );
	}
	// ========================================================================================
	// ========================================================================================
	public Figure invcosttud_vs_nkvEl03Cprice215Invcosttud( int cap ){
		String xName = Headers.cappedOf( cap, NKV_EL03_CARBON215_INVCOSTTUD );
		return investmentCostTud( cap, xName );
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

		Axis xAxis = Axis.builder().title(xName).autoRange( Axis.AutoRange.REVERSED )
				 .showZeroLine( false )
				 .zeroLineWidth( 0 )
				 .zeroLineColor( "lightgray" )
				 .range( nkvCappedMax, nkvMin )
				 .build();

		Axis yAxis = Axis.builder().title( yName ).build();

		Layout layout = Layout.builder( "" ).xAxis( xAxis ).yAxis( yAxis ).width( plotWidth ).build();

		List<Trace> traces = new ArrayList<>();

		traces.add( this.vertialNkvOneLine( yName ) );

		traces.add( getTraceCyan( table2, xName, yName ) );
		traces.add( getTraceMagenta( table2, xName, yName ) );
		traces.add( getTraceOrange( table2, xName, yName ) );
		traces.add( getTraceRed( table2, xName, yName ) );

		return new Figure( layout, traces.toArray(new Trace[]{} ) );
	}
	// ========================================================================================
	// ========================================================================================
	public Figure cumcost50_vs_nkvEl03Cprice700InvcostTud(){

		String xName = NKV_EL03_CARBON700_INVCOSTTUD_CAPPED5;

		Axis xAxis = Axis.builder().title(xName).autoRange( Axis.AutoRange.REVERSED )
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

		Table table2 = Table.create( table.stringColumn( PROJECT_NAME ), table.doubleColumn( INVCOST_ORIG ), table.doubleColumn( xName ) ).sortDescendingOn( xName );

		DoubleColumn cumulativeCost = DoubleColumn.create( "cumulative_cost" );
		{
			double sum = 0.;
			for( Double cost : table2.doubleColumn( INVCOST_ORIG ) ){
				sum += cost;
				cumulativeCost.append( sum );
			}
		}

		new CsvWriter().write( table2, CsvWriteOptions.builder( new File( "cumCostsOrig.tsv" ) ).separator( '\t' ).usePrintFormatters( true ).build() );

		Axis yAxis = Axis.builder().title( "Kumulierte Investitionskosten (orig) [Mio]" ).build();

		Layout layout = Layout.builder( "" ).xAxis( xAxis ).yAxis( yAxis ).width( plotWidth ).build();

		List<Trace> traces = new ArrayList<>();

		var nameInLegend = "cumulative cost";
		traces.add( ScatterTrace.builder( table2.doubleColumn( xName ), cumulativeCost ).mode( ScatterTrace.Mode.LINE ).showLegend( true ).name( String.format( legendFormat, nameInLegend ) ).build() );

		return new Figure( layout, traces.toArray(new Trace[]{} ) );
	}
	// ========================================================================================
	// ========================================================================================
	public Figure cost_VS_nkvOrig(){
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

		String yName = INVCOST_ORIG;
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
	Figure carbon_vs_nkvEl03Cprice215Invcost50Capped5(){
		String xName = NKV_EL03_CARBON215_INVCOSTTUD_CAPPED5;

		Axis xAxis = Axis.builder().title(xName).autoRange( Axis.AutoRange.REVERSED )
				 .showZeroLine( false )
				 .zeroLineWidth( 0 )
				 .zeroLineColor( "lightgray" )
				 .range( nkvCappedMax, nkvMin )
				 .build();


		String yName = CO2_COST_ORIG;
		String y2Name = CO2_COST_EL03;

		Axis yAxis = Axis.builder().title( yName ).build();

		String title = "";
		if ( ADDTL_LANE_KM.equals( xName ) ) {
			title = "CO2 costs SIMTO addtl lane-km:";
		}

		Layout layout = Layout.builder( title ).xAxis( xAxis ).yAxis( yAxis ).width( plotWidth ).build();

		List<Trace> traces = new ArrayList<>();

		traces.add( vertialNkvOneLine( y2Name ) );

		traces.add( getTraceCyan( table, xName, y2Name ) );
		traces.add( getTraceMagenta( table, xName, y2Name ) );
		traces.add( getTraceOrange( table, xName, y2Name ) );
		traces.add( getTraceRed( table, xName, y2Name ) );

		return new Figure( layout, traces.toArray( new Trace[]{} ) );
	}
	// ========================================================================================
	// ========================================================================================
	public List<Figure> nkvElttimeCarbon215( int cap ) {
		List<Figure> figures = new ArrayList<>();
		String xName = Headers.cappedOf( cap, NKV_ELTTIME_CARBON215_INVCOSTTUD );
		figures.add( investmentCostTud( cap, xName ) );
		figures.add( cumulativeInvestmentCostTud( cap, xName ) );
		figures.add( cumBenefitVsCumCost(NKV_EL03_CARBON215_INVCOSTTUD ) );
		return figures;
	}
	// ========================================================================================
	// ========================================================================================
	public Figure invcosttud_vs_nkvElttimeCarbon700Invcosttud( int cap ){
		String xName = Headers.cappedOf( cap, NKV_ELTTIME_CARBON700TPR0_INVCOSTTUD );
		return investmentCostTud( cap, xName );
	}

	// ========================================================================================
	// ========================================================================================
	public Figure invcosttud_vs_nkvElttimeCarbon2000Invcosttud(){
		String xName = Headers.capped5Of( NKV_ELTTIME_CARBON2000_INVCOSTTUD );
		String y2Name = INVCOST_TUD;

		Axis xAxis = Axis.builder().title( xName ).autoRange( Axis.AutoRange.REVERSED )
//				 .range( nkvCappedMax, nkvMin )
								   .build();

		Axis yAxis = Axis.builder().title( y2Name ).build();

		Layout layout = Layout.builder("").xAxis( xAxis ).yAxis( yAxis ).width( plotWidth ).build();

		List<Trace> traces = new ArrayList<>();

		traces.add( vertialNkvOneLine( y2Name ) );

		traces.add( getTraceCyan( table, xName, y2Name ) );
		traces.add( getTraceMagenta( table, xName, y2Name ) );
		traces.add( getTraceOrange( table, xName, y2Name ) );
		traces.add( getTraceRed( table, xName, y2Name ) );

		return new Figure( layout, traces.toArray(new Trace[]{} ) );
	}

	// ========================================================================================
	// ========================================================================================
	private ScatterTrace vertialNkvOneLine( String y2Name ){
		return vertialNkvOneLine( table, y2Name );
	}
	private static ScatterTrace vertialNkvOneLine( Table table, String y2Name ){
		return ScatterTrace.builder( new double[]{1., 1.}, new double[]{0., 1.1 * table.numberColumn( y2Name ).max()} ).mode( ScatterTrace.Mode.LINE ).name( "NKV=1" ).build();
	}
	private Figure investmentCostTud( int cap, String xName ){
		Axis.AxisBuilder xAxisBuilder = Axis.builder();

		if ( cap ==Integer.MAX_VALUE ) {
			xName = NKV_EL03_CARBON215_INVCOSTTUD;
			xAxisBuilder.autoRange( Axis.AutoRange.REVERSED );
		} else {
			xAxisBuilder.range( nkvCappedMax, nkvMin );
		}

		Table table2 = table.sortDescendingOn( xName ); // cannot remember why this is necessary

		String yName = INVCOST_TUD;
		Axis yAxis = Axis.builder().title( yName ).build();

		Layout layout = Layout.builder( "" ).xAxis( xAxisBuilder.title( xName ).build() ).yAxis( yAxis ).width( plotWidth ).build();

		List<Trace> traces = new ArrayList<>();

		// the nkv=1 line:
		double[] xx = new double[]{1., 1.};
		double[] yy = new double[]{0., 1.1* table2.numberColumn( yName ).max() };
		traces.add( ScatterTrace.builder( xx, yy ).mode( ScatterTrace.Mode.LINE ).name("NKV=1").build() );

		traces.add( getTraceCyan( table2, xName, yName ) );
		traces.add( getTraceMagenta( table2, xName, yName ) );
		traces.add( getTraceOrange( table2, xName, yName ) );
		traces.add( getTraceRed( table2, xName, yName ) );

		return new Figure( layout, traces.toArray( new Trace[]{} ) );
	}
	private Figure cumulativeInvestmentCostTud( int cap, String xName ){
		Axis.AxisBuilder xAxisBuilder = Axis.builder();
		if ( cap ==Integer.MAX_VALUE ) {
			xName = NKV_EL03_CARBON215_INVCOSTTUD;
			xAxisBuilder.autoRange( Axis.AutoRange.REVERSED );
		} else {
			xAxisBuilder.range( nkvCappedMax, nkvMin );
		}

		String columnToCumulate = INVCOST_TUD;
		String yName = "cumulative_" + columnToCumulate;

		final Table table2 = createCumulatedTable( xName, columnToCumulate, yName, table );

//		new CsvWriter().write( table2, CsvWriteOptions.builder( new File( "cumCosts.tsv" ) ).separator( '\t' ).usePrintFormatters( true ).build() );

		Axis xAxis = xAxisBuilder.title( xName ).build();
		Axis yAxis = Axis.builder().title( yName ).build();
		Layout layout = Layout.builder( "" ).xAxis( xAxis ).yAxis( yAxis ).width( plotWidth ).build();

		List<Trace> traces = new ArrayList<>();

		var nameInLegend = "cumulative cost";
		traces.add( ScatterTrace.builder( table2.doubleColumn( xName ), table2.doubleColumn(yName) ).mode( ScatterTrace.Mode.LINE ).showLegend( true ).name( String.format( legendFormat, nameInLegend ) ).build() );

		traces.add( vertialNkvOneLine( table2, yName ) );

		return new Figure( layout, traces.toArray( new Trace[]{} ) );
	}
	private Figure cumBenefitVsCumCost( String nkvToUseOrig ){
		String nkvToUse = Headers.cappedOf( 30, nkvToUseOrig );
		Headers.addCap( 30, table, nkvToUseOrig );

		Axis.AxisBuilder xAxisBuilder = Axis.builder();

		final String BENEFIT = "benefit";

		// add benefits row:
		Table tableTmp = Table.create( table.stringColumn( PROJECT_NAME ), table.doubleColumn( nkvToUse ), table.doubleColumn( INVCOST_TUD ) );
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
		Layout layout = Layout.builder( "" ).xAxis( xAxis ).yAxis( yAxis ).width( plotWidth ).build();

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
