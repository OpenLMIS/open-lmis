package org.openlmis.functional;

import org.openlmis.UiUtils.TestCaseHelper;
import org.openlmis.pageobjects.HomePage;
import org.openlmis.pageobjects.LoginPage;
import org.openlmis.pageobjects.ManagePodPage;
import org.openlmis.pageobjects.edi.ConvertOrderPage;
import org.openqa.selenium.By;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

import static com.thoughtworks.selenium.SeleneseTestBase.*;
import static java.util.Arrays.asList;

public class ManagePod extends TestCaseHelper {

  public static final String CONVERT_TO_ORDER = "CONVERT_TO_ORDER";
  public static final String CREATE_REQUISITION = "CREATE_REQUISITION";
  public static final String AUTHORIZED = "AUTHORIZED";
  public static final String VIEW_REQUISITION = "VIEW_REQUISITION";
  public static final String VIEW_ORDER = "VIEW_ORDER";
  public static final String MANAGE_POD = "MANAGE_POD";
  public LoginPage loginPage;

  @BeforeMethod(groups = "requisition")
  public void setUp() throws InterruptedException, SQLException, IOException {
    super.setup();
    dbWrapper.deleteData();
    loginPage = PageFactory.getInstanceOfLoginPage(testWebDriver, baseUrlGlobal);
  }

  @Test(groups = {"requisition"}, dataProvider = "Data-Provider-Function-RnR")
  public void testVerifyManagePODValidFlowForRegularRnR(String program, String userSIC, String password) throws SQLException {
    setUpData(program, userSIC);

    HomePage homePage = loginPage.loginAs(userSIC, password);
    ConvertOrderPage convertOrderPage = homePage.navigateConvertToOrder();
    convertOrderPage.selectRequisitionToBeConvertedToOrder(1);
    convertOrderPage.clickConvertToOrderButton();
    convertOrderPage.clickOk();
    ManagePodPage managePodPage = homePage.navigateManagePOD();
    verifyHeadersOnManagePODScreen(managePodPage);
    verifyValuesOnManagePODScreen(managePodPage);
  }

  @Test(groups = {"requisition"}, dataProvider = "Data-Provider-Function-RnR")
  public void testVerifyManagePODWhenSupplyLineMissing(String program, String userSIC, String password) throws SQLException {
    setUpData(program, userSIC);
    dbWrapper.deleteSupplyLine();

    HomePage homePage = loginPage.loginAs(userSIC, password);
    ConvertOrderPage convertOrderPage = homePage.navigateConvertToOrder();
    convertOrderPage.selectRequisitionToBeConvertedToOrder(1);
    convertOrderPage.clickConvertToOrderButton();
    convertOrderPage.clickOk();
    ManagePodPage managePodPage = homePage.navigateManagePOD();
    managePodPage.verifyNoProductMessage();
    String id = String.valueOf(dbWrapper.getMaxRnrID());
    assertEquals("TRANSFER_FAILED", dbWrapper.getAttributeFromTable("orders", "status", "id", id));
    assertTrue(dbWrapper.getAttributeFromTable("orders", "ftpComment", "id", id).contains("supplyline.missing"));
  }

  @Test(groups = {"requisition"}, dataProvider = "Data-Provider-Function-RnR")
  public void testVerifyManagePODValidFlowForEmergencyRnR(String program, String userSIC, String password) throws SQLException {
    setUpData(program, userSIC);
    dbWrapper.updateFieldValue("requisitions", "Emergency", true);

    HomePage homePage = loginPage.loginAs(userSIC, password);
    ConvertOrderPage convertOrderPage = homePage.navigateConvertToOrder();
    convertOrderPage.selectRequisitionToBeConvertedToOrder(1);
    convertOrderPage.clickConvertToOrderButton();
    convertOrderPage.clickOk();
    ManagePodPage managePodPage = homePage.navigateManagePOD();
    verifyHeadersOnManagePODScreen(managePodPage);
    assertTrue(testWebDriver.findElement(By.xpath("//i[@class='icon-ok']")).isDisplayed());
    verifyValuesOnManagePODScreen(managePodPage);
  }

  @Test(groups = {"requisition"}, dataProvider = "Data-Provider-Function-RnR")
  public void testManagePODWhenRequisitionNotConvertedToOrder(String program, String userSIC, String password) throws SQLException {
    setUpData(program, userSIC);

    HomePage homePage = loginPage.loginAs(userSIC, password);
    ManagePodPage managePodPage = homePage.navigateManagePOD();
    managePodPage.verifyNoProductMessage();
  }

  @Test(groups = {"requisition"}, dataProvider = "Data-Provider-Function-RnR")
  public void testManagePODWhenPodAlreadySubmitted(String program, String userSIC, String password) throws SQLException {
    setUpData(program, userSIC);

    HomePage homePage = loginPage.loginAs(userSIC, password);
    dbWrapper.insertOrders("RELEASED", userSIC, "MALARIA");
    ManagePodPage managePodPage = homePage.navigateManagePOD();
    dbWrapper.updateFieldValue("orders", "status", "RECEIVED", null, null);
    homePage.navigateHomePage();
    homePage.navigateManagePOD();
    managePodPage.verifyNoProductMessage();
  }

