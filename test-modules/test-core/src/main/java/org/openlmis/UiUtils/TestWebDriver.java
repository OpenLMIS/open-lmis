/*
 * This program is part of the OpenLMIS logistics management information system platform software.
 * Copyright © 2013 VillageReach
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *  
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 * You should have received a copy of the GNU Affero General Public License along with this program.  If not, see http://www.gnu.org/licenses.  For additional information contact info@OpenLMIS.org. 
 */

package org.openlmis.UiUtils;

import org.openqa.selenium.*;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import static org.openqa.selenium.OutputType.BYTES;


public class TestWebDriver {

  private static WebDriver driver;
  private static String BASE_URL;
  private int DEFAULT_WAIT_TIME = 10;

  public TestWebDriver(WebDriver driver) {
    TestWebDriver.driver = driver;
    maximizeBrowser();
  }

  public void setBaseURL(String BASE_URL) {
    driver.manage().deleteAllCookies();
    TestWebDriver.BASE_URL = BASE_URL;
    get();
  }

  public WebElement findElement(By by) {
    return driver.findElement(by);
  }


  public void get() {
    driver.get(BASE_URL);
  }

  public void getUrl(String url) {
    driver.get(url);
  }

  public static WebDriver getDriver() {
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

  public void maximizeBrowser() {
    driver.manage().window().maximize();
  }


  public void refresh() {
    driver.navigate().refresh();
  }

  public void waitForPageToLoad() {
    try {
      Thread.sleep(1000);
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
    (new WebDriverWait(driver, DEFAULT_WAIT_TIME)).until(new ExpectedCondition<Boolean>() {
      public Boolean apply(WebDriver d) {
        return (((org.openqa.selenium.JavascriptExecutor) driver).executeScript("return document.readyState").equals("complete"));
      }
    });
  }


  public void handleScroll() {
    ((org.openqa.selenium.JavascriptExecutor) driver).executeScript("scroll(0,1000);");
  }

  public void handleScrollByPixels(int x, int y) {
    ((org.openqa.selenium.JavascriptExecutor) driver).executeScript("scroll(" + x + "," + y + ");");
  }

  public void waitForElementToAppear(final WebElement element) {
    (new WebDriverWait(driver, DEFAULT_WAIT_TIME)).until(new ExpectedCondition<Boolean>() {
      public Boolean apply(WebDriver d) {
        return (element.isDisplayed());
      }
    });
  }

  public void waitForAjax() {
    waitForPageToLoad();
    final WebElement loader = findElement(By.id("loader"));
    (new WebDriverWait(driver, DEFAULT_WAIT_TIME)).until(new ExpectedCondition<Boolean>() {
      public Boolean apply(WebDriver d) {
        return (!loader.isDisplayed());
      }
    });
  }

  public void waitForElementToBeEnabled(final WebElement element) {
    (new WebDriverWait(driver, DEFAULT_WAIT_TIME)).until(new ExpectedCondition<Boolean>() {
      public Boolean apply(WebDriver d) {
        return (element.isEnabled());
      }
    });
  }

  public void waitForElementsToAppear(final WebElement element, final WebElement elementSecond) {
    (new WebDriverWait(driver, DEFAULT_WAIT_TIME)).until(new ExpectedCondition<Boolean>() {
      public Boolean apply(WebDriver d) {
        return (element.isDisplayed() || elementSecond.isDisplayed());
      }
    });
  }

  public void waitForElementsToAppear(final WebElement element, final WebElement elementSecond, final WebElement elementThird) {
    (new WebDriverWait(driver, DEFAULT_WAIT_TIME)).until(new ExpectedCondition<Boolean>() {
      public Boolean apply(WebDriver d) {
        return (element.isDisplayed() || elementSecond.isDisplayed() || elementThird.isDisplayed());
      }
    });
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

  public WebElement getFirstSelectedOption(WebElement element) {
    return new Select(element).getFirstSelectedOption();
  }

  public WebElement getElementById(String Id) {
    return driver.findElement(By.id(Id));
  }

  public WebElement getElementByName(String Name) {
    return driver.findElement(By.name(Name));
  }

  public WebElement getElementByXpath(String Xpath) {
    return driver.findElement(By.xpath(Xpath));
  }

  public List<WebElement> getElementsByXpath(String Xpath) {
    return driver.findElements(By.xpath(Xpath));
  }

  public List<WebElement> getElementsByLinkText(String linkText) {
    return driver.findElements(By.linkText(linkText));
  }

  public int getElementsSizeByXpath(String Xpath) {
    return driver.findElements(By.xpath(Xpath)).size();
  }

  public String getSelectedOptionDefault(WebElement element) {
    return element.getAttribute("value");
  }

  public List<WebElement> getAllSelectedOptions(WebElement element) {
    return new Select(element).getAllSelectedOptions();
  }

  public List<WebElement> getOptions(WebElement element) {
    return new Select(element).getOptions();
  }

  public List<String> getListOfOptions(WebElement element) {
    List<String> listOfOptions = new ArrayList<>();
    for (WebElement option : new Select(element).getOptions()) {
      listOfOptions.add(option.getText());
    }
    return listOfOptions;
  }

  public List<String> getListOfOptionGroupsWithOptions(WebElement element) {
    List<String> listOfOptionGroupsWithOptions = new ArrayList<>();
    for (WebElement option : element.findElements(By.tagName("optgroup"))) {
      listOfOptionGroupsWithOptions.add(option.getAttribute("label") + "\n" + option.getText());
    }
    return listOfOptionGroupsWithOptions;
  }

  public String getText(WebElement element) {
    return element.getText();
  }

  public String getAttribute(WebElement element, String value) {
    return element.getAttribute(value);
  }


  public void sleep(long timeToSleep) {
    try {
      Thread.sleep(timeToSleep);
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
  }

  public void click(final WebElement element) {
    Actions action = new Actions(driver);
    action.click(element).perform();
  }

  public void scrollAndClick(final WebElement element) {
    //Due to chrome bug where clicking on element present in DOM but not on screen requires scrolling
    scrollToElement(element);
    click(element);
  }

  public void clickForRadio(final WebElement element) {
    element.click();
    if (!element.isSelected()) {
      Actions action = new Actions(driver);
      action.click(element).perform();
    }
  }

  public void keyPress(final WebElement element) {
    waitForElementToAppear(element);

    if (driver instanceof FirefoxDriver) {
      element.sendKeys(Keys.RETURN);
      return;
    }

    if (element != null) {
      for (int i = 0; i < 15; i++) {
        element.sendKeys(Keys.TAB);
        if (driver.switchTo().activeElement().getText().equalsIgnoreCase(element.getText())) {
          break;
        }
      }
      element.sendKeys(Keys.RETURN);
    }
  }

  public void moveToElement(WebElement element) {
    new Actions(driver).moveToElement(element).perform();
  }

  public byte[] getScreenshot() {
    return ((TakesScreenshot) driver).getScreenshotAs(BYTES);
  }

  public void scrollToElement(WebElement elementToClick) {
    // Scroll the browser to the element's Y position
    ((org.openqa.selenium.JavascriptExecutor) driver).executeScript(String.format("window.scrollTo(0, %s);", elementToClick.getLocation().getY()));
  }

  public void closeBrowser() {
    String base = driver.getWindowHandle();
    Set<String> set = driver.getWindowHandles();
    set.remove(base);

    if (set.size() >= 1) {
      driver.switchTo().window((String) set.toArray()[0]);
      driver.close();
      driver.switchTo().window(base);
    }
  }

  public void switchWindow() {
    String base = driver.getWindowHandle();
    Set<String> set = driver.getWindowHandles();
    set.remove(base);

    if (set.size() >= 1) {
      driver.switchTo().window((String) set.toArray()[0]);
    }
  }
}
