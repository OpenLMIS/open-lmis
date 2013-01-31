package org.openlmis.pageobjects;


import com.thoughtworks.selenium.SeleneseTestNgHelper;
import org.openlmis.UiUtils.TestWebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.How;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.pagefactory.AjaxElementLocatorFactory;

import java.io.IOException;


public class ApprovePage extends Page {

    @FindBy(how = How.ID, using = "NoRequisitionsPendingMessage")
    private static WebElement NoRequisitionsPendingMessage;

    @FindBy(how = How.XPATH, using = "//div[@class='form-group']/h3")
    private static WebElement requisitionListHeader;

    @FindBy(how = How.XPATH, using = "//div[@class='ngCellText colt0']")
    private static WebElement firstRow;

    @FindBy(how = How.XPATH, using = "//div[@id='requisition-header']/h2")
    private static WebElement requisitionHeader;

    @FindBy(how = How.XPATH, using = "//div[@id='requisition-header']/div[@class='info-box']/div[@class='row-fluid'][1]/div[1]")
    private static WebElement facilityLabel;

    @FindBy(how = How.XPATH, using = "//div[@class='ngCellText colt13']/span")
    private static WebElement calculateOrderQuantity;

    @FindBy(how = How.XPATH, using = "//div[@class='ngCellText colt17']/span")
    private static WebElement packsToShip;

    @FindBy(how = How.XPATH, using = "//span[@ng-bind='row.entity.price']")
    private static WebElement pricePerPack;

    @FindBy(how = How.XPATH, using = "//span[@ng-bind='row.entity.cost']")
    private static WebElement totalCost;

    @FindBy(how = How.XPATH, using = "//span[@id='totalCost']")
    private static WebElement overalltotalCost;

    @FindBy(how = How.NAME, using = "quantityApproved")
    private static WebElement quantityApproved;

    @FindBy(how = How.NAME, using = "remarks")
    private static WebElement remarks;

    @FindBy(how = How.XPATH, using = "//input[@value='Approve']")
    private static WebElement approveButton;

    @FindBy(how = How.XPATH, using = "//input[@value='Save']")
    private static WebElement saveButton;

    @FindBy(how = How.ID, using = "saveSuccessMsgDiv")
    private static WebElement saveSuccessMsgDiv;

    public ApprovePage(TestWebDriver driver) throws  IOException {
        super(driver);
        PageFactory.initElements(new AjaxElementLocatorFactory(testWebDriver.getDriver(), 10), this);
        testWebDriver.setImplicitWait(25);

    }

    public void verifyNoRequisitionPendingMessage()
    {
        testWebDriver.waitForElementToAppear(requisitionListHeader);
        SeleneseTestNgHelper.assertTrue("NoRequisitionsPendingMessage is not displayed",NoRequisitionsPendingMessage.isDisplayed());
    }

    public void verifyandclickRequisitionPresentForApproval()
    {
        testWebDriver.waitForElementToAppear(requisitionListHeader);
        SeleneseTestNgHelper.assertTrue("No row of requisition is there for approval",firstRow.isDisplayed());
        firstRow.click();
    }

    public void verifyRnRHeader(String FCode, String FName, String FCstring, String program)
    {
        testWebDriver.waitForElementToAppear(requisitionHeader);
        String headerText=testWebDriver.getText(requisitionHeader);
        //SeleneseTestNgHelper.assertTrue(headerText.contains("Report and Requisition for "+program));
        String facilityText=testWebDriver.getText(facilityLabel);
        SeleneseTestNgHelper.assertTrue(facilityText.contains(FCode + FCstring + " - " + FName + FCstring));

    }

    public void verifyApprovedQuantity()
    {
        testWebDriver.waitForElementToAppear(quantityApproved);
        String actualCalculatedOrderQuantity=calculateOrderQuantity.getText();
        String actualApproveQuantity=testWebDriver.getAttribute(quantityApproved,"value");
        SeleneseTestNgHelper.assertEquals(actualApproveQuantity, actualCalculatedOrderQuantity);
    }

    public void verifyApprovedQuantityApprovedFromLowerHierarchy(String approvedQuantity)
    {
        testWebDriver.waitForElementToAppear(quantityApproved);
        String actualApproveQuantity=testWebDriver.getAttribute(quantityApproved,"value");
        SeleneseTestNgHelper.assertEquals(actualApproveQuantity, approvedQuantity);
    }

    public void editApproveQuantityAndVerifyTotalCost(String approvedQuantity)
    {
        testWebDriver.waitForElementToAppear(quantityApproved);
        quantityApproved.clear();
        quantityApproved.sendKeys(approvedQuantity);
        remarks.click();
        SeleneseTestNgHelper.assertEquals(packsToShip.getText().trim(),Integer.parseInt(approvedQuantity)/10);
        SeleneseTestNgHelper.assertEquals(String.valueOf(Float.parseFloat(packsToShip.getText().trim())*Float.parseFloat(pricePerPack.getText().trim())),totalCost.getText().trim()+".0");
        SeleneseTestNgHelper.assertEquals(overalltotalCost.getText().trim(),totalCost.getText().trim());
    }

   public void approveRequisition()
   {
       testWebDriver.waitForElementToAppear(approveButton);
       testWebDriver.waitForElementToAppear(saveButton);
       saveButton.click();
       testWebDriver.waitForElementToAppear(saveSuccessMsgDiv);
       SeleneseTestNgHelper.assertTrue("R&R saved successfully message not displayed", saveSuccessMsgDiv.isDisplayed());
       approveButton.click();
       testWebDriver.waitForElementToAppear(saveSuccessMsgDiv);
       SeleneseTestNgHelper.assertTrue("R&R approved successfully! message not displayed", saveSuccessMsgDiv.isDisplayed());

   }




}