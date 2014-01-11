package org.openlmis.functional;


import org.openlmis.UiUtils.TestCaseHelper;
import org.openlmis.pageobjects.*;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

public class ManageBudget extends TestCaseHelper{
  public static final String APPROVE_REQUISITION = "APPROVE_REQUISITION";
  public static final String CREATE_REQUISITION = "CREATE_REQUISITION";
  public static final String AUTHORIZED = "AUTHORIZED";
  public static final String AUTHORIZE_REQUISITION = "AUTHORIZE_REQUISITION";
  public static final String VIEW_REQUISITION = "VIEW_REQUISITION";
  public static final String program = "HIV";
  public static final String userSIC =  "storeIncharge";
  public static final String password = "Admin123";

  public LoginPage loginPage;


  @BeforeMethod(groups = "requisition")
  @cucumber.api.java.Before
  public void setUp() throws Exception {
    super.setup();
    dbWrapper.deleteData();
    setUpData(program, userSIC);
    dbWrapper.deleteProcessingPeriods();
    dbWrapper.insertProcessingPeriod("current Period", "current Period", "2013-10-03", "2016-01-30", 1, "M");

  }

  @Test(groups = {"requisition"})
  public void testVerifyBudgetWhenRegularRnRIsCreatedAndBudgetFlagIsTrueAndContainsBudgetInformation() throws Exception {
    dbWrapper.updateBudgetFlag(program, true);
    dbWrapper.insertBudgetData();

    LoginPage loginPage = new LoginPage(testWebDriver, baseUrlGlobal);
    HomePage homePage = loginPage.loginAs(userSIC, password);

    homePage.navigateHomePage();
    homePage.navigateInitiateRnRScreenAndSelectingRequiredFields(program, "Regular");
    homePage.clickProceed();

    InitiateRnRPage initiateRnRPage =new InitiateRnRPage(testWebDriver);
    enterDetailsInRnRForFirstProduct(100,50,50);

    InitiateRnR initiateRnR = new InitiateRnR();
    initiateRnR.verifyBudgetAmountPresentOnFooter("$200.00");
    initiateRnR.checkWhetherBudgetExceedWarningPresent(false);
    initiateRnRPage.saveRnR();

    enterDetailsInRnRForFirstProduct(100,100,200);
    initiateRnRPage.clickSubmitButton();
    initiateRnRPage.clickOk();
    initiateRnR.verifyBudgetAmountPresentOnFooter("$200.00");
    initiateRnR.checkWhetherBudgetExceedWarningPresent(true);


    initiateRnRPage.enterValue(9,"requestedQuantityFirstProduct");
    initiateRnRPage.enterExplanationReason();
    initiateRnRPage.clickNonFullSupplyTab();
    initiateRnR.verifyBudgetAmountPresentOnFooter("$200.00");
    initiateRnR.checkWhetherBudgetExceedWarningPresent(false);

    initiateRnRPage.authorizeRnR();
    initiateRnRPage.clickOk();

    ApprovePage approvePage = homePage.navigateToApprove();
    approvePage.clickRequisitionPresentForApproval();
    approvePage.editFullSupplyApproveQuantity("200");
    initiateRnR.verifyBudgetAmountPresentOnFooter("$200.00");
    initiateRnR.checkWhetherBudgetExceedWarningPresent(true);

    approvePage.clickApproveButton();
    approvePage.clickOk();

    viewRequisition();
    initiateRnR.verifyBudgetAmountPresentOnFooter("$200.00");
    initiateRnR.checkWhetherBudgetExceedWarningPresent(true);
  }

  @Test(groups = {"requisition"})
  public void testVerifyBudgetWhenRegularRnRIsCreatedAndBudgetFlagIsTrueAndDoNotContainsBudgetInformation() throws Exception {
    dbWrapper.updateBudgetFlag(program, true);

    LoginPage loginPage = new LoginPage(testWebDriver, baseUrlGlobal);
    HomePage homePage = loginPage.loginAs(userSIC, password);
    homePage.navigateHomePage();
    homePage.navigateInitiateRnRScreenAndSelectingRequiredFields(program, "Regular");
    homePage.clickProceed();

    InitiateRnRPage initiateRnRPage =new InitiateRnRPage(testWebDriver);
    enterDetailsInRnRForFirstProduct(0,70,60);

    InitiateRnR initiateRnR = new InitiateRnR();
    initiateRnR.verifyBudgetAmountNotAllocated();
    initiateRnR.checkWhetherBudgetExceedWarningPresent(false);

    initiateRnRPage.clickSubmitButton();
    initiateRnRPage.clickOk();

    initiateRnR.verifyBudgetAmountNotAllocated();
    initiateRnR.checkWhetherBudgetExceedWarningPresent(false);

    initiateRnRPage.clickNonFullSupplyTab();
    initiateRnR.verifyBudgetAmountNotAllocated();
    initiateRnR.checkWhetherBudgetExceedWarningPresent(false);

    initiateRnRPage.authorizeRnR();
    initiateRnRPage.clickOk();


    ApprovePage approvePage = homePage.navigateToApprove();
    approvePage.clickRequisitionPresentForApproval();
    initiateRnR.verifyBudgetAmountNotAllocated();
    initiateRnR.checkWhetherBudgetExceedWarningPresent(false);

    approvePage.clickApproveButton();
    approvePage.clickOk();

    viewRequisition();
    initiateRnR.verifyBudgetAmountNotAllocated();
    initiateRnR.checkWhetherBudgetExceedWarningPresent(false);
  }

