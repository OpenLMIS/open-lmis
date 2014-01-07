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


import org.openlmis.UiUtils.CaptureScreenshotOnFailureListener;
import org.openlmis.UiUtils.TestCaseHelper;
import org.openlmis.UiUtils.TestWebDriver;
import org.openlmis.pageobjects.*;
import org.openqa.selenium.JavascriptExecutor;
import org.testng.annotations.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static com.thoughtworks.selenium.SeleneseTestNgHelper.assertEquals;
import static com.thoughtworks.selenium.SeleneseTestNgHelper.assertTrue;

@Listeners(CaptureScreenshotOnFailureListener.class)

public class DistributionSyncTest extends TestCaseHelper {

  public String userSIC, password;

  @BeforeMethod(groups = {"distribution"})
  public void setUp() throws Exception {
    super.setup();
  }

  @Test(groups = {"distribution"}, dataProvider = "Data-Provider-Function")
  public void testMultipleFacilitySync(String userSIC, String password, String deliveryZoneCodeFirst, String deliveryZoneCodeSecond,
                                       String deliveryZoneNameFirst, String deliveryZoneNameSecond, String facilityCodeFirst,
                                       String facilityCodeSecond, String programFirst, String programSecond, String schedule) throws Exception {
    setup(userSIC, deliveryZoneCodeFirst, deliveryZoneCodeSecond, deliveryZoneNameFirst, deliveryZoneNameSecond, facilityCodeFirst, facilityCodeSecond, programFirst, programSecond, schedule);

    LoginPage loginPage = new LoginPage(testWebDriver, baseUrlGlobal);
    HomePage homePage = loginPage.loginAs(userSIC, password);
    DistributionPage distributionPage = homePage.navigateToDistributionWhenOnline();

    distributionPage.initiate(deliveryZoneNameFirst, programFirst);
    FacilityListPage facilityListPage = distributionPage.clickRecordData(1);

    RefrigeratorPage refrigeratorPage = facilityListPage.selectFacility("F10");
    facilityListPage.verifyFacilityIndicatorColor("Overall", "AMBER");

    EPIUsePage epiUsePage = refrigeratorPage.navigateToEpiUse();
    epiUsePage.verifyProductGroup("PG1-Name", 1);
    epiUsePage.enterData(10, 20, 30, 40, 50, "10/2011", 1);
    epiUsePage.verifyIndicator("GREEN");

    GeneralObservationPage generalObservationPage = epiUsePage.navigateToGeneralObservations();
    generalObservationPage.enterData("Some observations", "samuel", "Doe", "Verifier", "XYZ");

    CoveragePage coveragePage = generalObservationPage.navigateToCoverage();
    coveragePage.enterData(12, 34, 45, 56);

    EpiInventoryPage epiInventoryPage = coveragePage.navigateToEpiInventory();
    fillEpiInventoryWithOnlyDeliveredQuantity(epiInventoryPage, "2", "4", "6");

    facilityListPage.verifyFacilityIndicatorColor("Overall", "GREEN");

    homePage.navigateHomePage();
    homePage.navigateToDistributionWhenOnline();
    facilityListPage = distributionPage.clickRecordData(1);
    epiUsePage = facilityListPage.selectFacility("F11").navigateToEpiUse();
    facilityListPage.verifyFacilityIndicatorColor("Overall", "AMBER");

    epiUsePage.verifyProductGroup("PG1-Name", 1);
    epiUsePage.verifyIndicator("RED");
    epiUsePage.enterData(10, 20, 30, 40, 50, "10/2011", 1);
    epiUsePage.verifyIndicator("GREEN");

    generalObservationPage = epiUsePage.navigateToGeneralObservations();
    generalObservationPage.enterData("Some other observations", "john", "Doe", "Verifier2", "X Y Z");

    coveragePage = generalObservationPage.navigateToCoverage();
    coveragePage.enterData(12, 34, 45, 56);

    epiInventoryPage =coveragePage.navigateToEpiInventory();
    fillEpiInventoryWithOnlyDeliveredQuantity(epiInventoryPage, "2", "4", "6");

    facilityListPage.verifyFacilityIndicatorColor("Overall", "GREEN");

    homePage.navigateHomePage();
    homePage.navigateToDistributionWhenOnline();
    distributionPage.syncDistribution(1);

    assertTrue(distributionPage.getSyncMessage().contains("F10-Village Dispensary"));
    assertTrue(distributionPage.getSyncMessage().contains("F11-Central Hospital"));

    distributionPage.syncDistributionMessageDone();

    Map<String, String> facilityVisitF10 = dbWrapper.getFacilityVisitDetails("F10");
    assertEquals(facilityVisitF10.get("observations"), "Some observations");
    assertEquals(facilityVisitF10.get("confirmedByName"), "samuel");
    assertEquals(facilityVisitF10.get("confirmedByTitle"), "Doe");
    assertEquals(facilityVisitF10.get("verifiedByName"), "Verifier");
    assertEquals(facilityVisitF10.get("verifiedByTitle"), "XYZ");

    Map<String, String> facilityVisitF11 = dbWrapper.getFacilityVisitDetails("F11");
    assertEquals(facilityVisitF11.get("observations"), "Some other observations");
    assertEquals(facilityVisitF11.get("confirmedByName"), "john");
    assertEquals(facilityVisitF11.get("confirmedByTitle"), "Doe");
    assertEquals(facilityVisitF11.get("verifiedByName"), "Verifier2");
    assertEquals(facilityVisitF11.get("verifiedByTitle"), "X Y Z");

    distributionPage.clickRecordData(1);
    refrigeratorPage = facilityListPage.selectFacility("F10");
    facilityListPage.verifyFacilityIndicatorColor("Overall", "BLUE");
    facilityListPage.verifyFacilityIndicatorColor("individual", "BLUE");

    generalObservationPage = refrigeratorPage.navigateToGeneralObservations();
    generalObservationPage.verifyAllFieldsDisabled();

    epiUsePage = generalObservationPage.navigateToEpiUse();
    epiUsePage.verifyAllFieldsDisabled();

    homePage.navigateToDistributionWhenOnline();
    distributionPage.clickRecordData(1);
    facilityListPage.selectFacility("F11");
    facilityListPage.verifyFacilityIndicatorColor("Overall", "BLUE");

    homePage.navigateToDistributionWhenOnline();
    distributionPage.deleteDistribution();
    distributionPage.clickOk();
    //TODO verify everything in DB  , verify everything disabled on UI and values on UI
  }

