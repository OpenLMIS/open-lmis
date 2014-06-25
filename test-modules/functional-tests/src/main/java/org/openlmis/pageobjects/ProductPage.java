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

import static org.openqa.selenium.support.How.ID;

public class ProductPage extends Page {

  @FindBy(how = ID, using = "productsHeader")
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

  @FindBy(how = ID, using = "noResultsMessage")
  private static WebElement noResultsMessage = null;

  @FindBy(how = ID, using = "oneResultMessage")
  private static WebElement oneResultMessage = null;

  @FindBy(how = ID, using = "closeButton")
  private static WebElement closeButton = null;

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

  public String getSearchProductParameter() {
    testWebDriver.waitForElementToAppear(searchProgramProductParameter);
    return searchProgramProductParameter.getAttribute("value");
  }

  public void clickSearchIcon() {
    testWebDriver.waitForElementToAppear(searchIcon);
    searchIcon.click();
  }

  public void clickCloseButton() {
    testWebDriver.waitForElementToAppear(closeButton);
    closeButton.click();
  }

  public String getSearchOptionProduct() {
    testWebDriver.waitForElementToAppear(searchOptionButton);
    searchOptionButton.click();
    testWebDriver.waitForElementToAppear(searchOptionProduct);
    return searchOptionProduct.getText();
  }

  public void selectSearchOptionProduct() {
    testWebDriver.waitForElementToAppear(searchOptionButton);
    searchOptionButton.click();
    testWebDriver.waitForElementToAppear(searchOptionProduct);
    searchOptionProduct.click();
  }

  public String getSearchOptionProgram() {
    testWebDriver.waitForElementToAppear(searchOptionButton);
    searchOptionButton.click();
    testWebDriver.waitForElementToAppear(searchOptionProgram);
    return searchOptionProgram.getText();
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
    testWebDriver.waitForElementToAppear(noResultsMessage);
    return noResultsMessage.getText();
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
    testWebDriver.waitForElementToAppear(category);
    return category.getText();
  }

  public boolean isFullSupplyDisplayed(int rowNumber) {
    WebElement fullSupply = testWebDriver.getElementById("fullSupply" + (rowNumber - 1));
    testWebDriver.waitForElementToAppear(fullSupply);
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

  public String getUnit(int rowNumber) {
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
    WebElement globalActive = testWebDriver.getElementById("GlobalActive" + (rowNumber - 1));
    testWebDriver.waitForElementToAppear(globalActive);
    return globalActive.isDisplayed();
  }

  public boolean isActiveAtProgramDisplayed(int rowNumber) {
    WebElement activeAtProgram = testWebDriver.getElementById("activeAtProgram" + (rowNumber - 1));
    testWebDriver.waitForElementToAppear(activeAtProgram);
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
}
