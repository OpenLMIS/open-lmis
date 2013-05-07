/*
 * Copyright © 2013 VillageReach.  All Rights Reserved.  This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 *
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package org.openlmis.functional;

import org.openlmis.UiUtils.HttpClient;
import org.openlmis.UiUtils.ResponseEntity;
import org.openlmis.UiUtils.TestCaseHelper;
import org.openlmis.restapi.domain.Report;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import static com.thoughtworks.selenium.SeleneseTestNgHelper.assertTrue;
import static org.openlmis.functional.JsonUtility.getJsonStringFor;
import static org.openlmis.functional.JsonUtility.readObjectFromFile;


public class CommTrackTemplateTest extends TestCaseHelper {
  public static final String FULL_COMMTRACK_JSON_TXT_FILE_NAME = "CommTrackReportJson.txt";
  public WebDriver driver;

  @BeforeMethod(groups = {"webservice"})
  public void setUp() throws Exception {
    driver = new FirefoxDriver();
    driver.get("http://localhost:9091");
    super.setup();
    super.setupDataExternalVendor(true);
  }

  @AfterMethod(groups = {"webservice"})
  public void tearDown() {
    driver.close();
  }

  @Test(groups = {"webservice"})
  public void testCommTrackSubmitReportValidRnR() throws Exception {
    HttpClient client = new HttpClient();
    client.createContext();

    Report reportFromJson = readObjectFromFile(FULL_COMMTRACK_JSON_TXT_FILE_NAME, Report.class);
    reportFromJson.setUserId("commTrack");
    reportFromJson.setFacilityId(dbWrapper.getFacilityID("F10"));
    reportFromJson.setPeriodId(dbWrapper.getPeriodID("Period2"));
    reportFromJson.setProgramId(dbWrapper.getProgramID("HIV"));
    reportFromJson.getProducts().get(0).setProductCode("P10");

    ResponseEntity responseEntity =
      client.SendJSON(getJsonStringFor(reportFromJson),
        "http://localhost:9091/rest-api/requisitions.json",
        "POST",
        "commTrack",
        dbWrapper.getAuthToken("commTrack"));

    client.SendJSON("", "http://localhost:9091/", "GET", "", "");

    assertTrue(responseEntity.getResponse().contains("{\"R&R\":"));
  }
}

