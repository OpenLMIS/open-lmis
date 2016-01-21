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


import cucumber.api.DataTable;
import cucumber.api.java.en.And;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import org.openlmis.UiUtils.CaptureScreenshotOnFailureListener;
import org.openlmis.UiUtils.TestCaseHelper;
import org.openlmis.UiUtils.TestWebDriver;
import org.openlmis.pageobjects.*;
import org.openqa.selenium.JavascriptExecutor;
import org.testng.annotations.*;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static com.thoughtworks.selenium.SeleneseTestBase.assertEquals;
import static java.util.Arrays.asList;
import static org.testng.AssertJUnit.assertFalse;

@Listeners(CaptureScreenshotOnFailureListener.class)

public class ViewWarehouseLoadAmount extends TestCaseHelper {

  public static final String periodDisplayedByDefault = "Period14";
  public static final String district1 = "District1";
  public static final String district2 = "District2";
  public static final String parentGeoZone = "Dodoma";
  public static final String parentGeoZone1 = "Arusha";
  public String userSIC = "fieldcoordinator";
  public String deliveryZoneCodeFirst = "DZ1";
  public String deliveryZoneCodeSecond = "DZ2";
  public String deliveryZoneNameFirst = "Delivery Zone First";
  public String deliveryZoneNameSecond = "Delivery Zone Second";
  public String facilityCodeFirst = "F10";
  public String facilityCodeSecond = "F11";
  public String programFirst = "VACCINES";
  public String programSecond = "TB";
  public String schedule = "M";
  public String product = "P10";
  WarehouseLoadAmountPage warehouseLoadAmountPage;
  LoginPage loginPage;

  @BeforeMethod(groups = "distribution")
  public void setUp() throws InterruptedException, SQLException, IOException {
    super.setup();
    loginPage = PageObjectFactory.getLoginPage(testWebDriver, baseUrlGlobal);
  }

  @Given("^I have data available for distribution load amount$")
  public void setupDataForDistributionLoadAmount() throws SQLException {
    String productGroupCode = "PG1";
    List<String> rightsList = new ArrayList<>();
    rightsList.add("MANAGE_DISTRIBUTION");
    setupTestDataToInitiateRnRAndDistribution(facilityCodeFirst, facilityCodeSecond, true,
      programFirst, userSIC, rightsList, programSecond, district1, district1, parentGeoZone);
    dbWrapper.insertProductGroup(productGroupCode);
    dbWrapper.insertProductWithGroup("Product5", "ProductName5", productGroupCode, true);
    dbWrapper.insertProductWithGroup("Product6", "ProductName6", productGroupCode, true);
    dbWrapper.insertProgramProduct("Product5", programFirst, "10", "false");
    dbWrapper.insertProgramProduct("Product6", programFirst, "10", "true");
    dbWrapper.updateFieldValue("products", "active", "false", "code", "Product6");
  }

  @And("^I have data available for \"([^\"]*)\" (facility|facilities) attached to delivery zones$")
  public void setupDataForMultipleDeliveryZones(String facilityInstances, String facility) throws SQLException {
    if (facilityInstances.equalsIgnoreCase("Multiple")) {
      setupDataForDeliveryZone(true, deliveryZoneCodeFirst, deliveryZoneCodeSecond, deliveryZoneNameFirst, deliveryZoneNameSecond,
        facilityCodeFirst, facilityCodeSecond, programFirst, programSecond, schedule);
    } else if (facilityInstances.equalsIgnoreCase("Single")) {
      setupDataForDeliveryZone(false, deliveryZoneCodeFirst, deliveryZoneCodeSecond, deliveryZoneNameFirst, deliveryZoneNameSecond,
        facilityCodeFirst, facilityCodeSecond, programFirst, programSecond, schedule);
    }
  }

  @And("^I update population of facility \"([^\"]*)\" as \"([^\"]*)\"$")
  public void updatePopulationOfFacility(String facilityCode, String population) throws SQLException {
    dbWrapper.updatePopulationOfFacility(facilityCode, population);
  }

