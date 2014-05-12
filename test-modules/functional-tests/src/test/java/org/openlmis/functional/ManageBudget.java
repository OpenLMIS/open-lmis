package org.openlmis.functional;


import org.openlmis.UiUtils.TestCaseHelper;
import org.openlmis.pageobjects.*;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

import static java.util.Arrays.asList;

public class ManageBudget extends TestCaseHelper {
  public static final String APPROVE_REQUISITION = "APPROVE_REQUISITION";
  public static final String CREATE_REQUISITION = "CREATE_REQUISITION";
  public static final String AUTHORIZED = "AUTHORIZED";
  public static final String AUTHORIZE_REQUISITION = "AUTHORIZE_REQUISITION";
  public static final String VIEW_REQUISITION = "VIEW_REQUISITION";
  public static final String program = "HIV";
  public static final String userSIC = "storeInCharge";
  public static final String password = "Admin123";

  public LoginPage loginPage;
  InitiateRnRPage initiateRnRPage;


  @BeforeMethod(groups = "requisition")
  public void setUp() throws SQLException, IOException, InterruptedException {
    super.setup();
    dbWrapper.deleteData();
    setUpData(program, userSIC);
    dbWrapper.deleteTable("processing_periods");
    dbWrapper.insertCurrentPeriod("current Period", "current Period", 1, "M");
    loginPage = PageObjectFactory.getLoginPage(testWebDriver, baseUrlGlobal);
    initiateRnRPage = PageObjectFactory.getInitiateRnRPage(testWebDriver);
  }

  @Test(groups = {"requisition"})
  public void testVerifyBudgetWhenRegularRnRIsCreatedAndBudgetFlagIsTrueAndContainsBudgetInformation() throws SQLException {
    dbWrapper.updateFieldValue("programs", "budgetingApplies", "true", "name", program);
    dbWrapper.insertBudgetData();

    HomePage homePage = loginPage.loginAs(userSIC, password);

    homePage.navigateHomePage();
    homePage.navigateInitiateRnRScreenAndSelectingRequiredFields(program, "Regular");
    homePage.clickProceed();

    enterDetailsInRnRForFirstProduct(100, 50, 50);

    initiateRnRPage.verifyBudgetAmountPresentOnFooter("$200.00");
    initiateRnRPage.checkWhetherBudgetExceedWarningPresent(false);
    initiateRnRPage.saveRnR();

    enterDetailsInRnRForFirstProduct(100, 100, 200);
    initiateRnRPage.clickSubmitButton();
    initiateRnRPage.clickOk();
    initiateRnRPage.verifyBudgetAmountPresentOnFooter("$200.00");
    initiateRnRPage.checkWhetherBudgetExceedWarningPresent(true);

    initiateRnRPage.enterValueIfNotNull(9, "requestedQuantityFirstProduct");
    initiateRnRPage.enterExplanationReason();
    initiateRnRPage.clickNonFullSupplyTab();
    initiateRnRPage.verifyBudgetAmountPresentOnFooter("$200.00");
    initiateRnRPage.checkWhetherBudgetExceedWarningPresent(false);

    initiateRnRPage.authorizeRnR();
    initiateRnRPage.clickOk();

    ApprovePage approvePage = homePage.navigateToApprove();
    approvePage.clickRequisitionPresentForApproval();
    approvePage.editFullSupplyApproveQuantity("200");
    initiateRnRPage.verifyBudgetAmountPresentOnFooter("$200.00");
    initiateRnRPage.checkWhetherBudgetExceedWarningPresent(true);

    approvePage.clickApproveButton();
    approvePage.clickOk();

    viewRequisition();
    initiateRnRPage.verifyBudgetAmountPresentOnFooter("$200.00");
    initiateRnRPage.checkWhetherBudgetExceedWarningPresent(true);
  }

