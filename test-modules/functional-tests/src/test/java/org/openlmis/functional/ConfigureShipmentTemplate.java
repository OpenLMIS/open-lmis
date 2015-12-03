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
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import org.openlmis.UiUtils.CaptureScreenshotOnFailureListener;
import org.openlmis.UiUtils.TestCaseHelper;
import org.openlmis.pageobjects.HomePage;
import org.openlmis.pageobjects.LoginPage;
import org.openlmis.pageobjects.PageObjectFactory;
import org.openlmis.pageobjects.edi.ConfigureShipmentPage;
import org.openlmis.pageobjects.edi.ConfigureSystemSettingsPage;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

import java.io.IOException;
import java.sql.SQLException;

import static com.thoughtworks.selenium.SeleneseTestBase.assertFalse;
import static com.thoughtworks.selenium.SeleneseTestBase.assertTrue;
import static com.thoughtworks.selenium.SeleneseTestNgHelper.assertEquals;

@Listeners(CaptureScreenshotOnFailureListener.class)
public class ConfigureShipmentTemplate extends TestCaseHelper {


  @Given("^I have shipment file with Header In File as \"([^\"]*)\"$")
  public void setupShipmentFileConfiguration(String status) throws SQLException {
    dbWrapper.setupShipmentFileConfiguration(status);
  }

  @And("^I access configure shipment page$")
  public void accessOrderScreen() {
    HomePage homePage = PageObjectFactory.getHomePage(testWebDriver);
    ConfigureSystemSettingsPage configureSystemSettingsPage = homePage.navigateSystemSettingsScreen();
    configureSystemSettingsPage.navigateConfigureShipmentPage();
  }

  @And("^I should see include column headers unchecked$")
  public void verifyIncludeColumnHeader() {
    ConfigureShipmentPage configureShipmentPage = PageObjectFactory.getConfigureShipmentPage(testWebDriver);
    assertFalse(configureShipmentPage.getIncludeHeader());
  }

  @And("^I should see include checkbox for all data fields$")
  public void verifyDefaultDataFieldsCheckBox() {
    ConfigureShipmentPage configureShipmentPage = PageObjectFactory.getConfigureShipmentPage(testWebDriver);
    configureShipmentPage.verifyDefaultIncludeCheckboxForAllDataFields();
  }

  @And("^I should see default value of positions$")
  public void verifyDefaultPositionValues() {
    ConfigureShipmentPage configureShipmentPage = PageObjectFactory.getConfigureShipmentPage(testWebDriver);
    configureShipmentPage.verifyDefaultPositionValues();
  }

  @When("^I save shipment file format$")
  public void clickSave() {
    ConfigureShipmentPage configureShipmentPage = PageObjectFactory.getConfigureShipmentPage(testWebDriver);
    configureShipmentPage.clickSaveButton();
  }

  @Then("^I should see successful message \"([^\"]*)\"$")
  public void verifySaveSuccessfullyMessage(String message) {
    ConfigureShipmentPage configureShipmentPage = PageObjectFactory.getConfigureShipmentPage(testWebDriver);
    configureShipmentPage.verifyMessage(message);
  }

  private static final String user = "Admin123";
  private static final String password = "Admin123";
  LoginPage loginPage;

  @BeforeMethod(groups = "admin")
  public void setUp() throws InterruptedException, SQLException, IOException {
    super.setup();
    dbWrapper.setupShipmentFileConfiguration("false");
    dbWrapper.removeAllExistingRights("Admin");
    dbWrapper.assignRight("Admin", "SYSTEM_SETTINGS");
    loginPage = PageObjectFactory.getLoginPage(testWebDriver, baseUrlGlobal);
  }

