package org.tub.vsp.data.type;

public enum Severity {
    VB("Vordringlicher Bedarf (VB)"),
    VBE("Vordringlicher Bedarf - Engpassbeseitigung (VB-E)"),
    WBP("Weiterer Bedarf mit Planungsrecht (WB*)"),
    UNDEFINED("undefined");

    public final String description;

    Severity(String description) {
        this.description = description;
    }

    public static Severity getFromString(String description) {
        for (Severity v : values())
            if (v.description.equalsIgnoreCase(description)) {
                return v;
            }
        return UNDEFINED;
    }
}
