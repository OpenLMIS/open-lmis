/*
 * Copyright © 2013 VillageReach.  All Rights Reserved.  This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 *
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package org.openlmis.jasmine;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxProfile;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.How;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.pagefactory.AjaxElementLocatorFactory;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import static org.hamcrest.Matchers.containsString;
import static org.junit.Assert.assertThat;

public class JasmineTest {

  @FindBy(how = How.XPATH, using = "//span[@class='finished-at']")
  @SuppressWarnings("unused")
  private static WebElement finishDate;
  private WebDriver driver;


  @FindBy(how = How.XPATH, using = "//a[@class='description']")
  @SuppressWarnings("unused")
  private static WebElement description;

  private static final int DEFAULT_WAIT_TIME = 100;


  @BeforeClass
  public void setUp() throws Exception {
    createFirefoxDriver();
    addShutDownHook();
  }


  private void createFirefoxDriver() {
    FirefoxProfile profile = new FirefoxProfile();
    profile.setPreference("signed.applets.codebase_principal_support", true);
    profile.setPreference("javascript.enabled", true);
    driver = new FirefoxDriver(profile);
    driver.manage().window().maximize();

  }

  private void addShutDownHook() {
    Runtime.getRuntime().addShutdownHook(new Thread() {
      public void run() {
        driver.close();
      }
    });
  }

  @Test
  public void runJasmineTests() {
    driver.get("file://" + baseDir() + "/src/test/javascript/SpecRunner.html");
    PageFactory.initElements(new AjaxElementLocatorFactory(driver, 10), this);
    waitForElementToAppear(finishDate);

    assertThat(description.getText(), containsString("specs, 0 failures in"));
  }

  private String baseDir() {
    String baseDir = System.getProperty("user.dir");
    if (!baseDir.endsWith("openlmis-web")) {
      baseDir = baseDir + "/modules/openlmis-web";
    }
    return baseDir;
  }

  void waitForElementToAppear(final WebElement element) {
    (new WebDriverWait(driver, DEFAULT_WAIT_TIME)).until(new ExpectedCondition<Boolean>() {
      public Boolean apply(WebDriver d) {
        return (element.isDisplayed());
      }
    });
  }

}
