package org.openlmis.pageobjects;


import com.thoughtworks.selenium.SeleneseTestNgHelper;
import org.openlmis.UiUtils.TestWebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.How;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.pagefactory.AjaxElementLocatorFactory;

import java.io.IOException;
import java.math.BigDecimal;

import static java.math.BigDecimal.ROUND_HALF_UP;


public class OrderPage extends Page {

    @FindBy(how = How.ID, using = "NoRequisitionsPendingMessage")
    private static WebElement NoRequisitionsPendingMessage;

    @FindBy(how = How.XPATH, using = "//div[@class='ngViewport ng-scope']/div/div/div[@class='ngCell col0 ']/div/span")
    private static WebElement programOnOrderScreen;

    @FindBy(how = How.XPATH, using = "//div[@class='ngViewport ng-scope']/div/div/div[@class='ngCell col1 ']/div/span")
    private static WebElement facilityCodeOnOrderScreen;

    @FindBy(how = How.XPATH, using = "//div[@class='ngViewport ng-scope']/div/div/div[@class='ngCell col2 ']/div/span")
    private static WebElement facilityNameOnOrderScreen;

    @FindBy(how = How.XPATH, using = "//div[@class='ngViewport ng-scope']/div/div/div[@class='ngCell col3 ']/div/span")
    private static WebElement periodStartDateOnOrderScreen;

    @FindBy(how = How.XPATH, using = "//div[@class='ngViewport ng-scope']/div/div/div[@class='ngCell col4 ']/div/span")
    private static WebElement periodEndDateOnOrderScreen;

    @FindBy(how = How.XPATH, using = "//div[@class='ngViewport ng-scope']/div/div/div[@class='ngCell col7 ']/div/span")
    private static WebElement supplyDepotOnOrderScreen;





    public OrderPage(TestWebDriver driver) throws  IOException {
        super(driver);
        PageFactory.initElements(new AjaxElementLocatorFactory(testWebDriver.getDriver(), 10), this);
        testWebDriver.setImplicitWait(25);

    }


    public void verifyOrderListElements(String program, String facilityCode, String facilityName, String periodStartDate, String periodEndDate, String supplyFacilityName ) throws IOException {
        testWebDriver.waitForElementToAppear(programOnOrderScreen);
        SeleneseTestNgHelper.assertEquals(programOnOrderScreen.getText().trim(), program);
        SeleneseTestNgHelper.assertEquals(facilityCodeOnOrderScreen.getText().trim(), facilityCode);
        SeleneseTestNgHelper.assertEquals(facilityNameOnOrderScreen.getText().trim(), facilityName);
        SeleneseTestNgHelper.assertEquals(periodStartDateOnOrderScreen.getText().trim(), periodStartDate);
        SeleneseTestNgHelper.assertEquals(periodEndDateOnOrderScreen.getText().trim(), periodEndDate);
        SeleneseTestNgHelper.assertEquals(supplyDepotOnOrderScreen.getText().trim(), supplyFacilityName);

    }


}