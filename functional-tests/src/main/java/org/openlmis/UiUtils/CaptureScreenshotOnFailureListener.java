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

public class CaptureScreenshotOnFailureListener extends TestListenerAdapter
{

    @Override
    public void onTestFailure (ITestResult testResult)
    {
        // call the superclass
        super.onTestFailure(testResult);

        WebDriver driver = TestWebDriver.getDriver();

        // Create a calendar object so we can create a date and time for the screenshot
        Date dObj = new Date();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMdd-hhmmss");
        String time = formatter.format(dObj);
        // Get the users home path and append the screen shots folder destination
        String userDir = System.getProperty("user.dir");
        String screenShotsFolder = userDir + "/src/main/resources/";

        // The file includes the the test method and the test class
        String testMethodAndTestClass = testResult.getMethod().getMethodName() + "(" + testResult.getTestClass().getName() + ")";

        System.out.println(" *** This is where the capture file is created for the Test \n" + testMethodAndTestClass );

        // Create the filename for the screen shots
        String filename = screenShotsFolder
                + testMethodAndTestClass + "-"
                +time+"-screenshot"
                + ".png";


        // Take the screen shot and then copy the file to the screen shot folder
        File scrFile = ((TakesScreenshot)driver).getScreenshotAs(OutputType.FILE);

        try  {
            FileUtils.copyFile(scrFile, new File(filename));
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }

    } // end of onTestFailure

} // enf of class
