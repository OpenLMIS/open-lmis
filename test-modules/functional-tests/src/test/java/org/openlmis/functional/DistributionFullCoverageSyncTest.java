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
import org.openlmis.UiUtils.TestWebDriver;
import org.openlmis.pageobjects.*;
import org.openqa.selenium.JavascriptExecutor;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.io.IOException;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import static com.thoughtworks.selenium.SeleneseTestBase.assertEquals;
import static com.thoughtworks.selenium.SeleneseTestBase.assertTrue;

public class DistributionFullCoverageSyncTest extends TestCaseHelper {
  public static final String USER = "user";
  public static final String PASSWORD = "password";
  public static final String FIRST_DELIVERY_ZONE_CODE = "firstDeliveryZoneCode";
  public static final String SECOND_DELIVERY_ZONE_CODE = "secondDeliveryZoneCode";
  public static final String FIRST_DELIVERY_ZONE_NAME = "firstDeliveryZoneName";
  public static final String SECOND_DELIVERY_ZONE_NAME = "secondDeliveryZoneName";
  public static final String FIRST_FACILITY_CODE = "firstFacilityCode";
  public static final String SECOND_FACILITY_CODE = "secondFacilityCode";
  public static final String VACCINES_PROGRAM = "vaccinesProgram";
  public static final String TB_PROGRAM = "secondProgram";
  public static final String SCHEDULE = "schedule";
  public static final String PRODUCT_GROUP_CODE = "productGroupName";
  LoginPage loginPage;

  public final Map<String, String> fullCoverageData = new HashMap<String, String>() {{
    put(USER, "fieldCoordinator");
    put(PASSWORD, "Admin123");
    put(FIRST_DELIVERY_ZONE_CODE, "DZ1");
    put(SECOND_DELIVERY_ZONE_CODE, "DZ2");
    put(FIRST_DELIVERY_ZONE_NAME, "Delivery Zone First");
    put(SECOND_DELIVERY_ZONE_NAME, "Delivery Zone Second");
    put(FIRST_FACILITY_CODE, "F10");
    put(SECOND_FACILITY_CODE, "F11");
    put(VACCINES_PROGRAM, "VACCINES");
    put(TB_PROGRAM, "TB");
    put(SCHEDULE, "M");
    put(PRODUCT_GROUP_CODE, "PG1");
  }};

  @BeforeMethod(groups = {"distribution"})
  public void setUp() throws InterruptedException, SQLException, IOException {
    super.setup();
    setupDataForDistributionTest(fullCoverageData);
    dbWrapper.insertProductGroup(fullCoverageData.get(PRODUCT_GROUP_CODE));
    dbWrapper.insertProductWithGroup("Product5", "ProductName5", fullCoverageData.get(PRODUCT_GROUP_CODE), true);
    dbWrapper.insertProductWithGroup("Product6", "ProductName6", fullCoverageData.get(PRODUCT_GROUP_CODE), true);
    dbWrapper.insertProgramProduct("Product5", fullCoverageData.get(VACCINES_PROGRAM), "10", "false");
    dbWrapper.insertProgramProduct("Product6", fullCoverageData.get(VACCINES_PROGRAM), "10", "true");
    loginPage = PageObjectFactory.getLoginPage(testWebDriver, baseUrlGlobal);
  }

