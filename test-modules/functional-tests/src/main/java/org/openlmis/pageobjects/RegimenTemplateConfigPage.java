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

import static com.thoughtworks.selenium.SeleneseTestBase.assertEquals;
import static com.thoughtworks.selenium.SeleneseTestBase.assertTrue;


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

  @FindBy(how = How.XPATH, using = "//a[contains(text(),'Reporting fields')]")
  private static WebElement reportingFieldsTab;

  @FindBy(how = How.XPATH, using = "//div[@id='reportingFields']/div[2]/div[1]/input")
  private static WebElement noOfPatientsOnTreatmentCheckBox;

  @FindBy(how = How.XPATH, using = "//div[@id='reportingFields']/div[3]/div[1]/input")
  private static WebElement noOfPatientsToInitiateTreatmentCheckBox;

  @FindBy(how = How.XPATH, using = "//div[@id='reportingFields']/div[4]/div[1]/input")
  private static WebElement noOfPatientsStoppedTreatmentCheckBox;

  @FindBy(how = How.XPATH, using = "//div[@id='reportingFields']/div[5]/div[1]/input")
  private static WebElement remarksCheckBox;

  @FindBy(how = How.XPATH, using = "//div[@id='reportingFields']/div[2]/div[2]/input")
  private static WebElement noOfPatientsOnTreatmentTextField;

  @FindBy(how = How.XPATH, using = "//div[@id='reportingFields']/div[3]/div[2]/input")
  private static WebElement noOfPatientsToInitiateTreatmentTextField;

  @FindBy(how = How.XPATH, using = "//div[@id='reportingFields']/div[4]/div[2]/input")
  private static WebElement noOfPatientsStoppedTreatmentTextField;

  @FindBy(how = How.XPATH, using = "//div[@id='reportingFields']/div[5]/div[2]/input")
  private static WebElement remarksTextField;

  @FindBy(how = How.XPATH, using = "//div[@id='reportingFields']/div[2]/div[3]/span")
  private static WebElement noOfPatientsOnTreatmentDataType;

  @FindBy(how = How.XPATH, using = "//div[@id='reportingFields']/div[3]/div[3]/span")
  private static WebElement noOfPatientsToInitiateTreatmentDataType;

  @FindBy(how = How.XPATH, using = "//div[@id='reportingFields']/div[4]/div[3]/span")
  private static WebElement noOfPatientsStoppedTreatmentDataType;

  @FindBy(how = How.XPATH, using = "//div[@id='reportingFields']/div[5]/div[3]/span")
  private static WebElement remarksDataType;

  @FindBy(how = How.XPATH, using = "//div[1][@class='row-fluid rnr-template-columns ng-scope']/div[2][@class='span2']/div/span[@class='ng-binding']")
  private static WebElement addedCode;

  @FindBy(how = How.XPATH, using = "//div[1][@class='row-fluid rnr-template-columns ng-scope']/div[3][@class='span4']/div/span[@class='ng-binding']")
  private static WebElement addedName;

  @FindBy(how = How.XPATH, using = "//div[2][@class='ui-sortable ng-scope ng-pristine ng-valid']/div[1][@class='category-name']/div[@class='ng-binding']")
  private static WebElement addedCategory;

    private static String TEMPLATE_SUCCESS_MESSAGE = "Template saved successfully!";

  public RegimenTemplateConfigPage(TestWebDriver driver) {
    super(driver);
    PageFactory.initElements(new AjaxElementLocatorFactory(TestWebDriver.getDriver(), 10), this);
    testWebDriver.setImplicitWait(10);
  }

  public  WebElement getRegimenTemplateHeader() {
    return regimenTemplateHeader;
  }

  public  WebElement getRegimenTab() {
    return regimenTab;
  }

  public  WebElement getReportingFieldsTab() {
    return reportingFieldsTab;
  }

  public  WebElement getNoOfPatientsOnTreatmentCheckBox() {
    return noOfPatientsOnTreatmentCheckBox;
  }

  public  WebElement getNoOfPatientsToInitiateTreatmentCheckBox() {
    return noOfPatientsToInitiateTreatmentCheckBox;
  }

  public  WebElement getNoOfPatientsStoppedTreatmentCheckBox() {
    return noOfPatientsStoppedTreatmentCheckBox;
  }

  public  WebElement getRemarksCheckBox() {
    return remarksCheckBox;
  }

  public  WebElement getNoOfPatientsOnTreatmentTextField() {
    return noOfPatientsOnTreatmentTextField;
  }

  public  WebElement getNoOfPatientsToInitiateTreatmentTextField() {
    return noOfPatientsToInitiateTreatmentTextField;
  }

  public  WebElement getNoOfPatientsStoppedTreatmentTextField() {
    return noOfPatientsStoppedTreatmentTextField;
  }

  public  WebElement getRemarksTextField() {
    return remarksTextField;
  }

  public  WebElement getNoOfPatientsOnTreatmentDataType() {
    return noOfPatientsOnTreatmentDataType;
  }

  public  WebElement getNoOfPatientsToInitiateTreatmentDataType() {
    return noOfPatientsToInitiateTreatmentDataType;
  }

  public  WebElement getNoOfPatientsStoppedTreatmentDataType() {
    return noOfPatientsStoppedTreatmentDataType;
  }

  public  WebElement getRemarksDataType() {
    return remarksDataType;
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

  public  WebElement getDoneButton() {
    return doneButton;
  }

  public WebElement getLogoutLink() {
    return logoutLink;
  }

  public WebElement getDoneFailMessage() {
    return doneFailMessage;
  }

  public WebElement getSaveButton() {
    return SaveButton;
  }

  public WebElement getCancelButton() {
    return CancelButton;
  }

  public WebElement getChangeLink() {
    return changeLink;
  }

  public WebElement getSaveSuccessMsgDiv() {
    return saveSuccessMsgDiv;
  }

  public WebElement getSaveErrorMsgDiv() {
    return saveErrorMsgDiv;
  }

  public WebElement getNewRegimenCategoryDropDown() {
    return newRegimenCategoryDropDown;
  }

  public WebElement getNewRegimenCodeTextBox() {
    return newRegimenCodeTextBox;
  }

  public WebElement getNewRegimenNameTextBox() {
    return newRegimenNameTextBox;
  }

  public WebElement getNewRegimenActiveCheckBox() {
    return newRegimenActiveCheckBox;
  }

  public String getAddedCategory() {
     return addedCategory.getAttribute("value");
  }

  public String getAddedCode() {
      return addedCode.getText();
  }

    public String getAddedName() {
        return addedName.getText();
    }

  public WebElement getAddButton() {
    return addButton;
  }

  public WebElement getEditButton() {
    return editButton;
  }

  public String getTEMPLATE_SUCCESS_MESSAGE() {
    return TEMPLATE_SUCCESS_MESSAGE;
  }

  public void configureProgram(String program) {
    testWebDriver.waitForElementToAppear(testWebDriver.getElementByXpath("//a[@id='" + program + "']/span"));
    testWebDriver.getElementByXpath("//a[@id='" + program + "']/span").click();
    testWebDriver.waitForElementToAppear(addButton);
  }

  public void clickEditProgram(String program) {
    testWebDriver.waitForElementToAppear(testWebDriver.getElementByXpath("//a[@id='" + program + "']"));
    testWebDriver.getElementByXpath("//a[@id='" + program + "']").click();
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

  public void CancelRegime() {
    testWebDriver.waitForElementToAppear(CancelButton);
    CancelButton.click();
    testWebDriver.waitForElementToAppear(regimenTemplateHeader);
  }

}