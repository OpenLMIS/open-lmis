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

import org.openlmis.UiUtils.TestCaseHelper;
import org.openlmis.pageobjects.*;
import org.openqa.selenium.WebElement;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.io.IOException;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.thoughtworks.selenium.SeleneseTestBase.assertFalse;
import static com.thoughtworks.selenium.SeleneseTestBase.assertTrue;
import static java.util.Arrays.asList;
import static org.testng.AssertJUnit.assertEquals;

public class PODPagination extends TestCaseHelper {

  public static final String USER = "user";
  public static final String PASSWORD = "password";
  public static final String PROGRAM = "program";

  public Map<String, String> podPaginationData = new HashMap<String, String>() {{
    put(USER, "storeInCharge");
    put(PASSWORD, "Admin123");
    put(PROGRAM, "HIV");
  }};

  UpdatePodPage updatePodPage;

  @BeforeMethod(groups = {"requisition"})
  public void setUp() throws SQLException, IOException, InterruptedException {
    super.setup();
    updatePodPage = PageFactory.getInstanceOfUpdatePodPage(testWebDriver);

    dbWrapper.insertFacilities("F10", "F11");
    dbWrapper.configureTemplate(podPaginationData.get(PROGRAM));
    List<String> rightsList = asList("VIEW_ORDER", "MANAGE_POD");

    setupTestUserRoleRightsData("200", podPaginationData.get(USER), rightsList);
    dbWrapper.insertSupervisoryNode("F10", "N1", "Node 1", "null");
    dbWrapper.insertRoleAssignment("200", "store in-charge");
    dbWrapper.insertSchedule("Q1stM", "QuarterMonthly", "QuarterMonth");
    dbWrapper.insertSchedule("M", "Monthly", "Month");
    dbWrapper.insertProcessingPeriod("Period1", "first period", "2012-12-01", "2013-01-15", 1, "Q1stM");
    dbWrapper.insertProcessingPeriod("Period2", "second period", "2013-01-16", "2013-01-30", 1, "M");
    setupRequisitionGroupData("RG1", "RG2", "N1", "N2", "F10", "F11");
    dbWrapper.insertSupplyLines("N1", podPaginationData.get(PROGRAM), "F10", true);
    dbWrapper.insertFulfilmentRoleAssignment("storeInCharge", "store in-charge", "F10");
  }

  @Test(groups = {"requisition"})
  public void testRnRPaginationAndDefaultDisplayOrder() throws SQLException {
    dbWrapper.setupMultipleProducts(podPaginationData.get(PROGRAM), "Lvl3 Hospital", 11, true);
    dbWrapper.insertRequisitionWithMultipleLineItems(11, podPaginationData.get(PROGRAM), true, "F10", false);
    dbWrapper.convertRequisitionToOrder(dbWrapper.getMaxRnrID(), "READY_TO_PACK", podPaginationData.get(USER));

    LoginPage loginPage = PageFactory.getInstanceOfLoginPage(testWebDriver, baseUrlGlobal);
    HomePage homePage = loginPage.loginAs(podPaginationData.get(USER), podPaginationData.get(PASSWORD));
    ManagePodPage managePodPage = homePage.navigateManagePOD();
    managePodPage.selectRequisitionToUpdatePod(1);
    verifyNumberOFPageLinksDisplayed(25, 10);
    verifyPageNumberLinksDisplayed();
    verifyPageNumberSelected(1);
    verifyNextAndLastPageLinksEnabled();
    verifyFirstAndPreviousPageLinksDisabled();
    verifyNumberOfProductsVisibleOnPage(10);
    verifyProductDisplayOrderOnPage(new String[]{"F0", "F1", "F10", "F2", "F3", "F4", "F5", "F6", "F7", "F8"});
    verifyCategoryDisplayOrderOnPage(new String[]{"C1", "", "", "", "", "", "", "", "", ""});

    navigateToPage(2);
    verifyPageNumberSelected(2);
    verifyNextAndLastPageLinksEnabled();
    verifyFirstAndPreviousPageLinksEnabled();
    verifyNumberOfProductsVisibleOnPage(10);
    verifyProductDisplayOrderOnPage(new String[]{"F9", "NF0", "NF1", "NF10", "NF2", "NF3", "NF4", "NF5", "NF6", "NF7"});
    verifyCategoryDisplayOrderOnPage(new String[]{"C1", "", "", "", "", "", "", "", "", ""});

    updatePodPage.navigateToNextPage();
    verifyPageNumberSelected(3);
    verifyNextAndLastPageLinksDisabled();
    verifyFirstAndPreviousPageLinksEnabled();
    verifyNumberOfProductsVisibleOnPage(2);
    verifyProductDisplayOrderOnPage(new String[]{"NF8", "NF9"});
    verifyCategoryDisplayOrderOnPage(new String[]{"C1", ""});

    updatePodPage.navigateToFirstPage();
    verifyPageNumberSelected(1);
    verifyNextAndLastPageLinksEnabled();
    verifyFirstAndPreviousPageLinksDisabled();
    verifyNumberOfProductsVisibleOnPage(10);

    updatePodPage.navigateToLastPage();
    verifyPageNumberSelected(3);
    verifyNextAndLastPageLinksDisabled();
    verifyFirstAndPreviousPageLinksEnabled();
    verifyNumberOfProductsVisibleOnPage(2);

    updatePodPage.navigateToPreviousPage();
    verifyPageNumberSelected(2);
    verifyNextAndLastPageLinksEnabled();
    verifyFirstAndPreviousPageLinksEnabled();
    verifyNumberOfProductsVisibleOnPage(10);
  }