  @Test(groups = {"admin"})
  public void testEditPackedAndShippedDateDropDown() {
    HomePage homePage = loginPage.loginAs(user, password);
    ConfigureSystemSettingsPage configureSystemSettingsPage = homePage.navigateSystemSettingsScreen();
    ConfigureShipmentPage configureShipmentPage = configureSystemSettingsPage.navigateConfigureShipmentPage();
    configureShipmentPage.selectValueFromPackedDateDropDown("MM-dd-yyyy");
    configureShipmentPage.selectValueFromShippedDateDropDown("yyyy-MM-dd");
    configureShipmentPage.clickSaveButton();
    configureShipmentPage.verifyMessage("Shipment file configuration saved successfully!");
    testWebDriver.refresh();
    configureSystemSettingsPage.navigateConfigureShipmentPage();
    assertEquals(configureShipmentPage.getSelectedOptionOfPackedDateDropDown(), "MM-dd-yyyy");
    assertEquals(configureShipmentPage.getSelectedOptionOfShippedDateDropDown(), "yyyy-MM-dd");

    configureShipmentPage.selectValueFromPackedDateDropDown("yyyy/MM/dd");
    configureShipmentPage.selectValueFromShippedDateDropDown("ddMMyy");
    configureShipmentPage.clickSaveButton();
    configureShipmentPage.verifyMessage("Shipment file configuration saved successfully!");
    testWebDriver.refresh();
    configureSystemSettingsPage.navigateConfigureShipmentPage();
    assertEquals(configureShipmentPage.getSelectedOptionOfPackedDateDropDown(), "yyyy/MM/dd");
    assertEquals(configureShipmentPage.getSelectedOptionOfShippedDateDropDown(), "ddMMyy");
  }

  @Test(groups = {"admin"})
  public void testVerifyIncludeColumnHeaderONWithAllPositionsAltered() {
    HomePage homePage = loginPage.loginAs(user, password);
    ConfigureSystemSettingsPage configureSystemSettingsPage = homePage.navigateSystemSettingsScreen();
    ConfigureShipmentPage configureShipmentPage = configureSystemSettingsPage.navigateConfigureShipmentPage();
    configureShipmentPage.checkIncludeHeader();
    configureShipmentPage.setQuantityShipped("101");
    configureShipmentPage.setOrderNumber("1022");
    configureShipmentPage.checkCostCheckBox();
    configureShipmentPage.setCost("103");
    configureShipmentPage.setProductCode("104");
    configureShipmentPage.checkPackedDateCheckBox();
    configureShipmentPage.checkShippedDateCheckBox();
    configureShipmentPage.setPackedDate("105");
    configureShipmentPage.setShippedDate("106");
    configureShipmentPage.checkReplacedProductCodeCheckBox();
    configureShipmentPage.setReplacedProductCode("7");
    configureShipmentPage.clickSaveButton();
    configureShipmentPage.verifyMessage("Shipment file configuration saved successfully!");

    testWebDriver.refresh();
    configureSystemSettingsPage.navigateConfigureShipmentPage();

    assertTrue(configureShipmentPage.getIncludeHeader());
    assertEquals("101", configureShipmentPage.getQuantityShipped());
    assertEquals("102", configureShipmentPage.getOrderNumber());
    assertEquals("103", configureShipmentPage.getCost());
    assertEquals("104", configureShipmentPage.getProductCode());
    assertEquals("105", configureShipmentPage.getPackedDate());
    assertEquals("106", configureShipmentPage.getShippedDate());
    assertEquals("7", configureShipmentPage.getReplacedProductCode());

    setDefaultPositionValues();
  }

