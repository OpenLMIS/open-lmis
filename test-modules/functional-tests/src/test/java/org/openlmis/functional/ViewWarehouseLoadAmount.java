/*
 * Copyright © 2013 VillageReach.  All Rights Reserved.  This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 *
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package org.openlmis.functional;


import org.openlmis.UiUtils.CaptureScreenshotOnFailureListener;
import org.openlmis.UiUtils.TestCaseHelper;
import org.openlmis.pageobjects.DistributionPage;
import org.openlmis.pageobjects.HomePage;
import org.openlmis.pageobjects.LoginPage;
import org.openlmis.pageobjects.WarehouseLoadAmountPage;
import org.openqa.selenium.WebElement;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;
import org.testng.annotations.*;

import java.util.ArrayList;
import java.util.List;

import static com.thoughtworks.selenium.SeleneseTestBase.*;
import static com.thoughtworks.selenium.SeleneseTestBase.assertEquals;


@TransactionConfiguration(defaultRollback = true)
@Transactional

@Listeners(CaptureScreenshotOnFailureListener.class)

public class ViewWarehouseLoadAmount extends TestCaseHelper {

  public static final String periodDisplayedByDefault = "Period14";

  @BeforeMethod(groups = {"functional2", "smoke"})
  public void setUp() throws Exception {
    super.setup();
  }


  @Test(groups = {"smoke"}, dataProvider = "Data-Provider-Function")
  public void testShouldViewIsaOverrideIsaAndNoRecords(String userSIC, String password, String deliveryZoneCodeFirst, String deliveryZoneCodeSecond,
                                      String deliveryZoneNameFirst, String deliveryZoneNameSecond,
                                      String facilityCodeFirst, String facilityCodeSecond,
                                      String programFirst, String programSecond, String schedule, String product, String product2) throws Exception {

    List<String> rightsList = new ArrayList<String>();
    rightsList.add("MANAGE_DISTRIBUTION");
    setupTestDataToInitiateRnRForDistribution(true, programFirst, userSIC, "200", "openLmis", rightsList, programSecond);
    setupDataForDeliveryZone(deliveryZoneCodeFirst, deliveryZoneCodeSecond,
      deliveryZoneNameFirst, deliveryZoneNameSecond,
      facilityCodeFirst, facilityCodeSecond,
      programFirst, programSecond, schedule);

    dbWrapper.insertRoleAssignmentForDistribution(userSIC, "store in-charge", deliveryZoneCodeFirst);
    dbWrapper.insertRoleAssignmentForDistribution(userSIC, "store in-charge", deliveryZoneCodeSecond);
    dbWrapper.insertProgramProductISA(programFirst,product,"10","10","10","10",null,null,"0");
    dbWrapper.InsertOverridenIsa(facilityCodeFirst,programFirst,product2,1000);

    LoginPage loginPage = new LoginPage(testWebDriver, baseUrlGlobal);
    HomePage homePage = loginPage.loginAs(userSIC, password);
    DistributionPage distributionPage = homePage.navigatePlanDistribution();

    distributionPage.selectValueFromDeliveryZone(deliveryZoneNameFirst);
    distributionPage.selectValueFromProgram(programFirst);
    distributionPage.selectValueFromPeriod(periodDisplayedByDefault);
    distributionPage.clickViewLoadAmount();



    WarehouseLoadAmountPage  warehouseLoadAmountPage = new WarehouseLoadAmountPage(testWebDriver);
    assertEquals(facilityCodeFirst, warehouseLoadAmountPage.getFacilityCode(1));
    assertEquals(dbWrapper.getFacilityName(facilityCodeFirst), warehouseLoadAmountPage.getFacilityName(1));
    assertEquals(dbWrapper.getFacilityPopulation(facilityCodeFirst), warehouseLoadAmountPage.getFacilityPopulation(1));

    assertEquals(IsaProgramProduct(programFirst,product,warehouseLoadAmountPage.getFacilityPopulation(1)),warehouseLoadAmountPage.getProduct1Isa(1));
    assertEquals(dbWrapper.getOverridenIsa(facilityCodeFirst,programFirst,product2),warehouseLoadAmountPage.getProduct2Isa(1) );

    homePage.navigatePlanDistribution();
    distributionPage.selectValueFromDeliveryZone(deliveryZoneNameSecond);
    distributionPage.selectValueFromProgram(programSecond);
    distributionPage.selectValueFromPeriod(periodDisplayedByDefault);
    distributionPage.clickViewLoadAmount();
    assertEquals("msg.delivery.zone.no.record", warehouseLoadAmountPage.getNoRecordFoundMessage());
  }





  @AfterMethod(groups = {"functional2", "smoke"})
  public void tearDown() throws Exception {
    HomePage homePage = new HomePage(testWebDriver);
    homePage.logout(baseUrlGlobal);
    dbWrapper.deleteData();
    dbWrapper.closeConnection();
  }


  @DataProvider(name = "Data-Provider-Function")
  public Object[][] parameterIntTestProviderPositive() {
    return new Object[][]{
      {"fieldcoordinator", "Admin123", "DZ1", "DZ2", "Delivery Zone First", "Delivery Zone Second",
        "F10", "F11", "VACCINES", "TB", "M", "P10", "P11"}
    };

  }
}

