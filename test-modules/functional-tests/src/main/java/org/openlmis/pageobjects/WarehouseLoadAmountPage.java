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
import org.openqa.selenium.support.How;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.pagefactory.AjaxElementLocatorFactory;

import static org.openqa.selenium.support.How.ID;


public class WarehouseLoadAmountPage extends Page {

  @FindBy(how = ID, using = "aggregateTable")
  private WebElement aggregateTable = null;

  @FindBy(how = How.XPATH, using = "//div[@class='facilities-isa-amount-table']")
  private WebElement Table1 = null;

  @FindBy(how = ID, using = "warehouseLoadAmountLabel")
  private WebElement warehouseLoadAmountLabel = null;

  @FindBy(how = ID, using = "deliveryZoneLabel")
  private WebElement deliveryZoneLabel = null;

  @FindBy(how = ID, using = "deliveryZoneName")
  private WebElement deliveryZoneName = null;

  @FindBy(how = ID, using = "programLabel")
  private WebElement programLabel = null;

  @FindBy(how = ID, using = "programName")
  private WebElement programName = null;

  @FindBy(how = ID, using = "periodLabel")
  private WebElement periodLabel = null;

  @FindBy(how = ID, using = "periodName")
  private WebElement periodName = null;

  @FindBy(how = ID, using = "deliveryZoneNameLabel")
  private WebElement deliveryZoneNameLabel = null;

  @FindBy(how = ID, using = "totalLabel")
  private WebElement totalLabel = null;

  @FindBy(how = ID, using = "geoZoneLevelName")
  private WebElement geoZoneLevelName = null;

  @FindBy(how = ID, using = "aggregatePopulationLabel")
  private WebElement aggregatePopulationLabel = null;

  @FindBy(how = ID, using = "zoneTotalLabel")
  private WebElement zoneTotalLabel = null;

  public WarehouseLoadAmountPage(TestWebDriver driver) {
    super(driver);
    PageFactory.initElements(new AjaxElementLocatorFactory(TestWebDriver.getDriver(), 10), this);
    testWebDriver.setImplicitWait(10);
  }

  public String getPageHeader() {
    testWebDriver.waitForElementToAppear(warehouseLoadAmountLabel);
    return warehouseLoadAmountLabel.getText();
  }

  public String getDeliveryZoneLabelInHeader() {
    testWebDriver.waitForElementToAppear(deliveryZoneLabel);
    return deliveryZoneLabel.getText();
  }

  public String getDeliveryZoneNameInHeader() {
    testWebDriver.waitForElementToAppear(deliveryZoneName);
    return deliveryZoneName.getText();
  }

  public String getProgramLabelInHeader() {
    testWebDriver.waitForElementToAppear(programLabel);
    return programLabel.getText();
  }

  public String getProgramNameInHeader() {
    testWebDriver.waitForElementToAppear(programName);
    return programName.getText();
  }

  public String getPeriodLabelInHeader() {
    testWebDriver.waitForElementToAppear(periodLabel);
    return periodLabel.getText();
  }

  public String getPeriodNameInHeader() {
    testWebDriver.waitForElementToAppear(periodName);
    return periodName.getText();
  }

  public String getDeliveryZoneName() {
    testWebDriver.waitForElementToAppear(deliveryZoneNameLabel);
    return deliveryZoneNameLabel.getText() + " " + totalLabel.getText();
  }

  public String getGeoZoneLevelHeaderForAggregatedTable() {
    testWebDriver.waitForElementToAppear(geoZoneLevelName);
    return geoZoneLevelName.getText();
  }

  public String getPopulationHeaderForAggregatedTable() {
    testWebDriver.waitForElementToAppear(aggregatePopulationLabel);
    return aggregatePopulationLabel.getText();
  }

  public String getProductGroupHeaderForAggregatedTable(int productNumber) {
    WebElement productGroupHeader = testWebDriver.getElementById("productGroupAggregated" + (productNumber - 1));
    testWebDriver.waitForElementToAppear(productGroupHeader);
    return productGroupHeader.getText();
  }

  public String getProductNameHeaderForAggregatedTable(int productNumber) {
    WebElement productName = testWebDriver.getElementById("aggregateProductName" + (productNumber - 1));
    testWebDriver.waitForElementToAppear(productName);
    return productName.getText();
  }

  public String getGeoZonesFromAggregatedTable(int rowNumber) {
    WebElement geoZone = testWebDriver.getElementById("geoZoneName" + (rowNumber - 1));
    testWebDriver.waitForElementToAppear(geoZone);
    return geoZone.getText();
  }

  public String getAggregateTableGeoZoneTotalCaption(int tableNumber) {
    WebElement geoZoneLabel = testWebDriver.getElementById("geoZoneNameLabel" + (tableNumber - 1));
    testWebDriver.waitForElementToAppear(geoZoneLabel);
    return geoZoneLabel.getText();
  }

  public String getAggregateTableTotalCaption() {
    testWebDriver.waitForElementToAppear(zoneTotalLabel);
    return zoneTotalLabel.getText();
  }

