import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.testng.annotations.Test;

public class SerializationDeserialization {

    @Test
    void convertPojoToJson() throws JsonProcessingException {

        //created java obj using pojo
        Pojo stuobj = new Pojo();
        stuobj.setName("John");
        stuobj.setJob_title("SDET");
        String[] dep ={"Accounts","HR", "Transit"};
        stuobj.setDepartment(dep);

        //converting java -- json obj
        ObjectMapper objm= new ObjectMapper();
        String jsondata = objm.writerWithDefaultPrettyPrinter().writeValueAsString(stuobj);
        System.out.println(jsondata);
    }

    void convertJsontoPojo() throws JsonProcessingException {


        String jsondata = "{\n" +
                "  \"id\" : null,\n" +
                "  \"name\" : \"John\",\n" +
                "  \"job_title\" : \"SDET\",\n" +
                "  \"department\" : [ \"Accounts\", \"HR\", \"Transit\" ],\n" +
                "  \"salary\" : null\n" +
                "}";

        //converting json data -- Pojo obj
        ObjectMapper objm= new ObjectMapper();
        Pojo p = objm.readValue(jsondata,Pojo.class);
        System.out.println("Name:"+p.getName());
        System.out.println("JobTitle:"+p.getJob_title());
        System.out.println("Department:"+p.getDepartment()[0]);

    }

}
