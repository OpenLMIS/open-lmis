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


import com.thoughtworks.selenium.SeleneseTestNgHelper;
import org.openlmis.UiUtils.TestWebDriver;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.pagefactory.AjaxElementLocatorFactory;

import java.io.IOException;
import java.util.List;

import static com.thoughtworks.selenium.SeleneseTestBase.assertEquals;
import static com.thoughtworks.selenium.SeleneseTestBase.assertTrue;
import static org.openqa.selenium.support.How.ID;
import static org.openqa.selenium.support.How.XPATH;


public class ProgramProductISAPage extends Page {

  @FindBy(how = ID, using = "program")
  private static WebElement selectProgramSelectBox=null;

  @FindBy(how = XPATH, using = "//span[contains(text(),'Product name')]")
  private static WebElement productNameColumn=null;

  @FindBy(how = ID, using = "searchProduct")
  private static WebElement searchProductTextBox=null;

  @FindBy(how = XPATH, using = "//div[@id='wrap']/div[@class='content']/div/div[@class='ng-scope']/div[2]/div[4][@class='list-container']/div[1][@class='row-fluid list-row ng-scope']/div[3][@class='span2 offset2']/input[@class='btn btn-small btn-primary']")
  private static WebElement editFormulaButton=null;

  @FindBy(how = ID, using = "who-ratio")
  private static WebElement ratioTextBox=null;

  @FindBy(how = ID, using = "doses-per-year")
  private static WebElement dosesPerYearTextBox=null;

  @FindBy(how = ID, using = "wastage-factor")
  private static WebElement wastageRateTextBox=null;

  @FindBy(how = ID, using = "buffer-percentage")
  private static WebElement bufferPercentageTextBox=null;

  @FindBy(how = ID, using = "adjustment-value")
  private static WebElement adjustmentValueTextBox=null;

  @FindBy(how = ID, using = "minimum-value")
  private static WebElement minimumValueTextBox=null;

  @FindBy(how = ID, using = "maximum-value")
  private static WebElement maximumValueTextBox=null;

  @FindBy(how = XPATH, using = "//div[@id='ISA-population']/input")
  private static WebElement ISAPopulationTextField=null;

  @FindBy(how = XPATH, using = "//div[@id='ISA-population']/span[@ng-bind='isaValue']")
  private static WebElement isaValueLabel=null;

  @FindBy(how = XPATH, using = "//div[@id='ISA-population' and @class='calculatedAmount']/input[@class='ng-pristine ng-valid']")
  private static WebElement populationTextBox=null;

  @FindBy(how = XPATH, using = "//div[@id='programProductISA' and @class='modal in']/div[3][@class='modal-footer']/input[1][@class='btn btn-primary save-button']")
  private static WebElement programProductISASaveButton=null;

  @FindBy(how = XPATH, using = "//input[@value='Cancel']")
  private static WebElement programProductISACancelButton=null;

  @FindBy(how = ID, using = "calculateButton")
  private static WebElement testCalculationButton=null;

  @FindBy(how = ID, using = "saveFailMessage")
  private static WebElement saveFailMessage=null;

  @FindBy(how = ID, using = "searchProduct")
  private static WebElement searchProduct=null;

  @FindBy(how = XPATH, using = "(//div[@id='programProductFormula'])[1]")
  private static WebElement ISAFormulaFromConfigureProgramISAModalWindow=null;

  @FindBy(how = XPATH, using = "//div[@id='monthlyRestockAmount']/span[@class='ng-binding']")
  private static WebElement ISAFormulaFromISAFormulaModal=null;

  @FindBy(how = XPATH, using = "//span[contains(text(),'Monthly restock amount')]")
  private static WebElement monthlyRestockAmountLabel=null;

  @FindBy(how = ID, using = "productPrimaryName")
  private static WebElement productPrimaryName=null;

  @FindBy(how = XPATH, using = "//div[@class='modal-header']")
  private static WebElement programNameOnModalHeaderOFConfigureISAFormulaWindow =null;

  @FindBy(how = XPATH, using = "//div[@id='ISA-population']")
  private static WebElement programNameOnPopulationLabelOFConfigureISAFormulaWindow =null;