  @Test(groups = {"requisition"}, dataProvider = "Data-Provider-Function-RnR")
  public void testManagePODWhenManagePodRightsNotAssigned(String program, String userSIC, String password) throws SQLException {
    setupProductTestData("P10", "P11", program, "lvl3_hospital");
    dbWrapper.insertFacilities("F10", "F11");
    dbWrapper.configureTemplate(program);
    List<String> rightsList = asList(VIEW_REQUISITION);
    setupTestUserRoleRightsData("200", userSIC, rightsList);
    dbWrapper.insertSupervisoryNode("F10", "N1", "Node 1", "null");
    dbWrapper.insertRoleAssignment("200", "store in-charge");
    dbWrapper.insertSchedule("Q1stM", "QuarterMonthly", "QuarterMonth");
    dbWrapper.insertSchedule("M", "Monthly", "Month");
    dbWrapper.insertProcessingPeriod("Period1", "first period", "2012-12-01", "2013-01-15", 1, "Q1stM");
    dbWrapper.insertProcessingPeriod("Period2", "second period", "2013-01-16", "2013-01-30", 1, "M");
    setupRequisitionGroupData("RG1", "RG2", "N1", "N2", "F10", "F11");
    dbWrapper.insertSupplyLines("N1", program, "F10", true);
    dbWrapper.insertRequisitions(1, "MALARIA", true, "2012-12-01", "2015-12-01", "F10", false);
    dbWrapper.updateRequisitionStatus("SUBMITTED", userSIC, "MALARIA");
    dbWrapper.updateRequisitionStatus("AUTHORIZED", userSIC, "MALARIA");
    dbWrapper.updateRequisitionStatus("APPROVED", userSIC, "MALARIA");

    HomePage homePage = loginPage.loginAs(userSIC, password);
    homePage.navigateHomePage();
    testWebDriver.sleep(1000);
    assertFalse(testWebDriver.findElement(By.id("orders-menu")).isDisplayed());
  }

  private void verifyValuesOnManagePODScreen(ManagePodPage managePodPage) {
    assertEquals("F10 - Village Dispensary", managePodPage.getFacilityCodeName());
    assertEquals("Village Dispensary", managePodPage.getSupplyingDepotName());
    assertEquals("MALARIA", managePodPage.getProgramCodeName());
    assertEquals("Transfer failed", managePodPage.getOrderStatusDetails());
    assertEquals("PeriodName1 (01/12/2012 - 01/12/2015)", managePodPage.getPeriodDetails());
    assertEquals("Update POD", managePodPage.getUpdatePodLink());
  }

  private void verifyHeadersOnManagePODScreen(ManagePodPage managePodPage) {
    assertEquals("Order No.", managePodPage.getHeaderOrderNo());
    assertEquals("Supplying Depot", managePodPage.getHeaderSupplyingDepotName());
    assertEquals("Program", managePodPage.getHeaderProgramCodeName());
    assertEquals("Period", managePodPage.getHeaderPeriodDetails());
    assertEquals("Order Date/Time", managePodPage.getHeaderOrderDateTimeDetails());
    assertEquals("Order Status", managePodPage.getHeaderOrderStatusDetails());
    assertEquals("Emergency", managePodPage.getHeaderEmergency());
  }

  private void setUpData(String program, String userSIC) throws SQLException {
    setupProductTestData("P10", "P11", program, "lvl3_hospital");
    dbWrapper.insertFacilities("F10", "F11");
    dbWrapper.configureTemplate(program);
    List<String> rightsList = asList(CONVERT_TO_ORDER, VIEW_ORDER, MANAGE_POD);
    setupTestUserRoleRightsData("200", userSIC, rightsList);
    dbWrapper.insertSupervisoryNode("F10", "N1", "Node 1", "null");
    dbWrapper.insertRoleAssignment("200", "store in-charge");
    dbWrapper.insertSchedule("Q1stM", "QuarterMonthly", "QuarterMonth");
    dbWrapper.insertSchedule("M", "Monthly", "Month");
    dbWrapper.insertProcessingPeriod("Period1", "first period", "2012-12-01", "2013-01-15", 1, "Q1stM");
    dbWrapper.insertProcessingPeriod("Period2", "second period", "2013-01-16", "2013-01-30", 1, "M");
    setupRequisitionGroupData("RG1", "RG2", "N1", "N2", "F10", "F11");
    dbWrapper.insertSupplyLines("N1", program, "F10", true);
    dbWrapper.insertRequisitions(1, "MALARIA", true, "2012-12-01", "2015-12-01", "F10", false);
    dbWrapper.updateRequisitionStatus("SUBMITTED", userSIC, "MALARIA");
    dbWrapper.updateRequisitionStatus("AUTHORIZED", userSIC, "MALARIA");
    dbWrapper.updateRequisitionStatus("APPROVED", userSIC, "MALARIA");
    dbWrapper.insertFulfilmentRoleAssignment("storeInCharge", "store in-charge", "F10");
  }

  @AfterMethod(groups = "requisition")
  public void tearDown() throws SQLException {
    testWebDriver.sleep(500);
    if (!testWebDriver.getElementById("username").isDisplayed()) {
      HomePage homePage = PageFactory.getInstanceOfHomePage(testWebDriver);
      homePage.logout(baseUrlGlobal);
      dbWrapper.deleteData();
      dbWrapper.closeConnection();
    }
  }

  @DataProvider(name = "Data-Provider-Function-RnR")
  public Object[][] parameterIntTestProviderRnR() {
    return new Object[][]{
      {"HIV", "storeInCharge", "Admin123"}
    };
  }
}
