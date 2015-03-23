/*
 *
 *  * This program is part of the OpenLMIS logistics management information system platform software.
 *  * Copyright © 2013 VillageReach
 *  *
 *  * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *  *  
 *  * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 *  * You should have received a copy of the GNU Affero General Public License along with this program.  If not, see http://www.gnu.org/licenses.  For additional information contact info@OpenLMIS.org. 
 *
 */

package org.openlmis.functional;


import cucumber.api.DataTable;
import cucumber.api.java.en.And;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import org.openlmis.UiUtils.TestCaseHelper;
import org.openlmis.UiUtils.TestWebDriver;
import org.openlmis.pageobjects.*;
import org.openqa.selenium.JavascriptExecutor;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.io.IOException;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.thoughtworks.selenium.SeleneseTestBase.assertTrue;

public class DistributionEpiUseSyncTest extends TestCaseHelper {

  public static final String USER = "user";
  public static final String PASSWORD = "password";
  public static final String FIRST_DELIVERY_ZONE_CODE = "firstDeliveryZoneCode";
  public static final String SECOND_DELIVERY_ZONE_CODE = "secondDeliveryZoneCode";
  public static final String FIRST_DELIVERY_ZONE_NAME = "firstDeliveryZoneName";
  public static final String SECOND_DELIVERY_ZONE_NAME = "secondDeliveryZoneName";
  public static final String FIRST_FACILITY_CODE = "firstFacilityCode";
  public static final String SECOND_FACILITY_CODE = "secondFacilityCode";
  public static final String VACCINES_PROGRAM = "vaccinesProgram";
  public static final String TB_PROGRAM = "secondProgram";
  public static final String SCHEDULE = "schedule";
  public static final String PRODUCT_GROUP_CODE = "productGroupName";
  LoginPage loginPage;
  FacilityListPage facilityListPage;
  EPIUsePage epiUsePage;

  public Map<String, String> epiUseData = new HashMap<String, String>() {{
    put(USER, "fieldCoordinator");
    put(PASSWORD, "Admin123");
    put(FIRST_DELIVERY_ZONE_CODE, "DZ1");
    put(SECOND_DELIVERY_ZONE_CODE, "DZ2");
    put(FIRST_DELIVERY_ZONE_NAME, "Delivery Zone First");
    put(SECOND_DELIVERY_ZONE_NAME, "Delivery Zone Second");
    put(FIRST_FACILITY_CODE, "F10");
    put(SECOND_FACILITY_CODE, "F11");
    put(VACCINES_PROGRAM, "VACCINES");
    put(TB_PROGRAM, "TB");
    put(SCHEDULE, "M");
    put(PRODUCT_GROUP_CODE, "PG1");
  }};

  @BeforeMethod(groups = {"distribution"})
  public void setUp() throws InterruptedException, SQLException, IOException {
    super.setup();
    loginPage = PageObjectFactory.getLoginPage(testWebDriver, baseUrlGlobal);
    facilityListPage = PageObjectFactory.getFacilityListPage(testWebDriver);

    setupDataForDistributionTest(epiUseData);
    dbWrapper.insertProductGroup(epiUseData.get(PRODUCT_GROUP_CODE));
    dbWrapper.insertProductWithGroup("Product5", "ProductName5", epiUseData.get(PRODUCT_GROUP_CODE), true);
    dbWrapper.insertProductWithGroup("Product6", "ProductName6", epiUseData.get(PRODUCT_GROUP_CODE), true);
    dbWrapper.insertProgramProduct("Product5", epiUseData.get(VACCINES_PROGRAM), "10", "false");
    dbWrapper.insertProgramProduct("Product6", epiUseData.get(VACCINES_PROGRAM), "10", "true");
  }

