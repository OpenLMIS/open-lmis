package org.openlmis.functional;

import org.openlmis.pageobjects.LoginPage;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.testng.annotations.*;

import static org.testng.Assert.assertTrue;

public class LogIn {
    private WebDriver driver;
    private LoginPage login;
    private String baseurl;
    private String errorMessage;


    @BeforeClass
    public void setUp() {
        driver = new FirefoxDriver();
        baseurl = "http://192.168.34.2:8080/openlmis-web/";
        login = new LoginPage(driver, baseurl);
        errorMessage = "The username or password you entered is incorrect . Please try again.";
    }

    @AfterClass
    public void quitDriver() {
        driver.quit();
    }

    /* Test method to test positive scenarios*/
    @Test(dataProvider = "Data-Provider-Function-Positive")
    public void testLoginPositive(String identifier, String[] credentials) {
        login.login(credentials[0], credentials[1]);
        assertTrue(driver.getPageSource().contains("! Welcome " + credentials[0]));
        String url = driver.getCurrentUrl().toString();
        if (identifier.equalsIgnoreCase("Admin"))
            assertTrue(url.contains(baseurl + "admin/home") );
        else
            assertTrue(url.contains(baseurl + "home") );
        login.logout();
    }

    /* Test method to test negative scenarios*/
    @Test(dependsOnMethods = {"testLoginPositive"},
            dataProvider = "Data-Provider-Function-Negative")
    public void testLoginNegative(String[] credentials) {
        login.login(credentials[0], credentials[1]);
        assertTrue(driver.getPageSource().contains(errorMessage));

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
