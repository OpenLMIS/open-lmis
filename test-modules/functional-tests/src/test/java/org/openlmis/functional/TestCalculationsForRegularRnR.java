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

import cucumber.api.java.After;
import org.openlmis.UiUtils.TestCaseHelper;
import org.openlmis.pageobjects.HomePage;
import org.openlmis.pageobjects.InitiateRnRPage;
import org.openlmis.pageobjects.LoginPage;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import static com.thoughtworks.selenium.SeleneseTestBase.assertEquals;

public class TestCalculationsForRegularRnR extends TestCaseHelper {

  public String program="HIV", userSIC="storeInCharge", password="Admin123";

  public LoginPage loginPage;

  @BeforeMethod(groups = "requisition")
  public void setUp() throws Exception {
    super.setup();
    List<String> rightsList = new ArrayList<>();
    rightsList.add("CREATE_REQUISITION");
    rightsList.add("VIEW_REQUISITION");
    rightsList.add("AUTHORIZE_REQUISITION");
    setupTestDataToInitiateRnR(true, program, userSIC, "200", rightsList);
    dbWrapper.updateProductFullSupplyFlag(true, "P11");
  }

  @Test(groups = "requisition")
  public void testCalculationWhenMIs1WithStockOnHandCalculatedAndGZero() throws IOException, SQLException {
    dbWrapper.updateConfigureTemplate("HIV", "source", "U", "true", "quantityDispensed");
    dbWrapper.updateConfigureTemplate("HIV", "source", "C", "true", "stockInHand");
    dbWrapper.updateProductsByField("dosesPerDispensingUnit", "0", "P10");
    dbWrapper.updateProductFullSupplyFlag(false, "P11");
    LoginPage loginPage = new LoginPage(testWebDriver, baseUrlGlobal);
    HomePage homePage = loginPage.loginAs(userSIC, password);
    homePage.navigateInitiateRnRScreenAndSelectingRequiredFields(program, "Regular");
    InitiateRnRPage initiateRnRPage = homePage.clickProceed();
    enterDetailsForFirstProduct(10, 5, null, 14, 0, 5);
    initiateRnRPage.verifyNormalizedConsumptionForFirstProduct(164);
    initiateRnRPage.verifyAmcForFirstProduct(164);
    submitAndAuthorizeRnR();
    verifyNormalizedConsumptionAndAmcInDatabase(164,164,"P10");
  }

  @Test(groups = "requisition")
  public void testCalculationWhenNoPreviousPeriod() throws IOException, SQLException {
    dbWrapper.updateConfigureTemplate("HIV","source","U","true","quantityDispensed");
    dbWrapper.updateConfigureTemplate("HIV","source","C","true","stockInHand");
    dbWrapper.deletePeriod("Period2");
    dbWrapper.insertProcessingPeriod("currentPeriod","current period","2013-01-31","2016-01-31",1,"M");
    dbWrapper.updateProductFullSupplyFlag(false, "P11");
    LoginPage loginPage = new LoginPage(testWebDriver, baseUrlGlobal);
    HomePage homePage = loginPage.loginAs(userSIC, password);
    homePage.navigateInitiateRnRScreenAndSelectingRequiredFields(program, "Regular");
    InitiateRnRPage initiateRnRPage = homePage.clickProceed();
    enterDetailsForFirstProduct(10, 5, null, 14, 0, 0);
    initiateRnRPage.verifyNormalizedConsumptionForFirstProduct(14);
    initiateRnRPage.verifyAmcForFirstProduct(14);
    submitAndAuthorizeRnR();
    verifyNormalizedConsumptionAndAmcInDatabase(14,14,"P10");
  }

  @Test(groups = "requisition")
  public void testCalculationWhenFieldsNotVisibleAndSameProductInDifferentPrograms() throws Exception {
    dbWrapper.updateConfigureTemplate("HIV","source","C","false","quantityDispensed");
    dbWrapper.updateConfigureTemplate("HIV","source","U","true","stockInHand");
    dbWrapper.updateConfigureTemplate("HIV","source","C","false","normalizedConsumption");
    dbWrapper.updateConfigureTemplate("HIV","source","C","false","amc");
    dbWrapper.insertProgramProduct("P10", "ESS_MEDS", "10", "true");
    dbWrapper.insertFacilityApprovedProduct("P10", "ESS_MEDS", "lvl3_hospital");
    dbWrapper.configureTemplate("ESS_MEDS");
    dbWrapper.updateConfigureTemplate("ESS_MEDS", "source", "C", "false", "quantityDispensed");
    dbWrapper.updateConfigureTemplate("ESS_MEDS","source","U","true","stockInHand");
    dbWrapper.updateConfigureTemplate("ESS_MEDS","source","C","false","normalizedConsumption");
    dbWrapper.updateConfigureTemplate("ESS_MEDS","source","C","false","amc");
    dbWrapper.insertRequisitionGroupProgramScheduleForProgram("RG1", "ESS_MEDS", "M");
    dbWrapper.insertRoleAssignmentForSupervisoryNode(dbWrapper.getUserID(userSIC), "store in-charge", null, "ESS_MEDS");
    dbWrapper.updateProductFullSupplyFlag(false, "P11");

    LoginPage loginPage = new LoginPage(testWebDriver, baseUrlGlobal);
    HomePage homePage = loginPage.loginAs(userSIC, password);
    homePage.navigateInitiateRnRScreenAndSelectingRequiredFields(program, "Regular");
    homePage.clickProceed();
    enterDetailsForFirstProduct(10, 5, 7, null, 20, 10);
    submitAndAuthorizeRnR();
    verifyNormalizedConsumptionAndAmcInDatabase(54,54,"P10");

    homePage.navigateInitiateRnRScreenAndSelectingRequiredFields("ESSENTIAL MEDICINES", "Regular");
    homePage.clickProceed();
    enterDetailsForFirstProduct(10, 5, 2, null, 0, 0);
    submitAndAuthorizeRnR();
    verifyNormalizedConsumptionAndAmcInDatabase(13,13,"P10");
  }

