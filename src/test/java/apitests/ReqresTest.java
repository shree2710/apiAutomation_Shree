package apitests;

import io.restassured.response.Response;
import org.testng.Assert;
import org.testng.annotations.Test;
import services.ReqresService;

/**
 * Reqres users API. Goes through {@link ReqresService}, so the base URL and the
 * API key stay out of the test.
 */
public class ReqresTest {

    private final ReqresService reqres = new ReqresService();

    @Test(groups = {"regression"})
    public void getUsersReturnsRequestedPage() {
        Response response = reqres.getUsers(2);
        response.then().log().all();

        Assert.assertEquals(response.getStatusCode(), 200);
        Assert.assertEquals(response.jsonPath().getInt("page"), 2);
        Assert.assertFalse(response.jsonPath().getList("data").isEmpty(),
                "The users page should not be empty");
    }
}