  @Test(groups = {"requisition"})
  public void testRnRPaginationAndSpecificDisplayOrder() throws Exception {
    dbWrapper.setupMultipleProducts(podPaginationData.get(PROGRAM), "Lvl3 Hospital", 11, false);
    dbWrapper.insertRequisitionWithMultipleLineItems(11, podPaginationData.get(PROGRAM), true, "F10", false);
    dbWrapper.convertRequisitionToOrder(dbWrapper.getMaxRnrID(), "READY_TO_PACK", podPaginationData.get(USER));

    LoginPage loginPage = PageFactory.getInstanceOfLoginPage(testWebDriver, baseUrlGlobal);
    HomePage homePage = loginPage.loginAs(podPaginationData.get(USER), podPaginationData.get(PASSWORD));
    ManagePodPage managePodPage = homePage.navigateManagePOD();
    managePodPage.selectRequisitionToUpdatePod(1);
    verifyNumberOFPageLinksDisplayed(25, 10);
    verifyPageNumberLinksDisplayed();
    verifyProductDisplayOrderOnPage(new String[]{"F0", "NF0", "F1", "NF1", "F2", "NF2", "F3", "NF3", "F4", "NF4"});
    verifyCategoryDisplayOrderOnPage(new String[]{"C1", "", "", "", "", "", "", "", "", ""});

    navigateToPage(2);
    verifyProductDisplayOrderOnPage(new String[]{"F5", "NF5", "F6", "NF6", "F7", "NF7", "F8", "NF8", "F9", "NF9"});
    verifyCategoryDisplayOrderOnPage(new String[]{"C1", "", "", "", "", "", "", "", "", ""});

    updatePodPage.navigateToNextPage();
    verifyProductDisplayOrderOnPage(new String[]{"F10", "NF10"});
    verifyCategoryDisplayOrderOnPage(new String[]{"C1", ""});
  }

