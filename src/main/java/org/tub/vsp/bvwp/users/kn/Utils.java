package org.tub.vsp.bvwp.users.kn;

import org.apache.commons.math3.util.Pair;
import tech.tablesaw.aggregate.AggregateFunctions;
import tech.tablesaw.api.*;
import tech.tablesaw.columns.Column;
import tech.tablesaw.plotly.components.Axis;
import tech.tablesaw.plotly.components.Figure;
import tech.tablesaw.plotly.components.Layout;
import tech.tablesaw.plotly.components.Marker;
import tech.tablesaw.plotly.traces.BarTrace;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import static org.tub.vsp.bvwp.data.Headers.*;
import static org.tub.vsp.bvwp.data.HeadersKN.*;
import static org.tub.vsp.bvwp.users.kn.RunLocalCsvScrapingKN.createDefaultKey;

class Utils{
	static Figure barChartFigureInvKosten( Table table, String whichNkv, String welcheHhrelKosten ){

	    Table table2 = Table.create( table.column( PROJECT_NAME ), table.column( whichNkv ), table.column( welcheHhrelKosten ), table.column( EINSTUFUNG ), table.column(EINSTUFUNG_AS_NUMBER) )
					   .sortDescendingOn( EINSTUFUNG_AS_NUMBER );
	    table = null;

	    for( Row row : table2 ){
		if ( row.getString( PROJECT_NAME ).contains( "dummy" ) ) {
		    row.setDouble( whichNkv, 0.5 );
		    row.setDouble( welcheHhrelKosten, 0. );
		}
	    }

	    final String ALL = EINSTUFUNG;
	    if ( !table2.containsColumn( ALL ) ){
		StringColumn dummy = StringColumn.create( ALL );
		for( String string : table2.stringColumn( EINSTUFUNG ) ){
		    dummy.append( ALL );
		}
		table2.addColumns( dummy );
	    }

	    String aggregation = ALL;

	    String whichInvCostMrd = welcheHhrelKosten + " [Mrd Eu]";
	    table2.addColumns( table2.numberColumn( welcheHhrelKosten ).divide( 1000 ).setName( whichInvCostMrd ) );

	    Axis yAxis = Axis.builder().title( whichInvCostMrd ).build();
	    Layout layout = Layout.builder().barMode( Layout.BarMode.STACK ).yAxis( yAxis ).title( whichNkv ).build();

	    List<BarTrace> traces = new ArrayList<>();
	    {
		final Table tTmp = table2.where( table2.numberColumn( whichNkv ).isLessThan( 1. ) );
		System.out.println( tTmp.print(55));
		Table t3 = tTmp
					   .summarize( whichInvCostMrd, AggregateFunctions.sum ).by( aggregation );

		traces.add( myGenerateBarTrace( t3, aggregation, "Sum [", whichInvCostMrd, "NKV<1", "red" ) );
	    }
	    {
		Table t3 = table2.where( table2.numberColumn( whichNkv ).isBetweenInclusive( 1., 2.-Double.MIN_VALUE ) )
				 .summarize( whichInvCostMrd, AggregateFunctions.sum ).by( aggregation );

		traces.add( myGenerateBarTrace( t3, aggregation, "Sum [", whichInvCostMrd, "1<=NKV<2", "orange" ) );
	    }
	    {
		Table t3 = table2.where( table2.numberColumn( whichNkv ).isBetweenInclusive( 2., 3.-Double.MIN_VALUE ) )
				 .summarize( whichInvCostMrd, AggregateFunctions.sum ).by( aggregation );

		traces.add( myGenerateBarTrace( t3, aggregation, "Sum [", whichInvCostMrd, "2<=NKV<3", "yellow" ) );
	    }
	    {
		Table t3 = table2.where( table2.numberColumn( whichNkv ).isBetweenInclusive( 3., 4.-Double.MIN_VALUE ) )
				 .summarize( whichInvCostMrd, AggregateFunctions.sum ).by( aggregation );

		traces.add( myGenerateBarTrace( t3, aggregation, "Sum [", whichInvCostMrd, "3<=NKV<4", "AAAAFF" ) );
	    }
	    {
		Table t3 = table2.where( table2.numberColumn( whichNkv ).isBetweenInclusive( 4., 5.-Double.MIN_VALUE ) )
				 .summarize( whichInvCostMrd, AggregateFunctions.sum ).by( aggregation );

		traces.add( myGenerateBarTrace( t3, aggregation, "Sum [", whichInvCostMrd, "4<=NKV<5", "8888FF" ) );
	    }
	    {
		Table t3 = table2.where( table2.numberColumn( whichNkv ).isGreaterThanOrEqualTo(5. ) )
				 .summarize( whichInvCostMrd, AggregateFunctions.sum ).by( aggregation );

		traces.add( myGenerateBarTrace( t3, aggregation, "Sum [", whichInvCostMrd, "5<=NKV", "6666FF" ) );
	    }

	    final Figure figure = new Figure( layout, traces.toArray( new BarTrace[0] ) );
	    return figure;
	}
	private static BarTrace myGenerateBarTrace( Table t3, String catColumn, String x, String whichColumn, String name, String color ){
	    System.out.println( t3.print() );
	    return BarTrace.builder( t3.categoricalColumn( catColumn ), t3.numberColumn( x + whichColumn + "]" ) )
			   .orientation( BarTrace.Orientation.VERTICAL )
			   .name( name )
			   .marker( Marker.builder().color( color ).build() )
			   .build();
	}
	static Figure barChartFigureAnzahlProjekte( Table table, String whichNkv ){
	    Table table2 = Table.create( table.column( PROJECT_NAME ), table.column( whichNkv ), table.column( EINSTUFUNG ), table.column(EINSTUFUNG_AS_NUMBER) )
				.sortDescendingOn( EINSTUFUNG_AS_NUMBER );

	    for( Row row : table2 ){
		if ( row.getString( PROJECT_NAME ).contains( "dummy" ) ) {
		    row.setDouble( whichNkv, 0.5 );
		}
	    }

	    final String ALL = EINSTUFUNG;
	    if ( !table2.containsColumn( ALL ) ){
		StringColumn dummyColumn = StringColumn.create( ALL );
		for( String string : table2.stringColumn( EINSTUFUNG ) ){
		    dummyColumn.append( ALL );
		}
		table2.addColumns( dummyColumn );
	    }

	    LongColumn weightColumn = LongColumn.create("weight" );
	    for( String projectName : table2.stringColumn( PROJECT_NAME ) ){
		if ( projectName.contains( "dummy" ) ) {
		    weightColumn.append( 0 );
		} else {
		    weightColumn.append( 1 );
		}
	    }
	    table2.addColumns( weightColumn );


	    String aggregation = ALL;

	    System.out.println( table2.print() );
	    table = null;

	    Axis yAxis = Axis.builder().title( "Anzahl Projekte" ).build();
	    Layout layout = Layout.builder().barMode( Layout.BarMode.RELATIVE ).yAxis( yAxis ).title( whichNkv ).build();

	    List<BarTrace> traces = new ArrayList<>();
	    {
		Table t3 = table2.where( table2.numberColumn( whichNkv ).isLessThan( 1. ) )
				 .summarize( "weight", AggregateFunctions.sum ).by( aggregation );
		System.out.println( t3.print() );
		traces.add( myGenerateBarTrace( t3, aggregation, "Sum [", "weight", "NKV<1", "red" ) );
	    }
	    {
		Table t3 = table2.where( table2.numberColumn( whichNkv ).isBetweenInclusive( 1., 2.-Double.MIN_VALUE ) )
				 .summarize( whichNkv, AggregateFunctions.count ).by( aggregation );

		traces.add( myGenerateBarTrace( t3, aggregation, "Count [", whichNkv, "1<=NKV<2", "orange" ) );
	    }
	    {
		Table t3 = table2.where( table2.numberColumn( whichNkv ).isBetweenInclusive( 2., 3.-Double.MIN_VALUE ) )
				 .summarize( whichNkv, AggregateFunctions.count ).by( aggregation );

		traces.add( myGenerateBarTrace( t3, aggregation, "Count [", whichNkv, "2<=NKV<3", "yellow" ) );
	    }
	    {
		Table t3 = table2.where( table2.numberColumn( whichNkv ).isBetweenInclusive( 3., 4.-Double.MIN_VALUE ) )
				 .summarize( whichNkv, AggregateFunctions.count ).by( aggregation );

		traces.add( myGenerateBarTrace( t3, aggregation, "Count [", whichNkv, "3<=NKV<4", "AAAAFF" ) );
	    }
	    {
		Table t3 = table2.where( table2.numberColumn( whichNkv ).isBetweenInclusive( 4., 5.-Double.MIN_VALUE ) )
				 .summarize( whichNkv, AggregateFunctions.count ).by( aggregation );

		traces.add( myGenerateBarTrace( t3, aggregation, "Count [", whichNkv, "4<=NKV<5", "8888FF" ) );
	    }
	    {
		Table t3 = table2.where( table2.numberColumn( whichNkv ).isGreaterThanOrEqualTo(5. ) )
				 .summarize( whichNkv, AggregateFunctions.count ).by( aggregation );

		traces.add( myGenerateBarTrace( t3, aggregation, "Count [", whichNkv, "5<=NKV", "6666FF" ) );
	    }

	    final Figure figure = new Figure( layout, traces.toArray( new BarTrace[0] ) );
	    return figure;
	}
	private static Table createVariousNKVs( Table table2 ){
	    Table table3 = Table.create( table2.column( PROJECT_NAME )
			    , table2.column( BAUTYP )
			    , table2.column( INVCOST_BARWERT_ORIG )
			    , table2.column( INVCOST_TUD )
			    , table2.column( NKV_ORIG )
    //                        , table2.column( NKV_INVCOSTTUD )
			    , table2.column( NKV_ELTTIME_HIGH )
			    , table2.column( NKV_CARBON700 )
    //                        , table2.column( NKV_ELTTIME_CARBON700_INVCOSTTUD )
    //                        , table2.column( NKV_ELTTIME_CARBON700_EMOB_INVCOSTTUD )
    //                        , table2.column( NKV_ELTTIME_CARBON2000_EMOB_INVCOSTTUD )
    //                        , table2.column( NKV_ELTTIME_CARBON700_EMOB_INVCOSTTUD_10pctLessTraffic )
    //                        , table2.column( NKV_ELTTIME_CARBON700_EMOB_INVCOSTTUD_20pctLessTraffic )
    //                        ,table2.numberColumn( Headers.B_OVERALL )
    ////                        , table2.numberColumn( Headers.NKV_EL03_CARBON215_INVCOSTTUD )
    //                        , table2.numberColumn( Headers.NKV_ELTTIME_CARBON700TPR0_INVCOSTTUD )
				       );

	    for( Column<?> column : table3.columns() ){
		if ( column.name().startsWith( "NKV" ) ) {
		    NumberFormat format = NumberFormat.getNumberInstance( Locale.GERMAN );
		    format.setMaximumFractionDigits( 1 );
		    format.setMinimumFractionDigits( 1 );
		    ((DoubleColumn) column).setPrintFormatter( format, "n/a" );
		} else if ( column instanceof NumberColumn ){
		    NumberFormat format = NumberFormat.getNumberInstance( Locale.GERMAN );
    //                format.setMaximumFractionDigits( 1 );
    //                format.setMinimumFractionDigits( 1 );
		    ((DoubleColumn) column).setPrintFormatter( format, "n/a" );
		}
	    }

	    return table3;
	}
	static String createHeader1( String str ) {
	    return "<h1>" + str + "</h1>";
	}
	static String createHeader2( String str ) {
	    return "<h2>" + str + "</h2>";
	}
	static String createHeader3( String str ) {
	    return "<h3>" + str + "</h3>";
	}
	static void addHeaderPlusMultipleFigures( List<Pair<String,Figure>> figuresMap, String str, List<Figure> figuresList ) {
	    figuresMap.add( Pair.create( str, figuresList.removeFirst() ) );
	    for( Figure figure : figuresList ){
		figuresMap.add( Pair.create( createDefaultKey( figuresMap ), figure ) );
	    }
	}
}
