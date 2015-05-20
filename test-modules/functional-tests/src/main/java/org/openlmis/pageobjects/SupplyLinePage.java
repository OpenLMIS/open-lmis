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

import static org.openqa.selenium.support.How.ID;
import static org.openqa.selenium.support.How.XPATH;

public class SupplyLinePage extends FilterSearchPage {

  @FindBy(how = ID, using = "searchOptionButton")
  private static WebElement searchOptionButton = null;

  @FindBy(how = ID, using = "searchSupplyLine")
  private static WebElement searchSupplyLineParameter = null;

  @FindBy(how = ID, using = "supplyLineAddNew")
  private static WebElement supplyLineAddNew = null;

  @FindBy(how = ID, using = "searchOption0")
  private static WebElement searchOption1 = null;

  @FindBy(how = ID, using = "searchOption1")
  private static WebElement searchOption2 = null;

  @FindBy(how = ID, using = "searchOption2")
  private static WebElement searchOption3 = null;

  @FindBy(how = ID, using = "searchSupplyLineLabel")
  private static WebElement searchSupplyLineLabel = null;

  @FindBy(how = ID, using = "noResultMessage")
  private static WebElement noResultMessage = null;

  @FindBy(how = ID, using = "oneResultMessage")
  private static WebElement oneResultMessage = null;

  @FindBy(how = ID, using = "nResultsMessage")
  private static WebElement nResultsMessage = null;

  @FindBy(how = ID, using = "programHeader")
  private static WebElement programHeader = null;

  @FindBy(how = ID, using = "facilityHeader")
  private static WebElement supplyingFacilityHeader = null;

  @FindBy(how = ID, using = "supervisoryNodeHeader")
  private static WebElement supervisoryNodeHeader = null;

  @FindBy(how = ID, using = "descriptionHeader")
  private static WebElement descriptionHeader = null;

//  @FindBy(how = ID, using = "closeButton")
@FindBy(how = XPATH, using = "//a[@ng-click='clearSearch()']")
  private static WebElement closeSearchResultsButton = null;

  @FindBy(how = ID, using = "searchIcon")
  private static WebElement searchIcon = null;

  @FindBy(how = ID, using = "saveButton")
  private static WebElement saveButton = null;

  @FindBy(how = ID, using = "saveErrorMsgDiv")
  private static WebElement saveErrorMsgDiv = null;

  @FindBy(how = ID, using = "cancelButton")
  private static WebElement cancelButton = null;

  @FindBy(how = ID, using = "successMessage")
  private static WebElement successMessage = null;

  @FindBy(how = ID, using = "viewHereLink")
  private static WebElement viewHereLink = null;

  @FindBy(how = ID, using = "program")
  private static WebElement programDropDown = null;

  @FindBy(how = ID, using = "searchNode")
  private static WebElement supervisoryNodeField = null;

  @FindBy(how = ID, using = "associatedFacilityField")
  private static WebElement associatedFacilityField = null;

  @FindBy(how = ID, using = "exportOrdersFalse")
  private static WebElement exportOrdersFalse = null;

  @FindBy(how = ID, using = "exportOrdersTrue")
  private static WebElement exportOrdersTrue = null;

  @FindBy(how = ID, using = "description")
  private static WebElement description = null;

  @FindBy(how = ID, using = "clearSearch")
  private static WebElement clearSearch = null;

  @FindBy(how = ID, using = "addNewSupplyLineHeader")
  private static WebElement addSupplyLineHeader = null;

  @FindBy(how = ID, using = "editSupplyLineHeader")
  private static WebElement editSupplyLineHeader = null;

  @FindBy(how = ID, using = "programLabel")
  private static WebElement programLabel = null;

  @FindBy(how = ID, using = "facilityExportOrdersLabel")
  private static WebElement facilityExportOrdersLabel = null;

  @FindBy(how = ID, using = "supervisoryNodeLabel")
  private static WebElement supervisoryNodeLabel = null;

  @FindBy(how = ID, using = "descriptionLabel")
  private static WebElement descriptionLabel = null;

  @FindBy(how = ID, using = "supplyingFacilityLabel")
  private static WebElement supplyingFacilityLabel = null;