  @Test(groups = {"distribution"}, dataProvider = "Data-Provider-Function")
  public void shouldCheckAlreadySyncedFacilities(String userSIC, String password, String deliveryZoneCodeFirst, String deliveryZoneCodeSecond, String deliveryZoneNameFirst,
                                                 String deliveryZoneNameSecond, String facilityCodeFirst, String facilityCodeSecond, String programFirst, String programSecond,
                                                 String schedule) throws Exception {
    setup(userSIC, deliveryZoneCodeFirst, deliveryZoneCodeSecond, deliveryZoneNameFirst, deliveryZoneNameSecond, facilityCodeFirst, facilityCodeSecond, programFirst, programSecond, schedule);

    LoginPage loginPage = new LoginPage(testWebDriver, baseUrlGlobal);
    HomePage homePage = loginPage.loginAs(userSIC, password);
    DistributionPage distributionPage = homePage.navigateToDistributionWhenOnline();

    distributionPage.initiate(deliveryZoneNameFirst, programFirst);

    FacilityListPage facilityListPage = distributionPage.clickRecordData(1);

    fillFacilityData(facilityListPage, facilityCodeFirst);

    homePage.navigateHomePage();
    homePage.navigateToDistributionWhenOnline();
    distributionPage.syncDistribution(1);
    distributionPage.syncDistributionMessageDone();

    distributionPage.deleteDistribution();
    distributionPage.clickOk();

    distributionPage.initiate(deliveryZoneNameFirst, programFirst);
    distributionPage.clickOk();

    facilityListPage = distributionPage.clickRecordData(1);
    fillFacilityData(facilityListPage, facilityCodeFirst);
    fillFacilityData(facilityListPage, facilityCodeSecond);

    homePage.navigateHomePage();
    homePage.navigateToDistributionWhenOnline();
    distributionPage.syncDistribution(1);

    assertEquals(distributionPage.getFacilityAlreadySyncMessage(), "Already synced facilities : \n" + "F10-Village Dispensary");
    assertEquals(distributionPage.getSyncMessage(), "Synced facilities : \n" + "F11-Central Hospital");

    distributionPage.syncDistributionMessageDone();
  }

