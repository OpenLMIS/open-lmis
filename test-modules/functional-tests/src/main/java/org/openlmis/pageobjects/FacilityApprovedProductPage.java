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

import java.util.List;
import java.util.NoSuchElementException;

import static org.openqa.selenium.support.How.ID;

public class FacilityApprovedProductPage extends Page {

  @FindBy(how = ID, using = "facilityApprovedProductsHeader")
  private static WebElement facilityApprovedProductsHeader = null;

  @FindBy(how = ID, using = "programLabel")
  private static WebElement programLabel = null;

  @FindBy(how = ID, using = "programs")
  private static WebElement programDropDown = null;

  @FindBy(how = ID, using = "facilityTypeLabel")
  private static WebElement facilityTypeLabel = null;

  @FindBy(how = ID, using = "facilityType")
  private static WebElement facilityTypeDropDown = null;

  @FindBy(how = ID, using = "selectionMandatoryMessage")
  private static WebElement selectionMandatoryMessage = null;

  @FindBy(how = ID, using = "searchFacilityApprovedProductLabel")
  private static WebElement searchFacilityApprovedProductLabel = null;

  @FindBy(how = ID, using = "searchFacilityApprovedProduct")
  private static WebElement searchFacilityApprovedProductParameter = null;

  @FindBy(how = ID, using = "facilityApprovedProductAddNew")
  private static WebElement facilityApprovedProductAddNew = null;

  @FindBy(how = ID, using = "nRecordsMessage")
  private static WebElement nRecordsMessage = null;

  @FindBy(how = ID, using = "noRecordsMessage")
  private static WebElement noRecordsMessage = null;

  @FindBy(how = ID, using = "fullSupply")
  private static WebElement fullSupplyHeader = null;

  @FindBy(how = ID, using = "code")
  private static WebElement codeHeader = null;

  @FindBy(how = ID, using = "name")
  private static WebElement nameHeader = null;

  @FindBy(how = ID, using = "strength")
  private static WebElement strengthHeader = null;

  @FindBy(how = ID, using = "unitOfMeasure")
  private static WebElement unitOfMeasureHeader = null;

  @FindBy(how = ID, using = "maxMonthStocks")
  private static WebElement maxMonthsOfStockHeader = null;

  @FindBy(how = ID, using = "minMonthStocks")
  private static WebElement minMonthsOfStockHeader = null;

  @FindBy(how = ID, using = "eop")
  private static WebElement eopHeader = null;

  @FindBy(how = ID, using = "globalActive")
  private static WebElement globalActiveHeader = null;

  @FindBy(how = ID, using = "activeAtProgram")
  private static WebElement activeAtProgramHeader = null;

  @FindBy(how = ID, using = "noResultMessage")
  private static WebElement noResultMessage = null;

  @FindBy(how = ID, using = "oneResultMessage")
  private static WebElement oneResultMessage = null;

  @FindBy(how = ID, using = "nResultsMessage")
  private static WebElement nResultsMessage = null;

  @FindBy(how = ID, using = "searchIcon")
  private static WebElement searchIcon = null;

  @FindBy(how = ID, using = "saveButton")
  private static WebElement saveButton = null;

  @FindBy(how = ID, using = "saveErrorMsgDiv")
  private static WebElement saveErrorMsg = null;

  @FindBy(how = ID, using = "cancelButton")
  private static WebElement cancelButton = null;

  @FindBy(how = ID, using = "successMessage")
  private static WebElement successMessage = null;

  @FindBy(how = ID, using = "viewHereLink")
  private static WebElement viewHereLink = null;

  @FindBy(how = ID, using = "clearProductSearch")
  private static WebElement clearProductSearchButton;

  public FacilityApprovedProductPage(TestWebDriver driver) {
    super(driver);
    PageFactory.initElements(new AjaxElementLocatorFactory(TestWebDriver.getDriver(), 1), this);
    testWebDriver.setImplicitWait(1);
  }

  public String getFacilityApprovedProductHeader() {
    testWebDriver.waitForElementToAppear(facilityApprovedProductsHeader);
    return facilityApprovedProductsHeader.getText();
  }

  public String getProgramLabel() {
    testWebDriver.waitForElementToAppear(programLabel);
    return programLabel.getText();
  }

  public String getFacilityTypeLabel() {
    testWebDriver.waitForElementToAppear(facilityTypeLabel);
    return facilityTypeLabel.getText();
  }

  public void selectProgram(String program) {
    testWebDriver.waitForElementToAppear(programDropDown);
    testWebDriver.selectByVisibleText(programDropDown, program);
  }

  public void selectFacilityType(String facilityType) {
    testWebDriver.waitForElementToAppear(facilityTypeDropDown);
    testWebDriver.selectByVisibleText(facilityTypeDropDown, facilityType);
  }

  public boolean isSelectionMandatoryMessageDisplayed() {
    try {
      testWebDriver.waitForElementToAppear(selectionMandatoryMessage);
    } catch (TimeoutException e) {
      return false;
    } catch (NoSuchElementException e) {
      return false;
    }
    return selectionMandatoryMessage.isDisplayed();
  }

