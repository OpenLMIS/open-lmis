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
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.pagefactory.AjaxElementLocatorFactory;

import java.util.List;

import static org.openqa.selenium.support.How.*;
import static org.testng.AssertJUnit.assertEquals;

public class GeographicZonePage extends Page {

  @FindBy(how = ID, using = "searchGeoZone")
  private static WebElement searchGeoZoneTextField = null;

  @FindBy(how = ID, using = "geoZoneTab")
  private static WebElement geoZoneTab = null;

  @FindBy(how = ID, using = "searchOptionButton")
  private static WebElement searchOptionButton = null;

  @FindBy(how = ID, using = "add-new-geo-zone")
  private static WebElement addNewButton = null;

  @FindBy(how = ID, using = "code")
  private static WebElement codeTextField = null;

  @FindBy(how = ID, using = "name")
  private static WebElement nameTextField = null;

  @FindBy(how = ID, using = "levelCode")
  private static WebElement levelCodeDropDown = null;

  @FindBy(how = ID, using = "catchment-population")
  private static WebElement catchmentPopulationTextField = null;

  @FindBy(how = ID, using = "latitude")
  private static WebElement latitudeTextField = null;

  @FindBy(how = ID, using = "longitude")
  private static WebElement longitudeTextField = null;

  @FindBy(how = ID, using = "saveButton")
  private static WebElement saveButton = null;

  @FindBy(how = ID, using = "cancelButton")
  private static WebElement cancelButton = null;

  @FindBy(how = ID, using = "saveSuccessMsgDiv")
  private static WebElement successMsgDiv = null;

  @FindBy(how = ID, using = "selectParentGeoZone")
  private static WebElement parentDropDown = null;

  @FindBy(how = ID, using = "closeButton")
  private static WebElement closeButton = null;

  @FindBy(how = ID, using = "searchIcon")
  private static WebElement searchIcon = null;

  @FindBy(how = NAME, using = "searchResultTable")
  private static WebElement resultTable = null;

  @FindBy(how = ID, using = "searchGeoZoneHeader")
  private static WebElement searchGeoZoneHeader = null;

  @FindBy(how = ID, using = "selectedSearchOption")
  private static WebElement selectedSearchOption = null;

  @FindBy(how = ID, using = "nResultsMessage")
  private static WebElement nResultsMessage = null;

  @FindBy(how = ID, using = "noResultMessage")
  private static WebElement noResultMessage = null;

  @FindBy(how = ID, using = "oneResultMessage")
  private static WebElement oneResultMessage = null;

  @FindBy(how = ID, using = "searchOption1")
  private static WebElement geoZoneParentSearchOption = null;

  @FindBy(how = ID, using = "nameHeader")
  private static WebElement nameHeader = null;

  @FindBy(how = ID, using = "codeHeader")
  private static WebElement codeHeader = null;

  @FindBy(how = ID, using = "levelHeader")
  private static WebElement levelHeader = null;

  @FindBy(how = ID, using = "parentHeader")
  private static WebElement parentHeader = null;

  @FindBy(how = XPATH, using = "//*[@id='s2id_selectParentGeoZone']/a/span")
  private static WebElement selectParentGeoZoneField = null;

  @FindBy(how = ID, using = "saveErrorMsgDiv")
  private static WebElement errorMessage = null;

  @FindBy(how = ID, using = "codeLabel")
  private static WebElement codeLabel = null;

  @FindBy(how = ID, using = "levelLabel")
  private static WebElement levelLabel = null;

  @FindBy(how = ID, using = "nameLabel")
  private static WebElement nameLabel = null;

  @FindBy(how = ID, using = "parentLabel")
  private static WebElement parentLabel = null;

  @FindBy(how = ID, using = "populationLabel")
  private static WebElement populationLabel = null;

  @FindBy(how = ID, using = "latitudeLabel")
  private static WebElement latitudeLabel = null;

  @FindBy(how = ID, using = "longitudeLabel")
  private static WebElement longitudeLabel = null;

  @FindBy(how = ID, using = "addNewGeoZoneHeader")
  private static WebElement addNewGeoZoneHeader = null;

  @FindBy(how = ID, using = "editGeoZoneHeader")
  private static WebElement editGeoZoneHeader = null;

  public GeographicZonePage(TestWebDriver driver) {
    super(driver);
    PageFactory.initElements(new AjaxElementLocatorFactory(TestWebDriver.getDriver(), 1), this);
    testWebDriver.setImplicitWait(1);
  }

  public void searchGeoZone(String geoZoneName) {
    testWebDriver.waitForElementToAppear(searchGeoZoneTextField);
    sendKeys(searchGeoZoneTextField, geoZoneName);
    testWebDriver.click(searchIcon);
  }

