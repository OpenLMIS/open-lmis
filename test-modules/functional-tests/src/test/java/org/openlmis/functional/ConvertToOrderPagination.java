/*
 * Copyright © 2013 VillageReach.  All Rights Reserved.  This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 *
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package org.openlmis.functional;


import org.openlmis.UiUtils.CaptureScreenshotOnFailureListener;
import org.openlmis.UiUtils.TestCaseHelper;
import org.openlmis.pageobjects.ConvertOrderPage;
import org.openlmis.pageobjects.HomePage;
import org.openlmis.pageobjects.LoginPage;
import org.openlmis.pageobjects.ViewOrdersPage;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;
import org.testng.annotations.*;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import static com.thoughtworks.selenium.SeleneseTestBase.assertTrue;
import static com.thoughtworks.selenium.SeleneseTestNgHelper.assertEquals;

@TransactionConfiguration(defaultRollback = true)
@Transactional

@Listeners(CaptureScreenshotOnFailureListener.class)

public class ConvertToOrderPagination extends TestCaseHelper {


  @BeforeMethod(groups = {"requisition"})
  public void setUp() throws Exception {
    super.setup();
  }

  @Test(groups = {"requisition"}, dataProvider = "Data-Provider-Function-Positive")
  public void shouldConvertOnlyCurrentPageRequisitions(String program, String userSIC, String password) throws Exception {
    setUpData(program, userSIC);
    dbWrapper.insertRequisitionsToBeConvertedToOrder(50, "MALARIA");
    dbWrapper.insertRequisitionsToBeConvertedToOrder(1, "TB");
    LoginPage loginPage = new LoginPage(testWebDriver, baseUrlGlobal);
    HomePage homePage = loginPage.loginAs(userSIC, password);
    ConvertOrderPage convertOrderPage = homePage.navigateConvertToOrder();

    testWebDriver.sleep(2000);
    verifyNumberOfPageLinks(51, 50);
    verifyNextAndLastLinksEnabled();
    verifyPreviousAndFirstLinksDisabled();


    testWebDriver.getElementByXpath("//a[contains(text(), '2') and @class='ng-binding']").click();
    verifyPageLinksFromLastPage();
    verifyPreviousAndFirstLinksEnabled();
    verifyNextAndLastLinksDisabled();

    selectRequisitionToBeConvertedToOrder(1);
    testWebDriver.getElementByXpath("//a[contains(text(), '1') and @class='ng-binding']").click();
    selectRequisitionToBeConvertedToOrder(2);
    convertToOrder(convertOrderPage);

    verifyNumberOfPageLinks(49, 50);

    ViewOrdersPage viewOrdersPage = homePage.navigateViewOrders();
    int numberOfLineItems = viewOrdersPage.getNumberOfLineItems();
    assertTrue("Number of line items on view order screen should be equal to 1", numberOfLineItems == 1);
    viewOrdersPage.verifyProgram(1, "MALARIA");
  }

  @Test(groups = {"requisition"}, dataProvider = "Data-Provider-Function-Positive")
  public void shouldVerifyIntroductionOfPagination(String program, String userSIC, String password) throws Exception {
    setUpData(program, userSIC);
    dbWrapper.insertRequisitionsToBeConvertedToOrder(49, "MALARIA");
    LoginPage loginPage = new LoginPage(testWebDriver, baseUrlGlobal);
    HomePage homePage = loginPage.loginAs(userSIC, password);
    homePage.navigateConvertToOrder();

    testWebDriver.sleep(2000);
    verifyNumberOfPageLinks(49, 50);
    dbWrapper.insertRequisitionsToBeConvertedToOrder(2, "HIV");
    homePage.navigateHomePage();
    homePage.navigateConvertToOrder();
    verifyNumberOfPageLinks(51, 50);

  }

  private void setUpData(String program, String userSIC) throws SQLException, IOException {
    dbWrapper.setupMultipleProducts(program, "Lvl3 Hospital", 11, false);
    dbWrapper.insertFacilities("F10", "F11");
    dbWrapper.configureTemplate(program);
    List<String> rightsList = new ArrayList<String>();
    rightsList.add("CONVERT_TO_ORDER");
    rightsList.add("VIEW_ORDER");
    rightsList.add("VIEW_REQUISITION");
    setupTestUserRoleRightsData("200", userSIC, "openLmis", rightsList);
    dbWrapper.insertSupervisoryNode("F10", "N1", "Node 1", "null");
    dbWrapper.insertRoleAssignment("200", "store in-charge");
    dbWrapper.insertSchedule("Q1stM", "QuarterMonthly", "QuarterMonth");
    dbWrapper.insertSchedule("M", "Monthly", "Month");
    dbWrapper.insertProcessingPeriod("Period1", "first period", "2012-12-01", "2013-01-15", 1, "Q1stM");
    dbWrapper.insertProcessingPeriod("Period2", "second period", "2013-01-16", "2013-01-30", 1, "M");
    setupRequisitionGroupData("RG1", "RG2", "N1", "N2", "F10", "F11");
    dbWrapper.insertSupplyLines("N1", program, "F10");
  }

  public void selectRequisitionToBeConvertedToOrder(int whichRequisition) {
    testWebDriver.sleep(500);
    String baseXpath = "(//input[@class='ngSelectionCheckbox'])";
    testWebDriver.waitForElementToAppear(testWebDriver.getElementByXpath(baseXpath + "[" + whichRequisition + "]"));
    testWebDriver.getElementByXpath(baseXpath + "[" + whichRequisition + "]").click();
  }


  public void convertToOrder(ConvertOrderPage convertOrderPage) {
    convertOrderPage.clickConvertToOrderButton();
    convertOrderPage.clickOk();
  }

  public void verifyPageLinksFromLastPage() throws Exception {
    verifyNextAndLastLinksDisabled();
    verifyPreviousAndFirstLinksEnabled();

    testWebDriver.getElementByXpath("//a[contains(text(), '«')]").click();
    verifyNextAndLastLinksEnabled();
    verifyPreviousAndFirstLinksDisabled();

    testWebDriver.getElementByXpath("//a[contains(text(), '>')]").click();
    verifyNextAndLastLinksDisabled();
    verifyPreviousAndFirstLinksEnabled();

    testWebDriver.getElementByXpath("//a[contains(text(), '<')]").click();
    verifyNextAndLastLinksEnabled();
    verifyPreviousAndFirstLinksDisabled();

    testWebDriver.getElementByXpath("//a[contains(text(), '»')]").click();
    verifyNextAndLastLinksDisabled();
    verifyPreviousAndFirstLinksEnabled();
  }

  public void verifyNumberOfPageLinks(int numberOfProducts, int numberOfLineItemsPerPage) throws Exception {
    int numberOfPages = numberOfProducts / numberOfLineItemsPerPage;
    if (numberOfProducts % numberOfLineItemsPerPage != 0) {
      numberOfPages = numberOfPages + 1;
    }
    for (int i = 1; i <= numberOfPages; i++) {
      testWebDriver.waitForElementToAppear(testWebDriver.getElementByXpath("//a[contains(text(), '" + i + "') and @class='ng-binding']"));
      assertTrue(testWebDriver.getElementByXpath("//a[contains(text(), '" + i + "') and @class='ng-binding']").isDisplayed());
    }
  }

  public void verifyNextAndLastLinksEnabled() throws Exception {
    testWebDriver.sleep(1000);
    testWebDriver.waitForElementToAppear(testWebDriver.getElementByXpath("//a[contains(text(), '>')]"));
    assertEquals(testWebDriver.getElementByXpath("//a[contains(text(), '>')]").getCssValue("color"), "rgba(119, 119, 119, 1)");
    assertEquals(testWebDriver.getElementByXpath("//a[contains(text(), '»')]").getCssValue("color"), "rgba(119, 119, 119, 1)");
  }

  public void verifyPreviousAndFirstLinksEnabled() throws Exception {
    testWebDriver.sleep(1000);
    testWebDriver.waitForElementToAppear(testWebDriver.getElementByXpath("//a[contains(text(), '<')]"));
    assertEquals(testWebDriver.getElementByXpath("//a[contains(text(), '<')]").getCssValue("color"), "rgba(119, 119, 119, 1)");
    assertEquals(testWebDriver.getElementByXpath("//a[contains(text(), '«')]").getCssValue("color"), "rgba(119, 119, 119, 1)");
  }

  public void verifyNextAndLastLinksDisabled() throws Exception {
    testWebDriver.sleep(1000);
    testWebDriver.waitForElementToAppear(testWebDriver.getElementByXpath("//a[contains(text(), '>')]"));
    assertEquals(testWebDriver.getElementByXpath("//a[contains(text(), '>')]").getCssValue("color"), "rgba(204, 204, 204, 1)");
    assertEquals(testWebDriver.getElementByXpath("//a[contains(text(), '»')]").getCssValue("color"), "rgba(204, 204, 204, 1)");
  }

  public void verifyPreviousAndFirstLinksDisabled() throws Exception {
    testWebDriver.sleep(1000);
    testWebDriver.waitForElementToAppear(testWebDriver.getElementByXpath("//a[contains(text(), '«')]"));
    assertEquals(testWebDriver.getElementByXpath("//a[contains(text(), '«')]").getCssValue("color"), "rgba(204, 204, 204, 1)");
    assertEquals(testWebDriver.getElementByXpath("//a[contains(text(), '<')]").getCssValue("color"), "rgba(204, 204, 204, 1)");
  }


  @AfterMethod(groups = {"requisition"})
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

