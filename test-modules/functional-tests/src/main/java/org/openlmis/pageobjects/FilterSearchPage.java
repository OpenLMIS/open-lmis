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
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.pagefactory.AjaxElementLocatorFactory;

import static org.openqa.selenium.support.How.ID;

public class FilterSearchPage extends Page{

  @FindBy(how = ID, using = "filterButton")
  private static WebElement setFilterButton = null;

  @FindBy(how = ID, using = "searchGeoZone")
  private static WebElement searchGeoZone = null;

  @FindBy(how = ID, using = "doneFilters")
  private static WebElement applyFiltersButton = null;

  @FindBy(how = ID, using = "cancelFilters")
  private static WebElement cancelFiltersButton = null;

  @FindBy(how = ID, using = "facilityType")
  private static WebElement selectFacilityTypeField = null;

  @FindBy(how = ID, using = "setFiltersModal")
  private static WebElement setFiltersModal = null;

  @FindBy(how = ID, using = "selectedFacilityType")
  private static WebElement selectedFacilityType = null;

  @FindBy(how = ID, using = "geoZoneSearchIcon")
  private static WebElement geoZoneSearchIcon = null;

  @FindBy(how = ID, using = "facilityTypeLabel")
  private static WebElement facilityTypeLabel = null;

  @FindBy(how = ID, using = "geoZoneLabel")
  private static WebElement geoZoneLabel = null;

  @FindBy(how = ID, using = "selectedGeoZone")
  private static WebElement selectedGeoZone = null;

  public FilterSearchPage(TestWebDriver driver) {
    super(driver);
    PageFactory.initElements(new AjaxElementLocatorFactory(TestWebDriver.getDriver(), 1), this);
    testWebDriver.setImplicitWait(1);
  }

  public void clickFilterButton() {
    testWebDriver.waitForElementToAppear(setFilterButton);
    setFilterButton.click();
    testWebDriver.waitForElementToAppear(setFiltersModal);
  }


  public void selectFacilityType(String facilityType) {
    testWebDriver.waitForElementToAppear(selectFacilityTypeField);
    testWebDriver.selectByVisibleText(selectFacilityTypeField, facilityType);
  }

  public void clickApplyFilterButton() {
    testWebDriver.waitForElementToAppear(applyFiltersButton);
    applyFiltersButton.click();
  }

  public String getSelectedFacilityTypeOnFilterPopUp() {
    testWebDriver.waitForElementToAppear(selectedFacilityType);
    return selectedFacilityType.getText() ;
  }

  public String getSelectedFacilityTypeLabelOnAddFilterPage(){
    testWebDriver.waitForElementToAppear(facilityTypeLabel);
    return facilityTypeLabel.getText();
  }

  public String getSelectedGeoZoneLabelOnAddFilterPage(){
    testWebDriver.waitForElementToAppear(geoZoneLabel);
    return geoZoneLabel.getText();
  }

  public void searchGeographicZone(String geoZone) {
    testWebDriver.waitForElementToAppear(searchGeoZone);
    sendKeys(searchGeoZone, geoZone);
    geoZoneSearchIcon.click();
  }

  public boolean isSetFilterButtonPresent() {
    testWebDriver.waitForElementToAppear(setFilterButton);
    return setFilterButton.isDisplayed();
  }

  public void selectGeographicZoneResult(int rowNumber) {
    WebElement geoZoneResult = testWebDriver.getElementById("geoZoneResult" + (rowNumber-1));
    testWebDriver.waitForElementToAppear(geoZoneResult);
    geoZoneResult.click();
  }

  public String getSelectedGeoZoneOnFilterPopUp() {
    testWebDriver.waitForElementToAppear(selectedGeoZone);
    return selectedGeoZone.getText() ;
  }

  public void clickCancelFilterButton() {
    testWebDriver.waitForElementToAppear(cancelFiltersButton);
    cancelFiltersButton.click();
  }
}
