package org.openlmis.UiUtils;

import org.apache.commons.io.FileUtils;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.testng.ITestResult;
import org.testng.TestListenerAdapter;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class CaptureScreenshotOnFailureListener extends TestListenerAdapter {

  Date dObjnew = new Date();
  SimpleDateFormat formatternew = new SimpleDateFormat("yyyyMMdd");
  String dateFolder = formatternew.format(dObjnew);
  String screenShotsFolder = null;

  private void createDirectory() {
    String Separator = System.getProperty("file.separator");
    screenShotsFolder = System.getProperty("user.dir") + Separator+"src"+Separator+"main"+Separator+"resources"+Separator + dateFolder + Separator;
    if (!screenShotsFolder.contains("functional-tests"))
      screenShotsFolder = System.getProperty("user.dir") + Separator+"test-modules"+Separator+"functional-tests"+Separator+"src"+Separator+"main"+Separator+"resources"+Separator + dateFolder + Separator;
    if (!new File(screenShotsFolder).exists()) {
      (new File(screenShotsFolder)).mkdir();
    }

  }

  @Override
  public void onTestFailure(ITestResult testResult) {
    super.onTestFailure(testResult);

    WebDriver driver = TestWebDriver.getDriver();
    createDirectory();
    Date dObj = new Date();
    SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMdd-hhmmss");
    String time = formatter.format(dObj);
    String testMethodAndTestClass = testResult.getMethod().getMethodName() + "(" + testResult.getTestClass().getName() + ")";
    String filename = screenShotsFolder
      + testMethodAndTestClass + "-"
      + time + "-screenshot"
      + ".png";

    File scrFile = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);

    try {
      FileUtils.copyFile(scrFile, new File(filename));
    } catch (IOException e) {
      e.printStackTrace();
    }

  }

}
