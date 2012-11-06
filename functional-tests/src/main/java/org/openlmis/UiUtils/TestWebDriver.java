package org.openlmis.UiUtils;

import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.util.concurrent.TimeUnit;

import static org.testng.Assert.assertTrue;


public class TestWebDriver {

    private WebDriver driver;
    private String BASE_URL = "http://192.168.34.2:8080/openlmis-web/";
    private String ERROR_MESSAGE_LOGIN = "The username or password you entered is incorrect . Please try again.";
    private int DEFAULT_WAIT_TIME = 30;

    protected WebElement webElement;

    public TestWebDriver(WebDriver driver)  {

        this.driver=driver;
    }

    public void verifyUrl(String identifier) {
        String url = getCurrentUrl();
        if (identifier.equalsIgnoreCase("Admin"))
            assertTrue(url.contains(BASE_URL + "admin/home"));
        else
            assertTrue(url.contains(BASE_URL + "home"));

    }

    public void get(Object obj) {
        driver.get(BASE_URL);
    }

    public WebDriver getDriver()
    {
        return driver;
    }

    public String getCurrentUrl() {
        return driver.getCurrentUrl().toString();
    }


    public String getPageSource() {
        return driver.getPageSource();
    }



    private void setImplicitWait(int defaultTimeToWait) {
        driver.manage().timeouts().implicitlyWait(defaultTimeToWait, TimeUnit.SECONDS);
    }


    public void quitDriver() {
        driver.quit();
    }


    public void close() {
        driver.close();
    }

    public void waitForElementToAppear(final WebElement element) {
        (new WebDriverWait(driver, DEFAULT_WAIT_TIME)).until(new ExpectedCondition<Boolean>() {
            public Boolean apply(WebDriver d) {
                return (element.isDisplayed());
            }
        });
    }

    public void waitForTextToAppear(final String textToWaitFor) {
        (new WebDriverWait(driver, DEFAULT_WAIT_TIME)).until(new ExpectedCondition<Boolean>() {
            public Boolean apply(WebDriver d) {
                return (getPageSource().contains(textToWaitFor));
            }
        });
    }

    public boolean verifyErrorMessage() {
        waitForTextToAppear(ERROR_MESSAGE_LOGIN);
        return getPageSource().contains(ERROR_MESSAGE_LOGIN);
    }


}
