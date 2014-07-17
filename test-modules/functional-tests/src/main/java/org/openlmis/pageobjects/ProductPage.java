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

public class ProductPage extends Page {

  @FindBy(how = ID, using = "searchProductsHeaderLabel")
  private static WebElement searchProductsHeader = null;

  @FindBy(how = ID, using = "searchOptionButton")
  private static WebElement searchOptionButton = null;

  @FindBy(how = ID, using = "selectedSearchOption")
  private static WebElement selectedSearchOption = null;

  @FindBy(how = ID, using = "searchProgramProduct")
  private static WebElement searchProgramProductParameter = null;

  @FindBy(how = ID, using = "searchIcon")
  private static WebElement searchIcon = null;

  @FindBy(how = ID, using = "searchOption0")
  private static WebElement searchOptionProduct = null;

  @FindBy(how = ID, using = "searchOption1")
  private static WebElement searchOptionProgram = null;

  @FindBy(how = ID, using = "nResultsMessage")
  private static WebElement nResultsMessage = null;

  @FindBy(how = ID, using = "noResultMessage")
  private static WebElement noResultMessage = null;

  @FindBy(how = ID, using = "oneResultMessage")
  private static WebElement oneResultMessage = null;

  @FindBy(how = ID, using = "closeButton")
  private static WebElement closeSearchResultsButton = null;

  @FindBy(how = ID, using = "fullSupply")
  private static WebElement fullSupplyHeader = null;

  @FindBy(how = ID, using = "code")
  private static WebElement codeHeader = null;

  @FindBy(how = ID, using = "name")
  private static WebElement nameHeader = null;

  @FindBy(how = ID, using = "program")
  private static WebElement programHeader = null;

  @FindBy(how = ID, using = "strength")
  private static WebElement strengthHeader = null;

  @FindBy(how = ID, using = "unitOfMeasure")
  private static WebElement unitOfMeasureHeader = null;

  @FindBy(how = ID, using = "dispensingUnit")
  private static WebElement dispensingUnitHeader = null;

  @FindBy(how = ID, using = "packSize")
  private static WebElement packSizeHeader = null;

  @FindBy(how = ID, using = "globalActive")
  private static WebElement globalActiveHeader = null;

  @FindBy(how = ID, using = "activeAtProgram")
  private static WebElement activeAtProgramHeader = null;

  @FindBy(how = ID, using = "programProductTable")
  private static WebElement productSearchResults = null;

  @FindBy(how = ID, using = "productAddNew")
  private static WebElement productAddNewButton = null;

  @FindBy(how = ID, using = "addNewProductHeader")
  private static WebElement addNewProductHeader = null;

  @FindBy(how = ID, using = "editProductHeader")
  private static WebElement editProductHeader = null;

  @FindBy(how = ID, using = "basicInformationLabel")
  private static WebElement basicInformationLabel = null;

  @FindBy(how = ID, using = "productCodeLabel")
  private static WebElement productCodeLabel = null;

  @FindBy(how = ID, using = "code")
  private static WebElement codeInputField = null;

  @FindBy(how = ID, using = "productPrimaryNameLabel")
  private static WebElement productPrimaryNameLabel = null;

  @FindBy(how = ID, using = "primaryName")
  private static WebElement primaryNameInputField = null;

  @FindBy(how = ID, using = "productTypeLabel")
  private static WebElement productTypeLabel = null;

  @FindBy(how = ID, using = "type")
  private static WebElement typeInputField = null;

  @FindBy(how = ID, using = "productFullNameLabel")
  private static WebElement productFullNameLabel = null;

  @FindBy(how = ID, using = "fullName")
  private static WebElement fullNameInputField = null;

  @FindBy(how = ID, using = "productGroupLabel")
  private static WebElement productGroupLabel = null;

  @FindBy(how = ID, using = "productGroup")
  private static WebElement productGroupDropDown = null;

  @FindBy(how = ID, using = "productDescriptionLabel")
  private static WebElement productDescriptionLabel = null;

  @FindBy(how = ID, using = "description")
  private static WebElement descriptionInputField = null;

  @FindBy(how = ID, using = "productFormLabel")
  private static WebElement productFormLabel = null;

  @FindBy(how = ID, using = "form")
  private static WebElement formDropDown = null;

  @FindBy(how = ID, using = "productStrengthLabel")
  private static WebElement productStrengthLabel = null;

  @FindBy(how = ID, using = "strength")
  private static WebElement strengthInputField = null;

  @FindBy(how = ID, using = "dosageUnitLabel")
  private static WebElement dosageUnitLabel = null;

