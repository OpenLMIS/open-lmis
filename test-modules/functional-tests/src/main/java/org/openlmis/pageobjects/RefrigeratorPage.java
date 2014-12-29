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

import com.thoughtworks.selenium.SeleneseTestNgHelper;
import org.openlmis.UiUtils.TestWebDriver;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.pagefactory.AjaxElementLocatorFactory;

import java.util.List;
import java.util.Map;

import static com.thoughtworks.selenium.SeleneseTestBase.*;
import static org.openqa.selenium.support.How.*;

public class RefrigeratorPage extends DistributionTab {

  @FindBy(how = ID, using = "refrigeratorsLabel")
  private static WebElement refrigeratorsPageLabel = null;

  @FindBy(how = ID, using = "addNew")
  private static WebElement addNewButton = null;

  @FindBy(how = ID, using = "editReading0")
  public static WebElement showButtonForRefrigerator1 = null;

  @FindBy(how = XPATH, using = "//input[@value='Delete']")
  private static WebElement deleteButton = null;

  @FindBy(how = ID, using = "refrigeratorReadingDone")
  private static WebElement doneButton = null;

  @FindBy(how = XPATH, using = "//span[contains(text(),'Refrigerators')]")
  private static WebElement refrigeratorTab = null;

  @FindBy(how = NAME, using = "temperature0")
  public static WebElement refrigeratorTemperatureTextField = null;

  @FindBy(how = NAME, using = "lowAlarmEvent0")
  private static WebElement lowAlarmEventsTextField = null;

  @FindBy(how = NAME, using = "highAlarmEvent0")
  private static WebElement highAlarmEventsTextField = null;

  @FindBy(how = ID, using = "temperature0")
  private static WebElement refrigeratorTemperatureNR = null;

  @FindBy(how = ID, using = "functioningCorrectlyYes0")
  private static WebElement functioningCorrectlyYesRadio = null;

  @FindBy(how = ID, using = "functioningCorrectlyNo0")
  private static WebElement functioningCorrectlyNoRadio = null;

  @FindBy(how = ID, using = "functioningCorrectlyDontKnow0")
  private static WebElement functioningCorrectlyDoNotKnowRadio = null;

  @FindBy(how = ID, using = "functioningCorrectly0")
  private static WebElement functioningCorrectlyNR = null;

  @FindBy(how = ID, using = "lowAlarmEvent0")
  private static WebElement lowAlarmEventNR = null;

  @FindBy(how = ID, using = "highAlarmEvent0")
  private static WebElement highAlarmEventNR = null;

  @FindBy(how = ID, using = "problemSinceLastVisitYes0")
  private static WebElement problemSinceLastVisitYesRadio = null;

  @FindBy(how = ID, using = "problemSinceLastVisitNo0")
  private static WebElement problemSinceLastVisitNoRadio = null;

  @FindBy(how = ID, using = "problemSinceLastVisitDontKnow0")
  private static WebElement problemSinceLastVisitDoNotKnowRadio = null;

  @FindBy(how = ID, using = "problemSinceLastVisit0")
  private static WebElement problemSinceLastVisitNR = null;

  @FindBy(how = ID, using = "notes0")
  private static WebElement notesTextArea = null;

  @FindBy(how = XPATH, using = "//h3/span[contains(text(),'Refrigerators')]")
  private static WebElement refrigeratorsHeader = null;

  @FindBy(how = ID, using = "brand")
  private static WebElement brandTextField = null;

  @FindBy(how = ID, using = "model")
  private static WebElement modelTextField = null;

  @FindBy(how = ID, using = "manufacturerSerialNumber")
  private static WebElement manufacturerSerialNumberTextField = null;

  @FindBy(how = ID, using = "done-button")
  private static WebElement doneButtonOnModal = null;

  @FindBy(how = XPATH, using = "//h3[contains(text(),'New Refrigerator')]")
  public static WebElement newRefrigeratorHeaderOnModal = null;

  @FindBy(how = XPATH, using = "//a[contains(text(),'OK')]")
  public static WebElement OKButton = null;

  @FindBy(how = XPATH, using = "//h3[contains(text(),'Delete Refrigerator')]")
  public static WebElement deletePopUpHeader = null;

  @FindBy(how = XPATH, using = "//form/div[1]/div[1]/span[@class='status-icon']")
  public static WebElement individualRefrigeratorIcon = null;

  @FindBy(how = XPATH, using = "//ng-include/div/ul/li[2]/a/span[@class='status-icon']")
  public static WebElement overallRefrigeratorIcon = null;

