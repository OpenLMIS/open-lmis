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

import org.openlmis.UiUtils.HttpClient;
import org.openlmis.UiUtils.ResponseEntity;
import org.openlmis.UiUtils.TestCaseHelper;
import org.openlmis.pageobjects.*;
import org.openlmis.restapi.domain.Agent;
import org.openqa.selenium.WebDriver;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.util.List;

import static com.thoughtworks.selenium.SeleneseTestBase.*;
import static org.openlmis.functional.JsonUtility.getJsonStringFor;
import static org.openlmis.functional.JsonUtility.readObjectFromFile;


public class FacilityAPI extends TestCaseHelper {
  public WebDriver driver;
  public static final String FULL_JSON_TXT_FILE_NAME = "AgentValid.txt";
  public static final String userEmail = "Fatim_Doe@openlmis.com";
  public static final String URL = "http://localhost:9091/rest-api/facility.json";
  public static final String commTrackUser = "commTrack";
  public static final String PHONE_NUMBER = "0099887766";
  public static final String DEFAULT_AGENT_NAME = "AgentVinod";
  public static final String DEFAULT_PARENT_FACILITY_CODE = "F10";
  public static final String ACTIVE_STATUS = "true";
  public static final String DEFAULT_AGENT_CODE = "A2";
  public static final String JSON_EXTENSION = ".json";
  public static final String GET = "GET";
  public static final String POST = "POST";
  public static final String PUT = "PUT";

  @BeforeMethod(groups = {"webservice"})
  public void setUp() throws Exception {
    super.setup();
    super.setupDataExternalVendor(true);
  }

  @AfterMethod(groups = {"webservice"})
  public void tearDown() throws Exception {
    dbWrapper.deleteData();
    dbWrapper.closeConnection();
  }

