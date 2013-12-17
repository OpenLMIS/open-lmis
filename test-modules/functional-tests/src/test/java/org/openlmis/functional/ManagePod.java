package org.openlmis.functional;

import com.thoughtworks.selenium.SeleneseTestBase;
import org.openlmis.UiUtils.TestCaseHelper;
import org.openlmis.pageobjects.HomePage;
import org.openlmis.pageobjects.LoginPage;
import org.openlmis.pageobjects.ManagePodPage;
import org.openlmis.pageobjects.edi.ConvertOrderPage;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.testng.annotations.*;

import java.util.ArrayList;
import java.util.List;

import static com.thoughtworks.selenium.SeleneseTestBase.assertEquals;
import static com.thoughtworks.selenium.SeleneseTestBase.assertFalse;
import static com.thoughtworks.selenium.SeleneseTestBase.assertTrue;

public class ManagePod extends TestCaseHelper {

  public static final String APPROVE_REQUISITION = "APPROVE_REQUISITION";
  public static final String CONVERT_TO_ORDER = "CONVERT_TO_ORDER";
  public static final String CREATE_REQUISITION = "CREATE_REQUISITION";
  public static final String AUTHORIZED = "AUTHORIZED";
  public static final String AUTHORIZE_REQUISITION = "AUTHORIZE_REQUISITION";
  public static final String VIEW_REQUISITION = "VIEW_REQUISITION";
  public static final String VIEW_ORDER = "VIEW_ORDER";
  public static final String MANAGE_POD = "MANAGE_POD";
  public LoginPage loginPage;

  @BeforeMethod(groups = "requisition")
  @cucumber.api.java.Before
  public void setUp() throws Exception {
    super.setup();
    dbWrapper.deleteData();
  }

  @Test(groups = {"requisition"}, dataProvider = "Data-Provider-Function-RnR")
  public void testVerifyManagePODValidFlowForRegularRnR(String program, String userSIC, String password) throws Exception {
    setUpData(program, userSIC);
    dbWrapper.insertRequisitions(1, "MALARIA", true);
    dbWrapper.updateRequisitionStatus("SUBMITTED", userSIC, "MALARIA");
    dbWrapper.updateRequisitionStatus("AUTHORIZED", userSIC, "MALARIA");
    dbWrapper.updateRequisitionStatus("APPROVED", userSIC, "MALARIA");
    dbWrapper.insertFulfilmentRoleAssignment("storeIncharge", "store in-charge", "F10");

    LoginPage loginPage = new LoginPage(testWebDriver, baseUrlGlobal);
    HomePage homePage = loginPage.loginAs(userSIC, password);
    ConvertOrderPage convertOrderPage = homePage.navigateConvertToOrder();
    selectRequisitionToBeConvertedToOrder(1);
    convertOrderPage.clickConvertToOrderButton();
    convertOrderPage.clickOk();
    ManagePodPage managePodPage = homePage.navigateManagePOD();
    verifyHeadersOnManagePODScreen(managePodPage);
    verifyValuesOnManagePODScreen(managePodPage);
  }


  @Test(groups = {"requisition"}, dataProvider = "Data-Provider-Function-RnR")
  public void testVerifyManagePODWhenSupplyLineMissing(String program, String userSIC, String password) throws Exception {
    setUpData(program, userSIC);
    dbWrapper.insertRequisitions(1, "MALARIA", true);
    dbWrapper.updateRequisitionStatus("SUBMITTED", userSIC, "MALARIA");
    dbWrapper.updateRequisitionStatus("AUTHORIZED", userSIC, "MALARIA");
    dbWrapper.updateRequisitionStatus("APPROVED", userSIC, "MALARIA");
    dbWrapper.insertFulfilmentRoleAssignment("storeIncharge", "store in-charge", "F10");
    dbWrapper.deleteSupplyLine();
    LoginPage loginPage = new LoginPage(testWebDriver, baseUrlGlobal);
    HomePage homePage = loginPage.loginAs(userSIC, password);
    ConvertOrderPage convertOrderPage = homePage.navigateConvertToOrder();
    selectRequisitionToBeConvertedToOrder(1);
    convertOrderPage.clickConvertToOrderButton();
    convertOrderPage.clickOk();
    ManagePodPage managePodPage = homePage.navigateManagePOD();
    managePodPage.verifyMessageOnManagePodScreen();
    Integer id=dbWrapper.getMaxRnrID();
    assertEquals("TRANSFER_FAILED",dbWrapper.getOrderStatus((long)id));
    assertTrue(dbWrapper.getOrderFtpComment((long)id).contains("supplyline.missing"));
   }

