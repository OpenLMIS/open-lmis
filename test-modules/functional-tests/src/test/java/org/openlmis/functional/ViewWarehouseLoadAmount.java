/*
 * Copyright © 2013 VillageReach.  All Rights Reserved.  This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 *
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package org.openlmis.functional;


import com.thoughtworks.selenium.SeleneseTestNgHelper;
import org.openlmis.UiUtils.CaptureScreenshotOnFailureListener;
import org.openlmis.UiUtils.TestCaseHelper;
import org.openlmis.pageobjects.DistributionPage;
import org.openlmis.pageobjects.HomePage;
import org.openlmis.pageobjects.LoginPage;
import org.openlmis.pageobjects.WarehouseLoadAmountPage;
import org.openqa.selenium.WebElement;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;
import org.testng.annotations.*;

import java.util.ArrayList;
import java.util.List;

import static com.thoughtworks.selenium.SeleneseTestBase.assertEquals;


@TransactionConfiguration(defaultRollback = true)
@Transactional

@Listeners(CaptureScreenshotOnFailureListener.class)

public class ViewWarehouseLoadAmount extends TestCaseHelper {

  public static final String periodDisplayedByDefault = "Period14";
  public static final String district1 = "District1";
  public static final String district2 = "District2";
  public static final String parentGeoZone = "Dodoma";

  @BeforeMethod(groups = {"functional2", "smoke"})
  public void setUp() throws Exception {
    super.setup();
  }


  @Test(groups = {"smoke"}, dataProvider = "Data-Provider-Function")
  public void testShouldViewIsaOverrideIsaAndNoRecords(String userSIC, String password, String deliveryZoneCodeFirst, String deliveryZoneCodeSecond,
                                                       String deliveryZoneNameFirst, String deliveryZoneNameSecond,
                                                       String facilityCodeFirst, String facilityCodeSecond,
                                                       String programFirst, String programSecond, String schedule, String product, String product2) throws Exception {

    List<String> rightsList = new ArrayList<String>();
    rightsList.add("MANAGE_DISTRIBUTION");
    setupTestDataToInitiateRnRForDistribution(facilityCodeFirst, facilityCodeSecond, true, programFirst, userSIC, "200", "openLmis", rightsList, programSecond, district1, parentGeoZone, parentGeoZone);
    setupDataForDeliveryZone(deliveryZoneCodeFirst, deliveryZoneCodeSecond,
      deliveryZoneNameFirst, deliveryZoneNameSecond,
      facilityCodeFirst, facilityCodeSecond,
      programFirst, programSecond, schedule);

    dbWrapper.insertRoleAssignmentForDistribution(userSIC, "store in-charge", deliveryZoneCodeFirst);
    dbWrapper.insertRoleAssignmentForDistribution(userSIC, "store in-charge", deliveryZoneCodeSecond);
    dbWrapper.insertProgramProductISA(programFirst, product, "10", "10", "10", "10", null, null, "0");
    dbWrapper.InsertOverridenIsa(facilityCodeFirst, programFirst, product2, 1000);

    LoginPage loginPage = new LoginPage(testWebDriver, baseUrlGlobal);
    HomePage homePage = loginPage.loginAs(userSIC, password);
    DistributionPage distributionPage = homePage.navigatePlanDistribution();

    distributionPage.selectValueFromDeliveryZone(deliveryZoneNameFirst);
    distributionPage.selectValueFromProgram(programFirst);
    distributionPage.selectValueFromPeriod(periodDisplayedByDefault);
    distributionPage.clickViewLoadAmount();

    WarehouseLoadAmountPage warehouseLoadAmountPage = new WarehouseLoadAmountPage(testWebDriver);
    assertEquals(facilityCodeFirst, warehouseLoadAmountPage.getFacilityCode(1, 1));
    assertEquals(dbWrapper.getFacilityName(facilityCodeFirst), warehouseLoadAmountPage.getFacilityName(1, 1));
    assertEquals(dbWrapper.getFacilityPopulation(facilityCodeFirst), warehouseLoadAmountPage.getFacilityPopulation(1, 1));

    assertEquals(IsaProgramProduct(programFirst, product, warehouseLoadAmountPage.getFacilityPopulation(1, 1)), warehouseLoadAmountPage.getProduct1Isa(1, 1));
    assertEquals(dbWrapper.getOverridenIsa(facilityCodeFirst, programFirst, product2), warehouseLoadAmountPage.getProduct2Isa(1, 1));

    homePage.navigatePlanDistribution();
    distributionPage.selectValueFromDeliveryZone(deliveryZoneNameSecond);
    distributionPage.selectValueFromProgram(programSecond);
    distributionPage.selectValueFromPeriod(periodDisplayedByDefault);
    distributionPage.clickViewLoadAmount();
    assertEquals("No records found", warehouseLoadAmountPage.getNoRecordFoundMessage());
  }

  @Test(groups = {"smoke"}, dataProvider = "Data-Provider-Function")
  public void testShouldVerifyAggregateISAForDeliveryZone(String userSIC, String password, String deliveryZoneCodeFirst, String deliveryZoneCodeSecond,
                                                          String deliveryZoneNameFirst, String deliveryZoneNameSecond,
                                                          String facilityCodeFirst, String facilityCodeSecond,
                                                          String programFirst, String programSecond, String schedule, String product, String product2) throws Exception {

    List<String> rightsList = new ArrayList<String>();
    rightsList.add("MANAGE_DISTRIBUTION");
    setupTestDataToInitiateRnRForDistribution(facilityCodeFirst, facilityCodeSecond, true, programFirst, userSIC, "200", "openLmis", rightsList, programSecond, district1, district1, parentGeoZone);
    setupDataForDeliveryZoneForMultipleFacilitiesAttachedWithSingleDeliveryZone(deliveryZoneCodeFirst,
      deliveryZoneNameFirst,
      facilityCodeFirst, facilityCodeSecond,
      programFirst, programSecond, schedule);

    dbWrapper.insertRoleAssignmentForDistribution(userSIC, "store in-charge", deliveryZoneCodeFirst);
    dbWrapper.insertRoleAssignmentForDistribution(userSIC, "store in-charge", deliveryZoneCodeSecond);
    dbWrapper.InsertOverridenIsa(facilityCodeFirst, programFirst, product, 1000);
    dbWrapper.InsertOverridenIsa(facilityCodeFirst, programFirst, product2, 2000);
    dbWrapper.InsertOverridenIsa(facilityCodeSecond, programFirst, product, 3000);
    dbWrapper.InsertOverridenIsa(facilityCodeSecond, programFirst, product2, 4000);

    LoginPage loginPage = new LoginPage(testWebDriver, baseUrlGlobal);
    HomePage homePage = loginPage.loginAs(userSIC, password);
    DistributionPage distributionPage = homePage.navigatePlanDistribution();

    distributionPage.selectValueFromDeliveryZone(deliveryZoneNameFirst);
    distributionPage.selectValueFromProgram(programFirst);
    distributionPage.selectValueFromPeriod(periodDisplayedByDefault);
    distributionPage.clickViewLoadAmount();

    WarehouseLoadAmountPage warehouseLoadAmountPage = new WarehouseLoadAmountPage(testWebDriver);
    assertEquals(String.valueOf(Integer.parseInt(warehouseLoadAmountPage.getFacilityPopulation(1, 1)) + Integer.parseInt(warehouseLoadAmountPage.getFacilityPopulation(2, 1))), warehouseLoadAmountPage.getFacilityPopulation(3, 1));
    assertEquals(String.valueOf(Integer.parseInt(warehouseLoadAmountPage.getProduct1Isa(1, 1)) + Integer.parseInt(warehouseLoadAmountPage.getProduct1Isa(2, 1))), warehouseLoadAmountPage.getProduct1Isa(3, 1));
    assertEquals(String.valueOf(Integer.parseInt(warehouseLoadAmountPage.getProduct2Isa(1, 1)) + Integer.parseInt(warehouseLoadAmountPage.getProduct2Isa(2, 1))), warehouseLoadAmountPage.getProduct2Isa(3, 1));

    assertEquals(warehouseLoadAmountPage.getAggregatePopulation(1), String.valueOf(Integer.parseInt(warehouseLoadAmountPage.getFacilityPopulation(1, 1)) + Integer.parseInt(warehouseLoadAmountPage.getFacilityPopulation(2, 1))));
    assertEquals(warehouseLoadAmountPage.getAggregateProduct1Isa(1), String.valueOf(Integer.parseInt(warehouseLoadAmountPage.getProduct1Isa(1, 1)) + Integer.parseInt(warehouseLoadAmountPage.getProduct1Isa(2, 1))));
    assertEquals(warehouseLoadAmountPage.getAggregateProduct2Isa(1), String.valueOf(Integer.parseInt(warehouseLoadAmountPage.getProduct2Isa(1, 1)) + Integer.parseInt(warehouseLoadAmountPage.getProduct2Isa(2, 1))));

    assertEquals(warehouseLoadAmountPage.getAggregateProduct2Isa(1), warehouseLoadAmountPage.getAggregateProduct2Isa(2));
    assertEquals(warehouseLoadAmountPage.getAggregateProduct1Isa(1), warehouseLoadAmountPage.getAggregateProduct1Isa(2));
    assertEquals(warehouseLoadAmountPage.getAggregatePopulation(1), warehouseLoadAmountPage.getAggregatePopulation(2));

    assertEquals(String.valueOf(Integer.parseInt(warehouseLoadAmountPage.getFacilityPopulation(1, 1)) + Integer.parseInt(warehouseLoadAmountPage.getFacilityPopulation(2, 1))), warehouseLoadAmountPage.getAggregatePopulation(2));
    assertEquals(String.valueOf(Integer.parseInt(warehouseLoadAmountPage.getProduct1Isa(1, 1)) + Integer.parseInt(warehouseLoadAmountPage.getProduct1Isa(2, 1))), warehouseLoadAmountPage.getAggregateProduct1Isa(2));
    assertEquals(String.valueOf(Integer.parseInt(warehouseLoadAmountPage.getProduct2Isa(1, 1)) + Integer.parseInt(warehouseLoadAmountPage.getProduct2Isa(2, 1))), warehouseLoadAmountPage.getAggregateProduct2Isa(2));


  }

  @Test(groups = {"functional2"}, dataProvider = "Data-Provider-Function-Multiple-Facilities")
  public void testShouldVerifyAggregateISAForDeliveryZoneNegativeScenarios(String userSIC, String password, String deliveryZoneCodeFirst, String deliveryZoneCodeSecond,
                                                                           String deliveryZoneNameFirst, String deliveryZoneNameSecond,
                                                                           String facilityCodeFirst, String facilityCodeSecond, String facilityCodeThird, String facilityCodeFourth,
                                                                           String programFirst, String programSecond, String schedule, String product, String product2) throws Exception {

    List<String> rightsList = new ArrayList<String>();
    rightsList.add("MANAGE_DISTRIBUTION");
    setupTestDataToInitiateRnRForDistribution(facilityCodeFirst, facilityCodeSecond, true, programFirst, userSIC, "200", "openLmis", rightsList, programSecond, district1, district1, parentGeoZone);

    setupDataForDeliveryZoneForMultipleFacilitiesAttachedWithSingleDeliveryZone(deliveryZoneCodeFirst,
      deliveryZoneNameFirst,
      facilityCodeFirst, facilityCodeSecond,
      programFirst, programSecond, schedule);

    dbWrapper.insertGeographicZone(district2, district2, parentGeoZone);
    dbWrapper.insertFacilitiesWithDifferentGeoZones(facilityCodeThird, facilityCodeFourth, district2, district2);
    dbWrapper.insertDeliveryZoneMembers(deliveryZoneCodeFirst, facilityCodeThird);
    dbWrapper.insertDeliveryZoneMembers(deliveryZoneCodeFirst, facilityCodeFourth);


    dbWrapper.insertRoleAssignmentForDistribution(userSIC, "store in-charge", deliveryZoneCodeFirst);
    dbWrapper.insertRoleAssignmentForDistribution(userSIC, "store in-charge", deliveryZoneCodeSecond);
    dbWrapper.InsertOverridenIsa(facilityCodeFirst, programFirst, product, 1000);
    dbWrapper.InsertOverridenIsa(facilityCodeFirst, programFirst, product2, 9999999);
    dbWrapper.InsertOverridenIsa(facilityCodeSecond, programFirst, product, 3000);
    dbWrapper.InsertOverridenIsa(facilityCodeSecond, programFirst, product2, 888888);
    dbWrapper.InsertOverridenIsa(facilityCodeThird, programFirst, product, 111);
    dbWrapper.InsertOverridenIsa(facilityCodeThird, programFirst, product2, 222);
    dbWrapper.updatePopulationOfFacility(facilityCodeFirst, null);
    dbWrapper.updateOverridenIsa(facilityCodeFirst, programFirst, product, null);
    dbWrapper.updateOverridenIsa(facilityCodeSecond, programFirst, product, null);

    LoginPage loginPage = new LoginPage(testWebDriver, baseUrlGlobal);
    HomePage homePage = loginPage.loginAs(userSIC, password);
    DistributionPage distributionPage = homePage.navigatePlanDistribution();

    distributionPage.selectValueFromDeliveryZone(deliveryZoneNameFirst);
    distributionPage.selectValueFromProgram(programFirst);
    distributionPage.selectValueFromPeriod(periodDisplayedByDefault);
    distributionPage.clickViewLoadAmount();

    WarehouseLoadAmountPage warehouseLoadAmountPage = new WarehouseLoadAmountPage(testWebDriver);
    assertEquals(warehouseLoadAmountPage.getFacilityPopulation(1, 1), warehouseLoadAmountPage.getFacilityPopulation(3, 1));
    assertEquals("--", warehouseLoadAmountPage.getProduct1Isa(3, 1));
    assertEquals("--", warehouseLoadAmountPage.getProduct1Isa(2, 1));
    assertEquals("--", warehouseLoadAmountPage.getProduct1Isa(1, 1));
    assertEquals("--", warehouseLoadAmountPage.getFacilityPopulation(2, 1));
    assertEquals(String.valueOf(Integer.parseInt(warehouseLoadAmountPage.getProduct2Isa(1, 1)) + Integer.parseInt(warehouseLoadAmountPage.getProduct2Isa(2, 1))), warehouseLoadAmountPage.getProduct2Isa(3, 1));

    assertEquals(Integer.parseInt(warehouseLoadAmountPage.getFacilityPopulation(1, 2)) + Integer.parseInt(warehouseLoadAmountPage.getFacilityPopulation(2, 2)), warehouseLoadAmountPage.getFacilityPopulation(3, 2));
    assertEquals("--", warehouseLoadAmountPage.getProduct1Isa(1, 2));
    assertEquals("--", warehouseLoadAmountPage.getProduct2Isa(1, 2));
    assertEquals(warehouseLoadAmountPage.getProduct1Isa(2, 2), warehouseLoadAmountPage.getProduct1Isa(3, 2));
    assertEquals(warehouseLoadAmountPage.getProduct2Isa(2, 2), warehouseLoadAmountPage.getProduct2Isa(3, 2));

    assertEquals(warehouseLoadAmountPage.getAggregatePopulation(3), String.valueOf(Integer.parseInt(warehouseLoadAmountPage.getFacilityPopulation(1, 1)) + Integer.parseInt(warehouseLoadAmountPage.getFacilityPopulation(1, 2)) + Integer.parseInt(warehouseLoadAmountPage.getFacilityPopulation(2, 2))));
    assertEquals(warehouseLoadAmountPage.getAggregateProduct2Isa(3), String.valueOf(Integer.parseInt(warehouseLoadAmountPage.getProduct2Isa(1, 1)) + Integer.parseInt(warehouseLoadAmountPage.getProduct2Isa(2, 1)) + Integer.parseInt(warehouseLoadAmountPage.getProduct2Isa(2, 2))));
    assertEquals(warehouseLoadAmountPage.getAggregateProduct1Isa(3), warehouseLoadAmountPage.getProduct1Isa(2, 2));
    assertEquals("--", warehouseLoadAmountPage.getAggregateProduct1Isa(1));

    assertEquals(warehouseLoadAmountPage.getCityName(1), testWebDriver.getElementByXpath("//h3/span[contains(text(),'" + district1 + "')]").getText());
    assertEquals(warehouseLoadAmountPage.getCityName(2), testWebDriver.getElementByXpath("//h3/span[contains(text(),'" + district2 + "')]").getText());
    assertEquals(dbWrapper.getGeoLevelOfGeoZone(parentGeoZone), testWebDriver.getElementByXpath("//h3/span[contains(text(),\"District\")]").getText());


  }

  @Test(groups = {"functional2"}, dataProvider = "Data-Provider-Function-Multiple-GeoZones")
  public void testShouldVerifyMultipleGeographicZones(String userSIC, String password, String deliveryZoneCodeFirst, String deliveryZoneCodeSecond,
                                                      String deliveryZoneNameFirst, String deliveryZoneNameSecond,
                                                      String facilityCodeFirst, String facilityCodeSecond,
                                                      String programFirst, String programSecond, String schedule, String product,
                                                      String product2, String geoZone1, String geoZone2) throws Exception {

    List<String> rightsList = new ArrayList<String>();
    rightsList.add("MANAGE_DISTRIBUTION");
    setupTestDataToInitiateRnRForDistribution(facilityCodeFirst, facilityCodeSecond, true, programFirst, userSIC, "200", "openLmis", rightsList, programSecond, district1, parentGeoZone, parentGeoZone);
    setupDataForDeliveryZoneForMultipleFacilitiesAttachedWithSingleDeliveryZone(deliveryZoneCodeFirst,
      deliveryZoneNameFirst,
      facilityCodeFirst, facilityCodeSecond,
      programFirst, programSecond, schedule);

    dbWrapper.insertRoleAssignmentForDistribution(userSIC, "store in-charge", deliveryZoneCodeFirst);
    dbWrapper.insertRoleAssignmentForDistribution(userSIC, "store in-charge", deliveryZoneCodeSecond);
    dbWrapper.InsertOverridenIsa(facilityCodeFirst, programFirst, product, 1000);
    dbWrapper.InsertOverridenIsa(facilityCodeFirst, programFirst, product2, 2000);
    dbWrapper.InsertOverridenIsa(facilityCodeSecond, programFirst, product, 3000);
    dbWrapper.InsertOverridenIsa(facilityCodeSecond, programFirst, product2, 0);

    LoginPage loginPage = new LoginPage(testWebDriver, baseUrlGlobal);
    HomePage homePage = loginPage.loginAs(userSIC, password);
    DistributionPage distributionPage = homePage.navigatePlanDistribution();

    distributionPage.selectValueFromDeliveryZone(deliveryZoneNameFirst);
    distributionPage.selectValueFromProgram(programFirst);
    distributionPage.selectValueFromPeriod(periodDisplayedByDefault);
    distributionPage.clickViewLoadAmount();

    WarehouseLoadAmountPage warehouseLoadAmountPage = new WarehouseLoadAmountPage(testWebDriver);

    verifyWarehouseLoadAmountHeader(deliveryZoneNameFirst, programFirst, periodDisplayedByDefault);
    assertEquals(facilityCodeSecond, warehouseLoadAmountPage.getFacilityCode(1, 1));
    assertEquals(dbWrapper.getFacilityName(facilityCodeSecond), warehouseLoadAmountPage.getFacilityName(1, 1));
    assertEquals(dbWrapper.getFacilityPopulation(facilityCodeSecond), warehouseLoadAmountPage.getFacilityPopulation(1, 1));
    assertEquals(dbWrapper.getOverridenIsa(facilityCodeSecond, programFirst, product), warehouseLoadAmountPage.getProduct1Isa(1, 1));
    assertEquals(dbWrapper.getOverridenIsa(facilityCodeSecond, programFirst, product2), warehouseLoadAmountPage.getProduct2Isa(1, 1));
    assertEquals(geoZone1, warehouseLoadAmountPage.getGeoZoneName(1));

    assertEquals(facilityCodeFirst, warehouseLoadAmountPage.getFacilityCode(1, 2));
    assertEquals(dbWrapper.getFacilityName(facilityCodeFirst), warehouseLoadAmountPage.getFacilityName(1, 2));
    assertEquals(dbWrapper.getFacilityPopulation(facilityCodeFirst), warehouseLoadAmountPage.getFacilityPopulation(1, 2));
    assertEquals(dbWrapper.getOverridenIsa(facilityCodeFirst, programFirst, product), warehouseLoadAmountPage.getProduct1Isa(1, 2));
    assertEquals(dbWrapper.getOverridenIsa(facilityCodeFirst, programFirst, product2), warehouseLoadAmountPage.getProduct2Isa(1, 2));
    assertEquals(geoZone2, warehouseLoadAmountPage.getGeoZoneName(2));

    dbWrapper.updatePopulationOfFacility(facilityCodeFirst, null);
    dbWrapper.updateOverridenIsa(facilityCodeFirst, programFirst, product, null);
    homePage.navigatePlanDistribution();
    distributionPage.selectValueFromDeliveryZone(deliveryZoneNameFirst);
    distributionPage.selectValueFromProgram(programFirst);
    distributionPage.selectValueFromPeriod(periodDisplayedByDefault);
    distributionPage.clickViewLoadAmount();
    assertEquals("--", warehouseLoadAmountPage.getFacilityPopulation(1, 2));
    assertEquals("--", warehouseLoadAmountPage.getProduct1Isa(1, 2));

  }


  public void verifyWarehouseLoadAmountHeader(String deliverZone, String program, String period) {
    WebElement deliverZoneElement = testWebDriver.getElementByXpath("(//div[2]/div/div/div/span)[1]");
    WebElement programElement = testWebDriver.getElementByXpath("(//div[2]/div/div/div/span)[2]");
    WebElement periodElement = testWebDriver.getElementByXpath("(//div[2]/div/div/div/span)[3]");
    testWebDriver.waitForElementToAppear(deliverZoneElement);
    SeleneseTestNgHelper.assertEquals(deliverZoneElement.getText(), deliverZone);
    SeleneseTestNgHelper.assertEquals(programElement.getText(), program);
    SeleneseTestNgHelper.assertEquals(periodElement.getText(), period);
  }


  @AfterMethod(groups = {"functional2", "smoke"})
  public void tearDown() throws Exception {
    HomePage homePage = new HomePage(testWebDriver);
    homePage.logout(baseUrlGlobal);
    dbWrapper.deleteData();
    dbWrapper.closeConnection();
  }


  @DataProvider(name = "Data-Provider-Function")
  public Object[][] parameterIntTestProviderPositive() {
    return new Object[][]{
      {"fieldcoordinator", "Admin123", "DZ1", "DZ2", "Delivery Zone First", "Delivery Zone Second",
        "F10", "F11", "VACCINES", "TB", "M", "P10", "P11"}
    };

  }

  @DataProvider(name = "Data-Provider-Function-Multiple-Facilities")
  public Object[][] parameterIntTestProvider() {
    return new Object[][]{
      {"fieldcoordinator", "Admin123", "DZ1", "DZ2", "Delivery Zone First", "Delivery Zone Second",
        "F10", "F11", "F12", "F13", "VACCINES", "TB", "M", "P10", "P11"}
    };

  }

  @DataProvider(name = "Data-Provider-Function-Multiple-GeoZones")
  public Object[][] parameterIntTestProviderMultipleGeoZones() {
    return new Object[][]{
      {"fieldcoordinator", "Admin123", "DZ1", "DZ2", "Delivery Zone First", "Delivery Zone Second",
        "F10", "F11", "VACCINES", "TB", "M", "P10", "P11", "District", "Total"}
    };

  }

}

