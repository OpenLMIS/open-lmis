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

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import static com.thoughtworks.selenium.SeleneseTestBase.assertEquals;
import static com.thoughtworks.selenium.SeleneseTestBase.assertTrue;
import static org.openqa.selenium.support.How.ID;
import static org.openqa.selenium.support.How.XPATH;


public class ReportPage extends RequisitionPage {

  @FindBy(how = XPATH, using = "//div[contains(text(),'No Reports')]")
  private static WebElement noReportsMessage;

  @FindBy(how = XPATH, using = "//a[contains(text(),'Add New')]")
  private static WebElement addNewButton;

  @FindBy(how = XPATH, using = "//h2[contains(text(),'Add new report')]")
  private static WebElement addNewReportTitle;

  @FindBy(how = ID, using = "name")
  private static WebElement reportNameTextField;

  @FindBy(how = ID, using = "file")
  private static WebElement uploadField;

  @FindBy(how = ID, using = "saveReport")
  private static WebElement saveButton;

  @FindBy(how = XPATH, using = "//a[contains(text(),'Cancel')]")
  private static WebElement cancelButton;

  @FindBy(how = XPATH, using = "//div[@id='saveSuccessMsgDiv']")
  private static WebElement saveSuccessMessage;

  @FindBy(how = ID, using = "error")
  private static WebElement saveErrorMessage;

  @FindBy(how = XPATH, using = "//span[contains(text(),'Active Facility Report')]")
  private static WebElement ActiveFacilityHeader;

  @FindBy(how = XPATH, using = "//a[contains(text(),'PDF')]")
  private static WebElement PDF;

  @FindBy(how = XPATH, using = "//a[contains(text(),'XLS')]")
  private static WebElement XLS;

  @FindBy(how = XPATH, using = "//a[contains(text(),'CSV')]")
  private static WebElement CSV;

  @FindBy(how = XPATH, using = "//a[contains(text(),'HTML')]")
  private static WebElement HTML;

  @FindBy(how = XPATH, using = "(//span[contains(text(),'Please fill this value')])[1]")
  private static WebElement errorReportName;

  @FindBy(how = XPATH, using = "(//span[contains(text(),'Please fill this value')])[2]")
  private static WebElement errorFile;


  public ReportPage(TestWebDriver driver) throws IOException {
    super(driver);
    PageFactory.initElements(new AjaxElementLocatorFactory(TestWebDriver.getDriver(), 10), this);
    testWebDriver.setImplicitWait(10);

  }

  public void verifyNoReportsMessage() {
    testWebDriver.waitForElementToAppear(noReportsMessage);
    assertTrue("No reports message should be displayed", noReportsMessage.isDisplayed());
  }

  public void clickAddNewButton() {
    testWebDriver.waitForElementToAppear(addNewButton);
    addNewButton.click();
    testWebDriver.waitForElementToAppear(addNewReportTitle);
  }

  public void verifyItemsOnReportUploadScreen() {
    assertTrue("Report Name field missing", reportNameTextField.isDisplayed());
    assertTrue("Upload field missing", uploadField.isDisplayed());
    assertTrue("Save button missing", saveButton.isDisplayed());
    assertTrue("Cancel button missing", cancelButton.isDisplayed());
  }

  public void verifyItemsOnReportListScreen() {
    assertTrue("PDF link missing", PDF.isDisplayed());
    assertTrue("XLS link missing", XLS.isDisplayed());
    assertTrue("CSV link missing", CSV.isDisplayed());
    assertTrue("HTML link missing", HTML.isDisplayed());
  }

  public void enterReportName(String reportName) {
    testWebDriver.waitForElementToAppear(reportNameTextField);
    reportNameTextField.clear();
    reportNameTextField.sendKeys(reportName);
  }

  public void uploadFile(String fileName) {
    String uploadFilePath;
    uploadFilePath = this.getClass().getClassLoader().getResource(fileName).getFile();
    uploadField.sendKeys(uploadFilePath);

  }

  public void clickSaveButton() {
    testWebDriver.waitForElementToAppear(saveButton);
    saveButton.click();
  }

  public void clickCancelButton() {
    testWebDriver.waitForElementToAppear(cancelButton);
    cancelButton.click();
  }

  public void verifySuccessMessageDiv() {
    testWebDriver.sleep(500);
    assertTrue("Report created successfully message not displayed", saveSuccessMessage.isDisplayed());
  }

  public void verifyErrorMessageDivFooter() {
    testWebDriver.sleep(2500);
    assertTrue("Report with same name already exists message should show up", saveErrorMessage.isDisplayed());
  }

  public void verifyErrorMessageInvalidFile() {
    testWebDriver.sleep(2500);
    assertEquals("File uploaded is invalid", saveErrorMessage.getText()) ;
  }

  public void verifyErrorMessageDivReportName() {
    testWebDriver.sleep(500);
    assertTrue("Error message 'Please fill this value' should show up", errorReportName.isDisplayed());
  }

  public void verifyErrorMessageDivUploadFile() {
    testWebDriver.sleep(500);
    assertTrue("Error message 'Please fill this value' should show up", errorFile.isDisplayed());
  }

  public void verifyReportNameInList(String reportName, int reportIndex) {
    WebElement element = testWebDriver.getElementByXpath("//div[@id='wrap']/div/div/div/table/tbody/tr[" + reportIndex + "]/td[1]/div");
    testWebDriver.waitForElementToAppear(element);
    assertTrue("Report Name '" + reportName + "' should display in list", element.getText().trim().equalsIgnoreCase(reportName));
  }


}