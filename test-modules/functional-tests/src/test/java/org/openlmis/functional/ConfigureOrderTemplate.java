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


import cucumber.api.java.en.And;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import org.openlmis.UiUtils.CaptureScreenshotOnFailureListener;
import org.openlmis.UiUtils.TestCaseHelper;
import org.openlmis.pageobjects.HomePage;
import org.openlmis.pageobjects.LoginPage;
import org.openlmis.pageobjects.PageObjectFactory;
import org.openlmis.pageobjects.edi.ConfigureOrderPage;
import org.openlmis.pageobjects.edi.ConfigureSystemSettingsPage;
import org.testng.annotations.*;

import java.io.IOException;
import java.sql.SQLException;

import static com.thoughtworks.selenium.SeleneseTestBase.assertTrue;
import static com.thoughtworks.selenium.SeleneseTestNgHelper.assertEquals;

@Listeners(CaptureScreenshotOnFailureListener.class)

public class ConfigureOrderTemplate extends TestCaseHelper {
  ConfigureOrderPage configureOrderPage;
  LoginPage loginPage;

  @BeforeMethod(groups = "admin")
  public void setUp() throws InterruptedException, SQLException, IOException {
    super.setup();
    dbWrapper.setupOrderFileConfiguration("O", "TRUE");
    dbWrapper.deleteRowFromTable("order_file_columns", "openLMISField", "false");
    dbWrapper.removeAllExistingRights("Admin");
    dbWrapper.assignRight("Admin", "SYSTEM_SETTINGS");
    loginPage = PageObjectFactory.getLoginPage(testWebDriver, baseUrlGlobal);
  }

  @And("^I access configure order page$")
  public void accessOrderScreen() {
    HomePage homePage = PageObjectFactory.getHomePage(testWebDriver);
    ConfigureSystemSettingsPage configureSystemSettingsPage = homePage.navigateSystemSettingsScreen();
    configureSystemSettingsPage.navigateConfigureOrderPage();
  }

  @Then("^I should see order file prefix \"([^\"]*)\"$")
  public void verifyOrderPrefix(String prefix) {
    configureOrderPage = PageObjectFactory.getConfigureOrderPage(testWebDriver);
    assertEquals(configureOrderPage.getOrderPrefix(), prefix);
  }

  @And("^I should see include column header as \"([^\"]*)\"$")
  public void verifyIncludeColumnHeader(String indicator) {
    configureOrderPage = PageObjectFactory.getConfigureOrderPage(testWebDriver);
    assertEquals(String.valueOf(configureOrderPage.getIncludeOrderHeader()), indicator);
  }

  @And("^I should see all column headers disabled$")
  public void verifyAllColumnsDisabled() {
    configureOrderPage = PageObjectFactory.getConfigureOrderPage(testWebDriver);
    configureOrderPage.verifyColumnHeadersDisabled();
  }

  @And("^I should see include checkbox \"([^\"]*)\" for all column headers$")
  public void verifyAllColumnsDisabled(String flag) {
    configureOrderPage = PageObjectFactory.getConfigureOrderPage(testWebDriver);
    configureOrderPage.verifyIncludeCheckboxForAllColumnHeaders(flag);
  }

  @When("^I save order file format$")
  public void clickSave() {
    configureOrderPage = PageObjectFactory.getConfigureOrderPage(testWebDriver);
    configureOrderPage.clickSaveButton();
  }

  @Then("^I should see \"([^\"]*)\"$")
  public void verifySaveSuccessfullyMessage(String message) {
    configureOrderPage = PageObjectFactory.getConfigureOrderPage(testWebDriver);
    configureOrderPage.verifySuccessMessage(message);
  }

  @Test(groups = {"admin"}, dataProvider = "Data-Provider-Function")
  public void testEditPeriodAndOrderDateDropDown(String user, String password) {
    HomePage homePage = loginPage.loginAs(user, password);
    ConfigureSystemSettingsPage configureSystemSettingsPage = homePage.navigateSystemSettingsScreen();
    ConfigureOrderPage configureOrderPage = configureSystemSettingsPage.navigateConfigureOrderPage();
    configureOrderPage.selectValueFromPeriodDropDown("MM-dd-yyyy");
    configureOrderPage.selectValueFromOrderDateDropDown("yyyy-MM-dd");
    configureOrderPage.clickSaveButton();
    configureOrderPage.verifySuccessMessage("Order file configuration saved successfully!");

    testWebDriver.refresh();
    configureOrderPage = configureSystemSettingsPage.navigateConfigureOrderPage();

    assertEquals(configureOrderPage.getSelectedOptionOfPeriodDropDown(), "MM-dd-yyyy");
    assertEquals(configureOrderPage.getSelectedOptionOfOrderDateDropDown(), "yyyy-MM-dd");
    configureOrderPage.selectValueFromPeriodDropDown("MM/yy");
    configureOrderPage.selectValueFromOrderDateDropDown("dd/MM/yy");
    configureOrderPage.clickSaveButton();
  }