  @Test(groups = "requisition")
  public void testCalculationWhenMIs1WithStockOnHandAndQuantityConsumedUserInputAndMultipleProductsAndXLargerThan30M() throws IOException, SQLException {
    dbWrapper.updateConfigureTemplate("HIV","source","U","true","quantityDispensed");
    dbWrapper.updateConfigureTemplate("HIV","source","U","true","stockInHand");
    dbWrapper.updateConfigureTemplateValidationFlag("HIV", "true");
    dbWrapper.updateProductsByField("dosesPerDispensingUnit", "7", "P10");
    dbWrapper.insertProcessingPeriod("period3","feb2013","2013-01-31","2013-02-28",1,"M");
    LoginPage loginPage = new LoginPage(testWebDriver, baseUrlGlobal);
    HomePage homePage = loginPage.loginAs(userSIC, password);
    homePage.navigateInitiateRnRScreenAndSelectingRequiredFields(program, "Regular");
    InitiateRnRPage initiateRnRPage = homePage.clickProceed();
    enterDetailsForFirstProduct(15, 5, 2, 18, 45, 11);
    initiateRnRPage.verifyNormalizedConsumptionForFirstProduct(62);
    initiateRnRPage.verifyAmcForFirstProduct(62);
    enterDetailsForSecondProduct(8, 5, 5, 8, 20, 10);
    initiateRnRPage.verifyNormalizedConsumptionForSecondProduct(54);
    initiateRnRPage.verifyAmcForSecondProduct(54);
    submitAndAuthorizeRnR();
    verifyNormalizedConsumptionAndAmcInDatabase(62, 62, "P10");
    verifyNormalizedConsumptionAndAmcInDatabase(54,54,"P11");

    homePage.navigateInitiateRnRScreenAndSelectingRequiredFields(program, "Regular");
    initiateRnRPage = homePage.clickProceed();
    initiateRnRPage.skipSingleProduct(2);
    enterDetailsForFirstProduct(20,0,7,13,23,0);
    initiateRnRPage.verifyNormalizedConsumptionForFirstProduct(56);
    initiateRnRPage.verifyAmcForFirstProduct(59);
    submitAndAuthorizeRnR();
    verifyNormalizedConsumptionAndAmcInDatabase(56, 59, "P10");
  }

  //@Test(groups = "requisition")
  public void testCalculationWhenMIs1WithStockOnHandAndQuantityConsumedUserInputAndRnRExistsForDifferentFacility() throws IOException, SQLException {
    dbWrapper.updateConfigureTemplate("HIV","source","U","true","quantityDispensed");
    dbWrapper.updateConfigureTemplate("HIV","source","U","true","stockInHand");
    dbWrapper.updateConfigureTemplateValidationFlag("HIV", "false");
    dbWrapper.updateProductFullSupplyFlag(false,"P11");
    LoginPage loginPage = new LoginPage(testWebDriver, baseUrlGlobal);
    HomePage homePage = loginPage.loginAs(userSIC, password);
    homePage.navigateInitiateRnRScreenAndSelectingRequiredFields(program, "Regular");
    InitiateRnRPage initiateRnRPage = homePage.clickProceed();
    enterDetailsForFirstProduct(7, 5, 7, 8, 20, 10);
    initiateRnRPage.verifyNormalizedConsumptionForFirstProduct(54);
    initiateRnRPage.verifyAmcForFirstProduct(54);
    submitAndAuthorizeRnR();
    verifyNormalizedConsumptionAndAmcInDatabase(54, 54, "P10");

    dbWrapper.insertSupervisoryNodeSecond("F11","N2","Node2","N1");
    String userId= dbWrapper.getUserID(userSIC);
    dbWrapper.insertRoleAssignmentForSupervisoryNode(userId,"store in-charge","N2","HIV");

    homePage.navigateAndInitiateRnrForSupervisedFacility("HIV");
    initiateRnRPage = homePage.clickProceed();
    initiateRnRPage.skipSingleProduct(2);
    enterDetailsForFirstProduct(10, 10, 0, 13, 23, 0);
    initiateRnRPage.verifyNormalizedConsumptionForFirstProduct(56);
    initiateRnRPage.verifyAmcForFirstProduct(56);
    submitAndAuthorizeRnR();
    verifyNormalizedConsumptionAndAmcInDatabase(56, 56, "P10");
  }

