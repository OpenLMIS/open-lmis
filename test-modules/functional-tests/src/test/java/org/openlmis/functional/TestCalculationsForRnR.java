/*
 *
 *  * This program is part of the OpenLMIS logistics management information system platform software.
 *  * Copyright © 2013 VillageReach
 *  *
 *  * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *  *  
 *  * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 *  * You should have received a copy of the GNU Affero General Public License along with this program.  If not, see http://www.gnu.org/licenses.  For additional information contact info@OpenLMIS.org. 
 *
 */

package org.openlmis.functional;

import org.openlmis.UiUtils.TestCaseHelper;
import org.openlmis.pageobjects.*;
import org.openlmis.pageobjects.edi.ConvertOrderPage;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.io.IOException;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import static com.thoughtworks.selenium.SeleneseTestBase.assertEquals;
import static java.lang.Math.round;
import static java.util.Arrays.asList;

public class TestCalculationsForRnR extends TestCaseHelper {

  public static final int NUMBER_OF_DAYS_IN_MONTH = 30;
  private static final int MILLISECONDS_IN_ONE_DAY = 24 * 60 * 60 * 1000;
  public String program = "HIV", userSIC = "storeInCharge", password = "Admin123";
  LoginPage loginPage;
  HomePage homePage;
  InitiateRnRPage initiateRnRPage;

  @BeforeMethod(groups = "requisition")
  public void setUp() throws InterruptedException, SQLException, IOException {
    super.setup();
    List<String> rightsList = asList("CREATE_REQUISITION", "VIEW_REQUISITION", "AUTHORIZE_REQUISITION", "APPROVE_REQUISITION");
    setupTestDataToInitiateRnR(true, program, userSIC, rightsList);
    dbWrapper.updateFieldValue("products", "fullSupply", "true", "code", "P11");
    loginPage = PageObjectFactory.getLoginPage(testWebDriver, baseUrlGlobal);
    homePage = PageObjectFactory.getHomePage(testWebDriver);
    initiateRnRPage = PageObjectFactory.getInitiateRnRPage(testWebDriver);
  }

  @Test(groups = "requisition")
  public void testPatientOptionAndEffectOfChangingPackSize() throws SQLException {
    dbWrapper.updateFieldValue("program_rnr_columns", "rnrOptionId", "2", "label", "New Patients");
    dbWrapper.updateFieldValue("products", "fullSupply", "false", "code", "P11");
    dbWrapper.updateFieldValue("products", "packSize", "5", "code", "P10");
    dbWrapper.updateFieldValue("products", "packSize", "15", "code", "P11");

    HomePage homePage = loginPage.loginAs(userSIC, password);
    homePage.navigateInitiateRnRScreenAndSelectingRequiredFields(program, "Regular");
    InitiateRnRPage initiateRnRPage = homePage.clickProceed();

    enterDetailsForFirstProduct(10, 5, null, 14, 0, 5);
    assertEquals("19", initiateRnRPage.getPeriodicNormalisedConsumption());
    assertEquals("12", initiateRnRPage.getPacksToShip());
    initiateRnRPage.verifyPacksToShip(5);

    initiateRnRPage.addNonFullSupplyLineItems("95", "reason", "antibiotic", "P11", "Antibiotics");
    assertEquals("7", initiateRnRPage.getPacksToShip());

    initiateRnRPage.submitRnR();
    initiateRnRPage.clickOk();

    dbWrapper.updateFieldValue("products", "packSize", "100", "code", "P10");
    dbWrapper.updateFieldValue("products", "packSize", "50", "code", "P10");

    initiateRnRPage.authorizeRnR();
    initiateRnRPage.clickOk();

    ApprovePage approvePage = homePage.navigateToApprove();
    approvePage.clickRequisitionPresentForApproval();
    assertEquals("12", approvePage.getPacksToShip());
    approvePage.editFullSupplyApproveQuantity("6");
    assertEquals("2", approvePage.getPacksToShip());
    approvePage.accessNonFullSupplyTab();
    assertEquals("7", approvePage.getPacksToShip());
    approvePage.editNonFullSupplyApproveQuantity("75");
    assertEquals("5", approvePage.getPacksToShip());
    approvePage.clickApproveButton();
    approvePage.clickOk();

    Long rnrId = (long) dbWrapper.getMaxRnrID();
    assertEquals("2", dbWrapper.getRequisitionLineItemFieldValue(rnrId, "packsToShip", "P10"));
    assertEquals("5", dbWrapper.getRequisitionLineItemFieldValue(rnrId, "packsToShip", "P11"));
    dbWrapper.updateFieldValue("program_rnr_columns", "rnrOptionId", "1", "label", "New Patients");
  }

  @Test(groups = "requisition")
  public void testEffectOfChangingPatientOption() throws SQLException {
    dbWrapper.updateFieldValue("program_rnr_columns", "rnrOptionId", "2", "label", "New Patients");
    dbWrapper.updateFieldValue("products", "fullSupply", "false", "code", "P11");
    dbWrapper.updateFieldValue("products", "packSize", "5", "code", "P10");
    dbWrapper.updateFieldValue("products", "packSize", "15", "code", "P11");

    HomePage homePage = loginPage.loginAs(userSIC, password);
    homePage.navigateInitiateRnRScreenAndSelectingRequiredFields(program, "Regular");
    InitiateRnRPage initiateRnRPage = homePage.clickProceed();

    enterDetailsForFirstProduct(10, 5, null, 14, 0, 5);
    assertEquals("19", initiateRnRPage.getPeriodicNormalisedConsumption());
    assertEquals("12", initiateRnRPage.getPacksToShip());
    initiateRnRPage.verifyPacksToShip(5);

    initiateRnRPage.addNonFullSupplyLineItems("95", "reason", "antibiotic", "P11", "Antibiotics");
    assertEquals("7", initiateRnRPage.getPacksToShip());

    initiateRnRPage.submitRnR();
    initiateRnRPage.clickOk();

    dbWrapper.updateFieldValue("program_rnr_columns", "rnrOptionId", "1", "label", "New Patients");
    testWebDriver.refresh();
    initiateRnRPage.clickFullSupplyTab();
    assertEquals("29", initiateRnRPage.getPeriodicNormalisedConsumption());
    assertEquals("18", initiateRnRPage.getPacksToShip());

    initiateRnRPage.authorizeRnR();
    initiateRnRPage.clickOk();

    dbWrapper.updateFieldValue("program_rnr_columns", "rnrOptionId", "2", "label", "New Patients");
    testWebDriver.refresh();
    ApprovePage approvePage = homePage.navigateToApprove();
    approvePage.clickRequisitionPresentForApproval();
    assertEquals("18", approvePage.getPacksToShip());
    approvePage.editFullSupplyApproveQuantity("50");
    assertEquals("10", approvePage.getPacksToShip());
    approvePage.accessNonFullSupplyTab();
    assertEquals("7", approvePage.getPacksToShip());
    approvePage.editNonFullSupplyApproveQuantity("75");
    assertEquals("5", approvePage.getPacksToShip());
    approvePage.clickApproveButton();
    approvePage.clickOk();

    Long rnrId = (long) dbWrapper.getMaxRnrID();
    assertEquals("10", dbWrapper.getRequisitionLineItemFieldValue(rnrId, "packsToShip", "P10"));
    assertEquals("5", dbWrapper.getRequisitionLineItemFieldValue(rnrId, "packsToShip", "P11"));
    dbWrapper.updateFieldValue("program_rnr_columns", "rnrOptionId", "1", "label", "New Patients");
  }

