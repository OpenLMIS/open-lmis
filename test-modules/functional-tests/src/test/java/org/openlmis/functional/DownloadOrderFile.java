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


import cucumber.api.DataTable;
import cucumber.api.java.en.And;
import org.openlmis.UiUtils.CaptureScreenshotOnFailureListener;
import org.openlmis.UiUtils.TestCaseHelper;
import org.openlmis.pageobjects.HomePage;
import org.openlmis.pageobjects.LoginPage;
import org.openlmis.pageobjects.PageObjectFactory;
import org.openlmis.pageobjects.ViewOrdersPage;
import org.openlmis.pageobjects.edi.ConvertOrderPage;
import org.testng.annotations.*;

import java.io.IOException;
import java.sql.SQLException;
import java.text.NumberFormat;
import java.util.List;
import java.util.Map;

import static com.thoughtworks.selenium.SeleneseTestBase.assertTrue;
import static com.thoughtworks.selenium.SeleneseTestCase.assertEquals;
import static java.util.Arrays.asList;

@Listeners(CaptureScreenshotOnFailureListener.class)

public class DownloadOrderFile extends TestCaseHelper {

  public String program = "HIV";
  public String passwordUsers = "TQskzK3iiLfbRVHeM1muvBCiiKriibfl6lh8ipo91hb74G3OvsybvkzpPI4S3KIeWTXAiiwlUU0iiSxWii4wSuS8mokSAieie";
  public String userSICUserName = "storeInCharge";
  public String[] csvRows = null;

  @BeforeMethod(groups = "orderAndPod")
  public void setUp() throws InterruptedException, SQLException, IOException {
    super.setup();
  }

  @DataProvider(name = "envData")
  public Object[][] getEnvData() {
    return new Object[][]{};
  }

  @And("^I configure order file:$")
  public void setupOrderFileConfiguration(DataTable userTable) throws SQLException {
    List<Map<String, String>> data = userTable.asMaps(String.class, String.class);
    for (Map map : data)
      dbWrapper.setupOrderFileConfiguration(map.get("File Prefix").toString(), map.get("Header In File").toString());
  }

  @And("^I configure non openlmis order file columns:$")
  public void setupOrderFileNonOpenLMISColumns(DataTable userTable) throws SQLException {
    dbWrapper.deleteRowFromTable("order_file_columns", "openLMISField", "false");
    List<Map<String, String>> data = userTable.asMaps(String.class, String.class);
    for (Map map : data)
      dbWrapper.setupOrderFileNonOpenLMISColumns(map.get("Data Field Label").toString(), map.get("Include In Order File").toString(), map.get("Column Label").toString(), Integer.parseInt(map.get("Position").toString()));
  }

  @And("^I configure openlmis order file columns:$")
  public void setupOrderFileOpenLMISColumns(DataTable userTable) throws SQLException {
    List<Map<String, String>> data = userTable.asMaps(String.class, String.class);
    for (Map map : data)
      dbWrapper.setupOrderFileOpenLMISColumns(map.get("Data Field Label").toString(), map.get("Include In Order File").toString(), map.get("Column Label").toString(), Integer.parseInt(map.get("Position").toString()), map.get("Format").toString());
  }

  @And("^I download order file$")
  public void downloadOrderFile() throws InterruptedException {
    ViewOrdersPage viewOrdersPage = PageObjectFactory.getViewOrdersPage(testWebDriver);
    viewOrdersPage.downloadCSV();
    testWebDriver.sleep(5000);
  }

  @And("^I get order data in file prefix \"([^\"]*)\"$")
  public void getOrderDataFromDownloadedFile(String filePrefix) throws InterruptedException, SQLException, IOException {
    String orderId = String.valueOf(dbWrapper.getMaxRnrID());
    csvRows = readCSVFile(filePrefix + orderId + ".csv");
    testWebDriver.sleep(5000);
    deleteFile(filePrefix + orderId + ".csv");
  }

  @And("^I verify order file line \"([^\"]*)\" having \"([^\"]*)\"$")
  public void checkOrderFileData(int lineNumber, String data) {
    testWebDriver.sleep(1000);
    assertTrue("Order data incorrect in line number " + lineNumber, csvRows[lineNumber - 1].contains(data));
  }

