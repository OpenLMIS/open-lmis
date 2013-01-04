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

    @FindBy(how = How.XPATH, using = " //a[contains(text(),'Done')]")
    private static WebElement doneButton;



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

}