  @Test(groups = "requisition")
  public void testEffectOfChangingDosesPerDispensingUnit() throws SQLException {
    dbWrapper.updateFieldValue("products", "fullSupply", "false", "code", "P11");
    dbWrapper.updateFieldValue("products", "dosesPerDispensingUnit", "5", "code", "P10");

    HomePage homePage = loginPage.loginAs(userSIC, password);
    homePage.navigateInitiateRnRScreenAndSelectingRequiredFields(program, "Regular");
    InitiateRnRPage initiateRnRPage = homePage.clickProceed();

    enterDetailsForFirstProduct(10, 5, null, 14, 0, 5);
    assertEquals("14", initiateRnRPage.getPacksToShip());
    initiateRnRPage.verifyPacksToShip(10);

    initiateRnRPage.submitRnR();
    initiateRnRPage.clickOk();
    dbWrapper.updateFieldValue("products", "dosesPerDispensingUnit", "15", "code", "P10");

    initiateRnRPage.authorizeRnR();
    initiateRnRPage.clickOk();
    dbWrapper.updateFieldValue("products", "packSize", "100", "code", "P10");

    ApprovePage approvePage = homePage.navigateToApprove();
    approvePage.clickRequisitionPresentForApproval();
    assertEquals("14", approvePage.getPacksToShip());
    approvePage.clickApproveButton();
    approvePage.clickOk();

    Long rnrId = (long) dbWrapper.getMaxRnrID();
    assertEquals("14", dbWrapper.getRequisitionLineItemFieldValue(rnrId, "packsToShip", "P10"));
  }

  @Test(groups = "requisition")
  public void testEffectOfChangingRoundToZeroFlagWhenTrueInitially() throws SQLException {
    dbWrapper.updateFieldValue("products", "fullSupply", "false", "code", "P11");
    dbWrapper.updateFieldValue("products", "roundToZero", "true", "code", "P10");
    dbWrapper.updateFieldValue("products", "roundToZero", "true", "code", "P11");

    HomePage homePage = loginPage.loginAs(userSIC, password);
    homePage.navigateInitiateRnRScreenAndSelectingRequiredFields(program, "Regular");
    InitiateRnRPage initiateRnRPage = homePage.clickProceed();

    enterDetailsForFirstProduct(0, 0, null, 0, 0, null);
    assertEquals("0", initiateRnRPage.getPacksToShip());
    initiateRnRPage.verifyPacksToShip(10);

    initiateRnRPage.addNonFullSupplyLineItems("0", "reason", "antibiotic", "P11", "Antibiotics");
    assertEquals("0", initiateRnRPage.getPacksToShip());

    initiateRnRPage.submitRnR();
    initiateRnRPage.clickOk();

    dbWrapper.updateFieldValue("products", "roundToZero", "false", "code", "P10");
    dbWrapper.updateFieldValue("products", "roundToZero", "false", "code", "P11");

    initiateRnRPage.authorizeRnR();
    initiateRnRPage.clickOk();

    ApprovePage approvePage = homePage.navigateToApprove();
    approvePage.clickRequisitionPresentForApproval();
    approvePage.editFullSupplyApproveQuantity("0");
    assertEquals("0", approvePage.getPacksToShip());
    approvePage.editNonFullSupplyApproveQuantity("0");
    assertEquals("0", approvePage.getPacksToShip());
    approvePage.clickApproveButton();
    approvePage.clickOk();

    Long rnrId = (long) dbWrapper.getMaxRnrID();
    assertEquals("0", dbWrapper.getRequisitionLineItemFieldValue(rnrId, "packsToShip", "P10"));
    assertEquals("0", dbWrapper.getRequisitionLineItemFieldValue(rnrId, "packsToShip", "P11"));
  }

  @Test(groups = "requisition")
  public void testEffectOfChangingRoundToZeroFlagWhenFalseInitially() throws SQLException {
    dbWrapper.updateFieldValue("products", "fullSupply", "false", "code", "P11");
    dbWrapper.updateFieldValue("products", "roundToZero", "false", "code", "P10");
    dbWrapper.updateFieldValue("products", "roundToZero", "false", "code", "P11");

    HomePage homePage = loginPage.loginAs(userSIC, password);
    homePage.navigateInitiateRnRScreenAndSelectingRequiredFields(program, "Regular");
    InitiateRnRPage initiateRnRPage = homePage.clickProceed();

    enterDetailsForFirstProduct(0, 0, null, 0, 0, 0);
    assertEquals("1", initiateRnRPage.getPacksToShip());
    initiateRnRPage.verifyPacksToShip(10);

    initiateRnRPage.addNonFullSupplyLineItems("0", "reason", "antibiotic", "P11", "Antibiotics");
    assertEquals("1", initiateRnRPage.getPacksToShip());

    initiateRnRPage.submitRnR();
    initiateRnRPage.clickOk();

    dbWrapper.updateFieldValue("products", "roundToZero", "true", "code", "P10");
    dbWrapper.updateFieldValue("products", "roundToZero", "true", "code", "P11");

    initiateRnRPage.authorizeRnR();
    initiateRnRPage.clickOk();

    ApprovePage approvePage = homePage.navigateToApprove();
    approvePage.clickRequisitionPresentForApproval();
    approvePage.editFullSupplyApproveQuantity("0");
    assertEquals("1", approvePage.getPacksToShip());
    approvePage.editNonFullSupplyApproveQuantity("0");
    assertEquals("1", approvePage.getPacksToShip());
    approvePage.clickApproveButton();
    approvePage.clickOk();

    Long rnrId = (long) dbWrapper.getMaxRnrID();
    assertEquals("1", dbWrapper.getRequisitionLineItemFieldValue(rnrId, "packsToShip", "P10"));
    assertEquals("1", dbWrapper.getRequisitionLineItemFieldValue(rnrId, "packsToShip", "P11"));
  }

  @Test(groups = "requisition")
  public void testEffectOfChangingPackRoundingThreshold() throws SQLException {
    dbWrapper.updateFieldValue("products", "fullSupply", "false", "code", "P11");
    dbWrapper.updateFieldValue("products", "packRoundingThreshold", "5", "code", "P10");
    dbWrapper.updateFieldValue("products", "packRoundingThreshold", "7", "code", "P11");

    HomePage homePage = loginPage.loginAs(userSIC, password);
    homePage.navigateInitiateRnRScreenAndSelectingRequiredFields(program, "Regular");
    InitiateRnRPage initiateRnRPage = homePage.clickProceed();

    assertEquals(null, dbWrapper.getAttributeFromTable("requisition_line_items", "previousStockInHand", "productCode", "P10"));
    assertEquals(null, dbWrapper.getAttributeFromTable("requisition_line_items", "beginningBalance", "productCode", "P10"));

    enterDetailsForFirstProduct(10, 5, null, 14, 0, 5);
    assertEquals("9", initiateRnRPage.getPacksToShip());
    initiateRnRPage.verifyPacksToShip(10);

    initiateRnRPage.addNonFullSupplyLineItems("98", "reason", "antibiotic", "P11", "Antibiotics");
    assertEquals("10", initiateRnRPage.getPacksToShip());

    initiateRnRPage.submitRnR();
    initiateRnRPage.clickOk();

    assertEquals(null, dbWrapper.getAttributeFromTable("requisition_line_items", "previousStockInHand", "productCode", "P10"));
    assertEquals(10, dbWrapper.getAttributeFromTable("requisition_line_items", "beginningBalance", "productCode", "P10"));

    dbWrapper.updateFieldValue("products", "packRoundingThreshold", "7", "code", "P10");
    dbWrapper.updateFieldValue("products", "packRoundingThreshold", "9", "code", "P11");

    initiateRnRPage.authorizeRnR();
    initiateRnRPage.clickOk();

    ApprovePage approvePage = homePage.navigateToApprove();
    approvePage.clickRequisitionPresentForApproval();
    approvePage.editFullSupplyApproveQuantity("86");
    assertEquals("9", approvePage.getPacksToShip());
    approvePage.editNonFullSupplyApproveQuantity("98");
    assertEquals("10", approvePage.getPacksToShip());
    approvePage.clickApproveButton();
    approvePage.clickOk();

    Long rnrId = (long) dbWrapper.getMaxRnrID();
    assertEquals("9", dbWrapper.getRequisitionLineItemFieldValue(rnrId, "packsToShip", "P10"));
    assertEquals("10", dbWrapper.getRequisitionLineItemFieldValue(rnrId, "packsToShip", "P11"));
  }

