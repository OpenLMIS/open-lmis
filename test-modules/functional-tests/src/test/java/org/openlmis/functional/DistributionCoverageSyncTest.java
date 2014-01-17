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

import org.openlmis.UiUtils.TestCaseHelper;
import org.openlmis.UiUtils.TestWebDriver;
import org.openlmis.pageobjects.*;
import org.openqa.selenium.JavascriptExecutor;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.thoughtworks.selenium.SeleneseTestBase.assertEquals;
import static com.thoughtworks.selenium.SeleneseTestBase.assertTrue;

public class DistributionCoverageSyncTest extends TestCaseHelper {
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

  public final Map<String, String> coverageData = new HashMap<String, String>() {{
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

    Map<String, String> dataMap = coverageData;

    setupDataForDistributionTest(dataMap.get(USER), dataMap.get(FIRST_DELIVERY_ZONE_CODE), dataMap.get(SECOND_DELIVERY_ZONE_CODE),
      dataMap.get(FIRST_DELIVERY_ZONE_NAME), dataMap.get(SECOND_DELIVERY_ZONE_NAME), dataMap.get(FIRST_FACILITY_CODE),
      dataMap.get(SECOND_FACILITY_CODE), dataMap.get(VACCINES_PROGRAM), dataMap.get(TB_PROGRAM), dataMap.get(SCHEDULE),
      dataMap.get(PRODUCT_GROUP_CODE));
  }

  @Test(groups = {"distribution"})
  public void shouldVerifyLabelsAndTestApplyNRToAllAndSync() throws IOException, SQLException {

    LoginPage loginPage = new LoginPage(testWebDriver, baseUrlGlobal);
    HomePage homePage = loginPage.loginAs(coverageData.get(USER), coverageData.get(PASSWORD));

    DistributionPage distributionPage = homePage.navigateToDistributionWhenOnline();
    distributionPage.initiate(coverageData.get(FIRST_DELIVERY_ZONE_NAME), coverageData.get(VACCINES_PROGRAM));
    FacilityListPage facilityListPage = distributionPage.clickRecordData(1);
    RefrigeratorPage refrigeratorPage = facilityListPage.selectFacility(coverageData.get(FIRST_FACILITY_CODE));

    facilityListPage.verifyFacilityIndicatorColor("Overall", "AMBER");

    CoveragePage coveragePage = refrigeratorPage.navigateToCoverage();
    coveragePage.verifyIndicator("RED");

    assertEquals("Coverage", coveragePage.getTextOfCoverageHeader());
    assertEquals("Completely Vaccinated Children(doses)", coveragePage.getTextOfCompletelyVaccinatedHeader());
    assertEquals("Females", coveragePage.getTextOfFemaleHeader());
    assertEquals("Males", coveragePage.getTextOfMaleHeader());
    assertEquals("Health Center", coveragePage.getTextOfHealthCenterHeader());
    assertEquals("Mobile Brigade", coveragePage.getTextOfMobileBrigadeHeader());

    coveragePage.enterFemaleHealthCenter(5);
    coveragePage.clickApplyNRToAll();
    coveragePage.clickApplyNRToAll(); //just checking reapplying NR to all doesn't deselect NR check boxes
    coveragePage.verifyIndicator("GREEN");

    verifyAllFieldsDisabled(coveragePage);
    verifyDataOnCoveragePage(coveragePage, "", "", "", "");

    EPIUsePage epiUsePage = coveragePage.navigateToEpiUse();
    epiUsePage.enterData(10, 20, 30, 40, 50, "10/2011", 1);

    GeneralObservationPage generalObservationPage = epiUsePage.navigateToGeneralObservations();
    generalObservationPage.enterData("some observations", "samuel", "Doe", "Verifier", "XYZ");

    EpiInventoryPage epiInventoryPage = refrigeratorPage.navigateToEpiInventory();
    fillEpiInventoryWithOnlyDeliveredQuantity(epiInventoryPage, "2", "4", "6");

    facilityListPage.verifyFacilityIndicatorColor("Overall", "GREEN");

    homePage.navigateToDistributionWhenOnline();
    distributionPage.syncDistribution(1);
    assertTrue(distributionPage.getSyncMessage().contains("F10-Village Dispensary"));
    distributionPage.syncDistributionMessageDone();
    verifyFullCoveragesDataInDatabase(null, null, null, null, coverageData.get(FIRST_FACILITY_CODE));
  }

  @Test(groups = {"distribution"})
  public void shouldTestCoverageAndFacilityIconStatusAndSync() throws IOException, SQLException {
    dbWrapper.addRefrigeratorToFacility("brand", "model", "serial", "F10");

    LoginPage loginPage = new LoginPage(testWebDriver, baseUrlGlobal);
    HomePage homePage = loginPage.loginAs(coverageData.get(USER), coverageData.get(PASSWORD));

    DistributionPage distributionPage = homePage.navigateToDistributionWhenOnline();
    distributionPage.initiate(coverageData.get(FIRST_DELIVERY_ZONE_NAME), coverageData.get(VACCINES_PROGRAM));
    FacilityListPage facilityListPage = distributionPage.clickRecordData(1);
    RefrigeratorPage refrigeratorPage = facilityListPage.selectFacility(coverageData.get(FIRST_FACILITY_CODE));

    facilityListPage.verifyFacilityIndicatorColor("Overall", "RED");

    CoveragePage coveragePage = refrigeratorPage.navigateToCoverage();
    coveragePage.verifyIndicator("RED");
    coveragePage.toggleApplyNRToFemaleMobileBrigade();
    coveragePage.verifyIndicator("AMBER");

    coveragePage.enterFemaleHealthCenter(9999999);
    coveragePage.toggleApplyNRToMaleHealthCenter();
    coveragePage.toggleApplyNRToMaleHealthCenter();
    coveragePage.enterMaleHealthCenter(10);
    coveragePage.enterMaleMobileBrigade("0");
    coveragePage.toggleApplyNRToMaleMobileBrigade();

    coveragePage.verifyIndicator("GREEN");

    verifyEnableStatusOfFields(coveragePage, true, false, true, false);
    verifyDataOnCoveragePage(coveragePage, "9999999", "", "10", "");

    facilityListPage.verifyFacilityIndicatorColor("Overall", "AMBER");

    EPIUsePage epiUsePage = coveragePage.navigateToEpiUse();
    epiUsePage.enterData(10, 20, 30, 40, 50, "10/2011", 1);

    GeneralObservationPage generalObservationPage = epiUsePage.navigateToGeneralObservations();
    generalObservationPage.enterData("some observations", "samuel", "Doe", "Verifier", "XYZ");

    generalObservationPage.navigateToRefrigerators();
    refrigeratorPage.clickDelete();
    refrigeratorPage.clickOKButton();

    EpiInventoryPage epiInventoryPage = refrigeratorPage.navigateToEpiInventory();
    fillEpiInventoryWithOnlyDeliveredQuantity(epiInventoryPage, "2", "4", "6");

    facilityListPage.verifyFacilityIndicatorColor("Overall", "GREEN");

    homePage.navigateToDistributionWhenOnline();
    distributionPage.syncDistribution(1);
    assertTrue(distributionPage.getSyncMessage().contains("F10-Village Dispensary"));
    distributionPage.syncDistributionMessageDone();

    distributionPage.clickRecordData(1);
    facilityListPage.selectFacility(coverageData.get(FIRST_FACILITY_CODE));

    facilityListPage.verifyFacilityIndicatorColor("Overall", "BLUE");

    refrigeratorPage.navigateToCoverage();
    verifyAllFieldsDisabled(coveragePage);
    verifyFullCoveragesDataInDatabase(9999999, null, 10, null, coverageData.get(FIRST_FACILITY_CODE));
  }

  @Test(groups = {"distribution"})
  public void shouldTestFillCoverageFormAndSync() throws IOException, SQLException {

    LoginPage loginPage = new LoginPage(testWebDriver, baseUrlGlobal);
    HomePage homePage = loginPage.loginAs(coverageData.get(USER), coverageData.get(PASSWORD));

    DistributionPage distributionPage = homePage.navigateToDistributionWhenOnline();
    distributionPage.initiate(coverageData.get(FIRST_DELIVERY_ZONE_NAME), coverageData.get(VACCINES_PROGRAM));
    FacilityListPage facilityListPage = distributionPage.clickRecordData(1);
    RefrigeratorPage refrigeratorPage = facilityListPage.selectFacility(coverageData.get(FIRST_FACILITY_CODE));

    facilityListPage.verifyFacilityIndicatorColor("Overall", "AMBER");

    CoveragePage coveragePage = refrigeratorPage.navigateToCoverage();
    coveragePage.verifyIndicator("RED");
    coveragePage.enterData(12, 34, 45, "56");
    coveragePage.verifyIndicator("GREEN");

    EPIUsePage epiUsePage = coveragePage.navigateToEpiUse();
    epiUsePage.enterData(10, 20, 30, 40, 50, "10/2011", 1);

    GeneralObservationPage generalObservationPage = epiUsePage.navigateToGeneralObservations();
    generalObservationPage.enterData("some observations", "samuel", "Doe", "Verifier", "XYZ");

    EpiInventoryPage epiInventoryPage = refrigeratorPage.navigateToEpiInventory();
    fillEpiInventoryWithOnlyDeliveredQuantity(epiInventoryPage, "2", "4", "6");

    facilityListPage.verifyFacilityIndicatorColor("Overall", "GREEN");

    homePage.navigateToDistributionWhenOnline();
    distributionPage.syncDistribution(1);
    assertTrue(distributionPage.getSyncMessage().contains("F10-Village Dispensary"));
    distributionPage.syncDistributionMessageDone();
    verifyFullCoveragesDataInDatabase(12, 34, 45, 56, coverageData.get(FIRST_FACILITY_CODE));
  }

  @Test(groups = {"distribution"})
  public void shouldTestSyncIncompleteCoverageFormUnsuccessful() throws IOException, SQLException {

    LoginPage loginPage = new LoginPage(testWebDriver, baseUrlGlobal);
    HomePage homePage = loginPage.loginAs(coverageData.get(USER), coverageData.get(PASSWORD));

    DistributionPage distributionPage = homePage.navigateToDistributionWhenOnline();
    distributionPage.initiate(coverageData.get(FIRST_DELIVERY_ZONE_NAME), coverageData.get(VACCINES_PROGRAM));
    FacilityListPage facilityListPage = distributionPage.clickRecordData(1);
    RefrigeratorPage refrigeratorPage = facilityListPage.selectFacility(coverageData.get(FIRST_FACILITY_CODE));

    facilityListPage.verifyFacilityIndicatorColor("Overall", "AMBER");

    CoveragePage coveragePage = refrigeratorPage.navigateToCoverage();
    coveragePage.verifyIndicator("RED");
    coveragePage.enterMaleHealthCenter(33);
    coveragePage.enterFemaleHealthCenter(67);
    coveragePage.enterMaleMobileBrigade("0");
    coveragePage.toggleApplyNRToMaleMobileBrigade();
    coveragePage.verifyIndicator("AMBER");

    EPIUsePage epiUsePage = coveragePage.navigateToEpiUse();
    epiUsePage.enterData(10, 20, 30, 40, 50, "10/2011", 1);

    GeneralObservationPage generalObservationPage = epiUsePage.navigateToGeneralObservations();
    generalObservationPage.enterData("some observations", "samuel", "Doe", "Verifier", "XYZ");

    generalObservationPage.navigateToCoverage();
    verifyDataOnCoveragePage(coveragePage, "67", "", "33", "");
    verifyEnableStatusOfFields(coveragePage, true, true, true, false);

    EpiInventoryPage epiInventoryPage = refrigeratorPage.navigateToEpiInventory();
    fillEpiInventoryWithOnlyDeliveredQuantity(epiInventoryPage, "2", "4", "6");

    facilityListPage.verifyFacilityIndicatorColor("Overall", "AMBER");

    homePage.navigateToDistributionWhenOnline();
    distributionPage.clickSyncDistribution(1);
    assertEquals("No facility for the chosen zone, program and period is ready to be sync", distributionPage.getSyncAlertMessage());
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
  }

  public void fillEpiInventoryWithOnlyDeliveredQuantity(EpiInventoryPage epiInventoryPage, String deliveredQuantity1, String deliveredQuantity2, String deliveredQuantity3) {
    epiInventoryPage.applyNRToAll();
    epiInventoryPage.fillDeliveredQuantity(1, deliveredQuantity1);
    epiInventoryPage.fillDeliveredQuantity(2, deliveredQuantity2);
    epiInventoryPage.fillDeliveredQuantity(3, deliveredQuantity3);
  }

  private void verifyEnableStatusOfFields(CoveragePage coveragePage, boolean femaleHealthCenterFieldStatus, boolean femaleMobileBrigadeFieldStatus,
                                          boolean maleHealthCenterFieldStatus, boolean maleMobileBrigadeFieldStatus) {
    assertEquals(femaleHealthCenterFieldStatus, coveragePage.getStatusForField("femaleHealthCenter"));
    assertEquals(femaleMobileBrigadeFieldStatus, coveragePage.getStatusForField("femaleMobileBrigade"));
    assertEquals(maleHealthCenterFieldStatus, coveragePage.getStatusForField("maleHealthCenter"));
    assertEquals(maleMobileBrigadeFieldStatus, coveragePage.getStatusForField("maleMobileBrigade"));
  }

  private void verifyDataOnCoveragePage(CoveragePage coveragePage, String femaleHealthCenterValue,
                                        String femaleMobileBrigadeValue, String maleHealthCenterValue, String maleMobileBrigadeValue) {
    assertEquals(femaleHealthCenterValue, coveragePage.getValueForField("femaleHealthCenter"));
    assertEquals(femaleMobileBrigadeValue, coveragePage.getValueForField("femaleMobileBrigade"));
    assertEquals(maleHealthCenterValue, coveragePage.getValueForField("maleHealthCenter"));
    assertEquals(maleMobileBrigadeValue, coveragePage.getValueForField("maleMobileBrigade"));
  }

  private void verifyAllFieldsDisabled(CoveragePage coveragePage) {
    verifyEnableStatusOfFields(coveragePage, false, false, false, false);
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
