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
import org.openqa.selenium.support.How;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.pagefactory.AjaxElementLocatorFactory;

import static org.openqa.selenium.support.How.ID;


public class RegimenTemplateConfigPage extends Page {

  @FindBy(how = ID, using = "saveRegimen")
  private static WebElement SaveButton = null;

  @FindBy(how = ID, using = "cancelRegimen")
  private static WebElement CancelButton = null;

  @FindBy(how = ID, using = "saveSuccessMsgDiv")
  private static WebElement saveSuccessMsgDiv = null;

  @FindBy(how = ID, using = "saveErrorMsgDiv")
  private static WebElement saveErrorMsgDiv = null;

  @FindBy(how = ID, using = "newRegimenCategory")
  private static WebElement newRegimenCategoryDropDown = null;

  @FindBy(how = ID, using = "newRegimenCode")
  private static WebElement newRegimenCodeTextBox = null;

  @FindBy(how = ID, using = "newRegimenName")
  private static WebElement newRegimenNameTextBox = null;

  @FindBy(how = ID, using = "newRegimenActive")
  private static WebElement newRegimenActiveCheckBox = null;

  @FindBy(how = ID, using = "addNewRegimen")
  private static WebElement addButton = null;

  @FindBy(how = ID, using = "editRegimen")
  private static WebElement editButton = null;

  @FindBy(how = ID, using = "regimenDone")
  private static WebElement doneButton = null;

  @FindBy(how = ID, using = "doneFailMessage")
  private static WebElement doneFailMessage = null;

  @FindBy(how = How.XPATH, using = ".//*[@id='wrap']/div/div/div/div[2]/ul/li[1]/a")
  private static WebElement reportingFieldsTab = null;

  @FindBy(how = How.XPATH, using = ".//*[@id='reportingFields']/div[2]/div[1]/div/div[1]/span/input")
  private static WebElement noOfPatientsOnTreatmentCheckBox = null;

  @FindBy(how = How.XPATH, using = ".//*[@id='reportingFields']/div[2]/div[2]/div/div[1]/span/input")
  private static WebElement noOfPatientsToInitiateTreatmentCheckBox = null;

  @FindBy(how = How.XPATH, using = ".//*[@id='reportingFields']/div[2]/div[3]/div/div[1]/span/input")
  private static WebElement noOfPatientsStoppedTreatmentCheckBox = null;

  @FindBy(how = How.XPATH, using = ".//*[@id='reportingFields']/div[2]/div[4]/div/div[1]/span/input")
  private static WebElement remarksCheckBox = null;

  @FindBy(how = How.XPATH, using = ".//*[@id='reportingFields']/div[2]/div[1]/div/div[2]/input")
  private static WebElement noOfPatientsOnTreatmentTextField = null;

  @FindBy(how = How.XPATH, using = ".//*[@id='reportingFields']/div[2]/div[2]/div/div[2]/input")
  private static WebElement noOfPatientsToInitiateTreatmentTextField = null;

  @FindBy(how = How.XPATH, using = ".//*[@id='reportingFields']/div[2]/div[3]/div/div[2]/input")
  private static WebElement noOfPatientsStoppedTreatmentTextField = null;

  @FindBy(how = How.XPATH, using = ".//*[@id='reportingFields']/div[2]/div[4]/div/div[2]/input")
  private static WebElement remarksTextField = null;

  @FindBy(how = How.XPATH, using = ".//*[@id='reportingFields']/div[2]/div[1]/div/div[3]/span")
  private static WebElement noOfPatientsOnTreatmentDataType = null;

  @FindBy(how = How.XPATH, using = ".//*[@id='reportingFields']/div[2]/div[2]/div/div[3]/span")
  private static WebElement noOfPatientsToInitiateTreatmentDataType = null;

  @FindBy(how = How.XPATH, using = ".//*[@id='reportingFields']/div[2]/div[3]/div/div[3]/span")
  private static WebElement noOfPatientsStoppedTreatmentDataType = null;

  @FindBy(how = How.XPATH, using = ".//*[@id='reportingFields']/div[2]/div[4]/div/div[3]/span")
  private static WebElement remarksDataType = null;

  @FindBy(how = ID, using = "regimensTab")
  private static WebElement regimensTab = null;

  private static String baseRegimenDivXpath = "//div[@id='sortable']/div";

  public RegimenTemplateConfigPage(TestWebDriver driver) {
    super(driver);
    PageFactory.initElements(new AjaxElementLocatorFactory(TestWebDriver.getDriver(), 10), this);
    testWebDriver.setImplicitWait(10);
  }

  public void NoOfPatientsOnTreatmentCheckBox(boolean select) {
    if (select) selectCheckBox(noOfPatientsOnTreatmentCheckBox);
    else unSelectCheckBox(noOfPatientsOnTreatmentCheckBox);
  }

