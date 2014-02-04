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

import org.openlmis.UiUtils.TestCaseHelper;
import org.openlmis.pageobjects.*;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.thoughtworks.selenium.SeleneseTestBase.assertEquals;
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
  public void setUp() throws Exception {
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
  public void testVisitInformationPage() throws SQLException {
    loginPage.loginAs(visitInformationData.get(USER), visitInformationData.get(PASSWORD));
    initiateDistribution(visitInformationData.get(FIRST_DELIVERY_ZONE_NAME), visitInformationData.get(VACCINES_PROGRAM));
    GeneralObservationPage generalObservationPage = facilityListPage.selectFacility(visitInformationData.get(FIRST_FACILITY_CODE));
    verifyLabels();
    assertEquals("Was " + dbWrapper.getAttributeFromTable("facilities", "name", "code", visitInformationData.get(FIRST_FACILITY_CODE)) +
      " visited in " + "Period14" + "?", generalObservationPage.getWasFacilityVisitedLabel());
    generalObservationPage.verifyIndicator("RED");
    generalObservationPage.selectFacilityVisitedYes();
    generalObservationPage.verifyIndicator("AMBER");
    generalObservationPage.enterVisitDateAsFirstOfCurrentMonth();
    generalObservationPage.verifyIndicator("AMBER");
    generalObservationPage.enterObservations("Some Observations");
    generalObservationPage.verifyIndicator("AMBER");
    generalObservationPage.enterConfirmedByName("ConfirmName");
    generalObservationPage.verifyIndicator("AMBER");
    generalObservationPage.enterConfirmedByTitle("ConfirmTitle");
    generalObservationPage.verifyIndicator("AMBER");
    generalObservationPage.enterVerifiedByName("VerifyName");
    generalObservationPage.verifyIndicator("AMBER");
    generalObservationPage.enterVerifiedByTitle("VerifyTitle");
    generalObservationPage.verifyIndicator("GREEN");
    generalObservationPage.enterVehicleId("12U3-93");
    generalObservationPage.verifyIndicator("GREEN");
  }

  private void verifyLabels() {
    GeneralObservationPage generalObservationPage = PageFactory.getInstanceOfObservation(testWebDriver);
    assertEquals("Visit Info / Observations", generalObservationPage.getFacilityVisitTabLabel());
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

  public void initiateDistribution(String deliveryZoneNameFirst, String programFirst) {
    HomePage homePage = PageFactory.getInstanceOfHomePage(testWebDriver);
    DistributionPage distributionPage = homePage.navigateToDistributionWhenOnline();
    distributionPage.selectValueFromDeliveryZone(deliveryZoneNameFirst);
    distributionPage.selectValueFromProgram(programFirst);
    distributionPage.clickInitiateDistribution();
    distributionPage.clickRecordData(1);
  }
}
