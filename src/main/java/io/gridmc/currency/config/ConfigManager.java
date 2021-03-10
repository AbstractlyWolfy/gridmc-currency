package io.gridmc.currency.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.apache.commons.lang3.Validate;

import javax.annotation.Nonnull;
import java.io.*;
import java.util.function.Function;

/**
 * Handles Configuration files.
 */
public final class ConfigManager {

    /**
     * Default GSON instance.
     */
    private static final Gson GSON;

    static {
        // Build GSON instance.
        GSON = new GsonBuilder()
                .setPrettyPrinting()
                .serializeNulls()
                .create();
    }

    /**
     * Load the configuration file data.
     *
     * @param clazz    - clazz.
     * @param defaults - default data.
     * @return Config
     */
    public static <T> T loadConfigFile(@Nonnull Class<T> clazz, Function<Class<T>, T> defaults) {
        return loadConfigFile(clazz, GSON, defaults);
    }

    /**
     * Load the configuration file data.
     *
     * @param clazz    - clazz.
     * @param defaults - default data.
     * @return Config
     */
    public static <T> T loadConfigFile(@Nonnull Class<T> clazz, Gson gson, Function<Class<T>, T> defaults) {

        // Store the config data.
        T configObj = null;

        // Get the config file based on class and create string builder to build json.
        File file = getConfigFile(clazz);
        StringBuilder builder = new StringBuilder();

        // Create streams and buffers to read the appropriate data.
        try (FileInputStream inputStream = new FileInputStream(file); final InputStreamReader streamReader = new InputStreamReader(inputStream); final BufferedReader buffer = new BufferedReader(streamReader)) {
            String readLine;
            // Load in the appropriate data line by line into the StringBuilder
            while ((readLine = buffer.readLine()) != null) builder.append(readLine);
            // Load the data loaded through the StringBuilder into json.
            configObj = gson.fromJson(builder.toString(), clazz);
        } catch (IOException e) {

            // If the file does not exist, pre load the default data.
            if (e instanceof FileNotFoundException) {
                configObj = defaults.apply(clazz);
                saveConfigFile(clazz, gson, configObj);
                return configObj;
            }
        }

        // Return the config data for the user.
        return configObj;
    }

    /**
     * Save a configuration file.
     *
     * @param clazz - clazz.
     * @param obj   - data.
     */
    public static <T> void saveConfigFile(@Nonnull Class<T> clazz, T obj) {
        saveConfigFile(clazz, GSON, obj);
    }

    /**
     * Save a configuration file.
     *
     * @param clazz - clazz.
     * @param gson  - gson.
     * @param obj   - data.
     */
    public static <T> void saveConfigFile(@Nonnull Class<T> clazz, Gson gson, T obj) {

        // Get the config file based on class.
        File file = getConfigFile(clazz);

        // Create directories if they do not exist already.
        if (file.getParentFile() != null) file.getParentFile().mkdirs();

        // Create a FileWriter instance to write the json.
        try (FileWriter writer = new FileWriter(file)) {
            // Write json.
            gson.toJson(obj, writer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Get a configuration file.
     *
     * @param clazz - clazz.
     * @return File
     */
    public static File getConfigFile(@Nonnull Class<?> clazz) {

        // Validate that the config annotation exists
        Config config = Validate.notNull(
                clazz.getAnnotation(Config.class),
                "Config annotation` cannot be found."
        );

        // Return the default configurations file.
        return new File("configs/" + config.dir(), config.name() + ".json");
    }
}
