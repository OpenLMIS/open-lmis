package org.openlmis.functional;

import org.openlmis.pageobjects.LoginPage;
import org.testng.annotations.*;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

public class LogIn {

    private LoginPage login;

    @BeforeClass
    public void setUp() {
        login = new LoginPage();

    }

    @AfterClass
    public void quitDriver() {
        login.quitDriver();
    }

    /* Test method to test positive scenarios*/
    @Test(dataProvider = "Data-Provider-Function-Positive")
    public void testLoginPositive(String identifier, String[] credentials) {
        login.login(credentials[0], credentials[1]);
        assertEquals(login.verifyWelcomeMessage(credentials[0]),true);
        login.verifyUrl(identifier);
        login.logout();
    }

    /* Test method to test negative scenarios*/
    @Test(dependsOnMethods = {"testLoginPositive"},
            dataProvider = "Data-Provider-Function-Negative")
    public void testLoginNegative(String[] credentials) {
        login.login(credentials[0], credentials[1]);
        assertEquals(login.verifyErrorMessage(), true);

    }

    /* Data provider for supplying data to test method for positive scenarios*/
    @DataProvider(name = "Data-Provider-Function-Positive")
    public Object[][] parameterIntTestProviderPositive() {
        return new Object[][]{
                /* Positive scenarios*/
                {"Admin", new String[]{"Admin123", "Admin123"}},
                {"User", new String[]{"User123", "User123"}},
                {"User", new String[]{"USER123", "User123"}},
                {"Admin", new String[]{"ADMIN123", "Admin123"}}
        };
    }

    /* Data provider for supplying data to test method for negative scenarios*/
    @DataProvider(name = "Data-Provider-Function-Negative")
    public Object[][] parameterIntTestProviderNegative() {
        return new Object[][]{
                /* Negative scenarios*/
                {new String[]{"ADMIN123", "ADmin123"}},
                {new String[]{"ADMIN12*", "ADmin123"}},
                {new String[]{"ADMIN123", "ADmin12*"}},
                {new String[]{"USER123", "ADmin123"}},
                {new String[]{"", ""}}
        };
    }
}
