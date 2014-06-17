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

import java.io.IOException;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.thoughtworks.selenium.SeleneseTestBase.assertFalse;
import static com.thoughtworks.selenium.SeleneseTestBase.assertTrue;
import static java.util.Arrays.asList;
import static org.testng.AssertJUnit.assertEquals;

public class ManageFacilityApprovedProduct extends TestCaseHelper {

  LoginPage loginPage;
  FacilityApprovedProductPage facilityApprovedProductPage;

  public static final String ADMIN = "admin";
  public static final String PASSWORD = "password";

  public Map<String, String> testData = new HashMap<String, String>() {{
    put(PASSWORD, "Admin123");
    put(ADMIN, "Admin123");
  }};

  @BeforeMethod(groups = {"admin"})
  public void setUp() throws InterruptedException, SQLException, IOException {
    super.setup();
    dbWrapper.removeAllExistingRights("Admin");
    dbWrapper.assignRight("Admin", "MANAGE_FACILITY");
    dbWrapper.insertProductCategoryWithDisplayOrder("Antibiotic", "Antibiotics", 1);
    dbWrapper.insertProductCategoryWithDisplayOrder("anaesthetics", "anaesthetics", 1);
    dbWrapper.insertProductCategoryWithDisplayOrder("category3", "category3", 2);
    loginPage = PageObjectFactory.getLoginPage(testWebDriver, baseUrlGlobal);
    facilityApprovedProductPage = PageObjectFactory.getFacilityApprovedProductPage(testWebDriver);
  }

  @Test(groups = {"admin"})
  public void testRightsNotPresent() throws SQLException {
    dbWrapper.updateFieldValue("programs", "push", "t", "code", "MALARIA");
    dbWrapper.updateFieldValue("programs", "name", "hiv", "code", "HIV");
    dbWrapper.updateFieldValue("facility_types", "name", "lvl3 Hospital", "name", "Lvl3 Hospital");
    dbWrapper.updateFieldValue("facility_types", "displayOrder", "1", "name", "Warehouse");
    HomePage homePage = loginPage.loginAs(testData.get(ADMIN), testData.get(PASSWORD));
    homePage.navigateManageFacility();
    assertFalse(homePage.isFacilityApprovedProductTabDisplayed());
    homePage.logout();

    dbWrapper.assignRight("Admin", "MANAGE_FACILITY_APPROVED_PRODUCT");
    loginPage.loginAs(testData.get(ADMIN), testData.get(PASSWORD));
    homePage.navigateManageFacility();
    assertTrue(homePage.isFacilityApprovedProductTabDisplayed());
    facilityApprovedProductPage = homePage.navigateToFacilityApprovedProductPage();

    assertEquals("Facility approved products", facilityApprovedProductPage.getFacilityApprovedProductHeader());
    assertEquals("Program", facilityApprovedProductPage.getProgramLabel());
    assertEquals("Facility type", facilityApprovedProductPage.getFacilityTypeLabel());
    assertTrue(facilityApprovedProductPage.isSelectionMandatoryMessageDisplayed());
    assertEquals("Please select program and facility type to view product list", facilityApprovedProductPage.getSelectionMandatoryMessage());
    assertFalse(facilityApprovedProductPage.isAddNewButtonDisplayed());
    assertFalse(facilityApprovedProductPage.isSearchIconDisplayed());
    assertFalse(facilityApprovedProductPage.isNRecordsMessageDisplayed());

    List<String> expectedListOfFacilityTypes = asList("--Select facility type--", "lvl3 Hospital", "Warehouse", "Lvl2 Hospital", "Lvl1 Hospital",
      "Health Center", "Health Post", "Satellite Facility", "CHW", "DHMT", "State Office", "District Office");
    List<String> actualListOfFacilityTypes = facilityApprovedProductPage.getListOfFacilityTypes();
    assertTrue(actualListOfFacilityTypes.equals(expectedListOfFacilityTypes));

    dbWrapper.updateFieldValue("programs", "push", "f", "code", "MALARIA");
    dbWrapper.updateFieldValue("programs", "name", "HIV", "code", "HIV");
    dbWrapper.updateFieldValue("facility_types", "name", "Lvl3 Hospital", "name", "lvl3 Hospital");
    dbWrapper.updateFieldValue("facility_types", "displayOrder", "11", "name", "Warehouse");
  }

