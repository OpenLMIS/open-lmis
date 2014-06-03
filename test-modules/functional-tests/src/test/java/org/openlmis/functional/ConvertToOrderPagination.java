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


import cucumber.api.java.en.And;
import cucumber.api.java.en.Then;
import org.openlmis.UiUtils.CaptureScreenshotOnFailureListener;
import org.openlmis.UiUtils.TestCaseHelper;
import org.openlmis.UiUtils.TestWebDriver;
import org.openlmis.pageobjects.HomePage;
import org.openlmis.pageobjects.LoginPage;
import org.openlmis.pageobjects.PageObjectFactory;
import org.openlmis.pageobjects.ViewOrdersPage;
import org.openlmis.pageobjects.edi.ConvertOrderPage;
import org.openqa.selenium.ElementNotVisibleException;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.NoSuchElementException;
import org.testng.annotations.*;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

import static com.thoughtworks.selenium.SeleneseTestBase.assertTrue;
import static com.thoughtworks.selenium.SeleneseTestNgHelper.assertEquals;
import static java.util.Arrays.asList;

@Listeners(CaptureScreenshotOnFailureListener.class)

public class ConvertToOrderPagination extends TestCaseHelper {

  @BeforeMethod(groups = "orderAndPod")
  public void setUp() throws InterruptedException, SQLException, IOException {
    super.setup();
  }

  @And("^I have \"([^\"]*)\" requisitions for convert to order$")
  public void haveRequisitionsToBeConvertedToOrder(String requisitions) throws SQLException {
    String userSIC = "storeInCharge";
    setUpData("HIV", userSIC);
    dbWrapper.insertRequisitions(Integer.parseInt(requisitions), "MALARIA", true, "2012-12-01", "2015-12-01", "F10", false);
    dbWrapper.updateRequisitionStatus("SUBMITTED", userSIC, "MALARIA");
    dbWrapper.updateRequisitionStatus("APPROVED", userSIC, "MALARIA");
    dbWrapper.updateFieldValue("requisition_line_items", "quantityApproved", 10);
    dbWrapper.updatePacksToShip("1");
    dbWrapper.insertFulfilmentRoleAssignment(userSIC, "store in-charge", "F10");
  }

  @And("^I have a/an \"([^\"]*)\" order in \"([^\"]*)\" status$")
  public void setupDataForOrderInGivenState(String requisitionType, String orderStatus) throws SQLException {
    boolean isEmergency = true;
    if (requisitionType.toLowerCase().equals("regular")) {
      isEmergency = false;
    }
    String userSIC = "storeInCharge";
    setUpData("HIV", userSIC);
    dbWrapper.insertRequisitions(1, "HIV", true, "2012-12-01", "2015-12-01", "F10", isEmergency);
    dbWrapper.updateRequisitionStatus("APPROVED", userSIC, "HIV");
    dbWrapper.updateFieldValue("requisition_line_items", "packsToShip", 10);
    dbWrapper.convertRequisitionToOrder(dbWrapper.getMaxRnrID(), orderStatus, userSIC);
    dbWrapper.insertFulfilmentRoleAssignment(userSIC, "store in-charge", "F10");
  }

  @And("^I select \"([^\"]*)\" requisition on page \"([^\"]*)\"$")
  public void selectRequisition(String numberOfRequisitions, String page) {
    testWebDriver.sleep(5000);
    testWebDriver.handleScrollByPixels(0, 1000);
    String url = ((JavascriptExecutor) TestWebDriver.getDriver()).executeScript("return window.location.href").toString();
    url = url.substring(0, url.length() - 1) + page;
    testWebDriver.getUrl(url);
    PageObjectFactory.getConvertOrderPage(testWebDriver).selectRequisitionToBeConvertedToOrder(Integer.parseInt(numberOfRequisitions));
  }

  @And("^I convert selected requisitions to order$")
  public void convertSelectedRequisitionToOrder() {
    PageObjectFactory.getConvertOrderPage(testWebDriver);
    convertToOrder();
  }

  @Then("^\"([^\"]*)\" requisition converted to order$")
  public void requisitionConvertedToOrder(String requisitions) {
    HomePage homePage = PageObjectFactory.getHomePage(testWebDriver);
    ViewOrdersPage viewOrdersPage = homePage.navigateViewOrders();
    int numberOfLineItems = viewOrdersPage.getNumberOfLineItems();
    assertTrue("Number of line items on view order screen should be equal to " + Integer.parseInt(requisitions), numberOfLineItems == Integer.parseInt(requisitions));
  }

