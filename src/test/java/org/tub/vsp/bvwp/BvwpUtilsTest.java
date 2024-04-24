package org.tub.vsp.bvwp;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.util.Map;

class BvwpUtilsTest {
    //Doesn't work online
    @Test
    @Disabled
    void testTumConstructionCosts() {
        Map<String, Double> data = BvwpUtils.getConstructionCostsFromTumFile("/Users/paulheinrich/shared-svn");
        Assertions.assertEquals(data.get("A003-G20-HE-T1-HE"), 470.0);
        Assertions.assertEquals(data.get("A5-G20-HE-T2-HE"), 469.0);
    }
}