  @Test(groups = {"webservice"}, dataProvider = "Data-Provider-Function-Positive")
  public void testFacilityFeedUsingUI(String user, String program, String[] credentials) throws Exception {
    HttpClient client = new HttpClient();
    client.createContext();

    LoginPage loginPage = new LoginPage(testWebDriver, baseUrlGlobal);

    dbWrapper.insertUser("200", user, "Ag/myf1Whs0fxr1FFfK8cs3q/VJ1qMs3yuMLDTeEcZEGzstj/waaUsQNQTIKk1U5JRzrDbPLCzCO1/vB5YGaEQ==", "F10", "Jane_Doe@openlmis.com", "openLmis");

    HomePage homePage = loginPage.loginAs(credentials[0], credentials[1]);

    CreateFacilityPage createFacilityPage = homePage.navigateCreateFacility();
    String geoZone = "Ngorongoro";
    String facilityType = "Lvl3 Hospital";
    String operatedBy = "MoH";
    String facilityCodePrefix = "FCcode";
    String facilityNamePrefix = "FCname";
    String catchmentPopulationValue = "100";

    String date_time = createFacilityPage.enterValuesInFacilityAndClickSave(facilityCodePrefix, facilityNamePrefix, program, geoZone, facilityType, operatedBy, catchmentPopulationValue);
    createFacilityPage.verifyMessageOnFacilityScreen(facilityNamePrefix + date_time, "created");


    ResponseEntity responseEntity = client.SendJSON("", URL + "?facilityCode=" + facilityCodePrefix + date_time + "", GET, commTrackUser, dbWrapper.getAuthToken(commTrackUser));
      assertTrue("Response entity : " + responseEntity.getResponse(), responseEntity.getResponse().contains("\"code\":\"" + facilityCodePrefix + date_time + "\""));
      assertTrue("Response entity : " + responseEntity.getResponse(), responseEntity.getResponse().contains("\"name\":\"" + facilityNamePrefix + date_time + "\""));
      assertTrue("Response entity : " + responseEntity.getResponse(), responseEntity.getResponse().contains("\"facilityType\":\"" + facilityType + "\""));
      assertTrue("Response entity : " + responseEntity.getResponse(), responseEntity.getResponse().contains("\"description\":\"Testing description\""));
      assertTrue("Response entity : " + responseEntity.getResponse(), responseEntity.getResponse().contains("\"mainPhone\":\"9711231305\""));
      assertTrue("Response entity : " + responseEntity.getResponse(), responseEntity.getResponse().contains("\"fax\":\"9711231305\""));
      assertTrue("Response entity : " + responseEntity.getResponse(), responseEntity.getResponse().contains("\"address1\":\"Address1\""));
      assertTrue("Response entity : " + responseEntity.getResponse(), responseEntity.getResponse().contains("\"address2\":\"Address2\""));
      assertTrue("Response entity : " + responseEntity.getResponse(), responseEntity.getResponse().contains("\"geographicZone\":\"" + geoZone + "\""));
      assertTrue("Response entity : " + responseEntity.getResponse(), responseEntity.getResponse().contains("\"catchmentPopulation\":" + catchmentPopulationValue + ""));
      assertTrue("Response entity : " + responseEntity.getResponse(), responseEntity.getResponse().contains("\"latitude\":-555.5555"));
      assertTrue("Response entity : " + responseEntity.getResponse(), responseEntity.getResponse().contains("\"longitude\":444.4444"));
      assertTrue("Response entity : " + responseEntity.getResponse(), responseEntity.getResponse().contains("\"altitude\":4545.4545"));
      assertTrue("Response entity : " + responseEntity.getResponse(), responseEntity.getResponse().contains("\"operatedBy\":\"" + operatedBy + "\""));
      assertTrue("Response entity : " + responseEntity.getResponse(), responseEntity.getResponse().contains("\"coldStorageGrossCapacity\":3434.3434"));
      assertTrue("Response entity : " + responseEntity.getResponse(), responseEntity.getResponse().contains("\"coldStorageNetCapacity\":3535.3535"));
      assertTrue("Response entity : " + responseEntity.getResponse(), responseEntity.getResponse().contains("\"suppliesOthers\":true"));
      assertTrue("Response entity : " + responseEntity.getResponse(), responseEntity.getResponse().contains("\"hasElectricity\":true"));
      assertTrue("Response entity : " + responseEntity.getResponse(), responseEntity.getResponse().contains("\"hasElectronicSCC\":true"));
      assertTrue("Response entity : " + responseEntity.getResponse(), responseEntity.getResponse().contains("\"hasElectronicDAR\":true"));
      assertTrue("Response entity : " + responseEntity.getResponse(), responseEntity.getResponse().contains("\"active\":true"));
      assertTrue("Response entity : " + responseEntity.getResponse(), responseEntity.getResponse().contains("\"virtualFacility\":false"));
      assertTrue("Response entity : " + responseEntity.getResponse(), responseEntity.getResponse().contains("\"comment\":\"Comments\""));


    DeleteFacilityPage deleteFacilityPage = homePage.navigateSearchFacility();
    deleteFacilityPage.searchFacility(date_time);
    deleteFacilityPage.clickFacilityList(date_time);
    deleteFacilityPage.disableFacility(facilityCodePrefix + date_time, facilityNamePrefix + date_time);
    deleteFacilityPage.verifyDisabledFacility(facilityCodePrefix + date_time, facilityNamePrefix + date_time);

    responseEntity = client.SendJSON("", URL + "?facilityCode=" + facilityCodePrefix + date_time + "", GET, commTrackUser, dbWrapper.getAuthToken(commTrackUser));
    assertTrue("Response entity : " + responseEntity.getResponse(), responseEntity.getResponse().contains("\"enabled\":false"));

    deleteFacilityPage.enableFacility();

    responseEntity = client.SendJSON("", URL + "?facilityCode=" + facilityCodePrefix + date_time + "", GET, commTrackUser, dbWrapper.getAuthToken(commTrackUser));
    assertTrue("Response entity : " + responseEntity.getResponse(), responseEntity.getResponse().contains("\"enabled\":true"));

    responseEntity = client.SendJSON("", "http://localhost:9091/feeds/facility/recent", "GET", "", "");


    deleteFacilityPage = homePage.navigateSearchFacility();
    deleteFacilityPage.searchFacility(date_time);
    deleteFacilityPage.clickFacilityList(date_time);
    createFacilityPage.addProgram("VACCINES", true);
    createFacilityPage.saveFacility();
    Thread.sleep(5000);

    deleteFacilityPage = homePage.navigateSearchFacility();
    deleteFacilityPage.searchFacility(date_time);
    deleteFacilityPage.clickFacilityList(date_time);
    createFacilityPage.removeFirstProgram();
    createFacilityPage.saveFacility();

    Thread.sleep(5000);

    homePage.logout(baseUrlGlobal);
  }

    @Test(groups = {"webservice"}, dataProvider = "Data-Provider-Function-Positive")
    public void testInvalidFacilityCode(String user, String program, String[] credentials) throws Exception {
        HttpClient client = new HttpClient();
        client.createContext();

        ResponseEntity responseEntity = client.SendJSON("", URL + "?facilityCode=F100" + "", GET, commTrackUser, dbWrapper.getAuthToken(commTrackUser));
        assertEquals(responseEntity.getResponse(), "{\"error\":\"Invalid Facility code\"}");
        assertEquals(responseEntity.getStatus(), 400) ;

    }

    @Test(groups = {"webservice"}, dataProvider = "Data-Provider-Function-Positive")
    public void testInvalidUser(String user, String program, String[] credentials) throws Exception {
        HttpClient client = new HttpClient();
        client.createContext();

        ResponseEntity responseEntity = client.SendJSON("", URL + "?facilityCode=F100" + "", GET, "ABCD", dbWrapper.getAuthToken(commTrackUser));
        assertTrue("Response:" + responseEntity.getResponse(), responseEntity.getResponse().contains("Error 401 Authentication Failed")) ;
        assertEquals(responseEntity.getStatus(), 401) ;

    }

    @Test(groups = {"webservice"}, dataProvider = "Data-Provider-Function-Positive")
    public void testInvalidPassword(String user, String program, String[] credentials) throws Exception {
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