  @Test(groups = {"orderAndPod"}, dataProvider = "Data-Provider-Function-Positive")
  public void shouldConvertOnlyCurrentPageRequisitions(String program, String userSIC, String password) throws SQLException {
    setUpData(program, userSIC);
    dbWrapper.insertRequisitions(50, "MALARIA", true, "2012-12-01", "2015-12-01", "F10", false);
    dbWrapper.insertRequisitions(1, "TB", true, "2012-12-01", "2015-12-01", "F10", false);
    dbWrapper.updateRequisitionStatus("SUBMITTED", userSIC, "MALARIA");
    dbWrapper.updateRequisitionStatus("SUBMITTED", userSIC, "TB");
    dbWrapper.updateRequisitionStatus("APPROVED", userSIC, "MALARIA");
    dbWrapper.updateRequisitionStatus("APPROVED", userSIC, "TB");
    dbWrapper.updateFieldValue("requisition_line_items", "quantityApproved", 10);
    dbWrapper.updatePacksToShip("1");
    dbWrapper.insertFulfilmentRoleAssignment("storeInCharge", "store in-charge", "F10");

    LoginPage loginPage = PageObjectFactory.getLoginPage(testWebDriver, baseUrlGlobal);
    HomePage homePage = loginPage.loginAs(userSIC, password);
    ConvertOrderPage convertOrderPage = homePage.navigateConvertToOrder();
    verifyNumberOFPageLinksDisplayed(51, 50);
    verifyNextAndLastPageLinksEnabled();
    verifyPreviousAndFirstPageLinksDisabled();

    clickPageNumberLink(2);
    verifyPageLinksFromLastPage();
    verifyPreviousAndFirstPageLinksEnabled();
    verifyNextAndLastPageLinksDisabled();

    convertOrderPage.selectRequisitionToBeConvertedToOrder(1);
    clickPageNumberLink(1);
    convertOrderPage.selectRequisitionToBeConvertedToOrder(1);
    convertToOrder();
    verifyNumberOFPageLinksDisplayed(49, 50);

    ViewOrdersPage viewOrdersPage = homePage.navigateViewOrders();
    int numberOfLineItems = viewOrdersPage.getNumberOfLineItems();
    assertTrue("Number of line items on view order screen should be equal to 1, but found " + numberOfLineItems, numberOfLineItems == 1);
    viewOrdersPage.verifyProgram(1, "MALARIA");
  }

  private void clickPageNumberLink(int pageNumber) {
    testWebDriver.getElementById(String.valueOf(pageNumber)).click();
    testWebDriver.sleep(2000);
  }

  @Test(groups = {"orderAndPod"}, dataProvider = "Data-Provider-Function-Positive")
  public void shouldVerifyIntroductionOfPagination(String program, String userSIC, String password) throws SQLException {
    setUpData(program, userSIC);
    dbWrapper.insertRequisitions(49, "MALARIA", true, "2012-12-01", "2015-12-01", "F10", false);
    dbWrapper.updateRequisitionStatus("SUBMITTED", userSIC, "MALARIA");
    dbWrapper.updateRequisitionStatus("APPROVED", userSIC, "MALARIA");
    dbWrapper.insertFulfilmentRoleAssignment("storeInCharge", "store in-charge", "F10");

    LoginPage loginPage = PageObjectFactory.getLoginPage(testWebDriver, baseUrlGlobal);
    HomePage homePage = loginPage.loginAs(userSIC, password);
    homePage.navigateConvertToOrder();
    verifyNumberOFPageLinksDisplayed(49, 50);

    dbWrapper.insertRequisitions(2, "HIV", true, "2012-12-01", "2015-12-01", "F10", false);
    dbWrapper.updateRequisitionStatus("SUBMITTED", userSIC, "HIV");
    dbWrapper.updateRequisitionStatus("APPROVED", userSIC, "HIV");
    homePage.navigateHomePage();
    homePage.navigateConvertToOrder();
    verifyNumberOFPageLinksDisplayed(51, 50);
  }

  @Test(groups = {"orderAndPod"}, dataProvider = "Data-Provider-Function-Positive")
  public void shouldVerifyIntroductionOfPaginationForBoundaryValue(String program, String userSIC, String password) throws SQLException {
    setUpData(program, userSIC);
    dbWrapper.insertRequisitions(50, "MALARIA", true, "2012-12-01", "2015-12-01", "F10", false);
    dbWrapper.updateRequisitionStatus("SUBMITTED", userSIC, "MALARIA");
    dbWrapper.updateRequisitionStatus("APPROVED", userSIC, "MALARIA");
    dbWrapper.insertFulfilmentRoleAssignment("storeInCharge", "store in-charge", "F10");

    LoginPage loginPage = PageObjectFactory.getLoginPage(testWebDriver, baseUrlGlobal);
    HomePage homePage = loginPage.loginAs(userSIC, password);
    homePage.navigateConvertToOrder();
    verifyNumberOFPageLinksDisplayed(50, 50);
    verifyPageLinkNotPresent(2);

    dbWrapper.insertRequisitions(1, "HIV", true, "2012-12-01", "2015-12-01", "F10", false);
    dbWrapper.updateRequisitionStatus("SUBMITTED", userSIC, "HIV");
    dbWrapper.updateRequisitionStatus("APPROVED", userSIC, "HIV");
    homePage.navigateHomePage();
    homePage.navigateConvertToOrder();
    verifyNumberOFPageLinksDisplayed(51, 50);
  }

