package org.tub.vsp.bvwp.plot;

import org.apache.commons.math3.util.Pair;
import tech.tablesaw.plotly.components.Figure;

import java.util.List;

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
			result.append( "<div id='plot" + ii + "'>\n" );
		}
		result.append( "<h1>2. Zwischenbericht</h1>\n" ).append( System.lineSeparator() );
		for( int ii = 1001 ; ii < 1100 ; ii++ ){
			result.append( "<div id='plot" ).append( ii ).append( "'>\n" ).append( System.lineSeparator() );
		}
		result.append( "<h1>Part B</h1>\n" ).append( System.lineSeparator() );
		for( int ii = 0 ; ii < 26 ; ii++ ){
			final char c = (char) (ii + 65); // generate A, B, ... to be backwards compatible with what we had so far.  kai, mar'24
			result.append( "<div id='plot" ).append( c ).append( "'>\n" ).append( System.lineSeparator() );
		}
		return result.toString();
	}

	public static final String pageBottom = "</body>" + System.lineSeparator() + "</html>";

	public static String createPageV2( List<Pair<String, List<Figure>>> figures ) {
		StringBuilder result = new StringBuilder( "<html>" + System.lineSeparator()
									  + "<head>" + System.lineSeparator()
									  + "    <title>Multi-plot test</title>" + System.lineSeparator()
									  + "    <script src=\"https://cdn.plot.ly/plotly-latest.min.js\"></script>" + System.lineSeparator()
									  + "</head>" + System.lineSeparator()
									  + "<body>" + System.lineSeparator()
									  + "<h1>Part A</h1>" + System.lineSeparator() );

		// append the html that references each individual plot:
		{
			int cnt = 0;
			for( Pair<String, List<Figure>> entry : figures ){
				result.append( entry.getFirst() ).append( System.lineSeparator() );
				result.append( "<ul>" ).append( System.lineSeparator() );
				if( entry.getValue() != null ){
					for( Figure figure : entry.getValue() ){
						result.append( "<li id='plot" ).append( cnt ).append( "' style='display:inline-block' />" ).append( System.lineSeparator() );
						cnt++;
					}
				}
				result.append( "</ul>" ).append( System.lineSeparator() );
			}
		}

		// append the figures themselves:
		{
			int cnt = 0;
			for( Pair<String, List<Figure>> stringListPair : figures ){
				List<Figure> list = stringListPair.getSecond();
				if( list != null ){
					for( Figure figure : list ){
						result.append( figure.asJavascript( "plot" + cnt ) ).append( System.lineSeparator() );
						cnt++;
					}
				}
			}
		}

		// terminating lines:
		result.append( "</body>" ).append( System.lineSeparator() ).append( "</html>" );

		return result.toString();
	}
}
