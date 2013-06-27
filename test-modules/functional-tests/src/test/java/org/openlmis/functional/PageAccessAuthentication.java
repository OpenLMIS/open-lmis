/*
 * Copyright © 2013 VillageReach.  All Rights Reserved.  This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 *
 *  If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package org.openlmis.functional;


import org.openlmis.UiUtils.CaptureScreenshotOnFailureListener;
import org.openlmis.UiUtils.TestCaseHelper;
import org.openlmis.pageobjects.*;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;
import org.testng.annotations.*;

import java.util.ArrayList;
import java.util.List;

import static com.thoughtworks.selenium.SeleneseTestBase.assertEquals;
import static com.thoughtworks.selenium.SeleneseTestBase.assertFalse;
import static java.lang.String.valueOf;
import static org.openlmis.pageobjects.CreateFacilityPage.SaveButton;


@TransactionConfiguration(defaultRollback = true)
@Transactional

@Listeners(CaptureScreenshotOnFailureListener.class)

public class PageAccessAuthentication extends TestCaseHelper {

  @BeforeMethod(groups = {"smoke"})
  public void setUp() throws Exception {
    super.setup();
    }


  @Test(groups = {"smoke"}, dataProvider = "Data-Provider-Function-AdminUser")
  public void shouldNotAccessRequisitionPageByAdminUser(String userSIC, String password) throws Exception {
      LoginPage loginPage = new LoginPage(testWebDriver, baseUrlGlobal);
      loginPage.loginAs(userSIC, password);
      testWebDriver.getUrl(baseUrlGlobal + "public/pages/logistics/rnr/index.html#/init-rnr");
      assertEquals("You are not authorized to view the requested page.", new AccessDeniedPage(testWebDriver).getAccessDeniedText()) ;
  }

    @Test(groups = {"smoke"}, dataProvider = "Data-Provider-Function-StoreInchargeUserUser")
    public void shouldNotAccessAdminPageByRequisitionUser(String userSIC, String password) throws Exception {
        List<String> rightsList = new ArrayList<String>();
        rightsList.add("CREATE_REQUISITION");
        rightsList.add("VIEW_REQUISITION");
        setupTestUserRoleRightsData("200", userSIC, "openLmis", rightsList);

        LoginPage loginPage = new LoginPage(testWebDriver, baseUrlGlobal);
        loginPage.loginAs(userSIC, password);
        testWebDriver.getUrl(baseUrlGlobal + "public/pages/admin/facility/index.html#/create-facility");
        assertEquals("You are not authorized to view the requested page.", new AccessDeniedPage(testWebDriver).getAccessDeniedText()) ;
    }

  @AfterMethod(groups = {"smoke"})
  public void tearDown() throws Exception {
    HomePage homePage = new HomePage(testWebDriver);
    homePage.logout(baseUrlGlobal);
    dbWrapper.closeConnection();
  }


  @DataProvider(name = "Data-Provider-Function-AdminUser")
  public Object[][] parameterIntTestProviderAdminUser() {
    return new Object[][]{
      {"Admin123", "Admin123"}
    };
  }
   @DataProvider(name = "Data-Provider-Function-StoreInchargeUserUser")
   public Object[][] parameterIntTestProviderStoreInchargeUserUser() {
          return new Object[][]{
                  {"storeincharge", "Admin123"}
          };
  }

}

