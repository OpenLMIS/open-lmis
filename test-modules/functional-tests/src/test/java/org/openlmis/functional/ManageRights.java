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

  @BeforeMethod(groups = {"admin"})
  public void setUp() throws Exception {
    super.setup();
  }


  @Test(groups = {"admin"}, dataProvider = "Data-Provider-Function-Positive")
  public void testOnlyCreateRight(String program, String userSIC, String password) throws Exception {
    List<String> rightsList = new ArrayList<String>();
    rightsList.add("CREATE_REQUISITION");
    rightsList.add("VIEW_REQUISITION");
    setupTestDataToInitiateRnR(true, program, userSIC, "200", "openLmis", rightsList);

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


  @AfterMethod(groups = {"admin"})
  public void tearDown() throws Exception {
    HomePage homePage = new HomePage(testWebDriver);
    homePage.logout(baseUrlGlobal);
    dbWrapper.deleteData();
    dbWrapper.closeConnection();
  }


  @DataProvider(name = "Data-Provider-Function-Positive")
  public Object[][] parameterIntTestProviderPositive() {
    return new Object[][]{
      {"HIV", "storeIncharge", "Admin123"}
    };

  }
}