  @FindBy(how = ID, using = "dosageUnit")
  private static WebElement dosageUnitDropDown = null;

  @FindBy(how = ID, using = "productDispensingUnitLabel")
  private static WebElement productDispensingUnitLabel = null;

  @FindBy(how = ID, using = "dispensingUnit")
  private static WebElement dispensingUnitInputField = null;

  @FindBy(how = ID, using = "productDosesPerDispensingUnitLabel")
  private static WebElement productDosesPerDispensingUnitLabel = null;

  @FindBy(how = ID, using = "product.dosesPerDispensingUnit")
  private static WebElement dosesPerDispensingUnitInputField = null;

  @FindBy(how = ID, using = "productPackSizeLabel")
  private static WebElement productPackSizeLabel = null;

  @FindBy(how = ID, using = "product.packSize")
  private static WebElement packSizeInputField = null;

  @FindBy(how = ID, using = "productPackRoundingThresholdLabel")
  private static WebElement productPackRoundingThresholdLabel = null;

  @FindBy(how = ID, using = "product.packRoundingThreshold")
  private static WebElement packRoundingThresholdInputField = null;

  @FindBy(how = ID, using = "productRoundToZeroLabel")
  private static WebElement productRoundToZeroLabel = null;

  @FindBy(how = ID, using = "roundToZeroTrue")
  private static WebElement roundToZeroTrue = null;

  @FindBy(how = ID, using = "productActiveLabel")
  private static WebElement productActiveLabel = null;

  @FindBy(how = ID, using = "activeTrue")
  private static WebElement activeTrue = null;

  @FindBy(how = ID, using = "productFullSupplyLabel")
  private static WebElement productFullSupplyLabel = null;

  @FindBy(how = ID, using = "fullSupplyTrue")
  private static WebElement fullSupplyTrue = null;

  @FindBy(how = ID, using = "productTracerLabel")
  private static WebElement productTracerLabel = null;

  @FindBy(how = ID, using = "tracerTrue")
  private static WebElement tracerTrue = null;

  @FindBy(how = ID, using = "productArchivedLabel")
  private static WebElement productArchivedLabel = null;

  @FindBy(how = ID, using = "archivedFalse")
  private static WebElement archivedFalse = null;

  @FindBy(how = ID, using = "saveButton")
  private static WebElement saveButton = null;

  @FindBy(how = ID, using = "cancelButton")
  private static WebElement cancelButton = null;

  @FindBy(how = ID, using = "saveErrorMsgDiv")
  private static WebElement saveErrorMsg = null;

  @FindBy(how = ID, using = "saveSuccessMsgDiv")
  private static WebElement saveSuccessMsg = null;

  @FindBy(how = ID, using = "viewHereLink")
  private static WebElement viewHereLink = null;

  public ProductPage(TestWebDriver driver) {
    super(driver);
    PageFactory.initElements(new AjaxElementLocatorFactory(TestWebDriver.getDriver(), 1), this);
    testWebDriver.setImplicitWait(1);
  }

  public String getSearchProductHeader() {
    testWebDriver.waitForElementToAppear(searchProductsHeader);
    return searchProductsHeader.getText();
  }

  public void clickSearchOptionButton() {
    testWebDriver.waitForElementToAppear(searchOptionButton);
    searchOptionButton.click();
  }

  public String getSelectSearchOption() {
    testWebDriver.waitForElementToAppear(selectedSearchOption);
    return selectedSearchOption.getText();
  }

  public void enterSearchProductParameter(String parameter) {
    testWebDriver.waitForElementToAppear(searchProgramProductParameter);
    sendKeys(searchProgramProductParameter, parameter);
  }

  public void clickSearchIcon() {
    testWebDriver.waitForElementToAppear(searchIcon);
    searchIcon.click();
  }

  public void clickCloseSearchResultsButton() {
    testWebDriver.waitForElementToAppear(closeSearchResultsButton);
    closeSearchResultsButton.click();
  }

  public boolean isCloseSearchResultsButtonDisplayed() {
    try {
      testWebDriver.waitForElementToAppear(closeSearchResultsButton);
    } catch (TimeoutException e) {
      return false;
    } catch (NoSuchElementException e) {
      return false;
    }
    return closeSearchResultsButton.isDisplayed();
  }

  public void selectSearchOptionProduct() {
    testWebDriver.waitForElementToAppear(searchOptionButton);
    searchOptionButton.click();
    testWebDriver.waitForElementToAppear(searchOptionProduct);
    searchOptionProduct.click();
  }

