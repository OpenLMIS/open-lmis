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


import org.openlmis.UiUtils.TestWebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.How;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.pagefactory.AjaxElementLocatorFactory;
import org.openqa.selenium.support.ui.Select;

import static com.thoughtworks.selenium.SeleneseTestBase.assertFalse;
import static com.thoughtworks.selenium.SeleneseTestBase.assertTrue;
import static com.thoughtworks.selenium.SeleneseTestNgHelper.assertEquals;


public class TemplateConfigPage extends Page {

  @FindBy(how = How.XPATH, using = "//input[@value='Save']")
  private static WebElement SaveButton = null;

  @FindBy(how = How.XPATH, using = "//div[@id='saveSuccessMsgDiv' and @ng-show='message']")
  private static WebElement saveSuccessMsg = null;

  @FindBy(how = How.XPATH, using = "//li[@id='stockInHand']/span[@class='tpl-source']/span/select")
  private static WebElement stockInHandDropDown = null;

  @FindBy(how = How.XPATH, using = "//li[@id='quantityDispensed']/span[@class='tpl-source']/span/select")
  private static WebElement dropDownTotalConsumedQuantity = null;

  @FindBy(how = How.XPATH, using = "//li[@id='reasonForRequestedQuantity']/span[@class='tpl-error']/div")
  private static WebElement requestedQtyExplanationErrorMessage = null;

  @FindBy(how = How.XPATH, using = "//li[@id='quantityRequested']/span[@class='tpl-error']/div")
  private static WebElement requestedQtyErrorMessage = null;

  @FindBy(how = How.XPATH, using = "//li[@id='quantityDispensed']/span[@class='tpl-error']/div")
  private static WebElement totalConsumedQtyErrorMessage = null;

  @FindBy(how = How.XPATH, using = "//li[@id='stockInHand']/span[@class='tpl-error']/div")
  private static WebElement stockOnHandQtyErrorMessage = null;

  @FindBy(how = How.XPATH, using = "//li[@id='productCode']/span[@class='tpl-label']/input")
  private static WebElement productCode = null;

  @FindBy(how = How.XPATH, using = "//li[@id='productCode']/span[@class='tpl-source']/span[2]")
  private static WebElement productCodeSource = null;

  @FindBy(how = How.XPATH, using = "//li[@id='product']/span[@class='tpl-label']/input")
  private static WebElement productName = null;

  @FindBy(how = How.XPATH, using = "//li[@id='product']/span[@class='tpl-source']/span[2]")
  private static WebElement productNameSource = null;

  @FindBy(how = How.XPATH, using = "//li[@id='dispensingUnit']/span[@class='tpl-label']/input")
  private static WebElement unitOfIssue = null;

  @FindBy(how = How.XPATH, using = "//li[@id='dispensingUnit']/span[@class='tpl-source']/span[2]")
  private static WebElement unitOfIssueSource = null;

  @FindBy(how = How.XPATH, using = "//li[@id='beginningBalance']/span[@class='tpl-label']/input")
  private static WebElement beginningBalance = null;

  @FindBy(how = How.XPATH, using = "//li[@id='beginningBalance']/span[@class='tpl-source']/span[2]")
  private static WebElement beginningBalanceSource = null;

  @FindBy(how = How.XPATH, using = "//li[@id='quantityReceived']/span[@class='tpl-label']/input")
  private static WebElement totalReceivedQuantity = null;

  @FindBy(how = How.XPATH, using = "//li[@id='quantityReceived']/span[@class='tpl-source']/span[2]")
  private static WebElement totalReceivedQuantitySource = null;

  @FindBy(how = How.XPATH, using = "//li[@id='quantityDispensed']/span[@class='tpl-label']/input")
  private static WebElement totalConsumedQuantity = null;

  @FindBy(how = How.XPATH, using = "//li[@id='quantityDispensed']/span[@class='tpl-source']/span/select/option[@selected='selected']")
  private static WebElement totalConsumedQuantitySource = null;

  @FindBy(how = How.XPATH, using = "//li[@id='quantityDispensed']/span/input")
  private static WebElement totalConsumedQuantityCheckBox = null;

  @FindBy(how = How.XPATH, using = "//li[@id='reasonForRequestedQuantity']/span/input")
  private static WebElement requestedQuantityCheckBox = null;

