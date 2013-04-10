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

public class ViewRequisition extends TestCaseHelper {

  @BeforeMethod(groups = {"functional"})
  public void setUp() throws Exception {
    super.setup();
  }


  @Test(groups = {"functional"}, dataProvider = "Data-Provider-Function-Positive")
  public void testViewRequisition(String program, String userSIC, String password) throws Exception {

    setupTestDataToInitiateRnR(program, userSIC);
    dbWrapper.assignRight("store in-charge", "APPROVE_REQUISITION");
    dbWrapper.assignRight("store in-charge", "CONVERT_TO_ORDER");

    LoginPage loginPage = new LoginPage(testWebDriver, baseUrlGlobal);
    HomePage homePage = loginPage.loginAs(userSIC, password);
    homePage.navigateAndInitiateRnr(program);
    InitiateRnRPage initiateRnRPage = homePage.clickProceed();
    HomePage homePage1 = initiateRnRPage.clickHome();

    ViewRequisitionPage viewRequisitionPage = homePage1.navigateViewRequisition();
    viewRequisitionPage.verifyElementsOnViewRequisitionScreen();
    dbWrapper.insertValuesInRequisition();
    dbWrapper.updateRequisitionStatus("SUBMITTED");
    viewRequisitionPage.enterViewSearchCriteria();
    viewRequisitionPage.clickSearch();
    viewRequisitionPage.verifyNoRequisitionFound();
    dbWrapper.updateRequisitionStatus("AUTHORIZED");
    viewRequisitionPage.clickSearch();
    viewRequisitionPage.clickRnRList();

    HomePage homePageAuthorized = viewRequisitionPage.verifyFieldsPreApproval("12.50", "1");
    ViewRequisitionPage viewRequisitionPageAuthorized = homePageAuthorized.navigateViewRequisition();
    viewRequisitionPageAuthorized.enterViewSearchCriteria();
    viewRequisitionPageAuthorized.clickSearch();
    viewRequisitionPageAuthorized.verifyStatus("AUTHORIZED");
    viewRequisitionPageAuthorized.clickRnRList();

    HomePage homePageInApproval = viewRequisitionPageAuthorized.verifyFieldsPreApproval("12.50", "1");
    dbWrapper.updateRequisitionStatus("IN_APPROVAL");
    ViewRequisitionPage viewRequisitionPageInApproval = homePageInApproval.navigateViewRequisition();
    viewRequisitionPageInApproval.enterViewSearchCriteria();
    viewRequisitionPageInApproval.clickSearch();
    viewRequisitionPageInApproval.verifyStatus("IN_APPROVAL");

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
    viewRequisitionPageApproved.verifyStatus("APPROVED");
    viewRequisitionPageApproved.clickRnRList();
    viewRequisitionPageApproved.verifyComment("Dummy Comments", userSIC,1);
    viewRequisitionPageApproved.verifyCommentBoxNotPresent();

    HomePage homePageApproved = viewRequisitionPageApproved.verifyFieldsPostApproval("25.00", "1");

    dbWrapper.updateRequisition("F10");
    OrderPage orderPage = homePageApproved.navigateConvertToOrder();
    orderPage.convertToOrder();
    ViewRequisitionPage viewRequisitionPageOrdered = homePageApproved.navigateViewRequisition();
    viewRequisitionPageOrdered.enterViewSearchCriteria();
    viewRequisitionPageOrdered.clickSearch();
    viewRequisitionPageOrdered.verifyStatus("ORDERED");
    viewRequisitionPageOrdered.clickRnRList();
    viewRequisitionPageOrdered.verifyFieldsPostApproval("25.00", "1");
    viewRequisitionPageOrdered.verifyApprovedQuantityFieldPresent();
  }

  @AfterMethod(groups = {"functional"})
  public void tearDown() throws Exception {
    HomePage homePage = new HomePage(testWebDriver);
    homePage.logout(baseUrlGlobal);
    dbWrapper.deleteData();
    dbWrapper.closeConnection();
  }


  @DataProvider(name = "Data-Provider-Function-Positive")
  public Object[][] parameterIntTestProviderPositive() {
    return new Object[][]{
      {"HIV", "storeincharge", "Admin123"}
    };

  }
}

