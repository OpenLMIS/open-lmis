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

import static org.openqa.selenium.support.How.ID;

public class ReportPage extends Page {

  @FindBy(how = ID, using = "reportHeader")
  private static WebElement reportHeader = null;

  @FindBy(how = ID, using = "noReportsMessage")
  private static WebElement noReportsMessage = null;

  @FindBy(how = ID, using = "reportNameHeader")
  private static WebElement reportNameHeader = null;

  @FindBy(how = ID, using = "descriptionHeader")
  private static WebElement descriptionHeader = null;

  @FindBy(how = ID, using = "addNew")
  private static WebElement addNewButton = null;

  @FindBy(how = ID, using = "addNewHeader")
  private static WebElement addNewReportTitle = null;

  @FindBy(how = ID, using = "nameLabel")
  private static WebElement reportNameLabel = null;

  @FindBy(how = ID, using = "descriptionLabel")
  private static WebElement reportDescriptionLabel = null;

  @FindBy(how = ID, using = "uploadFileLabel")
  private static WebElement uploadFieldLabel = null;

  @FindBy(how = ID, using = "name")
  private static WebElement reportNameTextField = null;

  @FindBy(how = ID, using = "description")
  private static WebElement reportDescriptionTextField = null;

  @FindBy(how = ID, using = "file")
  private static WebElement uploadField = null;

  @FindBy(how = ID, using = "saveReport")
  private static WebElement saveButton = null;

  @FindBy(how = ID, using = "cancelButton")
  private static WebElement cancelButton = null;

  @FindBy(how = ID, using = "saveSuccessMsgDiv")
  private static WebElement saveSuccessMessage = null;

  @FindBy(how = ID, using = "error")
  private static WebElement saveErrorMessage = null;

  @FindBy(how = ID, using = "pdfButton")
  private static WebElement pdfButton = null;

  @FindBy(how = ID, using = "xlsButton")
  private static WebElement xlsButton = null;

  @FindBy(how = ID, using = "csvButton")
  private static WebElement csvButton = null;

  @FindBy(how = ID, using = "htmlButton")
  private static WebElement htmlButton = null;

  @FindBy(how = ID, using = "pdfTableButton")
  private static WebElement pdfTableButton = null;

  @FindBy(how = ID, using = "xlsTableButton")
  private static WebElement xlsTableButton = null;

  @FindBy(how = ID, using = "csvTableButton")
  private static WebElement csvTableButton = null;

  @FindBy(how = ID, using = "htmlTableButton")
  private static WebElement htmlTableButton = null;

  @FindBy(how = ID, using = "reportNameError")
  private static WebElement errorReportName = null;

  @FindBy(how = ID, using = "fileError")
  private static WebElement errorFile = null;

  @FindBy(how = ID, using = "reportName")
  private static WebElement reportName = null;

  @FindBy(how = How.XPATH, using = "//span[contains(text(),'Prev')]")
  private static WebElement prevCalender = null;

  @FindBy(how = How.XPATH, using = "//a[@class='ui-state-default' and contains(text(),'1')]")
  private static WebElement dateInCalender = null;

  public ReportPage(TestWebDriver driver) {
    super(driver);
    PageFactory.initElements(new AjaxElementLocatorFactory(TestWebDriver.getDriver(), 10), this);
    testWebDriver.setImplicitWait(10);
  }

  public String getReportHeader() {
    testWebDriver.waitForElementToAppear(reportHeader);
    return reportHeader.getText();
  }

  public String getReportNameHeader() {
    testWebDriver.waitForElementToAppear(reportNameHeader);
    return reportNameHeader.getText();
  }

  public String getDescriptionHeader() {
    testWebDriver.waitForElementToAppear(descriptionHeader);
    return descriptionHeader.getText();
  }

  public String getNameLabel() {
    testWebDriver.waitForElementToAppear(reportNameLabel);
    return reportNameLabel.getText();
  }

  public String getDescriptionLabel() {
    testWebDriver.waitForElementToAppear(reportDescriptionLabel);
    return reportDescriptionLabel.getText();
  }

