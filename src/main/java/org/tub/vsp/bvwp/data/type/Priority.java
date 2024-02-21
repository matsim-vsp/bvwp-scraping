package org.tub.vsp.bvwp.data.type;

public enum Priority{
    VBE("Vordringlicher Bedarf - Engpassbeseitigung (VB-E)"),
    VB("Vordringlicher Bedarf (VB)"),
    WBP("Weiterer Bedarf mit Planungsrecht (WB*)"),
    WB( "Weiterer Bedarf (WB)"),
    UNDEFINED("undefined");

    public final String description;

    Priority( String description ) {
        this.description = description;
    }

    public static Priority getFromString( String description ) {
        for ( Priority v : values())
            if (v.description.equalsIgnoreCase(description)) {
                return v;
            }
        return UNDEFINED;
    }
}
