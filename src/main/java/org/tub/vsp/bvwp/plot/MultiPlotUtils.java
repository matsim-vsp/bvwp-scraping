package org.tub.vsp.bvwp.plot;

import tech.tablesaw.plotly.components.Figure;

import java.util.Map;

/**
 * Bausteine, die für die Erstellung der Multiplots benötigt werden.
 * Für Beispiele zum Erstellen von Plots siehe {@link MultiPlotExample}
 */
public class MultiPlotUtils {
	public static String pageTop(){
		StringBuilder result =
				new StringBuilder( "<html>"
								   + System.lineSeparator()
								   + "<head>"
								   + System.lineSeparator()
								   + "    <title>Multi-plot test</title>"
								   + System.lineSeparator()
								   + "    <script src=\"https://cdn.plot.ly/plotly-latest.min.js\"></script>"
								   + System.lineSeparator()
								   + "</head>"
								   + System.lineSeparator()
								   + "<body>"
								   + System.lineSeparator()
								   + "<h1>Part A</h1>" + System.lineSeparator() );

		for( int ii = 0 ; ii < 99 ; ii++ ){
			result.append( "<div id='plot" ).append( ii ).append( "'>" ).append( System.lineSeparator() );
		}
		result.append( "<h1>2. Zwischenbericht</h1>" ).append( System.lineSeparator() );
		for( int ii = 1001 ; ii < 1100 ; ii++ ){
			result.append( "<div id='plot" ).append( ii ).append( "'>" ).append( System.lineSeparator() );
		}
		result.append( "<h1>Part B</h1>" ).append( System.lineSeparator() );
		for( int ii = 0 ; ii < 26 ; ii++ ){
			final char c = (char) (ii + 65); // generate A, B, ... to be backwards compatible with what we had so far.  kai, mar'24
			result.append( "<div id='plot" ).append( c ).append( "'>" ).append( System.lineSeparator() );
		}
		return result.toString();
	}

	public static final String pageBottom = "</body>" + System.lineSeparator() + "</html>";

	public static String createPageV2( Map<String,Figure> figures ) {
		StringBuilder result = new StringBuilder( "<html>" + System.lineSeparator()
									  + "<head>" + System.lineSeparator()
									  + "    <title>Multi-plot test</title>" + System.lineSeparator()
									  + "    <script src=\"https://cdn.plot.ly/plotly-latest.min.js\"></script>" + System.lineSeparator()
									  + "</head>" + System.lineSeparator()
									  + "<body>" + System.lineSeparator()
									  + "<h1>Part A</h1>" + System.lineSeparator() );

		// append the html that references each individual plot:
		{
			int ii = 0;
			for( Map.Entry<String, Figure> entry : figures.entrySet() ){
				result.append( entry.getKey() ).append( "<div id='plot" ).append( ii ).append( "'>" ).append( System.lineSeparator() );
				ii++;
			}
		}

		// append the figures themselves:
		{
			int ii = 0;
			for( Figure figure : figures.values() ){
				result.append( figure.asJavascript( "plot" + ii ) ).append( System.lineSeparator() );
				ii++;
			}
		}

		// terminating lines:
		result.append( "</body>" ).append( System.lineSeparator() ).append( "</html>" );

		return result.toString();
	}
}
