/*
 * Copyright © 2013 VillageReach.  All Rights Reserved.  This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 *
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package org.openlmis.functional;

import org.openlmis.UiUtils.HttpClient;
import org.openlmis.UiUtils.ResponseEntity;
import org.openlmis.UiUtils.TestCaseHelper;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import static com.thoughtworks.selenium.SeleneseTestNgHelper.assertEquals;
import static com.thoughtworks.selenium.SeleneseTestNgHelper.assertTrue;


public class ApproveRequisition extends TestCaseHelper {
  public static final String FULL_JSON_APPROVE_TXT_FILE_NAME = "FullJsonApprove.txt";
  public WebDriver driver;
  public Utils utility = new Utils();

  @BeforeMethod(groups = {"webservice"})
  public void setUp() throws Exception {
    driver = new FirefoxDriver();
    driver.get("http://localhost:9091");
    super.setup();
    super.setupDataExternalVendor();
    super.setupDataApproverExternalVendor();
  }

  @AfterMethod(groups = {"webservice"})
  public void tearDown() {
    driver.close();
  }


  @Test(groups = {"webservice"})
  public void testApproveRequisitionValidRnR() throws Exception {
    HttpClient client = new HttpClient();
    client.createContext();
    String response = utility.submitReport();
    String id = utility.getRequisitionIdFromResponse(response);
    assertEquals(dbWrapper.getRequisitionStatus(id), "AUTHORIZED");

    String json = utility.readJSON(FULL_JSON_APPROVE_TXT_FILE_NAME);
    json = utility.updateJSON(json, "requisitionId", id);
    json = utility.updateJSON(json, "userId", "commTrack1");
    json = utility.updateJSON(json, "productCode", "P10");
    json = utility.updateJSON(json, "quantityApproved", "65");

    ResponseEntity responseEntity = client.SendJSON(json,
      "http://localhost:9091/rest-api/requisitions/" + id + "/approve", "PUT", "commTrack",
      dbWrapper.getAuthToken("commTrack"));
    response = responseEntity.getResponse();
    client.SendJSON("", "http://localhost:9091/", "GET", "", "");

    assertTrue(response.contains("{\"R&R\":"));
    assertEquals(dbWrapper.getRequisitionStatus(id), "RELEASED");
  }

  @Test(groups = {"webservice"})
  public void testApproveRequisitionInValidUser() throws Exception {
    HttpClient client = new HttpClient();
    client.createContext();
    String response = utility.submitReport();
    String id = utility.getRequisitionIdFromResponse(response);

    String json = utility.readJSON(FULL_JSON_APPROVE_TXT_FILE_NAME);
    json = utility.updateJSON(json, "requisitionId", id);
    json = utility.updateJSON(json, "userId", "ABCD");
    json = utility.updateJSON(json, "productCode", "P10");
    json = utility.updateJSON(json, "quantityApproved", "65");

    ResponseEntity responseEntity = client.SendJSON(json,
      "http://localhost:9091/rest-api/requisitions/" + id + "/approve", "PUT",
      "commTrack", dbWrapper.getAuthToken("commTrack"));
    response = responseEntity.getResponse();
    client.SendJSON("", "http://localhost:9091/", "GET", "", "");

    assertEquals(response, "{\"error\":\"Please provide a valid username\"}");
  }

  @Test(groups = {"webservice"})
  public void testApproveRequisitionInvalidProduct() throws Exception {
    HttpClient client = new HttpClient();
    client.createContext();
    String response = utility.submitReport();
    String id = utility.getRequisitionIdFromResponse(response);

    String json = utility.readJSON(FULL_JSON_APPROVE_TXT_FILE_NAME);
    json = utility.updateJSON(json, "requisitionId", id);
    json = utility.updateJSON(json, "userId", "commTrack");
    json = utility.updateJSON(json, "productCode", "P1000");
    json = utility.updateJSON(json, "quantityApproved", "65");

    ResponseEntity responseEntity = client.SendJSON(json,
      "http://localhost:9091/rest-api/requisitions/" + id + "/approve", "PUT",
      "commTrack", dbWrapper.getAuthToken("commTrack"));
    response = responseEntity.getResponse();
    client.SendJSON("", "http://localhost:9091/", "GET", "", "");

//        assertEquals(response, "{\"error\":\"Invalid data.\"}");


  }

  @Test(groups = {"webservice"})
  public void testApproveRequisitionInvalidRequisitionId() throws Exception {
    HttpClient client = new HttpClient();
    client.createContext();
    String response = utility.submitReport();
    String id = "999999";
    String id2 = utility.getRequisitionIdFromResponse(response);


    String json = utility.readJSON(FULL_JSON_APPROVE_TXT_FILE_NAME);
    json = utility.updateJSON(json, "requisitionId", id2);
    json = utility.updateJSON(json, "userId", "commTrack");
    json = utility.updateJSON(json, "productCode", "P10");
    json = utility.updateJSON(json, "quantityApproved", "65");

    ResponseEntity responseEntity = client.SendJSON(json,
      "http://localhost:9091/rest-api/requisitions/" + id + "/approve", "PUT",
      "commTrack", dbWrapper.getAuthToken("commTrack"));
    response = responseEntity.getResponse();
    client.SendJSON("", "http://localhost:9091/", "GET", "", "");

    assertEquals(response, "{\"error\":\"Requisition Not Found\"}");


  }

  @Test(groups = {"webservice"})
  public void testApproveRequisitionBlankQuantityApproved() throws Exception {
    HttpClient client = new HttpClient();
    client.createContext();
    String response = utility.submitReport();
    String id = utility.getRequisitionIdFromResponse(response);

    String json = utility.readJSON(FULL_JSON_APPROVE_TXT_FILE_NAME);
    json = utility.updateJSON(json, "requisitionId", id);
    json = utility.updateJSON(json, "userId", "commTrack1");
    json = utility.updateJSON(json, "productCode", "P10");
    json = utility.updateJSON(json, "quantityApproved", "");

    ResponseEntity responseEntity = client.SendJSON(json,
      "http://localhost:9091/rest-api/requisitions/" + id + "/approve", "PUT",
      "commTrack", dbWrapper.getAuthToken("commTrack"));
    response = responseEntity.getResponse();
    client.SendJSON("", "http://localhost:9091/", "GET", "", "");

    assertEquals(response, "{\"error\":\"Invalid data.\"}");


  }

  @Test(groups = {"webservice"})
  public void testApproveRequisitionInValidVendor() throws Exception {
    HttpClient client = new HttpClient();
    client.createContext();
    String response = utility.submitReport();
    String id = utility.getRequisitionIdFromResponse(response);

    String json = utility.readJSON(FULL_JSON_APPROVE_TXT_FILE_NAME);
    json = utility.updateJSON(json, "requisitionId", id);
    json = utility.updateJSON(json, "userId", "commTrack100");
    json = utility.updateJSON(json, "productCode", "P10");
    json = utility.updateJSON(json, "quantityApproved", "65");

    ResponseEntity responseEntity = client.SendJSON(json,
      "http://localhost:9091/rest-api/requisitions/" + id + "/approve", "PUT",
      "commTrack1", dbWrapper.getAuthToken("commTrack"));
    response = responseEntity.getResponse();
    client.SendJSON("", "http://localhost:9091/", "GET", "", "");

    //assertEquals(response, null);
  }

}

