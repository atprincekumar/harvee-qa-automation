package tests;

import base.TestBase;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.openqa.selenium.By;
import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import pages.CartPage;
import pages.CheckoutPage;
import pages.LoginPage;
import pages.ProductsPage;

public class ShoppingCheckoutTests extends TestBase {

    @Test
    public void TC01_standardUserCanLogIn() {
        LoginPage login = new LoginPage(driver);
        login.open(baseUrl);
        login.login(standardUser, password);
        Assert.assertTrue(new ProductsPage(driver).isDisplayed(), "Products page should open");
    }

    @Test
    public void TC02_lockedOutUserCannotLogIn() {
        LoginPage login = new LoginPage(driver);
        login.open(baseUrl);
        login.login(lockedUser, password);
        Assert.assertTrue(login.errorText().toLowerCase().contains("locked"),
            "Locked-out user should receive a clear error");
    }
    @Test
    public void TC08_invalidPasswordIsRejected() {
        LoginPage login = new LoginPage(driver);
        login.open(baseUrl);
        login.login(standardUser, "incorrect-password");

        Assert.assertTrue(
                login.errorText().toLowerCase().contains("do not match"),
                "An invalid password should display the expected error message"
        );
    }

    @Test
    public void TC03_sortPriceLowToHighChecksWholeList() {
        LoginPage login = new LoginPage(driver);
        login.open(baseUrl);
        login.login(standardUser, password);
        ProductsPage products = new ProductsPage(driver);
        products.sortBy("lohi");
        List<BigDecimal> actual = products.getPrices();
        List<BigDecimal> sorted = new ArrayList<>(actual);
        Collections.sort(sorted);
        Assert.assertEquals(actual, sorted, "Every displayed price must be ascending");
    }

    @Test
    public void TC04_addTwoRemoveOneChecksBadgeAndContents() {
        LoginPage login = new LoginPage(driver);
        login.open(baseUrl);
        login.login(standardUser, password);
        ProductsPage products = new ProductsPage(driver);
        Assert.assertTrue(products.isDisplayed(), "Products should be available");
        List<String> initialNames = products.getNames();
        Assert.assertTrue(initialNames.size() >= 2, "At least two products are needed");
        products.addFirstNItems(2);
        Assert.assertEquals(products.cartBadgeCount(), "2");
        products.removeFirstItem();
        Assert.assertEquals(products.cartBadgeCount(), "1");
        products.openCart();
        CartPage cart = new CartPage(driver);
        Assert.assertEquals(cart.itemCount(), 1);
        Assert.assertEquals(cart.itemNames(), List.of(initialNames.get(1)),
                "Cart should contain the second product after removing the first");
    }

    @Test
    public void TC05_checkoutCalculatesTotalAndEmptiesCart() {
        LoginPage login = new LoginPage(driver);
        login.open(baseUrl);
        login.login(standardUser, password);
        ProductsPage products = new ProductsPage(driver);
        Assert.assertTrue(products.isDisplayed(), "Products should be available");
        List<BigDecimal> displayedPrices = products.getPrices();
        Assert.assertFalse(displayedPrices.isEmpty(), "At least one product price is required");
        BigDecimal expectedProductPrice = displayedPrices.get(0);
        products.addFirstNItems(1);
        products.openCart();
        CartPage cart = new CartPage(driver);
        Assert.assertEquals(cart.itemCount(), 1);
        cart.checkout();

        CheckoutPage checkout = new CheckoutPage(driver);
        checkout.fillCustomer("Prince", "Kumar", "110001");
        checkout.continueCheckout();

        BigDecimal subtotal = money(checkout.subtotalText());
        BigDecimal tax = money(checkout.taxText());
        BigDecimal total = money(checkout.totalText());
        Assert.assertEquals(subtotal.compareTo(expectedProductPrice), 0,
                "Subtotal must equal the price of the product added");
        Assert.assertEquals(total.compareTo(subtotal.add(tax)), 0,
            "Grand total must equal subtotal plus tax");


        checkout.finish();

        Assert.assertTrue(
                checkout.isOrderConfirmationDisplayed(),
                "Order confirmation should be visible"
        );

        driver.get(baseUrl + "/cart.html");
        Assert.assertEquals(
                driver.findElements(By.cssSelector(".cart_item")).size(),
                0,
                "Cart should be empty after completed order"
        );

    }

    @DataProvider(name = "missingCheckoutField")
    public Object[][] missingCheckoutField() {
        return new Object[][] {
            {"first name", "", "Kumar", "110001", "Error: First Name is required"},
            {"last name", "Prince", "", "110001", "Error: Last Name is required"},
            {"postal code", "Prince", "Kumar", "", "Error: Postal Code is required"}
        };
    }


    @Test(dataProvider = "missingCheckoutField")
    public void TC06_checkoutRequiredFieldsAreValidated(
            String missingField, String first, String last, String postal, String expectedError) {

        LoginPage login = new LoginPage(driver);
        login.open(baseUrl);
        login.login(standardUser, password);

        ProductsPage products = new ProductsPage(driver);
        products.addFirstNItems(1);
        products.openCart();

        CartPage cart = new CartPage(driver);
        Assert.assertEquals(
                cart.itemCount(), 1,
                "Cart should contain one product"
        );
        cart.checkout();

        CheckoutPage checkout = new CheckoutPage(driver);
        checkout.fillCustomer(first, last, postal);
        checkout.continueCheckout();

        Assert.assertEquals(
                checkout.errorText().trim(), expectedError,
                "Unexpected validation message for missing " + missingField
        );
    }



    @Test
    public void TC07_loggedOutUserCannotOpenProductsUrl() {
        LoginPage login = new LoginPage(driver);
        login.open(baseUrl);
        login.login(standardUser, password);

        ProductsPage products = new ProductsPage(driver);
        Assert.assertTrue(
                products.isDisplayed(),
                "Products page should be displayed after login"
        );

        products.logout();
        driver.get(baseUrl + "/inventory.html");

        Assert.assertTrue(
                new LoginPage(driver).isLoginButtonDisplayed(),
                "Logged-out user should be redirected to login"
        );
    }


    private BigDecimal money(String label) {
        String numeric = label.replaceAll("[^0-9.]", "");
        return new BigDecimal(numeric);
    }
}