  @FindBy(how = ID, using = "duplicateSerialNumberError")
  public static WebElement duplicateRefrigeratorMessage = null;

  public RefrigeratorPage(TestWebDriver driver) {
    super(driver);
    PageFactory.initElements(new AjaxElementLocatorFactory(TestWebDriver.getDriver(), 10), this);
    testWebDriver.setImplicitWait(10);
  }

  @Override
  public void verifyIndicator(String color) {
    verifyRefrigeratorColor("Overall", color);
  }

  @Override
  public void enterValues(List<Map<String, String>> dataMapList) {
    //To change body of implemented methods use File | Settings | File Templates.
  }

  @Override
  public void verifyData(List<Map<String, String>> map) {
    //To change body of implemented methods use File | Settings | File Templates.
  }

  @Override
  public void navigate() {
    testWebDriver.waitForElementToAppear(refrigeratorTab);
    refrigeratorTab.click();
    removeFocusFromElement();
  }

  public void enterValueInRefrigeratorTemperature(String value, int refrigeratorNumber) {
    WebElement refrigeratorTemperatureTextField = testWebDriver.getElementByName("temperature" + (refrigeratorNumber - 1));
    testWebDriver.waitForElementToAppear(refrigeratorTemperatureTextField);
    sendKeys(refrigeratorTemperatureTextField, value);
    refrigeratorTemperatureTextField.sendKeys(Keys.TAB);
  }

  public void clickProblemSinceLastVisitYesRadio(int refrigeratorNumber) {
    WebElement problemSinceLastVisitYesRadio = testWebDriver.getElementById("problemSinceLastVisitYes" + (refrigeratorNumber - 1));
    testWebDriver.waitForElementToAppear(problemSinceLastVisitYesRadio);
    problemSinceLastVisitYesRadio.click();
    removeFocusFromElement();
  }

  public void selectOtherProblem(int refrigeratorNumber) {
    WebElement problemOther = testWebDriver.getElementById("other" + (refrigeratorNumber - 1));
    testWebDriver.waitForElementToAppear(problemOther);
    problemOther.click();
    removeFocusFromElement();
  }

  public void enterTextInOtherProblemTextBox(String value, int refrigeratorNumber) {
    WebElement problemOtherTextBox = testWebDriver.getElementById("otherTextbox" + (refrigeratorNumber - 1));
    testWebDriver.waitForElementToAppear(problemOtherTextBox);
    sendKeys(problemOtherTextBox, value);
    problemOtherTextBox.sendKeys(Keys.TAB);
  }

  public void selectGasLeakProblem(int refrigeratorNumber) {
    WebElement problemGasLeak = testWebDriver.getElementById("gasLeakage" + (refrigeratorNumber - 1));
    testWebDriver.waitForElementToAppear(problemGasLeak);
    problemGasLeak.click();
    removeFocusFromElement();
  }

  public void selectBurnerProblem(int refrigeratorNumber) {
    WebElement burnerProblem = testWebDriver.getElementById("burnerProblem" + (refrigeratorNumber - 1));
    testWebDriver.waitForElementToAppear(burnerProblem);
    burnerProblem.click();
    removeFocusFromElement();
  }

  public void clickOKButton() {
    testWebDriver.waitForElementToAppear(OKButton);
    OKButton.click();
  }

  public void navigateToRefrigeratorTab() {
    testWebDriver.waitForElementToAppear(refrigeratorTab);
    refrigeratorTab.click();
    removeFocusFromElement();
  }

  @Override
  public void verifyAllFieldsDisabled() {
    assertFalse("refrigeratorTemperatureTextField enabled.", refrigeratorTemperatureTextField.isEnabled());
    assertFalse("refrigeratorTemperatureNR enabled.", refrigeratorTemperatureNR.isEnabled());

    assertFalse("functioningCorrectlyDoNotKnowRadio enabled.", functioningCorrectlyDoNotKnowRadio.isEnabled());
    assertFalse("functioningCorrectlyNoRadio enabled.", functioningCorrectlyNoRadio.isEnabled());
    assertFalse("functioningCorrectlyNR enabled.", functioningCorrectlyNR.isEnabled());
    assertFalse("functioningCorrectlyYesRadio enabled.", functioningCorrectlyYesRadio.isEnabled());

    assertFalse("lowAlarmEventsTextField enabled.", lowAlarmEventsTextField.isEnabled());
    assertFalse("lowAlarmEventNR enabled.", lowAlarmEventNR.isEnabled());

    assertFalse("highAlarmEventsTextField enabled.", highAlarmEventsTextField.isEnabled());
    assertFalse("highAlarmEventNR enabled.", highAlarmEventNR.isEnabled());

    assertFalse("problemSinceLastVisitDoNotKnowRadio enabled.", problemSinceLastVisitDoNotKnowRadio.isEnabled());
    assertFalse("problemSinceLastVisitNoRadio enabled.", problemSinceLastVisitNoRadio.isEnabled());
    assertFalse("problemSinceLastVisitNR enabled.", problemSinceLastVisitNR.isEnabled());
    assertFalse("problemSinceLastVisitYesRadio enabled.", problemSinceLastVisitYesRadio.isEnabled());

    assertFalse("notesTextArea enabled.", notesTextArea.isEnabled());

    assertFalse("Add new button enabled", addNewButton.isEnabled());
    assertFalse("Delete button enabled", deleteButton.isEnabled());
  }