  public SupplyLinePage(TestWebDriver driver) {
    super(driver);
    PageFactory.initElements(new AjaxElementLocatorFactory(TestWebDriver.getDriver(), 1), this);
    testWebDriver.setImplicitWait(1);
  }

  public String getSearchSupplyLineLabel() {
    testWebDriver.waitForElementToAppear(searchSupplyLineLabel);
    return searchSupplyLineLabel.getText();
  }

  public boolean isAddNewButtonDisplayed() {
    try {
      testWebDriver.waitForElementToAppear(supplyLineAddNew);
    } catch (TimeoutException e) {
      return false;
    } catch (NoSuchElementException e) {
      return false;
    }
    return supplyLineAddNew.isDisplayed();
  }

  public void clickAddNewButton() {
    testWebDriver.waitForElementToAppear(supplyLineAddNew);
    supplyLineAddNew.click();
  }

  public void clickSearchOptionButton() {
    testWebDriver.waitForElementToAppear(searchOptionButton);
    searchOptionButton.click();
  }

  public String getSelectedSearchOption() {
    testWebDriver.waitForElementToAppear(searchOptionButton);
    return searchOptionButton.getText();
  }

  public void selectSupplyLineAsSearchOption() {
    testWebDriver.waitForElementToAppear(searchOption1);
    searchOption1.click();
  }

  public void selectSupervisoryNodeAsSearchOption() {
    testWebDriver.waitForElementToAppear(searchOption2);
    searchOption2.click();
  }

  public void selectProgramAsSearchOption() {
    testWebDriver.waitForElementToAppear(searchOption3);
    searchOption3.click();
  }

  public void enterSearchParameter(String searchParameter) {
    testWebDriver.waitForElementToAppear(searchSupplyLineParameter);
    sendKeys(searchSupplyLineParameter, searchParameter);
  }

  public boolean isNoResultMessageDisplayed() {
    try {
      testWebDriver.waitForElementToAppear(noResultMessage);
    } catch (TimeoutException e) {
      return false;
    } catch (NoSuchElementException e) {
      return false;
    }
    return noResultMessage.isDisplayed();
  }

  public boolean isOneResultMessageDisplayed() {
    try {
      testWebDriver.waitForElementToAppear(oneResultMessage);
    } catch (TimeoutException e) {
      return false;
    } catch (NoSuchElementException e) {
      return false;
    }
    return oneResultMessage.isDisplayed();
  }

  public String getNResultsMessage() {
    testWebDriver.waitForElementToAppear(nResultsMessage);
    return nResultsMessage.getText();
  }

  public void closeSearchResults() {
    testWebDriver.waitForElementToAppear(closeSearchResultsButton);
      testWebDriver.click(closeSearchResultsButton);
      testWebDriver.refresh();

  }

  public boolean isProgramHeaderPresent() {
    try {
      testWebDriver.waitForElementToAppear(programHeader);
    } catch (TimeoutException e) {
      return false;
    } catch (NoSuchElementException e) {
      return false;
    }
    return programHeader.isDisplayed();
  }

  public String getProgramHeader() {
    testWebDriver.waitForElementToAppear(programHeader);
    return programHeader.getText();
  }

  public String getSupplyingFacilityHeader() {
    testWebDriver.waitForElementToAppear(supplyingFacilityHeader);
    return supplyingFacilityHeader.getText();
  }

  public String getSupervisoryNodeHeader() {
    testWebDriver.waitForElementToAppear(supervisoryNodeHeader);
    return supervisoryNodeHeader.getText();
  }

  public String getDescriptionHeader() {
    testWebDriver.waitForElementToAppear(descriptionHeader);
    return descriptionHeader.getText();
  }

  public String getProgram(int rowNumber) {
    WebElement program = testWebDriver.getElementById("program" + (rowNumber - 1));
    testWebDriver.waitForElementToAppear(program);
    return program.getText();
  }

  public String getSupplyingFacility(int rowNumber) {
    WebElement facility = testWebDriver.getElementById("facility" + (rowNumber - 1));
    testWebDriver.waitForElementToAppear(facility);
    return facility.getText();
  }

  public String getSupervisoryNode(int rowNumber) {
    WebElement node = testWebDriver.getElementById("supervisoryNode" + (rowNumber - 1));
    testWebDriver.waitForElementToAppear(node);
    return node.getText();
  }

