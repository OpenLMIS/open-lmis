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

import org.apache.commons.lang3.StringUtils;
import org.openlmis.UiUtils.HttpClient;
import org.openlmis.UiUtils.ResponseEntity;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.io.IOException;
import java.sql.SQLException;

import static com.thoughtworks.selenium.SeleneseTestBase.*;
import static java.lang.String.format;


public class FacilityAPI extends JsonUtility {
  public static final String URL = "http://localhost:9091/rest-api/facilities/%s";
  public static final String commTrackUser = "commTrack";
  public static final String GET = "GET";

  @BeforeMethod(groups = {"webservice", "webserviceSmoke"})
  public void setUp() throws InterruptedException, SQLException, IOException {
    super.setup();
    super.setupTestData(true);
    dbWrapper.updateRestrictLogin("commTrack", true);
  }

  @AfterMethod(groups = {"webservice", "webserviceSmoke"})
  public void tearDown() throws SQLException {
    dbWrapper.deleteData();
    dbWrapper.closeConnection();
  }

  @Test(groups = {"webserviceSmoke"})
  public void testFacilityAPI() throws SQLException {
    HttpClient client = new HttpClient();
    client.createContext();
    ResponseEntity responseEntity = client.SendJSON("", format(URL, "F10"), GET, commTrackUser, "Admin123");
    String response = responseEntity.getResponse();
    assertTrue("Response entity : " + response, response.contains("\"code\":\"F10\""));
    assertTrue("Response entity : " + response, response.contains("\"name\":\"Village Dispensary\""));
    assertTrue("Response entity : " + response, response.contains("\"facilityType\":\"Lvl3 Hospital\""));
    assertTrue("Response entity : " + response, response.contains("\"description\":\"IT department\""));
    assertTrue("Response entity : " + response, response.contains("\"gln\":\"G7645\""));
    assertTrue("Response entity : " + response, response.contains("\"mainPhone\":\"9876234981\""));
    assertTrue("Response entity : " + response, response.contains("\"fax\":\"fax\""));
    assertTrue("Response entity : " + response, response.contains("\"address1\":\"A\""));
    assertTrue("Response entity : " + response, response.contains("\"address2\":\"B\""));
    assertTrue("Response entity : " + response, response.contains("\"geographicZone\":\"Ngorongoro\""));
    assertTrue("Response entity : " + response, response.contains("\"catchmentPopulation\":333"));
    assertTrue("Response entity : " + response, response.contains("\"latitude\":22.1"));
    assertTrue("Response entity : " + response, response.contains("\"longitude\":1.2"));
    assertTrue("Response entity : " + response, response.contains("\"altitude\":3.3"));
    assertTrue("Response entity : " + response, response.contains("\"operatedBy\":\"NGO\""));
    assertTrue("Response entity : " + response, response.contains("\"coldStorageGrossCapacity\":9.9"));
    assertTrue("Response entity : " + response, response.contains("\"coldStorageNetCapacity\":6.6"));
    assertTrue("Response entity : " + response, response.contains("\"suppliesOthers\":true"));
    assertTrue("Response entity : " + response, response.contains("\"sdp\":true"));
    assertTrue("Response entity : " + response, response.contains("\"hasElectricity\":true"));
    assertTrue("Response entity : " + response, response.contains("\"online\":true"));
    assertTrue("Response entity : " + response, response.contains("\"hasElectronicSCC\":true"));
    assertTrue("Response entity : " + response, response.contains("\"hasElectronicDAR\":true"));
    assertTrue("Response entity : " + response, response.contains("\"active\":true"));
    assertTrue("Response entity : " + response, response.contains("\"goLiveDate\":1352572200000"));
    assertTrue("Response entity : " + response, response.contains("\"goDownDate\":-2592106200000"));
    assertTrue("Response entity : " + response, response.contains("\"stringGoLiveDate\":\"11-11-2012\""));
    assertTrue("Response entity : " + response, response.contains("\"stringGoDownDate\":\"11-11-1887\""));
    assertTrue("Response entity : " + response, response.contains("\"satellite\":true"));
    assertTrue("Response entity : " + response, response.contains("\"virtualFacility\":false"));
    assertFalse("Response entity : " + response, response.contains("\"parentFacility\""));
    assertTrue("Response entity : " + response, response.contains("\"comment\":\"fc\""));
    assertTrue("Response entity : " + response, response.contains("\"modifiedDate\":"));
    assertTrue("Response entity : " + response, response.contains("\"programsSupported\":["));
    assertTrue("Response entity : " + response, response.contains("\"HIV\""));
    assertTrue("Response entity : " + response, response.contains("\"ESS_MEDS\""));
    assertTrue("Response entity : " + response, response.contains("\"VACCINES\""));
    assertTrue("Response entity : " + response, response.contains("\"enabled\":true"));

    assertEquals(StringUtils.countMatches(response, ":"), 37);
    dbWrapper.updateFieldValue("facilities", "enabled", "false", "name", "Village Dispensary");
    responseEntity = client.SendJSON("", format(URL, "F10"), GET, commTrackUser, "Admin123");
    response = responseEntity.getResponse();
    assertTrue("Response entity : " + response, response.contains("\"enabled\":false"));

    dbWrapper.deleteProgramToFacilityMapping("ESS_MEDS");
    responseEntity = client.SendJSON("", format(URL, "F10"), GET, commTrackUser, "Admin123");
    response = responseEntity.getResponse();
    assertTrue("Response entity : " + response, response.contains("\"programsSupported\":["));
    assertTrue("Response entity : " + response, response.contains("\"HIV\""));
    assertTrue("Response entity : " + response, response.contains("\"VACCINES\""));

    responseEntity = client.SendJSON("", format(URL, "INVALID_FACILITY_CODE"), GET, commTrackUser, "Admin123");
    response = responseEntity.getResponse();
    assertEquals(response, "{\"error\":\"Invalid Facility code\"}");
    assertEquals(responseEntity.getStatus(), 400);

  }