  @Test(groups = {"requisition"})
  public void testVerifyBudgetWhenRegularRnRIsCreatedAndBudgetFlagIsTrueAndDoNotContainsBudgetInformation() throws SQLException {
    dbWrapper.updateFieldValue("programs", "budgetingApplies", "true", "name", program);

    HomePage homePage = loginPage.loginAs(userSIC, password);
    homePage.navigateHomePage();
    homePage.navigateInitiateRnRScreenAndSelectingRequiredFields(program, "Regular");
    homePage.clickProceed();

    enterDetailsInRnRForFirstProduct(0, 70, 60);

    initiateRnRPage.verifyBudgetAmountNotAllocated();
    initiateRnRPage.checkWhetherBudgetExceedWarningPresent(false);

    initiateRnRPage.clickSubmitButton();
    initiateRnRPage.clickOk();

    initiateRnRPage.verifyBudgetAmountNotAllocated();
    initiateRnRPage.checkWhetherBudgetExceedWarningPresent(false);

    initiateRnRPage.clickNonFullSupplyTab();
    initiateRnRPage.verifyBudgetAmountNotAllocated();
    initiateRnRPage.checkWhetherBudgetExceedWarningPresent(false);

    initiateRnRPage.authorizeRnR();
    initiateRnRPage.clickOk();

    ApprovePage approvePage = homePage.navigateToApprove();
    approvePage.clickRequisitionPresentForApproval();
    initiateRnRPage.verifyBudgetAmountNotAllocated();
    initiateRnRPage.checkWhetherBudgetExceedWarningPresent(false);

    approvePage.clickApproveButton();
    approvePage.clickOk();

    viewRequisition();
    initiateRnRPage.verifyBudgetAmountNotAllocated();
    initiateRnRPage.checkWhetherBudgetExceedWarningPresent(false);
  }

  @Test(groups = {"requisition"})
  public void testVerifyBudgetWhenRegularRnRIsCreatedAndBudgetFlagIsFalseAndBudgetFilePresent() throws SQLException {
    dbWrapper.updateFieldValue("programs", "budgetingApplies", "false", "name", program);
    dbWrapper.insertBudgetData();

    HomePage homePage = loginPage.loginAs(userSIC, password);
    homePage.navigateHomePage();

    homePage.navigateInitiateRnRScreenAndSelectingRequiredFields(program, "Regular");
    homePage.clickProceed();

    enterDetailsInRnRForFirstProduct(0, 100, 0);
    initiateRnRPage.enterValueIfNotNull(90, "newPatientFirstProduct");

    initiateRnRPage.verifyBudgetNotDisplayed();
    initiateRnRPage.checkWhetherBudgetExceedWarningPresent(false);

    initiateRnRPage.clickSubmitButton();
    initiateRnRPage.clickOk();
    initiateRnRPage.verifyBudgetNotDisplayed();
    initiateRnRPage.checkWhetherBudgetExceedWarningPresent(false);

    initiateRnRPage.clickNonFullSupplyTab();
    initiateRnRPage.verifyBudgetNotDisplayed();
    initiateRnRPage.checkWhetherBudgetExceedWarningPresent(false);

    initiateRnRPage.authorizeRnR();
    initiateRnRPage.clickOk();

    ApprovePage approvePage = homePage.navigateToApprove();
    approvePage.clickRequisitionPresentForApproval();
    initiateRnRPage.verifyBudgetNotDisplayed();
    initiateRnRPage.checkWhetherBudgetExceedWarningPresent(false);

    approvePage.clickApproveButton();
    approvePage.clickOk();

    viewRequisition();
    initiateRnRPage.verifyBudgetNotDisplayed();
    initiateRnRPage.checkWhetherBudgetExceedWarningPresent(false);
  }


  @Test(groups = {"requisition"})
  public void testVerifyBudgetWhenEmergencyRnRIsCreatedWhenBudgetFlagIsTrueAndBudgetInformationPresent() throws SQLException {
    dbWrapper.updateFieldValue("programs", "budgetingApplies", "true", "name", program);
    dbWrapper.insertBudgetData();

    HomePage homePage = loginPage.loginAs(userSIC, password);
    homePage.navigateHomePage();

    homePage.navigateInitiateRnRScreenAndSelectingRequiredFields(program, "Emergency");
    homePage.clickProceed();

    enterDetailsInRnRForFirstProduct(100, 100, 200);

    initiateRnRPage.verifyBudgetNotDisplayed();
    initiateRnRPage.checkWhetherBudgetExceedWarningPresent(false);

    initiateRnRPage.clickSubmitButton();
    initiateRnRPage.clickOk();
    initiateRnRPage.verifyBudgetNotDisplayed();
    initiateRnRPage.checkWhetherBudgetExceedWarningPresent(false);

    initiateRnRPage.clickNonFullSupplyTab();
    initiateRnRPage.verifyBudgetNotDisplayed();
    initiateRnRPage.checkWhetherBudgetExceedWarningPresent(false);

    initiateRnRPage.authorizeRnR();
    initiateRnRPage.clickOk();

    ApprovePage approvePage = homePage.navigateToApprove();
    approvePage.clickRequisitionPresentForApproval();
    initiateRnRPage.verifyBudgetNotDisplayed();
    initiateRnRPage.checkWhetherBudgetExceedWarningPresent(false);

    approvePage.clickApproveButton();
    approvePage.clickOk();

    viewRequisition();
    initiateRnRPage.verifyBudgetNotDisplayed();
    initiateRnRPage.checkWhetherBudgetExceedWarningPresent(false);
  }