  @FindBy(how = How.XPATH, using = "//li[@id='expirationDate']/span/input")
  private static WebElement expirationDateCheckBox = null;

  @FindBy(how = How.XPATH, using = "//li[@id='expirationDate']/span[@class='tpl-label']/input")
  private static WebElement expirationDateTextBox = null;

  @FindBy(how = How.XPATH, using = "//li[@id='expirationDate']/span[@class='tpl-source']/span[2]")
  private static WebElement expirationDateSource = null;

  @FindBy(how = How.XPATH, using = "//li[@id='quantityRequested']/span/input")
  private static WebElement requestedQuantityExplanationCheckBox = null;

  @FindBy(how = How.XPATH, using = "//li[@id='total']/span/input")
  private static WebElement totalCheckBox = null;

  @FindBy(how = How.XPATH, using = "//li[@id='total']/span[@class='tpl-label']/input")
  private static WebElement totalTextBox = null;

  @FindBy(how = How.XPATH, using = "//li[@id='total']/span[@class='tpl-source']/span[2]")
  private static WebElement totalSource = null;

  @FindBy(how = How.XPATH, using = "//li[@id='product']/span/input")
  private static WebElement productNameCheckBox = null;

  @FindBy(how = How.XPATH, using = "//li[@id='lossesAndAdjustments']/span[@class='tpl-label']/input")
  private static WebElement lossesAndAdj = null;

  @FindBy(how = How.XPATH, using = "//li[@id='lossesAndAdjustments']/span[@class='tpl-source']/span[2]")
  private static WebElement lossesAndAdjSource = null;

  @FindBy(how = How.XPATH, using = "//li[@id='stockInHand']/span[@class='tpl-label']/input")
  private static WebElement stockOnHand = null;

  @FindBy(how = How.XPATH, using = "//li[@id='stockInHand']/span[@class='tpl-source']/span/select/option[@selected='selected']")
  private static WebElement stockOnHandSource = null;

  @FindBy(how = How.XPATH, using = "//li[@id='stockInHand']/span/input")
  private static WebElement stockOnHandCheckBox = null;

  @FindBy(how = How.XPATH, using = "//li[@id='newPatientCount']/span[@class='tpl-label']/input")
  private static WebElement newPatients = null;

  @FindBy(how = How.XPATH, using = "//li[@id='newPatientCount']/span[@class='tpl-source']/span[2]")
  private static WebElement newPatientsSource = null;

  @FindBy(how = How.XPATH, using = "//li[@id='stockOutDays']/span[@class='tpl-label']/input")
  private static WebElement stockOutDays = null;

  @FindBy(how = How.XPATH, using = "//li[@id='stockOutDays']/span[@class='tpl-source']/span[2]")
  private static WebElement stockOutDaysSource = null;

  @FindBy(how = How.XPATH, using = "//li[@id='normalizedConsumption']/span[@class='tpl-label']/input")
  private static WebElement adjustedTotalConsumption = null;

  @FindBy(how = How.XPATH, using = "//li[@id='normalizedConsumption']/span[@class='tpl-source']/span[2]")
  private static WebElement adjustedTotalConsumptionSource = null;

  @FindBy(how = How.XPATH, using = "//li[@id='maxStockQuantity']/span[@class='tpl-label']/input")
  private static WebElement maxStockQuantity = null;

  @FindBy(how = How.XPATH, using = "//li[@id='maxStockQuantity']/span[@class='tpl-source']/span[2]")
  private static WebElement maxStockQuantitySource = null;

  @FindBy(how = How.XPATH, using = "//li[@id='calculatedOrderQuantity']/span[@class='tpl-label']/input")
  private static WebElement calculatedOrderQuantity = null;

  @FindBy(how = How.XPATH, using = "//li[@id='calculatedOrderQuantity']/span[@class='tpl-source']/span[2]")
  private static WebElement calculatedOrderQuantitySource = null;

  @FindBy(how = How.XPATH, using = "//li[@id='quantityRequested']/span[@class='tpl-label']/input")
  private static WebElement requestedQuantity = null;

  @FindBy(how = How.XPATH, using = "//li[@id='quantityRequested']/span[@class='tpl-source']/span[2]")
  private static WebElement requestedQuantitySource = null;

