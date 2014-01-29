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

import org.apache.commons.io.FileUtils;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.testng.ITestResult;
import org.testng.Reporter;
import org.testng.TestListenerAdapter;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class CaptureScreenshotOnFailureListener extends TestListenerAdapter {

  Date dObjNew = new Date();
  SimpleDateFormat formatterNew = new SimpleDateFormat("yyyyMMdd");
  String dateFolder = formatterNew.format(dObjNew);
  String screenShotsFolder = null;

  private void createDirectory() {
    String Separator = System.getProperty("file.separator");
    File parentDir = new File(System.getProperty("user.dir"));
    screenShotsFolder = parentDir.getParent() + Separator + "src" + Separator + "main" + Separator + "resources" + Separator + dateFolder + Separator;
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
      Reporter.log(filename, true);
    } catch (IOException e) {
      e.printStackTrace();
    }

  }

}
