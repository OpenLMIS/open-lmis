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