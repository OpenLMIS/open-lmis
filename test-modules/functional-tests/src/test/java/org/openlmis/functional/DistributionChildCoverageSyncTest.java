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

import static com.thoughtworks.selenium.SeleneseTestBase.*;
import static java.util.Arrays.asList;


public class DistributionChildCoverageSyncTest extends TestCaseHelper {

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

  public final Map<String, String> childCoverageData = new HashMap<String, String>() {{
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
    setupDataForDistributionTest(childCoverageData);
    dbWrapper.insertProductsForChildCoverage();
    insertRegimenProductMapping();
    loginPage = PageObjectFactory.getLoginPage(testWebDriver, baseUrlGlobal);
  }

  @Test(groups = {"distribution"})
  public void testShouldVerifyAllLabels() {
    HomePage homePage = loginPage.loginAs(childCoverageData.get(USER), childCoverageData.get(PASSWORD));
    DistributionPage distributionPage = homePage.navigateToDistributionWhenOnline();
    distributionPage.initiate(childCoverageData.get(FIRST_DELIVERY_ZONE_NAME), childCoverageData.get(VACCINES_PROGRAM));
    FacilityListPage facilityListPage = distributionPage.clickRecordData(1);
    VisitInformationPage visitInformationPage = facilityListPage.selectFacility(childCoverageData.get(FIRST_FACILITY_CODE));

    facilityListPage.verifyOverallFacilityIndicatorColor("AMBER");

    ChildCoveragePage childCoveragePage = visitInformationPage.navigateToChildCoverage();
    childCoveragePage.verifyIndicator("RED");

    verifyRegimensPresent();
    verifyHeadersPresent();
    verifyOpenVialsPresent();
  }

  @Test(groups = {"distribution"})
  public void testShouldVerifyTargetGroupAndCoverageRateIfCatchmentPopulationAndWhoRatioPresent() throws SQLException {
    HomePage homePage = loginPage.loginAs("Admin123", "Admin123");
    ProgramProductISAPage programProductISAPage = homePage.navigateProgramProductISA();
    programProductISAPage.fillProgramProductISA(childCoverageData.get(VACCINES_PROGRAM), "90", "1", "50", "30", "0", "100", "2000", "333");
    homePage.logout();

    loginPage.loginAs(childCoverageData.get(USER), childCoverageData.get(PASSWORD));
    DistributionPage distributionPage = homePage.navigateToDistributionWhenOnline();
    distributionPage.initiate(childCoverageData.get(FIRST_DELIVERY_ZONE_NAME), childCoverageData.get(VACCINES_PROGRAM));
    FacilityListPage facilityListPage = distributionPage.clickRecordData(1);
    VisitInformationPage visitInformationPage = facilityListPage.selectFacility(childCoverageData.get(FIRST_FACILITY_CODE));

    ChildCoveragePage childCoveragePage = visitInformationPage.navigateToChildCoverage();
    assertEquals(childCoveragePage.getTextOfTargetGroupValue(9), "25");
    assertEquals(childCoveragePage.getTextOfTargetGroupValue(10), "25");
    assertEquals(childCoveragePage.getTextOfTargetGroupValue(11), "25");
    assertEquals(childCoveragePage.getTextOfTargetGroupValue(1), "");
    assertEquals(childCoveragePage.getTextOfTargetGroupValue(12), "");

    String facilityId = dbWrapper.getAttributeFromTable("facilities", "id", "code", "F10");
    String facilityVisitId = dbWrapper.getAttributeFromTable("facility_visits", "id", "facilityId", facilityId);

    ResultSet childCoverageDetails = dbWrapper.getChildCoverageDetails(childCoveragePage.getTextOfRegimenPCV10Dose1(), facilityVisitId);

    assertEquals("25", childCoverageDetails.getInt("targetGroup"));

    assertEquals("0", childCoveragePage.getCoverageRateForGivenRow(9));
    assertEquals("0", childCoveragePage.getCoverageRateForGivenRow(10));
    assertEquals("0", childCoveragePage.getCoverageRateForGivenRow(11));
    assertEquals("", childCoveragePage.getCoverageRateForGivenRow(1));
    assertEquals("", childCoveragePage.getCoverageRateForGivenRow(12));

    childCoveragePage.enterOutreach11MonthsDataForGivenRow(9, "10");
    assertEquals("40", childCoveragePage.getCoverageRateForGivenRow(9));
    assertEquals("0", childCoveragePage.getCoverageRateForGivenRow(10));
    assertEquals("0", childCoveragePage.getCoverageRateForGivenRow(11));
    assertEquals("", childCoveragePage.getCoverageRateForGivenRow(1));
    assertEquals("", childCoveragePage.getCoverageRateForGivenRow(12));

    childCoveragePage.enterHealthCenter11MonthsDataForGivenRow(9, "19");
    assertEquals("116", childCoveragePage.getCoverageRateForGivenRow(9));
    assertEquals("0", childCoveragePage.getCoverageRateForGivenRow(10));
    assertEquals("0", childCoveragePage.getCoverageRateForGivenRow(11));
    assertEquals("", childCoveragePage.getCoverageRateForGivenRow(1));
    assertEquals("", childCoveragePage.getCoverageRateForGivenRow(12));
  }

  @Test(groups = {"distribution"})
  public void testShouldVerifyTargetGroupAndCoverageRateIfOnlyCatchmentPopulationPresent() {
    HomePage homePage = loginPage.loginAs(childCoverageData.get(USER), childCoverageData.get(PASSWORD));
    DistributionPage distributionPage = homePage.navigateToDistributionWhenOnline();
    distributionPage.initiate(childCoverageData.get(FIRST_DELIVERY_ZONE_NAME), childCoverageData.get(VACCINES_PROGRAM));
    FacilityListPage facilityListPage = distributionPage.clickRecordData(1);
    VisitInformationPage visitInformationPage = facilityListPage.selectFacility(childCoverageData.get(FIRST_FACILITY_CODE));

    ChildCoveragePage childCoveragePage = visitInformationPage.navigateToChildCoverage();
    assertEquals(childCoveragePage.getTextOfTargetGroupValue(9), "");
    assertEquals(childCoveragePage.getTextOfTargetGroupValue(10), "");
    assertEquals(childCoveragePage.getTextOfTargetGroupValue(11), "");
    assertEquals(childCoveragePage.getTextOfTargetGroupValue(1), "");
    assertEquals(childCoveragePage.getTextOfTargetGroupValue(12), "");

    assertEquals("", childCoveragePage.getCoverageRateForGivenRow(9));
    assertEquals("", childCoveragePage.getCoverageRateForGivenRow(10));
    assertEquals("", childCoveragePage.getCoverageRateForGivenRow(11));
    assertEquals("", childCoveragePage.getCoverageRateForGivenRow(1));
    assertEquals("", childCoveragePage.getCoverageRateForGivenRow(12));

    childCoveragePage.enterHealthCenter11MonthsDataForGivenRow(9, "19");
    childCoveragePage.enterOutreach11MonthsDataForGivenRow(9, "10");
    assertEquals("", childCoveragePage.getCoverageRateForGivenRow(9));
  }

  @Test(groups = {"distribution"})
  public void testShouldVerifyTargetGroupAndCoverageRateIfOnlyWhoRatioPresent() {
    HomePage homePage = loginPage.loginAs("Admin123", "Admin123");
    ProgramProductISAPage programProductISAPage = homePage.navigateProgramProductISA();
    programProductISAPage.fillProgramProductISA(childCoverageData.get(VACCINES_PROGRAM), "90", "1", "50", "30", "0", "100", "2000", "333");

    FacilityPage facilityPage = homePage.navigateManageFacility();
    homePage.navigateManageFacility();
    facilityPage.searchFacility("F10");
    facilityPage.clickFirstFacilityList();
    facilityPage.editPopulation("");
    facilityPage.saveFacility();
    homePage.logout();

    loginPage.loginAs(childCoverageData.get(USER), childCoverageData.get(PASSWORD));

    DistributionPage distributionPage = homePage.navigateToDistributionWhenOnline();
    distributionPage.initiate(childCoverageData.get(FIRST_DELIVERY_ZONE_NAME), childCoverageData.get(VACCINES_PROGRAM));
    FacilityListPage facilityListPage = distributionPage.clickRecordData(1);
    VisitInformationPage visitInformationPage = facilityListPage.selectFacility(childCoverageData.get(FIRST_FACILITY_CODE));

    ChildCoveragePage childCoveragePage = visitInformationPage.navigateToChildCoverage();
    assertEquals(childCoveragePage.getTextOfTargetGroupValue(9), "");
    assertEquals(childCoveragePage.getTextOfTargetGroupValue(10), "");
    assertEquals(childCoveragePage.getTextOfTargetGroupValue(11), "");
    assertEquals(childCoveragePage.getTextOfTargetGroupValue(1), "");
    assertEquals(childCoveragePage.getTextOfTargetGroupValue(12), "");

    assertEquals("", childCoveragePage.getCoverageRateForGivenRow(9));
    assertEquals("", childCoveragePage.getCoverageRateForGivenRow(10));
    assertEquals("", childCoveragePage.getCoverageRateForGivenRow(11));
    assertEquals("", childCoveragePage.getCoverageRateForGivenRow(1));
    assertEquals("", childCoveragePage.getCoverageRateForGivenRow(12));

    childCoveragePage.enterHealthCenter11MonthsDataForGivenRow(9, "19");
    childCoveragePage.enterOutreach11MonthsDataForGivenRow(9, "10");
    assertEquals("", childCoveragePage.getCoverageRateForGivenRow(9));
  }

  @Test(groups = {"distribution"})
  public void testShouldVerifyAllRegimensWithTargetValuePopulatedAndCoverageRateWhenRegimenMappedToProductInactiveAtGlobalLevel() throws SQLException {
    dbWrapper.updateFieldValue("products", "active", "f", "code", "P10");

    HomePage homePage = loginPage.loginAs("Admin123", "Admin123");
    ProgramProductISAPage programProductISAPage = homePage.navigateProgramProductISA();
    programProductISAPage.fillProgramProductISA(childCoverageData.get(VACCINES_PROGRAM), "0", "1", "50", "30", "0", "100", "2000", "333");
    homePage.logout();

    homePage = loginPage.loginAs(childCoverageData.get(USER), childCoverageData.get(PASSWORD));
    DistributionPage distributionPage = homePage.navigateToDistributionWhenOnline();
    distributionPage.initiate(childCoverageData.get(FIRST_DELIVERY_ZONE_NAME), childCoverageData.get(VACCINES_PROGRAM));
    FacilityListPage facilityListPage = distributionPage.clickRecordData(1);
    VisitInformationPage visitInformationPage = facilityListPage.selectFacility(childCoverageData.get(FIRST_FACILITY_CODE));
    ChildCoveragePage childCoveragePage = visitInformationPage.navigateToChildCoverage();

    verifyRegimensPresent();
    verifyOpenVialsPresent();
    assertEquals(childCoveragePage.getTextOfTargetGroupValue(9), "0");
    assertEquals(childCoveragePage.getTextOfTargetGroupValue(10), "0");
    assertEquals(childCoveragePage.getTextOfTargetGroupValue(11), "0");
    assertEquals(childCoveragePage.getTextOfTargetGroupValue(1), "");
    assertEquals(childCoveragePage.getTextOfTargetGroupValue(12), "");

    dbWrapper.updateFieldValue("products", "active", "t", "code", "P10");

    assertEquals("", childCoveragePage.getCoverageRateForGivenRow(9));
    assertEquals("", childCoveragePage.getCoverageRateForGivenRow(10));
    assertEquals("", childCoveragePage.getCoverageRateForGivenRow(11));
    assertEquals("", childCoveragePage.getCoverageRateForGivenRow(1));
    assertEquals("", childCoveragePage.getCoverageRateForGivenRow(12));

    childCoveragePage.enterHealthCenter11MonthsDataForGivenRow(9, "19");
    childCoveragePage.enterOutreach11MonthsDataForGivenRow(9, "10");
    assertEquals("", childCoveragePage.getCoverageRateForGivenRow(9));
  }

  @Test(groups = {"distribution"})
  public void testShouldVerifyAllRegimensWithTargetValuePopulatedAndCoverageRateWhenRegimenMappedToProductInactiveAtProgramLevel() throws SQLException {
    String productId = dbWrapper.getAttributeFromTable("products", "id", "code", "P10");
    dbWrapper.updateFieldValue("program_products", "active", "f", "productId", productId);

    HomePage homePage = loginPage.loginAs("Admin123", "Admin123");
    ProgramProductISAPage programProductISAPage = homePage.navigateProgramProductISA();
    programProductISAPage.fillProgramProductISA(childCoverageData.get(VACCINES_PROGRAM), "58", "1", "50", "30", "0", "100", "2000", "333");
    dbWrapper.updateFieldValue("facilities", "catchmentPopulation", "297", "code", childCoverageData.get(FIRST_FACILITY_CODE));
    homePage.logout();

    homePage = loginPage.loginAs(childCoverageData.get(USER), childCoverageData.get(PASSWORD));
    DistributionPage distributionPage = homePage.navigateToDistributionWhenOnline();
    distributionPage.initiate(childCoverageData.get(FIRST_DELIVERY_ZONE_NAME), childCoverageData.get(VACCINES_PROGRAM));
    FacilityListPage facilityListPage = distributionPage.clickRecordData(1);
    VisitInformationPage visitInformationPage = facilityListPage.selectFacility(childCoverageData.get(FIRST_FACILITY_CODE));
    ChildCoveragePage childCoveragePage = visitInformationPage.navigateToChildCoverage();

    verifyRegimensPresent();
    verifyOpenVialsPresent();
    assertEquals(childCoveragePage.getTextOfTargetGroupValue(9), "15");
    assertEquals(childCoveragePage.getTextOfTargetGroupValue(10), "15");
    assertEquals(childCoveragePage.getTextOfTargetGroupValue(11), "15");
    assertEquals(childCoveragePage.getTextOfTargetGroupValue(1), "");
    assertEquals(childCoveragePage.getTextOfTargetGroupValue(2), "");

    assertEquals("0", childCoveragePage.getCoverageRateForGivenRow(9));
    assertEquals("0", childCoveragePage.getCoverageRateForGivenRow(10));
    assertEquals("0", childCoveragePage.getCoverageRateForGivenRow(11));
    assertEquals("", childCoveragePage.getCoverageRateForGivenRow(1));
    assertEquals("", childCoveragePage.getCoverageRateForGivenRow(12));

    childCoveragePage.enterHealthCenter11MonthsDataForGivenRow(9, "19");
    childCoveragePage.enterOutreach11MonthsDataForGivenRow(9, "10");
    assertEquals("193", childCoveragePage.getCoverageRateForGivenRow(9));

    childCoveragePage.enterHealthCenter11MonthsDataForGivenRow(9, "162");
    assertEquals("1147", childCoveragePage.getCoverageRateForGivenRow(9));

    dbWrapper.updateFieldValue("program_products", "active", "t", "productId", productId);
  }

  @Test(groups = {"distribution"})
  public void testShouldVerifyAllRegimensWhenMoreThan12RegimensInMappingTable() throws SQLException {
    dbWrapper.insertTargetGroupEntityAndProductsInMappingTable("Antibiotic", "BCG", true);
    dbWrapper.insertTargetGroupEntityAndProductsInMappingTable("Glycerine", "P11", true);
    dbWrapper.insertTargetGroupEntityAndProductsInMappingTable("Paracetamol", "P10", true);

    HomePage homePage = loginPage.loginAs(childCoverageData.get(USER), childCoverageData.get(PASSWORD));
    DistributionPage distributionPage = homePage.navigateToDistributionWhenOnline();
    distributionPage.initiate(childCoverageData.get(FIRST_DELIVERY_ZONE_NAME), childCoverageData.get(VACCINES_PROGRAM));
    FacilityListPage facilityListPage = distributionPage.clickRecordData(1);
    VisitInformationPage visitInformationPage = facilityListPage.selectFacility(childCoverageData.get(FIRST_FACILITY_CODE));
    ChildCoveragePage childCoveragePage = visitInformationPage.navigateToChildCoverage();

    verifyRegimensPresent();
    verifyOpenVialsPresent();
    assertFalse(childCoveragePage.getTextOfChildCoverageTable().contains("Antibiotic"));
    assertFalse(childCoveragePage.getTextOfChildCoverageTable().contains("Glycerine"));
    assertFalse(childCoveragePage.getTextOfChildCoverageTable().contains("Paracetamol"));
  }

  @Test(groups = {"distribution"})
  public void testShouldVerifyAllRegimensWhenLessThan12RegimensInMappingTable() throws SQLException {
    dbWrapper.deleteRowFromTable("coverage_target_group_products", "targetGroupEntity", "BCG");
    dbWrapper.deleteRowFromTable("coverage_target_group_products", "targetGroupEntity", "Measles");

    HomePage homePage = loginPage.loginAs(childCoverageData.get(USER), childCoverageData.get(PASSWORD));
    DistributionPage distributionPage = homePage.navigateToDistributionWhenOnline();
    distributionPage.initiate(childCoverageData.get(FIRST_DELIVERY_ZONE_NAME), childCoverageData.get(VACCINES_PROGRAM));
    FacilityListPage facilityListPage = distributionPage.clickRecordData(1);
    VisitInformationPage visitInformationPage = facilityListPage.selectFacility(childCoverageData.get(FIRST_FACILITY_CODE));
    visitInformationPage.navigateToChildCoverage();

    verifyRegimensPresent();
    verifyOpenVialsPresent();
  }

  @Test(groups = {"distribution"})
  public void testShouldVerifyAllRegimensWhenInsertedInDifferentOrderInMappingTableAndMappedToPullTypePrograms() throws SQLException {
    dbWrapper.deleteRowFromTable("coverage_target_group_products", "targetGroupEntity", "BCG");
    dbWrapper.deleteRowFromTable("coverage_target_group_products", "targetGroupEntity", "Measles");
    dbWrapper.insertTargetGroupEntityAndProductsInMappingTable("Measles", "P10", true);
    dbWrapper.insertTargetGroupEntityAndProductsInMappingTable("BCG", "P10", true);

    HomePage homePage = loginPage.loginAs(childCoverageData.get(USER), childCoverageData.get(PASSWORD));
    DistributionPage distributionPage = homePage.navigateToDistributionWhenOnline();
    distributionPage.initiate(childCoverageData.get(FIRST_DELIVERY_ZONE_NAME), childCoverageData.get(VACCINES_PROGRAM));
    FacilityListPage facilityListPage = distributionPage.clickRecordData(1);
    VisitInformationPage visitInformationPage = facilityListPage.selectFacility(childCoverageData.get(FIRST_FACILITY_CODE));
    visitInformationPage.navigateToChildCoverage();

    verifyRegimensPresent();
    verifyOpenVialsPresent();
  }

  @Test(groups = {"distribution"})
  public void testShouldVerifyAllRegimensWhenRegimenMappedToProductInactiveAtGlobalLevelAfterCaching() throws SQLException {
    HomePage homePage = loginPage.loginAs("Admin123", "Admin123");
    ProgramProductISAPage programProductISAPage = homePage.navigateProgramProductISA();
    programProductISAPage.fillProgramProductISA(childCoverageData.get(VACCINES_PROGRAM), "0.879", "1", "50", "30", "0", "100", "2000", "333");
    homePage.logout();

    homePage = loginPage.loginAs(childCoverageData.get(USER), childCoverageData.get(PASSWORD));
    DistributionPage distributionPage = homePage.navigateToDistributionWhenOnline();
    distributionPage.initiate(childCoverageData.get(FIRST_DELIVERY_ZONE_NAME), childCoverageData.get(VACCINES_PROGRAM));
    FacilityListPage facilityListPage = distributionPage.clickRecordData(1);
    VisitInformationPage visitInformationPage = facilityListPage.selectFacility(childCoverageData.get(FIRST_FACILITY_CODE));
    ChildCoveragePage childCoveragePage = visitInformationPage.navigateToChildCoverage();

    dbWrapper.updateFieldValue("products", "active", "f", "code", "P10");

    verifyRegimensPresent();
    verifyOpenVialsPresent();
    assertEquals(childCoveragePage.getTextOfTargetGroupValue(9), "1");
    assertEquals(childCoveragePage.getTextOfTargetGroupValue(10), "1");
    assertEquals(childCoveragePage.getTextOfTargetGroupValue(11), "1");
    assertEquals(childCoveragePage.getTextOfTargetGroupValue(1), "");
    assertEquals(childCoveragePage.getTextOfTargetGroupValue(12), "");

    dbWrapper.updateFieldValue("products", "active", "t", "code", "P10");
  }

  @Test(groups = {"distribution"})
  public void testIndividualFieldNRHandlingAndTotalCalculation() throws SQLException {
    HomePage homePage = loginPage.loginAs(childCoverageData.get(USER), childCoverageData.get(PASSWORD));
    DistributionPage distributionPage = homePage.navigateToDistributionWhenOnline();
    distributionPage.initiate(childCoverageData.get(FIRST_DELIVERY_ZONE_NAME), childCoverageData.get(VACCINES_PROGRAM));
    FacilityListPage facilityListPage = distributionPage.clickRecordData(1);
    VisitInformationPage visitInformationPage = facilityListPage.selectFacility(childCoverageData.get(FIRST_FACILITY_CODE));
    ChildCoveragePage childCoveragePage = visitInformationPage.navigateToChildCoverage();

    childCoveragePage.applyNrToPolioOpenedVials();
    assertFalse(childCoveragePage.isOpenVialEnabled(2, 1));
    assertFalse(childCoveragePage.isOpenVialEnabled(2, 2));
    childCoveragePage.applyNrToPolioOpenedVials();
    assertTrue(childCoveragePage.isOpenVialEnabled(2, 1));
    assertTrue(childCoveragePage.isOpenVialEnabled(2, 2));
    childCoveragePage.enterOpenedVialsCountForGivenGroupAndRow(2, 1, "5");

    childCoveragePage.enterHealthCenter11MonthsDataForGivenRow(1, "10");
    assertEquals("10", childCoveragePage.getTotalForGivenColumnAndRow(1, 1));
    assertEquals("0", childCoveragePage.getTotalForGivenColumnAndRow(2, 1));
    assertEquals("10", childCoveragePage.getTotalForGivenColumnAndRow(3, 1));

    childCoveragePage.enterHealthCenter23MonthsDataForGivenRow(1, "032");
    assertEquals("10", childCoveragePage.getTotalForGivenColumnAndRow(1, 1));
    assertEquals("32", childCoveragePage.getTotalForGivenColumnAndRow(2, 1));
    assertEquals("42", childCoveragePage.getTotalForGivenColumnAndRow(3, 1));

    childCoveragePage.enterOutreach11MonthsDataForGivenRow(1, "27");
    assertEquals("37", childCoveragePage.getTotalForGivenColumnAndRow(1, 1));
    assertEquals("32", childCoveragePage.getTotalForGivenColumnAndRow(2, 1));
    assertEquals("69", childCoveragePage.getTotalForGivenColumnAndRow(3, 1));

    childCoveragePage.enterOutreach23MonthsDataForGivenRow(1, "0");
    assertEquals("37", childCoveragePage.getTotalForGivenColumnAndRow(1, 1));
    assertEquals("32", childCoveragePage.getTotalForGivenColumnAndRow(2, 1));
    assertEquals("69", childCoveragePage.getTotalForGivenColumnAndRow(3, 1));

    childCoveragePage.applyNRToHealthCenter11MonthsForGivenRow(1);
    assertEquals("27", childCoveragePage.getTotalForGivenColumnAndRow(1, 1));
    assertEquals("32", childCoveragePage.getTotalForGivenColumnAndRow(2, 1));
    assertEquals("59", childCoveragePage.getTotalForGivenColumnAndRow(3, 1));

    childCoveragePage.applyNRToHealthCenter11MonthsForGivenRow(1);
    childCoveragePage.enterHealthCenter11MonthsDataForGivenRow(1, "9999999");
    childCoveragePage.enterHealthCenter23MonthsDataForGivenRow(1, "9999999");
    childCoveragePage.enterOutreach11MonthsDataForGivenRow(1, "9999999");
    childCoveragePage.enterOutreach23MonthsDataForGivenRow(1, "9999999");
    assertEquals("19999998", childCoveragePage.getTotalForGivenColumnAndRow(1, 1));
    assertEquals("19999998", childCoveragePage.getTotalForGivenColumnAndRow(2, 1));
    assertEquals("39999996", childCoveragePage.getTotalForGivenColumnAndRow(3, 1));
  }

  @Test(groups = {"distribution"})
  public void testShouldVerifyWastageCoverageWhenPackSizeUpdatedAfterInitiatingDistribution() throws SQLException {
    dbWrapper.updateFieldValue("products", "packSize", "3", "code", "P11");
    insertOpenedVialsProductMapping();

    HomePage homePage = loginPage.loginAs(childCoverageData.get(USER), childCoverageData.get(PASSWORD));
    DistributionPage distributionPage = homePage.navigateToDistributionWhenOnline();
    distributionPage.initiate(childCoverageData.get(FIRST_DELIVERY_ZONE_NAME), childCoverageData.get(VACCINES_PROGRAM));
    FacilityListPage facilityListPage = distributionPage.clickRecordData(1);
    VisitInformationPage visitInformationPage = facilityListPage.selectFacility(childCoverageData.get(FIRST_FACILITY_CODE));
    ChildCoveragePage childCoveragePage = visitInformationPage.navigateToChildCoverage();

    dbWrapper.updateFieldValue("products", "packSize", "5", "code", "P10");
    childCoveragePage.enterHealthCenter11MonthsDataForGivenRow(9, "9");
    assertEquals("9", childCoveragePage.getTotalForGivenColumnAndRow(1, 9));
    assertEquals("0", childCoveragePage.getTotalForGivenColumnAndRow(2, 9));
    assertEquals("9", childCoveragePage.getTotalForGivenColumnAndRow(3, 9));
    childCoveragePage.enterOpenedVialsCountForGivenGroupAndRow(9, 1, "5");
    assertEquals("82", childCoveragePage.getWastageRateForGivenRow(9));

    childCoveragePage.enterOpenedVialsCountForGivenGroupAndRow(9, 1, "8");
    assertEquals("89", childCoveragePage.getWastageRateForGivenRow(9));
  }

  @Test(groups = {"distribution"})
  public void testShouldVerifyWastageCoverageWhenProductInactiveAndProductNotInProgram() throws SQLException {
    dbWrapper.updateFieldValue("products", "active", "f", "code", "P11");
    dbWrapper.updateFieldValue("products", "packSize", "3", "code", "P11");
    insertOpenedVialsProductMapping();
    dbWrapper.deleteRowFromTable("coverage_product_vials", "vial", "PCV");

    HomePage homePage = loginPage.loginAs(childCoverageData.get(USER), childCoverageData.get(PASSWORD));
    DistributionPage distributionPage = homePage.navigateToDistributionWhenOnline();
    distributionPage.initiate(childCoverageData.get(FIRST_DELIVERY_ZONE_NAME), childCoverageData.get(VACCINES_PROGRAM));
    FacilityListPage facilityListPage = distributionPage.clickRecordData(1);
    VisitInformationPage visitInformationPage = facilityListPage.selectFacility(childCoverageData.get(FIRST_FACILITY_CODE));
    ChildCoveragePage childCoveragePage = visitInformationPage.navigateToChildCoverage();

    childCoveragePage.enterOpenedVialsCountForGivenGroupAndRow(1, 1, "9999999");
    assertEquals("100", childCoveragePage.getWastageRateForGivenRow(1));

    childCoveragePage.enterOpenedVialsCountForGivenGroupAndRow(9, 1, "9999999");
    assertEquals("", childCoveragePage.getWastageRateForGivenRow(9));

    childCoveragePage.enterOpenedVialsCountForGivenGroupAndRow(12, 1, "99");
    assertEquals("", childCoveragePage.getWastageRateForGivenRow(12));
    childCoveragePage.enterOutreach11MonthsDataForGivenRow(12, "67");
    assertEquals("", childCoveragePage.getWastageRateForGivenRow(12));
  }

  @Test(groups = {"distribution"})
  public void testShouldVerifyWastageCoverageWhenMappingRemovedAfterCaching() throws SQLException {
    insertOpenedVialsProductMapping();

    HomePage homePage = loginPage.loginAs(childCoverageData.get(USER), childCoverageData.get(PASSWORD));
    DistributionPage distributionPage = homePage.navigateToDistributionWhenOnline();
    distributionPage.initiate(childCoverageData.get(FIRST_DELIVERY_ZONE_NAME), childCoverageData.get(VACCINES_PROGRAM));
    FacilityListPage facilityListPage = distributionPage.clickRecordData(1);
    VisitInformationPage visitInformationPage = facilityListPage.selectFacility(childCoverageData.get(FIRST_FACILITY_CODE));
    dbWrapper.deleteRowFromTable("coverage_target_group_products", "childCoverage", "true");
    dbWrapper.deleteRowFromTable("coverage_product_vials", "childCoverage", "true");

    ChildCoveragePage childCoveragePage = visitInformationPage.navigateToChildCoverage();

    childCoveragePage.enterOpenedVialsCountForGivenGroupAndRow(1, 1, "9999999");
    assertEquals("100", childCoveragePage.getWastageRateForGivenRow(1));

    childCoveragePage.enterOpenedVialsCountForGivenGroupAndRow(9, 1, "9999999");
    assertEquals("100", childCoveragePage.getWastageRateForGivenRow(9));
  }

  @Test(groups = {"distribution"})
  public void testShouldVerifyWastageCoverageWhenVialsInRegimenMappedToDifferentProductsAndVerifyNegativeValueAndNR() throws SQLException {
    dbWrapper.updateFieldValue("products", "packSize", "3", "code", "P11");
    insertOpenedVialsProductMapping();

    HomePage homePage = loginPage.loginAs(childCoverageData.get(USER), childCoverageData.get(PASSWORD));
    DistributionPage distributionPage = homePage.navigateToDistributionWhenOnline();
    distributionPage.initiate(childCoverageData.get(FIRST_DELIVERY_ZONE_NAME), childCoverageData.get(VACCINES_PROGRAM));
    FacilityListPage facilityListPage = distributionPage.clickRecordData(1);
    VisitInformationPage visitInformationPage = facilityListPage.selectFacility(childCoverageData.get(FIRST_FACILITY_CODE));
    ChildCoveragePage childCoveragePage = visitInformationPage.navigateToChildCoverage();

    childCoveragePage.enterHealthCenter11MonthsDataForGivenRow(2, "9");
    assertEquals("9", childCoveragePage.getTotalForGivenColumnAndRow(1, 2));
    assertEquals("9", childCoveragePage.getTotalForGivenColumnAndRow(3, 2));
    childCoveragePage.enterOpenedVialsCountForGivenGroupAndRow(2, 1, "7");
    assertEquals("57", childCoveragePage.getWastageRateForGivenRow(2));
    childCoveragePage.enterHealthCenter11MonthsDataForGivenRow(2, "9999999");
    childCoveragePage.enterOutreach11MonthsDataForGivenRow(2, "9999999");
    childCoveragePage.enterHealthCenter11MonthsDataForGivenRow(3, "99999999");
    childCoveragePage.enterOutreach11MonthsDataForGivenRow(3, "99999999");
    childCoveragePage.enterHealthCenter23MonthsDataForGivenRow(3, "99999999");
    childCoveragePage.enterOutreach23MonthsDataForGivenRow(3, "99999999");
    childCoveragePage.enterHealthCenter11MonthsDataForGivenRow(4, "99999999");
    childCoveragePage.enterOutreach11MonthsDataForGivenRow(4, "99999999");
    childCoveragePage.enterHealthCenter23MonthsDataForGivenRow(4, "99999999");
    childCoveragePage.enterOutreach23MonthsDataForGivenRow(4, "99999999");
    childCoveragePage.enterHealthCenter11MonthsDataForGivenRow(5, "99999999");
    childCoveragePage.enterOutreach11MonthsDataForGivenRow(5, "99999999");
    childCoveragePage.enterHealthCenter23MonthsDataForGivenRow(5, "99999999");
    childCoveragePage.enterOutreach23MonthsDataForGivenRow(5, "99999999");
    assertEquals("-666666500", childCoveragePage.getWastageRateForGivenRow(2));

    childCoveragePage.enterOpenedVialsCountForGivenGroupAndRow(2, 1, "9999999");
    assertEquals("-367", childCoveragePage.getWastageRateForGivenRow(2));

    childCoveragePage.enterOpenedVialsCountForGivenGroupAndRow(2, 2, "9999999");
    assertEquals("-8", childCoveragePage.getWastageRateForGivenRow(2));

    childCoveragePage.applyNRToHealthCenter11MonthsForGivenRow(3);
    assertFalse(childCoveragePage.isHealthCenter11MonthsEnabledForGivenRow(3));
    assertEquals("0", childCoveragePage.getWastageRateForGivenRow(2));
    childCoveragePage.applyNRToHealthCenter11MonthsForGivenRow(3);
    assertTrue(childCoveragePage.isHealthCenter11MonthsEnabledForGivenRow(3));
    assertEquals("", childCoveragePage.getHealthCenter11MonthsDataForGivenRow(3));
    assertEquals("0", childCoveragePage.getWastageRateForGivenRow(2));

    childCoveragePage.applyNrToPolioOpenedVials();
    assertFalse(childCoveragePage.isOpenVialEnabled(2, 1));
    assertFalse(childCoveragePage.isOpenVialEnabled(2, 2));
    assertEquals("", childCoveragePage.getWastageRateForGivenRow(2));
  }

  @Test(groups = {"distribution"})
  public void testShouldVerifyWastageCoverageWhenOnlyOneVialInRegimenIsMappedToProductSupportedByProgram() throws SQLException {
    dbWrapper.updateFieldValue("products", "packSize", "3", "code", "P11");
    insertOpenedVialsProductMapping();

    HomePage homePage = loginPage.loginAs(childCoverageData.get(USER), childCoverageData.get(PASSWORD));
    DistributionPage distributionPage = homePage.navigateToDistributionWhenOnline();
    distributionPage.initiate(childCoverageData.get(FIRST_DELIVERY_ZONE_NAME), childCoverageData.get(VACCINES_PROGRAM));
    FacilityListPage facilityListPage = distributionPage.clickRecordData(1);
    VisitInformationPage visitInformationPage = facilityListPage.selectFacility(childCoverageData.get(FIRST_FACILITY_CODE));
    ChildCoveragePage childCoveragePage = visitInformationPage.navigateToChildCoverage();

    childCoveragePage.enterHealthCenter11MonthsDataForGivenRow(6, "9");
    assertEquals("9", childCoveragePage.getTotalForGivenColumnAndRow(1, 6));
    assertEquals("0", childCoveragePage.getTotalForGivenColumnAndRow(2, 6));
    assertEquals("9", childCoveragePage.getTotalForGivenColumnAndRow(3, 6));
    childCoveragePage.enterOpenedVialsCountForGivenGroupAndRow(6, 1, "7");
    assertEquals("", childCoveragePage.getWastageRateForGivenRow(6));

    childCoveragePage.enterOpenedVialsCountForGivenGroupAndRow(6, 2, "9");
    assertEquals("67", childCoveragePage.getWastageRateForGivenRow(6));

    childCoveragePage.enterOpenedVialsCountForGivenGroupAndRow(6, 1, "89");
    assertEquals("67", childCoveragePage.getWastageRateForGivenRow(6));
  }

  @Test(groups = {"distribution"})
  public void testShouldVerifyApplyNRToAllAndSync() throws SQLException {
    HomePage homePage = loginPage.loginAs(childCoverageData.get(USER), childCoverageData.get(PASSWORD));
    DistributionPage distributionPage = homePage.navigateToDistributionWhenOnline();
    distributionPage.initiate(childCoverageData.get(FIRST_DELIVERY_ZONE_NAME), childCoverageData.get(VACCINES_PROGRAM));
    FacilityListPage facilityListPage = distributionPage.clickRecordData(1);
    VisitInformationPage visitInformationPage = facilityListPage.selectFacility(childCoverageData.get(FIRST_FACILITY_CODE));
    ChildCoveragePage childCoveragePage = visitInformationPage.navigateToChildCoverage();

    childCoveragePage.verifyIndicator("RED");

    childCoveragePage.enterHealthCenter11MonthsDataForGivenRow(6, "9");
    childCoveragePage.verifyIndicator("AMBER");
    childCoveragePage.enterOpenedVialsCountForGivenGroupAndRow(6, 1, "7");
    childCoveragePage.enterOpenedVialsCountForGivenGroupAndRow(6, 2, "9");

    childCoveragePage.applyNRToAll();
    childCoveragePage.clickCancel();
    assertTrue(childCoveragePage.isOpenVialEnabled(1, 1));
    childCoveragePage.applyNRToAll();
    childCoveragePage.clickOK();
    childCoveragePage.verifyAllFieldsDisabled();
    childCoveragePage.verifyIndicator("GREEN");

    testWebDriver.refresh();
    childCoveragePage.verifyAllFieldsDisabled();
    childCoveragePage.applyNRToAll();
    childCoveragePage.clickOK();
    childCoveragePage.verifyAllFieldsDisabled();

    EpiInventoryPage epiInventoryPage = childCoveragePage.navigateToEpiInventory();
    epiInventoryPage.navigateToChildCoverage();
    childCoveragePage.verifyAllFieldsDisabled();
    childCoveragePage.applyNRToAll();
    childCoveragePage.clickCancel();
    childCoveragePage.verifyAllFieldsDisabled();

    childCoveragePage.applyNrToPolioOpenedVials();
    childCoveragePage.verifyIndicator("AMBER");
    childCoveragePage.enterOpenedVialsCountForGivenGroupAndRow(2, 1, "89");
    childCoveragePage.verifyIndicator("AMBER");
    childCoveragePage.enterOpenedVialsCountForGivenGroupAndRow(2, 2, "9");
    childCoveragePage.verifyIndicator("GREEN");

    childCoveragePage.applyNRToHealthCenter11MonthsForGivenRow(1);
    childCoveragePage.verifyIndicator("AMBER");

    assertTrue(childCoveragePage.isOpenVialEnabled(2, 1));
    assertFalse(childCoveragePage.isOpenVialEnabled(1, 1));

    childCoveragePage.applyNRToAll();
    childCoveragePage.clickOK();

    childCoveragePage.navigateToVisitInformation();
    visitInformationPage.selectFacilityVisitedNo();
    visitInformationPage.selectReasonNoTransport();

    FullCoveragePage fullCoveragePage = visitInformationPage.navigateToFullCoverage();
    fullCoveragePage.clickApplyNRToAll();

    AdultCoveragePage adultCoveragePage = fullCoveragePage.navigateToAdultCoverage();
    adultCoveragePage.clickApplyNrToAll();
    adultCoveragePage.clickOK();

    facilityListPage.verifyIndividualFacilityIndicatorColor("F10", "GREEN");
    distributionPage = homePage.navigateToDistributionWhenOnline();
    distributionPage.syncDistribution(1);
    assertTrue(distributionPage.getSyncMessage().contains("F10-Village Dispensary"));
    distributionPage.syncDistributionMessageDone();

    String facilityId = dbWrapper.getAttributeFromTable("facilities", "id", "code", "F10");
    String facilityVisitId = dbWrapper.getAttributeFromTable("facility_visits", "id", "facilityId", facilityId);
    List<String> vaccinations = asList("BCG", "Polio (Newborn)", "Polio 1st dose", "Polio 2nd dose", "Polio 3rd dose", "Penta 1st dose", "Penta 2nd dose", "Penta 3rd dose", "PCV10 1st dose", "PCV10 2nd dose", "PCV10 3rd dose", "Measles");

    for (int i = 1; i <= 12; i++) {
      ResultSet childCoverageDetails = dbWrapper.getChildCoverageDetails(vaccinations.get(i - 1), facilityVisitId);
      assertEqualsAndNulls(childCoverageDetails.getString("healthCenter11months"), "null");
      assertEqualsAndNulls(childCoverageDetails.getString("outreach11months"), "null");
      if (i != 2) {
        assertEqualsAndNulls(childCoverageDetails.getString("healthCenter23months"), "null");
        assertEqualsAndNulls(childCoverageDetails.getString("outreach23months"), "null");
      }
    }
    List<String> openedVials = asList("BCG", "Polio10", "Polio20", "Penta1", "Penta10", "PCV", "Measles");
    for (int i = 1; i <= 7; i++) {
      ResultSet openedVialLineItem = dbWrapper.getChildOpenedVialLineItem(openedVials.get(i - 1), facilityVisitId);
      assertEqualsAndNulls(openedVialLineItem.getString("openedVials"), "null");
    }
  }

  @Test(groups = {"distribution"})
  public void testShouldVerifyApplyNRToSomeFieldsAndSync() throws SQLException {
    HomePage homePage = loginPage.loginAs(childCoverageData.get(USER), childCoverageData.get(PASSWORD));
    DistributionPage distributionPage = homePage.navigateToDistributionWhenOnline();
    distributionPage.initiate(childCoverageData.get(FIRST_DELIVERY_ZONE_NAME), childCoverageData.get(VACCINES_PROGRAM));
    FacilityListPage facilityListPage = distributionPage.clickRecordData(1);
    VisitInformationPage visitInformationPage = facilityListPage.selectFacility(childCoverageData.get(FIRST_FACILITY_CODE));
    ChildCoveragePage childCoveragePage = visitInformationPage.navigateToChildCoverage();

    childCoveragePage.applyNRToAll();
    childCoveragePage.clickOK();
    childCoveragePage.applyNRToHealthCenter11MonthsForGivenRow(1);
    childCoveragePage.applyNRToOutreach11MonthsForGivenRow(2);
    childCoveragePage.applyNRToHealthCenter23MonthsForGivenRow(11);
    childCoveragePage.applyNRToOutreach23MonthsDataForGivenRow(12);
    childCoveragePage.enterHealthCenter11MonthsDataForGivenRow(1, "9");
    childCoveragePage.enterOutreach11MonthsDataForGivenRow(2, "23");
    childCoveragePage.enterHealthCenter23MonthsDataForGivenRow(11, "1234567");
    childCoveragePage.enterOutreach23MonthsDataForGivenRow(12, "7654321");

    childCoveragePage.applyNrToBcgOpenedVials();
    childCoveragePage.enterOpenedVialsCountForGivenGroupAndRow(1, 1, "1234567");

    childCoveragePage.navigateToVisitInformation();
    visitInformationPage.selectFacilityVisitedNo();
    visitInformationPage.selectReasonNoTransport();

    FullCoveragePage fullCoveragePage = visitInformationPage.navigateToFullCoverage();
    fullCoveragePage.clickApplyNRToAll();

    AdultCoveragePage adultCoveragePage = fullCoveragePage.navigateToAdultCoverage();
    adultCoveragePage.clickApplyNrToAll();
    adultCoveragePage.clickOK();

    facilityListPage.verifyIndividualFacilityIndicatorColor("F10", "GREEN");
    distributionPage = homePage.navigateToDistributionWhenOnline();
    distributionPage.syncDistribution(1);
    assertTrue(distributionPage.getSyncMessage().contains("F10-Village Dispensary"));
    distributionPage.syncDistributionMessageDone();

    String facilityId = dbWrapper.getAttributeFromTable("facilities", "id", "code", "F10");
    String facilityVisitId = dbWrapper.getAttributeFromTable("facility_visits", "id", "facilityId", facilityId);

    ResultSet childCoverageDetails = dbWrapper.getChildCoverageDetails("BCG", facilityVisitId);
    assertEqualsAndNulls(childCoverageDetails.getString("healthcenter11months"), "9");
    assertEqualsAndNulls(childCoverageDetails.getString("outreach11months"), "null");
    assertEqualsAndNulls(childCoverageDetails.getString("healthcenter23months"), "null");
    assertEqualsAndNulls(childCoverageDetails.getString("outreach23months"), "null");

    childCoverageDetails = dbWrapper.getChildCoverageDetails("Polio (Newborn)", facilityVisitId);
    assertEqualsAndNulls(childCoverageDetails.getString("healthcenter11months"), "null");
    assertEqualsAndNulls(childCoverageDetails.getString("outreach11months"), "23");
    assertEqualsAndNulls(childCoverageDetails.getString("healthcenter23months"), "null");
    assertEqualsAndNulls(childCoverageDetails.getString("outreach23months"), "null");

    childCoverageDetails = dbWrapper.getChildCoverageDetails("PCV10 3rd dose", facilityVisitId);
    assertEqualsAndNulls(childCoverageDetails.getString("healthcenter11months"), "null");
    assertEqualsAndNulls(childCoverageDetails.getString("outreach11months"), "null");
    assertEqualsAndNulls(childCoverageDetails.getString("healthcenter23months"), "1234567");
    assertEqualsAndNulls(childCoverageDetails.getString("outreach23months"), "null");

    childCoverageDetails = dbWrapper.getChildCoverageDetails("Measles", facilityVisitId);
    assertEqualsAndNulls(childCoverageDetails.getString("healthcenter11months"), "null");
    assertEqualsAndNulls(childCoverageDetails.getString("outreach11months"), "null");
    assertEqualsAndNulls(childCoverageDetails.getString("healthcenter23months"), "null");
    assertEqualsAndNulls(childCoverageDetails.getString("outreach23months"), "7654321");

    ResultSet openedVialLineItem = dbWrapper.getChildOpenedVialLineItem("BCG", facilityVisitId);
    assertEquals(openedVialLineItem.getString("openedVials"), "1234567");

    openedVialLineItem = dbWrapper.getChildOpenedVialLineItem("Measles", facilityVisitId);
    assertEqualsAndNulls(openedVialLineItem.getString("openedVials"), "null");
  }

  @Test(groups = {"distribution"})
  public void testShouldVerifyChildCoverageSync() throws SQLException {
    HomePage homePage = loginPage.loginAs("Admin123", "Admin123");
    ProgramProductISAPage programProductISAPage = homePage.navigateProgramProductISA();
    programProductISAPage.fillProgramProductISA(childCoverageData.get(VACCINES_PROGRAM), "90", "1", "50", "30", "0", "100", "2000", "333");
    homePage.logout();

    loginPage.loginAs(childCoverageData.get(USER), childCoverageData.get(PASSWORD));
    DistributionPage distributionPage = homePage.navigateToDistributionWhenOnline();
    distributionPage.initiate(childCoverageData.get(FIRST_DELIVERY_ZONE_NAME), childCoverageData.get(VACCINES_PROGRAM));
    FacilityListPage facilityListPage = distributionPage.clickRecordData(1);
    VisitInformationPage visitInformationPage = facilityListPage.selectFacility(childCoverageData.get(FIRST_FACILITY_CODE));

    ChildCoveragePage childCoveragePage = visitInformationPage.navigateToChildCoverage();

    childCoveragePage.enterAllChildCoverageValues();
    childCoveragePage.enterOpenedVialsData();

    childCoveragePage.navigateToVisitInformation();
    visitInformationPage.selectFacilityVisitedNo();
    visitInformationPage.selectReasonNoTransport();

    FullCoveragePage fullCoveragePage = visitInformationPage.navigateToFullCoverage();
    fullCoveragePage.clickApplyNRToAll();

    AdultCoveragePage adultCoveragePage = fullCoveragePage.navigateToAdultCoverage();
    adultCoveragePage.clickApplyNrToAll();
    adultCoveragePage.clickOK();

    facilityListPage.verifyIndividualFacilityIndicatorColor("F10", "GREEN");
    distributionPage = homePage.navigateToDistributionWhenOnline();
    distributionPage.syncDistribution(1);
    assertTrue(distributionPage.getSyncMessage().contains("F10-Village Dispensary"));
    distributionPage.syncDistributionMessageDone();

    String facilityId = dbWrapper.getAttributeFromTable("facilities", "id", "code", "F10");
    String facilityVisitId = dbWrapper.getAttributeFromTable("facility_visits", "id", "facilityId", facilityId);

    List<String> vaccinations = asList("BCG", "Polio (Newborn)", "Polio 1st dose", "Polio 2nd dose", "Polio 3rd dose", "Penta 1st dose", "Penta 2nd dose", "Penta 3rd dose", "PCV10 1st dose", "PCV10 2nd dose", "PCV10 3rd dose", "Measles");

    for (int i = 1; i <= 12; i++) {
      ResultSet childCoverageDetails = dbWrapper.getChildCoverageDetails(vaccinations.get(i - 1), facilityVisitId);

      assertEquals(childCoverageDetails.getString("healthCenter11months"), String.valueOf(i));
      assertEquals(childCoverageDetails.getString("outreach11months"), String.valueOf(i + 10));
      if (i != 2) {
        assertEquals(childCoverageDetails.getString("healthCenter23months"), String.valueOf(i + 100));
        assertEquals(childCoverageDetails.getString("outreach23months"), String.valueOf(i + 11));
      }
    }

    List<String> openedVials = asList("BCG", "Polio10", "Polio20", "Penta1", "Penta10", "PCV", "Measles");
    for (int i = 1; i <= 7; i++) {
      ResultSet openedVialLineItem = dbWrapper.getChildOpenedVialLineItem(openedVials.get(i - 1), facilityVisitId);
      assertEquals(openedVialLineItem.getString("openedVials"), String.valueOf(i * 100));
    }
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

  private void verifyOpenVialsPresent() {
    ChildCoveragePage childCoveragePage = PageObjectFactory.getChildCoveragePage(testWebDriver);
    assertEquals(childCoveragePage.getTextOfOpenedVialsBCG(), "BCG");
    assertEquals(childCoveragePage.getTextOfOpenedVialsPolio10(), "Polio10");
    assertEquals(childCoveragePage.getTextOfOpenedVialsPolio20(), "Polio20");
    assertEquals(childCoveragePage.getTextOfOpenedVialsPenta1(), "Penta1");
    assertEquals(childCoveragePage.getTextOfOpenedVialsPenta10(), "Penta10");
    assertEquals(childCoveragePage.getTextOfOpenedVialsPCV(), "PCV");
    assertEquals(childCoveragePage.getTextOfOpenedVialsMeasles(), "Measles");
  }

  private void verifyHeadersPresent() {
    ChildCoveragePage childCoveragePage = PageObjectFactory.getChildCoveragePage(testWebDriver);
    assertEquals(childCoveragePage.getTextOfHeaderChildrenVaccination(), "Child Vaccinations (doses)");
    assertEquals(childCoveragePage.getTextOfHeaderTargetGroup(), "Target Group");
    assertEquals(childCoveragePage.getTextOfHeaderHealthCenter1(), "Health Center");
    assertEquals(childCoveragePage.getTextOfHeaderMobileBrigade1(), "Outreach");
    assertEquals(childCoveragePage.getTextOfHeaderTotal1(), "Total");
    assertEquals(childCoveragePage.getTextOfHeaderCoverageRate(), "Coverage Rate");
    assertEquals(childCoveragePage.getTextOfHeaderHealthCenter2(), "Health Center");
    assertEquals(childCoveragePage.getTextOfHeaderMobileBrigade2(), "Outreach");
    assertEquals(childCoveragePage.getTextOfHeaderTotal2(), "Total");
    assertEquals(childCoveragePage.getTextOfHeaderTotalVaccination(), "Total Vaccination");
    assertEquals(childCoveragePage.getTextOfHeaderOpenedVials(), "Opened Vials");
    assertEquals(childCoveragePage.getTextOfHeaderWastageRate(), "Opened Vials Wastage Rate");
    assertEquals(childCoveragePage.getTextOfHeaderCategory1(), "0-11 months");
    assertEquals(childCoveragePage.getTextOfHeaderCategory2(), "12-23 months");
  }

  private void verifyRegimensPresent() {
    ChildCoveragePage childCoveragePage = PageObjectFactory.getChildCoveragePage(testWebDriver);
    assertEquals(childCoveragePage.getTextOfRegimenBCG(), "BCG");
    assertEquals(childCoveragePage.getTextOfRegimenPolioNewBorn(), "Polio (Newborn)");
    assertEquals(childCoveragePage.getTextOfRegimenPolioDose1(), "Polio 1st dose");
    assertEquals(childCoveragePage.getTextOfRegimenPolioDose2(), "Polio 2nd dose");
    assertEquals(childCoveragePage.getTextOfRegimenPolioDose3(), "Polio 3rd dose");
    assertEquals(childCoveragePage.getTextOfRegimenPentaDose1(), "Penta 1st dose");
    assertEquals(childCoveragePage.getTextOfRegimenPentaDose2(), "Penta 2nd dose");
    assertEquals(childCoveragePage.getTextOfRegimenPentaDose3(), "Penta 3rd dose");
    assertEquals(childCoveragePage.getTextOfRegimenPCV10Dose1(), "PCV10 1st dose");
    assertEquals(childCoveragePage.getTextOfRegimenPCV10Dose2(), "PCV10 2nd dose");
    assertEquals(childCoveragePage.getTextOfRegimenPCV10Dose3(), "PCV10 3rd dose");
    assertEquals(childCoveragePage.getTextOfRegimenMeasles(), "Measles");
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

  @When("^I apply NR to healthCenter11Months for rowNumber \"([^\"]*)\"$")
  public void applyNrToHealthCenter11(String rowNumber) {
    ChildCoveragePage childCoveragePage = PageObjectFactory.getChildCoveragePage(testWebDriver);
    childCoveragePage.applyNRToHealthCenter11MonthsForGivenRow(Integer.parseInt(rowNumber));
  }

  @And("^I enter healthCenter11Months for rowNumber \"([^\"]*)\" as \"([^\"]*)\"$")
  public void enterHealthCenter11Data(String rowNumber, String value) {
    ChildCoveragePage childCoveragePage = PageObjectFactory.getChildCoveragePage(testWebDriver);
    childCoveragePage.enterHealthCenter11MonthsDataForGivenRow(Integer.parseInt(rowNumber), value);
  }

  @And("^I apply NR to all fields on child coverage page$")
  public void applyNrToAll() {
    ChildCoveragePage childCoveragePage = PageObjectFactory.getChildCoveragePage(testWebDriver);
    childCoveragePage.applyNRToAll();
    childCoveragePage.clickOK();
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
