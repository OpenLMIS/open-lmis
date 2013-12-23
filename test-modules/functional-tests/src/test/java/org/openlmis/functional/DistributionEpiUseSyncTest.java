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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.thoughtworks.selenium.SeleneseTestBase.assertEquals;
import static com.thoughtworks.selenium.SeleneseTestBase.assertTrue;
import static java.lang.String.valueOf;

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
  public void setUp() throws Exception {
    super.setup();

    Map<String, String> dataMap = epiUseData;

    setupDataForDistributionTest(dataMap.get(USER), dataMap.get(FIRST_DELIVERY_ZONE_CODE), dataMap.get(SECOND_DELIVERY_ZONE_CODE),
      dataMap.get(FIRST_DELIVERY_ZONE_NAME), dataMap.get(SECOND_DELIVERY_ZONE_NAME), dataMap.get(FIRST_FACILITY_CODE),
      dataMap.get(SECOND_FACILITY_CODE), dataMap.get(VACCINES_PROGRAM), dataMap.get(TB_PROGRAM), dataMap.get(SCHEDULE),
      dataMap.get(PRODUCT_GROUP_CODE));
  }

  @Test(groups = {"distribution"})
  public void testEpiUsePageSync() throws Exception {
    LoginPage loginPage = new LoginPage(testWebDriver, baseUrlGlobal);
    HomePage homePage = loginPage.loginAs(epiUseData.get(USER), epiUseData.get(PASSWORD));

    initiateDistribution(epiUseData.get(FIRST_DELIVERY_ZONE_NAME), epiUseData.get(VACCINES_PROGRAM));

    FacilityListPage facilityListPage = new FacilityListPage(testWebDriver);
    facilityListPage.selectFacility(epiUseData.get(FIRST_FACILITY_CODE));

    enterDataInEpiUsePage(10, 20, 30, 40, 50, "10/2011",1);

    enterDataInGeneralObservationsPage("some observations", "samuel", "Doe", "Verifier", "XYZ");

    DistributionPage distributionPage = homePage.navigatePlanDistribution();

    distributionPage.syncDistribution();
    assertTrue(distributionPage.getSyncMessage().contains("F10-Village Dispensary"));
    distributionPage.syncDistributionMessageDone();

    verifyEpiUseDataInDatabase(10, 20, 30, 40, 50, "10/2011", epiUseData.get(PRODUCT_GROUP_CODE),epiUseData.get(FIRST_FACILITY_CODE));
  }

  @Test(groups = {"distribution"})
  public void testEpiUseEditSync() throws Exception {
    LoginPage loginPage = new LoginPage(testWebDriver, baseUrlGlobal);
    HomePage homePage = loginPage.loginAs(epiUseData.get(USER), epiUseData.get(PASSWORD));

    initiateDistribution(epiUseData.get(FIRST_DELIVERY_ZONE_NAME), epiUseData.get(VACCINES_PROGRAM));

    FacilityListPage facilityListPage = new FacilityListPage(testWebDriver);
    facilityListPage.selectFacility(epiUseData.get(FIRST_FACILITY_CODE));

    EPIUse epiUse = new EPIUse(testWebDriver);
    epiUse.navigate();
    epiUse.verifyProductGroup("PG1-Name", 1);
    epiUse.verifyIndicator("RED");

    enterDataInEpiUsePage(10,20,30,40,50,"10/2011",1);

    RefrigeratorPage refrigeratorPage = new RefrigeratorPage(testWebDriver);
    refrigeratorPage.navigateToRefrigeratorTab();
    epiUse.navigate();

    epiUse.verifyTotal("30", 1);
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

    epiUse.verifyStockAtFirstOfMonthStatus(false, 1);
    epiUse.verifyReceivedStatus(false, 1);
    epiUse.verifyDistributedStatus(false, 1);
    epiUse.verifyLossStatus(false, 1);
    epiUse.verifyStockAtEndOfMonthStatus(false, 1);
    epiUse.verifyExpirationDateStatus(false, 1);

    epiUse.checkUncheckStockAtFirstOfMonthNotRecorded(1);
    epiUse.checkUncheckReceivedNotRecorded(1);
    epiUse.checkUncheckDistributedNotRecorded(1);
    epiUse.checkUncheckLossNotRecorded(1);
    epiUse.checkUncheckStockAtEndOfMonthNotRecorded(1);
    epiUse.checkUncheckExpirationDateNotRecorded(1);

    enterDataInEpiUsePage(20,30,40,50,60,"11/2012",1);

    refrigeratorPage.navigateToRefrigeratorTab();
    epiUse.navigate();
    epiUse.checkApplyNRToAllFields(false);
    epiUse.verifyTotal("50", 1);

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
    epiUse.verifyStockAtFirstOfMonthStatus(true, 1);
    epiUse.verifyReceivedStatus(true, 1);
    epiUse.verifyDistributedStatus(true, 1);
    epiUse.verifyLossStatus(true, 1);
    epiUse.verifyStockAtEndOfMonthStatus(true, 1);
    epiUse.verifyExpirationDateStatus(true, 1);
    epiUse.verifyIndicator("GREEN");

    enterDataInGeneralObservationsPage("some observations", "samuel", "Doe", "Verifier", "XYZ");

    DistributionPage distributionPage = homePage.navigatePlanDistribution();

    distributionPage.syncDistribution();
    assertTrue(distributionPage.getSyncMessage().contains("F10-Village Dispensary"));
    distributionPage.syncDistributionMessageDone();

    verifyEpiUseDataInDatabase(20, 30, 40, 50, 60, "11/2012", epiUseData.get(PRODUCT_GROUP_CODE),epiUseData.get(FIRST_FACILITY_CODE));
  }

  @Test(groups = {"distribution"})
  public void testEpiUsePageSyncWhenAllProductsInactiveAfterCaching() throws Exception {

    LoginPage loginPage = new LoginPage(testWebDriver, baseUrlGlobal);
    HomePage homePage = loginPage.loginAs(epiUseData.get(USER), epiUseData.get(PASSWORD));

    initiateDistribution(epiUseData.get(FIRST_DELIVERY_ZONE_NAME), epiUseData.get(VACCINES_PROGRAM));

    dbWrapper.updateActiveStatusOfProduct("Product5", "false");
    dbWrapper.updateActiveStatusOfProduct("Product6", "false");

    FacilityListPage facilityListPage = new FacilityListPage(testWebDriver);
    facilityListPage.selectFacility(epiUseData.get(FIRST_FACILITY_CODE));

    enterDataInEpiUsePage(10, 20, 30, 40, 50, "10/2011",1);

    enterDataInGeneralObservationsPage("some observations", "samuel", "Doe", "Verifier", "XYZ");

    DistributionPage distributionPage = homePage.navigatePlanDistribution();

    distributionPage.syncDistribution();
    assertTrue(distributionPage.getSyncMessage().contains("F10-Village Dispensary"));
    distributionPage.syncDistributionMessageDone();

    verifyEpiUseDataInDatabase(10, 20, 30, 40, 50, "10/2011", epiUseData.get(PRODUCT_GROUP_CODE),epiUseData.get(FIRST_FACILITY_CODE));

    dbWrapper.updateActiveStatusOfProduct("Product5", "true");
    dbWrapper.updateActiveStatusOfProduct("Product6", "true");
  }

  @Test(groups = {"distribution"})
  public void testEpiUsePageSyncWhenProductGroupAddedAfterCaching() throws Exception {

    LoginPage loginPage = new LoginPage(testWebDriver, baseUrlGlobal);
    HomePage homePage = loginPage.loginAs(epiUseData.get(USER), epiUseData.get(PASSWORD));

    initiateDistribution(epiUseData.get(FIRST_DELIVERY_ZONE_NAME), epiUseData.get(VACCINES_PROGRAM));

    dbWrapper.insertProductGroup("PG2");
    dbWrapper.insertProductWithGroup("Product7", "ProductName7", "PG2", true);
    dbWrapper.insertProgramProduct("Product7", epiUseData.get(VACCINES_PROGRAM), "10", "true");

    FacilityListPage facilityListPage = new FacilityListPage(testWebDriver);
    facilityListPage.selectFacility(epiUseData.get(FIRST_FACILITY_CODE));

    enterDataInEpiUsePage(10, 20, 30, 40, 50, "10/2011",1);

    enterDataInGeneralObservationsPage("some observations", "samuel", "Doe", "Verifier", "XYZ");

    DistributionPage distributionPage = homePage.navigatePlanDistribution();

    distributionPage.syncDistribution();
    assertTrue(distributionPage.getSyncMessage().contains("F10-Village Dispensary"));
    distributionPage.syncDistributionMessageDone();

    verifyEpiUseDataInDatabase(10, 20, 30, 40, 50, "10/2011", epiUseData.get(PRODUCT_GROUP_CODE),epiUseData.get(FIRST_FACILITY_CODE));
  }

  @Test(groups = {"distribution"})
  public void testEpiUsePageSyncWhenFacilityInactiveAfterCaching() throws Exception {


    LoginPage loginPage = new LoginPage(testWebDriver, baseUrlGlobal);
    HomePage homePage = loginPage.loginAs(epiUseData.get(USER), epiUseData.get(PASSWORD));

    initiateDistribution(epiUseData.get(FIRST_DELIVERY_ZONE_NAME), epiUseData.get(VACCINES_PROGRAM));

    dbWrapper.updateActiveStatusOfFacility("F10", "false");

    FacilityListPage facilityListPage = new FacilityListPage(testWebDriver);
    facilityListPage.selectFacility(epiUseData.get(FIRST_FACILITY_CODE));

    enterDataInEpiUsePage(10, 20, 30, 40, 50, "10/2011",1);

    enterDataInGeneralObservationsPage("some observations", "samuel", "Doe", "Verifier", "XYZ");

    DistributionPage distributionPage = homePage.navigatePlanDistribution();

    distributionPage.syncDistribution();
    assertTrue(distributionPage.getSyncMessage().contains("F10-Village Dispensary"));
    distributionPage.syncDistributionMessageDone();

    verifyEpiUseDataInDatabase(10, 20, 30, 40, 50, "10/2011", epiUseData.get(PRODUCT_GROUP_CODE),epiUseData.get(FIRST_FACILITY_CODE));

    dbWrapper.updateActiveStatusOfFacility("F10", "true");
  }

  @Test(groups = {"distribution"})
  public void testEpiUsePageSyncWhenFacilityDisabledAfterCaching() throws Exception {


    LoginPage loginPage = new LoginPage(testWebDriver, baseUrlGlobal);
    HomePage homePage = loginPage.loginAs(epiUseData.get(USER), epiUseData.get(PASSWORD));

    initiateDistribution(epiUseData.get(FIRST_DELIVERY_ZONE_NAME), epiUseData.get(VACCINES_PROGRAM));

    dbWrapper.updateFacilityFieldBYCode("enabled", "false", "F10");

    FacilityListPage facilityListPage = new FacilityListPage(testWebDriver);
    facilityListPage.selectFacility(epiUseData.get(FIRST_FACILITY_CODE));

    enterDataInEpiUsePage(10, 20, 30, 40, 50, "10/2011",1);

    enterDataInGeneralObservationsPage("some observations", "samuel", "Doe", "Verifier", "XYZ");

    DistributionPage distributionPage = homePage.navigatePlanDistribution();

    distributionPage.syncDistribution();
    assertTrue(distributionPage.getSyncMessage().contains("F10-Village Dispensary"));
    distributionPage.syncDistributionMessageDone();

    verifyEpiUseDataInDatabase(10, 20, 30, 40, 50, "10/2011", epiUseData.get(PRODUCT_GROUP_CODE),epiUseData.get(FIRST_FACILITY_CODE));

    dbWrapper.updateFacilityFieldBYCode("enabled", "true", "F10");
  }

  @Test(groups = {"distribution"})
  public void testEpiUsePageSyncWhenAllProgramInactiveAfterCaching() throws Exception {
    LoginPage loginPage = new LoginPage(testWebDriver, baseUrlGlobal);
    HomePage homePage = loginPage.loginAs(epiUseData.get(USER), epiUseData.get(PASSWORD));

    initiateDistribution(epiUseData.get(FIRST_DELIVERY_ZONE_NAME), epiUseData.get(VACCINES_PROGRAM));

    dbWrapper.updateActiveStatusOfProgram("VACCINES", false);

    FacilityListPage facilityListPage = new FacilityListPage(testWebDriver);
    facilityListPage.selectFacility(epiUseData.get(FIRST_FACILITY_CODE));

    enterDataInEpiUsePage(10, 20, 30, 40, 50, "10/2011",1);

    enterDataInGeneralObservationsPage("some observations", "samuel", "Doe", "Verifier", "XYZ");

    DistributionPage distributionPage = homePage.navigatePlanDistribution();

    distributionPage.syncDistribution();
    assertTrue(distributionPage.getSyncMessage().contains("F10-Village Dispensary"));
    distributionPage.syncDistributionMessageDone();

    verifyEpiUseDataInDatabase(10, 20, 30, 40, 50, "10/2011", epiUseData.get(PRODUCT_GROUP_CODE),epiUseData.get(FIRST_FACILITY_CODE));

    dbWrapper.updateActiveStatusOfProgram("VACCINES", true);
  }

  @Test(groups = {"distribution"})
  public void testEpiUsePageSyncWhenProgramDeletedAfterCaching() throws Exception {
    LoginPage loginPage = new LoginPage(testWebDriver, baseUrlGlobal);
    HomePage homePage = loginPage.loginAs(epiUseData.get(USER), epiUseData.get(PASSWORD));

    initiateDistribution(epiUseData.get(FIRST_DELIVERY_ZONE_NAME), epiUseData.get(VACCINES_PROGRAM));

    dbWrapper.deleteProgramToFacilityMapping("VACCINES");

    FacilityListPage facilityListPage = new FacilityListPage(testWebDriver);
    facilityListPage.selectFacility(epiUseData.get(FIRST_FACILITY_CODE));

    enterDataInEpiUsePage(10, 20, 30, 40, 50, "10/2011",1);

    enterDataInGeneralObservationsPage("some observations", "samuel", "Doe", "Verifier", "XYZ");

    DistributionPage distributionPage = homePage.navigatePlanDistribution();

    distributionPage.syncDistribution();
    assertTrue(distributionPage.getSyncMessage().contains("F10-Village Dispensary"));
    distributionPage.syncDistributionMessageDone();

    verifyEpiUseDataInDatabase(10, 20, 30, 40, 50, "10/2011", epiUseData.get(PRODUCT_GROUP_CODE),epiUseData.get(FIRST_FACILITY_CODE));
  }

  @Test(groups = {"distribution"})
  public void testEpiUsePageSyncWhenProductWithNoProductGroupAddedAfterCaching() throws Exception {
    LoginPage loginPage = new LoginPage(testWebDriver, baseUrlGlobal);
    HomePage homePage = loginPage.loginAs(epiUseData.get(USER), epiUseData.get(PASSWORD));

    initiateDistribution(epiUseData.get(FIRST_DELIVERY_ZONE_NAME), epiUseData.get(VACCINES_PROGRAM));

    dbWrapper.insertProducts("Product7", "Product8");
    dbWrapper.insertProgramProduct("Product7", "VACCINES", "10", "true");

    FacilityListPage facilityListPage = new FacilityListPage(testWebDriver);
    facilityListPage.selectFacility(epiUseData.get(FIRST_FACILITY_CODE));

    enterDataInEpiUsePage(10, 20, 30, 40, 50, "10/2011",1);

    enterDataInGeneralObservationsPage("some observations", "samuel", "Doe", "Verifier", "XYZ");

    DistributionPage distributionPage = homePage.navigatePlanDistribution();

    distributionPage.syncDistribution();
    assertTrue(distributionPage.getSyncMessage().contains("F10-Village Dispensary"));
    distributionPage.syncDistributionMessageDone();

    verifyEpiUseDataInDatabase(10, 20, 30, 40, 50, "10/2011", epiUseData.get(PRODUCT_GROUP_CODE),epiUseData.get(FIRST_FACILITY_CODE));
  }

  @Test(groups = {"distribution"})
  public void testEpiUsePageSyncWhenSomeFieldsEmpty() throws Exception {

    LoginPage loginPage = new LoginPage(testWebDriver, baseUrlGlobal);
    loginPage.loginAs(epiUseData.get(USER), epiUseData.get(PASSWORD));

    initiateDistribution(epiUseData.get(FIRST_DELIVERY_ZONE_NAME), epiUseData.get(VACCINES_PROGRAM));

    FacilityListPage facilityListPage = new FacilityListPage(testWebDriver);
    facilityListPage.selectFacility(epiUseData.get(FIRST_FACILITY_CODE));

    EPIUse epiUse = new EPIUse(testWebDriver);
    epiUse.navigate();
    epiUse.verifyProductGroup("PG1-Name", 1);

    epiUse.verifyIndicator("RED");

    epiUse.enterValueInStockAtFirstOfMonth("10", 1);
    epiUse.verifyIndicator("AMBER");

    enterDataInGeneralObservationsPage("some observations", "samuel", "Doe", "Verifier", "XYZ");

    facilityListPage.selectFacility("F10");
    facilityListPage.verifyFacilityIndicatorColor("Overall", "AMBER");

    verifyEpiUseDataInDatabase(null, null, null, null, null, null, epiUseData.get(PRODUCT_GROUP_CODE),epiUseData.get(FIRST_FACILITY_CODE));
  }

  @Test(groups = {"distribution"})
  public void testEpiUsePageSyncWhenNrAppliedToAllFields() throws Exception {


    LoginPage loginPage = new LoginPage(testWebDriver, baseUrlGlobal);
    HomePage homePage = loginPage.loginAs(epiUseData.get(USER), epiUseData.get(PASSWORD));

    initiateDistribution(epiUseData.get(FIRST_DELIVERY_ZONE_NAME), epiUseData.get(VACCINES_PROGRAM));

    FacilityListPage facilityListPage = new FacilityListPage(testWebDriver);
    facilityListPage.selectFacility(epiUseData.get(FIRST_FACILITY_CODE));

    EPIUse epiUse = new EPIUse(testWebDriver);
    epiUse.navigate();

    epiUse.verifyProductGroup("PG1-Name", 1);

    epiUse.verifyIndicator("RED");
    enterDataInEpiUsePage(10,20,30,40,50,"10/2011",1);
    epiUse.verifyIndicator("GREEN");

    epiUse.checkApplyNRToAllFields(true);
    epiUse.verifyIndicator("GREEN");
    epiUse.verifyStockAtFirstOfMonthStatus(false, 1);
    epiUse.verifyReceivedStatus(false, 1);
    epiUse.verifyDistributedStatus(false, 1);
    epiUse.verifyLossStatus(false, 1);
    epiUse.verifyStockAtEndOfMonthStatus(false, 1);
    epiUse.verifyExpirationDateStatus(false, 1);

    enterDataInGeneralObservationsPage("some observations", "samuel", "Doe", "Verifier", "XYZ");

    DistributionPage distributionPage = homePage.navigatePlanDistribution();

    distributionPage.syncDistribution();
    assertTrue(distributionPage.getSyncMessage().contains("F10-Village Dispensary"));
    distributionPage.syncDistributionMessageDone();

    distributionPage.clickRecordData();
    facilityListPage.selectFacility("F10");
    facilityListPage.verifyFacilityIndicatorColor("Overall", "BLUE");

    verifyEpiUseDataInDatabase(null, null, null, null, null, null, epiUseData.get(PRODUCT_GROUP_CODE),epiUseData.get(FIRST_FACILITY_CODE));
  }

  @Test(groups = {"distribution"})
  public void testEpiUsePageSyncWhenNRAppliedToFewFields() throws Exception {
    dbWrapper.insertProductGroup("PG2");
    dbWrapper.insertProductWithGroup("Product7", "ProductName7", "PG2", true);
    dbWrapper.insertProgramProduct("Product7", epiUseData.get(VACCINES_PROGRAM), "10", "true");
    LoginPage loginPage = new LoginPage(testWebDriver, baseUrlGlobal);
    HomePage homePage = loginPage.loginAs(epiUseData.get(USER), epiUseData.get(PASSWORD));

    initiateDistribution(epiUseData.get(FIRST_DELIVERY_ZONE_NAME), epiUseData.get(VACCINES_PROGRAM));

    FacilityListPage facilityListPage = new FacilityListPage(testWebDriver);
    facilityListPage.selectFacility(epiUseData.get(FIRST_FACILITY_CODE));

    EPIUse epiUse = new EPIUse(testWebDriver);
    epiUse.navigate();
    epiUse.checkApplyNRToStockAtFirstOfMonth0();
    epiUse.checkApplyNRToReceived0();
    epiUse.checkApplyNRToDistributed0();
    epiUse.checkApplyNRToLoss0();
    epiUse.enterValueInStockAtEndOfMonth("4",1);
    epiUse.enterValueInExpirationDate("12/2031",1);

    enterDataInEpiUsePage(10, 20, 30, 40, 50, "10/2011",2);

    enterDataInGeneralObservationsPage("some observations", "samuel", "Doe", "Verifier", "XYZ");

    DistributionPage distributionPage = homePage.navigatePlanDistribution();

    distributionPage.syncDistribution();
    assertTrue(distributionPage.getSyncMessage().contains("F10-Village Dispensary"));
    distributionPage.syncDistributionMessageDone();

    verifyEpiUseDataInDatabase(null, null, null, null, 4, "12/2031", epiUseData.get(PRODUCT_GROUP_CODE),epiUseData.get(FIRST_FACILITY_CODE));
    verifyEpiUseDataInDatabase(10, 20, 30, 40, 50, "10/2011", "PG2",epiUseData.get(FIRST_FACILITY_CODE));
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

  public void initiateDistribution(String deliveryZoneNameFirst, String programFirst) throws IOException {

    HomePage homePage = new HomePage(testWebDriver);
    DistributionPage distributionPage = homePage.navigatePlanDistribution();
    distributionPage.selectValueFromDeliveryZone(deliveryZoneNameFirst);
    distributionPage.selectValueFromProgram(programFirst);
    distributionPage.clickInitiateDistribution();
    distributionPage.clickRecordData();
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
