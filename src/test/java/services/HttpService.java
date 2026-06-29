package services;

import io.restassured.response.Response;

/**
 * Abstraction over the HTTP verbs every API service needs.
 *
 * Programming to this interface (instead of to {@code RestAssured.given()}
 * scattered through the tests) keeps call sites uniform and lets us swap or
 * decorate the transport later without touching test code.
 */
public interface HttpService {

    Response get(String endpoint);

    Response post(String endpoint, Object body);

    Response put(String endpoint, Object body);

    Response delete(String endpoint);
}
