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

import java.io.File;
import java.io.IOException;
import java.security.PublicKey;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import org.openlmis.UiUtils.TestCaseHelper;

import static com.thoughtworks.selenium.SeleneseTestNgHelper.*;
import static java.lang.System.getProperty;


public class submitReport extends TestCaseHelper {
    public WebDriver driver;
    @BeforeMethod(groups = {"webservice"})
    public void setUp() throws Exception {
        driver = new FirefoxDriver();
        driver.get("http://localhost:9091");
        super.setup();
    }
    @AfterMethod(groups = {"webservice"})
    public void tearDown() {
        driver.close();
    }
/*
    @Test(groups = {"webservice"})
    public void testSubmitReportInvalidFacility() throws Exception {
        setupData();
        HttpClient client = new HttpClient();
        client.createContext();
        String fileName = this.getClass().getClassLoader().getResource("FullJSON.txt").getFile();
        String json = readJSON(fileName);
        json = updateJSON(json, "facilityId", "100");
        json = updateJSON(json, "periodId", dbWrapper.getPeriodID("Period2"));
        json = updateJSON(json, "programId", dbWrapper.getProgramID("HIV"));
        String response = client.SendJSON(json, "http://localhost:9091/rest-api/requisitions.json", "POST", "commTrack", dbWrapper.getAuthToken("commTrack"));
        client.SendJSON("", "http://localhost:9091/", "GET", "", "");

        assertEquals(response, "{\"error\":\"User doesn't have access to Program & Facility.\"}");
    }

    @Test(groups = {"webservice"})
    public void testSubmitReportInvalidProgram() throws Exception {
        setupData();
        HttpClient client = new HttpClient();
        client.createContext();
        String fileName = this.getClass().getClassLoader().getResource("FullJSON.txt").getFile();
        String json = readJSON(fileName);
        json = updateJSON(json, "facilityId", dbWrapper.getFacilityID("F10"));
        json = updateJSON(json, "periodId", dbWrapper.getPeriodID("Period2"));
        json = updateJSON(json, "programId", "500");
        String response = client.SendJSON(json, "http://localhost:9091/rest-api/requisitions.json", "POST", "commTrack", dbWrapper.getAuthToken("commTrack"));
        client.SendJSON("", "http://localhost:9091/", "GET", "", "");

        assertEquals(response, "{\"error\":\"User doesn't have access to Program & Facility.\"}");
    }

    @Test(groups = {"webservice"})
    public void testSubmitReportValidRnR() throws Exception {
        setupData();
        HttpClient client = new HttpClient();
        client.createContext();
        String fileName = this.getClass().getClassLoader().getResource("FullJSON.txt").getFile();
        String json = readJSON(fileName);
        json = updateJSON(json, "facilityId", dbWrapper.getFacilityID("F10"));
        json = updateJSON(json, "periodId", dbWrapper.getPeriodID("Period2"));
        json = updateJSON(json, "programId", dbWrapper.getProgramID("HIV"));
        String response = client.SendJSON(json, "http://localhost:9091/rest-api/requisitions.json", "POST", "commTrack", dbWrapper.getAuthToken("commTrack"));
        client.SendJSON("", "http://localhost:9091/", "GET", "", "");

        assertTrue(response.contains("{\"R&R\":"));
    }

    @Test(groups = {"webservice"})
    public void testDuplicateSubmitReport() throws Exception {
        setupData();
        HttpClient client = new HttpClient();
        client.createContext();
        String fileName = this.getClass().getClassLoader().getResource("FullJSON.txt").getFile();
        String json = readJSON(fileName);
        json = updateJSON(json, "facilityId", dbWrapper.getFacilityID("F10"));
        json = updateJSON(json, "periodId", dbWrapper.getPeriodID("Period2"));
        json = updateJSON(json, "programId", dbWrapper.getProgramID("HIV"));
        String response = client.SendJSON(json, "http://localhost:9091/rest-api/requisitions.json", "POST", "commTrack", dbWrapper.getAuthToken("commTrack"));
        //client.SendJSON("", "http://localhost:9091/", "GET", "", "");

        response = client.SendJSON(json, "http://localhost:9091/rest-api/requisitions.json", "POST", "commTrack", dbWrapper.getAuthToken("commTrack"));
        client.SendJSON("", "http://localhost:9091/", "GET", "", "");

        assertEquals(response, "{\"error\":\"Invalid period.\"}");
    }

    @Test(groups = {"webservice"})
    public void testBlankProductSubmitReport() throws Exception {
        setupData();
        HttpClient client = new HttpClient();
        client.createContext();
        String fileName = this.getClass().getClassLoader().getResource("ProductJSON.txt").getFile();
        String json = readJSON(fileName);
        json = updateJSON(json, "facilityId", dbWrapper.getFacilityID("F10"));
        json = updateJSON(json, "periodId", dbWrapper.getPeriodID("Period2"));
        json = updateJSON(json, "programId", dbWrapper.getProgramID("HIV"));
        String response = client.SendJSON(json, "http://localhost:9091/rest-api/requisitions.json", "POST", "commTrack", dbWrapper.getAuthToken("commTrack"));
        client.SendJSON("", "http://localhost:9091/", "GET", "", "");

        assertEquals(response, "{\"error\":\"Invalid data.\"}");
    }

    @Test(groups = {"webservice"})
    public void testBlankBeginningBalanceSubmitReport() throws Exception {
        setupData();
        HttpClient client = new HttpClient();
        client.createContext();
        String fileName = this.getClass().getClassLoader().getResource("ProductJSON.txt").getFile();
        String json = readJSON(fileName);
        json = updateJSON(json, "facilityId", dbWrapper.getFacilityID("F10"));
        json = updateJSON(json, "periodId", dbWrapper.getPeriodID("Period2"));
        json = updateJSON(json, "programId", dbWrapper.getProgramID("HIV"));
        json = updateJSON(json, "productCode", "P10");
        String response = client.SendJSON(json, "http://localhost:9091/rest-api/requisitions.json", "POST", "commTrack", dbWrapper.getAuthToken("commTrack"));
        client.SendJSON("", "http://localhost:9091/", "GET", "", "");

        assertEquals(response, "{\"error\":\"Invalid data.\"}");
    }

    @Test(groups = {"webservice"})
    public void testMinimumSubmitReportValidRnR() throws Exception {
        setupData();
        HttpClient client = new HttpClient();
        client.createContext();
        String fileName = this.getClass().getClassLoader().getResource("MinimumJSON.txt").getFile();
        String json = readJSON(fileName);
        json = updateJSON(json, "userId", "commTrack");
        json = updateJSON(json, "facilityId", dbWrapper.getFacilityID("F10"));
        json = updateJSON(json, "periodId", dbWrapper.getPeriodID("Period2"));
        json = updateJSON(json, "programId", dbWrapper.getProgramID("HIV"));
        json = updateJSON(json, "productCode", "P10");
        String response = client.SendJSON(json, "http://localhost:9091/rest-api/requisitions.json", "POST", "commTrack", dbWrapper.getAuthToken("commTrack"));
        client.SendJSON("", "http://localhost:9091/", "GET", "", "");

        assertTrue(response.contains("{\"R&R\":"));
    }

    @Test(groups = {"webservice"})
    public void testSubmitReportInvalidUser() throws Exception {
        setupData();
        HttpClient client = new HttpClient();
        client.createContext();
        String fileName = this.getClass().getClassLoader().getResource("MinimumJSON.txt").getFile();
        String json = readJSON(fileName);
        json = updateJSON(json, "facilityId", dbWrapper.getFacilityID("F10"));
        json = updateJSON(json, "periodId", dbWrapper.getPeriodID("Period2"));
        json = updateJSON(json, "programId", dbWrapper.getProgramID("HIV"));
        json = updateJSON(json, "productCode", "P10");
        json = updateJSON(json, "userId", "commTrack1");
        String response = client.SendJSON(json, "http://localhost:9091/rest-api/requisitions.json", "POST", "commTrack", dbWrapper.getAuthToken("commTrack"));
        client.SendJSON("", "http://localhost:9091/", "GET", "", "");

        assertEquals(response, "{\"error\":\"Please provide a valid username\"}");
    }
*/
    @Test(groups = {"webservice"})
    public void testSubmitAndApproveReportValidRnR() throws Exception {
        setupData();
        setupDataApprover();
        HttpClient client = new HttpClient();
        client.createContext();
        String fileName = this.getClass().getClassLoader().getResource("FullJSON.txt").getFile();
        String json = readJSON(fileName);
        json = updateJSON(json, "facilityId", dbWrapper.getFacilityID("F10"));
        json = updateJSON(json, "periodId", dbWrapper.getPeriodID("Period2"));
        json = updateJSON(json, "programId", dbWrapper.getProgramID("HIV"));
        String response = client.SendJSON(json, "http://localhost:9091/rest-api/requisitions.json", "POST", "commTrack", dbWrapper.getAuthToken("commTrack"));
        client.SendJSON("", "http://localhost:9091/", "GET", "", "");
        assertTrue(response.contains("{\"R&R\":"));

        String id= response.substring(response.lastIndexOf(":")+1,response.lastIndexOf("}"));

        fileName = this.getClass().getClassLoader().getResource("FullJSON_Approve.txt").getFile();
        json = readJSON(fileName);
        json = updateJSON(json, "requisitionId", id);
        response = client.SendJSON(json, "http://localhost:9091/rest-api/"+ id +"/approve.json", "POST", "commTrack", dbWrapper.getAuthToken("commTrack"));
        client.SendJSON("", "http://localhost:9091/", "GET", "", "");

        assertTrue(response.contains("{\"R&R\":"));
    }

