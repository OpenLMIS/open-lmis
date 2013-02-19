package org.openlmis.UiUtils;


import org.testng.annotations.BeforeClass;


public class TestCaseHelper {


    protected static TestWebDriver testWebDriver;
    protected static boolean isSeleniumStarted = false;
    protected static DriverFactory driverFactory = new DriverFactory();



    public  void setupSuite() throws ClassNotFoundException {
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


    protected  void addTearDownShutDownHook() {
        Runtime.getRuntime().addShutdownHook(new Thread() {
            public void run() {
                if (testWebDriver != null) {
                    testWebDriver.close();
                }
            }
        });
    }


    protected  void loadDriver() {
        testWebDriver = new TestWebDriver(driverFactory.loadDriver());
    }




}
