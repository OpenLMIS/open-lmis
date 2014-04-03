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

import cucumber.api.java.en.And;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import org.openlmis.UiUtils.TestCaseHelper;
import org.openlmis.UiUtils.TestWebDriver;
import org.openlmis.pageobjects.*;
import org.openqa.selenium.JavascriptExecutor;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.io.IOException;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static com.thoughtworks.selenium.SeleneseTestBase.*;

public class DistributionVisitInformationSyncTest extends TestCaseHelper {
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

  public Map<String, String> visitInformationData = new HashMap<String, String>() {{
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
  public void setUp() throws InterruptedException, SQLException, IOException {
    super.setup();
    loginPage = PageObjectFactory.getLoginPage(testWebDriver, baseUrlGlobal);
    facilityListPage = PageObjectFactory.getFacilityListPage(testWebDriver);

    setupDataForDistributionTest(visitInformationData);
    dbWrapper.insertProductGroup(visitInformationData.get(PRODUCT_GROUP_CODE));
    dbWrapper.insertProductWithGroup("Product5", "ProductName5", visitInformationData.get(PRODUCT_GROUP_CODE), true);
    dbWrapper.insertProductWithGroup("Product6", "ProductName6", visitInformationData.get(PRODUCT_GROUP_CODE), true);
    dbWrapper.insertProgramProduct("Product5", visitInformationData.get(VACCINES_PROGRAM), "10", "false");
    dbWrapper.insertProgramProduct("Product6", visitInformationData.get(VACCINES_PROGRAM), "10", "true");
  }

  @Test(groups = {"distribution"})
  public void testVisitInformationPageWhenFacilityVisited() throws SQLException {
    loginPage.loginAs(visitInformationData.get(USER), visitInformationData.get(PASSWORD));
    initiateDistribution(visitInformationData.get(FIRST_DELIVERY_ZONE_NAME), visitInformationData.get(VACCINES_PROGRAM));
    VisitInformationPage visitInformationPage = facilityListPage.selectFacility(visitInformationData.get(FIRST_FACILITY_CODE));
    verifyLabels();
    assertEquals("Was " + dbWrapper.getAttributeFromTable("facilities", "name", "code", visitInformationData.get(FIRST_FACILITY_CODE)) +
      " visited in " + "Period14" + "?", visitInformationPage.getWasFacilityVisitedLabel());
    visitInformationPage.verifyIndicator("RED");
    visitInformationPage.selectFacilityVisitedYes();
    visitInformationPage.verifyIndicator("AMBER");
    testWebDriver.refresh();
    assertTrue(visitInformationPage.isYesRadioButtonSelected());
    visitInformationPage.enterVisitDateAsCurrentDate();
    visitInformationPage.verifyIndicator("AMBER");
    visitInformationPage.enterObservations("Some Observations");
    visitInformationPage.verifyIndicator("AMBER");
    visitInformationPage.enterConfirmedByName("ConfirmName");
    visitInformationPage.verifyIndicator("AMBER");
    visitInformationPage.enterConfirmedByTitle("ConfirmTitle");
    visitInformationPage.verifyIndicator("AMBER");
    visitInformationPage.enterVerifiedByName("VerifyName");
    visitInformationPage.verifyIndicator("AMBER");
    visitInformationPage.enterVerifiedByTitle("VerifyTitle");
    visitInformationPage.verifyIndicator("GREEN");
    visitInformationPage.enterVehicleId("12U3-93");
    visitInformationPage.verifyIndicator("GREEN");
  }

  @Test(groups = {"distribution"})
  public void testVisitInformationPageAndFacilityIndicatorWhenFacilityVisitedAndSync() throws SQLException {
    dbWrapper.addRefrigeratorToFacility("LG", "yu", "Hry3", visitInformationData.get(FIRST_FACILITY_CODE));
    HomePage homePage = loginPage.loginAs(visitInformationData.get(USER), visitInformationData.get(PASSWORD));
    initiateDistribution(visitInformationData.get(FIRST_DELIVERY_ZONE_NAME), visitInformationData.get(VACCINES_PROGRAM));
    VisitInformationPage visitInformationPage = facilityListPage.selectFacility(visitInformationData.get(FIRST_FACILITY_CODE));
    facilityListPage.verifyOverallFacilityIndicatorColor("RED");
    assertEquals("Was " + dbWrapper.getAttributeFromTable("facilities", "name", "code", visitInformationData.get(FIRST_FACILITY_CODE)) +
      " visited in " + "Period14" + "?", visitInformationPage.getWasFacilityVisitedLabel());
    visitInformationPage.verifyIndicator("RED");
    visitInformationPage.selectFacilityVisitedYes();
    visitInformationPage.verifyIndicator("AMBER");
    facilityListPage.verifyOverallFacilityIndicatorColor("AMBER");
    visitInformationPage.enterVisitDateAsCurrentDate();
    visitInformationPage.verifyIndicator("AMBER");
    visitInformationPage.enterObservations("Some Observations");
    visitInformationPage.verifyIndicator("AMBER");
    visitInformationPage.enterConfirmedByName("ConfirmName");
    visitInformationPage.verifyIndicator("AMBER");
    visitInformationPage.enterConfirmedByTitle("ConfirmTitle");
    visitInformationPage.verifyIndicator("AMBER");
    visitInformationPage.enterVerifiedByName("VerifyName");
    visitInformationPage.verifyIndicator("AMBER");

    fillFacilityData(true);

    DistributionPage distributionPage = homePage.navigateToDistributionWhenOnline();
    distributionPage.clickSyncDistribution(1);
    assertEquals("No facility for the chosen zone, program and period is ready to be sync", distributionPage.getSyncAlertMessage());
    distributionPage.clickRecordData(1);
    facilityListPage.selectFacility(visitInformationData.get(FIRST_FACILITY_CODE));

    facilityListPage.verifyOverallFacilityIndicatorColor("AMBER");
    visitInformationPage.enterVerifiedByTitle("VerifyTitle");
    visitInformationPage.verifyIndicator("GREEN");
    facilityListPage.verifyOverallFacilityIndicatorColor("GREEN");
    visitInformationPage.enterVehicleId("12U3-93");
    visitInformationPage.verifyIndicator("GREEN");
    visitInformationPage.enterVehicleId("012U3-93");
    facilityListPage.verifyOverallFacilityIndicatorColor("GREEN");

    distributionPage = homePage.navigateToDistributionWhenOnline();
    distributionPage.syncDistribution(1);
    assertTrue(distributionPage.getSyncMessage().contains(visitInformationData.get(FIRST_FACILITY_CODE) + "-Village Dispensary"));
    distributionPage.syncDistributionMessageDone();

    verifyFacilityVisitInformationInDatabase(visitInformationData.get(FIRST_FACILITY_CODE), "Some Observations",
      "ConfirmName", "ConfirmTitle", "VerifyName", "VerifyTitle", "012U3-93", "t", "t", null, null);
    homePage.navigateToDistributionWhenOnline();
    distributionPage.clickRecordData(1);
    facilityListPage.selectFacility(visitInformationData.get(FIRST_FACILITY_CODE));
    visitInformationPage.verifyAllFieldsDisabled();
    assertTrue(visitInformationPage.isYesRadioButtonSelected());
    verifyFacilityVisitInformationInDatabase(visitInformationData.get(FIRST_FACILITY_CODE), "Some Observations", "ConfirmName", "ConfirmTitle", "VerifyName", "VerifyTitle", "012U3-93", "t", "t", null, null);
  }

  @Test(groups = {"distribution"})
  public void testVisitInformationPageWhenFacilityNotVisitedAndNoRefrigeratorPresent() throws SQLException {
    loginPage.loginAs(visitInformationData.get(USER), visitInformationData.get(PASSWORD));
    initiateDistribution(visitInformationData.get(FIRST_DELIVERY_ZONE_NAME), visitInformationData.get(VACCINES_PROGRAM));
    VisitInformationPage visitInformationPage = facilityListPage.selectFacility(visitInformationData.get(FIRST_FACILITY_CODE));
    assertEquals("Was " + dbWrapper.getAttributeFromTable("facilities", "name", "code", visitInformationData.get(FIRST_FACILITY_CODE)) +
      " visited in " + "Period14" + "?", visitInformationPage.getWasFacilityVisitedLabel());
    visitInformationPage.verifyIndicator("RED");
    visitInformationPage.selectFacilityVisitedNo();
    visitInformationPage.verifyIndicator("AMBER");
    EpiInventoryPage epiInventoryPage = visitInformationPage.navigateToEpiInventory();
    epiInventoryPage.navigateToVisitInformation();
    assertTrue(visitInformationPage.isNoRadioButtonSelected());
    visitInformationPage.selectReasonBadWeather();
    visitInformationPage.verifyIndicator("GREEN");
    visitInformationPage.selectReasonOther();
    visitInformationPage.verifyIndicator("AMBER");
    testWebDriver.refresh();
    assertTrue(visitInformationPage.isOtherReasonSelected());
    visitInformationPage.enterOtherReasonInTextBox("Reason for not visiting the facility");
    visitInformationPage.verifyIndicator("GREEN");
    RefrigeratorPage refrigeratorPage = visitInformationPage.navigateToRefrigerators();
    refrigeratorPage.verifyIndicator("GREEN");
    assertFalse(refrigeratorPage.isAddNewButtonEnabled());
    epiInventoryPage = refrigeratorPage.navigateToEpiInventory();
    epiInventoryPage.verifyIndicator("GREEN");
    epiInventoryPage.verifyAllFieldsDisabled();
  }

  @Test(groups = {"distribution"})
  public void testVisitInformationPageSyncWhenFacilityNotVisitedAndAllFormsFilled() throws SQLException {
    dbWrapper.addRefrigeratorToFacility("LG", "GR890", "GNRE0989", visitInformationData.get(FIRST_FACILITY_CODE));
    HomePage homePage = loginPage.loginAs(visitInformationData.get(USER), visitInformationData.get(PASSWORD));
    initiateDistribution(visitInformationData.get(FIRST_DELIVERY_ZONE_NAME), visitInformationData.get(VACCINES_PROGRAM));
    VisitInformationPage visitInformationPage = facilityListPage.selectFacility(visitInformationData.get(FIRST_FACILITY_CODE));
    assertEquals("Was " + dbWrapper.getAttributeFromTable("facilities", "name", "code", visitInformationData.get(FIRST_FACILITY_CODE)) +
      " visited in " + "Period14" + "?", visitInformationPage.getWasFacilityVisitedLabel());
    RefrigeratorPage refrigeratorPage = visitInformationPage.navigateToRefrigerators();
    refrigeratorPage.clickDelete();
    refrigeratorPage.clickOKButton();

    refrigeratorPage.clickAddNew();
    refrigeratorPage.addNewRefrigerator("SAM", "800L", "GNR7876");

    refrigeratorPage.clickShowForRefrigerator(1);
    refrigeratorPage.enterValueInRefrigeratorTemperature("3", 1);
    refrigeratorPage.clickFunctioningCorrectlyYesRadio(1);
    refrigeratorPage.enterValueInLowAlarmEvents("2", 1);
    refrigeratorPage.enterValueInHighAlarmEvents("5", 1);
    refrigeratorPage.clickProblemSinceLastVisitNR(1);
    refrigeratorPage.clickDone();
    refrigeratorPage.verifyRefrigeratorColor("overall", "GREEN");
    refrigeratorPage.verifyRefrigeratorColor("individual", "GREEN");

    EPIUsePage epiUsePage = refrigeratorPage.navigateToEpiUse();
    epiUsePage.enterData(10, 20, 30, 40, 50, "10/2011", 1);

    EpiInventoryPage epiInventoryPage = refrigeratorPage.navigateToEpiInventory();
    epiInventoryPage.applyNRToAll();
    epiInventoryPage.fillDeliveredQuantity(1, "2");
    epiInventoryPage.fillDeliveredQuantity(2, "4");
    epiInventoryPage.fillDeliveredQuantity(3, "6");
    epiInventoryPage.verifyIndicator("GREEN");

    FullCoveragePage fullCoveragePage = epiInventoryPage.navigateToFullCoverage();
    fullCoveragePage.enterData(23, 66, 77, "45");

    fullCoveragePage.navigateToVisitInformation();

    visitInformationPage.verifyIndicator("RED");
    visitInformationPage.selectFacilityVisitedNo();
    visitInformationPage.verifyIndicator("AMBER");
    visitInformationPage.selectReasonBadWeather();
    visitInformationPage.verifyIndicator("GREEN");
    visitInformationPage.navigateToRefrigerators();

    refrigeratorPage.verifyIndicator("GREEN");
    assertFalse(refrigeratorPage.isAddNewButtonEnabled());
    refrigeratorPage.clickShowForRefrigerator(1);
    refrigeratorPage.verifyAllFieldsDisabled();

    refrigeratorPage.navigateToEpiInventory();
    epiInventoryPage.verifyIndicator("GREEN");
    epiInventoryPage.verifyAllFieldsDisabled();

    epiInventoryPage.navigateToEpiUse();
    epiUsePage.verifyIndicator("GREEN");
    epiUsePage.verifyAllFieldsDisabled();

    ChildCoveragePage childCoveragePage = epiUsePage.navigateToChildCoverage();
    childCoveragePage.applyNRToAll();
    childCoveragePage.clickOK();

    AdultCoveragePage adultCoveragePage = childCoveragePage.navigateToAdultCoverage();
    adultCoveragePage.clickApplyNrToAll();
    adultCoveragePage.clickOK();

    facilityListPage.verifyOverallFacilityIndicatorColor("GREEN");

    homePage.navigateHomePage();
    DistributionPage distributionPage = homePage.navigateToDistributionWhenOnline();
    distributionPage.syncDistribution(1);
    distributionPage.syncDistributionMessageDone();

    verifyEpiUseDataInDatabase(null, null, null, null, null, null, "PG1", visitInformationData.get(FIRST_FACILITY_CODE));
    verifyFacilityVisitInformationInDatabase(visitInformationData.get(FIRST_FACILITY_CODE), null, null, null, null, null, null, "t", "f", "ROAD_IMPASSABLE", null);
    verifyFullCoveragesDataInDatabase(23, 66, 77, 45, visitInformationData.get(FIRST_FACILITY_CODE));
    verifyEpiInventoryDataInDatabase(null, null, null, "P10", visitInformationData.get(FIRST_FACILITY_CODE));
    verifyEpiInventoryDataInDatabase(null, null, null, "Product6", visitInformationData.get(FIRST_FACILITY_CODE));
    verifyEpiInventoryDataInDatabase(null, null, null, "P11", visitInformationData.get(FIRST_FACILITY_CODE));
    verifyRefrigeratorReadingsNullInDatabase("GNRE0989", visitInformationData.get(FIRST_FACILITY_CODE));
    verifyRefrigeratorsDataInDatabase(visitInformationData.get(FIRST_FACILITY_CODE), "GNRE0989", "LG", "GR890", "t");

    distributionPage.clickRecordData(1);
    facilityListPage.selectFacility(visitInformationData.get(FIRST_FACILITY_CODE));
    facilityListPage.verifyOverallFacilityIndicatorColor("BLUE");
    facilityListPage.verifyIndividualFacilityIndicatorColor(visitInformationData.get(FIRST_FACILITY_CODE), "BLUE");

    verifyAllFieldsDisabled();
    testWebDriver.refresh();
    verifyAllFieldsDisabled();
  }

  @Test(groups = {"distribution"})
  public void testVisitInformationPageSyncWhenFacilityNotVisitedAndFormsPartiallyFilled() throws SQLException {
    dbWrapper.addRefrigeratorToFacility("LG", "800L", "GNR7878", "F10");
    dbWrapper.addRefrigeratorToFacility("LG", "800L", "GNR7878", "F11");
    HomePage homePage = loginPage.loginAs(visitInformationData.get(USER), visitInformationData.get(PASSWORD));
    initiateDistribution(visitInformationData.get(FIRST_DELIVERY_ZONE_NAME), visitInformationData.get(VACCINES_PROGRAM));
    VisitInformationPage visitInformationPage = facilityListPage.selectFacility(visitInformationData.get(FIRST_FACILITY_CODE));

    facilityListPage.verifyOverallFacilityIndicatorColor("RED");
    visitInformationPage.enterDataWhenFacilityVisited("samuel D", "Doe Abc", "Verifier", "Verifier Title");

    RefrigeratorPage refrigeratorPage = visitInformationPage.navigateToRefrigerators();
    refrigeratorPage.verifyRefrigeratorColor("overall", "RED");
    refrigeratorPage.clickShowForRefrigerator(1);
    refrigeratorPage.enterValueInRefrigeratorTemperature("3", 1);
    refrigeratorPage.clickFunctioningCorrectlyYesRadio(1);
    refrigeratorPage.enterValueInLowAlarmEvents("2", 1);
    refrigeratorPage.enterValueInHighAlarmEvents("5", 1);
    refrigeratorPage.clickDone();
    refrigeratorPage.verifyRefrigeratorColor("overall", "AMBER");

    EpiInventoryPage epiInventoryPage = refrigeratorPage.navigateToEpiInventory();
    epiInventoryPage.verifyIndicator("RED");
    epiInventoryPage.fillExistingQuantity(1, "1");
    epiInventoryPage.fillDeliveredQuantity(1, "2");
    epiInventoryPage.fillSpoiledQuantity(1, "3");
    epiInventoryPage.verifyIndicator("AMBER");

    EPIUsePage epiUsePage = epiInventoryPage.navigateToEpiUse();
    epiUsePage.verifyIndicator("RED");
    epiUsePage.enterValueInDistributed("5", 1);
    epiUsePage.verifyIndicator("AMBER");

    epiUsePage.navigateToVisitInformation();
    visitInformationPage.selectFacilityVisitedNo();
    visitInformationPage.selectReasonNoTransport();

    refrigeratorPage.verifyRefrigeratorColor("overall", "GREEN");
    epiInventoryPage.verifyIndicator("GREEN");
    epiInventoryPage.verifyAllFieldsDisabled();

    refrigeratorPage.navigateToEpiUse();
    epiUsePage.verifyIndicator("GREEN");
    epiUsePage.verifyAllFieldsDisabled();

    FullCoveragePage fullCoveragePage = refrigeratorPage.navigateToFullCoverage();
    fullCoveragePage.clickApplyNRToAll();

    ChildCoveragePage childCoveragePage = fullCoveragePage.navigateToChildCoverage();
    childCoveragePage.applyNRToAll();
    childCoveragePage.clickOK();

    AdultCoveragePage adultCoveragePage = childCoveragePage.navigateToAdultCoverage();
    adultCoveragePage.clickApplyNrToAll();
    adultCoveragePage.clickOK();

    adultCoveragePage.navigateToEpiInventory();
    facilityListPage.verifyOverallFacilityIndicatorColor("GREEN");

    homePage.navigateHomePage();
    DistributionPage distributionPage = homePage.navigateToDistributionWhenOnline();
    distributionPage.syncDistribution(1);
    distributionPage.syncDistributionMessageDone();

    verifyEpiUseDataInDatabase(null, null, null, null, null, null, "PG1", visitInformationData.get(FIRST_FACILITY_CODE));
    verifyFacilityVisitInformationInDatabase(visitInformationData.get(FIRST_FACILITY_CODE), null, null, null, null, null, null, "t", "f", "TRANSPORT_UNAVAILABLE", null);
    verifyFullCoveragesDataInDatabase(null, null, null, null, visitInformationData.get(FIRST_FACILITY_CODE));
    verifyEpiInventoryDataInDatabase(null, null, null, "P10", visitInformationData.get(FIRST_FACILITY_CODE));
    verifyEpiInventoryDataInDatabase(null, null, null, "Product6", visitInformationData.get(FIRST_FACILITY_CODE));
    verifyEpiInventoryDataInDatabase(null, null, null, "P11", visitInformationData.get(FIRST_FACILITY_CODE));
    verifyRefrigeratorReadingsNullInDatabase("GNR7878", visitInformationData.get(FIRST_FACILITY_CODE));
    verifyRefrigeratorsDataInDatabase(visitInformationData.get(FIRST_FACILITY_CODE), "GNR7878", "LG", "800L", "t");

    distributionPage.clickRecordData(1);
    facilityListPage.selectFacility(visitInformationData.get(FIRST_FACILITY_CODE));
    facilityListPage.verifyOverallFacilityIndicatorColor("BLUE");
    facilityListPage.verifyIndividualFacilityIndicatorColor(visitInformationData.get(FIRST_FACILITY_CODE), "BLUE");
    facilityListPage.verifyIndividualFacilityIndicatorColor(visitInformationData.get(SECOND_FACILITY_CODE), "RED");

    verifyAllFieldsDisabled();
    testWebDriver.refresh();
    verifyAllFieldsDisabled();
  }

  @Test(groups = {"distribution"})
  public void testVisitInformationPageAndFacilityIndicatorWhenFacilityNotVisited() throws SQLException {
    dbWrapper.addRefrigeratorToFacility("LG", "yu", "Hry3", visitInformationData.get(FIRST_FACILITY_CODE));
    loginPage.loginAs(visitInformationData.get(USER), visitInformationData.get(PASSWORD));
    initiateDistribution(visitInformationData.get(FIRST_DELIVERY_ZONE_NAME), visitInformationData.get(VACCINES_PROGRAM));
    VisitInformationPage visitInformationPage = facilityListPage.selectFacility(visitInformationData.get(FIRST_FACILITY_CODE));
    facilityListPage.verifyOverallFacilityIndicatorColor("RED");
    assertEquals("Was " + dbWrapper.getAttributeFromTable("facilities", "name", "code", visitInformationData.get(FIRST_FACILITY_CODE)) +
      " visited in " + "Period14" + "?", visitInformationPage.getWasFacilityVisitedLabel());
    visitInformationPage.verifyIndicator("RED");
    visitInformationPage.selectFacilityVisitedNo();
    visitInformationPage.verifyIndicator("AMBER");
    visitInformationPage.selectReasonBadWeather();
    visitInformationPage.verifyIndicator("GREEN");
    visitInformationPage.selectReasonOther();
    visitInformationPage.verifyIndicator("AMBER");

    fillFacilityData(false);
    facilityListPage.verifyOverallFacilityIndicatorColor("AMBER");
    visitInformationPage.enterOtherReasonInTextBox("Reason for not visiting the facility");
    visitInformationPage.verifyIndicator("GREEN");
    facilityListPage.verifyOverallFacilityIndicatorColor("GREEN");
  }

  private void verifyLabels() {
    VisitInformationPage visitInformationPage = PageObjectFactory.getVisitInformationPage(testWebDriver);
    assertEquals("Visit Info / Observations", visitInformationPage.getFacilityVisitTabLabel());
    assertEquals("Visit Info / Observations", visitInformationPage.getVisitInformationPageLabel());
  }

  public void initiateDistribution(String deliveryZoneNameFirst, String programFirst) {
    HomePage homePage = PageObjectFactory.getHomePage(testWebDriver);
    DistributionPage distributionPage = homePage.navigateToDistributionWhenOnline();
    distributionPage.selectValueFromDeliveryZone(deliveryZoneNameFirst);
    distributionPage.selectValueFromProgram(programFirst);
    distributionPage.clickInitiateDistribution();
    distributionPage.clickRecordData(1);
  }

  private VisitInformationPage fillFacilityData(Boolean wasFacilityVisited) {
    VisitInformationPage visitInformationPage = PageObjectFactory.getVisitInformationPage(testWebDriver);

    if (wasFacilityVisited) {
      RefrigeratorPage refrigeratorPage = visitInformationPage.navigateToRefrigerators();
      refrigeratorPage.navigateToRefrigerators();
      refrigeratorPage.clickDelete();
      refrigeratorPage.clickOKButton();

      refrigeratorPage.clickAddNew();
      refrigeratorPage.addNewRefrigerator("SAM", "800L", "GNR7876");

      refrigeratorPage.clickShowForRefrigerator(1);
      refrigeratorPage.enterValueInRefrigeratorTemperature("3", 1);
      refrigeratorPage.clickFunctioningCorrectlyNR(1);
      EpiInventoryPage epiInventoryPage = refrigeratorPage.navigateToEpiInventory();
      epiInventoryPage.navigateToRefrigerators();
      refrigeratorPage.clickShowForRefrigerator(1);
      assertTrue(refrigeratorPage.isFunctioningCorrectlyNRSelected(1));
      refrigeratorPage.enterValueInLowAlarmEvents("2", 1);
      refrigeratorPage.enterValueInHighAlarmEvents("5", 1);
      refrigeratorPage.clickProblemSinceLastVisitNR(1);
      refrigeratorPage.clickDone();
      refrigeratorPage.verifyRefrigeratorColor("overall", "GREEN");
      refrigeratorPage.verifyRefrigeratorColor("individual", "GREEN");

      epiInventoryPage = refrigeratorPage.navigateToEpiInventory();
      epiInventoryPage.applyNRToAll();
      epiInventoryPage.fillDeliveredQuantity(1, "2");
      epiInventoryPage.fillDeliveredQuantity(2, "4");
      epiInventoryPage.fillDeliveredQuantity(3, "6");

      EPIUsePage epiUsePage = visitInformationPage.navigateToEpiUse();
      epiUsePage.enterData(10, 20, 30, 40, 50, "10/2011", 1);
    }

    ChildCoveragePage childCoveragePage = visitInformationPage.navigateToChildCoverage();
    childCoveragePage.applyNRToAll();
    childCoveragePage.clickOK();

    FullCoveragePage fullCoveragePage = childCoveragePage.navigateToFullCoverage();
    fullCoveragePage.enterData(23, 66, 77, "45");

    AdultCoveragePage adultCoveragePage = fullCoveragePage.navigateToAdultCoverage();
    adultCoveragePage.enterDataInAllFields();

    adultCoveragePage.navigateToVisitInformation();

    return visitInformationPage;
  }

  private void verifyAllFieldsDisabled() {
    VisitInformationPage visitInformationPage = PageObjectFactory.getVisitInformationPage(testWebDriver);
    visitInformationPage.verifyAllFieldsDisabled();

    RefrigeratorPage refrigeratorPage = visitInformationPage.navigateToRefrigerators();
    refrigeratorPage.clickShowForRefrigerator(1);
    refrigeratorPage.verifyAllFieldsDisabled();

    EPIUsePage epiUsePage = refrigeratorPage.navigateToEpiUse();
    epiUsePage.verifyAllFieldsDisabled();

    FullCoveragePage fullCoveragePage = epiUsePage.navigateToFullCoverage();
    fullCoveragePage.verifyAllFieldsDisabled();

    EpiInventoryPage epiInventoryPage = fullCoveragePage.navigateToEpiInventory();
    epiInventoryPage.verifyAllFieldsDisabled();
    epiInventoryPage.navigateToVisitInformation();
  }

  @When("^I verify radio button \"([^\"]*)\" is selected$")
  public void verifyRadioButtonSelected(String radioButtonSelected) {
    VisitInformationPage visitInformationPage = PageObjectFactory.getVisitInformationPage(testWebDriver);
    if (radioButtonSelected.toLowerCase().equals("yes")) {
      assertTrue(visitInformationPage.isYesRadioButtonSelected());
      assertFalse(visitInformationPage.isNoRadioButtonSelected());
    } else if (radioButtonSelected.toLowerCase().equals("no")) {
      assertTrue(visitInformationPage.isNoRadioButtonSelected());
      assertFalse(visitInformationPage.isYesRadioButtonSelected());
    }
  }

  @And("^I verify visit date")
  public void verifyVisitDate() {
    VisitInformationPage visitInformationPage = PageObjectFactory.getVisitInformationPage(testWebDriver);
    String actualDate = visitInformationPage.getVisitDate();
    String expectedDate = new SimpleDateFormat("dd/MM/yyyy").format(new Date());
    assertEquals(expectedDate, actualDate);
  }

  @And("^I select visit date as current date$")
  public void enterVisitDateAsCurrentDate() {
    VisitInformationPage visitInformationPage = PageObjectFactory.getVisitInformationPage(testWebDriver);
    visitInformationPage.enterVisitDateAsCurrentDate();
  }

  @Then("^I enter vehicle id as \"([^\"]*)\"$")
  public void enterVehicleId(String vehicleId) {
    VisitInformationPage visitInformationPage = PageObjectFactory.getVisitInformationPage(testWebDriver);
    visitInformationPage.enterVehicleId(vehicleId);
    visitInformationPage.removeFocusFromElement();
  }

  @When("^I select \"([^\"]*)\" facility visited$")
  public void selectFacilityVisitedOption(String option) {
    VisitInformationPage visitInformationPage = PageObjectFactory.getVisitInformationPage(testWebDriver);
    if (option.toLowerCase().equals("yes")) {
      visitInformationPage.selectFacilityVisitedYes();
    } else if (option.toLowerCase().equals("no")) {
      visitInformationPage.selectFacilityVisitedNo();
    }
    visitInformationPage.removeFocusFromElement();
  }

  @And("^I select No Transport reason$")
  public void selectNoTransportReason() {
    VisitInformationPage visitInformationPage = PageObjectFactory.getVisitInformationPage(testWebDriver);
    visitInformationPage.selectReasonNoTransport();
  }

  @And("^I select Others reason$")
  public void selectOtherReason() {
    VisitInformationPage visitInformationPage = PageObjectFactory.getVisitInformationPage(testWebDriver);
    visitInformationPage.selectReasonOther();
  }

  @And("^I enter Other reason as \"([^\"]*)\"$")
  public void enterOtherReason(String reason) {
    VisitInformationPage visitInformationPage = PageObjectFactory.getVisitInformationPage(testWebDriver);
    visitInformationPage.enterOtherReasonInTextBox(reason);
    visitInformationPage.removeFocusFromElement();
  }

  @And("^I verify Others reason selected$")
  public void isNoTransportReasonSelected() {
    VisitInformationPage visitInformationPage = PageObjectFactory.getVisitInformationPage(testWebDriver);
    visitInformationPage.isOtherReasonSelected();
  }

  @And("^I verify Other reason entered as \"([^\"]*)\"$")
  public void verifyOtherReason(String reason) {
    VisitInformationPage visitInformationPage = PageObjectFactory.getVisitInformationPage(testWebDriver);
    assertEquals(reason, visitInformationPage.getOtherReason());
  }

  @AfterMethod(groups = "distribution")
  public void tearDown() throws SQLException {
    testWebDriver.sleep(500);
    if (!testWebDriver.getElementById("username").isDisplayed()) {
      HomePage homePage = PageObjectFactory.getHomePage(testWebDriver);
      homePage.logout(baseUrlGlobal);
      dbWrapper.deleteData();
      dbWrapper.closeConnection();
    }
    ((JavascriptExecutor) TestWebDriver.getDriver()).executeScript("indexedDB.deleteDatabase('open_lmis');");
  }
}
