/*
 * Copyright © 2013 VillageReach.  All Rights Reserved.  This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 *
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package org.openlmis.functional;


import com.thoughtworks.selenium.SeleneseTestNgHelper;
import org.openlmis.UiUtils.CaptureScreenshotOnFailureListener;
import org.openlmis.UiUtils.TestCaseHelper;
import org.openlmis.pageobjects.*;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;
import org.testng.annotations.*;

import java.io.IOException;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@TransactionConfiguration(defaultRollback = true)
@Transactional

@Listeners(CaptureScreenshotOnFailureListener.class)

public class FacilityMailingListingReport extends TestCaseHelper {

  public static final String STORE_IN_CHARGE = "store in-charge";
  public static final String APPROVE_REQUISITION = "APPROVE_REQUISITION";
  public static final String CONVERT_TO_ORDER = "CONVERT_TO_ORDER";
  public static final String SUBMITTED = "SUBMITTED";
  public static final String AUTHORIZED = "AUTHORIZED";
  public static final String IN_APPROVAL = "IN_APPROVAL";
  public static final String APPROVED = "APPROVED";
  public static final String RELEASED = "RELEASED";

  private HomePage homePage;
  private LoginPage loginPage;
  private FacilityMailingListReportPage facilityMailingListReportPage;

  @BeforeMethod(groups = {"functional"})
  public void setUp() throws Exception {
    super.setup();
  }

    private void login(String userName, String passWord) throws IOException {
        loginPage = new LoginPage(testWebDriver, baseUrlGlobal);
        homePage = loginPage.loginAs(userName, passWord);
    }

    private void navigateToFacilityMailingListReportingPage(String userName, String passWord) throws IOException {
        login(userName , passWord);
        facilityMailingListReportPage = homePage.navigateViewFacilityMailingListReport();
    }

 //   @Test(groups = {"functional"}, dataProvider = "Data-Provider-Function-Positive")
    public void verifyReportMenu(String [] credentials) throws IOException{
        // Assign rights here
       // List<String> rightsList = new ArrayList<String>();
        //rightsList.add("VIEW_REPORT");
        //setUpRoleRightstoUser(String "5", String userSIC, String vendorName, List<String> rightsList, String roleName , String roleType)

       /* LoginPage loginPage = new LoginPage(testWebDriver, baseUrlGlobal);
        HomePage homePage = loginPage.loginAs(credentials[0], credentials[1]);*/
        login(credentials[0],credentials[1]);
        SeleneseTestNgHelper.assertTrue(homePage.reportMenuIsDisplayed());
        homePage.logout(DEFAULT_BASE_URL);
    }
  //  @Test(groups = {"functional"}, dataProvider = "Data-Provider-Function-Positive")
    public void verifyReportMenuHiddenForUnauthorizedUser(String [] credentials) throws IOException{
        // Assign rights here
        //List<String> rightsList = new ArrayList<String>();
        //rightsList.add("VIEW_REPORT");
        //setUpRoleRightstoUser(String "5", String userSIC, String vendorName, List<String> rightsList, String roleName , String roleType)

/*        LoginPage loginPage = new LoginPage(testWebDriver, baseUrlGlobal);
        HomePage homePage = loginPage.loginAs(credentials[2], credentials[3]);*/
        login(credentials[2],credentials[3]);
        SeleneseTestNgHelper.assertFalse(homePage.reportMenuIsDisplayed());
        homePage.logout(DEFAULT_BASE_URL);
    }

  //  @Test(groups = {"functional"}, dataProvider = "Data-Provider-Function-Positive")
    public void verifyReportFiltersRendered(String [] credentials) throws Exception{
        navigateToFacilityMailingListReportingPage(credentials[0],credentials[1]);

        SeleneseTestNgHelper.assertTrue(facilityMailingListReportPage.facilityCodeIsDisplayed());
        SeleneseTestNgHelper.assertTrue(facilityMailingListReportPage.facilityNameIsDisplayed());
        SeleneseTestNgHelper.assertTrue(facilityMailingListReportPage.facilityTypeIsDisplayed());
    }

 //   @Test(groups = {"functional"}, dataProvider = "Data-Provider-Function-Positive")
    public void verifyPDFOUtput (String [] credentials) throws Exception {
           navigateToFacilityMailingListReportingPage(credentials[0],credentials[1]);
           facilityMailingListReportPage.verifyPdfReportOutput();
    }


 //   @Test(groups = {"functional"}, dataProvider = "Data-Provider-Function-Positive")
    public void verifyXLSOUtput (String [] credentials) throws Exception {
        navigateToFacilityMailingListReportingPage(credentials[0],credentials[1]);
         facilityMailingListReportPage.verifyXlsReportOutput();
    }
    //@Test(groups = {"functional"}, dataProvider = "Data-Provider-Function-Positive")
    public void verifySorting(String [] credentials) throws IOException {
        navigateToFacilityMailingListReportingPage(credentials[0],credentials[1]);
          facilityMailingListReportPage.verifySortAscByCode();
    }


    @Test(groups = {"functional"}, dataProvider = "Data-Provider-Function-Positive")
    public void verifyPagination(String [] credentials) throws Exception {
        navigateToFacilityMailingListReportingPage(credentials[0],credentials[1]);
        facilityMailingListReportPage.verifyPagination();
    }

  //  @Test(groups = {"functional"}, dataProvider = "Data-Provider-Function-Positive")
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

       LoginPage loginPage = new LoginPage(testWebDriver, baseUrlGlobal);

       HomePage homePage = loginPage.loginAs(credentials[0], credentials[1]);

       FacilityMailingListReportPage facilityListingReportPage = homePage.navigateViewFacilityMailingListReport();
       //facilityListingReportPage.enterFilterValuesInFacilityListingReport(geoZone, facilityType, status);
       //facilityListingReportPage.verifyHTMLReportOutputOnFacilityListingScreen();
   }



    private void setupRnRData(String[] credentials) throws IOException, SQLException {
        List<String> rightsList = new ArrayList<String>();
        rightsList.add("CREATE_REQUISITION");
        rightsList.add("VIEW_REQUISITION");
        setupTestDataToInitiateRnR(true, "HIV", credentials[2], "200", "openLmis", rightsList);
        dbWrapper.assignRight(STORE_IN_CHARGE, APPROVE_REQUISITION);
        dbWrapper.assignRight(STORE_IN_CHARGE, CONVERT_TO_ORDER);
        LoginPage loginPage = new LoginPage(testWebDriver, baseUrlGlobal);
        HomePage homePage = loginPage.loginAs(credentials[2], credentials[3]);
        homePage.navigateAndInitiateRnr("HIV");
        InitiateRnRPage initiateRnRPage = homePage.clickProceed();
        HomePage homePage1 = initiateRnRPage.clickHome();

        ViewRequisitionPage viewRequisitionPage = homePage1.navigateViewRequisition();
        viewRequisitionPage.verifyElementsOnViewRequisitionScreen();
        dbWrapper.insertValuesInRequisition();
        dbWrapper.updateRequisitionStatus(SUBMITTED);
        viewRequisitionPage.enterViewSearchCriteria();
        viewRequisitionPage.clickSearch();
        viewRequisitionPage.verifyNoRequisitionFound();
        dbWrapper.updateRequisitionStatus(AUTHORIZED);
        viewRequisitionPage.clickSearch();
        viewRequisitionPage.clickRnRList();

        HomePage homePageAuthorized = viewRequisitionPage.verifyFieldsPreApproval("12.50", "1");
        ViewRequisitionPage viewRequisitionPageAuthorized = homePageAuthorized.navigateViewRequisition();
        viewRequisitionPageAuthorized.enterViewSearchCriteria();
        viewRequisitionPageAuthorized.clickSearch();
        viewRequisitionPageAuthorized.verifyStatus(AUTHORIZED);
        viewRequisitionPageAuthorized.clickRnRList();

        HomePage homePageInApproval = viewRequisitionPageAuthorized.verifyFieldsPreApproval("12.50", "1");
        dbWrapper.updateRequisitionStatus(IN_APPROVAL);
        ViewRequisitionPage viewRequisitionPageInApproval = homePageInApproval.navigateViewRequisition();
        viewRequisitionPageInApproval.enterViewSearchCriteria();
        viewRequisitionPageInApproval.clickSearch();
        viewRequisitionPageInApproval.verifyStatus(IN_APPROVAL);

        ApprovePage approvePageTopSNUser = homePageInApproval.navigateToApprove();
        approvePageTopSNUser.verifyAndClickRequisitionPresentForApproval();
        approvePageTopSNUser.editApproveQuantityAndVerifyTotalCostViewRequisition("20");
        approvePageTopSNUser.addComments("Dummy Comments");
        approvePageTopSNUser.approveRequisition();
        approvePageTopSNUser.clickOk();
        approvePageTopSNUser.verifyNoRequisitionPendingMessage();
        ViewRequisitionPage viewRequisitionPageApproved = homePageInApproval.navigateViewRequisition();
        viewRequisitionPageApproved.enterViewSearchCriteria();
        viewRequisitionPageApproved.clickSearch();
        viewRequisitionPageApproved.verifyStatus(APPROVED);
        viewRequisitionPageApproved.clickRnRList();
        viewRequisitionPageApproved.verifyComment("Dummy Comments", "storeincharge", 1);
        viewRequisitionPageApproved.verifyCommentBoxNotPresent();

        HomePage homePageApproved = viewRequisitionPageApproved.verifyFieldsPostApproval("25.00", "1");

       // dbWrapper.updateRequisition("F10");
        ConvertOrderPage convertOrderPage = homePageApproved.navigateConvertToOrder();
        convertOrderPage.convertToOrder();
        ViewRequisitionPage viewRequisitionPageOrdered = homePageApproved.navigateViewRequisition();
        viewRequisitionPageOrdered.enterViewSearchCriteria();
        viewRequisitionPageOrdered.clickSearch();
        viewRequisitionPageOrdered.verifyStatus(RELEASED);
        viewRequisitionPageOrdered.clickRnRList();
        viewRequisitionPageOrdered.verifyFieldsPostApproval("25.00", "1");
        viewRequisitionPageOrdered.verifyApprovedQuantityFieldPresent();

        homePage = new HomePage(testWebDriver);
        homePage.logout(baseUrlGlobal);

    }


  @AfterMethod(groups = {"functional"})
  public void tearDown() throws Exception {
    HomePage homePage = new HomePage(testWebDriver);
    homePage.logout(baseUrlGlobal);
    //dbWrapper.deleteData();
    dbWrapper.closeConnection();
  }

  @DataProvider(name = "Data-Provider-Function-Positive")
  public Object[][] parameterIntTestProviderPositive() {
    return new Object[][]{
            {new String[]{"nidris", "Admin123", "storeincharge", "Admin123"}}
    };
  }

}
