import org.testng.annotations.Test;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;

public class GetAPITest {
   @Test
    public void getUsers() throws IOException {
       //String token = "reqres_1f66c01c2e0d493ca22fef8494ef8d21";
       String env = System.getProperty("env", "qa");

       Properties prop = new Properties();
       prop.load(new FileInputStream("src/main/resources/config.properties"));

       String baseUrl = prop.getProperty("baseUrl");
        given().header("x-api-key", "reqres_1f66c01c2e0d493ca22fef8494ef8d21")
                .log().all()
       .when().get(baseUrl).then().statusCode(200)
                .body("page",equalTo(2))
                .log().all();
    }
}
