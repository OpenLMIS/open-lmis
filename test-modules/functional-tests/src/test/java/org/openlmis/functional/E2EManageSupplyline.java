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

import java.util.ArrayList;

@TransactionConfiguration(defaultRollback = true)
@Transactional

@Listeners(CaptureScreenshotOnFailureListener.class)

public class E2EManageSupplyline extends TestCaseHelper {


  @BeforeMethod(groups = {"functional2"})
  public void setUp() throws Exception {
    super.setup();
  }

  @Test(groups = {"functional2"}, dataProvider = "Data-Provider-Function-Positive")
  public void testE2EManageSupplyline(String user, String program, String[] credentials) throws Exception {
    int c = 0;
    LoginPage loginPage = new LoginPage(testWebDriver, baseUrlGlobal);
    HomePage homePage = loginPage.loginAs(credentials[0], credentials[1]);
    ManageSupplylinePage manageSupplylinePage = homePage.navigateToSupplyline();
    manageSupplylinePage.createAndVerifySupplyline(1,"1", "1", "1","Test Description 01");
    manageSupplylinePage.createAndVerifySupplyline(2,"2", "1", "1","Test Description 02");
    manageSupplylinePage.editAndVerifySupplyline(1,"1", "1", "1","Test Description 01");
    manageSupplylinePage.editAndVerifySupplyline(2,"2", "1", "1","Test Description 02");
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
      {"User123", "HIV", new String[]{"Admin123", "Admin123"}}
    };
  }
}