  @Test(groups = {"admin"})
  public void testVerifyIncludeColumnHeaderOFFWithMandatoryPositionsAltered() {
    HomePage homePage = loginPage.loginAs(user, password);
    ConfigureSystemSettingsPage configureSystemSettingsPage = homePage.navigateSystemSettingsScreen();
    ConfigureShipmentPage configureShipmentPage = configureSystemSettingsPage.navigateConfigureShipmentPage();
    configureShipmentPage.unCheckIncludeHeader();
    configureShipmentPage.setQuantityShipped("101");
    configureShipmentPage.setOrderNumber("102");
    configureShipmentPage.unCheckCostCheckBox();
    configureShipmentPage.setProductCode("103");
    configureShipmentPage.unCheckPackedDateCheckBox();
    configureShipmentPage.unCheckShippedDateCheckBox();
    configureShipmentPage.clickSaveButton();
    configureShipmentPage.verifyMessage("Shipment file configuration saved successfully!");
    testWebDriver.refresh();
    configureSystemSettingsPage.navigateConfigureShipmentPage();

    assertFalse(configureShipmentPage.getIncludeHeader());
    assertEquals("101", configureShipmentPage.getQuantityShipped());
    assertEquals("102", configureShipmentPage.getOrderNumber());
    assertEquals("4", configureShipmentPage.getCost());
    assertEquals("103", configureShipmentPage.getProductCode());
    assertEquals("5", configureShipmentPage.getPackedDate());
    assertEquals("6", configureShipmentPage.getShippedDate());
    setDefaultPositionValues();
    configureSystemSettingsPage.navigateConfigureShipmentPage();
    configureShipmentPage.clickCancelButton();
    assertTrue("User should be redirected to home page", testWebDriver.getCurrentUrl().contains("public/pages/admin/edi/index.html#/configure-system-settings"));
  }

  @Test(groups = {"admin"})
  public void testVerifyInvalidPosition() {
    HomePage homePage = loginPage.loginAs(user, password);
    ConfigureSystemSettingsPage configureSystemSettingsPage = homePage.navigateSystemSettingsScreen();
    ConfigureShipmentPage configureShipmentPage = configureSystemSettingsPage.navigateConfigureShipmentPage();
    configureShipmentPage.checkReplacedProductCodeCheckBox();
    configureShipmentPage.checkShippedDateCheckBox();
    configureShipmentPage.setQuantityShipped("101");
    configureShipmentPage.setShippedDate("6");
    configureShipmentPage.setReplacedProductCode("6");
    configureShipmentPage.clickSaveButton();
    configureShipmentPage.verifyErrorMessage("Position numbers cannot have duplicate values");

    configureShipmentPage.setQuantityShipped("0");
    configureShipmentPage.setOrderNumber("101");
    configureShipmentPage.clickSaveButton();
    configureShipmentPage.verifyErrorMessage("Position number cannot be blank or zero for an included field");

    configureShipmentPage.setQuantityShipped("101");
    configureShipmentPage.setOrderNumber("");
    configureShipmentPage.clickSaveButton();
    configureShipmentPage.verifyErrorMessage("Position number cannot be blank or zero for an included field");
  }

  private void setDefaultPositionValues() {
    ConfigureShipmentPage configureShipmentPage = PageObjectFactory.getConfigureShipmentPage(testWebDriver);
    configureShipmentPage.unCheckIncludeHeader();
    configureShipmentPage.unCheckCostCheckBox();
    configureShipmentPage.unCheckPackedDateCheckBox();
    configureShipmentPage.unCheckShippedDateCheckBox();
    configureShipmentPage.unCheckReplacedProductCode();

    configureShipmentPage.setOrderNumber("1");
    configureShipmentPage.setProductCode("2");
    configureShipmentPage.setQuantityShipped("3");
    configureShipmentPage.setCost("4");
    configureShipmentPage.setPackedDate("5");
    configureShipmentPage.setShippedDate("6");
    configureShipmentPage.setReplacedProductCode("7");
    configureShipmentPage.selectValueFromPackedDateDropDown("dd/MM/yy");
    configureShipmentPage.selectValueFromShippedDateDropDown("dd/MM/yy");
    configureShipmentPage.clickSaveButton();
  }

  @AfterMethod(groups = "admin")
  public void tearDown() throws SQLException {
    dbWrapper.removeAllExistingRights("Admin");
    dbWrapper.insertAllAdminRightsAsSeedData();
    if (!testWebDriver.getElementById("username").isDisplayed()) {
      HomePage homePage = PageObjectFactory.getHomePage(testWebDriver);
      homePage.logout(baseUrlGlobal);
      dbWrapper.deleteData();
      dbWrapper.closeConnection();
    }
  }
}

