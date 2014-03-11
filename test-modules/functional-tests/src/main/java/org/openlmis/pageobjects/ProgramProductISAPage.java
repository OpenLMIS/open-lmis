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
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.pagefactory.AjaxElementLocatorFactory;

import java.util.List;

import static org.openqa.selenium.support.How.ID;


public class ProgramProductISAPage extends Page {

  @FindBy(how = ID, using = "program")
  private static WebElement selectProgramSelectBox = null;

  @FindBy(how = ID, using = "productNameLabel")
  private static WebElement productNameColumn = null;

  @FindBy(how = ID, using = "searchProduct")
  private static WebElement searchProductTextBox = null;

  @FindBy(how = ID, using = "editButton0")
  private static WebElement editFormulaButton = null;

  @FindBy(how = ID, using = "who-ratio")
  private static WebElement ratioTextBox = null;

  @FindBy(how = ID, using = "doses-per-year")
  private static WebElement dosesPerYearTextBox = null;

  @FindBy(how = ID, using = "wastage-factor")
  private static WebElement wastageRateTextBox = null;

  @FindBy(how = ID, using = "buffer-percentage")
  private static WebElement bufferPercentageTextBox = null;

  @FindBy(how = ID, using = "adjustment-value")
  private static WebElement adjustmentValueTextBox = null;

  @FindBy(how = ID, using = "minimum-value")
  private static WebElement minimumValueTextBox = null;

  @FindBy(how = ID, using = "maximum-value")
  private static WebElement maximumValueTextBox = null;

  @FindBy(how = ID, using = "catchmentPopulation")
  private static WebElement ISAPopulationTextField = null;

  @FindBy(how = ID, using = "calculatedIsaValue")
  private static WebElement isaValueCalculated = null;

  @FindBy(how = ID, using = "catchmentPopulation")
  private static WebElement populationTextBox = null;

  @FindBy(how = ID, using = "saveIsa")
  private static WebElement programProductISASaveButton = null;

  @FindBy(how = ID, using = "cancelIsa")
  private static WebElement programProductISACancelButton = null;

  @FindBy(how = ID, using = "calculateButton")
  private static WebElement testCalculationButton = null;

  @FindBy(how = ID, using = "saveFailMessage")
  private static WebElement saveFailMessage = null;

  @FindBy(how = ID, using = "searchProduct")
  private static WebElement searchProduct = null;

  @FindBy(how = ID, using = "programProductFormula")
  private static WebElement ISAFormulaFromConfigureProgramISAModalWindow = null;

  @FindBy(how = ID, using = "monthlyRestockFormula")
  private static WebElement ISAFormulaFromISAFormulaModal = null;

  @FindBy(how = ID, using = "monthlyRestockLabel")
  private static WebElement monthlyRestockAmountLabel = null;

  @FindBy(how = ID, using = "productPrimaryName")
  private static WebElement productPrimaryName = null;

  @FindBy(how = ID, using = "isaModalHeader")
  private static WebElement programNameOnModalHeaderOFConfigureISAFormulaWindow = null;

  @FindBy(how = ID, using = "ISA-population")
  private static WebElement programNameOnPopulationLabelOFConfigureISAFormulaWindow = null;

  public ProgramProductISAPage(TestWebDriver driver) {
    super(driver);
    PageFactory.initElements(new AjaxElementLocatorFactory(TestWebDriver.getDriver(), 10), this);
    testWebDriver.setImplicitWait(10);
  }

  public void fillProgramProductISA(String ratio, String dosesPerYear, String wastage, String bufferPercentage,
                                    String adjustmentValue, String minimumValue, String maximumValue) {
    testWebDriver.waitForElementToAppear(ratioTextBox);
    enterValueInRatioTextField(ratio);
    enterValueInDosesTextField(dosesPerYear);
    enterValueInWastageTextField(wastage);
    enterValueInBufferPercentageTextField(bufferPercentage);
    enterValueInAdjustmentTextField(adjustmentValue);
    enterValueInMinimumTextField(minimumValue);
    enterValueInMaximumTextField(maximumValue);
  }

  public void enterValueInMinimumTextField(String minimumValue) {
    testWebDriver.waitForElementToAppear(minimumValueTextBox);
    minimumValueTextBox.sendKeys(minimumValue);
  }

  public void enterValueInMaximumTextField(String minimumValue) {
    testWebDriver.waitForElementToAppear(maximumValueTextBox);
    maximumValueTextBox.sendKeys(minimumValue);
  }

  public void enterValueInAdjustmentTextField(String adjustmentValue) {
    testWebDriver.waitForElementToAppear(adjustmentValueTextBox);
    adjustmentValueTextBox.clear();
    adjustmentValueTextBox.sendKeys(adjustmentValue);
  }

  public void enterValueInBufferPercentageTextField(String bufferPercentage) {
    testWebDriver.waitForElementToAppear(bufferPercentageTextBox);
    bufferPercentageTextBox.clear();
    bufferPercentageTextBox.sendKeys(bufferPercentage);
  }

  public void enterValueInWastageTextField(String wastage) {
    testWebDriver.waitForElementToAppear(wastageRateTextBox);
    wastageRateTextBox.clear();
    wastageRateTextBox.sendKeys(wastage);
  }

  public void enterValueInDosesTextField(String dosesPerYear) {
    testWebDriver.waitForElementToAppear(dosesPerYearTextBox);
    dosesPerYearTextBox.clear();
    dosesPerYearTextBox.sendKeys(dosesPerYear);
  }