  @FindBy(how = How.XPATH, using = "//li[@id='reasonForRequestedQuantity']/span[@class='tpl-label']/input")
  private static WebElement requestedQuantityExplanation = null;

  @FindBy(how = How.XPATH, using = "//li[@id='reasonForRequestedQuantity']/span[@class='tpl-source']/span[2]")
  private static WebElement requestedQuantityExplanationSource = null;

  @FindBy(how = How.XPATH, using = "//li[@id='packsToShip']/span[@class='tpl-label']/input")
  private static WebElement packsToShip = null;

  @FindBy(how = How.XPATH, using = "//li[@id='packsToShip']/span[@class='tpl-source']/span[2]")
  private static WebElement packsToShipSource = null;

  @FindBy(how = How.XPATH, using = "//li[@id='price']/span[@class='tpl-label']/input")
  private static WebElement pricePerPack = null;

  @FindBy(how = How.XPATH, using = "//li[@id='price']/span[@class='tpl-source']/span[2]")
  private static WebElement pricePerPackSource = null;

  @FindBy(how = How.XPATH, using = "//li[@id='cost']/span[@class='tpl-label']/input")
  private static WebElement totalCost = null;

  @FindBy(how = How.XPATH, using = "//li[@id='cost']/span[@class='tpl-source']/span[2]")
  private static WebElement totalCostSource = null;

  @FindBy(how = How.XPATH, using = "//li[@id='remarks']/span[@class='tpl-label']/input")
  private static WebElement remarks = null;

  @FindBy(how = How.XPATH, using = "//li[@id='remarks']/span[@class='tpl-source']/span[2]")
  private static WebElement remarksSource = null;

  @FindBy(how = How.XPATH, using = "//li[@id='quantityApproved']/span[@class='tpl-label']/input")
  private static WebElement approvedQuantity = null;

  @FindBy(how = How.XPATH, using = "//li[@id='quantityApproved']/span[@class='tpl-source']/span[2]")
  private static WebElement approvedQuantitySource = null;

  @FindBy(how = How.XPATH, using = "//a[contains(text(),'Turn OFF')]")
  private static WebElement turnOffButton = null;

  @FindBy(how = How.XPATH, using = "//a[contains(text(),'Turn ON')]")
  private static WebElement turnOnButton = null;

  @FindBy(how = How.XPATH, using = "//div[@ng-show='arithmeticValidationMessageShown']/div/div/strong")
  private static WebElement OffOnIndicator = null;

  @FindBy(how = How.XPATH, using = "//div[@id='saveErrorMsgDiv']")
  private static WebElement errorMessageDiv = null;

  @FindBy(how = How.XPATH, using = "//li[@id='skipped']/span[1]/input")
  private static WebElement skipCheckBox = null;

  @FindBy(how = How.XPATH, using = "//li[@id='skipped']/span[3]/input")
  private static WebElement skipTextBox = null;

  @FindBy(how = How.XPATH, using = "//*[@id='newPatientCount']/span[5]/span/select/option[1]")
  private static WebElement patientOption1 = null;

  @FindBy(how = How.XPATH, using = "//*[@id='newPatientCount']/span[5]/span/select/option[2]")
  private static WebElement patientOption2 = null;

  private static String USER_INPUT = "User Input";
  private static String CALCULATED = "Calculated";


  public TemplateConfigPage(TestWebDriver driver) {
    super(driver);
    PageFactory.initElements(new AjaxElementLocatorFactory(TestWebDriver.getDriver(), 10), this);
    testWebDriver.setImplicitWait(10);
  }

  public void verifySourceForTotalConsumedQuantity(String optionToBeVerified) {
    Select select = new Select(dropDownTotalConsumedQuantity);
    assertEquals(select.getFirstSelectedOption().getText(), optionToBeVerified);
  }

  public void verifySourceForStockOnHand(String optionToBeVerified) {
    Select select = new Select(stockInHandDropDown);
    assertEquals(select.getFirstSelectedOption().getText(), optionToBeVerified);
  }

  public void clickTotalConsumedQuantity() {
    clickCheckBox(totalConsumedQuantityCheckBox);
  }

  public void unClickTotalConsumedQuantity() {
    unClickCheckBox(totalConsumedQuantityCheckBox);
  }

