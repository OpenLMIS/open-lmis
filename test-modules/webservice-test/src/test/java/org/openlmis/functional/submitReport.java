/*
 * Copyright © 2013 VillageReach.  All Rights Reserved.  This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 *
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package org.openlmis.functional;

import org.openlmis.UiUtils.HttpClient;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.testng.annotations.*;

import org.openlmis.UiUtils.TestCaseHelper;

import static com.thoughtworks.selenium.SeleneseTestNgHelper.*;


public class submitReport extends TestCaseHelper {
    public WebDriver driver;
    public Utils  utillity = new Utils();
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
        String fileName = this.getClass().getClassLoader().getResource("FullJSON.txt").getFile();
        String json = utillity.readJSON(fileName);
        json = utillity.updateJSON(json, "facilityId", "100");
        json = utillity.updateJSON(json, "periodId", dbWrapper.getPeriodID("Period2"));
        json = utillity.updateJSON(json, "programId", dbWrapper.getProgramID("HIV"));
        String response = client.SendJSON(json, "http://localhost:9091/rest-api/requisitions.json", "POST", "commTrack", dbWrapper.getAuthToken("commTrack"));
        client.SendJSON("", "http://localhost:9091/", "GET", "", "");

        assertEquals(response, "{\"error\":\"User doesn't have access to Program & Facility.\"}");
    }

    @Test(groups = {"webservice"})
    public void testSubmitReportInvalidProgram() throws Exception {
        HttpClient client = new HttpClient();
        client.createContext();
        String fileName = this.getClass().getClassLoader().getResource("FullJSON.txt").getFile();
        String json = utillity.readJSON(fileName);
        json = utillity.updateJSON(json, "facilityId", dbWrapper.getFacilityID("F10"));
        json = utillity.updateJSON(json, "periodId", dbWrapper.getPeriodID("Period2"));
        json = utillity.updateJSON(json, "programId", "500");
        String response = client.SendJSON(json, "http://localhost:9091/rest-api/requisitions.json", "POST", "commTrack", dbWrapper.getAuthToken("commTrack"));
        client.SendJSON("", "http://localhost:9091/", "GET", "", "");

        assertEquals(response, "{\"error\":\"User doesn't have access to Program & Facility.\"}");
    }

    @Test(groups = {"webservice"})
    public void testSubmitReportValidRnR() throws Exception {
        String response = utillity.submitReport();
        assertTrue(response.contains("{\"R&R\":"));
    }

    @Test(groups = {"webservice"})
    public void testDuplicateSubmitReport() throws Exception {
        HttpClient client = new HttpClient();
        client.createContext();
        String fileName = this.getClass().getClassLoader().getResource("FullJSON.txt").getFile();
        String json = utillity.readJSON(fileName);
        json = utillity.updateJSON(json, "facilityId", dbWrapper.getFacilityID("F10"));
        json = utillity.updateJSON(json, "periodId", dbWrapper.getPeriodID("Period2"));
        json = utillity.updateJSON(json, "programId", dbWrapper.getProgramID("HIV"));
        String response = client.SendJSON(json, "http://localhost:9091/rest-api/requisitions.json", "POST", "commTrack", dbWrapper.getAuthToken("commTrack"));
        //client.SendJSON("", "http://localhost:9091/", "GET", "", "");

        response = client.SendJSON(json, "http://localhost:9091/rest-api/requisitions.json", "POST", "commTrack", dbWrapper.getAuthToken("commTrack"));
        client.SendJSON("", "http://localhost:9091/", "GET", "", "");

        assertEquals(response, "{\"error\":\"Invalid period.\"}");
    }

    @Test(groups = {"webservice"})
    public void testBlankProductSubmitReport() throws Exception {
        HttpClient client = new HttpClient();
        client.createContext();
        String fileName = this.getClass().getClassLoader().getResource("ProductJSON.txt").getFile();
        String json = utillity.readJSON(fileName);
        json = utillity.updateJSON(json, "facilityId", dbWrapper.getFacilityID("F10"));
        json = utillity.updateJSON(json, "periodId", dbWrapper.getPeriodID("Period2"));
        json = utillity.updateJSON(json, "programId", dbWrapper.getProgramID("HIV"));
        String response = client.SendJSON(json, "http://localhost:9091/rest-api/requisitions.json", "POST", "commTrack", dbWrapper.getAuthToken("commTrack"));
        client.SendJSON("", "http://localhost:9091/", "GET", "", "");

        assertEquals(response, "{\"error\":\"Invalid data.\"}");
    }

    @Test(groups = {"webservice"})
    public void testInvalidProductSubmitReport() throws Exception {
        HttpClient client = new HttpClient();
        client.createContext();
        String fileName = this.getClass().getClassLoader().getResource("ProductJSON.txt").getFile();
        String json = utillity.readJSON(fileName);
        json = utillity.updateJSON(json, "facilityId", dbWrapper.getFacilityID("F10"));
        json = utillity.updateJSON(json, "periodId", dbWrapper.getPeriodID("Period2"));
        json = utillity.updateJSON(json, "programId", dbWrapper.getProgramID("HIV"));
        json = utillity.updateJSON(json, "productCode", "P10000");

        String response = client.SendJSON(json, "http://localhost:9091/rest-api/requisitions.json", "POST", "commTrack", dbWrapper.getAuthToken("commTrack"));
        client.SendJSON("", "http://localhost:9091/", "GET", "", "");

        assertEquals(response, "{\"error\":\"Invalid data.\"}");
    }

    @Test(groups = {"webservice"})
    public void testBlankBeginningBalanceSubmitReport() throws Exception {
        HttpClient client = new HttpClient();
        client.createContext();
        String fileName = this.getClass().getClassLoader().getResource("ProductJSON.txt").getFile();
        String json = utillity.readJSON(fileName);
        json = utillity.updateJSON(json, "facilityId", dbWrapper.getFacilityID("F10"));
        json = utillity.updateJSON(json, "periodId", dbWrapper.getPeriodID("Period2"));
        json = utillity.updateJSON(json, "programId", dbWrapper.getProgramID("HIV"));
        json = utillity.updateJSON(json, "productCode", "P10");
        String response = client.SendJSON(json, "http://localhost:9091/rest-api/requisitions.json", "POST", "commTrack", dbWrapper.getAuthToken("commTrack"));
        client.SendJSON("", "http://localhost:9091/", "GET", "", "");

        assertEquals(response, "{\"error\":\"Invalid data.\"}");
    }

    @Test(groups = {"webservice"})
    public void testMinimumSubmitReportValidRnR() throws Exception {
        HttpClient client = new HttpClient();
        client.createContext();
        String fileName = this.getClass().getClassLoader().getResource("MinimumJSON.txt").getFile();
        String json = utillity.readJSON(fileName);
        json = utillity.updateJSON(json, "userId", "commTrack");
        json = utillity.updateJSON(json, "facilityId", dbWrapper.getFacilityID("F10"));
        json = utillity.updateJSON(json, "periodId", dbWrapper.getPeriodID("Period2"));
        json = utillity.updateJSON(json, "programId", dbWrapper.getProgramID("HIV"));
        json = utillity.updateJSON(json, "productCode", "P10");
        String response = client.SendJSON(json, "http://localhost:9091/rest-api/requisitions.json", "POST", "commTrack", dbWrapper.getAuthToken("commTrack"));
        client.SendJSON("", "http://localhost:9091/", "GET", "", "");

        assertTrue(response.contains("{\"R&R\":"));
    }

    @Test(groups = {"webservice"})
    public void testSubmitReportInvalidUser() throws Exception {
        HttpClient client = new HttpClient();
        client.createContext();
        String fileName = this.getClass().getClassLoader().getResource("MinimumJSON.txt").getFile();
        String json = utillity.readJSON(fileName);
        json = utillity.updateJSON(json, "facilityId", dbWrapper.getFacilityID("F10"));
        json = utillity.updateJSON(json, "periodId", dbWrapper.getPeriodID("Period2"));
        json = utillity.updateJSON(json, "programId", dbWrapper.getProgramID("HIV"));
        json = utillity.updateJSON(json, "productCode", "P10");
        json = utillity.updateJSON(json, "userId", "commTrack100");
        String response = client.SendJSON(json, "http://localhost:9091/rest-api/requisitions.json", "POST", "commTrack", dbWrapper.getAuthToken("commTrack"));
        client.SendJSON("", "http://localhost:9091/", "GET", "", "");

        assertEquals(response, "{\"error\":\"Please provide a valid username\"}");
    }

    @Test(groups = {"webservice"})
    public void testMinimumSubmitReportInvalidVendor() throws Exception {
        HttpClient client = new HttpClient();
        client.createContext();
        String fileName = this.getClass().getClassLoader().getResource("MinimumJSON.txt").getFile();
        String json = utillity.readJSON(fileName);
        json = utillity.updateJSON(json, "userId", "commTrack");
        json = utillity.updateJSON(json, "facilityId", dbWrapper.getFacilityID("F10"));
        json = utillity.updateJSON(json, "periodId", dbWrapper.getPeriodID("Period2"));
        json = utillity.updateJSON(json, "programId", dbWrapper.getProgramID("HIV"));
        json = utillity.updateJSON(json, "productCode", "P10");
        String response = client.SendJSON(json, "http://localhost:9091/rest-api/requisitions.json", "POST", "commTrack100", dbWrapper.getAuthToken("commTrack"));
        client.SendJSON("", "http://localhost:9091/", "GET", "", "");

        assertEquals(response, "<html>");

    }
}