  @Test(groups = "requisition")
  public void testCalculationWhenMIs1WithStockOnHandCalculatedAndGZero() throws SQLException {
    dbWrapper.updateConfigureTemplate("HIV", "source", "U", "true", "quantityDispensed");
    dbWrapper.updateConfigureTemplate("HIV", "source", "C", "true", "stockInHand");
    dbWrapper.updateFieldValue("products", "dosesPerDispensingUnit", "0", "code", "P10");
    dbWrapper.updateFieldValue("products", "fullSupply", "false", "code", "P11");

    HomePage homePage = loginPage.loginAs(userSIC, password);
    homePage.navigateInitiateRnRScreenAndSelectingRequiredFields(program, "Regular");
    InitiateRnRPage initiateRnRPage = homePage.clickProceed();

    enterDetailsForFirstProduct(10, 5, null, 14, 0, 5);

    initiateRnRPage.verifyNormalizedConsumptionForFirstProduct(164);
    initiateRnRPage.verifyAmcForFirstProduct(164);

    submitAndAuthorizeRnR();

    verifyNormalizedConsumptionAndAmcInDatabase(164, 164, "P10");
    assertEquals("164", dbWrapper.getAttributeFromTable("requisition_line_items", "periodNormalizedConsumption", "productCode", "P10"));
  }

  @Test(groups = "requisition")
  public void testCalculationWhenNoPreviousPeriod() throws SQLException {
    dbWrapper.updateConfigureTemplate("HIV", "source", "U", "true", "quantityDispensed");
    dbWrapper.updateConfigureTemplate("HIV", "source", "C", "true", "stockInHand");
    dbWrapper.deleteRowFromTable("processing_periods", "name", "Period2");
    dbWrapper.insertProcessingPeriod("currentPeriod", "current period", "2013-01-31", "2016-01-31", 1, "M");
    dbWrapper.updateFieldValue("products", "fullSupply", "false", "code", "P11");

    HomePage homePage = loginPage.loginAs(userSIC, password);
    homePage.navigateInitiateRnRScreenAndSelectingRequiredFields(program, "Regular");
    InitiateRnRPage initiateRnRPage = homePage.clickProceed();

    enterDetailsForFirstProduct(10, 5, null, 14, 0, 0);
    submitAndAuthorizeRnR();

    initiateRnRPage.verifyNormalizedConsumptionForFirstProduct(14);
    initiateRnRPage.verifyAmcForFirstProduct(14);

    verifyNormalizedConsumptionAndAmcInDatabase(14, 14, "P10");
  }

  @Test(groups = "requisition")
  public void testCalculationWhenFieldsNotVisibleAndSameProductInDifferentPrograms() throws SQLException {
    dbWrapper.updateConfigureTemplate("HIV", "source", "C", "false", "quantityDispensed");
    dbWrapper.updateConfigureTemplate("HIV", "source", "U", "true", "stockInHand");
    dbWrapper.updateConfigureTemplate("HIV", "source", "C", "false", "normalizedConsumption");
    dbWrapper.updateConfigureTemplate("HIV", "source", "C", "false", "amc");
    dbWrapper.insertProgramProduct("P10", "ESS_MEDS", "10", "true");
    dbWrapper.insertFacilityApprovedProduct("P10", "ESS_MEDS", "lvl3_hospital");
    dbWrapper.configureTemplate("ESS_MEDS");
    dbWrapper.updateConfigureTemplate("ESS_MEDS", "source", "C", "false", "quantityDispensed");
    dbWrapper.updateConfigureTemplate("ESS_MEDS", "source", "U", "true", "stockInHand");
    dbWrapper.updateConfigureTemplate("ESS_MEDS", "source", "C", "false", "normalizedConsumption");
    dbWrapper.updateConfigureTemplate("ESS_MEDS", "source", "C", "false", "amc");
    dbWrapper.insertRequisitionGroupProgramScheduleForProgramAfterDelete("RG1", "ESS_MEDS", "M");
    dbWrapper.insertRoleAssignmentForSupervisoryNode(userSIC, "store in-charge", null, "ESS_MEDS");
    dbWrapper.updateFieldValue("products", "fullSupply", "false", "code", "P11");

    HomePage homePage = loginPage.loginAs(userSIC, password);
    homePage.navigateInitiateRnRScreenAndSelectingRequiredFields(program, "Regular");
    homePage.clickProceed();

    enterDetailsForFirstProduct(10, 5, 7, null, 20, 10);
    submitAndAuthorizeRnR();

    verifyNormalizedConsumptionAndAmcInDatabase(54, 54, "P10");

    homePage.navigateInitiateRnRScreenAndSelectingRequiredFields("ESSENTIAL MEDICINES", "Regular");
    homePage.clickProceed();

    enterDetailsForFirstProduct(10, 5, 2, null, 0, 0);
    submitAndAuthorizeRnR();

    verifyNormalizedConsumptionAndAmcInDatabase(13, 13, "P10");
  }

  @Test(groups = "requisition")
  public void testCalculationWhenMIs1WithStockOnHandAndQuantityConsumedUserInputAndMultipleProductsAndXLargerThan30M() throws SQLException {
    dbWrapper.updateConfigureTemplate("HIV", "source", "U", "true", "quantityDispensed");
    dbWrapper.updateConfigureTemplate("HIV", "source", "U", "true", "stockInHand");
    dbWrapper.updateConfigureTemplateValidationFlag("HIV", "true");
    dbWrapper.updateFieldValue("products", "dosesPerDispensingUnit", "7", "code", "P10");
    dbWrapper.insertProcessingPeriod("period3", "feb2013", "2013-01-31", "2013-02-28", 1, "M");

    HomePage homePage = loginPage.loginAs(userSIC, password);
    homePage.navigateInitiateRnRScreenAndSelectingRequiredFields(program, "Regular");
    InitiateRnRPage initiateRnRPage = homePage.clickProceed();

    enterDetailsForFirstProduct(15, 5, 2, 18, 45, 11);
    enterDetailsForSecondProduct(8, 5, 5, 8, 20, 10);
    submitAndAuthorizeRnR();

    initiateRnRPage.verifyNormalizedConsumptionForFirstProduct(62);
    initiateRnRPage.verifyAmcForFirstProduct(62);

    initiateRnRPage.verifyNormalizedConsumptionForSecondProduct(54);
    initiateRnRPage.verifyAmcForSecondProduct(54);

    verifyNormalizedConsumptionAndAmcInDatabase(62, 62, "P10");
    verifyNormalizedConsumptionAndAmcInDatabase(54, 54, "P11");

    homePage.navigateInitiateRnRScreenAndSelectingRequiredFields(program, "Regular");
    initiateRnRPage = homePage.clickProceed();
    initiateRnRPage.skipSingleProduct(2);
    enterDetailsForFirstProduct(20, 0, 7, 13, 23, 0);
    submitAndAuthorizeRnR();

    initiateRnRPage.verifyNormalizedConsumptionForFirstProduct(56);
    initiateRnRPage.verifyAmcForFirstProduct(59);
    verifyNormalizedConsumptionAndAmcInDatabase(56, 59, "P10");
  }