  @Test(groups = {"requisition"})
  public void testVerifyBudgetWhenEmergencyRnRIsCreatedWhenBudgetFlagIsFalseAndBudgetInformationNotPresent() throws SQLException {
    dbWrapper.updateFieldValue("programs", "budgetingApplies", "false", "name", program);
    HomePage homePage = loginPage.loginAs(userSIC, password);
    homePage.navigateHomePage();

    homePage.navigateInitiateRnRScreenAndSelectingRequiredFields(program, "Emergency");
    homePage.clickProceed();

    enterDetailsInRnRForFirstProduct(100, 100, 0);

    initiateRnRPage.verifyBudgetNotDisplayed();
    initiateRnRPage.checkWhetherBudgetExceedWarningPresent(false);

    initiateRnRPage.clickSubmitButton();
    initiateRnRPage.clickOk();
    initiateRnRPage.verifyBudgetNotDisplayed();
    initiateRnRPage.checkWhetherBudgetExceedWarningPresent(false);

    initiateRnRPage.clickNonFullSupplyTab();
    initiateRnRPage.verifyBudgetNotDisplayed();
    initiateRnRPage.checkWhetherBudgetExceedWarningPresent(false);

    initiateRnRPage.authorizeRnR();
    initiateRnRPage.clickOk();

    ApprovePage approvePage = homePage.navigateToApprove();
    approvePage.clickRequisitionPresentForApproval();
    initiateRnRPage.verifyBudgetNotDisplayed();
    initiateRnRPage.checkWhetherBudgetExceedWarningPresent(false);

    approvePage.clickApproveButton();
    approvePage.clickOk();

    viewRequisition();
    initiateRnRPage.verifyBudgetNotDisplayed();
    initiateRnRPage.checkWhetherBudgetExceedWarningPresent(false);
  }

  @Test(groups = {"requisition"})
  public void testVerifyBudgetWhenRegularRnRIsCreatedAndBudgetFlagIsTrueAndContainsBudgetInformationForDifferentPeriod() throws SQLException {
    dbWrapper.updateFieldValue("programs", "budgetingApplies", "true", "name", program);
    dbWrapper.insertBudgetData();
    dbWrapper.insertProcessingPeriod("PastPeriod", "past period", "2013-09-01", "2013-10-02", 1, "M");

    HomePage homePage = loginPage.loginAs(userSIC, password);

    homePage.navigateHomePage();
    homePage.navigateInitiateRnRScreenAndSelectingRequiredFields(program, "Regular");
    homePage.clickProceed();

    enterDetailsInRnRForFirstProduct(100, 100, 100);

    initiateRnRPage.verifyBudgetAmountNotAllocated();
    initiateRnRPage.checkWhetherBudgetExceedWarningPresent(false);

    initiateRnRPage.clickSubmitButton();
    initiateRnRPage.clickOk();
    initiateRnRPage.verifyBudgetAmountNotAllocated();
    initiateRnRPage.checkWhetherBudgetExceedWarningPresent(false);

    initiateRnRPage.clickNonFullSupplyTab();
    initiateRnRPage.verifyBudgetAmountNotAllocated();
    initiateRnRPage.checkWhetherBudgetExceedWarningPresent(false);

    initiateRnRPage.authorizeRnR();
    initiateRnRPage.clickOk();

    ApprovePage approvePage = homePage.navigateToApprove();
    approvePage.clickRequisitionPresentForApproval();
    initiateRnRPage.verifyBudgetAmountNotAllocated();
    initiateRnRPage.checkWhetherBudgetExceedWarningPresent(false);

    approvePage.clickApproveButton();
    approvePage.clickOk();

    viewRequisition();
    initiateRnRPage.verifyBudgetAmountNotAllocated();
    initiateRnRPage.checkWhetherBudgetExceedWarningPresent(false);

    homePage.navigateHomePage();
    homePage.navigateInitiateRnRScreenAndSelectingRequiredFields(program, "Regular");
    homePage.clickProceed();

    enterDetailsInRnRForFirstProduct(100, 100, 10);
    initiateRnRPage.enterValueIfNotNull(90, "newPatientFirstProduct");
    initiateRnRPage.verifyBudgetAmountPresentOnFooter("$200.00");
    initiateRnRPage.checkWhetherBudgetExceedWarningPresent(true);
  }