  @And("^I have role assigned to delivery zones$")
  public void setupRoleAssignmentForMultipleDeliveryZones() throws SQLException {
    dbWrapper.insertRoleAssignmentForDistribution(userSIC, "store in-charge", deliveryZoneCodeFirst);
    dbWrapper.insertRoleAssignmentForDistribution(userSIC, "store in-charge", deliveryZoneCodeSecond);
  }

  @And("^I have role assigned to delivery zone first$")
  public void setupRoleAssignmentForDeliveryZoneFirst() throws SQLException {
    dbWrapper.insertRoleAssignmentForDistribution(userSIC, "store in-charge", deliveryZoneCodeFirst);
  }

  @And("^I have following ISA values:$")
  public void setProgramProductISA(DataTable tableData) throws SQLException {
    for (Map<String, String> map : tableData.asMaps(String.class, String.class)) {
      dbWrapper.insertProgramProductISA(map.get("Program"), map.get("Product"), map.get("whoRatio"),
        map.get("dosesPerYear"), map.get("wastageFactor"), map.get("bufferPercentage"), map.get("minimumValue"),
        map.get("maximumValue"), map.get("adjustmentValue"));
    }
  }

  @And("^I have following override ISA values:$")
  public void setOverrideISA(DataTable tableData) throws SQLException {
    for (Map<String, String> map : tableData.asMaps(String.class, String.class)) {
      dbWrapper.InsertOverriddenIsa(map.get("Facility Code"), map.get("Program"),
        map.get("Product"), Integer.parseInt(map.get("ISA")));
    }
  }

  @Then("^I should see aggregate ISA values as per multiple facilities in one delivery zone$")
  public void verifyISAAndOverrideISAValuesAggregatedForMultipleFacilities() {
    warehouseLoadAmountPage = PageObjectFactory.getWarehouseLoadAmountPage(testWebDriver);
    assertEquals(String.valueOf(Integer.parseInt(warehouseLoadAmountPage.getFacilityPopulation(1, 1)) +
      Integer.parseInt(warehouseLoadAmountPage.getFacilityPopulation(1, 2))), warehouseLoadAmountPage.getTotalPopulation(1));
    assertEquals(String.valueOf(Integer.parseInt(warehouseLoadAmountPage.getProductIsa(1, 1, 1)) +
      Integer.parseInt(warehouseLoadAmountPage.getProductIsa(1, 2, 1))), warehouseLoadAmountPage.getTotalProductIsa(1, 1));
    assertEquals(String.valueOf(Integer.parseInt(warehouseLoadAmountPage.getProductIsa(1, 1, 2)) +
      Integer.parseInt(warehouseLoadAmountPage.getProductIsa(1, 2, 2))), warehouseLoadAmountPage.getTotalProductIsa(1, 2));

    assertEquals(warehouseLoadAmountPage.getAggregateTablePopulation(1), warehouseLoadAmountPage.getTotalPopulation(1));
    assertEquals(warehouseLoadAmountPage.getAggregateTableProductIsa(1, 1), warehouseLoadAmountPage.getTotalProductIsa(1, 1));
    assertEquals(warehouseLoadAmountPage.getAggregateTableProductIsa(1, 2), warehouseLoadAmountPage.getTotalProductIsa(1, 2));

    assertEquals(warehouseLoadAmountPage.getAggregateTableProductIsa(1, 1), warehouseLoadAmountPage.getAggregateTableTotalProductIsa(1));
    assertEquals(warehouseLoadAmountPage.getAggregateTableProductIsa(1, 2), warehouseLoadAmountPage.getAggregateTableTotalProductIsa(2));
    assertEquals(warehouseLoadAmountPage.getAggregateTablePopulation(1), warehouseLoadAmountPage.getAggregateTableTotalPopulation());
  }

  @Then("^I should see ISA values as per delivery zone facilities$")
  public void verifyISAAndOverrideISA() {
    warehouseLoadAmountPage = PageObjectFactory.getWarehouseLoadAmountPage(testWebDriver);
    assertEquals(facilityCodeSecond, warehouseLoadAmountPage.getFacilityCode(1, 1));
    assertEquals("Central Hospital", warehouseLoadAmountPage.getFacilityName(1, 1));
    assertEquals("333", warehouseLoadAmountPage.getFacilityPopulation(1, 1));

    assertEquals("31", warehouseLoadAmountPage.getProductIsa(1, 1, 1));
    assertEquals("101", warehouseLoadAmountPage.getProductIsa(1, 1, 2));
  }

