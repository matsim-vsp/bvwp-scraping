package org.tub.vsp.computation;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public record Modifications(double co2Price, double mehrFzkm) {
    private static final Logger log = LogManager.getLogger(Modifications.class);

    public Modifications {
        if (co2Price < 145) {
            log.warn("co2Price is no longer a factor, but the price.  You probably want a value >= 145.");
        }
    }

    public static Modifications createInducedWithMehrFzkm(double mehrFzkm) {
        return new Modifications( 145., mehrFzkm);
    }

    public static Modifications createInducedAndCo2WithMehrFzkm(double mehrFzkm) {
        return new Modifications( 5 * 145., mehrFzkm);
    }

    public static final Modifications NO_CHANGE = new Modifications( 145., 0.);
    public static final Modifications CO2_PRICE = new Modifications( 5. * 145., 0.);

    @Override public String toString() {
        return "[co2Price=" + co2Price + "; mehrFzkm=" + mehrFzkm + "]";
    }
}
