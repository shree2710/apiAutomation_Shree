package utils;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import exceptions.PayloadException;

/**
 * Reusable core utility #2.
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
}
