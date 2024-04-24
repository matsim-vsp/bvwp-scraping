package org.tub.vsp.bvwp.users.kn;

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

		String yName = NKV_EL03_CARBON215_INVCOST50_CAPPED10;
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
	public Figure invcost50_vs_nkvEl03Cprice215Invcost50Capped5(){
		String xName = NKV_EL03_CARBON215_INVCOST50_CAPPED5;

		Axis xAxis = Axis.builder().title(xName).autoRange( Axis.AutoRange.REVERSED )
				 .showZeroLine( false )
				 .zeroLineWidth( 0 )
				 .zeroLineColor( "lightgray" )
				 .range( nkvCappedMax, nkvMin )
				 .build();

		Table table2 = table.sortDescendingOn( xName ); // cannot remember why this is necessary

		String yName = INVCOST50;
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
	public Figure cumulativeCost50_vs_nkvEl03Cprice215Invcost50Capped5(){

		String xName = NKV_EL03_CARBON215_INVCOST50_CAPPED5;

		Axis xAxis = Axis.builder().title(xName).autoRange( Axis.AutoRange.REVERSED )
				 .showZeroLine( false )
				 .zeroLineWidth( 0 )
				 .zeroLineColor( "lightgray" )
				 .range( nkvCappedMax, nkvMin )
				 .build();

		Table table2 = Table.create( table.stringColumn( PROJECT_NAME ), table.doubleColumn( INVCOST50 ), table.doubleColumn( xName ) );

		table2 = table2.sortDescendingOn( xName ); // necessary to get cumulative cost right

		DoubleColumn cumulativeCost = DoubleColumn.create( "cumulative_cost" );
		{
			double sum = 0.;
			for( Double cost : table2.doubleColumn( INVCOST50 ) ){
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
	public Figure invcost50_vs_NkvEl03Cprice600Invcost50(){
		String xName = NKV_EL03_CARBON700_INVCOST50_CAPPED5;

		Axis xAxis = Axis.builder().title(xName).autoRange( Axis.AutoRange.REVERSED )
				 .showZeroLine( false )
				 .zeroLineWidth( 0 )
				 .zeroLineColor( "lightgray" )
				 .range( nkvCappedMax, nkvMin )
				 .build();

		Table table2 = table.sortDescendingOn( xName ); // cannot remember why this is necessary

		String yName = INVCOST50;
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
	public Figure cumulativeCost50_vs_nkvEl03Cprice600Invcost50Capped5(){

		String xName = NKV_EL03_CARBON700_INVCOST50_CAPPED5;

		Axis xAxis = Axis.builder().title(xName).autoRange( Axis.AutoRange.REVERSED )
				 .showZeroLine( false )
				 .zeroLineWidth( 0 )
				 .zeroLineColor( "lightgray" )
				 .range( nkvCappedMax, nkvMin )
				 .build();

		Table table2 = Table.create( table.stringColumn( PROJECT_NAME ), table.doubleColumn( INVCOST50 ), table.doubleColumn( xName ) );

		table2 = table2.sortDescendingOn( xName ); // necessary to get cumulative cost right

		DoubleColumn cumulativeCost = DoubleColumn.create( "cumulative_cost" );
		{
			double sum = 0.;
			for( Double cost : table2.doubleColumn( INVCOST50 ) ){
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

//		System.exit(-1);

		String yName = "cumulative_cost";
		Axis yAxis = Axis.builder().title( "Kumulierte Investitionskosten (orig) [Mio]" )
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
		String xName = NKV_EL03_CARBON215_INVCOST50_CAPPED5;

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
}
