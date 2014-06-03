package org.openlmis.functional;

import cucumber.api.java.en.And;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import org.openlmis.UiUtils.TestCaseHelper;
import org.openlmis.pageobjects.*;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.sql.SQLException;
import java.text.NumberFormat;
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

  @BeforeMethod(groups = "orderAndPod")
  public void setUp() throws Exception {
    super.setup();
    dbWrapper.deleteData();
    setUpData(updatePODData.get(PROGRAM), updatePODData.get(USER));
    updatePodPage = PageObjectFactory.getUpdatePodPage(testWebDriver);
    loginPage = PageObjectFactory.getLoginPage(testWebDriver, baseUrlGlobal);
  }

  @When("^I click POD print$")
  public void clickOnPrintButton() {
    updatePodPage.clickPrintButton();
    testWebDriver.sleep(500);
  }

  @Test(groups = {"orderAndPod"})
  public void testVerifyUpdatePODForReleasedOrdersValidFlowForRegularRnR() throws SQLException {
    initiateRnrAndConvertToOrder(false, 100);

    HomePage homePage = loginPage.loginAs(updatePODData.get(USER), updatePODData.get(PASSWORD));
    ManagePodPage managePodPage = homePage.navigateManagePOD();
    updatePodPage = managePodPage.selectRequisitionToUpdatePod(1);

    assertEquals("Proof of Delivery", updatePodPage.getTitle());
    verifyHeadersWithValuesOnUpdatePODScreen(getOrderNumber("O", "MALARIA", "R"));
    verifyHeadersOfPodTableOnUpdatePODScreen();
    verifyValuesOfPodTableOnUpdatePODScreen(1, "P10", "antibiotic Capsule 300/200/600 mg", "100", "Strip", "");
    assertEquals("", updatePodPage.getQuantityReceived(1));
    assertEquals("", updatePodPage.getNotes(1));
    verifyPodDataInDatabase(null, null, "P10", null);
    assertTrue(updatePodPage.isFullSupplyTickIconDisplayed(1));
    verifyRequisitionTypeAndColor("regular");

    updatePodPage.enterPodData("200", "openlmis open source logistic management system", null, 1);
    updatePodPage.enterDeliveryDetailsInPodScreen("Delivered Person", "", "");
    updatePodPage.clickSave();
    assertTrue(updatePodPage.isPodSuccessMessageDisplayed());
    testWebDriver.refresh();
    updatePodPage.verifyQuantityReceivedAndNotes("200", "openlmis open source logistic management system", 1);
    verifyPodDataInDatabase("200", "openlmis open source logistic management system", "P10", null);
    updatePodPage.verifyDeliveryDetailsOnPodScreenUI("Delivered Person", "", "");
    verifyDeliveryDetailsOfPodScreenInDatabase("Delivered Person", null, null);

    updatePodPage.enterPodData("990", "openlmis project", "90", 1);
    updatePodPage.enterDeliveryDetailsInPodScreen("Delivered", "Received Person", "27/02/2014");
    updatePodPage.clickSave();
    assertTrue(updatePodPage.isPodSuccessMessageDisplayed());
    testWebDriver.refresh();
    updatePodPage.verifyDeliveryDetailsOnPodScreenUI("Delivered", "Received Person", "27/02/2014");
    updatePodPage.verifyQuantityReceivedAndNotes("990", "openlmis project", 1);
    verifyPodDataInDatabase("990", "openlmis project", "P10", "90");
    verifyDeliveryDetailsOfPodScreenInDatabase("Delivered", "Received Person", "2014-02-27 00:00:00");
  }

  @Test(groups = {"orderAndPod"})
  public void testVerifyUpdatePODForReleasedOrdersValidFlowForEmergencyRnR() throws SQLException {
    initiateRnrAndConvertToOrder(true, 100);

    HomePage homePage = loginPage.loginAs(updatePODData.get(USER), updatePODData.get(PASSWORD));
    ManagePodPage managePodPage = homePage.navigateManagePOD();
    managePodPage.selectRequisitionToUpdatePod(1);

    verifyRequisitionTypeAndColor("emergency");
  }

  @Test(groups = {"orderAndPod"})
  public void testVerifyUpdatePODForReleasedOrdersWhenPacksToShipIsZero() throws SQLException {
    initiateRnrAndConvertToOrder(false, 0);

    HomePage homePage = loginPage.loginAs(updatePODData.get(USER), updatePODData.get(PASSWORD));
    ManagePodPage managePodPage = homePage.navigateManagePOD();
    updatePodPage = managePodPage.selectRequisitionToUpdatePod(1);

    assertEquals("No products.", updatePodPage.getNoProductsMessage());
    assertFalse(updatePodPage.getPodTableData().contains("P10"));
    assertFalse(updatePodPage.getPodTableData().contains("antibiotic Capsule 300/200/600 mg"));
    assertFalse(updatePodPage.getPodTableData().contains("100"));
    assertFalse(updatePodPage.getPodTableData().contains("Strip"));
    verifyRequisitionTypeAndColor("regular");
  }

  @Test(groups = {"orderAndPod"})
  public void testVerifyUpdatePODForReleasedOrdersWhenMultipleProducts() throws SQLException {
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

  @Test(groups = {"orderAndPod"})
  public void testVerifyUpdatePODForPackedOrdersWhenMultipleProducts() throws SQLException {
    dbWrapper.setupMultipleProducts(updatePODData.get(PROGRAM), "Lvl3 Hospital", 1, true);
    dbWrapper.insertRequisitionWithMultipleLineItems(1, updatePODData.get(PROGRAM), true, "F10", true);
    dbWrapper.convertRequisitionToOrder(dbWrapper.getMaxRnrID(), "READY_TO_PACK", updatePODData.get(USER));
    dbWrapper.updateFieldValue("orders", "status", "RELEASED", null, null);
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

  @Test(groups = {"orderAndPod"})
  public void testVerifyUpdatePODForPackedOrdersValidFlowForRegularRnR() throws SQLException {
    initiateRnrAndConvertToOrder(false, 1111);
    dbWrapper.updateFieldValue("orders", "status", "RELEASED", null, null);
    testDataForShipment(999, true, "P10", 99898998);
    dbWrapper.updateFieldValue("orders", "status", "PACKED", null, null);

    HomePage homePage = loginPage.loginAs(updatePODData.get(USER), updatePODData.get(PASSWORD));
    ManagePodPage managePodPage = homePage.navigateManagePOD();
    updatePodPage = managePodPage.selectRequisitionToUpdatePod(1);

    assertEquals("Proof of Delivery", updatePodPage.getTitle());
    verifyHeadersWithValuesOnUpdatePODScreen(getOrderNumber("O", "MALARIA", "R"));
    verifyHeadersOfPodTableOnUpdatePODScreen();
    verifyValuesOfPodTableOnUpdatePODScreen(1, "P10", "antibiotic Capsule 300/200/600 mg", "999", "Strip", "99898998");
    assertEquals("", updatePodPage.getQuantityReceived(1));
    assertEquals("", updatePodPage.getNotes(1));
    assertTrue(updatePodPage.isFullSupplyTickIconDisplayed(1));
    verifyRequisitionTypeAndColor("regular");
  }

  @Test(groups = {"orderAndPod"})
  public void testVerifyUpdatePODForPackedOrdersAdditionalProduct() throws SQLException {
    Integer id = dbWrapper.getProductId("P11");
    dbWrapper.updateFieldValue("program_products", "programId", "4", "productId", id.toString());
    initiateRnrAndConvertToOrder(false, 1111);
    dbWrapper.updateFieldValue("orders", "status", "RELEASED", null, null);
    testDataForShipment(999, true, "P10", 99898998);
    dbWrapper.insertShipmentData(dbWrapper.getMaxRnrID(), "P11", 0, null, false);
    dbWrapper.updateFieldValue("orders", "status", "PACKED", null, null);


    HomePage homePage = loginPage.loginAs(updatePODData.get(USER), updatePODData.get(PASSWORD));
    ManagePodPage managePodPage = homePage.navigateManagePOD();
    updatePodPage = managePodPage.selectRequisitionToUpdatePod(1);

    assertEquals("Proof of Delivery", updatePodPage.getTitle());
    verifyHeadersWithValuesOnUpdatePODScreen(getOrderNumber("O", "MALARIA", "R"));
    verifyHeadersOfPodTableOnUpdatePODScreen();
    verifyValuesOfPodTableOnUpdatePODScreen(1, "P10", "antibiotic Capsule 300/200/600 mg", "999", "Strip", "99898998");
    assertEquals("", updatePodPage.getQuantityReceived(1));
    assertEquals("", updatePodPage.getNotes(1));
    assertTrue(updatePodPage.isFullSupplyTickIconDisplayed(1));
    verifyRequisitionTypeAndColor("regular");
    assertEquals("Other", testWebDriver.getElementById("category").getText());
    verifyValuesOfPodTableOnUpdatePODScreen(2, "P11", "antibiotic Capsule 300/200/600 mg", "", "Strip", "0");
  }

  @Test(groups = {"orderAndPod"})
  public void testUpdatePODForPackedOrdersWhenPacksToShipAndQuantityShippedIsZeroAndSubmitPod() throws SQLException {
    initiateRnrAndConvertToOrder(false, 0);
    dbWrapper.updateFieldValue("orders", "status", "RELEASED", null, null);
    super.testDataForShipment(0, true, "P10", 0);
    dbWrapper.updateFieldValue("orders", "status", "PACKED", null, null);

    HomePage homePage = loginPage.loginAs(updatePODData.get(USER), updatePODData.get(PASSWORD));
    ManagePodPage managePodPage = homePage.navigateManagePOD();
    managePodPage.selectRequisitionToUpdatePod(1);

    verifyValuesOfPodTableOnUpdatePODScreen(1, "P10", "antibiotic Capsule 300/200/600 mg", "0", "Strip", "0");
    updatePodPage.enterPodData("45", "Some notes", null, 1);
    updatePodPage.clickSubmitButton();
    updatePodPage.clickOkButton();
    verifyPodDataInDatabase("45", "Some notes", "P10", null);
  }

  @And("^I enter \"([^\"]*)\" as quantity received, \"([^\"]*)\" as quantity returned and \"([^\"]*)\" as notes in row \"([^\"]*)\"$")
  public void enterPodDetails(String quantityReceived, String quantityReturned, String notes, String rowNumber) {
    updatePodPage = PageObjectFactory.getUpdatePodPage(testWebDriver);
    updatePodPage.enterPodData(quantityReceived, notes, quantityReturned, Integer.parseInt(rowNumber));
  }

  @And("^I enter \"([^\"]*)\" as deliveredBy,\"([^\"]*)\" as receivedBy and \"([^\"]*)\" as receivedDate$")
  public void enterDeliveryDetailsOnPodScreen(String deliveredBy, String receivedBy, String receivedDate) {
    updatePodPage = PageObjectFactory.getUpdatePodPage(testWebDriver);
    updatePodPage.enterDeliveryDetailsInPodScreen(deliveredBy, receivedBy, receivedDate);
  }


  @And("^I submit POD$")
  public void submitPOD() {
    updatePodPage = PageObjectFactory.getUpdatePodPage(testWebDriver);
    updatePodPage.clickSubmitButton();
    updatePodPage.clickCancelButton();
    updatePodPage.clickSubmitButton();
    updatePodPage.clickOkButton();
  }

  @When("^I click on update Pod link for Row \"([^\"]*)\"$")
  public void navigateUploadPodPage(Integer rowNumber) {
    HomePage homePage = PageObjectFactory.getHomePage(testWebDriver);
    ManagePodPage managePodPage = homePage.navigateManagePOD();
    managePodPage.selectRequisitionToUpdatePod(rowNumber);
  }

  @Then("^I should see all products to update pod$")
  public void verifyUpdatePodPage() {
    updatePodPage = PageObjectFactory.getUpdatePodPage(testWebDriver);
    assertTrue(updatePodPage.getProductCode(1).contains("P10"));
    assertTrue(updatePodPage.getProductName(1).contains("antibiotic"));
    assertFalse(updatePodPage.getProductCode(1).contains("P11"));
  }

  @Then("^I verify quantity received, quantity returned,notes,deliveredBy,receivedBy,receivedDate disabled$")
  public void verifyPodPageDisabled() {
    testWebDriver.sleep(1000);
    updatePodPage = PageObjectFactory.getUpdatePodPage(testWebDriver);
    assertFalse(updatePodPage.isQuantityReceivedEnabled(1));
    assertFalse(updatePodPage.isNotesEnabled(1));
    assertFalse(updatePodPage.isQuantityReturnedEnabled(1));
    assertFalse(updatePodPage.isDeliveryByFieldEnabled());
    assertFalse(updatePodPage.isReceivedByFieldEnabled());
    assertFalse(updatePodPage.isReceivedDateFieldEnabled());
  }

  @And("^I verify in database quantity received as \"([^\"]*)\", quantity returned as \"([^\"]*)\" and notes as \"([^\"]*)\"$")
  public void verifyPodDataSavedInDatabase(String quantityReceived, String quantityReturned, String notes) throws SQLException {
    verifyPodDataInDatabase(quantityReceived, notes, "P10", quantityReturned);
  }

  @And("^I verify in database deliveredBy as \"([^\"]*)\",receivedBy as \"([^\"]*)\" and receivedDate as \"([^\"]*)\"$")
  public void verifyDeliveryDetailsOfPodScreenSavedInDatabase(String deliveredBy, String receivedBy, String receivedDate) throws SQLException {
    verifyDeliveryDetailsOfPodScreenInDatabase(deliveredBy, receivedBy, receivedDate);
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
    assertEquals("Quantity Returned", testWebDriver.getElementByXpath("//table[@id='podTable']/thead/tr/th[8]/span").getText());
    assertEquals("Replaced Product Code", testWebDriver.getElementByXpath("//table[@id='podTable']/thead/tr/th[9]/span").getText());
    assertEquals("Notes", testWebDriver.getElementByXpath("//table[@id='podTable']/thead/tr/th[10]/span").getText());
  }

  private void verifyHeadersWithValuesOnUpdatePODScreen(String orderNumber) throws SQLException {
    assertEquals("Order No.: " + orderNumber, updatePodPage.getOrderNumberLabel() + ": " + updatePodPage.getOrderId());
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

  private String getOrderNumber(String prefix, String program, String type) throws SQLException {
    NumberFormat numberFormat = NumberFormat.getIntegerInstance();
    numberFormat.setMinimumIntegerDigits(8);
    numberFormat.setGroupingUsed(false);
    int id = dbWrapper.getMaxRnrID();
    return prefix + program.substring(0, Math.min(program.length(), 35)) + numberFormat.format(id) + type.substring(0, 1);
  }

  private void setUpData(String program, String userSIC) throws SQLException {
    setupProductTestData("P10", "P11", program, "lvl3_hospital");
    dbWrapper.insertFacilities("F10", "F11");
    dbWrapper.configureTemplate(program);
    List<String> rightsList = asList(CREATE_REQUISITION, CONVERT_TO_ORDER, VIEW_ORDER, MANAGE_POD);
    setupTestUserRoleRightsData(userSIC, rightsList);
    dbWrapper.insertSupervisoryNode("F10", "N1", "Node 1", "null");
    dbWrapper.insertRoleAssignment(userSIC, "store in-charge");
    dbWrapper.insertSchedule("Q1stM", "QuarterMonthly", "QuarterMonth");
    dbWrapper.insertSchedule("M", "Monthly", "Month");
    dbWrapper.insertProcessingPeriod("Period1", "first period", "2012-12-01", "2013-01-15", 1, "Q1stM");
    dbWrapper.insertProcessingPeriod("Period2", "second period", "2013-01-16", "2013-01-30", 1, "M");
    setupRequisitionGroupData("RG1", "RG2", "N1", "N2", "F10", "F11");
    dbWrapper.insertSupplyLines("N1", program, "F10", true);
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
}