  @Test(groups = "requisition")
  public void testCalculationOfAmcTrackingWhenMIs() throws IOException, SQLException {
    dbWrapper.updateProductFullSupplyFlag(false,"P11");
    dbWrapper.insertProcessingPeriod("feb13","feb13","2013-01-31","2013-02-28",1,"M");
    dbWrapper.insertProcessingPeriod("mar13","mar13","2013-03-01","2013-03-31",1,"M");
    dbWrapper.insertProcessingPeriod("apr13","apr13","2013-04-01","2013-04-30",1,"M");
    dbWrapper.insertProcessingPeriod("may13","may13","2013-05-01","2013-05-31",1,"M");
    dbWrapper.insertProcessingPeriod("jun13","jun13","2013-06-01","2013-06-30",1,"M");
    dbWrapper.insertProcessingPeriod("current","current","2013-07-01","2016-02-28",4,"M");
    LoginPage loginPage = new LoginPage(testWebDriver, baseUrlGlobal);
    HomePage homePage = loginPage.loginAs(userSIC, password);
    homePage.navigateInitiateRnRScreenAndSelectingRequiredFields(program, "Regular");
    InitiateRnRPage initiateRnRPage = homePage.clickProceed();
    enterDetailsForFirstProduct(10, 5, null,8, 20, 0);
    initiateRnRPage.verifyNormalizedConsumptionForFirstProduct(24);
    initiateRnRPage.verifyAmcForFirstProduct(24);
    submitAndAuthorizeRnR();
    verifyNormalizedConsumptionAndAmcInDatabase(24, 24, "P10");
    dbWrapper.updateCreatedDateInRequisitionStatusChanges("2013-01-08", (long) dbWrapper.getMaxRnrID());

    homePage.navigateInitiateRnRScreenAndSelectingRequiredFields(program, "Regular");
    initiateRnRPage = homePage.clickProceed();
    enterDetailsForFirstProduct(10, 5, null,10, 0, 2);
    initiateRnRPage.verifyNormalizedConsumptionForFirstProduct(16);
    initiateRnRPage.verifyAmcForFirstProduct(20);
    submitAndAuthorizeRnR();
    verifyNormalizedConsumptionAndAmcInDatabase(16,20,"P10");
    dbWrapper.updateCreatedDateInRequisitionStatusChanges("2013-02-08", (long) dbWrapper.getMaxRnrID());

    homePage.navigateInitiateRnRScreenAndSelectingRequiredFields(program, "Regular");
    initiateRnRPage = homePage.clickProceed();
    enterDetailsForFirstProduct(10, 5, null,5, 20, 0);
    initiateRnRPage.verifyNormalizedConsumptionForFirstProduct(15);
    initiateRnRPage.verifyAmcForFirstProduct(18);
    submitAndAuthorizeRnR();
    verifyNormalizedConsumptionAndAmcInDatabase(15,18,"P10");
    dbWrapper.updateCreatedDateInRequisitionStatusChanges("2013-03-08", (long) dbWrapper.getMaxRnrID());

    homePage.navigateInitiateRnRScreenAndSelectingRequiredFields(program, "Regular");
    initiateRnRPage = homePage.clickProceed();
    enterDetailsForFirstProduct(5, 5, null,0, 20, 0);
    initiateRnRPage.verifyNormalizedConsumptionForFirstProduct(0);
    initiateRnRPage.verifyAmcForFirstProduct(10);
    submitAndAuthorizeRnR();
    verifyNormalizedConsumptionAndAmcInDatabase(0,10,"P10");
  }

  @Test(groups = "requisition")
  public void testCalculationTrackingOfAmcWhenMIs2() throws IOException, SQLException {
    dbWrapper.updateProductFullSupplyFlag(false,"P11");
    dbWrapper.deletePeriod("Period2");
    dbWrapper.insertProcessingPeriod("feb13","feb13","2013-01-31","2013-02-28",2,"M");
    dbWrapper.insertProcessingPeriod("mar13","mar13","2013-03-01","2013-03-31",2,"M");
    dbWrapper.insertProcessingPeriod("apr13","apr13","2013-04-01","2013-04-30",2,"M");
    dbWrapper.insertProcessingPeriod("may13","may13","2013-05-01","2013-05-31",2,"M");
    LoginPage loginPage = new LoginPage(testWebDriver, baseUrlGlobal);
    HomePage homePage = loginPage.loginAs(userSIC, password);
    homePage.navigateInitiateRnRScreenAndSelectingRequiredFields(program, "Regular");
    InitiateRnRPage initiateRnRPage = homePage.clickProceed();
    enterDetailsForFirstProduct(10, 5, null,8, 0, 0);
    initiateRnRPage.verifyNormalizedConsumptionForFirstProduct(4);
    initiateRnRPage.verifyAmcForFirstProduct(4);
    submitAndAuthorizeRnR();
    verifyNormalizedConsumptionAndAmcInDatabase(4, 4, "P10");
    dbWrapper.updateCreatedDateInRequisitionStatusChanges("2013-02-08", (long) dbWrapper.getMaxRnrID());

    homePage.navigateInitiateRnRScreenAndSelectingRequiredFields(program, "Regular");
    initiateRnRPage = homePage.clickProceed();
    enterDetailsForFirstProduct(10, 5, null,0, 0, 1);
    initiateRnRPage.verifyNormalizedConsumptionForFirstProduct(3);
    initiateRnRPage.verifyAmcForFirstProduct(4);
    submitAndAuthorizeRnR();
    verifyNormalizedConsumptionAndAmcInDatabase(3,4,"P10");
    dbWrapper.updateCreatedDateInRequisitionStatusChanges("2013-03-08", (long) dbWrapper.getMaxRnrID());

    homePage.navigateInitiateRnRScreenAndSelectingRequiredFields(program, "Regular");
    initiateRnRPage = homePage.clickProceed();
    enterDetailsForFirstProduct(10, 5, null,5, 20, 0);
    initiateRnRPage.verifyNormalizedConsumptionForFirstProduct(4);
    initiateRnRPage.verifyAmcForFirstProduct(4);
    submitAndAuthorizeRnR();
    verifyNormalizedConsumptionAndAmcInDatabase(4,4,"P10");
  }

