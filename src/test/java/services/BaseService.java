package services;

import io.restassured.http.ContentType;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;

import static io.restassured.RestAssured.given;

/**
 * Common REST Assured plumbing shared by every concrete service.
 *
 * Each service is bound to a base URI (so the same code works against reqres,
 * the auth server, Petstore, etc.) and gets the four HTTP verbs for free.
 * Subclasses that need extras - path params, headers, query params - build on
 * top of {@link #request()} rather than re-deriving the spec.
 */
public abstract class BaseService implements HttpService {

    private final String baseUri;

    protected BaseService(String baseUri) {
        this.baseUri = baseUri;
    }

    /** A fresh JSON-configured request spec; the single place the spec is built. */
    protected RequestSpecification request() {
        return given()
                .baseUri(baseUri)
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON);
    }

    @Override
    public Response get(String endpoint) {
        return request().when().get(endpoint);
    }

    @Override
    public Response post(String endpoint, Object body) {
        return request().body(body).when().post(endpoint);
    }

    @Override
    public Response put(String endpoint, Object body) {
        return request().body(body).when().put(endpoint);
    }

    @Override
    public Response delete(String endpoint) {
        return request().when().delete(endpoint);
    }
}
