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
  public void testMultipleFacilitySync(String userSIC,
                                       String password,
                                       String deliveryZoneCodeFirst,
                                       String deliveryZoneCodeSecond,
                                       String deliveryZoneNameFirst,
                                       String deliveryZoneNameSecond,
                                       String facilityCodeFirst,
                                       String facilityCodeSecond,
                                       String programFirst,
                                       String programSecond,
                                       String schedule) throws Exception {

    List<String> rightsList = new ArrayList<>();
    rightsList.add("MANAGE_DISTRIBUTION");
    setupTestDataToInitiateRnRAndDistribution("F10",
      "F11",
      true,
      programFirst,
      userSIC,
      "200",
      rightsList,
      programSecond,
      "District1",
      "Ngorongoro",
      "Ngorongoro");
    setupDataForDeliveryZone(true,
      deliveryZoneCodeFirst,
      deliveryZoneCodeSecond,
      deliveryZoneNameFirst,
      deliveryZoneNameSecond,
      facilityCodeFirst,
      facilityCodeSecond,
      programFirst,
      programSecond,
      schedule);
    dbWrapper.insertRoleAssignmentForDistribution(userSIC, "store in-charge", deliveryZoneCodeFirst);
    dbWrapper.insertRoleAssignmentForDistribution(userSIC, "store in-charge", deliveryZoneCodeSecond);
    dbWrapper.insertProductGroup("PG1");
    dbWrapper.insertProductWithGroup("Product5", "ProductName5", "PG1", true);
    dbWrapper.insertProductWithGroup("Product6", "ProductName6", "PG1", true);
    dbWrapper.insertProgramProduct("Product5", programFirst, "10", "false");
    dbWrapper.insertProgramProduct("Product6", programFirst, "10", "true");

    LoginPage loginPage = new LoginPage(testWebDriver, baseUrlGlobal);
    HomePage homePage = loginPage.loginAs(userSIC, password);
    DistributionPage distributionPage = homePage.navigatePlanDistribution();

    distributionPage.selectValueFromDeliveryZone(deliveryZoneNameFirst);
    distributionPage.selectValueFromProgram(programFirst);
    distributionPage.clickInitiateDistribution();

    distributionPage.clickRecordData(1);
    FacilityListPage facilityListPage = new FacilityListPage(testWebDriver);
    facilityListPage.selectFacility("F10");
    facilityListPage.verifyFacilityIndicatorColor("Overall", "AMBER");

    EPIUsePage epiUse = new EPIUsePage(testWebDriver);
    epiUse.navigate();
    epiUse.verifyProductGroup("PG1-Name", 1);
    epiUse.verifyIndicator("RED");

    epiUse.enterValueInStockAtFirstOfMonth("10", 1);
    epiUse.verifyIndicator("AMBER");
    epiUse.enterValueInReceived("20", 1);
    epiUse.enterValueInDistributed("30", 1);
    epiUse.enterValueInLoss("40", 1);
    epiUse.enterValueInStockAtEndOfMonth("50", 1);
    epiUse.enterValueInExpirationDate("10/2011", 1);
    epiUse.verifyIndicator("GREEN");


    GeneralObservationPage generalObservationPage = new GeneralObservationPage(testWebDriver);
    generalObservationPage.navigate();
    generalObservationPage.enterObservations("Some observations");
    generalObservationPage.enterConfirmedByName("samuel");
    generalObservationPage.enterConfirmedByTitle("Doe");
    generalObservationPage.enterVerifiedByName("Verifier");
    generalObservationPage.enterVerifiedByTitle("XYZ");

    CoveragePage coveragePage = new CoveragePage(testWebDriver);
    coveragePage.navigate();
    coveragePage.enterData(12, 34, 45, 56);

    homePage.navigateHomePage();
    homePage.navigateOfflineDistribution();
    distributionPage.clickRecordData(1);
    facilityListPage.selectFacility("F10");

    facilityListPage.verifyFacilityIndicatorColor("Overall", "GREEN");

    homePage.navigateHomePage();
    homePage.navigatePlanDistribution();
    distributionPage.clickRecordData(1);
    facilityListPage.selectFacility("F11");
    facilityListPage.verifyFacilityIndicatorColor("Overall", "AMBER");

    epiUse.navigate();
    epiUse.verifyProductGroup("PG1-Name", 1);
    epiUse.verifyIndicator("RED");

    epiUse.enterValueInStockAtFirstOfMonth("10", 1);
    epiUse.verifyIndicator("AMBER");
    epiUse.enterValueInReceived("20", 1);
    epiUse.enterValueInDistributed("30", 1);
    epiUse.enterValueInLoss("40", 1);
    epiUse.enterValueInStockAtEndOfMonth("50", 1);
    epiUse.enterValueInExpirationDate("10/2011", 1);
    epiUse.verifyIndicator("GREEN");

    generalObservationPage.navigate();
    generalObservationPage.enterData("Some other observations", "john", "Doe", "Verifier2", "X Y Z");

    coveragePage.navigate();
    coveragePage.enterData(12, 34, 45, 56);

    homePage.navigateHomePage();
    homePage.navigateOfflineDistribution();
    distributionPage.clickRecordData(1);
    facilityListPage.selectFacility("F11");

    facilityListPage.verifyFacilityIndicatorColor("Overall", "GREEN");

    homePage.navigateHomePage();
    homePage.navigatePlanDistribution();

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
    facilityListPage.selectFacility("F10");
    facilityListPage.verifyFacilityIndicatorColor("Overall", "BLUE");
    facilityListPage.verifyFacilityIndicatorColor("individual", "BLUE");
    generalObservationPage.navigate();
    generalObservationPage.verifyAllFieldsDisabled();

    epiUse.navigate();
    epiUse.verifyAllFieldsDisabled();

    homePage.navigatePlanDistribution();
    distributionPage.clickRecordData(1);
    facilityListPage.selectFacility("F11");
    facilityListPage.verifyFacilityIndicatorColor("Overall", "BLUE");
    generalObservationPage.navigate();
    generalObservationPage.verifyAllFieldsDisabled();

    epiUse.navigate();
    epiUse.verifyAllFieldsDisabled();

    homePage.navigatePlanDistribution();
    distributionPage.deleteDistribution();
    distributionPage.ConfirmDeleteDistribution();

  }

  @Test(groups = {"distribution"}, dataProvider = "Data-Provider-Function")
  public void testDeleteDistributionAfterSync(String userSIC,
                                              String password,
                                              String deliveryZoneCodeFirst,
                                              String deliveryZoneCodeSecond,
                                              String deliveryZoneNameFirst,
                                              String deliveryZoneNameSecond,
                                              String facilityCodeFirst,
                                              String facilityCodeSecond,
                                              String programFirst,
                                              String programSecond,
                                              String schedule) throws Exception {

    List<String> rightsList = new ArrayList<>();
    rightsList.add("MANAGE_DISTRIBUTION");
    setupTestDataToInitiateRnRAndDistribution("F10",
      "F11",
      true,
      programFirst,
      userSIC,
      "200",
      rightsList,
      programSecond,
      "District1",
      "Ngorongoro",
      "Ngorongoro");
    setupDataForDeliveryZone(true,
      deliveryZoneCodeFirst,
      deliveryZoneCodeSecond,
      deliveryZoneNameFirst,
      deliveryZoneNameSecond,
      facilityCodeFirst,
      facilityCodeSecond,
      programFirst,
      programSecond,
      schedule);
    dbWrapper.insertRoleAssignmentForDistribution(userSIC, "store in-charge", deliveryZoneCodeFirst);
    dbWrapper.insertRoleAssignmentForDistribution(userSIC, "store in-charge", deliveryZoneCodeSecond);
    dbWrapper.insertProductGroup("PG1");
    dbWrapper.insertProductWithGroup("Product5", "ProductName5", "PG1", true);
    dbWrapper.insertProductWithGroup("Product6", "ProductName6", "PG1", true);
    dbWrapper.insertProgramProduct("Product5", programFirst, "10", "false");
    dbWrapper.insertProgramProduct("Product6", programFirst, "10", "true");

    LoginPage loginPage = new LoginPage(testWebDriver, baseUrlGlobal);
    HomePage homePage = loginPage.loginAs(userSIC, password);
    DistributionPage distributionPage = homePage.navigatePlanDistribution();

    distributionPage.selectValueFromDeliveryZone(deliveryZoneNameFirst);
    distributionPage.selectValueFromProgram(programFirst);
    distributionPage.clickInitiateDistribution();

    distributionPage.clickRecordData(1);
    FacilityListPage facilityListPage = new FacilityListPage(testWebDriver);
    facilityListPage.selectFacility("F10");

    EPIUsePage epiUse = new EPIUsePage(testWebDriver);
    epiUse.navigate();
    epiUse.checkApplyNRToAllFields(true);

    GeneralObservationPage generalObservationPage = new GeneralObservationPage(testWebDriver);
    generalObservationPage.navigate();
    generalObservationPage.enterData("Some observations", "samuel", "Doe", "Verifier", "XYZ");

    CoveragePage coveragePage = new CoveragePage(testWebDriver);
    coveragePage.navigate();
    coveragePage.enterData(23, 66, 77, 45);

    homePage.navigateHomePage();
    homePage.navigatePlanDistribution();

    distributionPage.syncDistribution(1);
    distributionPage.syncDistributionMessageDone();

    distributionPage.deleteDistribution();
    distributionPage.ConfirmDeleteDistribution();
    distributionPage.selectValueFromDeliveryZone(deliveryZoneNameFirst);
    distributionPage.selectValueFromProgram(programFirst);
    distributionPage.clickInitiateDistribution();
    distributionPage.ConfirmDeleteDistribution();

    distributionPage.clickRecordData(1);
    facilityListPage.selectFacility("F10");

    epiUse.navigate();
    epiUse.checkApplyNRToAllFields(true);

    generalObservationPage.navigate();
    generalObservationPage.enterData("Some observations", "samuel", "Doe", "Verifier", "XYZ");

    coveragePage.navigate();
    coveragePage.enterData(66, 78, 89, 9);
    facilityListPage.selectFacility("F11");
    epiUse.navigate();
    epiUse.checkApplyNRToAllFields(true);

    generalObservationPage.navigate();
    generalObservationPage.enterData("Some observations", "samuel", "Doe", "Verifier", "XYZ");

    homePage.navigateHomePage();
    homePage.navigatePlanDistribution();

    distributionPage.syncDistribution(1);
    assertEquals(distributionPage.getFacilityAlreadySyncMessage(),
      "Already synced facilities : \n" + "F10-Village Dispensary");
    assertEquals(distributionPage.getSyncMessage(), "Synced facilities : \n" + "F11-Central Hospital");
    distributionPage.syncDistributionMessageDone();


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