  @Test(groups = {"admin"})
  public void testProductsListSortingAndPagination() throws SQLException {
    dbWrapper.assignRight("Admin", "MANAGE_FACILITY_APPROVED_PRODUCT");
    dbWrapper.assignRight("Admin", "UPLOADS");
    HomePage homePage = loginPage.loginAs(testData.get(ADMIN), testData.get(PASSWORD));
    facilityApprovedProductPage = homePage.navigateToFacilityApprovedProductPage();

    facilityApprovedProductPage.selectProgram("HIV");
    facilityApprovedProductPage.selectFacilityType("Warehouse");
    testWebDriver.waitForAjax();
    assertTrue(facilityApprovedProductPage.isSearchFacilityApprovedProductLabelDisplayed());
    assertEquals("Search products", facilityApprovedProductPage.getSearchFacilityApprovedProductLabel());
    assertFalse(facilityApprovedProductPage.isClearSearchButtonEnabled());
    assertFalse(facilityApprovedProductPage.isSelectionMandatoryMessageDisplayed());
    assertTrue(facilityApprovedProductPage.isSearchIconDisplayed());
    assertTrue(facilityApprovedProductPage.isAddNewButtonDisplayed());
    assertTrue(facilityApprovedProductPage.isNoRecordsMessageDisplayed());
    assertEquals("No records found", facilityApprovedProductPage.getNoRecordsMessage());
    assertFalse(facilityApprovedProductPage.isNoResultMessageDisplayed());

    UploadPage uploadPage = homePage.navigateUploads();
    uploadPage.uploadProducts("QA_Products21.csv");
    uploadPage.verifySuccessMessageOnUploadScreen();
    uploadPage.uploadProgramProductMapping("QA_Program_Products21.csv");
    uploadPage.verifySuccessMessageOnUploadScreen();
    uploadPage.uploadFacilityTypeToProductMapping("QA_Facility_Type_To_Product_Mapping21.csv");
    uploadPage.verifySuccessMessageOnUploadScreen();

    homePage.navigateToFacilityApprovedProductPage();
    facilityApprovedProductPage.selectProgram("HIV");
    facilityApprovedProductPage.selectFacilityType("Warehouse");
    assertEquals("21 record(s) found", facilityApprovedProductPage.getNRecordsMessage());

    verifyNumberOFPageLinksDisplayed(21, 10);
    verifyPageNumberLinksDisplayed();
    verifyPageNumberSelected(1);
    verifyNextAndLastPageLinksEnabled();
    verifyPreviousAndFirstPageLinksDisabled();
    verifyNumberOfLineItemsVisibleOnPage(10);

    verifyCategoryOrderOnPage(asList("anaesthetics", "", "", "", "", "", "", "", "", "Antibiotics"));
    verifyNameOrderOnPage(asList("Indinavir 400mg Tablets", "Indinavir 400mg Tablets", "Indinavir 400mg Tablets",
      "Indinavir 400mg Tablets", "Indinavir 400mg Tablets", "Indinavir 400mg Tablets", "Indinavir 400mg Tablets", "Indinavir 400mg Tablets",
      "Indinavir 400mg Tablets", "AIndinavir 400mg Tablets"));
    verifyCodeOrderOnPage(asList("AP12", "AP14", "AP16", "AP18", "AP19", "P13", "P15", "P17", "P20", "P11"));

    navigateToPage(2);
    verifyPageNumberSelected(2);
    verifyNextAndLastPageLinksEnabled();
    verifyPreviousAndFirstPageLinksEnabled();
    verifyNumberOfLineItemsVisibleOnPage(10);
    verifyCategoryOrderOnPage(asList("Antibiotics", "", "", "", "", "category3", "", "", "", ""));
    verifyCodeOrderOnPage(asList("P10", "P12", "P14", "P16", "P18", "AP10", "AP11", "AP13", "AP15", "AP17"));

    navigateToNextPage();
    verifyPageNumberSelected(3);
    verifyNextAndLastPageLinksDisabled();
    verifyPreviousAndFirstPageLinksEnabled();
    verifyNumberOfLineItemsVisibleOnPage(1);
    verifyCategoryOrderOnPage(asList("category3"));
    verifyCodeOrderOnPage(asList("P19"));

    navigateToFirstPage();
    verifyPageNumberSelected(1);
    verifyNextAndLastPageLinksEnabled();
    verifyPreviousAndFirstPageLinksDisabled();
    verifyNumberOfLineItemsVisibleOnPage(10);
    verifyCategoryOrderOnPage(asList("anaesthetics", "", "", "", "", "", "", "", "", "Antibiotics"));
    verifyCodeOrderOnPage(asList("AP12", "AP14", "AP16", "AP18", "AP19", "P13", "P15", "P17", "P20", "P11"));

    navigateToLastPage();
    verifyPageNumberSelected(3);
    verifyNextAndLastPageLinksDisabled();
    verifyPreviousAndFirstPageLinksEnabled();
    verifyNumberOfLineItemsVisibleOnPage(1);

    navigateToPreviousPage();
    verifyPageNumberSelected(2);
    verifyNextAndLastPageLinksEnabled();
    verifyPreviousAndFirstPageLinksEnabled();
    verifyNumberOfLineItemsVisibleOnPage(10);

    testWebDriver.refresh();
    assertFalse(facilityApprovedProductPage.isFullSupplyHeaderPresent());
  }

