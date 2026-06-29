import org.testng.Assert;
import org.testng.annotations.Test;
import utils.JsonUtil;

public class SerializationDeserialization {

    @Test(groups = {"smoke", "regression"})
    void convertPojoToJson() {
        Pojo student = new Pojo();
        student.setName("John");
        student.setJob_title("SDET");
        student.setDepartment(new String[]{"Accounts", "HR", "Transit"});

        // Java object -> JSON via the shared utility
        String json = JsonUtil.toPrettyJson(student);
        System.out.println(json);
        Assert.assertTrue(json.contains("SDET"));
    }

    @Test(groups = {"regression"})
    void convertJsonToPojo() {
        String json = "{\n" +
                "  \"id\" : null,\n" +
                "  \"name\" : \"John\",\n" +
                "  \"job_title\" : \"SDET\",\n" +
                "  \"department\" : [ \"Accounts\", \"HR\", \"Transit\" ],\n" +
                "  \"salary\" : null\n" +
                "}";

        // JSON -> Java object via the shared utility
        Pojo p = JsonUtil.fromJson(json, Pojo.class);
        System.out.println("Name: " + p.getName());
        System.out.println("JobTitle: " + p.getJob_title());
        System.out.println("Department: " + p.getDepartment()[0]);

        Assert.assertEquals(p.getName(), "John");
        Assert.assertEquals(p.getJob_title(), "SDET");
    }
}