  @Test(groups = "requisition")
  public void testCalculationWhenMIs1WithStockOnHandAndQuantityConsumedUserInputAndRnRExistsForDifferentFacility() throws SQLException {
    dbWrapper.updateConfigureTemplate("HIV", "source", "U", "true", "quantityDispensed");
    dbWrapper.updateConfigureTemplate("HIV", "source", "U", "true", "stockInHand");
    dbWrapper.updateConfigureTemplateValidationFlag("HIV", "false");
    dbWrapper.updateFieldValue("products", "fullSupply", "false", "code", "P11");

    HomePage homePage = loginPage.loginAs(userSIC, password);
    homePage.navigateInitiateRnRScreenAndSelectingRequiredFields(program, "Regular");
    InitiateRnRPage initiateRnRPage = homePage.clickProceed();

    enterDetailsForFirstProduct(7, 5, 7, 8, 20, 10);
    submitAndAuthorizeRnR();

    initiateRnRPage.verifyNormalizedConsumptionForFirstProduct(54);
    initiateRnRPage.verifyAmcForFirstProduct(54);
    verifyNormalizedConsumptionAndAmcInDatabase(54, 54, "P10");

    dbWrapper.insertSupervisoryNode("F11", "N2", "Node2", "N1");
    dbWrapper.updateSupervisoryNodeForRequisitionGroup("RG2", "N2");
    dbWrapper.insertRoleAssignmentForSupervisoryNode(userSIC, "store in-charge", "N2", "HIV");

    homePage.navigateAndInitiateRnrForSupervisedFacility("HIV");
    homePage.selectFacilityForSupervisoryNodeRnR("F11 - Central Hospital");
    initiateRnRPage = homePage.clickProceed();

    enterDetailsForFirstProduct(10, 10, 0, 13, 23, 0);
    submitAndAuthorizeRnR();

    initiateRnRPage.verifyNormalizedConsumptionForFirstProduct(56);
    initiateRnRPage.verifyAmcForFirstProduct(56);
    verifyNormalizedConsumptionAndAmcInDatabase(56, 56, "P10");
  }

  @Test(groups = "requisition")
  public void testCalculationOfAmcTrackingWhenMIs1() throws SQLException {
    dbWrapper.updateFieldValue("products", "fullSupply", "false", "code", "P11");
    dbWrapper.insertProcessingPeriod("feb13", "feb13", "2013-01-31", "2013-02-28", 1, "M");
    dbWrapper.insertProcessingPeriod("mar13", "mar13", "2013-03-01", "2013-03-31", 1, "M");
    dbWrapper.insertProcessingPeriod("apr13", "apr13", "2013-04-01", "2013-04-30", 1, "M");
    dbWrapper.insertProcessingPeriod("may13", "may13", "2013-05-01", "2013-05-31", 1, "M");
    dbWrapper.insertProcessingPeriod("jun13", "jun13", "2013-06-01", "2013-06-30", 1, "M");
    dbWrapper.insertProcessingPeriod("current", "current", "2013-07-01", "2016-02-28", 4, "M");

    HomePage homePage = loginPage.loginAs(userSIC, password);
    homePage.navigateInitiateRnRScreenAndSelectingRequiredFields(program, "Regular");
    InitiateRnRPage initiateRnRPage = homePage.clickProceed();
    testWebDriver.waitForAjax();

    enterDetailsForFirstProduct(10, 5, null, 8, 20, 0);
    submitAndAuthorizeRnR();

    initiateRnRPage.verifyNormalizedConsumptionForFirstProduct(24);
    initiateRnRPage.verifyAmcForFirstProduct(24);
    verifyNormalizedConsumptionAndAmcInDatabase(24, 24, "P10");

    dbWrapper.updateCreatedDateInRequisitionStatusChanges("2013-01-08", (long) dbWrapper.getMaxRnrID());

    homePage.navigateInitiateRnRScreenAndSelectingRequiredFields(program, "Regular");
    initiateRnRPage = homePage.clickProceed();
    testWebDriver.waitForAjax();

    assertEquals("7", dbWrapper.getAttributeFromTable("requisition_line_items", "previousStockInHand", "rnrId", String.valueOf(dbWrapper.getMaxRnrID())));
    assertEquals("7", dbWrapper.getAttributeFromTable("requisition_line_items", "beginningBalance", "rnrId", String.valueOf(dbWrapper.getMaxRnrID())));

    enterDetailsForFirstProduct(10, 5, null, 10, 0, 2);
    submitAndAuthorizeRnR();

    assertEquals("7", dbWrapper.getAttributeFromTable("requisition_line_items", "previousStockInHand", "rnrId", String.valueOf(dbWrapper.getMaxRnrID())));
    assertEquals("10", dbWrapper.getAttributeFromTable("requisition_line_items", "beginningBalance", "rnrId", String.valueOf(dbWrapper.getMaxRnrID())));

    initiateRnRPage.verifyNormalizedConsumptionForFirstProduct(16);
    initiateRnRPage.verifyAmcForFirstProduct(20);
    verifyNormalizedConsumptionAndAmcInDatabase(16, 20, "P10");

    dbWrapper.updateCreatedDateInRequisitionStatusChanges("2013-02-08", (long) dbWrapper.getMaxRnrID());

    homePage.navigateInitiateRnRScreenAndSelectingRequiredFields(program, "Regular");
    initiateRnRPage = homePage.clickProceed();
    testWebDriver.waitForAjax();

    enterDetailsForFirstProduct(10, 5, null, 5, 20, 0);
    submitAndAuthorizeRnR();

    initiateRnRPage.verifyNormalizedConsumptionForFirstProduct(15);
    initiateRnRPage.verifyAmcForFirstProduct(18);
    verifyNormalizedConsumptionAndAmcInDatabase(15, 18, "P10");

    dbWrapper.updateCreatedDateInRequisitionStatusChanges("2013-03-08", (long) dbWrapper.getMaxRnrID());

    homePage.navigateInitiateRnRScreenAndSelectingRequiredFields(program, "Regular");
    initiateRnRPage = homePage.clickProceed();
    testWebDriver.waitForAjax();

    enterDetailsForFirstProduct(5, 5, null, 0, 20, 0);
    submitAndAuthorizeRnR();

    initiateRnRPage.verifyNormalizedConsumptionForFirstProduct(0);
    initiateRnRPage.verifyAmcForFirstProduct(10);
    verifyNormalizedConsumptionAndAmcInDatabase(0, 10, "P10");
  }

