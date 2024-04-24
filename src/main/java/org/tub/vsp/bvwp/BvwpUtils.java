package org.tub.vsp.bvwp;

import org.tub.vsp.bvwp.data.Headers;
import tech.tablesaw.api.Table;

public class BvwpUtils{

	public static final String SEPARATOR = System.lineSeparator() + "===========================================";

	private BvwpUtils(){} // do not instantiate

	public static String getPositivListe(){
	    return projectString( "BW", "A5" )
				   + projectString( "BW", "A6" )
				   + projectString( "BY", "A003" )
				   + projectString( "BY", "A008" ) // Ausbau München - Traunstein (- Salzburg)
				   + projectString( "BY", "A009" )
				   + projectString( "BY", "A092" )
				   + projectString( "BY", "A094" )
				   + projectString( "BY", "A099" )
				   + projectString( "HB", "A27" )
				   + projectString( "HE", "A3" )
				   + projectString( "HE", "A5" )
				   + projectString( "HE", "A45" )
				   + projectString( "HE", "A60" )
				   + projectString( "HE", "A67" )
				   + projectString( "HE", "A67" )
				   + projectString( "NI", "A2" )
				   + projectString( "NI", "A7" )
				   + projectString( "NI", "A27" )
				   + projectString( "NI", "A30" )
				   + projectString( "NW", "A1" )
				   + projectString( "NW", "A2" )
				   + projectString( "NW", "A3" )
				   + projectString( "NW", "A4" )
				   + projectString( "NW", "A30" )
				   + projectString( "NW", "A40" )
				   + projectString( "NW", "A42" )
				   + projectString( "NW", "A43" )
				   + projectString( "NW", "A45" )
				   + projectString( "NW", "A52" )
				   + projectString( "NW", "A57" )
				   + projectString( "NW", "A59" )
				   + projectString( "NW", "A559" )
				   + projectString( "RP", "A1" )
				   + projectString( "RP", "A1" )
				   + "(abcdef)"; // um das letzte "|" abzufangen
	}

	public static Table extractPriorityTable(Table table, String priority) {
		return table.where(table.stringColumn(Headers.EINSTUFUNG ).isEqualTo(priority ) );
	}

	public static String projectString(String bundesland, String road) {
	    return "(" + road + "-.*-" + bundesland + ".html" + ")|";
	}
}
