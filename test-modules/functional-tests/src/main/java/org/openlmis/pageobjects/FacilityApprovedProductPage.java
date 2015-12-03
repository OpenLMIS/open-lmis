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
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.pagefactory.AjaxElementLocatorFactory;

import java.util.List;

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

  @FindBy(how = ID, using = "nResultsMessage")
  private static WebElement nResultsMessage = null;

  @FindBy(how = ID, using = "searchIcon")
  private static WebElement searchIcon = null;

  @FindBy(how = ID, using = "clearProductSearch")
  private static WebElement clearProductSearchButton = null;

  @FindBy(how = ID, using = "addFacilityApprovedProductHeader")
  private static WebElement addFacilityApprovedProductHeader = null;

  @FindBy(how = ID, using = "categoryLabel")
  private static WebElement categoryLabel = null;

  @FindBy(how = ID, using = "productLabel")
  private static WebElement productLabel = null;

  @FindBy(how = ID, using = "maxMonthsOfStockLabel")
  private static WebElement maxMonthsOfStockLabel = null;

  @FindBy(how = ID, using = "minMonthsOfStockLabel")
  private static WebElement minMonthsOfStockLabel = null;

  @FindBy(how = ID, using = "eopLabel")
  private static WebElement eopLabel = null;

  @FindBy(how = ID, using = "productCategory")
  private static WebElement productCategoryDropDown = null;

  @FindBy(how = ID, using = "product")
  private static WebElement productDropDown = null;

  @FindBy(how = ID, using = "facilityTypeApprovedProduct.maxMonthsOfStock")
  private static WebElement maxMonthsOfStock = null;

  @FindBy(how = ID, using = "facilityTypeApprovedProduct.minMonthsOfStock")
  private static WebElement minMonthsOfStock = null;

  @FindBy(how = ID, using = "facilityTypeApprovedProduct.eop")
  private static WebElement eop = null;

  @FindBy(how = ID, using = "addedProductHeader")
  private static WebElement addedProductHeader = null;

  @FindBy(how = ID, using = "addedMaxMonthsOfStockHeader")
  private static WebElement addedMaxMonthsOfStockHeader = null;

  @FindBy(how = ID, using = "addedMinMonthsOfStockHeader")
  private static WebElement addedMinMonthsOfStockHeader = null;

  @FindBy(how = ID, using = "addedEopHeader")
  private static WebElement addedEopHeader = null;

  @FindBy(how = ID, using = "addFacilityTypeApprovedProduct")
  private static WebElement addFacilityTypeApprovedProductButton = null;

  @FindBy(how = ID, using = "doneFacilityApprovedProductAdd")
  private static WebElement doneFacilityApprovedProductAdd = null;

  @FindBy(how = ID, using = "cancelFacilityApprovedProductAdd")
  private static WebElement cancelFacilityApprovedProductAdd = null;

  @FindBy(how = ID, using = "saveSuccessMsgDiv")
  private static WebElement saveSuccessMsg = null;

  @FindBy(how = ID, using = "modalErrorMessage")
  private static WebElement addModalErrorMessage = null;

  @FindBy(how = ID, using = "saveErrorMsgDiv")
  private static WebElement saveErrorMsg = null;

  @FindBy(how = ID, using = "dialogMessage")
  private static WebElement dialogMessage = null;

  @FindBy(how = ID, using = "button_Cancel")
  private static WebElement cancelDeleteButton = null;

  @FindBy(how = ID, using = "button_OK")
  private static WebElement okDeleteButton = null;

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
    WebElement globalActive;
    try {
      globalActive = testWebDriver.getElementById("GlobalActive" + (rowNumber - 1));
      testWebDriver.waitForElementToAppear(globalActive);
    } catch (TimeoutException e) {
      return false;
    } catch (NoSuchElementException e) {
      return false;
    }
    globalActive = testWebDriver.getElementById("GlobalActive" + (rowNumber - 1));
    return globalActive.isDisplayed();
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

  public int getSizeOfResultsTable() {
    return testWebDriver.getElementsSizeByXpath("//*[@id='facilityApprovedProductTable']/tbody");
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

  public String getAddFacilityApprovedProductHeader() {
    testWebDriver.waitForElementToAppear(addFacilityApprovedProductHeader);
    return addFacilityApprovedProductHeader.getText();
  }

  public String getAddCategoryLabel() {
    testWebDriver.waitForElementToAppear(categoryLabel);
    return categoryLabel.getText();
  }

  public String getAddProductLabel() {
    testWebDriver.waitForElementToAppear(productLabel);
    return productLabel.getText();
  }

  public String getAddMaxMonthsOfStockLabel() {
    testWebDriver.waitForElementToAppear(maxMonthsOfStockLabel);
    return maxMonthsOfStockLabel.getText();
  }

  public String getAddMinMonthsOfStockLabel() {
    testWebDriver.waitForElementToAppear(minMonthsOfStockLabel);
    return minMonthsOfStockLabel.getText();
  }

  public String getAddEopLabel() {
    testWebDriver.waitForElementToAppear(eopLabel);
    return eopLabel.getText();
  }

  public List<String> getListOfCategories() {
    testWebDriver.waitForElementToAppear(productCategoryDropDown);
    return testWebDriver.getListOfOptions(productCategoryDropDown);
  }

  public List<String> getListOfProducts() {
    testWebDriver.waitForElementToAppear(productDropDown);
    return testWebDriver.getListOfOptions(productDropDown);
  }

  public void selectCategory(String category) {
    testWebDriver.waitForElementToAppear(productCategoryDropDown);
    testWebDriver.selectByVisibleText(productCategoryDropDown, category);
  }

  public void selectProduct(String product) {
    testWebDriver.waitForElementToAppear(productDropDown);
    testWebDriver.selectByVisibleText(productDropDown, product);
  }

  public void enterMaxMonthsOfStock(String maxMonthsInput) {
    testWebDriver.waitForElementToAppear(maxMonthsOfStock);
    sendKeys(maxMonthsOfStock, maxMonthsInput);
  }

  public void enterMinMonthsOfStock(String minMonthsInput) {
    testWebDriver.waitForElementToAppear(minMonthsOfStock);
    sendKeys(minMonthsOfStock, minMonthsInput);
  }

  public void enterEop(String eopInput) {
    testWebDriver.waitForElementToAppear(eop);
    sendKeys(eop, eopInput);
  }

  public String getAddedFacilityTypeApprovedProductNameLabel(int rowNumber) {
    WebElement productName = testWebDriver.getElementById("facilityTypeApprovedProductNameLabel" + (rowNumber - 1));
    testWebDriver.waitForElementToAppear(productName);
    return productName.getText();
  }

  public String getAddedMaxMonthsOfStock(int rowNumber) {
    WebElement maxMonthsOfStock = testWebDriver.getElementById("facilityTypeApprovedProduct.maxMonthsOfStock" + (rowNumber - 1));
    testWebDriver.waitForElementToAppear(maxMonthsOfStock);
    return maxMonthsOfStock.getAttribute("value");
  }

  public String getAddedMinMonthsOfStock(int rowNumber) {
    WebElement minMonthsOfStock = testWebDriver.getElementById("facilityTypeApprovedProduct.minMonthsOfStock" + (rowNumber - 1));
    testWebDriver.waitForElementToAppear(minMonthsOfStock);
    return minMonthsOfStock.getAttribute("value");
  }

  public String getAddedEop(int rowNumber) {
    WebElement eop = testWebDriver.getElementById("facilityTypeApprovedProduct.eop" + (rowNumber - 1));
    testWebDriver.waitForElementToAppear(eop);
    return eop.getAttribute("value");
  }

  public void reenterAddedMaxMonthsOfStock(String maxMonthsInput, int rowNumber) {
    WebElement maxMonthsOfStock = testWebDriver.getElementById("facilityTypeApprovedProduct.maxMonthsOfStock" + (rowNumber - 1));
    testWebDriver.waitForElementToAppear(maxMonthsOfStock);
    sendKeys(maxMonthsOfStock, maxMonthsInput);
  }

  public void clickCrossButtonForAddedRow(int rowNumber) {
    WebElement crossButton = testWebDriver.getElementById("crossButton" + (rowNumber - 1));
    testWebDriver.waitForElementToAppear(crossButton);
    crossButton.click();
  }

  public void clickAddProductButton() {
    testWebDriver.waitForElementToAppear(addFacilityTypeApprovedProductButton);
    addFacilityTypeApprovedProductButton.click();
  }

  public boolean isAddProductButtonEnabled() {
    testWebDriver.waitForElementToAppear(addFacilityTypeApprovedProductButton);
    return addFacilityTypeApprovedProductButton.isEnabled();
  }

  public void clickAddDoneButton() {
    testWebDriver.waitForElementToAppear(doneFacilityApprovedProductAdd);
    doneFacilityApprovedProductAdd.click();
  }

  public void clickAddCancelButton() {
    testWebDriver.waitForElementToAppear(cancelFacilityApprovedProductAdd);
    cancelFacilityApprovedProductAdd.click();
  }

  public String getAddModalErrorMessage() {
    testWebDriver.waitForElementToAppear(addModalErrorMessage);
    return addModalErrorMessage.getText();
  }

  public String getSaveSuccessMessage() {
    testWebDriver.waitForElementToAppear(saveSuccessMsg);
    return saveSuccessMsg.getText();
  }

  public String getAddedProductHeader() {
    testWebDriver.waitForElementToAppear(addedProductHeader);
    return addedProductHeader.getText();
  }

  public String getAddedMaxMonthsOfStockHeader() {
    testWebDriver.waitForElementToAppear(addedMaxMonthsOfStockHeader);
    return addedMaxMonthsOfStockHeader.getText();
  }

  public String getAddedMinMonthsOfStockHeader() {
    testWebDriver.waitForElementToAppear(addedMinMonthsOfStockHeader);
    return addedMinMonthsOfStockHeader.getText();
  }

  public String getAddedEopHeader() {
    testWebDriver.waitForElementToAppear(addedEopHeader);
    return addedEopHeader.getText();
  }

  public boolean isAddedProductHeaderDisplayed() {
    try {
      testWebDriver.waitForElementToAppear(addedProductHeader);
    } catch (TimeoutException e) {
      return false;
    } catch (NoSuchElementException e) {
      return false;
    }
    return addedProductHeader.isDisplayed();
  }

  public String getProductTypeInDropDown(int productNumber) {
    testWebDriver.getElementByXpath("//*[@id='s2id_product']/a/span").click();
    WebElement type = testWebDriver.getElementByXpath("//*[@id='select2-drop']/ul/li[" + (productNumber + 1) + "]/div/div/div[5]");
    testWebDriver.waitForElementToAppear(type);
    String text = type.getText();
    testWebDriver.getElementByXpath("//*[@id='s2id_product']/a/span").click();
    return text;
  }

  public void clickEditFacilityApprovedProductButton(int rowNumber) {
    WebElement editButton = testWebDriver.getElementById("editButton" + (rowNumber - 1));
    testWebDriver.waitForElementToAppear(editButton);
    editButton.click();
  }

  public boolean isEditFacilityApprovedProductButtonDisplayed(int rowNumber) {
    WebElement editButton;
    try {
      editButton = testWebDriver.getElementById("editButton" + (rowNumber - 1));
      testWebDriver.waitForElementToAppear(editButton);
    } catch (TimeoutException e) {
      return false;
    } catch (NoSuchElementException e) {
      return false;
    }
    return editButton.isDisplayed();
  }

  public void editMaxMonthsOfStock(String maxMonthsInput, int rowNumber) {
    WebElement maxMonthsOfStock = testWebDriver.getElementById("editMaxMonthsOfStock" + (rowNumber - 1));
    testWebDriver.waitForElementToAppear(maxMonthsOfStock);
    sendKeys(maxMonthsOfStock, maxMonthsInput);
  }

  public String getEditMaxMonthsOfStock(int rowNumber) {
    WebElement maxMonthsOfStock = testWebDriver.getElementById("editMaxMonthsOfStock" + (rowNumber - 1));
    testWebDriver.waitForElementToAppear(maxMonthsOfStock);
    return maxMonthsOfStock.getAttribute("value");
  }

  public void editMinMonthsOfStock(String minMonthsInput, int rowNumber) {
    WebElement minMonthsOfStock = testWebDriver.getElementById("editMinMonthsOfStock" + (rowNumber - 1));
    testWebDriver.waitForElementToAppear(minMonthsOfStock);
    sendKeys(minMonthsOfStock, minMonthsInput);
  }

  public void editEop(String eopInput, int rowNumber) {
    WebElement eop = testWebDriver.getElementById("editEop" + (rowNumber - 1));
    testWebDriver.waitForElementToAppear(eop);
    sendKeys(eop, eopInput);
  }

  public String getEditEop(int rowNumber) {
    WebElement eop = testWebDriver.getElementById("editEop" + (rowNumber - 1));
    testWebDriver.waitForElementToAppear(eop);
    return eop.getAttribute("value");
  }

  public void clickSaveButtonForEditProduct(int rowNumber) {
    WebElement saveEditButton = testWebDriver.getElementById("save" + (rowNumber - 1));
    testWebDriver.waitForElementToAppear(saveEditButton);
    saveEditButton.click();
  }

  public boolean isSaveButtonForEditProductDisplayed(int rowNumber) {
    WebElement saveEditButton;
    try {
      saveEditButton = testWebDriver.getElementById("save" + (rowNumber - 1));
      testWebDriver.waitForElementToAppear(saveEditButton);
    } catch (TimeoutException e) {
      return false;
    } catch (NoSuchElementException e) {
      return false;
    }
    return saveEditButton.isDisplayed();
  }

  public void clickCancelButtonForEditProduct(int rowNumber) {
    WebElement cancelEditButton = testWebDriver.getElementById("cancel" + (rowNumber - 1));
    testWebDriver.waitForElementToAppear(cancelEditButton);
    cancelEditButton.click();
  }

  public boolean isCancelButtonForEditProductDisplayed(int rowNumber) {
    WebElement cancelEditButton;
    try {
      cancelEditButton = testWebDriver.getElementById("cancel" + (rowNumber - 1));
      testWebDriver.waitForElementToAppear(cancelEditButton);
    } catch (TimeoutException e) {
      return false;
    } catch (NoSuchElementException e) {
      return false;
    }
    return cancelEditButton.isDisplayed();
  }

  public String getSaveErrorMessage() {
    testWebDriver.waitForElementToAppear(saveErrorMsg);
    return saveErrorMsg.getText();
  }

  public boolean isDeleteFacilityApprovedProductButtonDisplayed(int rowNumber) {
    WebElement deleteButton;
    try {
      deleteButton = testWebDriver.getElementById("deleteButton" + (rowNumber - 1));
      testWebDriver.waitForElementToAppear(deleteButton);
    } catch (TimeoutException e) {
      return false;
    } catch (NoSuchElementException e) {
      return false;
    }
    return deleteButton.isDisplayed();
  }

  public void clickDeleteButton(int rowNumber) {
    WebElement deleteButton = testWebDriver.getElementById("deleteButton" + (rowNumber - 1));
    testWebDriver.waitForElementToAppear(deleteButton);
    deleteButton.click();
  }

  public String getDialogBoxMessage() {
    testWebDriver.waitForElementToAppear(dialogMessage);
    return dialogMessage.getText();
  }

  public void clickCancelDeleteButton() {
    testWebDriver.waitForElementToAppear(cancelDeleteButton);
    cancelDeleteButton.click();
  }

  public void clickOkDeleteButton() {
    testWebDriver.waitForElementToAppear(okDeleteButton);
    okDeleteButton.click();
  }
}
