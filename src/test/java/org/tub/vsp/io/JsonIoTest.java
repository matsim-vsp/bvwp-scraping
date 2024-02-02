package org.tub.vsp.io;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.tub.vsp.data.LocalFileAccessor;
import org.tub.vsp.data.container.base.StreetBaseDataContainer;
import org.tub.vsp.scraping.StreetScraper;

import java.io.IOException;

class JsonIoTest {
    @Test
    void testSerializeAndDeserializeJson() throws IOException {
        StreetScraper streetScraper = new StreetScraper();
        StreetBaseDataContainer streetBaseData =
                streetScraper.extractBaseData(LocalFileAccessor.getLocalDocument("a20.html"))
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
    void testSerializeJson() throws IOException {
        StreetScraper streetScraper = new StreetScraper();
        StreetBaseDataContainer streetBaseData =
                streetScraper.extractBaseData(LocalFileAccessor.getLocalDocument("a20.html"))
                             .orElseThrow();

        //write Json to file
        JsonIo jsonIo = new JsonIo();
        String filePath = "src/test/resources/testData/referenceData/a20.json";
        StreetBaseDataContainer deserializedContainer = jsonIo.readJson(filePath, StreetBaseDataContainer.class);
        Assertions.assertEquals(streetBaseData, deserializedContainer);
    }
}