  @And("^I verify ISA values for Product1 as:$")
  public void verifyISAForProduct1(DataTable dataTable) {
    warehouseLoadAmountPage = PageObjectFactory.getWarehouseLoadAmountPage(testWebDriver);
    List<Map<String, String>> facilityProductISAMaps = dataTable.asMaps(String.class, String.class);
    for (Map<String, String> facilityProductISAMap : facilityProductISAMaps) {
      assertEquals(facilityProductISAMap.get("Facility1"), warehouseLoadAmountPage.getProductIsa(1, 1, 1));
      assertEquals(facilityProductISAMap.get("Facility2"), warehouseLoadAmountPage.getProductIsa(1, 2, 1));
    }
  }

  @And("^I verify ISA values for Product2 as:$")
  public void verifyISAForProduct2(DataTable dataTable) {
    warehouseLoadAmountPage = PageObjectFactory.getWarehouseLoadAmountPage(testWebDriver);
    List<Map<String, String>> facilityProductISAMaps = dataTable.asMaps(String.class, String.class);
    for (Map<String, String> facilityProductISAMap : facilityProductISAMaps) {
      assertEquals(facilityProductISAMap.get("Facility1"), warehouseLoadAmountPage.getProductIsa(1, 1, 2));
      assertEquals(facilityProductISAMap.get("Facility2"), warehouseLoadAmountPage.getProductIsa(1, 2, 2));
    }
  }

  @And("^I should not see inactive products on view load amount$")
  public void verifyInactiveProductsNotDisplayedOnViewLoadAmount() {
    warehouseLoadAmountPage = PageObjectFactory.getWarehouseLoadAmountPage(testWebDriver);
    assertFalse(warehouseLoadAmountPage.getAggregateTableData().contains("ProductName6"));
    assertFalse(warehouseLoadAmountPage.getTable1Data().contains("ProductName6"));

    assertFalse(warehouseLoadAmountPage.getAggregateTableData().contains("ProductName5"));
    assertFalse(warehouseLoadAmountPage.getTable1Data().contains("ProductName5"));

    assertFalse(warehouseLoadAmountPage.getAggregateTableData().contains("PG1-Name"));
    assertFalse(warehouseLoadAmountPage.getTable1Data().contains("PG1-Name"));
  }

  @Then("^I should see message \"([^\"]*)\"$")
  public void verifyNoRecordFoundMessage(String message) {
    warehouseLoadAmountPage = PageObjectFactory.getWarehouseLoadAmountPage(testWebDriver);
    assertEquals(message, warehouseLoadAmountPage.getNoRecordFoundMessage());
  }

