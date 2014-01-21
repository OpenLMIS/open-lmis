package org.openlmis.functional;

import org.openlmis.UiUtils.TestCaseHelper;
import org.openlmis.pageobjects.HomePage;
import org.openlmis.pageobjects.InitiateRnRPage;
import org.openlmis.pageobjects.LoginPage;
import org.openlmis.pageobjects.UpdatePodPage;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.io.IOException;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static com.thoughtworks.selenium.SeleneseTestBase.assertTrue;
import static org.testng.AssertJUnit.assertFalse;

public class UpdatePod extends TestCaseHelper {

  public static final String CONVERT_TO_ORDER = "CONVERT_TO_ORDER";
  public static final String CREATE_REQUISITION = "CREATE_REQUISITION";
  public static final String AUTHORIZED = "AUTHORIZED";
  public static final String VIEW_REQUISITION = "VIEW_REQUISITION";
  public static final String VIEW_ORDER = "VIEW_ORDER";
  public static final String MANAGE_POD = "MANAGE_POD";
  public static final String SUBMITTED = "SUBMITTED";
  public LoginPage loginPage;

  @BeforeMethod(groups = "requisition")
  public void setUp() throws Exception {
    super.setup();
    dbWrapper.deleteData();
  }

  @Test(groups = {"requisition"}, dataProvider = "Data-Provider-Function-RnR")
  public void testVerifyManagePODValidFlowForRegularRnR(String program,
                                                        String userSIC,
                                                        String password) throws Exception {
    setUpData(program, userSIC);
    dbWrapper.insertRequisitions(1, "MALARIA", true, "2012-12-01", "2015-12-01", "F10", false);
    dbWrapper.updateRequisitionStatus("SUBMITTED", userSIC, "MALARIA");
    dbWrapper.updateRequisitionStatus("AUTHORIZED", userSIC, "MALARIA");
    dbWrapper.updateFieldValue("requisition_line_items", "packsToShip", 100);
    dbWrapper.updateRequisitionStatus("APPROVED", userSIC, "MALARIA");
    dbWrapper.insertFulfilmentRoleAssignment("storeIncharge", "store in-charge", "F10");
    dbWrapper.insertOrders("RELEASED", userSIC, "MALARIA");
    dbWrapper.updateRequisitionStatus("RELEASED", userSIC, "MALARIA");

    LoginPage loginPage = new LoginPage(testWebDriver, baseUrlGlobal);
    HomePage homePage = loginPage.loginAs(userSIC, password);

    homePage.navigateManagePOD();
    UpdatePodPage updatePodPage = new UpdatePodPage(testWebDriver);
    updatePodPage.selectRequisitionToUpdatePod(1);

    verifyTitleOnUpdatePODScreen(updatePodPage);
    verifyHeadersWithValuesOnUpdatePODScreen(updatePodPage);
    verifyHeadersOfPodTableOnUpdatePODScreen(updatePodPage);
    verifyValuesOfPodTableOnUpdatePODScreen(updatePodPage);
    assertTrue(updatePodPage.getFullSupplyTickIcon());
  }

  @Test(groups = {"requisition"}, dataProvider = "Data-Provider-Function-RnR")
  public void testVerifyManagePODValidFlowForEmergencyRnR(String program, String userSIC, String password) throws Exception {
    setUpData(program, userSIC);
    dbWrapper.insertRequisitions(1, "MALARIA", true, "2012-12-01", "2015-12-01", "F10", true);
    dbWrapper.updateRequisitionStatus("SUBMITTED", userSIC, "MALARIA");
    dbWrapper.updateRequisitionStatus("AUTHORIZED", userSIC, "MALARIA");
    dbWrapper.updateFieldValue("requisition_line_items", "packsToShip", 100);
    dbWrapper.updateRequisitionStatus("APPROVED", userSIC, "MALARIA");
    dbWrapper.insertFulfilmentRoleAssignment("storeIncharge", "store in-charge", "F10");
    dbWrapper.insertOrders("RELEASED", userSIC, "MALARIA");
    dbWrapper.updateRequisitionStatus("RELEASED", userSIC, "MALARIA");

    LoginPage loginPage = new LoginPage(testWebDriver, baseUrlGlobal);
    HomePage homePage = loginPage.loginAs(userSIC, password);

    homePage.navigateManagePOD();
    UpdatePodPage updatePodPage = new UpdatePodPage(testWebDriver);
    updatePodPage.selectRequisitionToUpdatePod(1);
    assertTrue(updatePodPage.getHeaders().contains("Emergency"));
  }