  public String getDescription(int rowNumber) {
    WebElement description = testWebDriver.getElementById("description" + (rowNumber - 1));
    testWebDriver.waitForElementToAppear(description);
    return description.getText();
  }

  public void clickSearchIcon() {
    testWebDriver.waitForElementToAppear(searchIcon);
    searchIcon.click();
  }

  public boolean isSearchIconDisplayed() {
    testWebDriver.waitForElementToAppear(searchIcon);
    return searchIcon.isDisplayed();
  }

  public int getSizeOfResultsTable() {
    return testWebDriver.getElementsSizeByXpath("//*[@id='supplyLineTable']/tbody/tr");
  }

  public void clickSaveButton() {
    testWebDriver.waitForElementToAppear(saveButton);
    saveButton.click();
  }

  public void clickCancelButton() {
    testWebDriver.waitForElementToAppear(cancelButton);
    cancelButton.click();
  }

  public String getSuccessMessage() {
    testWebDriver.waitForElementToAppear(successMessage);
    return successMessage.getText();
  }

  public void clickViewHereLink() {
    testWebDriver.waitForElementToAppear(viewHereLink);
    viewHereLink.click();
  }

  public void selectProgram(String program) {
    testWebDriver.waitForElementToAppear(programDropDown);
    sendKeys(programDropDown, program);
  }

  public void searchSupervisoryNode(String node) {
    testWebDriver.waitForElementToAppear(supervisoryNodeField);
    supervisoryNodeField.click();
    sendKeys(supervisoryNodeField, node);
    testWebDriver.waitForAjax();
  }

  public void selectSupervisoryNodeResult(int rowNumber) {
    WebElement supervisoryNodeResult = testWebDriver.getElementById("result" + (rowNumber - 1));
    testWebDriver.waitForElementToAppear(supervisoryNodeResult);
    supervisoryNodeResult.click();
  }

  public void clickSupplyingFacilityField() {
    testWebDriver.waitForElementToAppear(associatedFacilityField);
    associatedFacilityField.click();
  }

  public void selectExportOrderFlag(boolean exportOrdersOption) {
    if (exportOrdersOption)
      exportOrdersTrue.click();
    else
      exportOrdersFalse.click();
  }

  public void enterValuesInSupplyLineFields(String program, String node, boolean exportOrdersOption) {
    selectProgram(program);
    searchSupervisoryNode(node);
    selectSupervisoryNodeResult(1);
    selectExportOrderFlag(exportOrdersOption);
  }

  public void enterDescription(String desc) {
    testWebDriver.waitForElementToAppear(description);
    description.sendKeys(desc);
  }

  public String getSaveErrorMessage() {
    testWebDriver.waitForElementToAppear(saveErrorMsgDiv);
    return saveErrorMsgDiv.getText();
  }

  public void clickClearNodeSearchResult() {
    testWebDriver.waitForElementToAppear(clearSearch);
    clearSearch.click();
  }

  public String getAddSupplyLineHeader() {
    testWebDriver.waitForElementToAppear(addSupplyLineHeader);
    return addSupplyLineHeader.getText();
  }

  public String getEditSupplyLineHeader() {
    testWebDriver.waitForElementToAppear(editSupplyLineHeader);
    return editSupplyLineHeader.getText();
  }

  public String getProgramLabel() {
    testWebDriver.waitForElementToAppear(programLabel);
    return programLabel.getText();
  }

  public String getFacilityExportOrdersLabel() {
    testWebDriver.waitForElementToAppear(facilityExportOrdersLabel);
    return facilityExportOrdersLabel.getText();
  }

  public String getSupervisoryNodeLabel() {
    testWebDriver.waitForElementToAppear(supervisoryNodeLabel);
    return supervisoryNodeLabel.getText();
  }

  public String getDescriptionLabel() {
    testWebDriver.waitForElementToAppear(descriptionLabel);
    return descriptionLabel.getText();
  }

  public String getSupplyingFacilityLabel() {
    testWebDriver.waitForElementToAppear(supplyingFacilityLabel);
    return supplyingFacilityLabel.getText();
  }
}

