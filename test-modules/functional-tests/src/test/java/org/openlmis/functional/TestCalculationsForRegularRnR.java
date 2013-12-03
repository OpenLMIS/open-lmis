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
    dbWrapper.updateConfigureTemplate("HIV","source","U","true","quantityDispensed");
    dbWrapper.updateConfigureTemplate("HIV","source","C","true","stockInHand");
    dbWrapper.updateProductsByField("dosesPerDispensingUnit","0","P10");
    dbWrapper.updateProductFullSupplyFlag(false,"P11");
    LoginPage loginPage = new LoginPage(testWebDriver, baseUrlGlobal);
    HomePage homePage = loginPage.loginAs(userSIC, password);
    homePage.navigateInitiateRnRScreenAndSelectingRequiredFields(program, "Regular");
    InitiateRnRPage initiateRnRPage = homePage.clickProceed();
    initiateRnRPage.enterBeginningBalanceForFirstProduct(10);
    initiateRnRPage.enterQuantityReceivedForFirstProduct(5);
    initiateRnRPage.enterQuantityDispensedForFirstProduct(14);
    initiateRnRPage.enterStockOutDaysForFirstProduct(0);
    initiateRnRPage.enterNewPatientCountForFirstProduct(5);
    initiateRnRPage.verifyNormalizedConsumptionForFirstProduct(155);
    initiateRnRPage.verifyAmcForFirstProduct(155);
    initiateRnRPage.submitRnR();
    initiateRnRPage.clickOk();
    initiateRnRPage.authorizeRnR();
    initiateRnRPage.clickOk();
    verifyNormalizedConsumptionAndAmcInDatabase(155,155,"P10");
    verifyNormalizedConsumptionAndAmcInDatabase(155,155,"P11");
  }

  @Test(groups = "requisition")
  public void testCalculationWhenMIs1WithStockOnHandAndQuantityConsumedCalculatedAndMultipleProductsAndXLargerThan30M() throws IOException, SQLException {
    dbWrapper.updateConfigureTemplate("HIV","source","U","true","quantityDispensed");
    dbWrapper.updateConfigureTemplate("HIV","source","U","true","stockInHand");
    dbWrapper.updateConfigureTemplateValidationFlag("HIV","true");
    dbWrapper.updateProductsByField("dosesPerDispensingUnit","7","P10");
    LoginPage loginPage = new LoginPage(testWebDriver, baseUrlGlobal);
    HomePage homePage = loginPage.loginAs(userSIC, password);
    homePage.navigateInitiateRnRScreenAndSelectingRequiredFields(program, "Regular");
    InitiateRnRPage initiateRnRPage = homePage.clickProceed();
    initiateRnRPage.enterBeginningBalanceForFirstProduct(15);
    initiateRnRPage.enterQuantityReceivedForFirstProduct(5);
    initiateRnRPage.enterStockOnHandForFirstProduct(2);
    initiateRnRPage.enterQuantityDispensedForFirstProduct(18);
    initiateRnRPage.enterStockOutDaysForFirstProduct(45);
    initiateRnRPage.enterNewPatientCountForFirstProduct(11);
    initiateRnRPage.verifyNormalizedConsumptionForFirstProduct(62);
    initiateRnRPage.verifyAmcForFirstProduct(62);
    initiateRnRPage.enterBeginningBalanceForSecondProduct(8);
    initiateRnRPage.enterQuantityReceivedForSecondProduct(5);
    initiateRnRPage.enterStockOnHandForSecondProduct(5);
    initiateRnRPage.enterQuantityDispensedForSecondProduct(8);
    initiateRnRPage.enterStockOutDaysForSecondProduct(20);
    initiateRnRPage.enterNewPatientCountForSecondProduct(10);
    initiateRnRPage.verifyNormalizedConsumptionForSecondProduct(54);
    initiateRnRPage.verifyAmcForSecondProduct(54);
    initiateRnRPage.submitRnR();
    initiateRnRPage.clickOk();
    initiateRnRPage.authorizeRnR();
    initiateRnRPage.clickOk();
    verifyNormalizedConsumptionAndAmcInDatabase(62,62,"P10");
    verifyNormalizedConsumptionAndAmcInDatabase(54,54,"P11");
  }

  public void verifyNormalizedConsumptionAndAmcInDatabase(Integer normalizedConsumption, Integer amc, String productCode) throws IOException, SQLException {
    Long rnrId = Long.valueOf(dbWrapper.getMaxRnrID());
    assertEquals(dbWrapper.getRequisitionLineItemFieldValue(rnrId,"normalizedConsumption",productCode),normalizedConsumption.toString());
    assertEquals(dbWrapper.getRequisitionLineItemFieldValue(rnrId, "amc", productCode), amc.toString());
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
