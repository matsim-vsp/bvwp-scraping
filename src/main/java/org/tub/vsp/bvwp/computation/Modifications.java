package org.tub.vsp.bvwp.computation;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public record Modifications(double co2Price, double mehrFzkm) {
    private static final Logger log = LogManager.getLogger(Modifications.class);
    private static final double co2PriceBVWP = 145.;
    private static final double co2PriceIncreased = 623.; //€ 623 (in 2012) corresponds to € 680 in 2020 including the inflation.

    public Modifications {
        if (co2Price < 145) {
            log.warn("co2Price is no longer a factor, but the price.  You probably want a value >= 145.");
        }
    }

    public static Modifications createInducedWithAdditionalFzkm( double mehrFzkm ) {
        return new Modifications( co2PriceBVWP, mehrFzkm);
    }

    public static Modifications createInducedAndCo2WithMehrFzkm(double mehrFzkm) {
        return new Modifications( co2PriceIncreased, mehrFzkm);
    }


    public static final Modifications NO_CHANGE = new Modifications(co2PriceBVWP, 0.);

    public static final Modifications CO2_PRICE = new Modifications(co2PriceIncreased, 0.);

    @Override public String toString() {
        return "[co2Price=" + co2Price + "; mehrFzkm=" + mehrFzkm + "]";
    }
}
