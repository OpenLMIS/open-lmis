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

    public WebDriver loadDriver() {
        return loadDriver(true);
    }

    public WebDriver loadDriverWithJavascriptDisabledIfPossible() {
        return loadDriver(false);
    }

    private WebDriver loadDriver(boolean enableJavascript) {
        /*
        For Firefox
         */
        driverType = System.getProperty("web.driver", "Firefox");
        /*
        For Chrome
         */
        //driverType=System.setProperty("webdriver.chrome.driver", "<path_to_chromedriver i.e.> /Users/Manjot/Downloads/chromedriver");
        //driverType = System.getProperty("webdriver.chrome.driver");

        if (driverType.equalsIgnoreCase("Firefox")) {
            return createFirefoxDriver(enableJavascript);
        }
//        else if (driverType.equals("<path_to_chromedriver> /Users/Manjot/Downloads/chromedriver"))
//        {
//                return new ChromeDriver();
//        }
      else if (driverType.equalsIgnoreCase("HTMLUnit")) {
            if (!enableJavascript) {
            }
            return new HtmlUnitDriver(BrowserVersion.INTERNET_EXPLORER_8);
        } else {
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
