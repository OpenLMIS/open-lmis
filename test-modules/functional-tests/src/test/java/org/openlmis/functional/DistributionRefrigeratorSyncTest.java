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
import java.util.Map;

import static com.thoughtworks.selenium.SeleneseTestBase.assertFalse;
import static com.thoughtworks.selenium.SeleneseTestBase.assertTrue;

public class DistributionRefrigeratorSyncTest extends TestCaseHelper {

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

  public Map<String, String> refrigeratorTestData = new HashMap<String, String>() {{
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

    setupDataForDistributionTest(refrigeratorTestData);
    dbWrapper.insertProductGroup(refrigeratorTestData.get(PRODUCT_GROUP_CODE));
    dbWrapper.insertProductWithGroup("Product5", "ProductName5", refrigeratorTestData.get(PRODUCT_GROUP_CODE), true);
    dbWrapper.insertProductWithGroup("Product6", "ProductName6", refrigeratorTestData.get(PRODUCT_GROUP_CODE), true);
    dbWrapper.insertProgramProduct("Product5", refrigeratorTestData.get(VACCINES_PROGRAM), "10", "false");
    dbWrapper.insertProgramProduct("Product6", refrigeratorTestData.get(VACCINES_PROGRAM), "10", "true");
  }

  @Test(groups = {"distribution"})
  public void testRefrigeratorPageSyncWith2Refrigerators() throws SQLException {
    dbWrapper.addRefrigeratorToFacility("LG", "", "GNR7878", "F10");
    HomePage homePage = loginPage.loginAs(refrigeratorTestData.get(USER), refrigeratorTestData.get(PASSWORD));
    initiateDistribution(refrigeratorTestData.get(FIRST_DELIVERY_ZONE_NAME), refrigeratorTestData.get(VACCINES_PROGRAM));
    VisitInformationPage visitInformationPage = facilityListPage.selectFacility(refrigeratorTestData.get(FIRST_FACILITY_CODE));
    visitInformationPage.enterDataWhenFacilityVisited("samuel", "Doe", "Verifier", "XYZ");
    RefrigeratorPage refrigeratorPage = visitInformationPage.navigateToRefrigerators();

    refrigeratorPage.clickAddNew();
    refrigeratorPage.addNewRefrigerator("TGNR7878");

    refrigeratorPage.clickShowForRefrigerator(1);
    refrigeratorPage.verifyRefrigeratorColor("individual", "RED");
    refrigeratorPage.enterValueInRefrigeratorTemperature("999.9", 1);
    refrigeratorPage.verifyRefrigeratorColor("overall", "AMBER");
    refrigeratorPage.verifyRefrigeratorColor("individual", "AMBER");
    refrigeratorPage.clickFunctioningCorrectlyYesRadio(1);
    refrigeratorPage.enterValueInLowAlarmEvents("1", 1);
    refrigeratorPage.enterValueInHighAlarmEvents("0", 1);
    refrigeratorPage.clickProblemSinceLastVisitDoNotKnowRadio(1);
    refrigeratorPage.enterValueInNotesTextArea("miscellaneous", 1);
    refrigeratorPage.clickDone();
    refrigeratorPage.verifyRefrigeratorColor("individual", "GREEN");
    refrigeratorPage.verifyRefrigeratorColor("overall", "AMBER");

    refrigeratorPage.clickShowForRefrigerator(2);
    refrigeratorPage.enterValueInRefrigeratorTemperature("5", 2);
    refrigeratorPage.clickProblemSinceLastVisitNoRadio(2);
    refrigeratorPage.enterValueInLowAlarmEvents("10", 2);
    refrigeratorPage.enterValueInHighAlarmEvents("05", 2);
    refrigeratorPage.clickFunctioningCorrectlyNoRadio(2);
    refrigeratorPage.selectBurnerProblem(2);
    refrigeratorPage.selectOtherProblem(2);
    refrigeratorPage.enterTextInOtherProblemTextBox("other problem", 2);
    refrigeratorPage.verifyRefrigeratorColor("overall", "GREEN");

    EPIUsePage epiUsePage = refrigeratorPage.navigateToEpiUse();
    epiUsePage.enterData(10, 20, 30, 40, 50, "10/2011", 1);

    ChildCoveragePage childCoveragePage = epiUsePage.navigateToChildCoverage();
    childCoveragePage.applyNRToAll();
    childCoveragePage.clickOK();

    AdultCoveragePage adultCoveragePage = childCoveragePage.navigateToAdultCoverage();
    adultCoveragePage.clickApplyNrToAll();
    adultCoveragePage.clickOK();

    FullCoveragePage fullCoveragePage = adultCoveragePage.navigateToFullCoverage();
    fullCoveragePage.enterData(45, 67, 89, "90");

    fullCoveragePage.navigateToEpiInventory();
    fillEpiInventoryWithOnlyDeliveredQuantity("2", "4", "6");

    DistributionPage distributionPage = homePage.navigateToDistributionWhenOnline();
    distributionPage.syncDistribution(1);
    assertTrue(distributionPage.getSyncMessage().contains("F10-Village Dispensary"));
    distributionPage.syncDistributionMessageDone();

    verifyRefrigeratorReadingDataInDatabase(refrigeratorTestData.get(FIRST_FACILITY_CODE), "GNR7878", 999.9F, "Y", 1, 0, "D", "miscellaneous");
    verifyRefrigeratorDetailsInReadingsTable(refrigeratorTestData.get(FIRST_FACILITY_CODE), "GNR7878", "LG", null);
    verifyRefrigeratorsDataInDatabase(refrigeratorTestData.get(FIRST_FACILITY_CODE), "GNR7878", "LG", null, "t");
    verifyRefrigeratorProblemDataNullInDatabase("GNR7878", refrigeratorTestData.get(FIRST_FACILITY_CODE));
    verifyRefrigeratorReadingDataInDatabase(refrigeratorTestData.get(FIRST_FACILITY_CODE), "TGNR7878", 5F, "N", 10, 5, "N", null);
    verifyRefrigeratorDetailsInReadingsTable(refrigeratorTestData.get(FIRST_FACILITY_CODE), "TGNR7878", null, null);
    verifyRefrigeratorsDataInDatabase(refrigeratorTestData.get(FIRST_FACILITY_CODE), "TGNR7878", null, null, "t");
    verifyRefrigeratorProblemDataInDatabase(refrigeratorTestData.get(FIRST_FACILITY_CODE), "TGNR7878", false, true, false, false, false, true, "other problem");
  }

  @Test(groups = {"distribution"})
  public void testRefrigeratorSyncWhenRefrigeratorHasProblem() throws SQLException {
    dbWrapper.addRefrigeratorToFacility("", "", "GNR7878", "F10");
    HomePage homePage = loginPage.loginAs(refrigeratorTestData.get(USER), refrigeratorTestData.get(PASSWORD));
    initiateDistribution(refrigeratorTestData.get(FIRST_DELIVERY_ZONE_NAME), refrigeratorTestData.get(VACCINES_PROGRAM));
    VisitInformationPage visitInformationPage = facilityListPage.selectFacility(refrigeratorTestData.get(FIRST_FACILITY_CODE));
    visitInformationPage.enterDataWhenFacilityVisited("samuel", "Doe", "Verifier", "XYZ");

    RefrigeratorPage refrigeratorPage = visitInformationPage.navigateToRefrigerators();

    refrigeratorPage.clickShowForRefrigerator(1);
    refrigeratorPage.verifyRefrigeratorColor("individual", "RED");
    refrigeratorPage.enterValueInRefrigeratorTemperature("-999.9", 1);
    refrigeratorPage.clickFunctioningCorrectlyYesRadio(1);
    refrigeratorPage.clickFunctioningCorrectlyNoRadio(1);
    refrigeratorPage.enterValueInLowAlarmEvents("1", 1);
    refrigeratorPage.enterValueInHighAlarmEvents("0", 1);
    refrigeratorPage.clickProblemSinceLastVisitYesRadio(1);
    refrigeratorPage.verifyRefrigeratorColor("overall", "AMBER");
    refrigeratorPage.selectOtherProblem(1);
    refrigeratorPage.verifyRefrigeratorColor("overall", "AMBER");
    refrigeratorPage.verifyRefrigeratorColor("individual", "AMBER");
    refrigeratorPage.enterTextInOtherProblemTextBox("others", 1);
    refrigeratorPage.selectGasLeakProblem(1);
    refrigeratorPage.verifyRefrigeratorColor("overall", "GREEN");
    refrigeratorPage.verifyRefrigeratorColor("individual", "GREEN");
    refrigeratorPage.clickDone();

    EPIUsePage epiUsePage = refrigeratorPage.navigateToEpiUse();
    epiUsePage.enterData(10, 20, 30, 40, 50, "10/2011", 1);

    ChildCoveragePage childCoveragePage = epiUsePage.navigateToChildCoverage();
    childCoveragePage.applyNRToAll();
    childCoveragePage.clickOK();

    FullCoveragePage fullCoveragePage = childCoveragePage.navigateToFullCoverage();
    fullCoveragePage.enterData(67, 44, 22, "11");

    AdultCoveragePage adultCoveragePage = fullCoveragePage.navigateToAdultCoverage();
    adultCoveragePage.clickApplyNrToAll();
    adultCoveragePage.clickOK();

    adultCoveragePage.navigateToEpiInventory();
    fillEpiInventoryWithOnlyDeliveredQuantity("2", "4", "6");

    DistributionPage distributionPage = homePage.navigateToDistributionWhenOnline();
    distributionPage.syncDistribution(1);
    assertTrue(distributionPage.getSyncMessage().contains("F10-Village Dispensary"));
    distributionPage.syncDistributionMessageDone();

    verifyRefrigeratorReadingDataInDatabase(refrigeratorTestData.get(FIRST_FACILITY_CODE), "GNR7878", -999.9F, "N", 1, 0, "Y", null);
    verifyRefrigeratorProblemDataInDatabase(refrigeratorTestData.get(FIRST_FACILITY_CODE), "GNR7878", false, false, true, false, false, true, "others");
    verifyRefrigeratorsDataInDatabase(refrigeratorTestData.get(FIRST_FACILITY_CODE), "GNR7878", null, null, "t");
    verifyRefrigeratorDetailsInReadingsTable(refrigeratorTestData.get(FIRST_FACILITY_CODE), "GNR7878", null, null);
  }

  @Test(groups = {"distribution"})
  public void testRefrigeratorSyncWhenProblemIsSelectedAndAppliedNRBeforeSync() throws SQLException {
    HomePage homePage = loginPage.loginAs(refrigeratorTestData.get(USER), refrigeratorTestData.get(PASSWORD));
    initiateDistribution(refrigeratorTestData.get(FIRST_DELIVERY_ZONE_NAME), refrigeratorTestData.get(VACCINES_PROGRAM));
    VisitInformationPage visitInformationPage = facilityListPage.selectFacility(refrigeratorTestData.get(FIRST_FACILITY_CODE));
    visitInformationPage.enterDataWhenFacilityVisited("samuel", "Doe", "Verifier", "XYZ");

    RefrigeratorPage refrigeratorPage = visitInformationPage.navigateToRefrigerators();
    refrigeratorPage.clickAddNew();
    refrigeratorPage.addNewRefrigerator("LG", "800L", "GNR7878");

    refrigeratorPage.clickShowForRefrigerator(1);
    refrigeratorPage.verifyRefrigeratorColor("individual", "RED");
    refrigeratorPage.applyNRToRefrigeratorTemperature(1);
    refrigeratorPage.clickProblemSinceLastVisitNoRadio(1);
    refrigeratorPage.clickProblemSinceLastVisitNR(1);
    refrigeratorPage.enterValueInLowAlarmEvents("1", 1);
    refrigeratorPage.applyNRToLowAlarmEvent(1);
    refrigeratorPage.applyNRToHighAlarmEvent(1);
    refrigeratorPage.verifyRefrigeratorColor("overall", "AMBER");
    refrigeratorPage.clickFunctioningCorrectlyNoRadio(1);
    refrigeratorPage.selectGasLeakProblem(1);
    refrigeratorPage.clickFunctioningCorrectlyNR(1);

    refrigeratorPage.verifyFieldsDisabledWhenAllNRSelected();

    refrigeratorPage.verifyRefrigeratorColor("overall", "GREEN");
    refrigeratorPage.verifyRefrigeratorColor("individual", "GREEN");
    refrigeratorPage.clickDone();

    EPIUsePage epiUsePage = refrigeratorPage.navigateToEpiUse();
    epiUsePage.enterData(10, 20, 30, 40, 50, "10/2011", 1);

    ChildCoveragePage childCoveragePage = epiUsePage.navigateToChildCoverage();
    childCoveragePage.applyNRToAll();
    childCoveragePage.clickOK();

    AdultCoveragePage adultCoveragePage = childCoveragePage.navigateToAdultCoverage();
    adultCoveragePage.clickApplyNrToAll();
    adultCoveragePage.clickOK();

    FullCoveragePage fullCoveragePage = adultCoveragePage.navigateToFullCoverage();
    fullCoveragePage.enterData(77, 56, 78, "34");

    fullCoveragePage.navigateToEpiInventory();
    fillEpiInventoryWithOnlyDeliveredQuantity("2", "4", "6");

    DistributionPage distributionPage = homePage.navigateToDistributionWhenOnline();
    distributionPage.syncDistribution(1);
    assertTrue(distributionPage.getSyncMessage().contains("F10-Village Dispensary"));
    distributionPage.syncDistributionMessageDone();

    verifyRefrigeratorReadingDataInDatabase(refrigeratorTestData.get(FIRST_FACILITY_CODE), "GNR7878", null, null, null, null, null, null);
    verifyRefrigeratorProblemDataNullInDatabase("GNR7878", refrigeratorTestData.get(FIRST_FACILITY_CODE));
    verifyRefrigeratorDetailsInReadingsTable(refrigeratorTestData.get(FIRST_FACILITY_CODE), "GNR7878", "LG", "800L");
    verifyRefrigeratorsDataInDatabase(refrigeratorTestData.get(FIRST_FACILITY_CODE), "GNR7878", "LG", "800L", "t");
  }

  @Test(groups = {"distribution"})
  public void testRefrigeratorSyncWhenRefrigeratorIsDeletedBeforeSync() throws SQLException {
    HomePage homePage = loginPage.loginAs(refrigeratorTestData.get(USER), refrigeratorTestData.get(PASSWORD));
    initiateDistribution(refrigeratorTestData.get(FIRST_DELIVERY_ZONE_NAME), refrigeratorTestData.get(VACCINES_PROGRAM));
    VisitInformationPage visitInformationPage = facilityListPage.selectFacility(refrigeratorTestData.get(FIRST_FACILITY_CODE));
    visitInformationPage.enterDataWhenFacilityVisited("samuel", "Doe", "Verifier", "XYZ");

    RefrigeratorPage refrigeratorPage = visitInformationPage.navigateToRefrigerators();
    refrigeratorPage.clickAddNew();
    refrigeratorPage.addNewRefrigerator("GNR7878");

    refrigeratorPage.clickShowForRefrigerator(1);
    refrigeratorPage.verifyRefrigeratorColor("individual", "RED");
    refrigeratorPage.enterValueInRefrigeratorTemperature("3", 1);
    refrigeratorPage.clickFunctioningCorrectlyNoRadio(1);
    refrigeratorPage.clickFunctioningCorrectlyYesRadio(1);
    refrigeratorPage.enterValueInLowAlarmEvents("1", 1);
    refrigeratorPage.enterValueInHighAlarmEvents("0", 1);
    refrigeratorPage.clickProblemSinceLastVisitNoRadio(1);

    refrigeratorPage.verifyRefrigeratorColor("overall", "GREEN");
    refrigeratorPage.verifyRefrigeratorColor("individual", "GREEN");
    refrigeratorPage.clickDone();

    EPIUsePage epiUsePage = refrigeratorPage.navigateToEpiUse();
    epiUsePage.enterData(10, 20, 30, 40, 50, "10/2011", 1);

    ChildCoveragePage childCoveragePage = epiUsePage.navigateToChildCoverage();
    childCoveragePage.applyNRToAll();
    childCoveragePage.clickOK();

    AdultCoveragePage adultCoveragePage = childCoveragePage.navigateToAdultCoverage();
    adultCoveragePage.clickApplyNrToAll();
    adultCoveragePage.clickOK();

    FullCoveragePage fullCoveragePage = adultCoveragePage.navigateToFullCoverage();
    fullCoveragePage.enterData(78, 67, 34, "12");

    fullCoveragePage.navigateToEpiInventory();
    fillEpiInventoryWithOnlyDeliveredQuantity("2", "4", "6");

    DistributionPage distributionPage = homePage.navigateToDistributionWhenOnline();
    distributionPage.syncDistribution(1);
    assertTrue(distributionPage.getSyncMessage().contains("F10-Village Dispensary"));
    distributionPage.syncDistributionMessageDone();

    initiateDistributionForPeriod("Period13");
    distributionPage.clickRecordData(2);
    facilityListPage.selectFacility(refrigeratorTestData.get(FIRST_FACILITY_CODE));
    facilityListPage.verifyOverallFacilityIndicatorColor("RED");
    visitInformationPage.navigateToRefrigerators();
    refrigeratorPage.verifyRefrigeratorColor("overall", "RED");

    refrigeratorPage.clickShowForRefrigerator(1);
    refrigeratorPage.verifyRefrigeratorColor("individual", "RED");
    refrigeratorPage.enterValueInRefrigeratorTemperature("3", 1);
    refrigeratorPage.clickFunctioningCorrectlyNoRadio(1);
    refrigeratorPage.enterValueInLowAlarmEvents("2", 1);
    refrigeratorPage.enterValueInHighAlarmEvents("2", 1);
    refrigeratorPage.clickProblemSinceLastVisitYesRadio(1);
    refrigeratorPage.verifyRefrigeratorColor("overall", "AMBER");
    refrigeratorPage.selectOtherProblem(1);
    refrigeratorPage.enterTextInOtherProblemTextBox("others", 1);
    testWebDriver.sleep(500);
    refrigeratorPage.verifyRefrigeratorColor("overall", "GREEN");
    refrigeratorPage.verifyRefrigeratorColor("individual", "GREEN");
    refrigeratorPage.clickDone();

    refrigeratorPage.clickDelete();
    refrigeratorPage.clickOKButton();

    refrigeratorPage.navigateToEpiUse();
    epiUsePage.enterData(10, 20, 30, 40, 50, "10/2011", 1);

    epiUsePage.navigateToChildCoverage();
    childCoveragePage.applyNRToAll();
    childCoveragePage.clickOK();

    childCoveragePage.navigateToAdultCoverage();
    adultCoveragePage.clickApplyNrToAll();
    adultCoveragePage.clickOK();

    adultCoveragePage.navigateToVisitInformation();
    visitInformationPage.enterDataWhenFacilityVisited("samuel", "Doe", "Verifier", "XYZ");

    visitInformationPage.navigateToFullCoverage();
    fullCoveragePage.enterData(78, 67, 34, "12");

    fullCoveragePage.navigateToEpiInventory();
    fillEpiInventoryWithOnlyDeliveredQuantity("2", "4", "6");

    homePage.navigateToDistributionWhenOnline();
    distributionPage.syncDistribution(2);
    assertTrue(distributionPage.getSyncMessage().contains("F10-Village Dispensary"));
    distributionPage.syncDistributionMessageDone();

    verifyRefrigeratorDetailsInReadingsTable(refrigeratorTestData.get(FIRST_FACILITY_CODE), "GNR7878", null, null);
    verifyRefrigeratorsDataInDatabase(refrigeratorTestData.get(FIRST_FACILITY_CODE), "GNR7878", null, null, "f");
  }

  @Test(groups = {"distribution"})
  public void testAddingDuplicateRefrigeratorForSameFacility() throws SQLException {
    dbWrapper.addRefrigeratorToFacility("LG", "800L", "GNR7878", "F10");
    loginPage.loginAs(refrigeratorTestData.get(USER), refrigeratorTestData.get(PASSWORD));
    initiateDistribution(refrigeratorTestData.get(FIRST_DELIVERY_ZONE_NAME), refrigeratorTestData.get(VACCINES_PROGRAM));
    RefrigeratorPage refrigeratorPage = facilityListPage.selectFacility(refrigeratorTestData.get(FIRST_FACILITY_CODE)).navigateToRefrigerators();

    refrigeratorPage.clickAddNew();
    refrigeratorPage.addNewRefrigerator("LG", "800L1", "GNR7878");
    refrigeratorPage.verifyDuplicateErrorMessage("Duplicate Identifier / Serial number");
  }

  @Test(groups = {"distribution"})
  public void testAddingDuplicateRefrigeratorForDifferentFacility() throws SQLException {
    dbWrapper.addRefrigeratorToFacility("", "800L", "GNR7878", "F10");
    HomePage homePage = loginPage.loginAs(refrigeratorTestData.get(USER), refrigeratorTestData.get(PASSWORD));
    initiateDistribution(refrigeratorTestData.get(FIRST_DELIVERY_ZONE_NAME), refrigeratorTestData.get(VACCINES_PROGRAM));
    VisitInformationPage visitInformationPage = facilityListPage.selectFacility(refrigeratorTestData.get(SECOND_FACILITY_CODE));
    visitInformationPage.enterDataWhenFacilityVisited("samuel", "Doe", "Verifier", "XYZ");

    RefrigeratorPage refrigeratorPage = visitInformationPage.navigateToRefrigerators();

    refrigeratorPage.clickAddNew();
    refrigeratorPage.addNewRefrigerator("LG22", "800L22", null);
    assertFalse(refrigeratorPage.isDoneButtonEnabled());
    refrigeratorPage.addNewRefrigerator("GNR7878");
    refrigeratorPage.clickShowForRefrigerator(1);
    refrigeratorPage.enterValueInRefrigeratorTemperature("3", 1);
    refrigeratorPage.clickFunctioningCorrectlyYesRadio(1);
    refrigeratorPage.enterValueInLowAlarmEvents("2", 1);
    refrigeratorPage.enterValueInHighAlarmEvents("2", 1);
    refrigeratorPage.clickProblemSinceLastVisitNR(1);
    refrigeratorPage.verifyRefrigeratorColor("overall", "GREEN");
    refrigeratorPage.verifyRefrigeratorColor("individual", "GREEN");
    refrigeratorPage.clickDone();

    EPIUsePage epiUsePage = refrigeratorPage.navigateToEpiUse();
    epiUsePage.enterData(10, 20, 30, 40, 50, "10/2011", 1);

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

    assertTrue(distributionPage.getSyncMessage().contains("F11-Central Hospital"));

    distributionPage.syncDistributionMessageDone();

    verifyRefrigeratorReadingDataInDatabase(refrigeratorTestData.get(SECOND_FACILITY_CODE), "GNR7878", 3.0f, "Y", 2, 2, null, null);
    verifyRefrigeratorProblemDataNullInDatabase("GNR7878", refrigeratorTestData.get(SECOND_FACILITY_CODE));
    verifyRefrigeratorsDataInDatabase("F11", "GNR7878", "LG22", "800L22", "t");
  }

  @Test(groups = {"distribution"})
  public void testUpdatingRefrigeratorAndSync() throws SQLException {
    dbWrapper.addRefrigeratorToFacility("LG", "800L", "GNR7878", "F10");
    HomePage homePage = loginPage.loginAs(refrigeratorTestData.get(USER), refrigeratorTestData.get(PASSWORD));
    initiateDistribution(refrigeratorTestData.get(FIRST_DELIVERY_ZONE_NAME), refrigeratorTestData.get(VACCINES_PROGRAM));
    VisitInformationPage visitInformationPage = facilityListPage.selectFacility(refrigeratorTestData.get(FIRST_FACILITY_CODE));
    visitInformationPage.enterDataWhenFacilityVisited("samuel", "Doe", "Verifier", "XYZ");

    RefrigeratorPage refrigeratorPage = visitInformationPage.navigateToRefrigerators();

    refrigeratorPage.clickDelete();
    refrigeratorPage.clickOKButton();

    facilityListPage = PageObjectFactory.getFacilityListPage(testWebDriver);
    facilityListPage.verifyOverallFacilityIndicatorColor("AMBER");

    refrigeratorPage.clickAddNew();
    refrigeratorPage.addNewRefrigerator("LG", "800L1", "GNR7878");
    refrigeratorPage.verifyRefrigeratorColor("overall", "RED");
    refrigeratorPage.clickShowForRefrigerator(1);
    refrigeratorPage.verifyRefrigeratorColor("individual", "RED");
    refrigeratorPage.enterValueInRefrigeratorTemperature("3", 1);
    refrigeratorPage.clickFunctioningCorrectlyYesRadio(1);
    refrigeratorPage.enterValueInLowAlarmEvents("2", 1);
    refrigeratorPage.enterValueInHighAlarmEvents("2", 1);
    refrigeratorPage.clickProblemSinceLastVisitNR(1);
    testWebDriver.sleep(1000);
    refrigeratorPage.verifyRefrigeratorColor("overall", "GREEN");
    refrigeratorPage.verifyRefrigeratorColor("individual", "GREEN");
    refrigeratorPage.clickDone();

    EPIUsePage epiUsePage = refrigeratorPage.navigateToEpiUse();
    epiUsePage.enterData(10, 20, 30, 40, 50, "10/2011", 1);

    ChildCoveragePage childCoveragePage = epiUsePage.navigateToChildCoverage();
    childCoveragePage.applyNRToAll();
    childCoveragePage.clickOK();

    AdultCoveragePage adultCoveragePage = childCoveragePage.navigateToAdultCoverage();
    adultCoveragePage.clickApplyNrToAll();
    adultCoveragePage.clickOK();

    FullCoveragePage fullCoveragePage = adultCoveragePage.navigateToFullCoverage();
    fullCoveragePage.enterData(67, 8, 33, "54");

    fullCoveragePage.navigateToEpiInventory();
    fillEpiInventoryWithOnlyDeliveredQuantity("2", "4", "6");

    DistributionPage distributionPage = homePage.navigateToDistributionWhenOnline();
    distributionPage.syncDistribution(1);
    assertTrue(distributionPage.getSyncMessage().contains("F10-Village Dispensary"));
    distributionPage.syncDistributionMessageDone();

    verifyRefrigeratorReadingDataInDatabase(refrigeratorTestData.get(FIRST_FACILITY_CODE), "GNR7878", 3.0f, "Y", 2, 2, null, null);
    verifyRefrigeratorProblemDataNullInDatabase("GNR7878", refrigeratorTestData.get(FIRST_FACILITY_CODE));
    verifyRefrigeratorsDataInDatabase("F10", "GNR7878", "LG", "800L1", "t");
  }

  private void initiateDistributionForPeriod(String periodName) {
    DistributionPage distributionPage = PageObjectFactory.getDistributionPage(testWebDriver);
    distributionPage.selectValueFromDeliveryZone(refrigeratorTestData.get(FIRST_DELIVERY_ZONE_NAME));
    distributionPage.selectValueFromProgram(refrigeratorTestData.get(VACCINES_PROGRAM));
    distributionPage.selectValueFromPeriod(periodName);
    distributionPage.clickInitiateDistribution();
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
