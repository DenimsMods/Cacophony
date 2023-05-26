package dev.denimred.cacophony.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.InstanceCreator;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import java.nio.file.Files;
import java.nio.file.Path;

import static dev.denimred.cacophony.Cacophony.LOGGER;
import static dev.denimred.cacophony.Cacophony.PLATFORM;

public abstract class SingletonJsonConfig<T extends SingletonJsonConfig<T>> {
    public transient final String filename;
    public transient final Path path;
    protected transient final Gson gson;

    @SuppressWarnings("unchecked")
    public SingletonJsonConfig(String filename) {
        this.filename = filename;
        this.path = PLATFORM.getConfigDir().resolve(filename);
        gson = new GsonBuilder().disableHtmlEscaping()
                                .setPrettyPrinting()
                                .setLenient()
                                .serializeNulls()
                                .registerTypeAdapter(getClass(), (InstanceCreator<T>) t -> (T) this) // This is a hack
                                .create();
    }

    public void load() {
        if (Files.isRegularFile(path)) {
            try (JsonReader reader = gson.newJsonReader(Files.newBufferedReader(path))) {
                gson.fromJson(reader, getClass());
            } catch (Exception e) {
                LOGGER.error("Failed to load config file: " + filename, e);
            }
        }
        save(); // Creates the file if it doesn't exist and ensures valid contents
    }

    public void save() {
        try (JsonWriter writer = gson.newJsonWriter(Files.newBufferedWriter(path))) {
            gson.toJson(gson.toJsonTree(this), writer);
        } catch (Exception e) {
            LOGGER.error("Failed to save config file: " + filename, e);
        }
    }
}
