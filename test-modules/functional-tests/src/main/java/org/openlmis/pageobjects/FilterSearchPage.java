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
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.pagefactory.AjaxElementLocatorFactory;

import java.util.NoSuchElementException;

import static org.openqa.selenium.By.cssSelector;
import static org.openqa.selenium.support.How.ID;

public class FilterSearchPage extends Page {

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

  @FindBy(how = ID, using = "removeFacilityTypeIcon")
  private static WebElement removeFacilityTypeIcon = null;

  @FindBy(how = ID, using = "removeGeoZoneIcon")
  private static WebElement removeGeoZoneIcon = null;

  @FindBy(how = ID, using = "addSelectedFacilities")
  private static WebElement addSelectedFacilities = null;

  @FindBy(how = ID, using = "noFacilityResultMessage")
  private static WebElement noFacilityResultMessage = null;

  @FindBy(how = ID, using = "oneFacilityResultMessage")
  private static WebElement oneFacilityResultMessage = null;

  @FindBy(how = ID, using = "nFacilityResultsMessage")
  private static WebElement nFacilityResultsMessage = null;

  @FindBy(how = ID, using = "tooManyFacilityResultsMessage")
  private static WebElement tooManyFacilityResultsMessage = null;

  @FindBy(how = ID, using = "noGeoZoneResult")
  private static WebElement noGeoZoneResultMessage = null;

  @FindBy(how = ID, using = "oneGeoZoneResult")
  private static WebElement oneGeoZoneResultMessage = null;

  @FindBy(how = ID, using = "nGeoZoneResults")
  private static WebElement nGeoZoneResultsMessage = null;

  @FindBy(how = ID, using = "tooManyGeoZoneResults")
  private static WebElement tooManyGeoZoneResultsMessage = null;

  @FindBy(how = ID, using = "searchFacilityIcon")
  private static WebElement searchFacilityIcon = null;

