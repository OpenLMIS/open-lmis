package org.openlmis.functional;

import org.openlmis.UiUtils.TestCaseHelper;
import org.openlmis.pageobjects.*;
import org.testng.annotations.*;

import static org.testng.Assert.assertEquals;

public class LogIn extends TestCaseHelper {

    @Test(dataProvider = "Data-Provider-Function-Positive")
    public void testLoginPositive(String identifier, String[] credentials) {
        LoginPage loginpage=new LoginPage(testWebDriver);
        loginpage.login(credentials[0], credentials[1]);
        //assertEquals(loginpage.verifyWelcomeMessage(credentials[0]), true);
        testWebDriver.verifyUrl(identifier);
        loginpage.logout();
    }

    @Test(dependsOnMethods = {"testLoginPositive"},
            dataProvider = "Data-Provider-Function-Negative")
    public void testLoginNegative(String[] credentials) {
        LoginPage loginpage=new LoginPage(testWebDriver);
        loginpage.login(credentials[0], credentials[1]);
        //assertEquals(testWebDriver.verifyErrorMessage(), true);
        testWebDriver.verifyUrlInvalid();

    }

    @DataProvider(name = "Data-Provider-Function-Positive")
    public Object[][] parameterIntTestProviderPositive() {
        return new Object[][]{
                {"Admin", new String[]{"Admin123", "Admin123"}},
                {"User", new String[]{"User123", "User123"}},
                {"User", new String[]{"USER123", "User123"}},
                {"Admin", new String[]{"ADMIN123", "Admin123"}}
        };
    }

    @DataProvider(name = "Data-Provider-Function-Negative")
    public Object[][] parameterIntTestProviderNegative() {
        return new Object[][]{
                {new String[]{"ADMIN123", "ADmin123"}},
                {new String[]{"ADMIN12*", "ADmin123"}},
                {new String[]{"ADMIN123", "ADmin12*"}},
                {new String[]{"USER123", "ADmin123"}},
                {new String[]{"", ""}}
        };
    }
}