  @Test(groups = {"webservice"})
  public void testInvalidFacilityCode() {
    HttpClient client = new HttpClient();
    client.createContext();

    ResponseEntity responseEntity = client.SendJSON("", format(URL, "F100"), GET, commTrackUser, "Admin123");
    assertEquals(responseEntity.getResponse(), "{\"error\":\"Invalid Facility code\"}");
    assertEquals(responseEntity.getStatus(), 400);
  }

  @Test(groups = {"webservice"})
  public void testBlankFacilityCode() {
    HttpClient client = new HttpClient();
    client.createContext();

    ResponseEntity responseEntity = client.SendJSON("", format(URL, ""), GET, commTrackUser, "Admin123");
    assertEquals(responseEntity.getStatus(), 404);
  }

  @Test(groups = {"webservice"})
  public void testInvalidUser() {
    HttpClient client = new HttpClient();
    client.createContext();

    ResponseEntity responseEntity = client.SendJSON("", format(URL, "F100"), GET, "ABCD", "Admin123");
    assertTrue("Response:" + responseEntity.getResponse(), responseEntity.getResponse().contains("Error 401 Authentication Failed"));
    assertEquals(responseEntity.getStatus(), 401);
  }

  @Test(groups = {"webservice"})
  public void testInvalidPassword() {
    HttpClient client = new HttpClient();
    client.createContext();

    ResponseEntity responseEntity = client.SendJSON("", format(URL, "F100"), GET, commTrackUser, "ABCD");
    assertTrue("Response:" + responseEntity.getResponse(), responseEntity.getResponse().contains("Error 401 Authentication Failed"));
    assertEquals(responseEntity.getStatus(), 401);
  }


  @DataProvider(name = "Data-Provider-Function-Positive")
  public Object[][] parameterIntTestProviderPositive() {
    return new Object[][]{
      {"User123", "HIV", new String[]{"Admin123", "Admin123"}}
    };
  }
}

