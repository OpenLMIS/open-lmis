package org.openlmis.functional;

import org.openlmis.UiUtils.TestCaseHelper;
import org.openlmis.pageobjects.HomePage;
import org.openlmis.pageobjects.LoginPage;
import org.openlmis.pageobjects.ManagePodPage;
import org.openlmis.pageobjects.UpdatePodPage;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.io.IOException;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.thoughtworks.selenium.SeleneseTestBase.assertNotEquals;
import static com.thoughtworks.selenium.SeleneseTestBase.assertTrue;
import static java.util.Arrays.asList;
import static org.testng.AssertJUnit.assertEquals;
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

  public static final String USER = "user";
  public static final String PASSWORD = "password";
  public static final String PROGRAM = "program";
  UpdatePodPage updatePodPage;

  public Map<String, String> updatePODData = new HashMap<String, String>() {{
    put(USER, "storeInCharge");
    put(PASSWORD, "Admin123");
    put(PROGRAM, "HIV");
  }};

  @BeforeMethod(groups = "requisition")
  public void setUp() throws InterruptedException, SQLException, IOException {
    super.setup();
    dbWrapper.deleteData();
    setUpData(updatePODData.get(PROGRAM), updatePODData.get(USER));
    updatePodPage = PageFactory.getInstanceOfUpdatePodPage(testWebDriver);
    loginPage = PageFactory.getInstanceOfLoginPage(testWebDriver, baseUrlGlobal);
  }

  @Test(groups = {"requisition"})
  public void testVerifyUpdatePODForReleasedOrdersValidFlowForRegularRnR() throws Exception {
    initiateRnrAndConvertToOrder(false, 100);

    HomePage homePage = loginPage.loginAs(updatePODData.get(USER), updatePODData.get(PASSWORD));
    ManagePodPage managePodPage = homePage.navigateManagePOD();
    UpdatePodPage updatePodPage = managePodPage.selectRequisitionToUpdatePod(1);

    assertEquals("Proof of Delivery", updatePodPage.getTitle());
    verifyHeadersWithValuesOnUpdatePODScreen();
    verifyHeadersOfPodTableOnUpdatePODScreen();
    verifyValuesOfPodTableOnUpdatePODScreen(1, "P10", "antibiotic Capsule 300/200/600 mg", "100", "Strip", "");
    assertEquals("", updatePodPage.getQuantityReceived(1));
    assertEquals("", updatePodPage.getNotes(1));
    verifyPodDataInDatabase(null, null, "P10");
    assertTrue(updatePodPage.isFullSupplyTickIconDisplayed(1));
    verifyRequisitionTypeAndColor("regular");

    updatePodPage.enterPodData("200", "openlmis open source logistic management system", 1);
    updatePodPage.clickSave();
    assertTrue(updatePodPage.isPodSuccessMessageDisplayed());
    testWebDriver.refresh();
    updatePodPage.verifyQuantityReceivedAndNotes("200", "openlmis open source logistic management system", 1);
    verifyPodDataInDatabase("200", "openlmis open source logistic management system", "P10");

    updatePodPage.enterPodData("990", "openlmis project", 1);
    updatePodPage.clickSave();
    assertTrue(updatePodPage.isPodSuccessMessageDisplayed());
    testWebDriver.refresh();

    updatePodPage.verifyQuantityReceivedAndNotes("990", "openlmis project", 1);
    verifyPodDataInDatabase("990", "openlmis project", "P10");
  }

  @Test(groups = {"requisition"})
  public void testVerifyUpdatePODForReleasedOrdersValidFlowForEmergencyRnR() throws Exception {
    initiateRnrAndConvertToOrder(true, 100);

    HomePage homePage = loginPage.loginAs(updatePODData.get(USER), updatePODData.get(PASSWORD));
    ManagePodPage managePodPage = homePage.navigateManagePOD();
    managePodPage.selectRequisitionToUpdatePod(1);

    verifyRequisitionTypeAndColor("emergency");
  }

  @Test(groups = {"requisition"})
  public void testVerifyUpdatePODForReleasedOrdersWhenPacksToShipIsZero() throws Exception {
    initiateRnrAndConvertToOrder(false, 0);

    HomePage homePage = loginPage.loginAs(updatePODData.get(USER), updatePODData.get(PASSWORD));
    ManagePodPage managePodPage = homePage.navigateManagePOD();
    UpdatePodPage updatePodPage = managePodPage.selectRequisitionToUpdatePod(1);

    assertEquals("No products.", updatePodPage.getNoProductsMessage());
    assertFalse(updatePodPage.getPodTableData().contains("P10"));
    assertFalse(updatePodPage.getPodTableData().contains("antibiotic Capsule 300/200/600 mg"));
    assertFalse(updatePodPage.getPodTableData().contains("100"));
    assertFalse(updatePodPage.getPodTableData().contains("Strip"));
    verifyRequisitionTypeAndColor("regular");
  }

  @Test(groups = {"requisition"})
  public void testVerifyUpdatePODForReleasedOrdersWhenMultipleProducts() throws Exception {
    dbWrapper.setupMultipleProducts(updatePODData.get(PROGRAM), "Lvl3 Hospital", 1, true);
    dbWrapper.insertRequisitionWithMultipleLineItems(1, updatePODData.get(PROGRAM), true, "F10", false);
    dbWrapper.convertRequisitionToOrder(dbWrapper.getMaxRnrID(), "READY_TO_PACK", updatePODData.get(USER));

    HomePage homePage = loginPage.loginAs(updatePODData.get(USER), updatePODData.get(PASSWORD));
    ManagePodPage managePodPage = homePage.navigateManagePOD();
    managePodPage.selectRequisitionToUpdatePod(1);

    verifyValuesOfPodTableOnUpdatePODScreen(1, "F0", "antibiotic Capsule 300/200/600 mg", "5", "Strip", "");
    verifyValuesOfPodTableOnUpdatePODScreen(2, "NF0", "antibiotic Capsule 300/200/600 mg", "50", "Strip", "");
    verifyRequisitionTypeAndColor("regular");
  }

  @Test(groups = {"requisition"})
  public void testVerifyUpdatePODForPackedOrdersWhenMultipleProducts() throws SQLException {
    dbWrapper.setupMultipleProducts(updatePODData.get(PROGRAM), "Lvl3 Hospital", 1, true);
    dbWrapper.insertRequisitionWithMultipleLineItems(1, updatePODData.get(PROGRAM), true, "F10", true);
    dbWrapper.convertRequisitionToOrder(dbWrapper.getMaxRnrID(), "READY_TO_PACK", updatePODData.get(USER));
    testDataForShipment(50, false, "NF0", 999);
    dbWrapper.updateFieldValue("orders", "status", "PACKED", null, null);

    HomePage homePage = loginPage.loginAs(updatePODData.get(USER), updatePODData.get(PASSWORD));
    ManagePodPage managePodPage = homePage.navigateManagePOD();
    managePodPage.selectRequisitionToUpdatePod(1);

    verifyValuesOfPodTableOnUpdatePODScreen(1, "NF0", "antibiotic Capsule 300/200/600 mg", "50", "Strip", "999");
    verifyRequisitionTypeAndColor("emergency");
    updatePodPage.getPodTableData();
    assertNotEquals(updatePodPage.getPodTableData(), "F0");
  }

  @Test(groups = {"requisition"})
  public void testVerifyUpdatePODForPackedOrdersValidFlowForRegularRnR() throws SQLException {
    initiateRnrAndConvertToOrder(false, 1111);
    testDataForShipment(999, true, "P10", 99898998);
    dbWrapper.updateFieldValue("orders", "status", "PACKED", null, null);

    HomePage homePage = loginPage.loginAs(updatePODData.get(USER), updatePODData.get(PASSWORD));
    ManagePodPage managePodPage = homePage.navigateManagePOD();
    UpdatePodPage updatePodPage = managePodPage.selectRequisitionToUpdatePod(1);

    assertEquals("Proof of Delivery", updatePodPage.getTitle());
    verifyHeadersWithValuesOnUpdatePODScreen();
    verifyHeadersOfPodTableOnUpdatePODScreen();
    verifyValuesOfPodTableOnUpdatePODScreen(1, "P10", "antibiotic Capsule 300/200/600 mg", "999", "Strip", "99898998");
    assertEquals("", updatePodPage.getQuantityReceived(1));
    assertEquals("", updatePodPage.getNotes(1));
    assertTrue(updatePodPage.isFullSupplyTickIconDisplayed(1));
    verifyRequisitionTypeAndColor("regular");
  }

  @Test(groups = {"requisition"})
  public void testVerifyUpdatePODForPackedOrdersAdditionalProduct() throws SQLException {
    Integer id = dbWrapper.getProductId("P11");
    dbWrapper.updateFieldValue("program_products", "programid", "4", "id", id.toString());
    initiateRnrAndConvertToOrder(false, 1111);
    testDataForShipment(999, true, "P10", 99898998);
    dbWrapper.insertShipmentData(dbWrapper.getMaxRnrID(), "P11", 0);
    dbWrapper.updateFieldValue("orders", "status", "PACKED", null, null);


    HomePage homePage = loginPage.loginAs(updatePODData.get(USER), updatePODData.get(PASSWORD));
    ManagePodPage managePodPage = homePage.navigateManagePOD();
    UpdatePodPage updatePodPage = managePodPage.selectRequisitionToUpdatePod(1);

    assertEquals("Proof of Delivery", updatePodPage.getTitle());
    verifyHeadersWithValuesOnUpdatePODScreen();
    verifyHeadersOfPodTableOnUpdatePODScreen();
    verifyValuesOfPodTableOnUpdatePODScreen(1, "P10", "antibiotic Capsule 300/200/600 mg", "999", "Strip", "99898998");
    assertEquals("", updatePodPage.getQuantityReceived(1));
    assertEquals("", updatePodPage.getNotes(1));
    assertTrue(updatePodPage.isFullSupplyTickIconDisplayed(1));
    verifyRequisitionTypeAndColor("regular");
    verifyValuesOfPodTableOnUpdatePODScreen(2, "P11", "antibiotic Capsule 300/200/600 mg", "", "Strip", "0");
  }

  @Test(groups = {"requisition"})
  public void testVerifyUpdatePODForPackedOrdersWhenPacksToShipAndQuantityShippedIsZero() throws SQLException {
    initiateRnrAndConvertToOrder(false, 0);
    super.testDataForShipment(0, true, "P10", 0);
    dbWrapper.updateFieldValue("orders", "status", "PACKED", null, null);

    HomePage homePage = loginPage.loginAs(updatePODData.get(USER), updatePODData.get(PASSWORD));
    ManagePodPage managePodPage = homePage.navigateManagePOD();
    managePodPage.selectRequisitionToUpdatePod(1);

    verifyValuesOfPodTableOnUpdatePODScreen(1, "P10", "antibiotic Capsule 300/200/600 mg", "0", "Strip", "0");
  }


  private void initiateRnrAndConvertToOrder(boolean isEmergencyRegular, int packsToShip) throws SQLException {
    dbWrapper.insertRequisitions(1, "HIV", true, "2012-12-01", "2015-12-01", "F10", isEmergencyRegular);
    dbWrapper.updateRequisitionStatus("APPROVED", updatePODData.get(USER), "HIV");
    dbWrapper.updateFieldValue("requisition_line_items", "packsToShip", packsToShip);
    dbWrapper.convertRequisitionToOrder(dbWrapper.getMaxRnrID(), "READY_TO_PACK", updatePODData.get(USER));
  }

  private void verifyValuesOfPodTableOnUpdatePODScreen(int rowNumber, String productCode, String productName, String packsToShip,
                                                       String unitOfIssue, String quantityShipped) {
    assertEquals(productCode, updatePodPage.getProductCode(rowNumber));
    assertEquals(productName, updatePodPage.getProductName(rowNumber));
    assertEquals(packsToShip, updatePodPage.getPacksToShip(rowNumber));
    assertEquals(unitOfIssue, updatePodPage.getUnitOfIssue(rowNumber));
    assertEquals(quantityShipped, updatePodPage.getQuantityShipped(rowNumber));
  }

  private void verifyHeadersOfPodTableOnUpdatePODScreen() {
    assertEquals("Full Supply", testWebDriver.getElementByXpath("//table[@id='podTable']/thead/tr/th[1]/span").getText());
    assertEquals("Product Code", testWebDriver.getElementByXpath("//table[@id='podTable']/thead/tr/th[2]/span").getText());
    assertEquals("Product Name", testWebDriver.getElementByXpath("//table[@id='podTable']/thead/tr/th[3]/span").getText());
    assertEquals("Unit of Issue", testWebDriver.getElementByXpath("//table[@id='podTable']/thead/tr/th[4]/span").getText());
    assertEquals("Packs to Ship", testWebDriver.getElementByXpath("//table[@id='podTable']/thead/tr/th[5]/span").getText());
    assertEquals("Quantity Shipped", testWebDriver.getElementByXpath("//table[@id='podTable']/thead/tr/th[6]/span").getText());
    assertEquals("Quantity Received", testWebDriver.getElementByXpath("//table[@id='podTable']/thead/tr/th[7]/span").getText());
    assertEquals("Notes", testWebDriver.getElementByXpath("//table[@id='podTable']/thead/tr/th[8]/span").getText());
  }

  private void verifyHeadersWithValuesOnUpdatePODScreen() throws SQLException {
    Integer id = dbWrapper.getMaxRnrID();
    assertEquals("Order No.: " + id, updatePodPage.getOrderNumberLabel() + ": " + updatePodPage.getOrderId());
    assertEquals("Facility: F10 - Village Dispensary", updatePodPage.getFacilityLabel() + ": " + updatePodPage.getFacilityCode());
    assertTrue((updatePodPage.getOrderDateTimeLabel() + ": " + updatePodPage.getOrderCreatedDate()).contains("Order Date/Time: " + new SimpleDateFormat("dd/MM/yyyy").format(new Date())));
    assertEquals("Supplying Depot: Village Dispensary", updatePodPage.getSupplyingDepotLabel() + ": " + updatePodPage.getSupplyingDepot());
    assertEquals("Reporting Period: 01/12/2012 - 01/12/2015", updatePodPage.getReportingPeriodLabel() + ": " + updatePodPage.getPeriodStartDate());
  }

  private void verifyRequisitionTypeAndColor(String requisitionType) {
    if (requisitionType.equals("regular")) {
      assertEquals("Regular", updatePodPage.getRequisitionType());
      assertEquals("rgba(219, 219, 219, 1)", updatePodPage.getRequisitionTypeColor());
    } else if (requisitionType.equals("emergency")) {
      assertEquals("Emergency", updatePodPage.getRequisitionType());
      assertEquals("rgba(210, 95, 91, 1)", updatePodPage.getRequisitionTypeColor());
    }
  }

  private void setUpData(String program, String userSIC) throws SQLException {
    setupProductTestData("P10", "P11", program, "lvl3_hospital");
    dbWrapper.insertFacilities("F10", "F11");
    dbWrapper.configureTemplate(program);
    List<String> rightsList = asList(CREATE_REQUISITION, CONVERT_TO_ORDER, VIEW_ORDER, MANAGE_POD);
    setupTestUserRoleRightsData("200", userSIC, rightsList);
    dbWrapper.insertSupervisoryNode("F10", "N1", "Node 1", "null");
    dbWrapper.insertRoleAssignment("200", "store in-charge");
    dbWrapper.insertSchedule("Q1stM", "QuarterMonthly", "QuarterMonth");
    dbWrapper.insertSchedule("M", "Monthly", "Month");
    dbWrapper.insertProcessingPeriod("Period1", "first period", "2012-12-01", "2013-01-15", 1, "Q1stM");
    dbWrapper.insertProcessingPeriod("Period2", "second period", "2013-01-16", "2013-01-30", 1, "M");
    setupRequisitionGroupData("RG1", "RG2", "N1", "N2", "F10", "F11");
    dbWrapper.insertSupplyLines("N1", program, "F10", true);
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
}
