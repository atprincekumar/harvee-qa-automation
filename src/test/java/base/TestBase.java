package base;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.time.Duration;
import java.time.Instant;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.UUID;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.testng.ITestResult;
import org.testng.Reporter;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;

public class TestBase {
    protected WebDriver driver;
    protected final String baseUrl = System.getenv().getOrDefault("BASE_URL", "https://www.saucedemo.com");
    protected final String standardUser = System.getenv().getOrDefault("STANDARD_USER", "standard_user");
    protected final String lockedUser = System.getenv().getOrDefault("LOCKED_USER", "locked_out_user");
    protected final String password = System.getenv().getOrDefault("PASSWORD", "secret_sauce");

    @BeforeMethod
    public void setUp() {
        ChromeOptions options = new ChromeOptions();
        if (Boolean.parseBoolean(System.getenv().getOrDefault("HEADLESS", "false"))) {
            options.addArguments("--headless=new");
        }
        driver = new ChromeDriver(options);
        driver.manage().timeouts().implicitlyWait(Duration.ZERO);
        driver.manage().window().maximize();
    }

    @AfterMethod(alwaysRun = true)
    public void tearDown(ITestResult result) {
        try {
            if (driver != null && !result.isSuccess()) {
                Path outputDir = Path.of("target", "failure-screenshots");
                String methodName = sanitizeFilenamePart(result.getMethod().getMethodName());
                String parameters = Arrays.stream(result.getParameters())
                        .map(parameter -> sanitizeFilenamePart(String.valueOf(parameter)))
                        .reduce((left, right) -> left + "_" + right)
                        .orElse("no-parameters");
                String timestamp = DateTimeFormatter.ofPattern("yyyyMMdd'T'HHmmss.SSSX")
                        .withZone(ZoneOffset.UTC)
                        .format(Instant.now());
                Path intendedPath = outputDir.resolve(methodName + "_" + parameters + "_"
                        + timestamp + "_" + UUID.randomUUID() + ".png").toAbsolutePath();

                try {
                    Files.createDirectories(outputDir);
                    byte[] screenshot = ((TakesScreenshot) driver).getScreenshotAs(OutputType.BYTES);
                    if (screenshot.length == 0) {
                        throw new IOException("WebDriver returned an empty screenshot");
                    }
                    Files.write(intendedPath, screenshot,
                            StandardOpenOption.CREATE_NEW, StandardOpenOption.WRITE);
                    if (Files.size(intendedPath) != screenshot.length) {
                        throw new IOException("Saved screenshot size does not match captured data");
                    }
                    Reporter.log("Failure screenshot saved: " + intendedPath, true);
                } catch (Exception e) {
                    String message = "Failed to capture or save failure screenshot; intended path: "
                            + intendedPath;
                    System.err.println(message);
                    e.printStackTrace(System.err);
                    Reporter.log(message + "; cause: " + e, true);
                }
            }
        } finally {
            if (driver != null) driver.quit();
        }
    }

    private static String sanitizeFilenamePart(String value) {
        if (value == null || value.isBlank()) {
            return "blank";
        }
        String sanitized = value.replaceAll("[^A-Za-z0-9._-]", "_");
        return sanitized.length() > 48 ? sanitized.substring(0, 48) : sanitized;
    }
}
