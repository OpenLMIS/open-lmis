package org.openlmis.functional;

import cucumber.api.java.en.And;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import org.openlmis.UiUtils.TestCaseHelper;
import org.openlmis.pageobjects.HomePage;
import org.openlmis.pageobjects.LoginPage;
import org.openlmis.pageobjects.ManagePodPage;
import org.openlmis.pageobjects.PageObjectFactory;
import org.openlmis.pageobjects.edi.ConvertOrderPage;
import org.openqa.selenium.By;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.io.IOException;
import java.sql.SQLException;
import java.text.NumberFormat;
import java.util.List;

import static com.thoughtworks.selenium.SeleneseTestBase.assertFalse;
import static com.thoughtworks.selenium.SeleneseTestBase.assertTrue;
import static java.util.Arrays.asList;
import static org.testng.AssertJUnit.assertEquals;

public class ManagePod extends TestCaseHelper {

  public static final String CONVERT_TO_ORDER = "CONVERT_TO_ORDER";
  public static final String CREATE_REQUISITION = "CREATE_REQUISITION";
  public static final String AUTHORIZED = "AUTHORIZED";
  public static final String VIEW_REQUISITION = "VIEW_REQUISITION";
  public static final String VIEW_ORDER = "VIEW_ORDER";
  public static final String MANAGE_POD = "MANAGE_POD";
  public LoginPage loginPage;

  @BeforeMethod(groups = "orderAndPod")
  public void setUp() throws InterruptedException, SQLException, IOException {
    super.setup();
    dbWrapper.deleteData();
    loginPage = PageObjectFactory.getLoginPage(testWebDriver, baseUrlGlobal);
  }

  @Test(groups = {"orderAndPod"}, dataProvider = "Data-Provider-Function-RnR")
  public void testVerifyManagePODValidFlowForRegularRnR(String program, String userSIC, String password) throws SQLException {
    setUpData(program, userSIC);

    HomePage homePage = loginPage.loginAs(userSIC, password);
    ConvertOrderPage convertOrderPage = homePage.navigateConvertToOrder();
    convertOrderPage.selectRequisitionToBeConvertedToOrder(1);
    convertOrderPage.clickConvertToOrderButton();
    convertOrderPage.clickOk();
    ManagePodPage managePodPage = homePage.navigateManagePOD();
    verifyHeadersOnManagePODScreen(managePodPage);
    verifyValuesOnManagePODScreen(getOrderNumber("O", "MALARIA", "R"));
  }

  @Test(groups = {"orderAndPod"}, dataProvider = "Data-Provider-Function-RnR")
  public void testVerifyManagePODWhenSupplyLineMissing(String program, String userSIC, String password) throws SQLException {
    setUpData(program, userSIC);
    dbWrapper.deleteSupplyLine();

    HomePage homePage = loginPage.loginAs(userSIC, password);
    ConvertOrderPage convertOrderPage = homePage.navigateConvertToOrder();
    convertOrderPage.selectRequisitionToBeConvertedToOrder(1);
    convertOrderPage.clickConvertToOrderButton();
    convertOrderPage.clickOk();
    assertFalse(convertOrderPage.isNoRequisitionPendingMessageDisplayed());
  }

  @Test(groups = {"orderAndPod"}, dataProvider = "Data-Provider-Function-RnR")
  public void testVerifyManagePODValidFlowForEmergencyRnR(String program, String userSIC, String password) throws SQLException {
    setUpData(program, userSIC);
    dbWrapper.updateFieldValue("requisitions", "Emergency", true);
    dbWrapper.updateFieldValue("order_number_configuration", "orderNumberPrefix", "#Ord 3", "orderNumberPrefix", "O");

    HomePage homePage = loginPage.loginAs(userSIC, password);
    ConvertOrderPage convertOrderPage = homePage.navigateConvertToOrder();
    convertOrderPage.selectRequisitionToBeConvertedToOrder(1);
    convertOrderPage.clickConvertToOrderButton();
    convertOrderPage.clickOk();
    ManagePodPage managePodPage = homePage.navigateManagePOD();
    verifyHeadersOnManagePODScreen(managePodPage);
    assertTrue(testWebDriver.findElement(By.xpath("//i[@class='icon-ok']")).isDisplayed());
    verifyValuesOnManagePODScreen(getOrderNumber("#Ord 3", "MALARIA", "E"));
    dbWrapper.updateFieldValue("order_number_configuration", "orderNumberPrefix", "O", "orderNumberPrefix", "#Ord 3");
  }

  @Test(groups = {"orderAndPod"}, dataProvider = "Data-Provider-Function-RnR")
  public void testManagePODWhenRequisitionNotConvertedToOrder(String program, String userSIC, String password) throws SQLException {
    setUpData(program, userSIC);

    HomePage homePage = loginPage.loginAs(userSIC, password);
    ManagePodPage managePodPage = homePage.navigateManagePOD();
    managePodPage.verifyNoOrderMessage();
  }

