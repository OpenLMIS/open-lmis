/*
 * Copyright © 2013 VillageReach.  All Rights Reserved.  This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 *
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package org.openlmis.functional;


import com.thoughtworks.selenium.SeleneseTestNgHelper;
import org.openlmis.UiUtils.CaptureScreenshotOnFailureListener;
import org.openlmis.UiUtils.TestCaseHelper;
import org.openlmis.pageobjects.*;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;
import org.testng.annotations.*;

import java.util.ArrayList;
import java.util.List;

import static com.thoughtworks.selenium.SeleneseTestBase.assertTrue;

@TransactionConfiguration(defaultRollback = true)
@Transactional

@Listeners(CaptureScreenshotOnFailureListener.class)

public class ConfigureRegimenProgramTemplate extends TestCaseHelper {

  private static String adultsRegimen = "Adults";
  private static String paediatricsRegimen = "Paediatrics";

  @BeforeMethod(groups = {"functional2", "smoke"})
  public void setUp() throws Exception {
    super.setup();
  }


  @Test(groups = {"smoke"}, dataProvider = "Data-Provider")
  public void testVerifyNewRegimenCreated(String program, String[] credentials) throws Exception {
    dbWrapper.setRegimenTemplateConfiguredForProgram(false, program);
    String expectedProgramsString = dbWrapper.getAllActivePrograms();
    LoginPage loginPage = new LoginPage(testWebDriver, baseUrlGlobal);
    HomePage homePage = loginPage.loginAs(credentials[0], credentials[1]);
    RegimenTemplateConfigPage regimenTemplateConfigPage = homePage.navigateToRegimenConfigTemplate();
    List<String> programsList = getProgramsListedOnRegimeScreen();
    verifyProgramsListedOnManageRegimenTemplateScreen(programsList, expectedProgramsString);
    regimenTemplateConfigPage.configureProgram(program);
    regimenTemplateConfigPage.AddNewRegimen(adultsRegimen, "Code1", "Name1", true);
    regimenTemplateConfigPage.SaveRegime();
    verifySuccessMessage(regimenTemplateConfigPage);
    verifyProgramConfigured(program);
  }

  private void verifyProgramsListedOnManageRegimenTemplateScreen(List<String> actualProgramsString, String expectedProgramsString) {
    for (String program : actualProgramsString)
      SeleneseTestNgHelper.assertTrue("Program " + program + " not present in expected string : " + expectedProgramsString, expectedProgramsString.contains(program));

  }

  private List<String> getProgramsListedOnRegimeScreen() {
    List<String> programsList = new ArrayList<String>();
    String regimenTableTillTR = "//table[@id='configureProgramRegimensTable']/tbody/tr";
    int size = testWebDriver.getElementsSizeByXpath(regimenTableTillTR);
    for (int counter = 1; counter < size + 1; counter++) {
      testWebDriver.waitForElementToAppear(testWebDriver.getElementByXpath(regimenTableTillTR + "[" + counter + "]/td[1]"));
      programsList.add(testWebDriver.getElementByXpath(regimenTableTillTR + "[" + counter + "]/td[1]").getText().trim());
    }
    return programsList;
  }

  private void verifySuccessMessage(RegimenTemplateConfigPage regimenTemplateConfigPage) {
    testWebDriver.waitForElementToAppear(regimenTemplateConfigPage.getSaveSuccessMsgDiv());
    assertTrue("saveSuccessMsgDiv should show up", regimenTemplateConfigPage.getSaveSuccessMsgDiv().isDisplayed());
    String saveSuccessfullyMessage = "Regimens saved successfully";
    assertTrue("Message showing '" + saveSuccessfullyMessage + "' should show up", regimenTemplateConfigPage.getSaveSuccessMsgDiv().getText().trim().equals(saveSuccessfullyMessage));

  }

  private void verifyProgramConfigured(String program) {
    testWebDriver.waitForElementToAppear(testWebDriver.getElementByXpath("//a[@id='" + program + "']"));
    assertTrue("", testWebDriver.getElementByXpath("//a[@id='" + program + "']").getText().trim().equals("Edit"));

  }

  @AfterMethod(groups = {"smoke", "functional2"})
  public void tearDown() throws Exception {
    HomePage homePage = new HomePage(testWebDriver);
    homePage.logout(baseUrlGlobal);
    dbWrapper.deleteData();
    dbWrapper.closeConnection();
  }

  @DataProvider(name = "Data-Provider")
  public Object[][] parameterVerifyRnRScreen() {
    return new Object[][]{
      {"ESSENTIAL MEDICINES", new String[]{"Admin123", "Admin123"}}
    };

  }
}