  @And("^I verify order date format \"([^\"]*)\" in line \"([^\"]*)\"$")
  public void checkOrderFileOrderDate(String dateFormat, int lineNumber) throws SQLException {
    String createdDate = dbWrapper.getCreatedDate("orders", dateFormat);
    assertTrue("Order date incorrect.", csvRows[lineNumber - 1].contains(createdDate));
  }

  @And("^I verify order id in line \"([^\"]*)\"$")
  public void checkOrderFileOrderId(int lineNumber) throws SQLException {
    String orderId = String.valueOf(dbWrapper.getMaxRnrID());
    assertTrue("Order date incorrect.", csvRows[lineNumber - 1].contains(orderId));
  }

  @Test(groups = {"orderAndPod"}, dataProvider = "Data-Provider-Function")
  public void testVerifyOrderFileForDefaultConfiguration(String password) throws InterruptedException, SQLException, IOException {
    dbWrapper.setupOrderFileConfiguration("O", "TRUE");
    setupDownloadOrderFileSetup(password);
    NumberFormat numberFormat = NumberFormat.getIntegerInstance();
    numberFormat.setMinimumIntegerDigits(8);
    numberFormat.setGroupingUsed(false);
    int id = dbWrapper.getMaxRnrID();
    String orderNumber = "OHIV" + numberFormat.format(id) + "R";
    verifyOrderNumberOnViewRequisitionPage(orderNumber, 1);
    getOrderDataFromDownloadedFile("O");

    checkOrderFileData(1, "Order number,Facility code,Product code,Product name,Approved quantity,Period,Order date");
    checkOrderFileData(2, "\"" + orderNumber + "\",\"F10\",\"P10\",\"antibiotic Capsule 300/200/600 mg\",\"10\",\"");
    checkOrderFileOrderDate("dd/MM/yy", 2);
  }

  @Test(groups = {"orderAndPod"}, dataProvider = "Data-Provider-Function")
  public void testVerifyOrderFileHavingStrengthFormDosageUnitIsNull(String password) throws InterruptedException, SQLException, IOException {
    dbWrapper.setupOrderFileConfiguration("O", "TRUE");

    List<String> rightsList = asList("CREATE_REQUISITION", "VIEW_REQUISITION", "APPROVE_REQUISITION");
    setupTestDataToInitiateRnR(true, program, userSICUserName, rightsList);

    setupTestRoleRightsData("lmu", "CONVERT_TO_ORDER,VIEW_ORDER");
    dbWrapper.insertUser("lmu", passwordUsers, "F10", "Jake_Doe@openlmis.com");
    dbWrapper.insertRoleAssignment("lmu", "lmu");
    dbWrapper.insertFulfilmentRoleAssignment("lmu", "lmu", "F10");

    dbWrapper.updateFieldValueToNull("products", "strength", "code", "P10");
    dbWrapper.updateFieldValueToNull("products", "formId", "code", "P10");
    dbWrapper.updateFieldValueToNull("products", "dosageUnitId", "code", "P10");

    LoginPage loginPage = PageObjectFactory.getLoginPage(testWebDriver, baseUrlGlobal);
    HomePage homePage = loginPage.loginAs(userSICUserName, password);
    homePage.navigateAndInitiateRnr(program);
    homePage.clickProceed();
    testWebDriver.sleep(2000);
    dbWrapper.insertValuesInRequisition(false);
    dbWrapper.insertValuesInRegimenLineItems("100", "200", "300", "Regimens data filled");
    dbWrapper.updateRequisitionStatus("SUBMITTED", userSICUserName, "HIV");
    dbWrapper.updateRequisitionStatus("AUTHORIZED", userSICUserName, "HIV");
    dbWrapper.updateFieldValue("requisition_line_items", "quantityApproved", 10);
    dbWrapper.updateRequisitionStatus("APPROVED", userSICUserName, "HIV");

    homePage.logout(baseUrlGlobal);
    loginPage.loginAs("lmu", password);
    homePage.navigateConvertToOrder();

    ConvertOrderPage convertOrderPage = PageObjectFactory.getConvertOrderPage(testWebDriver);
    convertOrderPage.clickConvertToOrderButton();
    convertOrderPage.clickCheckBoxConvertToOrder();
    convertOrderPage.clickConvertToOrderButton();
    convertOrderPage.clickOk();
    homePage.navigateViewOrders();

    NumberFormat numberFormat = NumberFormat.getIntegerInstance();
    numberFormat.setMinimumIntegerDigits(8);
    numberFormat.setGroupingUsed(false);
    int id = dbWrapper.getMaxRnrID();
    String orderNumber = "OHIV" + numberFormat.format(id) + "R";
    verifyOrderNumberOnViewRequisitionPage(orderNumber, 1);

    downloadOrderFile();

    getOrderDataFromDownloadedFile("O");

    checkOrderFileData(1, "Order number,Facility code,Product code,Product name,Approved quantity,Period,Order date");
    checkOrderFileData(2, "\"" + orderNumber + "\",\"F10\",\"P10\",\"antibiotic   \",\"10\",\"");
  }

