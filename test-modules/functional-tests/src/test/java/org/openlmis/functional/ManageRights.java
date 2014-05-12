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
import org.openlmis.pageobjects.PageObjectFactory;
import org.testng.annotations.*;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

import static java.util.Arrays.asList;

@Listeners(CaptureScreenshotOnFailureListener.class)

public class ManageRights extends TestCaseHelper {

  @BeforeMethod(groups = {"admin"})
  public void setUp() throws InterruptedException, SQLException, IOException {
    super.setup();
  }

  @Test(groups = {"admin"}, dataProvider = "Data-Provider-Function-Positive")
  public void testOnlyCreateRight(String program, String userSIC, String password) throws SQLException {
    List<String> rightsList = asList("CREATE_REQUISITION", "VIEW_REQUISITION");
    setupTestDataToInitiateRnR(true, program, userSIC, rightsList);

    String[] expectedMenuItem = {"Create / Authorize", "View"};
    HomePage homePage = PageObjectFactory.getLoginPage(testWebDriver, baseUrlGlobal).loginAs(userSIC, password);

    homePage.clickRequisitionSubMenuItem();
    homePage.verifySubMenuItems(expectedMenuItem);
    homePage.navigateAndInitiateRnr(program);
    homePage.clickProceed();

    InitiateRnRPage initiateRnRPage = PageObjectFactory.getInitiateRnRPage(testWebDriver);
    initiateRnRPage.enterValueIfNotNull(10, "beginningBalanceFirstProduct");
    initiateRnRPage.enterValueIfNotNull(10, "quantityDispensedFirstProduct");
    initiateRnRPage.enterValueIfNotNull(10, "quantityReceivedFirstProduct");
    initiateRnRPage.submitRnR();
    initiateRnRPage.verifyAuthorizeButtonNotPresent();

    initiateRnRPage.verifyBeginningBalanceForFirstProduct(10);
    initiateRnRPage.verifyQuantityReceivedForFirstProduct(10);
    initiateRnRPage.verifyQuantityDispensedForFirstProduct(10);
  }

  @AfterMethod(groups = {"admin"})
  public void tearDown() throws SQLException {
    HomePage homePage = PageObjectFactory.getHomePage(testWebDriver);
    homePage.logout(baseUrlGlobal);
    dbWrapper.deleteData();
    dbWrapper.closeConnection();
  }

  @DataProvider(name = "Data-Provider-Function-Positive")
  public Object[][] parameterIntTestProviderPositive() {
    return new Object[][]{
      {"HIV", "storeInCharge", "Admin123"}
    };
  }
}

