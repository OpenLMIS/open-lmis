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


public class commTrackTemplateTest extends TestCaseHelper {
    public WebDriver driver;
    public Utils  utillity = new Utils();
    @BeforeMethod(groups = {"webservice"})
    public void setUp() throws Exception {
        driver = new FirefoxDriver();
        driver.get("http://localhost:9091");
        super.setup();
        super.setupDataExternalVendor(true);
    }
    @AfterMethod(groups = {"webservice"})
    public void tearDown() {
        driver.close();
    }


    @Test(groups = {"webservice"})
    public void testCommTrackSubmitReportValidRnR() throws Exception {
        HttpClient client = new HttpClient();
        client.createContext();
        String fileName = this.getClass().getClassLoader().getResource("CommTrack_JSON.txt").getFile();
        String json = utillity.readJSON(fileName);
        json = utillity.updateJSON(json, "userId", "commTrack");
        json = utillity.updateJSON(json, "facilityId", dbWrapper.getFacilityID("F10"));
        json = utillity.updateJSON(json, "periodId", dbWrapper.getPeriodID("Period2"));
        json = utillity.updateJSON(json, "programId", dbWrapper.getProgramID("HIV"));
        json = utillity.updateJSON(json, "productCode", "P10");
        ResponseEntity responseEntity = client.SendJSON(json, "http://localhost:9091/rest-api/requisitions.json", "POST", "commTrack", dbWrapper.getAuthToken("commTrack"));
        client.SendJSON("", "http://localhost:9091/", "GET", "", "");

        assertTrue(responseEntity.getResponse().contains("{\"R&R\":"));
    }


}

