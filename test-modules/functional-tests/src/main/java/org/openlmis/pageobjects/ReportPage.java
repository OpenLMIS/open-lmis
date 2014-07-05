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

  @FindBy(how = ID, using = "viewHeader")
  private static WebElement viewHeader = null;

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

  @FindBy(how = ID, using = "pdfLink")
  private static WebElement PDF = null;

  @FindBy(how = ID, using = "xlsLink")
  private static WebElement XLS = null;

  @FindBy(how = ID, using = "csvLink")
  private static WebElement CSV = null;

  @FindBy(how = ID, using = "htmlLink")
  private static WebElement HTML = null;

  @FindBy(how = ID, using = "reportNameError")
  private static WebElement errorReportName = null;

  @FindBy(how = ID, using = "fileError")
  private static WebElement errorFile = null;


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

  public String getViewHeader() {
    testWebDriver.waitForElementToAppear(viewHeader);
    return viewHeader.getText();
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
    testWebDriver.waitForElementToAppear(PDF);
    return PDF.isDisplayed();
  }

  public boolean isHTMLLinkDisplayed() {
    testWebDriver.waitForElementToAppear(HTML);
    return HTML.isDisplayed();
  }

  public boolean isCSVLinkDisplayed() {
    testWebDriver.waitForElementToAppear(CSV);
    return CSV.isDisplayed();
  }

  public boolean isXLSLinkDisplayed() {
    testWebDriver.waitForElementToAppear(XLS);
    return XLS.isDisplayed();
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
}