  public String getUploadFileLabel() {
    testWebDriver.waitForElementToAppear(uploadFieldLabel);
    return uploadFieldLabel.getText();
  }

  public void clickAddNewButton() {
    testWebDriver.sleep(1000);
    testWebDriver.waitForElementToAppear(addNewButton);
    addNewButton.click();
    testWebDriver.waitForElementToAppear(addNewReportTitle);
  }

  public void enterReportName(String reportName) {
    testWebDriver.waitForElementToAppear(reportNameTextField);
    sendKeys(reportNameTextField, reportName);
  }

  public void enterReportDescription(String reportName) {
    testWebDriver.waitForElementToAppear(reportDescriptionTextField);
    sendKeys(reportDescriptionTextField, reportName);
  }

  public void uploadFile(String fileName) {
    String uploadFilePath;
    uploadFilePath = this.getClass().getClassLoader().getResource(fileName).getFile();
    sendKeys(uploadField, uploadFilePath);
  }

  public void clickSaveButton() {
    testWebDriver.waitForElementToAppear(saveButton);
    saveButton.click();
  }

  public void clickCancelButton() {
    testWebDriver.waitForElementToAppear(cancelButton);
    cancelButton.click();
  }

  public boolean isSaveButtonDisplayed() {
    testWebDriver.waitForElementToAppear(saveButton);
    return saveButton.isDisplayed();
  }

  public boolean isCancelButtonDisplayed() {
    testWebDriver.waitForElementToAppear(cancelButton);
    return cancelButton.isDisplayed();
  }

  public boolean isPDFLinkDisplayed() {
    testWebDriver.waitForElementToAppear(pdfButton);
    return pdfButton.isDisplayed();
  }

  public boolean isHTMLLinkDisplayed() {
    testWebDriver.waitForElementToAppear(htmlButton);
    return htmlButton.isDisplayed();
  }

  public boolean isCSVLinkDisplayed() {
    testWebDriver.waitForElementToAppear(csvButton);
    return csvButton.isDisplayed();
  }

  public boolean isXLSLinkDisplayed() {
    testWebDriver.waitForElementToAppear(xlsButton);
    return xlsButton.isDisplayed();
  }

  public boolean isPDFTableLinkDisplayed() {
    testWebDriver.waitForElementToAppear(pdfTableButton);
    return pdfTableButton.isDisplayed();
  }

  public boolean isHTMLTableLinkDisplayed() {
    testWebDriver.waitForElementToAppear(htmlTableButton);
    return htmlTableButton.isDisplayed();
  }

  public boolean isCSVTableLinkDisplayed() {
    testWebDriver.waitForElementToAppear(csvTableButton);
    return csvTableButton.isDisplayed();
  }

  public boolean isXLSTableLinkDisplayed() {
    testWebDriver.waitForElementToAppear(xlsTableButton);
    return xlsTableButton.isDisplayed();
  }

  public String getSaveSuccessMessage() {
    testWebDriver.waitForElementToAppear(saveSuccessMessage);
    return saveSuccessMessage.getText();
  }

  public String getSaveErrorMessage() {
    testWebDriver.waitForElementToAppear(saveErrorMessage);
    return saveErrorMessage.getText();
  }

  public String getErrorReportNameMessage() {
    testWebDriver.waitForElementToAppear(errorReportName);
    return errorReportName.getText();
  }

  public String getErrorFileMessage() {
    testWebDriver.waitForElementToAppear(errorFile);
    return errorFile.getText();
  }

  public String getReportName(int reportIndex) {
    WebElement element = testWebDriver.getElementById("reportName" + (reportIndex - 1));
    testWebDriver.waitForElementToAppear(element);
    return element.getText().trim();
  }

  public String getNoReportsMessage() {
    testWebDriver.waitForElementToAppear(noReportsMessage);
    return noReportsMessage.getText();
  }