  public void NoOfPatientsToInitiateTreatmentCheckBox(boolean select) {
    if (select) selectCheckBox(noOfPatientsToInitiateTreatmentCheckBox);
    else unSelectCheckBox(noOfPatientsToInitiateTreatmentCheckBox);
  }

  public void NoOfPatientsStoppedTreatmentCheckBox(boolean select) {
    if (select) selectCheckBox(noOfPatientsStoppedTreatmentCheckBox);
    else unSelectCheckBox(noOfPatientsStoppedTreatmentCheckBox);
  }

  public void RemarksCheckBox(boolean select) {
    if (select) selectCheckBox(remarksCheckBox);
    else unSelectCheckBox(remarksCheckBox);
  }

  public boolean IsSelectedNoOfPatientsOnTreatmentCheckBox() {
    return noOfPatientsOnTreatmentCheckBox.isSelected();
  }

  public boolean IsNoOfPatientsToInitiateTreatmentCheckBoxSelected() {
    return noOfPatientsToInitiateTreatmentCheckBox.isSelected();
  }

  public boolean IsNoOfPatientsStoppedTreatmentCheckBoxSelected() {
    return noOfPatientsStoppedTreatmentCheckBox.isSelected();
  }

  public boolean IsRemarksCheckBoxSelected() {
    return remarksCheckBox.isSelected();
  }

  public String getValueNoOfPatientsOnTreatmentTextField() {
    return noOfPatientsOnTreatmentTextField.getAttribute("value");
  }

  public String getValueNoOfPatientsToInitiateTreatmentTextField() {
    return noOfPatientsToInitiateTreatmentTextField.getAttribute("value");
  }

  public String getValueNoOfPatientsStoppedTreatmentTextField() {
    return noOfPatientsStoppedTreatmentTextField.getAttribute("value");
  }

  public String getValueRemarksTextField() {
    return remarksTextField.getAttribute("value");
  }

  public void setValueRemarksTextField(String value) {
    sendKeys(remarksTextField, value);
  }

  public String getTextNoOfPatientsOnTreatmentDataType() {
    return noOfPatientsOnTreatmentDataType.getText().trim();
  }

  public String getTextNoOfPatientsToInitiateTreatmentDataType() {
    return noOfPatientsToInitiateTreatmentDataType.getText().trim();
  }

  public String getTextNoOfPatientsStoppedTreatmentDataType() {
    return noOfPatientsStoppedTreatmentDataType.getText().trim();
  }

  public String getTextRemarksDataType() {
    return remarksDataType.getText().trim();
  }

  public void addNewRegimen(String category, String code, String name, Boolean isActive) {
    testWebDriver.waitForElementsToAppear(newRegimenCategoryDropDown, newRegimenCodeTextBox);
    testWebDriver.selectByVisibleText(newRegimenCategoryDropDown, category);
    newRegimenCodeTextBox.sendKeys(code);
    newRegimenNameTextBox.sendKeys(name);
    if (isActive) newRegimenActiveCheckBox.click();
    addButton.click();
  }

  public void clickReportingFieldTab() {
    testWebDriver.waitForElementToAppear(reportingFieldsTab);
    reportingFieldsTab.click();
    testWebDriver.waitForElementToAppear(noOfPatientsOnTreatmentCheckBox);
  }

  public void selectCheckBox(WebElement locator) {
    if (!locator.isSelected()) {
      locator.click();
    }
  }

  public void unSelectCheckBox(WebElement locator) {
    if (locator.isSelected()) {
      locator.click();
    }
  }

  public boolean IsDisplayedDoneFailMessage() {
    testWebDriver.waitForElementToAppear(doneFailMessage);
    return doneFailMessage.isDisplayed();
  }

  public void clickSaveButton() {
    testWebDriver.waitForElementToAppear(SaveButton);
    SaveButton.click();
  }

  public void clickCancelButton() {
    testWebDriver.waitForElementToAppear(CancelButton);
    CancelButton.click();
  }

  public boolean isDisplayedSaveSuccessMsgDiv() {
    testWebDriver.waitForElementToAppear(saveSuccessMsgDiv);
    return saveSuccessMsgDiv.isDisplayed();
  }

  public String getSaveSuccessMsgDiv() {
    testWebDriver.waitForElementToAppear(saveSuccessMsgDiv);
    return saveSuccessMsgDiv.getText().trim();
  }

  public boolean IsDisplayedSaveErrorMsgDiv() {
    testWebDriver.waitForElementToAppear(saveErrorMsgDiv);
    return saveErrorMsgDiv.isDisplayed();
  }

  public String getSaveErrorMsgDiv() {
    testWebDriver.waitForElementToAppear(saveErrorMsgDiv);
    return saveErrorMsgDiv.getText().trim();
  }