  @Test(groups = "requisition")
  public void testCalculationTrackingOfAmcWhenMIs3() throws IOException, SQLException {
    dbWrapper.updateProductFullSupplyFlag(false,"P11");
    dbWrapper.deletePeriod("Period2");
    dbWrapper.insertProcessingPeriod("feb13","feb13","2013-01-31","2013-02-28",3,"M");
    dbWrapper.insertProcessingPeriod("mar13","mar13","2013-03-01","2013-03-31",3,"M");
    dbWrapper.insertProcessingPeriod("apr13","apr13","2013-04-01","2013-04-30",3,"M");
    LoginPage loginPage = new LoginPage(testWebDriver, baseUrlGlobal);
    HomePage homePage = loginPage.loginAs(userSIC, password);
    homePage.navigateInitiateRnRScreenAndSelectingRequiredFields(program, "Regular");
    InitiateRnRPage initiateRnRPage = homePage.clickProceed();
    enterDetailsForFirstProduct(10, 5, null,8, 0, 1);
    initiateRnRPage.verifyNormalizedConsumptionForFirstProduct(6);
    initiateRnRPage.verifyAmcForFirstProduct(6);
    submitAndAuthorizeRnR();
    verifyNormalizedConsumptionAndAmcInDatabase(6, 6, "P10");
    dbWrapper.updateCreatedDateInRequisitionStatusChanges("2013-02-08", (long) dbWrapper.getMaxRnrID());

    homePage.navigateInitiateRnRScreenAndSelectingRequiredFields(program, "Regular");
    initiateRnRPage = homePage.clickProceed();
    enterDetailsForFirstProduct(10, 5, null,1, 1, 0);
    initiateRnRPage.verifyNormalizedConsumptionForFirstProduct(0);
    initiateRnRPage.verifyAmcForFirstProduct(0);
    submitAndAuthorizeRnR();
    verifyNormalizedConsumptionAndAmcInDatabase(0,0,"P10");
  }

  @Test(groups = "requisition")
  public void testCalculationTrackingOfAmcWhenMIs5() throws IOException, SQLException {
    dbWrapper.updateProductFullSupplyFlag(false,"P11");
    dbWrapper.deletePeriod("Period2");
    dbWrapper.insertProcessingPeriod("feb13","feb13","2013-01-31","2013-02-28",5,"M");
    dbWrapper.insertProcessingPeriod("mar13","mar13","2013-03-01","2013-03-31",5,"M");
    dbWrapper.insertProcessingPeriod("apr13","apr13","2013-04-01","2013-04-30",5,"M");
    LoginPage loginPage = new LoginPage(testWebDriver, baseUrlGlobal);
    HomePage homePage = loginPage.loginAs(userSIC, password);
    homePage.navigateInitiateRnRScreenAndSelectingRequiredFields(program, "Regular");
    InitiateRnRPage initiateRnRPage = homePage.clickProceed();
    enterDetailsForFirstProduct(10, 5, null,8, 0, 0);
    initiateRnRPage.verifyNormalizedConsumptionForFirstProduct(2);
    initiateRnRPage.verifyAmcForFirstProduct(2);
    submitAndAuthorizeRnR();
    verifyNormalizedConsumptionAndAmcInDatabase(2, 2, "P10");
    dbWrapper.updateCreatedDateInRequisitionStatusChanges("2013-02-08", (long) dbWrapper.getMaxRnrID());

    homePage.navigateInitiateRnRScreenAndSelectingRequiredFields(program, "Regular");
    initiateRnRPage = homePage.clickProceed();
    enterDetailsForFirstProduct(10, 5, null,0, 0, 0);
    initiateRnRPage.verifyNormalizedConsumptionForFirstProduct(0);
    initiateRnRPage.verifyAmcForFirstProduct(0);
    submitAndAuthorizeRnR();
    verifyNormalizedConsumptionAndAmcInDatabase(0,0,"P10");
  }

