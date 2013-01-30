package org.openlmis.pageobjects;


import com.thoughtworks.selenium.SeleneseTestNgHelper;
import org.openlmis.UiUtils.TestWebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.How;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.pagefactory.AjaxElementLocatorFactory;

import java.io.IOException;


public class InitiateRnRPage extends Page {

    @FindBy(how = How.XPATH, using = "//div[@id='requisition-header']/h2")
    private static WebElement requisitionHeader;

    @FindBy(how = How.XPATH, using = "//div[@id='requisition-header']/div[@class='info-box']/div[@class='row-fluid'][1]/div[1]")
    private static WebElement facilityLabel;

    @FindBy(how = How.XPATH, using = "//input[@value='Save']")
    private static WebElement saveButton;

    @FindBy(how = How.XPATH, using = "//input[@value='Submit']")
    private static WebElement submitButton;

    @FindBy(how = How.XPATH, using = "//input[@value='Authorize']")
    private static WebElement authorizeButton;


    @FindBy(how = How.XPATH, using = "//div[@id='saveSuccessMsgDiv' and @ng-bind='message']")
    private static WebElement successMessage;

    @FindBy(how = How.XPATH, using = "//div[@id='submitSuccessMsgDiv' and @ng-bind='submitMessage']")
    private static WebElement submitSuccessMessage;

    @FindBy(how = How.XPATH, using = "//div[@id='submitFailMessage' and @ng-bind='submitError']")
    private static WebElement submitErrorMessage;

    @FindBy(how = How.ID, using = "A_0")
    private static WebElement beginningBalance;

    @FindBy(how = How.ID, using = "B_0")
    private static WebElement quantityReceived;

    @FindBy(how = How.ID, using = "C_0")
    private static WebElement quantityDispensed;

    @FindBy(how = How.ID, using = "D_0")
    private static WebElement lossesAndAdjustments;

    @FindBy(how = How.ID, using = "E_7")
    private static WebElement stockOnHand;

    @FindBy(how = How.ID, using = "F_0")
    private static WebElement newPatient;

    @FindBy(how = How.XPATH, using = "//table[@class='table table-striped table-bordered']/tbody/tr[1]/td[13]/ng-switch/span/ng-switch/span")
    private static WebElement maximumStockQuantity;

    @FindBy(how = How.XPATH, using = "//table[@class='table table-striped table-bordered']/tbody/tr[1]/td[14]/ng-switch/span/ng-switch/span")
    private static WebElement caculatedOrderQuantity;

    @FindBy(how = How.ID, using = "J_0")
    private static WebElement requestedQuantity;

    @FindBy(how = How.XPATH, using = "//table[@class='table table-striped table-bordered']/tbody/tr[1]/td[11]/ng-switch/span/ng-switch/span")
    private static WebElement adjustedTotalConsumption;

    @FindBy(how = How.XPATH, using = "//table[@class='table table-striped table-bordered']/tbody/tr[1]/td[12]/ng-switch/span/ng-switch/span")
    private static WebElement amc;

    @FindBy(how = How.XPATH, using = "//table[@class='table table-striped table-bordered']/tbody/tr[1]/td[20]/ng-switch/span/ng-switch/span")
    private static WebElement totalCost;

    @FindBy(how = How.XPATH, using = "//table[@class='table table-striped table-bordered']/tbody/tr[1]/td[19]/ng-switch/span/ng-switch/span")
    private static WebElement pricePerPack;

    @FindBy(how = How.XPATH, using = "//table[@class='table table-striped table-bordered']/tbody/tr[1]/td[18]/ng-switch/span/ng-switch/span")
    private static WebElement packsToShip;

    @FindBy(how = How.ID, using = "W_0")
    private static WebElement requestedQuantityExplanation;

    @FindBy(how = How.ID, using = "X_0")
    private static WebElement totalStockOutDays;


    @FindBy(how = How.XPATH, using = "//a[@class='rnr-adjustment']")
    private static WebElement addDescription;

    @FindBy(how = How.XPATH, using = "//div[@class='adjustment-field']/div[@class='row-fluid']/div[@class='span5']/select")
    private static WebElement lossesAndAdjustmentSelect;


    @FindBy(how = How.XPATH, using = "//input[@ng-model='lossAndAdjustment.quantity']")
    private static WebElement quantityAdj;

