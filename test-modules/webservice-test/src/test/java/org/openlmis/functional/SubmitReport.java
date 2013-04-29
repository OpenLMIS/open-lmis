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
import static org.openlmis.UiUtils.HttpClient.GET;
import static org.openlmis.UiUtils.HttpClient.POST;


public class SubmitReport extends TestCaseHelper {

    public static final String MINIMUM_JSON_TXT_FILE_NAME = "MinimumJson.txt";
    public static final String FULL_JSON_TXT_FILE_NAME = "FullJson.txt";
    public static final String PRODUCT_JSON_TXT_FILE_NAME = "ProductJson.txt";

    public WebDriver driver;

    public Utils utility = new Utils();

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
        String json = utility.readJSON(FULL_JSON_TXT_FILE_NAME);
        json = utility.updateJSON(json, "facilityId", "100");
        json = utility.updateJSON(json, "periodId", dbWrapper.getPeriodID("Period2"));
        json = utility.updateJSON(json, "programId", dbWrapper.getProgramID("HIV"));

        ResponseEntity responseEntity = client.SendJSON(json, "http://localhost:9091/rest-api/requisitions.json", POST,
                "commTrack",
                dbWrapper.getAuthToken("commTrack"));

        String response = responseEntity.getResponse();
        client.SendJSON("", "http://localhost:9091/", GET, "", "");