  public void clickRequestedQuantity() {
    clickCheckBox(requestedQuantityCheckBox);
  }

  public void unClickRequestedQuantity() {
    unClickCheckBox(requestedQuantityCheckBox);
  }

  public void clickRequestedQuantityExplanation() {
    clickCheckBox(requestedQuantityExplanationCheckBox);
  }

  public void unClickRequestedQuantityExplanation() {
    unClickCheckBox(requestedQuantityExplanationCheckBox);
  }

  public void unClickExpirationDate() {
    unClickCheckBox(expirationDateCheckBox);
  }

  public void unClickTotal() {
    unClickCheckBox(totalCheckBox);
  }

  public void clickStockOnHand() {
    clickCheckBox(stockOnHandCheckBox);
  }

  public void unClickStockOnHand() {
    unClickCheckBox(stockOnHandCheckBox);
  }

  public void selectFromTotalConsumedQuantityDropDown(String optionToBeSelected) {
    testWebDriver.waitForElementToAppear(dropDownTotalConsumedQuantity);
    testWebDriver.selectByVisibleText(dropDownTotalConsumedQuantity, optionToBeSelected);
    testWebDriver.sleep(100);
  }

  public void selectFromStockOnHandDropDown(String optionToBeSelected) {
    testWebDriver.waitForElementToAppear(stockInHandDropDown);
    testWebDriver.selectByVisibleText(stockInHandDropDown, optionToBeSelected);
    testWebDriver.sleep(100);
  }

  public HomePage clickSaveButton() {
    testWebDriver.waitForElementToAppear(SaveButton);
    SaveButton.click();
    testWebDriver.sleep(100);
    return PageObjectFactory.getHomePage(testWebDriver);
  }

  public void verifyErrorMessageDivTotalConsumedQuantity(String totalConsumedQuantityError) {
    testWebDriver.waitForElementToAppear(totalConsumedQtyErrorMessage);
    assertTrue("Error message not displaying", totalConsumedQtyErrorMessage.isDisplayed());
    assertTrue("Error message saying '" + totalConsumedQuantityError + "' not displaying", totalConsumedQtyErrorMessage.getText().equals(totalConsumedQuantityError));
  }

  public void verifyErrorMessageDivStockOnHand(String stockOnHandError) {
    testWebDriver.waitForElementToAppear(stockOnHandQtyErrorMessage);
    assertTrue("Error message not displaying", stockOnHandQtyErrorMessage.isDisplayed());
    assertTrue("Error message saying '" + stockOnHandError + "' not displaying", stockOnHandQtyErrorMessage.getText().equals(stockOnHandError));
  }

  public void verifyErrorMessageDivRequestedQuantity(String requestedQuantityError) {
    testWebDriver.waitForElementToAppear(requestedQtyErrorMessage);
    assertTrue("Error message not displaying", requestedQtyErrorMessage.isDisplayed());
    assertTrue("Error message saying '" + requestedQuantityError + "' not displaying", requestedQtyErrorMessage.getText().equals(requestedQuantityError));
  }

  public void verifyErrorMessageRequestedQuantityExplanation(String requestedQuantityExplanation) {
    testWebDriver.waitForElementToAppear(requestedQtyExplanationErrorMessage);
    assertTrue("Error message not displaying", requestedQtyExplanationErrorMessage.isDisplayed());
    assertTrue("Error message saying '" + requestedQuantityExplanation + "' not displaying", requestedQtyExplanationErrorMessage.getText().equals(requestedQuantityExplanation));
  }

  public void verifyErrorMessageDivFooter() {
    testWebDriver.waitForElementToAppear(errorMessageDiv);
    assertTrue("Error message not displaying", errorMessageDiv.isDisplayed());
    String footerErrorMessage = "There are some errors in the form. Please resolve them.";
    assertTrue("Error message saying '" + footerErrorMessage + "' not displaying", errorMessageDiv.getText().equalsIgnoreCase(footerErrorMessage));
  }

  public void verifyTurnOffOnButtonAvailable(String messageToShow) {
    testWebDriver.waitForElementToAppear(turnOffButton);
    assertTrue(messageToShow, turnOffButton.isDisplayed());
  }

