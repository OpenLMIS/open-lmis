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
    setupTestDataToInitiateRnRAndDistribution(facilityCodeFirst, facilityCodeSecond, true, programFirst, userSIC, "200", rightsList,
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

    LoginPage loginPage = PageFactory.getInstanceOfLoginPage(testWebDriver, baseUrlGlobal);
    testWebDriver.sleep(1000);
    HomePage homePage = loginPage.loginAs(userSIC, password);
    testWebDriver.sleep(1000);
    DistributionPage distributionPage = homePage.navigateToDistributionWhenOnline();
    distributionPage.selectValueFromDeliveryZone(deliveryZoneNameFirst);
    distributionPage.selectValueFromProgram(programFirst);
    distributionPage.clickInitiateDistribution();

    waitForAppCacheComplete();

    switchOffNetworkInterface(wifiInterface);

    testWebDriver.sleep(3000);
    homePage.navigateHomePage();
    homePage.navigateOfflineDistribution();
    assertFalse("Delivery Zone selectBox displayed.", distributionPage.verifyDeliveryZoneSelectBoxNotPresent());
    assertFalse("Period selectBox displayed.", distributionPage.verifyPeriodSelectBoxNotPresent());
    assertFalse("Program selectBox displayed.", distributionPage.verifyProgramSelectBoxNotPresent());

    distributionPage.clickRecordData(1);
    FacilityListPage facilityListPage = PageFactory.getInstanceOfFacilityListPage(testWebDriver);
    VisitInformationPage visitInformationPage = facilityListPage.selectFacility(facilityCodeFirst);
    RefrigeratorPage refrigeratorPage = visitInformationPage.navigateToRefrigerators();
    facilityListPage.verifyFacilityIndicatorColor("Overall", "AMBER");

    refrigeratorPage.onRefrigeratorScreen();
    refrigeratorPage.clickAddNew();
    refrigeratorPage.enterValueInBrandModal("LG");
    refrigeratorPage.enterValueInModelModal("800 LITRES");
    refrigeratorPage.enterValueInManufacturingSerialNumberModal("GR-J287PGHV");
    refrigeratorPage.clickDoneOnModal();

    homePage.navigateHomePage();
    homePage.navigateOfflineDistribution();

    distributionPage.clickRecordData(1);
    facilityListPage.selectFacility(facilityCodeFirst);
    visitInformationPage.navigateToRefrigerators();

    String[] refrigeratorDetails = "LG;800 LITRES;GR-J287PGHV".split(";");
    for (int i = 0; i < refrigeratorDetails.length; i++) {
      assertEquals(testWebDriver.getElementByXpath("//div[@class='list-row ng-scope']/ng-include/form/div[1]/div[" + (i + 2) + "]").getText(),
        refrigeratorDetails[i]);
    }

    facilityListPage.verifyFacilityIndicatorColor("Overall", "RED");

    refrigeratorPage.verifyRefrigeratorColor("overall", "RED");
    refrigeratorPage.clickShowForRefrigerator1();
    refrigeratorPage.verifyRefrigeratorColor("individual", "RED");

    refrigeratorPage.enterValueInRefrigeratorTemperature("3");
    refrigeratorPage.verifyRefrigeratorColor("overall", "AMBER");
    refrigeratorPage.verifyRefrigeratorColor("individual", "AMBER");

    refrigeratorPage.clickFunctioningCorrectlyYesRadio();
    refrigeratorPage.enterValueInLowAlarmEvents("1");
    refrigeratorPage.enterValueInHighAlarmEvents("0");
    refrigeratorPage.clickProblemSinceLastVisitDoNotKnowRadio();

    refrigeratorPage.verifyRefrigeratorColor("overall", "GREEN");
    refrigeratorPage.verifyRefrigeratorColor("individual", "GREEN");

    refrigeratorPage.enterValueInNotesTextArea("miscellaneous");
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
    visitInformationPage.enterDataWhenFacilityVisited("some observations", "samuel", "Doe", "Verifier", "XYZ");
    visitInformationPage.verifyIndicator("GREEN");
    visitInformationPage.enterVehicleId("90U-L!K3");

    ChildCoveragePage childCoveragePage = visitInformationPage.navigateToChildCoverage();
    assertEquals(childCoveragePage.getTextOfTargetGroupValue(9), "300");
    assertEquals(childCoveragePage.getTextOfTargetGroupValue(10), "300");
    assertEquals(childCoveragePage.getTextOfTargetGroupValue(11), "300");
    assertEquals(childCoveragePage.getTextOfTargetGroupValue(1), "");
    assertEquals(childCoveragePage.getTextOfTargetGroupValue(12), "");

    homePage.navigateHomePage();
    homePage.navigateOfflineDistribution();
    distributionPage.clickRecordData(1);
    facilityListPage.selectFacility(facilityCodeFirst);
    visitInformationPage.navigateToRefrigerators();

    refrigeratorPage.clickShowForRefrigerator1();
    assertEquals(refrigeratorPage.getRefrigeratorTemperateTextFieldValue(), "3");
    assertEquals(refrigeratorPage.getLowAlarmEventsTextFieldValue(), "1");
    assertEquals(refrigeratorPage.getHighAlarmEventsTextFieldValue(), "0");
    assertEquals(refrigeratorPage.getNotesTextAreaValue(), "miscellaneous");
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

    facilityListPage.verifyFacilityIndicatorColor("Overall", "GREEN");

    homePage.navigateHomePage();
    homePage.navigateOfflineDistribution();

    distributionPage.syncDistribution(1);
    assertTrue(distributionPage.isFacilitySyncFailed());


    switchOnNetworkInterface(wifiInterface);
    testWebDriver.sleep(7000);

    distributionPage.clickRetryButton();
    assertEquals(distributionPage.getSyncStatusMessage(), "Sync Status");
    assertTrue("Incorrect Sync Facility", distributionPage.getSyncMessage().contains("F10-Village Dispensary"));

    distributionPage.syncDistributionMessageDone();
    assertEquals(distributionPage.getDistributionStatus(), "SYNCED");
    assertFalse(distributionPage.getTextDistributionList().contains("sync"));

    Map<String, String> distributionDetails = dbWrapper.getDistributionDetails(deliveryZoneNameFirst, programFirst, "Period14");
    assertEquals(distributionDetails.get("status"), "SYNCED");

    distributionPage.clickRecordData(1);
    facilityListPage.selectFacility(facilityCodeFirst);
    facilityListPage.verifyFacilityIndicatorColor("Overall", "BLUE");

    verifyEpiUseDataInDatabase(10, 20, 30, null, 50, "10/2011", "PG1", facilityCodeFirst);
    verifyRefrigeratorReadingDataInDatabase(facilityCodeFirst, "GR-J287PGHV", 3F, "Y", 1, 0, "D", "miscellaneous");
    verifyRefrigeratorProblemDataNullInDatabase("GR-J287PGHV", facilityCodeFirst);
    verifyFacilityVisitInformationInDatabase(facilityCodeFirst, "some observations", "samuel", "Doe", "Verifier", "XYZ", null, "t", "t");
    verifyFullCoveragesDataInDatabase(5, 7, 0, 9999999, facilityCodeFirst);
    verifyEpiInventoryDataInDatabase(null, "10", null, "P10", facilityCodeFirst);
    verifyEpiInventoryDataInDatabase(null, "20", null, "Product6", facilityCodeFirst);
    verifyEpiInventoryDataInDatabase(null, "30", null, "P11", facilityCodeFirst);

    ResultSet childCoverageDetails = dbWrapper.getChildCoverageDetails("PCV10 1st dose", "F10");
    assertEquals("300", childCoverageDetails.getInt("targetGroup"));

    visitInformationPage.verifyAllFieldsDisabled();

    visitInformationPage.navigateToEpiUse();
    epiUsePage.verifyAllFieldsDisabled();

    epiUsePage.navigateToRefrigerators();
    refrigeratorPage.clickShowForRefrigerator1();
    refrigeratorPage.verifyAllFieldsDisabled();

    refrigeratorPage.navigateToFullCoverage();
    fullCoveragePage.verifyAllFieldsDisabled();

    loginPage = PageFactory.getInstanceOfLoginPage(testWebDriver, baseUrlGlobal);
    testWebDriver.sleep(1000);
    homePage = loginPage.loginAs(userSIC, password);
    testWebDriver.sleep(1000);

    distributionPage = homePage.navigateToDistributionWhenOnline();
    testWebDriver.sleep(1000);
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
    facilityListPage.verifyFacilityIndicatorColor("Overall", "RED");
    visitInformationPage.navigateToRefrigerators();

    refrigeratorPage.verifyIndicator("RED");

    String data = "LG;800 LITRES;GR-J287PGHV";
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
    setupTestDataToInitiateRnRAndDistribution(facilityCodeFirst, facilityCodeSecond, true, programFirst, userSIC, "200", rightsList,
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

    LoginPage loginPage = PageFactory.getInstanceOfLoginPage(testWebDriver, baseUrlGlobal);
    testWebDriver.sleep(1000);
    HomePage homePage = loginPage.loginAs(userSIC, password);
    testWebDriver.sleep(1000);
    DistributionPage distributionPage = homePage.navigateToDistributionWhenOnline();
    distributionPage.selectValueFromDeliveryZone(deliveryZoneNameFirst);
    distributionPage.selectValueFromProgram(programFirst);
    distributionPage.clickInitiateDistribution();

    waitForAppCacheComplete();

    switchOffNetworkInterface(wifiInterface);

    testWebDriver.sleep(3000);
    homePage.navigateHomePage();
    homePage.navigateOfflineDistribution();
    distributionPage.clickRecordData(1);

    FacilityListPage facilityListPage = PageFactory.getInstanceOfFacilityListPage(testWebDriver);
    VisitInformationPage visitInformationPage = facilityListPage.selectFacility(facilityCodeFirst);
    RefrigeratorPage refrigeratorPage = visitInformationPage.navigateToRefrigerators();
    facilityListPage.verifyFacilityIndicatorColor("Overall", "AMBER");

    refrigeratorPage.onRefrigeratorScreen();
    refrigeratorPage.clickAddNew();
    refrigeratorPage.enterValueInBrandModal("LG");
    refrigeratorPage.enterValueInModelModal("800 LITRES");
    refrigeratorPage.enterValueInManufacturingSerialNumberModal("GR-J287PGHV");
    refrigeratorPage.clickDoneOnModal();

    facilityListPage.verifyFacilityIndicatorColor("Overall", "RED");

    refrigeratorPage.verifyRefrigeratorColor("overall", "RED");
    refrigeratorPage.clickShowForRefrigerator1();
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

    epiInventoryPage.verifyIndicator("GREEN");
    refrigeratorPage.verifyIndicator("GREEN");

    ChildCoveragePage childCoveragePage = visitInformationPage.navigateToChildCoverage();
    assertEquals(childCoveragePage.getTextOfTargetGroupValue(9), "300");
    assertEquals(childCoveragePage.getTextOfTargetGroupValue(10), "300");
    assertEquals(childCoveragePage.getTextOfTargetGroupValue(11), "300");
    assertEquals(childCoveragePage.getTextOfTargetGroupValue(1), "");
    assertEquals(childCoveragePage.getTextOfTargetGroupValue(12), "");

    homePage.navigateHomePage();
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
    refrigeratorPage.clickShowForRefrigerator1();
    refrigeratorPage.verifyAllFieldsDisabled();

    refrigeratorPage.navigateToVisitInformation();
    visitInformationPage.enterOtherReasonInTextBox("Reason for not visiting the facility");
    visitInformationPage.verifyIndicator("GREEN");

    facilityListPage.verifyFacilityIndicatorColor("Overall", "GREEN");

    homePage.navigateHomePage();
    homePage.navigateOfflineDistribution();

    distributionPage.syncDistribution(1);
    assertTrue(distributionPage.isFacilitySyncFailed());

    switchOnNetworkInterface(wifiInterface);
    testWebDriver.sleep(7000);

    distributionPage.clickRetryButton();
    assertEquals(distributionPage.getSyncStatusMessage(), "Sync Status");
    assertTrue("Incorrect Sync Facility", distributionPage.getSyncMessage().contains("F10-Village Dispensary"));

    distributionPage.syncDistributionMessageDone();
    assertEquals(distributionPage.getDistributionStatus(), "SYNCED");
    assertFalse(distributionPage.getTextDistributionList().contains("sync"));

    Map<String, String> distributionDetails = dbWrapper.getDistributionDetails(deliveryZoneNameFirst, programFirst, "Period14");
    assertEquals(distributionDetails.get("status"), "SYNCED");

    distributionPage.clickRecordData(1);
    facilityListPage.selectFacility(facilityCodeFirst);
    facilityListPage.verifyFacilityIndicatorColor("Overall", "BLUE");

    verifyEpiUseDataInDatabase(10, 20, 30, null, 50, "10/2011", "PG1", facilityCodeFirst);
    verifyFacilityVisitInformationInDatabase(facilityCodeFirst, null, null, null, null, null, null, "t", "f");
    verifyFullCoveragesDataInDatabase(5, 7, 0, 9999999, facilityCodeFirst);
    verifyEpiInventoryDataInDatabase(null, null, null, "P10", facilityCodeFirst);
    verifyEpiInventoryDataInDatabase(null, null, null, "Product6", facilityCodeFirst);
    verifyEpiInventoryDataInDatabase(null, null, null, "P11", facilityCodeFirst);

    visitInformationPage.verifyAllFieldsDisabled();

    visitInformationPage.navigateToEpiUse();
    epiUsePage.verifyAllFieldsDisabled();

    epiUsePage.navigateToRefrigerators();
    refrigeratorPage.clickShowForRefrigerator1();
    refrigeratorPage.verifyAllFieldsDisabled();

    refrigeratorPage.navigateToFullCoverage();
    fullCoveragePage.verifyAllFieldsDisabled();

  }

  private void configureISA() {
    LoginPage loginPage = PageFactory.getInstanceOfLoginPage(testWebDriver, baseUrlGlobal);
    HomePage homePage = loginPage.loginAs("Admin123", "Admin123");

    ProgramProductISAPage programProductISAPage = homePage.navigateProgramProductISA();
    programProductISAPage.fillProgramProductISA("VACCINES", "90", "1", "50", "30", "0", "100", "2000", "333");
    homePage.logout();
  }

  private void insertRegimenProductMapping() throws SQLException {
    dbWrapper.insertRegimensProductsInMappingTable("BCG", "BCG");
    dbWrapper.insertRegimensProductsInMappingTable("Polio (Newborn)", "polio10dose");
    dbWrapper.insertRegimensProductsInMappingTable("Polio 1st dose", "polio20dose");
    dbWrapper.insertRegimensProductsInMappingTable("Polio 2nd dose", "polio10dose");
    dbWrapper.insertRegimensProductsInMappingTable("Polio 3rd dose", "polio20dose");
    dbWrapper.insertRegimensProductsInMappingTable("Penta 1st dose", "penta1");
    dbWrapper.insertRegimensProductsInMappingTable("Penta 2nd dose", "penta10");
    dbWrapper.insertRegimensProductsInMappingTable("Penta 3rd dose", "penta1");
    dbWrapper.insertRegimensProductsInMappingTable("PCV10 1st dose", "P10");
    dbWrapper.insertRegimensProductsInMappingTable("PCV10 2nd dose", "P10");
    dbWrapper.insertRegimensProductsInMappingTable("PCV10 3rd dose", "P10");
    dbWrapper.insertRegimensProductsInMappingTable("Measles", "Measles");
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