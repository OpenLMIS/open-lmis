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


import cucumber.api.java.After;
import cucumber.api.java.Before;
import cucumber.api.java.en.And;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import org.openlmis.UiUtils.CaptureScreenshotOnFailureListener;
import org.openlmis.UiUtils.TestCaseHelper;
import org.openlmis.pageobjects.ConfigureEDIPage;
import org.openlmis.pageobjects.ConfigureBudgetPage;
import org.openlmis.pageobjects.HomePage;
import org.openlmis.pageobjects.LoginPage;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;
import org.testng.annotations.*;

import static com.thoughtworks.selenium.SeleneseTestBase.assertFalse;
import static com.thoughtworks.selenium.SeleneseTestBase.assertTrue;
import static com.thoughtworks.selenium.SeleneseTestNgHelper.assertEquals;


@TransactionConfiguration(defaultRollback = true)
@Transactional

@Listeners(CaptureScreenshotOnFailureListener.class)

public class ConfigureBudgetTemplate extends TestCaseHelper {


    @BeforeMethod(groups = "admin")
    @Before
    public void setUp() throws Exception {
        super.setup();
        dbWrapper.setupBudgetFileConfiguration("false");
        dbWrapper.defaultSetupBudgetFileColumns();
    }

    @And("^I access configure budget page$")
    public void accessOrderScreen() throws Exception {
        HomePage homePage = new HomePage(testWebDriver);
        ConfigureEDIPage configureEDIPage = homePage.navigateEdiScreen();
        configureEDIPage.navigateConfigureBudgetPage();
    }

    @And("^I should see default include column headers as \"([^\"]*)\"$")
    public void verifyIncludeColumnHeader(String status) throws Exception {
        ConfigureBudgetPage configureBudgetPage = new ConfigureBudgetPage(testWebDriver);
        assertEquals(String.valueOf(configureBudgetPage.getIncludeHeader()), status);
    }

    @And("^I verify default checkbox for all data fields$")
    public void verifyDefaultDataFieldsCheckBox() throws Exception {
        ConfigureBudgetPage configureBudgetPage = new ConfigureBudgetPage(testWebDriver);
        configureBudgetPage.verifyDefaultIncludeCheckboxForAllDataFields();
    }

    @And("^I verify default value of positions$")
    public void verifyDefaultPositionValues() throws Exception {
        ConfigureBudgetPage configureBudgetPage = new ConfigureBudgetPage(testWebDriver);
        configureBudgetPage.verifyDefaultPositionValues();
    }

    @When("^I save budget file format$")
    public void clickSave() throws Exception {
        ConfigureBudgetPage configureBudgetPage = new ConfigureBudgetPage(testWebDriver);
        configureBudgetPage.clickSaveButton();
    }

    @Then("^I should see budget successfull saved message as \"([^\"]*)\"$")
    public void verifySaveSuccessfullyMessage(String message) throws Exception {
        ConfigureBudgetPage configureBudgetPage = new ConfigureBudgetPage(testWebDriver);
        configureBudgetPage.verifyMessage(message);
    }

    @Test(groups = {"admin"}, dataProvider = "Data-Provider-Function")
    public void testVerifyDefaultSelectionOfPackedDropdown(String user, String password) throws Exception {
        LoginPage loginPage = new LoginPage(testWebDriver, baseUrlGlobal);
        HomePage homePage = loginPage.loginAs(user, password);
        ConfigureEDIPage configureEDIPage = homePage.navigateEdiScreen();
        ConfigureBudgetPage configureBudgetPage = configureEDIPage.navigateConfigureBudgetPage();
        String packedDate = configureBudgetPage.getSelectedOptionOfPeriodStartDateDropDown();
        assertEquals(packedDate, "dd/MM/yy");
        }

    @Test(groups = {"admin"}, dataProvider = "Data-Provider-Function")
    public void testEditPackedDropDown(String user, String password) throws Exception {
        LoginPage loginPage = new LoginPage(testWebDriver, baseUrlGlobal);
        HomePage homePage = loginPage.loginAs(user, password);
        ConfigureEDIPage configureEDIPage = homePage.navigateEdiScreen();
        ConfigureBudgetPage configureBudgetPage = configureEDIPage.navigateConfigureBudgetPage();
        configureBudgetPage.selectValueFromPeriodStartDateDropDown("MM-dd-yyyy");
        configureBudgetPage.clickSaveButton();
        configureBudgetPage.verifyMessage("Budget file configuration saved successfully!");
        String packedDate = configureBudgetPage.getSelectedOptionOfPeriodStartDateDropDown();
        assertEquals(packedDate, "MM-dd-yyyy");
        configureBudgetPage.selectValueFromPeriodStartDateDropDown("yyyy/MM/dd");
        configureBudgetPage.clickSaveButton();
        configureBudgetPage.verifyMessage("Budget file configuration saved successfully!");
        packedDate = configureBudgetPage.getSelectedOptionOfPeriodStartDateDropDown();
        assertEquals(packedDate, "yyyy/MM/dd");
        }

