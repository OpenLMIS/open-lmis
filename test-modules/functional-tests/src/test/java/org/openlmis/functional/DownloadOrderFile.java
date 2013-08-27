/*
 * Copyright © 2013 VillageReach.  All Rights Reserved.  This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 *
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package org.openlmis.functional;


import cucumber.api.DataTable;
import cucumber.api.java.After;
import cucumber.api.java.Before;
import cucumber.api.java.en.And;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import org.openlmis.UiUtils.CaptureScreenshotOnFailureListener;
import org.openlmis.UiUtils.TestCaseHelper;
import org.openlmis.pageobjects.*;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Listeners;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@TransactionConfiguration(defaultRollback = true)
@Transactional

@Listeners(CaptureScreenshotOnFailureListener.class)

public class DownloadOrderFile extends TestCaseHelper {
  public String facility_code;
  public String facility_name;
  public String date_time;
  public String geoZone = "Ngorongoro";
  public String parentGeoZone = "Dodoma";
  public String facilityType = "Lvl3 Hospital";
  public String operatedBy = "MoH";
  public String facilityCodePrefix = "FCcode";
  public String facilityNamePrefix = "FCname";
  public String catchmentPopulation = "500000";
  public String userIDSIC;
  public String periodDetails;
  public String periodTopSNUser;
  public String program = "HIV";

  public String passwordUsers = "TQskzK3iiLfbRVHeM1muvBCiiKriibfl6lh8ipo91hb74G3OvsybvkzpPI4S3KIeWTXAiiwlUU0iiSxWii4wSuS8mokSAieie";
  public String userSICUserName = "storeincharge";


  @Before
  public void setUp() throws Exception {
    super.setup();
  }

  @DataProvider(name = "envData")
  public Object[][] getEnvData() {
    return new Object[][]{};
  }


  @And("^I configure order file:$")
  public void setupOrderFileConfiguration(DataTable userTable) throws Exception {
    List<Map<String, String>> data = userTable.asMaps();
    for (Map map : data)
            dbWrapper.setupOrderFileConfiguration(map.get("File Prefix").toString(),map.get("Header In File").toString(),map.get("Date Pattern").toString(),map.get("Period Date Pattern").toString());
  }

  @And("^I configure non openlmis order file columns:$")
  public void setupOrderFileNonOpenLMISColumns(DataTable userTable) throws Exception {
    List<Map<String, String>> data = userTable.asMaps();
    for (Map map : data)
        dbWrapper.setupOrderFileNonOpenLMISColumns(map.get("Data Field Label").toString(),map.get("Include In Order File").toString(),map.get("Column Label").toString(),Integer.parseInt(map.get("Position").toString()) );
  }

  @And("^I configure openlmis order file columns:$")
  public void setupOrderFileOpenLMISColumns(DataTable userTable) throws Exception {
    dbWrapper.defaultSetupOrderFileOpenLMISColumns();
    List<Map<String, String>> data = userTable.asMaps();
    for (Map map : data)
        dbWrapper.setupOrderFileOpenLMISColumns(map.get("Data Field Label").toString(),map.get("Include In Order File").toString(),map.get("Column Label").toString(),Integer.parseInt(map.get("Position").toString()) );
  }

    @And("^I download order file$")
    public void downloadOrderFile() throws Exception {
        ViewOrdersPage viewOrderPage = new ViewOrdersPage(testWebDriver);
        viewOrderPage.downloadCSV();
        }

  @After
  public void tearDown() throws Exception {
    testWebDriver.sleep(500);
    if (!testWebDriver.getElementById("username").isDisplayed()) {
      HomePage homePage = new HomePage(testWebDriver);
      homePage.logout(baseUrlGlobal);
    }
    dbWrapper.deleteData();
    dbWrapper.closeConnection();
  }

}

