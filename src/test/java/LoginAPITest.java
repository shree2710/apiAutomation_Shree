import io.restassured.response.Response;
import org.testng.annotations.Test;
import services.AuthService;

public class LoginAPITest {

    @Test(groups = {"regression"})
    public void loginTestWithAuth() {
        AuthService authService = new AuthService();
        Response response = authService.login("{\"username\": \"uday1234\", \"password\": \"uday1234\"}");
        System.out.println(response.asPrettyString());
    }
}
