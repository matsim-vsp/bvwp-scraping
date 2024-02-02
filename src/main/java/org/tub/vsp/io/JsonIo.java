package org.tub.vsp.io;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.io.Writer;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

public class JsonIo {
    private static final Logger logger = LogManager.getLogger(JsonIo.class);

    Gson gson;

    public JsonIo(Gson gson) {
        this.gson = gson;
    }

    public JsonIo() {
        this.gson = new GsonBuilder().serializeNulls()
                                     .setPrettyPrinting()
                                     .create();
    }

    public void writeJson(Object object, String filePath) {
        //make sure that the directory exists
        Path path = Paths.get(filePath);
        try {
            Files.createDirectories(path.getParent());
        } catch (IOException e) {
            logger.error("Could not create directory for file {}", filePath);
            throw new RuntimeException(e);
        }

        try (Writer writer = Files.newBufferedWriter(path)) {
            gson.toJson(object, writer);
            writer.flush();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    public <T> T readJson(String filePath, Class<T> classOfT) throws IOException {
        return gson.fromJson(Files.newBufferedReader(Paths.get(filePath)), classOfT);
    }

    public <T> List<T> readJsonList(String filePath, Type type) throws IOException {
        return gson.fromJson(Files.newBufferedReader(Paths.get(filePath)), type);
    }
}