  public String getSelectionMandatoryMessage() {
    testWebDriver.waitForElementToAppear(selectionMandatoryMessage);
    return selectionMandatoryMessage.getText();
  }

  public String getSearchFacilityApprovedProductLabel() {
    testWebDriver.waitForElementToAppear(searchFacilityApprovedProductLabel);
    return searchFacilityApprovedProductLabel.getText();
  }

  public boolean isSearchFacilityApprovedProductLabelDisplayed() {
    try {
      testWebDriver.waitForElementToAppear(searchFacilityApprovedProductLabel);
    } catch (TimeoutException e) {
      return false;
    } catch (NoSuchElementException e) {
      return false;
    }
    return searchFacilityApprovedProductLabel.isDisplayed();
  }

  public boolean isNRecordsMessageDisplayed() {
    try {
      testWebDriver.waitForElementToAppear(nRecordsMessage);
    } catch (TimeoutException e) {
      return false;
    } catch (NoSuchElementException e) {
      return false;
    }
    return nRecordsMessage.isDisplayed();
  }

  public boolean isNoRecordsMessageDisplayed() {
    try {
      testWebDriver.waitForElementToAppear(noRecordsMessage);
    } catch (TimeoutException e) {
      return false;
    } catch (NoSuchElementException e) {
      return false;
    }
    return noRecordsMessage.isDisplayed();
  }

  public String getNRecordsMessage() {
    testWebDriver.waitForElementToAppear(nRecordsMessage);
    return nRecordsMessage.getText();
  }

  public String getNoRecordsMessage() {
    testWebDriver.waitForElementToAppear(noRecordsMessage);
    return noRecordsMessage.getText();
  }

  public boolean isAddNewButtonDisplayed() {
    try {
      testWebDriver.waitForElementToAppear(facilityApprovedProductAddNew);
    } catch (TimeoutException e) {
      return false;
    } catch (NoSuchElementException e) {
      return false;
    }
    return facilityApprovedProductAddNew.isDisplayed();
  }

  public void clickAddNewButton() {
    testWebDriver.waitForElementToAppear(facilityApprovedProductAddNew);
    facilityApprovedProductAddNew.click();
  }

