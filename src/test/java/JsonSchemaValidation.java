import io.restassured.module.jsv.JsonSchemaValidator;
import org.testng.annotations.Test;
import utils.ConfigReader;

import static io.restassured.RestAssured.given;

public class JsonSchemaValidation {

    @Test(groups = {"regression"})
    void schemaValidation() {
        String baseUrl = ConfigReader.get("jsonserver.baseUrl");
        given()
                .when().get(baseUrl + "/store")
                .then().assertThat()
                .body(JsonSchemaValidator.matchesJsonSchemaInClasspath("storeSchema.json"));
    }
}
