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
import java.util.Hashtable;
import java.util.Map;

import static java.lang.System.getProperty;
import static java.lang.System.setProperty;

public class DriverFactory {

  private String driverType;
  private String INPUT_ZIP_FILE_IEDRIVER = null;
  private String INPUT_ZIP_FILE_CHROMEDRIVER = null;
  private String INPUT_ZIP_FILE_CHROMEDRIVER_MAC = null;
  private String OUTPUT_FOLDER = null;
  private String Separator = null;
  Unzip unZip;

  public WebDriver loadDriver(String browser) throws InterruptedException, IOException {
    Separator = getProperty("file.separator");
    File parentDir = new File(getProperty("user.dir"));
    OUTPUT_FOLDER = parentDir.getPath() + Separator + "test-modules" + Separator + "test-core" + Separator + "src" + Separator + "main" + Separator + "java" + Separator + "org" + Separator + "openlmis" + Separator + "UiUtils" + Separator;
    INPUT_ZIP_FILE_IEDRIVER = OUTPUT_FOLDER + "IEDriverServer_Win32_2.33.0.zip";
    INPUT_ZIP_FILE_CHROMEDRIVER = OUTPUT_FOLDER + "chromedriver.zip";
    INPUT_ZIP_FILE_CHROMEDRIVER_MAC = OUTPUT_FOLDER + "chromedriver_mac.zip";

    return loadDriver(true, browser);
  }

  public String driverType() throws InterruptedException {
    return driverType.trim();
  }

  public WebDriver loadDriverWithJavascriptDisabledIfPossible(String browser) throws InterruptedException, IOException {
    return loadDriver(false, browser);
  }

  public void deleteExe() throws InterruptedException, IOException {
    unZip = new Unzip();
    unZip.deleteFile(OUTPUT_FOLDER + "IEDriverServer.exe");
    unZip.deleteFile(OUTPUT_FOLDER + "chromedriver.exe");
  }


  private WebDriver loadDriver(boolean enableJavascript, String browser) throws InterruptedException, IOException {
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

        case "chromeM":
            unZip = new Unzip();
            unZip.unZipIt(INPUT_ZIP_FILE_CHROMEDRIVER_MAC, OUTPUT_FOLDER);
            Thread.sleep(1500);
            driverType = setProperty("webdriver.chrome.driver", OUTPUT_FOLDER + "chromedriver 2");
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
    profile.setPreference("browser.helperApps.neverAsk.saveToDisk","text/csv");
    profile.setPreference("browser.download.dir",new File(System.getProperty("user.dir")).getParent());
    profile.setPreference("browser.download.folderList", 2);
    profile.setPreference("dom.storage.enabled", true);
    profile.setPreference("device.storage.enabled", true);
    //profile.setPreference("network.manage-offline-status", true);
    return new FirefoxDriver(profile);
  }

  private WebDriver createInternetExplorerDriver() throws IOException {
    Runtime.getRuntime().exec("RunDll32.exe InetCpl.cpl,ClearMyTracksByProcess 255");
    DesiredCapabilities ieCapabilities = DesiredCapabilities.internetExplorer();
    ieCapabilities.setCapability("ignoreZoomSetting", true);
    InternetExplorerDriver driver = new InternetExplorerDriver(ieCapabilities);
    return driver;
  }


  private WebDriver createChromeDriver() {
    DesiredCapabilities capabilities = DesiredCapabilities.chrome();
      Map<String, String> prefs = new Hashtable<String, String>();
      prefs.put("download.prompt_for_download", "false");
      prefs.put("download.default_directory", "C:\\Users\\openlmis\\Downloads");

    capabilities.setCapability("chrome.prefs", prefs);
    capabilities.setCapability("chrome.switches", Arrays.asList("--ignore-certificate-errors"));
    return new ChromeDriver(capabilities);
  }
}