    @FindBy(how = How.XPATH, using = "//input[@value='Add']")
    private static WebElement addButton;

    @FindBy(how = How.XPATH, using = "//div[@class='adjustment-list']/ul/li/span[@class='tpl-adjustment-type ng-binding']")
    private static WebElement adjList;

    @FindBy(how = How.XPATH, using = "//input[@id='D_6_0']")
    private static WebElement adjListValue;

    @FindBy(how = How.XPATH, using = "//div[@class='adjustment-total clearfix alert alert-warning ng-binding']")
    private static WebElement totalAdj;

    @FindBy(how = How.XPATH, using = "//input[@value='Done']")
    private static WebElement doneButton;

    @FindBy(how = How.XPATH, using = "//span[@class='alert alert-warning reason-request']")
    private static WebElement requestedQtyWarningMessage;


    String successText="R&R saved successfully!";


    public InitiateRnRPage(TestWebDriver driver) throws  IOException {
        super(driver);
        PageFactory.initElements(new AjaxElementLocatorFactory(testWebDriver.getDriver(), 10), this);
        testWebDriver.setImplicitWait(25);
    }

    public void verifyRnRHeader(String FCode, String FName, String FCstring, String program)
    {
       testWebDriver.waitForElementToAppear(requisitionHeader);
        String headerText=testWebDriver.getText(requisitionHeader);
        SeleneseTestNgHelper.assertTrue(headerText.contains("Report and Requisition for "+program));
        String facilityText=testWebDriver.getText(facilityLabel);
        SeleneseTestNgHelper.assertTrue(facilityText.contains(FCode + FCstring + " - " + FName + FCstring));

    }

    public void enterBeginningBalance(String A)
    {
        testWebDriver.sleep(1000);
        testWebDriver.waitForElementToAppear(beginningBalance);
        beginningBalance.sendKeys(A);
        String beginningBalanceValue=testWebDriver.getAttribute(beginningBalance,"value");
        SeleneseTestNgHelper.assertEquals(beginningBalanceValue, A);
    }

    public void enterQuantityReceived(String B)
    {
        testWebDriver.waitForElementToAppear(quantityReceived);
        quantityReceived.sendKeys(B);
        String quantityReceivedValue=testWebDriver.getAttribute(quantityReceived,"value");
        SeleneseTestNgHelper.assertEquals(quantityReceivedValue, B);
    }

    public void enterQuantityDispensed(String C)
    {
        testWebDriver.waitForElementToAppear(quantityDispensed);
        quantityDispensed.sendKeys(C);
        String quantityDispensedValue=testWebDriver.getAttribute(quantityDispensed,"value");
        SeleneseTestNgHelper.assertEquals(quantityDispensedValue, C);
    }

    public void enterLossesAndAdjustments(String adj)
    {
        testWebDriver.waitForElementToAppear(addDescription);
        addDescription.click();
        testWebDriver.waitForElementToAppear(lossesAndAdjustmentSelect);
        testWebDriver.selectByVisibleText(lossesAndAdjustmentSelect,"Transfer In");
        testWebDriver.waitForElementToAppear(quantityAdj);
        quantityAdj.clear();
        quantityAdj.sendKeys(adj);
        addButton.click();
        testWebDriver.waitForElementToAppear(adjList);
        String labelAdj=testWebDriver.getText(adjList);
        SeleneseTestNgHelper.assertEquals(labelAdj.trim(),"Transfer In" );
        String adjValue=testWebDriver.getAttribute(adjListValue,"value");
        SeleneseTestNgHelper.assertEquals(adjValue, adj);
        testWebDriver.waitForElementToAppear(totalAdj);
        String totalAdjValue=testWebDriver.getText(totalAdj);
        SeleneseTestNgHelper.assertEquals(totalAdjValue.substring("Total ".length()), adj);
        doneButton.click();
        testWebDriver.sleep(1000);


    }


    public void calculateAndVerifyStockOnHand(Integer A, Integer B, Integer C, Integer D)
    {
        enterBeginningBalance(A.toString());
        enterQuantityReceived(B.toString());
        enterQuantityDispensed(C.toString());
        enterLossesAndAdjustments(D.toString());
        beginningBalance.click();
        testWebDriver.waitForElementToAppear(stockOnHand);
        Integer StockOnHand = A+B-C+D;
        testWebDriver.sleep(1000);
        String stockOnHandValue= stockOnHand.getText();
        String StockOnHandValue = StockOnHand.toString();
        SeleneseTestNgHelper.assertEquals(stockOnHandValue, StockOnHandValue);
    }

