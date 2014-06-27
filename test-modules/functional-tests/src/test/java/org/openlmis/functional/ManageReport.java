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


import org.openlmis.UiUtils.CaptureScreenshotOnFailureListener;
import org.openlmis.UiUtils.TestCaseHelper;
import org.openlmis.pageobjects.HomePage;
import org.openlmis.pageobjects.LoginPage;
import org.openlmis.pageobjects.PageObjectFactory;
import org.openlmis.pageobjects.ReportPage;
import org.testng.annotations.*;

import java.io.IOException;
import java.sql.SQLException;

@Listeners(CaptureScreenshotOnFailureListener.class)

public class ManageReport extends TestCaseHelper {

  String reportName, fileName;
  LoginPage loginPage;

  @BeforeMethod(groups = {"admin"})
  public void setUp() throws InterruptedException, SQLException, IOException {
    super.setup();
    reportName = "Test-Report";
    fileName = "OrderRoutingConsistencyReport.jrxml";
    loginPage = PageObjectFactory.getLoginPage(testWebDriver, baseUrlGlobal);
  }

  @Test(groups = {"admin"}, dataProvider = "Data-Provider-Function-Positive")
  public void invalidScenariosReports(String[] credentials) {
    HomePage homePage = loginPage.loginAs(credentials[0], credentials[1]);
    ReportPage reportPage = homePage.navigateReportScreen();
    reportPage.clickAddNewButton();

    reportPage.verifyItemsOnReportUploadScreen();
    reportPage.clickSaveButton();
    reportPage.verifyErrorMessageDivReportName();
    reportPage.verifyErrorMessageDivUploadFile();

    reportPage.enterReportName(reportName);
    reportPage.clickSaveButton();
    reportPage.verifyErrorMessageDivUploadFile();

    reportPage.enterReportName("");
    reportPage.uploadFile(fileName);
    reportPage.clickSaveButton();
    reportPage.verifyErrorMessageDivReportName();
  }

  @Test(groups = {"admin"}, dataProvider = "Data-Provider-Function-Positive")
  public void uploadWrongReport(String[] credentials) {
    HomePage homePage = loginPage.loginAs(credentials[0], credentials[1]);
    ReportPage reportPage = homePage.navigateReportScreen();
    reportPage.clickAddNewButton();

    testWebDriver.sleep(1000);
    fileName = "invalidActivefacility.jrxml";
    reportPage.enterReportName(reportName);
    reportPage.uploadFile(fileName);
    reportPage.clickSaveButton();
    testWebDriver.sleep(1000);
    //verifyErrorMessageInvalidFile();
  }

  @Test(groups = {"admin"}, dataProvider = "Data-Provider-Function-Positive")
  public void uploadValidReport(String[] credentials) {
    HomePage homePage = loginPage.loginAs(credentials[0], credentials[1]);
    ReportPage reportPage = homePage.navigateReportScreen();

    reportPage.clickAddNewButton();
    reportPage.enterReportName(reportName);
    reportPage.uploadFile(fileName);
    reportPage.clickSaveButton();
    reportPage.verifySuccessMessageDiv();
    reportPage.verifyReportNameInList(reportName, 8);
    reportPage.verifyItemsOnReportListScreen();
  }

  @Test(groups = {"admin"}, dataProvider = "Data-Provider-Function-Positive")
  public void verifyDuplicateReport(String[] credentials) {
    HomePage homePage = loginPage.loginAs(credentials[0], credentials[1]);
    ReportPage reportPage = homePage.navigateReportScreen();

    reportPage.clickAddNewButton();
    reportPage.enterReportName(reportName);
    reportPage.uploadFile(fileName);
    reportPage.clickSaveButton();

    reportPage.clickAddNewButton();
    reportPage.enterReportName(reportName);
    reportPage.uploadFile(fileName);
    reportPage.clickSaveButton();

    reportPage.verifyErrorMessageDivFooter();

    reportPage.clickCancelButton();
    reportPage.verifyReportNameInList(reportName, 8);
  }

  @Test(groups = {"admin"}, dataProvider = "Data-Provider-Function-Positive")
  public void verifyDefaultReports(String[] credentials) {
    HomePage homePage = loginPage.loginAs(credentials[0], credentials[1]);
    ReportPage reportPage = homePage.navigateReportScreen();

    reportPage.verifyReportNameInList("Facilities Missing Supporting Requisition Group", 1);
    reportPage.verifyReportNameInList("Facilities Missing Create Requisition Role", 2);
    reportPage.verifyReportNameInList("Facilities Missing Authorize Requisition Role", 3);
    reportPage.verifyReportNameInList("Supervisory Nodes Missing Approve Requisition Role", 4);
    reportPage.verifyReportNameInList("Requisition Groups Missing Supply Line", 5);
    reportPage.verifyReportNameInList("Order Routing Inconsistencies", 6);
    reportPage.verifyReportNameInList("Delivery Zones Missing Manage Distribution Role", 7);
  }

  @AfterMethod(groups = {"admin"})
  public void tearDown() throws SQLException {
    HomePage homePage = PageObjectFactory.getHomePage(testWebDriver);
    homePage.logout(baseUrlGlobal);
    dbWrapper.deleteData();
    dbWrapper.deleteRowFromTable("templates", "name", reportName);
    dbWrapper.deleteRowFromTable("rights", "name", reportName);
    dbWrapper.closeConnection();
  }

  @DataProvider(name = "Data-Provider-Function-Positive")
  public Object[][] parameterIntTestProviderPositive() {
    return new Object[][]{
      {new String[]{"Admin123", "Admin123"}}
    };
  }
}
