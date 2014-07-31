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
import java.text.SimpleDateFormat;
import java.util.Date;
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
    dbWrapper.insertProductCategoryWithDisplayOrder("Antibiotic", "Antibiotics", 1);
    dbWrapper.insertProductCategoryWithDisplayOrder("anaesthetics", "anaesthetics", 1);
    dbWrapper.insertProductCategoryWithDisplayOrder("category3", "category3", 2);
    loginPage = PageObjectFactory.getLoginPage(testWebDriver, baseUrlGlobal);
    productPage = PageObjectFactory.getProductPage(testWebDriver);
  }

  @Test(groups = {"admin"})
  public void testRightsNotPresent() throws SQLException {
    dbWrapper.assignRight("Admin", "MANAGE_FACILITY");
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

    verifyCategoryOrderOnPage(asList("anaesthetics", "", "", "", "", "", "", "category3", "", "Antibiotics"));
    verifyProductNameOrderOnPage(asList("Indinavir 400mg Tablets19", "Indinavir 40mg Tablets8", "Indinavir 400mg Tablets13",
      "Indinavir 400mg Tablets17", "Indinavir 400mg Tablets21", "Indinavir 400mg Tablets4", "Indinavir 400mg Tablets6",
      "Indinavir 400mg Tablets10", "Indinavir 400mg Tablets11", "AIndinavir 400mg Tablets2"));
    verifyProgramOrderOnPage(asList("ESSENTIAL MEDICINES", "ESSENTIAL MEDICINES", "hiv", "hiv", "hiv", "hiv", "hiv", "hiv", "hiv", "MALARIA"));

    navigateToNextPage();
    verifyPageNumberSelected(2);
    verifyNextAndLastPageLinksDisabled();
    verifyPreviousAndFirstPageLinksEnabled();
    verifyNumberOfLineItemsVisibleOnPage(2);
    verifyCategoryOrderOnPage(asList("category3", ""));
    verifyProductNameOrderOnPage(asList("Indinavir 400mg Tablets12", "Indinavir 400mg Tablets16"));
    verifyProgramOrderOnPage(asList("MALARIA", "VACCINES"));

    productPage.clickCloseSearchResultsButton();
    assertFalse(productPage.isResultDisplayed());

    searchProduct("i");
    productPage.selectSearchOptionProduct();
    productPage.clickSearchIcon();
    testWebDriver.waitForAjax();
    assertEquals("21 matches found for 'i'", productPage.getNResultsMessage());
  }

  @Test(groups = {"admin"})
  public void testCancelAddNewProduct() throws SQLException {
    dbWrapper.assignRight("Admin", "MANAGE_PRODUCT");
    dbWrapper.insertProductGroup("ProductGroup3");
    dbWrapper.insertProductGroup("ProductGroup1");
    dbWrapper.insertProductGroup("productGroup2");
    dbWrapper.updateFieldValue("programs", "name", "hiv", "code", "HIV");
    dbWrapper.updateFieldValue("product_forms", "code", "capsule", "code", "Capsule");

    HomePage homePage = loginPage.loginAs(testData.get(ADMIN), testData.get(PASSWORD));
    productPage = homePage.navigateToProductPage();
    productPage.clickProductAddNewButton();
    assertEquals("Add Product", productPage.getAddNewProductHeader());
    assertEquals("Basic Information", productPage.getBasicInformationLabel());
    assertEquals("Product Code *", productPage.getProductCodeLabel());
    assertEquals("Product primary name *", productPage.getProductPrimaryNameLabel());
    assertEquals("Product type", productPage.getProductTypeLabel());
    assertEquals("Product full name", productPage.getProductFullNameLabel());
    assertEquals("Product group", productPage.getProductGroupLabel());
    assertEquals("Description", productPage.getProductDescriptionLabel());
    assertEquals("Product form", productPage.getProductFormLabel());
    assertEquals("Strength", productPage.getProductStrengthLabel());
    assertEquals("Dosage unit", productPage.getDosageUnitLabel());
    assertEquals("Dispensing unit *", productPage.getProductDispensingUnitLabel());
    assertEquals("Doses per dispensing unit *", productPage.getProductDosesPerDispensingUnitLabel());
    assertEquals("Pack size *", productPage.getProductPackSizeLabel());
    assertEquals("Pack rounding threshold *", productPage.getProductPackRoundingThresholdLabel());
    assertEquals("Round to zero *", productPage.getProductRoundToZeroLabel());
    assertEquals("Active product *", productPage.getProductActiveLabel());
    assertEquals("Full supply *", productPage.getProductFullSupplyLabel());
    assertEquals("Tracer *", productPage.getProductTracerLabel());
    assertEquals("Archived", productPage.getProductArchivedLabel());

    assertEquals(asList("--Select product group--", "ProductGroup1-Name", "productGroup2-Name", "ProductGroup3-Name"),
      productPage.getAllProductGroups());
    assertEquals(asList("--Select product form--", "Ampule", "Bottle", "capsule", "Device", "Drops", "Each", "Implant", "Inhaler",
      "Injectable", "Other", "Patch", "Powder", "Sachet", "Solution", "Tablet", "Tube", "Vial"), productPage.getAllForms());
    assertEquals(asList("--Select dosage Unit--", "cc", "each", "gm", "IU", "mcg", "mg", "ml"), productPage.getAllDosageUnits());

    productPage.clickOtherInfoAccordion();
    assertEquals("Other Information", productPage.getProductOtherInformationLabel());
    assertEquals("Generic product name", productPage.getProductGenericNameLabel());
    assertEquals("Alternate product name", productPage.getProductAlternateNameLabel());
    assertEquals("Alternate product code", productPage.getProductAlternateProductCodeLabel());
    assertEquals("Alternate pack size", productPage.getProductAlternatePackSizeLabel());
    assertEquals("Manufacturer", productPage.getProductManufacturerLabel());
    assertEquals("Manufacturer product code", productPage.getProductManufacturerProductCodeLabel());
    assertEquals("Manufacturer bar code", productPage.getProductManufacturerBarCodeLabel());
    assertEquals("GTIN", productPage.getProductGTINLabel());
    assertEquals("Expected shelf life (months)", productPage.getProductExpectedShelfLifeLabel());
    assertEquals("Product record last updated", productPage.getProductRecordLastUpdatedLabel());
    assertEquals("Alternate MoH bar code", productPage.getProductAlternateMoHBarCodeLabel());
    assertEquals("Contraceptive couple-years of protection", productPage.getProductContraceptiveCoupleYearsOfProtectionLabel());
    assertEquals("Store refrigerated", productPage.getProductStoreRefrigeratedLabel());
    assertEquals("Store at room temperature", productPage.getProductStoreAtRoomTemperatureLabel());
    assertEquals("Pack length (cm)", productPage.getProductPackLengthLabel());
    assertEquals("Pack width (cm)", productPage.getProductPackWidthLabel());
    assertEquals("Hazardous", productPage.getProductHazardousLabel());
    assertEquals("Flammable", productPage.getProductFlammableLabel());
    assertEquals("Pack height (cm)", productPage.getProductPackHeightLabel());
    assertEquals("Pack weight (cm)", productPage.getProductPackWeightLabel());
    assertEquals("Controlled substance", productPage.getProductControlledSubstanceLabel());
    assertEquals("Light sensitive", productPage.getProductLightSensitiveLabel());
    assertEquals("Pack per carton", productPage.getProductPackPerCartonLabel());
    assertEquals("Carton length (cm)", productPage.getProductCartonLengthLabel());
    assertEquals("Approved by WHO", productPage.getProductApprovedByWHOLabel());
    assertEquals("Carton width (cm)", productPage.getProductCartonWidthLabel());
    assertEquals("Carton height (cm)", productPage.getProductCartonHeightLabel());
    assertEquals("Carton per pallet", productPage.getProductCartonsPerPalletLabel());
    assertEquals("Special storage instructions", productPage.getProductSpecialStorageInstructionsLabel());
    assertEquals("Special transport instructions", productPage.getProductSpecialTransportInstructionsLabel());
    assertEquals("", productPage.getProductLastUpdated());

    productPage.clickProgramAssociationAccordion();
    assertEquals("Programs Associated", productPage.getProgramAssociationLabel());
    assertEquals("Program", productPage.getProgramHeaderOnEditPage());
    assertEquals("Category", productPage.getCategoryHeaderOnEditPage());
    assertEquals("Active", productPage.getActiveHeaderOnEditPage());
    assertEquals("Display order", productPage.getDisplayOrderHeaderOnEditPage());
    assertEquals("Doses per month", productPage.getDosesPerMonthHeaderOnEditPage());

    assertEquals(asList("-- Select category --", "anaesthetics", "Antibiotics", "category3"), productPage.getAllAddCategorySelect());
    assertEquals(asList("--Select Program--", "ESSENTIAL MEDICINES", "hiv", "MALARIA", "TB", "VACCINES"), productPage.getAllAddProgramSelect());
    productPage.collapseAll();

    productPage.clickSaveButton();
    assertEquals("There are some errors in the form. Please resolve them.", productPage.getSaveErrorMsg());

    productPage.enterCodeInput("P11");
    productPage.clickCancelButton();

    productPage.enterSearchProductParameter("P11");
    productPage.clickSearchIcon();
    testWebDriver.waitForAjax();
    assertEquals("No matches found for 'P11'", productPage.getNoResultsMessage());

    dbWrapper.updateFieldValue("product_forms", "code", "Capsule", "code", "capsule");
    dbWrapper.updateFieldValue("programs", "name", "HIV", "code", "HIV");
  }

  @Test(groups = {"admin"})
  public void testAddNewProductWithOnlyBasicInfo() throws SQLException {
    dbWrapper.assignRight("Admin", "MANAGE_PRODUCT");
    dbWrapper.insertProduct("P10", "product10");
    dbWrapper.insertProductGroup("ProductGroup1");

    HomePage homePage = loginPage.loginAs(testData.get(ADMIN), testData.get(PASSWORD));
    productPage = homePage.navigateToProductPage();
    productPage.clickProductAddNewButton();

    productPage.enterTypeInput("type");
    productPage.enterFullNameInput("name");
    productPage.enterDescriptionInput("desc");
    productPage.enterStrengthInput("strength");
    productPage.enterDispensingUnitInput("unit");
    productPage.enterDosesPerDispensingUnitInput("10");
    productPage.enterPackSizeInput("10");
    productPage.enterPackRoundingThresholdInput("1");
    productPage.clickRoundToZeroTrueButton();
    productPage.clickActiveTrueButton();
    productPage.clickFullSupplyTrueButton();
    productPage.clickTracerTrueButton();
    productPage.clickArchivedFalseButton();
    productPage.selectProductGroup("ProductGroup1-Name");
    productPage.selectForm("Bottle");
    productPage.selectDosageUnit("mcg");
    productPage.enterCodeInput("P10");
    productPage.enterPrimaryNameInput("product");
    productPage.clickSaveButton();
    assertEquals("Duplicate Product Code", productPage.getSaveErrorMsg());

    productPage.enterCodeInput("P11");
    productPage.clickSaveButton();

    assertEquals("Product \"product Bottle strength mcg\" created successfully.   View Here", productPage.getSaveSuccessMsg());
    productPage.enterSearchProductParameter("P11");
    productPage.clickSearchIcon();
    testWebDriver.waitForAjax();
    productPage.clickName(1);
    assertEquals("product", productPage.getPrimaryNameOnEditPage());
  }

  @Test(groups = {"admin"})
  public void testAddNewProductWithOtherInfo() throws SQLException {
    dbWrapper.assignRight("Admin", "MANAGE_PRODUCT");

    HomePage homePage = loginPage.loginAs(testData.get(ADMIN), testData.get(PASSWORD));
    productPage = homePage.navigateToProductPage();
    productPage.clickProductAddNewButton();
    productPage.expandAll();

    productPage.enterGenericName("generic name");
    productPage.enterAlternateName("alternate name");
    productPage.enterAlternateItemCode("alternate code");
    productPage.enterAlternatePackSize("10");
    productPage.enterManufacturer("manufacturer");
    productPage.enterManufacturerCode("code123");
    productPage.enterManufacturerBarCode("bar!234||");
    productPage.enterGtin("gtin");
    productPage.enterExpectedShelfLife("3");
    productPage.enterMohBarCode("!234|");
    productPage.enterContraceptiveCYP("5");
    productPage.enterPackLength("10.00");
    productPage.enterPackWidth("99.99");
    productPage.enterPackHeight(".9");
    productPage.enterPackWeight("0.9");
    productPage.enterPacksPerCarton("100");
    productPage.enterCartonLength("45");
    productPage.enterCartonWidth("90");
    productPage.enterCartonsPerPallet("8");
    productPage.enterCartonHeight("67");
    productPage.enterSpecialStorageInstructions("storage instructions * storage instructions * storage instructions * " +
      "storage instructions * storage instructions * storage instructions * storage instructions * storage instructions * ");
    productPage.enterSpecialTransportInstructions("transport instruction * transport instruction * transport instruction * " +
      "transport instruction * transport instruction * transport instruction * transport instruction * transport instruction * " +
      "transport instruction * transport instruction * transport instruction");
    productPage.clickStoreRefrigeratedTrueButton();
    productPage.clickStoreRoomTemperatureFalseButton();
    productPage.clickHazardousTrueButton();
    productPage.clickFlammableFalseButton();
    productPage.clickControlledSubstanceTrueButton();
    productPage.clickLightSensitiveFalseButton();
    productPage.clickApprovedByWHOTrueButton();
    productPage.clickSaveButton();
    assertEquals("There are some errors in the form. Please resolve them.", productPage.getSaveErrorMsg());

    productPage.enterDispensingUnitInput("unit");
    productPage.enterDosesPerDispensingUnitInput("10");
    productPage.enterPackSizeInput("10");
    productPage.enterPackRoundingThresholdInput("1");
    productPage.clickRoundToZeroTrueButton();
    productPage.clickActiveTrueButton();
    productPage.clickFullSupplyTrueButton();
    productPage.clickTracerTrueButton();
    productPage.enterCodeInput("P11");
    productPage.enterPrimaryNameInput("product");
    productPage.clickSaveButton();

    assertEquals("Product \"product \" created successfully.   View Here", productPage.getSaveSuccessMsg());
    productPage.enterSearchProductParameter("P11");
    productPage.clickSearchIcon();
    testWebDriver.waitForAjax();
    productPage.clickName(1);
    assertEquals("product", productPage.getPrimaryNameOnEditPage());
    productPage.clickOtherInfoAccordion();
    assertEquals("generic name", productPage.getGenericNameOnEditPage());
    assertEquals("9", productPage.getPackHeightOnEditPage());
  }

  @Test(groups = {"admin"})
  public void testAddNewProductWithProgramAssociation() throws SQLException {
    dbWrapper.assignRight("Admin", "MANAGE_PRODUCT");
    dbWrapper.insertProduct("P10", "product10");
    dbWrapper.insertProductGroup("ProductGroup1");

    HomePage homePage = loginPage.loginAs(testData.get(ADMIN), testData.get(PASSWORD));
    productPage = homePage.navigateToProductPage();
    productPage.clickProductAddNewButton();

    productPage.expandAll();
    productPage.selectAddProgram("VACCINES");
    productPage.selectAddCategory("Antibiotics");
    productPage.enterDisplayOrderAdd("32");
    productPage.enterDosesPerMonthAdd("23");
    productPage.clickActiveAdd();
    productPage.clickProgramProductAdd();

    assertEquals(asList("--Select Program--", "ESSENTIAL MEDICINES", "HIV", "MALARIA", "TB"), productPage.getAllAddProgramSelect());
    assertEquals(asList("-- Select category --", "anaesthetics", "Antibiotics", "category3"), productPage.getAllAddCategorySelect());
    productPage.clickSaveButton();
    assertEquals("There are some errors in the form. Please resolve them.", productPage.getSaveErrorMsg());

    productPage.enterDispensingUnitInput("unit");
    productPage.enterDosesPerDispensingUnitInput("10");
    productPage.enterPackSizeInput("10");
    productPage.enterPackRoundingThresholdInput("1");
    productPage.clickRoundToZeroTrueButton();
    productPage.clickActiveTrueButton();
    productPage.clickFullSupplyTrueButton();
    productPage.clickTracerTrueButton();
    productPage.enterCodeInput("P11");
    productPage.enterPrimaryNameInput("product");
    productPage.clickSaveButton();

    assertEquals("Product \"product \" created successfully.   View Here", productPage.getSaveSuccessMsg());
    productPage.enterSearchProductParameter("P11");
    productPage.clickSearchIcon();
    testWebDriver.waitForAjax();
    productPage.clickName(1);
    assertEquals("product", productPage.getPrimaryNameOnEditPage());
    productPage.clickProgramAssociationAccordion();
    assertEquals("VACCINES", productPage.getProgramSelected(1));
    assertEquals("Antibiotics", productPage.getCategorySelected(1));
    assertEquals("32", productPage.getDisplayOrder(1));
    assertEquals("23", productPage.getDosesPerMonth(1));
    assertTrue(productPage.isProgramProductActive(1));
  }

  @Test(groups = {"admin"})
  public void testEditExistingProduct() throws SQLException {
    dbWrapper.assignRight("Admin", "MANAGE_PRODUCT");
    dbWrapper.insertProduct("P10", "product10");
    dbWrapper.insertProgramProductsWithCategory("P10", "HIV", "anaesthetics", 1);
    dbWrapper.updateFieldValue("products", "modifiedDate", "07-20-2014", "code", "P10");

    HomePage homePage = loginPage.loginAs(testData.get(ADMIN), testData.get(PASSWORD));
    productPage = homePage.navigateToProductPage();
    productPage.enterSearchProductParameter("P10");
    productPage.clickSearchIcon();
    testWebDriver.waitForAjax();
    productPage.clickName(1);

    assertEquals("Edit Product", productPage.getEditProductHeader());
    productPage.enterTypeInput("type");
    productPage.enterPrimaryNameInput("product");
    productPage.expandAll();
    productPage.enterGenericName("generic");
    assertEquals("20/07/2014", productPage.getProductLastUpdated());
    productPage.clickProgramProductEdit(1);
    productPage.clickActiveProgramProductEdit(1);
    assertEquals(asList("-- Select category --", "anaesthetics", "Antibiotics", "category3"), productPage.getAllCategory(1));
    productPage.clickProgramProductEditDone(1);
    productPage.selectAddProgram("MALARIA");
    productPage.selectAddCategory("Antibiotics");
    productPage.enterDosesPerMonthAdd("21");
    productPage.clickProgramProductAdd();
    productPage.clickCancelButton();

    productPage.clickName(1);
    assertEquals("product10", productPage.getPrimaryNameOnEditPage());
    productPage.clickOtherInfoAccordion();
    assertEquals("20/07/2014", productPage.getProductLastUpdated());
    assertEquals("TDF/FTC/EFV", productPage.getGenericNameOnEditPage());
    assertEquals("2", productPage.getPackHeightOnEditPage());

    productPage.clickProgramAssociationAccordion();
    assertTrue(productPage.isProgramProductActive(1));
    productPage.enterTypeInput("type");
    productPage.enterPrimaryNameInput("product");
    productPage.expandAll();
    productPage.enterGenericName("generic");
    productPage.clickProgramProductEdit(1);
    productPage.clickActiveProgramProductEdit(1);
    productPage.selectAddProgram("MALARIA");
    productPage.selectAddCategory("Antibiotics");
    productPage.enterDosesPerMonthAdd("21");
    productPage.clickSaveButton();
    productPage.clickProgramProductAdd();
    assertEquals("Mark all program products as 'Done' before saving the form", productPage.getSaveErrorMsg());

    productPage.clickProgramProductEditDone(1);
    productPage.clickProgramProductEdit(1);
    productPage.enterDisplayOrderNewInput(1, "999");
    productPage.enterDosesPerMonthNewInput(1, "000");
    productPage.clickProgramProductEditCancel(1);
    productPage.clickSaveButton();

    assertEquals("Product \"product Capsule 300/200/600 mg\" updated successfully.   View Here", productPage.getSaveSuccessMsg());
    productPage.clickViewHere();
    assertEquals("product", productPage.getPrimaryNameOnEditPage());
    productPage.clickOtherInfoAccordion();
    assertEquals("generic", productPage.getGenericNameOnEditPage());
    assertEquals("2", productPage.getPackHeightOnEditPage());
    assertEquals((new SimpleDateFormat("dd/MM/yyyy")).format(new Date()), productPage.getProductLastUpdated());
    productPage.clickProgramAssociationAccordion();
    assertEquals("HIV", productPage.getProgramSelected(1));
    assertEquals("anaesthetics", productPage.getCategorySelected(1));
    assertEquals("1", productPage.getDisplayOrder(1));
    assertEquals("30", productPage.getDosesPerMonth(1));
    assertFalse(productPage.isProgramProductActive(1));
    assertEquals("MALARIA", productPage.getProgramSelected(2));
    assertEquals("Antibiotics", productPage.getCategorySelected(2));
    assertEquals("", productPage.getDisplayOrder(2));
    assertEquals("21", productPage.getDosesPerMonth(2));
    assertFalse(productPage.isProgramProductActive(2));
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
