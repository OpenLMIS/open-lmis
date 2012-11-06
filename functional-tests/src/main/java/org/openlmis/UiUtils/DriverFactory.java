package org.openlmis.UiUtils;


import com.gargoylesoftware.htmlunit.BrowserVersion;
import com.thoughtworks.selenium.CommandProcessor;
import com.thoughtworks.selenium.HttpCommandProcessor;
import org.openqa.selenium.SeleneseCommandExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxProfile;
import org.openqa.selenium.htmlunit.HtmlUnitDriver;
import org.openqa.selenium.ie.InternetExplorerDriver;
import org.openqa.selenium.remote.CommandExecutor;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.safari.SafariDriver;

public class DriverFactory {

    private String driverType;

    public WebDriver loadDriver() {
        return loadDriver(true);
    }

    public WebDriver loadDriverWithJavascriptDisabledIfPossible() {
        return loadDriver(false);
    }

    private WebDriver loadDriver(boolean enableJavascript) {
        //driverType = System.getProperty("web.driver", "Firefox");


        driverType = System.getProperty("web.driver", "Safari");


        if (driverType.equalsIgnoreCase("Firefox")) {
            return createFirefoxDriver(enableJavascript);
        } else if (driverType.equalsIgnoreCase("IE")) {
            return new InternetExplorerDriver();
        }
        else if (driverType.equalsIgnoreCase("Safari")) {
            //return new SafariDriver();
            CommandProcessor cp = new HttpCommandProcessor("localhost", 4444,
                    "*safariproxy", "http://192.168.34.2:8080/openlmis-web/");
            CommandExecutor executor = new SeleneseCommandExecutor(cp);
            DesiredCapabilities dc = new DesiredCapabilities();
            return new RemoteWebDriver(executor, dc);
        }
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
