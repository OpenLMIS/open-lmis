/*
 * This program is part of the OpenLMIS logistics management information system platform software.
 * Copyright © 2013 VillageReach
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *  
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 * You should have received a copy of the GNU Affero General Public License along with this program.  If not, see http://www.gnu.org/licenses.  For additional information contact info@OpenLMIS.org. 
 */

package org.openlmis.pageobjects;


import org.openlmis.UiUtils.DBWrapper;
import org.openlmis.UiUtils.TestWebDriver;
import org.openqa.selenium.ElementNotVisibleException;
import org.openqa.selenium.Keys;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.How;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.pagefactory.AjaxElementLocatorFactory;

import java.io.IOException;
import java.math.BigDecimal;
import java.sql.SQLException;

import static com.thoughtworks.selenium.SeleneseTestBase.*;
import static java.lang.Float.parseFloat;
import static org.openqa.selenium.support.How.*;


public class InitiateRnRPage extends RequisitionPage {

  @FindBy(how = XPATH, using = "//div[@id='requisition-header']/h2")
  private static WebElement requisitionHeader=null;

  @FindBy(how = XPATH, using = "//div[@id='requisition-header']/div/div[2]/div[1]/div[1]/span")
  private static WebElement facilityLabel=null;

  @FindBy(how = XPATH, using = "//input[@value='Save']")
  private static WebElement saveButton=null;

  @FindBy(how = XPATH, using = "//input[@value='Submit']")
  private static WebElement submitButton=null;

  @FindBy(how = XPATH, using = "//input[@value='Authorize']")
  private static WebElement authorizeButton=null;

  @FindBy(how = XPATH, using = "//input[@value='Approve']")
  private static WebElement approveButton=null;

  @FindBy(how = XPATH, using = "//div[@id='submitSuccessMsgDiv' and @openlmis-message='submitMessage']")
  private static WebElement submitSuccessMessage=null;

  @FindBy(how = XPATH, using = "//div[@id='submitFailMessage' and @openlmis-message='submitError']")
  private static WebElement submitErrorMessage=null;

  @FindBy(how = ID, using = "beginningBalance_0")
  private static WebElement beginningBalance=null;

  @FindBy(how = ID, using = "quantityReceived_0")
  private static WebElement quantityReceived=null;

  @FindBy(how = ID, using = "quantityDispensed_0")
  private static WebElement quantityDispensed=null;

  @FindBy(how = ID, using = "stockInHand_0")
  private static WebElement stockInHand=null;

  @FindBy(how = ID, using = "newPatientCount_0")
  private static WebElement newPatient=null;

  @FindBy(how = ID, using = "maxStockQuantity_0")
  private static WebElement maximumStockQuantity=null;

  @FindBy(how = ID, using = "calculatedOrderQuantity_0")
  private static WebElement caculatedOrderQuantity=null;

  @FindBy(how = ID, using = "quantityRequested_0")
  private static WebElement requestedQuantity=null;

  @FindBy(how = ID, using = "normalizedConsumption_0")
  private static WebElement adjustedTotalConsumption=null;

  @FindBy(how = ID, using = "amc_0")
  private static WebElement amc=null;

  @FindBy(how = ID, using = "cost_0")
  private static WebElement totalCost=null;

  @FindBy(how = ID, using = "price_0")
  private static WebElement pricePerPack=null;

  @FindBy(how = ID, using = "packsToShip_0")
  private static WebElement packsToShip=null;

  @FindBy(how = ID, using = "price_0")
  private static WebElement pricePerPackNonFullSupply=null;

  @FindBy(how = XPATH, using = "//span[@id='fullSupplyItemsCost']")
  private static WebElement totalCostFullSupplyFooter=null;

  @FindBy(how = XPATH, using = "//span[@id='nonFullSupplyItemsCost']")
  private static WebElement totalCostNonFullSupplyFooter=null;

  @FindBy(how = XPATH, using = "//span[@id='totalCost']")
  private static WebElement totalCostFooter=null;

  @FindBy(how = ID, using = "reasonForRequestedQuantity_0")
  private static WebElement requestedQuantityExplanation=null;

  @FindBy(how = ID, using = "expirationDate_0")
  private static WebElement expirationDate=null;

  @FindBy(how = ID, using = "remarks_0")
  private static WebElement remarks=null;

  @FindBy(how = ID, using = "stockOutDays_0")
  private static WebElement totalStockOutDays=null;

