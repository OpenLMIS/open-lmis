package org.openlmis.UiUtils;


import java.io.*;


public class TestCaseHelper {


    protected static TestWebDriver testWebDriver;
    protected static boolean isSeleniumStarted = false;
    protected static DriverFactory driverFactory = new DriverFactory();



    public  void setupSuite(String browser) throws ClassNotFoundException {
        try {
            if (!isSeleniumStarted) {
                loadDriver(browser);
                addTearDownShutDownHook();
                isSeleniumStarted = true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void tearDownSuite()
    {
      try{
        deleteExe();
      }catch(Exception e)
      {
        e.printStackTrace();
      }
    }


    protected  void addTearDownShutDownHook() {
        Runtime.getRuntime().addShutdownHook(new Thread() {
            public void run() {
                if (testWebDriver != null) {
                   // testWebDriver.close();
                  testWebDriver.quitDriver();
                  tearDownSuite();
                }
            }
        });
    }


    protected  void loadDriver(String browser) throws InterruptedException {
        testWebDriver = new TestWebDriver(driverFactory.loadDriver(browser));
    }

    protected  void deleteExe() throws InterruptedException, IOException {
        driverFactory.deleteExeDF();
    }




}