  public void enterSearchParameter(String searchParameter) {
    testWebDriver.waitForElementToAppear(searchFacilityApprovedProductParameter);
    sendKeys(searchFacilityApprovedProductParameter, searchParameter);
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

  public boolean isNResultsMessageDisplayed() {
    try {
      testWebDriver.waitForElementToAppear(nResultsMessage);
    } catch (TimeoutException e) {
      return false;
    } catch (NoSuchElementException e) {
      return false;
    }
    return nResultsMessage.isDisplayed();
  }

  public String getNResultsMessage() {
    testWebDriver.waitForElementToAppear(nResultsMessage);
    return nResultsMessage.getText();
  }

  public boolean isFullSupplyHeaderPresent() {
    try {
      testWebDriver.waitForElementToAppear(fullSupplyHeader);
    } catch (TimeoutException e) {
      return false;
    } catch (NoSuchElementException e) {
      return false;
    }
    return fullSupplyHeader.isDisplayed();
  }

  public String getFullSupplyHeader() {
    testWebDriver.waitForElementToAppear(fullSupplyHeader);
    return fullSupplyHeader.getText();
  }

  public String getCodeHeader() {
    testWebDriver.waitForElementToAppear(codeHeader);
    return codeHeader.getText();
  }

  public String getNameHeader() {
    testWebDriver.waitForElementToAppear(nameHeader);
    return nameHeader.getText();
  }

  public String getStrengthHeader() {
    testWebDriver.waitForElementToAppear(strengthHeader);
    return strengthHeader.getText();
  }

  public String getUnitOfMeasureHeader() {
    testWebDriver.waitForElementToAppear(unitOfMeasureHeader);
    return unitOfMeasureHeader.getText();
  }

  public String getMaxMonthsOfStockHeader() {
    testWebDriver.waitForElementToAppear(maxMonthsOfStockHeader);
    return maxMonthsOfStockHeader.getText();
  }

  public String getMinMonthsOfStockHeader() {
    testWebDriver.waitForElementToAppear(minMonthsOfStockHeader);
    return minMonthsOfStockHeader.getText();
  }

  public String getEopHeader() {
    testWebDriver.waitForElementToAppear(eopHeader);
    return eopHeader.getText();
  }

  public String getGlobalActiveHeader() {
    testWebDriver.waitForElementToAppear(globalActiveHeader);
    return globalActiveHeader.getText();
  }

  public String getActiveAtProgramHeader() {
    testWebDriver.waitForElementToAppear(activeAtProgramHeader);
    return activeAtProgramHeader.getText();
  }

  public boolean isFullSupply(int rowNumber) {
    try {
      WebElement fullSupply = testWebDriver.getElementById("fullSupply" + (rowNumber - 1));
      testWebDriver.waitForElementToAppear(fullSupply);
    } catch (TimeoutException e) {
      return false;
    } catch (NoSuchElementException e) {
      return false;
    }
    WebElement fullSupply = testWebDriver.getElementById("fullSupply" + (rowNumber - 1));
    return fullSupply.isDisplayed();
  }

  public String getCode(int rowNumber) {
    WebElement code = testWebDriver.getElementById("code" + (rowNumber - 1));
    testWebDriver.waitForElementToAppear(code);
    return code.getText();
  }

  public String getName(int rowNumber) {
    WebElement name = testWebDriver.getElementById("name" + (rowNumber - 1));
    testWebDriver.waitForElementToAppear(name);
    return name.getText();
  }

  public String getStrength(int rowNumber) {
    WebElement strength = testWebDriver.getElementById("strength" + (rowNumber - 1));
    testWebDriver.waitForElementToAppear(strength);
    return strength.getText();
  }

  public String getUnitOfMeasure(int rowNumber) {
    WebElement unit = testWebDriver.getElementById("unit" + (rowNumber - 1));
    testWebDriver.waitForElementToAppear(unit);
    return unit.getText();
  }

  public String getMaxMonthsOfStock(int rowNumber) {
    WebElement maxMonthsOfStock = testWebDriver.getElementById("maxMonthsOfStock" + (rowNumber - 1));
    testWebDriver.waitForElementToAppear(maxMonthsOfStock);
    return maxMonthsOfStock.getText();
  }

  public String getMinMonthsOfStock(int rowNumber) {
    WebElement minMonthsOfStock = testWebDriver.getElementById("minMonthsOfStock" + (rowNumber - 1));
    testWebDriver.waitForElementToAppear(minMonthsOfStock);
    return minMonthsOfStock.getText();
  }

  public String getEop(int rowNumber) {
    WebElement eop = testWebDriver.getElementById("eop" + (rowNumber - 1));
    testWebDriver.waitForElementToAppear(eop);
    return eop.getText();
  }

  public boolean isGloballyActive(int rowNumber) {
    try {
      WebElement GlobalActive = testWebDriver.getElementById("GlobalActive" + (rowNumber - 1));
      testWebDriver.waitForElementToAppear(GlobalActive);
    } catch (TimeoutException e) {
      return false;
    } catch (NoSuchElementException e) {
      return false;
    }
    WebElement GlobalActive = testWebDriver.getElementById("GlobalActive" + (rowNumber - 1));
    return GlobalActive.isDisplayed();
  }

  public boolean isActiveAtProgram(int rowNumber) {
    try {
      WebElement activeAtProgram = testWebDriver.getElementById("activeAtProgram" + (rowNumber - 1));
      testWebDriver.waitForElementToAppear(activeAtProgram);
    } catch (TimeoutException e) {
      return false;
    } catch (NoSuchElementException e) {
      return false;
    }
    WebElement activeAtProgram = testWebDriver.getElementById("activeAtProgram" + (rowNumber - 1));
    return activeAtProgram.isDisplayed();
  }

  public void clickSearchIcon() {
    testWebDriver.waitForElementToAppear(searchIcon);
    searchIcon.click();
  }

  public boolean isSearchIconDisplayed() {
    try {
      testWebDriver.waitForElementToAppear(searchIcon);
    } catch (TimeoutException e) {
      return false;
    } catch (NoSuchElementException e) {
      return false;
    }
    return searchIcon.isDisplayed();
  }

  public void clickSaveButton() {
    testWebDriver.waitForElementToAppear(saveButton);
    saveButton.click();
  }

  public void clickCancelButton() {
    testWebDriver.waitForElementToAppear(cancelButton);
    cancelButton.click();
  }

  public String getSaveErrorMessage() {
    testWebDriver.waitForElementToAppear(saveErrorMsg);
    return saveErrorMsg.getText();
  }

  public String getSuccessMessage() {
    testWebDriver.waitForElementToAppear(successMessage);
    return successMessage.getText();
  }

  public void clickViewHereLink() {
    testWebDriver.waitForElementToAppear(viewHereLink);
    viewHereLink.click();
  }

  public int getSizeOfResultsTable() {
    return testWebDriver.getElementsSizeByXpath("//*[@id='facilityApprovedProductTable']/tbody");
  }

  public List<String> getListOfPrograms() {
    testWebDriver.waitForElementToAppear(programDropDown);
    return testWebDriver.getListOfOptions(programDropDown);
  }

  public List<String> getListOfFacilityTypes() {
    testWebDriver.waitForElementToAppear(facilityTypeDropDown);
    return testWebDriver.getListOfOptions(facilityTypeDropDown);
  }

  public boolean isClearSearchButtonEnabled() {
    testWebDriver.waitForElementToAppear(clearProductSearchButton);
    return clearProductSearchButton.isEnabled();
  }

  public void clickClearSearchButton() {
    testWebDriver.waitForElementToAppear(clearProductSearchButton);
    clearProductSearchButton.click();
  }

  public String getCategory(int rowNumber) {
    WebElement category = testWebDriver.getElementById("category" + (rowNumber - 1));
    return category.getText();
  }
}