  @Test(groups = {"requisition"})
  public void testVerifyBudgetWhenRegularRnRIsCreatedAndBudgetFlagIsFalseAndBudgetFilePresent() throws Exception {
    dbWrapper.updateBudgetFlag(program, false);
    dbWrapper.insertBudgetData();

    LoginPage loginPage = new LoginPage(testWebDriver, baseUrlGlobal);
    HomePage homePage = loginPage.loginAs(userSIC, password);
    homePage.navigateHomePage();

    homePage.navigateInitiateRnRScreenAndSelectingRequiredFields(program, "Regular");
    homePage.clickProceed();

    InitiateRnRPage initiateRnRPage =new InitiateRnRPage(testWebDriver);
    enterDetailsInRnRForFirstProduct(0,100,0);
    initiateRnRPage.enterValue(90,"newPatientFirstProduct");

    InitiateRnR initiateRnR = new InitiateRnR();
    initiateRnR.verifyBudgetNotDisplayed();
    initiateRnR.checkWhetherBudgetExceedWarningPresent(false);

    initiateRnRPage.clickSubmitButton();
    initiateRnRPage.clickOk();
    initiateRnR.verifyBudgetNotDisplayed();
    initiateRnR.checkWhetherBudgetExceedWarningPresent(false);

    initiateRnRPage.clickNonFullSupplyTab();
    initiateRnR.verifyBudgetNotDisplayed();
    initiateRnR.checkWhetherBudgetExceedWarningPresent(false);

    initiateRnRPage.authorizeRnR();
    initiateRnRPage.clickOk();

    ApprovePage approvePage = homePage.navigateToApprove();
    approvePage.clickRequisitionPresentForApproval();
    initiateRnR.verifyBudgetNotDisplayed();
    initiateRnR.checkWhetherBudgetExceedWarningPresent(false);

    approvePage.clickApproveButton();
    approvePage.clickOk();

    viewRequisition();
    initiateRnR.verifyBudgetNotDisplayed();
    initiateRnR.checkWhetherBudgetExceedWarningPresent(false);
  }


  @Test(groups = {"requisition"})
  public void testVerifyBudgetWhenEmergencyRnRIsCreatedWhenBudgetFlagIsTrueAndBudgetInformationPresent() throws Exception {
    dbWrapper.updateBudgetFlag(program, true);
    dbWrapper.insertBudgetData();

    LoginPage loginPage = new LoginPage(testWebDriver, baseUrlGlobal);
    HomePage homePage = loginPage.loginAs(userSIC, password);
    homePage.navigateHomePage();

    homePage.navigateInitiateRnRScreenAndSelectingRequiredFields(program, "Emergency");
    homePage.clickProceed();

    InitiateRnRPage initiateRnRPage =new InitiateRnRPage(testWebDriver);
    enterDetailsInRnRForFirstProduct(100, 100, 200);

    InitiateRnR initiateRnR = new InitiateRnR();
    initiateRnR.verifyBudgetNotDisplayed();
    initiateRnR.checkWhetherBudgetExceedWarningPresent(false);

    initiateRnRPage.clickSubmitButton();
    initiateRnRPage.clickOk();
    initiateRnR.verifyBudgetNotDisplayed();
    initiateRnR.checkWhetherBudgetExceedWarningPresent(false);

    initiateRnRPage.clickNonFullSupplyTab();
    initiateRnR.verifyBudgetNotDisplayed();
    initiateRnR.checkWhetherBudgetExceedWarningPresent(false);

    initiateRnRPage.authorizeRnR();
    initiateRnRPage.clickOk();

    ApprovePage approvePage = homePage.navigateToApprove();
    approvePage.clickRequisitionPresentForApproval();
    initiateRnR.verifyBudgetNotDisplayed();
    initiateRnR.checkWhetherBudgetExceedWarningPresent(false);

    approvePage.clickApproveButton();
    approvePage.clickOk();

    viewRequisition();
    initiateRnR.verifyBudgetNotDisplayed();
    initiateRnR.checkWhetherBudgetExceedWarningPresent(false);
  }

