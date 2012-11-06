package org.openlmis.functional;

import org.openlmis.UiUtils.DriverFactory;
import org.openlmis.UiUtils.TestCaseHelper;
import org.openlmis.pageobjects.*;
import org.openqa.selenium.support.PageFactory;
import org.testng.annotations.*;

import static org.testng.Assert.assertEquals;

public class LogIn extends TestCaseHelper {

//    private DriverFactory driverFactory= new DriverFactory();


//    @BeforeClass
//    public void setUp() {
//        driverFactory.loadDriver();
//    }
//
//    @AfterClass
//    public void quitDriver() {
//        testWebDriver.quitDriver();
//    }

    /* Test method for positive scenarios*/
    @Test(dataProvider = "Data-Provider-Function-Positive")
    public void testLoginPositive(String identifier, String[] credentials) {
        testWebDriver.get(new LogIn());
        LoginPage loginpage=new LoginPage(testWebDriver);
        loginpage.login(credentials[0], credentials[1]);
        assertEquals(loginpage.verifyWelcomeMessage(credentials[0]), true);
        testWebDriver.verifyUrl(identifier);
        loginpage.logout();
    }

    /* Test method for negative scenarios*/
    @Test(dependsOnMethods = {"testLoginPositive"},
            dataProvider = "Data-Provider-Function-Negative")
    public void testLoginNegative(String[] credentials) {
        LoginPage loginpage=new LoginPage(testWebDriver);
        loginpage.login(credentials[0], credentials[1]);
        assertEquals(testWebDriver.verifyErrorMessage(), true);

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
