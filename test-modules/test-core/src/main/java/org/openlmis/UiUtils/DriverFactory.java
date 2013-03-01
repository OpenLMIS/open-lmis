package org.openlmis.UiUtils;


import com.gargoylesoftware.htmlunit.BrowserVersion;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxProfile;
import org.openqa.selenium.htmlunit.HtmlUnitDriver;
import org.openqa.selenium.ie.InternetExplorerDriver;


public class DriverFactory {

    private String driverType;

    public WebDriver loadDriver(String browser) {
        return loadDriver(true, browser);
    }

    public WebDriver loadDriverWithJavascriptDisabledIfPossible(String browser) {
        return loadDriver(false, browser);
    }

    private WebDriver loadDriver(boolean enableJavascript, String browser) {

        switch (browser) {
            case "firefox":
                driverType = System.getProperty("web.driver", "Firefox");
                return createFirefoxDriver(enableJavascript);
            case "ie":
                driverType = System.setProperty("webdriver.ie.driver", "C:/Program Files (x86)/IEDriver/IEDriverServer.exe");
                driverType = System.getProperty("webdriver.ie.driver");
                return new InternetExplorerDriver();


            case "Chrome":

                driverType = System.setProperty("webdriver.chrome.driver", "<path_to_chromedriver i.e.> /Users/Manjot/Downloads/chromedriver");
                driverType = System.getProperty("webdriver.chrome.driver");


            case "HTMLUnit":

                return new HtmlUnitDriver(BrowserVersion.INTERNET_EXPLORER_8);

            default:
                throw new RuntimeException(String.format("Driver %sNot Supported", driverType));
        }
    }

    private WebDriver createFirefoxDriver(boolean enableJavascript) {
        FirefoxProfile profile = new FirefoxProfile();
        profile.setPreference("signed.applets.codebase_principal_support", true);
        profile.setPreference("javascript.enabled", enableJavascript);
        return new FirefoxDriver(profile);
    }
}
