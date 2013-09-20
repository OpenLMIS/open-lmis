/*
 * Copyright © 2013 VillageReach.  All Rights Reserved.  This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 *
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package org.openlmis.functional;


import org.openlmis.UiUtils.CaptureScreenshotOnFailureListener;
import org.openlmis.UiUtils.TestCaseHelper;
import org.openlmis.pageobjects.*;
import org.openqa.selenium.JavascriptExecutor;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;
import org.testng.annotations.*;

import java.util.ArrayList;
import java.util.List;

import static com.thoughtworks.selenium.SeleneseTestBase.assertFalse;
import static com.thoughtworks.selenium.SeleneseTestNgHelper.assertEquals;


@TransactionConfiguration(defaultRollback = true)
@Transactional

@Listeners(CaptureScreenshotOnFailureListener.class)

public class E2EDistributionTest extends TestCaseHelper {

  public String userSIC, password;
  public static final String periodDisplayedByDefault = "Period14";
  public static final String periodNotToBeDisplayedInDropDown = "Period1";

  @BeforeMethod(groups = {"offline"})
  public void setUp() throws Exception {
    super.setup();
  }

  @Test(groups = {"offline"}, dataProvider = "Data-Provider-Function")
  public void testEditEPIUseOffline(String userSIC, String password, String deliveryZoneCodeFirst, String deliveryZoneCodeSecond,
                                    String deliveryZoneNameFirst, String deliveryZoneNameSecond,
                                    String facilityCodeFirst, String facilityCodeSecond,
                                    String programFirst, String programSecond, String schedule, String period, Integer totalNumberOfPeriods) throws Exception {

    List<String> rightsList = new ArrayList<String>();
    rightsList.add("MANAGE_DISTRIBUTION");
    setupTestDataToInitiateRnRAndDistribution("F10", "F11", true, programFirst, userSIC, "200", "openLmis", rightsList, programSecond, "District1", "Ngorongoro", "Ngorongoro");
    setupDataForDeliveryZone(true, deliveryZoneCodeFirst, deliveryZoneCodeSecond,
      deliveryZoneNameFirst, deliveryZoneNameSecond,
      facilityCodeFirst, facilityCodeSecond,
      programFirst, programSecond, schedule);
    dbWrapper.insertRoleAssignmentForDistribution(userSIC, "store in-charge", deliveryZoneCodeFirst);
    dbWrapper.insertRoleAssignmentForDistribution(userSIC, "store in-charge", deliveryZoneCodeSecond);
    dbWrapper.insertProductGroup("PG1");
    dbWrapper.insertProductWithGroup("Product5", "ProdutName5", "PG1", true);
    dbWrapper.insertProductWithGroup("Product6", "ProdutName6", "PG1", true);
    dbWrapper.insertProgramProduct("Product5", programFirst, "10", "false");
    dbWrapper.insertProgramProduct("Product6", programFirst, "10", "true");

    LoginPage loginPage = new LoginPage(testWebDriver, baseUrlGlobal);
    HomePage homePage = loginPage.loginAs(userSIC, password);
    DistributionPage distributionPage = homePage.navigatePlanDistribution();
    distributionPage.selectValueFromDeliveryZone(deliveryZoneNameFirst);
    distributionPage.selectValueFromProgram(programFirst);
    distributionPage.clickInitiateDistribution();

    testWebDriver.sleep(10000);
    switchOffNetwork();
    testWebDriver.sleep(2000);
    homePage.navigateHomePage();
    homePage.navigateOfflineDistribution();
    assertFalse("Delivery Zone selectbox displayed.", distributionPage.verifyDeliveryZoneSelectBoxNotPresent());
    assertFalse("Period selectbox displayed.", distributionPage.verifyPeriodSelectBoxNotPresent());
    assertFalse("Program selectbox displayed.", distributionPage.verifyProgramSelectBoxNotPresent());

    distributionPage.clickRecordData();
    FacilityListPage facilityListPage = new FacilityListPage(testWebDriver);
    facilityListPage.selectFacility("F10");

    RefrigeratorPage refrigeratorPage = new RefrigeratorPage(testWebDriver);
    refrigeratorPage.onRefrigeratorScreen();
    refrigeratorPage.clickAddNew();
    refrigeratorPage.enterValueInBrandModal("LG");
    refrigeratorPage.enterValueInModelModal("800 LITRES");
    refrigeratorPage.enterValueInManufacturingSerialNumberModal("GR-J287PGHV");
    refrigeratorPage.clickDoneOnModal();

    String[] refrigeratorDetails = "LG;800 LITRES;GR-J287PGHV".split(";");
    for (int i = 0; i < refrigeratorDetails.length; i++) {
      assertEquals(testWebDriver.getElementByXpath("//div[@class='list-row ng-scope']/ng-include/form/div[1]/div[" + (i + 2) + "]").getText(), refrigeratorDetails[i]);
    }

    refrigeratorPage.verifyIndividualRefrigeratorColor("overall", "RED");
    refrigeratorPage.clickEdit();
    refrigeratorPage.verifyIndividualRefrigeratorColor("individual", "RED");

    refrigeratorPage.enterValueInRefrigeratorTemperature("3");
    refrigeratorPage.verifyIndividualRefrigeratorColor("overall", "AMBER");
    refrigeratorPage.verifyIndividualRefrigeratorColor("individual", "AMBER");

    refrigeratorPage.clickFunctioningCorrectlyYesRadio();
    refrigeratorPage.enterValueInLowAlarmEvents("1");
    refrigeratorPage.enterValueInHighAlarmEvents("0");
    refrigeratorPage.clickProblemSinceLastVisitDontKnowRadio();

    refrigeratorPage.verifyIndividualRefrigeratorColor("overall", "GREEN");
    refrigeratorPage.verifyIndividualRefrigeratorColor("individual", "GREEN");

    refrigeratorPage.enterValueInNotesTextArea("miscellaneous");
    refrigeratorPage.clickDone();

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

    homePage.navigateHomePage();
    homePage.navigateOfflineDistribution();
    distributionPage.clickRecordData();
    facilityListPage.selectFacility("F10");

    refrigeratorPage.clickEdit();
    assertEquals(refrigeratorPage.getRefrigeratorTemperateTextFieldValue(), "3");
    assertEquals(refrigeratorPage.getLowAlarmEventsTextFieldValue(), "1");
    assertEquals(refrigeratorPage.getHighAlarmEventsTextFieldValue(), "0");
    assertEquals(refrigeratorPage.getNotesTextAreaValue(), "miscellaneous");
    refrigeratorPage.verifyIndividualRefrigeratorColor("overall", "GREEN");
    refrigeratorPage.verifyIndividualRefrigeratorColor("individual", "GREEN");

    epiUse.navigate();
    epiUse.verifyIndicator("GREEN");

    epiUse.verifyTotal("30", 1);
    epiUse.verifyStockAtFirstOfMonth("10", 1);
    epiUse.verifyReceived("20", 1);
    epiUse.verifyDistributed("30", 1);
    epiUse.verifyLoss("40", 1);
    epiUse.verifyStockAtEndOfMonth("50", 1);
    epiUse.verifyExpirationDate("10/2011", 1);

  }

  @Test(groups = {"offline"}, dataProvider = "Data-Provider-Function")
  public void testVerifySyncStatus(String userSIC, String password, String deliveryZoneCodeFirst, String deliveryZoneCodeSecond,
                                   String deliveryZoneNameFirst, String deliveryZoneNameSecond,
                                   String facilityCodeFirst, String facilityCodeSecond,
                                   String programFirst, String programSecond, String schedule, String period, Integer totalNumberOfPeriods) throws Exception {

    List<String> rightsList = new ArrayList<String>();
    rightsList.add("MANAGE_DISTRIBUTION");
    setupTestDataToInitiateRnRAndDistribution("F10", "F11", true, programFirst, userSIC, "200", "openLmis", rightsList, programSecond, "District1", "Ngorongoro", "Ngorongoro");
    setupDataForDeliveryZone(true, deliveryZoneCodeFirst, deliveryZoneCodeSecond,
      deliveryZoneNameFirst, deliveryZoneNameSecond,
      facilityCodeFirst, facilityCodeSecond,
      programFirst, programSecond, schedule);
    dbWrapper.insertRoleAssignmentForDistribution(userSIC, "store in-charge", deliveryZoneCodeFirst);
    dbWrapper.insertRoleAssignmentForDistribution(userSIC, "store in-charge", deliveryZoneCodeSecond);
    dbWrapper.insertProductGroup("PG1");
    dbWrapper.insertProductWithGroup("Product5", "ProdutName5", "PG1", true);
    dbWrapper.insertProductWithGroup("Product6", "ProdutName6", "PG1", true);
    dbWrapper.insertProgramProduct("Product5", programFirst, "10", "false");
    dbWrapper.insertProgramProduct("Product6", programFirst, "10", "true");
    dbWrapper.deleteDeliveryZoneMembers("F11");

    LoginPage loginPage = new LoginPage(testWebDriver, baseUrlGlobal);
    HomePage homePage = loginPage.loginAs(userSIC, password);
    DistributionPage distributionPage = homePage.navigatePlanDistribution();
    distributionPage.selectValueFromDeliveryZone(deliveryZoneNameFirst);
    distributionPage.selectValueFromProgram(programFirst);
    distributionPage.clickInitiateDistribution();

    testWebDriver.sleep(10000);
    switchOffNetwork();
    testWebDriver.sleep(2000);
    homePage.navigateHomePage();
    homePage.navigateOfflineDistribution();
    assertFalse("Delivery Zone selectbox displayed.", distributionPage.verifyDeliveryZoneSelectBoxNotPresent());
    assertFalse("Period selectbox displayed.", distributionPage.verifyPeriodSelectBoxNotPresent());
    assertFalse("Program selectbox displayed.", distributionPage.verifyProgramSelectBoxNotPresent());

    distributionPage.verifyDistributionColor("AMBER");
    distributionPage.clickRecordData();
    FacilityListPage facilityListPage = new FacilityListPage(testWebDriver);
    facilityListPage.selectFacility("F10");
    facilityListPage.verifyFacilityIndicatorColor("Overall", "AMBER");
//    facilityListPage.verifyFacilityIndicatorColor("Individual", "AMBER");
    RefrigeratorPage refrigeratorPage = new RefrigeratorPage(testWebDriver);
    refrigeratorPage.onRefrigeratorScreen();
    refrigeratorPage.clickAddNew();
    refrigeratorPage.enterValueInBrandModal("LG");
    refrigeratorPage.enterValueInModelModal("800 LITRES");
    refrigeratorPage.enterValueInManufacturingSerialNumberModal("GR-J287PGHV");
    refrigeratorPage.clickDoneOnModal();

    facilityListPage.verifyFacilityIndicatorColor("Overall", "RED");
//    facilityListPage.verifyFacilityIndicatorColor("Individual", "RED");
    homePage.navigateHomePage();
    homePage.navigateOfflineDistribution();

    distributionPage.verifyDistributionColor("RED");
    distributionPage.clickRecordData();
    facilityListPage.selectFacility("F10");

    refrigeratorPage.clickEdit();
    refrigeratorPage.enterValueInRefrigeratorTemperature("3");
    refrigeratorPage.clickFunctioningCorrectlyYesRadio();
    refrigeratorPage.enterValueInLowAlarmEvents("1");
    refrigeratorPage.enterValueInHighAlarmEvents("0");
    refrigeratorPage.clickProblemSinceLastVisitDontKnowRadio();

    GeneralObservation generalObservation = new GeneralObservation(testWebDriver);
    generalObservation.navigate();
    generalObservation.setObservations("Some observations");
    generalObservation.setConfirmedByName("samuel");
    generalObservation.setConfirmedByTitle("Doe");
    generalObservation.setVerifiedByName("Mai ka");
    generalObservation.setVerifiedByTitle("Laal");

    EPIUse epiUse=new EPIUse(testWebDriver);
    epiUse.navigate();
    epiUse.enterValueInStockAtFirstOfMonth("10", 1);
    epiUse.enterValueInReceived("20", 1);
    epiUse.enterValueInDistributed("30", 1);
    epiUse.enterValueInLoss("40", 1);
    epiUse.enterValueInStockAtEndOfMonth("50", 1);
    epiUse.enterValueInExpirationDate("10/2011", 1);

    facilityListPage.verifyFacilityIndicatorColor("Overall", "GREEN");
//    facilityListPage.verifyFacilityIndicatorColor("Individual", "GREEN");

    homePage.navigateHomePage();
    homePage.navigateOfflineDistribution();

    distributionPage.verifyDistributionColor("GREEN");

  }

  @AfterMethod(groups = {"offline"})
  public void tearDownNew() throws Exception {
    switchOnNetwork();
    testWebDriver.sleep(5000);
    dbWrapper.deleteData();
    dbWrapper.closeConnection();
    ((JavascriptExecutor) testWebDriver.getDriver()).executeScript("indexedDB.deleteDatabase('open_lmis');");
  }


  @DataProvider(name = "Data-Provider-Function")
  public Object[][] parameterIntTestProviderPositive() {
    return new Object[][]{
      {"storeincharge", "Admin123", "DZ1", "DZ2", "Delivery Zone First", "Delivery Zone Second",
        "F10", "F11", "VACCINES", "TB", "M", "Period", 14}
    };

  }
}

