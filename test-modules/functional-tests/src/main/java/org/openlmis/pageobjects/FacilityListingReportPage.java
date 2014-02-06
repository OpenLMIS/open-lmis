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

import static org.openqa.selenium.support.How.ID;
import static org.openqa.selenium.support.How.NAME;


public class FacilityListingReportPage extends Page {

  @FindBy(how = NAME, using = "zone")
  private static WebElement zone;

  @FindBy(how = NAME, using = "facilityType")
  private static WebElement facilityType;

  @FindBy(how = NAME, using = "status")
  private static WebElement status;

  @FindBy(how = How.XPATH, using = "//div[@ng-grid='gridOptions']")
  private static WebElement facilityListGrid;

  @FindBy(how = How.XPATH, using = "//div[@class='ngCellText ng-scope col3 colt3']/span")
  private static WebElement columnZone;

  @FindBy(how = How.XPATH, using = "//div[@class='ngCellText ng-scope col7 colt7']/span")
  private static WebElement columnActive;

  @FindBy(how = How.XPATH, using = "//div[@class='ngCellText ng-scope col2 colt2']/span")
  private static WebElement columnFacilityType;

  private String zoneFilter;
  private String facilityTypeFilter;
  private String statusFilter;

  public FacilityListingReportPage(TestWebDriver driver) {
    super(driver);
    PageFactory.initElements(new AjaxElementLocatorFactory(testWebDriver.getDriver(), 10), this);
    testWebDriver.setImplicitWait(10);

  }

  public void enterFilterValuesInFacilityListingReport(String zoneValue, String facilityTypeValue, String statusValue){

      zoneFilter = zoneValue;
      facilityTypeFilter = facilityTypeValue;
      statusFilter = statusValue;

      testWebDriver.waitForElementToAppear(zone);
      testWebDriver.selectByVisibleText(zone, zoneValue);
      testWebDriver.selectByVisibleText(facilityType, facilityTypeValue);
      testWebDriver.selectByValue(status, "1");
      testWebDriver.sleep(500);
  }

  public void verifyHTMLReportOutputOnFacilityListingScreen(){

    //verify facility list grid has the filtered record
    testWebDriver.waitForElementToAppear(facilityListGrid);

    SeleneseTestNgHelper.assertEquals(columnFacilityType.getText().trim(), facilityTypeFilter);

    SeleneseTestNgHelper.assertEquals(columnZone.getText().trim(), zoneFilter);

    SeleneseTestNgHelper.assertEquals(columnActive.getText().trim(), statusFilter);
  }

}