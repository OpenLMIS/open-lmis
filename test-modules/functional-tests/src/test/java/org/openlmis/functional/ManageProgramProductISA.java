/*
 * This program is part of the OpenLMIS logistics management information system platform software.
 * Copyright © 2013 VillageReach
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *  
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 * You should have received a copy of the GNU Affero General Public License along with this program.  If not, see http://www.gnu.org/licenses.  For additional information contact info@OpenLMIS.org. 
 */

package org.openlmis.functional;

import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import org.openlmis.UiUtils.CaptureScreenshotOnFailureListener;
import org.openlmis.UiUtils.TestCaseHelper;
import org.openlmis.pageobjects.HomePage;
import org.openlmis.pageobjects.LoginPage;
import org.openlmis.pageobjects.PageObjectFactory;
import org.openlmis.pageobjects.ProgramProductISAPage;
import org.openqa.selenium.WebElement;
import org.testng.annotations.*;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import static com.thoughtworks.selenium.SeleneseTestBase.*;

@Listeners(CaptureScreenshotOnFailureListener.class)

public class ManageProgramProductISA extends TestCaseHelper {

  public String program, userSIC, password;
  public ProgramProductISAPage programProductISAPage;

  HomePage homePage;
  LoginPage loginPage;

  @BeforeMethod(groups = "admin")
  public void setUp() throws InterruptedException, SQLException, IOException {
    super.setup();
    dbWrapper.removeAllExistingRights("Admin");
    dbWrapper.assignRight("Admin", "MANAGE_PROGRAM_PRODUCT");
    homePage = PageObjectFactory.getHomePage(testWebDriver);
    loginPage = PageObjectFactory.getLoginPage(testWebDriver, baseUrlGlobal);
  }

  @When("^I have data available for program product ISA$")
  public void setUpTestDataForProgramProductISA() throws SQLException {
    setupProgramProductTestDataWithCategories("C1", "Category 1", "P1", "antibiotic1", "VACCINES");
    setupProgramProductTestDataWithCategories("C2", "Category 2", "P2", "antibiotic2", "VACCINES");
    setupProgramProductTestDataWithCategories("C3", "Category 3", "P3", "antibiotic3", "TB");
    setupProgramProductTestDataWithCategories("C4", "Category 4", "P4", "antibiotic4", "TB");
    dbWrapper.updateProgramToAPushType("TB", false);
  }

  @Given("^I access program product ISA page for \"([^\"]*)\"$")
  public void accessProgramProductISAPage(String program) {
    programProductISAPage = PageObjectFactory.getProgramProductIsaPage(testWebDriver);
    programProductISAPage = navigateProgramProductISAPage(program);
  }

  @When(
    "^I type ratio \"([^\"]*)\" dosesPerYear \"([^\"]*)\" wastage \"([^\"]*)\" bufferPercentage \"([^\"]*)\" adjustmentValue \"([^\"]*)\" minimumValue \"([^\"]*)\" maximumValue \"([^\"]*)\"$")
  public void fillProgramProductISA(String ratio,
                                    String dosesPerYear,
                                    String wastage,
                                    String bufferPercentage,
                                    String adjustmentValue,
                                    String minimumValue,
                                    String maximumValue) {
    programProductISAPage = PageObjectFactory.getProgramProductIsaPage(testWebDriver);
    programProductISAPage.fillProgramProductISA(ratio, dosesPerYear, wastage, bufferPercentage, adjustmentValue,
      minimumValue, maximumValue);
  }

  @Then("^I verify calculated ISA value having population \"([^\"]*)\" as \"([^\"]*)\"$")
  public void verifyTestCalculatedISA(String population, String expectedISA) throws SQLException {
    programProductISAPage = PageObjectFactory.getProgramProductIsaPage(testWebDriver);
    String actualISA = programProductISAPage.calculateISA(population);
    assertEquals(expectedISA, actualISA);
  }

  @Then("^I click cancel$")
  public void clickCancel() {
    programProductISAPage = PageObjectFactory.getProgramProductIsaPage(testWebDriver);
    programProductISAPage.cancelISA();
  }

