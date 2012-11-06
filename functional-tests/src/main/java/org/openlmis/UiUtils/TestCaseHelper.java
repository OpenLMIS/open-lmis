package org.openlmis.UiUtils;


import org.apache.commons.lang.time.DateUtils;

import org.openlmis.UiUtils.DriverFactory;
import org.openlmis.UiUtils.TestWebDriver;
import org.openqa.selenium.Cookie;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebElement;
import org.testng.annotations.BeforeClass;


import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Date;


public class TestCaseHelper {


    protected static TestWebDriver testWebDriver;
    protected static boolean isSeleniumStarted = false;
    protected static DriverFactory driverFactory = new DriverFactory();


    @BeforeClass
    public static void setupSuite() throws ClassNotFoundException {
        try {
            if (!isSeleniumStarted) {
                loadDriver();
                addTearDownShutDownHook();
                isSeleniumStarted = true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    protected static void addTearDownShutDownHook() {
        Runtime.getRuntime().addShutdownHook(new Thread() {
            public void run() {
                if (testWebDriver != null) {
                    testWebDriver.close();
                }
            }
        });
    }

    protected static void loadDriver() {
        testWebDriver = new TestWebDriver(driverFactory.loadDriver());
    }


}
