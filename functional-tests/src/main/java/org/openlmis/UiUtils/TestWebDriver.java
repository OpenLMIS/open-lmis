package org.openlmis.UiUtils;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.Select;
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
        sleep(2000);
        String url = getCurrentUrl();
        if (identifier.equalsIgnoreCase("Admin"))
            assertTrue(url.contains(BASE_URL + "public/pages/admin/index.html"));
        else
            assertTrue(url.contains(BASE_URL + "public/pages/logistics/rnr/create.html#/init-rnr"));

    }



    public void verifyUrlInvalid() {
        String url = getCurrentUrl();
        assertTrue(url.contains(BASE_URL + "public/pages/login.html?error=true"));
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

    public void selectByVisibleText(WebElement element, String visibleText) {
        new Select(element).selectByVisibleText(visibleText);
    }

    public void selectByValue(WebElement element, String value) {
        new Select(element).selectByValue(value);
    }

    public void selectByIndex(WebElement element, int index) {
        new Select(element).selectByIndex(index);
    }



    public String getText(WebElement element) {
       return element.getText();
    }

    public String getValue(WebElement element, String value) {
        return element.getAttribute(value);
    }

    public void sleep(long timeToSleep) {
        try{
            Thread.sleep(timeToSleep);
        }catch(InterruptedException e)
        {
            e.printStackTrace();
        }
    }

    public void click(final WebElement element)
    {
        Actions action = new Actions(driver);
        action.click(element).perform();
    }

    public boolean mouseOver(final WebElement element) {
        boolean flag= false;
        waitForElementToAppear(element);
        sleep(1500);
        if (element!=null) {
            Actions builder = new Actions(driver);
            builder.moveToElement(element).perform();
            flag = true;
            return flag;
        } else
            flag = false;
            return flag;
    }

}
