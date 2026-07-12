package ui.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import utils.ConfigReader;


public class LoginPage extends BasePage {

    private static final By USERNAME = By.id("user-name");
    private static final By PASSWORD = By.id("password");
    private static final By LOGIN_BUTTON = By.id("login-button");
    private static final By ERROR = By.cssSelector("[data-test='error']");

    public LoginPage(WebDriver driver) {
        super(driver);
    }

    public LoginPage open() {
        driver.get(ConfigReader.get("ui.baseUrl"));
        return this;
    }

    public ProductsPage loginAs(String username, String password) {
        submit(username, password);
        return new ProductsPage(driver);
    }

    public String loginExpectingFailure(String username, String password) {
        submit(username, password);
        return errorMessage();
    }

    public String errorMessage() {
        return textOf(ERROR);
    }

    private void submit(String username, String password) {
        type(USERNAME, username);
        type(PASSWORD, password);
        click(LOGIN_BUTTON);
    }
}
