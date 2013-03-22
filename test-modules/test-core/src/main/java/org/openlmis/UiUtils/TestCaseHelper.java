/*
 * Copyright © 2013 VillageReach.  All Rights Reserved.  This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 *
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package org.openlmis.UiUtils;


import java.io.IOException;
import java.sql.SQLException;

import static java.lang.System.getProperty;

public class TestCaseHelper {

  protected DBWrapper dbWrapper;
  protected String baseUrlGlobal, dburlGlobal;

  protected static TestWebDriver testWebDriver;
  protected static boolean isSeleniumStarted = false;
  protected static DriverFactory driverFactory = new DriverFactory();
  public static final String DEFAULT_BROWSER = "firefox";
  public static final String DEFAULT_BASE_URL = "http://localhost:9091/";
  public static final String DEFAULT_DB_URL = "jdbc:postgresql://localhost:5432/open_lmis";


  public void setup() throws Exception {
    String browser = getProperty("browser", DEFAULT_BROWSER);
    baseUrlGlobal = getProperty("baseurl", DEFAULT_BASE_URL);
    dburlGlobal = getProperty("dburl", DEFAULT_DB_URL);


    dbWrapper = new DBWrapper(baseUrlGlobal, dburlGlobal);
    dbWrapper.deleteData();

    if (!isSeleniumStarted) {
      loadDriver(browser);
      addTearDownShutDownHook();
      isSeleniumStarted = true;
    }
  }

  public void tearDownSuite() {
    try {
      if (getProperty("os.name").startsWith("Windows") && driverFactory.driverType().contains("IEDriverServer")) {
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

    public void setupTestDataToInitiateRnR(String program, String userSIC) throws IOException, SQLException{
        dbWrapper.insertProducts("P10", "P11");
        dbWrapper.insertProgramProducts("P10", "P11", program);
        dbWrapper.insertFacilityApprovedProducts("P10", "P11", program, "Lvl3 Hospital");
        dbWrapper.insertFacilities("F10", "F11");
        dbWrapper.configureTemplate(program);
        dbWrapper.insertRole("store in-charge", "false", "");
        dbWrapper.insertRole("district pharmacist", "false", "");
       dbWrapper.assignRight("store in-charge", "CREATE_REQUISITION");
       dbWrapper.assignRight("store in-charge", "VIEW_REQUISITION");
        String passwordUsers = "TQskzK3iiLfbRVHeM1muvBCiiKriibfl6lh8ipo91hb74G3OvsybvkzpPI4S3KIeWTXAiiwlUU0iiSxWii4wSuS8mokSAieie";
        dbWrapper.insertUser("200", userSIC, passwordUsers, "F10", "Fatima_Doe@openlmis.com");
        dbWrapper.insertSupervisoryNode("F10", "N1", "Node 1", "null");
        dbWrapper.insertRoleAssignment("200", "store in-charge");
        dbWrapper.insertSchedules();
        dbWrapper.insertProcessingPeriods();
        dbWrapper.insertRequisitionGroups("RG1", "RG2", "N1", "N2");
        dbWrapper.insertRequisitionGroupMembers("F10", "F11");
        dbWrapper.insertRequisitionGroupProgramSchedule();
        dbWrapper.insertSupplyLines("N1", program, "F10");
    }


}