  @Test(groups = {"requisition"}, dataProvider = "Data-Provider-Function-RnR")
  public void testVerifyManagePODValidFlowForEmergencyRnR(String program, String userSIC, String password) throws Exception {
    setUpData(program, userSIC);
    dbWrapper.insertRequisitions(1, "MALARIA", true);
    dbWrapper.updateRequisitionToEmergency();
    dbWrapper.updateRequisitionStatus("SUBMITTED", userSIC, "MALARIA");
    dbWrapper.updateRequisitionStatus("AUTHORIZED", userSIC, "MALARIA");
    dbWrapper.updateRequisitionStatus("APPROVED", userSIC, "MALARIA");
    dbWrapper.insertFulfilmentRoleAssignment("storeIncharge", "store in-charge", "F10");

    LoginPage loginPage = new LoginPage(testWebDriver, baseUrlGlobal);
    HomePage homePage = loginPage.loginAs(userSIC, password);
    ConvertOrderPage convertOrderPage = homePage.navigateConvertToOrder();
    selectRequisitionToBeConvertedToOrder(1);
    convertOrderPage.clickConvertToOrderButton();
    convertOrderPage.clickOk();
    ManagePodPage managePodPage = homePage.navigateManagePOD();
    verifyHeadersOnManagePODScreen(managePodPage);
    assertTrue(testWebDriver.findElement(By.xpath("//i[@class='icon-ok']")).isDisplayed());
    verifyValuesOnManagePODScreen(managePodPage);
  }

  @Test(groups = {"requisition"}, dataProvider = "Data-Provider-Function-RnR")
  public void testManagePODWhenRequisitionNotConvertedToOrder(String program, String userSIC, String password) throws Exception {
    setUpData(program, userSIC);
    dbWrapper.insertRequisitions(1, "MALARIA", true);
    dbWrapper.updateRequisitionStatus("SUBMITTED", userSIC, "MALARIA");
    dbWrapper.updateRequisitionStatus("AUTHORIZED", userSIC, "MALARIA");
    dbWrapper.updateRequisitionStatus("APPROVED", userSIC, "MALARIA");
    dbWrapper.insertFulfilmentRoleAssignment("storeIncharge", "store in-charge", "F10");

    LoginPage loginPage = new LoginPage(testWebDriver, baseUrlGlobal);
    HomePage homePage = loginPage.loginAs(userSIC, password);
    ManagePodPage managePodPage = homePage.navigateManagePOD();
    managePodPage.verifyMessageOnManagePodScreen();
  }

  @Test(groups = {"requisition"}, dataProvider = "Data-Provider-Function-RnR")
  public void testManagePODWhenPodAlreadySubmitted(String program, String userSIC, String password) throws Exception {
    setUpData(program, userSIC);
    dbWrapper.insertRequisitions(1, "MALARIA", true);
    dbWrapper.updateRequisitionStatus("SUBMITTED", userSIC, "MALARIA");
    dbWrapper.updateRequisitionStatus("AUTHORIZED", userSIC, "MALARIA");
    dbWrapper.updateRequisitionStatus("APPROVED", userSIC, "MALARIA");
    dbWrapper.insertFulfilmentRoleAssignment("storeIncharge", "store in-charge", "F10");

    LoginPage loginPage = new LoginPage(testWebDriver, baseUrlGlobal);
    HomePage homePage = loginPage.loginAs(userSIC, password);
    dbWrapper.insertOrders("RELEASED", userSIC, "MALARIA");
    ManagePodPage managePodPage = homePage.navigateManagePOD();
    dbWrapper.updateOrderStatus("RECEIVED", userSIC, "MALARIA");
    homePage.navigateHomePage();
    homePage.navigateManagePOD();
    managePodPage.verifyMessageOnManagePodScreen();
  }

  @Test(groups = {"requisition"}, dataProvider = "Data-Provider-Function-RnR")
  public void testManagePODWhenManagePodRightsNotAssigned(String program, String userSIC, String password) throws Exception {
    setupProductTestData("P10", "P11", program, "lvl3_hospital");
    dbWrapper.insertFacilities("F10", "F11");
    dbWrapper.configureTemplate(program);
    List<String> rightsList = new ArrayList<>();
    rightsList.add(VIEW_REQUISITION);
    setupTestUserRoleRightsData("200", userSIC, rightsList);
    dbWrapper.insertSupervisoryNode("F10", "N1", "Node 1", "null");
    dbWrapper.insertRoleAssignment("200", "store in-charge");
    dbWrapper.insertSchedule("Q1stM", "QuarterMonthly", "QuarterMonth");
    dbWrapper.insertSchedule("M", "Monthly", "Month");
    dbWrapper.insertProcessingPeriod("Period1", "first period", "2012-12-01", "2013-01-15", 1, "Q1stM");
    dbWrapper.insertProcessingPeriod("Period2", "second period", "2013-01-16", "2013-01-30", 1, "M");
    setupRequisitionGroupData("RG1", "RG2", "N1", "N2", "F10", "F11");
    dbWrapper.insertSupplyLines("N1", program, "F10", true);
    dbWrapper.insertRequisitions(1, "MALARIA", true);
    dbWrapper.updateRequisitionStatus("SUBMITTED", userSIC, "MALARIA");
    dbWrapper.updateRequisitionStatus("AUTHORIZED", userSIC, "MALARIA");
    dbWrapper.updateRequisitionStatus("APPROVED", userSIC, "MALARIA");
    LoginPage loginPage = new LoginPage(testWebDriver, baseUrlGlobal);
    HomePage homePage = loginPage.loginAs(userSIC, password);
    homePage.navigateHomePage();
    testWebDriver.sleep(1000);
    assertFalse(testWebDriver.findElement(By.id("orders-menu")).isDisplayed());
  }

