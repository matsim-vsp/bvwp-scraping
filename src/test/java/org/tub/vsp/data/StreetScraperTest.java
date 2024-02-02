package org.tub.vsp.data;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.tub.vsp.data.container.StreetBaseDataContainer;
import org.tub.vsp.scraping.StreetScraper;

import java.io.IOException;
import java.util.Optional;

class StreetScraperTest {
    @Test
    void testStreetScraper() throws IOException {
        StreetScraper streetScraper = new StreetScraper();
        Optional<StreetBaseDataContainer> streetBaseData =
                streetScraper.extractBaseData(LocalFileAccessor.getLocalDocument("a20.html"));

        Assertions.assertTrue(streetBaseData.isPresent());
        StreetBaseDataContainer streetBaseDataContainer = streetBaseData.get();

        Assertions.assertNotNull(streetBaseDataContainer.getProjectInformation());
        Assertions.assertNotNull(streetBaseDataContainer.getPhysicalEffect());
        Assertions.assertNotNull(streetBaseDataContainer.getCostBenefitAnalysis());
    }

    @Test
    void testPartialProject() throws IOException {
        StreetScraper streetScraper = new StreetScraper();
        Optional<StreetBaseDataContainer> streetBaseData =
                streetScraper.extractBaseData(LocalFileAccessor.getLocalDocument("a1-partial.html"));

        Assertions.assertTrue(streetBaseData.isEmpty());
    }

    @Test
    void testPartialProjectReferencingMain() throws IOException {
        StreetScraper streetScraper = new StreetScraper();
        Optional<StreetBaseDataContainer> streetBaseData =
                streetScraper.extractBaseData(LocalFileAccessor.getLocalDocument("a96-partial.html"));

        Assertions.assertTrue(streetBaseData.isPresent());
    }

    @Test
    void testMainProjectReferencingPartial() throws IOException {
        StreetScraper streetScraper = new StreetScraper();
        Optional<StreetBaseDataContainer> streetBaseData =
                streetScraper.extractBaseData(LocalFileAccessor.getLocalDocument("a96-main.html"));

        Assertions.assertTrue(streetBaseData.isPresent());

        StreetBaseDataContainer streetBaseDataContainer = streetBaseData.get();
        Assertions.assertEquals(streetBaseDataContainer.getPhysicalEffect()
                                                       .getEmissionsDataContainer()
                                                       .emissions()
                                                       .size(), 0);
    }
}