  @Test(groups = {"orderAndPod"}, dataProvider = "Data-Provider-Function")
  public void testVerifyOrderFileForDefaultConfigurationWithNoHeaders(String password) throws InterruptedException, SQLException, IOException {
    dbWrapper.setupOrderFileConfiguration("O", "FALSE");
    setupDownloadOrderFileSetup(password);
    NumberFormat numberFormat = NumberFormat.getIntegerInstance();
    numberFormat.setMinimumIntegerDigits(8);
    numberFormat.setGroupingUsed(false);
    int id = dbWrapper.getMaxRnrID();
    String orderNumber = "OHIV" + numberFormat.format(id) + "R";
    verifyOrderNumberOnViewRequisitionPage(orderNumber, 1);

    getOrderDataFromDownloadedFile("O");
    checkOrderFileData(1, "\"" + orderNumber + "\",\"F10\",\"P10\",\"antibiotic Capsule 300/200/600 mg\",\"10\",\"");
    checkOrderFileOrderDate("dd/MM/yy", 1);
  }

  @Test(groups = {"orderAndPod"}, dataProvider = "Data-Provider-Function")
  public void testVerifyOrderFileForOnlySequenceInOrderNumber(String password) throws InterruptedException, SQLException, IOException {
    dbWrapper.setupOrderFileConfiguration("O", "FALSE");
    dbWrapper.updateFieldValue("order_number_configuration", "orderNumberPrefix", "#Ord 3", "orderNumberPrefix", "O");
    dbWrapper.updateFieldValue("order_number_configuration", "includeOrderNumberPrefix", false);
    dbWrapper.updateFieldValue("order_number_configuration", "includeProgramCode", false);
    dbWrapper.updateFieldValue("order_number_configuration", "includeRnrTypeSuffix", false);

    setupDownloadOrderFileSetup(password);
    NumberFormat numberFormat = NumberFormat.getIntegerInstance();
    numberFormat.setMinimumIntegerDigits(8);
    numberFormat.setGroupingUsed(false);
    int id = dbWrapper.getMaxRnrID();
    String orderNumber = numberFormat.format(id);
    verifyOrderNumberOnViewRequisitionPage(orderNumber, 1);

    getOrderDataFromDownloadedFile("O");
    checkOrderFileData(1, "\"" + orderNumber + "\",\"F10\",\"P10\",\"antibiotic Capsule 300/200/600 mg\",\"10\",\"01/12\",");
    checkOrderFileOrderDate("dd/MM/yy", 1);
    dbWrapper.updateFieldValue("order_number_configuration", "orderNumberPrefix", "O", "orderNumberPrefix", "#Ord 3");
    dbWrapper.updateFieldValue("order_number_configuration", "includeOrderNumberPrefix", true);
    dbWrapper.updateFieldValue("order_number_configuration", "includeProgramCode", true);
    dbWrapper.updateFieldValue("order_number_configuration", "includeRnrTypeSuffix", true);
  }