  @FindBy(how = XPATH, using = "//a[@class='rnr-adjustment']")
  private static WebElement addDescription=null;

  @FindBy(how = XPATH, using = "//div[@class='adjustment-field']/div[@class='row-fluid']/div[@class='span5']/select")
  private static WebElement lossesAndAdjustmentSelect=null;


  @FindBy(how = XPATH, using = "//input[@ng-model='lossAndAdjustment.quantity']")
  private static WebElement quantityAdj=null;

  @FindBy(how = ID, using = "addNonFullSupply")
  private static WebElement addButtonNonFullSupply=null;

  @FindBy(how = XPATH, using = "//table[@id='nonFullSupplyFrozenTable']/tbody/tr/td[2]/span")
  private static WebElement productDescriptionNonFullSupply=null;

  @FindBy(how = XPATH, using = "//table[@id='nonFullSupplyFrozenTable']/tbody/tr/td[1]/span")
  private static WebElement productCodeNonFullSupply=null;

  @FindBy(how = XPATH, using = "//div[@class='adjustment-list']/ul/li/span[@class='tpl-adjustment-type ng-binding']")
  private static WebElement adjList=null;

  @FindBy(how = ID, using = "lossesAndAdjustmentsDone")
  private static WebElement lossesAndAdjustmentsDone = null;

  @FindBy(how = ID, using = "reasonForRequestedQuantity_0_warning")
  private static WebElement requestedQtyWarningMessage = null;

  @FindBy(how = XPATH, using = "//div[@id='requisition-header']/div/div[2]/div[2]/div[3]/span")
  private static WebElement reportingPeriodInitRnRScreen=null;

  @FindBy(how = XPATH, using = "//span[@ng-bind='rnr.facility.geographicZone.name']")
  private static WebElement geoZoneInitRnRScreen=null;

  @FindBy(how = XPATH, using = "//span[@ng-bind='rnr.facility.geographicZone.parent.name']")
  private static WebElement parentGeoZoneInitRnRScreen=null;

  @FindBy(how = XPATH, using = "//span[@ng-bind='rnr.facility.operatedBy.text']")
  private static WebElement operatedByInitRnRScreen=null;

  @FindBy(how = ID, using = "addNonFullSupply")
  private static WebElement addNonFullSupplyItemButton = null;

  @FindBy(how = XPATH, using = "//input[@value='Add']")
  private static WebElement addNonFullSupplyButtonScreen=null;

  @FindBy(how = ID, using = "fullSupplyTab")
  private static WebElement fullSupplyTab=null;

  @FindBy(how = XPATH, using = "//select[@id='nonFullSupplyProductsCategory']")
  private static WebElement categoryDropDown=null;

  @FindBy(how = XPATH, using = "//select[@id='nonFullSupplyProductsCodeAndName']")
  private static WebElement productDropDown=null;

  @FindBy(how = XPATH, using = "//div[@id='s2id_nonFullSupplyProductsCategory']/a/span")
  private static WebElement categoryDropDownLink=null;

  @FindBy(how = XPATH, using = "//div[@id='select2-drop']/div/input")
  private static WebElement productDropDownTextField=null;

  @FindBy(how = XPATH, using = "//div[@class='select2-result-label']")
  private static WebElement productDropDownValue=null;

  @FindBy(how = XPATH, using = "//div[@id='s2id_nonFullSupplyProductsCodeAndName']/a/span")
  private static WebElement productDropDownLink=null;

  @FindBy(how = XPATH, using = "//div[@id='select2-drop']/div/input")
  private static WebElement categoryDropDownTextField=null;

  @FindBy(how = XPATH, using = "//div[@class='select2-result-label']")
  private static WebElement categoryDropDownValue=null;

  @FindBy(how = XPATH, using = "//input[@name='nonFullSupplyProductQuantityRequested0']")
  private static WebElement nonFullSupplyProductQuantityRequested=null;

  @FindBy(how = How.XPATH, using = "//div[@id='nonFullSupplyProductCodeAndName']/label")
  private static WebElement nonFullSupplyProductCodeAndName=null;

  @FindBy(how = XPATH, using = "//div[@id='nonFullSupplyProductReasonForRequestedQuantity']/input")
  private static WebElement nonFullSupplyProductReasonForRequestedQuantity=null;

  @FindBy(how = NAME, using = "newNonFullSupply.quantityRequested")
  private static WebElement requestedQuantityField=null;

