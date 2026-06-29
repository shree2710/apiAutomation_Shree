package services;

import io.restassured.response.Response;
import utils.ConfigReader;

/**
 * Auth API. The base URL now comes from config instead of a hardcoded constant.
 */
public class AuthService extends BaseService {

    private static final String LOGIN = "/api/auth/login";

    public AuthService() {
        super(ConfigReader.get("auth.baseUrl"));
    }

    public Response login(String payload) {
        return post(LOGIN, payload);
    }
}
