package apitests;

import io.restassured.module.jsv.JsonSchemaValidator;
import io.restassured.response.Response;
import org.testng.Assert;
import org.testng.annotations.Test;
import services.EmployeeService;

import java.util.HashMap;
import java.util.Map;

/**
 * Local json-server tests: a Map-based POST and a JSON-schema check, both
 * issued through {@link EmployeeService}.
 *
 * Requires a json-server on the configured {@code jsonserver.baseUrl}.
 */
public class EmployeeTest {

    private final EmployeeService employees = new EmployeeService();

    @Test(groups = {"regression"})
    public void postUsingHashMap() {
        Map<String, Object> data = new HashMap<>();
        data.put("name", "KPMG");
        data.put("location", "Bangalore");
        data.put("phone", "134221");
        data.put("role", new String[]{"SDE2", "Consultant", "Assistant Manager"});

        Response response = employees.createEmployee(data);
        response.then().log().all();

        Assert.assertEquals(response.getStatusCode(), 201);
        Assert.assertEquals(response.jsonPath().getString("name"), "KPMG");
    }

    @Test(groups = {"regression"})
    public void storeMatchesJsonSchema() {
        employees.getStore()
                .then().assertThat()
                .body(JsonSchemaValidator.matchesJsonSchemaInClasspath("storeSchema.json"));
    }
}
