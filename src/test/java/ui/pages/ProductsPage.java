package ui.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.util.List;
import java.util.stream.Collectors;

/**
 * SauceDemo products (inventory) page shown after a successful login.
 */
public class ProductsPage extends BasePage {

    private static final By TITLE = By.className("title");
    private static final By PRODUCT_NAME = By.className("inventory_item_name");

    public ProductsPage(WebDriver driver) {
        super(driver);
    }

    public boolean isLoaded() {
        return isDisplayed(TITLE);
    }

    public String title() {
        return textOf(TITLE);
    }

    /** Collections + streams demo: all product names currently rendered. */
    public List<String> productNames() {
        return driver.findElements(PRODUCT_NAME).stream()
                .map(WebElement::getText)
                .collect(Collectors.toList());
    }
}