  @Test(groups = {"requisition"})
  public void testVerifyBudgetWhenEmergencyRnRIsCreatedWhenBudgetFlagIsFalseAndBudgetInformationNotPresent() throws Exception {
    dbWrapper.updateBudgetFlag(program,false);

    LoginPage loginPage = new LoginPage(testWebDriver, baseUrlGlobal);
    HomePage homePage = loginPage.loginAs(userSIC, password);
    homePage.navigateHomePage();

    homePage.navigateInitiateRnRScreenAndSelectingRequiredFields(program, "Emergency");
    homePage.clickProceed();

    InitiateRnRPage initiateRnRPage =new InitiateRnRPage(testWebDriver);
    enterDetailsInRnRForFirstProduct(100,100,0);

    InitiateRnR initiateRnR = new InitiateRnR();
    initiateRnR.verifyBudgetNotDisplayed();
    initiateRnR.checkWhetherBudgetExceedWarningPresent(false);

    initiateRnRPage.clickSubmitButton();
    initiateRnRPage.clickOk();
    initiateRnR.verifyBudgetNotDisplayed();
    initiateRnR.checkWhetherBudgetExceedWarningPresent(false);

    initiateRnRPage.clickNonFullSupplyTab();
    initiateRnR.verifyBudgetNotDisplayed();
    initiateRnR.checkWhetherBudgetExceedWarningPresent(false);

    initiateRnRPage.authorizeRnR();
    initiateRnRPage.clickOk();

    ApprovePage approvePage = homePage.navigateToApprove();
    approvePage.clickRequisitionPresentForApproval();
    initiateRnR.verifyBudgetNotDisplayed();
    initiateRnR.checkWhetherBudgetExceedWarningPresent(false);

    approvePage.clickApproveButton();
    approvePage.clickOk();

    viewRequisition();
    initiateRnR.verifyBudgetNotDisplayed();
    initiateRnR.checkWhetherBudgetExceedWarningPresent(false);
  }

  @Test(groups = {"requisition"})
  public void testVerifyBudgetWhenRegularRnRIsCreatedAndBudgetFlagIsTrueAndContainsBudgetInformationForDifferentPeriod() throws Exception {
    dbWrapper.updateBudgetFlag(program, true);
    dbWrapper.insertBudgetData();
    dbWrapper.insertProcessingPeriod("PastPeriod", "past period","2013-09-01","2013-10-02",1,"M" );

    LoginPage loginPage = new LoginPage(testWebDriver, baseUrlGlobal);
    HomePage homePage = loginPage.loginAs(userSIC, password);

    homePage.navigateHomePage();
    homePage.navigateInitiateRnRScreenAndSelectingRequiredFields(program, "Regular");
    homePage.clickProceed();

    InitiateRnRPage initiateRnRPage =new InitiateRnRPage(testWebDriver);
    enterDetailsInRnRForFirstProduct(100,100,100);

    InitiateRnR initiateRnR = new InitiateRnR();
    initiateRnR.verifyBudgetAmountNotAllocated();
    initiateRnR.checkWhetherBudgetExceedWarningPresent(false);

    initiateRnRPage.clickSubmitButton();
    initiateRnRPage.clickOk();
    initiateRnR.verifyBudgetAmountNotAllocated();
    initiateRnR.checkWhetherBudgetExceedWarningPresent(false);

    initiateRnRPage.clickNonFullSupplyTab();
    initiateRnR.verifyBudgetAmountNotAllocated();
    initiateRnR.checkWhetherBudgetExceedWarningPresent(false);

    initiateRnRPage.authorizeRnR();
    initiateRnRPage.clickOk();

    ApprovePage approvePage = homePage.navigateToApprove();
    approvePage.clickRequisitionPresentForApproval();
    initiateRnR.verifyBudgetAmountNotAllocated();
    initiateRnR.checkWhetherBudgetExceedWarningPresent(false);

    approvePage.clickApproveButton();
    approvePage.clickOk();

    viewRequisition();
    initiateRnR.verifyBudgetAmountNotAllocated();
    initiateRnR.checkWhetherBudgetExceedWarningPresent(false);

    homePage.navigateHomePage();
    homePage.navigateInitiateRnRScreenAndSelectingRequiredFields(program, "Regular");
    homePage.clickProceed();

    enterDetailsInRnRForFirstProduct(100,100,10);
    initiateRnRPage.enterValue(90,"newPatientFirstProduct");
    initiateRnR.verifyBudgetAmountPresentOnFooter("$200.00");
    initiateRnR.checkWhetherBudgetExceedWarningPresent(true);
  }

