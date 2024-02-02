package org.tub.vsp.computation;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import static org.tub.vsp.computation.NkvCalculator.calculateNkv;

class ComputationTest {
    @Test
    void testOldA20() {
        //I think these values are wrong. See down below for the correct values
        NkvCalculator.Amounts amounts = new NkvCalculator.Amounts(-18.56, 1.46, 0.13, 131.53, 143.95, 9.75);
        NkvCalculator.Benefits benefits = new NkvCalculator.Benefits(-785.233, 2555.429, 1025.464, -151.319, -175.021,
                5305.683);
        double baukosten = 2737.176;
        {
            double nkv = calculateNkv(new Modifications(1., 145., 0.), amounts, benefits, baukosten);
            Assertions.assertEquals(1.938378, nkv, 0.001);
        }
        {
            double nkv = calculateNkv(new Modifications(5., 5. * 145., 0.), amounts, benefits, baukosten);
            Assertions.assertEquals(-0.089529, nkv, 0.001);
        }
    }

    @Test
    void testCorrectA20() {
        NkvCalculator.Amounts amounts = new NkvCalculator.Amounts(-18.56, 1.99, 0.2, 131.53, 143.95, 9.75);
        NkvCalculator.Benefits benefits = new NkvCalculator.Benefits(-785.233, 2555.429, 1025.464, -151.319, -175.021,
                5305.683);
        double baukosten = 2737.176;
        {
            double nkv = calculateNkv(new Modifications(1., 145., 0.), amounts, benefits, baukosten);
            Assertions.assertEquals(1.938378, nkv, 0.001);
        }
        {
            double nkv = calculateNkv(new Modifications(5., 5. * 145., 0.), amounts, benefits, baukosten);
            Assertions.assertEquals(-0.201216, nkv, 0.001);
        }
    }

}