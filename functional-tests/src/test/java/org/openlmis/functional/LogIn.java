package org.openlmis.functional;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

public class LogIn {
    private WebDriver driver;

    @BeforeClass
    public void FirefoxWebDriver() {
        driver = new FirefoxDriver();
    }

    @AfterClass
    public void quitDriver() {
        driver.quit();
    }

    @Test
    public void testLogin() {

    }
}