  public ProgramProductISAPage(TestWebDriver driver) throws IOException {
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

  public void verifyMandatoryFieldsToBeFilled() {
    assertTrue(testWebDriver.getAttribute(ratioTextBox,"ng-required").equals("true"));
    assertTrue(testWebDriver.getAttribute(dosesPerYearTextBox,"ng-required").equals("true"));
    assertTrue(testWebDriver.getAttribute(wastageRateTextBox,"ng-required").equals("true"));
    assertTrue(testWebDriver.getAttribute(bufferPercentageTextBox,"ng-required").equals("true"));
    assertTrue(testWebDriver.getAttribute(adjustmentValueTextBox, "ng-required").equals("true"));
  }

  public void verifyFieldsOnISAModalWindow() {
    assertTrue("ratioTextBox should be displayed",ratioTextBox.isDisplayed());
    assertTrue("dosesPerYearTextBox should be displayed",dosesPerYearTextBox.isDisplayed());
    assertTrue("wastageRateTextBox should be displayed", wastageRateTextBox.isDisplayed());
    assertTrue("bufferPercentageTextBox should be displayed", bufferPercentageTextBox.isDisplayed());
    assertTrue("adjustmentValueTextBox should be displayed", adjustmentValueTextBox.isDisplayed());
//    assertTrue("minimumValueTextBox should be displayed", minimumValueTextBox.isDisplayed());
    assertTrue("ISAPopulationTextField should be displayed", ISAPopulationTextField.isDisplayed());
    assertTrue("programProductISASaveButton should be displayed", programProductISASaveButton.isDisplayed());
    assertTrue("programProductISACancelButton should be displayed", programProductISACancelButton.isDisplayed());
  }

  public void verifyErrorMessageDiv() {
    assertTrue("saveFailMessage should be present",saveFailMessage.isDisplayed());
  }


  public String calculateISA(String population) {
    populationTextBox.clear();
    populationTextBox.sendKeys(population);
    testWebDriver.sleep(100);
    testCalculationButton.click();
    testWebDriver.sleep(100);
    return testWebDriver.getText(isaValueLabel);
  }

  public void verifyISAFormula(String ISAFormula) {
    testWebDriver.sleep(500);
    testWebDriver.waitForElementToAppear(ISAFormulaFromConfigureProgramISAModalWindow);
    String formulaConfigureProgramWindow = ISAFormulaFromConfigureProgramISAModalWindow.getText();
    assertEquals(ISAFormula, formulaConfigureProgramWindow);
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
    String productsLocator="//div[@id='productPrimaryName']";
    String productsListValue="";
    testWebDriver.waitForElementToAppear(productNameColumn);
    List<WebElement> productsList=testWebDriver.getElementsByXpath(productsLocator);
    for(WebElement product: productsList)
    {
      productsListValue=productsListValue+product.getText();
    }
    return productsListValue;
  }

  public void verifyMonthlyRestockAmountPresent() {
    testWebDriver.waitForElementToAppear(ratioTextBox);
    SeleneseTestNgHelper.assertTrue("monthlyRestockAmountLabel should be present", monthlyRestockAmountLabel.isDisplayed());
  }

  public void selectProgram(String program) {
    testWebDriver.waitForElementToAppear(selectProgramSelectBox);
    testWebDriver.selectByVisibleText(selectProgramSelectBox, program);
  }

  public void editFormula() {
    editFormulaButton.click();
  }

  public List<WebElement> getAllSelectOptionsFromProgramDropDown() {
    testWebDriver.waitForElementToAppear(selectProgramSelectBox);
    return testWebDriver.getOptions(selectProgramSelectBox);
  }

  public void saveISA() {
    programProductISASaveButton.click();
  }

  public void cancelISA() {
    programProductISACancelButton.click();
  }

  public void searchProduct(String product) {
    testWebDriver.waitForElementToAppear(searchProduct);
    searchProductTextBox.sendKeys(product);
  }

  public void verifySearchResults(String product) {
    testWebDriver.waitForElementToAppear(productPrimaryName);
    assertTrue("Product "+product+" should be displayed as a search Result",productPrimaryName.isDisplayed());
  }

  public String fillProgramProductISA(String program, String ratio, String dosesPerYear, String wastage, String bufferPercentage, String adjustmentValue, String minimumValue, String maximumValue, String population) {
    selectProgram(program);
    editFormula();
    fillProgramProductISA(ratio, dosesPerYear, wastage, bufferPercentage, adjustmentValue, minimumValue, maximumValue);
    String expectedISAValue = String.valueOf(Math.round(Integer.parseInt(calculateISA(population)) / 10));
    saveISA();
    return expectedISAValue;
  }

  public String getProgramNameDisplayedOnModalHeaderOFConfigureISAFormulaWindow(){
    return programNameOnModalHeaderOFConfigureISAFormulaWindow.getText();
  }

  public String getProgramNameDisplayedOnPopulationLabelOFConfigureISAFormulaWindow(){
    return programNameOnPopulationLabelOFConfigureISAFormulaWindow.getText();
  }
}