  @Test(groups = {"distribution"})
  public void testSyncWhenFacilityInactiveAfterCaching(String userSIC, String password, String deliveryZoneCodeFirst, String deliveryZoneCodeSecond, String deliveryZoneNameFirst,
                                                       String deliveryZoneNameSecond, String facilityCodeFirst, String facilityCodeSecond, String programFirst, String programSecond,
                                                       String schedule) throws Exception {
    dbWrapper.updateActiveStatusOfFacility("F10", "false");

    LoginPage loginPage = new LoginPage(testWebDriver, baseUrlGlobal);
    HomePage homePage = loginPage.loginAs(userSIC, password);
    DistributionPage distributionPage = homePage.navigateToDistributionWhenOnline();

    distributionPage.initiate(deliveryZoneNameFirst, programFirst);
    FacilityListPage facilityListPage = distributionPage.clickRecordData(1);

    fillFacilityData(facilityListPage, facilityCodeFirst);

    homePage.navigateToDistributionWhenOnline();
    distributionPage.syncDistribution(1);
    assertTrue(distributionPage.getSyncMessage().contains("F10-Village Dispensary"));
    distributionPage.syncDistributionMessageDone();

    verifyEpiUseDataInDatabase(10, 20, 30, 40, 50, "10/2011","PG-1", facilityCodeFirst);
    //TODO verify everything in DB
    dbWrapper.updateActiveStatusOfFacility("F10", "true");
  }

  @Test(groups = {"distribution"})
  public void testSyncWhenFacilityDisabledAfterCaching(String userSIC, String password, String deliveryZoneCodeFirst, String deliveryZoneCodeSecond, String deliveryZoneNameFirst,
                                                       String deliveryZoneNameSecond, String facilityCodeFirst, String facilityCodeSecond, String programFirst, String programSecond,
                                                       String schedule) throws Exception {

    dbWrapper.updateFacilityFieldBYCode("enabled", "false", "F10");

    LoginPage loginPage = new LoginPage(testWebDriver, baseUrlGlobal);
    HomePage homePage = loginPage.loginAs(userSIC, password);
    DistributionPage distributionPage = homePage.navigateToDistributionWhenOnline();

    distributionPage.initiate(deliveryZoneNameFirst, programFirst);
    FacilityListPage facilityListPage = distributionPage.clickRecordData(1);

    fillFacilityData(facilityListPage, facilityCodeFirst);

    homePage.navigateToDistributionWhenOnline();
    distributionPage.syncDistribution(1);
    assertTrue(distributionPage.getSyncMessage().contains("F10-Village Dispensary"));
    distributionPage.syncDistributionMessageDone();

    verifyEpiUseDataInDatabase(10, 20, 30, 40, 50, "10/2011", "PG-1", facilityCodeFirst);
    //TODO verify everything in DB
    dbWrapper.updateFacilityFieldBYCode("enabled", "true", "F10");
  }

  @Test(groups = {"distribution"})
  public void testSyncWhenAllProgramInactiveAfterCaching(String userSIC, String password, String deliveryZoneCodeFirst, String deliveryZoneCodeSecond, String deliveryZoneNameFirst,
                                                         String deliveryZoneNameSecond, String facilityCodeFirst, String facilityCodeSecond, String programFirst, String programSecond,
                                                         String schedule) throws Exception {

    dbWrapper.updateActiveStatusOfProgram("VACCINES", false);

    LoginPage loginPage = new LoginPage(testWebDriver, baseUrlGlobal);
    HomePage homePage = loginPage.loginAs(userSIC, password);
    DistributionPage distributionPage = homePage.navigateToDistributionWhenOnline();

    distributionPage.initiate(deliveryZoneNameFirst, programFirst);
    FacilityListPage facilityListPage = distributionPage.clickRecordData(1);

    fillFacilityData(facilityListPage, facilityCodeFirst);

    homePage.navigateToDistributionWhenOnline();
    distributionPage.syncDistribution(1);
    assertTrue(distributionPage.getSyncMessage().contains("F10-Village Dispensary"));
    distributionPage.syncDistributionMessageDone();

    verifyEpiUseDataInDatabase(10, 20, 30, 40, 50, "10/2011", "PG-1", facilityCodeFirst);
    //TODO verify everything in DB
    dbWrapper.updateActiveStatusOfProgram("VACCINES", true);
  }

