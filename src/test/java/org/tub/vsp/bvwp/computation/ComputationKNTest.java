package org.tub.vsp.bvwp.computation;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import static org.tub.vsp.bvwp.computation.ComputationKN.*;
import static org.tub.vsp.bvwp.computation.Modifications.co2PriceBVWP;

class ComputationKNTest {
    private static final Logger log = LogManager.getLogger(ComputationKNTest.class );
    @Test
//    @Disabled
        //needs to be fixed
    void test() {

        Amounts amounts = new Amounts(131.53, 143.95, 9.75, -18.56, 1.46, 0.13,
                        -25., 54_773.28, -6_083.34,48_689.94 );
        BenefitsAndInvestmentCosts benefits = new BenefitsAndInvestmentCosts(-785.233, 2555.429, 1025.464, -151.319, -175.021
                , 5305.683, 2737.176 );
        final double nkv_orig = benefits.all / benefits.investmentCosts;
        Assertions.assertEquals( 1.938378, nkv_orig, 0.001 );
        {
            double nkv = nkvOhneKR_induz(new Modifications(co2PriceBVWP, 0., 1, 1., 1. ), amounts, benefits, benefits.all );
            Assertions.assertEquals( nkv_orig, nkv, 0.001 );
        }

//        // doppelte Investitionskosten:
//        {
//            double nkv = nkvOhneKR_induz(new Modifications( co2PriceBVWP, 0., 2., 1., 1. ), amounts, benefits, benefits.all );
//            Assertions.assertEquals(nkv_orig/2, nkv, 0.001);
//        }
//
//        // neuer CO2-Preis:
//        {
//            double nkv = nkvOhneKR_induz(new Modifications( co2Price796, 0., 1., 1., 1. ), amounts, benefits, benefits.all  );
//            final double delta_nkv = (benefits.co2_betrieb + benefits.co2_infra) * (co2Price796 / co2PriceBVWP - 1.) / benefits.investmentCosts;
//            Assertions.assertEquals(nkv_orig+delta_nkv, nkv, 0.001);
//        }
//        // neuer CO2-Preis & eMob:
//        {
//            double nkv = nkvOhneKR_induz(new Modifications( co2Price796, 0., 1., 1., 0.1 ), amounts, benefits, benefits.all  );
//            final double delta_b = -benefits.co2_infra - benefits.co2_betrieb + benefits.co2_infra*co2Price796/co2PriceBVWP + benefits.co2_betrieb*0.1*co2Price796/co2PriceBVWP;
//            final double delta_nkv = delta_b / benefits.investmentCosts;
//            log.info( "delta_b=" + delta_b + "; delta_nkv=" + delta_nkv );
//            Assertions.assertEquals(nkv_orig+delta_nkv, nkv, 0.001);
//        }

        // induced traffic alone:
        {
            final Modifications modifications = new Modifications( co2PriceBVWP, amounts.pkwkm_induz * 4., 1, 1., 1. );
            double nkv = nkvOhneKR_induz( modifications, amounts, benefits, benefits.all );
            // not sure if we can do the full manual computation.  according to new approach, the CO2 for the additional fzkm is computed independently from the rest.  In consequence, these are:
            double additional_co2_from_induz = modifications.mehrFzkm() * 100; // mio km * 100g/km * ton/1000000g = 100 tons

            double discounted_benefits_per_ton_over_30_years = benefits.co2_betrieb / amounts.co2_kfz;

            // yyyyyy the above is still confused; co2_betrieb is per year; benefits are in mio and per 30 years.  Need to align!

            double delta_b = discounted_benefits_per_ton_over_30_years * additional_co2_from_induz ;
            log.info( "addtl_co2_from_induz=" + additional_co2_from_induz + "; delta_b=" + delta_b );
            // yyyyyy the above would still need to be multiplied with 30 years, and then discounted!!


            Assertions.assertEquals(nkv_orig + delta_b/benefits.investmentCosts, nkv, 0.001);
        }

        // deprecated:
//        // this is, for backwards compatibility, the older computation where I had not split b_co2_betrieb into pkw
//        // and lkw:
//        amounts = new ComputationKN.Amounts(131.53, 143.95, 9.75, -18.56, 1.46, 0.13,
//                        -25., 48_689.94, -25., 48_689.94 );
//        {
//            double nkv = nkv(new Modifications(5. * 145., amounts.getPkwkm_induz() * 4., 1, 1., 1. ), amounts, benefits, baukosten );
//            Assertions.assertEquals(-0.089529, nkv, 0.001);
//        }
    }

}
