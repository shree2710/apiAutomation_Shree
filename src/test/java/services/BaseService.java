package services;

import io.restassured.http.ContentType;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;

import static io.restassured.RestAssured.given;


public abstract class BaseService implements HttpService {

    private final String baseUri;

    protected BaseService(String baseUri) {
        this.baseUri = baseUri;
    }


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