  @Test(groups = {"requisition"})
  public void testCategoryDefaultDisplayOrder() throws SQLException {
    dbWrapper.setupMultipleCategoryProducts(podPaginationData.get(PROGRAM), "Lvl3 Hospital", 11, true);
    dbWrapper.insertRequisitionWithMultipleLineItems(11, podPaginationData.get(PROGRAM), true, "F10", false);
    dbWrapper.convertRequisitionToOrder(dbWrapper.getMaxRnrID(), "READY_TO_PACK", podPaginationData.get(USER));

    LoginPage loginPage = PageFactory.getInstanceOfLoginPage(testWebDriver, baseUrlGlobal);
    HomePage homePage = loginPage.loginAs(podPaginationData.get(USER), podPaginationData.get(PASSWORD));
    ManagePodPage managePodPage = homePage.navigateManagePOD();
    managePodPage.selectRequisitionToUpdatePod(1);
    verifyNumberOFPageLinksDisplayed(25, 10);
    verifyPageNumberLinksDisplayed();
    verifyProductDisplayOrderOnPage(new String[]{"F0", "NF0", "F1", "NF1", "F10", "NF10", "F2", "NF2", "F3", "NF3"});
    verifyCategoryDisplayOrderOnPage(new String[]{"C0", "", "C1", "", "C10", "", "C2", "", "C3", ""});

    navigateToPage(2);
    verifyProductDisplayOrderOnPage(new String[]{"F4", "NF4", "F5", "NF5", "F6", "NF6", "F7", "NF7", "F8", "NF8"});
    verifyCategoryDisplayOrderOnPage(new String[]{"C4", "", "C5", "", "C6", "", "C7", "", "C8", ""});

    updatePodPage.navigateToNextPage();
    verifyProductDisplayOrderOnPage(new String[]{"F9", "NF9"});
    verifyCategoryDisplayOrderOnPage(new String[]{"C9", ""});
  }

  @Test(groups = {"requisition"})
  public void testCategorySpecificDisplayOrder() throws Exception {
    dbWrapper.setupMultipleCategoryProducts(podPaginationData.get(PROGRAM), "Lvl3 Hospital", 11, false);
    dbWrapper.insertRequisitionWithMultipleLineItems(11, podPaginationData.get(PROGRAM), true, "F10", false);
    dbWrapper.convertRequisitionToOrder(dbWrapper.getMaxRnrID(), "READY_TO_PACK", podPaginationData.get(USER));

    LoginPage loginPage = PageFactory.getInstanceOfLoginPage(testWebDriver, baseUrlGlobal);
    HomePage homePage = loginPage.loginAs(podPaginationData.get(USER), podPaginationData.get(PASSWORD));
    ManagePodPage managePodPage = homePage.navigateManagePOD();
    managePodPage.selectRequisitionToUpdatePod(1);
    verifyNumberOFPageLinksDisplayed(25, 10);
    verifyPageNumberLinksDisplayed();
    verifyProductDisplayOrderOnPage(new String[]{"F0", "NF0", "F1", "NF1", "F2", "NF2", "F3", "NF3", "F4", "NF4"});
    verifyCategoryDisplayOrderOnPage(new String[]{"C0", "", "C1", "", "C2", "", "C3", "", "C4", ""});

    navigateToPage(2);
    verifyProductDisplayOrderOnPage(new String[]{"F5", "NF5", "F6", "NF6", "F7", "NF7", "F8", "NF8", "F9", "NF9"});
    verifyCategoryDisplayOrderOnPage(new String[]{"C5", "", "C6", "", "C7", "", "C8", "", "C9", ""});

    updatePodPage.navigateToNextPage();
    verifyProductDisplayOrderOnPage(new String[]{"F10", "NF10"});
    verifyCategoryDisplayOrderOnPage(new String[]{"C10", ""});
  }

