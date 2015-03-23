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


import cucumber.api.DataTable;
import cucumber.api.java.en.And;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import org.openlmis.UiUtils.CaptureScreenshotOnFailureListener;
import org.openlmis.UiUtils.TestCaseHelper;
import org.openlmis.pageobjects.*;
import org.testng.annotations.*;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static com.thoughtworks.selenium.SeleneseTestBase.assertEquals;
import static com.thoughtworks.selenium.SeleneseTestBase.assertTrue;
import static java.util.Arrays.asList;

@Listeners(CaptureScreenshotOnFailureListener.class)

public class ConfigureRegimenProgramTemplate extends TestCaseHelper {

  private static String adultsRegimen = "Adults";
  private static String paediatricsRegimen = "Paediatrics";
  private static String duplicateErrorMessageSave = "Cannot add duplicate regimen code for same program";
  private static String CODE1 = "Code1";
  private static String CODE2 = "Code2";
  private static String NAME1 = "Name1";
  private static String NAME2 = "Name2";

  public String expectedProgramsString;
  RegimenTemplateConfigPage regimenTemplateConfigPage;
  LoginPage loginPage;

  @BeforeMethod(groups = "admin")
  public void setUp() throws InterruptedException, SQLException, IOException {
    super.setup();
    dbWrapper.removeAllExistingRights("Admin");
    dbWrapper.assignRight("Admin", "MANAGE_REGIMEN_TEMPLATE");
    regimenTemplateConfigPage = PageObjectFactory.getRegimenTemplateConfigPage(testWebDriver);
    loginPage = PageObjectFactory.getLoginPage(testWebDriver, baseUrlGlobal);
  }

  @Given("^I have data available for programs configured$")
  public void setupDataForRegimenTemplateConfiguration() throws SQLException {
    dbWrapper.updateFieldValue("programs", "regimenTemplateConfigured", "false", null, null);
    expectedProgramsString = dbWrapper.getAllActivePrograms();
  }

  @When("^I access regimen configuration page$")
  public void navigatesToRegimenConfigurationPage() {
    HomePage homePage = PageObjectFactory.getHomePage(testWebDriver);
    homePage.navigateToRegimenConfigTemplate();
  }

  @Then("^I should see configured program list$")
  public void verifyProgramsListedOnManageRegimenTemplate() throws SQLException {
    List<String> programsList = getProgramsListedOnRegimeScreen();
    expectedProgramsString = dbWrapper.getAllActivePrograms();
    verifyProgramsListedOnManageRegimenTemplateScreen(programsList, expectedProgramsString);
  }

  @When("^I configure program \"([^\"]*)\" for regimen template$")
  public void createProgramForRegimenTemplate(String program) {
    regimenTemplateConfigPage = PageObjectFactory.getRegimenTemplateConfigPage(testWebDriver);
    regimenTemplateConfigPage.configureProgram(program);
  }

  @When("^I edit program \"([^\"]*)\" for regimen template$")
  public void editProgramForRegimenTemplate(String program) throws InterruptedException {
    regimenTemplateConfigPage = PageObjectFactory.getRegimenTemplateConfigPage(testWebDriver);
    regimenTemplateConfigPage.clickEditProgram(program);
  }

  @When("^I add new regimen:$")
  public void addRegimen(DataTable regimenTable) {
    List<Map<String, String>> data = regimenTable.asMaps(String.class, String.class);
    regimenTemplateConfigPage = PageObjectFactory.getRegimenTemplateConfigPage(testWebDriver);
    for (Map map : data)
      regimenTemplateConfigPage.addNewRegimen(map.get("Category").toString(), map.get("Code").toString(),
        map.get("Name").toString(), Boolean.parseBoolean(map.get("Active").toString()));
  }

  @And("^I save regimen$")
  public void saveRegimen() {
    regimenTemplateConfigPage = PageObjectFactory.getRegimenTemplateConfigPage(testWebDriver);
    regimenTemplateConfigPage.SaveRegime();
  }

  @Then("^I should see regimen created message$")
  public void verifyRegimenSuccessMessage() {
    verifySuccessMessage();
  }

  @And("^I access regimen reporting fields tab$")
  public void accessRegimenReportingField() {
    regimenTemplateConfigPage = PageObjectFactory.getRegimenTemplateConfigPage(testWebDriver);
    regimenTemplateConfigPage.clickReportingFieldTab();
  }