  @FindBy(how = ID, using = "reasonForRequestedQuantity")
  private static WebElement requestedQuantityExplanationField=null;

  @FindBy(how = ID, using = "showNonFullSupplyModal")
  private static WebElement addButtonOnNonFullSupplyTab = null;

  @FindBy(how = ID, using = "addLossesAndAdjustment")
  private static WebElement addLossesAndAdjustmentButton = null;

  @FindBy(how = XPATH, using = "//input[@value='Cancel']")
  private static WebElement cancelButton=null;

  @FindBy(how = XPATH, using = "//input[@id='doneNonFullSupply']")
  private static WebElement doneButtonNonFullSupply=null;

  @FindBy(how = XPATH, using = "//a[contains(text(),'Home')]")
  private static WebElement homeMenuItem=null;

  @FindBy(how = XPATH, using = "//div[@openlmis-message='error']")
  private static WebElement configureTemplateErrorDiv=null;

  @FindBy(how = XPATH, using = "//div[@id='requisition-header']/div/div[1]/div[@class='Emergency']/span")
  private static WebElement rnrEmergrncyLabel=null;

  @FindBy(how = XPATH, using = "//div[@id='requisition-header']/div/div[1]/div[@class='Regular']/span")
  private static WebElement rnrRegularLabel=null;

  @FindBy(how = ID, using = "beginningBalance_0")
  private static WebElement beginningBalanceLabel=null;

  @FindBy(how = ID, using = "selectAll")
  private static WebElement selectAllCheckbox=null;

  Float actualTotalCostFullSupply=0.0f;
  Float actualTotalCostNonFullSupply=0.0f;

  public InitiateRnRPage(TestWebDriver driver) throws IOException {
    super(driver);
    PageFactory.initElements(new AjaxElementLocatorFactory(TestWebDriver.getDriver(), 1), this);
    testWebDriver.setImplicitWait(2);
  }

  public void verifyRnRHeader(String FCode, String FName, String FCstring, String program, String periodDetails, String geoZone, String parentgeoZone, String operatedBy, String facilityType) {

    testWebDriver.sleep(1500);
    testWebDriver.waitForElementToAppear(requisitionHeader);
    String headerText = testWebDriver.getText(requisitionHeader);
    assertTrue(headerText.contains("Report and Requisition for " + program + " (" + facilityType + ")"));
    String facilityText = testWebDriver.getText(facilityLabel);
    assertTrue(facilityText.contains(FCode + FCstring + " - " + FName + FCstring));

    assertEquals(periodDetails.trim(), reportingPeriodInitRnRScreen.getText().trim());
    assertEquals(geoZone, geoZoneInitRnRScreen.getText().trim());
    assertEquals(parentgeoZone, parentGeoZoneInitRnRScreen.getText().trim());
    assertEquals(operatedBy, operatedByInitRnRScreen.getText().trim());
  }

  public void skipSingleProduct(int rowNumber){
    WebElement skipCheckBox = testWebDriver.getElementById("skip_" + (rowNumber-1));
    testWebDriver.waitForElementToAppear(skipCheckBox);
    skipCheckBox.click();
  }

  public void skipAllProduct(){
    testWebDriver.waitForElementToAppear(selectAllCheckbox);
    selectAllCheckbox.click();
  }

  public HomePage clickHome() throws IOException {
    testWebDriver.sleep(1000);
    testWebDriver.waitForElementToAppear(homeMenuItem);
    testWebDriver.keyPress(homeMenuItem);
    return new HomePage(testWebDriver);
  }

  public void enterBeginningBalance(String A) {
    String beginningBalanceValue = submitBeginningBalance(A);
    verifyFieldValue(A, beginningBalanceValue);
  }

  public void enterStockOnHand(String E) {
    String stockOnHand = submitStockOnHand(E);
    verifyFieldValue(E, stockOnHand);
  }

  public String submitBeginningBalance(String A) {
    testWebDriver.sleep(1000);
    testWebDriver.waitForElementToAppear(beginningBalance);
    beginningBalance.sendKeys(A);
    return testWebDriver.getAttribute(beginningBalance, "value");
  }

  public boolean enableBeginningBalance() {
    testWebDriver.waitForElementToAppear(beginningBalance);
    return beginningBalance.isEnabled();
  }