    @Test(groups = {"admin"}, dataProvider = "Data-Provider-Function")
    public void testVerifyIncludeColumnHeaderONWithAllPositionsAltered(String user, String password) throws Exception {
        LoginPage loginPage = new LoginPage(testWebDriver, baseUrlGlobal);
        HomePage homePage = loginPage.loginAs(user, password);
        ConfigureEDIPage configureEDIPage = homePage.navigateEdiScreen();
        ConfigureBudgetPage configureBudgetPage = configureEDIPage.navigateConfigureBudgetPage();
        configureBudgetPage.checkIncludeHeader();
        configureBudgetPage.setAllocatedBudget("101") ;
        configureBudgetPage.setFacilityCode("1022") ;
        configureBudgetPage.checkNotesCheckBox();
        configureBudgetPage.setNotes("103");
        configureBudgetPage.setProgramCode("104");
        configureBudgetPage.checkPeriodStartDateCheckBox();
        configureBudgetPage.setPeriodStartDate("105");
        configureBudgetPage.clickSaveButton();
        configureBudgetPage.verifyMessage("Budget file configuration saved successfully!");

        assertTrue(configureBudgetPage.getIncludeHeader());
        assertEquals("101", configureBudgetPage.getAllocatedBudget());
        assertEquals("102", configureBudgetPage.getFacilityCode());
        assertEquals("103", configureBudgetPage.getNotes());
        assertEquals("104", configureBudgetPage.getProgramCode());
        assertEquals("105", configureBudgetPage.getPeriodStartDate());
        }

    @Test(groups = {"admin"}, dataProvider = "Data-Provider-Function")
    public void testVerifyIncludeColumnHeaderOFFWithMandatoryPositionsAltered(String user, String password) throws Exception {
        LoginPage loginPage = new LoginPage(testWebDriver, baseUrlGlobal);
        HomePage homePage = loginPage.loginAs(user, password);
        ConfigureEDIPage configureEDIPage = homePage.navigateEdiScreen();
        ConfigureBudgetPage configureBudgetPage = configureEDIPage.navigateConfigureBudgetPage();
        configureBudgetPage.unCheckIncludeHeader();
        configureBudgetPage.setAllocatedBudget("101") ;
        configureBudgetPage.setFacilityCode("102") ;
        configureBudgetPage.unCheckCostCheckBox();
        configureBudgetPage.setProgramCode("103");
        configureBudgetPage.unCheckPeriodStartDateCheckBox();
        configureBudgetPage.clickSaveButton();
        configureBudgetPage.verifyMessage("Budget file configuration saved successfully!");

        assertFalse(configureBudgetPage.getIncludeHeader());
        assertEquals(configureBudgetPage.getAllocatedBudget(),"101");
        assertEquals(configureBudgetPage.getFacilityCode(),"102");
        assertEquals(configureBudgetPage.getNotes(),"5");
        assertEquals(configureBudgetPage.getProgramCode(),"103");
        assertEquals(configureBudgetPage.getPeriodStartDate(),"3");

        configureBudgetPage.clickCancelButton();
        assertTrue("User should be redirected to EDI Config page", testWebDriver.getCurrentUrl().contains("public/pages/admin/edi/index.html#/configure-edi-file"));

    }

    @Test(groups = {"admin"}, dataProvider = "Data-Provider-Function")
    public void testVerifyDuplicatePosition(String user, String password) throws Exception {
        LoginPage loginPage = new LoginPage(testWebDriver, baseUrlGlobal);
        HomePage homePage = loginPage.loginAs(user, password);
        ConfigureEDIPage configureEDIPage = homePage.navigateEdiScreen();
        ConfigureBudgetPage configureBudgetPage = configureEDIPage.navigateConfigureBudgetPage();
        configureBudgetPage.setAllocatedBudget("101") ;
        configureBudgetPage.setFacilityCode("101") ;
        configureBudgetPage.clickSaveButton();
        configureBudgetPage.verifyErrorMessage("Position numbers cannot have duplicate values");

    }

    @Test(groups = {"admin"}, dataProvider = "Data-Provider-Function")
    public void testVerifyZeroPosition(String user, String password) throws Exception {
        LoginPage loginPage = new LoginPage(testWebDriver, baseUrlGlobal);
        HomePage homePage = loginPage.loginAs(user, password);
        ConfigureEDIPage configureEDIPage = homePage.navigateEdiScreen();
        ConfigureBudgetPage configureBudgetPage = configureEDIPage.navigateConfigureBudgetPage();
        configureBudgetPage.setAllocatedBudget("0") ;
        configureBudgetPage.setFacilityCode("101") ;
        configureBudgetPage.clickSaveButton();
        configureBudgetPage.verifyErrorMessage("Position number cannot be blank or zero for an included field");

    }
    @Test(groups = {"admin"}, dataProvider = "Data-Provider-Function")
    public void testVerifyBlankPosition(String user, String password) throws Exception {
        LoginPage loginPage = new LoginPage(testWebDriver, baseUrlGlobal);
        HomePage homePage = loginPage.loginAs(user, password);
        ConfigureEDIPage configureEDIPage = homePage.navigateEdiScreen();
        ConfigureBudgetPage configureBudgetPage = configureEDIPage.navigateConfigureBudgetPage();
        configureBudgetPage.setAllocatedBudget("101") ;
        configureBudgetPage.setFacilityCode("") ;
        configureBudgetPage.clickSaveButton();
        configureBudgetPage.verifyErrorMessage("Position number cannot be blank or zero for an included field");

    }
    @After
    @AfterMethod(groups = "admin")
    public void tearDown() throws Exception {
        testWebDriver.sleep(500);
        if (!testWebDriver.getElementById("username").isDisplayed()) {
            HomePage homePage = new HomePage(testWebDriver);
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

