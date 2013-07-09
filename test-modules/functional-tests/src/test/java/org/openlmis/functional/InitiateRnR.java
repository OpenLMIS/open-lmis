/*
 * Copyright © 2013 VillageReach.  All Rights Reserved.  This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 *
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package org.openlmis.functional;


import com.thoughtworks.selenium.SeleneseTestNgHelper;
import org.openlmis.UiUtils.CaptureScreenshotOnFailureListener;
import org.openlmis.UiUtils.TestCaseHelper;
import org.openlmis.pageobjects.HomePage;
import org.openlmis.pageobjects.InitiateRnRPage;
import org.openlmis.pageobjects.LoginPage;
import org.openlmis.pageobjects.ViewRequisitionPage;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;
import org.testng.annotations.*;

import java.util.ArrayList;
import java.util.List;

import static com.thoughtworks.selenium.SeleneseTestBase.assertEquals;
import static com.thoughtworks.selenium.SeleneseTestBase.assertFalse;
import static com.thoughtworks.selenium.SeleneseTestBase.assertTrue;

@TransactionConfiguration(defaultRollback = true)
@Transactional

@Listeners(CaptureScreenshotOnFailureListener.class)

public class InitiateRnR extends TestCaseHelper {
  @BeforeMethod(groups = {"functional", "smoke"})
  public void setUp() throws Exception {
    super.setup();
  }

  @Test(groups = {"smoke"}, dataProvider = "Data-Provider-Function-Positive")
  public void testRnRWithActiveAndInactiveRegimen(String program, String userSIC, String categoryCode, String password, String regimenCode, String regimenName, String regimenCode2, String regimenName2) throws Exception {
    dbWrapper.setupMultipleProducts(program, "Lvl3 Hospital", 2, false);
    dbWrapper.insertFacilities("F10", "F11");
    dbWrapper.configureTemplate(program);
    List<String> rightsList = new ArrayList<String>();
    rightsList.add("CREATE_REQUISITION");
    rightsList.add("VIEW_REQUISITION");
    setupTestUserRoleRightsData("200", userSIC, "openLmis", rightsList);
    dbWrapper.insertSupervisoryNode("F10", "N1", "Node 1", "null");
    dbWrapper.insertRoleAssignment("200", "store in-charge");
    dbWrapper.insertSchedule("Q1stM", "QuarterMonthly", "QuarterMonth");
    dbWrapper.insertSchedule("M", "Monthly", "Month");
    dbWrapper.insertProcessingPeriod("Period1", "first period", "2012-12-01", "2013-01-15", 1, "Q1stM");
    dbWrapper.insertProcessingPeriod("Period2", "second period", "2013-01-16", "2013-01-30", 1, "M");
    setupRequisitionGroupData("RG1", "RG2", "N1", "N2", "F10", "F11");
    dbWrapper.insertSupplyLines("N1", program, "F10");
    dbWrapper.insertRegimenTemplateColumnsForProgram(program);
    dbWrapper.insertRegimenTemplateConfiguredForProgram(program, categoryCode, regimenCode, regimenName, true);
    dbWrapper.insertRegimenTemplateConfiguredForProgram(program, categoryCode, regimenCode2, regimenName2, false);
    LoginPage loginPage = new LoginPage(testWebDriver, baseUrlGlobal);
    HomePage homePage = loginPage.loginAs(userSIC, password);
    homePage.navigateAndInitiateRnr(program);
    InitiateRnRPage initiateRnRPage = homePage.clickProceed();

    testWebDriver.sleep(2000);
    assertTrue("Regimen tab should be displayed.", initiateRnRPage.existRegimenTab());
    initiateRnRPage.clickRegimenTab();
    assertEquals(initiateRnRPage.getRegimenTableRowCount(), 2);
    assertTrue("Regimen Code should be displayed.", initiateRnRPage.existRegimenCode(regimenCode, 2));
    assertTrue("Regimen Name should be displayed.", initiateRnRPage.existRegimenName(regimenName, 2));

    assertTrue("Reporing Field 1 should be displayed.", initiateRnRPage.existRegimenReportingField(3, 2));
    assertTrue("Reporing Field 2 should be displayed.", initiateRnRPage.existRegimenReportingField(4, 2));
    assertTrue("Reporing Field 3 should be displayed.", initiateRnRPage.existRegimenReportingField(5, 2));
    assertTrue("Reporing Field 4 should be displayed.", initiateRnRPage.existRegimenReportingField(6, 2));

  }

  @Test(groups = {"functional"}, dataProvider = "Data-Provider-Function-Positive")
  public void testRnRWithInvisibleProgramRegimenColumn(String program, String userSIC, String categoryCode, String password, String regimenCode, String regimenName, String regimenCode2, String regimenName2) throws Exception {
    dbWrapper.setupMultipleProducts(program, "Lvl3 Hospital", 2, false);
    dbWrapper.insertFacilities("F10", "F11");
    dbWrapper.configureTemplate(program);
    List<String> rightsList = new ArrayList<String>();
    rightsList.add("CREATE_REQUISITION");
    rightsList.add("VIEW_REQUISITION");
    setupTestUserRoleRightsData("200", userSIC, "openLmis", rightsList);
    dbWrapper.insertSupervisoryNode("F10", "N1", "Node 1", "null");
    dbWrapper.insertRoleAssignment("200", "store in-charge");
    dbWrapper.insertSchedule("Q1stM", "QuarterMonthly", "QuarterMonth");
    dbWrapper.insertSchedule("M", "Monthly", "Month");
    dbWrapper.insertProcessingPeriod("Period1", "first period", "2012-12-01", "2013-01-15", 1, "Q1stM");
    dbWrapper.insertProcessingPeriod("Period2", "second period", "2013-01-16", "2013-01-30", 1, "M");
    setupRequisitionGroupData("RG1", "RG2", "N1", "N2", "F10", "F11");
    dbWrapper.insertSupplyLines("N1", program, "F10");
    dbWrapper.insertRegimenTemplateConfiguredForProgram(program, categoryCode, regimenCode, regimenName, true);
    dbWrapper.insertRegimenTemplateConfiguredForProgram(program, categoryCode, regimenCode2, regimenName2, false);
    dbWrapper.updateProgramRegimenColumns(program, "remarks", false);
    LoginPage loginPage = new LoginPage(testWebDriver, baseUrlGlobal);
    HomePage homePage = loginPage.loginAs(userSIC, password);
    homePage.navigateAndInitiateRnr(program);
    InitiateRnRPage initiateRnRPage = homePage.clickProceed();

    testWebDriver.sleep(2000);
    initiateRnRPage.clickRegimenTab();
    //assertFalse("Reporing Field 4 should not be displayed.",initiateRnRPage.existRegimenReportingField(6,2));
  }

  @Test(groups = {"functional"}, dataProvider = "Data-Provider-Function-Positive")
  public void testRnRWithInActiveRegimen(String program, String userSIC, String categoryCode, String password, String regimenCode, String regimenName, String regimenCode2, String regimenName2) throws Exception {
    dbWrapper.setupMultipleProducts(program, "Lvl3 Hospital", 2, false);
    dbWrapper.insertFacilities("F10", "F11");
    dbWrapper.configureTemplate(program);
    List<String> rightsList = new ArrayList<String>();
    rightsList.add("CREATE_REQUISITION");
    rightsList.add("VIEW_REQUISITION");
    setupTestUserRoleRightsData("200", userSIC, "openLmis", rightsList);
    dbWrapper.insertSupervisoryNode("F10", "N1", "Node 1", "null");
    dbWrapper.insertRoleAssignment("200", "store in-charge");
    dbWrapper.insertSchedule("Q1stM", "QuarterMonthly", "QuarterMonth");
    dbWrapper.insertSchedule("M", "Monthly", "Month");
    dbWrapper.insertProcessingPeriod("Period1", "first period", "2012-12-01", "2013-01-15", 1, "Q1stM");
    dbWrapper.insertProcessingPeriod("Period2", "second period", "2013-01-16", "2013-01-30", 1, "M");
    setupRequisitionGroupData("RG1", "RG2", "N1", "N2", "F10", "F11");
    dbWrapper.insertSupplyLines("N1", program, "F10");
    dbWrapper.insertRegimenTemplateColumnsForProgram(program);
    dbWrapper.insertRegimenTemplateConfiguredForProgram(program, categoryCode, regimenCode, regimenName, false);
    dbWrapper.insertRegimenTemplateConfiguredForProgram(program, categoryCode, regimenCode2, regimenName2, false);
    LoginPage loginPage = new LoginPage(testWebDriver, baseUrlGlobal);
    HomePage homePage = loginPage.loginAs(userSIC, password);
    homePage.navigateAndInitiateRnr(program);
    InitiateRnRPage initiateRnRPage = homePage.clickProceed();

    testWebDriver.sleep(2000);
    assertFalse("Regimen tab should not be displayed.", initiateRnRPage.existRegimenTab());
  }


  @AfterMethod(groups = {"functional", "smoke"})
  public void tearDown() throws Exception {
    HomePage homePage = new HomePage(testWebDriver);
    homePage.logout(baseUrlGlobal);
    dbWrapper.deleteData();
    dbWrapper.closeConnection();
  }

  @DataProvider(name = "Data-Provider-Function-Positive")
  public Object[][] parameterIntTestProviderPositive() {
    return new Object[][]{
      {"HIV", "storeincharge", "ADULTS", "Admin123", "RegimenCode1", "RegimenName1", "RegimenCode2", "RegimenName2"}
    };


  }
}

