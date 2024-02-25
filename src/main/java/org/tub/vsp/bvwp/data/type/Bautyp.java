package org.tub.vsp.bvwp.data.type;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public enum Bautyp{
//	NB2_4 ("2-streifiger Neubau / 4-streifiger Neubau"),
	// z.B. https://bvwp-projekte.de/strasse/A25_B5-G20-SH/A25_B5-G20-SH.html .
	// Eher eine Ortsumgehung ("B5").
//	NB3_4 ("3-streifiger Neubau / 4-streifiger Neubau"),
	// z.B. https://bvwp-projekte.de/strasse/A46-B7-G41-NW/A46-B7-G41-NW.html .
	// Lückenschluss, der teilweise als Bundesstrasse ("B7") realisiert wird.
	NB4( "4-streifiger Neubau"),
	NB6( "6-streifiger Neubau"),
//	NB2_EW4( "2-streifiger Neubau / Erweiterung auf 4 Fahrstreifen"),
	NB4_EW4( "4-streifiger Neubau / Erweiterung auf 4 Fahrstreifen"),
	NB6_EW6( "6-streifiger Neubau / Erweiterung auf 6 Fahrstreifen"),
	EW4("Erweiterung auf 4 Fahrstreifen"),
	EW6("Erweiterung auf 6 Fahrstreifen"),
	EW8("Erweiterung auf 8 Fahrstreifen"),
	EW6_EW8("Erweiterung auf 6 Fahrstreifen / Erweiterung auf 8 Fahrstreifen"),
//	EW8_EW9("Erweiterung auf 8 Fahrstreifen / Erweiterung auf 9 Fahrstreifen"),
	KNOTENPUNKT("Aus / Neubau eines Knotenpunktes"),
	KNOTENPUNKT_EW4( "Aus / Neubau eines Knotenpunktes / Erweiterung auf 4 Fahrstreifen"),
	KNOTENPUNKT_EW6( "Aus / Neubau eines Knotenpunktes / Erweiterung auf 6 Fahrstreifen"),
	KNOTENPUNKT_NB4( "Aus / Neubau eines Knotenpunktes / 4-streifiger Neubau"),
	BLANK("")
	// Z.B. A5-G20-HE-T9-HE, wo es vermutl. im TP vergessen wurde.  kai, feb'24
	;

	private static final Logger log = LogManager.getLogger( Bautyp.class );

	public final String description;

	Bautyp( String description ) {
		this.description = description;
	}

	public static Bautyp getFromString( String description ) {
		for ( Bautyp v : values()) {
			if( v.description.equalsIgnoreCase( description ) ){
				return v;
			}
		}
		if ( description.equals( "Erweiterung auf 8 Fahrstreifen /"  ) ) {
			// m.E. Typo in A7-G60-HE.  Evtl. fehlt da auch Text, aber das wissen wir (ohne weitere Recherche) nicht. kai, feb'24
			return EW8;
		}
		if ( description.equals( "2-streifiger Neubau / Erweiterung auf 4 Fahrstreifen" ) ) {
			// Im AB-Bereich ist das nur https://bvwp-projekte.de/strasse/A98-G110-BW/A98-G110-BW.html ,
			// und die ist wohl erst NB einer Art Bundesstrasse, die im zweiten TP dann gedoppelt wird.  kai, feb'24
			return NB4;
		}
		if ( description.equals( "Erweiterung auf 8 Fahrstreifen / Erweiterung auf 9 Fahrstreifen" ) ) {
			return EW8;
		}
		log.error( "cannot find Bautyp for description=" + description );
//		Thread.dumpStack();
		throw new RuntimeException( "cannot find Bautyp for description=" + description );
//		return null;
	}

	}