  @Test(groups = {"distribution"})
  public void shouldVerifyLabelsAndTestApplyNRToAllAndSync() throws SQLException {
    HomePage homePage = loginPage.loginAs(fullCoverageData.get(USER), fullCoverageData.get(PASSWORD));
    DistributionPage distributionPage = homePage.navigateToDistributionWhenOnline();
    distributionPage.initiate(fullCoverageData.get(FIRST_DELIVERY_ZONE_NAME), fullCoverageData.get(VACCINES_PROGRAM));
    FacilityListPage facilityListPage = distributionPage.clickRecordData(1);
    VisitInformationPage visitInformationPage = facilityListPage.selectFacility(fullCoverageData.get(FIRST_FACILITY_CODE));
    visitInformationPage.enterDataWhenFacilityVisited("samuel", "Doe", "Verifier", "XYZ");

    facilityListPage.verifyOverallFacilityIndicatorColor("AMBER");

    FullCoveragePage fullCoveragePage = visitInformationPage.navigateToFullCoverage();
    fullCoveragePage.verifyIndicator("RED");

    assertEquals("Full Coverage", fullCoveragePage.getFullCoverageTabLabel());
    assertEquals("Full Coverage", fullCoveragePage.getTextOfFullCoverageHeader());
    assertEquals("Completely Vaccinated Children(doses)", fullCoveragePage.getTextOfCompletelyVaccinatedHeader());
    assertEquals("Females", fullCoveragePage.getTextOfFemaleHeader());
    assertEquals("Males", fullCoveragePage.getTextOfMaleHeader());
    assertEquals("Health Center", fullCoveragePage.getTextOfHealthCenterHeader());
    assertEquals("Outreach", fullCoveragePage.getTextOfMobileBrigadeHeader());

    fullCoveragePage.enterFemaleHealthCenter(5);
    fullCoveragePage.clickApplyNRToAll();
    fullCoveragePage.clickApplyNRToAll(); //just checking reapplying NR to all doesn't deselect NR check boxes
    fullCoveragePage.verifyIndicator("GREEN");

    verifyAllFieldsDisabled();
    verifyDataOnFullCoveragePage("", "", "", "");

    EPIUsePage epiUsePage = fullCoveragePage.navigateToEpiUse();
    epiUsePage.enterData(10, 20, 30, 40, 50, "10/2011", 1);

    ChildCoveragePage childCoveragePage = epiUsePage.navigateToChildCoverage();
    childCoveragePage.applyNRToAll();
    childCoveragePage.clickOK();

    AdultCoveragePage adultCoveragePage = childCoveragePage.navigateToAdultCoverage();
    adultCoveragePage.clickApplyNrToAll();
    adultCoveragePage.clickOK();

    adultCoveragePage.navigateToEpiInventory();
    fillEpiInventoryWithOnlyDeliveredQuantity("2", "4", "6");

    facilityListPage.verifyOverallFacilityIndicatorColor("GREEN");

    homePage.navigateToDistributionWhenOnline();
    distributionPage.syncDistribution(1);
    assertTrue(distributionPage.getSyncMessage().contains("F10-Village Dispensary"));
    distributionPage.syncDistributionMessageDone();
    verifyFullCoveragesDataInDatabase(null, null, null, null, fullCoverageData.get(FIRST_FACILITY_CODE));
  }

  @Test(groups = {"distribution"})
  public void shouldTestFullCoverageAndFacilityIconStatusAndSync() throws SQLException {
    dbWrapper.addRefrigeratorToFacility("brand", "model", "serial", "F10");

    HomePage homePage = loginPage.loginAs(fullCoverageData.get(USER), fullCoverageData.get(PASSWORD));
    DistributionPage distributionPage = homePage.navigateToDistributionWhenOnline();
    distributionPage.initiate(fullCoverageData.get(FIRST_DELIVERY_ZONE_NAME), fullCoverageData.get(VACCINES_PROGRAM));
    FacilityListPage facilityListPage = distributionPage.clickRecordData(1);
    VisitInformationPage visitInformationPage = facilityListPage.selectFacility(fullCoverageData.get(FIRST_FACILITY_CODE));

    facilityListPage.verifyOverallFacilityIndicatorColor("RED");
    visitInformationPage.enterDataWhenFacilityVisited("samuel", "Doe", "Verifier", "XYZ");

    FullCoveragePage fullCoveragePage = visitInformationPage.navigateToFullCoverage();
    fullCoveragePage.verifyIndicator("RED");
    fullCoveragePage.toggleApplyNRToFemaleMobileBrigade();
    fullCoveragePage.verifyIndicator("AMBER");

    fullCoveragePage.enterFemaleHealthCenter(9999999);
    fullCoveragePage.toggleApplyNRToMaleHealthCenter();
    fullCoveragePage.toggleApplyNRToMaleHealthCenter();
    fullCoveragePage.enterMaleHealthCenter(10);
    fullCoveragePage.enterMaleMobileBrigade("0");
    fullCoveragePage.toggleApplyNRToMaleMobileBrigade();

    fullCoveragePage.verifyIndicator("GREEN");

    verifyEnableStatusOfFields(true, false, true, false);
    verifyDataOnFullCoveragePage("9999999", "", "10", "");

    facilityListPage.verifyOverallFacilityIndicatorColor("AMBER");

    EPIUsePage epiUsePage = fullCoveragePage.navigateToEpiUse();
    epiUsePage.enterData(10, 20, 30, 40, 50, "10/2011", 1);

    ChildCoveragePage childCoveragePage = epiUsePage.navigateToChildCoverage();
    childCoveragePage.applyNRToAll();
    childCoveragePage.clickOK();

    AdultCoveragePage adultCoveragePage = childCoveragePage.navigateToAdultCoverage();
    adultCoveragePage.clickApplyNrToAll();
    adultCoveragePage.clickOK();

    RefrigeratorPage refrigeratorPage = adultCoveragePage.navigateToRefrigerators();
    refrigeratorPage.clickDelete();
    refrigeratorPage.clickOKButton();

    refrigeratorPage.navigateToEpiInventory();
    fillEpiInventoryWithOnlyDeliveredQuantity("2", "4", "6");

    facilityListPage.verifyOverallFacilityIndicatorColor("GREEN");

    homePage.navigateToDistributionWhenOnline();
    distributionPage.syncDistribution(1);
    assertTrue(distributionPage.getSyncMessage().contains("F10-Village Dispensary"));
    distributionPage.syncDistributionMessageDone();

    distributionPage.clickRecordData(1);
    facilityListPage.selectFacility(fullCoverageData.get(FIRST_FACILITY_CODE));

    facilityListPage.verifyOverallFacilityIndicatorColor("BLUE");

    refrigeratorPage.navigateToFullCoverage();
    verifyAllFieldsDisabled();
    verifyFullCoveragesDataInDatabase(9999999, null, 10, null, fullCoverageData.get(FIRST_FACILITY_CODE));
  }