  @Test(groups = {"distribution"})
  public void testEpiUsePageSync() throws SQLException {
    HomePage homePage = loginPage.loginAs(epiUseData.get(USER), epiUseData.get(PASSWORD));
    initiateDistribution(epiUseData.get(FIRST_DELIVERY_ZONE_NAME), epiUseData.get(VACCINES_PROGRAM));
    VisitInformationPage visitInformationPage = facilityListPage.selectFacility(epiUseData.get(FIRST_FACILITY_CODE));
    visitInformationPage.enterDataWhenFacilityVisited("samuel", "Doe", "Verifier", "XYZ");

    EPIUsePage epiUsePage = visitInformationPage.navigateToEpiUse();
    epiUsePage.verifyIndicator("RED");
    epiUsePage.verifyProductGroup("PG1-Name", 1);
    epiUsePage.enterData(10, 20, 30, 40, 50, "10/2011", 1);
    epiUsePage.verifyIndicator("GREEN");

    ChildCoveragePage childCoveragePage = epiUsePage.navigateToChildCoverage();
    childCoveragePage.applyNRToAll();
    childCoveragePage.clickOK();

    AdultCoveragePage adultCoveragePage = childCoveragePage.navigateToAdultCoverage();
    adultCoveragePage.clickApplyNrToAll();
    adultCoveragePage.clickOK();

    FullCoveragePage fullCoveragePage = adultCoveragePage.navigateToFullCoverage();
    fullCoveragePage.enterData(12, 34, 45, "56");

    fullCoveragePage.navigateToEpiInventory();
    fillEpiInventoryWithOnlyDeliveredQuantity("2", "4", "6");

    DistributionPage distributionPage = homePage.navigateToDistributionWhenOnline();
    distributionPage.syncDistribution(1);
    assertTrue(distributionPage.getSyncMessage().contains("F10-Village Dispensary"));
    distributionPage.syncDistributionMessageDone();

    verifyEpiUseDataInDatabase(10, 20, 30, 40, 50, "10/2011", epiUseData.get(PRODUCT_GROUP_CODE), epiUseData.get(FIRST_FACILITY_CODE));
  }