  @Test(groups = {"admin"}, dataProvider = "Data-Provider-Function")
  public void testMinimumProgramProductISA(String userSIC, String password, String program) throws SQLException {
    setUpTestDataForProgramProductISA();
    Login(userSIC, password);
    ProgramProductISAPage programProductISAPage = navigateProgramProductISAPage(program);
    programProductISAPage.fillProgramProductISA("1", "2", "3", "4", "5", "10", "1000");
    String actualISA = programProductISAPage.calculateISA("1");
    String expectedISA = String.valueOf(calculateISA("1", "2", "3", "4", "5", "10", "1000", "1"));
    assertEquals(expectedISA, actualISA);
    programProductISAPage.cancelISA();
    homePage.navigateHomePage();
  }

  @Test(groups = {"admin"}, dataProvider = "Data-Provider-Function")
  public void testMaximumProgramProductISA(String userSIC, String password, String program) throws SQLException {
    setUpTestDataForProgramProductISA();
    Login(userSIC, password);
    ProgramProductISAPage programProductISAPage = navigateProgramProductISAPage(program);
    programProductISAPage.fillProgramProductISA("1", "2", "3", "4", "55", "10", "50");
    String actualISA = programProductISAPage.calculateISA("1");
    String expectedISA = String.valueOf(calculateISA("1", "2", "3", "4", "55", "10", "50", "1"));
    assertEquals(expectedISA, actualISA);
    programProductISAPage.cancelISA();
    homePage.navigateHomePage();
  }

  @Test(groups = {"admin"}, dataProvider = "Data-Provider-Function")
  public void testProgramProductISA(String userSIC, String password, String program) throws SQLException {
    setUpTestDataForProgramProductISA();
    Login(userSIC, password);
    ProgramProductISAPage programProductISAPage = navigateProgramProductISAPage(program);
    programProductISAPage.fillProgramProductISA("1", "2", "3", "4", "5", "5", "1000");
    String actualISA = programProductISAPage.calculateISA("1");
    String expectedISA = String.valueOf(calculateISA("1", "2", "3", "4", "5", "5", "1000", "1"));
    assertEquals(expectedISA, actualISA);
    programProductISAPage.cancelISA();
    homePage.navigateHomePage();
  }

  @Test(groups = {"admin"}, dataProvider = "Data-Provider-Function")
  public void testISAFormula(String userSIC, String password, String program) throws SQLException {
    setUpTestDataForProgramProductISA();
    Login(userSIC, password);
    ProgramProductISAPage programProductISAPage = navigateProgramProductISAPage(program);
    programProductISAPage.fillProgramProductISA("999.999", "999", "999.999", "999.999", "999999", "5", "1000");
    programProductISAPage.calculateISA("1");
    String isaFormula = programProductISAPage.getISAFormulaFromISAFormulaModal();
    String expectedISAFormula = "(population) * 9.99999 * 999 * 999.999 / 12 * 10.99999 + 999999";
    assertEquals(expectedISAFormula, isaFormula);
    verifyProgramNameIsDisplayedOnConfigureISAFormulaWindow(programProductISAPage);
    programProductISAPage.saveISA();
    verifyISAFormula(programProductISAPage, isaFormula);
    homePage.navigateHomePage();
  }

  @Test(groups = {"admin"}, dataProvider = "Data-Provider-Function-Search")
  public void testSearchBox(String userSIC, String password, String program, String productName) throws SQLException {
    setUpTestDataForProgramProductISA();
    Login(userSIC, password);
    ProgramProductISAPage programProductISAPage = navigateConfigureProductISAPage();
    programProductISAPage.selectProgram(program);
    programProductISAPage.searchProduct(productName);
    verifySearchResults(programProductISAPage);
    homePage.navigateHomePage();
  }

