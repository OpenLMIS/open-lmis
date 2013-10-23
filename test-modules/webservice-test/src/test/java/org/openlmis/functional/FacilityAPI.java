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
import org.openlmis.UiUtils.TestCaseHelper;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import static com.thoughtworks.selenium.SeleneseTestBase.*;


public class FacilityAPI extends TestCaseHelper {
  public static final String URL = "http://localhost:9091/rest-api/facility.json";
  public static final String commTrackUser = "commTrack";
  public static final String GET = "GET";

  @BeforeMethod(groups = {"webservice"})
  public void setUp() throws Exception {
    super.setup();
    super.setupTestData(true);
  }

  @AfterMethod(groups = {"webservice"})
  public void tearDown() throws Exception {
    dbWrapper.deleteData();
    dbWrapper.closeConnection();
  }

    @Test(groups = {"webservice"})
    public void testFacilityAPI() throws Exception {
        HttpClient client = new HttpClient();
        client.createContext();

        ResponseEntity responseEntity = client.SendJSON("", URL + "?facilityCode=F10", GET, commTrackUser, "Admin123");
        assertTrue("Response entity : " + responseEntity.getResponse(), responseEntity.getResponse().contains("\"code\":\"F10\""));
        assertTrue("Response entity : " + responseEntity.getResponse(), responseEntity.getResponse().contains("\"name\":\"Village Dispensary\""));
        assertTrue("Response entity : " + responseEntity.getResponse(), responseEntity.getResponse().contains("\"facilityType\":\"Lvl3 Hospital\""));
        assertTrue("Response entity : " + responseEntity.getResponse(), responseEntity.getResponse().contains("\"description\":\"IT department\""));
        assertTrue("Response entity : " + responseEntity.getResponse(), responseEntity.getResponse().contains("\"gln\":\"G7645\""));
        assertTrue("Response entity : " + responseEntity.getResponse(), responseEntity.getResponse().contains("\"mainPhone\":\"9876234981\""));
        assertTrue("Response entity : " + responseEntity.getResponse(), responseEntity.getResponse().contains("\"fax\":\"fax\""));
        assertTrue("Response entity : " + responseEntity.getResponse(), responseEntity.getResponse().contains("\"address1\":\"A\""));
        assertTrue("Response entity : " + responseEntity.getResponse(), responseEntity.getResponse().contains("\"address2\":\"B\""));
        assertTrue("Response entity : " + responseEntity.getResponse(), responseEntity.getResponse().contains("\"geographicZone\":\"Ngorongoro\""));
        assertTrue("Response entity : " + responseEntity.getResponse(), responseEntity.getResponse().contains("\"catchmentPopulation\":333"));
        assertTrue("Response entity : " + responseEntity.getResponse(), responseEntity.getResponse().contains("\"latitude\":22.1"));
        assertTrue("Response entity : " + responseEntity.getResponse(), responseEntity.getResponse().contains("\"longitude\":1.2"));
        assertTrue("Response entity : " + responseEntity.getResponse(), responseEntity.getResponse().contains("\"altitude\":3.3"));
        assertTrue("Response entity : " + responseEntity.getResponse(), responseEntity.getResponse().contains("\"operatedBy\":\"NGO\""));
        assertTrue("Response entity : " + responseEntity.getResponse(), responseEntity.getResponse().contains("\"coldStorageGrossCapacity\":9.9"));
        assertTrue("Response entity : " + responseEntity.getResponse(), responseEntity.getResponse().contains("\"coldStorageNetCapacity\":6.6"));
        assertTrue("Response entity : " + responseEntity.getResponse(), responseEntity.getResponse().contains("\"suppliesOthers\":true"));
        assertTrue("Response entity : " + responseEntity.getResponse(), responseEntity.getResponse().contains("\"sdp\":true"));
        assertTrue("Response entity : " + responseEntity.getResponse(), responseEntity.getResponse().contains("\"hasElectricity\":true"));
        assertTrue("Response entity : " + responseEntity.getResponse(), responseEntity.getResponse().contains("\"online\":true"));
        assertTrue("Response entity : " + responseEntity.getResponse(), responseEntity.getResponse().contains("\"hasElectronicSCC\":true"));
        assertTrue("Response entity : " + responseEntity.getResponse(), responseEntity.getResponse().contains("\"hasElectronicDAR\":true"));
        assertTrue("Response entity : " + responseEntity.getResponse(), responseEntity.getResponse().contains("\"active\":true"));
        assertTrue("Response entity : " + responseEntity.getResponse(), responseEntity.getResponse().contains("\"goLiveDate\":1352572200000"));
        assertTrue("Response entity : " + responseEntity.getResponse(), responseEntity.getResponse().contains("\"goDownDate\":-2592106200000"));
        assertTrue("Response entity : " + responseEntity.getResponse(), responseEntity.getResponse().contains("\"satellite\":true"));
        assertTrue("Response entity : " + responseEntity.getResponse(), responseEntity.getResponse().contains("\"virtualFacility\":false"));
        assertFalse("Response entity : " + responseEntity.getResponse(), responseEntity.getResponse().contains("\"parentFacility\""));
        assertTrue("Response entity : " + responseEntity.getResponse(), responseEntity.getResponse().contains("\"comment\":\"fc\""));
        assertTrue("Response entity : " + responseEntity.getResponse(), responseEntity.getResponse().contains("\"modifiedDate\":"));
        assertTrue("Response entity : " + responseEntity.getResponse(),responseEntity.getResponse().contains("\"programsSupported\":["));
        assertTrue("Response entity : " + responseEntity.getResponse(), responseEntity.getResponse().contains("\"HIV\""));
        assertTrue("Response entity : " + responseEntity.getResponse(), responseEntity.getResponse().contains("\"ESS_MEDS\""));
        assertTrue("Response entity : " + responseEntity.getResponse(), responseEntity.getResponse().contains("\"VACCINES\""));
        assertTrue("Response entity : " + responseEntity.getResponse(), responseEntity.getResponse().contains("\"enabled\":true"));

        assertEquals(StringUtils.countMatches(responseEntity.getResponse(), ":"),35);

        dbWrapper.disableFacility("Village Dispensary");
        responseEntity = client.SendJSON("", URL + "?facilityCode=F10", GET, commTrackUser, "Admin123");
        assertTrue("Response entity : " + responseEntity.getResponse(), responseEntity.getResponse().contains("\"enabled\":false"));

        dbWrapper.deleteProgramToFacilityMapping("ESS_MEDS");
        responseEntity = client.SendJSON("", URL + "?facilityCode=F10", GET, commTrackUser, "Admin123");
        assertTrue("Response entity : " + responseEntity.getResponse(),responseEntity.getResponse().contains("\"programsSupported\":["));
        assertTrue("Response entity : " + responseEntity.getResponse(), responseEntity.getResponse().contains("\"HIV\""));
        assertTrue("Response entity : " + responseEntity.getResponse(), responseEntity.getResponse().contains("\"VACCINES\""));

        responseEntity = client.SendJSON("", URL + "?facilityCode=%20F10", GET, commTrackUser, "Admin123");
        assertEquals(responseEntity.getResponse(), "{\"error\":\"Invalid Facility code\"}");
        assertEquals(responseEntity.getStatus(), 400) ;

    }