  public String submitStockOnHand(String E) {
    testWebDriver.sleep(1000);
    testWebDriver.waitForElementToAppear(stockInHand);
    stockInHand.sendKeys(E);
    return testWebDriver.getAttribute(stockInHand, "value");
  }

  public void verifyFieldValue(String Expected, String Actual) {
    assertEquals(Expected, Actual);
  }

  public void verifyTemplateNotConfiguredMessage() {
    testWebDriver.waitForElementToAppear(configureTemplateErrorDiv);
    assertTrue("'Please contact admin to define R&R template for this program' div should show up", configureTemplateErrorDiv.isDisplayed());
    assertTrue("Please contact admin to define R&R template for this program should show up", configureTemplateErrorDiv.getText().equals("Please contact admin to define R&R template for this program"));

  }

  public void enterQuantityReceived(String B) {
    String quantityReceivedValue = submitQuantityReceived(B);
    verifyFieldValue(B, quantityReceivedValue);
  }

  public String submitQuantityReceived(String B) {
    testWebDriver.waitForElementToAppear(quantityReceived);
    quantityReceived.sendKeys(B);
    return testWebDriver.getAttribute(quantityReceived, "value");
  }

  public void enterQuantityDispensed(String C) {
    String quantityDispensedValue = submitQuantityDispensed(C);
    verifyFieldValue(C, quantityDispensedValue);
  }

  public String submitQuantityDispensed(String C) {
    testWebDriver.waitForElementToAppear(quantityDispensed);
    quantityDispensed.sendKeys(C);
    return testWebDriver.getAttribute(quantityDispensed, "value");
  }

  public void enterLossesAndAdjustments(String adj) {
    testWebDriver.waitForElementToAppear(addDescription);
    addDescription.click();
    testWebDriver.waitForElementToAppear(lossesAndAdjustmentSelect);
    testWebDriver.selectByVisibleText(lossesAndAdjustmentSelect, "Transfer In");

    testWebDriver.waitForElementToAppear(quantityAdj);
    quantityAdj.clear();
    quantityAdj.sendKeys(adj);

    testWebDriver.waitForElementToBeEnabled(addLossesAndAdjustmentButton);
    addLossesAndAdjustmentButton.click();

    testWebDriver.waitForElementToAppear(adjList);
    String labelAdj = testWebDriver.getText(adjList);

    assertEquals("Transfer In", labelAdj.trim());

    lossesAndAdjustmentsDone.click();
  }


  public void calculateAndVerifyStockOnHand(Integer A, Integer B, Integer C, Integer D) {
    Integer StockOnHand = A + B - C + D;
    String stockOnHandActualValue = StockOnHand.toString();
    String stockOnHandExpectedValue = calculateStockOnHand(A, B, C, D);
    verifyFieldValue(stockOnHandExpectedValue, stockOnHandActualValue);
  }

  public String calculateStockOnHand(Integer A, Integer B, Integer C, Integer D) {
    enterBeginningBalance(A.toString());
    enterQuantityReceived(B.toString());
    enterQuantityDispensed(C.toString());
    enterLossesAndAdjustments(D.toString());

    testWebDriver.waitForElementToAppear(stockInHand);
    testWebDriver.sleep(2000);
    return stockInHand.getText();
  }

  public void PopulateMandatoryFullSupplyDetails(int numberOfLineItems, int numberOfLineItemsPerPage) {
    int numberOfPages = numberOfLineItems / numberOfLineItemsPerPage;
    if (numberOfLineItems % numberOfLineItemsPerPage != 0) {
      numberOfPages = numberOfPages + 1;
    }

    for (int j = 1; j <= numberOfPages; j++) {
      testWebDriver.getElementByXpath("//a[contains(text(), '" + j + "') and @class='ng-binding']").click();
      if (j == numberOfPages && (numberOfLineItems % numberOfLineItemsPerPage) != 0) {
        numberOfLineItemsPerPage = numberOfLineItems % numberOfLineItemsPerPage;
      }
      for (int i = 0; i < numberOfLineItemsPerPage; i++) {
        testWebDriver.getElementById("beginningBalance_" + i).sendKeys("10");
        testWebDriver.getElementById("quantityReceived_" + i).sendKeys("10");
        testWebDriver.getElementById("quantityDispensed_" + i).sendKeys("10");
      }

    }
  }

  public void enterAndVerifyRequestedQuantityExplanation(Integer A) {
    String warningMessage = enterRequestedQuantity(A);
    String expectedWarningMessage = "Please enter a reason";
    verifyFieldValue(warningMessage.trim(), expectedWarningMessage);
    enterExplanation();
  }