  @Test(groups = {"distribution"})
  public void shouldTestFillFullCoverageFormAndSync() throws SQLException {
    HomePage homePage = loginPage.loginAs(fullCoverageData.get(USER), fullCoverageData.get(PASSWORD));
    DistributionPage distributionPage = homePage.navigateToDistributionWhenOnline();
    distributionPage.initiate(fullCoverageData.get(FIRST_DELIVERY_ZONE_NAME), fullCoverageData.get(VACCINES_PROGRAM));
    FacilityListPage facilityListPage = distributionPage.clickRecordData(1);
    VisitInformationPage visitInformationPage = facilityListPage.selectFacility(fullCoverageData.get(FIRST_FACILITY_CODE));

    facilityListPage.verifyOverallFacilityIndicatorColor("AMBER");
    visitInformationPage.enterDataWhenFacilityVisited("samuel", "Doe", "Verifier", "XYZ");

    FullCoveragePage fullCoveragePage = visitInformationPage.navigateToFullCoverage();
    fullCoveragePage.verifyIndicator("RED");
    fullCoveragePage.enterData(12, 34, 45, "56");
    fullCoveragePage.verifyIndicator("GREEN");

    EPIUsePage epiUsePage = fullCoveragePage.navigateToEpiUse();
    epiUsePage.enterData(10, 20, 30, 40, 50, "10/2011", 1);

    ChildCoveragePage childCoveragePage = epiUsePage.navigateToChildCoverage();
    childCoveragePage.applyNRToAll();
    childCoveragePage.clickOK();

    AdultCoveragePage adultCoveragePage = childCoveragePage.navigateToAdultCoverage();
    adultCoveragePage.clickApplyNrToAll();
    adultCoveragePage.clickOK();

    adultCoveragePage.navigateToEpiInventory();
    fillEpiInventoryWithOnlyDeliveredQuantity("2", "4", "6");

    facilityListPage.verifyOverallFacilityIndicatorColor("GREEN");

    homePage.navigateToDistributionWhenOnline();
    distributionPage.syncDistribution(1);
    assertTrue(distributionPage.getSyncMessage().contains("F10-Village Dispensary"));
    distributionPage.syncDistributionMessageDone();
    verifyFullCoveragesDataInDatabase(12, 34, 45, 56, fullCoverageData.get(FIRST_FACILITY_CODE));
  }

