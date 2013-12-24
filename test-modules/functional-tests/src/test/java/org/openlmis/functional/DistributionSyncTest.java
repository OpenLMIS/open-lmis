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
import java.util.HashMap;
import java.util.List;

import static com.thoughtworks.selenium.SeleneseTestBase.assertEquals;
import static com.thoughtworks.selenium.SeleneseTestBase.assertTrue;

@Listeners(CaptureScreenshotOnFailureListener.class)

public class DistributionSyncTest extends TestCaseHelper {

  public String userSIC, password;


  @BeforeMethod(groups = {"distribution"})
  public void setUp() throws Exception {
    super.setup();
  }

  @Test(groups = {"distribution"}, dataProvider = "Data-Provider-Function")
  public void testMultipleFacilitySync(String userSIC, String password, String deliveryZoneCodeFirst, String deliveryZoneCodeSecond,
                                       String deliveryZoneNameFirst, String deliveryZoneNameSecond,
                                       String facilityCodeFirst, String facilityCodeSecond,
                                       String programFirst, String programSecond, String schedule) throws Exception {

    List<String> rightsList = new ArrayList<>();
    rightsList.add("MANAGE_DISTRIBUTION");
    setupTestDataToInitiateRnRAndDistribution("F10", "F11", true, programFirst, userSIC, "200", rightsList, programSecond, "District1", "Ngorongoro", "Ngorongoro");
    setupDataForDeliveryZone(true, deliveryZoneCodeFirst, deliveryZoneCodeSecond,
      deliveryZoneNameFirst, deliveryZoneNameSecond,
      facilityCodeFirst, facilityCodeSecond,
      programFirst, programSecond, schedule);
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

    distributionPage.clickRecordData();
    FacilityListPage facilityListPage = new FacilityListPage(testWebDriver);
    facilityListPage.selectFacility("F10");
    facilityListPage.verifyFacilityIndicatorColor("Overall", "AMBER");

    EPIUse epiUse = new EPIUse(testWebDriver);
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
    generalObservationPage.setObservations("Some observations");
    generalObservationPage.setConfirmedByName("samuel");
    generalObservationPage.setConfirmedByTitle("Doe");
    generalObservationPage.setVerifiedByName("Verifier");
    generalObservationPage.setVerifiedByTitle("XYZ");

    homePage.navigateHomePage();
    homePage.navigateOfflineDistribution();
    distributionPage.clickRecordData();
    facilityListPage.selectFacility("F10");

    facilityListPage.verifyFacilityIndicatorColor("Overall", "GREEN");

    homePage.navigateHomePage();
    homePage.navigatePlanDistribution();
    distributionPage.clickRecordData();
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
    generalObservationPage.setObservations("Some observations");
    generalObservationPage.setConfirmedByName("samuel");
    generalObservationPage.setConfirmedByTitle("Doe");
    generalObservationPage.setVerifiedByName("Verifier");
    generalObservationPage.setVerifiedByTitle("X Y Z");

    homePage.navigateHomePage();
    homePage.navigateOfflineDistribution();
    distributionPage.clickRecordData();
    facilityListPage.selectFacility("F11");

    facilityListPage.verifyFacilityIndicatorColor("Overall", "GREEN");

    homePage.navigateHomePage();
    homePage.navigatePlanDistribution();

    distributionPage.syncDistribution();
    assertTrue(distributionPage.getSyncMessage().contains("F10-Village Dispensary"));
    assertTrue(distributionPage.getSyncMessage().contains("F11-Central Hospital"));
    distributionPage.syncDistributionMessageDone();

    HashMap m1 = dbWrapper.getFacilityVisitDetails();

    assertEquals("Some observations", m1.get("observations").toString());
    assertEquals("samuel", m1.get("confirmedByName").toString());
    assertEquals("Doe", m1.get("confirmedByTitle").toString());
    assertEquals("Verifier", m1.get("verifiedByName").toString());
    assertEquals("XYZ", m1.get("verifiedByTitle").toString());

    distributionPage.clickRecordData();
    facilityListPage.selectFacility("F10");
    facilityListPage.verifyFacilityIndicatorColor("Overall", "BLUE");
    facilityListPage.verifyFacilityIndicatorColor("individual", "BLUE");
    generalObservationPage.navigate();
    generalObservationPage.verifyAllFieldsDisabled();

    epiUse.navigate();
    epiUse.verifyAllFieldsDisabled();

    homePage.navigatePlanDistribution();
    distributionPage.clickRecordData();
    facilityListPage.selectFacility("F11");
    facilityListPage.verifyFacilityIndicatorColor("Overall", "BLUE");
    facilityListPage.verifyFacilityIndicatorColor("individual", "BLUE");
    generalObservationPage.navigate();
    generalObservationPage.verifyAllFieldsDisabled();

    epiUse.navigate();
    epiUse.verifyAllFieldsDisabled();

    homePage.navigatePlanDistribution();
      distributionPage.deleteDistribution();
      distributionPage.ConfirmDeleteDistribution();

  }

