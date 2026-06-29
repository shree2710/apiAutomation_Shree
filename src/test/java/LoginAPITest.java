import io.restassured.response.Response;
import org.testng.annotations.Test;

public class LoginAPITest {
    @Test
    public void logintestwithAuth(){
        AuthService as = new AuthService();
        Response r = as.login("{\"username\": \"uday1234\",\"password\":\"uday1234\"}");
        System.out.println(r.asPrettyString());
    }

    public void logintest(){

    }
}
