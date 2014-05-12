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

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import static com.thoughtworks.selenium.SeleneseTestBase.assertFalse;
import static com.thoughtworks.selenium.SeleneseTestBase.assertTrue;
import static com.thoughtworks.selenium.SeleneseTestNgHelper.assertEquals;
import static java.util.Arrays.asList;

@Listeners(CaptureScreenshotOnFailureListener.class)

public class E2EDistributionTest extends TestCaseHelper {

  public String userSIC, password;
  private String wifiInterface;


  @BeforeMethod(groups = {"offline"})
  public void setUp() throws InterruptedException, SQLException, IOException {
    super.setup();
    wifiInterface = getWiFiInterface();
  }

  @Test(groups = {"offline"}, dataProvider = "Data-Provider-Function")
  public void testE2EManageDistributionWhenFacilityVisited(String userSIC, String password, String deliveryZoneCodeFirst, String deliveryZoneCodeSecond,
                                                           String deliveryZoneNameFirst, String deliveryZoneNameSecond,
                                                           String facilityCodeFirst, String facilityCodeSecond,
                                                           String programFirst, String programSecond, String schedule) throws SQLException, IOException {
    List<String> rightsList = asList("MANAGE_DISTRIBUTION");
    setupTestDataToInitiateRnRAndDistribution(facilityCodeFirst, facilityCodeSecond, true, programFirst, userSIC, rightsList,
      programSecond, "District1", "Ngorongoro", "Ngorongoro");
    setupDataForDeliveryZone(true, deliveryZoneCodeFirst, deliveryZoneCodeSecond, deliveryZoneNameFirst, deliveryZoneNameSecond,
      facilityCodeFirst, facilityCodeSecond, programFirst, programSecond, schedule);
    dbWrapper.insertRoleAssignmentForDistribution(userSIC, "store in-charge", deliveryZoneCodeFirst);
    dbWrapper.insertRoleAssignmentForDistribution(userSIC, "store in-charge", deliveryZoneCodeSecond);
    dbWrapper.insertProductGroup("PG1");
    dbWrapper.insertProductWithGroup("Product5", "ProductName5", "PG1", true);
    dbWrapper.insertProductWithGroup("Product6", "ProductName6", "PG1", true);
    dbWrapper.insertProgramProduct("Product5", programFirst, "10", "false");
    dbWrapper.insertProgramProduct("Product6", programFirst, "10", "true");
    dbWrapper.deleteDeliveryZoneMembers(facilityCodeSecond);
    dbWrapper.insertProductsForChildCoverage();
    insertRegimenProductMapping();
    insertOpenedVialsProductMapping();
    configureISA();

    LoginPage loginPage = PageObjectFactory.getLoginPage(testWebDriver, baseUrlGlobal);
    testWebDriver.sleep(1000);
    HomePage homePage = loginPage.loginAs(userSIC, password);
    testWebDriver.sleep(1000);
    DistributionPage distributionPage = homePage.navigateToDistributionWhenOnline();

    waitForAppCacheComplete();

    distributionPage.selectValueFromDeliveryZone(deliveryZoneNameFirst);
    distributionPage.selectValueFromProgram(programFirst);
    distributionPage.clickInitiateDistribution();

    homePage.navigateHomePage();
    switchOffNetworkInterface(wifiInterface);

    testWebDriver.sleep(3000);

    homePage.navigateOfflineDistribution();
    assertFalse("Delivery Zone selectBox displayed.", distributionPage.verifyDeliveryZoneSelectBoxNotPresent());
    assertFalse("Period selectBox displayed.", distributionPage.verifyPeriodSelectBoxNotPresent());
    assertFalse("Program selectBox displayed.", distributionPage.verifyProgramSelectBoxNotPresent());

    distributionPage.clickRecordData(1);
    FacilityListPage facilityListPage = PageObjectFactory.getFacilityListPage(testWebDriver);
    VisitInformationPage visitInformationPage = facilityListPage.selectFacility(facilityCodeFirst);
    RefrigeratorPage refrigeratorPage = visitInformationPage.navigateToRefrigerators();
    facilityListPage.verifyOverallFacilityIndicatorColor("AMBER");

    refrigeratorPage.onRefrigeratorScreen();
    refrigeratorPage.clickAddNew();
    refrigeratorPage.enterValueInManufacturingSerialNumberModal("GR-J287PGHV");
    refrigeratorPage.clickDoneOnModal();

    homePage.navigateOfflineHomePage();
    homePage.navigateOfflineDistribution();

    distributionPage.clickRecordData(1);
    facilityListPage.selectFacility(facilityCodeFirst);
    visitInformationPage.navigateToRefrigerators();

    String[] refrigeratorDetails = "GR-J287PGHV;;".split(";");
    for (int i = 0; i < refrigeratorDetails.length; i++) {
      assertEquals(testWebDriver.getElementByXpath("//div[@class='list-row ng-scope']/ng-include/form/div[1]/div[" + (i + 2) + "]").getText(),
        refrigeratorDetails[i]);
    }

    facilityListPage.verifyOverallFacilityIndicatorColor("RED");

    refrigeratorPage.verifyRefrigeratorColor("overall", "RED");
    refrigeratorPage.clickShowForRefrigerator(1);
    refrigeratorPage.verifyRefrigeratorColor("individual", "RED");

    refrigeratorPage.enterValueInRefrigeratorTemperature("3", 1);
    refrigeratorPage.verifyRefrigeratorColor("overall", "AMBER");
    refrigeratorPage.verifyRefrigeratorColor("individual", "AMBER");

    refrigeratorPage.clickFunctioningCorrectlyYesRadio(1);
    refrigeratorPage.enterValueInLowAlarmEvents("1", 1);
    refrigeratorPage.enterValueInHighAlarmEvents("0", 1);
    refrigeratorPage.clickProblemSinceLastVisitDoNotKnowRadio(1);

    refrigeratorPage.verifyRefrigeratorColor("overall", "GREEN");
    refrigeratorPage.verifyRefrigeratorColor("individual", "GREEN");

    refrigeratorPage.enterValueInNotesTextArea("miscellaneous", 1);
    refrigeratorPage.clickDone();

    EPIUsePage epiUsePage = refrigeratorPage.navigateToEpiUse();
    epiUsePage.verifyProductGroup("PG1-Name", 1);
    epiUsePage.verifyIndicator("RED");

    epiUsePage.enterValueInStockAtFirstOfMonth("10", 1);
    epiUsePage.verifyIndicator("AMBER");
    epiUsePage.enterValueInReceived("20", 1);
    epiUsePage.enterValueInDistributed("30", 1);
    epiUsePage.checkApplyNRToLoss0();
    epiUsePage.enterValueInStockAtEndOfMonth("50", 1);
    epiUsePage.enterValueInExpirationDate("10/2011", 1);
    epiUsePage.verifyIndicator("GREEN");

    visitInformationPage = epiUsePage.navigateToVisitInformation();
    visitInformationPage.verifyIndicator("RED");
    visitInformationPage.enterDataWhenFacilityVisited("samuel", "Doe", "Verifier", "XYZ");
    visitInformationPage.verifyIndicator("GREEN");
    visitInformationPage.enterVehicleId("90U-L!K3");

    ChildCoveragePage childCoveragePage = visitInformationPage.navigateToChildCoverage();
    assertEquals(childCoveragePage.getTextOfTargetGroupValue(9), "300");
    assertEquals(childCoveragePage.getTextOfTargetGroupValue(10), "300");
    assertEquals(childCoveragePage.getTextOfTargetGroupValue(11), "300");
    assertEquals(childCoveragePage.getTextOfTargetGroupValue(1), "");
    assertEquals(childCoveragePage.getTextOfTargetGroupValue(12), "");

    for (int rowNumber = 1; rowNumber <= 12; rowNumber++) {
      childCoveragePage.enterHealthCenter11MonthsDataForGivenRow(rowNumber, String.valueOf(rowNumber));
      childCoveragePage.enterOutreach11MonthsDataForGivenRow(rowNumber, String.valueOf(rowNumber));
      if (rowNumber != 2) {
        childCoveragePage.enterHealthCenter23MonthsDataForGivenRow(rowNumber, String.valueOf(rowNumber));
        childCoveragePage.enterOutreach23MonthsDataForGivenRow(rowNumber, String.valueOf(rowNumber));
      }
    }
    childCoveragePage.enterOpenedVialsCountForGivenGroupAndRow(1, 1, "1");
    childCoveragePage.enterOpenedVialsCountForGivenGroupAndRow(2, 1, "2");
    childCoveragePage.enterOpenedVialsCountForGivenGroupAndRow(2, 2, "3");
    childCoveragePage.enterOpenedVialsCountForGivenGroupAndRow(6, 1, "4");
    childCoveragePage.enterOpenedVialsCountForGivenGroupAndRow(6, 2, "5");
    childCoveragePage.enterOpenedVialsCountForGivenGroupAndRow(9, 1, "6");
    childCoveragePage.enterOpenedVialsCountForGivenGroupAndRow(12, 1, "7");
    childCoveragePage.removeFocusFromElement();

    AdultCoveragePage adultCoveragePage = childCoveragePage.navigateToAdultCoverage();
    adultCoveragePage.enterDataInAllFields();

    homePage.navigateOfflineHomePage();
    homePage.navigateOfflineDistribution();
    distributionPage.clickRecordData(1);
    facilityListPage.selectFacility(facilityCodeFirst);
    visitInformationPage.navigateToRefrigerators();

    refrigeratorPage.clickShowForRefrigerator(1);
    assertEquals(refrigeratorPage.getRefrigeratorTemperateTextFieldValue(1), "3");
    assertEquals(refrigeratorPage.getLowAlarmEventsTextFieldValue(1), "1");
    assertEquals(refrigeratorPage.getHighAlarmEventsTextFieldValue(1), "0");
    assertEquals(refrigeratorPage.getNotesTextAreaValue(1), "miscellaneous");
    refrigeratorPage.verifyRefrigeratorColor("overall", "GREEN");
    refrigeratorPage.verifyRefrigeratorColor("individual", "GREEN");

    refrigeratorPage.navigateToEpiUse();
    epiUsePage.verifyIndicator("GREEN");

    epiUsePage.verifyTotal("30", 1);
    epiUsePage.verifyStockAtFirstOfMonth("10", 1);
    epiUsePage.verifyReceived("20", 1);
    epiUsePage.verifyDistributed("30", 1);
    epiUsePage.verifyLoss("", 1);
    epiUsePage.verifyLossStatus(false, 1);
    epiUsePage.verifyStockAtEndOfMonth("50", 1);
    epiUsePage.verifyExpirationDate("10/2011", 1);

    EpiInventoryPage epiInventoryPage = epiUsePage.navigateToEpiInventory();
    epiInventoryPage.applyNRToAll();
    epiInventoryPage.fillDeliveredQuantity(1, "10");
    epiInventoryPage.fillDeliveredQuantity(2, "20");
    epiInventoryPage.fillDeliveredQuantity(3, "30");

    epiInventoryPage.verifyIndicator("GREEN");

    FullCoveragePage fullCoveragePage = epiInventoryPage.navigateToFullCoverage();
    fullCoveragePage.verifyIndicator("RED");
    fullCoveragePage.enterData(5, 7, 0, "9999999");
    fullCoveragePage.verifyIndicator("GREEN");

    fullCoveragePage.navigateToChildCoverage();
    assertEquals("300", childCoveragePage.getTextOfTargetGroupValue(9));
    assertEquals("9", childCoveragePage.getHealthCenter11MonthsDataForGivenRow(9));
    assertEquals("9", childCoveragePage.getOutreach11MonthsDataForGivenRow(9));
    assertEquals("18", childCoveragePage.getTotalForGivenColumnAndRow(1, 9));
    assertEquals("6", childCoveragePage.getCoverageRateForGivenRow(9));
    assertEquals("9", childCoveragePage.getHealthCenter23MonthsDataForGivenRow(9));
    assertEquals("9", childCoveragePage.getOutreach23MonthsDataForGivenRow(9));
    assertEquals("18", childCoveragePage.getTotalForGivenColumnAndRow(2, 9));
    assertEquals("36", childCoveragePage.getTotalForGivenColumnAndRow(3, 9));
    assertEquals("6", childCoveragePage.getOpenedVialsCountForGivenGroupAndRow(9, 1));
    assertEquals("-100", childCoveragePage.getWastageRateForGivenRow(9));

    facilityListPage.verifyOverallFacilityIndicatorColor("GREEN");

    homePage.navigateOfflineDistribution();
    homePage.navigateOfflineDistribution();

    distributionPage.syncDistribution(1);
    assertTrue(distributionPage.isFacilitySyncFailed());

    switchOnNetworkInterface(wifiInterface);
    testWebDriver.sleep(7000);

    distributionPage.clickRetryButton();
    testWebDriver.sleep(2000);
    assertEquals(distributionPage.getSyncStatusMessage(), "Sync Status");
    assertTrue("Incorrect Sync Facility", distributionPage.getSyncMessage().contains("F10-Village Dispensary"));

    distributionPage.syncDistributionMessageDone();
    assertEquals(distributionPage.getDistributionStatus(), "SYNCED");
    assertFalse(distributionPage.getTextDistributionList().contains("sync"));

    Map<String, String> distributionDetails = dbWrapper.getDistributionDetails(deliveryZoneNameFirst, programFirst, "Period14");
    assertEquals(distributionDetails.get("status"), "SYNCED");

    distributionPage.clickRecordData(1);
    facilityListPage.selectFacility(facilityCodeFirst);
    facilityListPage.verifyOverallFacilityIndicatorColor("BLUE");

    verifyEpiUseDataInDatabase(10, 20, 30, null, 50, "10/2011", "PG1", facilityCodeFirst);
    verifyRefrigeratorReadingDataInDatabase(facilityCodeFirst, "GR-J287PGHV", 3F, "Y", 1, 0, "D", "miscellaneous");
    verifyRefrigeratorProblemDataNullInDatabase("GR-J287PGHV", facilityCodeFirst);
    verifyRefrigeratorDetailsInReadingsTable(facilityCodeFirst, "GR-J287PGHV", null, null);
    verifyRefrigeratorsDataInDatabase(facilityCodeFirst, "GR-J287PGHV", null, null, "t");
    verifyFacilityVisitInformationInDatabase(facilityCodeFirst, null, "samuel", "Doe", "Verifier", "XYZ", "90U-L!K3", "t", "t", null, null);
    verifyFullCoveragesDataInDatabase(5, 7, 0, 9999999, facilityCodeFirst);
    verifyEpiInventoryDataInDatabase(null, "10", null, "P10", facilityCodeFirst);
    verifyEpiInventoryDataInDatabase(null, "20", null, "Product6", facilityCodeFirst);
    verifyEpiInventoryDataInDatabase(null, "30", null, "P11", facilityCodeFirst);
    verifyChildCoverageDataInDatabase();
    verifyAdultCoverageDataInDatabase(facilityCodeFirst);

    String facilityId = dbWrapper.getAttributeFromTable("facilities", "id", "code", "F10");
    String facilityVisitId = dbWrapper.getAttributeFromTable("facility_visits", "id", "facilityId", facilityId);
    ResultSet childCoverageDetails = dbWrapper.getChildCoverageDetails("PCV10 1st dose", facilityVisitId);
    assertEquals("300", childCoverageDetails.getInt("targetGroup"));

    visitInformationPage.verifyAllFieldsDisabled();

    visitInformationPage.navigateToEpiUse();
    epiUsePage.verifyAllFieldsDisabled();

    epiUsePage.navigateToRefrigerators();
    refrigeratorPage.clickShowForRefrigerator(1);
    refrigeratorPage.verifyAllFieldsDisabled();

    refrigeratorPage.navigateToFullCoverage();
    fullCoveragePage.verifyAllFieldsDisabled();

    fullCoveragePage.navigateToChildCoverage();
    childCoveragePage.verifyAllFieldsDisabled();

    childCoveragePage.navigateToEpiInventory();
    epiInventoryPage.verifyAllFieldsDisabled();

    epiInventoryPage.navigateToAdultCoverage();
    adultCoveragePage.verifyAllFieldsDisabled();

    distributionPage.clickGoOnlineButton();
    testWebDriver.sleep(1000);
    distributionPage = homePage.navigateToDistributionWhenOnline();
    assertTrue(distributionPage.verifyDeliveryZoneSelectBoxNotPresent());
    assertTrue(distributionPage.verifyPeriodSelectBoxNotPresent());
    assertTrue(distributionPage.verifyDeliveryZoneSelectBoxNotPresent());
    distributionPage.deleteDistribution();
    distributionPage.clickOk();

    distributionPage.selectValueFromDeliveryZone(deliveryZoneNameFirst);
    distributionPage.selectValueFromProgram(programFirst);
    String str = distributionPage.getPeriodDropDownList();
    assertFalse(str.contains("Period14"));

    distributionPage.clickInitiateDistribution();

    testWebDriver.sleep(1000);
    distributionPage.selectValueFromPeriod("Period13");
    distributionPage.clickInitiateDistribution();

    waitForAppCacheComplete();

    distributionPage.clickRecordData(1);
    assertTrue(facilityListPage.getFacilitiesInDropDown().contains("F10"));
    visitInformationPage = facilityListPage.selectFacility(facilityCodeFirst);
    facilityListPage.verifyOverallFacilityIndicatorColor("RED");
    visitInformationPage.navigateToRefrigerators();

    refrigeratorPage.verifyIndicator("RED");

    String data = "GR-J287PGHV;;";
    String[] refrigeratorDetailsOnUI = data.split(";");
    for (int i = 0; i < refrigeratorDetails.length; i++)
      assertEquals(testWebDriver.getElementByXpath("//div[@class='list-row ng-scope']/ng-include/form/div[1]/div[" + (i + 2) + "]").getText(), refrigeratorDetailsOnUI[i]);
  }