  @Test(groups = {"distribution"}, dataProvider = "Data-Provider-Function-Multiple-Facilities")
  public void testShouldVerifyISAForDeliveryZoneNegativeScenarios(String userSIC, String password, String deliveryZoneCodeFirst,
                                                                  String deliveryZoneCodeSecond, String deliveryZoneNameFirst,
                                                                  String deliveryZoneNameSecond, String facilityCodeFirst,
                                                                  String facilityCodeSecond, String facilityCodeThird,
                                                                  String facilityCodeFourth, String programFirst, String programSecond,
                                                                  String schedule, String product1, String product2) throws SQLException {
    List<String> rightsList = asList("MANAGE_DISTRIBUTION");
    setupTestDataToInitiateRnRAndDistribution(facilityCodeFirst, facilityCodeSecond, true, programFirst, userSIC,
      rightsList, programSecond, district1, district1, parentGeoZone1);

    setupDataForDeliveryZone(false, deliveryZoneCodeFirst, deliveryZoneCodeSecond, deliveryZoneNameFirst, deliveryZoneNameSecond,
      facilityCodeFirst, facilityCodeSecond, programFirst, programSecond, schedule);

    addOnDataSetupForDeliveryZoneForMultipleFacilitiesAttachedWithSingleDeliveryZone(deliveryZoneCodeFirst, facilityCodeThird,
      facilityCodeFourth, district2, district2, parentGeoZone);

    dbWrapper.deleteAllProducts();
    dbWrapper.insertProductGroup("PG1");
    dbWrapper.insertProductGroup("PG2");
    dbWrapper.insertProductWithGroup(product1, "antibiotic", "PG1", true);
    dbWrapper.insertProductWithGroup(product2, "antibiotic", "PG2", true);
    dbWrapper.insertProgramProducts(product1, product2, programFirst);
    dbWrapper.insertFacilityApprovedProduct(product1, programFirst, "lvl3_hospital");
    dbWrapper.insertFacilityApprovedProduct(product2, programFirst, "lvl3_hospital");
    dbWrapper.updateFieldValue("products", "packSize", "4", "code", product1);
    dbWrapper.updateFieldValue("products", "packSize", "5", "code", product2);
    dbWrapper.updateProcessingPeriodByField("numberOfMonths", "2", periodDisplayedByDefault, schedule);

    dbWrapper.insertRoleAssignmentForDistribution(userSIC, "store in-charge", deliveryZoneCodeFirst);
    dbWrapper.insertRoleAssignmentForDistribution(userSIC, "store in-charge", deliveryZoneCodeSecond);
    dbWrapper.InsertOverriddenIsa(facilityCodeFirst, programFirst, product1, 1000);
    dbWrapper.InsertOverriddenIsa(facilityCodeFirst, programFirst, product2, 9999999);
    dbWrapper.InsertOverriddenIsa(facilityCodeSecond, programFirst, product1, 3000);
    dbWrapper.InsertOverriddenIsa(facilityCodeSecond, programFirst, product2, 888888);
    dbWrapper.InsertOverriddenIsa(facilityCodeThird, programFirst, product1, 51);
    dbWrapper.InsertOverriddenIsa(facilityCodeThird, programFirst, product2, 51);
    dbWrapper.InsertOverriddenIsa(facilityCodeFourth, programFirst, product2, 57);
    dbWrapper.updatePopulationOfFacility(facilityCodeFirst, null);
    dbWrapper.updateOverriddenIsa(facilityCodeFirst, programFirst, product1, null);
    dbWrapper.updateOverriddenIsa(facilityCodeSecond, programFirst, product1, null);

    HomePage homePage = loginPage.loginAs(userSIC, password);
    DistributionPage distributionPage = homePage.navigateToDistributionWhenOnline();

    distributionPage.selectValueFromDeliveryZone(deliveryZoneNameFirst);
    distributionPage.selectValueFromProgram(programFirst);
    distributionPage.selectValueFromPeriod(periodDisplayedByDefault);
    distributionPage.clickViewLoadAmount();

    warehouseLoadAmountPage = PageObjectFactory.getWarehouseLoadAmountPage(testWebDriver);

    assertEquals(warehouseLoadAmountPage.getFacilityPopulation(1, 1), warehouseLoadAmountPage.getTotalPopulation(1));
    assertEquals("--", warehouseLoadAmountPage.getTotalProductIsa(1, 1));
    assertEquals("--", warehouseLoadAmountPage.getProductIsa(1, 1, 1));
    assertEquals("--", warehouseLoadAmountPage.getProductIsa(1, 2, 1));
    assertEquals("4355556", warehouseLoadAmountPage.getTotalProductIsa(1, 2));
    assertEquals("4000000", warehouseLoadAmountPage.getProductIsa(1, 2, 2));
    assertEquals("355556", warehouseLoadAmountPage.getProductIsa(1, 1, 2));
    assertEquals("--", warehouseLoadAmountPage.getFacilityPopulation(1, 2));
    assertEquals("333", warehouseLoadAmountPage.getFacilityPopulation(1, 1));
    assertEquals("333", warehouseLoadAmountPage.getTotalPopulation(1));
    assertEquals(String.valueOf(Integer.parseInt(warehouseLoadAmountPage.getProductIsa(1, 1, 2)) +
      Integer.parseInt(warehouseLoadAmountPage.getProductIsa(1, 2, 2))), warehouseLoadAmountPage.getTotalProductIsa(1, 2));

    assertEquals(Integer.parseInt(warehouseLoadAmountPage.getFacilityPopulation(2, 1)) +
      Integer.parseInt(warehouseLoadAmountPage.getFacilityPopulation(2, 2)), warehouseLoadAmountPage.getTotalPopulation(2));
    assertEquals("--", warehouseLoadAmountPage.getProductIsa(2, 1, 1));
    assertEquals("23", warehouseLoadAmountPage.getProductIsa(2, 1, 2));
    assertEquals("21", warehouseLoadAmountPage.getProductIsa(2, 2, 2));
    assertEquals("26", warehouseLoadAmountPage.getProductIsa(2, 2, 1));
    assertEquals(warehouseLoadAmountPage.getProductIsa(2, 2, 1), warehouseLoadAmountPage.getTotalProductIsa(2, 1));
    assertEquals(String.valueOf(Integer.parseInt(warehouseLoadAmountPage.getProductIsa(2, 1, 2)) +
      Integer.parseInt(warehouseLoadAmountPage.getProductIsa(2, 2, 2))), warehouseLoadAmountPage.getTotalProductIsa(2, 2));

    assertEquals(warehouseLoadAmountPage.getAggregateTableTotalPopulation(),
      String.valueOf(Integer.parseInt(warehouseLoadAmountPage.getFacilityPopulation(1, 1)) +
        Integer.parseInt(warehouseLoadAmountPage.getFacilityPopulation(2, 1)) +
        Integer.parseInt(warehouseLoadAmountPage.getFacilityPopulation(2, 2)))
    );
    assertEquals(warehouseLoadAmountPage.getAggregateTableTotalProductIsa(2),
      String.valueOf(Integer.parseInt(warehouseLoadAmountPage.getProductIsa(1, 1, 2)) +
        Integer.parseInt(warehouseLoadAmountPage.getProductIsa(1, 2, 2)) + Integer.parseInt(warehouseLoadAmountPage.getProductIsa(2, 1, 2)) +
        Integer.parseInt(warehouseLoadAmountPage.getProductIsa(2, 2, 2)))
    );
    assertEquals(warehouseLoadAmountPage.getAggregateTableTotalProductIsa(1), warehouseLoadAmountPage.getProductIsa(2, 2, 1));
    assertEquals("--", warehouseLoadAmountPage.getTotalProductIsa(1, 1));

    assertEquals(warehouseLoadAmountPage.getAggregateTableProductIsa(1, 1), warehouseLoadAmountPage.getTotalProductIsa(1, 1));
    assertEquals(warehouseLoadAmountPage.getAggregateTableProductIsa(1, 2), warehouseLoadAmountPage.getTotalProductIsa(1, 2));
    assertEquals(warehouseLoadAmountPage.getAggregateTableProductIsa(2, 1), warehouseLoadAmountPage.getTotalProductIsa(2, 1));
    assertEquals(warehouseLoadAmountPage.getAggregateTableProductIsa(2, 2), warehouseLoadAmountPage.getTotalProductIsa(2, 2));

    assertEquals(warehouseLoadAmountPage.getAggregateTablePopulation(1), warehouseLoadAmountPage.getTotalPopulation(1));
    assertEquals(warehouseLoadAmountPage.getAggregateTablePopulation(2), warehouseLoadAmountPage.getTotalPopulation(2));

    verifyCaptionsAndLabels(deliveryZoneNameFirst);
  }

