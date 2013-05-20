/*
 * Copyright © 2013 VillageReach.  All Rights Reserved.  This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 *
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package org.openlmis.pageobjects;


import com.thoughtworks.selenium.SeleneseTestNgHelper;
import org.openlmis.UiUtils.SeleniumFileDownloadUtil;
import org.openlmis.UiUtils.TestWebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.How;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.pagefactory.AjaxElementLocatorFactory;

import java.io.File;
import java.io.IOException;

import static org.openqa.selenium.support.How.ID;
import static org.openqa.selenium.support.How.NAME;


public class StockedOutReportPage extends Page {

  @FindBy(how = NAME, using = "program")
  private static WebElement program;

  @FindBy(how = NAME, using = "schedule")
  private static WebElement schedule;

  @FindBy(how = NAME, using = "period")
  private static WebElement period;

  @FindBy(how = NAME, using = "requisitionGroup")
  private static WebElement  requisitionGroup;

  @FindBy(how = NAME, using = "facilityTypeElement")
  private static WebElement facilityType;


  @FindBy(how = How.XPATH, using = "//div[@ng-grid='gridOptions']")
  private static WebElement stockedOutReportListGrid;

  @FindBy(how = How.XPATH, using = "//div[@class='ngCellText ng-scope col0 colt0']/span")
  private static WebElement firstRowCodeColumn;

  @FindBy(how = How.XPATH, using = "//div[@class='ngCellText ng-scope col1 colt1']/span")
  private static WebElement firstRowFacilityNameColumn;

  @FindBy(how = How.XPATH, using = "//div[@class='ngCellText ng-scope col2 colt2']/span")
  private static WebElement  firstRowFacilityTypeColumn;

  @FindBy(how = How.XPATH, using = "//div[@class='ngCellText ng-scope col3 colt3']/span")
  private static WebElement  firstRowLocationColumn;


  @FindBy(how = ID, using = "pdf-button")
  private static WebElement PdfButton;

  @FindBy(how = ID, using = "xls-button")
  private static WebElement XLSButton;

  public StockedOutReportPage(TestWebDriver driver) throws IOException {
    super(driver);
    PageFactory.initElements(new AjaxElementLocatorFactory(testWebDriver.getDriver(), 10), this);
    testWebDriver.setImplicitWait(10);

  }

  public void enterFilterValues(String programValue, String scheduleValue, String periodValue, String requesitionGroupValue,
                                String facilityTypeValue){

      testWebDriver.waitForElementToAppear(program);
      testWebDriver.selectByVisibleText(program, programValue);
      testWebDriver.selectByVisibleText(schedule, scheduleValue);
      testWebDriver.selectByVisibleText(period, periodValue);
      testWebDriver.selectByVisibleText(requisitionGroup, requesitionGroupValue);
      testWebDriver.selectByVisibleText(facilityType, facilityTypeValue);

      testWebDriver.sleep(500);
  }

  public void verifyHTMLReportOutput(){

      testWebDriver.waitForElementToAppear(stockedOutReportListGrid);
      testWebDriver.waitForElementToAppear(firstRowCodeColumn);

      testWebDriver.sleep(500);

  }

  public void verifyPdfReportOutput() throws Exception {
      testWebDriver.waitForElementToAppear(PdfButton);
      PdfButton.click();
      testWebDriver.sleep(500);

      SeleniumFileDownloadUtil downloadHandler = new SeleniumFileDownloadUtil(TestWebDriver.getDriver());
      downloadHandler.setURI(testWebDriver.getCurrentUrl());
      File downloadedFile = downloadHandler.downloadFile(this.getClass().getSimpleName(), ".pdf");
      SeleneseTestNgHelper.assertEquals(downloadHandler.getLinkHTTPStatus(),200);
      SeleneseTestNgHelper.assertEquals(downloadedFile.exists(), true);

      testWebDriver.sleep(500);
    }

}