  public void clickProblemSinceLastVisitNoRadio(int refrigeratorNumber) {
    WebElement problemSinceLastVisitNoRadio = testWebDriver.getElementById("problemSinceLastVisitNo" + (refrigeratorNumber - 1));
    testWebDriver.waitForElementToAppear(problemSinceLastVisitNoRadio);
    problemSinceLastVisitNoRadio.click();
    removeFocusFromElement();
  }

  public void clickProblemSinceLastVisitDoNotKnowRadio(int refrigeratorNumber) {
    WebElement problemSinceLastVisitDoNotKnowRadio = testWebDriver.getElementById("problemSinceLastVisitDontKnow" + (refrigeratorNumber - 1));
    testWebDriver.waitForElementToAppear(problemSinceLastVisitDoNotKnowRadio);
    problemSinceLastVisitDoNotKnowRadio.click();
    removeFocusFromElement();
  }

  public void clickProblemSinceLastVisitNR(int refrigeratorNumber) {
    WebElement problemSinceLastVisitNR = testWebDriver.getElementById("problemSinceLastVisit" + (refrigeratorNumber - 1));
    testWebDriver.waitForElementToAppear(problemSinceLastVisitNR);
    problemSinceLastVisitNR.click();
    removeFocusFromElement();
  }

  public void enterValueInLowAlarmEvents(String value, int refrigeratorNumber) {
    WebElement lowAlarmEventsTextField = testWebDriver.getElementByName("lowAlarmEvent" + (refrigeratorNumber - 1));
    testWebDriver.waitForElementToAppear(lowAlarmEventsTextField);
    sendKeys(lowAlarmEventsTextField, value);
    lowAlarmEventsTextField.sendKeys(Keys.TAB);
  }

  public void enterValueInBrandModal(String value) {
    testWebDriver.waitForElementToAppear(brandTextField);
    sendKeys(brandTextField, value);
    brandTextField.sendKeys(Keys.TAB);
  }

  public void enterValueInModelModal(String value) {
    testWebDriver.waitForElementToAppear(modelTextField);
    sendKeys(modelTextField, value);
    modelTextField.sendKeys(Keys.TAB);
  }

  public void enterValueInManufacturingSerialNumberModal(String value) {
    testWebDriver.waitForElementToAppear(manufacturerSerialNumberTextField);
    sendKeys(manufacturerSerialNumberTextField, value);
    manufacturerSerialNumberTextField.sendKeys(Keys.TAB);
  }

  public void enterValueInHighAlarmEvents(String value, int refrigeratorNumber) {
    WebElement highAlarmEventsTextField = testWebDriver.getElementByName("highAlarmEvent" + (refrigeratorNumber - 1));
    testWebDriver.waitForElementToAppear(highAlarmEventsTextField);
    sendKeys(highAlarmEventsTextField, value);
    highAlarmEventsTextField.sendKeys(Keys.TAB);
  }

  public void enterValueInNotesTextArea(String value, int refrigeratorNumber) {
    WebElement notesTextArea = testWebDriver.getElementById("notes" + (refrigeratorNumber - 1));
    testWebDriver.waitForElementToAppear(notesTextArea);
    sendKeys(notesTextArea, value);
    notesTextArea.sendKeys(Keys.TAB);
  }

  public void clickDoneOnModal() {
    testWebDriver.waitForElementToAppear(doneButtonOnModal);
    doneButtonOnModal.click();
    removeFocusFromElement();
  }

  public void clickFunctioningCorrectlyYesRadio(int refrigeratorNumber) {
    WebElement functioningCorrectlyYesRadio = testWebDriver.getElementById("functioningCorrectlyYes" + (refrigeratorNumber - 1));
    testWebDriver.waitForElementToAppear(functioningCorrectlyYesRadio);
    functioningCorrectlyYesRadio.click();
    removeFocusFromElement();
  }

