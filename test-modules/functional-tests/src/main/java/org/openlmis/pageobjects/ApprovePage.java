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


public class ApprovePage extends Page {

  @FindBy(how = How.ID, using = "NoRequisitionsPendingMessage")
  private static WebElement NoRequisitionsPendingMessage;

  @FindBy(how = How.XPATH, using = "//div[@class='form-group']/h3")
  private static WebElement requisitionListHeader;

  @FindBy(how = How.XPATH, using = "//div[@class='ngCellText colt0']")
  private static WebElement firstRow;

  @FindBy(how = How.XPATH, using = "//div[@class='ngCellText colt3']/span")
  private static WebElement periodStartDate;

  @FindBy(how = How.XPATH, using = "//div[@class='ngCellText colt4']/span")
  private static WebElement periodEndDate;

  @FindBy(how = How.XPATH, using = "//div[@id='requisition-header']/h2")
  private static WebElement requisitionHeader;

  @FindBy(how = How.XPATH, using = "//div[@id='requisition-header']/div[@class='info-box']/div[@class='row-fluid'][1]/div[1]")
  private static WebElement facilityLabel;

  @FindBy(how = How.XPATH, using = "//div[@class='ngCellText colt13']/span")
  private static WebElement calculateOrderQuantity;

  @FindBy(how = How.XPATH, using = "//div[@ng-grid='nonFullSupplyGrid']/div[@class='ngViewport ng-scope']/div/div/div[15]/div/span")
  private static WebElement requestedOrderQuantityNonFullSupply;

  @FindBy(how = How.XPATH, using = "//div[@ng-grid='nonFullSupplyGrid']/div[@class='ngViewport ng-scope']/div/div/div[18]/div/span")
  private static WebElement packsToShipNonFullSupply;

  @FindBy(how = How.XPATH, using = "//div[@ng-grid='nonFullSupplyGrid']/div[@class='ngViewport ng-scope']/div/div/div[20]/div/span[@ng-bind='row.entity.cost']")
  private static WebElement totalCostNonFullSupply;

  @FindBy(how = How.XPATH, using = "//div[@ng-grid='nonFullSupplyGrid']/div[@class='ngViewport ng-scope']/div/div/div[19]/div/span[@ng-bind='row.entity.price']")
  private static WebElement pricePerPackNonFullSupply;

  @FindBy(how = How.XPATH, using = "//div[@ng-grid='nonFullSupplyGrid']/div[@class='ngViewport ng-scope']/div/div/div[17]/div/ng-form/input")
  private static WebElement approvedQuantityNonFullSupply;

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


  @FindBy(how = How.ID, using = "nonFullSupplyTab")
  private static WebElement nonFullSupplyTab;


  @FindBy(how = How.ID, using = "fullSupplyTab")
  private static WebElement fullSupplyTab;

  @FindBy(how = How.NAME, using = "remarks")
  private static WebElement remarks;

  @FindBy(how = How.XPATH, using = "//input[@value='Approve']")
  private static WebElement approveButton;

  @FindBy(how = How.XPATH, using = "//input[@value='Save']")
  private static WebElement saveButton;

  @FindBy(how = How.ID, using = "saveSuccessMsgDiv")
  private static WebElement saveSuccessMsgDiv;

  @FindBy(how = How.XPATH, using = "//div[@class='info-box']/div[2]/div[3]")
  private static WebElement reportingPeriodInitRnRScreen;

  @FindBy(how = How.XPATH, using = "//div[contains(text(),'R&R approved successfully!')]")
  private static WebElement approvedSuccessMessage;


  public ApprovePage(TestWebDriver driver) throws IOException {
    super(driver);
    PageFactory.initElements(new AjaxElementLocatorFactory(testWebDriver.getDriver(), 10), this);
    testWebDriver.setImplicitWait(25);

  }

  public void verifyNoRequisitionPendingMessage() {
    testWebDriver.waitForElementToAppear(requisitionListHeader);
    SeleneseTestNgHelper.assertTrue("NoRequisitionsPendingMessage is not displayed", NoRequisitionsPendingMessage.isDisplayed());
  }


  public String verifyandclickRequisitionPresentForApproval() {

    String period = null;
    testWebDriver.waitForElementToAppear(requisitionListHeader);
    SeleneseTestNgHelper.assertTrue("No row of requisition is there for approval", firstRow.isDisplayed());
    period = periodStartDate.getText().trim() + " - " + periodEndDate.getText().trim();
    firstRow.click();
    return period;
  }

