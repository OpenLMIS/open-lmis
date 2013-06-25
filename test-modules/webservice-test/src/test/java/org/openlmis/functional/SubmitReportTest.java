/*
 * Copyright © 2013 VillageReach.  All Rights Reserved.  This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 *
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
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

import static com.thoughtworks.selenium.SeleneseTestNgHelper.assertEquals;
import static com.thoughtworks.selenium.SeleneseTestNgHelper.assertTrue;
import static java.lang.System.getProperty;
import static org.openlmis.UiUtils.HttpClient.GET;
import static org.openlmis.UiUtils.HttpClient.POST;
import static org.openlmis.functional.JsonUtility.getJsonStringFor;
import static org.openlmis.functional.JsonUtility.readObjectFromFile;


public class SubmitReportTest extends TestCaseHelper {

  public static final String MINIMUM_JSON_TXT_FILE_NAME = "ReportMinimumJson.txt";
  public static final String FULL_JSON_TXT_FILE_NAME = "ReportFullJson.txt";
  public static final String PRODUCT_JSON_TXT_FILE_NAME = "ReportWithProductJson.txt";

  public WebDriver driver;

  @BeforeMethod(groups = {"webservice"})
  public void setUp() throws Exception {
    driver = new FirefoxDriver();
    driver.get("http://localhost:9091");
    super.setup();
    super.setupDataExternalVendor(false);
  }

  @AfterMethod(groups = {"webservice"})
  public void tearDown() {
    driver.close();
  }

  @Test(groups = {"webservice"})
  public void testSubmitReportInvalidFacility() throws Exception {
    HttpClient client = new HttpClient();
    client.createContext();

    Report reportFromJson = readObjectFromFile(FULL_JSON_TXT_FILE_NAME, Report.class);
    reportFromJson.setFacilityId(100L);
    reportFromJson.setPeriodId(dbWrapper.getPeriodID("Period2"));
    reportFromJson.setProgramId(dbWrapper.getProgramID("HIV"));

    ResponseEntity responseEntity = client.SendJSON(getJsonStringFor(reportFromJson),
      "http://localhost:9091/rest-api/requisitions.json",
      POST,
      "commTrack",
      dbWrapper.getAuthToken("commTrack"));

    String response = responseEntity.getResponse();
    client.SendJSON("", "http://localhost:9091/", GET, "", "");

    assertEquals(responseEntity.getStatus(), 400);
    assertEquals(response, "{\"error\":\"User doesn't have access to Program & Facility.\"}");
  }

  @Test(groups = {"webservice"})
  public void testSubmitReportInvalidProgram() throws Exception {
    HttpClient client = new HttpClient();
    client.createContext();

    Report reportFromJson = JsonUtility.readObjectFromFile(FULL_JSON_TXT_FILE_NAME, Report.class);
    reportFromJson.setFacilityId(dbWrapper.getFacilityID("F10"));
    reportFromJson.setPeriodId(dbWrapper.getPeriodID("Period2"));
    reportFromJson.setProgramId(500L);

    ResponseEntity responseEntity = client.SendJSON(getJsonStringFor(reportFromJson),
      "http://localhost:9091/rest-api/requisitions.json",
      POST,
      "commTrack",
      dbWrapper.getAuthToken("commTrack"));

    String response = responseEntity.getResponse();
    client.SendJSON("", "http://localhost:9091/", GET, "", "");
    assertEquals(responseEntity.getStatus(), 400);
    assertEquals(response, "{\"error\":\"User doesn't have access to Program & Facility.\"}");
  }

  @Test(groups = {"webservice"})
  public void testSubmitReportValidRnR() throws Exception {
    String response = submitReport();
    assertTrue(response.contains("{\"R&R\":"));
  }

  @Test(groups = {"webservice"})
  public void shouldThrowErrorOnSubmittingDuplicateReport() throws Exception {
    HttpClient client = new HttpClient();
    client.createContext();

    Report reportFromJson = readObjectFromFile(FULL_JSON_TXT_FILE_NAME, Report.class);
    reportFromJson.setFacilityId(dbWrapper.getFacilityID("F10"));
    reportFromJson.setPeriodId(dbWrapper.getPeriodID("Period2"));
    reportFromJson.setProgramId(dbWrapper.getProgramID("HIV"));

    String jsonStringFor = getJsonStringFor(reportFromJson);
    client.SendJSON(jsonStringFor,
      "http://localhost:9091/rest-api/requisitions.json",
      POST,
      "commTrack",
      dbWrapper.getAuthToken("commTrack"));

    ResponseEntity responseEntity = client.SendJSON(jsonStringFor,
      "http://localhost:9091/rest-api/requisitions.json",
      POST,
      "commTrack",
      dbWrapper.getAuthToken("commTrack"));

    client.SendJSON("", "http://localhost:9091/", GET, "", "");
    assertEquals(responseEntity.getStatus(), 400);
    assertEquals(responseEntity.getResponse(), "{\"error\":\"Please finish all R&R of previous period(s)\"}");
  }

  @Test(groups = {"webservice"})
  public void testBlankProductSubmitReport() throws Exception {
    HttpClient client = new HttpClient();
    client.createContext();
    Report reportFromJson = JsonUtility.readObjectFromFile(PRODUCT_JSON_TXT_FILE_NAME, Report.class);
    reportFromJson.setFacilityId(dbWrapper.getFacilityID("F10"));
    reportFromJson.setPeriodId(dbWrapper.getPeriodID("Period2"));
    reportFromJson.setProgramId(dbWrapper.getProgramID("HIV"));

    ResponseEntity responseEntity = client.SendJSON(getJsonStringFor(reportFromJson), "http://localhost:9091/rest-api/requisitions.json", POST,
      "commTrack",
      dbWrapper.getAuthToken("commTrack"));

    String response = responseEntity.getResponse();
    client.SendJSON("", "http://localhost:9091/", GET, "", "");
      assertEquals(responseEntity.getStatus(), 400);
    assertEquals(response, "{\"error\":\"Invalid product code\"}");
  }

  @Test(groups = {"webservice"})
  public void testInvalidProductSubmitReport() throws Exception {
    HttpClient client = new HttpClient();
    client.createContext();

    Report reportFromJson = JsonUtility.readObjectFromFile(FULL_JSON_TXT_FILE_NAME, Report.class);
    reportFromJson.setFacilityId(dbWrapper.getFacilityID("F10"));
    reportFromJson.setPeriodId(dbWrapper.getPeriodID("Period2"));
    reportFromJson.setProgramId(dbWrapper.getProgramID("HIV"));
    reportFromJson.getProducts().get(0).setProductCode("P10000");

    ResponseEntity responseEntity =
      client.SendJSON(getJsonStringFor(reportFromJson),
        "http://localhost:9091/rest-api/requisitions.json",
        POST,
        "commTrack",
        dbWrapper.getAuthToken("commTrack"));

    String response = responseEntity.getResponse();

    client.SendJSON("", "http://localhost:9091/", GET, "", "");
    assertEquals(responseEntity.getStatus(), 400);
    assertEquals(response, "{\"error\":\"Invalid product code\"}");
  }

  @Test(groups = {"webservice"})
  public void testBlankBeginningBalanceSubmitReport() throws Exception {
    HttpClient client = new HttpClient();
    client.createContext();

    Report reportFromJson = JsonUtility.readObjectFromFile(PRODUCT_JSON_TXT_FILE_NAME, Report.class);
    reportFromJson.setFacilityId(dbWrapper.getFacilityID("F10"));
    reportFromJson.setPeriodId(dbWrapper.getPeriodID("Period2"));
    reportFromJson.setProgramId(dbWrapper.getProgramID("HIV"));
    reportFromJson.getProducts().get(0).setProductCode("P10");

    ResponseEntity responseEntity =
      client.SendJSON(
        getJsonStringFor(reportFromJson),
        "http://localhost:9091/rest-api/requisitions.json",
        POST,
        "commTrack",
        dbWrapper.getAuthToken("commTrack"));

    String response = responseEntity.getResponse();

    client.SendJSON("", "http://localhost:9091/", GET, "", "");
    assertEquals(responseEntity.getStatus(), 400);
    assertEquals(response, "{\"error\":\"R&R has errors, please correct them to proceed.\"}");
  }

  @Test(groups = {"webservice"})
  public void testMinimumSubmitReportValidRnR() throws Exception {
    HttpClient client = new HttpClient();
    client.createContext();

    Report reportFromJson = JsonUtility.readObjectFromFile(MINIMUM_JSON_TXT_FILE_NAME, Report.class);
    reportFromJson.setUserId("commTrack");
    reportFromJson.setFacilityId(dbWrapper.getFacilityID("F10"));
    reportFromJson.setPeriodId(dbWrapper.getPeriodID("Period2"));
    reportFromJson.setProgramId(dbWrapper.getProgramID("HIV"));
    reportFromJson.getProducts().get(0).setProductCode("P10");

    ResponseEntity responseEntity =
      client.SendJSON(
        getJsonStringFor(reportFromJson),
        "http://localhost:9091/rest-api/requisitions.json",
        POST,
        "commTrack",
        dbWrapper.getAuthToken("commTrack"));

    String response = responseEntity.getResponse();

    client.SendJSON("", "http://localhost:9091/", GET, "", "");
    assertEquals(responseEntity.getStatus(), 201);
    assertTrue(response.contains("{\"R&R\":"));
  }

  @Test(groups = {"webservice"})
  public void testSubmitReportInvalidUser() throws Exception {
    HttpClient client = new HttpClient();
    client.createContext();

    Report reportFromJson = JsonUtility.readObjectFromFile(MINIMUM_JSON_TXT_FILE_NAME, Report.class);
    reportFromJson.setUserId("commTrack100");
    reportFromJson.setFacilityId(dbWrapper.getFacilityID("F10"));
    reportFromJson.setPeriodId(dbWrapper.getPeriodID("Period2"));
    reportFromJson.setProgramId(dbWrapper.getProgramID("HIV"));
    reportFromJson.getProducts().get(0).setProductCode("P10");

    ResponseEntity responseEntity =
      client.SendJSON(getJsonStringFor(reportFromJson),
        "http://localhost:9091/rest-api/requisitions.json", POST,
        "commTrack",
        dbWrapper.getAuthToken("commTrack"));

    String response = responseEntity.getResponse();

    client.SendJSON("", "http://localhost:9091/", GET, "", "");
    assertEquals(responseEntity.getStatus(), 400);
    assertEquals(response, "{\"error\":\"Please provide a valid username\"}");
  }

  @Test(groups = {"webservice"})
  public void shouldReturn401StatusWhenSubmittingReportWithInvalidVendor() throws Exception {
    HttpClient client = new HttpClient();
    client.createContext();

    Report reportFromJson = JsonUtility.readObjectFromFile(MINIMUM_JSON_TXT_FILE_NAME, Report.class);
    reportFromJson.setUserId("commTrack");
    reportFromJson.setFacilityId(dbWrapper.getFacilityID("F10"));
    reportFromJson.setPeriodId(dbWrapper.getPeriodID("Period2"));
    reportFromJson.setProgramId(dbWrapper.getProgramID("HIV"));
    reportFromJson.getProducts().get(0).setProductCode("P10");

    ResponseEntity responseEntity =
      client.SendJSON(
        getJsonStringFor(reportFromJson),
        "http://localhost:9091/rest-api/requisitions.json",
        POST,
        "commTrack1000",
        dbWrapper.getAuthToken("commTrack"));

    assertEquals(responseEntity.getStatus(), 401);
  }

  public String submitReport() throws Exception {
    baseUrlGlobal = getProperty("baseurl", DEFAULT_BASE_URL);
    dburlGlobal = getProperty("dburl", DEFAULT_DB_URL);
    dbWrapper = new DBWrapper(baseUrlGlobal, dburlGlobal);

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
      dbWrapper.getAuthToken("commTrack"));

    client.SendJSON("", "http://localhost:9091/", "GET", "", "");
    return responseEntity.getResponse();
  }

}

