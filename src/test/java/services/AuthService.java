package services;

import api_payloads.Credentials;
import io.restassured.response.Response;
import utils.ConfigReader;

/**
 * Auth API. The base URL comes from config, and the credentials payload is
 * serialized by the shared JsonUtil mapper via {@link BaseService}.
 */
public class AuthService extends BaseService {

    private static final String LOGIN = "/api/auth/login";

    public AuthService() {
        super(ConfigReader.get("auth.baseUrl"));
    }

    public Response login(Credentials credentials) {
        return post(LOGIN, credentials);
    }
}
