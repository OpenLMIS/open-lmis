/*
 * Copyright © 2013 VillageReach.  All Rights Reserved.  This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 *
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package org.openlmis.functional;


import org.openlmis.UiUtils.CaptureScreenshotOnFailureListener;
import org.openlmis.UiUtils.TestCaseHelper;
import org.openlmis.pageobjects.HomePage;
import org.openlmis.pageobjects.InitiateRnRPage;
import org.openlmis.pageobjects.LoginPage;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;
import org.testng.annotations.*;

import java.util.ArrayList;
import java.util.List;


@TransactionConfiguration(defaultRollback = true)
@Transactional

@Listeners(CaptureScreenshotOnFailureListener.class)

public class ManageRights extends TestCaseHelper {

  @BeforeMethod(groups = {"functional"})
  public void setUp() throws Exception {
    super.setup();
  }


  @Test(groups = {"functional"}, dataProvider = "Data-Provider-Function-Positive")
  public void testOnlyCreateRight(String program, String userSIC, String password) throws Exception {
    List<String> rightsList = new ArrayList<String>();
    rightsList.add("CREATE_REQUISITION");
    rightsList.add("VIEW_REQUISITION");
    setupTestDataToInitiateRnR(program, userSIC, "200", "openLmis", rightsList);

    LoginPage loginPage = new LoginPage(testWebDriver, baseUrlGlobal);
    HomePage homePage = loginPage.loginAs(userSIC, password);
    String[] expectedMenuItem = {"Create / Authorize", "View"};
    homePage.verifySubMenuItems(expectedMenuItem);
    String periodDetails = homePage.navigateAndInitiateRnr(program);
    InitiateRnRPage initiateRnRPage = homePage.clickProceed();

    initiateRnRPage.enterBeginningBalance("10");
    initiateRnRPage.enterQuantityDispensed("10");
    initiateRnRPage.enterQuantityReceived("10");
    initiateRnRPage.submitRnR();
    initiateRnRPage.verifyAuthorizeButtonNotPresent();
  }

  @Test(groups = {"functional"}, dataProvider = "Data-Provider-Function-Positive")
  public void testUserTryToAuthorizeUnSubmittedRnR(String program, String userSIC, String password) throws Exception {
    List<String> rightsList = new ArrayList<String>();
    rightsList.add("CREATE_REQUISITION");
    rightsList.add("VIEW_REQUISITION");
    setupTestDataToInitiateRnR(program, userSIC, "200", "openLmis", rightsList);

    dbWrapper.updateRoleRight("CREATE_REQUISITION", "AUTHORIZE_REQUISITION");
    LoginPage loginPage = new LoginPage(testWebDriver, baseUrlGlobal);
    HomePage homePage = loginPage.loginAs(userSIC, password);
    String[] expectedMenuItem = {"Create / Authorize", "View"};
    homePage.verifySubMenuItems(expectedMenuItem);
    homePage.navigateAndInitiateRnr(program);
    homePage.clickProceed();
    homePage.verifyErrorMessage();

    dbWrapper.insertValuesInRequisition();
    dbWrapper.updateRequisitionStatus("INITIATED");

    homePage.navigateAndInitiateRnr(program);
    homePage.clickProceed();
    homePage.verifyErrorMessage();
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

