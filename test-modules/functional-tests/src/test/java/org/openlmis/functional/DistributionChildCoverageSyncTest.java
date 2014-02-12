package org.openlmis.functional;

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
    Map<String, String> dataMap = childCoverageData;
    setupDataForDistributionTest(dataMap.get(USER), dataMap.get(FIRST_DELIVERY_ZONE_CODE), dataMap.get(SECOND_DELIVERY_ZONE_CODE),
      dataMap.get(FIRST_DELIVERY_ZONE_NAME), dataMap.get(SECOND_DELIVERY_ZONE_NAME), dataMap.get(FIRST_FACILITY_CODE),
      dataMap.get(SECOND_FACILITY_CODE), dataMap.get(VACCINES_PROGRAM), dataMap.get(TB_PROGRAM), dataMap.get(SCHEDULE),
      dataMap.get(PRODUCT_GROUP_CODE));
    loginPage = PageFactory.getInstanceOfLoginPage(testWebDriver, baseUrlGlobal);
  }

  @Test(groups = {"distribution"})
  public void testShouldVerifyAllLabels() {
    HomePage homePage = loginPage.loginAs(childCoverageData.get(USER), childCoverageData.get(PASSWORD));
    DistributionPage distributionPage = homePage.navigateToDistributionWhenOnline();
    distributionPage.initiate(childCoverageData.get(FIRST_DELIVERY_ZONE_NAME), childCoverageData.get(VACCINES_PROGRAM));
    FacilityListPage facilityListPage = distributionPage.clickRecordData(1);
    VisitInformationPage visitInformationPage = facilityListPage.selectFacility(childCoverageData.get(FIRST_FACILITY_CODE));

    facilityListPage.verifyFacilityIndicatorColor("Overall", "AMBER");

    ChildCoveragePage childCoveragePage = visitInformationPage.navigateToChildCoverage();
    childCoveragePage.verifyIndicator("RED");

    verifyRegimentsPresent();
    verifyHeadersPresent();
    verifyOpenVialsPresent();
  }

  @Test(groups = {"distribution"})
  public void testShouldVerifyTargetGroupIfCatchmentPopulationAndWhoRatioPresent() throws SQLException {
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
    assertEquals(childCoveragePage.getTextOfTargetGroupValue(9), "300");
    assertEquals(childCoveragePage.getTextOfTargetGroupValue(10), "300");
    assertEquals(childCoveragePage.getTextOfTargetGroupValue(11), "300");
    assertEquals(childCoveragePage.getTextOfTargetGroupValue(1), "");
    assertEquals(childCoveragePage.getTextOfTargetGroupValue(12), "");

    ResultSet childCoverageDetails = dbWrapper.getChildCoverageDetails(childCoveragePage.getTextOfRegimenPCV10Dose1(), "F10");
    assertEquals("300", childCoverageDetails.getInt("targetGroup"));
  }

  @Test(groups = {"distribution"})
  public void testShouldVerifyTargetGroupIfOnlyCatchmentPopulationPresent() {
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
  }

  @Test(groups = {"distribution"})
  public void testShouldVerifyTargetGroupIfOnlyWhoRatioPresent() {
    HomePage homePage = loginPage.loginAs("Admin123", "Admin123");
    ProgramProductISAPage programProductISAPage = homePage.navigateProgramProductISA();
    programProductISAPage.fillProgramProductISA(childCoverageData.get(VACCINES_PROGRAM), "90", "1", "50", "30", "0", "100", "2000", "333");

    ManageFacilityPage manageFacilityPage = homePage.navigateManageFacility();
    homePage.navigateSearchFacility();
    manageFacilityPage.searchFacility("F10");
    manageFacilityPage.clickFacilityList("F10");
    manageFacilityPage.editPopulation("");
    manageFacilityPage.saveFacility();
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
  }

  @Test(groups = {"distribution"})
  public void testShouldVerifyAllRegimensWithTargetValuePopulatedWhenRegimenMappedToProductInactiveAtGlobalLevel() throws SQLException {
    dbWrapper.updateFieldValue("products", "active", "f", "code", "P10");

    HomePage homePage = loginPage.loginAs("Admin123", "Admin123");
    ProgramProductISAPage programProductISAPage = homePage.navigateProgramProductISA();
    programProductISAPage.fillProgramProductISA(childCoverageData.get(VACCINES_PROGRAM), "90", "1", "50", "30", "0", "100", "2000", "333");
    homePage.logout();

    homePage = loginPage.loginAs(childCoverageData.get(USER), childCoverageData.get(PASSWORD));
    DistributionPage distributionPage = homePage.navigateToDistributionWhenOnline();
    distributionPage.initiate(childCoverageData.get(FIRST_DELIVERY_ZONE_NAME), childCoverageData.get(VACCINES_PROGRAM));
    FacilityListPage facilityListPage = distributionPage.clickRecordData(1);
    VisitInformationPage visitInformationPage = facilityListPage.selectFacility(childCoverageData.get(FIRST_FACILITY_CODE));
    ChildCoveragePage childCoveragePage = visitInformationPage.navigateToChildCoverage();

    verifyRegimentsPresent();
    verifyOpenVialsPresent();
    assertEquals(childCoveragePage.getTextOfTargetGroupValue(9), "300");
    assertEquals(childCoveragePage.getTextOfTargetGroupValue(10), "300");
    assertEquals(childCoveragePage.getTextOfTargetGroupValue(11), "300");
    assertEquals(childCoveragePage.getTextOfTargetGroupValue(1), "");
    assertEquals(childCoveragePage.getTextOfTargetGroupValue(12), "");

    dbWrapper.updateFieldValue("products", "active", "t", "code", "P10");
  }

  @Test(groups = {"distribution"})
  public void testShouldVerifyAllRegimensWithTargetValuePopulatedWhenRegimenMappedToProductInactiveAtProgramLevel() throws SQLException {
    String productId = dbWrapper.getAttributeFromTable("products", "id", "code", "P10");
    dbWrapper.updateFieldValue("program_products", "active", "f", "productid", productId);

    HomePage homePage = loginPage.loginAs("Admin123", "Admin123");
    ProgramProductISAPage programProductISAPage = homePage.navigateProgramProductISA();
    programProductISAPage.fillProgramProductISA(childCoverageData.get(VACCINES_PROGRAM), "50", "1", "50", "30", "0", "100", "2000", "333");
    homePage.logout();

    homePage = loginPage.loginAs(childCoverageData.get(USER), childCoverageData.get(PASSWORD));
    DistributionPage distributionPage = homePage.navigateToDistributionWhenOnline();
    distributionPage.initiate(childCoverageData.get(FIRST_DELIVERY_ZONE_NAME), childCoverageData.get(VACCINES_PROGRAM));
    FacilityListPage facilityListPage = distributionPage.clickRecordData(1);
    VisitInformationPage visitInformationPage = facilityListPage.selectFacility(childCoverageData.get(FIRST_FACILITY_CODE));
    ChildCoveragePage childCoveragePage = visitInformationPage.navigateToChildCoverage();

    verifyRegimentsPresent();
    verifyOpenVialsPresent();
    assertEquals(childCoveragePage.getTextOfTargetGroupValue(9), "167");
    assertEquals(childCoveragePage.getTextOfTargetGroupValue(10), "167");
    assertEquals(childCoveragePage.getTextOfTargetGroupValue(11), "167");
    assertEquals(childCoveragePage.getTextOfTargetGroupValue(1), "");
    assertEquals(childCoveragePage.getTextOfTargetGroupValue(2), "");

    dbWrapper.updateFieldValue("program_products", "active", "t", "productid", productId);

  }

  @Test(groups = {"distribution"})
  public void testShouldVerifyAllRegimensWhenMoreThan12RegimensInMappingTable() throws SQLException {
    dbWrapper.insertRegimensProductsInMappingTable("Antibiotic", "BCG");
    dbWrapper.insertRegimensProductsInMappingTable("Glycerine", "P11");
    dbWrapper.insertRegimensProductsInMappingTable("Paracetamol", "P10");

    HomePage homePage = loginPage.loginAs(childCoverageData.get(USER), childCoverageData.get(PASSWORD));
    DistributionPage distributionPage = homePage.navigateToDistributionWhenOnline();
    distributionPage.initiate(childCoverageData.get(FIRST_DELIVERY_ZONE_NAME), childCoverageData.get(VACCINES_PROGRAM));
    FacilityListPage facilityListPage = distributionPage.clickRecordData(1);
    VisitInformationPage visitInformationPage = facilityListPage.selectFacility(childCoverageData.get(FIRST_FACILITY_CODE));
    ChildCoveragePage childCoveragePage = visitInformationPage.navigateToChildCoverage();

    verifyRegimentsPresent();
    verifyOpenVialsPresent();
    assertFalse(childCoveragePage.getTextOfChildCoverageTable().contains("Antibiotic"));
    assertFalse(childCoveragePage.getTextOfChildCoverageTable().contains("Glycerine"));
    assertFalse(childCoveragePage.getTextOfChildCoverageTable().contains("Paracetamol"));
  }


  @Test(groups = {"distribution"})
  public void testShouldVerifyAllRegimensWhenLessThan12RegimensInMappingTable() throws SQLException {
    dbWrapper.deleteRowFromTable("coverage_vaccination_products", "vaccination", "BCG");
    dbWrapper.deleteRowFromTable("coverage_vaccination_products", "vaccination", "Measles");

    HomePage homePage = loginPage.loginAs(childCoverageData.get(USER), childCoverageData.get(PASSWORD));
    DistributionPage distributionPage = homePage.navigateToDistributionWhenOnline();
    distributionPage.initiate(childCoverageData.get(FIRST_DELIVERY_ZONE_NAME), childCoverageData.get(VACCINES_PROGRAM));
    FacilityListPage facilityListPage = distributionPage.clickRecordData(1);
    VisitInformationPage visitInformationPage = facilityListPage.selectFacility(childCoverageData.get(FIRST_FACILITY_CODE));
    visitInformationPage.navigateToChildCoverage();

    verifyRegimentsPresent();
    verifyOpenVialsPresent();
  }

  @Test(groups = {"distribution"})
  public void testShouldVerifyAllRegimensWhenInsertedInDifferentOrderInMappingTableAndMappedToPullTypePrograms() throws SQLException {
    dbWrapper.deleteRowFromTable("coverage_vaccination_products", "vaccination", "BCG");
    dbWrapper.deleteRowFromTable("coverage_vaccination_products", "vaccination", "Measles");
    dbWrapper.insertRegimensProductsInMappingTable("Measles", "P10");
    dbWrapper.insertRegimensProductsInMappingTable("BCG", "P10");

    HomePage homePage = loginPage.loginAs(childCoverageData.get(USER), childCoverageData.get(PASSWORD));
    DistributionPage distributionPage = homePage.navigateToDistributionWhenOnline();
    distributionPage.initiate(childCoverageData.get(FIRST_DELIVERY_ZONE_NAME), childCoverageData.get(VACCINES_PROGRAM));
    FacilityListPage facilityListPage = distributionPage.clickRecordData(1);
    VisitInformationPage visitInformationPage = facilityListPage.selectFacility(childCoverageData.get(FIRST_FACILITY_CODE));
    visitInformationPage.navigateToChildCoverage();

    verifyRegimentsPresent();
    verifyOpenVialsPresent();
  }

  @Test(groups = {"distribution"})
  public void testShouldVerifyAllRegimensWhenRegimenMappedToProductInactiveAtGlobalLevelAfterCaching() throws SQLException {
    HomePage homePage = loginPage.loginAs("Admin123", "Admin123");
    ProgramProductISAPage programProductISAPage = homePage.navigateProgramProductISA();
    programProductISAPage.fillProgramProductISA(childCoverageData.get(VACCINES_PROGRAM), "90", "1", "50", "30", "0", "100", "2000", "333");
    homePage.logout();

    homePage = loginPage.loginAs(childCoverageData.get(USER), childCoverageData.get(PASSWORD));
    DistributionPage distributionPage = homePage.navigateToDistributionWhenOnline();
    distributionPage.initiate(childCoverageData.get(FIRST_DELIVERY_ZONE_NAME), childCoverageData.get(VACCINES_PROGRAM));
    FacilityListPage facilityListPage = distributionPage.clickRecordData(1);
    VisitInformationPage visitInformationPage = facilityListPage.selectFacility(childCoverageData.get(FIRST_FACILITY_CODE));
    ChildCoveragePage childCoveragePage = visitInformationPage.navigateToChildCoverage();

    dbWrapper.updateFieldValue("products", "active", "f", "code", "P10");

    verifyRegimentsPresent();
    verifyOpenVialsPresent();
    assertEquals(childCoveragePage.getTextOfTargetGroupValue(9), "300");
    assertEquals(childCoveragePage.getTextOfTargetGroupValue(10), "300");
    assertEquals(childCoveragePage.getTextOfTargetGroupValue(11), "300");
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
    assertFalse(childCoveragePage.isOpenVialEnabled(1, 0));
    assertFalse(childCoveragePage.isOpenVialEnabled(1, 1));
    childCoveragePage.applyNrToPolioOpenedVials();
    assertTrue(childCoveragePage.isOpenVialEnabled(1, 0));
    assertTrue(childCoveragePage.isOpenVialEnabled(1, 1));
    childCoveragePage.enterOpenedVialsCountForGivenGroupAndRow(1, 0, "5");

    childCoveragePage.enterHealthCenter11MonthsDataForGivenRow(0, "10");
    assertEquals("10", childCoveragePage.getTotalForGivenColumnAndRow(0, 0));
    assertEquals("0", childCoveragePage.getTotalForGivenColumnAndRow(1, 0));
    assertEquals("10", childCoveragePage.getTotalForGivenColumnAndRow(2, 0));

    childCoveragePage.enterHealthCenter23MonthsDataForGivenRow(0, "032");
    assertEquals("10", childCoveragePage.getTotalForGivenColumnAndRow(0, 0));
    assertEquals("32", childCoveragePage.getTotalForGivenColumnAndRow(1, 0));
    assertEquals("42", childCoveragePage.getTotalForGivenColumnAndRow(2, 0));

    childCoveragePage.enterOutReach11MonthsDataForGivenRow(0, "27");
    assertEquals("37", childCoveragePage.getTotalForGivenColumnAndRow(0, 0));
    assertEquals("32", childCoveragePage.getTotalForGivenColumnAndRow(1, 0));
    assertEquals("69", childCoveragePage.getTotalForGivenColumnAndRow(2, 0));

    childCoveragePage.enterOutReach23MonthsDataForGivenRow(0, "0");
    assertEquals("37", childCoveragePage.getTotalForGivenColumnAndRow(0, 0));
    assertEquals("32", childCoveragePage.getTotalForGivenColumnAndRow(1, 0));
    assertEquals("69", childCoveragePage.getTotalForGivenColumnAndRow(2, 0));

    childCoveragePage.applyNRToHealthCenter11MonthsForGivenRow(0);
    assertEquals("27", childCoveragePage.getTotalForGivenColumnAndRow(0, 0));
    assertEquals("32", childCoveragePage.getTotalForGivenColumnAndRow(1, 0));
    assertEquals("59", childCoveragePage.getTotalForGivenColumnAndRow(2, 0));

    childCoveragePage.applyNRToHealthCenter11MonthsForGivenRow(0);
    childCoveragePage.enterHealthCenter11MonthsDataForGivenRow(0, "9999999");
    childCoveragePage.enterHealthCenter23MonthsDataForGivenRow(0, "9999999");
    childCoveragePage.enterOutReach11MonthsDataForGivenRow(0, "9999999");
    childCoveragePage.enterOutReach23MonthsDataForGivenRow(0, "9999999");
    assertEquals("19999998", childCoveragePage.getTotalForGivenColumnAndRow(0, 0));
    assertEquals("19999998", childCoveragePage.getTotalForGivenColumnAndRow(1, 0));
    assertEquals("39999996", childCoveragePage.getTotalForGivenColumnAndRow(2, 0));
  }

  private void verifyOpenVialsPresent() {
    ChildCoveragePage childCoveragePage = PageFactory.getInstanceOfChildCoveragePage(testWebDriver);
    assertEquals(childCoveragePage.getTextOfOpenedVialsBCG(), "BCG");
    assertEquals(childCoveragePage.getTextOfOpenedVialsPolio10(), "Polio10");
    assertEquals(childCoveragePage.getTextOfOpenedVialsPolio20(), "Polio20");
    assertEquals(childCoveragePage.getTextOfOpenedVialsPenta1(), "Penta1");
    assertEquals(childCoveragePage.getTextOfOpenedVialsPenta10(), "Penta10");
    assertEquals(childCoveragePage.getTextOfOpenedVialsPCV(), "PCV");
    assertEquals(childCoveragePage.getTextOfOpenedVialsMeasles(), "Measles");
  }

  private void verifyHeadersPresent() {
    ChildCoveragePage childCoveragePage = PageFactory.getInstanceOfChildCoveragePage(testWebDriver);
    assertEquals(childCoveragePage.getTextOfHeaderChildrenVaccination(), "Child Vaccinations (doses)");
    assertEquals(childCoveragePage.getTextOfHeaderTargetGroup(), "Target Group");
    assertEquals(childCoveragePage.getTextOfHeaderHealthCenter1(), "Health Center");
    assertEquals(childCoveragePage.getTextOfHeaderMobileBrigade1(), "Mobile Brigade");
    assertEquals(childCoveragePage.getTextOfHeaderTotal1(), "Total");
    assertEquals(childCoveragePage.getTextOfHeaderCoverageRate(), "Coverage Rate");
    assertEquals(childCoveragePage.getTextOfHeaderHealthCenter2(), "Health Center");
    assertEquals(childCoveragePage.getTextOfHeaderMobileBrigade2(), "Mobile Brigade");
    assertEquals(childCoveragePage.getTextOfHeaderTotal2(), "Total");
    assertEquals(childCoveragePage.getTextOfHeaderTotalVaccination(), "Total Vaccination");
    assertEquals(childCoveragePage.getTextOfHeaderOpenedVials(), "Opened Vials");
    assertEquals(childCoveragePage.getTextOfHeaderWastageRate(), "Opened Vials Wastage Rate");
    assertEquals(childCoveragePage.getTextOfHeaderCategory1(), "0-11 months");
    assertEquals(childCoveragePage.getTextOfHeaderCategory2(), "12-23 months");
  }

  private void verifyRegimentsPresent() {
    ChildCoveragePage childCoveragePage = PageFactory.getInstanceOfChildCoveragePage(testWebDriver);
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
    dbWrapper.setUpDataForChildCoverage();
    insertRegimenProductMapping();
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