  public String enterRequestedQuantity(Integer A) {
    testWebDriver.waitForElementToAppear(requestedQuantity);
    requestedQuantity.sendKeys(A.toString());

    testWebDriver.waitForElementToAppear(requestedQtyWarningMessage);
    return testWebDriver.getText(requestedQtyWarningMessage);
  }

  public void enterExplanation() {
    requestedQuantityExplanation.sendKeys("Due to bad climate");
    testWebDriver.sleep(1000);
  }

  public void enterValuesAndVerifyCalculatedOrderQuantity(Integer F, Integer X, Integer N, Integer P, Integer H,
                                                          Integer I, boolean emergency) {
    enterValuesCalculatedOrderQuantity(F, X);
    if (emergency)
      VerifyCalculatedOrderQuantityForEmergencyRnR();
    else
      VerifyCalculatedOrderQuantity(N, P, H, I);

    testWebDriver.sleep(1000);
  }

  public void enterValuesCalculatedOrderQuantity(Integer numberOfNewPatients, Integer StockOutDays) {
    testWebDriver.waitForElementToAppear(newPatient);
    newPatient.sendKeys(Keys.DELETE);
    newPatient.sendKeys(numberOfNewPatients.toString());
    testWebDriver.waitForElementToAppear(totalStockOutDays);
    totalStockOutDays.sendKeys(Keys.DELETE);
    totalStockOutDays.sendKeys(StockOutDays.toString());
    testWebDriver.waitForElementToAppear(adjustedTotalConsumption);
    testWebDriver.sleep(1500);
    adjustedTotalConsumption.click();
  }

  public void VerifyCalculatedOrderQuantity(Integer expectedAdjustedTotalConsumption, Integer expectedAMC, Integer expectedMaximumStockQuantity, Integer expectedCalculatedOrderQuantity) {
    String actualAdjustedTotalConsumption = testWebDriver.getText(adjustedTotalConsumption);
    verifyFieldValue(expectedAdjustedTotalConsumption.toString(), actualAdjustedTotalConsumption);
    String actualAmc = testWebDriver.getText(amc);
    verifyFieldValue(expectedAMC.toString(), actualAmc.trim());
    String actualMaximumStockQuantity = testWebDriver.getText(maximumStockQuantity);
    verifyFieldValue(expectedMaximumStockQuantity.toString(), actualMaximumStockQuantity.trim());
    String actualCalculatedOrderQuantity = testWebDriver.getText(caculatedOrderQuantity);
    verifyFieldValue(expectedCalculatedOrderQuantity.toString(), actualCalculatedOrderQuantity.trim());
  }

  public void VerifyCalculatedOrderQuantityForEmergencyRnR() {
    String actualAdjustedTotalConsumption = testWebDriver.getText(adjustedTotalConsumption);
    verifyFieldValue("", actualAdjustedTotalConsumption);
    String actualAmc = testWebDriver.getText(amc);
    verifyFieldValue("", actualAmc.trim());
    String actualMaximumStockQuantity = testWebDriver.getText(maximumStockQuantity);
    verifyFieldValue("", actualMaximumStockQuantity.trim());
    String actualCalculatedOrderQuantity = testWebDriver.getText(caculatedOrderQuantity);
    verifyFieldValue("", actualCalculatedOrderQuantity.trim());
  }

  public void verifyPacksToShip(String V) {
    testWebDriver.waitForElementToAppear(packsToShip);
    String actualPacksToShip = testWebDriver.getText(packsToShip);
    verifyFieldValue(V, actualPacksToShip.trim());
    testWebDriver.sleep(500);

  }

  public void calculateAndVerifyTotalCost() {
    actualTotalCostFullSupply = calculateTotalCost();
    assertEquals(actualTotalCostFullSupply.toString() + "0", totalCost.getText().substring(1));
    testWebDriver.sleep(500);
  }

  public float calculateTotalCost() {
    testWebDriver.waitForElementToAppear(packsToShip);
    String actualPacksToShip = testWebDriver.getText(packsToShip);
    testWebDriver.waitForElementToAppear(pricePerPack);
    String actualPricePerPack = testWebDriver.getText(pricePerPack).substring(1);
    if (actualPacksToShip.trim().equals(""))
      return parseFloat("0");
    else
      return parseFloat(actualPacksToShip) * parseFloat(actualPricePerPack);
  }

