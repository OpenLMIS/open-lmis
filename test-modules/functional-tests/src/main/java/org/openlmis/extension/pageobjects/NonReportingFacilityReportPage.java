/*
 * This program was produced for the U.S. Agency for International Development. It was prepared by the USAID | DELIVER PROJECT, Task Order 4. It is part of a project which utilizes code originally licensed under the terms of the Mozilla Public License (MPL) v2 and therefore is licensed under MPL v2 or later.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the Mozilla Public License as published by the Mozilla Foundation, either version 2 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the Mozilla Public License for more details.
 *
 * You should have received a copy of the Mozilla Public License along with this program. If not, see http://www.mozilla.org/MPL/
 */

package org.openlmis.extension.pageobjects;


import com.thoughtworks.selenium.SeleneseTestNgHelper;
import org.openlmis.UiUtils.SeleniumFileDownloadUtil;
import org.openlmis.UiUtils.TestWebDriver;
import org.openlmis.pageobjects.Page;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.How;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.pagefactory.AjaxElementLocatorFactory;

import java.io.File;
import java.io.IOException;

import static org.openqa.selenium.support.How.ID;
import static org.openqa.selenium.support.How.NAME;


public class NonReportingFacilityReportPage extends Page {

  @FindBy(how = NAME, using = "requisitionGroup")
  private static WebElement requisitionGroup;

  @FindBy(how = NAME, using = "facilityTypeElement")
  private static WebElement facilityTypeElement;

  @FindBy(how = NAME, using = "period")
  private static WebElement period;

  @FindBy(how = NAME, using = "program")
  private static WebElement program;

  @FindBy(how = NAME, using = "schedule")
  private static WebElement schedule;



  @FindBy(how = How.XPATH, using = "//div[@ng-grid='gridOptions']")
  private static WebElement nonReportingFacilityReportListGrid;

  @FindBy(how = How.XPATH, using = "//div[@class='ngCellText ng-scope col3 colt3']/span")
  private static WebElement firstRowCodeColumn;

  @FindBy(how = How.XPATH, using = "//div[@class='ngCellText ng-scope col7 colt7']/span")
  private static WebElement firstRowFacilityNameColumn;

  @FindBy(how = How.XPATH, using = "//div[@class='ngCellText ng-scope col2 colt2']/span")
  private static WebElement firstRowFacilityTypeColumn;

  @FindBy(how = How.XPATH, using = "//div[@class='ngCellText ng-scope col4 colt4']/span")
  private static WebElement firstRowLocationColumn;

  @FindBy(how = ID, using = "pdf-button")
  private static WebElement PdfButton;

  @FindBy(how = ID, using = "xls-button")
  private static WebElement XLSButton;

  public NonReportingFacilityReportPage(TestWebDriver driver)  {
    super(driver);
    PageFactory.initElements(new AjaxElementLocatorFactory(testWebDriver.getDriver(), 10), this);
    testWebDriver.setImplicitWait(10);

  }

  public void enterFilterValues(String programValue, String scheduleValue, String periodValue, String reportingGroupValue, String facilityTypeValue){
      testWebDriver.waitForElementToAppear(program);
      testWebDriver.selectByVisibleText(program,programValue);
      testWebDriver.selectByVisibleText(schedule, scheduleValue);
      testWebDriver.waitForElementToAppear(requisitionGroup);
      testWebDriver.selectByVisibleText(requisitionGroup,reportingGroupValue);
      testWebDriver.waitForElementToAppear(facilityTypeElement);
      testWebDriver.selectByVisibleText(facilityTypeElement, facilityTypeValue);
      testWebDriver.waitForElementToAppear(period);
      testWebDriver.selectByVisibleText(period, periodValue);
      testWebDriver.sleep(500);
  }

  public void verifyHTMLReportOutput(){

      testWebDriver.waitForElementToAppear(nonReportingFacilityReportListGrid);
      testWebDriver.waitForElementToAppear(firstRowCodeColumn);
      SeleneseTestNgHelper.assertEquals(firstRowCodeColumn.getText().trim(), "F11");
      SeleneseTestNgHelper.assertEquals(firstRowFacilityNameColumn.getText().trim(),"Central Hospital");
      SeleneseTestNgHelper.assertEquals(firstRowFacilityTypeColumn.getText().trim(),"Lvl3 Hospital");
      SeleneseTestNgHelper.assertEquals(firstRowLocationColumn.getText().trim(),"Root");

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