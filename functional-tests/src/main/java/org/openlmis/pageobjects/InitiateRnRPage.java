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

    @FindBy(how = How.XPATH, using = "//div[@id='requisition-header']/div/table/tbody/tr/td")
    private static WebElement facilityLabel;

    @FindBy(how = How.XPATH, using = "//input[@value='Save']")
    private static WebElement saveButton;

    @FindBy(how = How.XPATH, using = "//div[@id='saveSuccessMsgDiv' and @ng-model='message']")
    private static WebElement successMessage;

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

    @FindBy(how = How.ID, using = "H_12")
    private static WebElement maximumStockQuantity;

    @FindBy(how = How.ID, using = "I_13")
    private static WebElement caculatedOrderQuantity;

    @FindBy(how = How.ID, using = "J_0")
    private static WebElement requestedQuantity;

    @FindBy(how = How.ID, using = "N_10")
    private static WebElement adjustedTotalConsumption;

    @FindBy(how = How.ID, using = "P_11")
    private static WebElement amc;

    @FindBy(how = How.ID, using = "Q_19")
    private static WebElement totalCost;

    @FindBy(how = How.ID, using = "T_18")
    private static WebElement pricePerPack;

    @FindBy(how = How.ID, using = "V_17")
    private static WebElement packsToShip;

    @FindBy(how = How.ID, using = "W_0")
    private static WebElement requestedQuantityExplanation;

    @FindBy(how = How.ID, using = "X_0")
    private static WebElement totalStockOutDays;


    @FindBy(how = How.XPATH, using = "//a[@class='rnr-adjustment']/span[@class='add-adjustment']")
    private static WebElement addDescription;

    @FindBy(how = How.XPATH, using = "//div[@id='lossesAndAdjustments']/div[@class='modal-body']/div[@class='adjustment-field']/select")
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

    @FindBy(how = How.XPATH, using = "//a[contains(text(),'Done')]")
    private static WebElement doneButton;

    @FindBy(how = How.XPATH, using = "//span[@class='alert alert-warning reason-request']")
    private static WebElement requestedQtyWarningMessage;


    String successText="R&R saved successfully!";


    public InitiateRnRPage(TestWebDriver driver) throws  IOException {
        super(driver);
        PageFactory.initElements(new AjaxElementLocatorFactory(testWebDriver.getDriver(), 10), this);
        testWebDriver.setImplicitWait(25);
    }

    public void verifyRnRHeader(String FCstring, String program)
    {
       testWebDriver.waitForElementToAppear(requisitionHeader);
        String headerText=testWebDriver.getText(requisitionHeader);
        SeleneseTestNgHelper.assertTrue(headerText.contains("Report and Requisition for "+program));
        String facilityText=testWebDriver.getText(facilityLabel);
        SeleneseTestNgHelper.assertTrue(facilityText.contains("FCcode" + FCstring + " - FCname" + FCstring));

    }

    public void saveRnR(){
        saveButton.click();
        testWebDriver.sleep(1500);
        String successMessageText=testWebDriver.getText(successMessage);
        testWebDriver.sleep(1500);
        SeleneseTestNgHelper.assertEquals(successMessageText.trim(),successText);
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
      String actualPricePerPack=testWebDriver.getText(pricePerPack);
      Integer actualTotalCost=Integer.parseInt(actualPacksToShip)*Integer.parseInt(actualPricePerPack);
      SeleneseTestNgHelper.assertEquals(actualTotalCost.toString(), totalCost.getText());
      testWebDriver.sleep(500);
    }



}