  public void calculateAndVerifyTotalCostNonFullSupply() {
    actualTotalCostNonFullSupply = calculateTotalCostNonFullSupply();
    assertEquals(actualTotalCostNonFullSupply.toString() + "0", totalCost.getText().trim().substring(1));
    testWebDriver.sleep(500);
  }

  public float calculateTotalCostNonFullSupply() {
    testWebDriver.waitForElementToAppear(packsToShip);
    String actualPacksToShip = testWebDriver.getText(packsToShip);
    testWebDriver.waitForElementToAppear(pricePerPackNonFullSupply);
    String actualPricePerPack = testWebDriver.getText(pricePerPackNonFullSupply).substring(1);
    return parseFloat(actualPacksToShip.trim()) * parseFloat(actualPricePerPack.trim());
  }

  public void verifyCostOnFooter() {
    testWebDriver.waitForElementToAppear(totalCostFullSupplyFooter);
    String totalCostFullSupplyFooterValue = testWebDriver.getText(totalCostFullSupplyFooter);
    testWebDriver.waitForElementToAppear(totalCostNonFullSupplyFooter);
    String totalCostNonFullSupplyFooterValue = testWebDriver.getText(totalCostNonFullSupplyFooter);
    BigDecimal actualTotalCost = new BigDecimal(parseFloat(totalCostFullSupplyFooterValue.trim().substring(1)) + parseFloat(totalCostNonFullSupplyFooterValue.trim().substring(1))).setScale(2, BigDecimal.ROUND_HALF_UP);
    assertEquals(actualTotalCost.toString(), totalCostFooter.getText().trim().substring(1));
    fullSupplyTab.click();
    testWebDriver.sleep(500);
    actualTotalCostFullSupply = calculateTotalCost();
    assertEquals(totalCostFooter.getText().trim().substring(1),
            new BigDecimal(actualTotalCostFullSupply + actualTotalCostNonFullSupply).setScale(2,
                    BigDecimal.ROUND_HALF_UP).toString());
    testWebDriver.sleep(500);
  }

  public String getTotalCostFooter(){
     testWebDriver.waitForElementToAppear(totalCostFooter);
     return totalCostFooter.getText().trim().substring(1);
  }

  public String getFullySupplyCostFooter(){
     testWebDriver.waitForElementToAppear(totalCostFullSupplyFooter);
     return totalCostFullSupplyFooter.getText().trim().substring(1);
  }