  @Test(groups = {"requisition"})
  public void testVerifyBudgetWhenRegularRnRIsCreatedAndBudgetFlagIsTrueAndContainsBudgetInformationAndUpdated() throws SQLException {
    dbWrapper.updateFieldValue("programs", "budgetingApplies", "true", "name", program);
    dbWrapper.insertBudgetData();

    HomePage homePage = loginPage.loginAs(userSIC, password);

    homePage.navigateHomePage();
    homePage.navigateInitiateRnRScreenAndSelectingRequiredFields(program, "Regular");
    homePage.clickProceed();

    enterDetailsInRnRForFirstProduct(100, 50, 90);
    initiateRnRPage.saveRnR();

    initiateRnRPage.verifyBudgetAmountPresentOnFooter("$200.00");
    initiateRnRPage.checkWhetherBudgetExceedWarningPresent(true);
    dbWrapper.updateFieldValue("budget_line_items", "allocatedBudget", "300", null, null);
    testWebDriver.refresh();

    initiateRnRPage.verifyBudgetAmountPresentOnFooter("$200.00");
    initiateRnRPage.checkWhetherBudgetExceedWarningPresent(true);

    initiateRnRPage.clickSubmitButton();
    initiateRnRPage.clickOk();
    initiateRnRPage.verifyBudgetAmountPresentOnFooter("$200.00");
    initiateRnRPage.checkWhetherBudgetExceedWarningPresent(true);

    initiateRnRPage.clickNonFullSupplyTab();
    initiateRnRPage.verifyBudgetAmountPresentOnFooter("$200.00");
    initiateRnRPage.checkWhetherBudgetExceedWarningPresent(true);

    initiateRnRPage.authorizeRnR();
    initiateRnRPage.clickOk();

    ApprovePage approvePage = homePage.navigateToApprove();
    approvePage.clickRequisitionPresentForApproval();
    initiateRnRPage.verifyBudgetAmountPresentOnFooter("$200.00");
    initiateRnRPage.checkWhetherBudgetExceedWarningPresent(true);

    approvePage.clickApproveButton();
    approvePage.clickOk();

    viewRequisition();
    initiateRnRPage.verifyBudgetAmountPresentOnFooter("$200.00");
    initiateRnRPage.checkWhetherBudgetExceedWarningPresent(true);
  }

