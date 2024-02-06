package org.tub.vsp.data.type;

public enum Priority{
    VB("Vordringlicher Bedarf (VB)"),
    VBE("Vordringlicher Bedarf - Engpassbeseitigung (VB-E)"),
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