  public String getAggregateTablePopulation(int rowNumber) {
    WebElement population = testWebDriver.getElementById("populationForGeoZone" + (rowNumber - 1));
    testWebDriver.waitForElementToAppear(population);
    return population.getText();
  }

  public String getAggregateTableProductIsa(int rowNumber, int productNumber) {
    WebElement productIsa = testWebDriver.getElementById("productIsaForGeoZone" + (rowNumber - 1) + (productNumber - 1));
    testWebDriver.waitForElementToAppear(productIsa);
    return productIsa.getText();
  }

  public String getAggregateTableTotalPopulation() {
    WebElement populationTotal = testWebDriver.getElementById("zonesPopulationTotal");
    testWebDriver.waitForElementToAppear(populationTotal);
    return populationTotal.getText();
  }

  public String getAggregateTableTotalProductIsa(int productNumber) {
    WebElement totalProductIsa = testWebDriver.getElementById("productIsaAggregate" + (productNumber - 1));
    testWebDriver.waitForElementToAppear(totalProductIsa);
    return totalProductIsa.getText();
  }

  public String getGeoZoneTitleForTable(int tableNumber) {
    WebElement geoZoneTitle = testWebDriver.getElementById("geoZoneTitle" + (tableNumber - 1));
    testWebDriver.waitForElementToAppear(geoZoneTitle);
    return geoZoneTitle.getText();
  }

  public String getFacilityHeaderForTable(int tableNumber) {
    WebElement facilityHeader = testWebDriver.getElementById("facilityLabel" + (tableNumber - 1));
    testWebDriver.waitForElementToAppear(facilityHeader);
    return facilityHeader.getText();
  }

  public String getPopulationHeaderForTable(int tableNumber) {
    WebElement populationLabel = testWebDriver.getElementById("populationLabel" + (tableNumber - 1));
    testWebDriver.waitForElementToAppear(populationLabel);
    return populationLabel.getText();
  }

  public String getProductGroupHeaderForTable(int tableNumber, int productNumber) {
    WebElement productGroupHeader = testWebDriver.getElementById("productGroup" + (tableNumber - 1) + (productNumber - 1));
    testWebDriver.waitForElementToAppear(productGroupHeader);
    return productGroupHeader.getText();
  }

  public String getProductNameHeaderForTable(int tableNumber, int productNumber) {
    WebElement productName = testWebDriver.getElementById("product" + (tableNumber - 1) + (productNumber - 1));
    testWebDriver.waitForElementToAppear(productName);
    return productName.getText();
  }

  public String getFacilityName(int tableNumber, int rowNumber) {
    WebElement facilityName = testWebDriver.getElementById("facilityName" + (tableNumber - 1) + (rowNumber - 1));
    testWebDriver.waitForElementToAppear(facilityName);
    return facilityName.getText();
  }

  public String getFacilityCode(int tableNumber, int rowNumber) {
    WebElement facilityCode = testWebDriver.getElementById("facilityCode" + (tableNumber - 1) + (rowNumber - 1));
    testWebDriver.waitForElementToAppear(facilityCode);
    return facilityCode.getText();
  }

  public String getFacilityPopulation(int tableNumber, int rowNumber) {
    WebElement population = testWebDriver.getElementById("population" + (tableNumber - 1) + (rowNumber - 1));
    testWebDriver.waitForElementToAppear(population);
    return population.getText();
  }

  public String getProductIsa(int tableNumber, int rowNumber, int productNumber) {
    WebElement productIsa = testWebDriver.getElementById("isaAmount" + (tableNumber - 1) + (rowNumber - 1) + (productNumber - 1));
    testWebDriver.waitForElementToAppear(productIsa);
    return productIsa.getText();
  }

  public String getTotalProductIsa(int tableNumber, int productNumber) {
    WebElement totalProductIsa = testWebDriver.getElementById("isaAmountTotal" + (tableNumber - 1) + (productNumber - 1));
    testWebDriver.waitForElementToAppear(totalProductIsa);
    return totalProductIsa.getText();
  }

  public String getTotalPopulation(int tableNumber) {
    WebElement totalProductIsa = testWebDriver.getElementById("population" + (tableNumber - 1));
    testWebDriver.waitForElementToAppear(totalProductIsa);
    return totalProductIsa.getText();
  }

  public String getTableTotalCaption(int tableNumber) {
    WebElement geoZoneCaption = testWebDriver.getElementById("geoZoneNameLabel" + (tableNumber - 1));
    WebElement totalCaption = testWebDriver.getElementById("totalLabel" + (tableNumber - 1));
    testWebDriver.waitForElementToAppear(geoZoneCaption);
    testWebDriver.waitForElementToAppear(totalCaption);
    return geoZoneCaption.getText() + " " + totalCaption.getText();
  }

  public String getNoRecordFoundMessage() {
    WebElement noRecordFoundMessage = testWebDriver.getElementByXpath("//div[@class='alert alert-info']/span");
    testWebDriver.waitForElementToAppear(noRecordFoundMessage);
    return noRecordFoundMessage.getText();
  }

  public String getAggregateTableData() {
    return aggregateTable.getText();
  }

  public String getTable1Data() {
    return Table1.getText();
  }
}