  @Test(groups = {"requisition"})
  public void testVerifyBudgetWhenNonFullSupplyProductAndRegimenPresentInRegularRnR() throws SQLException {
    dbWrapper.updateFieldValue("programs", "budgetingApplies", "true", "name", program);
    dbWrapper.insertBudgetData();
    dbWrapper.insertRegimenTemplateColumnsForProgram(program);
    dbWrapper.insertRegimenTemplateConfiguredForProgram(program, "ADULTS", "Regimen", "Regimen1", true);
    dbWrapper.updateFieldValue("programs", "regimenTemplateConfigured", "true", null, null);

    HomePage homePage = loginPage.loginAs(userSIC, password);

    homePage.navigateHomePage();
    homePage.navigateInitiateRnRScreenAndSelectingRequiredFields(program, "Regular");
    homePage.clickProceed();

    enterDetailsInRnRForFirstProduct(100, 50, 50);

    initiateRnRPage.verifyBudgetAmountPresentOnFooter("$200.00");
    initiateRnRPage.checkWhetherBudgetExceedWarningPresent(false);

    initiateRnRPage.clickNonFullSupplyTab();
    initiateRnRPage.addNonFullSupplyLineItems("120", "reason", "antibiotic", "P11", "Antibiotics");
    initiateRnRPage.verifyBudgetAmountPresentOnFooter("$200.00");
    initiateRnRPage.checkWhetherBudgetExceedWarningPresent(true);

    initiateRnRPage.clickRegimenTab();
    initiateRnRPage.enterValuesOnRegimenScreen(3, 1, "8");
    initiateRnRPage.enterValuesOnRegimenScreen(4, 1, "9");
    initiateRnRPage.enterValuesOnRegimenScreen(5, 1, "10");
    initiateRnRPage.enterValuesOnRegimenScreen(6, 1, "11");
    initiateRnRPage.verifyBudgetAmountPresentOnFooter("$200.00");
    initiateRnRPage.checkWhetherBudgetExceedWarningPresent(true);

    initiateRnRPage.clickSubmitButton();
    initiateRnRPage.clickOk();
    initiateRnRPage.clickFullSupplyTab();
    initiateRnRPage.skipAllProduct();
    initiateRnRPage.verifyBudgetAmountPresentOnFooter("$200.00");
    initiateRnRPage.checkWhetherBudgetExceedWarningPresent(false);
    initiateRnRPage.clickNonFullSupplyTab();
    initiateRnRPage.enterValueIfNotNull(200, "requestedQuantityFirstProduct");
    initiateRnRPage.verifyBudgetAmountPresentOnFooter("$200.00");
    initiateRnRPage.checkWhetherBudgetExceedWarningPresent(true);

    initiateRnRPage.authorizeRnR();
    initiateRnRPage.clickOk();

    ApprovePage approvePage = homePage.navigateToApprove();
    approvePage.clickRequisitionPresentForApproval();
    approvePage.editNonFullSupplyApproveQuantity("50");
    initiateRnRPage.verifyBudgetAmountPresentOnFooter("$200.00");
    initiateRnRPage.checkWhetherBudgetExceedWarningPresent(false);

    approvePage.clickApproveButton();
    approvePage.clickOk();

    viewRequisition();
    initiateRnRPage.verifyBudgetAmountPresentOnFooter("$200.00");
    initiateRnRPage.checkWhetherBudgetExceedWarningPresent(false);
  }

  private void setUpData(String program, String userSIC) throws SQLException {
    setupProductTestData("P10", "P11", program, "lvl3_hospital");
    dbWrapper.insertFacilities("F10", "F11");
    dbWrapper.configureTemplate(program);
    List<String> rightsList = asList(CREATE_REQUISITION, VIEW_REQUISITION, AUTHORIZE_REQUISITION, APPROVE_REQUISITION);
    setupTestUserRoleRightsData(userSIC, rightsList);
    dbWrapper.insertSupervisoryNode("F10", "N1", "Node 1", "null");
    dbWrapper.insertRoleAssignment(userSIC, "store in-charge");
    dbWrapper.insertSchedule("Q1stM", "QuarterMonthly", "QuarterMonth");
    dbWrapper.insertSchedule("M", "Monthly", "Month");
    dbWrapper.insertProcessingPeriod("Period1", "first period", "2012-12-01", "2013-01-15", 1, "Q1stM");
    dbWrapper.insertProcessingPeriod("Period2", "second period", "2013-01-16", "2013-01-30", 1, "M");
    setupRequisitionGroupData("RG1", "RG2", "N1", "N2", "F10", "F11");
    dbWrapper.insertSupplyLines("N1", program, "F10", true);
  }

  public void enterDetailsInRnRForFirstProduct(int beginningBalance, int quantityReceived, int quantityDispensed) {
    initiateRnRPage.enterValueIfNotNull(beginningBalance, "beginningBalanceFirstProduct");
    initiateRnRPage.enterValueIfNotNull(quantityReceived, "quantityReceivedFirstProduct");
    initiateRnRPage.enterValueIfNotNull(quantityDispensed, "quantityDispensedFirstProduct");
  }

  public void viewRequisition() {
    HomePage homePage = PageObjectFactory.getHomePage(testWebDriver);
    ViewRequisitionPage viewRequisitionPage = homePage.navigateViewRequisition();
    viewRequisitionPage.enterViewSearchCriteria();
    viewRequisitionPage.clickSearch();
    viewRequisitionPage.clickRnRList();
  }

  @AfterMethod(groups = "requisition")
  public void tearDown() throws SQLException {
    testWebDriver.sleep(500);
    if (!testWebDriver.getElementById("username").isDisplayed()) {
      HomePage homePage = PageObjectFactory.getHomePage(testWebDriver);
      homePage.logout(baseUrlGlobal);
      dbWrapper.deleteData();
      dbWrapper.closeConnection();
    }
  }
}
