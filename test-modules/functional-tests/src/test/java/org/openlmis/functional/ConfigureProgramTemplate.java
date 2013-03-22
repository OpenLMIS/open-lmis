/*
 * Copyright © 2013 VillageReach.  All Rights Reserved.  This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 *
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package org.openlmis.functional;


import org.openlmis.UiUtils.CaptureScreenshotOnFailureListener;
import org.openlmis.UiUtils.TestCaseHelper;
import org.openlmis.pageobjects.HomePage;
import org.openlmis.pageobjects.InitiateRnRPage;
import org.openlmis.pageobjects.LoginPage;
import org.openlmis.pageobjects.TemplateConfigPage;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;
import org.testng.annotations.*;

@TransactionConfiguration(defaultRollback = true)
@Transactional

@Listeners(CaptureScreenshotOnFailureListener.class)

public class ConfigureProgramTemplate extends TestCaseHelper {

  @BeforeMethod(groups = {"functional"})
  public void setUp() throws Exception {
    super.setup();
  }

  @Test(groups = {"functional"}, dataProvider = "Data-Provider-Program-Not-Configured")
  public void testVerifyProgramNotConfigured(String program, String userSIC,String password) throws Exception {
    dbWrapper.insertProducts("P10", "P11");
    dbWrapper.insertProgramProducts("P10", "P11", program);
    dbWrapper.insertFacilityApprovedProducts("P10", "P11", program, "Lvl3 Hospital");
    dbWrapper.insertFacilities("F10", "F11");
    dbWrapper.insertRole("store in-charge", "false", "");
    dbWrapper.insertRole("district pharmacist", "false", "");
    dbWrapper.insertRoleRights();
    String passwordUsers = "TQskzK3iiLfbRVHeM1muvBCiiKriibfl6lh8ipo91hb74G3OvsybvkzpPI4S3KIeWTXAiiwlUU0iiSxWii4wSuS8mokSAieie";
    dbWrapper.insertUser("200", userSIC, passwordUsers, "F10", "Fatima_Doe@openlmis.com");
    dbWrapper.insertSupervisoryNode("F10", "N1", "Node 1", "null");
    dbWrapper.insertRoleAssignment("200", "store in-charge");
    dbWrapper.insertSchedules();
    dbWrapper.insertProcessingPeriods();
    dbWrapper.insertRequisitionGroups("RG1", "RG2", "N1", "N2");
    dbWrapper.insertRequisitionGroupMembers("F10", "F11");
    dbWrapper.insertRequisitionGroupProgramSchedule();
    dbWrapper.insertSupplyLines("N1", program, "F10");

    LoginPage loginPage = new LoginPage(testWebDriver, baseUrlGlobal);
    HomePage homePage = loginPage.loginAs(userSIC, password);
    String periodDetails = homePage.navigateAndInitiateRnr(program);
    InitiateRnRPage initiateRnRPage = homePage.clickProceed();
    initiateRnRPage.verifyTemplateNotConfiguredMessage();


  }

  @Test(groups = {"functional"}, dataProvider = "Data-Provider-Column-Label-Source")
  public void testVerifyColumnLabelsSourceAndMandatoryColumns(String program, String[] credentials) throws Exception {
    LoginPage loginPage = new LoginPage(testWebDriver, baseUrlGlobal);
    HomePage homePage = loginPage.loginAs(credentials[0], credentials[1]);

    TemplateConfigPage templateConfigPage = homePage.selectProgramToConfigTemplate(program);
    templateConfigPage.verifyMandatoryColumns();
    templateConfigPage.verifyColumnLabels();
    templateConfigPage.verifyColumnSource();

  }

  @Test(groups = {"functional"}, dataProvider = "Data-Provider-Column-Label-Source")
  public void testVerifyArithmeticValidationAndBusinessRules(String program, String[] credentials) throws Exception {
    LoginPage loginPage = new LoginPage(testWebDriver, baseUrlGlobal);
    HomePage homePage = loginPage.loginAs(credentials[0], credentials[1]);

    TemplateConfigPage templateConfigPage = homePage.selectProgramToConfigTemplate(program);
    templateConfigPage.verifyArithmeticValidations();
    templateConfigPage.verifyBusinessRules();


  }

  @AfterMethod(groups = {"functional"})
  public void tearDown() throws Exception {
    HomePage homePage = new HomePage(testWebDriver);
    homePage.logout(baseUrlGlobal);
    dbWrapper.deleteData();
    dbWrapper.closeConnection();
  }


  @DataProvider(name = "Data-Provider-Column-Label-Source")
  public Object[][] parameterColumnLabelSource() {
    return new Object[][]{
        {"HIV", new String[]{"Admin123", "Admin123"}}
    };

  }

  @DataProvider(name = "Data-Provider-Program-Not-Configured")
  public Object[][] parameterProgramNotConfigured() {
    return new Object[][]{
      {"HIV","storeincharge","Admin123"}
    };

  }
}

