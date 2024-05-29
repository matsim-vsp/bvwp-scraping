package org.tub.vsp.bvwp.io;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.tub.vsp.bvwp.data.LocalFileAccessor;
import org.tub.vsp.bvwp.data.container.base.rail.RailBaseDataContainer;
import org.tub.vsp.bvwp.data.container.base.street.StreetBaseDataContainer;
import org.tub.vsp.bvwp.scraping.RailScraper;
import org.tub.vsp.bvwp.scraping.StreetScraper;

import java.io.IOException;

class JsonIoTest {
    @Test
    void testSerializeAndDeserializeJson_street() throws IOException {
        StreetScraper streetScraper = new StreetScraper();
        StreetBaseDataContainer streetBaseData = streetScraper.extractBaseData(LocalFileAccessor.getLocalDocument("a20.html"))
                             .orElseThrow();

        //write Json to file
        JsonIo jsonIo = new JsonIo();
        String filePath = "output/a20.json";
        jsonIo.writeJson(streetBaseData, filePath);

        //read Json from file
        StreetBaseDataContainer deserializedContainer = jsonIo.readJson(filePath, StreetBaseDataContainer.class);
        Assertions.assertEquals(streetBaseData, deserializedContainer);
    }

    @Test
    void testSerializeAndDeserializeJson_rail() throws IOException {
        RailScraper railScraper = new RailScraper();
        RailBaseDataContainer railBaseData = railScraper.extractBaseData(LocalFileAccessor.getLocalDocument("2-003-v01.html"))
                                                        .orElseThrow();

        //write Json to file
        JsonIo jsonIo = new JsonIo();
        String filePath = "output/2-003-v01.json";
        jsonIo.writeJson(railBaseData, filePath);

        //read Json from file
        RailBaseDataContainer deserializedContainer = jsonIo.readJson(filePath, RailBaseDataContainer.class);
        Assertions.assertEquals(railBaseData, deserializedContainer);
    }

    @Test
    void testSerializeJson_street() throws IOException {
        StreetScraper streetScraper = new StreetScraper();
        StreetBaseDataContainer streetBaseData = streetScraper.extractBaseData(LocalFileAccessor.getLocalDocument("a20.html"))
                             .orElseThrow();

        //write Json to file
        JsonIo jsonIo = new JsonIo();
        String filePath = "src/test/resources/testData/referenceData/a20.json";
        StreetBaseDataContainer deserializedContainer = jsonIo.readJson(filePath, StreetBaseDataContainer.class);
        Assertions.assertEquals(streetBaseData, deserializedContainer);
    }

    @Test
    void testSerializeJson_rail() throws IOException {
        RailScraper railScraper = new RailScraper();
        RailBaseDataContainer streetBaseData = railScraper.extractBaseData(LocalFileAccessor.getLocalDocument("2-003-v01.html"))
                                                          .orElseThrow();

        //write Json to file
        JsonIo jsonIo = new JsonIo();
        String filePath = "src/test/resources/testData/referenceData/2-003-v01.json";
        RailBaseDataContainer deserializedContainer = jsonIo.readJson(filePath, RailBaseDataContainer.class);
        Assertions.assertEquals(streetBaseData, deserializedContainer);
    }
}