  @Then("^I should see regimen reporting fields$")
  public void verifyDefaultRegimenReportingFields() {
    verifyDefaultRegimenReportingFieldsValues();
  }

  @When("^I add new regimen reporting field:$")
  public void addRegimenReportingField(DataTable regimenReportingTable) {
    List<Map<String, String>> data = regimenReportingTable.asMaps(String.class, String.class);
    regimenTemplateConfigPage = PageObjectFactory.getRegimenTemplateConfigPage(testWebDriver);
    for (Map map : data) {
      regimenTemplateConfigPage.NoOfPatientsOnTreatmentCheckBox(
        Boolean.parseBoolean(map.get("NoOfPatientsOnTreatment").toString()));
      regimenTemplateConfigPage.setValueRemarksTextField(map.get("Remarks").toString());
    }
  }

  @Then("^I should see created regimen and reporting fields:$")
  public void verifyRegimenAndReportingFields(DataTable dataTable) {
    List<Map<String, String>> data = dataTable.asMaps(String.class, String.class);
    for (Map map : data)
      verifyProgramDetailsSaved(map.get("Code").toString(), map.get("Name").toString(), map.get("Remarks").toString());
  }

  @When("^I activate Number Of Patients On Treatment$")
  public void activeNoOfPatientsOnTreatment() {
    regimenTemplateConfigPage = PageObjectFactory.getRegimenTemplateConfigPage(testWebDriver);
    regimenTemplateConfigPage.NoOfPatientsOnTreatmentCheckBox(true);
  }


  @Test(groups = {"admin"}, dataProvider = "Data-Provider")
  public void testVerifyAtLeastOneColumnChecked(String program, String[] credentials) throws SQLException {

    dbWrapper.updateFieldValue("programs", "regimenTemplateConfigured", "false", null, null);
    HomePage homePage = loginPage.loginAs(credentials[0], credentials[1]);
    RegimenTemplateConfigPage regimenTemplateConfigPage = homePage.navigateToRegimenConfigTemplate();
    regimenTemplateConfigPage.configureProgram(program);
    regimenTemplateConfigPage.clickReportingFieldTab();
    verifyDefaultRegimenReportingFieldsValues();

    regimenTemplateConfigPage.NoOfPatientsOnTreatmentCheckBox(false);
    regimenTemplateConfigPage.NoOfPatientsStoppedTreatmentCheckBox(false);
    regimenTemplateConfigPage.NoOfPatientsToInitiateTreatmentCheckBox(false);
    regimenTemplateConfigPage.RemarksCheckBox(false);

    regimenTemplateConfigPage.SaveRegime();
    String oneShouldBeSelectedErrorMessage = "At least one column should be checked";
    regimenTemplateConfigPage.verifySaveErrorMessageDiv(oneShouldBeSelectedErrorMessage);
  }

  @Test(groups = {"admin"}, dataProvider = "Data-Provider-Function-Positive")
  public void testVerifyAlteredRegimensColumnsOnRnRScreen(String program, String adminUser, String userSIC, String password) throws SQLException {
    String newRemarksHeading = "Testing column";
    dbWrapper.updateFieldValue("programs", "regimenTemplateConfigured", "false", null, null);

    HomePage homePage = loginPage.loginAs(adminUser, adminUser);
    RegimenTemplateConfigPage regimenTemplateConfigPage = homePage.navigateToRegimenConfigTemplate();
    regimenTemplateConfigPage.configureProgram(program);
    regimenTemplateConfigPage.addNewRegimen(adultsRegimen, CODE1, NAME1, false);
    regimenTemplateConfigPage.clickReportingFieldTab();
    regimenTemplateConfigPage.setValueRemarksTextField(newRemarksHeading);
    regimenTemplateConfigPage.SaveRegime();
    verifySuccessMessage();
    homePage.logout(baseUrlGlobal);
    setUpDataForInitiateRnR(program, userSIC);

    homePage = loginPage.loginAs(userSIC, password);
    homePage.navigateAndInitiateRnr(program);
    InitiateRnRPage initiateRnRPage = homePage.clickProceed();

    testWebDriver.sleep(2000);
    initiateRnRPage.clickRegimenTab();
    testWebDriver.sleep(500);
    String tableXpathTillTr = "//table[@id='regimenTable']/thead/tr";
    int columns = initiateRnRPage.getSizeOfElements(tableXpathTillTr + "/th");
    initiateRnRPage.verifyColumnsHeadingPresent(tableXpathTillTr, "Code", columns);
    initiateRnRPage.verifyColumnsHeadingPresent(tableXpathTillTr, "Name", columns);
    initiateRnRPage.verifyColumnsHeadingPresent(tableXpathTillTr, "Number of patients on treatment", columns);
    initiateRnRPage.verifyColumnsHeadingPresent(tableXpathTillTr, "Number of patients to be initiated treatment",
      columns);
    initiateRnRPage.verifyColumnsHeadingPresent(tableXpathTillTr, "Number of patients stopped treatment", columns);
    initiateRnRPage.verifyColumnsHeadingPresent(tableXpathTillTr, newRemarksHeading, columns);
  }

