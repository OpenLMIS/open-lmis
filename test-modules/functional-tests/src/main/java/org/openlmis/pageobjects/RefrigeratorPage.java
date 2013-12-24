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

import static com.thoughtworks.selenium.SeleneseTestBase.*;
import static org.openqa.selenium.support.How.*;

public class RefrigeratorPage extends Page {

  @FindBy(how = ID, using = "addNew")
  private static WebElement addNewButton = null;

  @FindBy(how = ID, using = "editReading0")
  public static WebElement showButtonForRefrigerator1 = null;

  @FindBy(how = ID, using = "editReading1")
  public static WebElement showButtonForRefrigerator2 = null;

  @FindBy(how = XPATH, using = "//input[@value='Delete']")
  private static WebElement deleteButton = null;

  @FindBy(how = XPATH, using = "//a[contains(text(),'Done')]")
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
  private static WebElement functioningCorrectlyDontKnowRadio = null;

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
  private static WebElement problemSinceLastVisitDontKnowRadio = null;

  @FindBy(how = ID, using = "problemSinceLastVisit0")
  private static WebElement problemSinceLastVisitNR = null;

  @FindBy(how = ID, using = "notes")
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

  @FindBy(how = XPATH, using = "//div[@id='addRefrigeratorModal']/div[2]/div[3]/div/div")
  public static WebElement duplicateRefrigeratorMessage = null;

  @FindBy(how = ID, using = "other0")
  private static WebElement problemOther= null;

  public RefrigeratorPage(TestWebDriver driver) {
    super(driver);
    PageFactory.initElements(new AjaxElementLocatorFactory(TestWebDriver.getDriver(), 10), this);
    testWebDriver.setImplicitWait(10);
  }

  public void enterValueInRefrigeratorTemperature(String value) {
    testWebDriver.waitForElementToAppear(refrigeratorTemperatureTextField);
    sendKeys(refrigeratorTemperatureTextField, value);
    refrigeratorTemperatureTextField.sendKeys(Keys.TAB);
  }

  public void clickProblemSinceLastVisitYesRadio() {
    testWebDriver.waitForElementToAppear(problemSinceLastVisitYesRadio);
    problemSinceLastVisitYesRadio.click();
  }

  public void selectOtherProblem() {
    testWebDriver.waitForElementToAppear(problemOther);
    problemOther.click();
  }

  public void clickOKButton() {
    testWebDriver.waitForElementToAppear(OKButton);
    OKButton.click();
  }

  public void navigateToRefrigeratorTab() {
    testWebDriver.waitForElementToAppear(refrigeratorTab);
    refrigeratorTab.click();
  }

  public void verifyAllFieldsDisabled() {
    assertFalse("refrigeratorTemperatureTextField enabled.", refrigeratorTemperatureTextField.isEnabled());
    assertFalse("refrigeratorTemperatureNR enabled.", refrigeratorTemperatureNR.isEnabled());

    assertFalse("functioningCorrectlyDontKnowRadio enabled.", functioningCorrectlyDontKnowRadio.isEnabled());
    assertFalse("functioningCorrectlyNoRadio enabled.", functioningCorrectlyNoRadio.isEnabled());
    assertFalse("functioningCorrectlyNR enabled.", functioningCorrectlyNR.isEnabled());
    assertFalse("functioningCorrectlyYesRadio enabled.", functioningCorrectlyYesRadio.isEnabled());

    assertFalse("lowAlarmEventsTextField enabled.", lowAlarmEventsTextField.isEnabled());
    assertFalse("lowAlarmEventNR enabled.", lowAlarmEventNR.isEnabled());

    assertFalse("highAlarmEventsTextField enabled.", highAlarmEventsTextField.isEnabled());
    assertFalse("highAlarmEventNR enabled.", highAlarmEventNR.isEnabled());

    assertFalse("problemSinceLastVisitDontKnowRadio enabled.", problemSinceLastVisitDontKnowRadio.isEnabled());
    assertFalse("problemSinceLastVisitNoRadio enabled.", problemSinceLastVisitNoRadio.isEnabled());
    assertFalse("problemSinceLastVisitNR enabled.", problemSinceLastVisitNR.isEnabled());
    assertFalse("problemSinceLastVisitYesRadio enabled.", problemSinceLastVisitYesRadio.isEnabled());

    assertFalse("notesTextArea enabled.", notesTextArea.isEnabled());

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
    problemSinceLastVisitNR.sendKeys(Keys.TAB);
  }

  public void enterValueInLowAlarmEvents(String value) {
    testWebDriver.waitForElementToAppear(lowAlarmEventsTextField);
    sendKeys(lowAlarmEventsTextField, value);
    lowAlarmEventsTextField.sendKeys(Keys.TAB);
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
    highAlarmEventsTextField.sendKeys(Keys.TAB);
  }