  @Test(groups = {"requisition"}, dataProvider = "Data-Provider-Function-RnR")
  public void testVerifyManagePODWhenPacksToShipIsZero(String program, String userSIC, String password) throws Exception {
    setUpData(program, userSIC);
    dbWrapper.insertRequisitions(1, "MALARIA", true, "2012-12-01", "2015-12-01", "F10", false);
    dbWrapper.updateRequisitionStatus("SUBMITTED", userSIC, "MALARIA");
    dbWrapper.updateRequisitionStatus("AUTHORIZED", userSIC, "MALARIA");
    dbWrapper.updateFieldValue("requisition_line_items", "packsToShip", 0);
    dbWrapper.updateRequisitionStatus("APPROVED", userSIC, "MALARIA");
    dbWrapper.insertFulfilmentRoleAssignment("storeIncharge", "store in-charge", "F10");
    dbWrapper.insertOrders("RELEASED", userSIC, "MALARIA");
    dbWrapper.updateRequisitionStatus("RELEASED", userSIC, "MALARIA");

    LoginPage loginPage = new LoginPage(testWebDriver, baseUrlGlobal);
    HomePage homePage = loginPage.loginAs(userSIC, password);

    homePage.navigateManagePOD();
    UpdatePodPage updatePodPage = new UpdatePodPage(testWebDriver);
    updatePodPage.selectRequisitionToUpdatePod(1);

    verifyTitleOnUpdatePODScreen(updatePodPage);
    verifyHeadersWithValuesOnUpdatePODScreen(updatePodPage);
    verifyMessageOnPodScreen(updatePodPage);
    assertFalse(updatePodPage.getPodTableData().contains("P10"));
    assertFalse(updatePodPage.getPodTableData().contains("antibiotic Capsule 300/200/600 mg"));
    assertFalse(updatePodPage.getPodTableData().contains("100"));
    assertFalse(updatePodPage.getPodTableData().contains("Strip"));
  }

//  @Test(groups = {"requisition"}, dataProvider = "Data-Provider-Function-RnR")
  public void testVerifyManagePODWhenSomeProductsAreSkipped(String program, String userSIC, String password) throws Exception {
    setUpData(program, userSIC);
    LoginPage loginPage = new LoginPage(testWebDriver, baseUrlGlobal);
    HomePage homePage = loginPage.loginAs(userSIC, password);
    homePage.navigateAndInitiateRnr("MALARIA");
    InitiateRnRPage initiateRnRPage = homePage.clickProceed();
    initiateRnRPage.enterValue(100, "beginningBalanceFirstProduct");
    initiateRnRPage.enterValue(100, "quantityReceivedFirstProduct");
    initiateRnRPage.enterValue(100, "quantityDispensedFirstProduct");
    initiateRnRPage.skipSingleProduct(2);
    initiateRnRPage.submitRnR();
    initiateRnRPage.clickOk();

    dbWrapper.updateRequisitionStatus("AUTHORIZED", userSIC, "MALARIA");
    dbWrapper.updateFieldValue("requisition_line_items", "packsToShip", 100);
    dbWrapper.updateRequisitionStatus("APPROVED", userSIC, "MALARIA");
    dbWrapper.insertFulfilmentRoleAssignment("storeIncharge", "store in-charge", "F10");
    dbWrapper.insertOrders("RELEASED", userSIC, "MALARIA");
    dbWrapper.updateRequisitionStatus("RELEASED", userSIC, "MALARIA");

    homePage.navigateManagePOD();
    UpdatePodPage updatePodPage = new UpdatePodPage(testWebDriver);
    updatePodPage.selectRequisitionToUpdatePod(1);

    assertTrue(updatePodPage.getPodTableData().contains("P11"));
    assertTrue(updatePodPage.getPodTableData().contains("antibiotic Capsule 300/200/600 mg"));
    assertTrue(updatePodPage.getPodTableData().contains("100"));
    assertTrue(updatePodPage.getPodTableData().contains("Strip"));

    assertFalse(updatePodPage.getPodTableData().contains("P11"));
    assertFalse(updatePodPage.getPodTableData().contains("antibiotic Capsule 300/200/600 mg"));
    assertFalse(updatePodPage.getPodTableData().contains("100"));
    assertFalse(updatePodPage.getPodTableData().contains("Strip"));
  }

