import static io.restassured.RestAssured.given;

import io.restassured.module.jsv.JsonSchemaValidator;
import org.testng.annotations.Test;

public class JsonSchemaValidation {
    @Test
    void schemaValidation(){
        given().
                when().get("http://localhost:3000/store")
                .then().assertThat().body(JsonSchemaValidator.matchesJsonSchemaInClasspath("storeSchema.json"));

    }
}

