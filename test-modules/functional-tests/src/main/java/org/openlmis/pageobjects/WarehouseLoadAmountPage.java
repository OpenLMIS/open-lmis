/*
 * Copyright © 2013 VillageReach.  All Rights Reserved.  This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 *
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package org.openlmis.pageobjects;


import org.openlmis.UiUtils.TestWebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.pagefactory.AjaxElementLocatorFactory;

import java.io.IOException;


public class WarehouseLoadAmountPage extends Page {


  public WarehouseLoadAmountPage(TestWebDriver driver) throws IOException {
    super(driver);
    PageFactory.initElements(new AjaxElementLocatorFactory(TestWebDriver.getDriver(), 10), this);
    testWebDriver.setImplicitWait(10);

  }

  public String getFacilityName(int rowNumber, int tableNumber) {
    WebElement facilityName = testWebDriver.getElementByXpath("(//table[@class='table table-bordered table-striped']/tbody/tr[" + rowNumber + "]/td[1]/span[1])[" + tableNumber + "]");
    testWebDriver.waitForElementToAppear(facilityName);
    return facilityName.getText();
  }

  public String getFacilityCode(int rowNumber, int tableNumber) {
    WebElement facilityCode = testWebDriver.getElementByXpath("(//table[@class='table table-bordered table-striped']/tbody/tr[" + rowNumber + "]/td[1]/span[2])[" + tableNumber + "]");
    testWebDriver.waitForElementToAppear(facilityCode);
    return facilityCode.getText();
  }

  public String getFacilityPopulation(int rowNumber, int tableNumber) {
    WebElement population = testWebDriver.getElementByXpath("(//table[@class='table table-bordered table-striped']/tbody/tr[" + rowNumber + "]/td[2]/span[1])[" + tableNumber + "]");
    testWebDriver.waitForElementToAppear(population);
    return population.getText();
  }

  public String getGeoZoneName(int tableNumber) {
    WebElement geoZoneName = testWebDriver.getElementByXpath("(//h3/span)[" + tableNumber + "]");
    testWebDriver.waitForElementToAppear(geoZoneName);
    return geoZoneName.getText();
  }


  public String getProduct1Isa(int rowNumber, int tableNumber) {
    WebElement Product1 = testWebDriver.getElementByXpath("(//table[@class='table table-bordered table-striped']/tbody/tr[" + rowNumber + "]/td[3]/span[1])[" + tableNumber + "]");
    testWebDriver.waitForElementToAppear(Product1);
    return Product1.getText();
  }

  public String getProduct2Isa(int rowNumber, int tableNumber) {
    WebElement Product2 = testWebDriver.getElementByXpath("(//table[@class='table table-bordered table-striped']/tbody/tr[" + rowNumber + "]/td[4]/span[1])[" + tableNumber + "]");
    testWebDriver.waitForElementToAppear(Product2);
    return Product2.getText();
  }

  public String getNoRecordFoundMessage() {
    WebElement noRecordFoundMessage = testWebDriver.getElementByXpath("//div[@class='alert alert-info']/span");
    testWebDriver.waitForElementToAppear(noRecordFoundMessage);
    return noRecordFoundMessage.getText();
  }

}