  @Test(groups = {"admin"}, dataProvider = "Data-Provider")
  public void testVerifyMultipleCategoriesAddition(String program, String[] credentials) throws SQLException {
    dbWrapper.updateFieldValue("programs", "regimenTemplateConfigured", "false", null, null);
    HomePage homePage = loginPage.loginAs(credentials[0], credentials[1]);
    RegimenTemplateConfigPage regimenTemplateConfigPage = homePage.navigateToRegimenConfigTemplate();

    regimenTemplateConfigPage.configureProgram(program);
    regimenTemplateConfigPage.addNewRegimen(adultsRegimen, CODE1, NAME1, true);
    regimenTemplateConfigPage.addNewRegimen(paediatricsRegimen, CODE2, NAME1, true);
    regimenTemplateConfigPage.SaveRegime();
    verifySuccessMessage();
    verifyProgramConfigured(program);
  }

  @Test(groups = {"admin"}, dataProvider = "Data-Provider")
  public void testVerifyDuplicateCategoriesInterCategory(String program, String[] credentials) throws SQLException {
    dbWrapper.updateFieldValue("programs", "regimenTemplateConfigured", "false", null, null);
    HomePage homePage = loginPage.loginAs(credentials[0], credentials[1]);
    RegimenTemplateConfigPage regimenTemplateConfigPage = homePage.navigateToRegimenConfigTemplate();

    regimenTemplateConfigPage.configureProgram(program);
    regimenTemplateConfigPage.addNewRegimen(adultsRegimen, CODE1, NAME1, true);
    regimenTemplateConfigPage.addNewRegimen(paediatricsRegimen, CODE1, NAME2, true);
    verifyErrorMessage(duplicateErrorMessageSave);
  }

  @Test(groups = {"admin"}, dataProvider = "Data-Provider")
  public void testVerifyDuplicateCategoriesAdditionForSameCategory(String program,
                                                                   String[] credentials) throws SQLException {
    dbWrapper.updateFieldValue("programs", "regimenTemplateConfigured", "false", null, null);
    HomePage homePage = loginPage.loginAs(credentials[0], credentials[1]);
    RegimenTemplateConfigPage regimenTemplateConfigPage = homePage.navigateToRegimenConfigTemplate();

    regimenTemplateConfigPage.configureProgram(program);
    regimenTemplateConfigPage.addNewRegimen(adultsRegimen, CODE1, NAME1, true);
    regimenTemplateConfigPage.addNewRegimen(adultsRegimen, CODE1, NAME2, true);
    verifyErrorMessage(duplicateErrorMessageSave);
  }

  @Test(groups = {"admin"}, dataProvider = "Data-Provider-Multiple-Programs")
  public void testVerifyDuplicateCategoriesInterPrograms(String program1, String program2, String[] credentials) throws SQLException {
    dbWrapper.updateFieldValue("programs", "regimenTemplateConfigured", "false", null, null);
    HomePage homePage = loginPage.loginAs(credentials[0], credentials[1]);
    RegimenTemplateConfigPage regimenTemplateConfigPage = homePage.navigateToRegimenConfigTemplate();

    regimenTemplateConfigPage.configureProgram(program1);
    regimenTemplateConfigPage.addNewRegimen(adultsRegimen, CODE1, NAME1, true);
    regimenTemplateConfigPage.SaveRegime();
    verifySuccessMessage();
    verifyProgramConfigured(program1);

    regimenTemplateConfigPage.configureProgram(program2);
    regimenTemplateConfigPage.addNewRegimen(adultsRegimen, CODE1, NAME1, true);
    regimenTemplateConfigPage.SaveRegime();
    verifySuccessMessage();
    verifyProgramConfigured(program2);
  }

