/*
 * Copyright © 2013 VillageReach. All Rights Reserved. This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 *
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package org.openlmis.pageobjects;

import com.thoughtworks.selenium.SeleneseTestNgHelper;
import org.openlmis.UiUtils.TestWebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.pagefactory.AjaxElementLocatorFactory;

import static org.openqa.selenium.support.How.ID;
import static org.openqa.selenium.support.How.NAME;
import static org.openqa.selenium.support.How.XPATH;

public class RefrigeratorPage extends Page {

  @FindBy(how = XPATH, using = "//a[contains(text(),'Add New')]")
  private static WebElement addNewButton;

  @FindBy(how = XPATH, using = "//a[contains(text(),'Edit')]")
  public static WebElement editButton;

  @FindBy(how = XPATH, using = "//a[contains(text(),'Delete')]")
  private static WebElement deleteButton;

  @FindBy(how = XPATH, using = "//a[contains(text(),'Done')]")
  private static WebElement doneButton;

  @FindBy(how = XPATH, using = "//span[contains(text(),'Refrigerators')]")
  private static WebElement refrigeratorTab;

  @FindBy(how = NAME, using = "temperature0")
  public static WebElement refrigeratorTemperatureTextField;

  @FindBy(how = NAME, using = "lowAlarmEvent0")
  private static WebElement lowAlarmEventsTextField;

  @FindBy(how = NAME, using = "highAlarmEvent0")
  private static WebElement highAlarmEventsTextField;

  @FindBy(how = ID, using = "temperature0")
  private static WebElement refrigeratorTemperatureNR;

  @FindBy(how = ID, using = "functioningCorrectlyYes0")
  private static WebElement functioningCorrectlyYesRadio;

  @FindBy(how = ID, using = "functioningCorrectlyNo0")
  private static WebElement functioningCorrectlyNoRadio;

  @FindBy(how = ID, using = "functioningCorrectlyDontKnow0")
  private static WebElement functioningCorrectlyDontKnowRadio;

  @FindBy(how = ID, using = "functioningCorrectlyNR0")
  private static WebElement functioningCorrectlyNR;

  @FindBy(how = ID, using = "lowAlarmEventNR0")
  private static WebElement lowAlarmEventNR;

  @FindBy(how = ID, using = "highAlarmEventNR0")
  private static WebElement highAlarmEventNR;

  @FindBy(how = ID, using = "problemSinceLastVisitYes0")
  private static WebElement problemSinceLastVisitYesRadio;

  @FindBy(how = ID, using = "problemSinceLastVisitNo0")
  private static WebElement problemSinceLastVisitNoRadio;

  @FindBy(how = ID, using = "problemSinceLastVisitDontKnow0")
  private static WebElement problemSinceLastVisitDontKnowRadio;

  @FindBy(how = ID, using = "problemSinceLastVisitNR0")
  private static WebElement problemSinceLastVisitNR;

  @FindBy(how = ID, using = "operatorError0")
  private static WebElement operatorError;

  @FindBy(how = ID, using = "burnerProblem0")
  private static WebElement burnerProblem;

  @FindBy(how = ID, using = "gasLeakage0")
  private static WebElement gasLeakage;

  @FindBy(how = ID, using = "gasFault0")
  private static WebElement gasFault;

  @FindBy(how = ID, using = "other0")
  private static WebElement other;

  @FindBy(how = ID, using = "otherTextbox")
  private static WebElement otherTextBox;

  @FindBy(how = ID, using = "notes")
  private static WebElement notesTextArea;

  @FindBy(how = XPATH, using = "//h3/span[contains(text(),'Refrigerators')]")
  private static WebElement refrigeratorsHeader;

  @FindBy(how = ID, using = "brand")
  private static WebElement brandTextField;

  @FindBy(how = ID, using = "model")
  private static WebElement modelTextField;

  @FindBy(how = ID, using = "manufacturerSerialNumber")
  private static WebElement manufacturerSerialNumberTextField;

  @FindBy(how = ID, using = "done-button")
  private static WebElement doneButtonOnModal;

  @FindBy(how = ID, using = "button-cancel")
  private static WebElement cancelButtonOnModal;

  @FindBy(how = XPATH, using = "//h3[contains(text(),'New Refrigerator')]")
  public static WebElement newRefrigeratorHeaderOnModal;

  @FindBy(how = XPATH, using = "//a[contains(text(),'Next »')]")
  private static WebElement nextLink;

  @FindBy(how = XPATH, using = "//a[contains(text(),'« Previous')]")
  private static WebElement previousLink;

  @FindBy(how = XPATH, using = "//div[@id='saveSuccessMsgDiv']")
  private static WebElement saveSuccessMessageDiv;

  @FindBy(how = XPATH, using = "//a[contains(text(),'OK')]")
  public static WebElement OKButton;

  @FindBy(how = XPATH, using = "//h3[contains(text(),'Delete Refrigerator')]")
  public static WebElement deletePopUpHeader;


  public RefrigeratorPage(TestWebDriver driver) {
    super(driver);
    PageFactory.initElements(new AjaxElementLocatorFactory(TestWebDriver.getDriver(), 10), this);
    testWebDriver.setImplicitWait(10);
  }

  public void enterValueInRefrigeratorTemperature(String value) {
    testWebDriver.waitForElementToAppear(refrigeratorTemperatureTextField);
    sendKeys(refrigeratorTemperatureTextField, value);
  }

  public void clickProblemSinceLastVisitYesRadio() {
    testWebDriver.waitForElementToAppear(problemSinceLastVisitYesRadio);
    problemSinceLastVisitYesRadio.click();
  }

  public void clickOKButton() {
    testWebDriver.waitForElementToAppear(OKButton);
    OKButton.click();
  }

  public void clickProblemSinceLastVisitNoRadio() {
    testWebDriver.waitForElementToAppear(problemSinceLastVisitNoRadio);
    problemSinceLastVisitNoRadio.click();
  }

  public void clickProblemSinceLastVisitDontKnowRadio() {
    testWebDriver.waitForElementToAppear(problemSinceLastVisitDontKnowRadio);
    problemSinceLastVisitDontKnowRadio.click();
  }

  public void clickProblemSinceLastVisitNR() {
    testWebDriver.waitForElementToAppear(problemSinceLastVisitNR);
    problemSinceLastVisitNR.click();
  }

  public void clickOperatorError() {
    testWebDriver.waitForElementToAppear(operatorError);
    operatorError.click();
  }

  public void clickBurnerProblem() {
    testWebDriver.waitForElementToAppear(burnerProblem);
    burnerProblem.click();
  }

  public void clickGasLeakage() {
    testWebDriver.waitForElementToAppear(gasLeakage);
    gasLeakage.click();
  }

  public void clickGasFault() {
    testWebDriver.waitForElementToAppear(gasFault);
    gasFault.click();
  }

  public void clickOther() {
    testWebDriver.waitForElementToAppear(other);
    other.click();
  }

  public void enterValueInLowAlarmEvents(String value) {
    testWebDriver.waitForElementToAppear(lowAlarmEventsTextField);
    sendKeys(lowAlarmEventsTextField, value);
  }

  public void enterValueInBrandModal(String value) {
    testWebDriver.waitForElementToAppear(brandTextField);
    sendKeys(brandTextField, value);
  }

  public void enterValueInModelModal(String value) {
    testWebDriver.waitForElementToAppear(modelTextField);
    sendKeys(modelTextField, value);
  }

  public void enterValueInManufacturingSerialNumberModal(String value) {
    testWebDriver.waitForElementToAppear(manufacturerSerialNumberTextField);
    sendKeys(manufacturerSerialNumberTextField, value);
  }

  public void enterValueInHighAlarmEvents(String value) {
    testWebDriver.waitForElementToAppear(highAlarmEventsTextField);
    sendKeys(highAlarmEventsTextField, value);
  }

  public void enterValueInOtherTextBox(String value) {
    testWebDriver.waitForElementToAppear(otherTextBox);
    sendKeys(otherTextBox, value);
  }

  public void enterValueInNotesTextArea(String value) {
    testWebDriver.waitForElementToAppear(notesTextArea);
    sendKeys(notesTextArea, value);
  }

  public void clickRefrigeratorTemperatureNR() {
    testWebDriver.waitForElementToAppear(refrigeratorTemperatureNR);
    refrigeratorTemperatureNR.click();
  }

  public void clickDoneOnModal() {
    testWebDriver.waitForElementToAppear(doneButtonOnModal);
    doneButtonOnModal.click();
    testWebDriver.waitForElementToAppear(testWebDriver.getElementByXpath("(//a[contains(text(),'Edit')])[1]"));
  }

  public void clickCancelOnModal() {
    testWebDriver.waitForElementToAppear(cancelButtonOnModal);
    cancelButtonOnModal.click();
  }

  public void clickLowAlarmEventNR() {
    testWebDriver.waitForElementToAppear(lowAlarmEventNR);
    lowAlarmEventNR.click();
  }

  public void clickHighAlarmEventNR() {
    testWebDriver.waitForElementToAppear(highAlarmEventNR);
    highAlarmEventNR.click();
  }

  public void clickFunctioningCorrectlyYesRadio() {
    testWebDriver.waitForElementToAppear(functioningCorrectlyYesRadio);
    functioningCorrectlyYesRadio.click();
  }

  public void clickFunctioningCorrectlyNoRadio() {
    testWebDriver.waitForElementToAppear(functioningCorrectlyNoRadio);
    functioningCorrectlyNoRadio.click();
  }

  public void clickFunctioningCorrectlyDontKnowRadio() {
    testWebDriver.waitForElementToAppear(functioningCorrectlyDontKnowRadio);
    functioningCorrectlyDontKnowRadio.click();
  }

  public void clickFunctioningCorrectlyNR() {
    testWebDriver.waitForElementToAppear(functioningCorrectlyNR);
    functioningCorrectlyNR.click();
  }

  public void clickAddNew() {
    testWebDriver.waitForElementToAppear(addNewButton);
    addNewButton.click();
    testWebDriver.waitForElementToAppear(newRefrigeratorHeaderOnModal);
  }

  public void clickEdit() {
    testWebDriver.waitForElementToAppear(editButton);
    editButton.click();
    testWebDriver.waitForElementToAppear(refrigeratorTemperatureNR);
  }

  public void clickDelete() {
    testWebDriver.waitForElementToAppear(deleteButton);
    deleteButton.click();
  }

  public String getRefrigeratorTemperateTextFieldValue() {
    testWebDriver.waitForElementToAppear(refrigeratorTemperatureTextField);
    return testWebDriver.getAttribute(refrigeratorTemperatureTextField, "value");
  }

  public String getLowAlarmEventsTextFieldValue() {
    testWebDriver.waitForElementToAppear(lowAlarmEventsTextField);
    return testWebDriver.getAttribute(lowAlarmEventsTextField, "value");
  }

  public String getHighAlarmEventsTextFieldValue() {
    testWebDriver.waitForElementToAppear(highAlarmEventsTextField);
    return testWebDriver.getAttribute(highAlarmEventsTextField, "value");
  }

  public String getNotesTextAreaValue() {
    testWebDriver.waitForElementToAppear(notesTextArea);
    return testWebDriver.getAttribute(notesTextArea, "value");
  }



  public void onRefrigeratorScreen() {
    testWebDriver.sleep(500);
    testWebDriver.waitForElementToAppear(refrigeratorsHeader);
    SeleneseTestNgHelper.assertTrue("Refrigerator header should show up", refrigeratorsHeader.isDisplayed());
  }

  public void clickDone() {
    testWebDriver.waitForElementToAppear(doneButton);
    doneButton.click();
    testWebDriver.sleep(500);
  }

  public void addNewRefrigerator(String brand, String model, String manufacturerSerialNumber) {
    enterValueInBrandModal(brand);
    enterValueInModelModal(model);
    enterValueInManufacturingSerialNumberModal(manufacturerSerialNumber);
    clickDoneOnModal();
  }

  public void verifySuccessMessage(String message) {
    testWebDriver.waitForElementToAppear(saveSuccessMessageDiv);
    SeleneseTestNgHelper.assertEquals(saveSuccessMessageDiv.getText(), message);
  }


}