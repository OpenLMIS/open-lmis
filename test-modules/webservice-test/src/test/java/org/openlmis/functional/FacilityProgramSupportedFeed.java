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
import org.openlmis.pageobjects.*;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import static com.thoughtworks.selenium.SeleneseTestNgHelper.assertFalse;
import static com.thoughtworks.selenium.SeleneseTestNgHelper.assertTrue;


public class FacilityProgramSupportedFeed extends JsonUtility {

  @BeforeMethod(groups = {"webservice"})
  public void setUp() throws Exception {
    super.setup();
    super.setupTestData(true);
  }

  @AfterMethod(groups = {"webservice"})
  public void tearDown() throws Exception {
    HomePage homePage = new HomePage(testWebDriver);
    homePage.logout(baseUrlGlobal);
    dbWrapper.deleteData();
    dbWrapper.closeConnection();
  }

  @Test(groups = {"webservice"}, dataProvider = "Data-Provider-Function-Positive")
  public void testFacilityProgramSupportedFeed_Upload(String user, String program, String[] credentials) throws Exception {
    HttpClient client = new HttpClient();
    client.createContext();

    LoginPage loginPage = new LoginPage(testWebDriver, baseUrlGlobal);

    HomePage homePage = loginPage.loginAs(credentials[0], credentials[1]);
    UploadPage uploadPage = homePage.navigateUploads();
    uploadPage.uploadProgramSupportedByFacilities("QA_program_supported_WebService.csv");
    Thread.sleep(5000);
    ResponseEntity responseEntity = client.SendJSON("", "http://localhost:9091/feeds/program-supported/recent", "GET", "", "");
    String expected = "{\"code\":\"" + program + "\",\"name\":\"" + program + "\",\"active\":true,\"startDate\":1296585000000}";
    assertTrue(responseEntity.getResponse().contains(expected));

    uploadPage.uploadProgramSupportedByFacilities("QA_program_supported_Subsequent_WebService.csv");
    Thread.sleep(5000);
    responseEntity = client.SendJSON("", "http://localhost:9091/feeds/program-supported/recent", "GET", "", "");

    List<String> feedJSONList = XmlUtils.getNodeValues(responseEntity.getResponse(), "content");
    expected = "{\"code\":\"" + program + "\",\"name\":\"" + program + "\",\"active\":false,\"startDate\":1296585000000}";
    String expected1 = "{\"code\":\"" + program + "\",\"name\":\"" + program + "\",\"active\":true,\"startDate\":1304533800000}";

    assertTrue(feedJSONList.get(1).contains(expected));
    assertTrue(feedJSONList.get(2).contains(expected1));
  }

  @Test(groups = {"webservice"}, dataProvider = "Data-Provider-Function-Positive")
  public void testFacilityProgramSupportedFeed(String user, String program, String[] credentials) throws Exception {
    HttpClient client = new HttpClient();
    client.createContext();

    LoginPage loginPage = new LoginPage(testWebDriver, baseUrlGlobal);

    dbWrapper.insertUser("200", user, "Ag/myf1Whs0fxr1FFfK8cs3q/VJ1qMs3yuMLDTeEcZEGzstj/waaUsQNQTIKk1U5JRzrDbPLCzCO1/vB5YGaEQ==", "F10", "Jane_Doe@openlmis.com");

    HomePage homePage = loginPage.loginAs(credentials[0], credentials[1]);

    CreateFacilityPage createFacilityPage = homePage.navigateCreateFacility();
    String geoZone = "Ngorongoro";
    String facilityType = "Lvl3 Hospital";
    String operatedBy = "MoH";
    String facilityCodePrefix = "FCcode";
    String facilityNamePrefix = "FCname";

    String date_time = createFacilityPage.enterValuesInFacilityAndClickSave(facilityCodePrefix, facilityNamePrefix, program, geoZone, facilityType, operatedBy, "");
    createFacilityPage.verifyMessageOnFacilityScreen(facilityNamePrefix + date_time, "created");

    String str_date = date_time.split("-")[0].substring(0, 6) + "25";
    DateFormat formatter;
    Date d;
    formatter = new SimpleDateFormat("yyyyMMdd");
    d = (Date) formatter.parse(str_date);
    long dateLong = d.getTime();

    ResponseEntity responseEntity = client.SendJSON("", "http://localhost:9091/feeds/program-supported/recent", "GET", "", "");
    String expected = "\"facilityCode\":\"" + facilityCodePrefix + date_time + "\",\"programsSupported\":[{\"code\":\"" + program + "\",\"name\":\"" + program + "\",\"active\":true,\"startDate\":" + dateLong;
    assertTrue(responseEntity.getResponse().contains(expected));

    DeleteFacilityPage deleteFacilityPage = homePage.navigateSearchFacility();
    deleteFacilityPage.searchFacility(date_time);
    deleteFacilityPage.clickFacilityList(date_time);
    createFacilityPage.addProgram("VACCINES", true);
    createFacilityPage.saveFacility();

    Thread.sleep(5000);
    responseEntity = client.SendJSON("", "http://localhost:9091/feeds/program-supported/recent", "GET", "", "");

    List<String> feedJSONList = XmlUtils.getNodeValues(responseEntity.getResponse(), "content");
    assertTrue("responseEntity.getResponse() : "+responseEntity.getResponse(),feedJSONList.get(1).contains("\"active\":true"));
    assertTrue("responseEntity.getResponse() : "+responseEntity.getResponse(),feedJSONList.get(1).contains("\"active\":false"));

    deleteFacilityPage = homePage.navigateSearchFacility();
    deleteFacilityPage.searchFacility(date_time);
    deleteFacilityPage.clickFacilityList(date_time);
    createFacilityPage.removeFirstProgram();
    createFacilityPage.saveFacility();

    Thread.sleep(5000);
    responseEntity = client.SendJSON("", "http://localhost:9091/feeds/program-supported/recent", "GET", "", "");
    responseEntity = client.SendJSON("", "http://localhost:9091/feeds/program-supported/recent", "GET", "", "");

    feedJSONList = XmlUtils.getNodeValues(responseEntity.getResponse(), "content");
    assertTrue("feedJSONList.get(2) : "+feedJSONList.get(2),feedJSONList.get(2).contains("\"active\":false"));
    assertFalse("feedJSONList.get(2) : "+feedJSONList.get(2),feedJSONList.get(2).contains("\"active\":true"));

    deleteFacilityPage = homePage.navigateSearchFacility();
    deleteFacilityPage.searchFacility(date_time);
    deleteFacilityPage.clickFacilityList(date_time);
    createFacilityPage.activeInactiveFirstProgram();
    createFacilityPage.saveFacility();

    Thread.sleep(5000);
    responseEntity = client.SendJSON("", "http://localhost:9091/feeds/program-supported/recent", "GET", "", "");

    feedJSONList = XmlUtils.getNodeValues(responseEntity.getResponse(), "content");
    assertTrue("responseEntity.getResponse() : "+responseEntity.getResponse(),feedJSONList.get(3).contains("\"active\":true"));
    assertFalse("responseEntity.getResponse() : "+responseEntity.getResponse(),feedJSONList.get(3).contains("\"active\":false"));

  }

  @DataProvider(name = "Data-Provider-Function-Positive")
  public Object[][] parameterIntTestProviderPositive() {
    return new Object[][]{
      {"User123", "HIV", new String[]{"Admin123", "Admin123"}}
    };
  }

}

