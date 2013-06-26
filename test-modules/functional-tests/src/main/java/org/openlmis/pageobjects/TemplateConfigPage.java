/*
 * Copyright © 2013 VillageReach.  All Rights Reserved.  This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 *
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package org.openlmis.pageobjects;


import com.thoughtworks.selenium.SeleneseTestNgHelper;
import org.openlmis.UiUtils.TestWebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.How;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.pagefactory.AjaxElementLocatorFactory;
import org.openqa.selenium.support.ui.Select;

import java.io.IOException;

import static com.thoughtworks.selenium.SeleneseTestBase.assertTrue;
import static com.thoughtworks.selenium.SeleneseTestNgHelper.assertEquals;


public class TemplateConfigPage extends Page {


  @FindBy(how = How.LINK_TEXT, using = "Logout")
  private static WebElement logoutLink;

  @FindBy(how = How.XPATH, using = "//input[@value='Save']")
  private static WebElement SaveButton;

  @FindBy(how = How.XPATH, using = "//div[@id='saveSuccessMsgDiv' and @ng-show='message']")
  private static WebElement saveSuccessMsg;

  @FindBy(how = How.XPATH, using = "//div[@id='saveSuccessMsgDiv' and @ng-show='error']")
  private static WebElement saveErrorMsgDiv;

  @FindBy(how = How.XPATH, using = "//li[@id='E']/span[@class='tpl-source']/span/select")
  private static WebElement stockInHandDropDown;

  @FindBy(how = How.XPATH, using = "//li[@id='A']/span/span/span/span/input[@type='checkbox']")
  private static WebElement checkboxBeginningBalance;

  @FindBy(how = How.XPATH, using = "//li[@id='C']/span[@class='tpl-source']/span/select")
  private static WebElement dropDownTotalConsumedQuantity;

  @FindBy(how = How.XPATH, using = "//li[@id='W']/span[@class='tpl-error']/div")
  private static WebElement requestedQtyExplanationErrorMessage;

  @FindBy(how = How.XPATH, using = "//li[@id='J']/span[@class='tpl-error']/div")
  private static WebElement requestedQtyErrorMessage;

  @FindBy(how = How.XPATH, using = "//li[@id='C']/span[@class='tpl-error']/div")
  private static WebElement totalConsumedQtyErrorMessage;

  @FindBy(how = How.XPATH, using = "//li[@id='E']/span[@class='tpl-error']/div")
  private static WebElement stockOnHandQtyErrorMessage;

  @FindBy(how = How.XPATH, using = "//li[@id='O']/span[@class='tpl-label']/input")
  private static WebElement productCode;

  @FindBy(how = How.XPATH, using = "//li[@id='O']/span[@class='tpl-source']/span[2]")
  private static WebElement productCodeSource;

  @FindBy(how = How.XPATH, using = "//li[@id='R']/span[@class='tpl-label']/input")
  private static WebElement productName;

  @FindBy(how = How.XPATH, using = "//li[@id='R']/span[@class='tpl-source']/span[2]")
  private static WebElement productNameSource;

  @FindBy(how = How.XPATH, using = "//li[@id='U']/span[@class='tpl-label']/input")
  private static WebElement unitOfIssue;

  @FindBy(how = How.XPATH, using = "//li[@id='U']/span[@class='tpl-source']/span[2]")
  private static WebElement unitOfIssueSource;

  @FindBy(how = How.XPATH, using = "//li[@id='A']/span[@class='tpl-label']/input")
  private static WebElement beginningBalance;

  @FindBy(how = How.XPATH, using = "//li[@id='A']/span[@class='tpl-source']/span[2]")
  private static WebElement beginningBalanceSource;

  @FindBy(how = How.XPATH, using = "//li[@id='B']/span[@class='tpl-label']/input")
  private static WebElement totalReceivedQuantity;

  @FindBy(how = How.XPATH, using = "//li[@id='B']/span[@class='tpl-source']/span[2]")
  private static WebElement totalReceivedQuantitySource;

  @FindBy(how = How.XPATH, using = "//li[@id='C']/span[@class='tpl-label']/input")
  private static WebElement totalConsumedQuantity;

  @FindBy(how = How.XPATH, using = "//li[@id='C']/span[@class='tpl-source']/span/select/option[@selected='selected']")
  private static WebElement totalConsumedQuantitySource;

  @FindBy(how = How.XPATH, using = "//li[@id='C']/span/input")
  private static WebElement totalConsumedQuantityCheckBox;

  @FindBy(how = How.XPATH, using = "//li[@id='W']/span/input")
  private static WebElement requestedQuantityCheckBox;

  @FindBy(how = How.XPATH, using = "//li[@id='G']/span/input")
  private static WebElement expirationDateCheckBox;

  @FindBy(how = How.XPATH, using = "//li[@id='G']/span[@class='tpl-label']/input")
  private static WebElement expirationDateTextBox;

  @FindBy(how = How.XPATH, using = "//li[@id='G']/span[@class='tpl-source']/span[2]")
  private static WebElement expirationDateSource;

  @FindBy(how = How.XPATH, using = "//li[@id='J']/span/input")
  private static WebElement requestedQuantityExplanationCheckBox;

  @FindBy(how = How.XPATH, using = "//li[@id='Y']/span/input")
  private static WebElement totalCheckBox;

  @FindBy(how = How.XPATH, using = "//li[@id='Y']/span[@class='tpl-label']/input")
  private static WebElement totalTextBox;

  @FindBy(how = How.XPATH, using = "//li[@id='Y']/span[@class='tpl-source']/span[2]")
  private static WebElement totalSource;

  @FindBy(how = How.XPATH, using = "//li[@id='O']/span/input")
  private static WebElement productCodeCheckBox;

  @FindBy(how = How.XPATH, using = "//li[@id='R']/span/input")
  private static WebElement productNameCheckBox;

  @FindBy(how = How.XPATH, using = "//li[@id='L']/span[@class='tpl-visible']/input")
  private static WebElement remarksCheckBox;

  @FindBy(how = How.XPATH, using = "//li[@id='D']/span[@class='tpl-label']/input")
  private static WebElement lossesAndAdj;

  @FindBy(how = How.XPATH, using = "//li[@id='D']/span[@class='tpl-source']/span[2]")
  private static WebElement lossesAndAdjSource;

  @FindBy(how = How.XPATH, using = "//li[@id='E']/span[@class='tpl-label']/input")
  private static WebElement stockOnHand;

  @FindBy(how = How.XPATH, using = "//li[@id='E']/span[@class='tpl-source']/span/select/option[@selected='selected']")
  private static WebElement stockOnHandSource;

  @FindBy(how = How.XPATH, using = "//li[@id='E']/span/input")
  private static WebElement stockOnHandCheckBox;

  @FindBy(how = How.XPATH, using = "//li[@id='F']/span[@class='tpl-label']/input")
  private static WebElement newPatients;

  @FindBy(how = How.XPATH, using = "//li[@id='F']/span[@class='tpl-source']/span[2]")
  private static WebElement newPatientsSource;

  @FindBy(how = How.XPATH, using = "//li[@id='X']/span[@class='tpl-label']/input")
  private static WebElement stockOutDays;

  @FindBy(how = How.XPATH, using = "//li[@id='X']/span[@class='tpl-source']/span[2]")
  private static WebElement stockOutDaysSource;

  @FindBy(how = How.XPATH, using = "//li[@id='N']/span[@class='tpl-label']/input")
  private static WebElement adjustedTotalConsumption;

  @FindBy(how = How.XPATH, using = "//li[@id='N']/span[@class='tpl-source']/span[2]")
  private static WebElement adjustedTotalConsumptionSource;

  @FindBy(how = How.XPATH, using = "//li[@id='P']/span[@class='tpl-label']/input")
  private static WebElement AMC;

  @FindBy(how = How.XPATH, using = "//li[@id='P']/span[@class='tpl-source']/span[2]")
  private static WebElement AMCSource;

  @FindBy(how = How.XPATH, using = "//li[@id='H']/span[@class='tpl-label']/input")
  private static WebElement maxStockQuantity;

  @FindBy(how = How.XPATH, using = "//li[@id='H']/span[@class='tpl-source']/span[2]")
  private static WebElement maxStockQuantitySource;

  @FindBy(how = How.XPATH, using = "//li[@id='I']/span[@class='tpl-label']/input")
  private static WebElement calculatedOrderQuantity;

  @FindBy(how = How.XPATH, using = "//li[@id='I']/span[@class='tpl-source']/span[2]")
  private static WebElement calculatedOrderQuantitySource;

  @FindBy(how = How.XPATH, using = "//li[@id='J']/span[@class='tpl-label']/input")
  private static WebElement requestedQuantity;

  @FindBy(how = How.XPATH, using = "//li[@id='J']/span[@class='tpl-source']/span[2]")
  private static WebElement requestedQuantitySource;

  @FindBy(how = How.XPATH, using = "//li[@id='W']/span[@class='tpl-label']/input")
  private static WebElement requestedQuantityExplanation;

  @FindBy(how = How.XPATH, using = "//li[@id='W']/span[@class='tpl-source']/span[2]")
  private static WebElement requestedQuantityExplanationSource;

  @FindBy(how = How.XPATH, using = "//li[@id='V']/span[@class='tpl-label']/input")
  private static WebElement packsToShip;

  @FindBy(how = How.XPATH, using = "//li[@id='V']/span[@class='tpl-source']/span[2]")
  private static WebElement packsToShipSource;

  @FindBy(how = How.XPATH, using = "//li[@id='T']/span[@class='tpl-label']/input")
  private static WebElement pricePerPack;

  @FindBy(how = How.XPATH, using = "//li[@id='T']/span[@class='tpl-source']/span[2]")
  private static WebElement pricePerPackSource;

  @FindBy(how = How.XPATH, using = "//li[@id='Q']/span[@class='tpl-label']/input")
  private static WebElement totalCost;

  @FindBy(how = How.XPATH, using = "//li[@id='Q']/span[@class='tpl-source']/span[2]")
  private static WebElement totalCostSource;

  @FindBy(how = How.XPATH, using = "//li[@id='L']/span[@class='tpl-label']/input")
  private static WebElement remarks;

  @FindBy(how = How.XPATH, using = "//li[@id='L']/span[@class='tpl-source']/span[2]")
  private static WebElement remarksSource;

  @FindBy(how = How.XPATH, using = "//li[@id='K']/span[@class='tpl-label']/input")
  private static WebElement approvedQuantity;

  @FindBy(how = How.XPATH, using = "//li[@id='K']/span[@class='tpl-source']/span[2]")
  private static WebElement approvedQuantitySource;

  @FindBy(how = How.XPATH, using = "//a[contains(text(),'Turn OFF')]")
  private static WebElement turnOffButton;

  @FindBy(how = How.XPATH, using = "//a[contains(text(),'Turn ON')]")
  private static WebElement turnOnButton;

  @FindBy(how = How.XPATH, using = "//div[@ng-show='arithmeticValidationMessageShown']/div/div/strong")
  private static WebElement OffOnIndicator;

  @FindBy(how = How.XPATH, using = "//div[@id='saveErrorMsgDiv']")
  private static WebElement errorMessageDiv;

  private static String TEMPLATE_SUCCESS_MESSAGE = "Template saved successfully!";

  private static String USER_INPUT = "User Input";
  private static String CALCULATED = "Calculated";
  private static String REFERENCE_DATA = "Reference Data";

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
      unClickCheckBox(totalConsumedQuantityCheckBox) ;
  }

  public void clickRequestedQuantity() {
      clickCheckBox(requestedQuantityCheckBox);
  }

  public void unClickRequestedQuantity() {
      unClickCheckBox(requestedQuantityCheckBox) ;
  }

  public void clickRequestedQuantityExplanation() {
      clickCheckBox(requestedQuantityExplanationCheckBox);
  }

  public void unClickRequestedQuantityExplanation() {
      unClickCheckBox(requestedQuantityExplanationCheckBox) ;
  }

  public void clickExpirationDate() {
      clickCheckBox(expirationDateCheckBox);
  }

  public void unClickExpirationDate() {
      unClickCheckBox(expirationDateCheckBox) ;
  }

    public void clickTotal() {
        clickCheckBox(totalCheckBox);
    }

    public void unClickTotal() {
        unClickCheckBox(totalCheckBox) ;
    }

    public void clickStockOnHand() {
        clickCheckBox(stockOnHandCheckBox);
    }

    public void unClickStockOnHand() {
        unClickCheckBox(stockOnHandCheckBox) ;
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

  public HomePage clickSaveButton() throws IOException{
    testWebDriver.waitForElementToAppear(SaveButton);
    SaveButton.click();
    testWebDriver.sleep(100);
    return new HomePage(testWebDriver);
  }

  public void verifyErrorMessageDivTotalConsumedQuantity(String totalConsumedQuantityError) {
    testWebDriver.waitForElementToAppear(totalConsumedQtyErrorMessage);
    assertTrue("Error message not displaying", totalConsumedQtyErrorMessage.isDisplayed());
    assertTrue("Error message saying '"+totalConsumedQuantityError+"' not displaying", totalConsumedQtyErrorMessage.getText().equals(totalConsumedQuantityError));
  }

  public void verifyErrorMessageDivStockOnHand(String stockOnHandError) {
    testWebDriver.waitForElementToAppear(stockOnHandQtyErrorMessage);
    assertTrue("Error message not displaying", stockOnHandQtyErrorMessage.isDisplayed());
    assertTrue("Error message saying '"+stockOnHandError+"' not displaying", stockOnHandQtyErrorMessage.getText().equals(stockOnHandError));
  }

  public void verifyErrorMessageDivRequestedQuantity(String requestedQuantityError) {
    testWebDriver.waitForElementToAppear(requestedQtyErrorMessage);
    assertTrue("Error message not displaying", requestedQtyErrorMessage.isDisplayed());
    assertTrue("Error message saying '"+requestedQuantityError+"' not displaying", requestedQtyErrorMessage.getText().equals(requestedQuantityError));
  }

  public void verifyErrorMessageRequestedQuantityExplanation(String requestedQuantityExplanation) {
    testWebDriver.waitForElementToAppear(requestedQtyExplanationErrorMessage);
    assertTrue("Error message not displaying", requestedQtyExplanationErrorMessage.isDisplayed());
    assertTrue("Error message saying '"+requestedQuantityExplanation+"' not displaying", requestedQtyExplanationErrorMessage.getText().equals(requestedQuantityExplanation));
  }

  public void verifyErrorMessageDivFooter() {
    testWebDriver.waitForElementToAppear(errorMessageDiv);
      assertTrue("Error message not displaying", errorMessageDiv.isDisplayed());
    String footerErrorMessage = "There are some errors in the form. Please resolve them.";
    assertTrue("Error message saying '"+footerErrorMessage+"' not displaying", errorMessageDiv.getText().equalsIgnoreCase(footerErrorMessage));
  }

  public void verifyTurnOffOnButtonAvailable(String messageToShow) {
    testWebDriver.waitForElementToAppear(turnOffButton);
    assertTrue(messageToShow, turnOffButton.isDisplayed());

  }

  public void verifyTurnOffOnButtonNotAvailable(String messageToShow) {
    testWebDriver.sleep(100);
    SeleneseTestNgHelper.assertFalse(messageToShow, turnOffButton.isDisplayed());

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

  private void verifyCAndEUserInputsAndShouldBeDisplayed() throws IOException {
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

  private void verifyCDerivedEMustViceVersa(String program) throws IOException {
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

  public void verifyArithmeticValidations(String program) throws IOException {
    verifyCAndEUserInputsAndShouldBeDisplayed();
    verifyArithmeticValidationOnOff();
    verifyCDerivedEMustViceVersa(program);

  }

  private void prepareDataForBusinessRuleCE()throws IOException {
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

  private void prepareDataForBusinessRuleWJ() throws IOException{
    clickRequestedQuantity();
    unClickRequestedQuantityExplanation();
    clickSaveButton();
  }

  private void prepareDataForBusinessRuleJW() throws IOException {
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


  public void verifyBusinessRules() throws IOException {
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

  public void alterBeginningBalanceLabel(String columnHeadingToBeAltered) throws IOException {
    testWebDriver.waitForElementToAppear(SaveButton);
    beginningBalance.clear();
    beginningBalance.sendKeys(columnHeadingToBeAltered);
    clickSaveButton();
    verifySuccessDiv();
  }





  private void verifySuccessDiv() {
    String saveSuccessfullyMessage = "Template saved successfully!";
    assertTrue("'"+saveSuccessfullyMessage+"' div not showing up", saveSuccessMsg.isDisplayed());
    assertTrue("'"+saveSuccessfullyMessage+"' not showing up", saveSuccessMsg.getText().equals(saveSuccessfullyMessage));
  }

    public void clickCheckBox(WebElement chkBox) {
        testWebDriver.waitForElementToAppear(chkBox);
        if (!chkBox.isSelected())
            chkBox.click();
        testWebDriver.sleep(100);
    }

    public void unClickCheckBox(WebElement chkBox) {
        testWebDriver.waitForElementToAppear(chkBox);
        if (chkBox.isSelected())
            chkBox.click();
        testWebDriver.sleep(100);
    }
}