  @Test(groups = {"orderAndPod"}, dataProvider = "Data-Provider-Function")
  public void testVerifyOrderFileForMaxOrderNumber(String password) throws InterruptedException, SQLException, IOException {
    dbWrapper.setupOrderFileConfiguration("O", "FALSE");
    dbWrapper.updateFieldValue("order_number_configuration", "orderNumberPrefix", " #O 3 ", "orderNumberPrefix", "O");

    List<String> rightsList = asList("CREATE_REQUISITION", "VIEW_REQUISITION", "APPROVE_REQUISITION");
    setupTestDataToInitiateRnR(true, program, userSICUserName, rightsList);

    setupTestRoleRightsData("lmu", "CONVERT_TO_ORDER,VIEW_ORDER");
    dbWrapper.insertUser("lmu", passwordUsers, "F10", "Jake_Doe@openlmis.com");
    dbWrapper.insertRoleAssignment("lmu", "lmu");
    dbWrapper.insertFulfilmentRoleAssignment("lmu", "lmu", "F10");
    dbWrapper.insertCurrentPeriod("current", "current", 1, "M");

    LoginPage loginPage = PageObjectFactory.getLoginPage(testWebDriver, baseUrlGlobal);
    HomePage homePage = loginPage.loginAs(userSICUserName, password);
    homePage.navigateAndInitiateEmergencyRnr(program);
    homePage.clickProceed();
    testWebDriver.sleep(2000);
    dbWrapper.insertValuesInRequisition(false);
    dbWrapper.insertValuesInRegimenLineItems("100", "200", "300", "Regimens data filled");
    dbWrapper.updateRequisitionStatus("SUBMITTED", userSICUserName, "HIV");
    dbWrapper.updateRequisitionStatus("AUTHORIZED", userSICUserName, "HIV");
    dbWrapper.updateFieldValue("requisition_line_items", "quantityApproved", 10);
    dbWrapper.updateRequisitionStatus("APPROVED", userSICUserName, "HIV");

    homePage.logout(baseUrlGlobal);
    dbWrapper.updateFieldValue("programs", "code", "MALARIA123456789MALARIA123456789MALARIA123456789", "code", "HIV");
    loginPage.loginAs("lmu", password);
    homePage.navigateConvertToOrder();

    ConvertOrderPage convertOrderPage = PageObjectFactory.getConvertOrderPage(testWebDriver);
    convertOrderPage.clickConvertToOrderButton();
    convertOrderPage.clickCheckBoxConvertToOrder();
    convertOrderPage.clickConvertToOrderButton();
    convertOrderPage.clickOk();
    homePage.navigateViewOrders();
    downloadOrderFile();

    NumberFormat numberFormat = NumberFormat.getIntegerInstance();
    numberFormat.setMinimumIntegerDigits(8);
    numberFormat.setGroupingUsed(false);
    int id = dbWrapper.getMaxRnrID();
    String orderNumber = " #O 3 MALARIA123456789MALARIA123456789MAL" + numberFormat.format(id) + "E";
    verifyOrderNumberOnViewRequisitionPage(orderNumber, 1);

    getOrderDataFromDownloadedFile("O");
    checkOrderFileData(1, "\"" + orderNumber + "\",\"F10\",\"P10\",\"antibiotic Capsule 300/200/600 mg\",\"10\",");
    checkOrderFileOrderDate("dd/MM/yy", 1);
    dbWrapper.updateFieldValue("order_number_configuration", "orderNumberPrefix", "O", "orderNumberPrefix", " #O 3 ");
    dbWrapper.updateFieldValue("programs", "code", "HIV", "code", "MALARIA123456789MALARIA123456789MALARIA123456789");
  }