    @Test(groups = {"webservice"})
    public void testInvalidFacilityCode() throws Exception {
        HttpClient client = new HttpClient();
        client.createContext();

        ResponseEntity responseEntity = client.SendJSON("", URL + "?facilityCode=F100" + "", GET, commTrackUser, "Admin123");
        assertEquals(responseEntity.getResponse(), "{\"error\":\"Invalid Facility code\"}");
        assertEquals(responseEntity.getStatus(), 400) ;

    }

    @Test(groups = {"webservice"})
    public void testBlankFacilityCode() throws Exception {
        HttpClient client = new HttpClient();
        client.createContext();

        ResponseEntity responseEntity = client.SendJSON("", URL + "?facilityCode=", GET, commTrackUser, "Admin123");
        assertEquals(responseEntity.getResponse(), "{\"error\":\"Invalid Facility code\"}");
        assertEquals(responseEntity.getStatus(), 400) ;

    }

    @Test(groups = {"webservice"})
    public void testInvalidUser() throws Exception {
        HttpClient client = new HttpClient();
        client.createContext();

        ResponseEntity responseEntity = client.SendJSON("", URL + "?facilityCode=F100" + "", GET, "ABCD", "Admin123");
        assertTrue("Response:" + responseEntity.getResponse(), responseEntity.getResponse().contains("Error 401 Authentication Failed")) ;
        assertEquals(responseEntity.getStatus(), 401) ;

    }

    @Test(groups = {"webservice"})
    public void testInvalidPassword() throws Exception {
        HttpClient client = new HttpClient();
        client.createContext();

        ResponseEntity responseEntity = client.SendJSON("", URL + "?facilityCode=F100" + "", GET, commTrackUser, "ABCD");
        assertTrue("Response:" + responseEntity.getResponse(), responseEntity.getResponse().contains("Error 401 Authentication Failed")) ;
        assertEquals(responseEntity.getStatus(), 401) ;

    }


  @DataProvider(name = "Data-Provider-Function-Positive")
  public Object[][] parameterIntTestProviderPositive() {
    return new Object[][]{
      {"User123", "HIV", new String[]{"Admin123", "Admin123"}}
    };
  }
}

