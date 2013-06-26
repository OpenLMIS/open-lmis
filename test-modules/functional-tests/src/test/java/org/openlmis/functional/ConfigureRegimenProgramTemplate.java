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

import static com.thoughtworks.selenium.SeleneseTestBase.assertEquals;
import static com.thoughtworks.selenium.SeleneseTestBase.assertTrue;

@TransactionConfiguration(defaultRollback = true)
@Transactional

@Listeners(CaptureScreenshotOnFailureListener.class)

public class ConfigureRegimenProgramTemplate extends TestCaseHelper {

  private static String adultsRegimen = "Adults";
  private static String paediatricsRegimen = "Paediatrics";
  private static String duplicateErrorMessageSave = "Cannot add duplicate regimen for same program";
  private static String duplicateErrorMessageAdd = "";

  @BeforeMethod(groups = {"functional2", "smoke"})
  public void setUp() throws Exception {
    super.setup();
  }


  @Test(groups = {"smoke"}, dataProvider = "Data-Provider")
  public void testVerifyNewRegimenCreated(String program, String[] credentials) throws Exception {
    dbWrapper.setRegimenTemplateConfiguredForAllPrograms(false);
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

  @Test(groups = {"functional2"}, dataProvider = "Data-Provider")
  public void testVerifyMultipleCategoriesAddition(String program, String[] credentials) throws Exception {
    dbWrapper.setRegimenTemplateConfiguredForAllPrograms(false);
    LoginPage loginPage = new LoginPage(testWebDriver, baseUrlGlobal);
    HomePage homePage = loginPage.loginAs(credentials[0], credentials[1]);
    RegimenTemplateConfigPage regimenTemplateConfigPage = homePage.navigateToRegimenConfigTemplate();
    regimenTemplateConfigPage.configureProgram(program);
    regimenTemplateConfigPage.AddNewRegimen(adultsRegimen, "Code1", "Name1", true);
    regimenTemplateConfigPage.AddNewRegimen(paediatricsRegimen, "Code2", "Name1", true);
    regimenTemplateConfigPage.SaveRegime();
    verifySuccessMessage(regimenTemplateConfigPage);
    verifyProgramConfigured(program);
  }

  @Test(groups = {"functional2"}, dataProvider = "Data-Provider")
  public void testVerifyDuplicateCategoriesInterCategory(String program, String[] credentials) throws Exception {
    dbWrapper.setRegimenTemplateConfiguredForAllPrograms(false);
    LoginPage loginPage = new LoginPage(testWebDriver, baseUrlGlobal);
    HomePage homePage = loginPage.loginAs(credentials[0], credentials[1]);
    RegimenTemplateConfigPage regimenTemplateConfigPage = homePage.navigateToRegimenConfigTemplate();
    regimenTemplateConfigPage.configureProgram(program);
    regimenTemplateConfigPage.AddNewRegimen(adultsRegimen, "Code1", "Name1", true);
    regimenTemplateConfigPage.AddNewRegimen(paediatricsRegimen, "Code1", "Name2", true);
    verifyErrorMessage(regimenTemplateConfigPage, duplicateErrorMessageSave);
  }


  @Test(groups = {"functional2"}, dataProvider = "Data-Provider")
  public void testVerifyDuplicateCategoriesAdditionForSameCategory(String program, String[] credentials) throws Exception {
    dbWrapper.setRegimenTemplateConfiguredForAllPrograms(false);
    LoginPage loginPage = new LoginPage(testWebDriver, baseUrlGlobal);
    HomePage homePage = loginPage.loginAs(credentials[0], credentials[1]);
    RegimenTemplateConfigPage regimenTemplateConfigPage = homePage.navigateToRegimenConfigTemplate();
    regimenTemplateConfigPage.configureProgram(program);
    regimenTemplateConfigPage.AddNewRegimen(adultsRegimen, "Code1", "Name1", true);
    regimenTemplateConfigPage.AddNewRegimen(adultsRegimen, "Code1", "Name2", true);
    verifyErrorMessage(regimenTemplateConfigPage, duplicateErrorMessageSave);
  }

  @Test(groups = {"functional2"}, dataProvider = "Data-Provider-Multiple-Programs")
  public void testVerifyDuplicateCategoriesInterPrograms(String program1, String program2, String[] credentials) throws Exception {
    dbWrapper.setRegimenTemplateConfiguredForAllPrograms(false);
    LoginPage loginPage = new LoginPage(testWebDriver, baseUrlGlobal);
    HomePage homePage = loginPage.loginAs(credentials[0], credentials[1]);
    RegimenTemplateConfigPage regimenTemplateConfigPage = homePage.navigateToRegimenConfigTemplate();
    regimenTemplateConfigPage.configureProgram(program1);
    regimenTemplateConfigPage.AddNewRegimen(adultsRegimen, "Code1", "Name1", true);
    regimenTemplateConfigPage.SaveRegime();
    verifySuccessMessage(regimenTemplateConfigPage);
    verifyProgramConfigured(program1);

    regimenTemplateConfigPage.configureProgram(program2);
    regimenTemplateConfigPage.AddNewRegimen(adultsRegimen, "Code1", "Name1", true);
    regimenTemplateConfigPage.SaveRegime();
    verifySuccessMessage(regimenTemplateConfigPage);
    verifyProgramConfigured(program2);
  }

  @Test(groups = {"functional2"}, dataProvider = "Data-Provider")
  public void testVerifyEditCategory(String program, String[] credentials) throws Exception {
    dbWrapper.setRegimenTemplateConfiguredForAllPrograms(false);
    LoginPage loginPage = new LoginPage(testWebDriver, baseUrlGlobal);
    HomePage homePage = loginPage.loginAs(credentials[0], credentials[1]);
    RegimenTemplateConfigPage regimenTemplateConfigPage = homePage.navigateToRegimenConfigTemplate();
    regimenTemplateConfigPage.configureProgram(program);
    regimenTemplateConfigPage.AddNewRegimen(adultsRegimen, "Code1", "Name1", true);
    regimenTemplateConfigPage.SaveRegime();
    regimenTemplateConfigPage.clickEditProgram(program);
    verifyNonEditableRegimentsAdded("Code1", "Name1", 1);
    regimenTemplateConfigPage.clickEditButton();
    enterCategoriesValuesForEditing("Code2", "Name2", 1);
    regimenTemplateConfigPage.clickDoneButton();
    verifyNonEditableRegimentsAdded("Code2", "Name2", 1);
    regimenTemplateConfigPage.SaveRegime();
    verifySuccessMessage(regimenTemplateConfigPage);
    regimenTemplateConfigPage.clickEditProgram(program);
    verifyNonEditableRegimentsAdded("Code2", "Name2", 1);
    regimenTemplateConfigPage.SaveRegime();
    verifySuccessMessage(regimenTemplateConfigPage);
    verifyProgramConfigured(program);
  }

  private void verifyErrorMessage(RegimenTemplateConfigPage regimenTemplateConfigPage, String expectedErrorMessage) {
    testWebDriver.waitForElementToAppear(regimenTemplateConfigPage.getSaveErrorMsgDiv());
    assertEquals(expectedErrorMessage, regimenTemplateConfigPage.getSaveErrorMsgDiv().getText().trim());
  }

  private void verifyNonEditableRegimentsAdded(String code, String name, int indexOfCodeAdded) {
    testWebDriver.waitForElementToAppear(testWebDriver.getElementByXpath("//div[@id='sortable']/div[" + indexOfCodeAdded + "]/div[2]/div/span"));
    assertEquals(code, testWebDriver.getElementByXpath("//div[@id='sortable']/div[" + indexOfCodeAdded + "]/div[2]/div/span").getText().trim());
    assertEquals(name, testWebDriver.getElementByXpath("//div[@id='sortable']/div[" + indexOfCodeAdded + "]/div[3]/div/span").getText().trim());
  }

  private void verifyEditableRegimentsAdded(String code, String name, int indexOfCodeAdded) {
    testWebDriver.waitForElementToAppear(testWebDriver.getElementByXpath("//div[@id='sortable']/div[" + indexOfCodeAdded + "]/div[2]/input"));
    assertEquals(code, testWebDriver.getAttribute(testWebDriver.getElementByXpath("//div[@id='sortable']/div[" + indexOfCodeAdded + "]/div[2]/input"), "value").trim());
    assertEquals(name, testWebDriver.getAttribute(testWebDriver.getElementByXpath("//div[@id='sortable']/div[" + indexOfCodeAdded + "]/div[3]/input"), "value").trim());
  }

  private void enterCategoriesValuesForEditing(String code, String name, int indexOfCodeAdded) {
    testWebDriver.waitForElementToAppear(testWebDriver.getElementByXpath("//div[@id='sortable']/div[" + indexOfCodeAdded + "]/div[2]/input"));
    testWebDriver.getElementByXpath("//div[@id='sortable']/div[" + indexOfCodeAdded + "]/div[2]/input").clear();
    testWebDriver.getElementByXpath("//div[@id='sortable']/div[" + indexOfCodeAdded + "]/div[2]/input").sendKeys(code);

    testWebDriver.getElementByXpath("//div[@id='sortable']/div[" + indexOfCodeAdded + "]/div[3]/input").clear();
    testWebDriver.getElementByXpath("//div[@id='sortable']/div[" + indexOfCodeAdded + "]/div[3]/input").sendKeys(name);
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
    assertTrue("Program " + program + "should be configured", testWebDriver.getElementByXpath("//a[@id='" + program + "']").getText().trim().equals("Edit"));

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

  @DataProvider(name = "Data-Provider-Multiple-Programs")
  public Object[][] parameterMultiplePrograms() {
    return new Object[][]{
      {"ESSENTIAL MEDICINES", "TB", new String[]{"Admin123", "Admin123"}}
    };

  }
}

