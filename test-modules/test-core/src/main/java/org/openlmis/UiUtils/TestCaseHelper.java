package org.openlmis.UiUtils;


import java.io.*;


public class TestCaseHelper {


    protected static TestWebDriver testWebDriver;
    protected static boolean isSeleniumStarted = false;
    protected static DriverFactory driverFactory = new DriverFactory();


    public void setupSuite(String browser) throws ClassNotFoundException {
        try {
            loadDriver(browser);
            addTearDownShutDownHook();
            isSeleniumStarted = true;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void tearDownSuite() {
        try {
           if (System.getProperty("os.name").startsWith("Windows") && driverFactory.driverType().contains("IEDriverServer")) {
                Runtime.getRuntime().exec("taskkill /F /IM IEDriverServer.exe");
                Runtime.getRuntime().exec("taskkill /F /IM iexplore.exe");
            } else {
                testWebDriver.quitDriver();
            }
            driverFactory.deleteExe();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    protected void addTearDownShutDownHook() {
        Runtime.getRuntime().addShutdownHook(new Thread() {
            public void run() {
                if (testWebDriver != null) {
                    tearDownSuite();
                }
            }
        });
    }


    protected void loadDriver(String browser) throws InterruptedException {
        testWebDriver = new TestWebDriver(driverFactory.loadDriver(browser));
    }

}
