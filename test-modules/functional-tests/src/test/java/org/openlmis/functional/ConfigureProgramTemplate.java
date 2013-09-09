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
import org.testng.annotations.Test;
import org.testng.annotations.Listeners;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.DataProvider;

import java.util.ArrayList;
import java.util.List;

@TransactionConfiguration(defaultRollback = true)
@Transactional

@Listeners(CaptureScreenshotOnFailureListener.class)

public class ConfigureProgramTemplate extends TestCaseHelper {

  @BeforeMethod(groups = {"admin"})
  public void setUp() throws Exception {
    super.setup();
  }

  @Test(groups = {"admin"}, dataProvider = "Data-Provider-Program-Not-Configured")
  public void testVerifyProgramNotConfigured(String program, String userSIC, String password) throws Exception {
    List<String> rightsList = new ArrayList<String>();
    rightsList.add("CREATE_REQUISITION");
    rightsList.add("VIEW_REQUISITION");
    setupTestDataToInitiateRnR(false, program, userSIC, "200", "openLmis", rightsList);

    LoginPage loginPage = new LoginPage(testWebDriver, baseUrlGlobal);
    HomePage homePage = loginPage.loginAs(userSIC, password);
    homePage.navigateAndInitiateRnr(program);
    InitiateRnRPage initiateRnRPage = homePage.clickProceed();
    initiateRnRPage.verifyTemplateNotConfiguredMessage();

  }

  @Test(groups = {"admin"}, dataProvider = "Data-Provider-Verify-On-Rnr-Screen")
  public void testVerifyImpactOfChangesInConfigScreenOnRnRScreen(String program, String userSIC, String password, String[] credentials) throws Exception {
    List<String> rightsList = new ArrayList<String>();
    rightsList.add("CREATE_REQUISITION");
    rightsList.add("VIEW_REQUISITION");
    setupTestDataToInitiateRnR(true, program, userSIC, "200", "openLmis", rightsList);

    LoginPage loginPage = new LoginPage(testWebDriver, baseUrlGlobal);
    HomePage homePage = loginPage.loginAs(credentials[0], credentials[1]);
    TemplateConfigPage templateConfigPage = homePage.selectProgramToConfigTemplate(program);
    String newColumnHeading = "Altered";
      templateConfigPage.unClickExpirationDate();
      templateConfigPage.unClickTotal();
    templateConfigPage.alterBeginningBalanceLabel(newColumnHeading);



    homePage.logout(baseUrlGlobal);
    LoginPage loginPageSic = new LoginPage(testWebDriver, baseUrlGlobal);
    HomePage homePageSic = loginPageSic.loginAs(userSIC, password);
    homePageSic.navigateAndInitiateRnr(program);
    InitiateRnRPage initiateRnRPage = homePage.clickProceed();
    String tableXpathTillTr = "//table[@id='fullSupplyTable']/thead/tr";
//    int columns = initiateRnRPage.getSizeOfElements(tableXpathTillTr + "/th");
//    initiateRnRPage.verifyColumnsHeadingPresent(tableXpathTillTr, newColumnHeading, columns);
    int columns = initiateRnRPage.getSizeOfElements(tableXpathTillTr + "/th");
      initiateRnRPage.verifyColumnHeadingNotPresent(tableXpathTillTr, "Expiration Date", columns);
      initiateRnRPage.verifyColumnHeadingNotPresent(tableXpathTillTr, "Total", columns);
  }

  @Test(groups = {"admin"}, dataProvider = "Data-Provider-Column-Label-Source")
  public void testVerifyColumnLabelsSourceAndMandatoryColumns(String program, String[] credentials) throws Exception {
    LoginPage loginPage = new LoginPage(testWebDriver, baseUrlGlobal);
    HomePage homePage = loginPage.loginAs(credentials[0], credentials[1]);

    TemplateConfigPage templateConfigPage = homePage.selectProgramToConfigTemplate(program);
    templateConfigPage.verifyMandatoryColumns();
    templateConfigPage.verifyColumnLabels();
    templateConfigPage.verifyColumnSource();

  }

  @Test(groups = {"admin"}, dataProvider = "Data-Provider-Column-Label-Source")
  public void testVerifyArithmeticValidationAndBusinessRules(String program, String[] credentials) throws Exception {
    LoginPage loginPage = new LoginPage(testWebDriver, baseUrlGlobal);
    HomePage homePage = loginPage.loginAs(credentials[0], credentials[1]);

    TemplateConfigPage templateConfigPage = homePage.selectProgramToConfigTemplate(program);
    templateConfigPage.verifyArithmeticValidations(program);
    templateConfigPage.verifyBusinessRules();


  }

  @AfterMethod(groups = {"admin"})
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
      {"HIV", "storeincharge", "Admin123"}
    };

  }

  @DataProvider(name = "Data-Provider-Verify-On-Rnr-Screen")
  public Object[][] parameterVerifyRnRScreen() {
    return new Object[][]{
      {"HIV", "storeincharge", "Admin123", new String[]{"Admin123", "Admin123"}}
    };

  }
}