  public void enterValueInNotesTextArea(String value) {
    testWebDriver.waitForElementToAppear(notesTextArea);
    sendKeys(notesTextArea, value);
  }

  public void clickDoneOnModal() {
    testWebDriver.waitForElementToAppear(doneButtonOnModal);
    doneButtonOnModal.click();
    //testWebDriver.waitForElementToAppear(testWebDriver.getElementByXpath("(//a[contains(text(),'Show')])[1]"));
  }

  public void clickFunctioningCorrectlyYesRadio() {
    testWebDriver.waitForElementToAppear(functioningCorrectlyYesRadio);
    functioningCorrectlyYesRadio.click();
    functioningCorrectlyYesRadio.sendKeys(Keys.TAB);
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

  public void clickShowForRefrigerator1() {
    testWebDriver.waitForElementToAppear(showButtonForRefrigerator1);
    showButtonForRefrigerator1.click();
    testWebDriver.waitForElementToAppear(refrigeratorTemperatureNR);
  }

  public void clickShowForRefrigerator2() {
    testWebDriver.waitForElementToAppear(showButtonForRefrigerator2);
    showButtonForRefrigerator2.click();
    testWebDriver.waitForElementToAppear(refrigeratorTemperatureNR);
  }

  public void clickDelete() {
    testWebDriver.sleep(250);
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

  public void verifyIndividualRefrigeratorColor(String whichIcon, String color) {
    testWebDriver.waitForElementToAppear(individualRefrigeratorIcon);
    if (color.toLowerCase().equals("RED".toLowerCase()))
      color = "rgba(203, 64, 64, 1)";
    else if (color.toLowerCase().equals("GREEN".toLowerCase()))
      color = "rgba(69, 182, 0, 1)";
    else if (color.toLowerCase().equals("AMBER".toLowerCase()))
      color = "rgba(240, 165, 19, 1)";

    if (whichIcon.toLowerCase().equals("Overall".toLowerCase()))
      assertEquals(color, overallRefrigeratorIcon.getCssValue("background-color"));
    else if (whichIcon.toLowerCase().equals("Individual".toLowerCase()))
      assertEquals(color, individualRefrigeratorIcon.getCssValue("background-color"));
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


  public void verifyDuplicateErrorMessage(String message) {
    testWebDriver.waitForElementToAppear(duplicateRefrigeratorMessage);
    assertEquals(duplicateRefrigeratorMessage.getText(), message);
  }

  public void applyNRToRefrigeratorTemperature() {
    testWebDriver.waitForElementToAppear(refrigeratorTemperatureNR);
    refrigeratorTemperatureNR.click();
  }

  public void applyNRToLowAlarmEvent() {
    testWebDriver.waitForElementToAppear(lowAlarmEventNR);
    lowAlarmEventNR.click();
  }

  public void applyNRToHighAlarmEvent() {
    testWebDriver.waitForElementToAppear(highAlarmEventNR);
    highAlarmEventNR.click();
  }

  public void verifyFieldsDisabledWhenAllNRSelected() {
    assertFalse("refrigeratorTemperatureTextField enabled.", refrigeratorTemperatureTextField.isEnabled());
    assertTrue("refrigeratorTemperatureNR enabled.", refrigeratorTemperatureNR.isEnabled());

    assertFalse("functioningCorrectlyDontKnowRadio enabled.", functioningCorrectlyDontKnowRadio.isEnabled());
    assertFalse("functioningCorrectlyNoRadio enabled.", functioningCorrectlyNoRadio.isEnabled());
    assertTrue("functioningCorrectlyNR enabled.", functioningCorrectlyNR.isEnabled());
    assertFalse("functioningCorrectlyYesRadio enabled.", functioningCorrectlyYesRadio.isEnabled());

    assertFalse("lowAlarmEventsTextField enabled.", lowAlarmEventsTextField.isEnabled());
    assertTrue("lowAlarmEventNR enabled.", lowAlarmEventNR.isEnabled());

    assertFalse("highAlarmEventsTextField enabled.", highAlarmEventsTextField.isEnabled());
    assertTrue("highAlarmEventNR enabled.", highAlarmEventNR.isEnabled());

    assertFalse("problemSinceLastVisitDontKnowRadio enabled.", problemSinceLastVisitDontKnowRadio.isEnabled());
    assertFalse("problemSinceLastVisitNoRadio enabled.", problemSinceLastVisitNoRadio.isEnabled());
    assertTrue("problemSinceLastVisitNR enabled.", problemSinceLastVisitNR.isEnabled());
    assertFalse("problemSinceLastVisitYesRadio enabled.", problemSinceLastVisitYesRadio.isEnabled());

    assertTrue("notesTextArea enabled.", notesTextArea.isEnabled());

  }

}