  public void selectSearchOptionProgram() {
    testWebDriver.waitForElementToAppear(searchOptionButton);
    searchOptionButton.click();
    testWebDriver.waitForElementToAppear(searchOptionProgram);
    searchOptionProgram.click();
  }

  public String getNResultsMessage() {
    testWebDriver.waitForElementToAppear(nResultsMessage);
    return nResultsMessage.getText();
  }

  public String getNoResultsMessage() {
    testWebDriver.waitForElementToAppear(noResultMessage);
    return noResultMessage.getText();
  }

  public String getOneResultsMessage() {
    testWebDriver.waitForElementToAppear(oneResultMessage);
    return oneResultMessage.getText();
  }

  public String getFullSupplyHeader() {
    testWebDriver.waitForElementToAppear(fullSupplyHeader);
    return fullSupplyHeader.getText();
  }

  public String getCodeHeader() {
    testWebDriver.waitForElementToAppear(codeHeader);
    return codeHeader.getText();
  }

  public boolean isCodeHeaderDisplayed() {
    try {
      testWebDriver.waitForElementToAppear(codeHeader);
    } catch (TimeoutException e) {
      return false;
    } catch (NoSuchElementException e) {
      return false;
    }
    return codeHeader.isDisplayed();
  }

  public String getNameHeader() {
    testWebDriver.waitForElementToAppear(nameHeader);
    return nameHeader.getText();
  }

  public String getProgramHeader() {
    testWebDriver.waitForElementToAppear(programHeader);
    return programHeader.getText();
  }

  public String getStrengthHeader() {
    testWebDriver.waitForElementToAppear(strengthHeader);
    return strengthHeader.getText();
  }

  public String getUnitOfMeasureHeader() {
    testWebDriver.waitForElementToAppear(unitOfMeasureHeader);
    return unitOfMeasureHeader.getText();
  }

  public String getDispensingUnitHeader() {
    testWebDriver.waitForElementToAppear(dispensingUnitHeader);
    return dispensingUnitHeader.getText();
  }

  public String getPackSizeHeader() {
    testWebDriver.waitForElementToAppear(packSizeHeader);
    return packSizeHeader.getText();
  }

  public String getGlobalActiveHeader() {
    testWebDriver.waitForElementToAppear(globalActiveHeader);
    return globalActiveHeader.getText();
  }

  public String getActiveAtProgramHeader() {
    testWebDriver.waitForElementToAppear(activeAtProgramHeader);
    return activeAtProgramHeader.getText();
  }

  public String getCategory(int rowNumber) {
    WebElement category = testWebDriver.getElementById("category" + (rowNumber - 1));
    return category.getText();
  }

