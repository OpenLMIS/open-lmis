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
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

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

  @BeforeMethod(groups = {"orderAndPod"})
  public void setUp() throws Exception {
    super.setup();
    updatePodPage = PageObjectFactory.getUpdatePodPage(testWebDriver);

    dbWrapper.insertFacilities("F10", "F11");
    dbWrapper.configureTemplate(podPaginationData.get(PROGRAM));
    List<String> rightsList = asList("VIEW_ORDER", "MANAGE_POD");

    setupTestUserRoleRightsData(podPaginationData.get(USER), rightsList);
    dbWrapper.insertSupervisoryNode("F10", "N1", "Node 1", "null");
    dbWrapper.insertRoleAssignment(podPaginationData.get(USER), "store in-charge");
    dbWrapper.insertSchedule("Q1stM", "QuarterMonthly", "QuarterMonth");
    dbWrapper.insertSchedule("M", "Monthly", "Month");
    dbWrapper.insertProcessingPeriod("Period1", "first period", "2012-12-01", "2013-01-15", 1, "Q1stM");
    dbWrapper.insertProcessingPeriod("Period2", "second period", "2013-01-16", "2013-01-30", 1, "M");
    setupRequisitionGroupData("RG1", "RG2", "N1", "N2", "F10", "F11");
    dbWrapper.insertSupplyLines("N1", podPaginationData.get(PROGRAM), "F10", true);
    dbWrapper.insertFulfilmentRoleAssignment(podPaginationData.get(USER), "store in-charge", "F10");
  }

  @Test(groups = {"orderAndPod"})
  public void testRnRPaginationAndDefaultDisplayOrder() throws SQLException {
    dbWrapper.setupMultipleProducts(podPaginationData.get(PROGRAM), "Lvl3 Hospital", 11, true);
    dbWrapper.insertProgramProductsWithCategory("F5", "TB", "C1", null);
    dbWrapper.insertProgramProductsWithCategory("NF5", "TB", "C1", -5);
    dbWrapper.insertProgramProduct("F1", "TB", "30", "f");
    dbWrapper.insertRequisitionWithMultipleLineItems(11, podPaginationData.get(PROGRAM), true, "F10", false);
    dbWrapper.convertRequisitionToOrder(dbWrapper.getMaxRnrID(), "READY_TO_PACK", podPaginationData.get(USER));

    LoginPage loginPage = PageObjectFactory.getLoginPage(testWebDriver, baseUrlGlobal);
    HomePage homePage = loginPage.loginAs(podPaginationData.get(USER), podPaginationData.get(PASSWORD));
    ManagePodPage managePodPage = homePage.navigateManagePOD();
    managePodPage.selectRequisitionToUpdatePod(1);
    verifyNumberOFPageLinksDisplayed(25, 10);
    verifyPageNumberLinksDisplayed();
    verifyPageNumberSelected(1);
    verifyNextAndLastPageLinksEnabled();
    verifyPreviousAndFirstPageLinksDisabled();
    verifyNumberOfLineItemsVisibleOnPage(10);
    verifyProductDisplayOrderOnPage(new String[]{"F0", "F1", "F10", "F2", "F3", "F4", "F5", "F6", "F7", "F8"});
    verifyCategoryDisplayOrderOnPage(new String[]{"C1", "", "", "", "", "", "", "", "", ""});

    navigateToPage(2);
    verifyPageNumberSelected(2);
    verifyNextAndLastPageLinksEnabled();
    verifyPreviousAndFirstPageLinksEnabled();
    verifyNumberOfLineItemsVisibleOnPage(10);
    verifyProductDisplayOrderOnPage(new String[]{"F9", "NF0", "NF1", "NF10", "NF2", "NF3", "NF4", "NF5", "NF6", "NF7"});
    verifyCategoryDisplayOrderOnPage(new String[]{"C1", "", "", "", "", "", "", "", "", ""});

    navigateToNextPage();
    verifyPageNumberSelected(3);
    verifyNextAndLastPageLinksDisabled();
    verifyPreviousAndFirstPageLinksEnabled();
    verifyNumberOfLineItemsVisibleOnPage(2);
    verifyProductDisplayOrderOnPage(new String[]{"NF8", "NF9"});
    verifyCategoryDisplayOrderOnPage(new String[]{"C1", ""});

    navigateToFirstPage();
    verifyPageNumberSelected(1);
    verifyNextAndLastPageLinksEnabled();
    verifyPreviousAndFirstPageLinksDisabled();
    verifyNumberOfLineItemsVisibleOnPage(10);

    navigateToLastPage();
    verifyPageNumberSelected(3);
    verifyNextAndLastPageLinksDisabled();
    verifyPreviousAndFirstPageLinksEnabled();
    verifyNumberOfLineItemsVisibleOnPage(2);

    navigateToPreviousPage();
    verifyPageNumberSelected(2);
    verifyNextAndLastPageLinksEnabled();
    verifyPreviousAndFirstPageLinksEnabled();
    verifyNumberOfLineItemsVisibleOnPage(10);
  }

  @Test(groups = {"orderAndPod"})
  public void testRnRPaginationAndSpecificDisplayOrder() throws SQLException {
    dbWrapper.setupMultipleProducts(podPaginationData.get(PROGRAM), "Lvl3 Hospital", 11, false);
    dbWrapper.insertRequisitionWithMultipleLineItems(11, podPaginationData.get(PROGRAM), true, "F10", false);
    dbWrapper.convertRequisitionToOrder(dbWrapper.getMaxRnrID(), "READY_TO_PACK", podPaginationData.get(USER));

    LoginPage loginPage = PageObjectFactory.getLoginPage(testWebDriver, baseUrlGlobal);
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

    navigateToNextPage();
    verifyProductDisplayOrderOnPage(new String[]{"F10", "NF10"});
    verifyCategoryDisplayOrderOnPage(new String[]{"C1", ""});
  }

  @Test(groups = {"orderAndPod"})
  public void testCategoryDefaultDisplayOrder() throws SQLException {
    dbWrapper.setupMultipleCategoryProducts(podPaginationData.get(PROGRAM), "Lvl3 Hospital", 11, true);
    dbWrapper.insertRequisitionWithMultipleLineItems(11, podPaginationData.get(PROGRAM), true, "F10", false);
    dbWrapper.convertRequisitionToOrder(dbWrapper.getMaxRnrID(), "READY_TO_PACK", podPaginationData.get(USER));

    LoginPage loginPage = PageObjectFactory.getLoginPage(testWebDriver, baseUrlGlobal);
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

    navigateToNextPage();
    verifyProductDisplayOrderOnPage(new String[]{"F9", "NF9"});
    verifyCategoryDisplayOrderOnPage(new String[]{"C9", ""});
  }

  @Test(groups = {"orderAndPod"})
  public void testCategorySpecificDisplayOrder() throws SQLException {
    dbWrapper.setupMultipleCategoryProducts(podPaginationData.get(PROGRAM), "Lvl3 Hospital", 11, false);
    dbWrapper.insertProgramProductsWithCategory("F5", "TB", "C3", null);
    dbWrapper.insertProgramProductsWithCategory("NF5", "TB", "C2", -5);
    dbWrapper.insertProgramProduct("F1", "TB", "30", "f");
    dbWrapper.insertRequisitionWithMultipleLineItems(11, podPaginationData.get(PROGRAM), true, "F10", false);
    dbWrapper.convertRequisitionToOrder(dbWrapper.getMaxRnrID(), "READY_TO_PACK", podPaginationData.get(USER));

    LoginPage loginPage = PageObjectFactory.getLoginPage(testWebDriver, baseUrlGlobal);
    HomePage homePage = loginPage.loginAs(podPaginationData.get(USER), podPaginationData.get(PASSWORD));
    ManagePodPage managePodPage = homePage.navigateManagePOD();
    updatePodPage = managePodPage.selectRequisitionToUpdatePod(1);
    verifyNumberOFPageLinksDisplayed(25, 10);
    verifyPageNumberLinksDisplayed();
    for (int rowNumber = 1; rowNumber < 5; rowNumber++) {
      assertEqualsAndNulls(updatePodPage.getReplacedProductCode(rowNumber), "");
    }
    verifyProductDisplayOrderOnPage(new String[]{"F0", "NF0", "F1", "NF1", "F2", "NF2", "F3", "NF3", "F4", "NF4"});
    verifyCategoryDisplayOrderOnPage(new String[]{"C0", "", "C1", "", "C2", "", "C3", "", "C4", ""});

    navigateToPage(2);
    verifyProductDisplayOrderOnPage(new String[]{"F5", "NF5", "F6", "NF6", "F7", "NF7", "F8", "NF8", "F9", "NF9"});
    verifyCategoryDisplayOrderOnPage(new String[]{"C5", "", "C6", "", "C7", "", "C8", "", "C9", ""});

    navigateToNextPage();
    verifyProductDisplayOrderOnPage(new String[]{"F10", "NF10"});
    verifyCategoryDisplayOrderOnPage(new String[]{"C10", ""});
  }

  @Test(groups = {"orderAndPod"})
  public void testRnRPaginationAndDefaultDisplayOrderForPackedOrdersAndSave() throws SQLException {
    dbWrapper.setupMultipleProducts(podPaginationData.get(PROGRAM), "Lvl3 Hospital", 11, true);
    dbWrapper.insertProgramProductsWithCategory("F5", "TB", "C1", null);
    dbWrapper.insertProgramProductsWithCategory("NF5", "TB", "C1", -5);
    dbWrapper.insertProgramProduct("F1", "TB", "30", "f");
    dbWrapper.insertRequisitionWithMultipleLineItems(11, podPaginationData.get(PROGRAM), true, "F10", false);
    dbWrapper.convertRequisitionToOrder(dbWrapper.getMaxRnrID(), "READY_TO_PACK", podPaginationData.get(USER));
    dbWrapper.insertOneProduct("ZX");
    dbWrapper.insertOneProduct("ZX1");

    dbWrapper.insertProgramProductsWithoutDeleting("ZX", "ZX1", podPaginationData.get(PROGRAM));
    dbWrapper.insertFacilityApprovedProduct("ZX", podPaginationData.get(PROGRAM), dbWrapper.getAttributeFromTable("facility_types", "code", "name", "Lvl3 Hospital"));
    dbWrapper.insertFacilityApprovedProduct("ZX1", podPaginationData.get(PROGRAM), dbWrapper.getAttributeFromTable("facility_types", "code", "name", "Lvl3 Hospital"));

    enterTestDataForShipment(true, true);

    LoginPage loginPage = PageObjectFactory.getLoginPage(testWebDriver, baseUrlGlobal);
    HomePage homePage = loginPage.loginAs(podPaginationData.get(USER), podPaginationData.get(PASSWORD));
    ManagePodPage managePodPage = homePage.navigateManagePOD();
    managePodPage.selectRequisitionToUpdatePod(1);
    verifyNumberOFPageLinksDisplayed(22, 10);
    verifyPageNumberLinksDisplayed();
    verifyPageNumberSelected(1);
    verifyNextAndLastPageLinksEnabled();
    verifyPreviousAndFirstPageLinksDisabled();
    verifyNumberOfLineItemsVisibleOnPage(10);
    verifyProductDisplayOrderOnPage(new String[]{"F0", "F1", "F2", "F3", "F4", "F5", "F6", "F7", "F8", "F9"});
    assertEquals(updatePodPage.getReplacedProductCode(1), "NF0");
    assertEquals(updatePodPage.getReplacedProductCode(10), "NF9");
    verifyCategoryDisplayOrderOnPage(new String[]{"Antibiotics", "", "", "", "", "", "", "", "", ""});

    updatePodPage.enterPodData("110", "openlmis openlmis", null, 1);
    updatePodPage.enterDeliveryDetailsInPodScreen("Delivered Person", "Received Person", "27/02/2014");
    updatePodPage.clickSave();

    assertTrue(updatePodPage.isPodSuccessMessageDisplayed());
    testWebDriver.refresh();

    verifyPodDataInDatabase("110", "openlmis openlmis", "F0", null);
    updatePodPage.verifyDeliveryDetailsOnPodScreenUI("Delivered Person", "Received Person", "27/02/2014");
    verifyDeliveryDetailsOfPodScreenInDatabase("Delivered Person", "Received Person", "2014-02-27 00:00:00");

    updatePodPage.enterDeliveryDetailsInPodScreen("Delivered Person new", " ", "25/02/2014");
    updatePodPage.enterPodData("200", "openlmis openlmis", "65", 5);
    updatePodPage.clickSave();
    assertTrue(updatePodPage.isPodSuccessMessageDisplayed());
    testWebDriver.refresh();
    verifyPodDataInDatabase("200", "openlmis openlmis", "F4", "65");
    updatePodPage.verifyDeliveryDetailsOnPodScreenUI("Delivered Person new", " ", "25/02/2014");
    verifyDeliveryDetailsOfPodScreenInDatabase("Delivered Person new", " ", "2014-02-25 00:00:00");

    updatePodPage.enterDeliveryDetailsInPodScreen("Delivered Person new openLMIS", " ", "25/02/2014");
    navigateToPage(2);
    verifyPageNumberSelected(2);
    verifyPreviousAndFirstPageLinksEnabled();
    verifyNumberOfLineItemsVisibleOnPage(10);
    verifyProductDisplayOrderOnPage(new String[]{"NF0", "NF1", "NF2", "NF3", "NF4", "NF5", "NF6", "NF7", "NF8", "NF9"});
    assertEquals(updatePodPage.getReplacedProductCode(1), "");
    assertEquals(updatePodPage.getReplacedProductCode(10), "");
    verifyCategoryDisplayOrderOnPage(new String[]{"Antibiotics", "", "", "", "", "", "", "", "", ""});
    updatePodPage.verifyDeliveryDetailsOnPodScreenUI("Delivered Person new openLMIS", " ", "25/02/2014");
    verifyDeliveryDetailsOfPodScreenInDatabase("Delivered Person new openLMIS", " ", "2014-02-25 00:00:00");
    updatePodPage.enterPodData("10", "openlmis", "7", 1);
    updatePodPage.clickSave();
    testWebDriver.sleep(500);
    assertTrue(updatePodPage.isPodSuccessMessageDisplayed());
    testWebDriver.refresh();
    updatePodPage.enterPodData("5", "openlmis openlmis", null, 1);
    updatePodPage.enterPodData("11", "openlmis openlmis project", "99999999", 10);

    navigateToLastPage();
    verifyPageNumberSelected(3);
    verifyNextAndLastPageLinksDisabled();
    verifyPreviousAndFirstPageLinksEnabled();
    verifyNumberOfLineItemsVisibleOnPage(2);
    verifyProductDisplayOrderOnPage(new String[]{"ZX", "ZX1"});
    assertEquals(updatePodPage.getReplacedProductCode(1), "");
    assertEquals(updatePodPage.getReplacedProductCode(2), "");
    verifyCategoryDisplayOrderOnPage(new String[]{"Antibiotics", ""});
    updatePodPage.verifyQuantityReturnedOnUI("", 1);
    updatePodPage.enterPodData("11", "some notes", "99999999", 1);
    updatePodPage.enterPodData("110", "Notes", null, 2);
    updatePodPage.enterDeliveryDetailsInPodScreen("Delivered", "Received by facility incharge", "25/02/2013");

    navigateToFirstPage();
    verifyPageNumberSelected(1);
    verifyNextAndLastPageLinksEnabled();
    verifyPreviousAndFirstPageLinksDisabled();
    verifyNumberOfLineItemsVisibleOnPage(10);
    updatePodPage.verifyQuantityReturnedOnUI("", 10);
    updatePodPage.verifyDeliveryDetailsOnPodScreenUI("Delivered", "Received by facility incharge", "25/02/2013");

    verifyPodDataInDatabase("11", "some notes", "ZX", "99999999");
    verifyPodDataInDatabase("110", "Notes", "ZX1", null);

    verifyPodDataInDatabase("5", "openlmis openlmis", "NF0", null);
    updatePodPage.verifyQuantityReceivedAndNotes("110", "openlmis openlmis", 1);
    verifyPodDataInDatabase("110", "openlmis openlmis", "F0", null);
    updatePodPage.verifyQuantityReceivedAndNotes("200", "openlmis openlmis", 5);
    verifyPodDataInDatabase("200", "openlmis openlmis", "F4", "65");

    homePage.navigateHomePage();
    homePage.navigateManagePOD();
    managePodPage.selectRequisitionToUpdatePod(1);
    navigateToPage(2);
    updatePodPage.verifyQuantityReceivedAndNotes("5", "openlmis openlmis", 1);
    updatePodPage.verifyQuantityReturnedOnUI("", 1);
    updatePodPage.verifyQuantityReturnedOnUI("99999999", 10);
    verifyPodDataInDatabase("5", "openlmis openlmis", "NF0", null);
    updatePodPage.verifyQuantityReceivedAndNotes("11", "openlmis openlmis project", 10);
    verifyPodDataInDatabase("11", "openlmis openlmis project", "NF9", "99999999");
    verifyDeliveryDetailsOfPodScreenInDatabase("Delivered", "Received by facility incharge", "2013-02-25 00:00:00");
  }

  @Test(groups = {"orderAndPod"})
  public void testCategorySpecificDisplayOrderForPackedOrder() throws SQLException {
    dbWrapper.setupMultipleCategoryProducts(podPaginationData.get(PROGRAM), "Lvl3 Hospital", 11, false);
    dbWrapper.insertProgramProductsWithCategory("F5", "TB", "C3", null);
    dbWrapper.insertProgramProductsWithCategory("NF5", "TB", "C2", -5);
    dbWrapper.insertProgramProduct("F1", "TB", "30", "f");
    dbWrapper.insertRequisitionWithMultipleLineItems(11, podPaginationData.get(PROGRAM), true, "F10", false);
    dbWrapper.convertRequisitionToOrder(dbWrapper.getMaxRnrID(), "READY_TO_PACK", podPaginationData.get(USER));
    dbWrapper.insertOneProduct("ZX");
    dbWrapper.insertOneProduct("ZX1");

    dbWrapper.insertProgramProductsWithoutDeleting("ZX", "ZX1", podPaginationData.get(PROGRAM));
    dbWrapper.insertFacilityApprovedProduct("ZX", podPaginationData.get(PROGRAM), dbWrapper.getAttributeFromTable("facility_types", "code", "name", "Lvl3 Hospital"));
    dbWrapper.insertFacilityApprovedProduct("ZX1", podPaginationData.get(PROGRAM), dbWrapper.getAttributeFromTable("facility_types", "code", "name", "Lvl3 Hospital"));

    enterTestDataForShipment(true, true);

    LoginPage loginPage = PageObjectFactory.getLoginPage(testWebDriver, baseUrlGlobal);
    HomePage homePage = loginPage.loginAs(podPaginationData.get(USER), podPaginationData.get(PASSWORD));
    ManagePodPage managePodPage = homePage.navigateManagePOD();
    updatePodPage = managePodPage.selectRequisitionToUpdatePod(1);
    verifyNumberOFPageLinksDisplayed(25, 10);
    verifyPageNumberLinksDisplayed();
    assertEquals(updatePodPage.getReplacedProductCode(1), "NF0");
    assertEquals(updatePodPage.getReplacedProductCode(9), "NF3");
    assertEquals(updatePodPage.getReplacedProductCode(10), "");

    verifyProductDisplayOrderOnPage(new String[]{"F0", "NF0", "F1", "NF1", "ZX", "ZX1", "F2", "NF2", "F3", "NF3"});
    verifyCategoryDisplayOrderOnPage(new String[]{"Antibiotics0", "", "Antibiotics1", "", "", "", "Antibiotics2", "", "Antibiotics3", ""});

    navigateToPage(2);
    verifyProductDisplayOrderOnPage(new String[]{"F4", "NF4", "F5", "NF5", "F6", "NF6", "F7", "NF7", "F8", "NF8"});
    verifyCategoryDisplayOrderOnPage(new String[]{"Antibiotics4", "", "Antibiotics5", "", "Antibiotics6", "", "Antibiotics7", "", "Antibiotics8", ""});

    navigateToNextPage();
    verifyProductDisplayOrderOnPage(new String[]{"F9", "NF9"});
    verifyCategoryDisplayOrderOnPage(new String[]{"Antibiotics9", ""});
  }

  @Test(groups = {"orderAndPod"})
  public void testDisplayOrderAndCategoryForProductsNotSupportedByProgram() throws SQLException {
    dbWrapper.setupMultipleCategoryProducts(podPaginationData.get(PROGRAM), "Lvl3 Hospital", 11, true);
    dbWrapper.insertRequisitionWithMultipleLineItems(9, podPaginationData.get(PROGRAM), true, "F10", false);
    dbWrapper.convertRequisitionToOrder(dbWrapper.getMaxRnrID(), "READY_TO_PACK", podPaginationData.get(USER));
    dbWrapper.insertProduct("R1", "R1");
    dbWrapper.insertProduct("A1", "A1");
    dbWrapper.insertProduct("O1", "O1");

    dbWrapper.insertProgramProductsWithCategory("R1", "MALARIA", "C1", 5);
    dbWrapper.insertProgramProductsWithCategory("O1", "MALARIA", "C5", 10);

    dbWrapper.updateFieldValue("orders", "status", "RELEASED", null, null);
    testDataForShipment(3, true, "F1", 78);
    testDataForShipment(4, true, "F2", 785);
    testDataForShipment(6, false, "NF1", 378);
    testDataForShipment(8, false, "NF2", 678);
    testDataForShipment(6, true, "R1", 278);
    testDataForShipment(6, false, "O1", 1378);
    testDataForShipment(4, true, "F10", 478);
    testDataForShipment(4, true, "F9", 378);
    testDataForShipment(56, false, "NF10", 478);
    dbWrapper.updateFieldValue("orders", "status", "PACKED", null, null);

    LoginPage loginPage = PageObjectFactory.getLoginPage(testWebDriver, baseUrlGlobal);
    HomePage homePage = loginPage.loginAs(podPaginationData.get(USER), podPaginationData.get(PASSWORD));
    ManagePodPage managePodPage = homePage.navigateManagePOD();
    managePodPage.selectRequisitionToUpdatePod(1);
    verifyNumberOFPageLinksDisplayed(9, 10);
    verifyPageNumberLinksDisplayed();
    verifyProductDisplayOrderOnPage(new String[]{"F1", "NF1", "F10", "NF10", "F2", "NF2", "F9", "O1", "R1"});
    verifyCategoryDisplayOrderOnPage(new String[]{"Antibiotics1", "", "Antibiotics10", "", "Antibiotics2", "", "Antibiotics9"});
    assertEquals("Other", testWebDriver.getElementById("category").getText());
  }

  @Test(groups = {"orderAndPod"})
  public void testSubmitPod() throws SQLException {
    dbWrapper.setupMultipleProducts(podPaginationData.get(PROGRAM), "Lvl3 Hospital", 11, false);
    dbWrapper.insertRequisitionWithMultipleLineItems(11, podPaginationData.get(PROGRAM), true, "F10", false);
    dbWrapper.convertRequisitionToOrder(dbWrapper.getMaxRnrID(), "READY_TO_PACK", podPaginationData.get(USER));

    LoginPage loginPage = PageObjectFactory.getLoginPage(testWebDriver, baseUrlGlobal);
    HomePage homePage = loginPage.loginAs(podPaginationData.get(USER), podPaginationData.get(PASSWORD));
    ManagePodPage managePodPage = homePage.navigateManagePOD();
    managePodPage.selectRequisitionToUpdatePod(1);

    for (int i = 1; i <= 10; i++) {
      updatePodPage.enterPodData("1" + i, "notes", null, i);
    }

    navigateToPage(2);
    for (int i = 1; i <= 10; i++) {
      updatePodPage.enterPodData("2" + i, "notes", "56", i);
    }

    navigateToPage(3);
    updatePodPage.enterPodData("31", "notes", null, 1);
    updatePodPage.enterPodData("32", "notes", null, 2);
    updatePodPage.enterDeliveryDetailsInPodScreen("openlmis", "facility incharge", "27/02/2014");
    updatePodPage.clickSubmitButton();
    updatePodPage.clickCancelButton();

    ViewOrdersPage viewOrdersPage = homePage.navigateViewOrders();
    assertEquals("Ready to pack", viewOrdersPage.getOrderStatus(1));

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
    assertFalse(updatePodPage.isDeliveryByFieldEnabled());
    assertFalse(updatePodPage.isReceivedByFieldEnabled());
    assertFalse(updatePodPage.isReceivedDateFieldEnabled());

    navigateToPage(2);
    assertFalse(updatePodPage.isQuantityReceivedEnabled(1));
    assertFalse(updatePodPage.isNotesEnabled(1));
    assertFalse(updatePodPage.isQuantityReceivedEnabled(10));
    assertFalse(updatePodPage.isNotesEnabled(10));
    assertFalse(updatePodPage.isDeliveryByFieldEnabled());
    assertFalse(updatePodPage.isReceivedByFieldEnabled());
    assertFalse(updatePodPage.isReceivedDateFieldEnabled());

    navigateToPage(3);
    assertFalse(updatePodPage.isQuantityReceivedEnabled(1));
    assertFalse(updatePodPage.isNotesEnabled(1));
    assertFalse(updatePodPage.isQuantityReceivedEnabled(2));
    assertFalse(updatePodPage.isNotesEnabled(2));
    assertFalse(updatePodPage.isDeliveryByFieldEnabled());
    assertFalse(updatePodPage.isReceivedByFieldEnabled());
    assertFalse(updatePodPage.isReceivedDateFieldEnabled());

    homePage.navigateViewOrders();
    assertEquals("Received", viewOrdersPage.getOrderStatus(1));

    homePage.navigateManagePOD();
    managePodPage.verifyNoOrderMessage();
    verifyDeliveryDetailsOfPodScreenInDatabase("openlmis", "facility incharge", "2014-02-27 00:00:00");

  }

  @Test(groups = {"orderAndPod"})
  public void testSubmitPodFail() throws SQLException {
    dbWrapper.setupMultipleProducts(podPaginationData.get(PROGRAM), "Lvl3 Hospital", 11, false);
    dbWrapper.insertRequisitionWithMultipleLineItems(11, podPaginationData.get(PROGRAM), true, "F10", false);
    dbWrapper.convertRequisitionToOrder(dbWrapper.getMaxRnrID(), "READY_TO_PACK", podPaginationData.get(USER));
    dbWrapper.insertOneProduct("ZX");
    dbWrapper.insertOneProduct("ZX1");

    dbWrapper.insertProgramProductsWithoutDeleting("ZX", "ZX1", podPaginationData.get(PROGRAM));
    dbWrapper.insertFacilityApprovedProduct("ZX", podPaginationData.get(PROGRAM), dbWrapper.getAttributeFromTable("facility_types", "code", "name", "Lvl3 Hospital"));
    dbWrapper.insertFacilityApprovedProduct("ZX1", podPaginationData.get(PROGRAM), dbWrapper.getAttributeFromTable("facility_types", "code", "name", "Lvl3 Hospital"));

    enterTestDataForShipment(true, false);

    LoginPage loginPage = PageObjectFactory.getLoginPage(testWebDriver, baseUrlGlobal);
    HomePage homePage = loginPage.loginAs(podPaginationData.get(USER), podPaginationData.get(PASSWORD));
    ManagePodPage managePodPage = homePage.navigateManagePOD();
    managePodPage.selectRequisitionToUpdatePod(1);

    verifyProductDisplayOrderOnPage(new String[]{"F0", "NF0", "F1", "NF1", "ZX", "F2", "NF2", "F3", "NF3", "F4"});
    assertEquals(updatePodPage.getReplacedProductCode(1), "NF0");
    assertEquals(updatePodPage.getReplacedProductCode(2), "");
    assertEquals(updatePodPage.getReplacedProductCode(10), "NF4");
    verifyCategoryDisplayOrderOnPage(new String[]{"Antibiotics", "", "", "", "", "", "", "", "", ""});

    for (int i = 1; i <= 10; i++) {
      updatePodPage.enterPodData("1" + i, "notes", null, i);
    }

    updatePodPage.clickSubmitButton();
    assertTrue(updatePodPage.isPodFailMessageDisplayed());
    assertEquals("Received quantity can not be blank", updatePodPage.getPodFailMessage());
    assertEquals("Errors found on 2 pages", updatePodPage.getPageErrorsMessage());
    assertTrue(updatePodPage.isQuantityReceivedEnabled(1));
    assertTrue(updatePodPage.isNotesEnabled(1));
    assertTrue(updatePodPage.isDeliveryByFieldEnabled());
    assertTrue(updatePodPage.isReceivedByFieldEnabled());
    assertTrue(updatePodPage.isReceivedDateFieldEnabled());

    ViewOrdersPage viewOrdersPage = homePage.navigateViewOrders();
    assertEquals("Packed", viewOrdersPage.getOrderStatus(1));

    homePage.navigateManagePOD();
    managePodPage.selectRequisitionToUpdatePod(1);
    updatePodPage.clickSubmitButton();
    updatePodPage.clickPageErrorsMessage();
    updatePodPage.clickErrorPage(2);

    verifyProductDisplayOrderOnPage(new String[]{"NF4", "F5", "NF5", "ZX1", "F6", "NF6", "F7", "NF7", "F8", "NF8"});
    assertEquals(updatePodPage.getReplacedProductCode(1), "");
    assertEquals(updatePodPage.getReplacedProductCode(2), "NF5");
    verifyCategoryDisplayOrderOnPage(new String[]{"Antibiotics", "", "", "", "", "", "", "", "", ""});
    for (int i = 1; i <= 10; i++) {
      updatePodPage.enterPodData("2" + i, "notes", null, i);
    }

    updatePodPage.clickPageErrorsMessage();
    updatePodPage.clickErrorPage(3);
    verifyProductDisplayOrderOnPage(new String[]{"F9", "NF9"});
    assertEquals(updatePodPage.getReplacedProductCode(1), "NF9");
    assertEquals(updatePodPage.getReplacedProductCode(2), "");
    verifyCategoryDisplayOrderOnPage(new String[]{"Antibiotics", ""});
    updatePodPage.enterPodData("31", "", null, 1);
    updatePodPage.enterPodData("32", "", null, 2);

    updatePodPage.clickSubmitButton();
    updatePodPage.clickOkButton();
    testWebDriver.waitForAjax();
    assertTrue(updatePodPage.isPodSuccessMessageDisplayed());
    testWebDriver.sleep(500);

    assertFalse(updatePodPage.isQuantityReceivedEnabled(1));
    assertFalse(updatePodPage.isNotesEnabled(1));

    homePage.navigateViewOrders();
    assertEquals("Received", viewOrdersPage.getOrderStatus(1));

    homePage.navigateManagePOD();
    managePodPage.verifyNoOrderMessage();
  }

  private void enterTestDataForShipment(Boolean fullSupplyFlag1, Boolean fullSupplyFlag2) throws SQLException {
    dbWrapper.updateFieldValue("orders", "status", "RELEASED", null, null);
    for (Integer i = 0; i < 10; i++)
      testDataForShipmentWithReplacedProduct(0, fullSupplyFlag1, "F" + i, i, "NF" + i);
    for (Integer i = 0; i < 10; i++)
      testDataForShipment(0, fullSupplyFlag2, "NF" + i, i);

    testDataForShipment(0, false, "ZX", 78);
    testDataForShipment(0, true, "ZX1", 78);
    dbWrapper.updateFieldValue("orders", "status", "PACKED", null, null);
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

  private void verifyNumberOfLineItemsVisibleOnPage(int numberOfLineItems) {
    assertEquals(numberOfLineItems, testWebDriver.getElementsSizeByXpath("//table[@id='podTable']/tbody"));
  }

  @AfterMethod(groups = {"orderAndPod"})
  public void tearDown() throws SQLException {
    HomePage homePage = PageObjectFactory.getHomePage(testWebDriver);
    homePage.logout(baseUrlGlobal);
    dbWrapper.deleteData();
    dbWrapper.closeConnection();
  }
}