  @Test(groups = {"requisition"})
  public void testVerifyBudgetWhenRegularRnRIsCreatedAndBudgetFlagIsTrueAndContainsBudgetInformationAndUpdated() throws Exception {
    dbWrapper.updateBudgetFlag(program, true);
    dbWrapper.insertBudgetData();

    LoginPage loginPage = new LoginPage(testWebDriver, baseUrlGlobal);
    HomePage homePage = loginPage.loginAs(userSIC, password);

    homePage.navigateHomePage();
    homePage.navigateInitiateRnRScreenAndSelectingRequiredFields(program, "Regular");
    homePage.clickProceed();

    InitiateRnRPage initiateRnRPage =new InitiateRnRPage(testWebDriver);
    enterDetailsInRnRForFirstProduct(100,50,90);
    initiateRnRPage.saveRnR();

    InitiateRnR initiateRnR = new InitiateRnR();
    initiateRnR.verifyBudgetAmountPresentOnFooter("$200.00");
    initiateRnR.checkWhetherBudgetExceedWarningPresent(true);

    dbWrapper.updateBudgetLineItemsByField("allocatedBudget","300");
    testWebDriver.refresh();

    initiateRnR.verifyBudgetAmountPresentOnFooter("$200.00");
    initiateRnR.checkWhetherBudgetExceedWarningPresent(true);

    initiateRnRPage.clickSubmitButton();
    initiateRnRPage.clickOk();
    initiateRnR.verifyBudgetAmountPresentOnFooter("$200.00");
    initiateRnR.checkWhetherBudgetExceedWarningPresent(true);

    initiateRnRPage.clickNonFullSupplyTab();
    initiateRnR.verifyBudgetAmountPresentOnFooter("$200.00");
    initiateRnR.checkWhetherBudgetExceedWarningPresent(true);

    initiateRnRPage.authorizeRnR();
    initiateRnRPage.clickOk();

    ApprovePage approvePage = homePage.navigateToApprove();
    approvePage.clickRequisitionPresentForApproval();
    initiateRnR.verifyBudgetAmountPresentOnFooter("$200.00");
    initiateRnR.checkWhetherBudgetExceedWarningPresent(true);

    approvePage.clickApproveButton();
    approvePage.clickOk();

    viewRequisition();
    initiateRnR.verifyBudgetAmountPresentOnFooter("$200.00");
    initiateRnR.checkWhetherBudgetExceedWarningPresent(true);
  }

