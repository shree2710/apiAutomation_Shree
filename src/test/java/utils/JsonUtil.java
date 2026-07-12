package utils;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import exceptions.PayloadException;

import java.io.InputStream;

/**
 * Reusable core utility #2, used by both the API layer (request bodies and
 * response payloads in {@code services.BaseService}) and the UI layer
 * (JSON-driven test data).
 *
 * One shared, thread-safe {@link ObjectMapper} for the whole framework so that
 * every test serializes/deserializes JSON the same way. Checked Jackson
 * exceptions are wrapped in the framework's {@link PayloadException} so callers
 * do not have to handle {@code JsonProcessingException} everywhere.
 */
public final class JsonUtil {

    private static final ObjectMapper MAPPER = new ObjectMapper()
            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

    private JsonUtil() {
        // utility class - no instances
    }

    /** Serializes any object to a compact JSON string. */
    public static String toJson(Object object) {
        try {
            return MAPPER.writeValueAsString(object);
        } catch (Exception e) {
            throw new PayloadException("Could not serialize " + object.getClass().getSimpleName(), e);
        }
    }

    /** Serializes any object to a pretty-printed JSON string. */
    public static String toPrettyJson(Object object) {
        try {
            return MAPPER.writerWithDefaultPrettyPrinter().writeValueAsString(object);
        } catch (Exception e) {
            throw new PayloadException("Could not serialize " + object.getClass().getSimpleName(), e);
        }
    }

    /** Deserializes a JSON string into an instance of {@code type}. */
    public static <T> T fromJson(String json, Class<T> type) {
        try {
            return MAPPER.readValue(json, type);
        } catch (Exception e) {
            throw new PayloadException("Could not deserialize JSON into " + type.getSimpleName(), e);
        }
    }

    /**
     * Deserializes a JSON file on the classpath into an instance of {@code type}.
     * Used for JSON-driven test data (pass an array type for a list of records).
     */
    public static <T> T fromJsonResource(String resource, Class<T> type) {
        try (InputStream in = JsonUtil.class.getClassLoader().getResourceAsStream(resource)) {
            if (in == null) {
                throw new PayloadException("Test data file not found on the classpath: " + resource);
            }
            return MAPPER.readValue(in, type);
        } catch (PayloadException e) {
            throw e;
        } catch (Exception e) {
            throw new PayloadException("Could not read " + resource + " as " + type.getSimpleName(), e);
        }
    }
}