  private void verifyValuesOnManagePODScreen(ManagePodPage managePodPage) {
    SeleneseTestBase.assertEquals("F10 - Village Dispensary", managePodPage.getFacilityCodeName());
    SeleneseTestBase.assertEquals("Village Dispensary", managePodPage.getSupplyingDepotName());
    SeleneseTestBase.assertEquals("MALARIA", managePodPage.getProgramCodeName());
    SeleneseTestBase.assertEquals("Transfer failed", managePodPage.getOrderStatusDetails());
    SeleneseTestBase.assertEquals("PeriodName1 (01/12/2012 - 01/12/2015)", managePodPage.getPeriodDetails());
    SeleneseTestBase.assertEquals("Update POD", managePodPage.getUpdatePodLink());
  }

  private void verifyHeadersOnManagePODScreen(ManagePodPage managePodPage) {
    SeleneseTestBase.assertEquals("Order No.", managePodPage.getHeaderOrderNo());
    SeleneseTestBase.assertEquals("Supplying Depot", managePodPage.getHeaderSupplyingDepotName());
    SeleneseTestBase.assertEquals("Program", managePodPage.getHeaderProgramCodeName());
    SeleneseTestBase.assertEquals("Period", managePodPage.getHeaderPeriodDetails());
    SeleneseTestBase.assertEquals("Order Date/Time", managePodPage.getHeaderOrderDateTimeDetails());
    SeleneseTestBase.assertEquals("Order Status", managePodPage.getHeaderOrderStatusDetails());
    SeleneseTestBase.assertEquals("Emergency", managePodPage.getHeaderEmergency());
  }

  private void setUpData(String program, String userSIC) throws Exception {
    setupProductTestData("P10", "P11", program, "lvl3_hospital");
    dbWrapper.insertFacilities("F10", "F11");
    dbWrapper.configureTemplate(program);
    List<String> rightsList = new ArrayList<>();
    rightsList.add(CONVERT_TO_ORDER);
    rightsList.add(VIEW_ORDER);
    rightsList.add(MANAGE_POD);
    setupTestUserRoleRightsData("200", userSIC, rightsList);
    dbWrapper.insertSupervisoryNode("F10", "N1", "Node 1", "null");
    dbWrapper.insertRoleAssignment("200", "store in-charge");
    dbWrapper.insertSchedule("Q1stM", "QuarterMonthly", "QuarterMonth");
    dbWrapper.insertSchedule("M", "Monthly", "Month");
    dbWrapper.insertProcessingPeriod("Period1", "first period", "2012-12-01", "2013-01-15", 1, "Q1stM");
    dbWrapper.insertProcessingPeriod("Period2", "second period", "2013-01-16", "2013-01-30", 1, "M");
    setupRequisitionGroupData("RG1", "RG2", "N1", "N2", "F10", "F11");
    dbWrapper.insertSupplyLines("N1", program, "F10", true);
  }

  public void selectRequisitionToBeConvertedToOrder(int whichRequisition) {
    testWebDriver.waitForPageToLoad();
    List<WebElement> x = testWebDriver.getElementsByXpath("//input[@class='ngSelectionCheckbox']");
    testWebDriver.waitForElementToAppear(x.get(whichRequisition - 1));
    x.get(whichRequisition - 1).click();
  }

  @AfterMethod(groups = "requisition")
  @cucumber.api.java.After
  public void tearDown() throws Exception {
    testWebDriver.sleep(500);
    if (!testWebDriver.getElementById("username").isDisplayed()) {
      HomePage homePage = new HomePage(testWebDriver);
      homePage.logout(baseUrlGlobal);
      dbWrapper.deleteData();
      dbWrapper.closeConnection();
    }

  }

  @DataProvider(name = "Data-Provider-Function-RnR")
  public Object[][] parameterIntTestProviderRnR() {
    return new Object[][]{
      {"HIV", "storeIncharge", "Admin123"}
    };
  }

}
