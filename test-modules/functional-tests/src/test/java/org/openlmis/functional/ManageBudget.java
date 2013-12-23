package org.openlmis.functional;


import org.openlmis.UiUtils.TestCaseHelper;
import org.openlmis.pageobjects.*;
import org.openqa.selenium.By;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

public class ManageBudget extends TestCaseHelper{
  public static final String APPROVE_REQUISITION = "APPROVE_REQUISITION";
  public static final String CONVERT_TO_ORDER = "CONVERT_TO_ORDER";
  public static final String CREATE_REQUISITION = "CREATE_REQUISITION";
  public static final String AUTHORIZED = "AUTHORIZED";
  public static final String AUTHORIZE_REQUISITION = "AUTHORIZE_REQUISITION";
  public static final String VIEW_REQUISITION = "VIEW_REQUISITION";
  public static final String VIEW_ORDER = "VIEW_ORDER";

  public LoginPage loginPage;


  @BeforeMethod(groups = "requisition")
  @cucumber.api.java.Before
  public void setUp() throws Exception {
    super.setup();
    dbWrapper.deleteData();
  }

  @Test(groups = {"requisition"}, dataProvider = "Data-Provider-Function-RnR")
  public void testVerifyBudgetWhenRegularRnRIsCreatedAndBudgetFlagIsTrueAndContainsBudgetInformation(String program, String userSIC, String password) throws Exception {
    setUpData(program, userSIC);
    dbWrapper.updateBudgetFlag(program, true);
    dbWrapper.deleteProcessingPeriods();
    dbWrapper.insertProcessingPeriod("current Period", "current Period", "2013-10-03", "2014-01-30", 1, "M");
    dbWrapper.insertBudgetData();
    LoginPage loginPage = new LoginPage(testWebDriver, baseUrlGlobal);
    HomePage homePage = loginPage.loginAs(userSIC, password);
    homePage.navigateHomePage();
    homePage.navigateInitiateRnRScreenAndSelectingRequiredFields(program, "Regular");
    homePage.clickProceed();
    InitiateRnRPage initiateRnRPage =new InitiateRnRPage(testWebDriver);
    initiateRnRPage.enterValue(100, "beginningBalanceFirstProduct");
    initiateRnRPage.enterValue(100, "quantityReceivedFirstProduct");
    initiateRnRPage.enterValue(50, "quantityDispensedFirstProduct");
    initiateRnRPage.verifyBudgetInfoOnFooter();
    initiateRnRPage.clickSubmitButton();
    initiateRnRPage.clickOk();
    initiateRnRPage.verifyBudgetInfoOnFooter();
    initiateRnRPage.authorizeRnR();
    initiateRnRPage.clickOk();
    homePage.navigateToApprove();
    ApprovePage approvePage = new ApprovePage(testWebDriver);
    approvePage.ClickRequisitionPresentForApproval();
    initiateRnRPage.verifyBudgetInfoOnFooter();
    approvePage.clickApproveButton();
    approvePage.clickOk();

  }

  @Test(groups = {"requisition"}, dataProvider = "Data-Provider-Function-RnR")
  public void testVerifyBudgetWhenRegularRnRIsCreatedAndBudgetFlagIsTrueAndDoNotContainsBudgetInformation(String program, String userSIC, String password) throws Exception {
    setUpData(program, userSIC);
    dbWrapper.updateBudgetFlag(program,true);
    dbWrapper.deleteProcessingPeriods();
    dbWrapper.insertProcessingPeriod("current Period", "current Period", "2013-10-03", "2014-01-30", 1, "M");
    LoginPage loginPage = new LoginPage(testWebDriver, baseUrlGlobal);
    HomePage homePage = loginPage.loginAs(userSIC, password);
    homePage.navigateHomePage();
    homePage.navigateInitiateRnRScreenAndSelectingRequiredFields(program, "Regular");
    homePage.clickProceed();
    InitiateRnRPage initiateRnRPage =new InitiateRnRPage(testWebDriver);
    initiateRnRPage.enterValue(100, "beginningBalanceFirstProduct");
    initiateRnRPage.enterValue(100, "quantityReceivedFirstProduct");
    initiateRnRPage.enterValue(50, "quantityDispensedFirstProduct");
    assertEquals("Not allocated",testWebDriver.getElementById("allocatedBudgetNotApplicable").getText());
    assertTrue(testWebDriver.getElementByXpath("//span[@openlmis-message='label.allocated.budget']").isDisplayed());
    initiateRnRPage.clickSubmitButton();
    initiateRnRPage.clickOk();
    assertEquals("Not allocated",testWebDriver.getElementById("allocatedBudgetNotApplicable").getText());
    assertTrue(testWebDriver.getElementByXpath("//span[@openlmis-message='label.allocated.budget']").isDisplayed());
    initiateRnRPage.authorizeRnR();
    initiateRnRPage.clickOk();
    homePage.navigateToApprove();
    ApprovePage approvePage = new ApprovePage(testWebDriver);
    approvePage.ClickRequisitionPresentForApproval();
    assertEquals("Not allocated",testWebDriver.getElementById("allocatedBudgetNotApplicable").getText());
    assertTrue(testWebDriver.getElementByXpath("//span[@openlmis-message='label.allocated.budget']").isDisplayed());    approvePage.clickApproveButton();
    approvePage.clickOk();

  }