  @Test(groups = {"distribution"})
  public void testEpiUseEditSync() throws SQLException {
    HomePage homePage = loginPage.loginAs(epiUseData.get(USER), epiUseData.get(PASSWORD));
    initiateDistribution(epiUseData.get(FIRST_DELIVERY_ZONE_NAME), epiUseData.get(VACCINES_PROGRAM));
    VisitInformationPage visitInformationPage = facilityListPage.selectFacility(epiUseData.get(FIRST_FACILITY_CODE));
    visitInformationPage.enterDataWhenFacilityVisited("samuel", "Doe", "Verifier", "XYZ");

    EPIUsePage epiUsePage = visitInformationPage.navigateToEpiUse();
    epiUsePage.verifyProductGroup("PG1-Name", 1);
    epiUsePage.verifyIndicator("RED");
    epiUsePage.enterValueInLoss("0", 1);
    epiUsePage.verifyIndicator("AMBER");
    epiUsePage.enterData(10, 20, 30, 40, 50, "10/2011", 1);
    epiUsePage.verifyIndicator("GREEN");

    RefrigeratorPage refrigeratorPage = epiUsePage.navigateToRefrigerators();
    refrigeratorPage.navigateToEpiUse();

    epiUsePage.verifyTotal("30", 1);
    epiUsePage.verifyStockAtFirstOfMonth("10", 1);
    epiUsePage.verifyReceived("20", 1);
    epiUsePage.verifyDistributed("30", 1);
    epiUsePage.verifyLoss("40", 1);
    epiUsePage.verifyStockAtEndOfMonth("50", 1);
    epiUsePage.verifyExpirationDate("10/2011", 1);

    epiUsePage.checkUnCheckStockAtFirstOfMonthNotRecorded(1);
    epiUsePage.checkUnCheckReceivedNotRecorded(1);
    epiUsePage.checkUnCheckDistributedNotRecorded(1);
    epiUsePage.checkUnCheckLossNotRecorded(1);
    epiUsePage.checkUnCheckStockAtEndOfMonthNotRecorded(1);
    epiUsePage.checkUnCheckExpirationDateNotRecorded(1);

    epiUsePage.navigateToRefrigerators();
    refrigeratorPage.navigateToEpiUse();

    epiUsePage.verifyStockAtFirstOfMonthStatus(false, 1);
    epiUsePage.verifyReceivedStatus(false, 1);
    epiUsePage.verifyDistributedStatus(false, 1);
    epiUsePage.verifyLossStatus(false, 1);
    epiUsePage.verifyStockAtEndOfMonthStatus(false, 1);
    epiUsePage.verifyExpirationDateStatus(false, 1);

    epiUsePage.checkUnCheckStockAtFirstOfMonthNotRecorded(1);
    epiUsePage.checkUnCheckReceivedNotRecorded(1);
    epiUsePage.checkUnCheckDistributedNotRecorded(1);
    epiUsePage.checkUnCheckLossNotRecorded(1);
    epiUsePage.checkUnCheckStockAtEndOfMonthNotRecorded(1);
    epiUsePage.checkUnCheckExpirationDateNotRecorded(1);

    epiUsePage.enterData(20, 30, 40, 50, 60, "11/2012", 1);
    epiUsePage.verifyIndicator("GREEN");

    epiUsePage.navigateToRefrigerators();
    refrigeratorPage.navigateToEpiUse();
    epiUsePage.checkApplyNRToAllFields(false);
    epiUsePage.verifyTotal("50", 1);

    epiUsePage.verifyStockAtFirstOfMonth("20", 1);
    epiUsePage.verifyReceived("30", 1);
    epiUsePage.verifyDistributed("40", 1);
    epiUsePage.verifyLoss("50", 1);
    epiUsePage.verifyStockAtEndOfMonth("60", 1);
    epiUsePage.verifyExpirationDate("11/2012", 1);

    epiUsePage.verifyStockAtFirstOfMonthStatus(true, 1);
    epiUsePage.verifyReceivedStatus(true, 1);
    epiUsePage.verifyDistributedStatus(true, 1);
    epiUsePage.verifyLossStatus(true, 1);
    epiUsePage.verifyStockAtEndOfMonthStatus(true, 1);
    epiUsePage.verifyExpirationDateStatus(true, 1);
    epiUsePage.verifyIndicator("GREEN");

    FullCoveragePage fullCoveragePage = epiUsePage.navigateToFullCoverage();
    fullCoveragePage.enterData(12, 34, 45, "56");

    ChildCoveragePage childCoveragePage = fullCoveragePage.navigateToChildCoverage();
    childCoveragePage.applyNRToAll();
    childCoveragePage.clickOK();

    AdultCoveragePage adultCoveragePage = childCoveragePage.navigateToAdultCoverage();
    adultCoveragePage.clickApplyNrToAll();
    adultCoveragePage.clickOK();

    adultCoveragePage.navigateToEpiInventory();
    fillEpiInventoryWithOnlyDeliveredQuantity("2", "4", "6");

    DistributionPage distributionPage = homePage.navigateToDistributionWhenOnline();
    distributionPage.syncDistribution(1);
    assertTrue(distributionPage.getSyncMessage().contains("F10-Village Dispensary"));
    distributionPage.syncDistributionMessageDone();

    verifyEpiUseDataInDatabase(20, 30, 40, 50, 60, "11/2012", epiUseData.get(PRODUCT_GROUP_CODE), epiUseData.get(FIRST_FACILITY_CODE));
  }

  @Test(groups = {"distribution"})
  public void testEpiUsePageSyncWhenSomeFieldsEmpty() throws SQLException {
    loginPage.loginAs(epiUseData.get(USER), epiUseData.get(PASSWORD));
    initiateDistribution(epiUseData.get(FIRST_DELIVERY_ZONE_NAME), epiUseData.get(VACCINES_PROGRAM));
    VisitInformationPage visitInformationPage = facilityListPage.selectFacility(epiUseData.get(FIRST_FACILITY_CODE));
    visitInformationPage.enterDataWhenFacilityVisited("samuel", "Doe", "Verifier", "XYZ");

    EPIUsePage epiUsePage = visitInformationPage.navigateToEpiUse();
    epiUsePage.verifyProductGroup("PG1-Name", 1);
    epiUsePage.verifyIndicator("RED");
    epiUsePage.enterValueInStockAtFirstOfMonth("10", 1);
    epiUsePage.verifyIndicator("AMBER");

    ChildCoveragePage childCoveragePage = epiUsePage.navigateToChildCoverage();
    childCoveragePage.applyNRToAll();
    childCoveragePage.clickOK();

    FullCoveragePage fullCoveragePage = childCoveragePage.navigateToFullCoverage();
    fullCoveragePage.enterData(12, 34, 45, "56");

    AdultCoveragePage adultCoveragePage = fullCoveragePage.navigateToAdultCoverage();
    adultCoveragePage.clickApplyNrToAll();
    adultCoveragePage.clickOK();

    adultCoveragePage.navigateToEpiInventory();
    fillEpiInventoryWithOnlyDeliveredQuantity("2", "4", "6");

    facilityListPage.selectFacility(epiUseData.get(FIRST_FACILITY_CODE));
    facilityListPage.verifyOverallFacilityIndicatorColor("AMBER");

    verifyEpiUseDataInDatabase(null, null, null, null, null, null, epiUseData.get(PRODUCT_GROUP_CODE), epiUseData.get(FIRST_FACILITY_CODE));
  }