        assertEquals(400, responseEntity.getStatus());
        assertEquals(response, "{\"error\":\"User doesn't have access to Program & Facility.\"}");
    }

    @Test(groups = {"webservice"})
    public void testSubmitReportInvalidProgram() throws Exception {
        HttpClient client = new HttpClient();
        client.createContext();

        String json = utility.readJSON(FULL_JSON_TXT_FILE_NAME);
        json = utility.updateJSON(json, "facilityId", dbWrapper.getFacilityID("F10"));
        json = utility.updateJSON(json, "periodId", dbWrapper.getPeriodID("Period2"));
        json = utility.updateJSON(json, "programId", "500");

        ResponseEntity responseEntity = client.SendJSON(json, "http://localhost:9091/rest-api/requisitions.json", POST,
                "commTrack",
                dbWrapper.getAuthToken("commTrack"));

        String response = responseEntity.getResponse();
        client.SendJSON("", "http://localhost:9091/", GET, "", "");
        assertEquals(400, responseEntity.getStatus());
        assertEquals(response, "{\"error\":\"User doesn't have access to Program & Facility.\"}");
    }

    @Test(groups = {"webservice"})
    public void testSubmitReportValidRnR() throws Exception {
        String response = utility.submitReport();
        assertTrue(response.contains("{\"R&R\":"));
    }

    @Test(groups = {"webservice"})
    public void testDuplicateSubmitReport() throws Exception {
        HttpClient client = new HttpClient();
        client.createContext();
        String json = utility.readJSON(FULL_JSON_TXT_FILE_NAME);
        json = utility.updateJSON(json, "facilityId", dbWrapper.getFacilityID("F10"));
        json = utility.updateJSON(json, "periodId", dbWrapper.getPeriodID("Period2"));
        json = utility.updateJSON(json, "programId", dbWrapper.getProgramID("HIV"));

        client.SendJSON(json, "http://localhost:9091/rest-api/requisitions.json", POST,
                "commTrack",
                dbWrapper.getAuthToken("commTrack"));

        ResponseEntity responseEntity = client.SendJSON(json, "http://localhost:9091/rest-api/requisitions.json", POST,
                "commTrack",
                dbWrapper.getAuthToken("commTrack"));

        client.SendJSON("", "http://localhost:9091/", GET, "", "");
        assertEquals(400, responseEntity.getStatus());
        assertEquals(responseEntity.getResponse(), "{\"error\":\"Invalid period.\"}");
    }

    @Test(groups = {"webservice"})
    public void testBlankProductSubmitReport() throws Exception {
        HttpClient client = new HttpClient();
        client.createContext();
        String json = utility.readJSON(PRODUCT_JSON_TXT_FILE_NAME);
        json = utility.updateJSON(json, "facilityId", dbWrapper.getFacilityID("F10"));
        json = utility.updateJSON(json, "periodId", dbWrapper.getPeriodID("Period2"));
        json = utility.updateJSON(json, "programId", dbWrapper.getProgramID("HIV"));

        ResponseEntity responseEntity = client.SendJSON(json, "http://localhost:9091/rest-api/requisitions.json", POST,
                "commTrack",
                dbWrapper.getAuthToken("commTrack"));

        String response = responseEntity.getResponse();
        client.SendJSON("", "http://localhost:9091/", GET, "", "");
        assertEquals(400, responseEntity.getStatus());
        assertEquals(response, "{\"error\":\"Invalid data.\"}");
    }

    @Test(groups = {"webservice"})
    public void testInvalidProductSubmitReport() throws Exception {
        HttpClient client = new HttpClient();
        client.createContext();
        String json = utility.readJSON(PRODUCT_JSON_TXT_FILE_NAME);
        json = utility.updateJSON(json, "facilityId", dbWrapper.getFacilityID("F10"));
        json = utility.updateJSON(json, "periodId", dbWrapper.getPeriodID("Period2"));
        json = utility.updateJSON(json, "programId", dbWrapper.getProgramID("HIV"));
        json = utility.updateJSON(json, "productCode", "P10000");

        ResponseEntity responseEntity = client.SendJSON(json, "http://localhost:9091/rest-api/requisitions.json", POST,
                "commTrack",
                dbWrapper.getAuthToken("commTrack"));

        String response = responseEntity.getResponse();

        client.SendJSON("", "http://localhost:9091/", GET, "", "");
        assertEquals(400, responseEntity.getStatus());
        assertEquals(response, "{\"error\":\"Invalid data.\"}");
    }

    @Test(groups = {"webservice"})
    public void testBlankBeginningBalanceSubmitReport() throws Exception {
        HttpClient client = new HttpClient();
        client.createContext();
        String json = utility.readJSON(PRODUCT_JSON_TXT_FILE_NAME);
        json = utility.updateJSON(json, "facilityId", dbWrapper.getFacilityID("F10"));
        json = utility.updateJSON(json, "periodId", dbWrapper.getPeriodID("Period2"));
        json = utility.updateJSON(json, "programId", dbWrapper.getProgramID("HIV"));
        json = utility.updateJSON(json, "productCode", "P10");

        ResponseEntity responseEntity = client.SendJSON(json, "http://localhost:9091/rest-api/requisitions.json", POST,
                "commTrack",
                dbWrapper.getAuthToken("commTrack"));

        String response = responseEntity.getResponse();

        client.SendJSON("", "http://localhost:9091/", GET, "", "");
        assertEquals(400, responseEntity.getStatus());
        assertEquals(response, "{\"error\":\"Invalid data.\"}");
    }

    @Test(groups = {"webservice"})
    public void testMinimumSubmitReportValidRnR() throws Exception {
        HttpClient client = new HttpClient();
        client.createContext();
        String json = utility.readJSON(MINIMUM_JSON_TXT_FILE_NAME);
        json = utility.updateJSON(json, "userId", "commTrack");
        json = utility.updateJSON(json, "facilityId", dbWrapper.getFacilityID("F10"));
        json = utility.updateJSON(json, "periodId", dbWrapper.getPeriodID("Period2"));
        json = utility.updateJSON(json, "programId", dbWrapper.getProgramID("HIV"));
        json = utility.updateJSON(json, "productCode", "P10");

        ResponseEntity responseEntity = client.SendJSON(json, "http://localhost:9091/rest-api/requisitions.json", POST,
                "commTrack",
                dbWrapper.getAuthToken("commTrack"));

        String response = responseEntity.getResponse();

        client.SendJSON("", "http://localhost:9091/", GET, "", "");
        assertEquals(200, responseEntity.getStatus());
        assertTrue(response.contains("{\"R&R\":"));
    }

    @Test(groups = {"webservice"})
    public void testSubmitReportInvalidUser() throws Exception {
        HttpClient client = new HttpClient();
        client.createContext();
        String json = utility.readJSON(MINIMUM_JSON_TXT_FILE_NAME);
        json = utility.updateJSON(json, "facilityId", dbWrapper.getFacilityID("F10"));
        json = utility.updateJSON(json, "periodId", dbWrapper.getPeriodID("Period2"));
        json = utility.updateJSON(json, "programId", dbWrapper.getProgramID("HIV"));
        json = utility.updateJSON(json, "productCode", "P10");
        json = utility.updateJSON(json, "userId", "commTrack100");

        ResponseEntity responseEntity = client.SendJSON(json, "http://localhost:9091/rest-api/requisitions.json", POST,
                "commTrack",
                dbWrapper.getAuthToken("commTrack"));

        String response = responseEntity.getResponse();

        client.SendJSON("", "http://localhost:9091/", GET, "", "");
        assertEquals(400, responseEntity.getStatus());
        assertEquals(response, "{\"error\":\"Please provide a valid username\"}");
    }

    @Test(groups = {"webservice"})
    public void shouldReturn401StatusWhenSubmittingReportWithInvalidVendor() throws Exception {
        HttpClient client = new HttpClient();
        client.createContext();

        String json = utility.readJSON(MINIMUM_JSON_TXT_FILE_NAME);
        json = utility.updateJSON(json, "userId", "commTrack");
        json = utility.updateJSON(json, "facilityId", dbWrapper.getFacilityID("F10"));
        json = utility.updateJSON(json, "periodId", dbWrapper.getPeriodID("Period2"));
        json = utility.updateJSON(json, "programId", dbWrapper.getProgramID("HIV"));
        json = utility.updateJSON(json, "productCode", "P10");

        ResponseEntity responseEntity = client.SendJSON(json, "http://localhost:9091/rest-api/requisitions.json", POST,
                "commTrack1000", dbWrapper.getAuthToken("commTrack"));

        assertEquals(401, responseEntity.getStatus());
    }
}