  @Test(groups = "requisition")
  public void testCalculationTrackingOfAmcWhenMIs2() throws SQLException {
    dbWrapper.updateFieldValue("products", "fullSupply", "false", "code", "P11");
    dbWrapper.deleteRowFromTable("processing_periods", "name", "Period2");
    dbWrapper.insertProcessingPeriod("feb13", "feb13", "2013-01-31", "2013-02-28", 2, "M");
    dbWrapper.insertProcessingPeriod("mar13", "mar13", "2013-03-01", "2013-03-31", 2, "M");
    dbWrapper.insertProcessingPeriod("apr13", "apr13", "2013-04-01", "2013-04-30", 2, "M");
    dbWrapper.insertProcessingPeriod("may13", "may13", "2013-05-01", "2013-05-31", 2, "M");

    HomePage homePage = loginPage.loginAs(userSIC, password);
    homePage.navigateInitiateRnRScreenAndSelectingRequiredFields(program, "Regular");
    InitiateRnRPage initiateRnRPage = homePage.clickProceed();

    enterDetailsForFirstProduct(10, 5, null, 8, 0, 0);
    submitAndAuthorizeRnR();

    initiateRnRPage.verifyNormalizedConsumptionForFirstProduct(4);
    initiateRnRPage.verifyAmcForFirstProduct(4);
    verifyNormalizedConsumptionAndAmcInDatabase(4, 4, "P10");

    dbWrapper.updateCreatedDateInRequisitionStatusChanges("2013-02-08", (long) dbWrapper.getMaxRnrID());

    homePage.navigateInitiateRnRScreenAndSelectingRequiredFields(program, "Regular");
    initiateRnRPage = homePage.clickProceed();

    enterDetailsForFirstProduct(10, 5, null, 0, 0, 1);
    submitAndAuthorizeRnR();

    initiateRnRPage.verifyNormalizedConsumptionForFirstProduct(3);
    initiateRnRPage.verifyAmcForFirstProduct(4);
    verifyNormalizedConsumptionAndAmcInDatabase(3, 4, "P10");

    dbWrapper.updateCreatedDateInRequisitionStatusChanges("2013-03-08", (long) dbWrapper.getMaxRnrID());

    homePage.navigateInitiateRnRScreenAndSelectingRequiredFields(program, "Regular");
    initiateRnRPage = homePage.clickProceed();

    enterDetailsForFirstProduct(10, 5, null, 5, 20, 0);
    submitAndAuthorizeRnR();

    initiateRnRPage.verifyNormalizedConsumptionForFirstProduct(4);
    initiateRnRPage.verifyAmcForFirstProduct(4);
    verifyNormalizedConsumptionAndAmcInDatabase(4, 4, "P10");
  }

  @Test(groups = "requisition")
  public void testCalculationTrackingOfAmcWhenMIs3() throws SQLException {
    dbWrapper.updateConfigureTemplate("HIV", "source", "C", "true", "periodNormalizedConsumption");
    dbWrapper.updateFieldValue("products", "fullSupply", "false", "code", "P11");
    dbWrapper.deleteRowFromTable("processing_periods", "name", "Period2");
    dbWrapper.insertProcessingPeriod("feb13", "feb13", "2013-01-31", "2013-02-28", 3, "M");
    dbWrapper.insertProcessingPeriod("mar13", "mar13", "2013-03-01", "2013-03-31", 3, "M");
    dbWrapper.insertProcessingPeriod("apr13", "apr13", "2013-04-01", "2013-04-30", 3, "M");

    HomePage homePage = loginPage.loginAs(userSIC, password);
    homePage.navigateInitiateRnRScreenAndSelectingRequiredFields(program, "Regular");
    InitiateRnRPage initiateRnRPage = homePage.clickProceed();

    enterDetailsForFirstProduct(10, 5, null, 8, 0, 1);
    assertEquals("18", initiateRnRPage.getPeriodicNormalisedConsumption());
    submitAndAuthorizeRnR();

    initiateRnRPage.verifyNormalizedConsumptionForFirstProduct(6);
    initiateRnRPage.verifyAmcForFirstProduct(6);
    verifyNormalizedConsumptionAndAmcInDatabase(6, 6, "P10");
    assertEquals("18", dbWrapper.getRequisitionLineItemFieldValue((long) dbWrapper.getMaxRnrID(), "periodNormalizedConsumption", "P10"));

    dbWrapper.updateCreatedDateInRequisitionStatusChanges("2013-02-08", (long) dbWrapper.getMaxRnrID());

    homePage.navigateInitiateRnRScreenAndSelectingRequiredFields(program, "Regular");
    initiateRnRPage = homePage.clickProceed();

    enterDetailsForFirstProduct(10, 5, null, 1, 1, 0);
    assertEquals("0", initiateRnRPage.getPeriodicNormalisedConsumption());
    submitAndAuthorizeRnR();

    initiateRnRPage.verifyNormalizedConsumptionForFirstProduct(0);
    initiateRnRPage.verifyAmcForFirstProduct(0);
    verifyNormalizedConsumptionAndAmcInDatabase(0, 0, "P10");
    assertEquals("0", dbWrapper.getRequisitionLineItemFieldValue((long) dbWrapper.getMaxRnrID(), "periodNormalizedConsumption", "P10"));
  }

  @Test(groups = "requisition")
  public void testCalculationTrackingOfAmcWhenMIs5() throws SQLException {
    dbWrapper.updateFieldValue("products", "fullSupply", "false", "code", "P11");
    dbWrapper.deleteRowFromTable("processing_periods", "name", "Period2");
    dbWrapper.insertProcessingPeriod("feb13", "feb13", "2013-01-31", "2013-02-28", 5, "M");
    dbWrapper.insertProcessingPeriod("mar13", "mar13", "2013-03-01", "2013-03-31", 5, "M");
    dbWrapper.insertProcessingPeriod("apr13", "apr13", "2013-04-01", "2013-04-30", 5, "M");

    HomePage homePage = loginPage.loginAs(userSIC, password);
    homePage.navigateInitiateRnRScreenAndSelectingRequiredFields(program, "Regular");
    InitiateRnRPage initiateRnRPage = homePage.clickProceed();

    enterDetailsForFirstProduct(10, 5, null, 8, 0, 0);
    submitAndAuthorizeRnR();

    initiateRnRPage.verifyNormalizedConsumptionForFirstProduct(2);
    initiateRnRPage.verifyAmcForFirstProduct(2);
    verifyNormalizedConsumptionAndAmcInDatabase(2, 2, "P10");

    dbWrapper.updateCreatedDateInRequisitionStatusChanges("2013-02-08", (long) dbWrapper.getMaxRnrID());

    homePage.navigateInitiateRnRScreenAndSelectingRequiredFields(program, "Regular");
    initiateRnRPage = homePage.clickProceed();

    enterDetailsForFirstProduct(10, 5, null, 0, 0, 0);
    submitAndAuthorizeRnR();

    initiateRnRPage.verifyNormalizedConsumptionForFirstProduct(0);
    initiateRnRPage.verifyAmcForFirstProduct(0);
    verifyNormalizedConsumptionAndAmcInDatabase(0, 0, "P10");
  }

  @Test(groups = "requisition")
  public void testCalculationOfAmcNoTrackingForMoreThan5PeriodsWhenMIs1() throws SQLException {
    dbWrapper.updateFieldValue("products", "fullSupply", "false", "code", "P11");
    dbWrapper.insertProcessingPeriod("feb13", "feb13", "2013-01-31", "2013-02-28", 1, "M");
    dbWrapper.insertProcessingPeriod("mar13", "mar13", "2013-03-01", "2013-03-31", 1, "M");
    dbWrapper.insertProcessingPeriod("apr13", "apr13", "2013-04-01", "2013-04-30", 1, "M");
    dbWrapper.insertProcessingPeriod("may13", "may13", "2013-05-01", "2013-05-31", 1, "M");
    dbWrapper.insertProcessingPeriod("jun13", "jun13", "2013-06-01", "2013-06-30", 1, "M");
    dbWrapper.insertProcessingPeriod("current", "current", "2013-07-01", "2016-02-28", 4, "M");

    HomePage homePage = loginPage.loginAs(userSIC, password);
    homePage.navigateInitiateRnRScreenAndSelectingRequiredFields(program, "Regular");
    InitiateRnRPage initiateRnRPage = homePage.clickProceed();

    enterDetailsForFirstProduct(10, 5, null, 8, 20, 0);
    submitAndAuthorizeRnR();

    initiateRnRPage.verifyNormalizedConsumptionForFirstProduct(24);
    initiateRnRPage.verifyAmcForFirstProduct(24);
    verifyNormalizedConsumptionAndAmcInDatabase(24, 24, "P10");

    dbWrapper.updateCreatedDateInRequisitionStatusChanges("2013-01-08", (long) dbWrapper.getMaxRnrID());

    skipAllProductsAndAuthorizeRnr();
    dbWrapper.updateCreatedDateInRequisitionStatusChanges("2013-02-08", (long) dbWrapper.getMaxRnrID());

    skipAllProductsAndAuthorizeRnr();
    dbWrapper.updateCreatedDateInRequisitionStatusChanges("2013-03-08", (long) dbWrapper.getMaxRnrID());

    skipAllProductsAndAuthorizeRnr();
    dbWrapper.updateCreatedDateInRequisitionStatusChanges("2013-04-08", (long) dbWrapper.getMaxRnrID());

    skipAllProductsAndAuthorizeRnr();
    dbWrapper.updateCreatedDateInRequisitionStatusChanges("2013-05-08", (long) dbWrapper.getMaxRnrID());

    skipAllProductsAndAuthorizeRnr();
    dbWrapper.updateCreatedDateInRequisitionStatusChanges("2013-06-08", (long) dbWrapper.getMaxRnrID());

    homePage.navigateInitiateRnRScreenAndSelectingRequiredFields(program, "Regular");
    initiateRnRPage = homePage.clickProceed();

    enterDetailsForFirstProduct(10, 5, null, 0, 0, 0);
    submitAndAuthorizeRnR();

    initiateRnRPage.verifyNormalizedConsumptionForFirstProduct(0);
    initiateRnRPage.verifyAmcForFirstProduct(0);
    verifyNormalizedConsumptionAndAmcInDatabase(0, 0, "P10");
  }