  @Test(groups = {"orderAndPod"}, dataProvider = "Data-Provider-Function-RnR")
  public void testManagePODWhenPodAlreadySubmitted(String program, String userSIC, String password) throws SQLException {
    setUpData(program, userSIC);

    HomePage homePage = loginPage.loginAs(userSIC, password);
    dbWrapper.insertOrders("RELEASED", userSIC, "MALARIA");
    ManagePodPage managePodPage = homePage.navigateManagePOD();
    dbWrapper.updateFieldValue("orders", "status", "RECEIVED", null, null);
    homePage.navigateHomePage();
    homePage.navigateManagePOD();
    managePodPage.verifyNoOrderMessage();
  }

  @Test(groups = {"orderAndPod"}, dataProvider = "Data-Provider-Function-RnR")
  public void testManagePODWhenManagePodRightsNotAssigned(String program, String userSIC, String password) throws SQLException {
    setupProductTestData("P10", "P11", program, "lvl3_hospital");
    dbWrapper.insertFacilities("F10", "F11");
    dbWrapper.configureTemplate(program);
    List<String> rightsList = asList(VIEW_REQUISITION);
    setupTestUserRoleRightsData(userSIC, rightsList);
    dbWrapper.insertSupervisoryNode("F10", "N1", "Node 1", "null");
    dbWrapper.insertRoleAssignment(userSIC, "store in-charge");
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

  @When("^I access Manage POD page$")
  public void navigateManagePodPage() {
    HomePage homePage = PageObjectFactory.getHomePage(testWebDriver);
    homePage.navigateManagePOD();
  }

  @Then("^I should see list of orders to manage POD for \"([^\"]*)\" Rnr$")
  public void verifyListOfOrdersOnPodScreen(String rnrType) throws SQLException {
    NumberFormat numberFormat = NumberFormat.getIntegerInstance();
    numberFormat.setMinimumIntegerDigits(8);
    numberFormat.setGroupingUsed(false);
    int id = dbWrapper.getMaxRnrID();
    String orderNumber = "OHIV" + numberFormat.format(id) + "R";
    if (rnrType.equals("Emergency")) {
      orderNumber = "OHIV" + numberFormat.format(id) + "E";
    }
    testWebDriver.sleep(1000);
    assertEquals("Central Hospital", testWebDriver.findElement(By.xpath("//div/span[contains(text(),'Central Hospital')]")).getText());
    assertEquals(orderNumber, testWebDriver.findElement(By.xpath("//div/span[contains(text(),'" + orderNumber + "')]")).getText());
    assertEquals("Transfer failed", testWebDriver.findElement(By.xpath("//div/span[contains(text(),'Transfer failed')]")).getText());
    assertTrue(testWebDriver.findElement(By.xpath("//div/span[contains(text(),'Period1')]")).getText().contains("Period1"));
    assertEquals("Update POD", testWebDriver.findElement(By.xpath("//div/a[contains(text(),'Update POD')]")).getText());
    //TODO find proper xpath or give id for facility_code
    if (rnrType.equals("Emergency")) {
      assertTrue(testWebDriver.findElement(By.xpath("//i[@class='icon-ok']")).isDisplayed());
    }
  }

  @And("^I verify order not present on manage pod page$")
  public void verifyNoOrderPresent() {
    ManagePodPage managePodPage = PageObjectFactory.getManagePodPage(testWebDriver);
    managePodPage.verifyNoOrderMessage();
  }

  private void verifyValuesOnManagePODScreen(String orderNumber) throws SQLException {
    ManagePodPage managePodPage = PageObjectFactory.getManagePodPage(testWebDriver);
    assertEquals(orderNumber, managePodPage.getOrderNumber(1));
    assertEquals("F10 - Village Dispensary", managePodPage.getFacilityCodeName());
    assertEquals("Village Dispensary", managePodPage.getSupplyingDepotName());
    assertEquals("MALARIA", managePodPage.getProgramCodeName(1));
    assertEquals("Transfer failed", managePodPage.getOrderStatusDetails());
    assertEquals("PeriodName1 (01/12/2012 - 01/12/2015)", managePodPage.getPeriodDetails());
    assertEquals("Update POD", managePodPage.getUpdatePodLink());
  }

  private String getOrderNumber(String prefix, String program, String type) throws SQLException {
    NumberFormat numberFormat = NumberFormat.getIntegerInstance();
    numberFormat.setMinimumIntegerDigits(8);
    numberFormat.setGroupingUsed(false);
    int id = dbWrapper.getMaxRnrID();
    return prefix + program.substring(0, Math.min(program.length(), 35)) + numberFormat.format(id) + type.substring(0, 1);
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
    setupTestUserRoleRightsData(userSIC, rightsList);
    dbWrapper.insertSupervisoryNode("F10", "N1", "Node 1", "null");
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

  @AfterMethod(groups = "orderAndPod")
  public void tearDown() throws SQLException {
    testWebDriver.sleep(500);
    if (!testWebDriver.getElementById("username").isDisplayed()) {
      HomePage homePage = PageObjectFactory.getHomePage(testWebDriver);
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