  public void clickFunctioningCorrectlyNoRadio(int refrigeratorNumber) {
    WebElement functioningCorrectlyNoRadio = testWebDriver.getElementById("functioningCorrectlyNo" + (refrigeratorNumber - 1));
    testWebDriver.waitForElementToAppear(functioningCorrectlyNoRadio);
    functioningCorrectlyNoRadio.click();
    removeFocusFromElement();
  }

  public void clickFunctioningCorrectlyDoNotKnowRadio(int refrigeratorNumber) {
    WebElement functioningCorrectlyDoNotKnowRadio = testWebDriver.getElementById("functioningCorrectlyDontKnow" + (refrigeratorNumber - 1));
    testWebDriver.waitForElementToAppear(functioningCorrectlyDoNotKnowRadio);
    functioningCorrectlyDoNotKnowRadio.click();
    removeFocusFromElement();
  }

  public void clickFunctioningCorrectlyNR(int refrigeratorNumber) {
    WebElement functioningCorrectlyNR = testWebDriver.getElementById("functioningCorrectly" + (refrigeratorNumber - 1));
    testWebDriver.waitForElementToAppear(functioningCorrectlyNR);
    functioningCorrectlyNR.click();
    removeFocusFromElement();
  }

  public void clickAddNew() {
    testWebDriver.waitForElementToAppear(addNewButton);
    addNewButton.click();
    testWebDriver.waitForElementToAppear(newRefrigeratorHeaderOnModal);
  }

  public void clickShowForRefrigerator(int refrigeratorNumber) {
    WebElement showButtonForRefrigerator1 = testWebDriver.getElementById("editReading" + (refrigeratorNumber - 1));
    testWebDriver.waitForElementToAppear(showButtonForRefrigerator1);
    showButtonForRefrigerator1.click();
    testWebDriver.waitForElementToAppear(testWebDriver.getElementById("temperature" + (refrigeratorNumber - 1)));
  }

  public void clickDelete() {
    testWebDriver.sleep(250);
    testWebDriver.waitForElementToAppear(deleteButton);
    deleteButton.click();
  }

  public String getRefrigeratorTemperateTextFieldValue(int refrigeratorNumber) {
    WebElement refrigeratorTemperatureTextField = testWebDriver.getElementByName("temperature" + (refrigeratorNumber - 1));
    testWebDriver.waitForElementToAppear(refrigeratorTemperatureTextField);
    return testWebDriver.getAttribute(refrigeratorTemperatureTextField, "value");
  }

  public String getLowAlarmEventsTextFieldValue(int refrigeratorNumber) {
    WebElement lowAlarmEventsTextField = testWebDriver.getElementByName("lowAlarmEvent" + (refrigeratorNumber - 1));
    testWebDriver.waitForElementToAppear(lowAlarmEventsTextField);
    return testWebDriver.getAttribute(lowAlarmEventsTextField, "value");
  }

  public String getHighAlarmEventsTextFieldValue(int refrigeratorNumber) {
    WebElement highAlarmEventsTextField = testWebDriver.getElementByName("highAlarmEvent" + (refrigeratorNumber - 1));
    testWebDriver.waitForElementToAppear(highAlarmEventsTextField);
    return testWebDriver.getAttribute(highAlarmEventsTextField, "value");
  }

  public String getNotesTextAreaValue(int refrigeratorNumber) {
    WebElement notesTextArea = testWebDriver.getElementById("notes" + (refrigeratorNumber - 1));
    testWebDriver.waitForElementToAppear(notesTextArea);
    return testWebDriver.getAttribute(notesTextArea, "value");
  }

  public void verifyRefrigeratorColor(String whichIcon, String color) {
    testWebDriver.sleep(500);
    if (color.toLowerCase().equals("RED".toLowerCase()))
      color = "rgba(203, 64, 64, 1)";
    else if (color.toLowerCase().equals("GREEN".toLowerCase()))
      color = "rgba(69, 182, 0, 1)";
    else if (color.toLowerCase().equals("AMBER".toLowerCase()))
      color = "rgba(240, 165, 19, 1)";

    if (whichIcon.toLowerCase().equals("Overall".toLowerCase())) {
      assertEquals(color, overallRefrigeratorIcon.getCssValue("background-color"));
    } else if (whichIcon.toLowerCase().equals("Individual".toLowerCase())) {
      testWebDriver.waitForElementToAppear(individualRefrigeratorIcon);
      assertEquals(color, individualRefrigeratorIcon.getCssValue("background-color"));
    }
  }

