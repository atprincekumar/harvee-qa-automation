package pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.WebDriverWait;
import java.time.Duration;
import java.util.List;
import org.openqa.selenium.WebElement;


public class CartPage {
    private final WebDriver driver;
    private final WebDriverWait wait;
    public CartPage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        wait.until(d -> d.getCurrentUrl().contains("cart.html"));
    }
    public int itemCount() {
        wait.until(d -> !d.findElements(By.cssSelector(".cart_item")).isEmpty());
        return driver.findElements(By.cssSelector(".cart_item")).size();
    }

    public List<String> itemNames() {
        wait.until(d -> !d.findElements(By.cssSelector(".cart_item")).isEmpty());
        return driver.findElements(By.cssSelector(".cart_item .inventory_item_name"))
                .stream()
                .map(WebElement::getText)
                .toList();
    }

    public void removeFirstItem() {
        wait.until(d ->
                !d.findElements(By.cssSelector(".cart_item")).isEmpty()
        );

        List<WebElement> removeButtons =
                driver.findElements(By.cssSelector(".cart_item button"));

        if (removeButtons.isEmpty()) {
            throw new IllegalStateException(
                    "Cannot remove an item: no remove button was found in the cart."
            );
        }

        removeButtons.get(0).click();
    }

    public void checkout() {
        WebElement checkoutButton = wait.until(
                org.openqa.selenium.support.ui.ExpectedConditions
                        .elementToBeClickable(By.id("checkout"))
        );

        ((org.openqa.selenium.JavascriptExecutor) driver)
                .executeScript("arguments[0].click()", checkoutButton);

        wait.until(org.openqa.selenium.support.ui.ExpectedConditions
                .visibilityOfElementLocated(By.id("first-name")));
    }
}