  @Test(groups = {"admin"}, dataProvider = "Data-Provider")
  public void testVerifyEditCategory(String program, String[] credentials) throws SQLException, InterruptedException {
    dbWrapper.updateFieldValue("programs", "regimenTemplateConfigured", "false", null, null);
    HomePage homePage = loginPage.loginAs(credentials[0], credentials[1]);
    RegimenTemplateConfigPage regimenTemplateConfigPage = homePage.navigateToRegimenConfigTemplate();

    regimenTemplateConfigPage.configureProgram(program);
    regimenTemplateConfigPage.addNewRegimen(adultsRegimen, CODE1, NAME1, false);
    regimenTemplateConfigPage.SaveRegime();
    regimenTemplateConfigPage.clickEditProgram(program);
    verifyNonEditableRegimenAdded(CODE1, NAME1, true, 1);
    regimenTemplateConfigPage.clickEditButton();
    verifyEditableRegimenAdded(CODE1, NAME1, true, 1);
    enterCategoriesValuesForEditing(CODE2, NAME2, 1);
    regimenTemplateConfigPage.clickDoneButton();
    verifyNonEditableRegimenAdded(CODE2, NAME2, true, 1);
    regimenTemplateConfigPage.SaveRegime();
    verifySuccessMessage();
    regimenTemplateConfigPage.clickEditProgram(program);
    verifyNonEditableRegimenAdded(CODE2, NAME2, true, 1);
    regimenTemplateConfigPage.SaveRegime();
    verifySuccessMessage();
    verifyProgramConfigured(program);
  }

  @Test(groups = {"admin"}, dataProvider = "Data-Provider")
  public void testVerifyDuplicateCategoryOnDone(String program, String[] credentials) throws SQLException {
    dbWrapper.updateFieldValue("programs", "regimenTemplateConfigured", "false", null, null);
    HomePage homePage = loginPage.loginAs(credentials[0], credentials[1]);
    RegimenTemplateConfigPage regimenTemplateConfigPage = homePage.navigateToRegimenConfigTemplate();

    regimenTemplateConfigPage.configureProgram(program);
    regimenTemplateConfigPage.addNewRegimen(adultsRegimen, CODE1, NAME1, false);
    regimenTemplateConfigPage.addNewRegimen(adultsRegimen, CODE2, NAME1, true);
    verifyNonEditableRegimenAdded(CODE1, NAME1, true, 1);
    verifyNonEditableRegimenAdded(CODE2, NAME1, false, 2);
    regimenTemplateConfigPage.clickEditButton();
    enterCategoriesValuesForEditing(CODE2, NAME1, 1);
    regimenTemplateConfigPage.clickDoneButton();
    verifyErrorMessage(duplicateErrorMessageSave);
  }

  @Test(groups = {"admin"}, dataProvider = "Data-Provider")
  public void testVerifyCategoryErrorOnDone(String program, String[] credentials) throws SQLException {
    dbWrapper.updateFieldValue("programs", "regimenTemplateConfigured", "false", null, null);
    HomePage homePage = loginPage.loginAs(credentials[0], credentials[1]);
    RegimenTemplateConfigPage regimenTemplateConfigPage = homePage.navigateToRegimenConfigTemplate();

    regimenTemplateConfigPage.configureProgram(program);
    regimenTemplateConfigPage.addNewRegimen(adultsRegimen, CODE1, NAME1, true);
    regimenTemplateConfigPage.addNewRegimen(adultsRegimen, CODE2, NAME1, true);
    regimenTemplateConfigPage.clickEditButton();
    regimenTemplateConfigPage.clickSaveButton();
    String errorMessageONSaveBeforeDone = "Mark all regimens as 'Done' before saving the form";
    verifyErrorMessage(errorMessageONSaveBeforeDone);
    enterCategoriesValuesForEditing("", NAME1, 1);
    regimenTemplateConfigPage.clickDoneButton();
    verifyDoneErrorMessage();
  }