  public void clickSearchOptionButton() {
    testWebDriver.waitForElementToAppear(searchOptionButton);
    testWebDriver.click(searchOptionButton);
  }

  public void clickAddNewButton() {
    testWebDriver.waitForElementToAppear(addNewButton);
    testWebDriver.click(addNewButton);
  }

  public void enterGeoZoneName(String name) {
    testWebDriver.waitForElementToAppear(nameTextField);
    sendKeys(nameTextField, name);
  }

  public void enterGeoZoneCode(String code) {
    testWebDriver.waitForElementToAppear(codeTextField);
    sendKeys(codeTextField, code);
  }

  public void enterCatchmentPopulation(String population) {
    testWebDriver.waitForElementToAppear(catchmentPopulationTextField);
    sendKeys(catchmentPopulationTextField, population);
  }

  public void enterLatitude(String latitude) {
    testWebDriver.waitForElementToAppear(latitudeTextField);
    sendKeys(latitudeTextField, latitude);
  }

  public void enterLongitude(String longitude) {
    testWebDriver.waitForElementToAppear(longitudeTextField);
    sendKeys(longitudeTextField, longitude);
  }

  public void selectGeoZoneLevel(String level) {
    testWebDriver.waitForElementToAppear(levelCodeDropDown);
    testWebDriver.selectByVisibleText(levelCodeDropDown, level);
  }

  public void selectGeoZoneParent(String parent) {
    testWebDriver.waitForElementToAppear(parentDropDown);
    testWebDriver.selectByVisibleText(parentDropDown, parent);
  }

  public String getGeoZoneNameOnEditPage() {
    testWebDriver.waitForElementToAppear(nameTextField);
    return nameTextField.getAttribute("value");
  }

  public String getPopulationOnEditPage() {
    testWebDriver.waitForElementToAppear(catchmentPopulationTextField);
    return catchmentPopulationTextField.getAttribute("value");
  }

  public String getLatitudeOnEditPage() {
    testWebDriver.waitForElementToAppear(latitudeTextField);
    return latitudeTextField.getAttribute("value");
  }

  public String getLongitudeOnEditPage() {
    testWebDriver.waitForElementToAppear(longitudeTextField);
    return longitudeTextField.getAttribute("value");
  }

  public void verifyNumberOfItemsPerPage(int numberOfItemsPerPage) {
    assertEquals(numberOfItemsPerPage, testWebDriver.getElementsSizeByXpath("//*[@id='wrap']/div/div/div/div[3]/table/tbody/tr"));
  }

  public boolean isSuccessMessageDisplayed() {
    testWebDriver.waitForElementToAppear(successMsgDiv);
    return successMsgDiv.isDisplayed();
  }

  public String getSuccessMessage() {
    testWebDriver.waitForElementToAppear(successMsgDiv);
    return successMsgDiv.getText();
  }

  public void clickOnSaveButton() {
    testWebDriver.waitForElementToAppear(saveButton);
    saveButton.click();
  }

  public void clickOnCancelButton() {
    testWebDriver.waitForElementToAppear(cancelButton);
    cancelButton.click();
  }

  public boolean isSearchResultTableDisplayed() {

    try {
      testWebDriver.waitForElementToAppear(resultTable);
    } catch (TimeoutException e) {
      return false;
    } catch (NoSuchElementException e) {
      return false;
    }
    return resultTable.isDisplayed();
  }

  public void clickOnCrossButton() {
    testWebDriver.waitForElementToAppear(closeButton);
    testWebDriver.click(closeButton);
  }

  public void clickOnSearchResultLink(int rowNumber) {
    WebElement searchResult = testWebDriver.getElementById("geoZoneName" + (rowNumber - 1));
    testWebDriver.waitForElementToAppear(searchResult);
    searchResult.click();
  }

  public String getGeoZoneName(int rowNumber) {
    WebElement geoZoneName = testWebDriver.getElementById("geoZoneName" + (rowNumber - 1));
    testWebDriver.waitForElementToAppear(geoZoneName);
    return geoZoneName.getText();
  }

  public String getGeoZoneCode(int rowNumber) {
    WebElement geoZoneCode = testWebDriver.getElementById("code" + (rowNumber - 1));
    testWebDriver.waitForElementToAppear(geoZoneCode);
    return geoZoneCode.getText();
  }

  public String getLevelName(int rowNumber) {
    WebElement levelName = testWebDriver.getElementById("levelName" + (rowNumber - 1));
    testWebDriver.waitForElementToAppear(levelName);
    return levelName.getText();
  }

  public String getParentName(int rowNumber) {
    WebElement parentName = testWebDriver.getElementById("parentName" + (rowNumber - 1));
    testWebDriver.waitForElementToAppear(parentName);
    return parentName.getText();
  }

