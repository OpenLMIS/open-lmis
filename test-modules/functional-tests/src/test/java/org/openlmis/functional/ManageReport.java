/*
 * Copyright © 2013 VillageReach.  All Rights Reserved.  This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 *
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package org.openlmis.functional;


import org.openlmis.UiUtils.CaptureScreenshotOnFailureListener;
import org.openlmis.UiUtils.TestCaseHelper;
import org.openlmis.pageobjects.*;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;
import org.testng.annotations.Test;
import org.testng.annotations.Listeners;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.DataProvider;

import java.text.SimpleDateFormat;
import java.util.Date;

@TransactionConfiguration(defaultRollback = true)
@Transactional

@Listeners(CaptureScreenshotOnFailureListener.class)

public class ManageReport extends TestCaseHelper {

  String reportName, fileName;

  public ManageReport() {
    reportName = "Test-Report" + getCurrentDateAndTime();
    fileName = "activefacility.jrxml";
  }


  @BeforeMethod(groups = {"admin"})
  public void setUp() throws Exception {
    super.setup();
  }

  @Test(groups = {"admin"}, dataProvider = "Data-Provider-Function-Positive")
  public void invalidScenariosReports(String[] credentials) throws Exception {

    LoginPage loginPage = new LoginPage(testWebDriver, baseUrlGlobal);

    HomePage homePage = loginPage.loginAs(credentials[0], credentials[1]);
    ReportPage reportPage = homePage.navigateReportScreen();
    //reportPage.verifyNoReportsMessage();
    reportPage.clickAddNewButton();
    reportPage.verifyItemsOnReportUploadScreen();

    fileName = "invalidActivefacility.jrxml";

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

    reportPage.enterReportName(reportName);
    reportPage.uploadFile(fileName);
    reportPage.clickSaveButton();
    reportPage.verifyErrorMessageDivFooter();
  }

  @Test(groups = {"admin"}, dataProvider = "Data-Provider-Function-Positive", dependsOnMethods = "invalidScenariosReports")
  public void uploadManageReport(String[] credentials) throws Exception {

    LoginPage loginPage = new LoginPage(testWebDriver, baseUrlGlobal);

    HomePage homePage = loginPage.loginAs(credentials[0], credentials[1]);
    ReportPage reportPage = homePage.navigateReportScreen();
//    reportPage.verifyNoReportsMessage();
    reportPage.clickAddNewButton();
    reportPage.verifyItemsOnReportUploadScreen();

    fileName = "activefacility.jrxml";
    reportPage.enterReportName(reportName);
    reportPage.uploadFile(fileName);
    reportPage.clickSaveButton();
    reportPage.verifySuccessMessageDiv();
    reportPage.verifyReportNameInList(reportName, 8);
    reportPage.verifyItemsOnReportListScreen();
  }

  @Test(groups = {"admin"}, dataProvider = "Data-Provider-Function-Positive", dependsOnMethods = "uploadManageReport")
  public void verifyDuplicateReport(String[] credentials) throws Exception {

    LoginPage loginPage = new LoginPage(testWebDriver, baseUrlGlobal);

    HomePage homePage = loginPage.loginAs(credentials[0], credentials[1]);
    ReportPage reportPage = homePage.navigateReportScreen();

    reportPage.clickAddNewButton();
    reportPage.verifyItemsOnReportUploadScreen();

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
    public void verifyDefaultReports(String[] credentials) throws Exception {

        LoginPage loginPage = new LoginPage(testWebDriver, baseUrlGlobal);

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

  private String getCurrentDateAndTime() {
    Date dObj = new Date();
    SimpleDateFormat formatter_date_time = new SimpleDateFormat(
      "yyyyMMdd-hhmmss");
    return formatter_date_time.format(dObj);
  }


  @AfterMethod(groups = {"admin"})
  public void tearDown() throws Exception {
    HomePage homePage = new HomePage(testWebDriver);
    homePage.logout(baseUrlGlobal);
    dbWrapper.deleteData();
    dbWrapper.deleteReport(reportName);
    dbWrapper.closeConnection();
  }

  @DataProvider(name = "Data-Provider-Function-Positive")
  public Object[][] parameterIntTestProviderPositive() {
    return new Object[][]{
      {new String[]{"Admin123", "Admin123"}}
    };
  }
}