  @Test(groups = {"requisition"})
  public void testRnRPaginationAndDefaultDisplayOrderForPackedOrders() throws SQLException {
    dbWrapper.setupMultipleProducts(podPaginationData.get(PROGRAM), "Lvl3 Hospital", 11, true);
    dbWrapper.insertRequisitionWithMultipleLineItems(11, podPaginationData.get(PROGRAM), true, "F10", false);
    dbWrapper.convertRequisitionToOrder(dbWrapper.getMaxRnrID(), "READY_TO_PACK", podPaginationData.get(USER));

    enterTestDataForShipment();

    LoginPage loginPage = PageFactory.getInstanceOfLoginPage(testWebDriver, baseUrlGlobal);
    HomePage homePage = loginPage.loginAs(podPaginationData.get(USER), podPaginationData.get(PASSWORD));
    ManagePodPage managePodPage = homePage.navigateManagePOD();
    managePodPage.selectRequisitionToUpdatePod(1);
    verifyNumberOFPageLinksDisplayed(20, 10);
    verifyPageNumberLinksDisplayed();
    verifyPageNumberSelected(1);
    verifyNextAndLastPageLinksEnabled();
    verifyFirstAndPreviousPageLinksDisabled();
    verifyNumberOfProductsVisibleOnPage(10);
    verifyProductDisplayOrderOnPage(new String[]{"F0", "F1", "F2", "F3", "F4", "F5", "F6", "F7", "F8", "F9"});
    verifyCategoryDisplayOrderOnPage(new String[]{"Antibiotic", "", "", "", "", "", "", "", "", ""});
    updatePodPage.enterPodData("110", "openlmis openlmis", 1);
    updatePodPage.clickSave();
    assertTrue(updatePodPage.isPodSuccessMessageDisplayed());
    testWebDriver.refresh();
    updatePodPage.enterPodData("200", "openlmis openlmis", 5);
    updatePodPage.clickSave();
    assertTrue(updatePodPage.isPodSuccessMessageDisplayed());
    testWebDriver.refresh();

    navigateToPage(2);
    verifyPageNumberSelected(2);
    verifyFirstAndPreviousPageLinksEnabled();
    verifyNumberOfProductsVisibleOnPage(10);
    verifyProductDisplayOrderOnPage(new String[]{"NF0", "NF1", "NF2", "NF3", "NF4", "NF5", "NF6", "NF7", "NF8", "NF9"});
    verifyCategoryDisplayOrderOnPage(new String[]{"Antibiotic", "", "", "", "", "", "", "", "", ""});
    updatePodPage.enterPodData("10", "openlmis", 1);
    updatePodPage.clickSave();
    assertTrue(updatePodPage.isPodSuccessMessageDisplayed());
    testWebDriver.refresh();
    updatePodPage.enterPodData("11", "openlmis openlmis project", 10);
    updatePodPage.clickSave();
    assertTrue(updatePodPage.isPodSuccessMessageDisplayed());
    testWebDriver.refresh();

    updatePodPage.navigateToFirstPage();
    updatePodPage.verifyQuantityReceivedAndNotes("110", "openlmis openlmis", 1);
    verifyPodDataInDatabase("110", "openlmis openlmis", "F0");
    updatePodPage.verifyQuantityReceivedAndNotes("200", "openlmis openlmis", 5);
    verifyPodDataInDatabase("200", "openlmis openlmis", "F4");

    verifyPageNumberSelected(1);
    verifyNextAndLastPageLinksEnabled();
    verifyFirstAndPreviousPageLinksDisabled();
    verifyNumberOfProductsVisibleOnPage(10);

    homePage.navigateHomePage();
    homePage.navigateManagePOD();
    managePodPage.selectRequisitionToUpdatePod(1);
    navigateToPage(2);
    updatePodPage.verifyQuantityReceivedAndNotes("10", "openlmis", 1);
    verifyPodDataInDatabase("10", "openlmis", "NF0");
    updatePodPage.verifyQuantityReceivedAndNotes("11", "openlmis openlmis project", 10);
    verifyPodDataInDatabase("11", "openlmis openlmis project", "NF9");
  }

