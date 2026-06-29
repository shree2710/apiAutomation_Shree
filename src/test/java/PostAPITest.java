import org.testng.annotations.Test;
import utils.ConfigReader;

import java.util.HashMap;
import java.util.Map;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;

public class PostAPITest {

    @Test(groups = {"regression"})
    void postUsingHashMap() {
        Map<String, Object> data = new HashMap<>();
        data.put("name", "KPMG");
        data.put("location", "Bangalore");
        data.put("phone", "134221");
        data.put("role", new String[]{"SDE2", "Consultant", "Assistant Manager"});

        String baseUrl = ConfigReader.get("jsonserver.baseUrl");

        given()
                .contentType("application/json").body(data)
                .when().post(baseUrl + "/employees")
                .then().statusCode(201)
                .body("name", equalTo("KPMG"))
                .header("Content-Type", "application/json")
                .log().all();
    }
}
