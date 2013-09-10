/*
 * Copyright © 2013 VillageReach.  All Rights Reserved.  This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 *
 *  If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package org.openlmis.functional;


import cucumber.api.java.After;
import cucumber.api.java.Before;
import cucumber.api.java.en.And;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import org.openlmis.UiUtils.CaptureScreenshotOnFailureListener;
import org.openlmis.UiUtils.TestCaseHelper;
import org.openlmis.pageobjects.*;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;
import org.testng.annotations.*;

import static com.thoughtworks.selenium.SeleneseTestBase.assertFalse;
import static com.thoughtworks.selenium.SeleneseTestBase.assertTrue;
import static com.thoughtworks.selenium.SeleneseTestNgHelper.assertEquals;


@TransactionConfiguration(defaultRollback = true)
@Transactional

@Listeners(CaptureScreenshotOnFailureListener.class)

public class ConfigureShipmentTemplate extends TestCaseHelper {


    @BeforeMethod(groups = "admin")
    @Before
    public void setUp() throws Exception {
        super.setup();
        dbWrapper.setupShipmentFileConfiguration("false");
        dbWrapper.defaultSetupShipmentFileColumns();
    }

    @Given("^I have shipment file with Header In File as \"([^\"]*)\"$")
    public void setupShipmentFileConfiguration(String  status) throws Exception {
       dbWrapper.setupShipmentFileConfiguration(status);
    }

    @Given("^I have default shipment file columns$")
    public void setupShipmentFileColumns() throws Exception {
        dbWrapper.defaultSetupShipmentFileColumns();
    }

    @And("^I access configure shipment page$")
    public void accessOrderScreen() throws Exception {
        HomePage homePage = new HomePage(testWebDriver);
        ConfigureEDIPage configureEDIPage = homePage.navigateEdiScreen();
        configureEDIPage.navigateConfigureShipmentPage();
    }

    @And("^I should see include column headers as \"([^\"]*)\"$")
    public void verifyIncludeColumnHeader(String status) throws Exception {
        ConfigureShipmentPage configureShipmentPage = new ConfigureShipmentPage(testWebDriver);
        assertEquals(String.valueOf(configureShipmentPage.getIncludeHeader()), status);
    }

    @And("^I should see include checkbox for all data fields$")
    public void verifyDefaultDataFieldsCheckBox() throws Exception {
        ConfigureShipmentPage configureShipmentPage = new ConfigureShipmentPage(testWebDriver);
        configureShipmentPage.verifyDefaultIncludeCheckboxForAllDataFields();
    }

    @And("^I should see default value of positions$")
    public void verifyDefaultPositionValues() throws Exception {
        ConfigureShipmentPage configureShipmentPage = new ConfigureShipmentPage(testWebDriver);
        configureShipmentPage.verifyDefaultPositionValues();
    }

    @When("^I save shipment file format$")
    public void clickSave() throws Exception {
        ConfigureShipmentPage configureShipmentPage = new ConfigureShipmentPage(testWebDriver);
        configureShipmentPage.clickSaveButton();
    }

    @Then("^I should see successfull message \"([^\"]*)\"$")
    public void verifySaveSuccessfullyMessage(String message) throws Exception {
        ConfigureShipmentPage configureShipmentPage = new ConfigureShipmentPage(testWebDriver);
        configureShipmentPage.verifyMessage(message);
    }

    @Test(groups = {"admin"}, dataProvider = "Data-Provider-Function")
    public void testVerifyDefaultSelectionOfPackedAndShippedDateDropdown(String user, String password) throws Exception {
        LoginPage loginPage = new LoginPage(testWebDriver, baseUrlGlobal);
        HomePage homePage = loginPage.loginAs(user, password);
        ConfigureEDIPage configureEDIPage = homePage.navigateEdiScreen();
        ConfigureShipmentPage configureShipmentPage = configureEDIPage.navigateConfigureShipmentPage();
        String packedDate = configureShipmentPage.getSelectedOptionOfPackedDateDropDown();
        assertEquals(packedDate, "dd/MM/yy");
        String shippedDate = configureShipmentPage.getSelectedOptionOfShippedDateDropDown();
        assertEquals(shippedDate, "dd/MM/yy");
    }

    @Test(groups = {"admin"}, dataProvider = "Data-Provider-Function")
    public void testEditPackedAndShippedDateDropDown(String user, String password) throws Exception {
        LoginPage loginPage = new LoginPage(testWebDriver, baseUrlGlobal);
        HomePage homePage = loginPage.loginAs(user, password);
        ConfigureEDIPage configureEDIPage = homePage.navigateEdiScreen();
        ConfigureShipmentPage configureShipmentPage = configureEDIPage.navigateConfigureShipmentPage();
        configureShipmentPage.selectValueFromPackedDateDropDown("MM-dd-yyyy");
        configureShipmentPage.selectValueFromShippedDateDropDown("yyyy-MM-dd");
        configureShipmentPage.clickSaveButton();
        configureShipmentPage.verifyMessage("Shipment file configuration saved successfully!");
        String packedDate = configureShipmentPage.getSelectedOptionOfPackedDateDropDown();
        assertEquals(packedDate, "MM-dd-yyyy");
        String shippedDate = configureShipmentPage.getSelectedOptionOfShippedDateDropDown();
        assertEquals(shippedDate, "yyyy-MM-dd");
        configureShipmentPage.selectValueFromPackedDateDropDown("yyyy/MM/dd");
        configureShipmentPage.selectValueFromShippedDateDropDown("ddMMyy");
        configureShipmentPage.clickSaveButton();
        configureShipmentPage.verifyMessage("Shipment file configuration saved successfully!");
        packedDate = configureShipmentPage.getSelectedOptionOfPackedDateDropDown();
        assertEquals(packedDate, "yyyy/MM/dd");
        shippedDate = configureShipmentPage.getSelectedOptionOfShippedDateDropDown();
        assertEquals(shippedDate, "ddMMyy");
    }

    @Test(groups = {"admin"}, dataProvider = "Data-Provider-Function")
    public void testVerifyIncludeColumnHeaderONWithAllPositionsAltered(String user, String password) throws Exception {
        LoginPage loginPage = new LoginPage(testWebDriver, baseUrlGlobal);
        HomePage homePage = loginPage.loginAs(user, password);
        ConfigureEDIPage configureEDIPage = homePage.navigateEdiScreen();
        ConfigureShipmentPage configureShipmentPage = configureEDIPage.navigateConfigureShipmentPage();
        configureShipmentPage.checkIncludeHeader();
        configureShipmentPage.setQuantityShipped("101") ;
        configureShipmentPage.setOrderNumber("1022");
        configureShipmentPage.checkCostCheckBox();
        configureShipmentPage.setCost("103");
        configureShipmentPage.setProductCode("104");
        configureShipmentPage.checkPackedDateCheckBox();
        configureShipmentPage.checkShippedDateCheckBox();
        configureShipmentPage.setPackedDate("105");
        configureShipmentPage.setShippedDate("106");
        configureShipmentPage.clickSaveButton();
        configureShipmentPage.verifyMessage("Shipment file configuration saved successfully!");

        assertTrue(configureShipmentPage.getIncludeHeader());
        assertEquals("101", configureShipmentPage.getQuantityShipped());
        assertEquals("102", configureShipmentPage.getOrderNumber());
        assertEquals("103", configureShipmentPage.getCost());
        assertEquals("104", configureShipmentPage.getProductCode());
        assertEquals("105", configureShipmentPage.getPackedDate());
        assertEquals("106", configureShipmentPage.getShippedDate());
    }

    @Test(groups = {"admin"}, dataProvider = "Data-Provider-Function")
    public void testVerifyIncludeColumnHeaderOFFWithMandatoryPositionsAltered(String user, String password) throws Exception {
        LoginPage loginPage = new LoginPage(testWebDriver, baseUrlGlobal);
        HomePage homePage = loginPage.loginAs(user, password);
        ConfigureEDIPage configureEDIPage = homePage.navigateEdiScreen();
        ConfigureShipmentPage configureShipmentPage = configureEDIPage.navigateConfigureShipmentPage();
        configureShipmentPage.unCheckIncludeHeader();
        configureShipmentPage.setQuantityShipped("101") ;
        configureShipmentPage.setOrderNumber("102");
        configureShipmentPage.unCheckCostCheckBox();
        configureShipmentPage.setProductCode("103");
        configureShipmentPage.unCheckPackedDateCheckBox();
        configureShipmentPage.unCheckShippedDateCheckBox();
        configureShipmentPage.clickSaveButton();
        configureShipmentPage.verifyMessage("Shipment file configuration saved successfully!");

        assertFalse(configureShipmentPage.getIncludeHeader());
        assertEquals("101", configureShipmentPage.getQuantityShipped());
        assertEquals("102", configureShipmentPage.getOrderNumber());
        assertEquals("4", configureShipmentPage.getCost());
        assertEquals("103", configureShipmentPage.getProductCode());
        assertEquals("5", configureShipmentPage.getPackedDate());
        assertEquals("6", configureShipmentPage.getShippedDate());
    }

    @Test(groups = {"admin"}, dataProvider = "Data-Provider-Function")
    public void testVerifyDuplicatePosition(String user, String password) throws Exception {
        LoginPage loginPage = new LoginPage(testWebDriver, baseUrlGlobal);
        HomePage homePage = loginPage.loginAs(user, password);
        ConfigureEDIPage configureEDIPage = homePage.navigateEdiScreen();
        ConfigureShipmentPage configureShipmentPage = configureEDIPage.navigateConfigureShipmentPage();
        configureShipmentPage.setQuantityShipped("101") ;
        configureShipmentPage.setOrderNumber("101");
        configureShipmentPage.clickSaveButton();
        configureShipmentPage.verifyErrorMessage("Position numbers cannot have duplicate values");

    }

    @After
    @AfterMethod(groups = "admin")
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