  @Test(groups = {"requisition"}, dataProvider = "Data-Provider-Function-RnR")
  public void testVerifyBudgetWhenRegularRnRIsCreatedAndBudgetFlagIsFalse(String program, String userSIC, String password) throws Exception {
    setUpData(program, userSIC);
    dbWrapper.updateBudgetFlag(program,false);
    dbWrapper.deleteProcessingPeriods();
    dbWrapper.insertProcessingPeriod("current Period", "current Period", "2013-10-03", "2014-01-30", 1, "M");
    dbWrapper.insertBudgetData();
    LoginPage loginPage = new LoginPage(testWebDriver, baseUrlGlobal);
    HomePage homePage = loginPage.loginAs(userSIC, password);
    homePage.navigateHomePage();
    homePage.navigateInitiateRnRScreenAndSelectingRequiredFields(program, "Regular");
    homePage.clickProceed();
    InitiateRnRPage initiateRnRPage =new InitiateRnRPage(testWebDriver);
    initiateRnRPage.enterValue(100, "beginningBalanceFirstProduct");
    initiateRnRPage.enterValue(100, "quantityReceivedFirstProduct");
    initiateRnRPage.enterValue(50, "quantityDispensedFirstProduct");
    assertFalse(testWebDriver.getElementById("allocatedBudgetAmount").isDisplayed());
    assertFalse(testWebDriver.getElementByXpath("//span[@openlmis-message='label.allocated.budget']").isDisplayed());
    initiateRnRPage.clickSubmitButton();
    initiateRnRPage.clickOk();
    assertFalse(testWebDriver.getElementById("allocatedBudgetAmount").isDisplayed());
    assertFalse(testWebDriver.getElementByXpath("//span[@openlmis-message='label.allocated.budget']").isDisplayed());
    initiateRnRPage.authorizeRnR();
    initiateRnRPage.clickOk();
    homePage.navigateToApprove();
    ApprovePage approvePage = new ApprovePage(testWebDriver);
    approvePage.ClickRequisitionPresentForApproval();
    assertFalse(testWebDriver.getElementById("allocatedBudgetAmount").isDisplayed());
    assertFalse(testWebDriver.getElementByXpath("//span[@openlmis-message='label.allocated.budget']").isDisplayed());
    approvePage.clickApproveButton();
    approvePage.clickOk();
  }


  @Test(groups = {"requisition"}, dataProvider = "Data-Provider-Function-RnR")
  public void testVerifyBudgetWhenEmergencyRnRIsCreated(String program, String userSIC, String password) throws Exception {
    setUpData(program, userSIC);
    dbWrapper.updateBudgetFlag(program,true);
    dbWrapper.deleteProcessingPeriods();
    dbWrapper.insertProcessingPeriod("current Period", "current Period", "2013-10-03", "2014-01-30", 1, "M");
    dbWrapper.insertBudgetData();
    LoginPage loginPage = new LoginPage(testWebDriver, baseUrlGlobal);
    HomePage homePage = loginPage.loginAs(userSIC, password);
    homePage.navigateHomePage();
    homePage.navigateInitiateRnRScreenAndSelectingRequiredFields(program, "Emergency");
    homePage.clickProceed();
    InitiateRnRPage initiateRnRPage =new InitiateRnRPage(testWebDriver);
    initiateRnRPage.enterValue(100, "beginningBalanceFirstProduct");
    initiateRnRPage.enterValue(100, "quantityReceivedFirstProduct");
    initiateRnRPage.enterValue(50,  "quantityDispensedFirstProduct");
    assertFalse(testWebDriver.getElementById("allocatedBudgetAmount").isDisplayed());
    assertFalse(testWebDriver.getElementByXpath("//span[@openlmis-message='label.allocated.budget']").isDisplayed());
    initiateRnRPage.clickSubmitButton();
    initiateRnRPage.clickOk();
    assertFalse(testWebDriver.getElementById("allocatedBudgetAmount").isDisplayed());
    assertFalse(testWebDriver.getElementByXpath("//span[@openlmis-message='label.allocated.budget']").isDisplayed());
    initiateRnRPage.authorizeRnR();
    initiateRnRPage.clickOk();
    homePage.navigateToApprove();
    ApprovePage approvePage = new ApprovePage(testWebDriver);
    approvePage.ClickRequisitionPresentForApproval();
    assertFalse(testWebDriver.getElementById("allocatedBudgetAmount").isDisplayed());
    assertFalse(testWebDriver.getElementByXpath("//span[@openlmis-message='label.allocated.budget']").isDisplayed());
    approvePage.clickApproveButton();
    approvePage.clickOk();
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
  @DataProvider(name = "Data-Provider-Function-RnR")
  public Object[][] parameterIntTestProviderRnR() {
    return new Object[][]{
      {"HIV", "storeIncharge", "Admin123"}
    };
  }
}