  @Test(groups = {"distribution"})
  public void testSyncWhenProgramDeletedAfterCaching(String userSIC, String password, String deliveryZoneCodeFirst, String deliveryZoneCodeSecond, String deliveryZoneNameFirst,
                                                     String deliveryZoneNameSecond, String facilityCodeFirst, String facilityCodeSecond, String programFirst, String programSecond,
                                                     String schedule) throws Exception {

    dbWrapper.deleteProgramToFacilityMapping("VACCINES");

    LoginPage loginPage = new LoginPage(testWebDriver, baseUrlGlobal);
    HomePage homePage = loginPage.loginAs(userSIC, password);
    DistributionPage distributionPage = homePage.navigateToDistributionWhenOnline();

    distributionPage.initiate(deliveryZoneNameFirst, programFirst);
    FacilityListPage facilityListPage = distributionPage.clickRecordData(1);

    fillFacilityData(facilityListPage, facilityCodeFirst);

    distributionPage.syncDistribution(1);
    assertTrue(distributionPage.getSyncMessage().contains("F10-Village Dispensary"));
    distributionPage.syncDistributionMessageDone();

    verifyEpiUseDataInDatabase(10, 20, 30, 40, 50, "10/2011", "PG-1", facilityCodeFirst);
    //TODO verify everything in DB
  }

  private void setup(String userSIC, String deliveryZoneCodeFirst, String deliveryZoneCodeSecond, String deliveryZoneNameFirst, String deliveryZoneNameSecond, String facilityCodeFirst, String facilityCodeSecond, String programFirst, String programSecond, String schedule) throws Exception {
    List<String> rightsList = new ArrayList<>();
    rightsList.add("MANAGE_DISTRIBUTION");
    setupTestDataToInitiateRnRAndDistribution("F10", "F11", true, programFirst, userSIC, "200", rightsList, programSecond, "District1", "Ngorongoro", "Ngorongoro");
    setupDataForDeliveryZone(true, deliveryZoneCodeFirst, deliveryZoneCodeSecond, deliveryZoneNameFirst, deliveryZoneNameSecond, facilityCodeFirst, facilityCodeSecond, programFirst,
      programSecond, schedule);
    dbWrapper.insertRoleAssignmentForDistribution(userSIC, "store in-charge", deliveryZoneCodeFirst);
    dbWrapper.insertRoleAssignmentForDistribution(userSIC, "store in-charge", deliveryZoneCodeSecond);
    dbWrapper.insertProductGroup("PG1");
    dbWrapper.insertProductWithGroup("Product5", "ProductName5", "PG1", true);
    dbWrapper.insertProductWithGroup("Product6", "ProductName6", "PG1", true);
    dbWrapper.insertProgramProduct("Product5", programFirst, "10", "false");
    dbWrapper.insertProgramProduct("Product6", programFirst, "10", "true");
  }

  private GeneralObservationPage fillFacilityData(FacilityListPage facilityListPage, String facilityCode) {
    RefrigeratorPage refrigeratorPage = facilityListPage.selectFacility(facilityCode);
    EPIUsePage epiUse = refrigeratorPage.navigateToEpiUse();
    epiUse.checkApplyNRToAllFields(true);
    GeneralObservationPage generalObservationPage = epiUse.navigateToGeneralObservations();
    generalObservationPage.enterData("Some observations", "samuel", "Doe", "Verifier", "XYZ");
    EpiInventoryPage epiInventoryPage = generalObservationPage.navigateToEpiInventory();
    fillEpiInventoryWithOnlyDeliveredQuantity(epiInventoryPage, "2", "4", "6");
    CoveragePage coveragePage = epiInventoryPage.navigateToCoverage();
    coveragePage.enterData(23, 66, 77, 45);
    return generalObservationPage;
  }

  public void fillEpiInventoryWithOnlyDeliveredQuantity(EpiInventoryPage epiInventoryPage, String deliveredQuantity1, String deliveredQuantity2, String deliveredQuantity3) {
    epiInventoryPage.applyNRToAll();
    epiInventoryPage.fillDeliveredQuantity(1, deliveredQuantity1);
    epiInventoryPage.fillDeliveredQuantity(2, deliveredQuantity2);
    epiInventoryPage.fillDeliveredQuantity(3, deliveredQuantity3);
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

  @DataProvider(name = "Data-Provider-Function")
  public Object[][] parameterIntTestProviderPositive() {
    return new Object[][]{{"fieldCoordinator", "Admin123", "DZ1", "DZ2", "Delivery Zone First", "Delivery Zone Second", "F10", "F11", "VACCINES", "TB", "M"}};

  }
}

