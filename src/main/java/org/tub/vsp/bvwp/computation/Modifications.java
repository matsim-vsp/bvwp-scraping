package org.tub.vsp.bvwp.computation;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public record Modifications(double co2Price, double mehrFzkm, double constructionCostFactor) {
    private static final Logger log = LogManager.getLogger(Modifications.class);
    public static final double co2PriceBVWP = 145.;
//    public static final double co2Price5fach = 5*145.; //KNs Annahme
    public static final double co2Price680 = 623.; //€ 623 (in 2012) corresponds to € 680 in 2020 including the inflation.
    // Kurioserweise steht dies (zwar) im pdf (https://www.umweltbundesamt.de/publikationen/methodenkonvention-umweltkosten; in Euro_2020), aber die Webseite
    // (https://www.umweltbundesamt.de/daten/umwelt-wirtschaft/gesellschaftliche-kosten-von-umweltbelastungen#klimakosten-von-treibhausgas-emissionen) von
    // 792 (2020, in Euro_2022).  Mir nicht klar, wie sie das gemacht haben.  Plausibel finde ich 809 in 2022 mit Euro_2022, und da war halt viel Inflation.

    public Modifications {
        if (co2Price < co2PriceBVWP) {
            log.warn("co2Price is no longer a factor, but the price.  You probably want a value >= 145.");
        }
    }

    // I think that we should inline this and then remove the method.  kai, mar'24

    // I think that we should inline this and then remove the method.  kai, mar'24

    // I think that we should inline this and then remove the method.  kai, mar'24

	public static final Modifications NO_CHANGE = new Modifications(co2PriceBVWP, 0., 1 );

    @Override public String toString() {
        return "[co2Price=" + co2Price + "; mehrFzkm=" + mehrFzkm + "]";
    }
}