  @Test(groups = {"distribution"})
  public void testEpiUsePageSyncWhenNrAppliedToAllFields() throws SQLException {
    HomePage homePage = loginPage.loginAs(epiUseData.get(USER), epiUseData.get(PASSWORD));
    initiateDistribution(epiUseData.get(FIRST_DELIVERY_ZONE_NAME), epiUseData.get(VACCINES_PROGRAM));
    VisitInformationPage visitInformationPage = facilityListPage.selectFacility(epiUseData.get(FIRST_FACILITY_CODE));
    visitInformationPage.enterDataWhenFacilityVisited("samuel", "Doe", "Verifier", "XYZ");

    EPIUsePage epiUsePage = visitInformationPage.navigateToEpiUse();

    epiUsePage.verifyProductGroup("PG1-Name", 1);
    epiUsePage.verifyIndicator("RED");
    epiUsePage.verifyProductGroup("PG1-Name", 1);
    epiUsePage.enterData(10, 20, 30, 40, 50, "10/2011", 1);
    epiUsePage.verifyIndicator("GREEN");

    epiUsePage.checkApplyNRToAllFields(true);
    epiUsePage.verifyIndicator("GREEN");
    epiUsePage.verifyStockAtFirstOfMonthStatus(false, 1);
    epiUsePage.verifyReceivedStatus(false, 1);
    epiUsePage.verifyDistributedStatus(false, 1);
    epiUsePage.verifyLossStatus(false, 1);
    epiUsePage.verifyStockAtEndOfMonthStatus(false, 1);
    epiUsePage.verifyExpirationDateStatus(false, 1);

    ChildCoveragePage childCoveragePage = epiUsePage.navigateToChildCoverage();
    childCoveragePage.applyNRToAll();
    childCoveragePage.clickOK();

    FullCoveragePage fullCoveragePage = childCoveragePage.navigateToFullCoverage();
    fullCoveragePage.enterData(12, 34, 45, "56");

    AdultCoveragePage adultCoveragePage = fullCoveragePage.navigateToAdultCoverage();
    adultCoveragePage.clickApplyNrToAll();
    adultCoveragePage.clickOK();

    adultCoveragePage.navigateToEpiInventory();
    fillEpiInventoryWithOnlyDeliveredQuantity("2", "4", "6");

    DistributionPage distributionPage = homePage.navigateToDistributionWhenOnline();
    distributionPage.syncDistribution(1);
    assertTrue(distributionPage.getSyncMessage().contains("F10-Village Dispensary"));
    distributionPage.syncDistributionMessageDone();

    distributionPage.clickRecordData(1);
    facilityListPage.selectFacility("F10");
    facilityListPage.verifyOverallFacilityIndicatorColor("BLUE");

    verifyEpiUseDataInDatabase(null, null, null, null, null, null, epiUseData.get(PRODUCT_GROUP_CODE), epiUseData.get(FIRST_FACILITY_CODE));
  }