    public void enterAndVerifyRequestedQuantityExplanation(Integer A)
    {
        String expectedWarningMessage="Please enter a reason";
        testWebDriver.waitForElementToAppear(requestedQuantity);
        requestedQuantity.sendKeys(A.toString());
        testWebDriver.waitForElementToAppear(requestedQtyWarningMessage);
        String warningMessage=testWebDriver.getText(requestedQtyWarningMessage);
        SeleneseTestNgHelper.assertEquals(warningMessage.trim(), expectedWarningMessage);
        requestedQuantityExplanation.sendKeys("Due to bad climate");
        testWebDriver.sleep(1000);
    }

    public void enterValuesAndVerifyCalculatedOrderQuantity(Integer F, Integer X,Integer N, Integer P, Integer H, Integer I)
    {
        testWebDriver.waitForElementToAppear(newPatient);
        newPatient.sendKeys(F.toString());
        testWebDriver.waitForElementToAppear(totalStockOutDays);
        totalStockOutDays.sendKeys(X.toString());
        testWebDriver.waitForElementToAppear(adjustedTotalConsumption);
        String actualAdjustedTotalConsumption=testWebDriver.getText(adjustedTotalConsumption);
        SeleneseTestNgHelper.assertEquals(actualAdjustedTotalConsumption,N.toString());
        String actualAmc=testWebDriver.getText(amc);
        SeleneseTestNgHelper.assertEquals(actualAmc.trim(),P.toString());
        String actualMaximumStockQuantity=testWebDriver.getText(maximumStockQuantity);
        SeleneseTestNgHelper.assertEquals(actualMaximumStockQuantity.trim(), H.toString() );
        String actualCalculatedOrderQuantity=testWebDriver.getText(caculatedOrderQuantity);
        SeleneseTestNgHelper.assertEquals(actualCalculatedOrderQuantity.trim(), I.toString());
        testWebDriver.sleep(1000);


    }

    public void verifyPacksToShip(Integer V)
    {
      testWebDriver.waitForElementToAppear(packsToShip);
      String actualPacksToShip=testWebDriver.getText(packsToShip);
      SeleneseTestNgHelper.assertEquals(actualPacksToShip.trim(), V.toString());
      testWebDriver.sleep(500);

    }

    public void calculateAndVerifyTotalCost()
    {
      testWebDriver.waitForElementToAppear(packsToShip);
      String actualPacksToShip=testWebDriver.getText(packsToShip);
      testWebDriver.waitForElementToAppear(pricePerPack);
      String actualPricePerPack=testWebDriver.getText(pricePerPack).substring(1);
      Float actualTotalCost=Float.parseFloat(actualPacksToShip)*Float.parseFloat(actualPricePerPack);
      SeleneseTestNgHelper.assertEquals(actualTotalCost.toString()+"0", totalCost.getText().substring(1));
      testWebDriver.sleep(500);
    }

    public void saveRnR(){
        saveButton.click();
        testWebDriver.sleep(1500);
        String successMessageText=testWebDriver.getText(successMessage);
        testWebDriver.sleep(1500);
        SeleneseTestNgHelper.assertTrue("R&R saved successfully! message not displayed", successMessage.isDisplayed());
    }

    public void submitRnR() {
        submitButton.click();
        testWebDriver.sleep(1500);
    }

    public void authorizeRnR() {
        authorizeButton.click();
        testWebDriver.sleep(1500);
    }



    public void verifySubmitRnrSuccessMsg(){
        SeleneseTestNgHelper.assertTrue("RnR Submit Success message not displayed", submitSuccessMessage.isDisplayed());
    }

    public void verifyAuthorizeRnrSuccessMsg(){
        SeleneseTestNgHelper.assertTrue("RnR authorize Success message not displayed", submitSuccessMessage.isDisplayed());
    }

    public void verifySubmitRnrErrorMsg(){
        SeleneseTestNgHelper.assertTrue("RnR Fail message not displayed", submitErrorMessage.isDisplayed());
    }

    public void verifyBeginningBalanceDisabled(){
        SeleneseTestNgHelper.assertFalse("BB Not disabled", beginningBalance.isEnabled());
    }
}