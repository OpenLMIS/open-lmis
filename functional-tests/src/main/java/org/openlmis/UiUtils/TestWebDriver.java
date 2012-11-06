package org.openlmis.UiUtils;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.util.concurrent.TimeUnit;

import static org.testng.Assert.assertTrue;


public class TestWebDriver {

    private WebDriver driver;
    private String BASE_URL;
    private String ERROR_MESSAGE_LOGIN;
    private int DEFAULT_WAIT_TIME = 30;


    public TestWebDriver(WebDriver driver)  {

        this.driver=driver;
    }

    public void setBaseURL(String BASE_URL)
    {
        this.BASE_URL=BASE_URL;
        get();
    }

    public void setErrorMessage(String ERROR_MESSAGE_LOGIN)
    {
        this.ERROR_MESSAGE_LOGIN=ERROR_MESSAGE_LOGIN;
    }

    public void verifyUrl(String identifier) {
        String url = getCurrentUrl();
        if (identifier.equalsIgnoreCase("Admin"))
            assertTrue(url.contains(BASE_URL + "resources/pages/admin/index.html"));
        else
            assertTrue(url.contains(BASE_URL + "resources/pages/logistics/index.html"));

    }


    public void get() {
        driver.get(BASE_URL);
    }

    public WebDriver getDriver()
    {
        return driver;
    }

    public String getCurrentUrl() {
        return driver.getCurrentUrl();
    }


    public String getPageSource() {
        return driver.getPageSource();
    }

    public void setImplicitWait(int defaultTimeToWait) {
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