  public String getReportDescription(int reportIndex) {
    WebElement element = testWebDriver.getElementById("reportDescription" + (reportIndex - 1));
    testWebDriver.waitForElementToAppear(element);
    return element.getText().trim();
  }

  public void clickReport(int reportIndex) {
    WebElement element = testWebDriver.getElementById("reportName" + (reportIndex - 1));
    testWebDriver.waitForElementToAppear(element);
    element.click();
  }

  public String getReportName() {
    testWebDriver.waitForElementToAppear(reportName);
    return reportName.getText();
  }

  public String getParameterDisplayName(String displayName) {
    WebElement element = testWebDriver.getElementById("displayName_" + displayName);
    testWebDriver.waitForElementToAppear(element);
    return element.getText().trim();
  }

  public String getParameterDescription(String displayName) {
    WebElement element = testWebDriver.getElementById("description_" + displayName);
    testWebDriver.waitForElementToAppear(element);
    return element.getText().trim();
  }

  public String getParameterDate(String displayName) {
    WebElement element = testWebDriver.getElementById("date_" + displayName);
    testWebDriver.waitForElementToAppear(element);
    return element.getAttribute("value");
  }

  public boolean isParameterTrueOptionSelected(String displayName) {
    WebElement element = testWebDriver.getElementById("true_" + displayName);
    testWebDriver.waitForElementToAppear(element);
    return element.isSelected();
  }

  public boolean isParameterFalseOptionSelected(String displayName) {
    WebElement element = testWebDriver.getElementById("false_" + displayName);
    testWebDriver.waitForElementToAppear(element);
    return element.isSelected();
  }

  public String getParameterString(String displayName) {
    WebElement element = testWebDriver.getElementById("string_" + displayName);
    testWebDriver.waitForElementToAppear(element);
    return element.getAttribute("value");
  }

  public String getParameterInt(String displayName) {
    WebElement element = testWebDriver.getElementById("integer_" + displayName);
    testWebDriver.waitForElementToAppear(element);
    return element.getAttribute("value");
  }

  public String getParameterFloat(String displayName) {
    WebElement element = testWebDriver.getElementById("float_" + displayName);
    testWebDriver.waitForElementToAppear(element);
    return element.getAttribute("value");
  }

  public String getUnSupportedDataTypeText(String displayName) {
    WebElement element = testWebDriver.getElementById("unSupportedDataType_" + displayName);
    testWebDriver.waitForElementToAppear(element);
    return element.getText();
  }

  public void selectParameterDate(String displayName) {
    WebElement element = testWebDriver.getElementById("date_" + displayName);
    testWebDriver.waitForElementToAppear(element);
    testWebDriver.sleep(1500);
    element.click();
    testWebDriver.waitForElementToAppear(prevCalender);
    prevCalender.click();
    testWebDriver.waitForElementToAppear(dateInCalender);
    dateInCalender.click();
    testWebDriver.sleep(500);
  }

  public void selectParameterFalseOption(String displayName) {
    WebElement element = testWebDriver.getElementById("false_" + displayName);
    testWebDriver.waitForElementToAppear(element);
    element.click();
  }

  public void enterStringParameterInput(String displayName, String input) {
    WebElement element = testWebDriver.getElementById("string_" + displayName);
    testWebDriver.waitForElementToAppear(element);
    sendKeys(element, input);
  }

  public void enterIntParameterInput(String displayName, String input) {
    WebElement element = testWebDriver.getElementById("integer_" + displayName);
    testWebDriver.waitForElementToAppear(element);
    sendKeys(element, input);
  }

  public void enterFloatParameterInput(String displayName, String input) {
    WebElement element = testWebDriver.getElementById("float_" + displayName);
    testWebDriver.waitForElementToAppear(element);
    sendKeys(element, input);
  }

  public void clickCsvLink() {
    testWebDriver.waitForElementToAppear(csvButton);
    csvButton.click();
  }

  public void clickCsvTableLink() {
    testWebDriver.waitForElementToAppear(csvTableButton);
    csvTableButton.click();
  }
}