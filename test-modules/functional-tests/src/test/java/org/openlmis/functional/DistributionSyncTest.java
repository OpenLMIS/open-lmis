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
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

import java.io.IOException;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.thoughtworks.selenium.SeleneseTestBase.assertFalse;
import static com.thoughtworks.selenium.SeleneseTestNgHelper.assertEquals;
import static com.thoughtworks.selenium.SeleneseTestNgHelper.assertTrue;
import static java.util.Arrays.asList;
import static org.testng.Assert.assertNull;

@Listeners(CaptureScreenshotOnFailureListener.class)

public class DistributionSyncTest extends TestCaseHelper {


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

  public Map<String, String> distributionTestData = new HashMap<String, String>() {{
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
    loginPage = PageFactory.getInstanceOfLoginPage(testWebDriver, baseUrlGlobal);
    facilityListPage = PageFactory.getInstanceOfFacilityListPage(testWebDriver);

    Map<String, String> dataMap = distributionTestData;
    setupDataForDistributionTest(dataMap.get(USER), dataMap.get(FIRST_DELIVERY_ZONE_CODE), dataMap.get(SECOND_DELIVERY_ZONE_CODE),
      dataMap.get(FIRST_DELIVERY_ZONE_NAME), dataMap.get(SECOND_DELIVERY_ZONE_NAME), dataMap.get(FIRST_FACILITY_CODE),
      dataMap.get(SECOND_FACILITY_CODE), dataMap.get(VACCINES_PROGRAM), dataMap.get(TB_PROGRAM), dataMap.get(SCHEDULE),
      dataMap.get(PRODUCT_GROUP_CODE));

    dbWrapper.addRefrigeratorToFacility("LG", "800L", "GNR7878", "F10");
    dbWrapper.updateFieldValue("facilities", "active", "true", "code", "F10");
    dbWrapper.updateFieldValue("facilities", "enabled", "true", "code", "F10");
    dbWrapper.updateFieldValue("programs", "active", "true", "code", "VACCINES");
    dbWrapper.updateFieldValue("products", "active", "true", "code", "P10");
    dbWrapper.updateFieldValue("products", "active", "true", "code", "P11");
    dbWrapper.updateFieldValue("products", "active", "true", "code", "Product6");
    dbWrapper.updateFieldValue("products", "active", "true", "code", "Product5");
  }

  @Test(groups = {"distribution"})
  public void testMultipleFacilitySync() throws Exception {
    HomePage homePage = loginPage.loginAs(distributionTestData.get(USER), distributionTestData.get(PASSWORD));
    initiateDistribution(distributionTestData.get(FIRST_DELIVERY_ZONE_NAME), distributionTestData.get(VACCINES_PROGRAM));
    RefrigeratorPage refrigeratorPage = facilityListPage.selectFacility(distributionTestData.get(SECOND_FACILITY_CODE));

    facilityListPage.verifyFacilityIndicatorColor("Overall", "AMBER");

    EPIUsePage epiUsePage = refrigeratorPage.navigateToEpiUse();
    epiUsePage.enterData(70, 80, 90, 100, 9999999, "10/2011", 1);

    GeneralObservationPage generalObservationPage = epiUsePage.navigateToGeneralObservations();
    generalObservationPage.enterData("Some observations", "samuel D", "Doe Abc", "Verifier", "Verifier Title");

    FullCoveragePage fullCoveragePage = generalObservationPage.navigateToFullCoverage();
    fullCoveragePage.clickApplyNRToAll();

    fullCoveragePage.navigateToEpiInventory();
    fillEpiInventoryWithOnlyDeliveredQuantity("2", "4", "6");

    facilityListPage.verifyFacilityIndicatorColor("Overall", "GREEN");

    homePage.navigateHomePage();
    initiateDistribution(distributionTestData.get(FIRST_DELIVERY_ZONE_NAME), distributionTestData.get(VACCINES_PROGRAM));
    facilityListPage = PageFactory.getInstanceOfFacilityListPage(testWebDriver);
    facilityListPage.selectFacility(distributionTestData.get(FIRST_FACILITY_CODE));

    facilityListPage.verifyFacilityIndicatorColor("Overall", "RED");

    fillFacilityData();
    facilityListPage.verifyFacilityIndicatorColor("Overall", "GREEN");

    homePage.navigateHomePage();
    homePage.navigateToDistributionWhenOnline();
    DistributionPage distributionPage = homePage.navigateToDistributionWhenOnline();

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
    assertEquals(facilityVisitF11.get("observations"), "Some observations");
    assertEquals(facilityVisitF11.get("confirmedByName"), "samuel D");
    assertEquals(facilityVisitF11.get("confirmedByTitle"), "Doe Abc");
    assertEquals(facilityVisitF11.get("verifiedByName"), "Verifier");
    assertEquals(facilityVisitF11.get("verifiedByTitle"), "Verifier Title");

    distributionPage.clickRecordData(1);
    facilityListPage.selectFacility("F10");
    facilityListPage.verifyFacilityIndicatorColor("Overall", "BLUE");
    facilityListPage.verifyFacilityIndicatorColor("individual", "BLUE");

    verifyAllFieldsDisabled();

    homePage.navigateToDistributionWhenOnline();
    distributionPage.clickRecordData(1);
    facilityListPage.selectFacility("F11");
    facilityListPage.verifyFacilityIndicatorColor("Overall", "BLUE");

    generalObservationPage = refrigeratorPage.navigateToGeneralObservations();
    generalObservationPage.verifyAllFieldsDisabled();

    epiUsePage = generalObservationPage.navigateToEpiUse();
    epiUsePage.verifyAllFieldsDisabled();

    homePage.navigateToDistributionWhenOnline();
    distributionPage.deleteDistribution();
    distributionPage.clickOk();

    verifyEpiUseDataInDatabase(70, 80, 90, 100, 9999999, "10/2011", "PG1", distributionTestData.get(SECOND_FACILITY_CODE));
    verifyGeneralObservationsDataInDatabase(distributionTestData.get(SECOND_FACILITY_CODE), "Some observations", "samuel D", "Doe Abc", "Verifier", "Verifier Title");
    verifyFullCoveragesDataInDatabase(null, null, null, null, distributionTestData.get(SECOND_FACILITY_CODE));
    verifyEpiInventoryDataInDatabase(null, "2", null, "P10", distributionTestData.get(SECOND_FACILITY_CODE));
    verifyEpiInventoryDataInDatabase(null, "4", null, "Product6", distributionTestData.get(SECOND_FACILITY_CODE));
    verifyEpiInventoryDataInDatabase(null, "6", null, "P11", distributionTestData.get(SECOND_FACILITY_CODE));

    verifySyncedDataInDatabase(distributionTestData.get(FIRST_FACILITY_CODE));
  }

  @Test(groups = {"distribution"})
  public void shouldCheckAlreadySyncedFacilities() throws Exception {
    dbWrapper.addRefrigeratorToFacility("LG", "800L", "GNR7878", "F11");
    HomePage homePage = loginPage.loginAs(distributionTestData.get(USER), distributionTestData.get(PASSWORD));
    initiateDistribution(distributionTestData.get(FIRST_DELIVERY_ZONE_NAME), distributionTestData.get(VACCINES_PROGRAM));
    RefrigeratorPage refrigeratorPage = facilityListPage.selectFacility(distributionTestData.get(FIRST_FACILITY_CODE));

    facilityListPage.verifyFacilityIndicatorColor("Overall", "RED");

    refrigeratorPage.clickShowForRefrigerator1();
    refrigeratorPage.enterValueInRefrigeratorTemperature("3");
    refrigeratorPage.clickFunctioningCorrectlyYesRadio();
    refrigeratorPage.enterValueInLowAlarmEvents("2");
    refrigeratorPage.enterValueInHighAlarmEvents("5");
    refrigeratorPage.clickProblemSinceLastVisitNR();
    refrigeratorPage.verifyRefrigeratorColor("overall", "GREEN");
    refrigeratorPage.verifyRefrigeratorColor("individual", "GREEN");
    refrigeratorPage.clickDone();

    EPIUsePage epiUsePage = refrigeratorPage.navigateToEpiUse();
    epiUsePage.enterData(70, 80, 90, 100, 9999999, "10/2011", 1);

    GeneralObservationPage generalObservationPage = epiUsePage.navigateToGeneralObservations();
    generalObservationPage.enterData("Some observations", "samuel D", "Doe Abc", "Verifier", "Verifier Title");

    FullCoveragePage fullCoveragePage = generalObservationPage.navigateToFullCoverage();
    fullCoveragePage.clickApplyNRToAll();

    fullCoveragePage.navigateToEpiInventory();
    fillEpiInventoryWithOnlyDeliveredQuantity("2", "4", "6");
    facilityListPage.verifyFacilityIndicatorColor("Overall", "GREEN");

    homePage.navigateHomePage();
    DistributionPage distributionPage = homePage.navigateToDistributionWhenOnline();
    distributionPage.syncDistribution(1);
    distributionPage.syncDistributionMessageDone();

    verifyEpiUseDataInDatabase(70, 80, 90, 100, 9999999, "10/2011", "PG1", distributionTestData.get(FIRST_FACILITY_CODE));
    verifyGeneralObservationsDataInDatabase(distributionTestData.get(FIRST_FACILITY_CODE), "Some observations", "samuel D", "Doe Abc", "Verifier", "Verifier Title");
    verifyRefrigeratorProblemDataNullInDatabase("GNR7878", distributionTestData.get(FIRST_FACILITY_CODE));
    verifyRefrigeratorReadingDataInDatabase(distributionTestData.get(FIRST_FACILITY_CODE), "GNR7878", 3.0F, "Y", 2, 5, null, null);
    verifyFullCoveragesDataInDatabase(null, null, null, null, distributionTestData.get(FIRST_FACILITY_CODE));
    verifyEpiInventoryDataInDatabase(null, "2", null, "P10", distributionTestData.get(FIRST_FACILITY_CODE));
    verifyEpiInventoryDataInDatabase(null, "4", null, "Product6", distributionTestData.get(FIRST_FACILITY_CODE));
    verifyEpiInventoryDataInDatabase(null, "6", null, "P11", distributionTestData.get(FIRST_FACILITY_CODE));

    distributionPage.deleteDistribution();
    distributionPage.clickOk();

    distributionPage.selectValueFromDeliveryZone(distributionTestData.get(FIRST_DELIVERY_ZONE_NAME));
    distributionPage.selectValueFromProgram(distributionTestData.get(VACCINES_PROGRAM));
    distributionPage.clickInitiateDistribution();
    distributionPage.clickOk();

    facilityListPage = distributionPage.clickRecordData(1);
    assertFalse(facilityListPage.getFacilitiesInDropDown().contains(distributionTestData.get(FIRST_FACILITY_CODE)));

    facilityListPage.selectFacility(distributionTestData.get(SECOND_FACILITY_CODE));

    fillFacilityData();

    homePage.navigateHomePage();
    homePage.navigateToDistributionWhenOnline();
    distributionPage.syncDistribution(1);

    assertEquals(distributionPage.getSyncMessage(), "Synced facilities : \n" + "F11-Central Hospital");

    distributionPage.syncDistributionMessageDone();

    verifyEpiUseDataInDatabase(70, 80, 90, 100, 9999999, "10/2011", "PG1", distributionTestData.get(FIRST_FACILITY_CODE));
    verifyGeneralObservationsDataInDatabase(distributionTestData.get(FIRST_FACILITY_CODE), "Some observations", "samuel D", "Doe Abc", "Verifier", "Verifier Title");
    verifyRefrigeratorProblemDataNullInDatabase("GNR7878", distributionTestData.get(FIRST_FACILITY_CODE));
    verifyRefrigeratorReadingDataInDatabase(distributionTestData.get(FIRST_FACILITY_CODE), "GNR7878", 3.0F, "Y", 2, 5, null, null);

    verifySyncedDataInDatabase(distributionTestData.get(SECOND_FACILITY_CODE));
  }

  @Test(groups = {"distribution"})
  public void testSyncWhenFacilityInactiveAfterCaching() throws Exception {
    HomePage homePage = loginPage.loginAs(distributionTestData.get(USER), distributionTestData.get(PASSWORD));

    initiateDistribution(distributionTestData.get(FIRST_DELIVERY_ZONE_NAME), distributionTestData.get(VACCINES_PROGRAM));
    dbWrapper.updateFieldValue("facilities", "active", "false", "code", distributionTestData.get(FIRST_FACILITY_CODE));
    assertTrue(facilityListPage.getFacilitiesInDropDown().contains(distributionTestData.get(FIRST_FACILITY_CODE)));
    deleteDistribution();

    initiateNextDistributionForSamePeriod(distributionTestData.get(FIRST_DELIVERY_ZONE_NAME), distributionTestData.get(VACCINES_PROGRAM));
    assertTrue(facilityListPage.getFacilitiesInDropDown().contains(distributionTestData.get(FIRST_FACILITY_CODE)));

    facilityListPage.selectFacility(distributionTestData.get(FIRST_FACILITY_CODE));
    verifyProductsAreDisplayed();

    facilityListPage.verifyFacilityIndicatorColor("Overall", "RED");

    fillFacilityData();

    DistributionPage distributionPage = homePage.navigateToDistributionWhenOnline();
    distributionPage.syncDistribution(1);
    assertTrue(distributionPage.getSyncMessage().contains("F10-Village Dispensary"));
    distributionPage.syncDistributionMessageDone();

    verifySyncedDataInDatabase(distributionTestData.get(FIRST_FACILITY_CODE));

    deleteDistribution();

    initiateNextDistributionForSamePeriod(distributionTestData.get(FIRST_DELIVERY_ZONE_NAME), distributionTestData.get(VACCINES_PROGRAM));
    assertFalse(facilityListPage.getFacilitiesInDropDown().contains(distributionTestData.get(FIRST_FACILITY_CODE)));
    deleteDistribution();

    initiateNextDistributionForGivenPeriod(distributionTestData.get(FIRST_DELIVERY_ZONE_NAME), distributionTestData.get(VACCINES_PROGRAM), "Period13");
    assertFalse(facilityListPage.getFacilitiesInDropDown().contains(distributionTestData.get(FIRST_FACILITY_CODE)));

    //dbWrapper.updateActiveStatusOfFacility("F10", "true");
  }

  @Test(groups = {"distribution"})
  public void testSyncWhenFacilityDisabledAfterCaching() throws Exception {
    HomePage homePage = loginPage.loginAs(distributionTestData.get(USER), distributionTestData.get(PASSWORD));

    initiateDistribution(distributionTestData.get(FIRST_DELIVERY_ZONE_NAME), distributionTestData.get(VACCINES_PROGRAM));
    dbWrapper.updateFieldValue("facilities", "enabled", "false", "code", "F10");
    dbWrapper.updateFieldValue("facilities", "active", "false", "code", distributionTestData.get(FIRST_FACILITY_CODE));
    assertTrue(facilityListPage.getFacilitiesInDropDown().contains(distributionTestData.get(FIRST_FACILITY_CODE)));
    deleteDistribution();

    initiateNextDistributionForSamePeriod(distributionTestData.get(FIRST_DELIVERY_ZONE_NAME), distributionTestData.get(VACCINES_PROGRAM));
    assertTrue(facilityListPage.getFacilitiesInDropDown().contains(distributionTestData.get(FIRST_FACILITY_CODE)));

    facilityListPage.selectFacility(distributionTestData.get(FIRST_FACILITY_CODE));
    verifyProductsAreDisplayed();

    facilityListPage.verifyFacilityIndicatorColor("Overall", "RED");

    fillFacilityData();

    DistributionPage distributionPage = homePage.navigateToDistributionWhenOnline();
    distributionPage.syncDistribution(1);
    assertTrue(distributionPage.getSyncMessage().contains("F10-Village Dispensary"));
    distributionPage.syncDistributionMessageDone();

    verifySyncedDataInDatabase(distributionTestData.get(FIRST_FACILITY_CODE));

    deleteDistribution();

    initiateNextDistributionForSamePeriod(distributionTestData.get(FIRST_DELIVERY_ZONE_NAME), distributionTestData.get(VACCINES_PROGRAM));
    assertFalse(facilityListPage.getFacilitiesInDropDown().contains(distributionTestData.get(FIRST_FACILITY_CODE)));
    deleteDistribution();

    initiateNextDistributionForGivenPeriod(distributionTestData.get(FIRST_DELIVERY_ZONE_NAME), distributionTestData.get(VACCINES_PROGRAM), "Period13");
    assertFalse(facilityListPage.getFacilitiesInDropDown().contains(distributionTestData.get(FIRST_FACILITY_CODE)));

    //dbWrapper.updateFacilityFieldBYCode("enabled", "true", "F10");
    //dbWrapper.updateActiveStatusOfFacility(distributionTestData.get(FIRST_FACILITY_CODE), "true");
  }

  @Test(groups = {"distribution"})
  public void testSyncWhenAllProgramInactiveAfterCaching() throws Exception {
    HomePage homePage = loginPage.loginAs(distributionTestData.get(USER), distributionTestData.get(PASSWORD));

    initiateDistribution(distributionTestData.get(FIRST_DELIVERY_ZONE_NAME), distributionTestData.get(VACCINES_PROGRAM));
    dbWrapper.updateFieldValue("programs", "active", "false", "code", "VACCINES");

    assertTrue(facilityListPage.getFacilitiesInDropDown().contains(distributionTestData.get(FIRST_FACILITY_CODE)));

    facilityListPage.selectFacility(distributionTestData.get(FIRST_FACILITY_CODE));
    verifyProductsAreDisplayed();

    facilityListPage.verifyFacilityIndicatorColor("Overall", "RED");

    fillFacilityData();

    DistributionPage distributionPage = homePage.navigateToDistributionWhenOnline();
    distributionPage.syncDistribution(1);
    assertTrue(distributionPage.getSyncMessage().contains("F10-Village Dispensary"));
    distributionPage.syncDistributionMessageDone();

    verifySyncedDataInDatabase(distributionTestData.get(FIRST_FACILITY_CODE));

    deleteDistribution();

    distributionPage.selectValueFromDeliveryZone(distributionTestData.get(FIRST_DELIVERY_ZONE_NAME));
    assertFalse(distributionPage.getAllSelectOptionsFromProgram().contains(distributionTestData.get(VACCINES_PROGRAM)));

    //dbWrapper.updateActiveStatusOfProgram("VACCINES", true);
  }

  @Test(groups = {"distribution"})
  public void testSyncWhenProgramDeletedAfterCaching() throws Exception {
    HomePage homePage = loginPage.loginAs(distributionTestData.get(USER), distributionTestData.get(PASSWORD));

    initiateDistribution(distributionTestData.get(FIRST_DELIVERY_ZONE_NAME), distributionTestData.get(VACCINES_PROGRAM));
    dbWrapper.deleteProgramToFacilityMapping("VACCINES");

    assertTrue(facilityListPage.getFacilitiesInDropDown().contains(distributionTestData.get(FIRST_FACILITY_CODE)));
    facilityListPage.selectFacility(distributionTestData.get(FIRST_FACILITY_CODE));

    facilityListPage.verifyFacilityIndicatorColor("Overall", "RED");
    verifyProductsAreDisplayed();

    fillFacilityData();

    DistributionPage distributionPage = homePage.navigateToDistributionWhenOnline();
    distributionPage.syncDistribution(1);
    assertTrue(distributionPage.getSyncMessage().contains("F10-Village Dispensary"));
    distributionPage.syncDistributionMessageDone();

    verifySyncedDataInDatabase(distributionTestData.get(FIRST_FACILITY_CODE));

    deleteDistribution();

    distributionPage.selectValueFromDeliveryZone(distributionTestData.get(FIRST_DELIVERY_ZONE_NAME));
    assertFalse(distributionPage.getAllSelectOptionsFromProgram().contains(distributionTestData.get(VACCINES_PROGRAM)));
  }

  @Test(groups = {"distribution"})
  public void testSyncWhenAllProductsInactiveAfterCaching() throws Exception {
    HomePage homePage = loginPage.loginAs(distributionTestData.get(USER), distributionTestData.get(PASSWORD));

    initiateDistribution(distributionTestData.get(FIRST_DELIVERY_ZONE_NAME), distributionTestData.get(VACCINES_PROGRAM));
    dbWrapper.updateFieldValue("products", "active", "false", "code", "P10");
    dbWrapper.updateFieldValue("products", "active", "false", "code", "P11");
    dbWrapper.updateFieldValue("products", "active", "false", "code", "Product6");
    dbWrapper.updateFieldValue("products", "active", "false", "code", "Product5");

    assertTrue(facilityListPage.getFacilitiesInDropDown().contains(distributionTestData.get(FIRST_FACILITY_CODE)));
    deleteDistribution();

    initiateNextDistributionForSamePeriod(distributionTestData.get(FIRST_DELIVERY_ZONE_NAME), distributionTestData.get(VACCINES_PROGRAM));
    assertTrue(facilityListPage.getFacilitiesInDropDown().contains(distributionTestData.get(FIRST_FACILITY_CODE)));
    facilityListPage.selectFacility(distributionTestData.get(FIRST_FACILITY_CODE));

    facilityListPage.verifyFacilityIndicatorColor("Overall", "RED");
    verifyProductsAreDisplayed();

    fillFacilityData();

    DistributionPage distributionPage = homePage.navigateToDistributionWhenOnline();
    distributionPage.syncDistribution(1);
    assertTrue(distributionPage.getSyncMessage().contains("F10-Village Dispensary"));
    distributionPage.syncDistributionMessageDone();

    verifySyncedDataInDatabase(distributionTestData.get(FIRST_FACILITY_CODE));

    deleteDistribution();

    initiateNextDistributionForSamePeriod(distributionTestData.get(FIRST_DELIVERY_ZONE_NAME), distributionTestData.get(VACCINES_PROGRAM));
    assertFalse(facilityListPage.getFacilitiesInDropDown().contains(distributionTestData.get(FIRST_FACILITY_CODE)));
    deleteDistribution();

    initiateNextDistributionForGivenPeriod(distributionTestData.get(FIRST_DELIVERY_ZONE_NAME), distributionTestData.get(VACCINES_PROGRAM), "Period13");
    assertTrue(facilityListPage.getFacilitiesInDropDown().contains(distributionTestData.get(FIRST_FACILITY_CODE)));
    RefrigeratorPage refrigeratorPage = facilityListPage.selectFacility(distributionTestData.get(FIRST_FACILITY_CODE));
    facilityListPage.verifyFacilityIndicatorColor("Overall", "AMBER");
    verifyProductsAreNotDisplayed();

    refrigeratorPage.navigateToRefrigerators();
    String data = "SAM;800L;GNR7876";
    String[] refrigeratorDetails = data.split(";");

    for (int i = 0; i < refrigeratorDetails.length; i++) {
      assertEquals(testWebDriver.getElementByXpath("//div[@class='list-row ng-scope']/ng-include/form/div[1]/div[" + (i + 2) + "]").getText(), refrigeratorDetails[i]);
    }

    //dbWrapper.updateActiveStatusOfProduct("Product5", "true");
    //dbWrapper.updateActiveStatusOfProduct("Product6", "true");
    //dbWrapper.updateActiveStatusOfProduct("P10", "true");
    //dbWrapper.updateActiveStatusOfProduct("P11", "true");
  }

  @Test(groups = {"distribution"})
  public void testSyncWhenAllProductsAreInactive() throws Exception {
    dbWrapper.updateFieldValue("products", "active", "false", "code", "P10");
    dbWrapper.updateFieldValue("products", "active", "false", "code", "P11");
    dbWrapper.updateFieldValue("products", "active", "false", "code", "Product6");
    dbWrapper.updateFieldValue("products", "active", "false", "code", "Product5");

    HomePage homePage = loginPage.loginAs(distributionTestData.get(USER), distributionTestData.get(PASSWORD));
    initiateDistribution(distributionTestData.get(FIRST_DELIVERY_ZONE_NAME), distributionTestData.get(VACCINES_PROGRAM));
    RefrigeratorPage refrigeratorPage = facilityListPage.selectFacility(distributionTestData.get(FIRST_FACILITY_CODE));

    refrigeratorPage.clickShowForRefrigerator1();
    refrigeratorPage.enterValueInRefrigeratorTemperature("3");
    refrigeratorPage.clickFunctioningCorrectlyYesRadio();
    refrigeratorPage.enterValueInLowAlarmEvents("2");
    refrigeratorPage.enterValueInHighAlarmEvents("5");
    refrigeratorPage.clickProblemSinceLastVisitNR();
    refrigeratorPage.verifyRefrigeratorColor("overall", "GREEN");
    refrigeratorPage.verifyRefrigeratorColor("individual", "GREEN");
    refrigeratorPage.clickDone();

    GeneralObservationPage generalObservationPage = refrigeratorPage.navigateToGeneralObservations();
    generalObservationPage.enterData("Some observations", "samuel D", "Doe Abc", "Verifier", "Verifier Title");

    FullCoveragePage fullCoveragePage = generalObservationPage.navigateToFullCoverage();
    fullCoveragePage.clickApplyNRToAll();

    verifyProductsAreNotDisplayed();
    facilityListPage.verifyFacilityIndicatorColor("Overall", "GREEN");

    homePage.navigateHomePage();
    DistributionPage distributionPage = homePage.navigateToDistributionWhenOnline();
    distributionPage.syncDistribution(1);
    distributionPage.syncDistributionMessageDone();

    verifyGeneralObservationsDataInDatabase(distributionTestData.get(FIRST_FACILITY_CODE), "Some observations", "samuel D", "Doe Abc", "Verifier", "Verifier Title");
    verifyRefrigeratorProblemDataNullInDatabase("GNR7878", distributionTestData.get(FIRST_FACILITY_CODE));
    verifyRefrigeratorReadingDataInDatabase(distributionTestData.get(FIRST_FACILITY_CODE), "GNR7878", 3.0F, "Y", 2, 5, null, null);
    verifyFullCoveragesDataInDatabase(null, null, null, null, distributionTestData.get(FIRST_FACILITY_CODE));

    //dbWrapper.updateActiveStatusOfProduct("P10","true");
    //dbWrapper.updateActiveStatusOfProduct("P11","true");
    //dbWrapper.updateActiveStatusOfProduct("Product5","true");
    //dbWrapper.updateActiveStatusOfProduct("Product6","true");
  }

  @Test(groups = {"distribution"})
  public void testSyncWhenProductGroupAddedAfterCaching() throws Exception {
    HomePage homePage = loginPage.loginAs(distributionTestData.get(USER), distributionTestData.get(PASSWORD));

    initiateDistribution(distributionTestData.get(FIRST_DELIVERY_ZONE_NAME), distributionTestData.get(VACCINES_PROGRAM));
    dbWrapper.insertProductGroup("PG2");
    dbWrapper.insertProductWithGroup("Product7", "ProductName7", "PG2", true);
    dbWrapper.insertProgramProduct("Product7", distributionTestData.get(VACCINES_PROGRAM), "10", "true");

    assertTrue(facilityListPage.getFacilitiesInDropDown().contains(distributionTestData.get(FIRST_FACILITY_CODE)));
    deleteDistribution();

    initiateNextDistributionForSamePeriod(distributionTestData.get(FIRST_DELIVERY_ZONE_NAME), distributionTestData.get(VACCINES_PROGRAM));
    assertTrue(facilityListPage.getFacilitiesInDropDown().contains(distributionTestData.get(FIRST_FACILITY_CODE)));
    RefrigeratorPage refrigeratorPage = facilityListPage.selectFacility(distributionTestData.get(FIRST_FACILITY_CODE));

    facilityListPage.verifyFacilityIndicatorColor("Overall", "RED");
    verifyProductsAreDisplayed();

    fillFacilityData();

    DistributionPage distributionPage = homePage.navigateToDistributionWhenOnline();
    distributionPage.syncDistribution(1);
    assertTrue(distributionPage.getSyncMessage().contains("F10-Village Dispensary"));
    distributionPage.syncDistributionMessageDone();

    verifyEpiUseDataInDatabase(10, 20, 30, 40, 50, "10/2011", distributionTestData.get(PRODUCT_GROUP_CODE), distributionTestData.get(FIRST_FACILITY_CODE));

    deleteDistribution();

    initiateNextDistributionForSamePeriod(distributionTestData.get(FIRST_DELIVERY_ZONE_NAME), distributionTestData.get(VACCINES_PROGRAM));
    assertFalse(facilityListPage.getFacilitiesInDropDown().contains(distributionTestData.get(FIRST_FACILITY_CODE)));
    deleteDistribution();

    initiateNextDistributionForGivenPeriod(distributionTestData.get(FIRST_DELIVERY_ZONE_NAME), distributionTestData.get(VACCINES_PROGRAM), "Period13");
    assertTrue(facilityListPage.getFacilitiesInDropDown().contains(distributionTestData.get(FIRST_FACILITY_CODE)));
    facilityListPage.selectFacility(distributionTestData.get(FIRST_FACILITY_CODE));
    facilityListPage.verifyFacilityIndicatorColor("Overall", "RED");

    EPIUsePage epiUsePage = refrigeratorPage.navigateToEpiUse();
    assertTrue(epiUsePage.getProductGroup(1).equals("PG1-Name"));
    assertTrue(epiUsePage.getProductGroup(2).equals("PG2-Name"));
    assertNull(epiUsePage.getNoProductsAddedMessage());

    EpiInventoryPage epiInventoryPage = epiUsePage.navigateToEpiInventory();
    assertTrue(epiInventoryPage.getProductName(1).equals("antibiotic"));
    assertTrue(epiInventoryPage.getProductName(2).equals("ProductName6"));
    assertTrue(epiInventoryPage.getProductName(3).equals("ProductName7"));
    assertTrue(epiInventoryPage.getProductName(4).equals("antibiotic"));
    assertNull(epiInventoryPage.getNoProductsAddedMessage());

    epiInventoryPage.navigateToRefrigerators();
    ManageRefrigerator manageRefrigerator = new ManageRefrigerator();
    manageRefrigerator.verifyRefrigeratorAdded("SAM;800L;GNR7876");
  }

  @Test(groups = {"distribution"})
  public void testSyncWhenProductWithNoProductGroupAddedAfterCaching() throws Exception {
    HomePage homePage = loginPage.loginAs(distributionTestData.get(USER), distributionTestData.get(PASSWORD));

    initiateDistribution(distributionTestData.get(FIRST_DELIVERY_ZONE_NAME), distributionTestData.get(VACCINES_PROGRAM));
    dbWrapper.insertOneProduct("Product7");
    dbWrapper.insertProductWithGroup("Product9", "Product9", "PG1", true);
    dbWrapper.insertProgramProduct("Product7", "VACCINES", "10", "true");
    dbWrapper.insertProgramProduct("Product9", distributionTestData.get(VACCINES_PROGRAM), "10", "true");

    assertTrue(facilityListPage.getFacilitiesInDropDown().contains(distributionTestData.get(FIRST_FACILITY_CODE)));
    deleteDistribution();

    initiateNextDistributionForSamePeriod(distributionTestData.get(FIRST_DELIVERY_ZONE_NAME), distributionTestData.get(VACCINES_PROGRAM));
    assertTrue(facilityListPage.getFacilitiesInDropDown().contains(distributionTestData.get(FIRST_FACILITY_CODE)));
    RefrigeratorPage refrigeratorPage = facilityListPage.selectFacility(distributionTestData.get(FIRST_FACILITY_CODE));

    facilityListPage.verifyFacilityIndicatorColor("Overall", "RED");
    verifyProductsAreDisplayed();

    fillFacilityData();

    DistributionPage distributionPage = homePage.navigateToDistributionWhenOnline();
    distributionPage.syncDistribution(1);
    assertTrue(distributionPage.getSyncMessage().contains("F10-Village Dispensary"));
    distributionPage.syncDistributionMessageDone();

    verifyEpiUseDataInDatabase(10, 20, 30, 40, 50, "10/2011", distributionTestData.get(PRODUCT_GROUP_CODE), distributionTestData.get(FIRST_FACILITY_CODE));

    deleteDistribution();

    initiateNextDistributionForSamePeriod(distributionTestData.get(FIRST_DELIVERY_ZONE_NAME), distributionTestData.get(VACCINES_PROGRAM));
    assertFalse(facilityListPage.getFacilitiesInDropDown().contains(distributionTestData.get(FIRST_FACILITY_CODE)));
    deleteDistribution();

    initiateNextDistributionForGivenPeriod(distributionTestData.get(FIRST_DELIVERY_ZONE_NAME), distributionTestData.get(VACCINES_PROGRAM), "Period13");
    assertTrue(facilityListPage.getFacilitiesInDropDown().contains(distributionTestData.get(FIRST_FACILITY_CODE)));
    facilityListPage.selectFacility(distributionTestData.get(FIRST_FACILITY_CODE));
    facilityListPage.verifyFacilityIndicatorColor("Overall", "RED");

    EPIUsePage epiUsePage = refrigeratorPage.navigateToEpiUse();
    assertTrue(epiUsePage.getProductGroup(1).equals("PG1-Name"));
    //assertNull(epiUsePage.getProductGroup(2));
    assertNull(epiUsePage.getNoProductsAddedMessage());

    EpiInventoryPage epiInventoryPage = epiUsePage.navigateToEpiInventory();
    assertTrue(epiInventoryPage.getProductName(1).equals("antibiotic"));
    assertTrue(epiInventoryPage.getProductName(2).equals("ProductName6"));
    assertTrue(epiInventoryPage.getProductName(3).equals("Product9"));
    assertTrue(epiInventoryPage.getProductName(4).equals("antibiotic"));
    assertTrue(epiInventoryPage.getProductName(5).equals("antibiotic"));
    assertNull(epiInventoryPage.getNoProductsAddedMessage());
  }


  public void setupDataForDistributionTest(String userSIC, String deliveryZoneCodeFirst, String deliveryZoneCodeSecond,
                                           String deliveryZoneNameFirst, String deliveryZoneNameSecond,
                                           String facilityCodeFirst, String facilityCodeSecond,
                                           String programFirst, String programSecond, String schedule, String productGroupCode) throws Exception {
    List<String> rightsList = asList("MANAGE_DISTRIBUTION");
    setupTestDataToInitiateRnRAndDistribution(facilityCodeFirst, facilityCodeSecond, true, programFirst, userSIC, "200", rightsList,
      programSecond, "District1", "Ngorongoro", "Ngorongoro");
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

  private GeneralObservationPage fillFacilityData() {
    RefrigeratorPage refrigeratorPage = PageFactory.getInstanceOfRefrigeratorPage(testWebDriver);
    refrigeratorPage.navigateToRefrigerators();
    refrigeratorPage.clickDelete();
    refrigeratorPage.clickOKButton();

    refrigeratorPage.clickAddNew();
    refrigeratorPage.addNewRefrigerator("SAM", "800L", "GNR7876");

    refrigeratorPage.clickShowForRefrigerator1();
    refrigeratorPage.enterValueInRefrigeratorTemperature("3");
    refrigeratorPage.clickFunctioningCorrectlyYesRadio();
    refrigeratorPage.enterValueInLowAlarmEvents("2");
    refrigeratorPage.enterValueInHighAlarmEvents("5");
    refrigeratorPage.clickProblemSinceLastVisitNR();
    refrigeratorPage.clickDone();
    refrigeratorPage.verifyRefrigeratorColor("overall", "GREEN");
    refrigeratorPage.verifyRefrigeratorColor("individual", "GREEN");

    EPIUsePage epiUsePage = refrigeratorPage.navigateToEpiUse();
    epiUsePage.enterData(10, 20, 30, 40, 50, "10/2011", 1);

    GeneralObservationPage generalObservationPage = epiUsePage.navigateToGeneralObservations();
    generalObservationPage.enterData("Some observations", "samuel", "Doe", "Verifier", "XYZ");

    EpiInventoryPage epiInventoryPage = generalObservationPage.navigateToEpiInventory();
    fillEpiInventoryWithOnlyDeliveredQuantity("2", "4", "6");

    FullCoveragePage fullCoveragePage = epiInventoryPage.navigateToFullCoverage();
    fullCoveragePage.enterData(23, 66, 77, "45");

    return generalObservationPage;
  }

  private void verifySyncedDataInDatabase(String facilityCode) throws SQLException {
    verifyRefrigeratorProblemDataNullInDatabase("GNR7876", facilityCode);
    verifyRefrigeratorReadingDataInDatabase(facilityCode, "GNR7876", 3.0F, "Y", 2, 5, null, null);

    verifyEpiUseDataInDatabase(10, 20, 30, 40, 50, "10/2011", "PG1", facilityCode);

    verifyGeneralObservationsDataInDatabase(facilityCode, "Some observations", "samuel", "Doe", "Verifier", "XYZ");

    verifyFullCoveragesDataInDatabase(23, 66, 77, 45, facilityCode);

    verifyEpiInventoryDataInDatabase(null, "2", null, "P10", facilityCode);
    verifyEpiInventoryDataInDatabase(null, "4", null, "Product6", facilityCode);
    verifyEpiInventoryDataInDatabase(null, "6", null, "P11", facilityCode);
  }

  public void fillEpiInventoryWithOnlyDeliveredQuantity(String deliveredQuantity1, String deliveredQuantity2, String deliveredQuantity3) {
    EpiInventoryPage epiInventoryPage = PageFactory.getInstanceOfEpiInventoryPage(testWebDriver);
    epiInventoryPage.applyNRToAll();
    epiInventoryPage.fillDeliveredQuantity(1, deliveredQuantity1);
    epiInventoryPage.fillDeliveredQuantity(2, deliveredQuantity2);
    epiInventoryPage.fillDeliveredQuantity(3, deliveredQuantity3);
  }

  public void initiateDistribution(String deliveryZoneNameFirst, String programFirst) throws IOException {

    HomePage homePage = PageFactory.getInstanceOfHomePage(testWebDriver);
    DistributionPage distributionPage = homePage.navigateToDistributionWhenOnline();
    distributionPage.selectValueFromDeliveryZone(deliveryZoneNameFirst);
    distributionPage.selectValueFromProgram(programFirst);
    distributionPage.clickInitiateDistribution();
    distributionPage.clickRecordData(1);
  }

  public void initiateNextDistributionForGivenPeriod(String deliveryZoneNameFirst, String programFirst, String period) throws IOException {
    DistributionPage distributionPage = PageFactory.getInstanceOfDistributionPage(testWebDriver);
    distributionPage.selectValueFromDeliveryZone(deliveryZoneNameFirst);
    distributionPage.selectValueFromProgram(programFirst);
    distributionPage.selectValueFromPeriod(period);
    distributionPage.clickInitiateDistribution();
    distributionPage.clickRecordData(1);
  }

  private void initiateNextDistributionForSamePeriod(String deliveryZoneNameFirst, String programFirst) throws IOException {
    DistributionPage distributionPage = PageFactory.getInstanceOfDistributionPage(testWebDriver);
    distributionPage.selectValueFromDeliveryZone(deliveryZoneNameFirst);
    distributionPage.selectValueFromProgram(programFirst);
    distributionPage.clickInitiateDistribution();
    distributionPage.clickOk();
    distributionPage.clickRecordData(1);
  }

  private void deleteDistribution() throws IOException {
    HomePage homePage = PageFactory.getInstanceOfHomePage(testWebDriver);
    DistributionPage distributionPage = homePage.navigateToDistributionWhenOnline();
    distributionPage.deleteDistribution();
    distributionPage.clickOk();
  }

  private void verifyProductsAreDisplayed() {
    RefrigeratorPage refrigeratorPage = PageFactory.getInstanceOfRefrigeratorPage(testWebDriver);
    EPIUsePage epiUsePage = refrigeratorPage.navigateToEpiUse();
    assertTrue(epiUsePage.getProductGroup(1).equals("PG1-Name"));
    //assertNull(epiUsePage.getProductGroup(2));
    assertNull(epiUsePage.getNoProductsAddedMessage());
    EpiInventoryPage epiInventoryPage = epiUsePage.navigateToEpiInventory();
    assertTrue(epiInventoryPage.getProductName(1).equals("antibiotic"));
    assertTrue(epiInventoryPage.getProductName(2).equals("ProductName6"));
    assertTrue(epiInventoryPage.getProductName(3).equals("antibiotic"));
    //assertNull(epiInventoryPage.getProductName(4));
    assertNull(epiInventoryPage.getNoProductsAddedMessage());
  }

  private void verifyProductsAreNotDisplayed() {
    RefrigeratorPage refrigeratorPage = PageFactory.getInstanceOfRefrigeratorPage(testWebDriver);
    EPIUsePage epiUsePage = refrigeratorPage.navigateToEpiUse();
    //assertNull(epiUsePage.getProductGroup(1));
    //assertNull(epiUsePage.getProductGroup(2));
    assertTrue(epiUsePage.getNoProductsAddedMessage().contains("No products added"));
    epiUsePage.verifyIndicator("GREEN");
    EpiInventoryPage epiInventoryPage = epiUsePage.navigateToEpiInventory();
    //assertNull(epiInventoryPage.getProductName(1));
    //assertNull(epiInventoryPage.getProductName(2));
    //assertNull(epiInventoryPage.getProductName(3));
    assertTrue(epiInventoryPage.getNoProductsAddedMessage().contains("No products added"));
    epiInventoryPage.verifyIndicator("GREEN");
  }

  private void verifyAllFieldsDisabled() {
    RefrigeratorPage refrigeratorPage = PageFactory.getInstanceOfRefrigeratorPage(testWebDriver);
    refrigeratorPage.clickShowForRefrigerator1();
    refrigeratorPage.verifyAllFieldsDisabled();

    GeneralObservationPage generalObservationPage = refrigeratorPage.navigateToGeneralObservations();
    generalObservationPage.verifyAllFieldsDisabled();

    EPIUsePage epiUsePage = generalObservationPage.navigateToEpiUse();
    epiUsePage.verifyAllFieldsDisabled();

    FullCoveragePage fullCoveragePage = epiUsePage.navigateToFullCoverage();
    assertFalse(fullCoveragePage.getStatusForField("femaleHealthCenter"));
    assertFalse(fullCoveragePage.getStatusForField("femaleMobileBrigade"));
    assertFalse(fullCoveragePage.getStatusForField("maleHealthCenter"));
    assertFalse(fullCoveragePage.getStatusForField("maleMobileBrigade"));

    EpiInventoryPage epiInventoryPage = fullCoveragePage.navigateToEpiInventory();
    assertFalse(epiInventoryPage.getExistingQuantityStatus(1));
    assertFalse(epiInventoryPage.getExistingQuantityStatus(2));
    assertFalse(epiInventoryPage.getExistingQuantityStatus(3));

    assertFalse(epiInventoryPage.getDeliveredQuantityStatus(1));
    assertFalse(epiInventoryPage.getDeliveredQuantityStatus(2));
    assertFalse(epiInventoryPage.getDeliveredQuantityStatus(3));

    assertFalse(epiInventoryPage.getSpoiledQuantityStatus(1));
    assertFalse(epiInventoryPage.getSpoiledQuantityStatus(2));
    assertFalse(epiInventoryPage.getSpoiledQuantityStatus(3));
  }

  @AfterMethod(groups = "distribution")
  public void tearDown() throws Exception {
    testWebDriver.sleep(500);
    if (!testWebDriver.getElementById("username").isDisplayed()) {
      HomePage homePage = PageFactory.getInstanceOfHomePage(testWebDriver);
      homePage.logout(baseUrlGlobal);
      dbWrapper.deleteData();
      dbWrapper.closeConnection();
    }
    ((JavascriptExecutor) TestWebDriver.getDriver()).executeScript("indexedDB.deleteDatabase('open_lmis');");
  }
}