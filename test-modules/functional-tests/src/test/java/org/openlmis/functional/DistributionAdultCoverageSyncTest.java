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
import cucumber.api.java.en.When;
import org.openlmis.UiUtils.TestCaseHelper;
import org.openlmis.UiUtils.TestWebDriver;
import org.openlmis.pageobjects.*;
import org.openqa.selenium.JavascriptExecutor;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.io.IOException;
import java.sql.ResultSet;
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
    setupDataForDistributionTest(adultCoverageData);
    dbWrapper.insertProductsForAdultCoverage();
    loginPage = PageObjectFactory.getLoginPage(testWebDriver, baseUrlGlobal);
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
    assertEquals("Opened Vials Wastage Rate", adultCoveragePage.getWastageRateLabel());
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
      assertEquals("", adultCoveragePage.getTargetGroup(rowNumber));
      assertEquals("0", adultCoveragePage.getTotalTetanusFirst(rowNumber));
      assertEquals("0", adultCoveragePage.getTotalTetanus2To5(rowNumber));
      assertEquals("0", adultCoveragePage.getTotalTetanus(rowNumber));
      assertEquals("", adultCoveragePage.getCoverageRate(rowNumber));
      assertEquals("", adultCoveragePage.getOutreachFirstInput(rowNumber));
      assertEquals("", adultCoveragePage.getOutreach2To5Input(rowNumber));
      assertEquals(true, adultCoveragePage.isOutreachFirstEnabled(rowNumber));
      assertEquals(true, adultCoveragePage.isOutreach2To5Enabled(rowNumber));
      assertEquals(false, adultCoveragePage.isOutreachFirstNrSelected(rowNumber));
      assertEquals(false, adultCoveragePage.isOutreach2To5NrSelected(rowNumber));
    }

    assertEquals("", adultCoveragePage.getWastageRate());
    assertEquals("", adultCoveragePage.getOpenedVialInputField());

    assertEquals("", adultCoveragePage.getHealthCenterFirstInput(1));
    assertEquals("", adultCoveragePage.getHealthCenterFirstInput(2));
    assertEquals("", adultCoveragePage.getHealthCenterFirstInput(7));
    assertEquals("", adultCoveragePage.getHealthCenterFirstInput(2));
    assertEquals("", adultCoveragePage.getHealthCenterFirstInput(7));
    assertEquals("", adultCoveragePage.getHealthCenter2To5Input(1));
    assertEquals("", adultCoveragePage.getHealthCenter2To5Input(2));
    assertEquals("", adultCoveragePage.getHealthCenter2To5Input(7));

    assertEquals(true, adultCoveragePage.isHealthCenterFirstEnabled(1));
    assertEquals(true, adultCoveragePage.isHealthCenterFirstEnabled(2));
    assertEquals(true, adultCoveragePage.isHealthCenterFirstEnabled(7));
    assertEquals(true, adultCoveragePage.isHealthCenter2To5Enabled(1));
    assertEquals(true, adultCoveragePage.isHealthCenter2To5Enabled(2));
    assertEquals(true, adultCoveragePage.isHealthCenter2To5Enabled(7));

    assertEquals(false, adultCoveragePage.isHealthCenterFirstNrSelected(1));
    assertEquals(false, adultCoveragePage.isHealthCenterFirstNrSelected(2));
    assertEquals(false, adultCoveragePage.isHealthCenterFirstNrSelected(7));
    assertEquals(false, adultCoveragePage.isHealthCenter2To5NrSelected(1));
    assertEquals(false, adultCoveragePage.isHealthCenter2To5NrSelected(2));
    assertEquals(false, adultCoveragePage.isHealthCenter2To5NrSelected(7));

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
    dbWrapper.updateFieldValue("facilities", "catchmentPopulation", 8989);

    HomePage homePage = loginPage.loginAs(adultCoverageData.get(USER), adultCoverageData.get(PASSWORD));
    DistributionPage distributionPage = homePage.navigateToDistributionWhenOnline();
    distributionPage.initiate(adultCoverageData.get(FIRST_DELIVERY_ZONE_NAME), adultCoverageData.get(VACCINES_PROGRAM));
    FacilityListPage facilityListPage = distributionPage.clickRecordData(1);
    VisitInformationPage visitInformationPage = facilityListPage.selectFacility(adultCoverageData.get(FIRST_FACILITY_CODE));

    facilityListPage.verifyOverallFacilityIndicatorColor("AMBER");

    AdultCoveragePage adultCoveragePage = visitInformationPage.navigateToAdultCoverage();
    adultCoveragePage.verifyIndicator("RED");

    for (int rowNumber = 1; rowNumber <= 7; rowNumber++) {
      assertEquals("30", adultCoveragePage.getTargetGroup(rowNumber));
      assertEquals("0", adultCoveragePage.getCoverageRate(rowNumber));
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
    dbWrapper.updateFieldValue("facilities", "catchmentPopulation", 8989);

    HomePage homePage = loginPage.loginAs(adultCoverageData.get(USER), adultCoverageData.get(PASSWORD));
    DistributionPage distributionPage = homePage.navigateToDistributionWhenOnline();
    distributionPage.initiate(adultCoverageData.get(FIRST_DELIVERY_ZONE_NAME), adultCoverageData.get(VACCINES_PROGRAM));
    FacilityListPage facilityListPage = distributionPage.clickRecordData(1);
    VisitInformationPage visitInformationPage = facilityListPage.selectFacility(adultCoverageData.get(FIRST_FACILITY_CODE));

    facilityListPage.verifyOverallFacilityIndicatorColor("AMBER");

    AdultCoveragePage adultCoveragePage = visitInformationPage.navigateToAdultCoverage();
    adultCoveragePage.verifyIndicator("RED");

    for (int rowNumber = 1; rowNumber <= 7; rowNumber++) {
      assertEquals("2997", adultCoveragePage.getTargetGroup(rowNumber));
      assertEquals("0", adultCoveragePage.getCoverageRate(rowNumber));
    }

    adultCoveragePage.enterOpenedVialInputField("90");
    assertEquals("100", adultCoveragePage.getWastageRate());
  }

  @Test(groups = {"distribution"})
  public void testShouldVerifyTargetGroupAndWastageRateWhenNoCatchmentPopulation() throws SQLException {
    insertProductMappingToGroup();
    dbWrapper.insertAdultCoverageOpenedVialMapping("tetanus");
    dbWrapper.insertProgramProductISA("VACCINES", "tetanus", "45", "12", "3", "4", "4", "2", "4");
    dbWrapper.updateFieldValue("facilities", "catchmentPopulation", null);

    HomePage homePage = loginPage.loginAs(adultCoverageData.get(USER), adultCoverageData.get(PASSWORD));
    DistributionPage distributionPage = homePage.navigateToDistributionWhenOnline();
    distributionPage.initiate(adultCoverageData.get(FIRST_DELIVERY_ZONE_NAME), adultCoverageData.get(VACCINES_PROGRAM));
    FacilityListPage facilityListPage = distributionPage.clickRecordData(1);
    VisitInformationPage visitInformationPage = facilityListPage.selectFacility(adultCoverageData.get(FIRST_FACILITY_CODE));

    facilityListPage.verifyOverallFacilityIndicatorColor("AMBER");

    AdultCoveragePage adultCoveragePage = visitInformationPage.navigateToAdultCoverage();
    adultCoveragePage.verifyIndicator("RED");

    for (int rowNumber = 1; rowNumber <= 7; rowNumber++) {
      assertEquals("", adultCoveragePage.getTargetGroup(rowNumber));
      assertEquals("", adultCoveragePage.getCoverageRate(rowNumber));
    }

    adultCoveragePage.enterOpenedVialInputField("90");
    assertEquals("100", adultCoveragePage.getWastageRate());
  }

  @Test(groups = {"distribution"})
  public void testShouldVerifyTargetGroupAndWastageRateWhenNoWhoRatio() throws SQLException {
    insertProductMappingToGroup();
    dbWrapper.insertAdultCoverageOpenedVialMapping("tetanus");
    dbWrapper.updateFieldValue("facilities", "catchmentPopulation", "8980", "code", adultCoverageData.get(FIRST_FACILITY_CODE));
    HomePage homePage = loginPage.loginAs(adultCoverageData.get(USER), adultCoverageData.get(PASSWORD));
    DistributionPage distributionPage = homePage.navigateToDistributionWhenOnline();
    distributionPage.initiate(adultCoverageData.get(FIRST_DELIVERY_ZONE_NAME), adultCoverageData.get(VACCINES_PROGRAM));
    FacilityListPage facilityListPage = distributionPage.clickRecordData(1);
    VisitInformationPage visitInformationPage = facilityListPage.selectFacility(adultCoverageData.get(FIRST_FACILITY_CODE));

    facilityListPage.verifyOverallFacilityIndicatorColor("AMBER");

    AdultCoveragePage adultCoveragePage = visitInformationPage.navigateToAdultCoverage();
    adultCoveragePage.verifyIndicator("RED");

    for (int rowNumber = 1; rowNumber <= 7; rowNumber++) {
      assertEquals("", adultCoveragePage.getTargetGroup(rowNumber));
      assertEquals("", adultCoveragePage.getCoverageRate(rowNumber));
    }

    adultCoveragePage.enterOpenedVialInputField("90");
    assertEquals("100", adultCoveragePage.getWastageRate());
  }

  @Test(groups = {"distribution"})
  public void testShouldVerifyWastageRateAndTargetGroupForAllGroups() throws SQLException {
    insertProductMappingToGroup();
    dbWrapper.insertAdultCoverageOpenedVialMapping("tetanus");
    dbWrapper.insertProgramProductISA("VACCINES", "tetanus", "0.45", "12", "3", "4", "4", "2", "4");
    dbWrapper.updateFieldValue("facilities", "catchmentPopulation", "8984", "code", adultCoverageData.get(FIRST_FACILITY_CODE));

    HomePage homePage = loginPage.loginAs(adultCoverageData.get(USER), adultCoverageData.get(PASSWORD));
    DistributionPage distributionPage = homePage.navigateToDistributionWhenOnline();
    distributionPage.initiate(adultCoverageData.get(FIRST_DELIVERY_ZONE_NAME), adultCoverageData.get(VACCINES_PROGRAM));
    FacilityListPage facilityListPage = distributionPage.clickRecordData(1);
    VisitInformationPage visitInformationPage = facilityListPage.selectFacility(adultCoverageData.get(FIRST_FACILITY_CODE));

    facilityListPage.verifyOverallFacilityIndicatorColor("AMBER");

    AdultCoveragePage adultCoveragePage = visitInformationPage.navigateToAdultCoverage();
    adultCoveragePage.verifyIndicator("RED");

    for (int rowNumber = 1; rowNumber <= 7; rowNumber++) {
      assertEquals("4", adultCoveragePage.getTargetGroup(rowNumber));
      assertEquals("0", adultCoveragePage.getCoverageRate(rowNumber));
    }

    adultCoveragePage.enterOpenedVialInputField("90");
    assertEquals("100", adultCoveragePage.getWastageRate());

    adultCoveragePage.enterHealthCenterFirstInput(1, "544");
    assertEquals("13600", adultCoveragePage.getCoverageRate(1));
    assertEquals("40", adultCoveragePage.getWastageRate());
  }

  @Test(groups = {"distribution"})
  public void testShouldVerifyTotalAndApplyNrToIndividualField() throws SQLException {
    insertProductMappingToGroup();
    dbWrapper.insertProgramProductISA("VACCINES", "tetanus", "45", "12", "3", "4", "4", "2", "4");
    dbWrapper.updateFieldValue("facilities", "catchmentPopulation", "8984", "code", adultCoverageData.get(FIRST_FACILITY_CODE));

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
    dbWrapper.updateFieldValue("products", "active", false);
    dbWrapper.insertTargetGroupEntityAndProductsInMappingTable("Other not MIF", "tetanus", false);
    dbWrapper.insertTargetGroupEntityAndProductsInMappingTable("abc", "tetanus", false);
    dbWrapper.insertTargetGroupEntityAndProductsInMappingTable("pregnant women", "tetanus", false);
    dbWrapper.insertTargetGroupEntityAndProductsInMappingTable("MIF 15-49 years - Workers", "tetanus", false);
    dbWrapper.insertTargetGroupEntityAndProductsInMappingTable("MIF 15-49 years - Community", "tetanus", false);
    dbWrapper.insertTargetGroupEntityAndProductsInMappingTable("MIF 15-49 years - Students", "tetanus", false);
    dbWrapper.insertTargetGroupEntityAndProductsInMappingTable("Students not MIF", "tetanus", false);
    dbWrapper.insertTargetGroupEntityAndProductsInMappingTable("Workers not MIF", "tetanus", false);

    dbWrapper.insertAdultCoverageOpenedVialMapping("tetanus");
    dbWrapper.insertProgramProductISA("VACCINES", "tetanus", "405", "12", "3", "4", "4", "2", "4");
    dbWrapper.updateFieldValue("facilities", "catchmentPopulation", "89", "code", adultCoverageData.get(FIRST_FACILITY_CODE));

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
      assertEquals("31", adultCoveragePage.getTargetGroup(rowNumber));
    }

    assertEquals("1810387", adultCoveragePage.getCoverageRate(1));
    assertEquals("1810452", adultCoveragePage.getCoverageRate(2));
    assertEquals("1810516", adultCoveragePage.getCoverageRate(3));
    assertEquals("1810581", adultCoveragePage.getCoverageRate(4));
    assertEquals("18103548", adultCoveragePage.getCoverageRate(5));
    assertEquals("18103613", adultCoveragePage.getCoverageRate(6));
    assertEquals("18103677", adultCoveragePage.getCoverageRate(7));

    adultCoveragePage.enterOpenedVialInputField("90");
    assertEquals("-2120051", adultCoveragePage.getWastageRate());
  }

  @Test(groups = {"distribution"})
  public void testShouldVerifyCoverageRateAndWastageRateCalculationWhenMappingRemovedAfterCatching() throws SQLException {
    dbWrapper.updateFieldValue("products", "active", false);
    dbWrapper.insertTargetGroupEntityAndProductsInMappingTable("Other not MIF", "tetanus", false);
    dbWrapper.insertTargetGroupEntityAndProductsInMappingTable("abc", "tetanus", false);
    dbWrapper.insertTargetGroupEntityAndProductsInMappingTable("pregnant women", "tetanus", false);
    dbWrapper.insertTargetGroupEntityAndProductsInMappingTable("MIF 15-49 years - Workers", "tetanus", false);
    dbWrapper.insertTargetGroupEntityAndProductsInMappingTable("MIF 15-49 years - Community", "tetanus", false);
    dbWrapper.insertTargetGroupEntityAndProductsInMappingTable("MIF 15-49 years - Students", "tetanus", false);
    dbWrapper.insertTargetGroupEntityAndProductsInMappingTable("Students not MIF", "tetanus", false);
    dbWrapper.insertTargetGroupEntityAndProductsInMappingTable("Workers not MIF", "tetanus", false);

    dbWrapper.insertAdultCoverageOpenedVialMapping("tetanus");
    dbWrapper.insertProgramProductISA("VACCINES", "tetanus", "405", "12", "3", "4", "4", "2", "4");
    dbWrapper.updateFieldValue("facilities", "catchmentPopulation", "89", "code", adultCoverageData.get(FIRST_FACILITY_CODE));

    HomePage homePage = loginPage.loginAs(adultCoverageData.get(USER), adultCoverageData.get(PASSWORD));
    DistributionPage distributionPage = homePage.navigateToDistributionWhenOnline();
    distributionPage.initiate(adultCoverageData.get(FIRST_DELIVERY_ZONE_NAME), adultCoverageData.get(VACCINES_PROGRAM));
    FacilityListPage facilityListPage = distributionPage.clickRecordData(1);
    VisitInformationPage visitInformationPage = facilityListPage.selectFacility(adultCoverageData.get(FIRST_FACILITY_CODE));

    facilityListPage.verifyOverallFacilityIndicatorColor("AMBER");

    dbWrapper.deleteRowFromTable("coverage_target_group_products", "childCoverage", "false");
    dbWrapper.deleteRowFromTable("coverage_product_vials", "childCoverage", "false");

    AdultCoveragePage adultCoveragePage = visitInformationPage.navigateToAdultCoverage();
    adultCoveragePage.verifyIndicator("RED");

    for (int rowNumber = 1; rowNumber <= 7; rowNumber++) {
      adultCoveragePage.enterOutreachFirstInput(rowNumber, "5612" + (rowNumber * 20));
      assertEquals("31", adultCoveragePage.getTargetGroup(rowNumber));
    }

    assertEquals("1810387", adultCoveragePage.getCoverageRate(1));
    assertEquals("1810452", adultCoveragePage.getCoverageRate(2));
    assertEquals("1810516", adultCoveragePage.getCoverageRate(3));
    assertEquals("1810581", adultCoveragePage.getCoverageRate(4));
    assertEquals("18103548", adultCoveragePage.getCoverageRate(5));
    assertEquals("18103613", adultCoveragePage.getCoverageRate(6));
    assertEquals("18103677", adultCoveragePage.getCoverageRate(7));

    adultCoveragePage.enterOpenedVialInputField("90");
    assertEquals("-2120051", adultCoveragePage.getWastageRate());
  }

  @Test(groups = {"distribution"})
  public void testShouldVerifyCoverageAndWastageRateCalculationWhenCatchmentPopulationZero() throws SQLException {
    dbWrapper.updateFieldValue("products", "packSize", 11);
    insertProductMappingToGroup();
    dbWrapper.insertAdultCoverageOpenedVialMapping("tetanus");
    dbWrapper.insertProgramProductISA("VACCINES", "tetanus", "45", "12", "3", "4", "4", "2", "4");
    dbWrapper.updateFieldValue("facilities", "catchmentPopulation", "0", "code", adultCoverageData.get(FIRST_FACILITY_CODE));

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
      assertEquals("0", adultCoveragePage.getTargetGroup(rowNumber));
      assertEquals("", adultCoveragePage.getCoverageRate(rowNumber));
    }

    adultCoveragePage.enterOpenedVialInputField("1234567");
    assertEquals("97", adultCoveragePage.getWastageRate());
  }

  @Test(groups = {"distribution"})
  public void testShouldVerifyCoverageAndWastageRateCalculationWhenPackSizeChangedAfterInitiatingDistribution() throws SQLException {
    dbWrapper.updateFieldValue("products", "packSize", 10);
    insertProductMappingToGroup();
    dbWrapper.insertAdultCoverageOpenedVialMapping("tetanus");
    dbWrapper.insertProgramProductISA("VACCINES", "tetanus", "45", "12", "3", "4", "4", "2", "4");
    dbWrapper.updateFieldValue("facilities", "catchmentPopulation", "0", "code", adultCoverageData.get(FIRST_FACILITY_CODE));

    HomePage homePage = loginPage.loginAs(adultCoverageData.get(USER), adultCoverageData.get(PASSWORD));
    DistributionPage distributionPage = homePage.navigateToDistributionWhenOnline();
    distributionPage.initiate(adultCoverageData.get(FIRST_DELIVERY_ZONE_NAME), adultCoverageData.get(VACCINES_PROGRAM));
    FacilityListPage facilityListPage = distributionPage.clickRecordData(1);
    dbWrapper.updateFieldValue("products", "packSize", 15);
    VisitInformationPage visitInformationPage = facilityListPage.selectFacility(adultCoverageData.get(FIRST_FACILITY_CODE));

    facilityListPage.verifyOverallFacilityIndicatorColor("AMBER");

    AdultCoveragePage adultCoveragePage = visitInformationPage.navigateToAdultCoverage();
    adultCoveragePage.verifyIndicator("RED");

    for (int rowNumber = 1; rowNumber <= 7; rowNumber++) {
      adultCoveragePage.enterOutreachFirstInput(rowNumber, "5612" + rowNumber);
    }

    for (int rowNumber = 1; rowNumber <= 7; rowNumber++) {
      assertEquals("0", adultCoveragePage.getTargetGroup(rowNumber));
      assertEquals("", adultCoveragePage.getCoverageRate(rowNumber));
    }

    adultCoveragePage.enterOpenedVialInputField("1234567");
    assertEquals("97", adultCoveragePage.getWastageRate());
  }

  @Test(groups = {"distribution"})
  public void testShouldVerifyFormStatusAndApplyNrToAll() throws SQLException {
    dbWrapper.updateFieldValue("products", "packSize", 10);
    insertProductMappingToGroup();
    dbWrapper.insertAdultCoverageOpenedVialMapping("tetanus");
    dbWrapper.insertProgramProductISA("VACCINES", "tetanus", "45", "12", "3", "4", "4", "2", "4");
    dbWrapper.updateFieldValue("facilities", "catchmentPopulation", "0", "code", adultCoverageData.get(FIRST_FACILITY_CODE));

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

    adultCoveragePage.enterOpenedVialInputField("1234567");
    assertEquals("97", adultCoveragePage.getWastageRate());

    adultCoveragePage.verifyIndicator("AMBER");

    adultCoveragePage.clickApplyNrToAll();
    adultCoveragePage.clickCancel();

    adultCoveragePage.verifyIndicator("AMBER");
    assertTrue(adultCoveragePage.isOutreachFirstEnabled(4));
    assertTrue(adultCoveragePage.isOpenedVialsEnabled());
    assertEquals("97", adultCoveragePage.getWastageRate());
    assertEquals("56123", adultCoveragePage.getOutreachFirstInput(3));
    assertEquals("1234567", adultCoveragePage.getOpenedVialInputField());

    adultCoveragePage.clickApplyNrToAll();
    adultCoveragePage.clickOK();

    adultCoveragePage.verifyIndicator("GREEN");
    adultCoveragePage.verifyAllFieldsDisabled();
    assertEquals("", adultCoveragePage.getWastageRate());
    assertEquals("", adultCoveragePage.getOutreachFirstInput(3));
    assertEquals("", adultCoveragePage.getOpenedVialInputField());

    adultCoveragePage.clickApplyNrToAll();
    adultCoveragePage.clickOK();

    adultCoveragePage.verifyIndicator("GREEN");

    adultCoveragePage.clickApplyNrToAll();
    adultCoveragePage.clickCancel();

    adultCoveragePage.verifyIndicator("GREEN");
    adultCoveragePage.verifyAllFieldsDisabled();

    adultCoveragePage.applyNrToOpenedVials();
    adultCoveragePage.verifyIndicator("AMBER");
    adultCoveragePage.enterOpenedVialInputField("23");
    adultCoveragePage.verifyIndicator("GREEN");
    assertEquals("100", adultCoveragePage.getWastageRate());
  }

  @Test(groups = {"distribution"})
  public void testShouldVerifyFacilityStatusAndApplyNrToAllSync() throws SQLException {
    dbWrapper.addRefrigeratorToFacility("LG", "800L", "GNR7878", "F10");
    dbWrapper.insertProductGroup("PG-1");
    dbWrapper.insertProductWithGroup("Product5", "ProductName5", "PG-1", true);
    dbWrapper.insertProgramProduct("Product5", "VACCINES", "10", "true");

    insertProductMappingToGroup();
    dbWrapper.insertAdultCoverageOpenedVialMapping("tetanus");
    dbWrapper.insertProgramProductISA("VACCINES", "tetanus", "45", "12", "3", "4", "4", "2", "4");

    HomePage homePage = loginPage.loginAs(adultCoverageData.get(USER), adultCoverageData.get(PASSWORD));
    DistributionPage distributionPage = homePage.navigateToDistributionWhenOnline();
    distributionPage.initiate(adultCoverageData.get(FIRST_DELIVERY_ZONE_NAME), adultCoverageData.get(VACCINES_PROGRAM));
    FacilityListPage facilityListPage = distributionPage.clickRecordData(1);
    VisitInformationPage visitInformationPage = facilityListPage.selectFacility(adultCoverageData.get(FIRST_FACILITY_CODE));

    facilityListPage.verifyOverallFacilityIndicatorColor("RED");

    AdultCoveragePage adultCoveragePage = visitInformationPage.navigateToAdultCoverage();
    adultCoveragePage.verifyIndicator("RED");

    adultCoveragePage.enterOutreachFirstInput(1, "5612");

    adultCoveragePage.verifyIndicator("AMBER");
    facilityListPage.verifyOverallFacilityIndicatorColor("AMBER");

    adultCoveragePage.navigateToVisitInformation();
    visitInformationPage.enterDataWhenFacilityVisited("CN", "CT", "VN", "VT");

    EPIUsePage epiUsePage = visitInformationPage.navigateToEpiUse();
    epiUsePage.checkApplyNRToAllFields(true);

    RefrigeratorPage refrigeratorPage = visitInformationPage.navigateToRefrigerators();
    refrigeratorPage.clickDelete();
    refrigeratorPage.clickOKButton();

    EpiInventoryPage epiInventoryPage = refrigeratorPage.navigateToEpiInventory();
    epiInventoryPage.applyNRToAll();
    epiInventoryPage.fillDeliveredQuantity(1, "1");
    epiInventoryPage.fillDeliveredQuantity(2, "2");
    epiInventoryPage.fillDeliveredQuantity(3, "3");
    epiInventoryPage.fillDeliveredQuantity(4, "4");

    FullCoveragePage fullCoveragePage = epiInventoryPage.navigateToFullCoverage();
    fullCoveragePage.clickApplyNRToAll();

    ChildCoveragePage childCoveragePage = fullCoveragePage.navigateToChildCoverage();
    childCoveragePage.applyNRToAll();
    childCoveragePage.clickOK();

    facilityListPage.verifyOverallFacilityIndicatorColor("AMBER");

    childCoveragePage.navigateToAdultCoverage();
    adultCoveragePage.enterDataInAllFields();

    adultCoveragePage.verifyIndicator("GREEN");
    facilityListPage.verifyOverallFacilityIndicatorColor("GREEN");

    adultCoveragePage.navigateToRefrigerators();
    refrigeratorPage.navigateToAdultCoverage();
    adultCoveragePage.verifyIndicator("GREEN");

    adultCoveragePage.clickApplyNrToAll();
    adultCoveragePage.clickOK();
    adultCoveragePage.navigateToChildCoverage();
    childCoveragePage.navigateToAdultCoverage();
    adultCoveragePage.verifyIndicator("GREEN");
    adultCoveragePage.verifyAllFieldsDisabled();
    facilityListPage.verifyOverallFacilityIndicatorColor("GREEN");

    homePage.navigateToDistributionWhenOnline();
    distributionPage.syncDistribution(1);
    assertTrue(distributionPage.getSyncMessage().contains("F10-Village Dispensary"));
    distributionPage.syncDistributionMessageDone();

    verifyAdultCoverageDataNullInDatabase(adultCoverageData.get(FIRST_FACILITY_CODE));
  }

  @Test(groups = {"distribution"})
  public void testShouldVerifyFacilityStatusAndSyncWhenAllFieldsFilled() throws SQLException {
    insertProductMappingToGroup();
    dbWrapper.insertAdultCoverageOpenedVialMapping("tetanus");
    dbWrapper.insertProgramProductISA("VACCINES", "tetanus", "45", "12", "3", "4", "4", "2", "4");

    HomePage homePage = loginPage.loginAs(adultCoverageData.get(USER), adultCoverageData.get(PASSWORD));
    DistributionPage distributionPage = homePage.navigateToDistributionWhenOnline();
    distributionPage.initiate(adultCoverageData.get(FIRST_DELIVERY_ZONE_NAME), adultCoverageData.get(VACCINES_PROGRAM));
    FacilityListPage facilityListPage = distributionPage.clickRecordData(1);
    VisitInformationPage visitInformationPage = facilityListPage.selectFacility(adultCoverageData.get(FIRST_FACILITY_CODE));

    facilityListPage.verifyOverallFacilityIndicatorColor("AMBER");

    AdultCoveragePage adultCoveragePage = visitInformationPage.navigateToAdultCoverage();
    adultCoveragePage.verifyIndicator("RED");

    adultCoveragePage.applyNrToOpenedVials();

    adultCoveragePage.verifyIndicator("AMBER");
    facilityListPage.verifyOverallFacilityIndicatorColor("AMBER");
    adultCoveragePage.applyNrToOpenedVials();

    adultCoveragePage.navigateToVisitInformation();
    visitInformationPage.selectFacilityVisitedNo();
    visitInformationPage.selectReasonBadWeather();

    FullCoveragePage fullCoveragePage = visitInformationPage.navigateToFullCoverage();
    fullCoveragePage.clickApplyNRToAll();

    ChildCoveragePage childCoveragePage = fullCoveragePage.navigateToChildCoverage();
    childCoveragePage.applyNRToAll();
    childCoveragePage.clickOK();

    facilityListPage.verifyOverallFacilityIndicatorColor("AMBER");

    childCoveragePage.navigateToAdultCoverage();
    adultCoveragePage.enterDataInAllFields();

    adultCoveragePage.verifyIndicator("GREEN");
    facilityListPage.verifyOverallFacilityIndicatorColor("GREEN");

    adultCoveragePage.navigateToFullCoverage();
    fullCoveragePage.navigateToAdultCoverage();
    adultCoveragePage.verifyIndicator("GREEN");

    adultCoveragePage.navigateToChildCoverage();
    childCoveragePage.navigateToAdultCoverage();
    adultCoveragePage.verifyIndicator("GREEN");
    facilityListPage.verifyOverallFacilityIndicatorColor("GREEN");

    homePage.navigateToDistributionWhenOnline();
    distributionPage.syncDistribution(1);
    assertTrue(distributionPage.getSyncMessage().contains("F10-Village Dispensary"));
    distributionPage.syncDistributionMessageDone();

    verifyAdultCoverageDataInDatabase(adultCoverageData.get(FIRST_FACILITY_CODE));
  }

  @Test(groups = {"distribution"})
  public void testShouldVerifyFacilityStatusAndSyncWhenFewFieldsNR() throws SQLException {
    insertProductMappingToGroup();
    dbWrapper.insertAdultCoverageOpenedVialMapping("tetanus");
    dbWrapper.insertProgramProductISA("VACCINES", "tetanus", "45", "12", "3", "4", "4", "2", "4");

    HomePage homePage = loginPage.loginAs(adultCoverageData.get(USER), adultCoverageData.get(PASSWORD));
    DistributionPage distributionPage = homePage.navigateToDistributionWhenOnline();
    distributionPage.initiate(adultCoverageData.get(FIRST_DELIVERY_ZONE_NAME), adultCoverageData.get(VACCINES_PROGRAM));
    FacilityListPage facilityListPage = distributionPage.clickRecordData(1);
    VisitInformationPage visitInformationPage = facilityListPage.selectFacility(adultCoverageData.get(FIRST_FACILITY_CODE));

    facilityListPage.verifyOverallFacilityIndicatorColor("AMBER");

    AdultCoveragePage adultCoveragePage = visitInformationPage.navigateToAdultCoverage();
    adultCoveragePage.verifyIndicator("RED");

    adultCoveragePage.applyNrToOpenedVials();

    adultCoveragePage.verifyIndicator("AMBER");
    facilityListPage.verifyOverallFacilityIndicatorColor("AMBER");
    adultCoveragePage.applyNrToOpenedVials();

    adultCoveragePage.navigateToVisitInformation();
    visitInformationPage.selectFacilityVisitedNo();
    visitInformationPage.selectReasonBadWeather();

    FullCoveragePage fullCoveragePage = visitInformationPage.navigateToFullCoverage();
    fullCoveragePage.clickApplyNRToAll();

    ChildCoveragePage childCoveragePage = fullCoveragePage.navigateToChildCoverage();
    childCoveragePage.applyNRToAll();
    childCoveragePage.clickOK();

    facilityListPage.verifyOverallFacilityIndicatorColor("AMBER");

    homePage.navigateToDistributionWhenOnline();
    distributionPage.clickSyncDistribution(1);
    distributionPage.clickRecordData(1);
    facilityListPage.selectFacility(adultCoverageData.get(FIRST_FACILITY_CODE));

    visitInformationPage.navigateToAdultCoverage();
    adultCoveragePage.enterDataInAllFields();

    adultCoveragePage.verifyIndicator("GREEN");
    facilityListPage.verifyOverallFacilityIndicatorColor("GREEN");
    adultCoveragePage.applyNrToOpenedVials();
    adultCoveragePage.applyHealthCenter2To5Nr(7);
    adultCoveragePage.applyHealthCenterFirstNr(7);

    adultCoveragePage.verifyIndicator("GREEN");
    facilityListPage.verifyOverallFacilityIndicatorColor("GREEN");

    homePage.navigateToDistributionWhenOnline();
    distributionPage.syncDistribution(1);
    assertTrue(distributionPage.getSyncMessage().contains("F10-Village Dispensary"));
    distributionPage.syncDistributionMessageDone();

    String facilityId = dbWrapper.getAttributeFromTable("facilities", "id", "code", "F10");
    String facilityVisitId = dbWrapper.getAttributeFromTable("facility_visits", "id", "facilityId", facilityId);

    List<String> demographicGroups = asList("Pregnant Women", "MIF 15-49 years - Community", "MIF 15-49 years - Students",
      "MIF 15-49 years - Workers", "Students not MIF", "Workers not MIF", "Other not MIF");

    for (int rowNumber = 1; rowNumber <= 7; rowNumber++) {
      ResultSet adultCoverageDetails = dbWrapper.getAdultCoverageDetails(demographicGroups.get(rowNumber - 1), facilityVisitId);
      assertEquals(adultCoverageDetails.getString("outreachTetanus1"), "2" + rowNumber);
      assertEquals(adultCoverageDetails.getString("outreachTetanus2To5"), "4" + rowNumber);
      if (rowNumber < 3) {
        assertEquals(adultCoverageDetails.getString("healthCenterTetanus1"), "1" + rowNumber);
        assertEquals(adultCoverageDetails.getString("healthCenterTetanus2To5"), "3" + rowNumber);
      } else if (rowNumber == 7) {
        assertEquals(adultCoverageDetails.getString("healthCenterTetanus1"), (String) null);
        assertEquals(adultCoverageDetails.getString("healthCenterTetanus2To5"), (String) null);
      }
    }
    ResultSet adultOpenedVialLineItem = dbWrapper.getAdultOpenedVialLineItem(facilityVisitId);
    assertEquals(adultOpenedVialLineItem.getString("openedVials"), (String) null);
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

  @When("^I apply NR to outreach2To5 for rowNumber \"([^\"]*)\"$")
  public void applyNrToHealthCenter11(String rowNumber) {
    AdultCoveragePage adultCoveragePage = PageObjectFactory.getAdultCoveragePage(testWebDriver);
    adultCoveragePage.applyOutreach2To5Nr(Integer.parseInt(rowNumber));
  }

  @And("^I enter outreach2To5 for rowNumber \"([^\"]*)\" as \"([^\"]*)\"$")
  public void enterHealthCenter11Data(String rowNumber, String value) {
    AdultCoveragePage adultCoveragePage = PageObjectFactory.getAdultCoveragePage(testWebDriver);
    adultCoveragePage.enterOutreach2To5Input(Integer.parseInt(rowNumber), value);
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