  private void skipAllProductsAndAuthorizeRnr() {
    homePage.navigateInitiateRnRScreenAndSelectingRequiredFields(program, "Regular");
    InitiateRnRPage initiateRnRPage = homePage.clickProceed();

    initiateRnRPage.skipAllProduct();
    submitAndAuthorizeRnR();
  }

  @Test(groups = "requisition")
  public void testCalculationAmcNoTrackingForMoreThan2MWhenMIs2() throws SQLException {
    dbWrapper.updateFieldValue("products", "fullSupply", "false", "code", "P11");
    dbWrapper.deleteRowFromTable("processing_periods", "name", "Period2");
    dbWrapper.insertProcessingPeriod("feb13", "feb13", "2013-01-31", "2013-02-28", 2, "M");
    dbWrapper.insertProcessingPeriod("mar13", "mar13", "2013-03-01", "2013-03-31", 2, "M");
    dbWrapper.insertProcessingPeriod("apr13", "apr13", "2013-04-01", "2013-04-30", 2, "M");
    dbWrapper.insertProcessingPeriod("may13", "may13", "2013-05-01", "2013-05-31", 2, "M");

    HomePage homePage = loginPage.loginAs(userSIC, password);
    homePage.navigateInitiateRnRScreenAndSelectingRequiredFields(program, "Regular");
    InitiateRnRPage initiateRnRPage = homePage.clickProceed();

    enterDetailsForFirstProduct(10, 5, null, 8, 0, 0);
    submitAndAuthorizeRnR();

    initiateRnRPage.verifyNormalizedConsumptionForFirstProduct(4);
    initiateRnRPage.verifyAmcForFirstProduct(4);
    verifyNormalizedConsumptionAndAmcInDatabase(4, 4, "P10");

    dbWrapper.updateCreatedDateInRequisitionStatusChanges("2013-02-08", (long) dbWrapper.getMaxRnrID());

    skipAllProductsAndAuthorizeRnr();
    dbWrapper.updateCreatedDateInRequisitionStatusChanges("2013-03-08", (long) dbWrapper.getMaxRnrID());

    skipAllProductsAndAuthorizeRnr();
    dbWrapper.updateCreatedDateInRequisitionStatusChanges("2013-04-08", (long) dbWrapper.getMaxRnrID());

    homePage.navigateInitiateRnRScreenAndSelectingRequiredFields(program, "Regular");
    initiateRnRPage = homePage.clickProceed();

    enterDetailsForFirstProduct(10, 5, null, 5, 0, 0);
    submitAndAuthorizeRnR();

    initiateRnRPage.verifyNormalizedConsumptionForFirstProduct(3);
    initiateRnRPage.verifyAmcForFirstProduct(3);
    verifyNormalizedConsumptionAndAmcInDatabase(3, 3, "P10");
  }

  @Test(groups = "requisition")
  public void testCalculationWhenProductSkipped() throws SQLException {
    dbWrapper.updateConfigureTemplate("HIV", "source", "U", "true", "quantityDispensed");
    dbWrapper.updateConfigureTemplate("HIV", "source", "U", "true", "stockInHand");
    dbWrapper.updateConfigureTemplateValidationFlag("HIV", "false");
    dbWrapper.insertProcessingPeriod("period3", "feb2013", "2013-01-31", "2013-02-28", 1, "M");

    HomePage homePage = loginPage.loginAs(userSIC, password);
    homePage.navigateInitiateRnRScreenAndSelectingRequiredFields(program, "Regular");
    InitiateRnRPage initiateRnRPage = homePage.clickProceed();

    initiateRnRPage.skipSingleProduct(2);
    enterDetailsForFirstProduct(15, 5, 5, 18, 0, 11);
    submitAndAuthorizeRnR();

    initiateRnRPage.verifyNormalizedConsumptionForFirstProduct(51);
    initiateRnRPage.verifyAmcForFirstProduct(51);
    verifyNormalizedConsumptionAndAmcInDatabase(51, 51, "P10");

    homePage.navigateInitiateRnRScreenAndSelectingRequiredFields(program, "Regular");
    initiateRnRPage = homePage.clickProceed();

    initiateRnRPage.skipSingleProduct(1);
    enterDetailsForSecondProduct(20, 0, 7, 13, 0, 0);
    submitAndAuthorizeRnR();

    initiateRnRPage.verifyNormalizedConsumptionForSecondProduct(13);
    initiateRnRPage.verifyAmcForSecondProduct(13);
    verifyNormalizedConsumptionAndAmcInDatabase(13, 13, "P11");
  }

  @Test(groups = "requisition")
  public void testCalculationTrackingWhenRequisitionConvertedToOrder() throws SQLException {
    dbWrapper.updateConfigureTemplate("HIV", "source", "U", "true", "quantityDispensed");
    dbWrapper.updateConfigureTemplate("HIV", "source", "U", "true", "stockInHand");
    dbWrapper.updateConfigureTemplateValidationFlag("HIV", "false");
    dbWrapper.updateFieldValue("products", "fullSupply", "false", "code", "P11");
    dbWrapper.insertProcessingPeriod("feb13", "feb13", "2013-01-31", "2013-02-28", 2, "M");
    dbWrapper.insertRole("fulfilment", "convert to order");
    dbWrapper.assignRight("fulfilment", "CONVERT_TO_ORDER");
    dbWrapper.insertRoleAssignment(userSIC, "store in-charge");
    dbWrapper.insertFulfilmentRoleAssignment(userSIC, "fulfilment", "F10");

    HomePage homePage = loginPage.loginAs(userSIC, password);
    homePage.navigateInitiateRnRScreenAndSelectingRequiredFields(program, "Regular");
    InitiateRnRPage initiateRnRPage = homePage.clickProceed();

    enterDetailsForFirstProduct(7, 5, 7, 8, 20, 10);
    submitAndAuthorizeRnR();

    initiateRnRPage.verifyNormalizedConsumptionForFirstProduct(54);
    initiateRnRPage.verifyAmcForFirstProduct(54);
    verifyNormalizedConsumptionAndAmcInDatabase(54, 54, "P10");

    ApprovePage approvePage = homePage.navigateToApprove();
    approvePage.clickRequisitionPresentForApproval();
    approvePage.clickApproveButton();
    approvePage.clickOk();

    ConvertOrderPage convertOrderPage = homePage.navigateConvertToOrder();
    convertOrderPage.clickCheckBoxConvertToOrder();
    convertOrderPage.clickConvertToOrderButton();
    convertOrderPage.clickOk();

    homePage.navigateInitiateRnRScreenAndSelectingRequiredFields(program, "Regular");
    initiateRnRPage = homePage.clickProceed();

    enterDetailsForFirstProduct(10, 10, 0, 13, 0, 0);
    submitAndAuthorizeRnR();

    initiateRnRPage.verifyNormalizedConsumptionForFirstProduct(13);
    initiateRnRPage.verifyAmcForFirstProduct(34);
    verifyNormalizedConsumptionAndAmcInDatabase(13, 34, "P10");
  }

