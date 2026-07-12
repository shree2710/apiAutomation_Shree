package services;

import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import utils.ConfigReader;

/**
 * Reqres users API. The API key is a service-level concern, so it lives in the
 * request template rather than being repeated at every call site.
 */
public class ReqresService extends BaseService {

    private static final String USERS = "/api/users";

    public ReqresService() {
        super(ConfigReader.get("reqres.baseUrl"));
    }

    @Override
    protected RequestSpecification request() {
        return super.request().header("x-api-key", ConfigReader.get("reqres.apiKey"));
    }

    public Response getUsers(int page) {
        return request().queryParam("page", page).when().get(USERS);
    }
}