  public void verifyTurnOffOnButtonNotAvailable(String messageToShow) {
    testWebDriver.sleep(100);
    assertFalse(messageToShow, turnOffButton.isDisplayed());
  }

  public void clickTurnOffOnButton(WebElement button) {
    testWebDriver.waitForElementToAppear(button);
    button.click();
    testWebDriver.sleep(100);
  }

  public void verifyTextOffOnButton(WebElement button, String textToVerify, String messageToShow) {
    testWebDriver.waitForElementToAppear(button);
    assertTrue(messageToShow, button.getText().equalsIgnoreCase(textToVerify));
  }

  public void verifyONOffIndicatorOnScreen(WebElement indicator, String textToVerify) {
    testWebDriver.waitForElementToAppear(indicator);
    assertEquals(textToVerify, indicator.getText().trim());
  }

  public void verifySaveSuccessDiv() {
    testWebDriver.waitForElementToAppear(saveSuccessMsg);
    assertTrue("Success message should display", saveSuccessMsg.isDisplayed());
  }

  private void verifyCAndEUserInputsAndShouldBeDisplayed() {
    testWebDriver.waitForElementToAppear(SaveButton);
    unClickTotalConsumedQuantity();
    unClickStockOnHand();
    selectFromTotalConsumedQuantityDropDown(USER_INPUT);
    selectFromStockOnHandDropDown(USER_INPUT);
    clickSaveButton();
    verifyErrorMessageDivFooter();
    verifyErrorMessageDivTotalConsumedQuantity("If 'Total Consumed Quantity' is user input then it should be visible");
    verifyErrorMessageDivStockOnHand("If 'Stock on Hand' is user input then it should be visible");
  }

  private void verifyArithmeticValidationOnOff() {
    clickTotalConsumedQuantity();
    clickStockOnHand();
    selectFromTotalConsumedQuantityDropDown(USER_INPUT);
    verifyTurnOffOnButtonAvailable("Option to choose to switch Arithmetic Validation ON/OFF is not available");
    clickTurnOffOnButton(turnOffButton);

    verifyTextOffOnButton(turnOnButton, "Turn On", "Should show 'Turn ON' on button");
    verifyONOffIndicatorOnScreen(OffOnIndicator, "OFF");
    clickTurnOffOnButton(turnOnButton);

    verifyTextOffOnButton(turnOffButton, "Turn OFF", "Should show 'Turn OFF' on button");
    verifyONOffIndicatorOnScreen(OffOnIndicator, "ON");
    selectFromStockOnHandDropDown(CALCULATED);
    verifyTurnOffOnButtonNotAvailable("Option to choose to switch Arithmetic Validation ON/OFF should not be visible");
  }

  private void verifyCDerivedEMustViceVersa(String program) {
    selectFromTotalConsumedQuantityDropDown(CALCULATED);
    selectFromStockOnHandDropDown(USER_INPUT);
    unClickStockOnHand();
    clickSaveButton();
    verifyErrorMessageDivFooter();
    verifyErrorMessageDivTotalConsumedQuantity("User needs to enter 'Stock on Hand' to calculate 'Total Consumed Quantity'");
    verifyErrorMessageDivStockOnHand("If 'Stock on Hand' is user input then it should be visible");

    clickStockOnHand();
    selectFromTotalConsumedQuantityDropDown(USER_INPUT);
    selectFromStockOnHandDropDown(CALCULATED);
    HomePage homePage = clickSaveButton();
    verifySaveSuccessDiv();
    homePage.selectProgramToConfigTemplate(program);
    unClickTotalConsumedQuantity();
    clickSaveButton();

    verifyErrorMessageDivFooter();
    verifyErrorMessageDivStockOnHand("User needs to enter 'Total Consumed Quantity' to calculate 'Stock on Hand'");
    verifyErrorMessageDivTotalConsumedQuantity("If 'Total Consumed Quantity' is user input then it should be visible");
  }

  public void verifyArithmeticValidations(String program) {
    verifyCAndEUserInputsAndShouldBeDisplayed();
    verifyArithmeticValidationOnOff();
    verifyCDerivedEMustViceVersa(program);
  }

  private void prepareDataForBusinessRuleCE() {
    clickTotalConsumedQuantity();
    clickStockOnHand();
    selectFromTotalConsumedQuantityDropDown(CALCULATED);
    selectFromStockOnHandDropDown(CALCULATED);
    clickSaveButton();
  }

