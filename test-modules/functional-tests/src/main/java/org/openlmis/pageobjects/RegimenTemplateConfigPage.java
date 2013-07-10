/*
 * Copyright © 2013 VillageReach.  All Rights Reserved.  This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 *
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package org.openlmis.pageobjects;


import org.openlmis.UiUtils.TestWebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.How;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.pagefactory.AjaxElementLocatorFactory;


public class RegimenTemplateConfigPage extends Page {


  @FindBy(how = How.LINK_TEXT, using = "Logout")
  private static WebElement logoutLink;

  @FindBy(how = How.XPATH, using = "//input[@value='Save']")
  private static WebElement SaveButton;

  @FindBy(how = How.XPATH, using = "//a[contains(text(),'Cancel')]")
  private static WebElement CancelButton;

  @FindBy(how = How.XPATH, using = "//a[contains(text(),'(Change)')]")
  private static WebElement changeLink;

  @FindBy(how = How.ID, using = "saveSuccessMsgDiv")
  private static WebElement saveSuccessMsgDiv;

  @FindBy(how = How.ID, using = "saveErrorMsgDiv")
  private static WebElement saveErrorMsgDiv;

  @FindBy(how = How.ID, using = "newRegimenCategory")
  private static WebElement newRegimenCategoryDropDown;

  @FindBy(how = How.ID, using = "newRegimenCode")
  private static WebElement newRegimenCodeTextBox;

  @FindBy(how = How.ID, using = "newRegimenName")
  private static WebElement newRegimenNameTextBox;

  @FindBy(how = How.ID, using = "newRegimenActive")
  private static WebElement newRegimenActiveCheckBox;

  @FindBy(how = How.XPATH, using = "//input[@value='Add']")
  private static WebElement addButton;

  @FindBy(how = How.XPATH, using = "//input[@value='Edit']")
  private static WebElement editButton;

  @FindBy(how = How.XPATH, using = "//input[@value='Done']")
  private static WebElement doneButton;

  @FindBy(how = How.ID, using = "doneFailMessage")
  private static WebElement doneFailMessage;

  @FindBy(how = How.XPATH, using = "//h2[contains(text(),'Regimen Template')]")
  private static WebElement regimenTemplateHeader;

  @FindBy(how = How.XPATH, using = "//a[contains(text(),'Regimens')]")
  private static WebElement regimenTab;

  @FindBy(how = How.XPATH, using = ".//*[@id='wrap']/div/div/div/div[2]/ul/li[1]/a")
  private static WebElement reportingFieldsTab;

  @FindBy(how = How.XPATH, using = ".//*[@id='reportingFields']/div[2]/div[3]/ng-switch/div/div[1]/span/input")
  private static WebElement noOfPatientsOnTreatmentCheckBox;

  @FindBy(how = How.XPATH, using = ".//*[@id='reportingFields']/div[2]/div[4]/ng-switch/div/div[1]/span/input")
  private static WebElement noOfPatientsToInitiateTreatmentCheckBox;

  @FindBy(how = How.XPATH, using = ".//*[@id='reportingFields']/div[2]/div[5]/ng-switch/div/div[1]/span/input")
  private static WebElement noOfPatientsStoppedTreatmentCheckBox;

  @FindBy(how = How.XPATH, using = ".//*[@id='reportingFields']/div[2]/div[6]/ng-switch/div/div[1]/span/input")
  private static WebElement remarksCheckBox;

  @FindBy(how = How.XPATH, using = ".//*[@id='reportingFields']/div[2]/div[3]/ng-switch/div/div[2]/input")
  private static WebElement noOfPatientsOnTreatmentTextField;

  @FindBy(how = How.XPATH, using = ".//*[@id='reportingFields']/div[2]/div[4]/ng-switch/div/div[2]/input")
  private static WebElement noOfPatientsToInitiateTreatmentTextField;

  @FindBy(how = How.XPATH, using = ".//*[@id='reportingFields']/div[2]/div[5]/ng-switch/div/div[2]/input")
  private static WebElement noOfPatientsStoppedTreatmentTextField;

  @FindBy(how = How.XPATH, using = ".//*[@id='reportingFields']/div[2]/div[1]/ng-switch/div/div[2]/input")
  private static WebElement codeTextField;

  @FindBy(how = How.XPATH, using = ".//*[@id='reportingFields']/div[2]/div[2]/ng-switch/div/div[2]/input")
  private static WebElement nameTextField;

  @FindBy(how = How.XPATH, using = "//div[@ng-switch-when='code']/div[1]/span/i")
  private static WebElement codeOKIcon;

  @FindBy(how = How.XPATH, using = "//div[@ng-switch-when='name']/div[1]/span/i")
  private static WebElement nameOKIcon;

  @FindBy(how = How.XPATH, using = ".//*[@id='reportingFields']/div[2]/div[6]/ng-switch/div/div[2]/input")
  private static WebElement remarksTextField;

  @FindBy(how = How.XPATH, using = ".//*[@id='reportingFields']/div[2]/div[3]/ng-switch/div/div[3]/span")
  private static WebElement noOfPatientsOnTreatmentDataType;

  @FindBy(how = How.XPATH, using = ".//*[@id='reportingFields']/div[2]/div[4]/ng-switch/div/div[3]/span")
  private static WebElement noOfPatientsToInitiateTreatmentDataType;

  @FindBy(how = How.XPATH, using = ".//*[@id='reportingFields']/div[2]/div[5]/ng-switch/div/div[3]/span")
  private static WebElement noOfPatientsStoppedTreatmentDataType;

  @FindBy(how = How.XPATH, using = ".//*[@id='reportingFields']/div[2]/div[6]/ng-switch/div/div[3]/span")
  private static WebElement remarksDataType;

  @FindBy(how = How.XPATH, using = ".//*[@id='reportingFields']/div[2]/div[1]/ng-switch/div/div[3]/span")
  private static WebElement codeDataType;

  @FindBy(how = How.XPATH, using = ".//*[@id='reportingFields']/div[2]/div[2]/ng-switch/div/div[3]/span")
  private static WebElement nameDataType;

  @FindBy(how = How.XPATH, using = "//div[1][@class='row-fluid rnr-template-columns ng-scope']/div[2][@class='span2']/div/span[@class='ng-binding']")
  private static WebElement addedCode;

  @FindBy(how = How.XPATH, using = "//div[1][@class='row-fluid rnr-template-columns ng-scope']/div[3][@class='span4']/div/span[@class='ng-binding']")
  private static WebElement addedName;

  @FindBy(how = How.XPATH, using = "//div[2][@class='ui-sortable ng-scope ng-pristine ng-valid']/div[1][@class='category-name']/div[@class='ng-binding']")
  private static WebElement addedCategory;

  private static String TEMPLATE_SUCCESS_MESSAGE = "Template saved successfully!";
  private static String baseRegimenDivXpath = "//div[@id='sortable']/div";

  public RegimenTemplateConfigPage(TestWebDriver driver) {
    super(driver);
    PageFactory.initElements(new AjaxElementLocatorFactory(TestWebDriver.getDriver(), 10), this);
    testWebDriver.setImplicitWait(10);
  }

  public void NoOfPatientsOnTreatmentCheckBox(boolean select) {
      if (select)
        selectCheckBox(noOfPatientsOnTreatmentCheckBox);
      else
        unSelectCheckBox(noOfPatientsOnTreatmentCheckBox);
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

  public String getValueCodeTextField() {
    return codeTextField.getAttribute("value");
  }

  public String getValueNameTextField() {
    return nameTextField.getAttribute("value");
  }

  public WebElement getCodeOKIcon() {
    return codeOKIcon;
  }

  public WebElement getNameOKIcon() {
    return nameOKIcon;
  }

  public void setValueRemarksTextField(String value) {
    sendKeys(remarksTextField, value);
  }

  public void setValueCodeTextField(String value) {
    sendKeys(codeTextField, value);
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

  public String getTextCodeDataType() {
    return codeDataType.getText().trim();
  }

  public String getTextNameDataType() {
    return nameDataType.getText().trim();
  }

  public void AddNewRegimen(String category, String code, String name, Boolean isActive) {
    testWebDriver.waitForElementsToAppear(newRegimenCategoryDropDown, newRegimenCodeTextBox);
    testWebDriver.selectByVisibleText(newRegimenCategoryDropDown, category);
    newRegimenCodeTextBox.sendKeys(code);
    newRegimenNameTextBox.sendKeys(name);
    if (isActive)
      newRegimenActiveCheckBox.click();
    addButton.click();

  }

  public void clickReportingFieldTab()
  {
    testWebDriver.waitForElementToAppear(reportingFieldsTab);
    reportingFieldsTab.click();
    testWebDriver.waitForElementToAppear(noOfPatientsOnTreatmentCheckBox);
  }



  public void selectCheckBox(WebElement locator)
  {
   if(!locator.isSelected()){
     locator.click();
   }
  }

  public void unSelectCheckBox(WebElement locator)
  {
    if(locator.isSelected()){
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

  public boolean IsDisplayedSaveSuccessMsgDiv() {
    testWebDriver.waitForElementToAppear(saveSuccessMsgDiv);
    return saveSuccessMsgDiv.isDisplayed() ;
  }

  public String getSaveSuccessMsgDiv() {
    testWebDriver.waitForElementToAppear(saveSuccessMsgDiv);
    return saveSuccessMsgDiv.getText().trim() ;
  }

  public boolean IsDisplayedSaveErrorMsgDiv() {
    testWebDriver.waitForElementToAppear(saveErrorMsgDiv);
    return saveErrorMsgDiv.isDisplayed() ;
  }

  public String getSaveErrorMsgDiv() {
    testWebDriver.waitForElementToAppear(saveErrorMsgDiv);
    return saveErrorMsgDiv.getText().trim() ;
  }

  public String getNewRegimenCodeTextBoxValue() {
    return newRegimenCodeTextBox.getText();
  }

  public String getNewRegimenNameTextBoxValue() {
    return newRegimenNameTextBox.getText();
  }

  public boolean IsNewRegimenActiveCheckBoxSelected() {
    return newRegimenActiveCheckBox.isSelected();
  }

  public String getAddedCategoryValue() {
     return addedCategory.getAttribute("value");
  }

  public String getNonEditableAddedCode(int indexOfCodeAdded) {
      testWebDriver.waitForElementToAppear(testWebDriver.getElementByXpath(baseRegimenDivXpath + "[" + indexOfCodeAdded + "]/div[2]/div/span"));
      return testWebDriver.getElementByXpath(baseRegimenDivXpath + "[" + indexOfCodeAdded + "]/div[2]/div/span").getText().trim();
  }

  public String getNonEditableAddedName(int indexOfCodeAdded) {
      testWebDriver.waitForElementToAppear(testWebDriver.getElementByXpath(baseRegimenDivXpath + "[" + indexOfCodeAdded + "]/div[2]/div/span"));
      return testWebDriver.getElementByXpath(baseRegimenDivXpath + "[" + indexOfCodeAdded + "]/div[3]/div/span").getText().trim();
  }

  public boolean getNonEditableAddedActiveCheckBox(int indexOfCodeAdded) {
      testWebDriver.waitForElementToAppear(testWebDriver.getElementByXpath(baseRegimenDivXpath + "[" + indexOfCodeAdded + "]/div[2]/div/span"));
      return testWebDriver.getElementByXpath(baseRegimenDivXpath + "[" + indexOfCodeAdded + "]/div[4]/input").isSelected();
  }

    public String getEditableAddedCode(int indexOfCodeAdded) {
        testWebDriver.waitForElementToAppear(testWebDriver.getElementByXpath(baseRegimenDivXpath + "[" + indexOfCodeAdded + "]/div[2]/input"));
        return testWebDriver.getAttribute(testWebDriver.getElementByXpath(baseRegimenDivXpath + "[" + indexOfCodeAdded + "]/div[2]/input"), "value").trim();
    }

    public String getEditableAddedName(int indexOfCodeAdded) {
        testWebDriver.waitForElementToAppear(testWebDriver.getElementByXpath(baseRegimenDivXpath + "[" + indexOfCodeAdded + "]/div[2]/input"));
        return testWebDriver.getAttribute(testWebDriver.getElementByXpath(baseRegimenDivXpath + "[" + indexOfCodeAdded + "]/div[3]/input"), "value").trim();
    }

    public boolean getEditableAddedActiveCheckBox(int indexOfCodeAdded) {
        testWebDriver.waitForElementToAppear(testWebDriver.getElementByXpath(baseRegimenDivXpath + "[" + indexOfCodeAdded + "]/div[2]/input"));
        return testWebDriver.getElementByXpath(baseRegimenDivXpath + "[" + indexOfCodeAdded + "]/div[4]/input").isSelected();
    }


  public String getTEMPLATE_SUCCESS_MESSAGE() {
    return TEMPLATE_SUCCESS_MESSAGE;
  }

  public void configureProgram(String program) throws InterruptedException {
    testWebDriver.waitForElementToAppear(testWebDriver.getElementByXpath("//a[@id='" + program + "']/span"));
    testWebDriver.getElementByXpath("//a[@id='" + program + "']/span").click();
    Thread.sleep(100);
    testWebDriver.getElementByXpath(".//*[@id='wrap']/div/div/div/div[2]/ul/li[2]/a").click();
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

  public void CancelRegime(String program) {
    testWebDriver.waitForElementToAppear(CancelButton);
    CancelButton.click();
    testWebDriver.waitForElementToAppear(testWebDriver.getElementByXpath("//a[@id='" + program + "']/span"));
  }

    private void sendKeys(WebElement locator, String value) {
        int length = testWebDriver.getAttribute(locator, "value").length();
        for (int i = 0; i < length; i++)
            locator.sendKeys("\u0008");
        locator.sendKeys(value);
    }
}