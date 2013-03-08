package org.openlmis.UiUtils;


import com.gargoylesoftware.htmlunit.BrowserVersion;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxProfile;
import org.openqa.selenium.htmlunit.HtmlUnitDriver;
import org.openqa.selenium.ie.InternetExplorerDriver;
import java.io.*;

public class DriverFactory {

  private String driverType;
  private String INPUT_ZIP_FILE = null;
  private String OUTPUT_FOLDER = null;
  private  String Separator = null;
    Unzip unZip;

  public WebDriver loadDriver(String browser) throws InterruptedException {
    Separator = System.getProperty("file.separator");
    INPUT_ZIP_FILE = System.getProperty("user.dir") +Separator+"test-modules"+Separator+"test-core"+ Separator+"src"+Separator+"main"+Separator+"java"+Separator + "org" + Separator+"openlmis"+Separator+"UiUtils"+Separator+"IEDriverServer_x64_2.31.0.zip";
    OUTPUT_FOLDER = System.getProperty("user.dir") +Separator+"test-modules"+Separator+"test-core"+ Separator+"src"+Separator+"main"+Separator+"java"+Separator + "org" + Separator+"openlmis"+Separator+"UiUtils"+Separator;

    return loadDriver(true, browser);
  }

  public WebDriver loadDriverWithJavascriptDisabledIfPossible(String browser) throws InterruptedException{
    return loadDriver(false, browser);
  }

    public void deleteExeDF() throws InterruptedException, IOException{
        if (System.getProperty("os.name").startsWith("Windows") && driverType.trim().startsWith("Firefox")) {
            Runtime.getRuntime().exec("taskkill /F /IM firefox.exe");
        }
        else if (System.getProperty("os.name").startsWith("Windows") && driverType.trim().contains("IEDriverServer")){
            Runtime.getRuntime().exec("taskkill /F /IM IEDriverServer.exe");
            Runtime.getRuntime().exec("taskkill /F /IM iexplore.exe");
            unZip = new Unzip();
            unZip.deleteFile(OUTPUT_FOLDER+ "IEDriverServer.exe");
        }
        else {
            Runtime.getRuntime().exec("killall -9 firefox-bin");
        }
    }


  private WebDriver loadDriver(boolean enableJavascript, String browser) throws InterruptedException {

    switch (browser) {
      case "firefox":
        driverType = System.getProperty("web.driver", "Firefox");
        return createFirefoxDriver(enableJavascript);

      case "ie":
          unZip=new Unzip();
          unZip.unZipIt(INPUT_ZIP_FILE,OUTPUT_FOLDER);
        Thread.sleep(1500);
        driverType = System.setProperty("webdriver.ie.driver", OUTPUT_FOLDER+"IEDriverServer.exe");
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
