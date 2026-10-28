package pages;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.openqa.selenium.support.ui.ExpectedConditions;
import java.time.Duration;

public class ProductsPage {
    private final WebDriver driver;
    private final WebDriverWait wait;
    private final By inventoryItems = By.cssSelector(".inventory_item");
    private final By sortDropdown = By.cssSelector(".product_sort_container");
    private final By cartBadge = By.cssSelector(".shopping_cart_badge");
    private final By cartLink = By.cssSelector(".shopping_cart_link");

    public ProductsPage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(10));
    }

    public boolean isDisplayed() {
        return wait.until(d -> !d.findElements(inventoryItems).isEmpty());
    }

    public void sortBy(String optionValue) {
        new Select(wait.until(d -> d.findElement(sortDropdown))).selectByValue(optionValue);
    }

    public List<BigDecimal> getPrices() {
        List<BigDecimal> prices = new ArrayList<>();
        for (WebElement item : driver.findElements(inventoryItems)) {
            String raw = item.findElement(By.cssSelector(".inventory_item_price")).getText();
            prices.add(new BigDecimal(raw.replace("$", "").trim()));
        }
        return prices;
    }

    public List<String> getNames() {
        List<String> names = new ArrayList<>();
        for (WebElement item : driver.findElements(inventoryItems)) {
            names.add(item.findElement(By.cssSelector(".inventory_item_name")).getText());
        }
        return names;
    }

    public void addFirstNItems(int count) {
        wait.until(d -> d.findElements(inventoryItems).size() >= count);
        for (int i = 0; i < count; i++) {
            int itemIndex = i;
            WebElement addButton = wait.until(d -> {
                List<WebElement> currentItems = d.findElements(inventoryItems);
                if (currentItems.size() <= itemIndex) return null;
                WebElement button = currentItems.get(itemIndex).findElement(By.cssSelector("button"));
                return button.isDisplayed() && button.isEnabled() ? button : null;
            });
            ((JavascriptExecutor) driver).executeScript("arguments[0].click()", addButton);
            wait.until(d -> {
                List<WebElement> currentItems = d.findElements(inventoryItems);
                return currentItems.size() > itemIndex
                        && "Remove".equals(currentItems.get(itemIndex)
                                .findElement(By.cssSelector("button")).getText());
            });
        }
    }

    public void removeFirstItem() {
        WebElement removeButton = wait.until(d -> {
            List<WebElement> items = d.findElements(inventoryItems);
            if (items.isEmpty()) return null;
            WebElement button = items.get(0).findElement(By.cssSelector("button"));
            return button.isDisplayed() && button.isEnabled()
                    && "Remove".equals(button.getText()) ? button : null;
        });
        ((JavascriptExecutor) driver).executeScript("arguments[0].click()", removeButton);
        wait.until(d -> {
            List<WebElement> items = d.findElements(inventoryItems);
            return !items.isEmpty()
                    && "Add to cart".equals(items.get(0)
                            .findElement(By.cssSelector("button")).getText());
        });
    }

    public String cartBadgeCount() {
        return driver.findElements(cartBadge).isEmpty() ? "0" : driver.findElement(cartBadge).getText();
    }


        public void openCart() {
            WebElement link = wait.until(ExpectedConditions.elementToBeClickable(cartLink));
            ((JavascriptExecutor) driver).executeScript("arguments[0].click()", link);
            wait.until(d -> d.getCurrentUrl().contains("cart.html"));
        }

        public void logout() {
            WebElement menuButton = wait.until(ExpectedConditions
                    .elementToBeClickable(By.id("react-burger-menu-btn")));
            ((JavascriptExecutor) driver).executeScript("arguments[0].click()", menuButton);

            WebElement logoutLink = wait.until(ExpectedConditions
                    .visibilityOfElementLocated(By.id("logout_sidebar_link")));

            ((JavascriptExecutor) driver).executeScript("arguments[0].click()", logoutLink);
            wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("login-button")));
        }
    }

