/*
 * Copyright © 2013 VillageReach.  All Rights Reserved.  This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 *
 *  If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package org.openlmis.functional;


import cucumber.api.java.Before;
import cucumber.api.java.en.And;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import org.openlmis.UiUtils.CaptureScreenshotOnFailureListener;
import org.openlmis.UiUtils.TestCaseHelper;
import org.openlmis.pageobjects.ConfigureOrderPage;
import org.openlmis.pageobjects.HomePage;
import org.openlmis.pageobjects.LoginPage;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;
import org.testng.annotations.*;

import static com.thoughtworks.selenium.SeleneseTestBase.assertTrue;
import static com.thoughtworks.selenium.SeleneseTestNgHelper.assertEquals;


@TransactionConfiguration(defaultRollback = true)
@Transactional

@Listeners(CaptureScreenshotOnFailureListener.class)

public class ConfigureOrderTemplate extends TestCaseHelper {


  @BeforeMethod(groups = "functional2")
  @Before
  public void setUp() throws Exception {
    super.setup();
  }


  @And("^I access configure order screen$")
  public void accessOrderScreen() throws Exception {
    HomePage homePage = new HomePage(testWebDriver);
    homePage.navigateConfigureOrderScreen();
  }

  @Then("^I should see order file prefix \"([^\"]*)\"$")
  public void verifyOrderPrefix(String prefix) throws Exception {
    ConfigureOrderPage configureOrderPage = new ConfigureOrderPage(testWebDriver);
    assertEquals(configureOrderPage.getOrderPrefix(), prefix);
  }

  @And("^I should see include column header as \"([^\"]*)\"$")
  public void verifyIncludeColumnHeader(String indicator) throws Exception {
    ConfigureOrderPage configureOrderPage = new ConfigureOrderPage(testWebDriver);
    assertEquals(String.valueOf(configureOrderPage.getIncludeOrderHeader()), indicator);
  }

  @And("^I should see all column headers disabled$")
  public void verifyAllColumnsDisabled() throws Exception {
    ConfigureOrderPage configureOrderPage = new ConfigureOrderPage(testWebDriver);
    configureOrderPage.verifyColumnHeadersDisabled();
  }

  @And("^I should see include checkbox \"([^\"]*)\" for all column headers$")
  public void verifyAllColumnsDisabled(String flag) throws Exception {
    ConfigureOrderPage configureOrderPage = new ConfigureOrderPage(testWebDriver);
    configureOrderPage.verifyIncludeCheckboxForAllColumnHeaders(flag);
  }

  @When("^I save order file format$")
  public void clickSave() throws Exception {
    ConfigureOrderPage configureOrderPage = new ConfigureOrderPage(testWebDriver);
    configureOrderPage.clickSaveButton();
  }

  @Then("^I should see \"([^\"]*)\"$")
  public void verifySaveSuccessfullyMessage(String message) throws Exception {
    ConfigureOrderPage configureOrderPage = new ConfigureOrderPage(testWebDriver);
    configureOrderPage.verifySuccessMessage(message);
  }

  @Test(groups = {"functional2"}, dataProvider = "Data-Provider-Function")
  public void testVerifyDefaultSelectionOfPeriodAndOrderDateDropdown(String user, String password) throws Exception {
    LoginPage loginPage = new LoginPage(testWebDriver, baseUrlGlobal);
    HomePage homePage = loginPage.loginAs(user, password);
    ConfigureOrderPage configureOrderPage = homePage.navigateConfigureOrderScreen();
    String period=configureOrderPage.getSelectedOptionOfPeriodDropDown();
    assertEquals(period,"MM/yy");
    String orderDate=configureOrderPage.getSelectedOptionOfOrderDateDropDown();
    assertEquals(orderDate,"dd/MM/yy");
  }

  @Test(groups = {"functional2"}, dataProvider = "Data-Provider-Function")
  public void testEditPeriodAndOrderDateDropDown(String user, String password) throws Exception {
    LoginPage loginPage = new LoginPage(testWebDriver, baseUrlGlobal);
    HomePage homePage = loginPage.loginAs(user, password);
    ConfigureOrderPage configureOrderPage = homePage.navigateConfigureOrderScreen();
    configureOrderPage.selectValueFromPeriodDropDown("MM-dd-yyyy");
    configureOrderPage.selectValueFromOrderDateDropDown("yyyy-MM-dd");
    configureOrderPage.clickSaveButton();
    configureOrderPage.verifySuccessMessage("Order file configuration saved successfully!");
    String period=configureOrderPage.getSelectedOptionOfPeriodDropDown();
    assertEquals(period,"MM-dd-yyyy");
    String orderDate=configureOrderPage.getSelectedOptionOfOrderDateDropDown();
    assertEquals(orderDate,"yyyy-MM-dd");
    configureOrderPage.selectValueFromPeriodDropDown("MM/yy");
    configureOrderPage.selectValueFromOrderDateDropDown("dd/MM/yy");
    configureOrderPage.clickSaveButton();
    configureOrderPage.verifySuccessMessage("Order file configuration saved successfully!");
  }

  @Test(groups = {"functional2"}, dataProvider = "Data-Provider-Function")
  public void testVerifyIncludeColumnHeaderONWithHeadersAltered(String user, String password) throws Exception {
    String facilityCode = "FC";
    String orderNumber = "ON";
    String approvedQuantity = "Approved quantityApproved quantityApproved quantit";
    String productCode = "PC";
    String period = "Period1";
    String orderData = "OD";
    String orderPrefix = "OP";

    LoginPage loginPage = new LoginPage(testWebDriver, baseUrlGlobal);
    HomePage homePage = loginPage.loginAs(user, password);
    ConfigureOrderPage configureOrderPage = homePage.navigateConfigureOrderScreen();
    configureOrderPage.setOrderPrefix(orderPrefix);
    configureOrderPage.checkIncludeOrderHeader();
    configureOrderPage.verifyColumnHeadersEnabled();
    configureOrderPage.setFacilityCode(facilityCode);
    configureOrderPage.setOrderNumber(orderNumber);
    configureOrderPage.setApprovedQuantity(approvedQuantity);
    configureOrderPage.setProductCode(productCode);
    configureOrderPage.setPeriod(period);
    configureOrderPage.setOrderDate(orderData);
    configureOrderPage.clickSaveButton();
    configureOrderPage.verifySuccessMessage("Order file configuration saved successfully!");

    assertEquals(facilityCode, configureOrderPage.getFacilityCode());
    assertEquals(orderNumber, configureOrderPage.getOrderNumber());
    assertEquals(approvedQuantity, configureOrderPage.getApprovedQuantity());
    assertEquals(productCode, configureOrderPage.getProductCode());
    assertEquals(period, configureOrderPage.getPeriod());
    assertEquals(orderData, configureOrderPage.getOrderDate());
  }

  @Test(groups = {"functional2"}, dataProvider = "Data-Provider-Function")
  public void testVerifyIncludeColumnHeaderONWithHeadersBlank(String user, String password) throws Exception {
    String facilityCode = "";
    String orderNumber = "";
    String approvedQuantity = "";
    String productCode = "";
    String period = "";
    String orderData = "";
    String orderPrefix = "";

    LoginPage loginPage = new LoginPage(testWebDriver, baseUrlGlobal);
    HomePage homePage = loginPage.loginAs(user, password);
    ConfigureOrderPage configureOrderPage = homePage.navigateConfigureOrderScreen();
    configureOrderPage.setOrderPrefix(orderPrefix);
    configureOrderPage.checkIncludeOrderHeader();
    configureOrderPage.verifyColumnHeadersEnabled();
    configureOrderPage.setFacilityCode(facilityCode);
    configureOrderPage.setOrderNumber(orderNumber);
    configureOrderPage.setApprovedQuantity(approvedQuantity);
    configureOrderPage.setProductCode(productCode);
    configureOrderPage.setPeriod(period);
    configureOrderPage.setOrderDate(orderData);
    configureOrderPage.clickSaveButton();
    configureOrderPage.verifySuccessMessage("Order file configuration saved successfully!");

    assertEquals(facilityCode, configureOrderPage.getFacilityCode());
    assertEquals(orderNumber, configureOrderPage.getOrderNumber());
    assertEquals(approvedQuantity, configureOrderPage.getApprovedQuantity());
    assertEquals(productCode, configureOrderPage.getProductCode());
    assertEquals(period, configureOrderPage.getPeriod());
    assertEquals(orderData, configureOrderPage.getOrderDate());
  }

  @Test(groups = {"functional2"}, dataProvider = "Data-Provider-Function")
  public void testVerifyAllIncludeCheckBoxesUnchecked(String user, String password) throws Exception {
    LoginPage loginPage = new LoginPage(testWebDriver, baseUrlGlobal);
    HomePage homePage = loginPage.loginAs(user, password);
    ConfigureOrderPage configureOrderPage = homePage.navigateConfigureOrderScreen();
    configureOrderPage.unCheckFacilityCodeCheckBox();
    configureOrderPage.unCheckApprovedQuantityCheckBox();
    configureOrderPage.unCheckIncludeOrderHeader();
    configureOrderPage.unCheckOrderDateCheckBox();
    configureOrderPage.unCheckOrderNumberCheckBox();
    configureOrderPage.unCheckPeriodCheckBox();
    configureOrderPage.unCheckProductCodeCheckBox();
    configureOrderPage.clickSaveButton();
    configureOrderPage.verifySuccessMessage("Order file configuration saved successfully!");
    configureOrderPage.clickCancelButton();
    assertTrue("User should be redirected to home page", testWebDriver.getCurrentUrl().contains("public/pages/index.html#/index.html"));

  }

  @Test(groups = {"functional2"}, dataProvider = "Data-Provider-Function")
  public void testVerifyAddNewButtonFunctionality(String user, String password) throws Exception {
    String successMessage = "Order file configuration saved successfully!";

    LoginPage loginPage = new LoginPage(testWebDriver, baseUrlGlobal);
    HomePage homePage = loginPage.loginAs(user, password);
    ConfigureOrderPage configureOrderPage = homePage.navigateConfigureOrderScreen();
    configureOrderPage.clickAddNewButton();
    configureOrderPage.verifyElementsOnAddNewButtonClick(6,"true","Not applicable","");
    configureOrderPage.clickSaveButton();
    configureOrderPage.verifySuccessMessage(successMessage);
    configureOrderPage.clickRemoveIcon(6);
    configureOrderPage.clickSaveButton();
    configureOrderPage.verifySuccessMessage(successMessage);
  }




  @AfterMethod(groups = "functional2")
  public void tearDown() throws Exception {
    testWebDriver.sleep(500);
    if (!testWebDriver.getElementById("username").isDisplayed()) {
      HomePage homePage = new HomePage(testWebDriver);
      homePage.logout(baseUrlGlobal);
    }
    dbWrapper.deleteData();
    dbWrapper.closeConnection();
  }


  @DataProvider(name = "Data-Provider-Function")
  public Object[][] parameterIntTestProviderPositive() {
    return new Object[][]{
      {"Admin123", "Admin123"}
    };

  }

}

