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
    dbWrapper.deleteData();
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
    initiateRnRPage.enterBeginningBalance(10);
    initiateRnRPage.enterQuantityReceived(5);
    initiateRnRPage.enterQuantityDispensed(14);
    initiateRnRPage.enterStockOutDays(0);
    initiateRnRPage.enterNewPatientCount(5);
    initiateRnRPage.verifyNormalizedConsumption(155);
    initiateRnRPage.verifyAmc(155);
    initiateRnRPage.authorizeRnR();
    verifyNormalizedConsumptionAndAmcInDatabase(155,155);
  }

  public void verifyNormalizedConsumptionAndAmcInDatabase(Integer normalizedConsumption, Integer amc) throws IOException, SQLException {
    Long rnrId = Long.valueOf(dbWrapper.getMaxRnrID());
    assertEquals(dbWrapper.getRequisitionLineItemFieldValue(rnrId,"normalizedConsumption","P10"),normalizedConsumption.toString());
    assertEquals(dbWrapper.getRequisitionLineItemFieldValue(rnrId,"amc","P10"),amc.toString());
  }

}
