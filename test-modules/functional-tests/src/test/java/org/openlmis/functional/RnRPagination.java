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
import org.openlmis.pageobjects.*;
import org.testng.annotations.*;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

import static com.thoughtworks.selenium.SeleneseTestNgHelper.assertEquals;
import static java.util.Arrays.asList;

@Listeners(CaptureScreenshotOnFailureListener.class)

public class RnRPagination extends TestCaseHelper {

  private static final String FULL_SUPPLY_BASE_LOCATOR = "//table[@id='fullSupplyTable']";
  private static final String NON_FULL_SUPPLY_BASE_LOCATOR = "//table[@id='nonFullSupplyTable']";

  InitiateRnRPage initiateRnRPage;
  LoginPage loginPage;

  @BeforeMethod(groups = {"requisition"})
  public void setUp() throws InterruptedException, SQLException, IOException {
    super.setup();
    initiateRnRPage = PageObjectFactory.getInitiateRnRPage(testWebDriver);
    loginPage = PageObjectFactory.getLoginPage(testWebDriver, baseUrlGlobal);
  }

  @Test(groups = {"requisition"}, dataProvider = "Data-Provider-Function-Positive")
  public void testRnRPaginationAndSpecificDisplayOrder(String program, String userSIC, String password) throws SQLException {
    dbWrapper.setupMultipleProducts(program, "Lvl3 Hospital", 11, false);
    dbWrapper.insertProgramProduct("F5", "MALARIA", "70", "t");
    dbWrapper.insertProgramProduct("NF4", "MALARIA", "90", "t");
    setupData(program, userSIC);

    HomePage homePage = loginPage.loginAs(userSIC, password);
    homePage.navigateAndInitiateRnr(program);
    InitiateRnRPage initiateRnRPage = homePage.clickProceed();

    testWebDriver.sleep(2000);
    verifyNumberOFPageLinksDisplayed(11, 10);
    verifyNextAndLastPageLinksEnabled();
    verifyPreviousAndFirstPageLinksDisabled();
    verifyDisplayOrderFullSupply(10);
    testWebDriver.getElementById("nextPageLink").click();
    assertEquals(testWebDriver.getElementById("productCode_0").getText(), "F10");
    testWebDriver.getElementById("firstPageLink").click();

    initiateRnRPage.PopulateMandatoryFullSupplyDetails(11, 10);

    assertEquals(testWebDriver.getElementById("category_0").getText(), "Antibiotics");
    testWebDriver.getElementById("firstPageLink").click();
    assertEquals(testWebDriver.getElementById("category_0").getText(), "Antibiotics");
    assertEquals(testWebDriver.getElementById("category_1").getText(), "");
    testWebDriver.getElementById("nextPageLink").click();
    verifyPageLinksFromLastPage();

    initiateRnRPage.addMultipleNonFullSupplyLineItems(11, false);
    verifyDisplayOrderNonFullSupply(10);
    testWebDriver.getElementById("nextPageLink").click();
    assertEquals(testWebDriver.getElementById("productCode_0").getText(), "NF10");
    testWebDriver.getElementById("firstPageLink").click();
    verifyNumberOFPageLinksDisplayed(11, 10);
    verifyPreviousAndFirstPageLinksDisabled();
    verifyNextAndLastPageLinksEnabled();

    assertEquals(testWebDriver.getElementById("category_0").getText(), "Antibiotics");
    assertEquals(testWebDriver.getElementById("category_1").getText(), "");
    testWebDriver.getElementById("nextPageLink").click();
    assertEquals(testWebDriver.getElementById("category_0").getText(), "Antibiotics");
    verifyPageLinksFromLastPage();

    initiateRnRPage.submitRnR();

    dbWrapper.updateRequisitionStatus("AUTHORIZED", userSIC, "HIV");
    ViewRequisitionPage viewRequisitionPage = homePage.navigateViewRequisition();
    viewRequisitionPage.enterViewSearchCriteria();
    viewRequisitionPage.clickSearch();
    viewRequisitionPage.clickRnRList();

    verifyNumberOFPageLinksDisplayed(11, 10);
    verifyNextAndLastPageLinksEnabled();
    verifyPreviousAndFirstPageLinksDisabled();
    verifyDisplayOrderFullSupplyOnViewRequisition(10);
    testWebDriver.getElementById("nextPageLink").click();
    assertEquals(testWebDriver.getElementById("productCode_0").getText(), "F10");
    testWebDriver.getElementById("firstPageLink").click();

    testWebDriver.getElementByXpath("//a[contains(text(), '2') and @class='ng-binding']").click();
    assertEquals(testWebDriver.getElementById("category_0").getText(), "Antibiotics");
    verifyPageLinksFromLastPage();

    viewRequisitionPage.clickNonFullSupplyTab();

    verifyDisplayOrderNonFullSupplyOnViewRequisition(10);
    testWebDriver.getElementById("nextPageLink").click();
    assertEquals(testWebDriver.getElementById("productCode_0").getText(), "NF10");
    testWebDriver.getElementById("firstPageLink").click();

    verifyNumberOFPageLinksDisplayed(11, 10);
    verifyPreviousAndFirstPageLinksDisabled();
    verifyNextAndLastPageLinksEnabled();

    testWebDriver.getElementByXpath("//a[contains(text(), '2') and @class='ng-binding']").click();
    assertEquals(testWebDriver.getElementById("category_0").getText(), "Antibiotics");

    verifyPageLinksFromLastPage();
  }