  public void onRefrigeratorScreen() {
    testWebDriver.sleep(500);
    testWebDriver.waitForElementToAppear(refrigeratorsHeader);
    SeleneseTestNgHelper.assertTrue("Refrigerator header should show up", refrigeratorsHeader.isDisplayed());
  }

  public void clickDone() {
    testWebDriver.sleep(500);
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

  public void addNewRefrigerator(String manufacturerSerialNumber) {
    enterValueInManufacturingSerialNumberModal(manufacturerSerialNumber);
    clickDoneOnModal();
  }

  public void verifyDuplicateErrorMessage(String message) {
    testWebDriver.waitForElementToAppear(duplicateRefrigeratorMessage);
    assertEquals(duplicateRefrigeratorMessage.getText(), message);
  }

  public void applyNRToRefrigeratorTemperature(int refrigeratorNumber) {
    WebElement refrigeratorTemperatureNR = testWebDriver.getElementById("temperature" + (refrigeratorNumber - 1));
    testWebDriver.waitForElementToAppear(refrigeratorTemperatureNR);
    refrigeratorTemperatureNR.click();
    removeFocusFromElement();
  }

  public void applyNRToLowAlarmEvent(int refrigeratorNumber) {
    WebElement lowAlarmEventNR = testWebDriver.getElementById("lowAlarmEvent" + (refrigeratorNumber - 1));
    testWebDriver.waitForElementToAppear(lowAlarmEventNR);
    lowAlarmEventNR.click();
    removeFocusFromElement();
  }

  public void applyNRToHighAlarmEvent(int refrigeratorNumber) {
    WebElement highAlarmEventNR = testWebDriver.getElementById("highAlarmEvent" + (refrigeratorNumber - 1));
    testWebDriver.waitForElementToAppear(highAlarmEventNR);
    highAlarmEventNR.click();
    removeFocusFromElement();
  }

  public void verifyFieldsDisabledWhenAllNRSelected() {
    assertFalse("refrigeratorTemperatureTextField enabled.", refrigeratorTemperatureTextField.isEnabled());
    assertTrue("refrigeratorTemperatureNR enabled.", refrigeratorTemperatureNR.isEnabled());

    assertFalse("functioningCorrectlyDoNotKnowRadio enabled.", functioningCorrectlyDoNotKnowRadio.isEnabled());
    assertFalse("functioningCorrectlyNoRadio enabled.", functioningCorrectlyNoRadio.isEnabled());
    assertTrue("functioningCorrectlyNR enabled.", functioningCorrectlyNR.isEnabled());
    assertFalse("functioningCorrectlyYesRadio enabled.", functioningCorrectlyYesRadio.isEnabled());

    assertFalse("lowAlarmEventsTextField enabled.", lowAlarmEventsTextField.isEnabled());
    assertTrue("lowAlarmEventNR enabled.", lowAlarmEventNR.isEnabled());

    assertFalse("highAlarmEventsTextField enabled.", highAlarmEventsTextField.isEnabled());
    assertTrue("highAlarmEventNR enabled.", highAlarmEventNR.isEnabled());

    assertFalse("problemSinceLastVisitDoNotKnowRadio enabled.", problemSinceLastVisitDoNotKnowRadio.isEnabled());
    assertFalse("problemSinceLastVisitNoRadio enabled.", problemSinceLastVisitNoRadio.isEnabled());
    assertTrue("problemSinceLastVisitNR enabled.", problemSinceLastVisitNR.isEnabled());
    assertFalse("problemSinceLastVisitYesRadio enabled.", problemSinceLastVisitYesRadio.isEnabled());

    assertTrue("notesTextArea enabled.", notesTextArea.isEnabled());
  }

  public boolean isAddNewButtonEnabled() {
    return addNewButton.isEnabled();
  }

  @Override
  public void removeFocusFromElement() {
    testWebDriver.waitForElementToAppear(refrigeratorsPageLabel);
    refrigeratorsPageLabel.click();
  }

  public boolean isFunctioningCorrectlyNRSelected(int refrigeratorNumber) {
    WebElement functioningCorrectlyNR = testWebDriver.getElementById("functioningCorrectly" + (refrigeratorNumber - 1));
    testWebDriver.waitForElementToAppear(functioningCorrectlyNR);
    return functioningCorrectlyNR.isSelected();
  }

  public boolean isDoneButtonEnabled() {
    return doneButtonOnModal.isEnabled();
  }
}