  @Test(groups = "requisition")
  public void testCalculationOfAmcNoTrackingForMoreThan5PeriodsWhenMIs1() throws IOException, SQLException {
    dbWrapper.updateProductFullSupplyFlag(false,"P11");
    dbWrapper.insertProcessingPeriod("feb13","feb13","2013-01-31","2013-02-28",1,"M");
    dbWrapper.insertProcessingPeriod("mar13","mar13","2013-03-01","2013-03-31",1,"M");
    dbWrapper.insertProcessingPeriod("apr13","apr13","2013-04-01","2013-04-30",1,"M");
    dbWrapper.insertProcessingPeriod("may13","may13","2013-05-01","2013-05-31",1,"M");
    dbWrapper.insertProcessingPeriod("jun13","jun13","2013-06-01","2013-06-30",1,"M");
    dbWrapper.insertProcessingPeriod("current","current","2013-07-01","2016-02-28",4,"M");
    LoginPage loginPage = new LoginPage(testWebDriver, baseUrlGlobal);
    HomePage homePage = loginPage.loginAs(userSIC, password);
    homePage.navigateInitiateRnRScreenAndSelectingRequiredFields(program, "Regular");
    InitiateRnRPage initiateRnRPage = homePage.clickProceed();
    enterDetailsForFirstProduct(10, 5, null,8, 20, 0);
    initiateRnRPage.verifyNormalizedConsumptionForFirstProduct(24);
    initiateRnRPage.verifyAmcForFirstProduct(24);
    submitAndAuthorizeRnR();
    verifyNormalizedConsumptionAndAmcInDatabase(24, 24, "P10");
    dbWrapper.updateCreatedDateInRequisitionStatusChanges("2013-01-08", (long) dbWrapper.getMaxRnrID());

    homePage.navigateInitiateRnRScreenAndSelectingRequiredFields(program, "Regular");
    initiateRnRPage = homePage.clickProceed();
    initiateRnRPage.skipAllProduct();
    submitAndAuthorizeRnR();
    dbWrapper.updateCreatedDateInRequisitionStatusChanges("2013-02-08", (long) dbWrapper.getMaxRnrID());

    homePage.navigateInitiateRnRScreenAndSelectingRequiredFields(program, "Regular");
    initiateRnRPage = homePage.clickProceed();
    initiateRnRPage.skipAllProduct();
    submitAndAuthorizeRnR();
    dbWrapper.updateCreatedDateInRequisitionStatusChanges("2013-03-08", (long) dbWrapper.getMaxRnrID());

    homePage.navigateInitiateRnRScreenAndSelectingRequiredFields(program, "Regular");
    initiateRnRPage = homePage.clickProceed();
    initiateRnRPage.skipAllProduct();
    submitAndAuthorizeRnR();
    dbWrapper.updateCreatedDateInRequisitionStatusChanges("2013-04-08", (long) dbWrapper.getMaxRnrID());

    homePage.navigateInitiateRnRScreenAndSelectingRequiredFields(program, "Regular");
    initiateRnRPage = homePage.clickProceed();
    initiateRnRPage.skipAllProduct();
    submitAndAuthorizeRnR();
    dbWrapper.updateCreatedDateInRequisitionStatusChanges("2013-05-08", (long) dbWrapper.getMaxRnrID());

    homePage.navigateInitiateRnRScreenAndSelectingRequiredFields(program, "Regular");
    initiateRnRPage = homePage.clickProceed();
    initiateRnRPage.skipAllProduct();
    submitAndAuthorizeRnR();
    dbWrapper.updateCreatedDateInRequisitionStatusChanges("2013-06-08", (long) dbWrapper.getMaxRnrID());

    homePage.navigateInitiateRnRScreenAndSelectingRequiredFields(program, "Regular");
    initiateRnRPage = homePage.clickProceed();
    enterDetailsForFirstProduct(10, 5, null,0, 0, 0);
    initiateRnRPage.verifyNormalizedConsumptionForFirstProduct(0);
    initiateRnRPage.verifyAmcForFirstProduct(0);
    submitAndAuthorizeRnR();
    verifyNormalizedConsumptionAndAmcInDatabase(0,0,"P10");
  }

  @Test(groups = "requisition")
  public void testCalculationAmcNoTrackingForMoreThan2MWhenMIs2() throws IOException, SQLException {
    dbWrapper.updateProductFullSupplyFlag(false,"P11");
    dbWrapper.deletePeriod("Period2");
    dbWrapper.insertProcessingPeriod("feb13","feb13","2013-01-31","2013-02-28",2,"M");
    dbWrapper.insertProcessingPeriod("mar13","mar13","2013-03-01","2013-03-31",2,"M");
    dbWrapper.insertProcessingPeriod("apr13","apr13","2013-04-01","2013-04-30",2,"M");
    dbWrapper.insertProcessingPeriod("may13","may13","2013-05-01","2013-05-31",2,"M");
    LoginPage loginPage = new LoginPage(testWebDriver, baseUrlGlobal);
    HomePage homePage = loginPage.loginAs(userSIC, password);
    homePage.navigateInitiateRnRScreenAndSelectingRequiredFields(program, "Regular");
    InitiateRnRPage initiateRnRPage = homePage.clickProceed();
    enterDetailsForFirstProduct(10, 5, null,8, 0, 0);
    initiateRnRPage.verifyNormalizedConsumptionForFirstProduct(4);
    initiateRnRPage.verifyAmcForFirstProduct(4);
    submitAndAuthorizeRnR();
    verifyNormalizedConsumptionAndAmcInDatabase(4, 4, "P10");
    dbWrapper.updateCreatedDateInRequisitionStatusChanges("2013-02-08", (long) dbWrapper.getMaxRnrID());

    homePage.navigateInitiateRnRScreenAndSelectingRequiredFields(program, "Regular");
    initiateRnRPage = homePage.clickProceed();
    initiateRnRPage.skipAllProduct();
    submitAndAuthorizeRnR();
    dbWrapper.updateCreatedDateInRequisitionStatusChanges("2013-03-08", (long) dbWrapper.getMaxRnrID());

    homePage.navigateInitiateRnRScreenAndSelectingRequiredFields(program, "Regular");
    initiateRnRPage = homePage.clickProceed();
    initiateRnRPage.skipAllProduct();
    submitAndAuthorizeRnR();
    dbWrapper.updateCreatedDateInRequisitionStatusChanges("2013-04-08", (long) dbWrapper.getMaxRnrID());

    homePage.navigateInitiateRnRScreenAndSelectingRequiredFields(program, "Regular");
    initiateRnRPage = homePage.clickProceed();
    enterDetailsForFirstProduct(10, 5, null,5, 0, 0);
    initiateRnRPage.verifyNormalizedConsumptionForFirstProduct(3);
    initiateRnRPage.verifyAmcForFirstProduct(3);
    submitAndAuthorizeRnR();
    verifyNormalizedConsumptionAndAmcInDatabase(3,3,"P10");
  }