  @Test(groups = {"admin"}, dataProvider = "Data-Provider")
  public void testVerifyCancelButtonFunctionality(String program, String[] credentials) throws SQLException {
    dbWrapper.updateFieldValue("programs", "regimenTemplateConfigured", "false", null, null);
    HomePage homePage = loginPage.loginAs(credentials[0], credentials[1]);
    RegimenTemplateConfigPage regimenTemplateConfigPage = homePage.navigateToRegimenConfigTemplate();

    regimenTemplateConfigPage.configureProgram(program);
    regimenTemplateConfigPage.CancelRegime(program);
    assertTrue("Clicking Cancel button should be redirected to Regimen Template screen",
      testWebDriver.getElementById(program).isDisplayed());
  }

  private void verifyDefaultRegimenReportingFieldsValues() {
    assertTrue("noOfPatientsOnTreatmentCheckBox should be checked",
      regimenTemplateConfigPage.IsSelectedNoOfPatientsOnTreatmentCheckBox());
    assertTrue("noOfPatientsToInitiateTreatmentCheckBox should be checked",
      regimenTemplateConfigPage.IsNoOfPatientsToInitiateTreatmentCheckBoxSelected());
    assertTrue("noOfPatientsStoppedTreatmentCheckBox should be checked",
      regimenTemplateConfigPage.IsNoOfPatientsStoppedTreatmentCheckBoxSelected());
    assertTrue("remarksCheckBox should be checked", regimenTemplateConfigPage.IsRemarksCheckBoxSelected());

    assertEquals("Number of patients on treatment",
      regimenTemplateConfigPage.getValueNoOfPatientsOnTreatmentTextField());
    assertEquals("Number of patients to be initiated treatment",
      regimenTemplateConfigPage.getValueNoOfPatientsToInitiateTreatmentTextField());
    assertEquals("Number of patients stopped treatment",
      regimenTemplateConfigPage.getValueNoOfPatientsStoppedTreatmentTextField());
    assertEquals("Remarks", regimenTemplateConfigPage.getValueRemarksTextField());

    assertEquals("Numeric", regimenTemplateConfigPage.getTextNoOfPatientsOnTreatmentDataType());
    assertEquals("Numeric", regimenTemplateConfigPage.getTextNoOfPatientsStoppedTreatmentDataType());
    assertEquals("Numeric", regimenTemplateConfigPage.getTextNoOfPatientsToInitiateTreatmentDataType());
    assertEquals("Text", regimenTemplateConfigPage.getTextRemarksDataType());
  }

  private void verifyErrorMessage(String expectedErrorMessage) {
    regimenTemplateConfigPage.IsDisplayedSaveErrorMsgDiv();
    assertEquals(expectedErrorMessage, regimenTemplateConfigPage.getSaveErrorMsgDiv());
  }

  private void verifyDoneErrorMessage() {
    assertTrue("Done regimen Error div should show up", regimenTemplateConfigPage.IsDisplayedDoneFailMessage());
  }

  private void verifyNonEditableRegimenAdded(String code, String name, boolean activeCheckBoxSelected, int indexOfCodeAdded) {
    assertEquals(code, regimenTemplateConfigPage.getNonEditableAddedCode(indexOfCodeAdded));
    assertEquals(name, regimenTemplateConfigPage.getNonEditableAddedName(indexOfCodeAdded));
    assertEquals(activeCheckBoxSelected, regimenTemplateConfigPage.getNonEditableAddedActiveCheckBox(indexOfCodeAdded));
  }

  private void verifyEditableRegimenAdded(String code, String name, boolean activeCheckBoxSelected, int indexOfCodeAdded) {
    assertEquals(code, regimenTemplateConfigPage.getEditableAddedCode(indexOfCodeAdded));
    assertEquals(name, regimenTemplateConfigPage.getEditableAddedName(indexOfCodeAdded));
    assertEquals(activeCheckBoxSelected, regimenTemplateConfigPage.getEditableAddedActiveCheckBox(indexOfCodeAdded));
  }

  private void enterCategoriesValuesForEditing(String code, String name, int indexOfCodeAdded) {
    String baseRegimenDivXpath = "//div[@id='sortable']/div";
    testWebDriver.waitForElementToAppear(
      testWebDriver.getElementByXpath(baseRegimenDivXpath + "[" + indexOfCodeAdded + "]/div[2]/input"));
    sendKeys(baseRegimenDivXpath + "[" + indexOfCodeAdded + "]/div[2]/input", code);
    sendKeys(baseRegimenDivXpath + "[" + indexOfCodeAdded + "]/div[3]/input", name);
  }