  @Test(groups = {"distribution"}, dataProvider = "Data-Provider-Function-Multiple-GeoZones")
  public void testShouldVerifyISAForGeographicZones(String userSIC, String password, String deliveryZoneCodeFirst, String deliveryZoneCodeSecond,
                                                    String deliveryZoneNameFirst, String deliveryZoneNameSecond, String facilityCodeFirst,
                                                    String facilityCodeSecond, String programFirst, String programSecond, String schedule,
                                                    String product, String product2) throws SQLException {
    List<String> rightsList = asList("MANAGE_DISTRIBUTION");
    setupTestDataToInitiateRnRAndDistribution(facilityCodeFirst, facilityCodeSecond, true, programFirst, userSIC,
      rightsList, programSecond, district1, parentGeoZone, parentGeoZone);
    setupDataForDeliveryZone(false, deliveryZoneCodeFirst, deliveryZoneCodeSecond, deliveryZoneNameFirst, deliveryZoneNameSecond,
      facilityCodeFirst, facilityCodeSecond, programFirst, programSecond, schedule);

    dbWrapper.insertRoleAssignmentForDistribution(userSIC, "store in-charge", deliveryZoneCodeFirst);
    dbWrapper.insertRoleAssignmentForDistribution(userSIC, "store in-charge", deliveryZoneCodeSecond);
    dbWrapper.InsertOverriddenIsa(facilityCodeFirst, programFirst, product, 1000);
    dbWrapper.InsertOverriddenIsa(facilityCodeFirst, programFirst, product2, 2000);
    dbWrapper.InsertOverriddenIsa(facilityCodeSecond, programFirst, product, 3000);
    dbWrapper.InsertOverriddenIsa(facilityCodeSecond, programFirst, product2, 0);

    HomePage homePage = loginPage.loginAs(userSIC, password);
    DistributionPage distributionPage = homePage.navigateToDistributionWhenOnline();

    distributionPage.selectValueFromDeliveryZone(deliveryZoneNameFirst);
    distributionPage.selectValueFromProgram(programFirst);
    distributionPage.selectValueFromPeriod(periodDisplayedByDefault);
    distributionPage.clickViewLoadAmount();

    warehouseLoadAmountPage = PageObjectFactory.getWarehouseLoadAmountPage(testWebDriver);

    verifyWarehouseLoadAmountHeader(deliveryZoneNameFirst, programFirst, periodDisplayedByDefault);
    assertEquals(facilityCodeSecond, warehouseLoadAmountPage.getFacilityCode(1, 1));
    assertEquals("Central Hospital", warehouseLoadAmountPage.getFacilityName(1, 1));
    assertEquals("333", warehouseLoadAmountPage.getFacilityPopulation(1, 1));
    assertEquals("300", warehouseLoadAmountPage.getProductIsa(1, 1, 1));
    assertEquals("0", warehouseLoadAmountPage.getProductIsa(1, 1, 2));

    assertEquals(facilityCodeFirst, warehouseLoadAmountPage.getFacilityCode(2, 1));
    assertEquals("Village Dispensary", warehouseLoadAmountPage.getFacilityName(2, 1));
    assertEquals("333", warehouseLoadAmountPage.getFacilityPopulation(2, 1));
    assertEquals("100", warehouseLoadAmountPage.getProductIsa(2, 1, 1));
    assertEquals("200", warehouseLoadAmountPage.getProductIsa(2, 1, 2));
    dbWrapper.updatePopulationOfFacility(facilityCodeFirst, null);
    dbWrapper.updateOverriddenIsa(facilityCodeFirst, programFirst, product, null);
    homePage.navigateToDistributionWhenOnline();
    distributionPage.selectValueFromDeliveryZone(deliveryZoneNameFirst);
    distributionPage.selectValueFromProgram(programFirst);
    distributionPage.selectValueFromPeriod(periodDisplayedByDefault);
    distributionPage.clickViewLoadAmount();
    assertEquals("--", warehouseLoadAmountPage.getFacilityPopulation(2, 1));
    assertEquals("--", warehouseLoadAmountPage.getProductIsa(2, 1, 1));

  }

