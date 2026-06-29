import org.testng.annotations.Test;
import utils.ConfigReader;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;

public class GetAPITest {

    @Test
    public void getUsers() {
        String baseUrl = ConfigReader.get("reqres.baseUrl");
        String apiKey = ConfigReader.get("reqres.apiKey");

        given().header("x-api-key", apiKey)
                .log().all()
                .when().get(baseUrl)
                .then().statusCode(200)
                .body("page", equalTo(2))
                .log().all();
    }
}