  public boolean isFullSupplyDisplayed(int rowNumber) {
    WebElement fullSupply = testWebDriver.getElementById("fullSupply" + (rowNumber - 1));
    try {
      testWebDriver.waitForElementToAppear(fullSupply);
    } catch (TimeoutException e) {
      return false;
    } catch (NoSuchElementException e) {
      return false;
    }
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

  public String getProgram(int rowNumber) {
    WebElement program = testWebDriver.getElementById("program" + (rowNumber - 1));
    testWebDriver.waitForElementToAppear(program);
    return program.getText();
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

  public String getDispensingUnit(int rowNumber) {
    WebElement dispensingUnit = testWebDriver.getElementById("dispensingUnit" + (rowNumber - 1));
    testWebDriver.waitForElementToAppear(dispensingUnit);
    return dispensingUnit.getText();
  }

  public String getPackSize(int rowNumber) {
    WebElement packSize = testWebDriver.getElementById("packSize" + (rowNumber - 1));
    testWebDriver.waitForElementToAppear(packSize);
    return packSize.getText();
  }

  public boolean isGlobalActiveDisplayed(int rowNumber) {
    WebElement globalActive = testWebDriver.getElementById("globalActive" + (rowNumber - 1));
    try {
      testWebDriver.waitForElementToAppear(globalActive);
    } catch (TimeoutException e) {
      return false;
    } catch (NoSuchElementException e) {
      return false;
    }
    return globalActive.isDisplayed();
  }

  public boolean isActiveAtProgramDisplayed(int rowNumber) {
    WebElement activeAtProgram = testWebDriver.getElementById("activeAtProgram" + (rowNumber - 1));
    try {
      testWebDriver.waitForElementToAppear(activeAtProgram);
    } catch (TimeoutException e) {
      return false;
    } catch (NoSuchElementException e) {
      return false;
    }
    return activeAtProgram.isDisplayed();
  }

  public String getUndefinedActive(int rowNumber) {
    WebElement undefinedActive = testWebDriver.getElementById("undefinedActive" + (rowNumber - 1));
    testWebDriver.waitForElementToAppear(undefinedActive);
    return undefinedActive.getText();
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

  public boolean isResultDisplayed() {
    try {
      testWebDriver.waitForElementToAppear(productSearchResults);
    } catch (TimeoutException e) {
      return false;
    } catch (NoSuchElementException e) {
      return false;
    }
    return productSearchResults.isDisplayed();
  }

  public int getSizeOfResultsTable() {
    testWebDriver.waitForElementToAppear(testWebDriver.getElementByXpath("//*[@id='programProductTable']/tbody"));
    return testWebDriver.getElementsSizeByXpath("//*[@id='programProductTable']/tbody");
  }

  public String getAddNewProductHeader() {
    testWebDriver.waitForElementToAppear(addNewProductHeader);
    return addNewProductHeader.getText();
  }

  public String getEditProductHeader() {
    testWebDriver.waitForElementToAppear(editProductHeader);
    return editProductHeader.getText();
  }

  public String getBasicInformationLabel() {
    testWebDriver.waitForElementToAppear(basicInformationLabel);
    return basicInformationLabel.getText();
  }

  public String getProductCodeLabel() {
    testWebDriver.waitForElementToAppear(productCodeLabel);
    return productCodeLabel.getText();
  }

  public String getProductPrimaryNameLabel() {
    testWebDriver.waitForElementToAppear(productPrimaryNameLabel);
    return productPrimaryNameLabel.getText();
  }

  public String getProductTypeLabel() {
    testWebDriver.waitForElementToAppear(productTypeLabel);
    return productTypeLabel.getText();
  }

  public String getProductFullNameLabel() {
    testWebDriver.waitForElementToAppear(productFullNameLabel);
    return productFullNameLabel.getText();
  }

  public String getProductGroupLabel() {
    testWebDriver.waitForElementToAppear(productGroupLabel);
    return productGroupLabel.getText();
  }

  public String getProductDescriptionLabel() {
    testWebDriver.waitForElementToAppear(productDescriptionLabel);
    return productDescriptionLabel.getText();
  }

  public String getProductFormLabel() {
    testWebDriver.waitForElementToAppear(productFormLabel);
    return productFormLabel.getText();
  }

  public String getProductStrengthLabel() {
    testWebDriver.waitForElementToAppear(productStrengthLabel);
    return productStrengthLabel.getText();
  }

  public String getDosageUnitLabel() {
    testWebDriver.waitForElementToAppear(dosageUnitLabel);
    return dosageUnitLabel.getText();
  }

  public String getProductDispensingUnitLabel() {
    testWebDriver.waitForElementToAppear(productDispensingUnitLabel);
    return productDispensingUnitLabel.getText();
  }

  public String getProductDosesPerDispensingUnitLabel() {
    testWebDriver.waitForElementToAppear(productDosesPerDispensingUnitLabel);
    return productDosesPerDispensingUnitLabel.getText();
  }

  public String getProductPackSizeLabel() {
    testWebDriver.waitForElementToAppear(productPackSizeLabel);
    return productPackSizeLabel.getText();
  }

  public String getProductPackRoundingThresholdLabel() {
    testWebDriver.waitForElementToAppear(productPackRoundingThresholdLabel);
    return productPackRoundingThresholdLabel.getText();
  }

  public String getProductRoundToZeroLabel() {
    testWebDriver.waitForElementToAppear(productRoundToZeroLabel);
    return productRoundToZeroLabel.getText();
  }

  public String getProductActiveLabel() {
    testWebDriver.waitForElementToAppear(productActiveLabel);
    return productActiveLabel.getText();
  }

  public String getProductFullSupplyLabel() {
    testWebDriver.waitForElementToAppear(productFullSupplyLabel);
    return productFullSupplyLabel.getText();
  }

  public String getProductTracerLabel() {
    testWebDriver.waitForElementToAppear(productTracerLabel);
    return productTracerLabel.getText();
  }

  public String getProductArchivedLabel() {
    testWebDriver.waitForElementToAppear(productArchivedLabel);
    return productArchivedLabel.getText();
  }

  public void clickProductAddNewButton() {
    testWebDriver.waitForElementToAppear(productAddNewButton);
    productAddNewButton.click();
  }

  public void enterCodeInput(String code) {
    testWebDriver.waitForElementToAppear(codeInputField);
    sendKeys(codeInputField, code);
  }

  public void enterPrimaryNameInput(String name) {
    testWebDriver.waitForElementToAppear(primaryNameInputField);
    sendKeys(primaryNameInputField, name);
  }

  public void enterTypeInput(String type) {
    testWebDriver.waitForElementToAppear(typeInputField);
    sendKeys(typeInputField, type);
  }

  public void enterFullNameInput(String fullName) {
    testWebDriver.waitForElementToAppear(fullNameInputField);
    sendKeys(fullNameInputField, fullName);
  }

  public void selectProductGroup(String productGroup) {
    testWebDriver.waitForElementToAppear(productGroupDropDown);
    testWebDriver.selectByVisibleText(productGroupDropDown, productGroup);
  }

  public List<String> getAllProductGroups() {
    testWebDriver.waitForElementToAppear(productGroupDropDown);
    return testWebDriver.getListOfOptions(productGroupDropDown);
  }

  public void selectForm(String form) {
    testWebDriver.waitForElementToAppear(formDropDown);
    testWebDriver.selectByVisibleText(formDropDown, form);
  }

  public List<String> getAllForms() {
    testWebDriver.waitForElementToAppear(formDropDown);
    return testWebDriver.getListOfOptions(formDropDown);
  }

  public void enterDescriptionInput(String description) {
    testWebDriver.waitForElementToAppear(descriptionInputField);
    sendKeys(descriptionInputField, description);
  }

  public void enterStrengthInput(String strength) {
    testWebDriver.waitForElementToAppear(strengthInputField);
    sendKeys(strengthInputField, strength);
  }

  public void selectDosageUnit(String dosageUnit) {
    testWebDriver.waitForElementToAppear(dosageUnitDropDown);
    testWebDriver.selectByVisibleText(dosageUnitDropDown, dosageUnit);
  }

  public List<String> getAllDosageUnits() {
    testWebDriver.waitForElementToAppear(dosageUnitDropDown);
    return testWebDriver.getListOfOptions(dosageUnitDropDown);
  }

  public void enterDispensingUnitInput(String dispensingUnit) {
    testWebDriver.waitForElementToAppear(dispensingUnitInputField);
    sendKeys(dispensingUnitInputField, dispensingUnit);
  }

  public void enterDosesPerDispensingUnitInput(String dosesPerDispensingUnit) {
    testWebDriver.waitForElementToAppear(dosesPerDispensingUnitInputField);
    sendKeys(dosesPerDispensingUnitInputField, dosesPerDispensingUnit);
  }

  public void enterPackSizeInput(String packSize) {
    testWebDriver.waitForElementToAppear(packSizeInputField);
    sendKeys(packSizeInputField, packSize);
  }

  public void enterPackRoundingThresholdInput(String packRoundingThreshold) {
    testWebDriver.waitForElementToAppear(packRoundingThresholdInputField);
    sendKeys(packRoundingThresholdInputField, packRoundingThreshold);
  }

  public void clickRoundToZeroTrueButton() {
    testWebDriver.waitForElementToAppear(roundToZeroTrue);
    roundToZeroTrue.click();
  }

  public void clickActiveTrueButton() {
    testWebDriver.waitForElementToAppear(activeTrue);
    activeTrue.click();
  }

  public void clickFullSupplyTrueButton() {
    testWebDriver.waitForElementToAppear(fullSupplyTrue);
    fullSupplyTrue.click();
  }

  public void clickTracerTrueButton() {
    testWebDriver.waitForElementToAppear(tracerTrue);
    tracerTrue.click();
  }

  public void clickArchivedFalseButton() {
    testWebDriver.waitForElementToAppear(archivedFalse);
    archivedFalse.click();
  }

  public void clickSaveButton() {
    testWebDriver.waitForElementToAppear(saveButton);
    saveButton.click();
  }

  public void clickCancelButton() {
    testWebDriver.waitForElementToAppear(cancelButton);
    cancelButton.click();
  }

  public String getSaveErrorMsg() {
    testWebDriver.waitForElementToAppear(saveErrorMsg);
    return saveErrorMsg.getText();
  }

  public String getSaveSuccessMsg() {
    testWebDriver.waitForElementToAppear(saveSuccessMsg);
    return saveSuccessMsg.getText();
  }

  public void clickViewHere() {
    testWebDriver.waitForElementToAppear(viewHereLink);
    viewHereLink.click();
  }

  public String getPrimaryNameOnEditPage() {
    testWebDriver.waitForElementToAppear(primaryNameInputField);
    return primaryNameInputField.getAttribute("value");
  }

  public void clickName(int rowNumber) {
    WebElement name = testWebDriver.getElementById("name" + (rowNumber - 1));
    testWebDriver.waitForElementToAppear(name);
    name.click();
  }
}