  private void verifyWarehouseLoadAmountHeader(String deliverZone, String program, String period) {
    assertEquals("Warehouse load amount", warehouseLoadAmountPage.getPageHeader());
    assertEquals("Delivery Zone", warehouseLoadAmountPage.getDeliveryZoneLabelInHeader());
    assertEquals(deliverZone, warehouseLoadAmountPage.getDeliveryZoneNameInHeader());
    assertEquals("Program", warehouseLoadAmountPage.getProgramLabelInHeader());
    assertEquals(program, warehouseLoadAmountPage.getProgramNameInHeader());
    assertEquals("Period", warehouseLoadAmountPage.getPeriodLabelInHeader());
    assertEquals(period, warehouseLoadAmountPage.getPeriodNameInHeader());
  }

  private void verifyCaptionsAndLabels(String deliveryZoneNameFirst) {
    assertEquals("District1", warehouseLoadAmountPage.getGeoZoneTitleForTable(1));
    assertEquals("District2", warehouseLoadAmountPage.getGeoZoneTitleForTable(2));
    assertEquals(deliveryZoneNameFirst + " Total", warehouseLoadAmountPage.getDeliveryZoneName());
    assertEquals("Zone Total", warehouseLoadAmountPage.getAggregateTableTotalCaption());
    assertEquals(district1, warehouseLoadAmountPage.getAggregateTableGeoZoneTotalCaption(1));
    assertEquals(district2, warehouseLoadAmountPage.getAggregateTableGeoZoneTotalCaption(2));
    assertEquals(district1, warehouseLoadAmountPage.getGeoZonesFromAggregatedTable(1));
    assertEquals(district2, warehouseLoadAmountPage.getGeoZonesFromAggregatedTable(2));
    assertEquals("District", warehouseLoadAmountPage.getGeoZoneLevelHeaderForAggregatedTable());

    assertEquals("Population", warehouseLoadAmountPage.getPopulationHeaderForAggregatedTable());
    assertEquals("PG1-Name", warehouseLoadAmountPage.getProductGroupHeaderForAggregatedTable(1));
    assertEquals("PG2-Name", warehouseLoadAmountPage.getProductGroupHeaderForAggregatedTable(2));
    assertEquals("antibiotic", warehouseLoadAmountPage.getProductNameHeaderForAggregatedTable(1));
    assertEquals("antibiotic", warehouseLoadAmountPage.getProductNameHeaderForAggregatedTable(2));

    assertEquals("Facility", warehouseLoadAmountPage.getFacilityHeaderForTable(1));
    assertEquals("Population", warehouseLoadAmountPage.getPopulationHeaderForTable(1));
    assertEquals("PG1-Name", warehouseLoadAmountPage.getProductGroupHeaderForTable(1, 1));
    assertEquals("PG2-Name", warehouseLoadAmountPage.getProductGroupHeaderForTable(1, 2));
    assertEquals("antibiotic", warehouseLoadAmountPage.getProductNameHeaderForTable(1, 1));
    assertEquals("antibiotic", warehouseLoadAmountPage.getProductNameHeaderForTable(1, 2));
    assertEquals("District1 Total", warehouseLoadAmountPage.getTableTotalCaption(1));

    assertEquals("Facility", warehouseLoadAmountPage.getFacilityHeaderForTable(2));
    assertEquals("Population", warehouseLoadAmountPage.getPopulationHeaderForTable(2));
    assertEquals("PG1-Name", warehouseLoadAmountPage.getProductGroupHeaderForTable(2, 1));
    assertEquals("PG2-Name", warehouseLoadAmountPage.getProductGroupHeaderForTable(2, 2));
    assertEquals("antibiotic", warehouseLoadAmountPage.getProductNameHeaderForTable(2, 1));
    assertEquals("antibiotic", warehouseLoadAmountPage.getProductNameHeaderForTable(2, 2));
    assertEquals("District2 Total", warehouseLoadAmountPage.getTableTotalCaption(2));
  }

