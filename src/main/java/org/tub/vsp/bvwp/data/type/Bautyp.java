package org.tub.vsp.bvwp.data.type;

public enum Bautyp{

	NB4( "4-streifiger Neubau"),
	EW6("Erweiterung auf 6 Fahrstreifen");
	


	public final String description;

	Bautyp( String description ) {
		this.description = description;
	}

	public static Bautyp getFromString( String description ) {
		for ( Bautyp v : values())
			if (v.description.equalsIgnoreCase(description)) {
				return v;
			}
		throw new RuntimeException( "cannot find Bautyp for description=" + description );
	}

	}
