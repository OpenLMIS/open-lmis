/*
 * Copyright © 2013 VillageReach.  All Rights Reserved.  This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 *
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package org.openlmis.pageobjects;


import com.thoughtworks.selenium.SeleneseTestNgHelper;
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
import java.sql.SQLException;
import java.util.Date;
import java.text.*;

import static com.thoughtworks.selenium.SeleneseTestBase.assertFalse;
import static com.thoughtworks.selenium.SeleneseTestBase.assertTrue;
import static org.openqa.selenium.support.How.*;


public class InitiateRnRPage extends Page {

  @FindBy(how = XPATH, using = "//div[@id='requisition-header']/h2")
  private static WebElement requisitionHeader;

  @FindBy(how = XPATH, using = "//div[@id='requisition-header']/div[@class='info-box']/div[@class='row-fluid'][1]/div[1]")
  private static WebElement facilityLabel;

  @FindBy(how = XPATH, using = "//input[@value='Save']")
  private static WebElement saveButton;

  @FindBy(how = XPATH, using = "//input[@value='Submit']")
  private static WebElement submitButton;

  @FindBy(how = XPATH, using = "//input[@value='Authorize']")
  private static WebElement authorizeButton;

  @FindBy(how = XPATH, using = "//input[@value='Approve']")
  private static WebElement approveButton;

  @FindBy(how = XPATH, using = "//div[@id='saveSuccessMsgDiv' and @openlmis-message='message']")
  private static WebElement successMessage;

  @FindBy(how = XPATH, using = "//div[@id='submitSuccessMsgDiv' and @openlmis-message='submitMessage']")
  private static WebElement submitSuccessMessage;

  @FindBy(how = XPATH, using = "//div[@id='submitFailMessage' and @openlmis-message='submitError']")
  private static WebElement submitErrorMessage;

  @FindBy(how = ID, using = "beginningBalance_0")
  private static WebElement beginningBalance;

  @FindBy(how = ID, using = "quantityReceived_0")
  private static WebElement quantityReceived;

  @FindBy(how = ID, using = "quantityDispensed_0")
  private static WebElement quantityDispensed;

  @FindBy(how = ID, using = "stockInHand_0")
  private static WebElement stockInHand;

  @FindBy(how = ID, using = "newPatientCount_0")
  private static WebElement newPatient;

  @FindBy(how = ID, using = "maxStockQuantity_0")
  private static WebElement maximumStockQuantity;

  @FindBy(how = ID, using = "calculatedOrderQuantity_0")
  private static WebElement caculatedOrderQuantity;

  @FindBy(how = ID, using = "quantityRequested_0")
  private static WebElement requestedQuantity;

  @FindBy(how = ID, using = "normalizedConsumption_0")
  private static WebElement adjustedTotalConsumption;

  @FindBy(how = ID, using = "amc_0")
  private static WebElement amc;

  @FindBy(how = ID, using = "cost_0")
  private static WebElement totalCost;

  @FindBy(how = ID, using = "price_0")
  private static WebElement pricePerPack;

  @FindBy(how = ID, using = "packsToShip_0")
  private static WebElement packsToShip;

  @FindBy(how = ID, using = "price_0")
  private static WebElement pricePerPackNonFullSupply;

  @FindBy(how = XPATH, using = "//span[@id='fullSupplyItemsCost']")
  private static WebElement totalCostFullSupplyFooter;

  @FindBy(how = XPATH, using = "//span[@id='nonFullSupplyItemsCost']")
  private static WebElement totalCostNonFullSupplyFooter;

  @FindBy(how = XPATH, using = "//span[@id='totalCost']")
  private static WebElement totalCostFooter;

  @FindBy(how = ID, using = "reasonForRequestedQuantity_0")
  private static WebElement requestedQuantityExplanation;

  @FindBy(how = ID, using = "stockOutDays_0")
  private static WebElement totalStockOutDays;

  @FindBy(how = XPATH, using = "//a[@class='rnr-adjustment']")
  private static WebElement addDescription;

  @FindBy(how = XPATH, using = "//div[@class='adjustment-field']/div[@class='row-fluid']/div[@class='span5']/select")
  private static WebElement lossesAndAdjustmentSelect;


  @FindBy(how = XPATH, using = "//input[@ng-model='lossAndAdjustment.quantity']")
  private static WebElement quantityAdj;

  @FindBy(how = ID, using = "addNonFullSupply")
  private static WebElement addButtonNonFullSupply;

  @FindBy(how = XPATH, using = "//table[@id='nonFullSupplyTable']/tbody/tr/td[2]/ng-switch/span")
  private static WebElement productDescriptionNonFullSupply;

  @FindBy(how = XPATH, using = "//table[@id='nonFullSupplyTable']/tbody/tr/td[1]/ng-switch/span")
  private static WebElement productCodeNonFullSupply;

  @FindBy(how = XPATH, using = "//div[@class='adjustment-list']/ul/li/span[@class='tpl-adjustment-type ng-binding']")
  private static WebElement adjList;

  @FindBy(how = XPATH, using = "//input[@value='Done']")
  private static WebElement doneButton;

  @FindBy(how = XPATH, using = "//span[@class='alert alert-warning reason-request']")
  private static WebElement requestedQtyWarningMessage;

  @FindBy(how = XPATH, using = "//div[@class='info-box']/div[2]/div[3]")
  private static WebElement reportingPeriodInitRnRScreen;

  @FindBy(how = XPATH, using = "//span[@ng-bind='rnr.facility.geographicZone.name']")
  private static WebElement geoZoneInitRnRScreen;

  @FindBy(how = XPATH, using = "//span[@ng-bind='rnr.facility.geographicZone.parent.name']")
  private static WebElement parentGeoZoneInitRnRScreen;

  @FindBy(how = XPATH, using = "//span[@ng-bind='rnr.facility.operatedBy.text']")
  private static WebElement operatedByInitRnRScreen;

  @FindBy(how = XPATH, using = "//input[@id='addNonFullSupply' and @value='Add']")
  private static WebElement addNonFullSupplyButton;

  @FindBy(how = XPATH, using = "//input[@value='Add']")
  private static WebElement addNonFullSupplyButtonScreen;

  @FindBy(how = ID, using = "nonFullSupplyTab")
  private static WebElement nonFullSupplyTab;

  @FindBy(how = ID, using = "fullSupplyTab")
  private static WebElement fullSupplyTab;

  @FindBy(how = XPATH, using = "//select[@id='nonFullSupplyProductsCategory']")
  private static WebElement categoryDropDown;

  @FindBy(how = XPATH, using = "//select[@id='nonFullSupplyProductsCodeAndName']")
  private static WebElement productDropDown;

  @FindBy(how = XPATH, using = "//div[@id='s2id_nonFullSupplyProductsCategory']/a/span")
  private static WebElement categoryDropDownLink;

  @FindBy(how = XPATH, using = "//input[@class='select2-input select2-focused']")
  private static WebElement productDropDownTextField;

  @FindBy(how = XPATH, using = "//div[@class='select2-result-label']")
  private static WebElement productDropDownValue;

  @FindBy(how = XPATH, using = "//div[@id='s2id_nonFullSupplyProductsCodeAndName']/a/span")
  private static WebElement productDropDownLink;


  @FindBy(how = XPATH, using = "//input[@class='select2-input select2-focused']")
  private static WebElement categoryDropDownTextField;

  @FindBy(how = XPATH, using = "//div[@class='select2-result-label']")
  private static WebElement categoryDropDownValue;

  @FindBy(how = XPATH, using = "//select[@id='nonFullSupplyProductsCode']")
  private static WebElement productCodeDropDown;

  @FindBy(how = XPATH, using = "//input[@name='nonFullSupplyProductQuantityRequested0']")
  private static WebElement nonFullSupplyProductQuantityRequested;

  @FindBy(how = How.XPATH, using = "//div[@id='nonFullSupplyProductCodeAndName']/label")
  private static WebElement nonFullSupplyProductCodeAndName;

  @FindBy(how = XPATH, using = "//div[@id='nonFullSupplyProductReasonForRequestedQuantity']/input")
  private static WebElement nonFullSupplyProductReasonForRequestedQuantity;

  @FindBy(how = NAME, using = "newNonFullSupply.quantityRequested")
  private static WebElement requestedQuantityField;

  @FindBy(how = ID, using = "reasonForRequestedQuantity")
  private static WebElement requestedQuantityExplanationField;

  @FindBy(how = XPATH, using = "//input[@value='Add']")
  private static WebElement addButton;

  @FindBy(how = XPATH, using = "//input[@ng-click='addNonFullSupplyLineItem()']")
  private static WebElement addButtonEnabled;

  @FindBy(how = XPATH, using = "//input[@value='Cancel']")
  private static WebElement cancelButton;

  @FindBy(how = XPATH, using = "//input[@id='doneNonFullSupply']")
  private static WebElement doneButtonNonFullSupply;

  @FindBy(how = XPATH, using = "//a[contains(text(),'Home')]")
  private static WebElement homeMenuItem;

  @FindBy(how = XPATH, using = "//div[@openlmis-message='error']")
  private static WebElement configureTemplateErrorDiv;

  @FindBy(how = XPATH , using = "//input[@value='Comments']")
  private static WebElement commentsButton;

  String successText = "R&R saved successfully!";
  Float actualTotalCostFullSupply, actualTotalCostNonFullSupply;

  @FindBy(how = ID, using = "addComment")
  private static WebElement addCommentTextArea;

  @FindBy(how = ID, using = "addButton")
  private static WebElement addCommentButton;


  @FindBy(how = ID, using = "commentClose")
  private static WebElement commentCloseIcon;

  @FindBy(how = XPATH , using = "//ul[@id='comments-list']/li[1]/span")
  private static WebElement lastComment;

  @FindBy(how = XPATH , using = "//ul[@id='comments-list']/li[1]/div")
  private static WebElement lastCommentAddedBy;

  public InitiateRnRPage(TestWebDriver driver) throws IOException {
    super(driver);
    PageFactory.initElements(new AjaxElementLocatorFactory(testWebDriver.getDriver(), 1), this);
    testWebDriver.setImplicitWait(1);
  }

  public void verifyRnRHeader(String FCode, String FName, String FCstring, String program, String periodDetails, String geoZone, String parentgeoZone, String operatedBy, String facilityType) {

    testWebDriver.sleep(1500);
    testWebDriver.waitForElementToAppear(requisitionHeader);
    String headerText = testWebDriver.getText(requisitionHeader);
    assertTrue(headerText.contains("Report and Requisition for " + program + " (" + facilityType + ")"));
    String facilityText = testWebDriver.getText(facilityLabel);
    assertTrue(facilityText.contains(FCode + FCstring + " - " + FName + FCstring));

    SeleneseTestNgHelper.assertEquals(reportingPeriodInitRnRScreen.getText().trim().substring("Reporting Period: ".length()), periodDetails.trim());
    SeleneseTestNgHelper.assertEquals(geoZone, geoZoneInitRnRScreen.getText().trim());
    SeleneseTestNgHelper.assertEquals(parentgeoZone, parentGeoZoneInitRnRScreen.getText().trim());
    SeleneseTestNgHelper.assertEquals(operatedBy, operatedByInitRnRScreen.getText().trim());


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

  public String submitBeginningBalance(String A) {
    testWebDriver.sleep(1000);
    testWebDriver.waitForElementToAppear(beginningBalance);
    beginningBalance.sendKeys(A);
    return testWebDriver.getAttribute(beginningBalance, "value");
  }

  public void verifyFieldValue(String Expected, String Actual) {
    SeleneseTestNgHelper.assertEquals(Actual, Expected);
  }

  public void verifyTemplateNotConfiguredMessage() {
    testWebDriver.waitForElementToAppear(configureTemplateErrorDiv);
    assertTrue("Please contact admin to define R&R template for this program should show up", configureTemplateErrorDiv.isDisplayed());

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
    addButton.click();
    testWebDriver.waitForElementToAppear(adjList);
    String labelAdj = testWebDriver.getText(adjList);
    SeleneseTestNgHelper.assertEquals(labelAdj.trim(), "Transfer In");
//    String adjValue = testWebDriver.getAttribute(adjListValue, "value");
    // SeleneseTestNgHelper.assertEquals(adjValue, adj);
    //testWebDriver.waitForElementToAppear(totalAdj);
    //String totalAdjValue = testWebDriver.getText(totalAdj);
    testWebDriver.sleep(1000);
    //SeleneseTestNgHelper.assertEquals(totalAdjValue.substring("Total ".length()), adj);
    testWebDriver.sleep(1000);
    doneButton.click();
    testWebDriver.sleep(1000);


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
    beginningBalance.click();
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

  public void enterValuesAndVerifyCalculatedOrderQuantity(Integer F, Integer X, Integer N, Integer P, Integer H, Integer I) {
    enterValuesCalculatedOrderQuantity(F, X);
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

  public void verifyPacksToShip(Integer V) {
    testWebDriver.waitForElementToAppear(packsToShip);
    String actualPacksToShip = testWebDriver.getText(packsToShip);
    verifyFieldValue(V.toString(), actualPacksToShip.trim());
    testWebDriver.sleep(500);

  }

  public void calculateAndVerifyTotalCost() {
    actualTotalCostFullSupply = calculateTotalCost();
    SeleneseTestNgHelper.assertEquals(actualTotalCostFullSupply.toString() + "0", totalCost.getText().substring(1));
    testWebDriver.sleep(500);
  }

  public float calculateTotalCost() {
    testWebDriver.waitForElementToAppear(packsToShip);
    String actualPacksToShip = testWebDriver.getText(packsToShip);
    testWebDriver.waitForElementToAppear(pricePerPack);
    String actualPricePerPack = testWebDriver.getText(pricePerPack).substring(1);
    return Float.parseFloat(actualPacksToShip) * Float.parseFloat(actualPricePerPack);
  }

  public void calculateAndVerifyTotalCostNonFullSupply() {
    actualTotalCostNonFullSupply = calculateTotalCostNonFullSupply();
    SeleneseTestNgHelper.assertEquals(actualTotalCostNonFullSupply.toString() + "0", totalCost.getText().trim().substring(1));
    testWebDriver.sleep(500);
  }

  public float calculateTotalCostNonFullSupply() {
    testWebDriver.waitForElementToAppear(packsToShip);
    String actualPacksToShip = testWebDriver.getText(packsToShip);
    testWebDriver.waitForElementToAppear(pricePerPackNonFullSupply);
    String actualPricePerPack = testWebDriver.getText(pricePerPackNonFullSupply).substring(1);
    return Float.parseFloat(actualPacksToShip.trim()) * Float.parseFloat(actualPricePerPack.trim());
  }

  public void verifyCostOnFooter() {
    testWebDriver.waitForElementToAppear(totalCostFullSupplyFooter);
    String totalCostFullSupplyFooterValue = testWebDriver.getText(totalCostFullSupplyFooter);
    testWebDriver.waitForElementToAppear(totalCostNonFullSupplyFooter);
    String totalCostNonFullSupplyFooterValue = testWebDriver.getText(totalCostNonFullSupplyFooter);
    Float actualTotalCost = Float.parseFloat(totalCostFullSupplyFooterValue.trim()) + Float.parseFloat(totalCostNonFullSupplyFooterValue.trim());
    SeleneseTestNgHelper.assertEquals(actualTotalCost.toString() + "0", totalCostFooter.getText().trim());
    SeleneseTestNgHelper.assertEquals(totalCostFooter.getText().trim(), (actualTotalCostFullSupply + actualTotalCostNonFullSupply) + "0");
    testWebDriver.sleep(500);
  }

  public void addNonFullSupplyLineItems(String requestedQuantityValue, String requestedQuantityExplanationValue, String productPrimaryName, String productCode, String category, String baseurl, String dburl) throws IOException, SQLException {
    testWebDriver.waitForElementToAppear(nonFullSupplyTab);
    nonFullSupplyTab.click();
    DBWrapper dbWrapper = new DBWrapper(baseurl, dburl);
    String nonFullSupplyItems = dbWrapper.fetchNonFullSupplyData(productCode, "2", "1");
    testWebDriver.waitForElementToAppear(addNonFullSupplyButtonScreen);
    testWebDriver.sleep(1000);
    addButton.click();
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
    SeleneseTestNgHelper.assertEquals(testWebDriver.getSelectedOptionDefault(categoryDropDown).trim(), "");
    SeleneseTestNgHelper.assertEquals(testWebDriver.getSelectedOptionDefault(productDropDown).trim(), "");
    SeleneseTestNgHelper.assertEquals(requestedQuantityField.getAttribute("value").trim(), "");
    SeleneseTestNgHelper.assertEquals(requestedQuantityExplanationField.getAttribute("value").trim(), "");

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
    testWebDriver.waitForElementToAppear(addNonFullSupplyButton);
    testWebDriver.sleep(1000);
    addNonFullSupplyButton.click();
    testWebDriver.sleep(500);
    testWebDriver.waitForElementToAppear(nonFullSupplyProductCodeAndName);
    SeleneseTestNgHelper.assertEquals(nonFullSupplyProductCodeAndName.getText().trim(), productCode + " | " + productPrimaryName);
    SeleneseTestNgHelper.assertEquals(nonFullSupplyProductQuantityRequested.getAttribute("value").trim(), requestedQuantityValue);
    SeleneseTestNgHelper.assertEquals(nonFullSupplyProductReasonForRequestedQuantity.getAttribute("value").trim(), requestedQuantityExplanationValue);
    doneButtonNonFullSupply.click();
    testWebDriver.sleep(500);

    SeleneseTestNgHelper.assertEquals(productDescriptionNonFullSupply.getText().trim(), nonFullSupplyItems);
    SeleneseTestNgHelper.assertEquals(productCodeNonFullSupply.getText().trim(), productCode);
    SeleneseTestNgHelper.assertEquals(testWebDriver.getAttribute(requestedQuantity, "value").trim(), requestedQuantityValue);
    SeleneseTestNgHelper.assertEquals(testWebDriver.getAttribute(requestedQuantityExplanation, "value").trim(), requestedQuantityExplanationValue);

  }

  public int getSizeOfElements(String xpath) {
    return testWebDriver.getElementsSizeByXpath(xpath);
  }

  public void verifyColumnsHeadingPresent(String xpathTillTrTag, String heading, int noOfColumns) {
    boolean flag = false;
    String actualColumnHeading = null;
    for (int i = 0; i < noOfColumns; i++) {
      actualColumnHeading = testWebDriver.getElementByXpath(xpathTillTrTag + "/th[" + (i + 1) + "]").getText();
      if (actualColumnHeading.trim().contains(heading))
        flag = true;
    }
    SeleneseTestNgHelper.assertEquals(flag, true);
  }

  public void verifyColumnHeadingNotPresent(String xpathTillTrTag, String heading, int noOfColumns) {
    boolean flag = false;
    String actualColumnHeading = null;
    for (int i = 0; i < noOfColumns; i++) {
      actualColumnHeading = testWebDriver.getElementByXpath(xpathTillTrTag + "/th[" + (i + 1) + "]").getText();
      if (actualColumnHeading.trim().contains(heading))
        flag = true;
    }
    SeleneseTestNgHelper.assertEquals(flag, false);
  }

  public void addMultipleNonFullSupplyLineItems(int numberOfLineItems, boolean isMultipleCategories) throws IOException, SQLException {
    testWebDriver.waitForElementToAppear(nonFullSupplyTab);
    nonFullSupplyTab.click();

    testWebDriver.waitForElementToAppear(addNonFullSupplyButtonScreen);
    testWebDriver.sleep(1000);
    addButton.click();
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
      testWebDriver.waitForElementToAppear(addNonFullSupplyButton);
      testWebDriver.sleep(1000);
      addNonFullSupplyButton.click();
      testWebDriver.sleep(500);
      testWebDriver.waitForElementToAppear(nonFullSupplyProductCodeAndName);
    }
    doneButtonNonFullSupply.click();
    testWebDriver.sleep(500);
  }

  public void saveRnR() {
    saveButton.click();
    testWebDriver.sleep(1500);
    assertTrue("R&R saved successfully! message not displayed", successMessage.isDisplayed());
  }

  public void submitRnR() {
    submitButton.click();
    testWebDriver.sleep(1500);
  }

  public void authorizeRnR() {
    authorizeButton.click();
    testWebDriver.sleep(250);
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

  public void clearNewPatientField() {
    newPatient.sendKeys("\u0008");
    testWebDriver.sleep(500);
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

  public void addComments(String comments) {
      commentsButton.click();
      addCommentTextArea.sendKeys(comments);
      addCommentButton.click();
      commentCloseIcon.click();
  }

    public void verifyLastComment(String comments,String AddedBy) {
        boolean isAddedBy;
        boolean isAddedOn;
        commentsButton.click();
        SeleneseTestNgHelper.assertEquals(lastComment.getText(), comments);

        isAddedBy = lastCommentAddedBy.getText().contains("By: " + AddedBy);
        SeleneseTestNgHelper.assertTrue(isAddedBy);
        Date date = new Date();
        SimpleDateFormat ft = new SimpleDateFormat ("dd/MM/YYYY");
        isAddedOn = lastCommentAddedBy.getText().contains(ft.format(date));
        SeleneseTestNgHelper.assertTrue(isAddedOn);
        commentCloseIcon.click();
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
}