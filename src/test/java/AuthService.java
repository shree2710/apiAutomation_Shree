import io.restassured.response.Response;

public class AuthService extends BaseService {
   public static final String basepath = "/api/auth/";

   public Response login(String payload){
       return postRequest(payload,basepath+ "login");
   }
}