  @AfterMethod(groups = "distribution")
  public void tearDown() throws SQLException {
    testWebDriver.sleep(500);
    if (!testWebDriver.getElementById("username").isDisplayed()) {
      HomePage homePage = PageObjectFactory.getHomePage(testWebDriver);
      homePage.logout(baseUrlGlobal);
      dbWrapper.deleteData();
      dbWrapper.closeConnection();
      ((JavascriptExecutor) TestWebDriver.getDriver()).executeScript("indexedDB.deleteDatabase('open_lmis');");
    }
  }

  @DataProvider(name = "Data-Provider-Function-Multiple-Facilities")
  public Object[][] parameterIntTestProvider() {
    return new Object[][]{
      {"fieldCoordinator", "Admin123", "DZ1", "DZ2", "Delivery Zone First", "Delivery Zone Second",
        "F10", "F11", "F12", "F13", "VACCINES", "TB", "M", "P10", "P11"}
    };
  }

  @DataProvider(name = "Data-Provider-Function-Multiple-GeoZones")
  public Object[][] parameterIntTestProviderMultipleGeoZones() {
    return new Object[][]{
      {"fieldCoordinator", "Admin123", "DZ1", "DZ2", "Delivery Zone First", "Delivery Zone Second",
        "F10", "F11", "VACCINES", "TB", "M", "P10", "P11"}
    };
  }
}

