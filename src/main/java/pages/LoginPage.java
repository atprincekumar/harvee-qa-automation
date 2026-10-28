package pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.WebDriverWait;
import java.time.Duration;

public class LoginPage {
    private final WebDriver driver;
    private final WebDriverWait wait;
    private final By username = By.id("user-name");
    private final By password = By.id("password");
    private final By loginButton = By.id("login-button");
    private final By error = By.cssSelector("[data-test='error']");

    public LoginPage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(10));
    }

    public void open(String baseUrl) { driver.get(baseUrl); }

    public void login(String user, String pass) {
        wait.until(d -> d.findElement(username).isDisplayed());
        driver.findElement(username).clear();
        driver.findElement(username).sendKeys(user);
        driver.findElement(password).clear();
        driver.findElement(password).sendKeys(pass);
        driver.findElement(loginButton).click();
    }
    public boolean isLoginButtonDisplayed() {
        return wait.until(d -> !d.findElements(loginButton).isEmpty()
                && d.findElement(loginButton).isDisplayed());
    }

    public String errorText() {
        return wait.until(d -> d.findElement(error)).getText();
    }
}
