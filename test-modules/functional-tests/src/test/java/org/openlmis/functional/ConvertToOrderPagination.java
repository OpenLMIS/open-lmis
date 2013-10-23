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


import cucumber.api.java.After;
import cucumber.api.java.Before;
import cucumber.api.java.en.And;
import cucumber.api.java.en.Then;
import org.openlmis.UiUtils.CaptureScreenshotOnFailureListener;
import org.openlmis.UiUtils.TestCaseHelper;
import org.openlmis.pageobjects.ConvertOrderPage;
import org.openlmis.pageobjects.HomePage;
import org.openlmis.pageobjects.LoginPage;
import org.openlmis.pageobjects.ViewOrdersPage;
import org.openqa.selenium.ElementNotVisibleException;
import org.openqa.selenium.NoSuchElementException;
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


  @BeforeMethod(groups = "requisition")
  @Before
  public void setUp() throws Exception {
    super.setup();
  }

  @And("^I have \"([^\"]*)\" requisitions for convert to order$")
  public void haveRequisitionsToBeConvertedToOrder(String requisitions) throws IOException, SQLException {
    String userSIC = "storeIncharge";
    setUpData("HIV", userSIC);
    dbWrapper.insertRequisitionsToBeConvertedToOrder(Integer.parseInt(requisitions), "MALARIA", true);
    dbWrapper.updateRequisitionStatus("SUBMITTED", userSIC, "MALARIA");
    dbWrapper.updateRequisitionStatus("APPROVED", userSIC, "MALARIA");
    dbWrapper.insertFulfilmentRoleAssignment(userSIC, "store in-charge", "F10");
  }

  @And("^I select \"([^\"]*)\" requisition on page \"([^\"]*)\"$")
  public void selectRequisition(String numberOfRequisitions, String page) throws IOException, SQLException {
    testWebDriver.sleep(3000);
    testWebDriver.getElementByXpath("//a[contains(text(), '" + page + "') and @class='ng-binding']").click();
    selectRequisitionToBeConvertedToOrder(Integer.parseInt(numberOfRequisitions));
  }

  @And("^I access convert to order$")
  public void accessConvertToOrder() throws IOException, SQLException {
    ConvertOrderPage convertOrderPage = new ConvertOrderPage(testWebDriver);
    convertToOrder(convertOrderPage);
  }

  @Then("^\"([^\"]*)\" requisition converted to order$")
  public void requisitionConvertedToOrder(String requisitions) throws IOException, SQLException {
    HomePage homePage = new HomePage(testWebDriver);
    ViewOrdersPage viewOrdersPage = homePage.navigateViewOrders();
    int numberOfLineItems = viewOrdersPage.getNumberOfLineItems();
    assertTrue("Number of line items on view order screen should be equal to " + Integer.parseInt(requisitions), numberOfLineItems == Integer.parseInt(requisitions));
  }

  @Test(groups = {"requisition"}, dataProvider = "Data-Provider-Function-Positive")
  public void shouldConvertOnlyCurrentPageRequisitions(String program, String userSIC, String password) throws Exception {
    setUpData(program, userSIC);
    dbWrapper.insertRequisitionsToBeConvertedToOrder(50, "MALARIA", true);
    dbWrapper.insertRequisitionsToBeConvertedToOrder(1, "TB", true);
    dbWrapper.updateRequisitionStatus("SUBMITTED", userSIC, "MALARIA");
    dbWrapper.updateRequisitionStatus("SUBMITTED", userSIC, "TB");
    dbWrapper.updateRequisitionStatus("APPROVED", userSIC, "MALARIA");
    dbWrapper.updateRequisitionStatus("APPROVED", userSIC, "TB");
    dbWrapper.insertFulfilmentRoleAssignment("storeIncharge", "store in-charge", "F10");
    LoginPage loginPage = new LoginPage(testWebDriver, baseUrlGlobal);
    HomePage homePage = loginPage.loginAs(userSIC, password);
    ConvertOrderPage convertOrderPage = homePage.navigateConvertToOrder();
    verifyNumberOfPageLinks(51, 50);
    verifyNextAndLastLinksEnabled();
    verifyPreviousAndFirstLinksDisabled();


    clickPageNumberLink(2);
    verifyPageLinksFromLastPage();
    verifyPreviousAndFirstLinksEnabled();
    verifyNextAndLastLinksDisabled();

    selectRequisitionToBeConvertedToOrder(1);
    clickPageNumberLink(1);
    selectRequisitionToBeConvertedToOrder(1);
    convertToOrder(convertOrderPage);

    verifyNumberOfPageLinks(49, 50);

    ViewOrdersPage viewOrdersPage = homePage.navigateViewOrders();
    int numberOfLineItems = viewOrdersPage.getNumberOfLineItems();
    assertTrue("Number of line items on view order screen should be equal to 1", numberOfLineItems == 1);
    viewOrdersPage.verifyProgram(1, "MALARIA");
  }

  private void clickPageNumberLink(int pageNumber) {
    testWebDriver.getElementByXpath("//a[contains(text(), '" + pageNumber + "') and @class='ng-binding']").click();
    testWebDriver.sleep(2000);
  }

  @Test(groups = {"requisition"}, dataProvider = "Data-Provider-Function-Positive")
  public void shouldVerifyIntroductionOfPagination(String program, String userSIC, String password) throws Exception {
    setUpData(program, userSIC);
    dbWrapper.insertRequisitionsToBeConvertedToOrder(49, "MALARIA", true);
    dbWrapper.updateRequisitionStatus("SUBMITTED", userSIC, "MALARIA");
    dbWrapper.updateRequisitionStatus("APPROVED", userSIC, "MALARIA");
    dbWrapper.insertFulfilmentRoleAssignment("storeIncharge", "store in-charge", "F10");
    LoginPage loginPage = new LoginPage(testWebDriver, baseUrlGlobal);
    HomePage homePage = loginPage.loginAs(userSIC, password);
    homePage.navigateConvertToOrder();
    verifyNumberOfPageLinks(49, 50);
    dbWrapper.insertRequisitionsToBeConvertedToOrder(2, "HIV", true);
    dbWrapper.updateRequisitionStatus("SUBMITTED", userSIC, "HIV");
    dbWrapper.updateRequisitionStatus("APPROVED", userSIC, "HIV");
    homePage.navigateHomePage();
    homePage.navigateConvertToOrder();
    verifyNumberOfPageLinks(51, 50);

  }

  @Test(groups = {"requisition"}, dataProvider = "Data-Provider-Function-Positive")
  public void shouldVerifyIntroductionOfPaginationForBoundaryValue(String program, String userSIC, String password) throws Exception {
    setUpData(program, userSIC);
    dbWrapper.insertRequisitionsToBeConvertedToOrder(50, "MALARIA", true);
    dbWrapper.updateRequisitionStatus("SUBMITTED", userSIC, "MALARIA");
    dbWrapper.updateRequisitionStatus("APPROVED", userSIC, "MALARIA");
    dbWrapper.insertFulfilmentRoleAssignment("storeIncharge", "store in-charge", "F10");
    LoginPage loginPage = new LoginPage(testWebDriver, baseUrlGlobal);
    HomePage homePage = loginPage.loginAs(userSIC, password);
    homePage.navigateConvertToOrder();
    verifyNumberOfPageLinks(50, 50);
    verifyPageLinkNotPresent(2);
    dbWrapper.insertRequisitionsToBeConvertedToOrder(1, "HIV", true);
    dbWrapper.updateRequisitionStatus("SUBMITTED", userSIC, "HIV");
    dbWrapper.updateRequisitionStatus("APPROVED", userSIC, "HIV");
    homePage.navigateHomePage();
    homePage.navigateConvertToOrder();
    verifyNumberOfPageLinks(51, 50);

  }

  @Test(groups = {"requisition"}, dataProvider = "Data-Provider-Function-Positive")
  public void shouldVerifySearch(String program, String userSIC, String password) throws Exception {
    setUpData(program, userSIC);
    dbWrapper.insertRequisitionsToBeConvertedToOrder(55, "MALARIA", true);
    dbWrapper.insertRequisitionsToBeConvertedToOrder(40, "TB", true);
    dbWrapper.updateRequisitionStatus("SUBMITTED", userSIC, "MALARIA");
    dbWrapper.updateRequisitionStatus("SUBMITTED", userSIC, "TB");
    dbWrapper.updateRequisitionStatus("APPROVED", userSIC, "MALARIA");
    dbWrapper.updateRequisitionStatus("APPROVED", userSIC, "TB");
    dbWrapper.insertFulfilmentRoleAssignment("storeIncharge", "store in-charge", "F10");
    LoginPage loginPage = new LoginPage(testWebDriver, baseUrlGlobal);
    HomePage homePage = loginPage.loginAs(userSIC, password);
    ConvertOrderPage convertOrderPage = homePage.navigateConvertToOrder();
    verifyNumberOfPageLinks(80, 50);
    convertOrderPage.searchWithOption("All", "TB");
    verifyNumberOfPageLinks(40, 50);
    verifyProgramInGrid(40, 50, "TB");
    verifyPageLinkNotPresent(2);
    convertOrderPage.searchWithOption("All", "MALARIA");
    verifyNumberOfPageLinks(55, 50);
    verifyProgramInGrid(55, 50, "MALARIA");
  }

  @Test(groups = {"requisition"}, dataProvider = "Data-Provider-Function-Positive")
  public void shouldVerifySearchWithDifferentOptions(String program, String userSIC, String password) throws Exception {
    setUpData(program, userSIC);
    dbWrapper.insertRequisitionsToBeConvertedToOrder(55, "MALARIA", true);
    dbWrapper.insertRequisitionsToBeConvertedToOrder(40, "TB", false);
    dbWrapper.updateRequisitionStatus("SUBMITTED", userSIC, "MALARIA");
    dbWrapper.updateRequisitionStatus("SUBMITTED", userSIC, "TB");
    dbWrapper.updateRequisitionStatus("APPROVED", userSIC, "MALARIA");
    dbWrapper.updateRequisitionStatus("APPROVED", userSIC, "TB");
    dbWrapper.insertFulfilmentRoleAssignment("storeIncharge", "store in-charge", "F10");
    LoginPage loginPage = new LoginPage(testWebDriver, baseUrlGlobal);
    HomePage homePage = loginPage.loginAs(userSIC, password);
    ConvertOrderPage convertOrderPage = homePage.navigateConvertToOrder();
    convertOrderPage.searchWithIndex(5, "Village Dispensary");
    verifyNumberOfPageLinks(55, 50);
    verifySupplyingDepotInGrid(55, 50, "Village Dispensary");
  }

  private void setUpData(String program, String userSIC) throws SQLException, IOException {
    dbWrapper.setupMultipleProducts(program, "Lvl3 Hospital", 11, false);
    dbWrapper.insertFacilities("F10", "F11");
    dbWrapper.configureTemplate(program);
    List<String> rightsList = new ArrayList<String>();
    rightsList.add("CONVERT_TO_ORDER");
    rightsList.add("VIEW_ORDER");
    rightsList.add("VIEW_REQUISITION");
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

  public void selectRequisitionToBeConvertedToOrder(int whichRequisition) {
    testWebDriver.waitForPageToLoad();
    String baseXpath = "(//input[@class='ngSelectionCheckbox'])";
    testWebDriver.waitForElementToAppear(testWebDriver.getElementByXpath(baseXpath + "[" + whichRequisition + "]"));
    testWebDriver.getElementByXpath(baseXpath + "[" + whichRequisition + "]").click();
  }


  public void convertToOrder(ConvertOrderPage convertOrderPage) {
    testWebDriver.sleep(2000);
    convertOrderPage.clickConvertToOrderButton();
    convertOrderPage.clickOk();
  }

  public void verifyProgramInGrid(int numberOfProducts, int numberOfLineItemsPerPage, String program) throws Exception {
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

  public void verifySupplyingDepotInGrid(int numberOfProducts, int numberOfLineItemsPerPage, String supplyingDepot) throws Exception {
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

  public void verifyPageLinkNotPresent(int i) throws Exception {
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

    @Test(groups = {"requisition"}, dataProvider = "Data-Provider-Function-Positive")
    public void VerifyConvertToOrderAccessOnRequisition(String program, String userSIC, String password) throws Exception {
        setUpData(program, userSIC);
        dbWrapper.insertRequisitionsToBeConvertedToOrder(50, "MALARIA", true);
        dbWrapper.insertRequisitionsToBeConvertedToOrder(1, "TB", true);
        dbWrapper.updateRequisitionStatus("SUBMITTED", userSIC, "MALARIA");
        dbWrapper.updateRequisitionStatus("SUBMITTED", userSIC, "TB");
        dbWrapper.updateRequisitionStatus("APPROVED", userSIC, "MALARIA");
        dbWrapper.updateRequisitionStatus("APPROVED", userSIC, "TB");
        dbWrapper.insertFulfilmentRoleAssignment("storeIncharge", "store in-charge", "F10");
        dbWrapper.updateSupplyLines("F10","F11");
        LoginPage loginPage = new LoginPage(testWebDriver, baseUrlGlobal);
        HomePage homePage = loginPage.loginAs(userSIC, password);
        ConvertOrderPage convertOrderPage = homePage.navigateConvertToOrder();
        assertEquals("No requisitions to be converted to orders", convertOrderPage.getNoRequisitionPendingMessage());
    }

  @AfterMethod(groups = "requisition")
  @After
  public void tearDown() throws Exception {
    testWebDriver.sleep(500);
    if (!testWebDriver.getElementById("username").isDisplayed()) {
      HomePage homePage = new HomePage(testWebDriver);
      homePage.logout(baseUrlGlobal);
      dbWrapper.deleteData();
      dbWrapper.closeConnection();
    }

  }


  @DataProvider(name = "Data-Provider-Function-Positive")
  public Object[][] parameterIntTestProviderPositive() {
    return new Object[][]{
      {"HIV", "storeIncharge", "Admin123"}
    };

  }
}

