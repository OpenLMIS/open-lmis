/*
 * Copyright © 2013 VillageReach.  All Rights Reserved.  This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 *
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package org.openlmis.functional;


import cucumber.api.DataTable;
import cucumber.api.java.After;
import cucumber.api.java.Before;
import cucumber.api.java.en.And;
import lombok.installer.IdeFinder;
import org.openlmis.UiUtils.CaptureScreenshotOnFailureListener;
import org.openlmis.UiUtils.TestCaseHelper;
import org.openlmis.pageobjects.ConvertOrderPage;
import org.openlmis.pageobjects.HomePage;
import org.openlmis.pageobjects.LoginPage;
import org.openlmis.pageobjects.ViewOrdersPage;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;
import org.testng.annotations.*;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static com.thoughtworks.selenium.SeleneseTestBase.assertTrue;
import static com.thoughtworks.selenium.SeleneseTestNgHelper.assertEquals;
import static java.lang.System.getProperty;

@TransactionConfiguration(defaultRollback = true)
@Transactional

@Listeners(CaptureScreenshotOnFailureListener.class)

public class DownloadOrderFile extends TestCaseHelper {

  public String program = "HIV";

  public String passwordUsers = "TQskzK3iiLfbRVHeM1muvBCiiKriibfl6lh8ipo91hb74G3OvsybvkzpPI4S3KIeWTXAiiwlUU0iiSxWii4wSuS8mokSAieie";
  public String userSICUserName = "storeincharge";
  private final String separator = getProperty("file.separator");
  public  String[] csvRows;

    @Before
  @BeforeMethod(groups = "functional")
  public void setUp() throws Exception {
    super.setup();
  }

  @DataProvider(name = "envData")
  public Object[][] getEnvData() {
    return new Object[][]{};
  }


  @And("^I configure order file:$")
  public void setupOrderFileConfiguration(DataTable userTable) throws Exception {
    List<Map<String, String>> data = userTable.asMaps();
    for (Map map : data)
      dbWrapper.setupOrderFileConfiguration(map.get("File Prefix").toString(), map.get("Header In File").toString());
  }

  @And("^I configure non openlmis order file columns:$")
  public void setupOrderFileNonOpenLMISColumns(DataTable userTable) throws Exception {
    dbWrapper.deleteOrderFileNonOpenLMISColumns();
    List<Map<String, String>> data = userTable.asMaps();
    for (Map map : data)
      dbWrapper.setupOrderFileNonOpenLMISColumns(map.get("Data Field Label").toString(), map.get("Include In Order File").toString(), map.get("Column Label").toString(), Integer.parseInt(map.get("Position").toString()));
  }

  @And("^I configure openlmis order file columns:$")
  public void setupOrderFileOpenLMISColumns(DataTable userTable) throws Exception {
    dbWrapper.defaultSetupOrderFileOpenLMISColumns();
    List<Map<String, String>> data = userTable.asMaps();
    for (Map map : data)
      dbWrapper.setupOrderFileOpenLMISColumns(map.get("Data Field Label").toString(), map.get("Include In Order File").toString(), map.get("Column Label").toString(), Integer.parseInt(map.get("Position").toString()), map.get("Format").toString());
  }

  @And("^I download order file$")
  public void downloadOrderFile() throws Exception {
    ViewOrdersPage viewOrderPage = new ViewOrdersPage(testWebDriver);
    viewOrderPage.downloadCSV();
  }

    @And("^I get order data in file prefix \"([^\"]*)\"$")
    public String[] getOrderDataFromDownloadedFile(String filePrefix) throws Exception {
        csvRows = null;
        testWebDriver.sleep(100);
        String orderId = dbWrapper.getOrderId();



        csvRows = readCSVFile(filePrefix + orderId + ".csv");
        testWebDriver.sleep(1000);
        deleteFile(filePrefix + orderId + ".csv");

        return csvRows;
    }

  @And("^I verify order file line \"([^\"]*)\" having \"([^\"]*)\"$")
  public void checkOrderFileData(int lineNumber,String data) throws Exception {
   assertTrue("Order data incorrect in line number "+ lineNumber, csvRows[lineNumber-1].contains(data));
  }

  @And("^I verify order date format \"([^\"]*)\" in line \"([^\"]*)\"$")
    public void checkOrderFileOrderDate(String dateFormat,int lineNumber) throws Exception {
      String createdDate = dbWrapper.getCreatedDate("orders", dateFormat);
      assertTrue("Order date incorrect.", csvRows[lineNumber-1].contains(createdDate));
  }
    @And("^I verify order id in line \"([^\"]*)\"$")
    public void checkOrderFileOrderId(int lineNumber) throws Exception {
        String orderId = dbWrapper.getOrderId();
        assertTrue("Order date incorrect.", csvRows[lineNumber-1].contains(orderId));
    }

  @Test(groups = {"functional"}, dataProvider = "Data-Provider-Function")
  public void testVerifyOrderFileForSpecificConfiguration(String password) throws Exception {
    dbWrapper.setupOrderFileConfiguration("Zero", "TRUE");
    dbWrapper.defaultSetupOrderFileOpenLMISColumns();

    dbWrapper.setupOrderFileOpenLMISColumns("create.facility.code", "TRUE", "Facility code", 5, "");
    dbWrapper.setupOrderFileOpenLMISColumns("header.order.number", "TRUE", "Order number", 7, "");
    dbWrapper.setupOrderFileOpenLMISColumns("header.quantity.approved", "TRUE", "Approved quantity", 2, "");
    dbWrapper.setupOrderFileOpenLMISColumns("header.product.code", "TRUE", "Product code", 3, "");
    dbWrapper.setupOrderFileOpenLMISColumns("header.order.date", "TRUE", "Order date", 4, "MM-dd-yyyy");
    dbWrapper.setupOrderFileOpenLMISColumns("label.period", "TRUE", "Period", 6, "yyyy-MM");
    dbWrapper.deleteOrderFileNonOpenLMISColumns();
    dbWrapper.setupOrderFileNonOpenLMISColumns("Not Applicable", "TRUE", "Extra 1", 1);
    dbWrapper.setupOrderFileNonOpenLMISColumns("Not Applicable", "TRUE", "", 8);

    setupDownloadOrderFileSetup(password);
    getOrderDataFromDownloadedFile("Zero");
    checkOrderFileData(1, "Extra 1,Approved quantity,Product code,Order date,Facility code,Period,Order number,");
    checkOrderFileData(2, ",10,P10,");
    checkOrderFileData(2,",F10,2012-01,");
    checkOrderFileOrderDate("MM-dd-yyyy", 2);
    checkOrderFileOrderId(2);
  }

  @Test(groups = {"functional"}, dataProvider = "Data-Provider-Function")
  public void testVerifyOrderFileForDefaultConfiguration(String password) throws Exception {
    dbWrapper.setupOrderFileConfiguration("O", "TRUE");
    dbWrapper.defaultSetupOrderFileOpenLMISColumns();

    setupDownloadOrderFileSetup(password);
    getOrderDataFromDownloadedFile("O");
    checkOrderFileData(1,"Order number,Facility code,Product code,Approved quantity,Period,Order date");
    checkOrderFileData(2,",F10,P10,10,01/12,");
    checkOrderFileOrderDate("dd/MM/yy", 2);
    checkOrderFileOrderId(2);
  }

  @Test(groups = {"functional"}, dataProvider = "Data-Provider-Function")
  public void testVerifyOrderFileForDefaultConfigurationWithNoHeades(String password) throws Exception {
    dbWrapper.setupOrderFileConfiguration("O", "FALSE");
    dbWrapper.defaultSetupOrderFileOpenLMISColumns();

    setupDownloadOrderFileSetup(password);
    getOrderDataFromDownloadedFile("O");
    checkOrderFileData(1,",F10,P10,10,01/12,");
    checkOrderFileOrderDate("dd/MM/yy", 1);
    checkOrderFileOrderId(1);
  }

  public void setupDownloadOrderFileSetup(String password) throws Exception {
    List<String> rightsList = new ArrayList<String>();
    rightsList.add("CREATE_REQUISITION");
    rightsList.add("VIEW_REQUISITION");
    rightsList.add("APPROVE_REQUISITION");
    setupTestDataToInitiateRnR(true, program, userSICUserName, "200", "openLmis", rightsList);

    setupTestRoleRightsData("lmu", "ADMIN", "CONVERT_TO_ORDER,VIEW_ORDER");
    dbWrapper.insertUser("212", "lmu", passwordUsers, "F10", "Jake_Doe@openlmis.com", "openLmis");
    dbWrapper.insertRoleAssignment("212", "lmu");

    LoginPage loginPage = new LoginPage(testWebDriver, baseUrlGlobal);
    HomePage homePage = loginPage.loginAs(userSICUserName, password);
    homePage.navigateAndInitiateRnr(program);
    homePage.clickProceed();
    testWebDriver.sleep(2000);
    dbWrapper.insertValuesInRequisition();
    dbWrapper.insertValuesInRegimenLineItems("100", "200", "300", "Regimens data filled");
    dbWrapper.updateRequisitionStatus("SUBMITTED");
    dbWrapper.updateRequisitionStatus("AUTHORIZED");
    dbWrapper.insertApprovedQuantity(10);
    dbWrapper.updateRequisitionStatus("APPROVED");

    homePage.logout(baseUrlGlobal);
    loginPage.loginAs("lmu", password);
    homePage.navigateConvertToOrder();

    ConvertOrderPage convertOrderPage = new ConvertOrderPage(testWebDriver);
    convertOrderPage.clickConvertToOrderButton();
    convertOrderPage.clickCheckBoxConvertToOrder();
    convertOrderPage.clickConvertToOrderButton();
    convertOrderPage.clickOk();
    homePage.navigateViewOrders();
    downloadOrderFile();

  }

  @After
  @AfterMethod(groups = "functional")
  public void tearDown() throws Exception {
    testWebDriver.sleep(500);
    if (!testWebDriver.getElementById("username").isDisplayed()) {
      HomePage homePage = new HomePage(testWebDriver);
      homePage.logout(baseUrlGlobal);
    }
    dbWrapper.deleteData();
    dbWrapper.closeConnection();
  }

  @DataProvider(name = "Data-Provider-Function")
  public Object[][] parameterIntTestProviderPositive() {
    return new Object[][]{
      {"Admin123"}
    };
  }
}