  @Test(groups = {"offline"}, dataProvider = "Data-Provider-Function")
  public void testE2EManageDistributionWhenFacilityNotVisited(String userSIC, String password, String deliveryZoneCodeFirst,
                                                              String deliveryZoneCodeSecond, String deliveryZoneNameFirst, String deliveryZoneNameSecond,
                                                              String facilityCodeFirst, String facilityCodeSecond,
                                                              String programFirst, String programSecond, String schedule) throws SQLException, IOException {
    List<String> rightsList = asList("MANAGE_DISTRIBUTION");
    setupTestDataToInitiateRnRAndDistribution(facilityCodeFirst, facilityCodeSecond, true, programFirst, userSIC, rightsList,
      programSecond, "District1", "Ngorongoro", "Ngorongoro");
    setupDataForDeliveryZone(true, deliveryZoneCodeFirst, deliveryZoneCodeSecond, deliveryZoneNameFirst, deliveryZoneNameSecond,
      facilityCodeFirst, facilityCodeSecond, programFirst, programSecond, schedule);
    dbWrapper.insertRoleAssignmentForDistribution(userSIC, "store in-charge", deliveryZoneCodeFirst);
    dbWrapper.insertRoleAssignmentForDistribution(userSIC, "store in-charge", deliveryZoneCodeSecond);
    dbWrapper.insertProductGroup("PG1");
    dbWrapper.insertProductWithGroup("Product5", "ProductName5", "PG1", true);
    dbWrapper.insertProductWithGroup("Product6", "ProductName6", "PG1", true);
    dbWrapper.insertProgramProduct("Product5", programFirst, "10", "false");
    dbWrapper.insertProgramProduct("Product6", programFirst, "10", "true");
    dbWrapper.deleteDeliveryZoneMembers(facilityCodeSecond);
    dbWrapper.insertProductsForChildCoverage();
    insertRegimenProductMapping();
    configureISA();

    LoginPage loginPage = PageObjectFactory.getLoginPage(testWebDriver, baseUrlGlobal);
    testWebDriver.sleep(1000);
    HomePage homePage = loginPage.loginAs(userSIC, password);
    testWebDriver.sleep(1000);
    DistributionPage distributionPage = homePage.navigateToDistributionWhenOnline();
    waitForAppCacheComplete();
    distributionPage.selectValueFromDeliveryZone(deliveryZoneNameFirst);
    distributionPage.selectValueFromProgram(programFirst);
    distributionPage.clickInitiateDistribution();
    testWebDriver.waitForAjax();

    switchOffNetworkInterface(wifiInterface);

    testWebDriver.sleep(3000);

    testWebDriver.refresh();
    distributionPage.clickRecordData(1);

    FacilityListPage facilityListPage = PageObjectFactory.getFacilityListPage(testWebDriver);
    VisitInformationPage visitInformationPage = facilityListPage.selectFacility(facilityCodeFirst);
    RefrigeratorPage refrigeratorPage = visitInformationPage.navigateToRefrigerators();
    facilityListPage.verifyOverallFacilityIndicatorColor("AMBER");

    refrigeratorPage.onRefrigeratorScreen();
    refrigeratorPage.clickAddNew();
    refrigeratorPage.enterValueInManufacturingSerialNumberModal("GR-J287PGHV");
    refrigeratorPage.clickDoneOnModal();

    facilityListPage.verifyOverallFacilityIndicatorColor("RED");

    refrigeratorPage.verifyRefrigeratorColor("overall", "RED");
    refrigeratorPage.clickShowForRefrigerator(1);
    refrigeratorPage.verifyRefrigeratorColor("individual", "RED");

    refrigeratorPage.clickDone();

    EPIUsePage epiUsePage = refrigeratorPage.navigateToEpiUse();
    epiUsePage.verifyProductGroup("PG1-Name", 1);
    epiUsePage.verifyIndicator("RED");

    epiUsePage.enterValueInStockAtFirstOfMonth("10", 1);
    epiUsePage.verifyIndicator("AMBER");
    epiUsePage.enterValueInReceived("20", 1);
    epiUsePage.enterValueInDistributed("30", 1);
    epiUsePage.checkApplyNRToLoss0();
    epiUsePage.enterValueInStockAtEndOfMonth("50", 1);
    epiUsePage.enterValueInExpirationDate("10/2011", 1);
    epiUsePage.verifyIndicator("GREEN");

    EpiInventoryPage epiInventoryPage = epiUsePage.navigateToEpiInventory();
    epiInventoryPage.applyNRToAll();
    epiInventoryPage.verifyIndicator("AMBER");

    visitInformationPage = epiUsePage.navigateToVisitInformation();
    visitInformationPage.verifyIndicator("RED");
    visitInformationPage.selectFacilityVisitedNo();
    visitInformationPage.verifyIndicator("AMBER");
    visitInformationPage.selectReasonBadWeather();
    visitInformationPage.verifyIndicator("GREEN");
    visitInformationPage.selectReasonOther();
    visitInformationPage.verifyIndicator("AMBER");

    visitInformationPage.navigateToEpiInventory();
    epiInventoryPage.verifyIndicator("GREEN");
    epiInventoryPage.verifyAllFieldsDisabled();

    epiInventoryPage.navigateToRefrigerators();
    refrigeratorPage.verifyIndicator("GREEN");
    refrigeratorPage.clickShowForRefrigerator(1);
    refrigeratorPage.verifyAllFieldsDisabled();

    refrigeratorPage.navigateToEpiUse();
    epiUsePage.verifyIndicator("GREEN");
    epiUsePage.verifyAllFieldsDisabled();

    ChildCoveragePage childCoveragePage = visitInformationPage.navigateToChildCoverage();
    assertEquals(childCoveragePage.getTextOfTargetGroupValue(9), "300");
    assertEquals(childCoveragePage.getTextOfTargetGroupValue(10), "300");
    assertEquals(childCoveragePage.getTextOfTargetGroupValue(11), "300");
    assertEquals(childCoveragePage.getTextOfTargetGroupValue(1), "");
    assertEquals(childCoveragePage.getTextOfTargetGroupValue(12), "");

    AdultCoveragePage adultCoveragePage = childCoveragePage.navigateToAdultCoverage();
    adultCoveragePage.enterDataInAllFields();

    adultCoveragePage.navigateToChildCoverage();

    for (int rowNumber = 1; rowNumber <= 12; rowNumber++) {
      childCoveragePage.enterHealthCenter11MonthsDataForGivenRow(rowNumber, String.valueOf(rowNumber));
      childCoveragePage.enterOutreach11MonthsDataForGivenRow(rowNumber, String.valueOf(rowNumber));
      if (rowNumber != 2) {
        childCoveragePage.enterHealthCenter23MonthsDataForGivenRow(rowNumber, String.valueOf(rowNumber));
        childCoveragePage.enterOutreach23MonthsDataForGivenRow(rowNumber, String.valueOf(rowNumber));
      }
    }
    childCoveragePage.enterOpenedVialsCountForGivenGroupAndRow(1, 1, "1");
    childCoveragePage.enterOpenedVialsCountForGivenGroupAndRow(2, 1, "2");
    childCoveragePage.enterOpenedVialsCountForGivenGroupAndRow(2, 2, "3");
    childCoveragePage.enterOpenedVialsCountForGivenGroupAndRow(6, 1, "4");
    childCoveragePage.enterOpenedVialsCountForGivenGroupAndRow(6, 2, "5");
    childCoveragePage.enterOpenedVialsCountForGivenGroupAndRow(9, 1, "6");
    childCoveragePage.enterOpenedVialsCountForGivenGroupAndRow(12, 1, "7");
    childCoveragePage.removeFocusFromElement();

    homePage.navigateOfflineHomePage();
    homePage.navigateOfflineDistribution();
    distributionPage.clickRecordData(1);
    facilityListPage.selectFacility(facilityCodeFirst);
    visitInformationPage.navigateToEpiUse();
    epiUsePage.verifyIndicator("GREEN");

    epiUsePage.verifyTotal("30", 1);
    epiUsePage.verifyStockAtFirstOfMonth("10", 1);
    epiUsePage.verifyReceived("20", 1);
    epiUsePage.verifyDistributed("30", 1);
    epiUsePage.verifyLoss("", 1);
    epiUsePage.verifyLossStatus(false, 1);
    epiUsePage.verifyStockAtEndOfMonth("50", 1);
    epiUsePage.verifyExpirationDate("10/2011", 1);

    FullCoveragePage fullCoveragePage = epiUsePage.navigateToFullCoverage();
    fullCoveragePage.verifyIndicator("RED");
    fullCoveragePage.enterData(5, 7, 0, "9999999");
    fullCoveragePage.verifyIndicator("GREEN");

    fullCoveragePage.navigateToEpiInventory();
    epiInventoryPage.verifyAllFieldsDisabled();

    epiInventoryPage.navigateToRefrigerators();
    refrigeratorPage.clickShowForRefrigerator(1);
    refrigeratorPage.verifyAllFieldsDisabled();

    refrigeratorPage.navigateToVisitInformation();
    visitInformationPage.enterOtherReasonInTextBox("Reason for not visiting the facility");
    visitInformationPage.verifyIndicator("GREEN");

    visitInformationPage.navigateToChildCoverage();
    childCoveragePage.verifyIndicator("GREEN");
    assertEquals("300", childCoveragePage.getTextOfTargetGroupValue(9));
    assertEquals("9", childCoveragePage.getHealthCenter11MonthsDataForGivenRow(9));
    assertEquals("9", childCoveragePage.getOutreach11MonthsDataForGivenRow(9));
    assertEquals("18", childCoveragePage.getTotalForGivenColumnAndRow(1, 9));
    assertEquals("6", childCoveragePage.getCoverageRateForGivenRow(9));
    assertEquals("9", childCoveragePage.getHealthCenter23MonthsDataForGivenRow(9));
    assertEquals("9", childCoveragePage.getOutreach23MonthsDataForGivenRow(9));
    assertEquals("18", childCoveragePage.getTotalForGivenColumnAndRow(2, 9));
    assertEquals("36", childCoveragePage.getTotalForGivenColumnAndRow(3, 9));
    assertEquals("6", childCoveragePage.getOpenedVialsCountForGivenGroupAndRow(9, 1));
    assertEquals("", childCoveragePage.getWastageRateForGivenRow(9));

    childCoveragePage.navigateToAdultCoverage();
    adultCoveragePage.verifyIndicator("GREEN");
    assertEquals("21", adultCoveragePage.getOutreachFirstInput(1));
    assertEquals("41", adultCoveragePage.getOutreach2To5Input(1));
    assertEquals("11", adultCoveragePage.getHealthCenterFirstInput(1));
    assertEquals("31", adultCoveragePage.getHealthCenter2To5Input(1));
    assertEquals("999", adultCoveragePage.getOpenedVialInputField());
    assertEquals("32", adultCoveragePage.getTotalTetanusFirst(1));
    assertEquals("72", adultCoveragePage.getTotalTetanus2To5(1));
    assertEquals("104", adultCoveragePage.getTotalTetanus(1));
    assertEquals("616", adultCoveragePage.getTotalTetanus());

    facilityListPage.verifyOverallFacilityIndicatorColor("GREEN");

    homePage.navigateOfflineHomePage();
    homePage.navigateOfflineDistribution();

    distributionPage.syncDistribution(1);
    assertTrue(distributionPage.isFacilitySyncFailed());
    distributionPage.clickCancelSyncRetry();

    switchOnNetworkInterface(wifiInterface);
    testWebDriver.sleep(7000);

    distributionPage.clickGoOnlineButton();

    distributionPage.syncDistribution(1);
    testWebDriver.sleep(2000);
    assertEquals(distributionPage.getSyncStatusMessage(), "Sync Status");
    assertTrue("Incorrect Sync Facility", distributionPage.getSyncMessage().contains("F10-Village Dispensary"));

    distributionPage.syncDistributionMessageDone();
    assertEquals(distributionPage.getDistributionStatus(), "SYNCED");
    assertFalse(distributionPage.getTextDistributionList().contains("sync"));

    Map<String, String> distributionDetails = dbWrapper.getDistributionDetails(deliveryZoneNameFirst, programFirst, "Period14");
    assertEquals(distributionDetails.get("status"), "SYNCED");

    distributionPage.clickRecordData(1);
    facilityListPage.selectFacility(facilityCodeFirst);
    facilityListPage.verifyOverallFacilityIndicatorColor("BLUE");

    verifyEpiUseDataInDatabase(null, null, null, null, null, null, "PG1", facilityCodeFirst);
    verifyFacilityVisitInformationInDatabase(facilityCodeFirst, null, null, null, null, null, null, "t", "f", "OTHER", "Reason for not visiting the facility");
    verifyFullCoveragesDataInDatabase(5, 7, 0, 9999999, facilityCodeFirst);
    verifyEpiInventoryDataInDatabase(null, null, null, "P10", facilityCodeFirst);
    verifyEpiInventoryDataInDatabase(null, null, null, "Product6", facilityCodeFirst);
    verifyEpiInventoryDataInDatabase(null, null, null, "P11", facilityCodeFirst);
    verifyAdultCoverageDataInDatabase(facilityCodeFirst);
    verifyChildCoverageDataInDatabase();

    String facilityId = dbWrapper.getAttributeFromTable("facilities", "id", "code", "F10");
    String facilityVisitId = dbWrapper.getAttributeFromTable("facility_visits", "id", "facilityId", facilityId);
    ResultSet childCoverageDetails = dbWrapper.getChildCoverageDetails("PCV10 1st dose", facilityVisitId);
    assertEquals("300", childCoverageDetails.getInt("targetGroup"));

    visitInformationPage.verifyAllFieldsDisabled();

    visitInformationPage.navigateToEpiUse();
    epiUsePage.verifyAllFieldsDisabled();

    epiUsePage.navigateToRefrigerators();
    refrigeratorPage.clickShowForRefrigerator(1);
    refrigeratorPage.verifyAllFieldsDisabled();

    refrigeratorPage.navigateToFullCoverage();
    fullCoveragePage.verifyAllFieldsDisabled();

    fullCoveragePage.navigateToChildCoverage();
    childCoveragePage.verifyAllFieldsDisabled();

    childCoveragePage.navigateToEpiInventory();
    epiInventoryPage.verifyAllFieldsDisabled();

    epiInventoryPage.navigateToAdultCoverage();
    adultCoveragePage.verifyAllFieldsDisabled();
  }

