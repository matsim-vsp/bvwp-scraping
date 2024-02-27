package org.tub.vsp.bvwp.data;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.File;
import java.io.IOException;

public class LocalFileAccessor {
    public static Document getLocalDocument(String fileName) throws IOException {
        File input = new File("src/test/resources/testData/" + fileName);
        return Jsoup.parse(input, "UTF-8");
    }
}