  public void verifyRnRHeader(String FCode, String FName, String FCstring, String program, String periodDetails) {
    testWebDriver.sleep(1000);
    testWebDriver.waitForElementToAppear(requisitionHeader);
    testWebDriver.waitForElementToAppear(facilityLabel);
    testWebDriver.waitForElementToAppear(reportingPeriodInitRnRScreen);
    String headerText = testWebDriver.getText(requisitionHeader);
    SeleneseTestNgHelper.assertTrue(headerText.contains("Report and Requisition for " + program));
    String facilityText = testWebDriver.getText(facilityLabel);
    SeleneseTestNgHelper.assertTrue(facilityText.contains(FCode + FCstring + " - " + FName + FCstring));

    SeleneseTestNgHelper.assertEquals(reportingPeriodInitRnRScreen.getText().trim().substring("Reporting Period: ".length()), periodDetails.trim());

  }

  public void verifyApprovedQuantity() {
    testWebDriver.waitForElementToAppear(fullSupplyTab);
    testWebDriver.waitForElementToAppear(nonFullSupplyTab);
    testWebDriver.waitForElementToAppear(quantityApproved);
    testWebDriver.sleep(1500);
    testWebDriver.waitForElementToAppear(calculateOrderQuantity);
    String actualCalculatedOrderQuantity = calculateOrderQuantity.getText();
    String actualApproveQuantity = testWebDriver.getAttribute(quantityApproved, "value");
    SeleneseTestNgHelper.assertEquals(actualApproveQuantity, actualCalculatedOrderQuantity);
    nonFullSupplyTab.click();

    testWebDriver.waitForElementToAppear(approvedQuantityNonFullSupply);
    String actualRequestedOrderQuantity = requestedOrderQuantityNonFullSupply.getText();
    String actualApproveQuantityNonFullSupply = testWebDriver.getAttribute(approvedQuantityNonFullSupply, "value");
    SeleneseTestNgHelper.assertEquals(actualApproveQuantityNonFullSupply, actualRequestedOrderQuantity);

  }

  public void verifyApprovedQuantityApprovedFromLowerHierarchy(String approvedQuantity) {
    testWebDriver.waitForElementToAppear(quantityApproved);
    String actualApproveQuantity = testWebDriver.getAttribute(quantityApproved, "value");
    SeleneseTestNgHelper.assertEquals(actualApproveQuantity, approvedQuantity);
  }

  public void editApproveQuantityAndVerifyTotalCost(String approvedQuantity) {
    testWebDriver.waitForElementToAppear(fullSupplyTab);
    fullSupplyTab.click();
    testWebDriver.sleep(1000);
    testWebDriver.waitForElementToAppear(quantityApproved);
    quantityApproved.clear();
    quantityApproved.sendKeys(approvedQuantity);
    remarks.click();
    SeleneseTestNgHelper.assertEquals(packsToShip.getText().trim(), Integer.parseInt(approvedQuantity) / 10);

    BigDecimal cost = new BigDecimal((Float.parseFloat(packsToShip.getText().trim()) * Float.parseFloat(pricePerPack.getText().trim()))).setScale(2, ROUND_HALF_UP);
    SeleneseTestNgHelper.assertEquals(String.valueOf(cost), totalCost.getText().trim());
    String totalCostFullSupplyLineItem=totalCost.getText().trim();
    testWebDriver.sleep(1000);
    nonFullSupplyTab.click();

    testWebDriver.waitForElementToAppear(approvedQuantityNonFullSupply);
    approvedQuantityNonFullSupply.clear();
    approvedQuantityNonFullSupply.sendKeys(approvedQuantity);
    remarks.click();
    SeleneseTestNgHelper.assertEquals(packsToShipNonFullSupply.getText().trim(), Integer.parseInt(approvedQuantity) / 10);

    BigDecimal costNonFullSupply = new BigDecimal((Float.parseFloat(packsToShipNonFullSupply.getText().trim()) * Float.parseFloat(pricePerPackNonFullSupply.getText().trim()))).setScale(2, ROUND_HALF_UP);
    SeleneseTestNgHelper.assertEquals(String.valueOf(costNonFullSupply), totalCostNonFullSupply.getText().trim());


    BigDecimal totalOverAllCost = new BigDecimal((Float.parseFloat(totalCostFullSupplyLineItem) + Float.parseFloat(totalCostNonFullSupply.getText().trim()))).setScale(2, ROUND_HALF_UP);
    SeleneseTestNgHelper.assertEquals(overalltotalCost.getText().trim(), String.valueOf(totalOverAllCost));



  }

  public void approveRequisition() {
    testWebDriver.waitForElementToAppear(approveButton);
    testWebDriver.waitForElementToAppear(saveButton);
    saveButton.click();
    testWebDriver.waitForElementToAppear(saveSuccessMsgDiv);
    SeleneseTestNgHelper.assertTrue("R&R saved successfully message not displayed", saveSuccessMsgDiv.isDisplayed());
    approveButton.click();
    testWebDriver.waitForElementToAppear(approvedSuccessMessage);
    SeleneseTestNgHelper.assertTrue("R&R approved successfully! message not displayed", approvedSuccessMessage.isDisplayed());

  }


}