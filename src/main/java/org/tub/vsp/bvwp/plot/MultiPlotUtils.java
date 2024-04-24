package org.tub.vsp.bvwp.plot;

/**
 * Bausteine, die für die Erstellung der Multiplots benötigt werden.
 * Für Beispiele zum Erstellen von Plots siehe {@link MultiPlotExample}
 */
public class MultiPlotUtils {
	public static String pageTop(){
		String result =
				"<html>"
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
						+ "<h1>Part A</h1>" + System.lineSeparator();

		for( int ii = 0 ; ii < 99 ; ii++ ){
			result += "<div id='plot" + ii + "'>" + System.lineSeparator();
		}
		result += "<h1>Part B</h1>" + System.lineSeparator();
		for( int ii = 0 ; ii < 26 ; ii++ ){
			final char c = (char) (ii + 65); // generate A, B, ... to be backwards compatible with what we had so far.  kai, mar'24
			result += "<div id='plot" + c + "'>" + System.lineSeparator();
		}
		return result;
	}

	public static final String pageBottom = "</body>" + System.lineSeparator() + "</html>";

}
