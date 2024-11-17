package org.tub.vsp.bvwp.computation;

import com.ibm.icu.impl.Assert;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ComputationKN {
    // yyyyyy Einbau von Jahreswerten (z.B. Elektrifizierung über Zeit).  Voraussetzung: Ich kann die Diskontierung
    // nachbauen.

    private static final Logger log = LogManager.getLogger(ComputationKN.class);

    public static final double FZKM_AB = 221000;
    public static final double LANE_KM_AB = 60000.;

    enum CO2_PER_KM { typicalValues, fromPrins };
    private static final CO2_PER_KM co2PerKm = CO2_PER_KM.fromPrins;

    static final class Amounts {
        final double rz;
        final double rz_induz;
        final double rz_verl;
        final double pkwkm_all;
        final double pkwkm_induz;
        final double pkwkm_verl;
        final double co2_pkw;
        final double co2_lkw;
        final double co2_kfz;
        final double pkwkm_reroute;
        final double co2_per_pkwkm;
        final double lkwkm_all;
        final double co2_per_lkwkm;

        Amounts( double pkwkm_all, double pkwkm_induz, double pkwkm_verl,
                 double pers_h, double pers_h_induz, double pers_h_verl,
                 double lkwkmAll,
                 double co2_pkw, double co2_lkw, double co2_kfz
               ) {

            this.rz = pers_h;
            this.rz_induz = pers_h_induz;
            this.rz_verl = pers_h_verl;

            this.pkwkm_all = pkwkm_all;
            this.pkwkm_induz = pkwkm_induz;
            this.pkwkm_verl = pkwkm_verl;
            this.pkwkm_reroute = pkwkm_all - pkwkm_induz - pkwkm_verl;

            lkwkm_all = lkwkmAll;

            this.co2_pkw = co2_pkw;
            this.co2_lkw = co2_lkw;
            this.co2_kfz = co2_kfz;


            switch ( co2PerKm ) {
                case typicalValues -> {
                    this.co2_per_pkwkm = 0.1 / 1000 * 1_000_000;
                    // 100g/km = 0.1kg/km converted to tons/km, but then the unit in bvwp-projekte is mio pkw-km.

                    this.co2_per_lkwkm = 2. / 1000 * 1_000_000;
                    // approx. 121g per transported ton x 20t/Lkw
                }
                case fromPrins -> {
                    this.co2_per_pkwkm = co2_pkw / pkwkm_all;
                    this.co2_per_lkwkm = co2_lkw / lkwkm_all;
                }
                default -> throw new IllegalStateException( "Unexpected value: " + co2PerKm );
            }

//            log.info( "co2 per pkwkm [t/mio-km=g/km]=" + (this.co2_pkw/this.pkwkm_all) + "; co2 per lkwkm=" + (this.co2_lkw/this.lkwkm_all) );
        }

    }

    static final class BenefitsAndInvestmentCosts{
        final double fzkm;
        final double rz;
        final double impl;
        final double co2_infra;
        final double co2_betrieb;
        final double all;
        final double investmentCosts;

        BenefitsAndInvestmentCosts( double fzkm, double rz, double impl, double co2_infra, double co2_betrieb, double all, double investmentCosts ) {
            // yyyyyy ist alles noch ganz schön unklar benannt!!
            this.fzkm = fzkm;
            this.rz = rz;
            this.impl = impl;
            this.co2_infra = co2_infra;
            this.co2_betrieb = co2_betrieb;
            this.all = all;
            this.investmentCosts = investmentCosts;
        }
    }


    static double nkvOhneKR_induz( Modifications modifications, Amounts amounts, BenefitsAndInvestmentCosts benefits, double b_all ) {
        prn("=== incoming", b_all, b_all);

        // ### preparations:

        double b_per_co2 = benefits.co2_betrieb / amounts.co2_kfz;
        // (this divides all (negative) co2 benefits by all emissions, including LKW.  We just need (discounted) benefits
        // per ton co2, indep of where this comes from.)

        // corrections:
        b_all -= benefits.co2_infra;
        b_all += benefits.co2_infra * modifications.co2Price()/145. ;

        b_all -= benefits.co2_betrieb;
        b_all += benefits.co2_betrieb * modifications.co2Price()/145. * modifications.emobCorrFact() ;

        b_all += modifications.mehrFzkm() * 200 * b_per_co2 * modifications.co2Price() / 145 * modifications.discountCorrFact() * modifications.emobCorrFact() ;
        // 200 t / 1 mio km = 200g/km

        // Problem ist, dass wir den (negativen) CO2-Benefit pro induzierten Fzkm nicht kennen.  PRINS weist nur Deltas der CO2-Emissionen sowie
        // der Fzkm aus.  Wenn man das durcheinander dividiert, dann sind die Resultate "all over the place".  Eine Begründung könnte sein, dass
        // z.B. viele Fze vorher Landstrasse und jetzt BAB fahren, und daher anders emittieren, obwohl sie die gleichen Fzkm fahren.  Daher werden
        // die Emissionen des zusätzlichen induzierten Verkehrs "separat" gerechnet ... mit 200g/km.  kai, nov'24

        Assert.assrt( modifications.discountCorrFact()==1 );  // m.E. anderen Weg nicht mehr verfolgt
        b_all *= modifications.discountCorrFact();

        return b_all / ( benefits.investmentCosts * modifications.investmentCostFactor() );
    }
    private static void prn(String msg, double b_all, double b_tmp) {
//        if ( Double.isNaN( b_all ) || Double.isNaN( b_tmp ) )
        {
            log.info( String.format( "%1$20s: before = %2$5.0f; corr = %3$5.0f; after = %4$5.0f", msg, b_tmp, b_all - b_tmp, b_all ) );
        }
    }

    static Double b_co2(Modifications modifications, Amounts amounts, BenefitsAndInvestmentCosts benefits ) {
        // yyyyyy for the time being this returns the benefits since they are easier to compute.  !!!!

        double b_co2 = 0.;

        // co2 Bau
        {
			b_co2 += benefits.co2_infra / 145. * modifications.co2Price() ;
        }

        // co2 Betrieb

        double b_per_co2 = benefits.co2_betrieb / amounts.co2_kfz;
        // (this divides all (negative) co2 benefits by all fzkm, including LKW.  We just need (discounted) benefits
        // of ton co2, indep of where it comes from.)

        // -- b_co2 calculation is now done "by hand".  I.e. take pkwkm, multiply with emissions per km (obtained
        // from co2_pkwkm / pkwkm), and then multiply b_per_co2:
        final double b_co2_induz = amounts.pkwkm_induz * amounts.co2_per_pkwkm * b_per_co2;
        final double b_co2_verl = amounts.pkwkm_verl * amounts.co2_per_pkwkm * b_per_co2;
        final double b_co2_reroute = amounts.pkwkm_reroute * amounts.co2_per_pkwkm * b_per_co2;
        final double b_co2_addtlInduz = modifications.mehrFzkm() * amounts.co2_per_pkwkm * b_per_co2;

        {
            b_co2 += b_co2_reroute / 145. * modifications.co2Price() * modifications.emobCorrFact();
        }
        {
            b_co2 += b_co2_verl / 145. * modifications.co2Price() * modifications.emobCorrFact();
        }
        {
            b_co2 += b_co2_induz / 145. * modifications.co2Price() * modifications.emobCorrFact();

            b_co2 += b_co2_addtlInduz / 145. * modifications.co2Price() * modifications.emobCorrFact();
            // mehrFzkm are those which are on top of PRINS
        }

        return b_co2;

        // Note that this really says nothing about old vs new co2 price, or old vs new addl traffic.  That all depends on the settings in "modifications".
    }

    /**
     * @param co2_infra_eur benefit in €/a
     * @param co2_betrieb_t emissions in t/a
     * @return new nkv
     */
    static double nkv_rail(double co2_price, double baukosten, double benefit, double co2_infra_eur, double co2_betrieb_t) {
        benefit -= co2_infra_eur;
        benefit -= co2_betrieb_t;

        benefit += co2_price / 145. * co2_infra_eur;
        benefit += co2_price * co2_betrieb_t;

        return benefit / baukosten;
    }
}
