package uitests;

import org.openqa.selenium.WebDriver;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import ui.driver.DriverFactory;
import ui.model.LoginUser;
import ui.pages.LoginPage;
import ui.pages.ProductsPage;
import utils.ConfigReader;
import utils.JsonUtil;

import java.util.Arrays;

/**
 * UI tests for SauceDemo login - showcasing the page objects, the
 * {@code DriverFactory}, and both shared core utilities: {@code ConfigReader}
 * for the environment/credentials and {@code JsonUtil} for the JSON-driven
 * negative-login data.
 */
public class SauceLoginTest {

    private static final String LOGIN_DATA = "testdata/ui-login-users.json";

    @BeforeMethod(alwaysRun = true)
    public void setUp() {
        DriverFactory.init();
    }

    /** Negative-login rows, deserialized from JSON by the shared JsonUtil mapper. */
    @DataProvider(name = "invalidUsers")
    public Object[][] invalidUsers() {
        LoginUser[] users = JsonUtil.fromJsonResource(LOGIN_DATA, LoginUser[].class);
        return Arrays.stream(users)
                .map(user -> new Object[]{user})
                .toArray(Object[][]::new);
    }

    @Test(groups = {"smoke", "regression"})
    public void validLoginShowsProducts() {
        WebDriver driver = DriverFactory.getDriver();

        ProductsPage products = new LoginPage(driver)
                .open()
                .loginAs(ConfigReader.get("ui.username"), ConfigReader.get("ui.password"));

        Assert.assertTrue(products.isLoaded(), "Products page should load after a valid login");
        Assert.assertEquals(products.title(), "Products");
        Assert.assertFalse(products.productNames().isEmpty(), "Product list should not be empty");
    }

    @Test(groups = {"regression"}, dataProvider = "invalidUsers")
    public void invalidLoginShowsError(LoginUser user) {
        WebDriver driver = DriverFactory.getDriver();

        String error = new LoginPage(driver)
                .open()
                .loginExpectingFailure(user.getUsername(), user.getPassword());

        Assert.assertTrue(error.toLowerCase().contains(user.getExpectedError().toLowerCase()),
                "Expected an error containing '" + user.getExpectedError()
                        + "' for the " + user.getDescription() + ", but got: " + error);
    }

    @AfterMethod(alwaysRun = true)
    public void tearDown() {
        DriverFactory.quit();
    }
}
