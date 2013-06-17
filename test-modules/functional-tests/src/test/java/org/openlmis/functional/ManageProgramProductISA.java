/*
 * Copyright © 2013 VillageReach.  All Rights Reserved.  This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 *
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package org.openlmis.functional;


import org.openlmis.UiUtils.CaptureScreenshotOnFailureListener;
import org.openlmis.UiUtils.TestCaseHelper;
import org.openlmis.pageobjects.ProgramProductISAPage;
import org.openlmis.pageobjects.HomePage;
import org.openlmis.pageobjects.LoginPage;
import org.openqa.selenium.WebElement;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;
import org.testng.annotations.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static com.thoughtworks.selenium.SeleneseTestBase.*;


@TransactionConfiguration(defaultRollback = true)
@Transactional

@Listeners(CaptureScreenshotOnFailureListener.class)

public class ManageProgramProductISA extends TestCaseHelper {

    @BeforeMethod(groups = {"functional2"})
    public void setUp() throws Exception {
        super.setup();
        setupProgramProductTestData("P1", "P2", "VACCINES");
    }


    @Test(groups = {"functional2"}, dataProvider = "Data-Provider-Function")
    public void testMinimumProgramProductISA(String userSIC, String password, String program) throws Exception {
        ProgramProductISAPage programProductISAPage = navigateProgramProductISAPage(userSIC, password, program);
        programProductISAPage.fillProgramProductISA("1", "2", "3", "4", "5", "10");
        String actualISA = programProductISAPage.fillPopulation("1");
        String expectedISA = calculateISA("1", "2", "3", "4", "5", "10", "1");
        assertEquals(actualISA, expectedISA);
        programProductISAPage.cancelISA();
        HomePage homePage = new HomePage(testWebDriver);
        homePage.navigateHomePage();
    }

    @Test(groups = {"functional2"}, dataProvider = "Data-Provider-Function")
    public void testProgramProductISA(String userSIC, String password, String program) throws Exception {
        ProgramProductISAPage programProductISAPage = navigateProgramProductISAPage(userSIC, password, program);
        programProductISAPage.fillProgramProductISA("1", "2", "3", "4", "50", "10");
        String actualISA = programProductISAPage.fillPopulation("1");
        String expectedISA = calculateISA("1", "2", "3", "4", "50", "10", "1");
        assertEquals(actualISA, expectedISA);
        programProductISAPage.cancelISA();
        HomePage homePage = new HomePage(testWebDriver);
        homePage.navigateHomePage();
    }

    @Test(groups = {"functional2"}, dataProvider = "Data-Provider-Function")
    public void testISAFormula(String userSIC, String password, String program) throws Exception {
        ProgramProductISAPage programProductISAPage = navigateProgramProductISAPage(userSIC, password, program);
        programProductISAPage.fillProgramProductISA("12345678", "2", "3", "4", "50", "10");
        String actualISA = programProductISAPage.fillPopulation("1");
        programProductISAPage.saveISA();
        programProductISAPage.verifySuccessMessageDiv();
        HomePage homePage = new HomePage(testWebDriver);
        homePage.navigateHomePage();
    }

    private ProgramProductISAPage navigateProgramProductISAPage(String userSIC, String password, String program) throws IOException {
        LoginPage loginPage = new LoginPage(testWebDriver, baseUrlGlobal);
        HomePage homePage = loginPage.loginAs(userSIC, password);
        ProgramProductISAPage programProductISAPage = homePage.navigateProgramProductISA();
        programProductISAPage.selectProgram(program);
        programProductISAPage.editFormula();
        return programProductISAPage;
    }

    public String calculateISA(String ratio, String dosesPerYear, String wastage, String bufferPercentage, String adjustmentValue, String minimumValue, String population) {
        Float calculatedISA = Integer.parseInt(population) * Float.parseFloat(ratio) * Float.parseFloat(dosesPerYear) * Float.parseFloat(wastage) / 12 * Float.parseFloat(bufferPercentage) + Float.parseFloat(adjustmentValue);
        if (calculatedISA < Float.parseFloat(minimumValue))
            return (minimumValue);
        else
            return (Float.toString(calculatedISA));
    }

    @AfterMethod(groups = {"functional2"})
    public void tearDown() throws Exception {
        HomePage homePage = new HomePage(testWebDriver);
        homePage.logout(baseUrlGlobal);
        dbWrapper.deleteData();
        dbWrapper.closeConnection();
    }


    @DataProvider(name = "Data-Provider-Function")
    public Object[][] parameterIntTestProviderPositive() {
        return new Object[][]{
                {"Admin123", "Admin123", "VACCINES"}
        };

    }
}

