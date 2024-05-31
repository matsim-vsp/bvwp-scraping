package org.tub.vsp.bvwp.computation;

import org.junit.jupiter.api.Test;
import org.tub.vsp.bvwp.data.LocalFileAccessor;
import org.tub.vsp.bvwp.data.container.base.rail.RailBaseDataContainer;
import org.tub.vsp.bvwp.scraping.RailScraper;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;

class NkvCalculatorRailTest {
    @Test
    void test_rail() throws IOException {
        RailScraper railScraper = new RailScraper();
        RailBaseDataContainer railBaseDataContainer = railScraper.extractBaseData(LocalFileAccessor.getLocalDocument("2-003-v01.html")).orElseThrow();

        var nkvCalculator = new NkvCalculatorRail(railBaseDataContainer);
        Double calculateNkv = nkvCalculator.calculateNkv(Modifications.NO_CHANGE);
        assertEquals(2.1593, calculateNkv, 10e-4);
    }
}