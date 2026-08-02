package com.saucedemo.tests;

import com.saucedemo.base.BaseTest;
import com.saucedemo.constants.FrameworkConstants;
import com.saucedemo.pages.CartPage;
import com.saucedemo.pages.CheckoutCompletePage;
import com.saucedemo.pages.CheckoutOverviewPage;
import com.saucedemo.pages.CheckoutPage;
import com.saucedemo.pages.InventoryPage;
import com.saucedemo.pages.LoginPage;
import com.saucedemo.utils.LoggerUtility;
import com.saucedemo.utils.RandomDataGenerator;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

/**
 * CheckoutTest contains test scenarios covering complete end-to-end purchasing,
 * order confirmation headers, dispatch messages, and price summary mathematics.
 */
public class CheckoutTest extends BaseTest {

    private InventoryPage inventoryPage;

    @BeforeMethod(alwaysRun = true)
    public void loginToInventory() {
        LoginPage loginPage = new LoginPage();
        inventoryPage = loginPage.login(FrameworkConstants.STANDARD_USER, FrameworkConstants.STANDARD_PASSWORD);
    }

    @Test(description = "Verify complete e2e purchase workflow from cart to complete screen",
            groups = {FrameworkConstants.GROUP_SMOKE, FrameworkConstants.GROUP_SANITY, FrameworkConstants.GROUP_REGRESSION})
    public void testCompletePurchase() {
        LoggerUtility.info("Executing testCompletePurchase...");
        String item = "Sauce Labs Backpack";

        inventoryPage.addItemToCartByName(item);
        inventoryPage.getHeaderComponent().clickShoppingCart();

        CartPage cartPage = new CartPage();
        CheckoutPage checkoutPage = cartPage.clickCheckout();

        checkoutPage.fillCheckoutInformation(RandomDataGenerator.randomFirstName(), RandomDataGenerator.randomLastName(), RandomDataGenerator.randomPostalCode());
        CheckoutOverviewPage overviewPage = checkoutPage.clickContinue();

        Assert.assertEquals(overviewPage.getPageTitleText(), FrameworkConstants.OVERVIEW_PAGE_TITLE, "Overview page title mismatch.");

        CheckoutCompletePage completePage = overviewPage.clickFinish();
        Assert.assertTrue(completePage.isOrderCompleteHeaderDisplayed(), "Order complete header was not displayed.");
        Assert.assertEquals(completePage.getCompleteHeaderMessage(), FrameworkConstants.COMPLETE_HEADER_TEXT, "Complete header message text mismatch.");
    }

    @Test(description = "Verify order confirmation header and text on completion screen",
            groups = {FrameworkConstants.GROUP_SANITY, FrameworkConstants.GROUP_REGRESSION})
    public void testVerifyOrderConfirmation() {
        LoggerUtility.info("Executing testVerifyOrderConfirmation...");
        inventoryPage.addItemToCartByName("Sauce Labs Fleece Jacket");
        inventoryPage.getHeaderComponent().clickShoppingCart();

        CartPage cartPage = new CartPage();
        CheckoutPage checkoutPage = cartPage.clickCheckout();

        checkoutPage.fillCheckoutInformation(RandomDataGenerator.randomFirstName(), RandomDataGenerator.randomLastName(), RandomDataGenerator.randomPostalCode());
        CheckoutOverviewPage overviewPage = checkoutPage.clickContinue();
        CheckoutCompletePage completePage = overviewPage.clickFinish();

        String expectedHeader = FrameworkConstants.COMPLETE_HEADER_TEXT;
        String expectedText = "Your order has been dispatched, and will arrive just as fast as the pony can get there!";

        Assert.assertEquals(completePage.getCompleteHeaderMessage(), expectedHeader, "Confirmation header mismatch.");
        Assert.assertEquals(completePage.getCompleteText(), expectedText, "Dispatch message text mismatch.");
    }

    @Test(description = "Verify checkout summary subtotal, tax calculation, and final total math",
            groups = {FrameworkConstants.GROUP_REGRESSION})
    public void testVerifyCheckoutSummary() {
        LoggerUtility.info("Executing testVerifyCheckoutSummary...");
        String item1 = "Sauce Labs Backpack"; // $29.99
        String item2 = "Sauce Labs Bike Light"; // $9.99

        double price1 = inventoryPage.getItemPriceByName(item1);
        double price2 = inventoryPage.getItemPriceByName(item2);
        double expectedSubtotal = price1 + price2;

        inventoryPage.addItemToCartByName(item1)
                     .addItemToCartByName(item2);
        inventoryPage.getHeaderComponent().clickShoppingCart();

        CartPage cartPage = new CartPage();
        CheckoutPage checkoutPage = cartPage.clickCheckout();
        checkoutPage.fillCheckoutInformation(RandomDataGenerator.randomFirstName(), RandomDataGenerator.randomLastName(), RandomDataGenerator.randomPostalCode());

        CheckoutOverviewPage overviewPage = checkoutPage.clickContinue();
        
        double actualSubtotal = overviewPage.getSubtotal();
        double actualTax = overviewPage.getTax();
        double actualTotal = overviewPage.getTotal();
        
        Assert.assertEquals(actualSubtotal, expectedSubtotal, 0.01, "Calculated subtotal mismatch.");
        
        double expectedCalculatedTotal = Math.round((actualSubtotal + actualTax) * 100.0) / 100.0;
        Assert.assertEquals(actualTotal, expectedCalculatedTotal, 0.01, "Subtotal + Tax does not equal Final Total.");
    }
}
