/*
 * This program is part of the OpenLMIS logistics management information system platform software.
 * Copyright © 2013 VillageReach
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *  
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 * You should have received a copy of the GNU Affero General Public License along with this program.  If not, see http://www.gnu.org/licenses.  For additional information contact info@OpenLMIS.org. 
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
