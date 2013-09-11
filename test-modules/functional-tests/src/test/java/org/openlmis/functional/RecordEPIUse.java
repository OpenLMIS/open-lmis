/*
 * Copyright © 2013 VillageReach.  All Rights Reserved.  This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 *
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package org.openlmis.functional;


import cucumber.api.DataTable;
import cucumber.api.Scenario;
import cucumber.api.java.After;
import cucumber.api.java.en.And;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import org.openlmis.UiUtils.CaptureScreenshotOnFailureListener;
import org.openlmis.UiUtils.TestCaseHelper;
import org.openlmis.pageobjects.*;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;
import org.testng.annotations.*;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static com.thoughtworks.selenium.SeleneseTestBase.assertFalse;


@TransactionConfiguration(defaultRollback = true)
@Transactional

@Listeners(CaptureScreenshotOnFailureListener.class)

public class RecordEPIUse extends TestCaseHelper {

  public String userSIC, password;
  public static final String periodDisplayedByDefault = "Period14";
  public static final String periodNotToBeDisplayedInDropDown = "Period1";

  @BeforeMethod(groups = {"distribution","offline"})
  public void setUp() throws Exception {
    super.setup();
  }
    @Then("^I should see product group \"([^\"]*)\"")
    public void verifyProductGroup(String productGroup) {
        new EPIUse(testWebDriver).verifyProductGroup(productGroup, 1);
    }

    @When("^I Enter EPI values without end of month:$")
    public void enterEPIValues(DataTable tableData) {
        EPIUse epiUse = new EPIUse(testWebDriver);
        Map<String, String> epiData = tableData.asMaps().get(0);

        epiUse.enterValueInDistributed(epiData.get("distributed"), 1);
        epiUse.enterValueInExpirationDate(epiData.get("expirationDate"), 1);
        epiUse.enterValueInLoss(epiData.get("loss"), 1);
        epiUse.enterValueInReceived(epiData.get("received"), 1);
        epiUse.enterValueInStockAtFirstOfMonth(epiData.get("firstOfMonth"), 1);
    }

    @When("^I verify saved EPI values:$")
    public void verifySavedEPIValues(DataTable tableData) {
        new RefrigeratorPage(testWebDriver).navigateToRefrigeratorTab();
        EPIUse epiUse = new EPIUse(testWebDriver);
        epiUse.navigate();
        Map<String, String> epiData = tableData.asMaps().get(0);

        epiUse.verifyData(epiData);
    }

    @And("^I verify total is \"([^\"]*)\"$")
    public void verifyTotalField(String total) {
        new EPIUse(testWebDriver).verifyTotal(total, 1);
    }


    @Then("^Navigate to EPI tab$")
    public void navigateToEpiTab() throws IOException {
        EPIUse epiUse = new EPIUse(testWebDriver);
        epiUse.navigate();
    }

    @Then("^Verify indicator should be \"([^\"]*)\"$")
    public void shouldVerifyIndicatorColor(String color) throws IOException, SQLException {
        EPIUse epiUse = new EPIUse(testWebDriver);
        epiUse.verifyIndicator(color);
    }

    @When("^I enter EPI end of month as \"([^\"]*)\"")
    public void enterEPIEndOfMonth(String endOfMonth) {
        new EPIUse(testWebDriver).enterValueInStockAtEndOfMonth(endOfMonth, 1);
    }


    @Test(groups = {"distribution"}, dataProvider = "Data-Provider-Function")
    public void testEditEPIUse(String userSIC, String password, String deliveryZoneCodeFirst, String deliveryZoneCodeSecond,
                                                    String deliveryZoneNameFirst, String deliveryZoneNameSecond,
                                                    String facilityCodeFirst, String facilityCodeSecond,
                                                    String programFirst, String programSecond, String schedule, String period, Integer totalNumberOfPeriods) throws Exception {

        List<String> rightsList = new ArrayList<String>();
        rightsList.add("MANAGE_DISTRIBUTION");
        setupTestDataToInitiateRnRAndDistribution("F10", "F11", true, programFirst, userSIC, "200", "openLmis", rightsList, programSecond, "District1", "Ngorongoro", "Ngorongoro");
        setupDataForDeliveryZone(true, deliveryZoneCodeFirst, deliveryZoneCodeSecond,
                deliveryZoneNameFirst, deliveryZoneNameSecond,
                facilityCodeFirst, facilityCodeSecond,
                programFirst, programSecond, schedule);
        dbWrapper.insertRoleAssignmentForDistribution(userSIC, "store in-charge", deliveryZoneCodeFirst);
        dbWrapper.insertRoleAssignmentForDistribution(userSIC, "store in-charge", deliveryZoneCodeSecond);
        dbWrapper.insertProductGroup("PG1");
        dbWrapper.insertProductGroup("PG2");
        dbWrapper.insertProductGroup("PG3");
        dbWrapper.insertProductWithGroup("Product1","ProdutName1","PG1",true);
        dbWrapper.insertProductWithGroup("Product2","ProdutName2","PG1",false);
        dbWrapper.insertProductWithGroup("Product3","ProdutName3","PG2",false);
        dbWrapper.insertProductWithGroup("Product4","ProdutName4","PG2",false);
        dbWrapper.insertProductWithGroup("Product5","ProdutName5","PG3",true);
        dbWrapper.insertProductWithGroup("Product6","ProdutName6","PG3",true);
        dbWrapper.insertProgramProduct("Product1",programFirst,"10","false");
        dbWrapper.insertProgramProduct("Product2",programFirst,"10","true");
        dbWrapper.insertProgramProduct("Product3",programFirst,"10","true");
        dbWrapper.insertProgramProduct("Product4",programFirst,"10","true");
        dbWrapper.insertProgramProduct("Product5",programFirst,"10","false");
        dbWrapper.insertProgramProduct("Product6",programFirst,"10","true");

        LoginPage loginPage = new LoginPage(testWebDriver, baseUrlGlobal);
        HomePage homePage = loginPage.loginAs(userSIC, password);
        DistributionPage distributionPage = homePage.navigatePlanDistribution();
        distributionPage.selectValueFromDeliveryZone(deliveryZoneNameFirst);
        distributionPage.selectValueFromProgram(programFirst);
        distributionPage.clickInitiateDistribution();
        distributionPage.clickRecordData();
        FacilityListPage facilityListPage = new FacilityListPage(testWebDriver);
        facilityListPage.selectFacility("F10");
        EPIUse epiUse = new EPIUse(testWebDriver);
        epiUse.navigate();
        epiUse.verifyProductGroup("PG3-Name",1);
        epiUse.verifyIndicator("RED");

        epiUse.enterValueInStockAtFirstOfMonth("10",1);
        epiUse.verifyIndicator("AMBER");
        epiUse.enterValueInReceived("20", 1);
        epiUse.enterValueInDistributed("30", 1);
        epiUse.enterValueInLoss("40", 1);
        epiUse.enterValueInStockAtEndOfMonth("50",1);
        epiUse.enterValueInExpirationDate("10/2011",1);


        RefrigeratorPage refrigeratorPage = new RefrigeratorPage(testWebDriver);
        refrigeratorPage.navigateToRefrigeratorTab();
        epiUse.navigate();

        epiUse.verifyTotal("30",1);
        epiUse.verifyStockAtFirstOfMonth("10", 1);
        epiUse.verifyReceived("20", 1);
        epiUse.verifyDistributed("30", 1);
        epiUse.verifyLoss("40", 1);
        epiUse.verifyStockAtEndOfMonth("50", 1);
        epiUse.verifyExpirationDate("10/2011", 1);

        epiUse.checkUncheckStockAtFirstOfMonthNotRecorded(1);
        epiUse.checkUncheckReceivedNotRecorded(1);
        epiUse.checkUncheckDistributedNotRecorded(1);
        epiUse.checkUncheckLossNotRecorded(1);
        epiUse.checkUncheckStockAtEndOfMonthNotRecorded(1);
        epiUse.checkUncheckExpirationDateNotRecorded(1);

        refrigeratorPage.navigateToRefrigeratorTab();
        epiUse.navigate();

        epiUse.verifyStockAtFirstOfMonthStatus(false,1);
        epiUse.verifyReceivedStatus(false,1);
        epiUse.verifyDistributedStatus(false,1);
        epiUse.verifyLossStatus(false,1);
        epiUse.verifyStockAtEndOfMonthStatus(false,1);
        epiUse.verifyExpirationDateStatus(false,1);

        epiUse.checkUncheckStockAtFirstOfMonthNotRecorded(1);
        epiUse.checkUncheckReceivedNotRecorded(1);
        epiUse.checkUncheckDistributedNotRecorded(1);
        epiUse.checkUncheckLossNotRecorded(1);
        epiUse.checkUncheckStockAtEndOfMonthNotRecorded(1);
        epiUse.checkUncheckExpirationDateNotRecorded(1);

        epiUse.enterValueInStockAtFirstOfMonth("20",1);
        epiUse.enterValueInReceived("30", 1);
        epiUse.enterValueInDistributed("40", 1);
        epiUse.enterValueInLoss("50", 1);
        epiUse.enterValueInStockAtEndOfMonth("60",1);
        epiUse.enterValueInExpirationDate("11/2012",1);

        refrigeratorPage.navigateToRefrigeratorTab();
        epiUse.navigate();
        epiUse.checkApplyNRToAllFields(false);
        epiUse.verifyTotal("50",1);

        epiUse.verifyStockAtFirstOfMonth("20", 1);
        epiUse.verifyReceived("30", 1);
        epiUse.verifyDistributed("40", 1);
        epiUse.verifyLoss("50", 1);
        epiUse.verifyStockAtEndOfMonth("60", 1);
        epiUse.verifyExpirationDate("11/2012", 1);

        epiUse.verifyStockAtFirstOfMonthStatus(true, 1);
        epiUse.verifyReceivedStatus(true, 1);
        epiUse.verifyDistributedStatus(true, 1);
        epiUse.verifyLossStatus(true, 1);
        epiUse.verifyStockAtEndOfMonthStatus(true, 1);
        epiUse.verifyExpirationDateStatus(true, 1);
        epiUse.verifyIndicator("GREEN");
        epiUse.verifyStockAtFirstOfMonthStatus(true,1);
        epiUse.verifyReceivedStatus(true,1);
        epiUse.verifyDistributedStatus(true,1);
        epiUse.verifyLossStatus(true,1);
        epiUse.verifyStockAtEndOfMonthStatus(true,1);
        epiUse.verifyExpirationDateStatus(true,1);
        epiUse.verifyIndicator("GREEN");

        epiUse.checkApplyNRToAllFields(true);
        refrigeratorPage.navigateToRefrigeratorTab();
        epiUse.navigate();

        epiUse.verifyStockAtFirstOfMonthStatus(false,1);
        epiUse.verifyReceivedStatus(false,1);
        epiUse.verifyDistributedStatus(false,1);
        epiUse.verifyLossStatus(false,1);
        epiUse.verifyStockAtEndOfMonthStatus(false,1);
        epiUse.verifyExpirationDateStatus(false,1);

    }

    @Test(groups = {"offline"}, dataProvider = "Data-Provider-Function")
    public void testEditEPIUseOffline(String userSIC, String password, String deliveryZoneCodeFirst, String deliveryZoneCodeSecond,
                               String deliveryZoneNameFirst, String deliveryZoneNameSecond,
                               String facilityCodeFirst, String facilityCodeSecond,
                               String programFirst, String programSecond, String schedule, String period, Integer totalNumberOfPeriods) throws Exception {

        List<String> rightsList = new ArrayList<String>();
        rightsList.add("MANAGE_DISTRIBUTION");
        setupTestDataToInitiateRnRAndDistribution("F10", "F11", true, programFirst, userSIC, "200", "openLmis", rightsList, programSecond, "District1", "Ngorongoro", "Ngorongoro");
        setupDataForDeliveryZone(true, deliveryZoneCodeFirst, deliveryZoneCodeSecond,
                deliveryZoneNameFirst, deliveryZoneNameSecond,
                facilityCodeFirst, facilityCodeSecond,
                programFirst, programSecond, schedule);
        dbWrapper.insertRoleAssignmentForDistribution(userSIC, "store in-charge", deliveryZoneCodeFirst);
        dbWrapper.insertRoleAssignmentForDistribution(userSIC, "store in-charge", deliveryZoneCodeSecond);
        dbWrapper.insertProductGroup("PG1");
        dbWrapper.insertProductWithGroup("Product5", "ProdutName5", "PG1", true);
        dbWrapper.insertProductWithGroup("Product6", "ProdutName6", "PG1", true);
        dbWrapper.insertProgramProduct("Product5", programFirst, "10", "false");
        dbWrapper.insertProgramProduct("Product6", programFirst, "10", "true");

        LoginPage loginPage = new LoginPage(testWebDriver, baseUrlGlobal);
        HomePage homePage = loginPage.loginAs(userSIC, password);
        DistributionPage distributionPage = homePage.navigatePlanDistribution();
        distributionPage.selectValueFromDeliveryZone(deliveryZoneNameFirst);
        distributionPage.selectValueFromProgram(programFirst);
        distributionPage.clickInitiateDistribution();

        testWebDriver.sleep(10000);
        switchOffNetwork();
        testWebDriver.sleep(5000);
        homePage.navigateHomePage();
        testWebDriver.sleep(5000);
        homePage.navigatePlanDistribution();
        assertFalse("Delivery Zone selectbox displayed.",distributionPage.IsDisplayedSelectDeliveryZoneSelectBox());
        assertFalse("Period selectbox displayed.", distributionPage.IsDisplayedSelectPeriodSelectBox());
        assertFalse("Program selectbox displayed.", distributionPage.IsDisplayedSelectProgramSelectBox());

        distributionPage.clickRecordData();
        FacilityListPage facilityListPage = new FacilityListPage(testWebDriver);
        facilityListPage.selectFacility("F10");
        EPIUse epiUse = new EPIUse(testWebDriver);
        epiUse.navigate();
        epiUse.verifyProductGroup("PG1-Name",1);
        epiUse.verifyIndicator("RED");

        epiUse.enterValueInStockAtFirstOfMonth("10",1);
        epiUse.verifyIndicator("AMBER");
        epiUse.enterValueInReceived("20", 1);
        epiUse.enterValueInDistributed("30", 1);
        epiUse.enterValueInLoss("40", 1);
        epiUse.enterValueInStockAtEndOfMonth("50",1);
        epiUse.enterValueInExpirationDate("10/2011",1);


        RefrigeratorPage refrigeratorPage = new RefrigeratorPage(testWebDriver);
        refrigeratorPage.navigateToRefrigeratorTab();
        epiUse.navigate();

        epiUse.verifyTotal("30",1);
        epiUse.verifyStockAtFirstOfMonth("10", 1);
        epiUse.verifyReceived("20", 1);
        epiUse.verifyDistributed("30", 1);
        epiUse.verifyLoss("40", 1);
        epiUse.verifyStockAtEndOfMonth("50", 1);
        epiUse.verifyExpirationDate("10/2011", 1);
    }


  @AfterMethod(groups = {"distribution"})
  @After
  public void tearDown(Scenario scenario) throws Exception {
    testWebDriver.sleep(250);

    if (!testWebDriver.getElementById("username").isDisplayed()) {
      if(scenario.isFailed())
      {
        testWebDriver.captureScreenShotForCucumberRun();
      }
      HomePage homePage = new HomePage(testWebDriver);
      homePage.logout(baseUrlGlobal);
      dbWrapper.deleteData();
      dbWrapper.closeConnection();
    }

  }

    @AfterMethod(groups = {"offline"})
    public void tearDownNew() throws Exception {
        switchOnNetwork();
        testWebDriver.sleep(10000);
        dbWrapper.deleteData();
        dbWrapper.closeConnection();
    }


    @DataProvider(name = "Data-Provider-Function")
    public Object[][] parameterIntTestProviderPositive() {
        return new Object[][]{
                {"storeincharge", "Admin123", "DZ1", "DZ2", "Delivery Zone First", "Delivery Zone Second",
                        "F10", "F11", "VACCINES", "TB", "M", "Period", 14}
        };

  }
}

