/*
 * Copyright © 2013 VillageReach.  All Rights Reserved.  This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 *
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package org.openlmis.functional;


import cucumber.api.DataTable;
import cucumber.api.java.After;
import cucumber.api.java.en.And;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import org.openlmis.UiUtils.CaptureScreenshotOnFailureListener;
import org.openlmis.UiUtils.TestCaseHelper;
import org.openlmis.pageobjects.*;
import org.openqa.selenium.JavascriptExecutor;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;
import org.testng.annotations.*;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static com.thoughtworks.selenium.SeleneseTestBase.assertFalse;


@TransactionConfiguration(defaultRollback = true)
@Transactional

@Listeners(CaptureScreenshotOnFailureListener.class)

public class E2EDistributionTest extends TestCaseHelper {

  public String userSIC, password;
  public static final String periodDisplayedByDefault = "Period14";
  public static final String periodNotToBeDisplayedInDropDown = "Period1";

  @BeforeMethod(groups = {"offline"})
  public void setUp() throws Exception {
    super.setup();
  }

    @Test(groups = {"offline"}, dataProvider = "Data-Provider-Function")
    public void testEditEPIUseOffline(String userSIC, String password, String deliveryZoneCodeFirst, String deliveryZoneCodeSecond,
                               String deliveryZoneNameFirst, String deliveryZoneNameSecond,
                               String facilityCodeFirst, String facilityCodeSecond,
                               String programFirst, String programSecond, String schedule, String period, Integer totalNumberOfPeriods) throws Exception {

        List<String> rightsList = new ArrayList<String>();
        rightsList.add("MANAGE_DISTRIBUTION");
        setupTestDataToInitiateRnRAndDistribution("F10", "F11", true, programFirst, userSIC, "200", "openLmis", rightsList, programSecond, "District1", "Ngorongoro", "Ngorongoro");
        setupDataForDeliveryZone(true, deliveryZoneCodeFirst, deliveryZoneCodeSecond,
                deliveryZoneNameFirst, deliveryZoneNameSecond,
                facilityCodeFirst, facilityCodeSecond,
                programFirst, programSecond, schedule);
        dbWrapper.insertRoleAssignmentForDistribution(userSIC, "store in-charge", deliveryZoneCodeFirst);
        dbWrapper.insertRoleAssignmentForDistribution(userSIC, "store in-charge", deliveryZoneCodeSecond);
        dbWrapper.insertProductGroup("PG1");
        dbWrapper.insertProductWithGroup("Product5", "ProdutName5", "PG1", true);
        dbWrapper.insertProductWithGroup("Product6", "ProdutName6", "PG1", true);
        dbWrapper.insertProgramProduct("Product5",programFirst,"10","false");
        dbWrapper.insertProgramProduct("Product6",programFirst,"10","true");

        LoginPage loginPage = new LoginPage(testWebDriver, baseUrlGlobal);
        HomePage homePage = loginPage.loginAs(userSIC, password);
        DistributionPage distributionPage = homePage.navigatePlanDistribution();
        distributionPage.selectValueFromDeliveryZone(deliveryZoneNameFirst);
        distributionPage.selectValueFromProgram(programFirst);
        distributionPage.clickInitiateDistribution();

        testWebDriver.sleep(10000);
        switchOffNetwork();
        testWebDriver.sleep(2000);
        homePage.navigateHomePage();
        homePage.navigateOfflineDistribution();
        assertFalse("Delivery Zone selectbox displayed.", distributionPage.verifyDeliveryZoneSelectBoxNotPresent());
        assertFalse("Period selectbox displayed.", distributionPage.verifyPeriodSelectBoxNotPresent());
        assertFalse("Program selectbox displayed.",distributionPage.verifyProgramSelectBoxNotPresent());

        distributionPage.clickRecordData();
        FacilityListPage facilityListPage = new FacilityListPage(testWebDriver);
        facilityListPage.selectFacility("F10");
        EPIUse epiUse = new EPIUse(testWebDriver);
        epiUse.navigate();
        epiUse.verifyProductGroup("PG1-Name",1);
        epiUse.verifyIndicator("RED");

        epiUse.enterValueInStockAtFirstOfMonth("10",1);
        epiUse.verifyIndicator("AMBER");
        epiUse.enterValueInReceived("20", 1);
        epiUse.enterValueInDistributed("30", 1);
        epiUse.enterValueInLoss("40", 1);
        epiUse.enterValueInStockAtEndOfMonth("50",1);
        epiUse.enterValueInExpirationDate("10/2011",1);


        RefrigeratorPage refrigeratorPage = new RefrigeratorPage(testWebDriver);
        refrigeratorPage.navigateToRefrigeratorTab();
        epiUse.navigate();

        epiUse.verifyTotal("30",1);
        epiUse.verifyStockAtFirstOfMonth("10", 1);
        epiUse.verifyReceived("20", 1);
        epiUse.verifyDistributed("30", 1);
        epiUse.verifyLoss("40", 1);
        epiUse.verifyStockAtEndOfMonth("50", 1);
        epiUse.verifyExpirationDate("10/2011", 1);
    }

    @AfterMethod(groups = {"offline"})
    public void tearDownNew() throws Exception {
        switchOnNetwork();
        testWebDriver.sleep(5000);
        dbWrapper.deleteData();
        dbWrapper.closeConnection();
        ((JavascriptExecutor) testWebDriver.getDriver()).executeScript("indexedDB.deleteDatabase('open_lmis');");
    }


    @DataProvider(name = "Data-Provider-Function")
    public Object[][] parameterIntTestProviderPositive() {
        return new Object[][]{
                {"storeincharge", "Admin123", "DZ1", "DZ2", "Delivery Zone First", "Delivery Zone Second",
                        "F10", "F11", "VACCINES", "TB", "M", "Period", 14}
        };

  }
}

