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
import static java.util.Arrays.asList;
import static org.testng.AssertJUnit.assertEquals;
import static org.testng.AssertJUnit.assertTrue;

public class ManageProduct extends TestCaseHelper {

  LoginPage loginPage;
  ProductPage productPage;

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
    productPage = PageObjectFactory.getProductPage(testWebDriver);
  }

  @Test(groups = {"admin"})
  public void testRightsNotPresent() throws SQLException {
    HomePage homePage = loginPage.loginAs(testData.get(ADMIN), testData.get(PASSWORD));
    homePage.navigateManageFacility();

    assertFalse(homePage.isProductTabDisplayed());
    homePage.logout();

    dbWrapper.assignRight("Admin", "MANAGE_PRODUCT");
    loginPage.loginAs(testData.get(ADMIN), testData.get(PASSWORD));
    homePage.navigateManageFacility();
    assertTrue(homePage.isProductTabDisplayed());
    productPage = homePage.navigateToProductPage();

    assertEquals("Search product", productPage.getSearchProductHeader());
    assertTrue(productPage.isSearchIconDisplayed());
    assertTrue(homePage.isProductTabDisplayed());
    assertEquals("Product", productPage.getSelectSearchOption());
    assertFalse(productPage.isResultDisplayed());
    productPage.clickSearchOptionButton();
    assertFalse(productPage.isCloseSearchResultsButtonDisplayed());
  }

  @Test(groups = {"admin"})
  public void testProductSearchSortAndPagination() throws SQLException {
    dbWrapper.assignRight("Admin", "MANAGE_PRODUCT");
    dbWrapper.assignRight("Admin", "UPLOADS");
    dbWrapper.updateFieldValue("programs", "name", "hiv", "code", "HIV");

    HomePage homePage = loginPage.loginAs(testData.get(ADMIN), testData.get(PASSWORD));
    productPage = homePage.navigateToProductPage();
    searchProduct("P");
    assertEquals("No matches found for 'P'", productPage.getNoResultsMessage());

    UploadPage uploadPage = homePage.navigateUploads();
    uploadPage.uploadProducts("QA_Products_For_Products_CRUD21.csv");
    uploadPage.verifySuccessMessageOnUploadScreen();
    uploadPage.uploadProgramProductMapping("QA_Program_Products15.csv");
    uploadPage.verifySuccessMessageOnUploadScreen();

    homePage.navigateToProductPage();
    testWebDriver.waitForAjax();
    searchProduct("P");
    assertEquals("21 matches found for 'P'", productPage.getNResultsMessage());

    verifyNumberOFPageLinksDisplayed(21, 10);
    verifyPageNumberLinksDisplayed();
    verifyPageNumberSelected(1);
    verifyNextAndLastPageLinksEnabled();
    verifyPreviousAndFirstPageLinksDisabled();
    verifyNumberOfLineItemsVisibleOnPage(10);

    verifyCategoryOrderOnPage(asList("anaesthetics", "", "", "", "", "", "", "", "Antibiotics", ""));
    verifyProductNameOrderOnPage(asList("Indinavir 400mg Tablets19", "Indinavir 40mg Tablets8", "Indinavir 400mg Tablets13",
      "Indinavir 400mg Tablets17", "Indinavir 400mg Tablets21", "Indinavir 400mg Tablets4", "Indinavir 400mg Tablets6",
      "Indinavir 400mg Tablets15", "AIndinavir 400mg Tablets2", "indinavir 400mg Tablets1"));
    verifyProgramOrderOnPage(asList("ESSENTIAL MEDICINES", "ESSENTIAL MEDICINES", "hiv", "hiv", "hiv", "hiv", "hiv", "TB", "MALARIA", "TB"));

    navigateToPage(2);
    verifyPageNumberSelected(2);
    verifyNextAndLastPageLinksEnabled();
    verifyPreviousAndFirstPageLinksEnabled();
    verifyNumberOfLineItemsVisibleOnPage(10);
    verifyCategoryOrderOnPage(asList("Antibiotics", "category3", "", "", "", "Uncategorised", "", "", "", ""));
    verifyProgramOrderOnPage(asList("TB", "hiv", "hiv", "MALARIA", "VACCINES", "", "", "", "", ""));
    verifyProductNameOrderOnPage(asList("Indinavir 400mg Tablets3", "Indinavir 400mg Tablets10", "Indinavir 400mg Tablets11",
      "Indinavir 400mg Tablets12", "Indinavir 400mg Tablets16", "Indinavir 400mg Tablets14", "Indinavir 400mg Tablets18",
      "Indinavir 400mg Tablets20", "Indinavir 400mg Tablets5", "Indinavir 400mg Tablets7"));
    assertEquals("NA", productPage.getUndefinedActive(8));

    navigateToNextPage();
    verifyPageNumberSelected(3);
    verifyNextAndLastPageLinksDisabled();
    verifyPreviousAndFirstPageLinksEnabled();
    verifyNumberOfLineItemsVisibleOnPage(1);
    verifyCategoryOrderOnPage(asList("Uncategorised"));
    verifyProductNameOrderOnPage(asList("Indinavir 400mg Tablets9"));
    verifyProgramOrderOnPage(asList(""));

    navigateToFirstPage();
    verifyPageNumberSelected(1);
    verifyNextAndLastPageLinksEnabled();
    verifyPreviousAndFirstPageLinksDisabled();
    verifyNumberOfLineItemsVisibleOnPage(10);
    verifyCategoryOrderOnPage(asList("anaesthetics", "", "", "", "", "", "", "", "Antibiotics", ""));
    verifyProductNameOrderOnPage(asList("Indinavir 400mg Tablets19", "Indinavir 40mg Tablets8", "Indinavir 400mg Tablets13",
      "Indinavir 400mg Tablets17", "Indinavir 400mg Tablets21", "Indinavir 400mg Tablets4", "Indinavir 400mg Tablets6",
      "Indinavir 400mg Tablets15", "AIndinavir 400mg Tablets2", "indinavir 400mg Tablets1"));
    verifyProgramOrderOnPage(asList("ESSENTIAL MEDICINES", "ESSENTIAL MEDICINES", "hiv", "hiv", "hiv", "hiv", "hiv", "TB", "MALARIA", "TB"));

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
    assertFalse(productPage.isCodeHeaderDisplayed());

    searchProduct("AP10");
    assertEquals("1 match found for 'AP10'", productPage.getOneResultsMessage());
    assertEquals("AP10", productPage.getCode(1));
    verifyNumberOfLineItemsVisibleOnPage(1);

    dbWrapper.updateFieldValue("programs", "name", "HIV", "code", "HIV");
  }

  @Test(groups = {"admin"})
  public void testProductSearchOnProgramSortAndPagination() throws SQLException {
    dbWrapper.assignRight("Admin", "MANAGE_PRODUCT");
    dbWrapper.assignRight("Admin", "UPLOADS");
    dbWrapper.updateFieldValue("programs", "name", "hiv", "code", "HIV");

    HomePage homePage = loginPage.loginAs(testData.get(ADMIN), testData.get(PASSWORD));
    UploadPage uploadPage = homePage.navigateUploads();
    uploadPage.uploadProducts("QA_Products_For_Products_CRUD21.csv");
    uploadPage.verifySuccessMessageOnUploadScreen();
    uploadPage.uploadProgramProductMapping("QA_Program_Products15.csv");
    uploadPage.verifySuccessMessageOnUploadScreen();

    homePage.navigateToProductPage();
    productPage.selectSearchOptionProgram();
    assertEquals("Program", productPage.getSelectSearchOption());
    searchProduct("i");
    testWebDriver.sleep(500);
    assertEquals("12 matches found for 'i'", productPage.getNResultsMessage());

    assertEquals("Full Supply", productPage.getFullSupplyHeader());
    assertEquals("Code", productPage.getCodeHeader());
    assertEquals("Name", productPage.getNameHeader());
    assertEquals("Program", productPage.getProgramHeader());
    assertEquals("Strength", productPage.getStrengthHeader());
    assertEquals("Unit of measure", productPage.getUnitOfMeasureHeader());
    assertEquals("Dispensing unit", productPage.getDispensingUnitHeader());
    assertEquals("Pack Size", productPage.getPackSizeHeader());
    assertEquals("Global active", productPage.getGlobalActiveHeader());
    assertEquals("Active at program", productPage.getActiveAtProgramHeader());

    assertFalse(productPage.isActiveAtProgramDisplayed(7));
    assertFalse(productPage.isGlobalActiveDisplayed(6));
    assertFalse(productPage.isFullSupplyDisplayed(6));
    assertTrue(productPage.isActiveAtProgramDisplayed(2));
    assertTrue(productPage.isGlobalActiveDisplayed(5));
    assertTrue(productPage.isFullSupplyDisplayed(4));
    assertEquals("10", productPage.getPackSize(1));
    assertEquals("Btl of 18Tabl", productPage.getDispensingUnit(7));
    assertEquals("mg", productPage.getUnitOfMeasure(5));
    assertEquals("300/200/600", productPage.getStrength(8));
    assertEquals("P11", productPage.getCode(10));

    verifyCategoryOrderOnPage(asList("anaesthetics", "", "anaesthetics", "", "", "", "", "category3", "", "Antibiotics"));
    verifyProductNameOrderOnPage(asList("Indinavir 400mg Tablets19", "Indinavir 40mg Tablets8", "Indinavir 400mg Tablets13",
      "Indinavir 400mg Tablets17", "Indinavir 400mg Tablets21", "Indinavir 400mg Tablets4", "Indinavir 400mg Tablets6",
      "Indinavir 400mg Tablets10", "Indinavir 400mg Tablets11", "AIndinavir 400mg Tablets2"));
    verifyProgramOrderOnPage(asList("ESSENTIAL MEDICINES", "ESSENTIAL MEDICINES", "hiv", "hiv", "hiv", "hiv", "hiv", "hiv", "hiv", "MALARIA"));

    navigateToNextPage();
    verifyPageNumberSelected(2);
    verifyNextAndLastPageLinksDisabled();
    verifyPreviousAndFirstPageLinksEnabled();
    verifyNumberOfLineItemsVisibleOnPage(2);
    verifyCategoryOrderOnPage(asList("category3", "category3"));
    verifyProductNameOrderOnPage(asList("Indinavir 400mg Tablets12", "Indinavir 400mg Tablets16"));
    verifyProgramOrderOnPage(asList("MALARIA", "VACCINES"));

    productPage.clickCloseSearchResultsButton();
    assertFalse(productPage.isResultDisplayed());

    searchProduct("i");
    productPage.selectSearchOptionProduct();
    productPage.clickSearchIcon();
    assertEquals("21 matches found for 'i'", productPage.getNResultsMessage());
  }

  public void searchProduct(String searchParameter) {
    productPage.enterSearchProductParameter(searchParameter);
    productPage.clickSearchIcon();
    testWebDriver.waitForAjax();
  }

  private void verifyProgramOrderOnPage(List<String> programs) {
    for (int i = 1; i <= programs.size(); i++) {
      assertEquals(programs.get(i - 1), productPage.getProgram(i));
    }
  }

  private void verifyCategoryOrderOnPage(List<String> categories) {
    for (int i = 1; i <= categories.size(); i++) {
      assertEquals(categories.get(i - 1), productPage.getCategory(i));
    }
  }

  private void verifyProductNameOrderOnPage(List<String> productNames) {
    for (int i = 1; i <= productNames.size(); i++) {
      assertEquals(productNames.get(i - 1), productPage.getName(i));
    }
  }

  private void verifyNumberOfLineItemsVisibleOnPage(int numberOfLineItems) {
    assertEquals(numberOfLineItems, productPage.getSizeOfResultsTable());
  }

  @AfterMethod(groups = {"admin"})
  public void tearDown() throws SQLException {
    HomePage homePage = PageObjectFactory.getHomePage(testWebDriver);
    homePage.logout(baseUrlGlobal);
    dbWrapper.removeAllExistingRights("Admin");
    dbWrapper.insertAllAdminRightsAsSeedData();
    dbWrapper.deleteData();
    dbWrapper.closeConnection();
  }
}
