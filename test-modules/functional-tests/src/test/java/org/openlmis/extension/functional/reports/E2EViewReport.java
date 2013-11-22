/*
 * Copyright © 2013 VillageReach.  All Rights Reserved.  This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 *
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package org.openlmis.extension.functional.reports;


import org.openlmis.UiUtils.CaptureScreenshotOnFailureListener;
import org.openlmis.UiUtils.TestCaseHelper;
import org.openlmis.extension.pageobjects.*;
import org.openlmis.pageobjects.*;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;
import org.testng.annotations.*;

import java.text.SimpleDateFormat;
import java.util.Date;

@TransactionConfiguration(defaultRollback = true)
@Transactional

@Listeners(CaptureScreenshotOnFailureListener.class)

public class E2EViewReport extends TestCaseHelper {

  public static final String STORE_IN_CHARGE = "store in-charge";
  public static final String APPROVE_REQUISITION = "APPROVE_REQUISITION";
  public static final String CONVERT_TO_ORDER = "CONVERT_TO_ORDER";
  public static final String SUBMITTED = "SUBMITTED";
  public static final String AUTHORIZED = "AUTHORIZED";
  public static final String IN_APPROVAL = "IN_APPROVAL";
  public static final String APPROVED = "APPROVED";
  public static final String RELEASED = "RELEASED";

  @BeforeMethod(groups = {"report"})
  public void setUp() throws Exception {
    super.setup();
  }

 @Test(groups = {"report"}, dataProvider = "Data-Provider-Function-Positive")
  public void verifyFacilityMailingListReport(String[] credentials) throws Exception {

      String geoZone = "Ngorongoro";
      String facilityType = "Lvl3 Hospital";
      String facilityCodePrefix = "FCcode";
      String facilityNamePrefix = "FCname";

      Date dObj = new Date();
      SimpleDateFormat formatter_date_time = new SimpleDateFormat(
              "yyyyMMdd-hhmmss");
      String date_time = formatter_date_time.format(dObj);

      dbWrapper.insertFacilities(facilityNamePrefix + date_time, facilityCodePrefix + date_time);

      ReportLoginPage loginPage = new ReportLoginPage(testWebDriver, baseUrlGlobal);

      ExtensionHomePage homePage = loginPage.loginAs(credentials[0], credentials[1]);

      FacilityMailingListReportPage facilityMailingListReportPage = homePage.navigateViewFacilityMailingListReport();
      facilityMailingListReportPage.enterFilterValuesInFacilityMailingListReport(facilityNamePrefix + date_time, facilityCodePrefix + date_time, facilityType);
      facilityMailingListReportPage.verifyHTMLReportOutputOnFacilityMailingListScreen();
      facilityMailingListReportPage.verifyPdfReportOutputOnFacilityMailingListScreen();
      testWebDriver.sleep(500);
      //homePage.goBack();
      facilityMailingListReportPage.verifyMailingReportOutputOnFacilityMailingListScreen();
      //homePage.goBack();
      facilityMailingListReportPage.verifyXlsReportOutputOnFacilityMailingListScreen();
      //homePage.goBack();

 }


   @Test(groups = {"report"}, dataProvider = "Data-Provider-Function-Positive")
   public void verifyFacilityListingReport(String[] credentials) throws Exception{

       String geoZone = "Ngorongoro";
       String facilityType = "Lvl3 Hospital";
       String facilityCodePrefix = "FCcode";
       String facilityNamePrefix = "FCname";
       String status = "true";

       Date dObj = new Date();
       SimpleDateFormat formatter_date_time = new SimpleDateFormat(
               "yyyyMMdd-hhmmss");
       String date_time = formatter_date_time.format(dObj);

       dbWrapper.insertFacilities(facilityNamePrefix + date_time, facilityCodePrefix + date_time);

       ReportLoginPage loginPage = new ReportLoginPage(testWebDriver, baseUrlGlobal);

       ExtensionHomePage homePage = loginPage.loginAs(credentials[0], credentials[1]);

       FacilityListingReportPage facilityListingReportPage = homePage.navigateViewFacilityListingReport();
       facilityListingReportPage.enterFilterValuesInFacilityListingReport(geoZone, facilityType, status);
       facilityListingReportPage.verifyHTMLReportOutputOnFacilityListingScreen();
   }


  @Test(groups = {"report"}, dataProvider = "Data-Provider-Function-Positive" )
  public void verifySummaryReport(String[] credentials) throws Exception{
      ReportLoginPage loginPage = new ReportLoginPage(testWebDriver, baseUrlGlobal);
      ExtensionHomePage homePage = loginPage.loginAs(credentials[0], credentials[1]);
      SummaryReportPage summaryReportPage = homePage.navigateViewSummaryReport();
      summaryReportPage.enterFilterValuesInSummaryReport("Period2");
      summaryReportPage.verifyHTMLReportOutputOnSummaryReportScreen();
      summaryReportPage.verifyPdfReportOutputOnSummaryReportScreen();
      testWebDriver.sleep(500);

  }

    @Test(groups = {"report"}, dataProvider = "Data-Provider-Function-Positive")
    public void verifyNonReportingFacilityReport(String[] credentials) throws Exception{

        ReportLoginPage loginPage = new ReportLoginPage(testWebDriver, baseUrlGlobal);
        ExtensionHomePage homePage = loginPage.loginAs(credentials[0], credentials[1]);
        NonReportingFacilityReportPage nonReportingFacilityReportPage = homePage.navigateViewNonReportingFacilityReport();
        nonReportingFacilityReportPage.enterFilterValues("HIV","Monthly" , "Period2", "Requistion Group 2", "Lvl3 Hospital");
        nonReportingFacilityReportPage.verifyHTMLReportOutput();
        nonReportingFacilityReportPage.verifyPdfReportOutput();
        testWebDriver.sleep(500);
        //homePage.goBack();
    }

    @Test(groups = {"report"}, dataProvider = "Data-Provider-Function-Positive")
    public void verifyAverageConsumptionReport(String[] credentials) throws Exception{

        ReportLoginPage loginPage = new ReportLoginPage(testWebDriver, baseUrlGlobal);
        ExtensionHomePage homePage = loginPage.loginAs(credentials[0], credentials[1]);
        AverageConsumptionReportPage averageConsumptionReportPage = homePage.navigateViewAverageConsumptionReport();
        averageConsumptionReportPage.enterFilterValues("Monthly", "2013","Jan","2013","May","Root","Antibiotics","Lvl3 Hospital","Requistion Group 2","TDF/FTC/EFV","HIV");
        averageConsumptionReportPage.verifyHTMLReportOutput();
        averageConsumptionReportPage.verifyPdfReportOutput();
        testWebDriver.sleep(500);
        //homePage.goBack();
    }

    @Test(groups = {"report"}, dataProvider = "Data-Provider-Function-Positive")
    public void verifyAdjustmentSummaryReport(String[] credentials) throws Exception{

        ReportLoginPage loginPage = new ReportLoginPage(testWebDriver, baseUrlGlobal);
        ExtensionHomePage homePage = loginPage.loginAs(credentials[0], credentials[1]);
        AdjustmentSummaryReportPage adjustmentSummaryReportPage = homePage.navigateViewAdjustmentSummaryReport();
        adjustmentSummaryReportPage.enterFilterValues("Monthly", "2013","Jan","2013","May","Root","Antibiotics","Lvl3 Hospital","Requistion Group 2","TDF/FTC/EFV","HIV", "Damaged");
        adjustmentSummaryReportPage.verifyHTMLReportOutput();
        adjustmentSummaryReportPage.verifyPdfReportOutput();
        testWebDriver.sleep(500);
        //homePage.goBack();
    }

    @Test(groups = {"report"}, dataProvider = "Data-Provider-Function-Positive")
    public void verifyStockedOutReport(String[] credentials) throws Exception{

        ReportLoginPage loginPage = new ReportLoginPage(testWebDriver, baseUrlGlobal);
        ExtensionHomePage homePage = loginPage.loginAs(credentials[0], credentials[1]);
        StockedOutReportPage stockedOutReportPage = homePage.navigateViewStockedOutReport();
        stockedOutReportPage.enterFilterValues("HIV","TDF/FTC/EFV","Monthly","Requistion Group 2","Lvl3 Hospital");
        stockedOutReportPage.verifyHTMLReportOutput();
        stockedOutReportPage.verifyPdfReportOutput();
        testWebDriver.sleep(500);
        //homePage.goBack();
    }


  @AfterMethod(groups = {"report"})
  public void tearDown() throws Exception {
    ExtensionHomePage homePage = new ExtensionHomePage(testWebDriver);
    homePage.logout(baseUrlGlobal);
    //dbWrapper.deleteData();
    dbWrapper.closeConnection();
  }

  @DataProvider(name = "Data-Provider-Function-Positive")
  public Object[][] parameterIntTestProviderPositive() {
    return new Object[][]{
            {new String[]{"Admin123", "Admin123", "storeincharge", "Admin123"}}
    };
  }

}
