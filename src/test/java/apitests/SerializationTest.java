package apitests;

import api_payloads.Employee;
import org.testng.Assert;
import org.testng.annotations.Test;
import utils.JsonUtil;

import java.util.List;

/**
 * Offline JSON round-trip through the shared {@link JsonUtil} mapper.
 */
public class SerializationTest {

    @Test(groups = {"smoke", "regression"})
    public void convertPojoToJson() {
        Employee employee = new Employee();
        employee.setName("John");
        employee.setJob_title("SDET");
        employee.setDepartment(List.of("Accounts", "HR", "Transit"));

        String json = JsonUtil.toPrettyJson(employee);

        Assert.assertTrue(json.contains("SDET"), "Serialized JSON should carry the job title");
        Assert.assertTrue(json.contains("Accounts"), "Serialized JSON should carry the departments");
    }

    @Test(groups = {"regression"})
    public void convertJsonToPojo() {
        String json = """
                {
                  "id" : null,
                  "name" : "John",
                  "job_title" : "SDET",
                  "department" : [ "Accounts", "HR", "Transit" ],
                  "salary" : null
                }
                """;

        Employee employee = JsonUtil.fromJson(json, Employee.class);

        Assert.assertEquals(employee.getName(), "John");
        Assert.assertEquals(employee.getJob_title(), "SDET");
        Assert.assertEquals(employee.getDepartment(), List.of("Accounts", "HR", "Transit"));
    }
}