  public void enterValueInRatioTextField(String ratio) {
    testWebDriver.waitForElementToAppear(ratioTextBox);
    ratioTextBox.sendKeys(Keys.ARROW_RIGHT);
    ratioTextBox.sendKeys(Keys.BACK_SPACE);
    ratioTextBox.sendKeys(ratio);
  }

  public boolean verifyErrorMessageDiv() {
    return saveFailMessage.isDisplayed();
  }

  public String calculateISA(String population) {
    populationTextBox.clear();
    populationTextBox.sendKeys(population);
    testWebDriver.sleep(100);
    testCalculationButton.click();
    testWebDriver.sleep(100);
    return testWebDriver.getText(isaValueCalculated);
  }

  public String verifyISAFormula() {
    testWebDriver.sleep(500);
    testWebDriver.waitForElementToAppear(ISAFormulaFromConfigureProgramISAModalWindow);
    return ISAFormulaFromConfigureProgramISAModalWindow.getText();
  }

  public String getISAFormulaFromISAFormulaModal() {
    testWebDriver.waitForElementToAppear(ISAFormulaFromISAFormulaModal);
    return ISAFormulaFromISAFormulaModal.getText();
  }

  public void selectValueFromProgramDropDown(String valueToBeSelected) {
    testWebDriver.waitForElementToAppear(selectProgramSelectBox);
    testWebDriver.selectByVisibleText(selectProgramSelectBox, valueToBeSelected);
  }

  public String getProductsDisplayingOnConfigureProgramISAPage() {
    String productsLocator = "//div[@id='productPrimaryName']";
    String productsListValue = "";
    testWebDriver.waitForElementToAppear(productNameColumn);
    List<WebElement> productsList = testWebDriver.getElementsByXpath(productsLocator);
    for (WebElement product : productsList) {
      productsListValue = productsListValue + product.getText();
    }
    return productsListValue;
  }

  public boolean verifyMonthlyRestockAmountPresent() {
    testWebDriver.waitForElementToAppear(ratioTextBox);
    return monthlyRestockAmountLabel.isDisplayed();
  }

  public void selectProgram(String program) {
    testWebDriver.waitForElementToAppear(selectProgramSelectBox);
    testWebDriver.selectByVisibleText(selectProgramSelectBox, program);
  }

  public void editFormula() {
    testWebDriver.waitForElementToAppear(editFormulaButton);
    editFormulaButton.click();
  }

  public List<WebElement> getAllSelectOptionsFromProgramDropDown() {
    testWebDriver.waitForElementToAppear(selectProgramSelectBox);
    return testWebDriver.getOptions(selectProgramSelectBox);
  }

  public void saveISA() {
    testWebDriver.waitForElementToAppear(programProductISASaveButton);
    programProductISASaveButton.click();
  }

  public void cancelISA() {
    programProductISACancelButton.click();
  }

  public void searchProduct(String product) {
    testWebDriver.waitForElementToAppear(searchProduct);
    searchProductTextBox.sendKeys(product);
  }

  public boolean verifySearchResults() {
    testWebDriver.waitForElementToAppear(productPrimaryName);
    return productPrimaryName.isDisplayed();
  }

  public String fillProgramProductISA(String program, String ratio, String dosesPerYear, String wastage, String bufferPercentage, String adjustmentValue, String minimumValue, String maximumValue, String population) {
    selectProgram(program);
    editFormula();
    fillProgramProductISA(ratio, dosesPerYear, wastage, bufferPercentage, adjustmentValue, minimumValue, maximumValue);
    String expectedISAValue = String.valueOf(Math.round(Integer.parseInt(calculateISA(population)) / 10));
    saveISA();
    return expectedISAValue;
  }

  public String getProgramNameDisplayedOnModalHeaderOFConfigureISAFormulaWindow() {
    return programNameOnModalHeaderOFConfigureISAFormulaWindow.getText();
  }

  public String getProgramNameDisplayedOnPopulationLabelOFConfigureISAFormulaWindow() {
    return programNameOnPopulationLabelOFConfigureISAFormulaWindow.getText();
  }

  public boolean verifyDosesPerYearTextBoxFieldOnISAModalWindowIsDisplayed() {
    return dosesPerYearTextBox.isDisplayed();
  }

  public boolean verifyWastageRateTextBoxFieldOnISAModalWindowIsDisplayed() {
    return wastageRateTextBox.isDisplayed();
  }

  public boolean verifyBufferPercentageTextBoxFieldOnISAModalWindowIsDisplayed() {
    return bufferPercentageTextBox.isDisplayed();
  }

  public boolean verifyRatioTextBoxFieldOnISAModalWindowIsDisplayed() {
    return ratioTextBox.isDisplayed();
  }

  public boolean verifyISAPopulationTextFieldOnISAModalWindowIsDisplayed() {
    return ISAPopulationTextField.isDisplayed();
  }

  public boolean verifyAdjustmentValueTextBoxFieldOnISAModalWindowIsDisplayed() {
    return adjustmentValueTextBox.isDisplayed();
  }

  public boolean verifyProgramProductISASaveButtonFieldOnISAModalWindowIsDisplayed() {
    return programProductISASaveButton.isDisplayed();
  }

  public boolean verifyProgramProductISACancelButtonFieldOnISAModalWindowIsDisplayed() {
    return programProductISACancelButton.isDisplayed();
  }
}