  @Test(groups = {"orderAndPod"}, dataProvider = "Data-Provider-Function-Positive")
  public void shouldVerifySearch(String program, String userSIC, String password) throws SQLException {
    setUpData(program, userSIC);
    dbWrapper.insertRequisitions(55, "MALARIA", true, "2012-12-01", "2015-12-01", "F10", false);
    dbWrapper.insertRequisitions(40, "TB", true, "2012-12-01", "2015-12-01", "F10", false);
    dbWrapper.updateRequisitionStatus("SUBMITTED", userSIC, "MALARIA");
    dbWrapper.updateRequisitionStatus("SUBMITTED", userSIC, "TB");
    dbWrapper.updateRequisitionStatus("APPROVED", userSIC, "MALARIA");
    dbWrapper.updateRequisitionStatus("APPROVED", userSIC, "TB");
    dbWrapper.insertFulfilmentRoleAssignment("storeInCharge", "store in-charge", "F10");

    LoginPage loginPage = PageObjectFactory.getLoginPage(testWebDriver, baseUrlGlobal);
    HomePage homePage = loginPage.loginAs(userSIC, password);
    ConvertOrderPage convertOrderPage = homePage.navigateConvertToOrder();
    verifyNumberOFPageLinksDisplayed(80, 50);
    convertOrderPage.searchWithOption("All", "TB");
    verifyNumberOFPageLinksDisplayed(40, 50);
    verifyProgramInGrid(40, 50, "TB");
    verifyPageLinkNotPresent(2);
    convertOrderPage.searchWithOption("All", "MALARIA");
    verifyNumberOFPageLinksDisplayed(55, 50);
    verifyProgramInGrid(55, 50, "MALARIA");
  }

  @Test(groups = {"orderAndPod"}, dataProvider = "Data-Provider-Function-Positive")
  public void shouldVerifySearchWithDifferentOptions(String program, String userSIC, String password) throws SQLException {
    setUpData(program, userSIC);
    dbWrapper.insertRequisitions(55, "MALARIA", true, "2012-12-01", "2015-12-01", "F10", false);
    dbWrapper.insertRequisitions(40, "TB", false, "2012-12-01", "2015-12-01", "F10", false);
    dbWrapper.updateRequisitionStatus("SUBMITTED", userSIC, "MALARIA");
    dbWrapper.updateRequisitionStatus("SUBMITTED", userSIC, "TB");
    dbWrapper.updateRequisitionStatus("APPROVED", userSIC, "MALARIA");
    dbWrapper.updateRequisitionStatus("APPROVED", userSIC, "TB");
    dbWrapper.insertFulfilmentRoleAssignment("storeInCharge", "store in-charge", "F10");

    LoginPage loginPage = PageObjectFactory.getLoginPage(testWebDriver, baseUrlGlobal);
    HomePage homePage = loginPage.loginAs(userSIC, password);
    ConvertOrderPage convertOrderPage = homePage.navigateConvertToOrder();
    convertOrderPage.searchWithIndex(5, "Village Dispensary");
    verifyNumberOFPageLinksDisplayed(55, 50);
    verifySupplyingDepotInGrid(55, 50, "Village Dispensary");
  }

  private void setUpData(String program, String userSIC) throws SQLException {
    setupProductTestData("P10", "P11", program, "lvl3_hospital");
    dbWrapper.insertFacilities("F10", "F11");
    dbWrapper.configureTemplate(program);
    List<String> rightsList = asList("CONVERT_TO_ORDER", "VIEW_ORDER");

    setupTestUserRoleRightsData(userSIC, rightsList);
    dbWrapper.insertSupervisoryNode("F10", "N1", "Node 1", "null");
    dbWrapper.insertRoleAssignment(userSIC, "store in-charge");
    dbWrapper.insertSchedule("Q1stM", "QuarterMonthly", "QuarterMonth");
    dbWrapper.insertSchedule("M", "Monthly", "Month");
    dbWrapper.insertProcessingPeriod("Period1", "first period", "2012-12-01", "2013-01-15", 1, "Q1stM");
    dbWrapper.insertProcessingPeriod("Period2", "second period", "2013-01-16", "2013-01-30", 1, "M");
    setupRequisitionGroupData("RG1", "RG2", "N1", "N2", "F10", "F11");
    dbWrapper.insertSupplyLines("N1", program, "F10", true);
  }

