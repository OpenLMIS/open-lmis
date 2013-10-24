/*
 * This program is part of the OpenLMIS logistics management information system platform software.
 * Copyright © 2013 VillageReach
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *  
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 * You should have received a copy of the GNU Affero General Public License along with this program.  If not, see http://www.gnu.org/licenses.  For additional information contact info@OpenLMIS.org. 
 */

package org.openlmis.functional;

import org.openlmis.UiUtils.DBWrapper;
import org.openlmis.UiUtils.HttpClient;
import org.openlmis.UiUtils.ResponseEntity;
import org.openlmis.UiUtils.TestCaseHelper;
import org.openlmis.restapi.domain.Report;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.io.IOException;
import java.sql.SQLException;

import static com.thoughtworks.selenium.SeleneseTestBase.assertEquals;
import static com.thoughtworks.selenium.SeleneseTestBase.assertFalse;
import static com.thoughtworks.selenium.SeleneseTestBase.assertTrue;
import static java.lang.System.getProperty;
import static org.openlmis.functional.JsonUtility.getJsonStringFor;
import static org.openlmis.functional.JsonUtility.readObjectFromFile;


public class GetRequsitionDetailsAPI extends TestCaseHelper {

  public static final String FULL_JSON_APPROVE_TXT_FILE_NAME = "ReportJsonApprove.txt";
  public static final String FULL_JSON_TXT_FILE_NAME = "ReportFullJson.txt";
  public static final String URL = "http://localhost:9091/rest-api/requisitions/";
  public WebDriver driver;

  @BeforeMethod(groups = {"webservice"})
  public void setUp() throws Exception {
    driver = new FirefoxDriver();
    driver.get("http://localhost:9091");
    super.setup();
    super.setupTestData(false);
    super.setupDataRequisitionApprover();
  }

  @AfterMethod(groups = {"webservice"})
  public void tearDown() throws IOException, SQLException {
    driver.close();
    dbWrapper.deleteData();
    dbWrapper.closeConnection();
  }

  @Test(groups = {"webservice"})
  public void testGetRequisitionDetails() throws Exception {
    HttpClient client = new HttpClient();

    client.createContext();

    String response = submitReport();
    Long id = getRequisitionIdFromResponse(response);

    ResponseEntity responseEntity = client.SendJSON("", URL + id, "GET", "commTrack", "Admin123");
      assertTrue("Response entity : " + responseEntity.getResponse(), responseEntity.getResponse().contains("\"programCode\":\"HIV\""));
      assertTrue("Response entity : " + responseEntity.getResponse(), responseEntity.getResponse().contains("\"agentCode\":\"F10\""));
      assertTrue("Response entity : " + responseEntity.getResponse(), responseEntity.getResponse().contains("\"emergency\":false"));
      assertTrue("Response entity : " + responseEntity.getResponse(), responseEntity.getResponse().contains("\"periodStartDate\":1358274600000"));
      assertTrue("Response entity : " + responseEntity.getResponse(), responseEntity.getResponse().contains("\"periodEndDate\":1359484200000"));
      assertTrue("Response entity : " + responseEntity.getResponse(), responseEntity.getResponse().contains("\"status\":\"AUTHORIZED\""));
      }

  public String submitReport() throws Exception {
    baseUrlGlobal = getProperty("baseurl", DEFAULT_BASE_URL);
    dbUrlGlobal = getProperty("dbUrl", DEFAULT_DB_URL);
    dbWrapper = new DBWrapper(baseUrlGlobal, dbUrlGlobal);

    HttpClient client = new HttpClient();
    client.createContext();

    Report reportFromJson = readObjectFromFile(FULL_JSON_TXT_FILE_NAME, Report.class);
    reportFromJson.setFacilityId(dbWrapper.getFacilityID("F10"));
    reportFromJson.setPeriodId(dbWrapper.getPeriodID("Period2"));
    reportFromJson.setProgramId(dbWrapper.getProgramID("HIV"));

    ResponseEntity responseEntity = client.SendJSON(getJsonStringFor(reportFromJson),
      "http://localhost:9091/rest-api/requisitions.json",
      "POST",
      "commTrack",
      "Admin123");

    client.SendJSON("", "http://localhost:9091/", "GET", "", "");

    return responseEntity.getResponse();
  }


  private Long getRequisitionIdFromResponse(String response) {
    return Long.parseLong(response.substring(response.lastIndexOf(":") + 1, response.lastIndexOf("}")));
  }

}

