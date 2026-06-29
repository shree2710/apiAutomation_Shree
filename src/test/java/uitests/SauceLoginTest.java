package uitests;

import org.openqa.selenium.WebDriver;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import ui.driver.DriverFactory;
import ui.pages.LoginPage;
import ui.pages.ProductsPage;
import utils.ConfigReader;

/**
 * UI test for SauceDemo login - exercises the page objects, the
 * {@link DriverFactory}, and the shared {@link ConfigReader}.
 */
public class SauceLoginTest {

    @BeforeMethod(alwaysRun = true)
    public void setUp() {
        DriverFactory.init();
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

    @Test(groups = {"regression"})
    public void invalidLoginShowsError() {
        WebDriver driver = DriverFactory.getDriver();

        String error = new LoginPage(driver)
                .open()
                .loginExpectingFailure("invalid_user", "wrong_password");

        Assert.assertTrue(error.toLowerCase().contains("username and password"),
                "An error message should be shown for invalid credentials");
    }

    @AfterMethod(alwaysRun = true)
    public void tearDown() {
        DriverFactory.quit();
    }
}
