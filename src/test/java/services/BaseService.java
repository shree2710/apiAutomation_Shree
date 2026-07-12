package services;

import io.restassured.http.ContentType;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import utils.JsonUtil;

import static io.restassured.RestAssured.given;

/**
 * Shared transport for every API service: one place that owns the base URI,
 * the JSON content type, and the serialization of request/response payloads.
 *
 * Bodies go out through {@link JsonUtil} and responses come back through it,
 * so the whole framework uses a single ObjectMapper instead of letting each
 * caller (or REST Assured) pick its own.
 */
public abstract class BaseService implements HttpService {

    private final String baseUri;

    protected BaseService(String baseUri) {
        this.baseUri = baseUri;
    }

    /**
     * The request template for this service. Subclasses override it to add
     * service-specific concerns such as auth headers.
     */
    protected RequestSpecification request() {
        return given()
                .baseUri(baseUri)
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON);
    }

    /** Serializes a payload with the shared mapper; raw JSON strings pass through untouched. */
    protected static String serialize(Object body) {
        return (body instanceof String json) ? json : JsonUtil.toJson(body);
    }

    /** Deserializes a response body into {@code type} using the shared mapper. */
    protected static <T> T as(Response response, Class<T> type) {
        return JsonUtil.fromJson(response.asString(), type);
    }

    @Override
    public Response get(String endpoint) {
        return request().when().get(endpoint);
    }

    @Override
    public Response post(String endpoint, Object body) {
        return request().body(serialize(body)).when().post(endpoint);
    }

    @Override
    public Response put(String endpoint, Object body) {
        return request().body(serialize(body)).when().put(endpoint);
    }

    @Override
    public Response delete(String endpoint) {
        return request().when().delete(endpoint);
    }
}