  @Test(groups = {"requisition"})
  public void testVerifyBudgetWhenNonFullSupplyProductAndRegimenPresentInRegularRnR() throws Exception {
    dbWrapper.updateBudgetFlag(program, true);
    dbWrapper.insertBudgetData();
    dbWrapper.insertRegimenTemplateColumnsForProgram(program);
    dbWrapper.insertRegimenTemplateConfiguredForProgram(program, "ADULTS", "Regimen", "Regimen1", true);
    dbWrapper.setRegimenTemplateConfiguredForAllPrograms(true);

    LoginPage loginPage = new LoginPage(testWebDriver, baseUrlGlobal);
    HomePage homePage = loginPage.loginAs(userSIC, password);

    homePage.navigateHomePage();
    homePage.navigateInitiateRnRScreenAndSelectingRequiredFields(program, "Regular");
    homePage.clickProceed();

    InitiateRnRPage initiateRnRPage =new InitiateRnRPage(testWebDriver);
    enterDetailsInRnRForFirstProduct(100,50,50);

    InitiateRnR initiateRnR = new InitiateRnR();
    initiateRnR.verifyBudgetAmountPresentOnFooter("$200.00");
    initiateRnR.checkWhetherBudgetExceedWarningPresent(false);

    initiateRnRPage.clickNonFullSupplyTab();
    initiateRnRPage.addNonFullSupplyLineItems("120","reason","antibiotic","P11","Antibiotics");
    initiateRnR.verifyBudgetAmountPresentOnFooter("$200.00");
    initiateRnR.checkWhetherBudgetExceedWarningPresent(true);

    initiateRnRPage.clickRegimenTab();
    initiateRnRPage.enterValuesOnRegimenScreen(3,2,"8");
    initiateRnRPage.enterValuesOnRegimenScreen(4,2,"9");
    initiateRnRPage.enterValuesOnRegimenScreen(5,2,"10");
    initiateRnRPage.enterValuesOnRegimenScreen(6,2,"11");
    initiateRnR.verifyBudgetAmountPresentOnFooter("$200.00");
    initiateRnR.checkWhetherBudgetExceedWarningPresent(true);

    initiateRnRPage.clickSubmitButton();
    initiateRnRPage.clickOk();
    initiateRnRPage.clickFullSupplyTab();
    initiateRnRPage.skipAllProduct();
    initiateRnR.verifyBudgetAmountPresentOnFooter("$200.00");
    initiateRnR.checkWhetherBudgetExceedWarningPresent(false);
    initiateRnRPage.clickNonFullSupplyTab();
    initiateRnRPage.enterValue(200,"requestedQuantityFirstProduct");
    initiateRnR.verifyBudgetAmountPresentOnFooter("$200.00");
    initiateRnR.checkWhetherBudgetExceedWarningPresent(true);

    initiateRnRPage.authorizeRnR();
    initiateRnRPage.clickOk();

    ApprovePage approvePage = homePage.navigateToApprove();
    approvePage.clickRequisitionPresentForApproval();
    approvePage.editNonFullSupplyApproveQuantity("50");
    initiateRnR.verifyBudgetAmountPresentOnFooter("$200.00");
    initiateRnR.checkWhetherBudgetExceedWarningPresent(false);

    approvePage.clickApproveButton();
    approvePage.clickOk();

    viewRequisition();
    initiateRnR.verifyBudgetAmountPresentOnFooter("$200.00");
    initiateRnR.checkWhetherBudgetExceedWarningPresent(false);
  }

  private void setUpData(String program, String userSIC) throws Exception {
    setupProductTestData("P10", "P11", program, "lvl3_hospital");
    dbWrapper.insertFacilities("F10", "F11");
    dbWrapper.configureTemplate(program);
    List<String> rightsList = new ArrayList<>();
    rightsList.add(CREATE_REQUISITION);
    rightsList.add(VIEW_REQUISITION);
    rightsList.add(AUTHORIZE_REQUISITION);
    rightsList.add(APPROVE_REQUISITION);
    setupTestUserRoleRightsData("200", userSIC, rightsList);
    dbWrapper.insertSupervisoryNode("F10", "N1", "Node 1", "null");
    dbWrapper.insertRoleAssignment("200", "store in-charge");
    dbWrapper.insertSchedule("Q1stM", "QuarterMonthly", "QuarterMonth");
    dbWrapper.insertSchedule("M", "Monthly", "Month");
    dbWrapper.insertProcessingPeriod("Period1", "first period", "2012-12-01", "2013-01-15", 1, "Q1stM");
    dbWrapper.insertProcessingPeriod("Period2", "second period", "2013-01-16", "2013-01-30", 1, "M");
    setupRequisitionGroupData("RG1", "RG2", "N1", "N2", "F10", "F11");
    dbWrapper.insertSupplyLines("N1", program, "F10", true);
  }



  public void enterDetailsInRnRForFirstProduct(int beginningBalance, int quantityReceived, int quantityDispensed) throws IOException {
    InitiateRnRPage initiateRnRPage =new InitiateRnRPage(testWebDriver);
    initiateRnRPage.enterValue(beginningBalance, "beginningBalanceFirstProduct");
    initiateRnRPage.enterValue(quantityReceived, "quantityReceivedFirstProduct");
    initiateRnRPage.enterValue(quantityDispensed, "quantityDispensedFirstProduct");
  }

  public void viewRequisition() throws IOException {
    HomePage homePage = new HomePage(testWebDriver);
    ViewRequisitionPage viewRequisitionPage=homePage.navigateViewRequisition();
    viewRequisitionPage.enterViewSearchCriteria();
    viewRequisitionPage.clickSearch();
    viewRequisitionPage.clickRnRList();
  }

  @AfterMethod(groups = "requisition")
  @cucumber.api.java.After
  public void tearDown() throws Exception {
    testWebDriver.sleep(500);
    if (!testWebDriver.getElementById("username").isDisplayed()) {
      HomePage homePage = new HomePage(testWebDriver);
      homePage.logout(baseUrlGlobal);
      dbWrapper.deleteData();
      dbWrapper.closeConnection();
    }

  }
}