  @Test(groups = "requisition")
  public void testCalculationWhenProductSkipped() throws IOException, SQLException {
    dbWrapper.updateConfigureTemplate("HIV","source","U","true","quantityDispensed");
    dbWrapper.updateConfigureTemplate("HIV","source","U","true","stockInHand");
    dbWrapper.updateConfigureTemplateValidationFlag("HIV", "false");
    dbWrapper.insertProcessingPeriod("period3","feb2013","2013-01-31","2013-02-28",1,"M");
    LoginPage loginPage = new LoginPage(testWebDriver, baseUrlGlobal);
    HomePage homePage = loginPage.loginAs(userSIC, password);
    homePage.navigateInitiateRnRScreenAndSelectingRequiredFields(program, "Regular");
    InitiateRnRPage initiateRnRPage = homePage.clickProceed();
    initiateRnRPage.skipSingleProduct(2);
    enterDetailsForFirstProduct(15, 5, 5, 18, 0, 11);
    initiateRnRPage.verifyNormalizedConsumptionForFirstProduct(51);
    initiateRnRPage.verifyAmcForFirstProduct(51);
    submitAndAuthorizeRnR();
    verifyNormalizedConsumptionAndAmcInDatabase(51, 51, "P10");

    homePage.navigateInitiateRnRScreenAndSelectingRequiredFields(program, "Regular");
    initiateRnRPage = homePage.clickProceed();
    initiateRnRPage.skipSingleProduct(1);
    enterDetailsForSecondProduct(20, 0, 7, 13, 0, 0);
    initiateRnRPage.verifyNormalizedConsumptionForSecondProduct(13);
    initiateRnRPage.verifyAmcForSecondProduct(13);
    submitAndAuthorizeRnR();
    verifyNormalizedConsumptionAndAmcInDatabase(13, 13, "P11");
  }

  public void verifyNormalizedConsumptionAndAmcInDatabase(Integer normalizedConsumption, Integer amc, String productCode) throws IOException, SQLException {
    Long rnrId = Long.valueOf(dbWrapper.getMaxRnrID());
    assertEquals(normalizedConsumption.toString(), dbWrapper.getRequisitionLineItemFieldValue(rnrId,"normalizedConsumption",productCode));
    assertEquals(amc.toString(), dbWrapper.getRequisitionLineItemFieldValue(rnrId, "amc", productCode));
  }

  public void enterDetailsForFirstProduct(Integer beginningBalance, Integer quantityReceived, Integer stockOnHand,
                                          Integer quantityDispensed, Integer stockOutDays, Integer newPatientCount) throws IOException {
    InitiateRnRPage initiateRnRPage = new InitiateRnRPage(testWebDriver);
    if (beginningBalance != null)
      initiateRnRPage.enterBeginningBalanceForFirstProduct(beginningBalance);
    if (quantityReceived != null)
      initiateRnRPage.enterQuantityReceivedForFirstProduct(quantityReceived);
    if (stockOnHand != null)
      initiateRnRPage.enterStockOnHandForFirstProduct(stockOnHand);
    if (quantityDispensed != null)
      initiateRnRPage.enterQuantityDispensedForFirstProduct(quantityDispensed);
    if (stockOutDays != null)
      initiateRnRPage.enterStockOutDaysForFirstProduct(stockOutDays);
    if (newPatientCount != null)
      initiateRnRPage.enterNewPatientCountForFirstProduct(newPatientCount);
  }

  public void enterDetailsForSecondProduct(Integer beginningBalance, Integer quantityReceived, Integer stockOnHand,
                                           Integer quantityDispensed, Integer stockOutDays, Integer newPatientCount) throws IOException {
    InitiateRnRPage initiateRnRPage = new InitiateRnRPage(testWebDriver);
    if (beginningBalance != null)
      initiateRnRPage.enterBeginningBalanceForSecondProduct(beginningBalance);
    if (quantityReceived != null)
      initiateRnRPage.enterQuantityReceivedForSecondProduct(quantityReceived);
    if (stockOnHand != null)
      initiateRnRPage.enterStockOnHandForSecondProduct(stockOnHand);
    if (quantityDispensed != null)
      initiateRnRPage.enterQuantityDispensedForSecondProduct(quantityDispensed);
    if (stockOutDays != null)
      initiateRnRPage.enterStockOutDaysForSecondProduct(stockOutDays);
    if (newPatientCount != null)
      initiateRnRPage.enterNewPatientCountForSecondProduct(newPatientCount);
  }

  public void submitAndAuthorizeRnR() throws IOException {
    InitiateRnRPage initiateRnRPage= new InitiateRnRPage(testWebDriver);
    initiateRnRPage.submitRnR();
    initiateRnRPage.clickOk();
    initiateRnRPage.authorizeRnR();
    initiateRnRPage.clickOk();
  }


  @Test(groups = "requisition")
  public void testCalculationForEmergencyRnRWhenNumberOfMonthsIs1() throws IOException, SQLException {
    String createdDate = "2013-12-03";
    dbWrapper.deleteCurrentPeriod();
    dbWrapper.insertProcessingPeriod("current", "current period", "2013-10-01", "2016-01-30", 1, "M");
    dbWrapper.updateProductsByField("dosesPerDispensingUnit", "11", "P10");

    LoginPage loginPage = new LoginPage(testWebDriver, baseUrlGlobal);
    HomePage homePage = loginPage.loginAs(userSIC, password);

    homePage.navigateInitiateRnRScreenAndSelectingRequiredFields(program, "Emergency");
    InitiateRnRPage initiateRnRPage = homePage.clickProceed();
    dbWrapper.updateCreatedDateAfterRequisitionIsInitiated(createdDate);
    testWebDriver.refresh();
    enterDetailsForFirstProduct(10, 5, null, 5, 99, 1);
    initiateRnRPage.verifyNormalizedConsumptionForFirstProduct(8);
    initiateRnRPage.verifyAmcForFirstProduct(8);
    initiateRnRPage.skipSingleProduct(2);
    submitAndAuthorizeRnR();
    verifyNormalizedConsumptionAndAmcInDatabase(8, 8, "P10");
  }