    @Test(groups = {"distribution"}, dataProvider = "Data-Provider-Function")
    public void testDeleteDistributionAfterSync(String userSIC, String password, String deliveryZoneCodeFirst, String deliveryZoneCodeSecond,
                                         String deliveryZoneNameFirst, String deliveryZoneNameSecond,
                                         String facilityCodeFirst, String facilityCodeSecond,
                                         String programFirst, String programSecond, String schedule) throws Exception {

        List<String> rightsList = new ArrayList<>();
        rightsList.add("MANAGE_DISTRIBUTION");
        setupTestDataToInitiateRnRAndDistribution("F10", "F11", true, programFirst, userSIC, "200", rightsList, programSecond, "District1", "Ngorongoro", "Ngorongoro");
        setupDataForDeliveryZone(true, deliveryZoneCodeFirst, deliveryZoneCodeSecond,
                deliveryZoneNameFirst, deliveryZoneNameSecond,
                facilityCodeFirst, facilityCodeSecond,
                programFirst, programSecond, schedule);
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

        distributionPage.clickRecordData();
        FacilityListPage facilityListPage = new FacilityListPage(testWebDriver);
        facilityListPage.selectFacility("F10");

        EPIUse epiUse = new EPIUse(testWebDriver);
        epiUse.navigate();
        epiUse.checkApplyNRToAllFields(true);

        GeneralObservationPage generalObservationPage = new GeneralObservationPage(testWebDriver);
        generalObservationPage.navigate();
        generalObservationPage.setObservations("Some observations");
        generalObservationPage.setConfirmedByName("samuel");
        generalObservationPage.setConfirmedByTitle("Doe");
        generalObservationPage.setVerifiedByName("Verifier");
        generalObservationPage.setVerifiedByTitle("XYZ");

        homePage.navigateHomePage();
        homePage.navigatePlanDistribution();

        distributionPage.syncDistribution();
        distributionPage.syncDistributionMessageDone();

        distributionPage.deleteDistribution();
        distributionPage.ConfirmDeleteDistribution();
        distributionPage.selectValueFromDeliveryZone(deliveryZoneNameFirst);
        distributionPage.selectValueFromProgram(programFirst);
        distributionPage.clickInitiateDistribution();
        distributionPage.ConfirmDeleteDistribution();

        distributionPage.clickRecordData();
        facilityListPage.selectFacility("F10");

        epiUse.navigate();
        epiUse.checkApplyNRToAllFields(true);

        generalObservationPage.navigate();
        generalObservationPage.setObservations("Some observations");
        generalObservationPage.setConfirmedByName("samuel");
        generalObservationPage.setConfirmedByTitle("Doe");
        generalObservationPage.setVerifiedByName("Verifier");
        generalObservationPage.setVerifiedByTitle("XYZ");

        facilityListPage.selectFacility("F11");
        epiUse.navigate();
        epiUse.checkApplyNRToAllFields(true);

        generalObservationPage.navigate();
        generalObservationPage.setObservations("Some observations");
        generalObservationPage.setConfirmedByName("samuel");
        generalObservationPage.setConfirmedByTitle("Doe");
        generalObservationPage.setVerifiedByName("Verifier");
        generalObservationPage.setVerifiedByTitle("XYZ");

        homePage.navigateHomePage();
        homePage.navigatePlanDistribution();

        distributionPage.syncDistribution();
        assertEquals(distributionPage.getFacilityAlreadySyncMessage(),"Already synchronized facilities : \n" +
                "F10-Village Dispensary");
        assertEquals(distributionPage.getSyncMessage(),"Synchronized facilities : \n" +
                "F11-Central Hospital");
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
    return new Object[][]{
      {"fieldCoordinator", "Admin123", "DZ1", "DZ2", "Delivery Zone First", "Delivery Zone Second",
        "F10", "F11", "VACCINES", "TB", "M"}
    };

  }
}

