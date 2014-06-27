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

import static com.thoughtworks.selenium.SeleneseTestBase.assertEquals;
import static com.thoughtworks.selenium.SeleneseTestBase.assertTrue;

@Listeners(CaptureScreenshotOnFailureListener.class)

public class ManageReport extends TestCaseHelper {

  String reportName, fileName;
  LoginPage loginPage;
  ReportPage reportPage;

  @BeforeMethod(groups = {"admin"})
  public void setUp() throws InterruptedException, SQLException, IOException {
    super.setup();
    dbWrapper.deleteRowFromTable("report_rights", "rightName", reportName);
    dbWrapper.deleteRowFromTable("templates", "name", reportName);
    dbWrapper.deleteRowFromTable("rights", "name", reportName);
    reportName = "Test-Report";
    fileName = "OrderRoutingConsistencyReport.jrxml";
    loginPage = PageObjectFactory.getLoginPage(testWebDriver, baseUrlGlobal);
  }

  @Test(groups = {"admin"}, dataProvider = "Data-Provider-Function-Positive")
  public void invalidScenariosReports(String[] credentials) {
    HomePage homePage = loginPage.loginAs(credentials[0], credentials[1]);
    reportPage = homePage.navigateReportScreen();
    assertEquals("Reports", reportPage.getReportHeader());
    assertEquals("Report name", reportPage.getReportNameHeader());
    assertEquals("View", reportPage.getViewHeader());
    reportPage.clickAddNewButton();

    assertEquals("Report name *", reportPage.getNameLabel());
    assertEquals("Description", reportPage.getDescriptionLabel());
    assertEquals("Upload file *", reportPage.getUploadFileLabel());
    assertTrue("Save button missing", reportPage.isSaveButtonDisplayed());
    assertTrue("Cancel button missing", reportPage.isCancelButtonDisplayed());

    reportPage.clickSaveButton();
    assertEquals("Please fill this value", reportPage.getErrorReportNameMessage());
    assertEquals("Please fill this value", reportPage.getErrorFileMessage());

    reportPage.enterReportName(reportName);
    reportPage.clickSaveButton();
    assertEquals("Please fill this value", reportPage.getErrorFileMessage());

    reportPage.enterReportName("");
    reportPage.uploadFile(fileName);
    reportPage.clickSaveButton();
    assertEquals("Please fill this value", reportPage.getErrorReportNameMessage());
  }

  @Test(groups = {"admin"}, dataProvider = "Data-Provider-Function-Positive")
  public void uploadWrongReport(String[] credentials) {
    HomePage homePage = loginPage.loginAs(credentials[0], credentials[1]);
    reportPage = homePage.navigateReportScreen();
    reportPage.clickAddNewButton();

    testWebDriver.sleep(1000);
    fileName = "invalidActivefacility.jrxml";
    reportPage.enterReportName(reportName);
    reportPage.uploadFile(fileName);
    reportPage.clickSaveButton();
    testWebDriver.sleep(1000);
    assertEquals("File uploaded is invalid", reportPage.getSaveErrorMessage());
  }

  @Test(groups = {"admin"}, dataProvider = "Data-Provider-Function-Positive")
  public void uploadValidReport(String[] credentials) {
    HomePage homePage = loginPage.loginAs(credentials[0], credentials[1]);
    reportPage = homePage.navigateReportScreen();

    reportPage.clickAddNewButton();
    reportPage.enterReportName(reportName);
    reportPage.uploadFile(fileName);
    reportPage.clickSaveButton();
    assertEquals("Report created successfully", reportPage.getSaveSuccessMessage());
    assertTrue("Report Name '" + reportName + "' should display in list", reportPage.getReportName(8).equalsIgnoreCase(reportName));
    verifyItemsOnReportListScreen();
  }

  @Test(groups = {"admin"}, dataProvider = "Data-Provider-Function-Positive")
  public void verifyDuplicateReport(String[] credentials) {
    HomePage homePage = loginPage.loginAs(credentials[0], credentials[1]);
    reportPage = homePage.navigateReportScreen();

    reportPage.clickAddNewButton();
    reportPage.enterReportName(reportName);
    reportPage.uploadFile(fileName);
    reportPage.enterReportDescription("describe");
    reportPage.clickSaveButton();

    reportPage.clickAddNewButton();
    reportPage.enterReportName(reportName);
    reportPage.uploadFile(fileName);
    reportPage.clickSaveButton();
    assertEquals("Report with same name already exists", reportPage.getSaveErrorMessage());

    reportPage.clickCancelButton();
    assertTrue("Report Name '" + reportName + "' should display in list", reportPage.getReportName(8).equalsIgnoreCase(reportName));
  }

  @Test(groups = {"admin"}, dataProvider = "Data-Provider-Function-Positive")
  public void verifyDefaultReports(String[] credentials) {
    HomePage homePage = loginPage.loginAs(credentials[0], credentials[1]);
    reportPage = homePage.navigateReportScreen();

    reportName = "Facilities Missing Supporting Requisition Group";
    assertTrue("Report Name '" + reportName + "' should display in list", reportPage.getReportName(1).equalsIgnoreCase(reportName));
    reportName = "Facilities Missing Create Requisition Role";
    assertTrue("Report Name '" + reportName + "' should display in list", reportPage.getReportName(2).equalsIgnoreCase(reportName));
    reportName = "Facilities Missing Authorize Requisition Role";
    assertTrue("Report Name '" + reportName + "' should display in list", reportPage.getReportName(3).equalsIgnoreCase(reportName));
    reportName = "Supervisory Nodes Missing Approve Requisition Role";
    assertTrue("Report Name '" + reportName + "' should display in list", reportPage.getReportName(4).equalsIgnoreCase(reportName));
    reportName = "Requisition Groups Missing Supply Line";
    assertTrue("Report Name '" + reportName + "' should display in list", reportPage.getReportName(5).equalsIgnoreCase(reportName));
    reportName = "Order Routing Inconsistencies";
    assertTrue("Report Name '" + reportName + "' should display in list", reportPage.getReportName(6).equalsIgnoreCase(reportName));
    reportName = "Delivery Zones Missing Manage Distribution Role";
    assertTrue("Report Name '" + reportName + "' should display in list", reportPage.getReportName(7).equalsIgnoreCase(reportName));
    reportName = "Test-Report";
  }

  public void verifyItemsOnReportListScreen() {
    assertTrue("PDF link missing", reportPage.isPDFLinkDisplayed());
    assertTrue("XLS link missing", reportPage.isXLSLinkDisplayed());
    assertTrue("CSV link missing", reportPage.isCSVLinkDisplayed());
    assertTrue("HTML link missing", reportPage.isHTMLLinkDisplayed());
  }

  @AfterMethod(groups = {"admin"})
  public void tearDown() throws SQLException {
    HomePage homePage = PageObjectFactory.getHomePage(testWebDriver);
    homePage.logout(baseUrlGlobal);
    dbWrapper.deleteData();
    dbWrapper.deleteRowFromTable("report_rights", "rightName", reportName);
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
