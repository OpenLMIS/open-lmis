/*
 * Copyright © 2013 VillageReach.  All Rights Reserved.  This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 *
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
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

import static com.thoughtworks.selenium.SeleneseTestBase.assertTrue;
import static org.openqa.selenium.support.How.ID;
import static org.openqa.selenium.support.How.XPATH;


public class ReportPage extends RequisitionPage {

  @FindBy(how = XPATH, using = "//div[contains(text(),'No Reports')]")
  private static WebElement noReportsMessage;

  @FindBy(how = XPATH, using = "//a[contains(text(),'Add new')]")
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
    PageFactory.initElements(new AjaxElementLocatorFactory(testWebDriver.getDriver(), 10), this);
    testWebDriver.setImplicitWait(10);

  }

  public void verifyNoReportsMessage()
  {
    testWebDriver.waitForElementToAppear(noReportsMessage);
    assertTrue("No reports message should be displayed", noReportsMessage.isDisplayed());
  }

  public void clickAddNewButton()
  {
    testWebDriver.waitForElementToAppear(addNewButton);
    addNewButton.click();
    testWebDriver.waitForElementToAppear(addNewReportTitle);
  }

  public void verifyItemsOnReportUploadScreen()
  {
   assertTrue("Report Name field missing",reportNameTextField.isDisplayed());
   assertTrue("Upload field missing",uploadField.isDisplayed());
   assertTrue("Save button missing",saveButton.isDisplayed());
   assertTrue("Cancel button missing",cancelButton.isDisplayed());
  }

  public void verifyItemsOnReportListScreen()
  {
    assertTrue("PDF link missing",PDF.isDisplayed());
    assertTrue("XLS link missing",XLS.isDisplayed());
    assertTrue("CSV link missing",CSV.isDisplayed());
    assertTrue("HTML link missing",HTML.isDisplayed());
  }

  public void enterReportName(String reportName)
  {
    testWebDriver.waitForElementToAppear(reportNameTextField);
    reportNameTextField.clear();
    reportNameTextField.sendKeys(reportName);
  }

  public void uploadFile(String fileName) {
    String  uploadFilePath;
    uploadFilePath = this.getClass().getClassLoader().getResource(fileName).getFile();
    uploadField.sendKeys(uploadFilePath);

  }

  public void clickSaveButton()
  {
    testWebDriver.waitForElementToAppear(saveButton);
    saveButton.click();
  }

  public void verifySuccessMessageDiv()
  {
    testWebDriver.sleep(500);
    assertTrue("Report created successfully message not displayed",saveSuccessMessage.isDisplayed());
  }

  public void verifyErrorMessageDivReportName()
  {
    testWebDriver.sleep(500);
    assertTrue("Error message 'Please fill this value' should show up",errorReportName.isDisplayed());
  }

  public void verifyErrorMessageDivUploadFile()
  {
    testWebDriver.sleep(500);
    assertTrue("Error message 'Please fill this value' should show up",errorFile.isDisplayed());
  }

  public void verifyReportNameInList(String reportName,int reportIndex)
  {
    WebElement element=testWebDriver.getElementByXpath("//div[@id='wrap']/div/div/div/table/tbody/tr["+reportIndex+"]/td[1]/div");
    testWebDriver.waitForElementToAppear(element);
    assertTrue("Report Name '"+reportName+"' should display in list",element.getText().trim().equalsIgnoreCase(reportName));
  }
   public String getCurrentDateAndTime()
   {
     Date dObj = new Date();
     SimpleDateFormat formatter_date_time = new SimpleDateFormat(
       "yyyyMMdd-hhmmss");
     return formatter_date_time.format(dObj);
   }



}