  public void addNonFullSupplyLineItems(String requestedQuantityValue, String requestedQuantityExplanationValue,
                                        String productPrimaryName, String productCode, String category)
    throws IOException, SQLException {
    DBWrapper dbWrapper = new DBWrapper();
    String nonFullSupplyItems = dbWrapper.fetchNonFullSupplyData(productCode, "2", "1");
    clickNonFullSupplyTab();
    testWebDriver.sleep(1000);

    addButtonOnNonFullSupplyTab.click();
    testWebDriver.sleep(1000);

    assertFalse("Add button not enabled", addButtonNonFullSupply.isEnabled());
    assertTrue("Close button not displayed", cancelButton.isDisplayed());
    testWebDriver.waitForElementToAppear(categoryDropDownLink);

    categoryDropDownLink.click();
    testWebDriver.waitForElementToAppear(categoryDropDownTextField);
    categoryDropDownTextField.sendKeys(category);
    testWebDriver.waitForElementToAppear(categoryDropDownValue);
    categoryDropDownValue.click();

    productDropDownLink.click();
    testWebDriver.waitForElementToAppear(productDropDownTextField);
    productDropDownTextField.sendKeys(productCode);
    testWebDriver.waitForElementToAppear(productDropDownValue);
    productDropDownValue.click();

    requestedQuantityField.clear();
    requestedQuantityField.sendKeys(requestedQuantityValue);
    requestedQuantityExplanationField.clear();
    requestedQuantityExplanationField.sendKeys(requestedQuantityExplanationValue);
    testWebDriver.waitForElementToAppear(cancelButton);
    cancelButton.click();
    testWebDriver.waitForElementToAppear(addNonFullSupplyButtonScreen);
    addNonFullSupplyButtonScreen.click();

    testWebDriver.waitForElementToAppear(categoryDropDownLink);
    assertEquals("", testWebDriver.getSelectedOptionDefault(categoryDropDown).trim());
    assertEquals("", testWebDriver.getSelectedOptionDefault(productDropDown).trim());
    assertEquals("", requestedQuantityField.getAttribute("value").trim());
    assertEquals("", requestedQuantityExplanationField.getAttribute("value").trim());

    testWebDriver.waitForElementToAppear(categoryDropDownLink);

    categoryDropDownLink.click();
    testWebDriver.sleep(200);
    testWebDriver.waitForElementToAppear(categoryDropDownTextField);
    categoryDropDownTextField.sendKeys(category);
    testWebDriver.waitForElementToAppear(categoryDropDownValue);
    categoryDropDownValue.click();

    productDropDownLink.click();
    testWebDriver.sleep(200);
    testWebDriver.waitForElementToAppear(productDropDownTextField);
    productDropDownTextField.sendKeys(productCode);
    testWebDriver.waitForElementToAppear(productDropDownValue);
    productDropDownValue.click();

    requestedQuantityField.clear();
    requestedQuantityField.sendKeys(requestedQuantityValue);
    requestedQuantityExplanationField.clear();
    requestedQuantityExplanationField.sendKeys(requestedQuantityExplanationValue);

    testWebDriver.waitForElementToBeEnabled(addNonFullSupplyItemButton);
    addNonFullSupplyItemButton.click();

    testWebDriver.waitForElementToAppear(nonFullSupplyProductCodeAndName);
    assertEquals(productCode + " | " + productPrimaryName, nonFullSupplyProductCodeAndName.getText().trim());
    assertEquals(requestedQuantityValue, nonFullSupplyProductQuantityRequested.getAttribute("value").trim());
    assertEquals(requestedQuantityExplanationValue, nonFullSupplyProductReasonForRequestedQuantity.getAttribute("value").trim());
    doneButtonNonFullSupply.click();
    testWebDriver.sleep(1000);

    assertEquals(nonFullSupplyItems, productDescriptionNonFullSupply.getText().trim());
    assertEquals(productCode, productCodeNonFullSupply.getText().trim());
    testWebDriver.waitForElementToAppear(requestedQuantity);
    assertEquals(requestedQuantityValue, testWebDriver.getAttribute(requestedQuantity, "value").trim());
    assertEquals(requestedQuantityExplanationValue, testWebDriver.getAttribute(requestedQuantityExplanation, "value").trim());

  }

  public int getSizeOfElements(String xpath) {
    return testWebDriver.getElementsSizeByXpath(xpath);
  }

  public void verifyColumnsHeadingPresent(String xpathTillTrTag, String heading, int noOfColumns) {
    boolean flag = false;
    String actualColumnHeading;
    for (int i = 0; i < noOfColumns; i++) {
      try {
        WebElement columnElement = testWebDriver.getElementByXpath(xpathTillTrTag + "/th[" + (i + 1) + "]");
        columnElement.click();
        actualColumnHeading = columnElement.getText();
      } catch (ElementNotVisibleException e) {
        continue;
      } catch (NoSuchElementException e) {
        continue;
      }
      if (actualColumnHeading.trim().toUpperCase().equals(heading.toUpperCase())) {
        flag = true;
        break;
      }
    }
    assertTrue(flag);
  }

  public void verifyColumnHeadingNotPresent(String xpathTillTrTag, String heading, int noOfColumns) {
    boolean flag = false;
    String actualColumnHeading;
    for (int i = 0; i < noOfColumns; i++) {
      try {
        WebElement columnElement = testWebDriver.getElementByXpath(xpathTillTrTag + "/th[" + (i + 1) + "]");
        columnElement.click();
        actualColumnHeading = columnElement.getText();
      } catch (ElementNotVisibleException e) {
        continue;
      } catch (NoSuchElementException e) {
        continue;
      }
      if (actualColumnHeading.trim().toUpperCase().equals(heading.toUpperCase())) {
        flag = true;
        break;
      }
    }
    assertFalse(flag);
  }


