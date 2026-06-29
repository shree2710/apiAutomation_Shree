import io.restassured.http.ContentType;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;

import static io.restassured.RestAssured.given;

public class BaseService {
    public static final String base_url= "http://64.227.160.186:8080";
    private RequestSpecification rs;

    public BaseService(){
        rs= given().baseUri(base_url);
    }

    protected Response postRequest(String payload,String endpoint){
        return rs.contentType(ContentType.JSON).body(payload).post(endpoint);
    }
}
