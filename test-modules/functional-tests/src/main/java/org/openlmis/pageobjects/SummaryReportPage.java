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

import java.io.IOException;

import static org.openqa.selenium.support.How.NAME;


public class SummaryReportPage extends Page {

  @FindBy(how = NAME, using = "period")
  private static WebElement period;

  /*@FindBy(how = NAME, using = "facilityType")
  private static WebElement facilityType;

  @FindBy(how = NAME, using = "status")
  private static WebElement status;*/

  @FindBy(how = How.XPATH, using = "//div[@ng-grid='gridOptions']")
  private static WebElement summaryReportListGrid;

  @FindBy(how = How.XPATH, using = "//div[@class='ngCellText ng-scope col3 colt3']/span")
  private static WebElement columnZone;

  @FindBy(how = How.XPATH, using = "//div[@class='ngCellText ng-scope col7 colt7']/span")
  private static WebElement columnActive;

  @FindBy(how = How.XPATH, using = "//div[@class='ngCellText ng-scope col2 colt2']/span")
  private static WebElement columnFacilityType;

  @FindBy(how = How.XPATH, using = "//div[@class='ngCellText ng-scope col4 colt4']/span")
  private static WebElement columnBeginingBalance;

  @FindBy(how = How.XPATH, using = "//div[@class='ngCellText ng-scope col5 colt5']/span")
  private static WebElement columnReceived;

  @FindBy(how = How.XPATH, using = "//div[@class='ngCellText ng-scope col6 colt6']/span")
  private static WebElement columnDispensed;

  public SummaryReportPage(TestWebDriver driver) throws IOException {
    super(driver);
    PageFactory.initElements(new AjaxElementLocatorFactory(testWebDriver.getDriver(), 10), this);
    testWebDriver.setImplicitWait(10);

  }

  public void enterFilterValuesInSummaryReport(String periodValue){

      testWebDriver.waitForElementToAppear(period);
      testWebDriver.selectByVisibleText(period,periodValue);
      testWebDriver.sleep(500);
  }

  public void verifyHTMLReportOutputOnSummaryReportScreen(){

    testWebDriver.waitForElementToAppear(summaryReportListGrid);
    testWebDriver.sleep(500);
    SeleneseTestNgHelper.assertEquals(columnBeginingBalance.getText().trim(), 1);

    SeleneseTestNgHelper.assertEquals(columnReceived.getText().trim(), 1);

    SeleneseTestNgHelper.assertEquals(columnDispensed.getText().trim(), 1);
  }

}