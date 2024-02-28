package org.tub.vsp.bvwp.computation;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public record Modifications(double co2Price, double mehrFzkm) {
    private static final Logger log = LogManager.getLogger(Modifications.class);
    private static final double co2PriceBVWP = 145.;
    private static final double co2Price5fach = 5*145.; //KNs Annahme
    private static final double co2Price680 = 623.; //€ 623 (in 2012) corresponds to € 680 in 2020 including the inflation.

    public Modifications {
        if (co2Price < co2PriceBVWP) {
            log.warn("co2Price is no longer a factor, but the price.  You probably want a value >= 145.");
        }
    }

    public static Modifications createInducedWithAdditionalFzkm( double mehrFzkm ) {
        return new Modifications( co2PriceBVWP, mehrFzkm);
    }

    public static Modifications createInducedAndCo2WithMehrFzkm(double mehrFzkm) {
        return new Modifications( co2Price5fach, mehrFzkm);
    }

    public static Modifications createCo2withoutInduzed (double co2Price){
        return new Modifications(co2Price, 0);
    }

    public static final Modifications NO_CHANGE = new Modifications(co2PriceBVWP, 0.);

    public static final Modifications CO2_PRICE_5FACH = new Modifications(co2Price5fach, 0.);
    public static final Modifications CO2_PRICE_680 = new Modifications(co2Price680, 0.);

    @Override public String toString() {
        return "[co2Price=" + co2Price + "; mehrFzkm=" + mehrFzkm + "]";
    }
}