  @Test(groups = {"admin"}, dataProvider = "Data-Provider-Function-Verify-Push-Type-Program")
  public void testPushTypeProgramsInDropDown(String userSIC, String password, String program1, String program2) throws SQLException {
    setUpTestDataForProgramProductISA();
    Login(userSIC, password);
    ProgramProductISAPage programProductISAPage = navigateConfigureProductISAPage();

    List<WebElement> valuesPresentInDropDown = programProductISAPage.getAllSelectOptionsFromProgramDropDown();
    List<String> programValuesToBeVerified = new ArrayList<>();
    programValuesToBeVerified.add(program1);
    verifyAllSelectFieldValues(programValuesToBeVerified, valuesPresentInDropDown);

    dbWrapper.updateProgramToAPushType(program2, true);

    homePage.navigateHomePage();
    homePage.navigateProgramProductISA();
    valuesPresentInDropDown = programProductISAPage.getAllSelectOptionsFromProgramDropDown();
    List<String> programValuesToBeVerifiedAfterUpdate = new ArrayList<>();
    programValuesToBeVerifiedAfterUpdate.add(program1);
    programValuesToBeVerifiedAfterUpdate.add(program2);
    verifyAllSelectFieldValues(programValuesToBeVerifiedAfterUpdate, valuesPresentInDropDown);
  }

  @Test(groups = {"admin"}, dataProvider = "Data-Provider-Function-Multiple-Programs")
  public void testProgramProductsMappings(String userSIC, String password, String program1, String program2, String product1,
                                          String product2, String product3, String product4) throws SQLException {
    setUpTestDataForProgramProductISA();
    Login(userSIC, password);
    ProgramProductISAPage programProductISAPage = navigateConfigureProductISAPage();

    programProductISAPage.selectValueFromProgramDropDown(program1);
    String productsList = programProductISAPage.getProductsDisplayingOnConfigureProgramISAPage();
    assertTrue("Product " + product1 + " should be displayed", productsList.contains(product1));
    assertTrue("Product " + product2 + " should be displayed", productsList.contains(product2));

    dbWrapper.updateProgramToAPushType(program2, true);

    homePage.navigateHomePage();
    homePage.navigateProgramProductISA();
    programProductISAPage.selectValueFromProgramDropDown(program2);
    productsList = programProductISAPage.getProductsDisplayingOnConfigureProgramISAPage();
    assertTrue("Product " + product3 + " should be displayed", productsList.contains(product3));
    assertTrue("Product " + product4 + " should be displayed", productsList.contains(product4));
  }

  @Test(groups = {"admin"}, dataProvider = "Data-Provider-Function")
  public void testVerifyMandatoryFields(String userSIC, String password, String program) throws SQLException {
    setUpTestDataForProgramProductISA();
    Login(userSIC, password);
    ProgramProductISAPage programProductISAPage = navigateProgramProductISAPage(program);
    programProductISAPage.fillProgramProductISA("", "1", "2", "3", "4", "10", "10");
    verifyFieldsOnISAModalWindow(programProductISAPage);
    programProductISAPage.saveISA();
    verifyErrorMessageDiv(programProductISAPage);
  }

  @Test(groups = {"admin"}, dataProvider = "Data-Provider-Function")
  public void testVerifyMonthlyRestockAmountFieldAvailability(String userSIC, String password, String program) throws SQLException {
    setUpTestDataForProgramProductISA();
    Login(userSIC, password);
    ProgramProductISAPage programProductISAPage = navigateProgramProductISAPage(program);
    programProductISAPage.fillProgramProductISA("1", "2", "3", "4", "0", "10", "10");
    verifyMonthlyRestockAmountPresent(programProductISAPage);
    programProductISAPage.cancelISA();
    homePage.navigateHomePage();
  }

  private ProgramProductISAPage navigateProgramProductISAPage(String program) {
    homePage = PageObjectFactory.getHomePage(testWebDriver);
    ProgramProductISAPage programProductISAPage = homePage.navigateProgramProductISA();
    programProductISAPage.selectProgram(program);
    programProductISAPage.editFormula();
    return programProductISAPage;
  }

  private ProgramProductISAPage navigateConfigureProductISAPage() {
    programProductISAPage = homePage.navigateProgramProductISA();
    return programProductISAPage;
  }

  private HomePage Login(String userSIC, String password) {
    return loginPage.loginAs(userSIC, password);
  }

  private void verifyAllSelectFieldValues(List<String> valuesToBeVerified, List<WebElement> valuesPresentInDropDown) {
    String collectionOfValuesPresentINDropDown = "";
    int valuesToBeVerifiedCounter = valuesToBeVerified.size();
    int valuesInSelectFieldCounter = valuesPresentInDropDown.size();

    if (valuesToBeVerifiedCounter == valuesInSelectFieldCounter - 1) {
      for (WebElement webElement : valuesPresentInDropDown) {
        collectionOfValuesPresentINDropDown = collectionOfValuesPresentINDropDown + webElement.getText().trim();
      }
      for (String values : valuesToBeVerified) {
        assertTrue(collectionOfValuesPresentINDropDown.contains(values));
      }
    } else {
      fail("Values in select field are not same in number as values to be verified");
    }
  }