  public String getNonEditableAddedCode(int indexOfCodeAdded) {
    testWebDriver.waitForElementToAppear(
      testWebDriver.getElementByXpath(baseRegimenDivXpath + "[" + indexOfCodeAdded + "]/div[2]/div/span"));
    return testWebDriver.getElementByXpath(
      baseRegimenDivXpath + "[" + indexOfCodeAdded + "]/div[2]/div/span").getText().trim();
  }

  public String getNonEditableAddedName(int indexOfCodeAdded) {
    testWebDriver.waitForElementToAppear(
      testWebDriver.getElementByXpath(baseRegimenDivXpath + "[" + indexOfCodeAdded + "]/div[2]/div/span"));
    return testWebDriver.getElementByXpath(
      baseRegimenDivXpath + "[" + indexOfCodeAdded + "]/div[3]/div/span").getText().trim();
  }

  public boolean getNonEditableAddedActiveCheckBox(int indexOfCodeAdded) {
    testWebDriver.waitForElementToAppear(
      testWebDriver.getElementByXpath(baseRegimenDivXpath + "[" + indexOfCodeAdded + "]/div[2]/div/span"));
    return testWebDriver.getElementByXpath(
      baseRegimenDivXpath + "[" + indexOfCodeAdded + "]/div[4]/input").isSelected();
  }

  public String getEditableAddedCode(int indexOfCodeAdded) {
    testWebDriver.waitForElementToAppear(
      testWebDriver.getElementByXpath(baseRegimenDivXpath + "[" + indexOfCodeAdded + "]/div[2]/input"));
    return testWebDriver.getAttribute(
      testWebDriver.getElementByXpath(baseRegimenDivXpath + "[" + indexOfCodeAdded + "]/div[2]/input"), "value").trim();
  }

  public String getEditableAddedName(int indexOfCodeAdded) {
    testWebDriver.waitForElementToAppear(
      testWebDriver.getElementByXpath(baseRegimenDivXpath + "[" + indexOfCodeAdded + "]/div[2]/input"));
    return testWebDriver.getAttribute(
      testWebDriver.getElementByXpath(baseRegimenDivXpath + "[" + indexOfCodeAdded + "]/div[3]/input"), "value").trim();
  }

  public boolean getEditableAddedActiveCheckBox(int indexOfCodeAdded) {
    testWebDriver.waitForElementToAppear(
      testWebDriver.getElementByXpath(baseRegimenDivXpath + "[" + indexOfCodeAdded + "]/div[2]/input"));
    return testWebDriver.getElementByXpath(
      baseRegimenDivXpath + "[" + indexOfCodeAdded + "]/div[4]/input").isSelected();
  }

  public void configureProgram(String program) {
    WebElement configureProgram = testWebDriver.getElementById(program);
    testWebDriver.waitForElementToAppear(configureProgram);
    configureProgram.click();
    testWebDriver.waitForElementToAppear(regimensTab);
    regimensTab.click();
    testWebDriver.waitForElementToAppear(addButton);
  }

  public void clickEditProgram(String program) throws InterruptedException {
    testWebDriver.waitForElementToAppear(testWebDriver.getElementByXpath("//a[@id='" + program + "']"));
    testWebDriver.getElementByXpath("//a[@id='" + program + "']").click();
    Thread.sleep(100);
    testWebDriver.getElementByXpath(".//*[@id='wrap']/div/div/div/div[2]/ul/li[2]/a").click();
    testWebDriver.waitForElementToAppear(addButton);
  }

  public void clickEditButton() {
    testWebDriver.waitForElementToAppear(editButton);
    editButton.click();
    testWebDriver.waitForElementToAppear(doneButton);
  }

  public void clickDoneButton() {
    testWebDriver.waitForElementToAppear(doneButton);
    doneButton.click();
    testWebDriver.waitForElementsToAppear(editButton, saveErrorMsgDiv, doneFailMessage);
  }

  public void SaveRegime() {
    SaveButton.click();
  }

  public void verifySaveErrorMessageDiv(String errorMessage) {
    testWebDriver.waitForElementToAppear(saveErrorMsgDiv);
    SeleneseTestNgHelper.assertEquals(errorMessage, saveErrorMsgDiv.getText());
  }

  public void CancelRegime(String program) {
    testWebDriver.waitForElementToAppear(CancelButton);
    CancelButton.click();
    testWebDriver.waitForElementToAppear(testWebDriver.getElementByXpath("//a[@id='" + program + "']/span"));
  }

  public void sendKeys(WebElement locator, String value) {
    int length = testWebDriver.getAttribute(locator, "value").length();
    for (int i = 0; i < length; i++) {
      locator.sendKeys(Keys.ARROW_RIGHT);
    }
    for (int i = 0; i < length; i++) {
      locator.sendKeys("\u0008");
    }
    locator.sendKeys(value);
  }
}