  @Test(groups = {"requisition"})
  public void testSubmitPod() throws SQLException {
    dbWrapper.setupMultipleProducts(podPaginationData.get(PROGRAM), "Lvl3 Hospital", 11, false);
    dbWrapper.insertRequisitionWithMultipleLineItems(11, podPaginationData.get(PROGRAM), true, "F10", false);
    dbWrapper.convertRequisitionToOrder(dbWrapper.getMaxRnrID(), "READY_TO_PACK", podPaginationData.get(USER));

    LoginPage loginPage = PageFactory.getInstanceOfLoginPage(testWebDriver, baseUrlGlobal);
    HomePage homePage = loginPage.loginAs(podPaginationData.get(USER), podPaginationData.get(PASSWORD));
    ManagePodPage managePodPage = homePage.navigateManagePOD();
    managePodPage.selectRequisitionToUpdatePod(1);

    for(int i=1; i<=10; i++) {
      updatePodPage.enterPodData("1" + i, "notes", i);
    }

    navigateToPage(2);
    for(int i=1; i<=10; i++) {
      updatePodPage.enterPodData("2" + i, "notes", i);
    }

    navigateToPage(3);
    updatePodPage.enterPodData("31", "notes", 1);
    updatePodPage.enterPodData("32", "notes", 2);

    updatePodPage.clickSubmitButton();
    updatePodPage.clickCancelButton();

    ViewOrdersPage viewOrdersPage = homePage.navigateViewOrders();
    assertEquals("Ready to pack", viewOrdersPage.getOrderStatus());

    homePage.navigateManagePOD();
    managePodPage.selectRequisitionToUpdatePod(1);
    updatePodPage.clickSubmitButton();
    updatePodPage.clickOkButton();
    assertTrue(updatePodPage.isPodSuccessMessageDisplayed());
    assertEquals("Proof of Delivery submitted successfully", updatePodPage.getPodSuccessMessage());

    assertFalse(updatePodPage.isQuantityReceivedEnabled(1));
    assertFalse(updatePodPage.isNotesEnabled(1));
    assertFalse(updatePodPage.isQuantityReceivedEnabled(10));
    assertFalse(updatePodPage.isNotesEnabled(10));

    navigateToPage(2);
    assertFalse(updatePodPage.isQuantityReceivedEnabled(1));
    assertFalse(updatePodPage.isNotesEnabled(1));
    assertFalse(updatePodPage.isQuantityReceivedEnabled(10));
    assertFalse(updatePodPage.isNotesEnabled(10));

    navigateToPage(3);
    assertFalse(updatePodPage.isQuantityReceivedEnabled(1));
    assertFalse(updatePodPage.isNotesEnabled(1));
    assertFalse(updatePodPage.isQuantityReceivedEnabled(2));
    assertFalse(updatePodPage.isNotesEnabled(2));

    homePage.navigateViewOrders();
    assertEquals("Received", viewOrdersPage.getOrderStatus());

    homePage.navigateManagePOD();
    managePodPage.verifyNoProductMessage();
  }

  @Test(groups = {"requisition"})
  public void testSubmitPodFail() throws SQLException {
    dbWrapper.setupMultipleProducts(podPaginationData.get(PROGRAM), "Lvl3 Hospital", 11, false);
    dbWrapper.insertRequisitionWithMultipleLineItems(11, podPaginationData.get(PROGRAM), true, "F10", false);
    dbWrapper.convertRequisitionToOrder(dbWrapper.getMaxRnrID(), "READY_TO_PACK", podPaginationData.get(USER));

    LoginPage loginPage = PageFactory.getInstanceOfLoginPage(testWebDriver, baseUrlGlobal);
    HomePage homePage = loginPage.loginAs(podPaginationData.get(USER), podPaginationData.get(PASSWORD));
    ManagePodPage managePodPage = homePage.navigateManagePOD();
    managePodPage.selectRequisitionToUpdatePod(1);

    for(int i=1; i<=10; i++) {
      updatePodPage.enterPodData("1" + i, "notes", i);
    }

    updatePodPage.clickSubmitButton();
    assertTrue(updatePodPage.isPodFailMessageDisplayed());
    assertEquals("Received quantity can not be blank", updatePodPage.getPodFailMessage());
    assertEquals("Errors found on 2 pages", updatePodPage.getPageErrorsMessage());
    assertTrue(updatePodPage.isQuantityReceivedEnabled(1));
    assertTrue(updatePodPage.isNotesEnabled(1));

    ViewOrdersPage viewOrdersPage = homePage.navigateViewOrders();
    assertEquals("Ready to pack", viewOrdersPage.getOrderStatus());

    homePage.navigateManagePOD();
    managePodPage.selectRequisitionToUpdatePod(1);
    updatePodPage.clickSubmitButton();
    updatePodPage.clickPageErrorsMessage();
    updatePodPage.clickErrorPage(2);

    verifyProductDisplayOrderOnPage(new String[]{"F5", "NF5", "F6", "NF6", "F7", "NF7", "F8", "NF8", "F9", "NF9"});
    for(int i=1; i<=10; i++) {
      updatePodPage.enterPodData("2" + i, "notes", i);
    }

    updatePodPage.clickPageErrorsMessage();
    updatePodPage.clickErrorPage(3);
    updatePodPage.enterPodData("31", "", 1);
    updatePodPage.enterPodData("32", "", 2);

    updatePodPage.clickSubmitButton();
    updatePodPage.clickOkButton();
    assertTrue(updatePodPage.isPodSuccessMessageDisplayed());

    assertFalse(updatePodPage.isQuantityReceivedEnabled(1));
    assertFalse(updatePodPage.isNotesEnabled(1));

    homePage.navigateViewOrders();
    assertEquals("Received", viewOrdersPage.getOrderStatus());

    homePage.navigateManagePOD();
    managePodPage.verifyNoProductMessage();
  }