  public void verifyProgramNameIsDisplayedOnConfigureISAFormulaWindow(ProgramProductISAPage programProductISAPage) {
    assertTrue(programProductISAPage.getProgramNameDisplayedOnModalHeaderOFConfigureISAFormulaWindow().contains(
      "ISA formula for antibiotic1"));
    assertTrue(programProductISAPage.getProgramNameDisplayedOnPopulationLabelOFConfigureISAFormulaWindow().contains(
      "doses of antibiotic1 per month"));
  }

  public void verifyISAFormula(ProgramProductISAPage programProductISAPage, String ISAFormula) {
    assertEquals(programProductISAPage.verifyISAFormula(), ISAFormula);
  }

  public void verifyFieldsOnISAModalWindow(ProgramProductISAPage programProductISAPage) {
    assertTrue(programProductISAPage.verifyRatioTextBoxFieldOnISAModalWindowIsDisplayed());
    assertTrue(programProductISAPage.verifyDosesPerYearTextBoxFieldOnISAModalWindowIsDisplayed());
    assertTrue(programProductISAPage.verifyBufferPercentageTextBoxFieldOnISAModalWindowIsDisplayed());
    assertTrue(programProductISAPage.verifyWastageRateTextBoxFieldOnISAModalWindowIsDisplayed());
    assertTrue(programProductISAPage.verifyProgramProductISACancelButtonFieldOnISAModalWindowIsDisplayed());
    assertTrue(programProductISAPage.verifyProgramProductISASaveButtonFieldOnISAModalWindowIsDisplayed());
    assertTrue(programProductISAPage.verifyISAPopulationTextFieldOnISAModalWindowIsDisplayed());
    assertTrue(programProductISAPage.verifyAdjustmentValueTextBoxFieldOnISAModalWindowIsDisplayed());
  }

  public void verifyErrorMessageDiv(ProgramProductISAPage programProductISAPage) {
    assertTrue(programProductISAPage.verifyErrorMessageDiv());
  }

  private void verifySearchResults(ProgramProductISAPage programProductISAPage) {
    assertTrue(programProductISAPage.verifySearchResults());
  }

  public void verifyMonthlyRestockAmountPresent(ProgramProductISAPage programProductISAPage) {
    assertTrue(programProductISAPage.verifyMonthlyRestockAmountPresent());
  }

  @AfterMethod(groups = "admin")
  public void tearDown() throws SQLException {
    testWebDriver.sleep(500);
    dbWrapper.removeAllExistingRights("Admin");
    dbWrapper.insertAllAdminRightsAsSeedData();
    if (!testWebDriver.getElementById("username").isDisplayed()) {
      HomePage homePage = PageObjectFactory.getHomePage(testWebDriver);
      homePage.logout(baseUrlGlobal);
      dbWrapper.deleteData();
      dbWrapper.closeConnection();
    }
  }

  @DataProvider(name = "Data-Provider-Function")
  public Object[][] parameterIntTestProviderPositive() {
    return new Object[][]{{"Admin123", "Admin123", "VACCINES"}};
  }

  @DataProvider(name = "Data-Provider-Function-Search")
  public Object[][] parameterIntTestSearch() {
    return new Object[][]{{"Admin123", "Admin123", "VACCINES", "antibiotic1"}};
  }

  @DataProvider(name = "Data-Provider-Function-Verify-Push-Type-Program")
  public Object[][] parameterIntTestPushTypeProgram() {
    return new Object[][]{{"Admin123", "Admin123", "VACCINES", "TB"}};
  }

  @DataProvider(name = "Data-Provider-Function-Multiple-Programs")
  public Object[][] parameterIntTestMultipleProducts() {
    return new Object[][]{{"Admin123", "Admin123", "VACCINES", "TB", "antibiotic1", "antibiotic2", "antibiotic3", "antibiotic4"}};
  }
}