  private void verifyBusinessRuleCE() {
    String independentFieldsCalculatedError = "Interdependent fields ('Total Consumed Quantity', 'Stock on Hand') cannot be of type Calculated at the same time";
    verifyErrorMessageDivStockOnHand(independentFieldsCalculatedError);
    verifyErrorMessageDivTotalConsumedQuantity(independentFieldsCalculatedError);
    verifyErrorMessageDivFooter();
  }

  private void prepareDataForBusinessRuleWJ() {
    clickRequestedQuantity();
    unClickRequestedQuantityExplanation();
    clickSaveButton();
  }

  private void prepareDataForBusinessRuleJW() {
    unClickRequestedQuantity();
    clickRequestedQuantityExplanation();
    clickSaveButton();
  }

  private void verifyBusinessRuleJW() {
    verifyErrorMessageDivRequestedQuantity("If 'Requested Quantity' is displayed, then 'Requested Quantity Explanation' must also be displayed");
    verifyErrorMessageDivFooter();
  }

  private void verifyBusinessRuleWJ() {
    verifyErrorMessageRequestedQuantityExplanation("If 'Requested Quantity Explanation' is displayed, then 'Requested Quantity' must also be displayed");
    verifyErrorMessageDivFooter();
  }

  public void verifyBusinessRules() {
    prepareDataForBusinessRuleCE();
    verifyBusinessRuleCE();
    prepareDataForBusinessRuleWJ();
    verifyBusinessRuleWJ();
    prepareDataForBusinessRuleJW();
    verifyBusinessRuleJW();
  }

  public void verifyColumnLabels() {
    testWebDriver.waitForElementToAppear(SaveButton);
    assertTrue("product code is not showing up", productCode.isDisplayed());
    assertTrue("product name is not showing up", productName.isDisplayed());
    assertTrue("unit of issue is not showing up", unitOfIssue.isDisplayed());
    assertTrue("beginning balance is not showing up", beginningBalance.isDisplayed());
    assertTrue("totalReceivedQuantity is not showing up", totalReceivedQuantity.isDisplayed());
    assertTrue("totalConsumedQuantity is not showing up", totalConsumedQuantity.isDisplayed());
    assertTrue("lossesAndAdj is not showing up", lossesAndAdj.isDisplayed());
    assertTrue("stockOnHand is not showing up", stockOnHand.isDisplayed());
    assertTrue("newPatients is not showing up", newPatients.isDisplayed());
    assertTrue("stockOutDays is not showing up", stockOutDays.isDisplayed());
    assertTrue("adjustedTotalConsumption is not showing up", adjustedTotalConsumption.isDisplayed());
    assertTrue("maxStockQuantity is not showing up", maxStockQuantity.isDisplayed());
    assertTrue("calculatedOrderQuantity is not showing up", calculatedOrderQuantity.isDisplayed());
    assertTrue("requestedQuantity is not showing up", requestedQuantity.isDisplayed());
    assertTrue("requestedQuantityExplanation is not showing up", requestedQuantityExplanation.isDisplayed());
    assertTrue("packsToShip is not showing up", packsToShip.isDisplayed());
    assertTrue("pricePerPack is not showing up", pricePerPack.isDisplayed());
    assertTrue("totalCost is not showing up", totalCost.isDisplayed());
    assertTrue("remarks is not showing up", remarks.isDisplayed());
    assertTrue("approvedQuantity is not showing up", approvedQuantity.isDisplayed());
    assertTrue("Expiration Date is not showing up", expirationDateTextBox.isDisplayed());
    assertTrue("Total is not showing up", totalTextBox.isDisplayed());
  }