  @Test(groups = {"orderAndPod"}, dataProvider = "Data-Provider-Function")
  public void testVerifyOrderFileForSpecificConfiguration(String password) throws SQLException, IOException, InterruptedException {
    dbWrapper.setupOrderFileConfiguration("Zero", "TRUE");
    dbWrapper.setupOrderFileOpenLMISColumns("create.facility.code", "TRUE", "Facility code", 6, "");
    dbWrapper.setupOrderFileOpenLMISColumns("header.order.number", "TRUE", "Order number", 8, "");
    dbWrapper.setupOrderFileOpenLMISColumns("header.quantity.approved", "TRUE", "Approved quantity", 2, "");
    dbWrapper.setupOrderFileOpenLMISColumns("header.product.code", "TRUE", "Product code", 3, "");
    dbWrapper.setupOrderFileOpenLMISColumns("header.product.name", "TRUE", "Product name", 4, "");
    dbWrapper.setupOrderFileOpenLMISColumns("header.order.date", "TRUE", "Order date", 5, "MM-dd-yyyy");
    dbWrapper.setupOrderFileOpenLMISColumns("label.period", "TRUE", "Period", 7, "yyyy-MM");
    dbWrapper.deleteRowFromTable("order_file_columns", "openLMISField", "false");
    dbWrapper.setupOrderFileNonOpenLMISColumns("Not Applicable", "TRUE", "Extra 1", 1);
    dbWrapper.setupOrderFileNonOpenLMISColumns("Not Applicable", "TRUE", "", 9);

    setupDownloadOrderFileSetup(password);
    NumberFormat numberFormat = NumberFormat.getIntegerInstance();
    numberFormat.setMinimumIntegerDigits(8);
    numberFormat.setGroupingUsed(false);
    int id = dbWrapper.getMaxRnrID();
    String orderNumber = "OHIV" + numberFormat.format(id) + "R";
    verifyOrderNumberOnViewRequisitionPage(orderNumber, 1);

    getOrderDataFromDownloadedFile("Zero");
    checkOrderFileData(1, "Extra 1,Approved quantity,Product code,Product name,Order date,Facility code,Period,Order number,");
    checkOrderFileData(2, ",\"10\",\"P10\",\"antibiotic Capsule 300/200/600 mg\"");
    checkOrderFileData(2, ",\"F10\",\"2012-01\",");
    checkOrderFileOrderDate("MM-dd-yyyy", 2);

    dbWrapper.setupOrderFileOpenLMISColumns("create.facility.code", "TRUE", "Facility code", 2, "");
    dbWrapper.setupOrderFileOpenLMISColumns("header.order.number", "TRUE", "Order number", 1, "");
    dbWrapper.setupOrderFileOpenLMISColumns("header.quantity.approved", "TRUE", "Approved quantity", 5, "");
    dbWrapper.setupOrderFileOpenLMISColumns("header.product.code", "TRUE", "Product code", 3, "");
    dbWrapper.setupOrderFileOpenLMISColumns("header.product.name", "TRUE", "Product name", 4, "");
    dbWrapper.setupOrderFileOpenLMISColumns("header.order.date", "TRUE", "Order date", 7, "dd/MM/yy");
    dbWrapper.setupOrderFileOpenLMISColumns("label.period", "TRUE", "Period", 6, "MM/yy");
    dbWrapper.deleteRowFromTable("order_file_columns", "openLMISField", "false");
  }

  public void setupDownloadOrderFileSetup(String password) throws SQLException, InterruptedException {
    List<String> rightsList = asList("CREATE_REQUISITION", "VIEW_REQUISITION", "APPROVE_REQUISITION");
    setupTestDataToInitiateRnR(true, program, userSICUserName, rightsList);

    setupTestRoleRightsData("lmu", "CONVERT_TO_ORDER,VIEW_ORDER");
    dbWrapper.insertUser("lmu", passwordUsers, "F10", "Jake_Doe@openlmis.com");
    dbWrapper.insertRoleAssignment("lmu", "lmu");
    dbWrapper.insertFulfilmentRoleAssignment("lmu", "lmu", "F10");

    LoginPage loginPage = PageObjectFactory.getLoginPage(testWebDriver, baseUrlGlobal);
    HomePage homePage = loginPage.loginAs(userSICUserName, password);
    homePage.navigateAndInitiateRnr(program);
    homePage.clickProceed();
    testWebDriver.sleep(2000);
    dbWrapper.insertValuesInRequisition(false);
    dbWrapper.insertValuesInRegimenLineItems("100", "200", "300", "Regimens data filled");
    dbWrapper.updateRequisitionStatus("SUBMITTED", userSICUserName, "HIV");
    dbWrapper.updateRequisitionStatus("AUTHORIZED", userSICUserName, "HIV");
    dbWrapper.updateFieldValue("requisition_line_items", "quantityApproved", 10);
    dbWrapper.updateRequisitionStatus("APPROVED", userSICUserName, "HIV");

    homePage.logout(baseUrlGlobal);
    loginPage.loginAs("lmu", password);
    homePage.navigateConvertToOrder();

    ConvertOrderPage convertOrderPage = PageObjectFactory.getConvertOrderPage(testWebDriver);
    convertOrderPage.clickConvertToOrderButton();
    convertOrderPage.clickCheckBoxConvertToOrder();
    convertOrderPage.clickConvertToOrderButton();
    convertOrderPage.clickOk();
    homePage.navigateViewOrders();
    downloadOrderFile();
  }

  public void verifyOrderNumberOnViewRequisitionPage(String orderNumber, int rowNumber) {
    ViewOrdersPage viewOrdersPage = PageObjectFactory.getViewOrdersPage(testWebDriver);
    assertEquals(orderNumber, viewOrdersPage.getOrderNumber(rowNumber));
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

  @DataProvider(name = "Data-Provider-Function")
  public Object[][] parameterIntTestProviderPositive() {
    return new Object[][]{
      {"Admin123"}
    };
  }
}

