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


import cucumber.api.DataTable;
import cucumber.api.java.en.And;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import org.openlmis.UiUtils.CaptureScreenshotOnFailureListener;
import org.openlmis.UiUtils.TestCaseHelper;
import org.openlmis.pageobjects.FacilityPage;
import org.openlmis.pageobjects.HomePage;
import org.openlmis.pageobjects.LoginPage;
import org.openlmis.pageobjects.PageObjectFactory;
import org.testng.annotations.*;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import static com.thoughtworks.selenium.SeleneseTestBase.assertEquals;
import static com.thoughtworks.selenium.SeleneseTestBase.assertTrue;
import static java.lang.String.valueOf;
import static org.openlmis.pageobjects.FacilityPage.saveButton;

@Listeners(CaptureScreenshotOnFailureListener.class)

public class ManageISA extends TestCaseHelper {

  private static String geoZone = "Ngorongoro";
  private static String facilityType = "Lvl3 Hospital";
  private static String operatedBy = "MoH";
  private static String facilityCodePrefix = "FCcode";
  private static String facilityNamePrefix = "FCname";
  public String user, program, product, productName, category, whoRatio, dosesPerYear, wastageFactor, bufferPercentage, minimumValue,
    maximumValue, adjustmentValue, date_time;
  static FacilityPage facilityPage;

  @BeforeMethod(groups = "admin")
  public void setUp() throws InterruptedException, SQLException, IOException {
    super.setup();
    dbWrapper.removeAllExistingRights("Admin");
    dbWrapper.assignRight("Admin", "MANAGE_PROGRAM_PRODUCT");
    dbWrapper.assignRight("Admin", "MANAGE_FACILITY");
  }

  @Given("^I have the following data for override ISA:$")
  public void theFollowingDataExist(DataTable tableData) throws SQLException {
    List<Map<String, String>> data = tableData.asMaps(String.class, String.class);
    for (Map map : data) {
      user = map.get("user").toString();
      program = map.get("program").toString();
      product = map.get("product").toString();
      productName = map.get("productName").toString();
      category = map.get("category").toString();
      whoRatio = map.get("whoRatio").toString();
      dosesPerYear = map.get("dosesPerYear").toString();
      wastageFactor = map.get("wastageFactor").toString();
      bufferPercentage = map.get("bufferPercentage").toString();
      minimumValue = map.get("minimumValue").toString();
      maximumValue = map.get("maximumValue").toString();
      adjustmentValue = map.get("adjustmentValue").toString();

      String categoryName = "Category 1";
      setupProgramProductTestDataWithCategories(category, categoryName, product, productName, program);
      setupProgramProductISA(program, product, whoRatio, dosesPerYear, wastageFactor, bufferPercentage, minimumValue, maximumValue, adjustmentValue);
    }
  }

  @When("^I create facility$")
  public void createFacility() {
    facilityPage = PageObjectFactory.getFacilityPage(testWebDriver);
    date_time = PageObjectFactory.getFacilityPage(testWebDriver).enterValuesInFacility(facilityCodePrefix, facilityNamePrefix,
      program, geoZone, facilityType, operatedBy, valueOf(333), true);
  }

  @And("^I override ISA \"([^\"]*)\"$")
  public void overrideISA(String isaValue) {
    PageObjectFactory.getFacilityPage(testWebDriver).overrideIsa(isaValue, 1);
  }

  @Then("^I should see calculated ISA \"([^\"]*)\"$")
  public void verifyCalculatedISA(String isaValue) {
    assertEquals(Integer.parseInt(isaValue), PageObjectFactory.getFacilityPage(testWebDriver).getCalculatedIsa());
  }

  @When("^I click ISA done$")
  public void clickISADone() {
    PageObjectFactory.getFacilityPage(testWebDriver).clickIsaDoneButton();
  }

  @When("^I save facility$")
  public void clickSave() {
    saveButton.click();
  }