  private void setupData(String program, String userSIC) throws SQLException {
    dbWrapper.insertFacilities("F10", "F11");
    dbWrapper.configureTemplate(program);
    List<String> rightsList = asList("CREATE_REQUISITION", "VIEW_REQUISITION");
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

  @Test(groups = {"requisition"}, dataProvider = "Data-Provider-Function-Positive")
  public void testProductDefaultDisplayOrder(String program, String userSIC, String password) throws SQLException {
    dbWrapper.setupMultipleProducts(program, "Lvl3 Hospital", 11, true);
    setupData(program, userSIC);

    HomePage homePage = loginPage.loginAs(userSIC, password);
    homePage.navigateAndInitiateRnr(program);
    InitiateRnRPage initiateRnRPage = homePage.clickProceed();

    verifyDefaultDisplayOrderFullSupply();
    assertEquals(testWebDriver.getElementById("category_0").getText(), "Antibiotics");
    testWebDriver.getElementById("nextPageLink").click();
    assertEquals(testWebDriver.getElementById("productCode_0").getText(), "F9");
    testWebDriver.getElementById("firstPageLink").click();
    assertEquals(testWebDriver.getElementById("category_0").getText(), "Antibiotics");

    initiateRnRPage.addMultipleNonFullSupplyLineItems(11, false);
    verifyDefaultDisplayOrderNonFullSupply();
    testWebDriver.getElementById("nextPageLink").click();
    assertEquals(testWebDriver.getElementById("productCode_0").getText(), "NF9");
    testWebDriver.getElementById("firstPageLink").click();
    assertEquals(testWebDriver.getElementById("category_0").getText(), "Antibiotics");
  }

  @Test(groups = {"requisition"}, dataProvider = "Data-Provider-Function-Positive")
  public void testCategoryDisplayOrder(String program, String userSIC, String password) throws SQLException {
    dbWrapper.setupMultipleCategoryProducts(program, "Lvl3 Hospital", 11, false);
    setupData(program, userSIC);

    HomePage homePage = loginPage.loginAs(userSIC, password);
    homePage.navigateAndInitiateRnr(program);
    InitiateRnRPage initiateRnRPage = homePage.clickProceed();

    verifyCategoryDisplayOrder(10);
    verifyDisplayOrderFullSupply(10);

    initiateRnRPage.addMultipleNonFullSupplyLineItems(11, true);
    verifyCategoryDisplayOrder(10);
    verifyDisplayOrderNonFullSupply(10);
  }

  @Test(groups = {"requisition"}, dataProvider = "Data-Provider-Function-Positive")
  public void testCategoryDefaultDisplayOrder(String program, String userSIC, String password) throws SQLException {
    dbWrapper.setupMultipleCategoryProducts(program, "Lvl3 Hospital", 11, true);
    setupData(program, userSIC);

    HomePage homePage = loginPage.loginAs(userSIC, password);
    homePage.navigateAndInitiateRnr(program);
    InitiateRnRPage initiateRnRPage = homePage.clickProceed();

    verifyCategoryDefaultDisplayOrderFullSupply();
    verifyDefaultDisplayOrderFullSupply();

    initiateRnRPage.addMultipleNonFullSupplyLineItems(11, true);
    verifyCategoryDefaultDisplayOrderNonFullSupply();
    verifyDefaultDisplayOrderNonFullSupply();
  }

  public void verifyDisplayOrderFullSupply(int numberOfLineItemsPerPage) {
    for (int i = 0; i < numberOfLineItemsPerPage; i++) {
      testWebDriver.waitForElementToAppear(testWebDriver.getElementByXpath(FULL_SUPPLY_BASE_LOCATOR + "/tbody[" + (i + 1) + "]/tr[2]/td[1]/ng-switch/span/ng-switch/span"));
      assertEquals(testWebDriver.getElementById("productCode_" + i).getText(), "F" + i);
    }
  }

  public void verifyDisplayOrderFullSupplyOnViewRequisition(int numberOfLineItemsPerPage) {
    for (int i = 0; i < numberOfLineItemsPerPage; i++) {
      testWebDriver.waitForElementToAppear(testWebDriver.getElementByXpath("//span[@id='productCode_" + i + "']"));
      assertEquals(testWebDriver.getElementByXpath("//span[@id='productCode_" + i + "']").getText(), "F" + i);
    }
  }

  public void verifyDisplayOrderNonFullSupply(int numberOfLineItemsPerPage) {
    for (int i = 0; i < numberOfLineItemsPerPage; i++) {
      testWebDriver.waitForElementToAppear(testWebDriver.getElementByXpath(NON_FULL_SUPPLY_BASE_LOCATOR + "/tbody[" + (i + 1) + "]/tr[2]/td[1]/ng-switch/span"));
      assertEquals(testWebDriver.getElementById("productCode_" + i).getText(), "NF" + i);
    }
  }

  public void verifyDisplayOrderNonFullSupplyOnViewRequisition(int numberOfLineItemsPerPage) {
    for (int i = 0; i < numberOfLineItemsPerPage; i++) {
      testWebDriver.waitForElementToAppear(testWebDriver.getElementByXpath("//span[@id='productCode_" + i + "']"));
      assertEquals(testWebDriver.getElementByXpath("//span[@id='productCode_" + i + "']").getText(), "NF" + i);
    }
  }

  public void verifyDefaultDisplayOrderFullSupply() {
    testWebDriver.waitForElementToAppear(testWebDriver.getElementById("productCode_0"));
    assertEquals(initiateRnRPage.getProductCode(0), "F0");
    assertEquals(initiateRnRPage.getProductCode(1), "F1");
    assertEquals(initiateRnRPage.getProductCode(2), "F10");
  }

  public void verifyDefaultDisplayOrderNonFullSupply() {
    testWebDriver.waitForElementToAppear(testWebDriver.getElementById("productCode_0"));
    assertEquals(initiateRnRPage.getProductCode(0), "NF0");
    assertEquals(initiateRnRPage.getProductCode(1), "NF1");
    assertEquals(initiateRnRPage.getProductCode(2), "NF10");
  }

  public void verifyCategoryDisplayOrder(int numberOfLineItems) {
    testWebDriver.waitForElementToAppear(testWebDriver.getElementById("category_0"));
    for (int i = 0; i < numberOfLineItems; i++) {
      assertEquals(initiateRnRPage.getCategoryText(i), "Antibiotics" + i);
    }
  }

  public void verifyCategoryDefaultDisplayOrderFullSupply() {
    testWebDriver.waitForElementToAppear(testWebDriver.getElementById("category_0"));
    assertEquals(initiateRnRPage.getCategoryText(0), "Antibiotics0");
    assertEquals(initiateRnRPage.getCategoryText(1), "Antibiotics1");
    assertEquals(initiateRnRPage.getCategoryText(2), "Antibiotics10");
  }

  public void verifyCategoryDefaultDisplayOrderNonFullSupply() {
    assertEquals(initiateRnRPage.getCategoryText(0), "Antibiotics0");
    assertEquals(initiateRnRPage.getCategoryText(1), "Antibiotics1");
    assertEquals(initiateRnRPage.getCategoryText(2), "Antibiotics10");

  }

  @AfterMethod(groups = {"requisition"})
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