  @Test(groups = "requisition")
  public void testCalculationForEmergencyRnRWhenNumberOfMonthsIs2() throws IOException, SQLException {
    String createdDate1 = "2013-09-03";
    String createdDate2 = "2013-12-03";
    dbWrapper.deleteCurrentPeriod();
    dbWrapper.insertProcessingPeriod("previous", "previous period", "2013-07-01", "2013-09-30", 2, "M");
    dbWrapper.insertProcessingPeriod("current", "current period", "2013-10-01", "2016-01-30", 2, "M");
    LoginPage loginPage = new LoginPage(testWebDriver, baseUrlGlobal);
    HomePage homePage = loginPage.loginAs(userSIC, password);
    homePage.navigateInitiateRnRScreenAndSelectingRequiredFields(program, "Regular");
    InitiateRnRPage initiateRnRPage = homePage.clickProceed();
    dbWrapper.updateCreatedDateAfterRequisitionIsInitiated(createdDate1);
    testWebDriver.refresh();
    enterDetailsForFirstProduct(10, 5, null, 5, 99, 0);
    initiateRnRPage.skipSingleProduct(2);
    submitAndAuthorizeRnR();
    dbWrapper.updateCreatedDateAfterRequisitionIsInitiated(createdDate1);

    homePage.navigateInitiateRnRScreenAndSelectingRequiredFields(program, "Emergency");
    initiateRnRPage = homePage.clickProceed();
    dbWrapper.updateCreatedDateAfterRequisitionIsInitiated(createdDate2);
    testWebDriver.refresh();
    enterDetailsForFirstProduct(10, 5, null, 5, 99, 0);
    initiateRnRPage.verifyNormalizedConsumptionForFirstProduct(5);
    initiateRnRPage.verifyAmcForFirstProduct(5);
    initiateRnRPage.skipSingleProduct(2);
    submitAndAuthorizeRnR();
    verifyNormalizedConsumptionAndAmcInDatabase(5, 5, "P10");
  }

  @Test(groups = "requisition")
  public void testCalculationForEmergencyRnRWhenNumberOfMonthsIs1AndDosesPerDispensingUnitIsZero() throws IOException, SQLException {
    String createdDate = "2013-12-03";
    dbWrapper.deleteCurrentPeriod();
    dbWrapper.insertProcessingPeriod("current", "current period", "2013-10-01", "2016-01-30", 1, "M");
    LoginPage loginPage = new LoginPage(testWebDriver, baseUrlGlobal);
    HomePage homePage = loginPage.loginAs(userSIC, password);
    homePage.navigateInitiateRnRScreenAndSelectingRequiredFields(program, "Regular");
    InitiateRnRPage initiateRnRPage = homePage.clickProceed();
    dbWrapper.updateCreatedDateAfterRequisitionIsInitiated(createdDate);
    testWebDriver.refresh();
    enterDetailsForFirstProduct(10, 5, null, 5, 99, 0);
    initiateRnRPage.skipSingleProduct(2);
    submitAndAuthorizeRnR();
    dbWrapper.updateCreatedDateAfterRequisitionIsInitiated(createdDate);
    dbWrapper.updateProductsByField("dosesPerDispensingUnit", "0", "P10");


    homePage.navigateInitiateRnRScreenAndSelectingRequiredFields(program, "Emergency");
    initiateRnRPage = homePage.clickProceed();
    dbWrapper.updateCreatedDateAfterRequisitionIsInitiated(createdDate);
    testWebDriver.refresh();
    enterDetailsForFirstProduct(10, 5, null, 5, 99, 1);
    initiateRnRPage.verifyNormalizedConsumptionForFirstProduct(35);
    initiateRnRPage.verifyAmcForFirstProduct(20);
    initiateRnRPage.skipSingleProduct(2);
    submitAndAuthorizeRnR();
    verifyNormalizedConsumptionAndAmcInDatabase(35, 20, "P10");
  }


  @Test(groups = "requisition")
  public void testCalculationForEmergencyRnRWhenNumberOfMonthsIsForMultipleProducts() throws IOException, SQLException {
    String createdDate = "2013-12-03";
    dbWrapper.deleteCurrentPeriod();
    dbWrapper.insertProcessingPeriod("current", "current period", "2013-10-01", "2016-01-30", 1, "M");
    LoginPage loginPage = new LoginPage(testWebDriver, baseUrlGlobal);
    HomePage homePage = loginPage.loginAs(userSIC, password);
    homePage.navigateInitiateRnRScreenAndSelectingRequiredFields(program, "Emergency");
    InitiateRnRPage initiateRnRPage = homePage.clickProceed();
    dbWrapper.updateCreatedDateAfterRequisitionIsInitiated(createdDate);
    testWebDriver.refresh();
    enterDetailsForFirstProduct(10, 5, null, 5, 99, 0);
    initiateRnRPage.verifyNormalizedConsumptionForFirstProduct(5);
    initiateRnRPage.verifyAmcForFirstProduct(5);
    initiateRnRPage.skipSingleProduct(2);
    submitAndAuthorizeRnR();
    verifyNormalizedConsumptionAndAmcInDatabase(5, 5, "P10");
    dbWrapper.updateCreatedDateAfterRequisitionIsInitiated(createdDate);

    homePage.navigateInitiateRnRScreenAndSelectingRequiredFields(program, "Emergency");
    initiateRnRPage = homePage.clickProceed();
    dbWrapper.updateCreatedDateAfterRequisitionIsInitiated(createdDate);
    testWebDriver.refresh();
    initiateRnRPage.skipSingleProduct(1);
    enterDetailsForSecondProduct(10, 5, null, 5, 99, 1);
    initiateRnRPage.verifyNormalizedConsumptionForSecondProduct(8);
    initiateRnRPage.verifyAmcForSecondProduct(8);
    submitAndAuthorizeRnR();
    verifyNormalizedConsumptionAndAmcInDatabase(8, 8, "P11");
    dbWrapper.updateCreatedDateAfterRequisitionIsInitiated(createdDate);
  }

