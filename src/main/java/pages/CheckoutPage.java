package pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.WebDriverWait;
import java.time.Duration;

public class CheckoutPage {
    private final WebDriver driver;
    private final WebDriverWait wait;
    private final By firstName = By.id("first-name");
    private final By lastName = By.id("last-name");
    private final By postalCode = By.id("postal-code");
    public CheckoutPage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(10));
    }
    public void fillCustomer(String first, String last, String postal) {
        if (first != null) wait.until(org.openqa.selenium.support.ui.ExpectedConditions
                .visibilityOfElementLocated(firstName)).sendKeys(first);
        if (last != null) wait.until(org.openqa.selenium.support.ui.ExpectedConditions
                .visibilityOfElementLocated(lastName)).sendKeys(last);
        if (postal != null) wait.until(org.openqa.selenium.support.ui.ExpectedConditions
                .visibilityOfElementLocated(postalCode)).sendKeys(postal);
    }
    public void continueCheckout() {
        org.openqa.selenium.WebElement continueButton = wait.until(
                org.openqa.selenium.support.ui.ExpectedConditions
                        .elementToBeClickable(By.id("continue")));
        ((org.openqa.selenium.JavascriptExecutor) driver)
                .executeScript("arguments[0].click()", continueButton);
    }
    public String errorText() {
        return wait.until(d -> d.findElement(By.cssSelector("[data-test='error']"))).getText();
    }
    public String subtotalText() { return driver.findElement(By.cssSelector(".summary_subtotal_label")).getText(); }
    public String taxText() { return driver.findElement(By.cssSelector(".summary_tax_label")).getText(); }
    public String totalText() { return driver.findElement(By.cssSelector(".summary_total_label")).getText(); }
    public void finish() { driver.findElement(By.id("finish")).click(); }
    public boolean isOrderConfirmationDisplayed() {
        return wait.until(
                d -> d.findElement(
                        By.cssSelector(".complete-header")
                ).isDisplayed()
        );
    }
}
