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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.thoughtworks.selenium.SeleneseTestBase.assertTrue;
import static java.lang.String.valueOf;

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
  public void setUp() throws Exception {
    super.setup();

    Map<String, String> dataMap = refrigeratorTestData;

    setupDataForDistributionTest(dataMap.get(USER), dataMap.get(FIRST_DELIVERY_ZONE_CODE), dataMap.get(SECOND_DELIVERY_ZONE_CODE),
      dataMap.get(FIRST_DELIVERY_ZONE_NAME), dataMap.get(SECOND_DELIVERY_ZONE_NAME), dataMap.get(FIRST_FACILITY_CODE),
      dataMap.get(SECOND_FACILITY_CODE), dataMap.get(VACCINES_PROGRAM), dataMap.get(TB_PROGRAM), dataMap.get(SCHEDULE),
      dataMap.get(PRODUCT_GROUP_CODE));
  }

  @Test(groups = {"distribution"})
  public void testRefrigeratorPageSyncWith2Refrigerators() throws Exception {

    HomePage homePage = new HomePage(testWebDriver);
    dbWrapper.addRefrigeratorToFacility("LG", "800L1", "TGNR7878", "F10");
    initiateDistributionAndVerifyStatusColor();

    RefrigeratorPage refrigeratorPage = new RefrigeratorPage(testWebDriver);
    refrigeratorPage.clickShowForRefrigerator1();
    refrigeratorPage.verifyRefrigeratorColor("individual", "RED");
    refrigeratorPage.enterValueInRefrigeratorTemperature("999.9");
    refrigeratorPage.verifyRefrigeratorColor("overall", "AMBER");
    refrigeratorPage.verifyRefrigeratorColor("individual", "AMBER");
    refrigeratorPage.clickFunctioningCorrectlyYesRadio();
    refrigeratorPage.enterValueInLowAlarmEvents("1");
    refrigeratorPage.enterValueInHighAlarmEvents("0");
    refrigeratorPage.clickProblemSinceLastVisitDontKnowRadio();
    refrigeratorPage.enterValueInNotesTextArea("miscellaneous");
    refrigeratorPage.clickDone();
    refrigeratorPage.verifyRefrigeratorColor("individual", "GREEN");
    refrigeratorPage.verifyRefrigeratorColor("overall", "AMBER");

    refrigeratorPage.clickShowForRefrigerator2();
    refrigeratorPage.enterValueInRefrigeratorTemperatureForSecondRefrigerator("5");
    refrigeratorPage.clickFunctioningCorrectlyNoRadioForSecondRefrigerator();
    refrigeratorPage.enterValueInLowAlarmEventsForSecondRefrigerator("10");
    refrigeratorPage.enterValueInHighAlarmEventsForSecondRefrigerator("05");
    refrigeratorPage.clickProblemSinceLastVisitNoRadioForSecondRefrigerator();
    refrigeratorPage.verifyRefrigeratorColor("individual", "GREEN");
    refrigeratorPage.verifyRefrigeratorColor("overall", "GREEN");
    //TODO check why it is unable to find done button for second refrigerator when Done buttons for both refrigerator have same ID
    //refrigeratorPage.clickDone();

    enterDataInEpiUsePage(10, 20, 30, 40, 50, "10/2011", 1);
    enterDataInGeneralObservationsPage("some observations", "samuel", "Doe", "Verifier", "XYZ");
    DistributionPage distributionPage = homePage.navigatePlanDistribution();
    distributionPage.syncDistribution(1);
    assertTrue(distributionPage.getSyncMessage().contains("F10-Village Dispensary"));
    distributionPage.syncDistributionMessageDone();

    verifyRefrigeratorReadingDataInDatabase(refrigeratorTestData.get(FIRST_FACILITY_CODE), "GNR7878", 999.9F, "Y", 1, 0, "D", "miscellaneous");
    verifyRefrigeratorProblemDataNullInDatabase("GNR7878", refrigeratorTestData.get(FIRST_FACILITY_CODE));

    verifyRefrigeratorReadingDataInDatabase(refrigeratorTestData.get(FIRST_FACILITY_CODE), "TGNR7878", 5F, "N", 10, 5, "N", null);
    verifyRefrigeratorProblemDataNullInDatabase("TGNR7878", refrigeratorTestData.get(FIRST_FACILITY_CODE));
  }

  @Test(groups = {"distribution"})
  public void testRefrigeratorSyncWhenRefrigeratorHasProblem() throws Exception {

    HomePage homePage = new HomePage(testWebDriver);
    initiateDistributionAndVerifyStatusColor();

    RefrigeratorPage refrigeratorPage = new RefrigeratorPage(testWebDriver);
    refrigeratorPage.clickShowForRefrigerator1();
    refrigeratorPage.verifyRefrigeratorColor("individual", "RED");
    refrigeratorPage.enterValueInRefrigeratorTemperature("-999.9");
    refrigeratorPage.clickFunctioningCorrectlyYesRadio();
    refrigeratorPage.clickFunctioningCorrectlyNoRadio();
    refrigeratorPage.enterValueInLowAlarmEvents("1");
    refrigeratorPage.enterValueInHighAlarmEvents("0");
    refrigeratorPage.clickProblemSinceLastVisitYesRadio();
    refrigeratorPage.verifyRefrigeratorColor("overall", "AMBER");
    refrigeratorPage.selectOtherProblem();
    refrigeratorPage.verifyRefrigeratorColor("overall", "GREEN");
    refrigeratorPage.verifyRefrigeratorColor("individual", "GREEN");
    refrigeratorPage.enterTextInOtherProblemTextBox("others");
    refrigeratorPage.selectGasLeakProblem();
    refrigeratorPage.clickDone();

    enterDataInEpiUsePage(10, 20, 30, 40, 50, "10/2011", 1);
    enterDataInGeneralObservationsPage("some observations", "samuel", "Doe", "Verifier", "XYZ");
    DistributionPage distributionPage = homePage.navigatePlanDistribution();
    distributionPage.syncDistribution(1);
    assertTrue(distributionPage.getSyncMessage().contains("F10-Village Dispensary"));
    distributionPage.syncDistributionMessageDone();

    verifyRefrigeratorReadingDataInDatabase(refrigeratorTestData.get(FIRST_FACILITY_CODE), "GNR7878", -999.9F, "N", 1, 0, "Y", null);
    verifyRefrigeratorProblemDataInDatabase(refrigeratorTestData.get(FIRST_FACILITY_CODE), "GNR7878", false, false, true, false, false, true, "others");
  }

  @Test(groups = {"distribution"})
  public void testRefrigeratorSyncWhenProblemIsSelectedAndAppliedNRBeforeSync() throws Exception {

    HomePage homePage = new HomePage(testWebDriver);
    initiateDistributionAndVerifyStatusColor();

    RefrigeratorPage refrigeratorPage = new RefrigeratorPage(testWebDriver);
    refrigeratorPage.clickShowForRefrigerator1();
    refrigeratorPage.verifyRefrigeratorColor("individual", "RED");
    refrigeratorPage.applyNRToRefrigeratorTemperature();
    refrigeratorPage.clickFunctioningCorrectlyYesRadio();
    refrigeratorPage.clickFunctioningCorrectlyNR();
    refrigeratorPage.enterValueInLowAlarmEvents("1");
    refrigeratorPage.applyNRToLowAlarmEvent();
    refrigeratorPage.applyNRToHighAlarmEvent();
    refrigeratorPage.verifyRefrigeratorColor("overall", "AMBER");
    refrigeratorPage.clickProblemSinceLastVisitYesRadio();
    refrigeratorPage.selectGasLeakProblem();
    refrigeratorPage.clickProblemSinceLastVisitNR();

    refrigeratorPage.verifyFieldsDisabledWhenAllNRSelected();

    refrigeratorPage.verifyRefrigeratorColor("overall", "GREEN");
    refrigeratorPage.verifyRefrigeratorColor("individual", "GREEN");
    refrigeratorPage.clickDone();

    enterDataInEpiUsePage(10, 20, 30, 40, 50, "10/2011", 1);
    enterDataInGeneralObservationsPage("some observations", "samuel", "Doe", "Verifier", "XYZ");
    DistributionPage distributionPage = homePage.navigatePlanDistribution();
    distributionPage.syncDistribution(1);
    assertTrue(distributionPage.getSyncMessage().contains("F10-Village Dispensary"));
    distributionPage.syncDistributionMessageDone();

    verifyRefrigeratorReadingDataInDatabase(refrigeratorTestData.get(FIRST_FACILITY_CODE), "GNR7878", null, null, null, null, null, null);
    verifyRefrigeratorProblemDataNullInDatabase("GNR7878", refrigeratorTestData.get(FIRST_FACILITY_CODE));
  }

  @Test(groups = {"distribution"})
  public void testRefrigeratorSyncWhenRefrigeratorIsDeletedBeforeSync() throws Exception {

    HomePage homePage = new HomePage(testWebDriver);
    initiateDistributionAndVerifyStatusColor();

    RefrigeratorPage refrigeratorPage = new RefrigeratorPage(testWebDriver);
    refrigeratorPage.clickShowForRefrigerator1();
    refrigeratorPage.verifyRefrigeratorColor("individual", "RED");
    refrigeratorPage.enterValueInRefrigeratorTemperature("3");
    refrigeratorPage.clickFunctioningCorrectlyYesRadio();
    refrigeratorPage.clickFunctioningCorrectlyNoRadio();
    refrigeratorPage.enterValueInLowAlarmEvents("1");
    refrigeratorPage.enterValueInHighAlarmEvents("0");
    refrigeratorPage.clickProblemSinceLastVisitNoRadio();

    refrigeratorPage.verifyRefrigeratorColor("overall", "GREEN");
    refrigeratorPage.verifyRefrigeratorColor("individual", "GREEN");
    refrigeratorPage.clickDone();

    enterDataInEpiUsePage(10, 20, 30, 40, 50, "10/2011", 1);
    enterDataInGeneralObservationsPage("some observations", "samuel", "Doe", "Verifier", "XYZ");
    DistributionPage distributionPage = homePage.navigatePlanDistribution();
    distributionPage.syncDistribution(1);
    assertTrue(distributionPage.getSyncMessage().contains("F10-Village Dispensary"));
    distributionPage.syncDistributionMessageDone();

    initiateDistributionForPeriod("Period13");
    distributionPage.clickRecordData(2);
    FacilityListPage facilityListPage = new FacilityListPage(testWebDriver);
    facilityListPage.selectFacility(refrigeratorTestData.get(FIRST_FACILITY_CODE));
    facilityListPage.verifyFacilityIndicatorColor("Overall", "RED");
    refrigeratorPage.verifyRefrigeratorColor("overall", "RED");

    refrigeratorPage.clickShowForRefrigerator1();
    refrigeratorPage.verifyRefrigeratorColor("individual", "RED");
    refrigeratorPage.enterValueInRefrigeratorTemperature("3");
    refrigeratorPage.clickFunctioningCorrectlyYesRadio();
    refrigeratorPage.enterValueInLowAlarmEvents("2");
    refrigeratorPage.enterValueInHighAlarmEvents("2");
    refrigeratorPage.clickProblemSinceLastVisitYesRadio();
    refrigeratorPage.verifyRefrigeratorColor("overall", "AMBER");
    refrigeratorPage.selectOtherProblem();
    refrigeratorPage.verifyRefrigeratorColor("overall", "GREEN");
    refrigeratorPage.verifyRefrigeratorColor("individual", "GREEN");
    refrigeratorPage.clickDone();

    refrigeratorPage.clickDelete();
    refrigeratorPage.clickOKButton();

    enterDataInEpiUsePage(10, 20, 30, 40, 50, "10/2011", 1);
    enterDataInGeneralObservationsPage("some observations", "samuel", "Doe", "Verifier", "XYZ");
    homePage.navigatePlanDistribution();
    distributionPage.syncDistribution(2);
    assertTrue(distributionPage.getSyncMessage().contains("F10-Village Dispensary"));
    distributionPage.syncDistributionMessageDone();

    verifyRefrigeratorsDataInDatabase("F10", "GNR7878", "LG", "800L", "f");

  }

  @Test(groups = {"distribution"})
  public void testAddingDuplicateRefrigeratorForSameFacility() throws Exception {

    HomePage homePage = new HomePage(testWebDriver);
    initiateDistributionAndVerifyStatusColor();

    RefrigeratorPage refrigeratorPage = new RefrigeratorPage(testWebDriver);
    refrigeratorPage.clickAddNew();
    refrigeratorPage.addNewRefrigerator("LG", "800L1", "GNR7878");
    refrigeratorPage.verifyDuplicateErrorMessage("Duplicate Manufacturer Serial Number");

  }

  @Test(groups = {"distribution"})
  public void testAddingDuplicateRefrigeratorForDifferentFacility() throws Exception {
    LoginPage loginPage = new LoginPage(testWebDriver, baseUrlGlobal);
    HomePage homePage = loginPage.loginAs(refrigeratorTestData.get(USER), refrigeratorTestData.get(PASSWORD));
    initiateDistribution(refrigeratorTestData.get(FIRST_DELIVERY_ZONE_NAME), refrigeratorTestData.get(VACCINES_PROGRAM));
    FacilityListPage facilityListPage = new FacilityListPage(testWebDriver);
    facilityListPage.selectFacility(refrigeratorTestData.get(SECOND_FACILITY_CODE));
    RefrigeratorPage refrigeratorPage = new RefrigeratorPage(testWebDriver);
    refrigeratorPage.clickAddNew();
    refrigeratorPage.addNewRefrigerator("LG22", "800L22", "GNR7878");

    refrigeratorPage.clickShowForRefrigerator1();
    refrigeratorPage.enterValueInRefrigeratorTemperature("3");
    refrigeratorPage.clickFunctioningCorrectlyYesRadio();
    refrigeratorPage.enterValueInLowAlarmEvents("2");
    refrigeratorPage.enterValueInHighAlarmEvents("2");
    refrigeratorPage.clickProblemSinceLastVisitNR();
    refrigeratorPage.verifyRefrigeratorColor("overall", "GREEN");
    refrigeratorPage.verifyRefrigeratorColor("individual", "GREEN");
    refrigeratorPage.clickDone();

    enterDataInEpiUsePage(10, 20, 30, 40, 50, "10/2011", 1);
    enterDataInGeneralObservationsPage("some observations", "samuel", "Doe", "Verifier", "XYZ");
    DistributionPage distributionPage = homePage.navigatePlanDistribution();
    distributionPage.syncDistribution(1);
    assertTrue(distributionPage.getSyncMessage().contains("F11-Central Hospital"));
    distributionPage.syncDistributionMessageDone();

    verifyRefrigeratorReadingDataInDatabase(refrigeratorTestData.get(SECOND_FACILITY_CODE), "GNR7878", 3.0f, "Y", 2, 2, null, null);
    verifyRefrigeratorProblemDataNullInDatabase("GNR7878", refrigeratorTestData.get(SECOND_FACILITY_CODE));

    verifyRefrigeratorsDataInDatabase("F11", "GNR7878", "LG22", "800L22", "t");
  }

  @Test(groups = {"distribution"})
  public void testUpdatingRefrigeratorAndSync() throws Exception {
    HomePage homePage = new HomePage(testWebDriver);
    initiateDistributionAndVerifyStatusColor();

    RefrigeratorPage refrigeratorPage = new RefrigeratorPage(testWebDriver);
    refrigeratorPage.clickDelete();
    refrigeratorPage.clickOKButton();

    FacilityListPage facilityListPage = new FacilityListPage(testWebDriver);
    facilityListPage.verifyFacilityIndicatorColor("overall", "AMBER");

    refrigeratorPage.clickAddNew();
    refrigeratorPage.addNewRefrigerator("LG", "800L1", "GNR7878");
    refrigeratorPage.verifyRefrigeratorColor("overall", "RED");
    refrigeratorPage.clickShowForRefrigerator1();
    refrigeratorPage.verifyRefrigeratorColor("individual", "RED");
    refrigeratorPage.enterValueInRefrigeratorTemperature("3");
    refrigeratorPage.clickFunctioningCorrectlyYesRadio();
    refrigeratorPage.enterValueInLowAlarmEvents("2");
    refrigeratorPage.enterValueInHighAlarmEvents("2");
    refrigeratorPage.clickProblemSinceLastVisitNR();
    refrigeratorPage.verifyRefrigeratorColor("overall", "GREEN");
    refrigeratorPage.verifyRefrigeratorColor("individual", "GREEN");
    refrigeratorPage.clickDone();

    enterDataInEpiUsePage(10, 20, 30, 40, 50, "10/2011", 1);
    enterDataInGeneralObservationsPage("some observations", "samuel", "Doe", "Verifier", "XYZ");
    DistributionPage distributionPage = homePage.navigatePlanDistribution();
    distributionPage.syncDistribution(1);
    assertTrue(distributionPage.getSyncMessage().contains("F10-Village Dispensary"));
    distributionPage.syncDistributionMessageDone();

    verifyRefrigeratorReadingDataInDatabase(refrigeratorTestData.get(FIRST_FACILITY_CODE), "GNR7878", 3.0f, "Y", 2, 2, null, null);
    verifyRefrigeratorProblemDataNullInDatabase("GNR7878", refrigeratorTestData.get(FIRST_FACILITY_CODE));
    verifyRefrigeratorsDataInDatabase("F10", "GNR7878", "LG", "800L1", "t");

  }

  private void initiateDistributionForPeriod(String periodName) throws IOException {
    DistributionPage distributionPage = new DistributionPage(testWebDriver);
    distributionPage.selectValueFromDeliveryZone(refrigeratorTestData.get(FIRST_DELIVERY_ZONE_NAME));
    distributionPage.selectValueFromProgram(refrigeratorTestData.get(VACCINES_PROGRAM));
    distributionPage.selectValueFromPeriod(periodName);
    distributionPage.clickInitiateDistribution();
  }

  public void setupDataForDistributionTest(String userSIC, String deliveryZoneCodeFirst, String deliveryZoneCodeSecond,
                                           String deliveryZoneNameFirst, String deliveryZoneNameSecond,
                                           String facilityCodeFirst, String facilityCodeSecond,
                                           String programFirst, String programSecond, String schedule, String productGroupCode) throws Exception {
    List<String> rightsList = new ArrayList<>();
    rightsList.add("MANAGE_DISTRIBUTION");
    setupTestDataToInitiateRnRAndDistribution(facilityCodeFirst, facilityCodeSecond, true, programFirst, userSIC, "200", rightsList, programSecond,
      "District1", "Ngorongoro", "Ngorongoro");
    setupDataForDeliveryZone(true, deliveryZoneCodeFirst, deliveryZoneCodeSecond, deliveryZoneNameFirst, deliveryZoneNameSecond,
      facilityCodeFirst, facilityCodeSecond, programFirst, programSecond, schedule);
    dbWrapper.insertRoleAssignmentForDistribution(userSIC, "store in-charge", deliveryZoneCodeFirst);
    dbWrapper.insertRoleAssignmentForDistribution(userSIC, "store in-charge", deliveryZoneCodeSecond);
    dbWrapper.insertProductGroup(productGroupCode);
    dbWrapper.insertProductWithGroup("Product5", "ProductName5", productGroupCode, true);
    dbWrapper.insertProductWithGroup("Product6", "ProductName6", productGroupCode, true);
    dbWrapper.insertProgramProduct("Product5", programFirst, "10", "false");
    dbWrapper.insertProgramProduct("Product6", programFirst, "10", "true");
    dbWrapper.addRefrigeratorToFacility("LG", "800L", "GNR7878", "F10");
  }

  public void initiateDistribution(String deliveryZoneNameFirst, String programFirst) throws IOException {

    HomePage homePage = new HomePage(testWebDriver);
    DistributionPage distributionPage = homePage.navigatePlanDistribution();
    distributionPage.selectValueFromDeliveryZone(deliveryZoneNameFirst);
    distributionPage.selectValueFromProgram(programFirst);
    distributionPage.clickInitiateDistribution();
    distributionPage.clickRecordData(1);
  }

  public void enterDataInGeneralObservationsPage(String observation, String confirmName, String confirmTitle, String verifierName,
                                                 String verifierTitle) {
    GeneralObservationPage generalObservationPage = new GeneralObservationPage(testWebDriver);
    generalObservationPage.navigate();
    generalObservationPage.setObservations(observation);
    generalObservationPage.setConfirmedByName(confirmName);
    generalObservationPage.setConfirmedByTitle(confirmTitle);
    generalObservationPage.setVerifiedByName(verifierName);
    generalObservationPage.setVerifiedByTitle(verifierTitle);
  }

  public void enterDataInEpiUsePage(Integer stockAtFirstOfMonth, Integer receivedValue, Integer distributedValue,
                                    Integer loss, Integer stockAtEndOfMonth, String expirationDate, int rowNumber) {
    EPIUse epiUse = new EPIUse(testWebDriver);
    epiUse.navigate();

    epiUse.verifyProductGroup("PG1-Name", 1);

    epiUse.enterValueInStockAtFirstOfMonth(stockAtFirstOfMonth.toString(), rowNumber);
    epiUse.verifyIndicator("AMBER");
    epiUse.enterValueInReceived(receivedValue.toString(), rowNumber);
    epiUse.enterValueInDistributed(distributedValue.toString(), rowNumber);
    epiUse.enterValueInLoss(valueOf(loss), rowNumber);
    epiUse.enterValueInStockAtEndOfMonth(stockAtEndOfMonth.toString(), rowNumber);
    epiUse.enterValueInExpirationDate(expirationDate, rowNumber);
    epiUse.verifyIndicator("GREEN");
  }

  public void initiateDistributionAndVerifyStatusColor() throws IOException {

    LoginPage loginPage = new LoginPage(testWebDriver, baseUrlGlobal);
    HomePage homePage = loginPage.loginAs(refrigeratorTestData.get(USER), refrigeratorTestData.get(PASSWORD));
    initiateDistribution(refrigeratorTestData.get(FIRST_DELIVERY_ZONE_NAME), refrigeratorTestData.get(VACCINES_PROGRAM));
    FacilityListPage facilityListPage = new FacilityListPage(testWebDriver);
    facilityListPage.selectFacility(refrigeratorTestData.get(FIRST_FACILITY_CODE));
    RefrigeratorPage refrigeratorPage = new RefrigeratorPage(testWebDriver);
    facilityListPage.verifyFacilityIndicatorColor("Overall", "RED");
    refrigeratorPage.verifyRefrigeratorColor("overall", "RED");
  }

  @AfterMethod(groups = "distribution")
  public void tearDown() throws Exception {
    testWebDriver.sleep(500);
    if (!testWebDriver.getElementById("username").isDisplayed()) {
      HomePage homePage = new HomePage(testWebDriver);
      homePage.logout(baseUrlGlobal);
      dbWrapper.deleteData();
      dbWrapper.closeConnection();
    }
    ((JavascriptExecutor) TestWebDriver.getDriver()).executeScript("indexedDB.deleteDatabase('open_lmis');");
  }
}