  @Test(groups = {"admin"})
  public void testProductSearchSortAndPagination() throws SQLException {
    dbWrapper.assignRight("Admin", "MANAGE_FACILITY_APPROVED_PRODUCT");
    dbWrapper.assignRight("Admin", "UPLOADS");
    HomePage homePage = loginPage.loginAs(testData.get(ADMIN), testData.get(PASSWORD));
    UploadPage uploadPage = homePage.navigateUploads();
    uploadPage.uploadProducts("QA_Products21.csv");
    uploadPage.verifySuccessMessageOnUploadScreen();
    uploadPage.uploadProgramProductMapping("QA_Program_Products21.csv");
    uploadPage.uploadFacilityTypeToProductMapping("QA_Facility_Type_To_Product_Mapping21.csv");
    uploadPage.verifySuccessMessageOnUploadScreen();

    homePage.navigateToFacilityApprovedProductPage();
    facilityApprovedProductPage.selectProgram("HIV");
    facilityApprovedProductPage.selectFacilityType("Warehouse");
    assertEquals("21 record(s) found", facilityApprovedProductPage.getNRecordsMessage());
    verifyNumberOFPageLinksDisplayed(21, 10);
    facilityApprovedProductPage.enterSearchParameter("AP");
    facilityApprovedProductPage.clickSearchIcon();
    testWebDriver.sleep(500);

    assertFalse(facilityApprovedProductPage.isNRecordsMessageDisplayed());
    assertEquals("10 matches found for 'AP'", facilityApprovedProductPage.getNResultsMessage());
    assertTrue(facilityApprovedProductPage.isClearSearchButtonEnabled());

    verifyNumberOFPageLinksDisplayed(10, 10);
    verifyPageNumberLinksDisplayed();
    verifyPageNumberSelected(1);
    verifyNextAndLastPageLinksDisabled();
    verifyPreviousAndFirstPageLinksDisabled();
    verifyNumberOfLineItemsVisibleOnPage(10);

    verifyCategoryOrderOnPage(asList("anaesthetics", "", "", "", "", "category3", "", "", "", ""));
    verifyCodeOrderOnPage(asList("AP12", "AP14", "AP16", "AP18", "AP19", "AP10", "AP11", "AP13", "AP15", "AP17"));

    facilityApprovedProductPage.clickClearSearchButton();
    testWebDriver.sleep(500);
    assertFalse(facilityApprovedProductPage.isNResultsMessageDisplayed());
    assertTrue(facilityApprovedProductPage.isNRecordsMessageDisplayed());
    verifyNumberOFPageLinksDisplayed(21, 10);

    assertEquals("Full Supply", facilityApprovedProductPage.getFullSupplyHeader());
    assertEquals("Code", facilityApprovedProductPage.getCodeHeader());
    assertEquals("Name", facilityApprovedProductPage.getNameHeader());
    assertEquals("Strength", facilityApprovedProductPage.getStrengthHeader());
    assertEquals("Unit of measure", facilityApprovedProductPage.getUnitOfMeasureHeader());
    assertEquals("Max months of stock", facilityApprovedProductPage.getMaxMonthsOfStockHeader());
    assertEquals("Min months of stock", facilityApprovedProductPage.getMinMonthsOfStockHeader());
    assertEquals("Emergency Order Point", facilityApprovedProductPage.getEopHeader());
    assertEquals("Global active", facilityApprovedProductPage.getGlobalActiveHeader());
    assertEquals("Active at program", facilityApprovedProductPage.getActiveAtProgramHeader());

    assertTrue(facilityApprovedProductPage.isFullSupply(1));
    assertFalse(facilityApprovedProductPage.isFullSupply(6));
    assertEquals("300/200/600", facilityApprovedProductPage.getStrength(1));
    assertEquals("mg", facilityApprovedProductPage.getUnitOfMeasure(2));
    assertEquals("7", facilityApprovedProductPage.getMaxMonthsOfStock(8));
    assertEquals("", facilityApprovedProductPage.getMinMonthsOfStock(4));
    assertEquals("", facilityApprovedProductPage.getEop(3));
    assertTrue(facilityApprovedProductPage.isGloballyActive(2));
    assertFalse(facilityApprovedProductPage.isGloballyActive(10));
    assertTrue(facilityApprovedProductPage.isActiveAtProgram(10));
    assertFalse(facilityApprovedProductPage.isActiveAtProgram(7));

    facilityApprovedProductPage.enterSearchParameter("P");
    facilityApprovedProductPage.clickSearchIcon();
    testWebDriver.sleep(500);

    assertEquals("21 matches found for 'P'", facilityApprovedProductPage.getNResultsMessage());
    verifyNumberOFPageLinksDisplayed(21, 10);
    navigateToLastPage();
    verifyPageNumberSelected(3);
    testWebDriver.sleep(500);
    verifyNextAndLastPageLinksDisabled();
    verifyPreviousAndFirstPageLinksEnabled();

    testWebDriver.refresh();
    assertFalse(facilityApprovedProductPage.isFullSupplyHeaderPresent());
  }

