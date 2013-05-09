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
import org.testng.annotations.*;

@TransactionConfiguration(defaultRollback = true)
@Transactional

@Listeners(CaptureScreenshotOnFailureListener.class)

public class ManageReport extends TestCaseHelper {

  @BeforeMethod(groups = {"functional2"})
  public void setUp() throws Exception {
    super.setup();
  }

  @Test(groups = {"functional2"}, dataProvider = "Data-Provider-Function-Positive")
  public void invalidScenariosReports(String[] credentials) throws Exception {

    LoginPage loginPage = new LoginPage(testWebDriver, baseUrlGlobal);

    HomePage homePage = loginPage.loginAs(credentials[0], credentials[1]);
    ReportPage reportPage = homePage.navigateReportScreen();
    reportPage.verifyNoReportsMessage();
    reportPage.clickAddNewButton();
    reportPage.verifyItemsOnReportUploadScreen();

    String reportName="Test-Report"+reportPage.getCurrentDateAndTime();
    String fileName="activefacility.jrxml";

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

  @Test(groups = {"functional2"}, dataProvider = "Data-Provider-Function-Positive")
  public void uploadManageReport(String[] credentials) throws Exception {

    LoginPage loginPage = new LoginPage(testWebDriver, baseUrlGlobal);

    HomePage homePage = loginPage.loginAs(credentials[0], credentials[1]);
    ReportPage reportPage = homePage.navigateReportScreen();
    reportPage.verifyNoReportsMessage();
    reportPage.clickAddNewButton();
    reportPage.verifyItemsOnReportUploadScreen();

    String reportName="Test-Report"+reportPage.getCurrentDateAndTime();
    String fileName="activefacility.jrxml";

    reportPage.enterReportName(reportName);
    reportPage.uploadFile(fileName);
    reportPage.clickSaveButton();
    reportPage.verifySuccessMessageDiv();
    reportPage.verifyReportNameInList(reportName,1);
    reportPage.verifyItemsOnReportListScreen();

  }


  @AfterMethod(groups = {"functional2"})
  public void tearDown() throws Exception {
    HomePage homePage = new HomePage(testWebDriver);
    homePage.logout(baseUrlGlobal);
    dbWrapper.deleteData();
    dbWrapper.closeConnection();
  }

  @DataProvider(name = "Data-Provider-Function-Positive")
  public Object[][] parameterIntTestProviderPositive() {
    return new Object[][]{
      {new String[]{"Admin123", "Admin123"}}
    };
  }
}