  private void enterTestDataForShipment() throws SQLException {
    for (Integer i = 0; i < 10; i++)
      testDataForShipment(0, true, "F" + i, i);
    for (Integer i = 0; i < 10; i++)
      testDataForShipment(0, true, "NF" + i, i);
    dbWrapper.updateFieldValue("orders", "status", "PACKED", null, null);
  }

  private void verifyNextAndLastPageLinksDisabled() {
    assertFalse(updatePodPage.isNextPageLinkEnabled());
    assertFalse(updatePodPage.isLastPageLinkEnabled());
  }

  private void verifyFirstAndPreviousPageLinksEnabled() {
    assertTrue(updatePodPage.isFirstPageLinkEnabled());
    assertTrue(updatePodPage.isPreviousPageLinkEnabled());
  }

  private void navigateToPage(int pageNumber) {
    WebElement page = testWebDriver.getElementById(String.valueOf(pageNumber));
    testWebDriver.waitForElementToAppear(page);
    page.click();
  }

  private void verifyNumberOfProductsVisibleOnPage(int numberOfProducts) {
    assertEquals(numberOfProducts, testWebDriver.getElementsSizeByXpath("//table[@id='podTable']/tbody"));
  }

  private void verifyFirstAndPreviousPageLinksDisabled() {
    assertFalse(updatePodPage.isFirstPageLinkEnabled());
    assertFalse(updatePodPage.isPreviousPageLinkEnabled());
  }

  private void verifyNextAndLastPageLinksEnabled() {
    assertTrue(updatePodPage.isNextPageLinkEnabled());
    assertTrue(updatePodPage.isLastPageLinkEnabled());
  }

  private void verifyPageNumberSelected(int pageNumber) {
    WebElement page = testWebDriver.getElementById(String.valueOf(pageNumber));
    testWebDriver.waitForElementToAppear(page);
    assertEquals("rgba(96, 172, 175, 1)", page.getCssValue("background-color"));
  }

  private void verifyPageNumberLinksDisplayed() {
    assertTrue(updatePodPage.isFirstPageLinkDisplayed());
    assertTrue(updatePodPage.isPreviousPageLinkDisplayed());
    assertTrue(updatePodPage.isNextPageLinkDisplayed());
    assertTrue(updatePodPage.isLastPageLinkDisplayed());
  }

  private void verifyNumberOFPageLinksDisplayed(int numberOfProducts, int numberOfLineItemsPerPage) {
    testWebDriver.waitForAjax();
    int numberOfPages = numberOfProducts / numberOfLineItemsPerPage;
    if (numberOfProducts % numberOfLineItemsPerPage != 0) {
      numberOfPages = numberOfPages + 1;
    }
    for (int i = 1; i <= numberOfPages; i++) {
      WebElement page = testWebDriver.getElementById(String.valueOf(i));
      testWebDriver.waitForElementToAppear(page);
      assertTrue(page.isDisplayed());
    }
  }

  private void verifyProductDisplayOrderOnPage(String[] productCodes) {
    for (int i = 1; i < productCodes.length; i++) {
      assertEquals(productCodes[i - 1], updatePodPage.getProductCode(i));
    }
  }

  private void verifyCategoryDisplayOrderOnPage(String[] categoryCodes) {
    for (int i = 1; i < categoryCodes.length; i++) {
      assertEquals(categoryCodes[i - 1], updatePodPage.getCategoryName(i));
    }
  }

  @AfterMethod(groups = {"requisition"})
  public void tearDown() throws SQLException {
    HomePage homePage = PageFactory.getInstanceOfHomePage(testWebDriver);
    homePage.logout(baseUrlGlobal);
    dbWrapper.deleteData();
    dbWrapper.closeConnection();
  }
}
