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
    dbWrapper.insertProductCategoryWithDisplayOrder("Antibiotic", "Antibiotics", 1);
    dbWrapper.insertProductCategoryWithDisplayOrder("anaesthetics", "anaesthetics", 1);
    dbWrapper.insertProductCategoryWithDisplayOrder("category3", "category3", 2);
    loginPage = PageObjectFactory.getLoginPage(testWebDriver, baseUrlGlobal);
    facilityApprovedProductPage = PageObjectFactory.getFacilityApprovedProductPage(testWebDriver);
  }

  @Test(groups = {"admin"})
  public void testRightsNotPresent() throws SQLException {
    dbWrapper.assignRight("Admin", "MANAGE_FACILITY");
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
    uploadPage.uploadProducts("QA_Products_For_Facility_Approved_Products21.csv");
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
      "Indinavir 40mg Tablets", "AIndinavir 400mg Tablets"));
    verifyCodeOrderOnPage(asList("AP12", "AP14", "AP16", "AP18", "AP19", "P13", "P15", "P20", "P17", "P11"));

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
    verifyCodeOrderOnPage(asList("AP12", "AP14", "AP16", "AP18", "AP19", "P13", "P15", "P20", "P17", "P11"));

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
    uploadPage.uploadProducts("QA_Products_For_Facility_Approved_Products21.csv");
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
    assertEquals("Max months of stock *", facilityApprovedProductPage.getMaxMonthsOfStockHeader());
    assertEquals("Min months of stock", facilityApprovedProductPage.getMinMonthsOfStockHeader());
    assertEquals("Emergency Order Point", facilityApprovedProductPage.getEopHeader());
    assertEquals("Global active", facilityApprovedProductPage.getGlobalActiveHeader());
    assertEquals("Active at program", facilityApprovedProductPage.getActiveAtProgramHeader());

    assertTrue(facilityApprovedProductPage.isFullSupply(1));
    assertFalse(facilityApprovedProductPage.isFullSupply(6));
    assertEquals("300/200/600", facilityApprovedProductPage.getStrength(1));
    assertEquals("mg", facilityApprovedProductPage.getUnitOfMeasure(2));
    assertEquals("7", facilityApprovedProductPage.getMaxMonthsOfStock(9));
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

  @Test(groups = {"admin"})
  public void testAddFacilityTypeApprovedProductsPopUp() throws SQLException {
    setupData();
    HomePage homePage = loginPage.loginAs(testData.get(ADMIN), testData.get(PASSWORD));
    homePage.navigateToFacilityApprovedProductPage();

    facilityApprovedProductPage.selectProgram("HIV");
    facilityApprovedProductPage.selectFacilityType("Warehouse");
    assertEquals("No records found", facilityApprovedProductPage.getNoRecordsMessage());

    facilityApprovedProductPage.clickAddNewButton();
    assertEquals("Add product(s) for HIV at Warehouse", facilityApprovedProductPage.getAddFacilityApprovedProductHeader());
    assertEquals("Category:", facilityApprovedProductPage.getAddCategoryLabel());
    assertEquals("Product code / name:", facilityApprovedProductPage.getAddProductLabel());
    assertEquals("Max months of stock *", facilityApprovedProductPage.getAddMaxMonthsOfStockLabel());
    assertEquals("Min months of stock", facilityApprovedProductPage.getAddMinMonthsOfStockLabel());
    assertEquals("Emergency Order Point", facilityApprovedProductPage.getAddEopLabel());
    assertFalse(facilityApprovedProductPage.isAddProductButtonEnabled());

    List<String> expectedListOfCategories = asList("", "anaesthetics", "anaesthetics2", "Antibiotics", "Antibiotics4", "category3", "category31", "category4",
      "category5", "category6");
    List<String> actualListOfCategories = facilityApprovedProductPage.getListOfCategories();
    assertTrue(actualListOfCategories.equals(expectedListOfCategories));

    facilityApprovedProductPage.selectCategory("Antibiotics4");
    List<String> expectedListOfProducts = asList("", "Code | Name | Strength | Unit of measure | Type", "p2 | product3 | 300/200/600 | mg | true");
    List<String> actualListOfProducts = facilityApprovedProductPage.getListOfProducts();
    assertTrue(actualListOfProducts.equals(expectedListOfProducts));

    facilityApprovedProductPage.selectCategory("Antibiotics");

    expectedListOfProducts = asList("", "Code | Name | Strength | Unit of measure | Type", "p1 | Product1 | 300/200/600 | mg | true", "P3 | product2 | 300/200/600 | mg | false");
    actualListOfProducts = facilityApprovedProductPage.getListOfProducts();
    assertTrue(actualListOfProducts.equals(expectedListOfProducts));
    assertEquals("Full supply", facilityApprovedProductPage.getProductTypeInDropDown(1));
    assertEquals("Non full supply", facilityApprovedProductPage.getProductTypeInDropDown(2));

    facilityApprovedProductPage.selectProduct("P3 | product2 | 300/200/600 | mg | false");
    assertFalse(facilityApprovedProductPage.isAddProductButtonEnabled());
    facilityApprovedProductPage.enterMinMonthsOfStock("23.00");
    facilityApprovedProductPage.enterEop("99.99");
    assertFalse(facilityApprovedProductPage.isAddProductButtonEnabled());
    facilityApprovedProductPage.enterMaxMonthsOfStock("00.00");
    assertTrue(facilityApprovedProductPage.isAddProductButtonEnabled());
    facilityApprovedProductPage.clickAddProductButton();

    assertEquals("Product Code |  Name", facilityApprovedProductPage.getAddedProductHeader());
    assertEquals("Max months of stock *", facilityApprovedProductPage.getAddedMaxMonthsOfStockHeader());
    assertEquals("Min months of stock", facilityApprovedProductPage.getAddedMinMonthsOfStockHeader());
    assertEquals("Emergency Order Point", facilityApprovedProductPage.getAddedEopHeader());
    assertEquals("P3 | product2", facilityApprovedProductPage.getAddedFacilityTypeApprovedProductNameLabel(1));
    assertEquals("00.00", facilityApprovedProductPage.getAddedMaxMonthsOfStock(1));
    assertEquals("23.00", facilityApprovedProductPage.getAddedMinMonthsOfStock(1));
    assertEquals("99.99", facilityApprovedProductPage.getAddedEop(1));

    facilityApprovedProductPage.reenterAddedMaxMonthsOfStock("", 1);
    assertEquals("", facilityApprovedProductPage.getAddedMaxMonthsOfStock(1));
    facilityApprovedProductPage.clickAddDoneButton();
    assertEquals("Please correct the highlighted fields before submitting", facilityApprovedProductPage.getAddModalErrorMessage());

    facilityApprovedProductPage.clickCrossButtonForAddedRow(1);
    assertFalse(facilityApprovedProductPage.isAddedProductHeaderDisplayed());
    facilityApprovedProductPage.clickAddDoneButton();
    facilityApprovedProductPage.clickAddCancelButton();
    testWebDriver.waitForAjax();
    assertEquals("No records found", facilityApprovedProductPage.getNoRecordsMessage());
  }

  @Test(groups = {"admin"})
  public void testAddingNewFacilityTypeApprovedProductsPopUp() throws SQLException {
    setupData();
    HomePage homePage = loginPage.loginAs(testData.get(ADMIN), testData.get(PASSWORD));
    homePage.navigateToFacilityApprovedProductPage();

    facilityApprovedProductPage.selectProgram("HIV");
    facilityApprovedProductPage.selectFacilityType("Warehouse");
    assertEquals("No records found", facilityApprovedProductPage.getNoRecordsMessage());
    facilityApprovedProductPage.clickAddNewButton();

    facilityApprovedProductPage.selectCategory("Antibiotics");
    facilityApprovedProductPage.selectProduct("P3 | product2 | 300/200/600 | mg | false");
    facilityApprovedProductPage.enterMaxMonthsOfStock("23.00");
    facilityApprovedProductPage.enterMinMonthsOfStock("00.00");
    facilityApprovedProductPage.clickAddProductButton();
    facilityApprovedProductPage.selectCategory("Antibiotics");
    assertFalse(facilityApprovedProductPage.getListOfProducts().contains("P3 | product2 | 300/200/600 | mg | false"));
    facilityApprovedProductPage.selectCategory("Antibiotics4");
    facilityApprovedProductPage.selectProduct("p2 | product3 | 300/200/600 | mg | true");
    facilityApprovedProductPage.enterMaxMonthsOfStock("99.99");
    facilityApprovedProductPage.enterEop("0.80");
    facilityApprovedProductPage.clickAddProductButton();

    facilityApprovedProductPage.selectCategory("Antibiotics");
    facilityApprovedProductPage.selectProduct("p1 | Product1 | 300/200/600 | mg | true");
    facilityApprovedProductPage.enterMaxMonthsOfStock("99.99");
    facilityApprovedProductPage.enterEop("0.80");
    facilityApprovedProductPage.clickAddProductButton();
    facilityApprovedProductPage.selectCategory("Antibiotics");
    assertFalse(facilityApprovedProductPage.getListOfProducts().contains("p1 | Product1 | 300/200/600 | mg | true"));

    facilityApprovedProductPage.clickCrossButtonForAddedRow(1);
    facilityApprovedProductPage.selectCategory("Antibiotics");
    assertTrue(facilityApprovedProductPage.getListOfProducts().contains("p1 | Product1 | 300/200/600 | mg | true"));

    assertEquals("P3 | product2", facilityApprovedProductPage.getAddedFacilityTypeApprovedProductNameLabel(1));
    assertEquals("p2 | product3", facilityApprovedProductPage.getAddedFacilityTypeApprovedProductNameLabel(2));
    assertEquals("23.00", facilityApprovedProductPage.getAddedMaxMonthsOfStock(1));

    facilityApprovedProductPage.reenterAddedMaxMonthsOfStock("00.00", 1);
    assertEquals("00.00", facilityApprovedProductPage.getAddedMaxMonthsOfStock(1));
    facilityApprovedProductPage.clickAddDoneButton();
    testWebDriver.waitForAjax();
    assertEquals("2 product(s) added successfully", facilityApprovedProductPage.getSaveSuccessMessage());
    assertEquals("2 record(s) found", facilityApprovedProductPage.getNRecordsMessage());

    verifyCategoryOrderOnPage(asList("Antibiotics", "Antibiotics4"));
    verifyCodeOrderOnPage(asList("P3", "p2"));
    assertEquals("0", facilityApprovedProductPage.getMaxMonthsOfStock(1));
    assertEquals("99.99", facilityApprovedProductPage.getMaxMonthsOfStock(2));
    assertEquals("0", facilityApprovedProductPage.getMinMonthsOfStock(1));
    assertEquals("0.8", facilityApprovedProductPage.getEop(2));

    facilityApprovedProductPage.clickAddNewButton();
    testWebDriver.sleep(500);
    assertFalse(facilityApprovedProductPage.getListOfCategories().contains("Antibiotics4"));
    facilityApprovedProductPage.selectCategory("Antibiotics");
    assertFalse(facilityApprovedProductPage.getListOfProducts().contains("P3 | product2 | 300/200/600 | mg | false"));
    facilityApprovedProductPage.selectProduct("p1 | Product1 | 300/200/600 | mg | true");
    facilityApprovedProductPage.enterMaxMonthsOfStock("00.00");
    facilityApprovedProductPage.clickAddProductButton();
    facilityApprovedProductPage.clickAddCancelButton();
    testWebDriver.waitForAjax();
    assertEquals("2 record(s) found", facilityApprovedProductPage.getNRecordsMessage());
  }

  @Test(groups = {"admin"})
  public void testEditingFacilityTypeApprovedProduct() throws SQLException {
    setupData();
    dbWrapper.insertFacilityApprovedProduct("p1", "HIV", "warehouse");
    dbWrapper.insertFacilityApprovedProduct("p2", "HIV", "warehouse");
    dbWrapper.insertFacilityApprovedProduct("P3", "HIV", "warehouse");
    dbWrapper.insertFacilityApprovedProduct("p4", "HIV", "warehouse");

    HomePage homePage = loginPage.loginAs(testData.get(ADMIN), testData.get(PASSWORD));
    homePage.navigateToFacilityApprovedProductPage();

    facilityApprovedProductPage.selectProgram("HIV");
    facilityApprovedProductPage.selectFacilityType("Warehouse");
    assertTrue(facilityApprovedProductPage.isEditFacilityApprovedProductButtonDisplayed(1));
    assertTrue(facilityApprovedProductPage.isEditFacilityApprovedProductButtonDisplayed(2));
    assertTrue(facilityApprovedProductPage.isEditFacilityApprovedProductButtonDisplayed(3));
    assertTrue(facilityApprovedProductPage.isEditFacilityApprovedProductButtonDisplayed(4));

    facilityApprovedProductPage.clickEditFacilityApprovedProductButton(2);
    facilityApprovedProductPage.clickEditFacilityApprovedProductButton(1);
    testWebDriver.sleep(1000);
    facilityApprovedProductPage.editMaxMonthsOfStock("0.00", 2);
    facilityApprovedProductPage.clickCancelButtonForEditProduct(2);
    testWebDriver.sleep(1000);

    assertEquals("3", facilityApprovedProductPage.getMaxMonthsOfStock(2));
    assertEquals("3", facilityApprovedProductPage.getEditMaxMonthsOfStock(1));
    assertFalse(facilityApprovedProductPage.isSaveButtonForEditProductDisplayed(2));
    assertTrue(facilityApprovedProductPage.isEditFacilityApprovedProductButtonDisplayed(2));
    assertFalse(facilityApprovedProductPage.isCancelButtonForEditProductDisplayed(2));
    assertTrue(facilityApprovedProductPage.isSaveButtonForEditProductDisplayed(1));
    assertFalse(facilityApprovedProductPage.isEditFacilityApprovedProductButtonDisplayed(1));
    assertTrue(facilityApprovedProductPage.isCancelButtonForEditProductDisplayed(1));

    facilityApprovedProductPage.clickCancelButtonForEditProduct(1);
    testWebDriver.sleep(1000);
    assertEquals("3", facilityApprovedProductPage.getMaxMonthsOfStock(1));

    facilityApprovedProductPage.clickEditFacilityApprovedProductButton(3);
    facilityApprovedProductPage.clickEditFacilityApprovedProductButton(4);
    testWebDriver.sleep(1000);
    facilityApprovedProductPage.editMaxMonthsOfStock("", 3);
    facilityApprovedProductPage.editMinMonthsOfStock("", 3);
    facilityApprovedProductPage.editEop("00.00", 3);
    facilityApprovedProductPage.editEop("0.", 4);
    facilityApprovedProductPage.clickSaveButtonForEditProduct(3);
    testWebDriver.sleep(1000);
    assertEquals("Please correct the highlighted fields before submitting", facilityApprovedProductPage.getSaveErrorMessage());

    facilityApprovedProductPage.editMaxMonthsOfStock("99.", 3);
    testWebDriver.sleep(1000);
    facilityApprovedProductPage.clickSaveButtonForEditProduct(3);
    testWebDriver.waitForAjax();

    assertEquals("\"product2\" updated successfully", facilityApprovedProductPage.getSaveSuccessMessage());
    assertFalse(facilityApprovedProductPage.isSaveButtonForEditProductDisplayed(3));
    assertTrue(facilityApprovedProductPage.isEditFacilityApprovedProductButtonDisplayed(3));
    assertEquals("3", facilityApprovedProductPage.getMaxMonthsOfStock(2));
    assertEquals("99", facilityApprovedProductPage.getMaxMonthsOfStock(3));
    assertEquals("", facilityApprovedProductPage.getMinMonthsOfStock(3));
    assertEquals("0", facilityApprovedProductPage.getEop(3));
    assertEquals("0.", facilityApprovedProductPage.getEditEop(4));
    assertTrue(facilityApprovedProductPage.isSaveButtonForEditProductDisplayed(4));
    assertFalse(facilityApprovedProductPage.isEditFacilityApprovedProductButtonDisplayed(4));
    facilityApprovedProductPage.clickSaveButtonForEditProduct(4);
    testWebDriver.waitForAjax();

    assertEquals("\"product3\" updated successfully", facilityApprovedProductPage.getSaveSuccessMessage());
    assertEquals("3", facilityApprovedProductPage.getMaxMonthsOfStock(4));
    assertEquals("0", facilityApprovedProductPage.getEop(4));
  }

  @Test(groups = {"admin"})
  public void testDeletingFacilityTypeApprovedProduct() throws SQLException {
    setupData();
    dbWrapper.insertFacilityApprovedProduct("p1", "HIV", "warehouse");
    dbWrapper.insertFacilityApprovedProduct("p2", "HIV", "warehouse");
    dbWrapper.insertFacilityApprovedProduct("p4", "HIV", "warehouse");

    HomePage homePage = loginPage.loginAs(testData.get(ADMIN), testData.get(PASSWORD));
    homePage.navigateToFacilityApprovedProductPage();
    facilityApprovedProductPage.selectProgram("HIV");
    facilityApprovedProductPage.selectFacilityType("Warehouse");

    verifyNumberOfLineItemsVisibleOnPage(3);
    verifyCategoryOrderOnPage(asList("anaesthetics2", "Antibiotics", "Antibiotics4"));
    verifyNameOrderOnPage(asList("product4", "Product1", "product3"));

    assertTrue(facilityApprovedProductPage.isDeleteFacilityApprovedProductButtonDisplayed(1));
    assertTrue(facilityApprovedProductPage.isDeleteFacilityApprovedProductButtonDisplayed(2));
    assertTrue(facilityApprovedProductPage.isDeleteFacilityApprovedProductButtonDisplayed(3));
    assertTrue(facilityApprovedProductPage.isEditFacilityApprovedProductButtonDisplayed(1));
    assertTrue(facilityApprovedProductPage.isEditFacilityApprovedProductButtonDisplayed(3));

    facilityApprovedProductPage.clickAddNewButton();
    assertFalse(facilityApprovedProductPage.getListOfCategories().contains("anaesthetics2"));
    facilityApprovedProductPage.selectCategory("Antibiotics");
    assertFalse(facilityApprovedProductPage.getListOfProducts().contains("p1 | Product1 | 300/200/600 | mg | true"));
    facilityApprovedProductPage.clickAddCancelButton();

    facilityApprovedProductPage.clickDeleteButton(2);
    assertEquals("Product \"Product1\" will be deleted from \"Warehouse\" and \"HIV\" assignment", facilityApprovedProductPage.getDialogBoxMessage());
    facilityApprovedProductPage.clickCancelDeleteButton();
    facilityApprovedProductPage.clickDeleteButton(1);
    assertEquals("Product \"product4\" will be deleted from \"Warehouse\" and \"HIV\" assignment", facilityApprovedProductPage.getDialogBoxMessage());
    facilityApprovedProductPage.clickOkDeleteButton();
    testWebDriver.waitForAjax();
    assertEquals("Product \"product4\" deleted successfully", facilityApprovedProductPage.getSaveSuccessMessage());

    verifyNumberOfLineItemsVisibleOnPage(2);
    verifyCategoryOrderOnPage(asList("Antibiotics", "Antibiotics4"));
    verifyNameOrderOnPage(asList("Product1", "product3"));

    testWebDriver.refresh();
    facilityApprovedProductPage.selectProgram("HIV");
    facilityApprovedProductPage.selectFacilityType("Warehouse");
    verifyNumberOfLineItemsVisibleOnPage(2);
    verifyCategoryOrderOnPage(asList("Antibiotics", "Antibiotics4"));
    verifyNameOrderOnPage(asList("Product1", "product3"));

    facilityApprovedProductPage.clickAddNewButton();
    facilityApprovedProductPage.selectCategory("Antibiotics");
    assertFalse(facilityApprovedProductPage.getListOfProducts().contains("p1 | Product1 | 300/200/600 | mg | true"));
    assertTrue(facilityApprovedProductPage.getListOfCategories().contains("anaesthetics2"));
    facilityApprovedProductPage.selectCategory("anaesthetics2");
    assertTrue(facilityApprovedProductPage.getListOfProducts().contains("p4 | product4 | 300/200/600 | mg | true"));
    facilityApprovedProductPage.selectProduct("p4 | product4 | 300/200/600 | mg | true");
    facilityApprovedProductPage.enterMaxMonthsOfStock("0.99");
    facilityApprovedProductPage.clickAddProductButton();
    facilityApprovedProductPage.clickAddDoneButton();
    testWebDriver.waitForAjax();

    verifyNumberOfLineItemsVisibleOnPage(3);
    verifyCategoryOrderOnPage(asList("anaesthetics2", "Antibiotics", "Antibiotics4"));
    verifyNameOrderOnPage(asList("product4", "Product1", "product3"));
    assertEquals("0.99", facilityApprovedProductPage.getMaxMonthsOfStock(1));
  }

  private void setupData() throws SQLException {
    dbWrapper.assignRight("Admin", "MANAGE_FACILITY_APPROVED_PRODUCT");
    dbWrapper.insertProductCategoryWithDisplayOrder("Antibiotic4", "Antibiotics4", 0);
    dbWrapper.insertProductCategoryWithDisplayOrder("anaesthetics2", "anaesthetics2", 1);
    dbWrapper.insertProductCategoryWithDisplayOrder("category31", "category31", 1);
    dbWrapper.insertProductCategoryWithDisplayOrder("category4", "category4", 1);
    dbWrapper.insertProductCategoryWithDisplayOrder("category5", "category5", 1);
    dbWrapper.insertProductCategoryWithDisplayOrder("category6", "category6", 2);
    dbWrapper.insertProduct("p1", "Product1");
    dbWrapper.insertProduct("p2", "product3");
    dbWrapper.insertProduct("P3", "product2");
    dbWrapper.insertProduct("p4", "product4");
    dbWrapper.insertProduct("p5", "product5");
    dbWrapper.insertProduct("p6", "product6");
    dbWrapper.insertProduct("p7", "product7");
    dbWrapper.insertProduct("p8", "product8");
    dbWrapper.insertProduct("p9", "product9");
    dbWrapper.insertProduct("p10", "product10");
    dbWrapper.insertProgramProductsWithCategory("p1", "HIV", "Antibiotic", 2);
    dbWrapper.insertProgramProductsWithCategory("p2", "HIV", "Antibiotic4", 2);
    dbWrapper.insertProgramProductsWithCategory("P3", "HIV", "Antibiotic", 1);
    dbWrapper.insertProgramProductsWithCategory("p4", "HIV", "anaesthetics2", 2);
    dbWrapper.insertProgramProductsWithCategory("p5", "HIV", "category31", 2);
    dbWrapper.insertProgramProductsWithCategory("p6", "HIV", "anaesthetics", 2);
    dbWrapper.insertProgramProductsWithCategory("p7", "HIV", "category4", 2);
    dbWrapper.insertProgramProductsWithCategory("p8", "HIV", "category5", 2);
    dbWrapper.insertProgramProductsWithCategory("p9", "HIV", "category6", 2);
    dbWrapper.insertProgramProductsWithCategory("p10", "HIV", "category3", 2);
    dbWrapper.updateActiveStatusOfProgramProduct("p1", "HIV", "false");
    dbWrapper.updateFieldValue("products", "active", "false", "code", "P3");
    dbWrapper.updateFieldValue("products", "fullSupply", "false", "code", "P3");
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