  public void verifyNormalizedConsumptionAndAmcInDatabase(Integer normalizedConsumption, Integer amc, String productCode) throws SQLException {
    Long rnrId = (long) dbWrapper.getMaxRnrID();
    assertEquals(normalizedConsumption.toString(), dbWrapper.getRequisitionLineItemFieldValue(rnrId, "normalizedConsumption", productCode));
    assertEquals(amc.toString(), dbWrapper.getRequisitionLineItemFieldValue(rnrId, "amc", productCode));
  }

  public void enterDetailsForFirstProduct(Integer beginningBalance, Integer quantityReceived, Integer stockInHand,
                                          Integer quantityDispensed, Integer stockOutDays, Integer newPatientCount) {
    initiateRnRPage.enterValueIfNotNull(beginningBalance, "beginningBalanceFirstProduct");
    initiateRnRPage.enterValueIfNotNull(quantityReceived, "quantityReceivedFirstProduct");
    initiateRnRPage.enterValueIfNotNull(stockInHand, "stockInHandFirstProduct");
    initiateRnRPage.enterValueIfNotNull(quantityDispensed, "quantityDispensedFirstProduct");
    initiateRnRPage.enterValueIfNotNull(stockOutDays, "totalStockOutDaysFirstProduct");
    initiateRnRPage.enterValueIfNotNull(newPatientCount, "newPatientFirstProduct");
  }

  public void enterDetailsForSecondProduct(Integer beginningBalance, Integer quantityReceived, Integer stockInHand,
                                           Integer quantityDispensed, Integer stockOutDays, Integer newPatientCount) {
    initiateRnRPage.enterValueIfNotNull(beginningBalance, "beginningBalanceSecondProduct");
    initiateRnRPage.enterValueIfNotNull(quantityReceived, "quantityReceivedSecondProduct");
    initiateRnRPage.enterValueIfNotNull(stockInHand, "stockInHandSecondProduct");
    initiateRnRPage.enterValueIfNotNull(quantityDispensed, "quantityDispensedSecondProduct");
    initiateRnRPage.enterValueIfNotNull(stockOutDays, "totalStockOutDaysSecondProduct");
    initiateRnRPage.enterValueIfNotNull(newPatientCount, "newPatientSecondProduct");
  }

  public void submitAndAuthorizeRnR() {
    initiateRnRPage.submitRnR();
    initiateRnRPage.clickOk();

    initiateRnRPage.authorizeRnR();
    initiateRnRPage.clickOk();
  }

  @Test(groups = "requisition")
  public void testCalculationForEmergencyRnRWhenNumberOfMonthsIs1AndVerifyDefaultApprovedQuantityAndOtherFields() throws SQLException, ParseException {
    String periodStartDate = "2013-10-01";
    dbWrapper.deleteCurrentPeriod();
    dbWrapper.updateConfigureTemplate("HIV", "source", "C", "false", "maxStockQuantity");
    dbWrapper.updateConfigureTemplate("HIV", "source", "C", "false", "calculatedOrderQuantity");
    dbWrapper.insertProcessingPeriod("current", "current period", periodStartDate, "2016-01-30", 1, "M");
    dbWrapper.updateFieldValue("products", "dosesPerDispensingUnit", "11", "code", "P10");

    HomePage homePage = loginPage.loginAs(userSIC, password);
    homePage.navigateInitiateRnRScreenAndSelectingRequiredFields(program, "Emergency");
    InitiateRnRPage initiateRnRPage = homePage.clickProceed();
    int stockOutDays = calculateReportingDays(periodStartDate) + 10;

    enterDetailsForFirstProduct(10, 5, null, 5, stockOutDays, 1);
    initiateRnRPage.skipSingleProduct(2);
    submitAndAuthorizeRnR();

    initiateRnRPage.verifyNormalizedConsumptionForFirstProduct(8);
    initiateRnRPage.verifyAmcForFirstProduct(8);
    verifyNormalizedConsumptionAndAmcInDatabase(8, 8, "P10");

    dbWrapper.getRequisitionLineItemFieldValue((long) dbWrapper.getMaxRnrID(), "maxStockQuantity", "24");
    dbWrapper.getRequisitionLineItemFieldValue((long) dbWrapper.getMaxRnrID(), "calculatedOrderQuantity", "14");
    dbWrapper.getRequisitionLineItemFieldValue((long) dbWrapper.getMaxRnrID(), "quantityApproved", "14");
  }

  @Test(groups = "requisition")
  public void testCalculationForEmergencyRnRWhenNumberOfMonthsIs1AndDosesPerDispensingUnitIsZero() throws SQLException, ParseException {
    dbWrapper.deleteCurrentPeriod();
    String periodStartDate = "2013-10-01";
    dbWrapper.insertProcessingPeriod("current", "current period", periodStartDate, "2016-01-30", 1, "M");

    HomePage homePage = loginPage.loginAs(userSIC, password);
    homePage.navigateInitiateRnRScreenAndSelectingRequiredFields(program, "Regular");
    InitiateRnRPage initiateRnRPage = homePage.clickProceed();

    int stockOutDays = calculateReportingDays(periodStartDate) + 10;

    enterDetailsForFirstProduct(10, 5, null, 5, stockOutDays, 0);
    initiateRnRPage.skipSingleProduct(2);
    submitAndAuthorizeRnR();
    dbWrapper.updateFieldValue("products", "dosesPerDispensingUnit", "0", "code", "P10");

    homePage.navigateInitiateRnRScreenAndSelectingRequiredFields(program, "Emergency");
    initiateRnRPage = homePage.clickProceed();
    testWebDriver.refresh();

    enterDetailsForFirstProduct(10, 5, null, 5, stockOutDays, 1);
    initiateRnRPage.skipSingleProduct(2);
    submitAndAuthorizeRnR();

    initiateRnRPage.verifyNormalizedConsumptionForFirstProduct(35);
    initiateRnRPage.verifyAmcForFirstProduct(20);
    verifyNormalizedConsumptionAndAmcInDatabase(35, 20, "P10");
  }

  @Test(groups = "requisition")
  public void testCalculationForEmergencyRnRWhenNumberOfMonthsIs1ForMultipleProducts() throws SQLException, ParseException {
    String periodStartDate = "2013-10-01";
    dbWrapper.deleteCurrentPeriod();
    dbWrapper.insertProcessingPeriod("current", "current period", periodStartDate, "2016-01-30", 1, "M");

    HomePage homePage = loginPage.loginAs(userSIC, password);

    homePage.navigateInitiateRnRScreenAndSelectingRequiredFields(program, "Emergency");
    InitiateRnRPage initiateRnRPage = homePage.clickProceed();
    int stockOutDays = calculateReportingDays(periodStartDate) + 10;

    enterDetailsForFirstProduct(10, 5, null, 5, stockOutDays, 0);
    initiateRnRPage.skipSingleProduct(2);
    submitAndAuthorizeRnR();

    initiateRnRPage.verifyNormalizedConsumptionForFirstProduct(5);
    initiateRnRPage.verifyAmcForFirstProduct(5);
    verifyNormalizedConsumptionAndAmcInDatabase(5, 5, "P10");

    homePage.navigateInitiateRnRScreenAndSelectingRequiredFields(program, "Emergency");
    initiateRnRPage = homePage.clickProceed();

    initiateRnRPage.skipSingleProduct(1);
    enterDetailsForSecondProduct(10, 5, null, 5, stockOutDays, 1);
    submitAndAuthorizeRnR();

    initiateRnRPage.verifyNormalizedConsumptionForSecondProduct(8);
    initiateRnRPage.verifyAmcForSecondProduct(8);
    verifyNormalizedConsumptionAndAmcInDatabase(8, 8, "P11");
  }

