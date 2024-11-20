package org.tub.vsp.bvwp.data.type;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public enum Einstufung{
    VBE("Vordringlicher Bedarf - Engpassbeseitigung (VB-E)"),
    VB("Vordringlicher Bedarf (VB)"),
    WBP("Weiterer Bedarf mit Planungsrecht (WB*)"),
    WB( "Weiterer Bedarf (WB)"),
    KB( "Kein Bedarf (KB)")
//    ,@Deprecated /* yyyyyy lieber aufklären!! */ UNDEFINED("undefined")
    ;

    private static final Logger log = LogManager.getLogger( Einstufung.class );

    public final String description;

    Einstufung( String description ) {
        this.description = description;
    }

    public static Einstufung getFromString( String description ) {
        for ( Einstufung v : values()){
            if( v.description.equalsIgnoreCase( description ) ){
                return v;
            }
            if ( v.name().equalsIgnoreCase( description ) ) {
                return v;
            }
        }
        log.warn("description={}", description);
        throw new RuntimeException( "unknown Einstufung=" + description );
//        return UNDEFINED;
    }
}
