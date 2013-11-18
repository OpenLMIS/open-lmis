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

  public String getGeoZoneNameTitle(int rowNumber) {
    WebElement city = testWebDriver.getElementByXpath("//table[@id='aggregateTable']/tbody/tr["+rowNumber+"]/td[1]/span[1]");
    testWebDriver.waitForElementToAppear(city);
    return city.getText();
  }

  public String getGeoZoneTotalCaption(int tableNumber) {
    WebElement geoZone = testWebDriver.getElementByXpath("(//table[@class='table table-bordered table-striped']/tbody/tr[3]/td[1]/span[1])["+tableNumber+"]");
    testWebDriver.waitForElementToAppear(geoZone);
    return geoZone.getText();
  }

  public String getCitiesFromAggregatedTable(int rowNumber) {
    WebElement cities = testWebDriver.getElementByXpath("//table[@id='aggregateTable']/tbody/tr["+rowNumber+"]/td[1]/span");
    testWebDriver.waitForElementToAppear(cities);
    return cities.getText();
  }

  public String getAggregateTableCaption() {
    WebElement caption = testWebDriver.getElementByXpath("//table[@id='aggregateTable']/tbody/tr[3]/td[1]/span");
    testWebDriver.waitForElementToAppear(caption);
    return caption.getText();
  }

  public String getAggregatePopulation(int rowNumber) {
    WebElement population = testWebDriver.getElementByXpath("//table[@id='aggregateTable']/tbody/tr["+rowNumber+"]/td[2]/span");
    testWebDriver.waitForElementToAppear(population);
    return population.getText();
  }

  public String getAggregateProduct1Isa(int rowNumber) {
    WebElement Product1 = testWebDriver.getElementByXpath("//table[@id='aggregateTable']/tbody/tr["+rowNumber+"]/td[3]/span");
    testWebDriver.waitForElementToAppear(Product1);
    return Product1.getText();
  }

  public String getAggregateProduct2Isa(int rowNumber) {
    WebElement Product2 = testWebDriver.getElementByXpath("//table[@id='aggregateTable']/tbody/tr["+rowNumber+"]/td[4]/span");
    testWebDriver.waitForElementToAppear(Product2);
    return Product2.getText();
  }


  public String getNoRecordFoundMessage() {
    WebElement noRecordFoundMessage = testWebDriver.getElementByXpath("//div[@class='alert alert-info']/span");
    testWebDriver.waitForElementToAppear(noRecordFoundMessage);
    return noRecordFoundMessage.getText();
  }

}