package apitests;

import api_payloads.Credentials;
import io.restassured.response.Response;
import org.testng.Assert;
import org.testng.annotations.Test;
import services.AuthService;

/**
 * Login against the private auth server (not part of the CI suite - see
 * FAILURE_ANALYSIS.md).
 */
public class AuthTest {

    private final AuthService auth = new AuthService();

    @Test(groups = {"regression"})
    public void loginWithValidCredentials() {
        Response response = auth.login(new Credentials("uday1234", "uday1234"));
        response.then().log().all();

        Assert.assertEquals(response.getStatusCode(), 200);
    }
}
