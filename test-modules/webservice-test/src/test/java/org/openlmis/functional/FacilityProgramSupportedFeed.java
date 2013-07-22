/*
 * Copyright © 2013 VillageReach.  All Rights Reserved.  This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 *
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package org.openlmis.functional;

import org.openlmis.UiUtils.HttpClient;
import org.openlmis.UiUtils.ResponseEntity;
import org.openlmis.UiUtils.TestCaseHelper;
import org.openlmis.pageobjects.CreateFacilityPage;
import org.openlmis.pageobjects.DeleteFacilityPage;
import org.openlmis.pageobjects.HomePage;
import org.openlmis.pageobjects.LoginPage;
import org.openqa.selenium.WebDriver;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import static com.thoughtworks.selenium.SeleneseTestNgHelper.assertEquals;
import static com.thoughtworks.selenium.SeleneseTestNgHelper.assertTrue;
import static com.thoughtworks.selenium.SeleneseTestNgHelper.assertFalse;


public class FacilityProgramSupportedFeed extends TestCaseHelper {
  public WebDriver driver;

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
  public void testFacilityProgramSupportedFeed(String user, String program, String[] credentials) throws Exception {
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

      String date_time = createFacilityPage.enterValuesInFacilityAndClickSave(facilityCodePrefix, facilityNamePrefix, program, geoZone, facilityType, operatedBy, "");
      createFacilityPage.verifyMessageOnFacilityScreen(facilityNamePrefix + date_time, "created");

      String str_date=date_time.split("-")[0].substring(0,6)+"25";
      DateFormat formatter ;
      Date d;
      formatter = new SimpleDateFormat("yyyyMMdd");
      d = (Date)formatter.parse(str_date);
      long dateLong = d.getTime();

      ResponseEntity responseEntity = client.SendJSON("", "http://localhost:9091/feeds/programSupported/recent", "GET", "", "");
      String expected="\"facilityCode\":\"" + facilityCodePrefix + date_time + "\",\"programsSupported\":[{\"code\":\"" + program + "\",\"name\":\"" + program + "\",\"active\":true,\"startDate\":" + dateLong;
      assertTrue(responseEntity.getResponse().contains(expected));

      DeleteFacilityPage deleteFacilityPage = homePage.navigateSearchFacility();
      deleteFacilityPage.searchFacility(date_time);
      deleteFacilityPage.clickFacilityList(date_time);
      createFacilityPage.addProgram("VACCINES", true);
      createFacilityPage.saveFacility();

      Thread.sleep(5000);
      responseEntity = client.SendJSON("", "http://localhost:9091/feeds/programSupported/recent", "GET", "", "");

      List<String> feedJSONList=XmlUtils.getNodeValues(responseEntity.getResponse(),"content");
      assertTrue(feedJSONList.get(1).contains("\"active\":true"));
      assertTrue(feedJSONList.get(1).contains("\"active\":false"));

      deleteFacilityPage = homePage.navigateSearchFacility();
      deleteFacilityPage.searchFacility(date_time);
      deleteFacilityPage.clickFacilityList(date_time);
      createFacilityPage.removeFirstProgram();
      createFacilityPage.saveFacility();

      Thread.sleep(5000);
      responseEntity = client.SendJSON("", "http://localhost:9091/feeds/programSupported/recent", "GET", "", "");

      feedJSONList=XmlUtils.getNodeValues(responseEntity.getResponse(),"content");
      assertTrue(feedJSONList.get(2).contains("\"active\":false"));
      assertFalse(feedJSONList.get(2).contains("\"active\":true"));

      deleteFacilityPage = homePage.navigateSearchFacility();
      deleteFacilityPage.searchFacility(date_time);
      deleteFacilityPage.clickFacilityList(date_time);
      createFacilityPage.activeInactiveFirstProgram();
      createFacilityPage.saveFacility();

      Thread.sleep(5000);
      responseEntity = client.SendJSON("", "http://localhost:9091/feeds/programSupported/recent", "GET", "", "");

      feedJSONList=XmlUtils.getNodeValues(responseEntity.getResponse(),"content");
      assertTrue(feedJSONList.get(3).contains("\"active\":true"));
      assertFalse(feedJSONList.get(3).contains("\"active\":false"));

  }
    @DataProvider(name = "Data-Provider-Function-Positive")
    public Object[][] parameterIntTestProviderPositive() {
        return new Object[][]{
                {"User123", "HIV", new String[]{"Admin123", "Admin123"}}
        };
    }
}