  @Test(groups = "requisition")
  public void testCalculationForEmergencyRnRWhenNumberOfMonthsIs1AndDayDifferenceIsGreaterThanStockOutDays() throws IOException, SQLException {
    String createdDate = "2013-12-03";
    dbWrapper.deleteCurrentPeriod();
    dbWrapper.insertProcessingPeriod("current", "current period", "2013-10-01", "2016-01-30", 1, "M");

    LoginPage loginPage = new LoginPage(testWebDriver, baseUrlGlobal);
    HomePage homePage = loginPage.loginAs(userSIC, password);
    homePage.navigateInitiateRnRScreenAndSelectingRequiredFields(program, "Emergency");
    InitiateRnRPage initiateRnRPage = homePage.clickProceed();
    dbWrapper.updateCreatedDateAfterRequisitionIsInitiated(createdDate);
    testWebDriver.refresh();
    enterDetailsForFirstProduct(10, 5, null, 5, 10, 1);
    initiateRnRPage.verifyNormalizedConsumptionForFirstProduct(6);
    initiateRnRPage.verifyAmcForFirstProduct(6);
    initiateRnRPage.skipSingleProduct(2);
    submitAndAuthorizeRnR();
    verifyNormalizedConsumptionAndAmcInDatabase(6, 6, "P10");
  }

  @Test(groups = "requisition")
  public void testCalculationForEmergencyRnRWhenNumberOfMonthsIs1AndDayDifferenceIsZero() throws IOException, SQLException {
    String createdDate = "2013-12-03";
    dbWrapper.deleteCurrentPeriod();
    dbWrapper.insertProcessingPeriod("current", "current period", "2013-10-01", "2016-01-30", 1, "M");
    dbWrapper.updateProductsByField("dosesPerDispensingUnit", "7", "P10");
    LoginPage loginPage = new LoginPage(testWebDriver, baseUrlGlobal);
    HomePage homePage = loginPage.loginAs(userSIC, password);
    homePage.navigateInitiateRnRScreenAndSelectingRequiredFields(program, "Emergency");
    InitiateRnRPage initiateRnRPage = homePage.clickProceed();
    dbWrapper.updateCreatedDateAfterRequisitionIsInitiated(createdDate);
    testWebDriver.refresh();
    enterDetailsForFirstProduct(10, 5, null, 5, 64, 1);
    initiateRnRPage.verifyNormalizedConsumptionForFirstProduct(9);
    initiateRnRPage.verifyAmcForFirstProduct(9);
    initiateRnRPage.skipSingleProduct(2);
    submitAndAuthorizeRnR();
    verifyNormalizedConsumptionAndAmcInDatabase(9, 9, "P10");
  }


  @Test(groups = "requisition")
  public void testCalculationForEmergencyRnRWhenNumberOfMonthsIs3() throws IOException, SQLException {
    String createdDate1 = "2013-09-03";
    String createdDate2 = "2013-12-03";
    dbWrapper.deleteCurrentPeriod();
    dbWrapper.insertProcessingPeriod("current", "current period", "2013-10-01", "2016-01-30", 3, "M");
    LoginPage loginPage = new LoginPage(testWebDriver, baseUrlGlobal);
    HomePage homePage = loginPage.loginAs(userSIC, password);
    homePage.navigateInitiateRnRScreenAndSelectingRequiredFields(program, "Regular");
    InitiateRnRPage initiateRnRPage = homePage.clickProceed();
    dbWrapper.updateCreatedDateAfterRequisitionIsInitiated(createdDate1);
    testWebDriver.refresh();
    enterDetailsForFirstProduct(10, 5, null, 5, 99, 0);
    initiateRnRPage.skipSingleProduct(2);
    submitAndAuthorizeRnR();
    dbWrapper.updateCreatedDateAfterRequisitionIsInitiated(createdDate1);

    homePage.navigateInitiateRnRScreenAndSelectingRequiredFields(program, "Regular");
    initiateRnRPage = homePage.clickProceed();
    dbWrapper.updateCreatedDateAfterRequisitionIsInitiated(createdDate1);
    testWebDriver.refresh();
    enterDetailsForFirstProduct(10, 5, null, 5, 99, 1);
    initiateRnRPage.skipSingleProduct(2);
    submitAndAuthorizeRnR();
    dbWrapper.updateCreatedDateAfterRequisitionIsInitiated(createdDate1);

    homePage.navigateInitiateRnRScreenAndSelectingRequiredFields(program, "Emergency");
    initiateRnRPage = homePage.clickProceed();
    dbWrapper.updateCreatedDateAfterRequisitionIsInitiated(createdDate2);
    testWebDriver.refresh();
    enterDetailsForFirstProduct(10, 5, null, 5, 99, 0);
    initiateRnRPage.verifyNormalizedConsumptionForFirstProduct(5);
    initiateRnRPage.verifyAmcForFirstProduct(6);
    initiateRnRPage.skipSingleProduct(2);
    submitAndAuthorizeRnR();
    verifyNormalizedConsumptionAndAmcInDatabase(5, 6, "P10");
  }


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

}