  public void verifyColumnSource() {
    String REFERENCE_DATA = "Reference Data";
    testWebDriver.waitForElementToAppear(SaveButton);
    assertEquals(productCodeSource.getText().trim(), REFERENCE_DATA);
    assertEquals(productNameSource.getText().trim(), REFERENCE_DATA);
    assertEquals(unitOfIssueSource.getText().trim(), REFERENCE_DATA);
    assertEquals(beginningBalanceSource.getText().trim(), USER_INPUT);
    assertEquals(totalReceivedQuantitySource.getText().trim(), USER_INPUT);
    assertEquals(totalConsumedQuantitySource.getText().trim(), USER_INPUT);
    assertEquals(lossesAndAdjSource.getText().trim(), USER_INPUT);
    assertEquals(stockOnHandSource.getText().trim(), USER_INPUT);
    assertEquals(newPatientsSource.getText().trim(), USER_INPUT);
    assertEquals(stockOutDaysSource.getText().trim(), USER_INPUT);
    assertEquals(adjustedTotalConsumptionSource.getText().trim(), CALCULATED);
    assertEquals(maxStockQuantitySource.getText().trim(), CALCULATED);
    assertEquals(calculatedOrderQuantitySource.getText().trim(), CALCULATED);
    assertEquals(requestedQuantitySource.getText().trim(), USER_INPUT);
    assertEquals(requestedQuantityExplanationSource.getText().trim(), USER_INPUT);
    assertEquals(packsToShipSource.getText().trim(), CALCULATED);
    assertEquals(pricePerPackSource.getText().trim(), REFERENCE_DATA);
    assertEquals(totalCostSource.getText().trim(), CALCULATED);
    assertEquals(remarksSource.getText().trim(), USER_INPUT);
    assertEquals(approvedQuantitySource.getText().trim(), USER_INPUT);
    assertEquals(expirationDateSource.getText().trim(), USER_INPUT);
    assertEquals(totalSource.getText().trim(), CALCULATED);
  }

  private void verifyMandatoryColumnsEditable(WebElement mandatoryElement) {
    testWebDriver.waitForElementToAppear(mandatoryElement);
    assertTrue("Mandatory columns should be non-editable", mandatoryElement.getAttribute("disabled").trim().equalsIgnoreCase("true"));
  }

  public void verifyMandatoryColumns() {
    verifyMandatoryColumnsEditable(productNameCheckBox);
  }

  public void configureTemplate() {
    testWebDriver.waitForElementToAppear(SaveButton);
    verifySourceForTotalConsumedQuantity(USER_INPUT);
    verifySourceForStockOnHand(USER_INPUT);
    testWebDriver.selectByVisibleText(stockInHandDropDown, CALCULATED);
    testWebDriver.sleep(1500);
    SaveButton.click();
    testWebDriver.sleep(2000);
    verifySuccessDiv();
  }

  public void alterBeginningBalanceLabel(String columnHeadingToBeAltered) {
    testWebDriver.waitForElementToAppear(SaveButton);
    beginningBalance.clear();
    beginningBalance.sendKeys(columnHeadingToBeAltered);
  }

  public void saveConfiguration() {
    clickSaveButton();
    verifySuccessDiv();
  }

  private void verifySuccessDiv() {
    String saveSuccessfullyMessage = "Template saved successfully!";
    assertTrue("'" + saveSuccessfullyMessage + "' div not showing up", saveSuccessMsg.isDisplayed());
    assertTrue("'" + saveSuccessfullyMessage + "' not showing up", saveSuccessMsg.getText().equals(saveSuccessfullyMessage));
  }

  private void clickCheckBox(WebElement chkBox) {
    testWebDriver.waitForElementToAppear(chkBox);
    if (!chkBox.isSelected())
      chkBox.click();
    testWebDriver.sleep(100);
  }

  private void unClickCheckBox(WebElement chkBox) {
    testWebDriver.waitForElementToAppear(chkBox);
    if (chkBox.isSelected())
      chkBox.click();
    testWebDriver.sleep(100);
  }

  public void checkSkip() {
    clickCheckBox(skipCheckBox);
  }

  public void setSkipTextBox(String text) {
    sendKeys(skipTextBox, text);
  }

  public boolean isPatientOption1Selected() {
    testWebDriver.waitForElementToAppear(patientOption1);
    return patientOption1.isSelected();
  }

  public boolean isPatientOption2Selected() {
    testWebDriver.waitForElementToAppear(patientOption2);
    return patientOption2.isSelected();
  }

  public void selectPatientOption2() {
    testWebDriver.sleep(1000);
    testWebDriver.waitForElementToAppear(patientOption2);
    patientOption2.click();
    testWebDriver.sleep(2000);
    assertTrue(isPatientOption2Selected());
  }
}