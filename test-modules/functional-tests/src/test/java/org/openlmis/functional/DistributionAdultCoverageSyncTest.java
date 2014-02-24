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
import org.openlmis.UiUtils.TestWebDriver;
import org.openlmis.pageobjects.*;
import org.openqa.selenium.JavascriptExecutor;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.io.IOException;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.thoughtworks.selenium.SeleneseTestCase.assertEquals;
import static java.util.Arrays.asList;
import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;

public class DistributionAdultCoverageSyncTest extends TestCaseHelper {

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

  public final Map<String, String> adultCoverageData = new HashMap<String, String>() {{
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
    Map<String, String> dataMap = adultCoverageData;
    setupDataForDistributionTest(dataMap.get(USER), dataMap.get(FIRST_DELIVERY_ZONE_CODE), dataMap.get(SECOND_DELIVERY_ZONE_CODE),
      dataMap.get(FIRST_DELIVERY_ZONE_NAME), dataMap.get(SECOND_DELIVERY_ZONE_NAME), dataMap.get(FIRST_FACILITY_CODE),
      dataMap.get(SECOND_FACILITY_CODE), dataMap.get(VACCINES_PROGRAM), dataMap.get(TB_PROGRAM), dataMap.get(SCHEDULE));
    loginPage = PageFactory.getInstanceOfLoginPage(testWebDriver, baseUrlGlobal);
  }

  @Test(groups = {"distribution"})
  public void testShouldVerifyAllLabelsAndDefaultValuesWhenNoMapping() {
    HomePage homePage = loginPage.loginAs(adultCoverageData.get(USER), adultCoverageData.get(PASSWORD));
    DistributionPage distributionPage = homePage.navigateToDistributionWhenOnline();
    distributionPage.initiate(adultCoverageData.get(FIRST_DELIVERY_ZONE_NAME), adultCoverageData.get(VACCINES_PROGRAM));
    FacilityListPage facilityListPage = distributionPage.clickRecordData(1);
    VisitInformationPage visitInformationPage = facilityListPage.selectFacility(adultCoverageData.get(FIRST_FACILITY_CODE));

    facilityListPage.verifyOverallFacilityIndicatorColor("AMBER");

    AdultCoveragePage adultCoveragePage = visitInformationPage.navigateToAdultCoverage();
    adultCoveragePage.verifyIndicator("RED");

    assertEquals("Adult Coverage", adultCoveragePage.getAdultCoveragePageHeader());
    assertEquals("Adult Coverage", adultCoveragePage.getAdultCoverageTabLabel());
    assertEquals("Group Tetanus Vaccinations (doses)", adultCoveragePage.getGroupVaccinationLabel());
    assertEquals("Target Group", adultCoveragePage.getTargetGroupLabel());
    assertEquals("Tetanus 1st dose", adultCoveragePage.getTetanusFirstLabel());
    assertEquals("Tetanus 2nd-5th dose", adultCoveragePage.getTetanusSecondFifthLabel());
    assertEquals("Health Center", adultCoveragePage.getHealthCenter1Label());
    assertEquals("Outreach", adultCoveragePage.getOutreach1Label());
    assertEquals("Health Center", adultCoveragePage.getHealthCenter2To5Label());
    assertEquals("Outreach", adultCoveragePage.getOutreach2To5Label());
    assertEquals("Total", adultCoveragePage.getTotal1Label());
    assertEquals("Total", adultCoveragePage.getTotal2To5Label());
    assertEquals("Total Tetanus", adultCoveragePage.getTotalTetanusLabel());
    assertEquals("Coverage Rate", adultCoveragePage.getCoverageRateLabel());
    assertEquals("Opened Vials", adultCoveragePage.getOpenedVialsLabel());
    assertEquals("Pregnant Women", adultCoveragePage.getPregnantWomenLabel());
    assertEquals("MIF 15-49 years", adultCoveragePage.getMifLabel());
    assertEquals("Community", adultCoveragePage.getCommunityLabel());
    assertEquals("Students", adultCoveragePage.getStudentsLabel());
    assertEquals("Workers", adultCoveragePage.getWorkersLabel());
    assertEquals("Students not MIF", adultCoveragePage.getStudentNotMifLabel());
    assertEquals("Workers not MIF", adultCoveragePage.getWorkerNotMifLabel());
    assertEquals("Other not MIF", adultCoveragePage.getOtherNotMifLabel());
    assertEquals("Total", adultCoveragePage.getTotalRowLabel());

    for (int rowNumber = 1; rowNumber <= 7; rowNumber++) {
      verifyTargetGroup(rowNumber, "");
      verifyTetanusTotalFirst(rowNumber, "0");
      verifyTetanusTotal2To5(rowNumber, "0");
      verifyTotalTetanus(rowNumber, "0");
      verifyCoverageRate(rowNumber, "");
      verifyOutreachFirstInput(rowNumber, "");
      verifyOutreach2To5Input(rowNumber, "");
      verifyOutreachFirstEnabled(rowNumber, true);
      verifyOutreach2To5Enabled(rowNumber, true);
      verifyOutreachFirstNrSelected(rowNumber, false);
      verifyOutreach2To5NrSelected(rowNumber, false);
    }

    assertEquals("", adultCoveragePage.getWastageRate());
    assertEquals("", adultCoveragePage.getOpenedVialInputField());

    verifyHealthCenterFirstInput(1, "");
    verifyHealthCenterFirstInput(2, "");
    verifyHealthCenterFirstInput(7, "");
    verifyHealthCenter2To5Input(1, "");
    verifyHealthCenter2To5Input(2, "");
    verifyHealthCenter2To5Input(7, "");

    verifyHealthCenterFirstEnabled(1, true);
    verifyHealthCenterFirstEnabled(2, true);
    verifyHealthCenterFirstEnabled(7, true);
    verifyHealthCenter2To5Enabled(1, true);
    verifyHealthCenter2To5Enabled(2, true);
    verifyHealthCenter2To5Enabled(7, true);

    verifyHealthCenterFirstNrSelected(1, false);
    verifyHealthCenterFirstNrSelected(2, false);
    verifyHealthCenterFirstNrSelected(7, false);
    verifyHealthCenter2To5NrSelected(1, false);
    verifyHealthCenter2To5NrSelected(2, false);
    verifyHealthCenter2To5NrSelected(7, false);

    assertEquals("0", adultCoveragePage.getTotalTetanus1());
    assertEquals("0", adultCoveragePage.getTotalTetanus2To5());
    assertEquals("0", adultCoveragePage.getTotalTetanus());
    assertEquals("0", adultCoveragePage.getTotalHealthCenterTetanus1());
    assertEquals("0", adultCoveragePage.getTotalHealthCenterTetanus2To5());
    assertEquals("0", adultCoveragePage.getTotalOutreachTetanus1());
    assertEquals("0", adultCoveragePage.getTotalOutreachTetanus2To5());
  }

  @Test(groups = {"distribution"})
  public void testShouldVerifyTargetGroupAndWastageRateWhenMappingWhenProductInactiveAtProgram() throws SQLException {
    dbWrapper.updateActiveStatusOfProgramProduct("tetanus", "VACCINES", "false");
    insertProductMappingToGroup();
    dbWrapper.insertAdultCoverageOpenedVialMapping("tetanus");
    dbWrapper.insertProgramProductISA("VACCINES", "tetanus", "4", "12", "3", "4", "4", "2", "4");
    dbWrapper.updateFieldValue("facilities", "catchmentpopulation", 8989);

    HomePage homePage = loginPage.loginAs(adultCoverageData.get(USER), adultCoverageData.get(PASSWORD));
    DistributionPage distributionPage = homePage.navigateToDistributionWhenOnline();
    distributionPage.initiate(adultCoverageData.get(FIRST_DELIVERY_ZONE_NAME), adultCoverageData.get(VACCINES_PROGRAM));
    FacilityListPage facilityListPage = distributionPage.clickRecordData(1);
    VisitInformationPage visitInformationPage = facilityListPage.selectFacility(adultCoverageData.get(FIRST_FACILITY_CODE));

    facilityListPage.verifyOverallFacilityIndicatorColor("AMBER");

    AdultCoveragePage adultCoveragePage = visitInformationPage.navigateToAdultCoverage();
    adultCoveragePage.verifyIndicator("RED");

    for (int rowNumber = 1; rowNumber <= 7; rowNumber++) {
      verifyTargetGroup(rowNumber, "360");
      verifyCoverageRate(rowNumber, "0");
    }

    adultCoveragePage.enterOpenedVialInputField("90");
    assertEquals("100", adultCoveragePage.getWastageRate());
  }

  @Test(groups = {"distribution"})
  public void testShouldVerifyTargetGroupAndWastageRateWhenMappingWhenProductGloballyInactive() throws SQLException {
    dbWrapper.insertAdultCoverageOpenedVialMapping("tetanus");
    dbWrapper.updateFieldValue("products", "active", false);
    insertProductMappingToGroup();
    dbWrapper.insertProgramProductISA("VACCINES", "tetanus", "400", "12", "3", "4", "4", "2", "4");
    dbWrapper.updateFieldValue("facilities", "catchmentpopulation", 8989);

    HomePage homePage = loginPage.loginAs(adultCoverageData.get(USER), adultCoverageData.get(PASSWORD));
    DistributionPage distributionPage = homePage.navigateToDistributionWhenOnline();
    distributionPage.initiate(adultCoverageData.get(FIRST_DELIVERY_ZONE_NAME), adultCoverageData.get(VACCINES_PROGRAM));
    FacilityListPage facilityListPage = distributionPage.clickRecordData(1);
    VisitInformationPage visitInformationPage = facilityListPage.selectFacility(adultCoverageData.get(FIRST_FACILITY_CODE));

    facilityListPage.verifyOverallFacilityIndicatorColor("AMBER");

    AdultCoveragePage adultCoveragePage = visitInformationPage.navigateToAdultCoverage();
    adultCoveragePage.verifyIndicator("RED");

    for (int rowNumber = 1; rowNumber <= 7; rowNumber++) {
      verifyTargetGroup(rowNumber, "35956");
      verifyCoverageRate(rowNumber, "0");
    }

    adultCoveragePage.enterOpenedVialInputField("90");
    assertEquals("100", adultCoveragePage.getWastageRate());
  }

  @Test(groups = {"distribution"})
  public void testShouldVerifyTargetGroupAndWastageRateWhenNoCatchmentPopulation() throws SQLException {
    insertProductMappingToGroup();
    dbWrapper.insertAdultCoverageOpenedVialMapping("tetanus");
    dbWrapper.insertProgramProductISA("VACCINES", "tetanus", "45", "12", "3", "4", "4", "2", "4");
    dbWrapper.updateFieldValue("facilities", "catchmentpopulation", null);

    HomePage homePage = loginPage.loginAs(adultCoverageData.get(USER), adultCoverageData.get(PASSWORD));
    DistributionPage distributionPage = homePage.navigateToDistributionWhenOnline();
    distributionPage.initiate(adultCoverageData.get(FIRST_DELIVERY_ZONE_NAME), adultCoverageData.get(VACCINES_PROGRAM));
    FacilityListPage facilityListPage = distributionPage.clickRecordData(1);
    VisitInformationPage visitInformationPage = facilityListPage.selectFacility(adultCoverageData.get(FIRST_FACILITY_CODE));

    facilityListPage.verifyOverallFacilityIndicatorColor("AMBER");

    AdultCoveragePage adultCoveragePage = visitInformationPage.navigateToAdultCoverage();
    adultCoveragePage.verifyIndicator("RED");

    for (int rowNumber = 1; rowNumber <= 7; rowNumber++) {
      verifyTargetGroup(rowNumber, "");
      verifyCoverageRate(rowNumber, "");
    }

    adultCoveragePage.enterOpenedVialInputField("90");
    assertEquals("100", adultCoveragePage.getWastageRate());
  }

  @Test(groups = {"distribution"})
  public void testShouldVerifyTargetGroupAndWastageRateWhenNoWhoRatio() throws SQLException {
    insertProductMappingToGroup();
    dbWrapper.insertAdultCoverageOpenedVialMapping("tetanus");
    dbWrapper.updateFieldValue("facilities", "catchmentpopulation", "8980", "code", adultCoverageData.get(FIRST_FACILITY_CODE));
    HomePage homePage = loginPage.loginAs(adultCoverageData.get(USER), adultCoverageData.get(PASSWORD));
    DistributionPage distributionPage = homePage.navigateToDistributionWhenOnline();
    distributionPage.initiate(adultCoverageData.get(FIRST_DELIVERY_ZONE_NAME), adultCoverageData.get(VACCINES_PROGRAM));
    FacilityListPage facilityListPage = distributionPage.clickRecordData(1);
    VisitInformationPage visitInformationPage = facilityListPage.selectFacility(adultCoverageData.get(FIRST_FACILITY_CODE));

    facilityListPage.verifyOverallFacilityIndicatorColor("AMBER");

    AdultCoveragePage adultCoveragePage = visitInformationPage.navigateToAdultCoverage();
    adultCoveragePage.verifyIndicator("RED");

    for (int rowNumber = 1; rowNumber <= 7; rowNumber++) {
      verifyTargetGroup(rowNumber, "");
      verifyCoverageRate(rowNumber, "");
    }

    adultCoveragePage.enterOpenedVialInputField("90");
    assertEquals("100", adultCoveragePage.getWastageRate());
  }

  @Test(groups = {"distribution"})
  public void testShouldVerifyWastageRateAndTargetGroupForAllGroups() throws SQLException {
    insertProductMappingToGroup();
    dbWrapper.insertAdultCoverageOpenedVialMapping("tetanus");
    dbWrapper.insertProgramProductISA("VACCINES", "tetanus", "45", "12", "3", "4", "4", "2", "4");
    dbWrapper.updateFieldValue("facilities", "catchmentpopulation", "8984", "code", adultCoverageData.get(FIRST_FACILITY_CODE));

    HomePage homePage = loginPage.loginAs(adultCoverageData.get(USER), adultCoverageData.get(PASSWORD));
    DistributionPage distributionPage = homePage.navigateToDistributionWhenOnline();
    distributionPage.initiate(adultCoverageData.get(FIRST_DELIVERY_ZONE_NAME), adultCoverageData.get(VACCINES_PROGRAM));
    FacilityListPage facilityListPage = distributionPage.clickRecordData(1);
    VisitInformationPage visitInformationPage = facilityListPage.selectFacility(adultCoverageData.get(FIRST_FACILITY_CODE));

    facilityListPage.verifyOverallFacilityIndicatorColor("AMBER");

    AdultCoveragePage adultCoveragePage = visitInformationPage.navigateToAdultCoverage();
    adultCoveragePage.verifyIndicator("RED");

    for (int rowNumber = 1; rowNumber <= 7; rowNumber++) {
      verifyTargetGroup(rowNumber, "4043");
      verifyCoverageRate(rowNumber, "0");
    }

    adultCoveragePage.enterOpenedVialInputField("90");
    assertEquals("100", adultCoveragePage.getWastageRate());
  }

  @Test(groups = {"distribution"})
  public void testShouldVerifyTotalAndApplyNrToIndividualField() throws SQLException {
    insertProductMappingToGroup();
    dbWrapper.insertProgramProductISA("VACCINES", "tetanus", "45", "12", "3", "4", "4", "2", "4");
    dbWrapper.updateFieldValue("facilities", "catchmentpopulation", "8984", "code", adultCoverageData.get(FIRST_FACILITY_CODE));

    HomePage homePage = loginPage.loginAs(adultCoverageData.get(USER), adultCoverageData.get(PASSWORD));
    DistributionPage distributionPage = homePage.navigateToDistributionWhenOnline();
    distributionPage.initiate(adultCoverageData.get(FIRST_DELIVERY_ZONE_NAME), adultCoverageData.get(VACCINES_PROGRAM));
    FacilityListPage facilityListPage = distributionPage.clickRecordData(1);
    VisitInformationPage visitInformationPage = facilityListPage.selectFacility(adultCoverageData.get(FIRST_FACILITY_CODE));

    facilityListPage.verifyOverallFacilityIndicatorColor("AMBER");

    AdultCoveragePage adultCoveragePage = visitInformationPage.navigateToAdultCoverage();
    adultCoveragePage.verifyIndicator("RED");

    for (int rowNumber = 1; rowNumber <= 2; rowNumber++) {
      adultCoveragePage.enterHealthCenterFirstInput(rowNumber, "5611" + rowNumber);
      adultCoveragePage.enterHealthCenter2To5Input(rowNumber, "5623" + rowNumber);
      adultCoveragePage.enterOutreachFirstInput(rowNumber, "5612" + rowNumber);
      adultCoveragePage.enterOutreach2To5Input(rowNumber, "5624" + rowNumber);
    }

    assertEquals("112232", adultCoveragePage.getTotalTetanusFirst(1));
    assertEquals("112472", adultCoveragePage.getTotalTetanus2To5(1));
    assertEquals("224704", adultCoveragePage.getTotalTetanus(1));
    assertEquals("112234", adultCoveragePage.getTotalTetanusFirst(2));
    assertEquals("112474", adultCoveragePage.getTotalTetanus2To5(2));
    assertEquals("224708", adultCoveragePage.getTotalTetanus(2));

    assertEquals("224466", adultCoveragePage.getTotalTetanus1());
    assertEquals("224946", adultCoveragePage.getTotalTetanus2To5());
    assertEquals("449412", adultCoveragePage.getTotalTetanus());
    assertEquals("112223", adultCoveragePage.getTotalHealthCenterTetanus1());
    assertEquals("112463", adultCoveragePage.getTotalHealthCenterTetanus2To5());
    assertEquals("112243", adultCoveragePage.getTotalOutreachTetanus1());
    assertEquals("112483", adultCoveragePage.getTotalOutreachTetanus2To5());

    adultCoveragePage.applyHealthCenter2To5Nr(2);
    assertTrue(adultCoveragePage.isHealthCenter2To5NrSelected(2));
    assertFalse(adultCoveragePage.isHealthCenter2To5Enabled(2));
    assertEquals("", adultCoveragePage.getHealthCenter2To5Input(2));

    assertEquals("56242", adultCoveragePage.getTotalTetanus2To5(2));
    assertEquals("168476", adultCoveragePage.getTotalTetanus(2));

    assertEquals("168714", adultCoveragePage.getTotalTetanus2To5());
    assertEquals("393180", adultCoveragePage.getTotalTetanus());

    adultCoveragePage.applyHealthCenter2To5Nr(2);
    assertFalse(adultCoveragePage.isHealthCenter2To5NrSelected(2));
    assertTrue(adultCoveragePage.isHealthCenter2To5Enabled(2));

    assertEquals("56242", adultCoveragePage.getTotalTetanus2To5(2));
    assertEquals("168476", adultCoveragePage.getTotalTetanus(2));

    assertEquals("168714", adultCoveragePage.getTotalTetanus2To5());
    assertEquals("393180", adultCoveragePage.getTotalTetanus());

    assertEquals("", adultCoveragePage.getWastageRate());
  }

  @Test(groups = {"distribution"})
  public void testShouldVerifyCoverageRateAndWastageRateCalculation() throws SQLException {
    insertProductMappingToGroup();
    dbWrapper.insertAdultCoverageOpenedVialMapping("tetanus");
    dbWrapper.insertProgramProductISA("VACCINES", "tetanus", "405", "12", "3", "4", "4", "2", "4");
    dbWrapper.updateFieldValue("facilities", "catchmentpopulation", "89", "code", adultCoverageData.get(FIRST_FACILITY_CODE));

    HomePage homePage = loginPage.loginAs(adultCoverageData.get(USER), adultCoverageData.get(PASSWORD));
    DistributionPage distributionPage = homePage.navigateToDistributionWhenOnline();
    distributionPage.initiate(adultCoverageData.get(FIRST_DELIVERY_ZONE_NAME), adultCoverageData.get(VACCINES_PROGRAM));
    FacilityListPage facilityListPage = distributionPage.clickRecordData(1);
    VisitInformationPage visitInformationPage = facilityListPage.selectFacility(adultCoverageData.get(FIRST_FACILITY_CODE));

    facilityListPage.verifyOverallFacilityIndicatorColor("AMBER");

    AdultCoveragePage adultCoveragePage = visitInformationPage.navigateToAdultCoverage();
    adultCoveragePage.verifyIndicator("RED");

    for (int rowNumber = 1; rowNumber <= 7; rowNumber++) {
      adultCoveragePage.enterOutreachFirstInput(rowNumber, "5612" + (rowNumber * 20));
      verifyTargetGroup(rowNumber, "360");
    }

    assertEquals("155894", adultCoveragePage.getCoverageRate(1));
    assertEquals("155900", adultCoveragePage.getCoverageRate(2));
    assertEquals("155906", adultCoveragePage.getCoverageRate(3));
    assertEquals("155911", adultCoveragePage.getCoverageRate(4));
    assertEquals("1558917", adultCoveragePage.getCoverageRate(5));
    assertEquals("1558922", adultCoveragePage.getCoverageRate(6));
    assertEquals("1558928", adultCoveragePage.getCoverageRate(7));

    adultCoveragePage.enterOpenedVialInputField("90");
    assertEquals("-2120051", adultCoveragePage.getWastageRate());
  }

  @Test(groups = {"distribution"})
  public void testShouldVerifyCoverageAndWastageRateCalculationWhenCatchmentPopulationZero() throws SQLException {
    insertProductMappingToGroup();
    dbWrapper.insertAdultCoverageOpenedVialMapping("tetanus");
    dbWrapper.insertProgramProductISA("VACCINES", "tetanus", "45", "12", "3", "4", "4", "2", "4");
    dbWrapper.updateFieldValue("facilities", "catchmentpopulation", "0", "code", adultCoverageData.get(FIRST_FACILITY_CODE));

    HomePage homePage = loginPage.loginAs(adultCoverageData.get(USER), adultCoverageData.get(PASSWORD));
    DistributionPage distributionPage = homePage.navigateToDistributionWhenOnline();
    distributionPage.initiate(adultCoverageData.get(FIRST_DELIVERY_ZONE_NAME), adultCoverageData.get(VACCINES_PROGRAM));
    FacilityListPage facilityListPage = distributionPage.clickRecordData(1);
    VisitInformationPage visitInformationPage = facilityListPage.selectFacility(adultCoverageData.get(FIRST_FACILITY_CODE));

    facilityListPage.verifyOverallFacilityIndicatorColor("AMBER");

    AdultCoveragePage adultCoveragePage = visitInformationPage.navigateToAdultCoverage();
    adultCoveragePage.verifyIndicator("RED");

    for (int rowNumber = 1; rowNumber <= 7; rowNumber++) {
      adultCoveragePage.enterOutreachFirstInput(rowNumber, "5612" + rowNumber);
    }

    for (int rowNumber = 1; rowNumber <= 7; rowNumber++) {
      verifyTargetGroup(rowNumber, "0");
      verifyCoverageRate(rowNumber, "");
    }

    adultCoveragePage.enterOpenedVialInputField("1234567");
    assertEquals("97", adultCoveragePage.getWastageRate());
  }


  public void setupDataForDistributionTest(String userSIC, String deliveryZoneCodeFirst, String deliveryZoneCodeSecond,
                                           String deliveryZoneNameFirst, String deliveryZoneNameSecond,
                                           String facilityCodeFirst, String facilityCodeSecond,
                                           String programFirst, String programSecond, String schedule) throws SQLException {
    List<String> rightsList = asList("MANAGE_DISTRIBUTION");
    setupTestDataToInitiateRnRAndDistribution(facilityCodeFirst, facilityCodeSecond, true, programFirst, userSIC, "200", rightsList,
      programSecond, "District1", "Ngorongoro", "Ngorongoro");
    setupDataForDeliveryZone(true, deliveryZoneCodeFirst, deliveryZoneCodeSecond, deliveryZoneNameFirst, deliveryZoneNameSecond,
      facilityCodeFirst, facilityCodeSecond, programFirst, programSecond, schedule);
    dbWrapper.insertRoleAssignmentForDistribution(userSIC, "store in-charge", deliveryZoneCodeFirst);
    dbWrapper.insertRoleAssignmentForDistribution(userSIC, "store in-charge", deliveryZoneCodeSecond);
    dbWrapper.insertProductsForAdultCoverage();
  }

  public void verifyTargetGroup(int rowNumber, String value) {
    AdultCoveragePage adultCoveragePage = PageFactory.getInstanceOfAdultCoveragePage(testWebDriver);
    assertEquals(value, adultCoveragePage.getTargetGroup(rowNumber));
  }

  public void verifyTetanusTotalFirst(int rowNumber, String value) {
    AdultCoveragePage adultCoveragePage = PageFactory.getInstanceOfAdultCoveragePage(testWebDriver);
    assertEquals(value, adultCoveragePage.getTotalTetanusFirst(rowNumber));
  }

  public void verifyTetanusTotal2To5(int rowNumber, String value) {
    AdultCoveragePage adultCoveragePage = PageFactory.getInstanceOfAdultCoveragePage(testWebDriver);
    assertEquals(value, adultCoveragePage.getTotalTetanus2To5(rowNumber));
  }

  public void verifyTotalTetanus(int rowNumber, String value) {
    AdultCoveragePage adultCoveragePage = PageFactory.getInstanceOfAdultCoveragePage(testWebDriver);
    assertEquals(value, adultCoveragePage.getTotalTetanus(rowNumber));
  }

  public void verifyCoverageRate(int rowNumber, String value) {
    AdultCoveragePage adultCoveragePage = PageFactory.getInstanceOfAdultCoveragePage(testWebDriver);
    assertEquals(value, adultCoveragePage.getCoverageRate(rowNumber));
  }

  public void verifyOutreachFirstInput(int rowNumber, String value) {
    AdultCoveragePage adultCoveragePage = PageFactory.getInstanceOfAdultCoveragePage(testWebDriver);
    assertEquals(value, adultCoveragePage.getOutreachFirstInput(rowNumber));
  }

  public void verifyOutreach2To5Input(int rowNumber, String value) {
    AdultCoveragePage adultCoveragePage = PageFactory.getInstanceOfAdultCoveragePage(testWebDriver);
    assertEquals(value, adultCoveragePage.getOutreach2To5Input(rowNumber));
  }

  public void verifyHealthCenterFirstInput(int rowNumber, String value) {
    AdultCoveragePage adultCoveragePage = PageFactory.getInstanceOfAdultCoveragePage(testWebDriver);
    assertEquals(value, adultCoveragePage.getHealthCenterFirstInput(rowNumber));
  }

  public void verifyHealthCenter2To5Input(int rowNumber, String value) {
    AdultCoveragePage adultCoveragePage = PageFactory.getInstanceOfAdultCoveragePage(testWebDriver);
    assertEquals(value, adultCoveragePage.getHealthCenter2To5Input(rowNumber));
  }

  public void verifyOutreachFirstEnabled(int rowNumber, boolean value) {
    AdultCoveragePage adultCoveragePage = PageFactory.getInstanceOfAdultCoveragePage(testWebDriver);
    assertEquals(value, adultCoveragePage.isOutreachFirstEnabled(rowNumber));
  }

  public void verifyOutreach2To5Enabled(int rowNumber, boolean value) {
    AdultCoveragePage adultCoveragePage = PageFactory.getInstanceOfAdultCoveragePage(testWebDriver);
    assertEquals(value, adultCoveragePage.isOutreach2To5Enabled(rowNumber));
  }

  public void verifyHealthCenterFirstEnabled(int rowNumber, boolean value) {
    AdultCoveragePage adultCoveragePage = PageFactory.getInstanceOfAdultCoveragePage(testWebDriver);
    assertEquals(value, adultCoveragePage.isHealthCenterFirstEnabled(rowNumber));
  }

  public void verifyHealthCenter2To5Enabled(int rowNumber, boolean value) {
    AdultCoveragePage adultCoveragePage = PageFactory.getInstanceOfAdultCoveragePage(testWebDriver);
    assertEquals(value, adultCoveragePage.isHealthCenter2To5Enabled(rowNumber));
  }

  public void verifyOutreachFirstNrSelected(int rowNumber, boolean value) {
    AdultCoveragePage adultCoveragePage = PageFactory.getInstanceOfAdultCoveragePage(testWebDriver);
    assertEquals(value, adultCoveragePage.isOutreachFirstNrSelected(rowNumber));
  }

  public void verifyOutreach2To5NrSelected(int rowNumber, boolean value) {
    AdultCoveragePage adultCoveragePage = PageFactory.getInstanceOfAdultCoveragePage(testWebDriver);
    assertEquals(value, adultCoveragePage.isOutreach2To5NrSelected(rowNumber));
  }

  public void verifyHealthCenterFirstNrSelected(int rowNumber, boolean value) {
    AdultCoveragePage adultCoveragePage = PageFactory.getInstanceOfAdultCoveragePage(testWebDriver);
    assertEquals(value, adultCoveragePage.isHealthCenterFirstNrSelected(rowNumber));
  }

  public void verifyHealthCenter2To5NrSelected(int rowNumber, boolean value) {
    AdultCoveragePage adultCoveragePage = PageFactory.getInstanceOfAdultCoveragePage(testWebDriver);
    assertEquals(value, adultCoveragePage.isHealthCenter2To5NrSelected(rowNumber));
  }

  public void insertProductMappingToGroup() throws SQLException {
    dbWrapper.insertTargetGroupEntityAndProductsInMappingTable("Pregnant Women", "tetanus", false);
    dbWrapper.insertTargetGroupEntityAndProductsInMappingTable("MIF 15-49 years - Community", "tetanus", false);
    dbWrapper.insertTargetGroupEntityAndProductsInMappingTable("MIF 15-49 years - Students", "tetanus", false);
    dbWrapper.insertTargetGroupEntityAndProductsInMappingTable("MIF 15-49 years - Workers", "tetanus", false);
    dbWrapper.insertTargetGroupEntityAndProductsInMappingTable("Students not MIF", "tetanus", false);
    dbWrapper.insertTargetGroupEntityAndProductsInMappingTable("Workers not MIF", "tetanus", false);
    dbWrapper.insertTargetGroupEntityAndProductsInMappingTable("Other not MIF", "tetanus", false);
  }

  @AfterMethod(groups = "distribution")
  public void tearDown() throws SQLException {
    testWebDriver.sleep(500);
    if (!testWebDriver.getElementById("username").isDisplayed()) {
      HomePage homePage = PageFactory.getInstanceOfHomePage(testWebDriver);
      homePage.logout(baseUrlGlobal);
      dbWrapper.deleteData();
      dbWrapper.closeConnection();
    }
    ((JavascriptExecutor) TestWebDriver.getDriver()).executeScript("indexedDB.deleteDatabase('open_lmis');");
  }
}