  public void addMultipleNonFullSupplyLineItems(int numberOfLineItems, boolean isMultipleCategories) throws IOException, SQLException {
    clickNonFullSupplyTab();
    testWebDriver.sleep(1000);
    addButtonOnNonFullSupplyTab.click();
    testWebDriver.sleep(1000);
    testWebDriver.waitForElementToAppear(categoryDropDownLink);

    for (int i = 0; i < numberOfLineItems; i++) {
      categoryDropDownLink.click();
      testWebDriver.waitForElementToAppear(categoryDropDownTextField);
      if (isMultipleCategories) {
        categoryDropDownTextField.sendKeys("Antibiotics" + i);
      } else {
        categoryDropDownTextField.sendKeys("Antibiotics");
      }
      testWebDriver.waitForElementToAppear(categoryDropDownValue);
      categoryDropDownValue.click();

      productDropDownLink.click();
      testWebDriver.waitForElementToAppear(productDropDownTextField);
      productDropDownTextField.sendKeys("NF" + i);
      testWebDriver.waitForElementToAppear(productDropDownValue);
      productDropDownValue.click();

      requestedQuantityField.clear();
      requestedQuantityField.sendKeys("10");
      requestedQuantityExplanationField.clear();
      requestedQuantityExplanationField.sendKeys("Due to certain reasons: " + i);

      testWebDriver.waitForElementToBeEnabled(addNonFullSupplyItemButton);
      addNonFullSupplyItemButton.click();

      testWebDriver.sleep(500);
      testWebDriver.waitForElementToAppear(nonFullSupplyProductCodeAndName);
    }
    doneButtonNonFullSupply.click();
    testWebDriver.sleep(500);
  }

  public void saveRnR() {
    saveButton.click();
    testWebDriver.sleep(1500);
  }

  public void submitRnR() {
    submitButton.click();
    testWebDriver.sleep(250);
  }

  public void authorizeRnR() {
    authorizeButton.click();
    testWebDriver.sleep(250);
  }

  public void verifyAllFieldsDisabled() {

    assertFalse("beginningBalance should be disabled", beginningBalance.isEnabled());
    assertFalse("quantityReceived should be disabled", quantityReceived.isEnabled());
    assertFalse("quantityDispensed should be disabled", quantityDispensed.isEnabled());
    assertFalse("newPatient should be disabled", newPatient.isEnabled());
    assertFalse("totalStockOutDays should be disabled", totalStockOutDays.isEnabled());
    assertFalse("requestedQuantity should be disabled", requestedQuantity.isEnabled());
    assertFalse("requestedQuantityExplanation should be disabled", requestedQuantityExplanation.isEnabled());
    assertFalse("expirationDate should be disabled", expirationDate.isEnabled());
    assertFalse("remarks should be disabled", remarks.isEnabled());
  }

  public void verifySaveButtonDisabled() {
    assertFalse("saveButton should be disabled", saveButton.isEnabled());
  }

  public void verifySubmitRnrSuccessMsg() {
    assertTrue("RnR Submit Success message not displayed", submitSuccessMessage.isDisplayed());
  }

  public void verifyAuthorizeRnrSuccessMsg() {
    assertTrue("RnR authorize Success message not displayed", submitSuccessMessage.isDisplayed());
  }

  public void verifySubmitRnrErrorMsg() {
    testWebDriver.sleep(1000);
    assertTrue("RnR Fail message not displayed", submitErrorMessage.isDisplayed());
  }

  public void verifyAuthorizeButtonNotPresent() {
    boolean authorizeButtonPresent;
    try {
      authorizeButton.click();
      authorizeButtonPresent = true;
    } catch (ElementNotVisibleException e) {
      authorizeButtonPresent = false;
    }
    assertFalse(authorizeButtonPresent);
  }

  public void verifyApproveButtonNotPresent() {
    boolean approveButtonPresent = false;
    try {
      approveButton.click();
      approveButtonPresent = true;
    } catch (ElementNotVisibleException e) {
      approveButtonPresent = false;
    } catch (NoSuchElementException e) {
      approveButtonPresent = false;
    } finally {
      assertFalse(approveButtonPresent);
    }
  }

  public String getEmergencyLabelText() {
     testWebDriver.waitForElementToAppear(rnrEmergrncyLabel);
    return rnrEmergrncyLabel.getText();
  }

  public String getRegularLabelText() {
    testWebDriver.waitForElementToAppear(rnrRegularLabel);
    return rnrRegularLabel.getText();
  }

  public String getBeginningBalance() {
    testWebDriver.waitForElementToAppear(beginningBalanceLabel);
    return beginningBalanceLabel.getText();
  }


  public String getCategoryText(Integer rowNumber) {
    return testWebDriver.getElementById("category_" + rowNumber).getText();
  }

  public String getProductCode(Integer rowNumber) {
    return testWebDriver.getElementById("productCode_" + rowNumber).getText();
  }
}