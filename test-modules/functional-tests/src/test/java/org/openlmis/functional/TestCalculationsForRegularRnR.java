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

import static com.thoughtworks.selenium.SeleneseTestBase.assertEquals;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class TestCalculationsForRegularRnR extends TestCaseHelper {

  public String program = "HIV", userSIC = "storeInCharge", password = "Admin123";

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
    initiateRnRPage.submitRnR();
    initiateRnRPage.clickOk();
    initiateRnRPage.authorizeRnR();
    initiateRnRPage.clickOk();
    verifyNormalizedConsumptionAndAmcInDatabase(164, 164, "P10");
  }

  @Test(groups = "requisition")
  public void testCalculationWhenMIs1WithStockOnHandAndQuantityConsumedCalculatedAndMultipleProductsAndXLargerThan30M() throws IOException, SQLException {
    dbWrapper.updateConfigureTemplate("HIV", "source", "U", "true", "quantityDispensed");
    dbWrapper.updateConfigureTemplate("HIV", "source", "U", "true", "stockInHand");
    dbWrapper.updateConfigureTemplateValidationFlag("HIV", "true");
    dbWrapper.updateProductsByField("dosesPerDispensingUnit", "7", "P10");
    dbWrapper.insertProcessingPeriod("period3", "feb2013", "2013-01-31", "2013-02-28", 1, "M");
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
    initiateRnRPage.submitRnR();
    initiateRnRPage.clickOk();
    initiateRnRPage.authorizeRnR();
    initiateRnRPage.clickOk();
    verifyNormalizedConsumptionAndAmcInDatabase(62, 62, "P10");
    verifyNormalizedConsumptionAndAmcInDatabase(54, 54, "P11");

    homePage.navigateInitiateRnRScreenAndSelectingRequiredFields(program, "Regular");
    initiateRnRPage = homePage.clickProceed();
    initiateRnRPage.skipSingleProduct(2);
    enterDetailsForFirstProduct(20, 0, 7, 13, 23, 0);
    initiateRnRPage.verifyNormalizedConsumptionForFirstProduct(56);
    initiateRnRPage.verifyAmcForFirstProduct(59);
    initiateRnRPage.submitRnR();
    initiateRnRPage.clickOk();
    initiateRnRPage.authorizeRnR();
    initiateRnRPage.clickOk();
    verifyNormalizedConsumptionAndAmcInDatabase(56, 59, "P10");
  }

  public void verifyNormalizedConsumptionAndAmcInDatabase(Integer normalizedConsumption, Integer amc, String productCode) throws IOException, SQLException {
    Long rnrId = Long.valueOf(dbWrapper.getMaxRnrID());
    assertEquals(dbWrapper.getRequisitionLineItemFieldValue(rnrId, "normalizedConsumption", productCode), normalizedConsumption.toString());
    assertEquals(dbWrapper.getRequisitionLineItemFieldValue(rnrId, "amc", productCode), amc.toString());
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
    initiateRnRPage.submitRnR();
    initiateRnRPage.clickOk();
    initiateRnRPage.authorizeRnR();
    initiateRnRPage.clickOk();
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
    initiateRnRPage.submitRnR();
    initiateRnRPage.clickOk();
    initiateRnRPage.authorizeRnR();
    initiateRnRPage.clickOk();
    dbWrapper.updateCreatedDateAfterRequisitionIsInitiated(createdDate1);

    homePage.navigateInitiateRnRScreenAndSelectingRequiredFields(program, "Emergency");
    initiateRnRPage = homePage.clickProceed();
    dbWrapper.updateCreatedDateAfterRequisitionIsInitiated(createdDate2);
    testWebDriver.refresh();
    enterDetailsForFirstProduct(10, 5, null, 5, 99, 0);
    initiateRnRPage.verifyNormalizedConsumptionForFirstProduct(5);
    initiateRnRPage.verifyAmcForFirstProduct(5);
    initiateRnRPage.skipSingleProduct(2);
    initiateRnRPage.submitRnR();
    initiateRnRPage.clickOk();
    initiateRnRPage.authorizeRnR();
    initiateRnRPage.clickOk();
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
    initiateRnRPage.submitRnR();
    initiateRnRPage.clickOk();
    initiateRnRPage.authorizeRnR();
    initiateRnRPage.clickOk();
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
    initiateRnRPage.submitRnR();
    initiateRnRPage.clickOk();
    initiateRnRPage.authorizeRnR();
    initiateRnRPage.clickOk();
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
    initiateRnRPage.submitRnR();
    initiateRnRPage.clickOk();
    initiateRnRPage.authorizeRnR();
    initiateRnRPage.clickOk();
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
    initiateRnRPage.submitRnR();
    initiateRnRPage.clickOk();
    initiateRnRPage.authorizeRnR();
    initiateRnRPage.clickOk();
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
    initiateRnRPage.submitRnR();
    initiateRnRPage.clickOk();
    initiateRnRPage.authorizeRnR();
    initiateRnRPage.clickOk();
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
    initiateRnRPage.submitRnR();
    initiateRnRPage.clickOk();
    initiateRnRPage.authorizeRnR();
    initiateRnRPage.clickOk();
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
    initiateRnRPage.submitRnR();
    initiateRnRPage.clickOk();
    initiateRnRPage.authorizeRnR();
    initiateRnRPage.clickOk();
    dbWrapper.updateCreatedDateAfterRequisitionIsInitiated(createdDate1);

    homePage.navigateInitiateRnRScreenAndSelectingRequiredFields(program, "Regular");
    initiateRnRPage = homePage.clickProceed();
    dbWrapper.updateCreatedDateAfterRequisitionIsInitiated(createdDate1);
    testWebDriver.refresh();
    enterDetailsForFirstProduct(10, 5, null, 5, 99, 1);
    initiateRnRPage.skipSingleProduct(2);
    initiateRnRPage.submitRnR();
    initiateRnRPage.clickOk();
    initiateRnRPage.authorizeRnR();
    initiateRnRPage.clickOk();
    dbWrapper.updateCreatedDateAfterRequisitionIsInitiated(createdDate1);

    homePage.navigateInitiateRnRScreenAndSelectingRequiredFields(program, "Emergency");
    initiateRnRPage = homePage.clickProceed();
    dbWrapper.updateCreatedDateAfterRequisitionIsInitiated(createdDate2);
    testWebDriver.refresh();
    enterDetailsForFirstProduct(10, 5, null, 5, 99, 0);
    initiateRnRPage.verifyNormalizedConsumptionForFirstProduct(5);
    initiateRnRPage.verifyAmcForFirstProduct(6);
    initiateRnRPage.skipSingleProduct(2);
    initiateRnRPage.submitRnR();
    initiateRnRPage.clickOk();
    initiateRnRPage.authorizeRnR();
    initiateRnRPage.clickOk();
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