  private void convertToOrder() {
    ConvertOrderPage convertOrderPage = PageObjectFactory.getConvertOrderPage(testWebDriver);
    convertOrderPage.clickConvertToOrderButton();
    convertOrderPage.clickOk();
  }

  public void verifyProgramInGrid(int numberOfProducts, int numberOfLineItemsPerPage, String program) {
    int numberOfPages = numberOfProducts / numberOfLineItemsPerPage;
    if (numberOfProducts % numberOfLineItemsPerPage != 0) {
      numberOfPages = numberOfPages + 1;
    }
    int trackPages = 0;
    while (numberOfPages != trackPages) {
      testWebDriver.getElementByXpath("//a[contains(text(), '" + (trackPages + 1) + "') and @class='ng-binding']").click();
      testWebDriver.waitForPageToLoad();
      for (int i = 1; i < testWebDriver.getElementsSizeByXpath("//div[@class='ngCanvas']/div"); i++)
        assertEquals(testWebDriver.getElementByXpath("//div[@class='ngCanvas']/div[" + i + "]/div[2]/div[2]/div/span").getText().trim(), program);
      trackPages++;
    }
  }

  public void verifySupplyingDepotInGrid(int numberOfProducts, int numberOfLineItemsPerPage, String supplyingDepot) {
    int numberOfPages = numberOfProducts / numberOfLineItemsPerPage;
    if (numberOfProducts % numberOfLineItemsPerPage != 0) {
      numberOfPages = numberOfPages + 1;
    }
    int trackPages = 0;
    while (numberOfPages != trackPages) {
      testWebDriver.getElementByXpath("//a[contains(text(), '" + (trackPages + 1) + "') and @class='ng-binding']").click();
      testWebDriver.waitForPageToLoad();
      for (int i = 1; i < testWebDriver.getElementsSizeByXpath("//div[@class='ngCanvas']/div"); i++)
        assertEquals(testWebDriver.getElementByXpath("//div[@class='ngCanvas']/div[" + i + "]/div[9]/div[2]/div/span").getText().trim(), supplyingDepot);
      trackPages++;
    }
  }

  public void verifyPageLinkNotPresent(int i) {
    boolean flag = false;
    try {
      testWebDriver.getElementByXpath("//a[contains(text(), '" + i + "') and @class='ng-binding']").click();
    } catch (NoSuchElementException e) {
      flag = true;
    } catch (ElementNotVisibleException e) {
      flag = true;
    }
    assertTrue("Link number" + i + " should not appear", flag);
  }

  @Test(groups = {"orderAndPod"}, dataProvider = "Data-Provider-Function-Positive")
  public void VerifyConvertToOrderAccessOnRequisition(String program, String userSIC, String password) throws SQLException {
    setUpData(program, userSIC);
    dbWrapper.insertRequisitions(50, "MALARIA", true, "2012-12-01", "2015-12-01", "F10", false);
    dbWrapper.insertRequisitions(1, "TB", true, "2012-12-01", "2015-12-01", "F10", false);
    dbWrapper.updateRequisitionStatus("SUBMITTED", userSIC, "MALARIA");
    dbWrapper.updateRequisitionStatus("SUBMITTED", userSIC, "TB");
    dbWrapper.updateRequisitionStatus("APPROVED", userSIC, "MALARIA");
    dbWrapper.updateRequisitionStatus("APPROVED", userSIC, "TB");
    dbWrapper.insertFulfilmentRoleAssignment("storeInCharge", "store in-charge", "F10");
    dbWrapper.updateSupplyLines("F10", "F11");
    LoginPage loginPage = PageObjectFactory.getLoginPage(testWebDriver, baseUrlGlobal);
    HomePage homePage = loginPage.loginAs(userSIC, password);
    ConvertOrderPage convertOrderPage = homePage.navigateConvertToOrder();
    assertEquals("No requisitions to be converted to orders", convertOrderPage.getNoRequisitionPendingMessage());
  }

  @AfterMethod(groups = "orderAndPod")
  public void tearDown() throws SQLException {
    testWebDriver.sleep(500);
    if (!testWebDriver.getElementById("username").isDisplayed()) {
      HomePage homePage = PageObjectFactory.getHomePage(testWebDriver);
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

