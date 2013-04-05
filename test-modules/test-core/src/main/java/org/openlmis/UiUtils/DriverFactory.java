/*
 * Copyright © 2013 VillageReach.  All Rights Reserved.  This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 *
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package org.openlmis.UiUtils;


import com.gargoylesoftware.htmlunit.BrowserVersion;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxProfile;
import org.openqa.selenium.htmlunit.HtmlUnitDriver;
import org.openqa.selenium.ie.InternetExplorerDriver;
import org.openqa.selenium.remote.DesiredCapabilities;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;

import static java.lang.System.getProperty;
import static java.lang.System.setProperty;

public class DriverFactory {

  private String driverType;
  private String INPUT_ZIP_FILE_IEDRIVER = null;
  private String INPUT_ZIP_FILE_CHROMEDRIVER = null;
  private String OUTPUT_FOLDER = null;
  private String Separator = null;
  Unzip unZip;

  public WebDriver loadDriver(String browser) throws InterruptedException {
    Separator = getProperty("file.separator");
    File parentDir = new File(getProperty("user.dir")).getParentFile();
    OUTPUT_FOLDER = parentDir.getParent() + Separator + "test-core" + Separator + "src" + Separator + "main" + Separator + "java" + Separator + "org" + Separator + "openlmis" + Separator + "UiUtils" + Separator;
    INPUT_ZIP_FILE_IEDRIVER = OUTPUT_FOLDER + "IEDriverServer_x64_2.31.0.zip";
    INPUT_ZIP_FILE_CHROMEDRIVER = OUTPUT_FOLDER + "chromedriver.zip";


    return loadDriver(true, browser);
  }

  public String driverType() throws InterruptedException {
    return driverType.trim();
  }

  public WebDriver loadDriverWithJavascriptDisabledIfPossible(String browser) throws InterruptedException {
    return loadDriver(false, browser);
  }

  public void deleteExe() throws InterruptedException, IOException {
    unZip = new Unzip();
    unZip.deleteFile(OUTPUT_FOLDER + "IEDriverServer.exe");
    unZip.deleteFile(OUTPUT_FOLDER + "chromedriver.exe");
  }


  private WebDriver loadDriver(boolean enableJavascript, String browser) throws InterruptedException {
    switch (browser) {
      case "firefox":
        driverType = getProperty("web.driver", "Firefox");
        return createFirefoxDriver(enableJavascript);

      case "ie":
        unZip = new Unzip();
        unZip.unZipIt(INPUT_ZIP_FILE_IEDRIVER, OUTPUT_FOLDER);
        Thread.sleep(1500);
        driverType = setProperty("webdriver.ie.driver", OUTPUT_FOLDER + "IEDriverServer.exe");
        driverType = getProperty("webdriver.ie.driver");

        return createInternetExplorerDriver();


      case "chrome":
        unZip = new Unzip();
        unZip.unZipIt(INPUT_ZIP_FILE_CHROMEDRIVER, OUTPUT_FOLDER);
        Thread.sleep(1500);
        driverType = setProperty("webdriver.chrome.driver", OUTPUT_FOLDER + "chromedriver.exe");
        driverType = getProperty("webdriver.chrome.driver");
        return createChromeDriver();

      case "HTMLUnit":
        return new HtmlUnitDriver(BrowserVersion.INTERNET_EXPLORER_8);

      default:
        driverType = getProperty("web.driver", "Firefox");
        return createFirefoxDriver(enableJavascript);
    }
  }

  private WebDriver createFirefoxDriver(boolean enableJavascript) {
    FirefoxProfile profile = new FirefoxProfile();
    profile.setAcceptUntrustedCertificates(true);
    profile.setPreference("signed.applets.codebase_principal_support", true);
    profile.setPreference("javascript.enabled", enableJavascript);
    return new FirefoxDriver(profile);
  }

  private WebDriver createInternetExplorerDriver() {
    DesiredCapabilities ieCapabilities = DesiredCapabilities.internetExplorer();
    ieCapabilities.setCapability("ignoreZoomSetting", true);
    InternetExplorerDriver driver = new InternetExplorerDriver(ieCapabilities);
    driver.get(getProperty("baseurl", TestCaseHelper.DEFAULT_BASE_URL));
    driver.navigate().to("javascript:document.getElementById('overridelink').click()");
    return driver;
  }


  private WebDriver createChromeDriver() {
    DesiredCapabilities capabilities = DesiredCapabilities.chrome();
    capabilities.setCapability("chrome.switches", Arrays.asList("--ignore-certificate-errors"));
    return new ChromeDriver(capabilities);
  }
}
