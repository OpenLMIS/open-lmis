/*
 * Copyright © 2013 VillageReach.  All Rights Reserved.  This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 *
 *  If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package org.openlmis.functional;


import cucumber.api.DataTable;
import cucumber.api.java.After;
import cucumber.api.java.Before;
import cucumber.api.java.en.And;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import org.openlmis.UiUtils.CaptureScreenshotOnFailureListener;
import org.openlmis.UiUtils.TestCaseHelper;
import org.openlmis.pageobjects.CreateFacilityPage;
import org.openlmis.pageobjects.DeleteFacilityPage;
import org.openlmis.pageobjects.HomePage;
import org.openlmis.pageobjects.LoginPage;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;
import org.testng.annotations.*;

import java.util.List;
import java.util.Map;

import static java.lang.String.valueOf;
import static org.openlmis.pageobjects.CreateFacilityPage.SaveButton;


@TransactionConfiguration(defaultRollback = true)
@Transactional

@Listeners(CaptureScreenshotOnFailureListener.class)

public class ManageISA extends TestCaseHelper {

  private static String geoZone = "Ngorongoro";
  private static String facilityType = "Lvl3 Hospital";
  private static String operatedBy = "MoH";
  private static String facilityCodePrefix = "FCcode";
  private static String facilityNamePrefix = "FCname";
  public String user, password, program, product, productName, category, whoratio, dosesperyear, wastageFactor,
    bufferpercentage, minimumvalue, maximumvalue, adjustmentvalue, date_time;

  @BeforeMethod(groups = "functional2")
  @Before
  public void setUp() throws Exception {
    super.setup();
  }


  @Given("^I have the following data for override ISA:$")
  public void theFollowingDataExist(DataTable tableData) throws Exception {
    List<Map<String, String>> data = tableData.asMaps();
    for (Map map : data) {
      user = map.get("user").toString();
      password = map.get("password").toString();
      program = map.get("program").toString();
      product = map.get("product").toString();
      productName = map.get("productName").toString();
      category = map.get("category").toString();
      whoratio = map.get("whoratio").toString();
      dosesperyear = map.get("dosesperyear").toString();
      wastageFactor = map.get("wastageFactor").toString();
      bufferpercentage = map.get("bufferpercentage").toString();
      minimumvalue = map.get("minimumvalue").toString();
      maximumvalue = map.get("maximumvalue").toString();
      adjustmentvalue = map.get("adjustmentvalue").toString();

      setupProgramProductTestDataWithCategories(product, productName, category, program);
      setupProgramProductISA(program, product, whoratio, dosesperyear, wastageFactor, bufferpercentage, minimumvalue, maximumvalue, adjustmentvalue);

    }
  }

  @When("^I create facilities$")
  public void createFacility() throws Exception {
    CreateFacilityPage createFacilityPage = new CreateFacilityPage(testWebDriver);
    date_time = createFacilityPage.enterValuesInFacility(facilityCodePrefix, facilityNamePrefix,
      program, geoZone, facilityType, operatedBy, valueOf(333), true);
  }

  @And("^I override ISA \"([^\"]*)\"$")
  public void overrideISA(String isaValue) throws Exception {
    CreateFacilityPage createFacilityPage = new CreateFacilityPage(testWebDriver);
    createFacilityPage.overrideIsa(Integer.parseInt(isaValue));
  }

  @Then("^I should see calculated ISA \"([^\"]*)\"$")
  public void verifyCalculatedISA(String isaValue) throws Exception {
    CreateFacilityPage createFacilityPage = new CreateFacilityPage(testWebDriver);
    createFacilityPage.verifyCalculatedIsa(Integer.parseInt(isaValue));
  }

  @When("^I click ISA done$")
  public void clickISADone() throws Exception {
    CreateFacilityPage createFacilityPage = new CreateFacilityPage(testWebDriver);
    createFacilityPage.clickIsaDoneButton();
  }

  @When("^I save facility$")
  public void clickSave() throws Exception {
    SaveButton.click();
  }

  @Then("^I should see save successfully$")
  public void verifySaveSuccessfully() throws Exception {
    CreateFacilityPage createFacilityPage = new CreateFacilityPage(testWebDriver);
    createFacilityPage.verifySuccessMessage();
  }

  @When("^I search facility$")
  public void searchFacility() throws Exception {
    DeleteFacilityPage deleteFacilityPage = new DeleteFacilityPage(testWebDriver);
    deleteFacilityPage.searchFacility(date_time);
    deleteFacilityPage.clickFacilityList(date_time);
  }

  @Then("^I should see overridden ISA \"([^\"]*)\"$")
  public void verifyOverriddenISA(String isa) throws Exception {
    CreateFacilityPage createFacilityPage = new CreateFacilityPage(testWebDriver);
    createFacilityPage.verifyOverriddenIsa(isa);
  }

  @Test(groups = {"functional2"}, dataProvider = "Data-Provider-Function")
  public void shouldOverrideIsaExistingFacility(String userSIC, String password, String program) throws Exception {
    setupProgramProductTestDataWithCategories("P1", "antibiotic1", "C1", "VACCINES");
    setupProgramProductISA(program, "P1", "1", "2", "3", "100", "100", "1000", "5");
    LoginPage loginPage = new LoginPage(testWebDriver, baseUrlGlobal);
    loginPage.loginAs(userSIC, password);

    CreateFacilityPage createFacilityPage = new HomePage(testWebDriver).navigateCreateFacility();

    String date_time = createFacilityPage.enterValuesInFacility(facilityCodePrefix, facilityNamePrefix,
      program, geoZone, facilityType, operatedBy, valueOf(333), true);
    SaveButton.click();
    DeleteFacilityPage deleteFacilityPage = new DeleteFacilityPage(testWebDriver);
    deleteFacilityPage.searchFacility(date_time);
    deleteFacilityPage.clickFacilityList(date_time);

    createFacilityPage.overrideIsa(24);
    createFacilityPage.verifyCalculatedIsa(100);
    createFacilityPage.clickIsaDoneButton();
    createFacilityPage.verifyOverriddenIsa("24");

    createFacilityPage.overrideIsa(30);
    createFacilityPage.clickIsaCancelButton();
    createFacilityPage.verifyOverriddenIsa("24");

    createFacilityPage.overrideIsa(30);
    createFacilityPage.clickUseCalculatedIsaButton();
    createFacilityPage.clickIsaDoneButton();
    createFacilityPage.verifyOverriddenIsa("");

    createFacilityPage.editPopulation(valueOf(30));
    createFacilityPage.overrideIsa(24);
    createFacilityPage.verifyCalculatedIsa(100);
    createFacilityPage.clickIsaCancelButton();

    createFacilityPage.editPopulation(valueOf(3000000));
    createFacilityPage.overrideIsa(124);
    createFacilityPage.verifyCalculatedIsa(1000);
    createFacilityPage.clickIsaCancelButton();
    createFacilityPage.verifyOverriddenIsa("");

    createFacilityPage.overrideIsa(24);
    createFacilityPage.clickIsaDoneButton();
    SaveButton.click();
    createFacilityPage.verifySuccessMessage();
    deleteFacilityPage.clickFacilityList(date_time);
    createFacilityPage.verifyOverriddenIsa("24");
  }


  @AfterMethod(groups = "functional2")
  @After
  public void tearDown() throws Exception {
    if (!testWebDriver.getElementById("username").isDisplayed()) {
      HomePage homePage = new HomePage(testWebDriver);
      homePage.logout(baseUrlGlobal);
      dbWrapper.deleteData();
      dbWrapper.closeConnection();
    }
  }


  @DataProvider(name = "Data-Provider-Function")
  public Object[][] parameterIntTestProviderPositive() {
    return new Object[][]{
      {"Admin123", "Admin123", "VACCINES"}
    };

  }

}

