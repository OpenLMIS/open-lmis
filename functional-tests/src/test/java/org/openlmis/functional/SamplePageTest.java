package org.openlmis.functional;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import static org.testng.Assert.assertTrue;

public class SamplePageTest {
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
        driver.get("http://localhost:9090/openlmis-web/spring_security_login");
        assertTrue(driver.getCurrentUrl().contains("spring_security_login"));
    }
}