  private void verifyMessageOnPodScreen(UpdatePodPage updatePodPage) {
    assertTrue(updatePodPage.getNoProductsMessage().contains("No products."));
  }

  private void verifyValuesOfPodTableOnUpdatePODScreen(UpdatePodPage updatePodPage) {
    assertTrue(updatePodPage.getProductCode(1).contains("P10"));
    assertTrue(updatePodPage.getProductName(1).contains("antibiotic Capsule 300/200/600 mg"));
    assertTrue(updatePodPage.getPacksToShip(1).contains("100"));
    assertTrue(updatePodPage.getUnitOfIssue(1).contains("Strip"));
    assertTrue(updatePodPage.getQuantityReceived(1).contains(""));
    assertTrue(updatePodPage.getQuantityShipped(1).contains(""));
    assertTrue(updatePodPage.getNotes(1).contains(""));
  }

  private void verifyHeadersOfPodTableOnUpdatePODScreen(UpdatePodPage updatePodPage) {
    String podTableDetails = updatePodPage.getPodTableData();
    assertTrue(podTableDetails.contains("Full Supply"));
    assertTrue(podTableDetails.contains("Product Code"));
    assertTrue(podTableDetails.contains("Product Name"));
    assertTrue(podTableDetails.contains("Unit of Issue"));
    assertTrue(podTableDetails.contains("Packs to Ship"));
    assertTrue(podTableDetails.contains("Quantity Shipped"));
    assertTrue(podTableDetails.contains("Quantity Received"));
    assertTrue(podTableDetails.contains("Notes"));
  }

  private void verifyHeadersWithValuesOnUpdatePODScreen(UpdatePodPage updatePodPage) throws IOException, SQLException {
    String headerDetails = updatePodPage.getHeaders();
    Integer id = dbWrapper.getMaxRnrID();
    assertTrue(headerDetails.contains("Order No.: " + id));
    assertTrue(headerDetails.contains("Facility:  F10 - Village Dispensary"));
    assertTrue(headerDetails.contains("Order Date/Time:"));
    assertTrue(headerDetails.contains("Supplying Depot:  Village Dispensary\n"));
    assertTrue(headerDetails.contains("Reporting Period: 01/12/2012 - 01/12/2015"));
    assertTrue(headerDetails.contains(new SimpleDateFormat("dd/MM/yyyy").format(new Date())));
    assertTrue(headerDetails.contains("Regular"));
  }

  private void verifyTitleOnUpdatePODScreen(UpdatePodPage updatePodPage) {
    assertTrue(updatePodPage.getTitle().contains("Proof of Delivery"));
  }

  private void setUpData(String program, String userSIC) throws Exception {
    setupProductTestData("P10", "P11", program, "lvl3_hospital");
    dbWrapper.insertFacilities("F10", "F11");
    dbWrapper.configureTemplate(program);
    List<String> rightsList = new ArrayList<>();
    rightsList.add(CREATE_REQUISITION);
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

  @AfterMethod(groups = "requisition")
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
    return new Object[][]{{"HIV", "storeIncharge", "Admin123"}};
  }
}