  @Test(groups = {"distribution"})
  public void testEpiUsePageSyncWhenNRAppliedToFewFields() throws SQLException {
    dbWrapper.insertProductGroup("PG2");
    dbWrapper.insertProductWithGroup("Product7", "ProductName7", "PG2", true);
    dbWrapper.insertProgramProduct("Product7", epiUseData.get(VACCINES_PROGRAM), "10", "true");

    HomePage homePage = loginPage.loginAs(epiUseData.get(USER), epiUseData.get(PASSWORD));
    initiateDistribution(epiUseData.get(FIRST_DELIVERY_ZONE_NAME), epiUseData.get(VACCINES_PROGRAM));
    VisitInformationPage visitInformationPage = facilityListPage.selectFacility(epiUseData.get(FIRST_FACILITY_CODE));
    visitInformationPage.enterDataWhenFacilityVisited("samuel", "Doe", "Verifier", "XYZ");

    EPIUsePage epiUsePage = visitInformationPage.navigateToEpiUse();
    epiUsePage.verifyIndicator("RED");
    epiUsePage.verifyProductGroup("PG1-Name", 1);
    epiUsePage.checkApplyNRToStockAtFirstOfMonth0();
    epiUsePage.verifyIndicator("AMBER");
    epiUsePage.checkApplyNRToReceived0();
    epiUsePage.checkApplyNRToDistributed0();
    epiUsePage.checkApplyNRToLoss0();
    epiUsePage.enterValueInStockAtEndOfMonth("4", 1);
    epiUsePage.enterValueInExpirationDate("12/2031", 1);
    epiUsePage.enterData(10, 20, 30, 40, 50, "10/2011", 2);
    epiUsePage.verifyIndicator("GREEN");

    ChildCoveragePage childCoveragePage = epiUsePage.navigateToChildCoverage();
    childCoveragePage.applyNRToAll();
    childCoveragePage.clickOK();

    FullCoveragePage fullCoveragePage = childCoveragePage.navigateToFullCoverage();
    fullCoveragePage.enterData(12, 34, 45, "56");

    AdultCoveragePage adultCoveragePage = fullCoveragePage.navigateToAdultCoverage();
    adultCoveragePage.clickApplyNrToAll();
    adultCoveragePage.clickOK();

    EpiInventoryPage epiInventoryPage = adultCoveragePage.navigateToEpiInventory();
    fillEpiInventoryWithOnlyDeliveredQuantity("2", "4", "6");
    epiInventoryPage.fillDeliveredQuantity(4, "8");

    DistributionPage distributionPage = homePage.navigateToDistributionWhenOnline();
    distributionPage.syncDistribution(1);
    assertTrue(distributionPage.getSyncMessage().contains("F10-Village Dispensary"));
    distributionPage.syncDistributionMessageDone();

    verifyEpiUseDataInDatabase(null, null, null, null, 4, "12/2031", epiUseData.get(PRODUCT_GROUP_CODE), epiUseData.get(FIRST_FACILITY_CODE));
    verifyEpiUseDataInDatabase(10, 20, 30, 40, 50, "10/2011", "PG2", epiUseData.get(FIRST_FACILITY_CODE));
  }

  @Test(groups = {"distribution"})
  public void shouldDisplayNoProductsAddedMessageOnEpiUsePageWhenNoActiveProducts() throws SQLException {
    dbWrapper.updateFieldValue("products", "active", "false", "code", "P10");
    dbWrapper.updateFieldValue("products", "active", "false", "code", "P11");
    dbWrapper.updateFieldValue("products", "active", "false", "code", "Product6");

    HomePage homePage = loginPage.loginAs(epiUseData.get(USER), epiUseData.get(PASSWORD));
    DistributionPage distributionPage = homePage.navigateToDistributionWhenOnline();
    distributionPage.initiate(epiUseData.get(FIRST_DELIVERY_ZONE_NAME), epiUseData.get(VACCINES_PROGRAM));
    FacilityListPage facilityListPage = distributionPage.clickRecordData(1);
    EPIUsePage epiUsePage = facilityListPage.selectFacility(epiUseData.get(FIRST_FACILITY_CODE)).navigateToEpiUse();

    assertTrue(epiUsePage.getNoProductsAddedMessage().contains("No products added"));
    epiUsePage.verifyIndicator("GREEN");

    dbWrapper.updateFieldValue("products", "active", "true", "code", "P10");
    dbWrapper.updateFieldValue("products", "active", "true", "code", "P11");
    dbWrapper.updateFieldValue("products", "active", "true", "code", "Product6");
  }