  private void configureISA() {
    LoginPage loginPage = PageObjectFactory.getLoginPage(testWebDriver, baseUrlGlobal);
    HomePage homePage = loginPage.loginAs("Admin123", "Admin123");

    ProgramProductISAPage programProductISAPage = homePage.navigateProgramProductISA();
    programProductISAPage.fillProgramProductISA("VACCINES", "90", "1", "50", "30", "0", "100", "2000", "333");
    homePage.logout();
  }

  private void insertRegimenProductMapping() throws SQLException {
    dbWrapper.insertTargetGroupEntityAndProductsInMappingTable("BCG", "BCG", true);
    dbWrapper.insertTargetGroupEntityAndProductsInMappingTable("Polio (Newborn)", "polio10dose", true);
    dbWrapper.insertTargetGroupEntityAndProductsInMappingTable("Polio 1st dose", "polio20dose", true);
    dbWrapper.insertTargetGroupEntityAndProductsInMappingTable("Polio 2nd dose", "polio10dose", true);
    dbWrapper.insertTargetGroupEntityAndProductsInMappingTable("Polio 3rd dose", "polio20dose", true);
    dbWrapper.insertTargetGroupEntityAndProductsInMappingTable("Penta 1st dose", "penta1", true);
    dbWrapper.insertTargetGroupEntityAndProductsInMappingTable("Penta 2nd dose", "penta10", true);
    dbWrapper.insertTargetGroupEntityAndProductsInMappingTable("Penta 3rd dose", "penta1", true);
    dbWrapper.insertTargetGroupEntityAndProductsInMappingTable("PCV10 1st dose", "P10", true);
    dbWrapper.insertTargetGroupEntityAndProductsInMappingTable("PCV10 2nd dose", "P10", true);
    dbWrapper.insertTargetGroupEntityAndProductsInMappingTable("PCV10 3rd dose", "P10", true);
    dbWrapper.insertTargetGroupEntityAndProductsInMappingTable("Measles", "Measles", true);
  }