  public void editAlreadyExistingGeoZone(String name, String code, String catchmentPopulation, String latitude, String longitude, String parent) {
    testWebDriver.waitForElementToAppear(nameTextField);
    sendKeys(nameTextField, name);
    sendKeys(codeTextField, code);
    sendKeys(catchmentPopulationTextField, catchmentPopulation);
    sendKeys(latitudeTextField, latitude);
    sendKeys(longitudeTextField, longitude);
    testWebDriver.selectByValue(parentDropDown, parent);
  }

  public void clickOnViewHereLink() {
    testWebDriver.getElementById("viewHere").click();
  }

  public boolean isGeoZoneTabVisible() {
    testWebDriver.waitForElementToAppear(geoZoneTab);
    return geoZoneTab.isDisplayed();
  }

  public String getGeoZoneTabLabel() {
    testWebDriver.waitForElementToAppear(geoZoneTab);
    return geoZoneTab.getText();
  }

  public String getGeoZoneSearchPageHeader() {
    testWebDriver.waitForElementToAppear(searchGeoZoneHeader);
    return searchGeoZoneHeader.getText();
  }

  public String getSelectedSearchOption() {
    testWebDriver.waitForElementToAppear(selectedSearchOption);
    return selectedSearchOption.getText();
  }

  public String getNResultsMessage() {
    testWebDriver.waitForElementToAppear(nResultsMessage);
    return nResultsMessage.getText();
  }

  public void selectGeoZoneParentSearchOption() {
    testWebDriver.waitForElementToAppear(geoZoneParentSearchOption);
    geoZoneParentSearchOption.click();
  }

  public String getNameHeader() {
    testWebDriver.waitForElementToAppear(nameHeader);
    return nameHeader.getText();
  }

  public String getCodeHeader() {
    testWebDriver.waitForElementToAppear(codeHeader);
    return codeHeader.getText();
  }

  public String getLevelHeader() {
    testWebDriver.waitForElementToAppear(levelHeader);
    return levelHeader.getText();
  }

  public String getParentHeader() {
    testWebDriver.waitForElementToAppear(parentHeader);
    return parentHeader.getText();
  }

  public void clickSelectParentField() {
    testWebDriver.waitForElementToAppear(selectParentGeoZoneField);
    selectParentGeoZoneField.click();
  }

  public boolean isParentDropDownEmpty() {
    WebElement noResults = testWebDriver.findElement(By.className("select2-no-results"));
    testWebDriver.waitForElementToAppear(noResults);
    return noResults.isDisplayed();
  }

  public List<String> getListOfLevels() {
    testWebDriver.waitForElementToAppear(levelCodeDropDown);
    return testWebDriver.getListOfOptions(levelCodeDropDown);
  }

  public List<String> getListOfParentGroupsWithOptions() {
    testWebDriver.waitForElementToAppear(parentDropDown);
    return testWebDriver.getListOfOptionGroupsWithOptions(parentDropDown);
  }

  public String getSaveErrorMessage() {
    testWebDriver.waitForElementToAppear(errorMessage);
    return errorMessage.getText();
  }

  public String getNoResultMessage() {
    testWebDriver.waitForElementToAppear(noResultMessage);
    return noResultMessage.getText();
  }

  public String getOneResultMessage() {
    testWebDriver.waitForElementToAppear(oneResultMessage);
    return oneResultMessage.getText();
  }

  public boolean isLevelCodeDropDownEnabled() {
    testWebDriver.waitForElementToAppear(levelCodeDropDown);
    return levelCodeDropDown.isEnabled();
  }

  public String getNameLabel() {
    testWebDriver.waitForElementToAppear(nameLabel);
    return nameLabel.getText();
  }

  public String getCodeLabel() {
    testWebDriver.waitForElementToAppear(codeLabel);
    return codeLabel.getText();
  }

  public String getPopulationLabel() {
    testWebDriver.waitForElementToAppear(populationLabel);
    return populationLabel.getText();
  }

  public String getParentLabel() {
    testWebDriver.waitForElementToAppear(parentLabel);
    return parentLabel.getText();
  }

  public String getLatitudeLabel() {
    testWebDriver.waitForElementToAppear(latitudeLabel);
    return latitudeLabel.getText();
  }

  public String getLongitudeLabel() {
    testWebDriver.waitForElementToAppear(longitudeLabel);
    return longitudeLabel.getText();
  }

  public String getLevelLabel() {
    testWebDriver.waitForElementToAppear(levelLabel);
    return levelLabel.getText();
  }

  public String getAddNewGeoZoneHeader() {
    testWebDriver.waitForElementToAppear(addNewGeoZoneHeader);
    return addNewGeoZoneHeader.getText();
  }

  public String getEditGeoZoneHeader() {
    testWebDriver.waitForElementToAppear(editGeoZoneHeader);
    return editGeoZoneHeader.getText();
  }
}
