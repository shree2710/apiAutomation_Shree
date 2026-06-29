package utils;

import exceptions.ConfigException;

import java.io.InputStream;
import java.util.Properties;

/**
 * Reusable core utility #1.
 *
 * Loads {@code config.properties} once from the classpath and exposes typed,
 * fail-fast accessors. Unlike loading via {@code FileInputStream}, classpath
 * loading does not depend on the process working directory, so it behaves the
 * same when run from the IDE, Maven, or CI.
 *
 * System properties win over the file, so any value can be overridden at run
 * time, e.g. {@code -Dpetstore.baseUrl=...}.
 */
public final class ConfigReader {

    private static final String CONFIG_FILE = "config.properties";
    private static final Properties PROPERTIES = load();

    private ConfigReader() {
        // utility class - no instances
    }

    private static Properties load() {
        Properties props = new Properties();
        try (InputStream in = ConfigReader.class.getClassLoader().getResourceAsStream(CONFIG_FILE)) {
            if (in == null) {
                throw new ConfigException(CONFIG_FILE + " not found on the classpath");
            }
            props.load(in);
            return props;
        } catch (ConfigException e) {
            throw e;
        } catch (Exception e) {
            throw new ConfigException("Failed to load " + CONFIG_FILE, e);
        }
    }

    /** Returns the value for {@code key}, or throws if it is missing/blank. */
    public static String get(String key) {
        String value = System.getProperty(key, PROPERTIES.getProperty(key));
        if (value == null || value.isBlank()) {
            throw new ConfigException("Missing required config key: " + key);
        }
        return value.trim();
    }

    /** Returns the value for {@code key}, or {@code defaultValue} when absent. */
    public static String get(String key, String defaultValue) {
        String value = System.getProperty(key, PROPERTIES.getProperty(key));
        return (value == null || value.isBlank()) ? defaultValue : value.trim();
    }
}
