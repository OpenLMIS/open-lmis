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
import org.openlmis.pageobjects.*;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.io.IOException;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.thoughtworks.selenium.SeleneseTestBase.*;
import static java.util.Arrays.asList;

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
    loginPage = PageFactory.getInstanceOfLoginPage(testWebDriver, baseUrlGlobal);
    facilityListPage = PageFactory.getInstanceOfFacilityListPage(testWebDriver);

    Map<String, String> dataMap = visitInformationData;
    setupDataForDistributionTest(dataMap.get(USER), dataMap.get(FIRST_DELIVERY_ZONE_CODE), dataMap.get(SECOND_DELIVERY_ZONE_CODE),
      dataMap.get(FIRST_DELIVERY_ZONE_NAME), dataMap.get(SECOND_DELIVERY_ZONE_NAME), dataMap.get(FIRST_FACILITY_CODE),
      dataMap.get(SECOND_FACILITY_CODE), dataMap.get(VACCINES_PROGRAM), dataMap.get(TB_PROGRAM), dataMap.get(SCHEDULE),
      dataMap.get(PRODUCT_GROUP_CODE));
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
    visitInformationPage.enterVisitDateAsFirstOfCurrentMonth();
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
  public void testVisitInformationPageAndFacilityIndicatorWhenFacilityVisited() throws SQLException {
    dbWrapper.addRefrigeratorToFacility("LG", "yu", "Hry3", visitInformationData.get(FIRST_FACILITY_CODE));
    loginPage.loginAs(visitInformationData.get(USER), visitInformationData.get(PASSWORD));
    initiateDistribution(visitInformationData.get(FIRST_DELIVERY_ZONE_NAME), visitInformationData.get(VACCINES_PROGRAM));
    VisitInformationPage visitInformationPage = facilityListPage.selectFacility(visitInformationData.get(FIRST_FACILITY_CODE));
    facilityListPage.verifyFacilityIndicatorColor("Overall", "RED");
    assertEquals("Was " + dbWrapper.getAttributeFromTable("facilities", "name", "code", visitInformationData.get(FIRST_FACILITY_CODE)) +
      " visited in " + "Period14" + "?", visitInformationPage.getWasFacilityVisitedLabel());
    visitInformationPage.verifyIndicator("RED");
    visitInformationPage.selectFacilityVisitedYes();
    visitInformationPage.verifyIndicator("AMBER");
    facilityListPage.verifyFacilityIndicatorColor("Overall", "AMBER");
    visitInformationPage.enterVisitDateAsFirstOfCurrentMonth();
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
    facilityListPage.verifyFacilityIndicatorColor("Overall", "AMBER");
    visitInformationPage.enterVerifiedByTitle("VerifyTitle");
    visitInformationPage.verifyIndicator("GREEN");
    facilityListPage.verifyFacilityIndicatorColor("Overall", "GREEN");
    visitInformationPage.enterVehicleId("12U3-93");
    visitInformationPage.verifyIndicator("GREEN");
    facilityListPage.verifyFacilityIndicatorColor("Overall", "GREEN");
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
    visitInformationPage.selectReasonBadWeather();
    visitInformationPage.verifyIndicator("GREEN");
    visitInformationPage.selectReasonOther();
    visitInformationPage.verifyIndicator("AMBER");
    visitInformationPage.enterOtherReasonInTextBox("Reason for not visiting the facility");
    visitInformationPage.verifyIndicator("GREEN");
    RefrigeratorPage refrigeratorPage = visitInformationPage.navigateToRefrigerators();
    refrigeratorPage.verifyIndicator("GREEN");
    assertFalse(refrigeratorPage.isAddNewButtonEnabled());
    EpiInventoryPage epiInventoryPage = refrigeratorPage.navigateToEpiInventory();
    epiInventoryPage.verifyIndicator("GREEN");
    epiInventoryPage.verifyAllFieldsDisabled();
  }

  @Test(groups = {"distribution"})
  public void testVisitInformationPageWhenFacilityNotVisitedAndAllFormsFilled() throws SQLException {
    dbWrapper.addRefrigeratorToFacility("LG", "GR890", "GNRE0989", visitInformationData.get(FIRST_FACILITY_CODE));
    loginPage.loginAs(visitInformationData.get(USER), visitInformationData.get(PASSWORD));
    initiateDistribution(visitInformationData.get(FIRST_DELIVERY_ZONE_NAME), visitInformationData.get(VACCINES_PROGRAM));
    VisitInformationPage visitInformationPage = facilityListPage.selectFacility(visitInformationData.get(FIRST_FACILITY_CODE));
    assertEquals("Was " + dbWrapper.getAttributeFromTable("facilities", "name", "code", visitInformationData.get(FIRST_FACILITY_CODE)) +
      " visited in " + "Period14" + "?", visitInformationPage.getWasFacilityVisitedLabel());
    RefrigeratorPage refrigeratorPage = visitInformationPage.navigateToRefrigerators();
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

    EpiInventoryPage epiInventoryPage = epiUsePage.navigateToEpiInventory();
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
    visitInformationPage.selectReasonOther();
    visitInformationPage.verifyIndicator("AMBER");
    visitInformationPage.enterOtherReasonInTextBox("Reason for not visiting the facility");
    visitInformationPage.verifyIndicator("GREEN");
    visitInformationPage.navigateToRefrigerators();

    refrigeratorPage.verifyIndicator("GREEN");
    assertFalse(refrigeratorPage.isAddNewButtonEnabled());
    refrigeratorPage.clickShowForRefrigerator1();
    refrigeratorPage.verifyAllFieldsDisabled();
    refrigeratorPage.navigateToEpiInventory();

    epiInventoryPage.verifyIndicator("GREEN");
    epiInventoryPage.verifyAllFieldsDisabled();
  }

  @Test(groups = {"distribution"})
  public void testVisitInformationPageWhenFacilityNotVisitedAndFormsPartiallyFilled() throws SQLException {
    dbWrapper.addRefrigeratorToFacility("LG", "GR890", "GNRE0989", visitInformationData.get(FIRST_FACILITY_CODE));
    loginPage.loginAs(visitInformationData.get(USER), visitInformationData.get(PASSWORD));
    initiateDistribution(visitInformationData.get(FIRST_DELIVERY_ZONE_NAME), visitInformationData.get(VACCINES_PROGRAM));
    VisitInformationPage visitInformationPage = facilityListPage.selectFacility(visitInformationData.get(FIRST_FACILITY_CODE));
    assertEquals("Was " + dbWrapper.getAttributeFromTable("facilities", "name", "code", visitInformationData.get(FIRST_FACILITY_CODE)) +
      " visited in " + "Period14" + "?", visitInformationPage.getWasFacilityVisitedLabel());
    RefrigeratorPage refrigeratorPage = visitInformationPage.navigateToRefrigerators();
    refrigeratorPage.clickDelete();
    refrigeratorPage.clickOKButton();

    refrigeratorPage.clickAddNew();
    refrigeratorPage.addNewRefrigerator("SAM", "800L", "GNR7876");

    refrigeratorPage.clickShowForRefrigerator1();
    refrigeratorPage.enterValueInRefrigeratorTemperature("3");
    refrigeratorPage.clickFunctioningCorrectlyYesRadio();
    refrigeratorPage.enterValueInLowAlarmEvents("2");
    refrigeratorPage.enterValueInHighAlarmEvents("5");
    refrigeratorPage.clickDone();
    refrigeratorPage.verifyRefrigeratorColor("overall", "AMBER");
    refrigeratorPage.verifyRefrigeratorColor("individual", "AMBER");

    EpiInventoryPage epiInventoryPage = refrigeratorPage.navigateToEpiInventory();
    epiInventoryPage.fillDeliveredQuantity(1, "2");
    epiInventoryPage.fillDeliveredQuantity(2, "4");
    epiInventoryPage.verifyIndicator("AMBER");

    epiInventoryPage.navigateToVisitInformation();

    visitInformationPage.verifyIndicator("RED");
    visitInformationPage.selectFacilityVisitedNo();
    visitInformationPage.verifyIndicator("AMBER");
    visitInformationPage.selectReasonBadWeather();
    visitInformationPage.verifyIndicator("GREEN");
    visitInformationPage.selectReasonOther();
    visitInformationPage.verifyIndicator("AMBER");
    visitInformationPage.enterOtherReasonInTextBox("Reason for not visiting the facility");
    visitInformationPage.verifyIndicator("GREEN");
    visitInformationPage.navigateToRefrigerators();

    refrigeratorPage.verifyIndicator("GREEN");
    assertFalse(refrigeratorPage.isAddNewButtonEnabled());
    refrigeratorPage.clickShowForRefrigerator1();
    refrigeratorPage.verifyAllFieldsDisabled();
    refrigeratorPage.navigateToEpiInventory();

    epiInventoryPage.verifyIndicator("GREEN");
    epiInventoryPage.verifyAllFieldsDisabled();
  }

  @Test(groups = {"distribution"})
  public void testVisitInformationPageAndFacilityIndicatorWhenFacilityNotVisited() throws SQLException {
    dbWrapper.addRefrigeratorToFacility("LG", "yu", "Hry3", visitInformationData.get(FIRST_FACILITY_CODE));
    loginPage.loginAs(visitInformationData.get(USER), visitInformationData.get(PASSWORD));
    initiateDistribution(visitInformationData.get(FIRST_DELIVERY_ZONE_NAME), visitInformationData.get(VACCINES_PROGRAM));
    VisitInformationPage visitInformationPage = facilityListPage.selectFacility(visitInformationData.get(FIRST_FACILITY_CODE));
    facilityListPage.verifyFacilityIndicatorColor("Overall", "RED");
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
    facilityListPage.verifyFacilityIndicatorColor("Overall", "AMBER");
    visitInformationPage.enterOtherReasonInTextBox("Reason for not visiting the facility");
    visitInformationPage.verifyIndicator("GREEN");
    facilityListPage.verifyFacilityIndicatorColor("Overall", "GREEN");
  }

  private void verifyLabels() {
    VisitInformationPage visitInformationPage = PageFactory.getInstanceOfVisitInformation(testWebDriver);
    assertEquals("Visit Info / Observations", visitInformationPage.getFacilityVisitTabLabel());
    assertEquals("Visit Info / Observations", visitInformationPage.getVisitInformationPageLabel());
  }

  public void setupDataForDistributionTest(String userSIC, String deliveryZoneCodeFirst, String deliveryZoneCodeSecond,
                                           String deliveryZoneNameFirst, String deliveryZoneNameSecond,
                                           String facilityCodeFirst, String facilityCodeSecond,
                                           String programFirst, String programSecond, String schedule, String productGroupCode) throws SQLException {
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

  public void initiateDistribution(String deliveryZoneNameFirst, String programFirst) {
    HomePage homePage = PageFactory.getInstanceOfHomePage(testWebDriver);
    DistributionPage distributionPage = homePage.navigateToDistributionWhenOnline();
    distributionPage.selectValueFromDeliveryZone(deliveryZoneNameFirst);
    distributionPage.selectValueFromProgram(programFirst);
    distributionPage.clickInitiateDistribution();
    distributionPage.clickRecordData(1);
  }

  private VisitInformationPage fillFacilityData(Boolean wasFacilityVisity) {
    VisitInformationPage visitInformationPage = PageFactory.getInstanceOfVisitInformation(testWebDriver);

    if (wasFacilityVisity) {
      RefrigeratorPage refrigeratorPage = visitInformationPage.navigateToRefrigerators();
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

      EpiInventoryPage epiInventoryPage = refrigeratorPage.navigateToEpiInventory();
      epiInventoryPage.applyNRToAll();
      epiInventoryPage.fillDeliveredQuantity(1, "2");
      epiInventoryPage.fillDeliveredQuantity(2, "4");
      epiInventoryPage.fillDeliveredQuantity(3, "6");
    }

    EPIUsePage epiUsePage = visitInformationPage.navigateToEpiUse();
    epiUsePage.enterData(10, 20, 30, 40, 50, "10/2011", 1);

    FullCoveragePage fullCoveragePage = epiUsePage.navigateToFullCoverage();
    fullCoveragePage.enterData(23, 66, 77, "45");

    fullCoveragePage.navigateToVisitInformation();

    return visitInformationPage;
  }

  @When("^I verify radio button \"([^\"]*)\" is selected$")
  public void verifyRadioButtonSelected(String radioButtonSelected) {
    VisitInformationPage visitInformationPage = PageFactory.getInstanceOfVisitInformation(testWebDriver);
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
    VisitInformationPage visitInformationPage = PageFactory.getInstanceOfVisitInformation(testWebDriver);
    String actualDate = visitInformationPage.getVisitDate();
    String expectedDate = "01/" + new SimpleDateFormat("MM/yyyy").format(new Date());
    assertEquals(expectedDate, actualDate);
  }

  @And("^I select visit date as current date$")
  public void enterVisitDateAsFirstOfCurrentMonth() {
    VisitInformationPage visitInformationPage = PageFactory.getInstanceOfVisitInformation(testWebDriver);
    visitInformationPage.enterVisitDateAsFirstOfCurrentMonth();
  }

  @Then("^I enter vehicle id as \"([^\"]*)\"$")
  public void enterVehicleId(String vehicleId) {
    VisitInformationPage visitInformationPage = PageFactory.getInstanceOfVisitInformation(testWebDriver);
    visitInformationPage.enterVehicleId(vehicleId);

  }

  @When("^I select \"([^\"]*)\" facility visited$")
  public void selectFacilityVisitedOption(String option) {
    VisitInformationPage visitInformationPage = PageFactory.getInstanceOfVisitInformation(testWebDriver);
    if (option.toLowerCase().equals("yes")) {
      visitInformationPage.selectFacilityVisitedYes();
    } else if (option.toLowerCase().equals("no")) {
      visitInformationPage.selectFacilityVisitedNo();
    }
  }
}