  @Test(groups = {"admin"}, dataProvider = "Data-Provider-Function")
  public void testVerifyIncludeColumnHeaderONWithHeadersAltered(String user, String password) {
    String facilityCode = "FC";
    String orderNumber = "ON";
    String approvedQuantity = "Approved quantityApproved quantityApproved quantit";
    String productCode = "PC";
    String productName = "PName";
    String period = "Period1";
    String orderData = "OD";
    String orderPrefix = "OP";

    HomePage homePage = loginPage.loginAs(user, password);
    ConfigureSystemSettingsPage configureSystemSettingsPage = homePage.navigateSystemSettingsScreen();
    ConfigureOrderPage configureOrderPage = configureSystemSettingsPage.navigateConfigureOrderPage();
    configureOrderPage.setOrderPrefix(orderPrefix);
    configureOrderPage.checkIncludeOrderHeader();
    configureOrderPage.verifyColumnHeadersEnabled();
    configureOrderPage.setFacilityCode(facilityCode);
    configureOrderPage.setOrderNumber(orderNumber);
    configureOrderPage.setApprovedQuantity(approvedQuantity);
    configureOrderPage.setProductCode(productCode);
    configureOrderPage.setProductName(productName);
    configureOrderPage.setPeriod(period);
    configureOrderPage.setOrderDate(orderData);
    configureOrderPage.clickSaveButton();
    configureOrderPage.verifySuccessMessage("Order file configuration saved successfully!");

    testWebDriver.refresh();
    configureOrderPage = configureSystemSettingsPage.navigateConfigureOrderPage();

    assertEquals(facilityCode, configureOrderPage.getFacilityCode());
    assertEquals(orderNumber, configureOrderPage.getOrderNumber());
    assertEquals(approvedQuantity, configureOrderPage.getApprovedQuantity());
    assertEquals(productCode, configureOrderPage.getProductCode());
    assertEquals(period, configureOrderPage.getPeriod());
    assertEquals(orderData, configureOrderPage.getOrderDate());
  }

  @Test(groups = {"admin"}, dataProvider = "Data-Provider-Function")
  public void testVerifyIncludeColumnHeaderONWithHeadersBlank(String user, String password) {
    String facilityCode = "";
    String orderNumber = "";
    String approvedQuantity = "";
    String productCode = "";
    String productName = "";
    String period = "";
    String orderData = "";
    String orderPrefix = "";

    HomePage homePage = loginPage.loginAs(user, password);
    ConfigureSystemSettingsPage configureSystemSettingsPage = homePage.navigateSystemSettingsScreen();
    ConfigureOrderPage configureOrderPage = configureSystemSettingsPage.navigateConfigureOrderPage();
    configureOrderPage.setOrderPrefix(orderPrefix);
    configureOrderPage.checkIncludeOrderHeader();
    configureOrderPage.verifyColumnHeadersEnabled();
    configureOrderPage.setFacilityCode(facilityCode);
    configureOrderPage.setOrderNumber(orderNumber);
    configureOrderPage.setApprovedQuantity(approvedQuantity);
    configureOrderPage.setProductCode(productCode);
    configureOrderPage.setProductName(productName);
    configureOrderPage.setPeriod(period);
    configureOrderPage.setOrderDate(orderData);
    configureOrderPage.clickSaveButton();
    configureOrderPage.verifySuccessMessage("Order file configuration saved successfully!");

    testWebDriver.refresh();
    configureOrderPage = configureSystemSettingsPage.navigateConfigureOrderPage();

    assertEquals(facilityCode, configureOrderPage.getFacilityCode());
    assertEquals(orderNumber, configureOrderPage.getOrderNumber());
    assertEquals(approvedQuantity, configureOrderPage.getApprovedQuantity());
    assertEquals(productCode, configureOrderPage.getProductCode());
    assertEquals(period, configureOrderPage.getPeriod());
    assertEquals(orderData, configureOrderPage.getOrderDate());
  }

  @Test(groups = {"admin"}, dataProvider = "Data-Provider-Function")
  public void testVerifyAllIncludeCheckBoxesUnchecked(String user, String password) {
    HomePage homePage = loginPage.loginAs(user, password);
    ConfigureSystemSettingsPage configureSystemSettingsPage = homePage.navigateSystemSettingsScreen();
    ConfigureOrderPage configureOrderPage = configureSystemSettingsPage.navigateConfigureOrderPage();
    configureOrderPage.unCheckFacilityCodeCheckBox();
    configureOrderPage.unCheckApprovedQuantityCheckBox();
    configureOrderPage.unCheckIncludeOrderHeader();
    configureOrderPage.unCheckOrderDateCheckBox();
    configureOrderPage.unCheckOrderNumberCheckBox();
    configureOrderPage.unCheckPeriodCheckBox();
    configureOrderPage.unCheckProductCodeCheckBox();
    configureOrderPage.clickSaveButton();
    configureOrderPage.verifySuccessMessage("Order file configuration saved successfully!");

    testWebDriver.refresh();
    configureOrderPage = configureSystemSettingsPage.navigateConfigureOrderPage();

    configureOrderPage.clickCancelButton();
    assertTrue("User should be redirected to home page", testWebDriver.getCurrentUrl().contains("public/pages/admin/edi/index.html#/configure-system-settings"));
  }

  @Test(groups = {"admin"}, dataProvider = "Data-Provider-Function")
  public void testVerifyAddNewButtonFunctionality(String user, String password) {
    String successMessage = "Order file configuration saved successfully!";
    HomePage homePage = loginPage.loginAs(user, password);
    ConfigureSystemSettingsPage configureSystemSettingsPage = homePage.navigateSystemSettingsScreen();
    ConfigureOrderPage configureOrderPage = configureSystemSettingsPage.navigateConfigureOrderPage();
    configureOrderPage.clickAddNewButton();
    configureOrderPage.verifyElementsOnAddNewButtonClick(7, "true", "Not applicable", "");
    configureOrderPage.clickSaveButton();
    configureOrderPage.verifySuccessMessage(successMessage);

    testWebDriver.refresh();
    configureOrderPage = configureSystemSettingsPage.navigateConfigureOrderPage();

    configureOrderPage.clickRemoveIcon(7);
    configureOrderPage.clickSaveButton();
    configureOrderPage.verifySuccessMessage(successMessage);
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

  @DataProvider(name = "Data-Provider-Function")
  public Object[][] parameterIntTestProviderPositive() {
    return new Object[][]{
      {"Admin123", "Admin123"}
    };
  }
}