  @Test(groups = "requisition")
  public void testCalculationForEmergencyRnRWhenNumberOfMonthsIs1AndDayDifferenceIsGreaterThanStockOutDays() throws SQLException, ParseException {
    dbWrapper.deleteCurrentPeriod();
    dbWrapper.insertProcessingPeriod("current", "current period", "2013-10-01", "2016-01-30", 1, "M");

    HomePage homePage = loginPage.loginAs(userSIC, password);
    homePage.navigateInitiateRnRScreenAndSelectingRequiredFields(program, "Emergency");
    InitiateRnRPage initiateRnRPage = homePage.clickProceed();

    Integer reportingDays = calculateReportingDays("2013-10-01");
    Integer newPatientCount = 1;
    Integer quantityDispensed = 5;
    Integer quantityReceived = 5;
    Integer beginningBalance = 10;
    Integer stockOutDays = 10;

    enterDetailsForFirstProduct(beginningBalance, quantityReceived, null, quantityDispensed, stockOutDays, newPatientCount);
    Integer normalizedConsumption = round((quantityDispensed * NUMBER_OF_DAYS_IN_MONTH / ((float) reportingDays - stockOutDays)) + round(30 / 10));
    initiateRnRPage.skipSingleProduct(2);
    submitAndAuthorizeRnR();

    initiateRnRPage.verifyNormalizedConsumptionForFirstProduct(normalizedConsumption);
    initiateRnRPage.verifyAmcForFirstProduct(normalizedConsumption);
    verifyNormalizedConsumptionAndAmcInDatabase(normalizedConsumption, normalizedConsumption, "P10");
  }

  @Test(groups = "requisition")
  public void testCalculationForEmergencyRnRWhenNumberOfMonthsIs1AndDayDifferenceIsEqualToStockOutDays() throws SQLException, ParseException {
    dbWrapper.deleteCurrentPeriod();
    dbWrapper.insertProcessingPeriod("current", "current period", "2013-10-01", "2016-01-30", 1, "M");

    HomePage homePage = loginPage.loginAs(userSIC, password);
    homePage.navigateInitiateRnRScreenAndSelectingRequiredFields(program, "Emergency");
    InitiateRnRPage initiateRnRPage = homePage.clickProceed();

    Integer reportingDays = calculateReportingDays("2013-10-01");
    Integer newPatientCount = 1;
    Integer quantityDispensed = 5;
    Integer quantityReceived = 5;
    Integer beginningBalance = 10;

    enterDetailsForFirstProduct(beginningBalance, quantityReceived, null, quantityDispensed, reportingDays, newPatientCount);
    Integer normalizedConsumption = quantityDispensed + newPatientCount * round(30 / 10);
    initiateRnRPage.skipSingleProduct(2);
    submitAndAuthorizeRnR();

    initiateRnRPage.verifyNormalizedConsumptionForFirstProduct(normalizedConsumption);
    initiateRnRPage.verifyAmcForFirstProduct(normalizedConsumption);
    verifyNormalizedConsumptionAndAmcInDatabase(normalizedConsumption, normalizedConsumption, "P10");
  }

  @Test(groups = "requisition")
  public void testCalculationForEmergencyRnRWhenNumberOfMonthsIs1() throws SQLException, ParseException {
    verifyCalculationForEmergencyForGivenNumberOfMonths(1);
  }

  @Test(groups = "requisition")
  public void testCalculationForEmergencyRnRWhenNumberOfMonthsIs2() throws SQLException, ParseException {
    verifyCalculationForEmergencyForGivenNumberOfMonths(2);
  }

  @Test(groups = "requisition")
  public void testCalculationForEmergencyRnRWhenNumberOfMonthsIs3() throws SQLException, ParseException {
    verifyCalculationForEmergencyForGivenNumberOfMonths(3);
  }

  @AfterMethod(groups = "requisition")
  public void tearDown() throws SQLException {
    testWebDriver.sleep(500);
    if (!testWebDriver.getElementById("username").isDisplayed()) {
      HomePage homePage = PageObjectFactory.getHomePage(testWebDriver);
      homePage.logout(baseUrlGlobal);
      dbWrapper.deleteData();
      dbWrapper.closeConnection();
    }
  }

  private void verifyCalculationForEmergencyForGivenNumberOfMonths(int numberOfMonths) throws SQLException, ParseException {
    dbWrapper.deleteCurrentPeriod();
    dbWrapper.deleteRowFromTable("processing_periods", "name", "Period2");
    dbWrapper.insertProcessingPeriod("current", "current period", "2013-10-01", "2016-01-30", numberOfMonths, "M");

    HomePage homePage = loginPage.loginAs(userSIC, password);
    homePage.navigateInitiateRnRScreenAndSelectingRequiredFields(program, "Regular");
    InitiateRnRPage initiateRnRPage = homePage.clickProceed();

    Integer reportingDays = numberOfMonths * NUMBER_OF_DAYS_IN_MONTH;
    Integer newPatientCount = 0;
    Integer quantityDispensed = 5;
    Integer quantityReceived = 5;
    Integer beginningBalance = 10;
    Integer stockOutDays = reportingDays - 10;

    enterDetailsForFirstProduct(beginningBalance, quantityReceived, null, quantityDispensed, stockOutDays, newPatientCount);
    initiateRnRPage.skipSingleProduct(2);
    submitAndAuthorizeRnR();

    Integer normalizedConsumptionForRegular = round(quantityDispensed * NUMBER_OF_DAYS_IN_MONTH / ((float) reportingDays - stockOutDays));

    homePage.navigateInitiateRnRScreenAndSelectingRequiredFields(program, "Emergency");
    initiateRnRPage = homePage.clickProceed();

    reportingDays = calculateReportingDays("2013-10-01");
    stockOutDays = reportingDays - 10;
    newPatientCount = 1;

    enterDetailsForFirstProduct(beginningBalance, quantityReceived, null, quantityDispensed, stockOutDays, newPatientCount);
    initiateRnRPage.skipSingleProduct(2);
    submitAndAuthorizeRnR();

    Integer normalizedConsumptionForEmergency = round(quantityDispensed * NUMBER_OF_DAYS_IN_MONTH / ((float) reportingDays - stockOutDays))
      + newPatientCount * round((float) 30 / 10);
    Integer amcForEmergency = round(((float) normalizedConsumptionForEmergency + normalizedConsumptionForRegular) / 2);

    initiateRnRPage.verifyNormalizedConsumptionForFirstProduct(normalizedConsumptionForEmergency);
    initiateRnRPage.verifyAmcForFirstProduct(amcForEmergency);
    verifyNormalizedConsumptionAndAmcInDatabase(normalizedConsumptionForEmergency, amcForEmergency, "P10");
  }

  private int calculateReportingDays(String periodStartString) throws ParseException {
    DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
    Date periodStartDate = formatter.parse(periodStartString);
    return (int) ((new Date().getTime() - periodStartDate.getTime()) / MILLISECONDS_IN_ONE_DAY);
  }

}