  @Then("^I should see save successfully$")
  public void verifySaveSuccessfully() {
    assertTrue(PageObjectFactory.getFacilityPage(testWebDriver).isSuccessMessageDisplayed());
  }

  @When("^I search facility$")
  public void searchFacility() {
    facilityPage = PageObjectFactory.getFacilityPage(testWebDriver);
    facilityPage.searchFacility(date_time);
    facilityPage.clickFirstFacilityList();
  }

  @Then("^I should see overridden ISA \"([^\"]*)\"$")
  public void verifyOverriddenISA(String isa) {
    facilityPage = PageObjectFactory.getFacilityPage(testWebDriver);
    facilityPage.verifyOverriddenIsa(isa);
  }

  @Test(groups = {"admin"}, dataProvider = "Data-Provider-Function")
  public void shouldOverrideIsaExistingFacility(String userSIC, String password, String program) throws SQLException {
    setupProgramProductTestDataWithCategories("C1", "Category 1", "P1", "antibiotic1", "VACCINES");
    setupProgramProductISA(program, "P1", "1", "2", "3", "100", "100", "1000", "5");
    LoginPage loginPage = PageObjectFactory.getLoginPage(testWebDriver, baseUrlGlobal);
    loginPage.loginAs(userSIC, password);
    HomePage homePage = PageObjectFactory.getHomePage(testWebDriver);
    facilityPage = homePage.navigateManageFacility();
    homePage.clickCreateFacilityButton();
    assertEquals("Add new facility", facilityPage.getNewFacilityHeader());

    String date_time = facilityPage.enterValuesInFacility(facilityCodePrefix, facilityNamePrefix, program, geoZone,
      facilityType, operatedBy, valueOf(333), true);
    saveButton.click();
    facilityPage.searchFacility(date_time);
    facilityPage.clickFirstFacilityList();

    facilityPage.overrideIsa("24", 1);
    assertEquals("100", facilityPage.getCalculatedIsa());
    facilityPage.clickIsaDoneButton();
    facilityPage.verifyOverriddenIsa("24");

    facilityPage.overrideIsa("30", 1);
    facilityPage.clickIsaCancelButton();
    facilityPage.verifyOverriddenIsa("24");

    facilityPage.overrideIsa("30", 1);
    facilityPage.clickUseCalculatedIsaButton();
    facilityPage.clickIsaDoneButton();
    facilityPage.verifyOverriddenIsa("");

    facilityPage.editPopulation(valueOf("30"));
    facilityPage.overrideIsa("24", 1);
    assertEquals("100", facilityPage.getCalculatedIsa());
    facilityPage.clickIsaCancelButton();

    facilityPage.editPopulation(valueOf(3000000));
    facilityPage.overrideIsa("124", 1);
    assertEquals("1000", facilityPage.getCalculatedIsa());
    facilityPage.clickIsaCancelButton();
    facilityPage.verifyOverriddenIsa("");

    facilityPage.overrideIsa("24", 1);
    facilityPage.clickIsaDoneButton();
    saveButton.click();
    assertTrue(facilityPage.isSuccessMessageDisplayed());
    facilityPage.clickFirstFacilityList();
    facilityPage.verifyOverriddenIsa("24");
  }

  @AfterMethod(groups = "admin")
  public void tearDown() throws SQLException {
    testWebDriver.sleep(500);
    if (!testWebDriver.getElementById("username").isDisplayed()) {
      HomePage homePage = PageObjectFactory.getHomePage(testWebDriver);
      homePage.logout(baseUrlGlobal);
    }
    dbWrapper.removeAllExistingRights("Admin");
    dbWrapper.insertAllAdminRightsAsSeedData();
    dbWrapper.deleteData();
    dbWrapper.closeConnection();
  }

  @DataProvider(name = "Data-Provider-Function")
  public Object[][] parameterIntTestProviderPositive() {
    return new Object[][]{
      {"Admin123", "Admin123", "VACCINES"}
    };
  }
}