    public void setupData() throws IOException, SQLException {
        dbWrapper.insertVendor("commTrack");
        List<String> rightsList = new ArrayList<String>();
        rightsList.add("CREATE_REQUISITION");
        rightsList.add("VIEW_REQUISITION");
        rightsList.add("AUTHORIZE_REQUISITION");
        setupTestDataToInitiateRnR(true,"HIV", "commTrack", "700", "commTrack", rightsList);

    }

    public void setupDataApprover() throws IOException, SQLException {
        List<String> rightsList = new ArrayList<String>();
        rightsList.add("APPROVE_REQUISITION");
        rightsList.add("VIEW_REQUISITION");
        rightsList.add("CONVERT_TO_ORDER");
        setupTestDataToApproveRnR("commTrack1", "701", "commTrack", rightsList);
    }

    public String readJSON(String fileName) throws IOException {
        String json = "";
        File file = new File(fileName);
        Scanner scanner = new Scanner(file);
        while (scanner.hasNextLine()) {
            json = json + scanner.nextLine();
        }
        scanner.close();
        return json;
    }

    public String updateJSON(String json, String field, String value) throws IOException {
        String updateValue = "\"" + field + "\" : \"" + value + "\"";
        String orignalValue = "\"" + field + "\" : \"\"";

        System.out.println(json.contains(orignalValue));
        json = json.replace(orignalValue, updateValue);

        return json;
    }

}