  @FindBy(how = ID, using = "searchMultipleFacilitiesIcon")
  private static WebElement searchMultipleFacilitiesIcon = null;

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
    return selectedFacilityType.getText();
  }

  public boolean isSelectedFacilityTypeOnFilterPopUpDisplayed() {
    try {
      testWebDriver.waitForElementToAppear(selectedFacilityType);
    } catch (TimeoutException e) {
      return false;
    } catch (NoSuchElementException e) {
      return false;
    }
    return selectedFacilityType.isDisplayed();
  }

  public String getSelectedFacilityTypeLabelOnAddFilterPage() {
    testWebDriver.waitForElementToAppear(facilityTypeLabel);
    return facilityTypeLabel.getText();
  }

  public String getSelectedGeoZoneLabelOnAddFilterPage() {
    testWebDriver.waitForElementToAppear(geoZoneLabel);
    return geoZoneLabel.getText();
  }

  public void searchGeographicZone(String geoZone) {
    testWebDriver.waitForElementToAppear(searchGeoZone);
    sendKeys(searchGeoZone, geoZone);
    geoZoneSearchIcon.click();
  }

  public boolean isSetFilterButtonPresent() {
    try {
      testWebDriver.waitForElementToAppear(setFilterButton);
    } catch (TimeoutException e) {
      return false;
    } catch (NoSuchElementException e) {
      return false;
    }
    return setFilterButton.isDisplayed();
  }

  public void selectGeographicZoneResult(int rowNumber) {
    WebElement geoZoneResult = testWebDriver.getElementById("geoZoneResult" + (rowNumber - 1));
    testWebDriver.waitForElementToAppear(geoZoneResult);
    geoZoneResult.click();
  }

  public String getSelectedGeoZoneOnFilterPopUp() {
    testWebDriver.waitForElementToAppear(selectedGeoZone);
    return selectedGeoZone.getText();
  }

  public boolean isSelectedGeoZoneOnFilterPopUpDisplayed() {
    try {
      testWebDriver.waitForElementToAppear(selectedGeoZone);
    } catch (TimeoutException e) {
      return false;
    } catch (NoSuchElementException e) {
      return false;
    }
    return selectedGeoZone.isDisplayed();
  }

  public void clickCancelFilterButton() {
    testWebDriver.waitForElementToAppear(cancelFiltersButton);
    cancelFiltersButton.click();
  }

  public void clickRemoveFacilityTypeFilter() {
    testWebDriver.waitForElementToAppear(removeFacilityTypeIcon);
    removeFacilityTypeIcon.click();
  }

  public boolean isRemoveFacilityTypeFilterDisplayed() {
    try {
      testWebDriver.waitForElementToAppear(removeFacilityTypeIcon);
    } catch (TimeoutException e) {
      return false;
    } catch (NoSuchElementException e) {
      return false;
    }
    return removeFacilityTypeIcon.isDisplayed();
  }

  public void clickRemoveGeoZoneFilter() {
    testWebDriver.waitForElementToAppear(removeGeoZoneIcon);
    removeGeoZoneIcon.click();
  }

  public boolean isRemoveGeoZoneFilterDisplayed() {
    try {
      testWebDriver.waitForElementToAppear(removeGeoZoneIcon);
    } catch (TimeoutException e) {
      return false;
    } catch (NoSuchElementException e) {
      return false;
    }
    return removeGeoZoneIcon.isDisplayed();
  }

  public void clickOnAddSelectedFacilityButton() {
    testWebDriver.waitForElementToAppear(addSelectedFacilities);
    addSelectedFacilities.click();
  }

  public boolean isAddSelectedFacilityButtonEnabled() {
    try {
      testWebDriver.waitForElementToAppear(addSelectedFacilities);
    } catch (TimeoutException e) {
      return false;
    } catch (NoSuchElementException e) {
      return false;
    }
    return addSelectedFacilities.isEnabled();
  }

  public void checkFacilityToBeAssociated(int rowNumber) {
    WebElement facilityCheckBox = testWebDriver.getElementById("facilityCheckBox" + (rowNumber - 1));
    testWebDriver.waitForElementToAppear(facilityCheckBox);
    facilityCheckBox.click();
  }

  public boolean isNoFacilityResultMessageDisplayed() {
    try {
      testWebDriver.waitForElementToAppear(noFacilityResultMessage);
    } catch (TimeoutException e) {
      return false;
    } catch (NoSuchElementException e) {
      return false;
    }
    return noFacilityResultMessage.isDisplayed();
  }

  public String getNFacilityResultsMessage() {
    testWebDriver.waitForElementToAppear(nFacilityResultsMessage);
    return nFacilityResultsMessage.getText();
  }

  public String getOneFacilityResultMessage() {
    testWebDriver.waitForElementToAppear(oneFacilityResultMessage);
    return oneFacilityResultMessage.getText();
  }

  public String getNoFacilityResultMessage() {
    testWebDriver.waitForElementToAppear(noFacilityResultMessage);
    return noFacilityResultMessage.getText();
  }

  public String getTooManyFacilitySearchResultMessage() {
    testWebDriver.waitForElementToAppear(tooManyFacilityResultsMessage);
    return tooManyFacilityResultsMessage.getText();
  }

  public String getNGeoZoneResultsMessage() {
    testWebDriver.waitForElementToAppear(nGeoZoneResultsMessage);
    return nGeoZoneResultsMessage.getText();
  }

  public String getOneGeoZoneResultMessage() {
    testWebDriver.waitForElementToAppear(oneGeoZoneResultMessage);
    return oneGeoZoneResultMessage.getText();
  }

  public String getNoGeoZoneResultMessage() {
    testWebDriver.waitForElementToAppear(noGeoZoneResultMessage);
    return noGeoZoneResultMessage.getText();
  }

  public String getTooManyGeoZoneSearchResultMessage() {
    testWebDriver.waitForElementToAppear(tooManyGeoZoneResultsMessage);
    return tooManyGeoZoneResultsMessage.getText();
  }

  public String getSearchFacilityText() {
    WebElement facilitySearch = testWebDriver.findElement(cssSelector("input[ng-model='facilitySearchParam']"));
    testWebDriver.waitForElementToAppear(facilitySearch);
    return facilitySearch.getText();
  }

  public void selectFacility(int rowNumber) {
    WebElement facilityResult = testWebDriver.getElementById("facilityResult" + (rowNumber - 1));
    testWebDriver.waitForElementToAppear(facilityResult);
    facilityResult.click();
  }

  public boolean isSearchFacilityIconDisplayed() {
    try {
      testWebDriver.waitForElementToAppear(searchFacilityIcon);
    } catch (TimeoutException e) {
      return false;
    } catch (NoSuchElementException e) {
      return false;
    }
    return searchFacilityIcon.isDisplayed();
  }

  public boolean isSearchMultipleFacilitiesIconDisplayed() {
    try {
      testWebDriver.waitForElementToAppear(searchMultipleFacilitiesIcon);
    } catch (TimeoutException e) {
      return false;
    } catch (NoSuchElementException e) {
      return false;
    }
    return searchMultipleFacilitiesIcon.isDisplayed();
  }

  public void searchMultipleFacilities(String facilityCode) {
    WebElement multipleFacilitiesSearch = testWebDriver.findElement(cssSelector("input[ng-model='multipleFacilitiesSearchParam']"));
    testWebDriver.waitForElementToAppear(multipleFacilitiesSearch);
    sendKeys(multipleFacilitiesSearch, facilityCode);
    searchMultipleFacilitiesIcon.click();
  }

  public void searchFacility(String facilityCode) {
    WebElement facilitySearch = testWebDriver.findElement(cssSelector("input[ng-model='facilitySearchParam']"));
    testWebDriver.waitForElementToAppear(facilitySearch);
    sendKeys(facilitySearch, facilityCode);
    searchFacilityIcon.click();
  }

  public String getFacilityResult(int rowNumber) {
    WebElement element = testWebDriver.getElementById("facilityResult" + (rowNumber - 1));
    testWebDriver.waitForElementToAppear(element);
    return element.getText();
  }
}
