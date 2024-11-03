package org.tub.vsp.bvwp.computation;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import static org.tub.vsp.bvwp.computation.ComputationKN.*;
import static org.tub.vsp.bvwp.computation.ComputationKN.nkv;
import static org.tub.vsp.bvwp.computation.Modifications.*;

class ComputationKNTest {
    @Test
//    @Disabled
        //needs to be fixed
    void test() {

        Amounts amounts = new Amounts(131.53, 143.95, 9.75, -18.56, 1.46, 0.13,
                        -25., 54_773.28, -25.,48_689.94 );
        BenefitsAndBaukosten benefits = new BenefitsAndBaukosten(-785.233, 2555.429, 1025.464, -151.319, -175.021
                , 5305.683, 2737.176 );
        {
            double nkv = nkv(new Modifications(co2PriceBVWP, 0., 1, 1., 1. ), amounts, benefits );
            Assertions.assertEquals(1.938378, nkv, 0.001);
        }

        // basic modifications:
        {
            double nkv = nkv(new Modifications( co2PriceBVWP, 0., 2., 1., 1. ), amounts, benefits );
            Assertions.assertEquals(1.938378/2, nkv, 0.001);
            // yyyyyy Baukosten?  Oder Investitionskosten?  Der streetBaseContainer sagt "alle Kosten"!?!?!?
        }
        {
            double nkv = nkv(new Modifications( co2Price796, 0., 1., 1., 1. ), amounts, benefits );
            Assertions.assertEquals(1.367378, nkv, 0.001);
            // yyyyyy check by manual computation
        }

        // everything with induced traffic comes near the end:
        {
            double nkv = nkv(new Modifications(5. * 145., amounts.getPkwkm_induz() * 4., 1, 1., 1. ), amounts, benefits );
//            Assertions.assertEquals(-0.2963518828995041, nkv, 0.001);
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
