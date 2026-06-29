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
 * Values resolve with the precedence: system property ({@code -Dkey=...}) ->
 * environment variable ({@code KEY_WITH_UNDERSCORES}) -> the file. This lets
 * secrets (API keys, credentials) be supplied by CI or the shell instead of
 * being committed, while the file stays a sensible default.
 */
public final class ConfigReader {

    private static final String CONFIG_FILE = "config.properties";
    private static final Properties PROPERTIES = load();

    private ConfigReader() {
        // utility class - no instances
    }

    private static Properties load() {
        Properties props = new Properties();
        loadInto(props, CONFIG_FILE, true);
        // Environment overlay: -Denv=qa|staging|prod (or ENV var) overrides base values.
        String env = environment();
        if (env != null && !env.isBlank()) {
            loadInto(props, "config-" + env.trim().toLowerCase() + ".properties", false);
        }
        return props;
    }

    /** Loads {@code resource} into {@code props}, overwriting existing keys. */
    private static void loadInto(Properties props, String resource, boolean required) {
        try (InputStream in = ConfigReader.class.getClassLoader().getResourceAsStream(resource)) {
            if (in == null) {
                if (required) {
                    throw new ConfigException(resource + " not found on the classpath");
                }
                return; // optional overlay simply absent
            }
            props.load(in);
        } catch (ConfigException e) {
            throw e;
        } catch (Exception e) {
            throw new ConfigException("Failed to load " + resource, e);
        }
    }

    /** The selected environment from {@code -Denv} or the {@code ENV} variable, if any. */
    private static String environment() {
        String env = System.getProperty("env");
        return (env != null && !env.isBlank()) ? env : System.getenv("ENV");
    }

    /** Returns the value for {@code key}, or throws if it is missing/blank. */
    public static String get(String key) {
        String value = resolve(key);
        if (value == null || value.isBlank()) {
            throw new ConfigException("Missing required config key: " + key);
        }
        return value.trim();
    }

    /** Returns the value for {@code key}, or {@code defaultValue} when absent. */
    public static String get(String key, String defaultValue) {
        String value = resolve(key);
        return (value == null || value.isBlank()) ? defaultValue : value.trim();
    }

    /** Resolves a key as: system property -> environment variable -> file. */
    private static String resolve(String key) {
        String sysProp = System.getProperty(key);
        if (sysProp != null && !sysProp.isBlank()) {
            return sysProp;
        }
        String env = System.getenv(toEnvVar(key));
        if (env != null && !env.isBlank()) {
            return env;
        }
        return PROPERTIES.getProperty(key);
    }

    /** Maps a config key to its environment-variable form, e.g. {@code ui.baseUrl -> UI_BASEURL}. */
    private static String toEnvVar(String key) {
        return key.toUpperCase().replace('.', '_').replace('-', '_');
    }
}