  public void initiateDistribution(String deliveryZoneNameFirst, String programFirst) {
    HomePage homePage = PageObjectFactory.getHomePage(testWebDriver);
    DistributionPage distributionPage = homePage.navigateToDistributionWhenOnline();
    distributionPage.selectValueFromDeliveryZone(deliveryZoneNameFirst);
    distributionPage.selectValueFromProgram(programFirst);
    distributionPage.clickInitiateDistribution();
    distributionPage.clickRecordData(1);
  }

  public void fillEpiInventoryWithOnlyDeliveredQuantity(String deliveredQuantity1, String deliveredQuantity2, String deliveredQuantity3) {
    EpiInventoryPage epiInventoryPage = PageObjectFactory.getEpiInventoryPage(testWebDriver);
    epiInventoryPage.applyNRToAll();
    epiInventoryPage.fillDeliveredQuantity(1, deliveredQuantity1);
    epiInventoryPage.fillDeliveredQuantity(2, deliveredQuantity2);
    epiInventoryPage.fillDeliveredQuantity(3, deliveredQuantity3);
  }

  @Then("^I should see product group \"([^\"]*)\"")
  public void verifyProductGroup(String productGroup) {
    epiUsePage = PageObjectFactory.getEpiUsePage(testWebDriver);
    epiUsePage.verifyProductGroup(productGroup, 1);
  }

  @When("^I Enter EPI values without end of month:$")
  public void enterEPIValues(DataTable tableData) {
    Map<String, String> epiData = tableData.asMaps(String.class, String.class).get(0);
    epiUsePage = PageObjectFactory.getEpiUsePage(testWebDriver);
    epiUsePage.enterValueInDistributed(epiData.get("distributed"), 1);
    epiUsePage.enterValueInExpirationDate(epiData.get("expirationDate"), 1);
    epiUsePage.enterValueInLoss(epiData.get("loss"), 1);
    epiUsePage.enterValueInReceived(epiData.get("received"), 1);
    epiUsePage.enterValueInStockAtFirstOfMonth(epiData.get("firstOfMonth"), 1);
  }

  @When("^I verify saved EPI values:$")
  public void verifySavedEPIValues(DataTable tableData) {
    epiUsePage = PageObjectFactory.getEpiUsePage(testWebDriver);
    epiUsePage.navigateToRefrigerators();
    epiUsePage.navigateToEpiUse();
    List<Map<String, String>> epiData = tableData.asMaps(String.class, String.class);
    epiUsePage.verifyData(epiData);
  }

  @And("^I verify total is \"([^\"]*)\"$")
  public void verifyTotalField(String total) {
    epiUsePage = PageObjectFactory.getEpiUsePage(testWebDriver);
    epiUsePage.verifyTotal(total, 1);
  }

  @Then("^Verify indicator should be \"([^\"]*)\"$")
  public void shouldVerifyIndicatorColor(String color) throws SQLException {
    epiUsePage = PageObjectFactory.getEpiUsePage(testWebDriver);
    epiUsePage.verifyIndicator(color);
  }

  @When("^I enter EPI end of month as \"([^\"]*)\"")
  public void enterEPIEndOfMonth(String endOfMonth) throws InterruptedException {
    epiUsePage = PageObjectFactory.getEpiUsePage(testWebDriver);
    epiUsePage.enterValueInStockAtEndOfMonth(endOfMonth, 1);
    epiUsePage.removeFocusFromElement();
  }

  @AfterMethod(groups = "distribution")
  public void tearDown() throws SQLException {
    testWebDriver.sleep(500);
    if (!testWebDriver.getElementById("username").isDisplayed()) {
      HomePage homePage = PageObjectFactory.getHomePage(testWebDriver);
      homePage.logout(baseUrlGlobal);
      dbWrapper.deleteData();
      dbWrapper.closeConnection();
    }
    ((JavascriptExecutor) TestWebDriver.getDriver()).executeScript("indexedDB.deleteDatabase('open_lmis');");
  }

}
