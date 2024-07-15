package org.tub.vsp.bvwp.computation;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import static org.tub.vsp.bvwp.computation.ComputationKN.nkv;

class ComputationKNTest {
    @Test
    @Disabled
        //needs to be fixed
    void test() {

        ComputationKN.Amounts amounts = new ComputationKN.Amounts(131.53, 143.95, 9.75, -18.56, 1.46, 0.13, 54_773.28
                , 48_689.94);
        ComputationKN.Benefits benefits = new ComputationKN.Benefits(-785.233, 2555.429, 1025.464, -151.319, -175.021
                , 5305.683);
        double baukosten = 2737.176;
        {
            double nkv = nkv(new Modifications(145., 0., 1, 1., 1. ), amounts, benefits, baukosten );
            Assertions.assertEquals(1.938378, nkv, 0.001);
        }
        {
            double nkv = nkv(new Modifications(5. * 145., amounts.getPkwkm_induz() * 4., 1, 1., 1. ), amounts, benefits, baukosten );
            Assertions.assertEquals(-0.2963518828995041, nkv, 0.001);
        }

        // this is, for backwards compatibility, the older computation where I had not split b_co2_betrieb into pkw
        // and lkw:
        amounts = new ComputationKN.Amounts(131.53, 143.95, 9.75, -18.56, 1.46, 0.13, 48_689.94, 48_689.94);
        {
            double nkv = nkv(new Modifications(5. * 145., amounts.getPkwkm_induz() * 4., 1, 1., 1. ), amounts, benefits, baukosten );
            Assertions.assertEquals(-0.089529, nkv, 0.001);
        }
    }

}
