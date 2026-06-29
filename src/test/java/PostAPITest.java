import org.testng.annotations.Test;

import java.util.HashMap;
import static io.restassured.RestAssured.*;
import static org.hamcrest.Matchers.*;

public class PostAPITest {
    @Test
    void postUsingHashMap(){
        HashMap data = new HashMap();
        data.put("name", "KPMG");
        data.put("location:", "Bangalore");
        data.put("phone:", "134221");

        String vacancies[] ={"SDE2","Consultant", "Assistant Manager"};
        data.put("role:",vacancies);

        given()
                .contentType("application/json").body(data).
                when().post("http://localhost:3000/employees").
                then().statusCode(201)
                .body("name",equalTo("KPMG"))
                .header("Content-Type","application/json").
                log().all();
    }
}