  @Test(groups = {"distribution"})
  public void shouldTestSyncIncompleteFullCoverageFormUnsuccessful() {
    HomePage homePage = loginPage.loginAs(fullCoverageData.get(USER), fullCoverageData.get(PASSWORD));
    DistributionPage distributionPage = homePage.navigateToDistributionWhenOnline();
    distributionPage.initiate(fullCoverageData.get(FIRST_DELIVERY_ZONE_NAME), fullCoverageData.get(VACCINES_PROGRAM));
    FacilityListPage facilityListPage = distributionPage.clickRecordData(1);
    VisitInformationPage visitInformationPage = facilityListPage.selectFacility(fullCoverageData.get(FIRST_FACILITY_CODE));

    facilityListPage.verifyOverallFacilityIndicatorColor("AMBER");
    visitInformationPage.enterDataWhenFacilityVisited("samuel", "Doe", "Verifier", "XYZ");

    FullCoveragePage fullCoveragePage = visitInformationPage.navigateToFullCoverage();
    fullCoveragePage.verifyIndicator("RED");
    fullCoveragePage.enterMaleHealthCenter(33);
    fullCoveragePage.enterFemaleHealthCenter(67);
    fullCoveragePage.enterMaleMobileBrigade("0");
    fullCoveragePage.toggleApplyNRToMaleMobileBrigade();
    fullCoveragePage.verifyIndicator("AMBER");

    EPIUsePage epiUsePage = fullCoveragePage.navigateToEpiUse();
    epiUsePage.enterData(10, 20, 30, 40, 50, "10/2011", 1);

    epiUsePage.navigateToFullCoverage();
    verifyDataOnFullCoveragePage("67", "", "33", "");
    verifyEnableStatusOfFields(true, true, true, false);

    ChildCoveragePage childCoveragePage = epiUsePage.navigateToChildCoverage();
    childCoveragePage.applyNRToAll();
    childCoveragePage.clickOK();

    AdultCoveragePage adultCoveragePage = childCoveragePage.navigateToAdultCoverage();
    adultCoveragePage.clickApplyNrToAll();
    adultCoveragePage.clickOK();

    adultCoveragePage.navigateToEpiInventory();
    fillEpiInventoryWithOnlyDeliveredQuantity("2", "4", "6");

    facilityListPage.verifyOverallFacilityIndicatorColor("AMBER");

    homePage.navigateToDistributionWhenOnline();
    distributionPage.clickSyncDistribution(1);
    assertEquals("No facility for the chosen zone, program and period is ready to be sync", distributionPage.getSyncAlertMessage());
  }

  public void fillEpiInventoryWithOnlyDeliveredQuantity(String deliveredQuantity1, String deliveredQuantity2, String deliveredQuantity3) {
    EpiInventoryPage epiInventoryPage = PageObjectFactory.getEpiInventoryPage(testWebDriver);
    epiInventoryPage.applyNRToAll();
    epiInventoryPage.fillDeliveredQuantity(1, deliveredQuantity1);
    epiInventoryPage.fillDeliveredQuantity(2, deliveredQuantity2);
    epiInventoryPage.fillDeliveredQuantity(3, deliveredQuantity3);
  }

  private void verifyEnableStatusOfFields(boolean femaleHealthCenterFieldStatus, boolean femaleMobileBrigadeFieldStatus,
                                          boolean maleHealthCenterFieldStatus, boolean maleMobileBrigadeFieldStatus) {
    FullCoveragePage fullCoveragePage = PageObjectFactory.getFullCoveragePage(testWebDriver);
    assertEquals(femaleHealthCenterFieldStatus, fullCoveragePage.getStatusForField("femaleHealthCenter"));
    assertEquals(femaleMobileBrigadeFieldStatus, fullCoveragePage.getStatusForField("femaleMobileBrigade"));
    assertEquals(maleHealthCenterFieldStatus, fullCoveragePage.getStatusForField("maleHealthCenter"));
    assertEquals(maleMobileBrigadeFieldStatus, fullCoveragePage.getStatusForField("maleMobileBrigade"));
  }

  private void verifyDataOnFullCoveragePage(String femaleHealthCenterValue, String femaleMobileBrigadeValue,
                                            String maleHealthCenterValue, String maleMobileBrigadeValue) {
    FullCoveragePage fullCoveragePage = PageObjectFactory.getFullCoveragePage(testWebDriver);
    assertEquals(femaleHealthCenterValue, fullCoveragePage.getValueForField("femaleHealthCenter"));
    assertEquals(femaleMobileBrigadeValue, fullCoveragePage.getValueForField("femaleMobileBrigade"));
    assertEquals(maleHealthCenterValue, fullCoveragePage.getValueForField("maleHealthCenter"));
    assertEquals(maleMobileBrigadeValue, fullCoveragePage.getValueForField("maleMobileBrigade"));
  }

  private void verifyAllFieldsDisabled() {
    verifyEnableStatusOfFields(false, false, false, false);
  }

  @AfterMethod(groups = "distribution")
  public void tearDown() throws SQLException {
    testWebDriver.sleep(500);
    if (!testWebDriver.getElementById("username").isDisplayed()) {
      HomePage homePage = PageObjectFactory.getHomePage(testWebDriver);
      homePage.logout(baseUrlGlobal);
      dbWrapper.deleteData();
      dbWrapper.closeConnection();
    }
    ((JavascriptExecutor) TestWebDriver.getDriver()).executeScript("indexedDB.deleteDatabase('open_lmis');");
  }
}
