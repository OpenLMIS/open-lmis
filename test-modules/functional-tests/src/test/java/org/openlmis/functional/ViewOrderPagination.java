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
import org.openlmis.pageobjects.LoginPage;
import org.testng.annotations.*;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

import static java.util.Arrays.asList;

@Listeners(CaptureScreenshotOnFailureListener.class)

public class ViewOrderPagination extends TestCaseHelper {

  @BeforeMethod(groups = "requisition")
  public void setUp() throws InterruptedException, SQLException, IOException {
    super.setup();
  }

  @Test(groups = {"requisition"}, dataProvider = "Data-Provider-Function-Positive")
  public void verifyPagination(String program, String userSIC, String password) throws SQLException {
    setUpData(program, userSIC);
    dbWrapper.insertRequisitions(50, "MALARIA", true, "2012-12-01", "2015-12-01", "F10", false);
    dbWrapper.insertRequisitions(1, "TB", true, "2012-12-01", "2015-12-01", "F10", false);
    dbWrapper.updateRequisitionStatus("SUBMITTED", userSIC, "MALARIA");
    dbWrapper.updateRequisitionStatus("SUBMITTED", userSIC, "TB");
    dbWrapper.updateRequisitionStatus("APPROVED", userSIC, "MALARIA");
    dbWrapper.updateRequisitionStatus("APPROVED", userSIC, "TB");
    dbWrapper.updateFieldValue("requisition_line_items", "quantityApproved", 10);
    dbWrapper.updatePacksToShip("1");
    dbWrapper.insertFulfilmentRoleAssignment(userSIC, "store in-charge", "F10");
    dbWrapper.insertOrders("RELEASED", userSIC, "MALARIA");
    dbWrapper.insertOrders("RELEASED", userSIC, "TB");

    LoginPage loginPage = PageFactory.getLoginPage(testWebDriver, baseUrlGlobal);
    HomePage homePage = loginPage.loginAs(userSIC, password);
    homePage.navigateViewOrders();
    verifyNumberOfPageLinks(51, 50);
    verifyNextAndLastLinksEnabled();
    verifyPreviousAndFirstLinksDisabled();

    testWebDriver.getElementByXpath("//a[contains(text(), '2') and @class='ng-binding']").click();
    verifyPageLinksFromLastPage();
    verifyPreviousAndFirstLinksEnabled();
    verifyNextAndLastLinksDisabled();
  }

  private void setUpData(String program, String userSIC) throws SQLException {
    setupProductTestData("P10", "P11", program, "lvl3_hospital");
    dbWrapper.insertFacilities("F10", "F11");
    dbWrapper.configureTemplate(program);
    List<String> rightsList = asList("CONVERT_TO_ORDER", "VIEW_ORDER");

    setupTestUserRoleRightsData("200", userSIC, rightsList);
    dbWrapper.insertSupervisoryNode("F10", "N1", "Node 1", "null");
    dbWrapper.insertRoleAssignment("200", "store in-charge");
    dbWrapper.insertSchedule("Q1stM", "QuarterMonthly", "QuarterMonth");
    dbWrapper.insertSchedule("M", "Monthly", "Month");
    dbWrapper.insertProcessingPeriod("Period1", "first period", "2012-12-01", "2013-01-15", 1, "Q1stM");
    dbWrapper.insertProcessingPeriod("Period2", "second period", "2013-01-16", "2013-01-30", 1, "M");
    setupRequisitionGroupData("RG1", "RG2", "N1", "N2", "F10", "F11");
    dbWrapper.insertSupplyLines("N1", program, "F10", true);
  }

  @AfterMethod(groups = "requisition")
  public void tearDown() throws SQLException {
    testWebDriver.sleep(500);
    if (!testWebDriver.getElementById("username").isDisplayed()) {
      HomePage homePage = PageFactory.getHomePage(testWebDriver);
      homePage.logout(baseUrlGlobal);
      dbWrapper.deleteData();
      dbWrapper.closeConnection();
    }
  }

  @DataProvider(name = "Data-Provider-Function-Positive")
  public Object[][] parameterIntTestProviderPositive() {
    return new Object[][]{
      {"HIV", "storeInCharge", "Admin123"}
    };
  }
}