  private void verifyCodeOrderOnPage(List<String> codes) {
    for (int i = 1; i <= codes.size(); i++) {
      assertEquals(codes.get(i - 1), facilityApprovedProductPage.getCode(i));
    }
  }

  private void verifyCategoryOrderOnPage(List<String> categories) {
    for (int i = 1; i <= categories.size(); i++) {
      assertEquals(categories.get(i - 1), facilityApprovedProductPage.getCategory(i));
    }
  }

  private void verifyNameOrderOnPage(List<String> names) {
    for (int i = 1; i <= names.size(); i++) {
      assertEquals(names.get(i - 1), facilityApprovedProductPage.getName(i));
    }
  }

  private void verifyNumberOfLineItemsVisibleOnPage(int numberOfLineItems) {
    assertEquals(numberOfLineItems, facilityApprovedProductPage.getSizeOfResultsTable());
  }

  @AfterMethod(groups = {"admin"})
  public void tearDown() throws SQLException {
    dbWrapper.updateFieldValue("programs", "push", "f", "code", "MALARIA");
    dbWrapper.updateFieldValue("programs", "name", "HIV", "code", "HIV");
    dbWrapper.updateFieldValue("facility_types", "name", "Lvl3 Hospital", "name", "lvl3 Hospital");
    dbWrapper.updateFieldValue("facility_types", "displayOrder", "11", "name", "Warehouse");

    HomePage homePage = PageObjectFactory.getHomePage(testWebDriver);
    homePage.logout(baseUrlGlobal);
    dbWrapper.removeAllExistingRights("Admin");
    dbWrapper.insertAllAdminRightsAsSeedData();
    dbWrapper.deleteData();
    dbWrapper.closeConnection();
  }
}
