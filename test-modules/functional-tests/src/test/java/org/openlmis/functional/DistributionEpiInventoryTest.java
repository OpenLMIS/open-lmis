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

import com.thoughtworks.selenium.SeleneseTestBase;
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

import static com.thoughtworks.selenium.SeleneseTestNgHelper.assertEquals;
import static org.testng.AssertJUnit.assertFalse;
import static org.testng.AssertJUnit.assertTrue;

public class DistributionEpiInventoryTest extends TestCaseHelper {

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

  public Map<String, String> epiInventoryData = new HashMap<String, String>() {{
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

    Map<String, String> dataMap = epiInventoryData;

    setupDataForDistributionTest(dataMap.get(USER), dataMap.get(FIRST_DELIVERY_ZONE_CODE), dataMap.get(SECOND_DELIVERY_ZONE_CODE),
      dataMap.get(FIRST_DELIVERY_ZONE_NAME), dataMap.get(SECOND_DELIVERY_ZONE_NAME), dataMap.get(FIRST_FACILITY_CODE),
      dataMap.get(SECOND_FACILITY_CODE), dataMap.get(VACCINES_PROGRAM), dataMap.get(TB_PROGRAM), dataMap.get(SCHEDULE),
      dataMap.get(PRODUCT_GROUP_CODE));
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

  @Test(groups = {"distribution"})
  public void shouldDisplayAllActiveFullAndNonFullSupplyProductsWithIdealQuantityOnEpiInventoryPage() throws Exception {
    dbWrapper.updateProductFullSupplyStatus("P10", false);

    LoginPage loginPage = new LoginPage(testWebDriver, baseUrlGlobal);
    HomePage homePage = loginPage.loginAs("Admin123", "Admin123");

    ProgramProductISAPage programProductISAPage = homePage.navigateProgramProductISA();
    String expectedISAValue = programProductISAPage.fillProgramProductISA(epiInventoryData.get(VACCINES_PROGRAM), "100", "1", "50", "30", "0", "100", "2000", "333");

    homePage.navigateHomePage();
    ManageFacilityPage manageFacilityPage = homePage.navigateManageFacility();
    manageFacilityPage.overrideISA("567", 3, epiInventoryData.get(FIRST_FACILITY_CODE));

    loginPage = manageFacilityPage.logout();

    homePage = loginPage.loginAs(epiInventoryData.get(USER), epiInventoryData.get(PASSWORD));
    DistributionPage distributionPage = homePage.navigateToDistributionWhenOnline();
    distributionPage.initiate(epiInventoryData.get(FIRST_DELIVERY_ZONE_NAME), epiInventoryData.get(VACCINES_PROGRAM));

    FacilityListPage facilityListPage = distributionPage.clickRecordData(1);
    EpiInventoryPage epiInventoryPage = facilityListPage.selectFacility(epiInventoryData.get(FIRST_FACILITY_CODE)).navigateToEpiInventory();

    verifyLabels(epiInventoryPage);

    assertEquals(epiInventoryPage.getIsaValue(1), expectedISAValue);
    assertEquals(epiInventoryPage.getProductName(1), "antibiotic");

    assertEquals(epiInventoryPage.getIsaValue(2), "--");
    assertEquals(epiInventoryPage.getProductName(2), "ProductName6");

    assertEquals(epiInventoryPage.getIsaValue(3), "57");
    assertEquals(epiInventoryPage.getProductName(3), "antibiotic");

    assertFalse(epiInventoryPage.getDataEpiInventory().contains("ProductName5"));

    dbWrapper.updateProductFullSupplyStatus("P10", true);
  }

  @Test(groups = {"distribution"})
  public void shouldNotDisplayGloballyInactiveProductsOnEpiInventoryPage() throws Exception {
    dbWrapper.updateActiveStatusOfProduct("Product6", "false");

    LoginPage loginPage = new LoginPage(testWebDriver, baseUrlGlobal);
    HomePage homePage = loginPage.loginAs(epiInventoryData.get(USER), epiInventoryData.get(PASSWORD));
    DistributionPage distributionPage = homePage.navigateToDistributionWhenOnline();
    distributionPage.initiate(epiInventoryData.get(FIRST_DELIVERY_ZONE_NAME), epiInventoryData.get(VACCINES_PROGRAM));

    FacilityListPage facilityListPage = distributionPage.clickRecordData(1);
    EpiInventoryPage epiInventoryPage = facilityListPage.selectFacility(epiInventoryData.get(FIRST_FACILITY_CODE)).navigateToEpiInventory();

    verifyLabels(epiInventoryPage);

    assertEquals(epiInventoryPage.getIsaValue(1), "--");
    assertEquals(epiInventoryPage.getProductName(1), "antibiotic");

    assertEquals(epiInventoryPage.getIsaValue(2), "--");
    assertEquals(epiInventoryPage.getProductName(2), "antibiotic");

    assertFalse(epiInventoryPage.getDataEpiInventory().contains("ProductName6"));

    dbWrapper.updateActiveStatusOfProduct("Product6", "true");
  }

  @Test(groups = {"distribution"})
  public void shouldDisplayNoProductsAddedMessageWhOnEpiInventoryPageWhenNoActiveProducts() throws Exception {
    dbWrapper.updateActiveStatusOfProduct("P10", "false");
    dbWrapper.updateActiveStatusOfProduct("P11", "false");
    dbWrapper.updateActiveStatusOfProduct("Product6", "false");

    LoginPage loginPage = new LoginPage(testWebDriver, baseUrlGlobal);
    HomePage homePage = loginPage.loginAs(epiInventoryData.get(USER), epiInventoryData.get(PASSWORD));
    DistributionPage distributionPage = homePage.navigateToDistributionWhenOnline();
    distributionPage.initiate(epiInventoryData.get(FIRST_DELIVERY_ZONE_NAME), epiInventoryData.get(VACCINES_PROGRAM));

    FacilityListPage facilityListPage = distributionPage.clickRecordData(1);
    EpiInventoryPage epiInventoryPage = facilityListPage.selectFacility(epiInventoryData.get(FIRST_FACILITY_CODE)).navigateToEpiInventory();

    assertTrue(epiInventoryPage.getNoProductsAddedMessage().contains("No products added"));
    epiInventoryPage.verifyIndicator("GREEN");

    dbWrapper.updateActiveStatusOfProduct("P10", "true");
    dbWrapper.updateActiveStatusOfProduct("P11", "true");
    dbWrapper.updateActiveStatusOfProduct("Product6", "true");

  }

  @Test(groups = {"distribution"})
  public void shouldFillInEpiInventoryDataAndVerifyIndicatorStatusWithLocalCaching() throws Exception {
    LoginPage loginPage = new LoginPage(testWebDriver, baseUrlGlobal);
    HomePage homePage = loginPage.loginAs(epiInventoryData.get(USER), epiInventoryData.get(PASSWORD));

    DistributionPage distributionPage = homePage.navigateToDistributionWhenOnline();
    distributionPage.initiate(epiInventoryData.get(FIRST_DELIVERY_ZONE_NAME), epiInventoryData.get(VACCINES_PROGRAM));

    FacilityListPage facilityListPage = distributionPage.clickRecordData(1);
    RefrigeratorPage refrigeratorPage = facilityListPage.selectFacility(epiInventoryData.get(FIRST_FACILITY_CODE)).navigateToRefrigerators();
    refrigeratorPage.clickAddNew();
    refrigeratorPage.addNewRefrigerator("new", "new", "SR111");
    EpiInventoryPage epiInventoryPage = refrigeratorPage.navigateToEpiInventory();

    epiInventoryPage.verifyIndicator("RED");
    facilityListPage.verifyFacilityIndicatorColor("Overall", "RED");

    epiInventoryPage.fillDeliveredQuantity(1, "1");
    epiInventoryPage.fillDeliveredQuantity(2, "2");
    epiInventoryPage.fillDeliveredQuantity(3, "3");

    epiInventoryPage.verifyIndicator("AMBER");
    facilityListPage.verifyFacilityIndicatorColor("Overall", "AMBER");

    epiInventoryPage.applyNRToAll();
    assertTrue(epiInventoryPage.getDeliveredQuantityStatus(1));
    assertTrue(epiInventoryPage.getDeliveredQuantityStatus(2));
    assertTrue(epiInventoryPage.getDeliveredQuantityStatus(3));

    epiInventoryPage.toggleExistingQuantityNR(1);
    epiInventoryPage.fillExistingQuantity(1, "5");

    epiInventoryPage.toggleSpoiledQuantityNR(2);
    epiInventoryPage.fillSpoiledQuantity(2, "-");
    assertTrue(epiInventoryPage.errorMessageDisplayed(2));

    epiInventoryPage.fillSpoiledQuantity(2, "4");

    epiInventoryPage = epiInventoryPage.navigateToGeneralObservations().navigateToEpiInventory();

    assertEquals(epiInventoryPage.getDeliveredQuantity(1), "1");

    epiInventoryPage.verifyIndicator("GREEN");
  }

  @Test(groups = {"distribution"})
  public void testEpiInventoryPageSync() throws Exception {
    LoginPage loginPage = new LoginPage(testWebDriver, baseUrlGlobal);
    HomePage homePage = loginPage.loginAs(epiInventoryData.get(USER), epiInventoryData.get(PASSWORD));

    initiateDistribution(epiInventoryData.get(FIRST_DELIVERY_ZONE_NAME), epiInventoryData.get(VACCINES_PROGRAM));

    FacilityListPage facilityListPage = new FacilityListPage(testWebDriver);
    RefrigeratorPage refrigeratorPage = facilityListPage.selectFacility(epiInventoryData.get(FIRST_FACILITY_CODE));

    EpiInventoryPage epiInventoryPage = refrigeratorPage.navigateToEpiInventory();
    epiInventoryPage .verifyIndicator("RED");

    epiInventoryPage.fillExistingQuantity(1,"1");
    epiInventoryPage.fillDeliveredQuantity(1,"2");
    epiInventoryPage.fillSpoiledQuantity(1,"3");

    epiInventoryPage.verifyIndicator("AMBER");

    epiInventoryPage.fillExistingQuantity(2,"11");
    epiInventoryPage.fillDeliveredQuantity(2,"12");
    epiInventoryPage.fillSpoiledQuantity(2,"13");

    epiInventoryPage.fillExistingQuantity(3,"21");
    epiInventoryPage.fillDeliveredQuantity(3,"22");
    epiInventoryPage.fillSpoiledQuantity(3,"23");

    epiInventoryPage .verifyIndicator("GREEN");

    GeneralObservationPage generalObservationPage = epiInventoryPage.navigateToGeneralObservations();
    generalObservationPage.enterData("some observations", "samuel", "Doe", "Verifier", "XYZ");

    CoveragePage coveragePage = generalObservationPage.navigateToCoverage();
    coveragePage.enterData(12, 34, 45, "56");

    EPIUsePage epiUsePage = coveragePage.navigateToEpiUse();
    epiUsePage.enterData(70, 80, 90, 100, 9999999, "10/2011", 1);

    DistributionPage distributionPage = homePage.navigateToDistributionWhenOnline();
    distributionPage.syncDistribution(1);
    SeleneseTestBase.assertTrue(distributionPage.getSyncMessage().contains("F10-Village Dispensary"));
    distributionPage.syncDistributionMessageDone();

    verifyEpiInventoryDataInDatabase("1","2","3", "P10", epiInventoryData.get(FIRST_FACILITY_CODE));
    verifyEpiInventoryDataInDatabase("11","12","13", "Product6", epiInventoryData.get(FIRST_FACILITY_CODE));
    verifyEpiInventoryDataInDatabase("21","22","23", "P11", epiInventoryData.get(FIRST_FACILITY_CODE));
  }

  @Test(groups = {"distribution"})
  public void testEpiInventoryPageSyncWhenApplyNRAll() throws Exception {
    LoginPage loginPage = new LoginPage(testWebDriver, baseUrlGlobal);
    HomePage homePage = loginPage.loginAs(epiInventoryData.get(USER), epiInventoryData.get(PASSWORD));

    initiateDistribution(epiInventoryData.get(FIRST_DELIVERY_ZONE_NAME), epiInventoryData.get(VACCINES_PROGRAM));

    FacilityListPage facilityListPage = new FacilityListPage(testWebDriver);
    RefrigeratorPage refrigeratorPage = facilityListPage.selectFacility(epiInventoryData.get(FIRST_FACILITY_CODE));

    EpiInventoryPage epiInventoryPage = refrigeratorPage.navigateToEpiInventory();
    epiInventoryPage .verifyIndicator("RED");

    epiInventoryPage.applyNRToAll();

    epiInventoryPage.verifyIndicator("AMBER");

    epiInventoryPage.fillDeliveredQuantity(1,"1");
    epiInventoryPage.fillDeliveredQuantity(2,"2");
    epiInventoryPage.fillDeliveredQuantity(3,"3");

    epiInventoryPage .verifyIndicator("GREEN");

    GeneralObservationPage generalObservationPage = epiInventoryPage.navigateToGeneralObservations();
    generalObservationPage.enterData("some observations", "samuel", "Doe", "Verifier", "XYZ");

    CoveragePage coveragePage = generalObservationPage.navigateToCoverage();
    coveragePage.enterData(12, 34, 45, "56");

    EPIUsePage epiUsePage = coveragePage.navigateToEpiUse();
    epiUsePage.enterData(70, 80, 90, 100, 9999999, "10/2011", 1);

    DistributionPage distributionPage = homePage.navigateToDistributionWhenOnline();
    distributionPage.syncDistribution(1);
    SeleneseTestBase.assertTrue(distributionPage.getSyncMessage().contains("F10-Village Dispensary"));
    distributionPage.syncDistributionMessageDone();

    verifyEpiInventoryDataInDatabase(null,"1",null, "P10", epiInventoryData.get(FIRST_FACILITY_CODE));
    verifyEpiInventoryDataInDatabase(null,"2",null, "Product6", epiInventoryData.get(FIRST_FACILITY_CODE));
    verifyEpiInventoryDataInDatabase(null,"3",null, "P11", epiInventoryData.get(FIRST_FACILITY_CODE));
  }

  @Test(groups = {"distribution"})
  public void testEpiInventoryPageSyncWhenApplyNRToFewFields() throws Exception {
    LoginPage loginPage = new LoginPage(testWebDriver, baseUrlGlobal);
    HomePage homePage = loginPage.loginAs(epiInventoryData.get(USER), epiInventoryData.get(PASSWORD));

    initiateDistribution(epiInventoryData.get(FIRST_DELIVERY_ZONE_NAME), epiInventoryData.get(VACCINES_PROGRAM));

    FacilityListPage facilityListPage = new FacilityListPage(testWebDriver);
    RefrigeratorPage refrigeratorPage = facilityListPage.selectFacility(epiInventoryData.get(FIRST_FACILITY_CODE));

    EpiInventoryPage epiInventoryPage = refrigeratorPage.navigateToEpiInventory();
    epiInventoryPage .verifyIndicator("RED");

    epiInventoryPage.applyNRToAll();

    epiInventoryPage.verifyIndicator("AMBER");

    epiInventoryPage.fillDeliveredQuantity(1,"1");
    epiInventoryPage.fillDeliveredQuantity(2,"2");
    epiInventoryPage.fillDeliveredQuantity(3,"3");
    epiInventoryPage.toggleExistingQuantityNR(1);
    epiInventoryPage.fillExistingQuantity(1,"77");
    epiInventoryPage.toggleSpoiledQuantityNR(3);
    epiInventoryPage.fillSpoiledQuantity(3,"99");


    epiInventoryPage .verifyIndicator("GREEN");

    GeneralObservationPage generalObservationPage = epiInventoryPage.navigateToGeneralObservations();
    generalObservationPage.enterData("some observations", "samuel", "Doe", "Verifier", "XYZ");

    CoveragePage coveragePage = generalObservationPage.navigateToCoverage();
    coveragePage.enterData(12, 34, 45, "56");

    EPIUsePage epiUsePage = coveragePage.navigateToEpiUse();
    epiUsePage.enterData(70, 80, 90, 100, 9999999, "10/2011", 1);

    DistributionPage distributionPage = homePage.navigateToDistributionWhenOnline();
    distributionPage.syncDistribution(1);
    SeleneseTestBase.assertTrue(distributionPage.getSyncMessage().contains("F10-Village Dispensary"));
    distributionPage.syncDistributionMessageDone();

    verifyEpiInventoryDataInDatabase("77","1",null, "P10", epiInventoryData.get(FIRST_FACILITY_CODE));
    verifyEpiInventoryDataInDatabase(null,"2",null, "Product6", epiInventoryData.get(FIRST_FACILITY_CODE));
    verifyEpiInventoryDataInDatabase(null,"3","99", "P11", epiInventoryData.get(FIRST_FACILITY_CODE));
  }

  public void initiateDistribution(String deliveryZoneNameFirst, String programFirst) throws IOException {

    HomePage homePage = new HomePage(testWebDriver);
    DistributionPage distributionPage = homePage.navigateToDistributionWhenOnline();
    distributionPage.selectValueFromDeliveryZone(deliveryZoneNameFirst);
    distributionPage.selectValueFromProgram(programFirst);
    distributionPage.clickInitiateDistribution();
    distributionPage.clickRecordData(1);
  }

  public void verifyLabels(EpiInventoryPage epiInventoryPage) {
    assertEquals(epiInventoryPage.getLabelVialsUnitsLabel(), "(Vials/Units)");
    assertEquals(epiInventoryPage.getLabelIdealQuantity(), "Ideal Quantity");
    assertEquals(epiInventoryPage.getLabelExistingQuantity(), "Existing Quantity");
    assertEquals(epiInventoryPage.getLabelDeliveredQuantity(), "Delivered Quantity");
    assertEquals(epiInventoryPage.getLabelSpoiledQuantity(), "Spoiled Quantity");
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