  private void verifyProgramsListedOnManageRegimenTemplateScreen(List<String> actualProgramsString, String expectedProgramsString) {
    for (String program : actualProgramsString)
      assertTrue("Program " + program + " not present in expected string : " + expectedProgramsString,
        expectedProgramsString.contains(program));
  }

  private List<String> getProgramsListedOnRegimeScreen() {
    List<String> programsList = new ArrayList<>();
    String regimenTableTillTR = "//table[@id='configureProgramRegimensTable']/tbody/tr";
    int size = testWebDriver.getElementsSizeByXpath(regimenTableTillTR);
    for (int counter = 1; counter < size + 1; counter++) {
      testWebDriver.waitForElementToAppear(
        testWebDriver.getElementByXpath(regimenTableTillTR + "[" + counter + "]/td[1]"));
      programsList.add(
        testWebDriver.getElementByXpath(regimenTableTillTR + "[" + counter + "]/td[1]").getText().trim());
    }
    return programsList;
  }

  private void verifySuccessMessage() {
    testWebDriver.waitForAjax();
    assertTrue("saveSuccessMsgDiv should show up", regimenTemplateConfigPage.isDisplayedSaveSuccessMsgDiv());
    String saveSuccessfullyMessage = "Regimens saved successfully";
    assertEquals(saveSuccessfullyMessage, regimenTemplateConfigPage.getSaveSuccessMsgDiv());
  }

  private void verifyProgramConfigured(String program) {
    testWebDriver.waitForElementToAppear(testWebDriver.getElementById(program));
    assertTrue("Program " + program + "should be configured",
      testWebDriver.getElementById(program).getText().trim().equals("Edit"));
  }

  private void verifyProgramDetailsSaved(String code, String name, String reportingField) {
    verifyNonEditableRegimenAdded(code, name, false, 1);
    regimenTemplateConfigPage.clickReportingFieldTab();
    assertEquals(reportingField, regimenTemplateConfigPage.getValueRemarksTextField());
  }

  private void setUpDataForInitiateRnR(String program, String userSIC) throws SQLException {
    dbWrapper.setupMultipleProducts(program, "Lvl3 Hospital", 2, false);
    dbWrapper.insertFacilities("F10", "F11");
    dbWrapper.configureTemplate(program);
    List<String> rightsList = asList("CREATE_REQUISITION", "VIEW_REQUISITION");
    setupTestUserRoleRightsData(userSIC, rightsList);
    dbWrapper.deleteSupervisoryNodes();
    dbWrapper.insertSupervisoryNode("F10", "N1", "Node 1", "null");
    dbWrapper.insertRoleAssignment(userSIC, "store in-charge");
    dbWrapper.insertSchedule("Q1stM", "QuarterMonthly", "QuarterMonth");
    dbWrapper.insertSchedule("M", "Monthly", "Month");
    dbWrapper.insertProcessingPeriod("Period1", "first period", "2012-12-01", "2013-01-15", 1, "Q1stM");
    dbWrapper.insertProcessingPeriod("Period2", "second period", "2013-01-16", "2013-01-30", 1, "M");
    setupRequisitionGroupData("RG1", "RG2", "N1", "N2", "F10", "F11");
    dbWrapper.insertSupplyLines("N1", program, "F10", true);
  }

  @AfterMethod(groups = "admin")
  public void tearDown() throws SQLException {
    testWebDriver.sleep(500);
    dbWrapper.removeAllExistingRights("Admin");
    dbWrapper.insertAllAdminRightsAsSeedData();
    if (!testWebDriver.getElementById("username").isDisplayed()) {
      HomePage homePage = PageObjectFactory.getHomePage(testWebDriver);
      homePage.logout(baseUrlGlobal);
      dbWrapper.deleteData();
      dbWrapper.closeConnection();
    }
  }

  @DataProvider(name = "Data-Provider")
  public Object[][] parameterVerifyRnRScreen() {
    return new Object[][]{{"ESSENTIAL MEDICINES", new String[]{"Admin123", "Admin123"}}};
  }

  @DataProvider(name = "Data-Provider-Multiple-Programs")
  public Object[][] parameterMultiplePrograms() {
    return new Object[][]{{"ESSENTIAL MEDICINES", "TB", new String[]{"Admin123", "Admin123"}}};
  }

  @DataProvider(name = "Data-Provider-Function-Positive")
  public Object[][] parameterIntTestProviderPositive() {
    return new Object[][]{{"HIV", "Admin123", "storeInCharge", "Admin123"}};
  }
}