  public void insertOpenedVialsProductMapping() throws SQLException {
    dbWrapper.insertChildCoverageProductVial("BCG", "P10");
    dbWrapper.insertChildCoverageProductVial("Polio10", "P11");
    dbWrapper.insertChildCoverageProductVial("Polio20", "P10");
    dbWrapper.insertChildCoverageProductVial("Penta1", "penta1");
    dbWrapper.insertChildCoverageProductVial("Penta10", "P11");
    dbWrapper.insertChildCoverageProductVial("PCV", "P10");
    dbWrapper.insertChildCoverageProductVial("Measles", "Measles");
  }

  @AfterMethod(groups = {"offline"})
  public void tearDownNew() throws IOException, SQLException {
    switchOnNetworkInterface(wifiInterface);
    testWebDriver.sleep(5000);
    dbWrapper.deleteData();
    dbWrapper.closeConnection();
    ((JavascriptExecutor) TestWebDriver.getDriver()).executeScript("indexedDB.deleteDatabase('open_lmis');");
  }

  @DataProvider(name = "Data-Provider-Function")
  public Object[][] parameterIntTestProviderPositive() {
    return new Object[][]{
      {"storeInCharge", "Admin123", "DZ1", "DZ2", "Delivery Zone First", "Delivery Zone Second",
        "F10", "F11", "VACCINES", "TB", "M"}
    };
  }
}