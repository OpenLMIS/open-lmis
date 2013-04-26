/*
 * Copyright © 2013 VillageReach.  All Rights Reserved.  This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 *
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package org.openlmis.functional;

import org.openlmis.UiUtils.HttpClient;
import org.openlmis.UiUtils.TestCaseHelper;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;


import static com.thoughtworks.selenium.SeleneseTestNgHelper.assertEquals;
import static com.thoughtworks.selenium.SeleneseTestNgHelper.assertTrue;


public class approveRequisition extends TestCaseHelper {
    public WebDriver driver;
    public Utils  utillity = new Utils();

    @BeforeMethod(groups = {"webservice"})
    public void setUp() throws Exception {
        driver = new FirefoxDriver();
        driver.get("http://localhost:9091");
        super.setup();
        super.setupDataExternalVendor(false);
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
        String response = utillity.submitReport();
        String id= utillity.getRequisitionIdFromResponse(response);
        assertEquals(dbWrapper.getRequisitionStatus(id) ,"AUTHORIZED");

        String fileName = this.getClass().getClassLoader().getResource("FullJSON_Approve.txt").getFile();
        String json = utillity.readJSON(fileName);
        json = utillity.updateJSON(json, "requisitionId", id);
        json = utillity.updateJSON(json, "userId", "commTrack1");
        json = utillity.updateJSON(json, "productCode", "P10");
        json = utillity.updateJSON(json, "quantityApproved", "65");

        response = client.SendJSON(json, "http://localhost:9091/rest-api/requisitions/"+ id +"/approve", "PUT", "commTrack", dbWrapper.getAuthToken("commTrack"));
        client.SendJSON("", "http://localhost:9091/", "GET", "", "");

        assertTrue(response.contains("{\"R&R\":"));
        assertEquals(dbWrapper.getRequisitionStatus(id) ,"RELEASED");

        //System.out.println(dbWrapper.getOrderId(id));


    }

    @Test(groups = {"webservice"})
    public void testApproveRequisitionInValidUser() throws Exception {
        HttpClient client = new HttpClient();
        client.createContext();
        String response = utillity.submitReport();
        String id= utillity.getRequisitionIdFromResponse(response);

        String fileName = this.getClass().getClassLoader().getResource("FullJSON_Approve.txt").getFile();
        String json = utillity.readJSON(fileName);
        json = utillity.updateJSON(json, "requisitionId", id);
        json = utillity.updateJSON(json, "userId", "ABCD");
        json = utillity.updateJSON(json, "productCode", "P10");
        json = utillity.updateJSON(json, "quantityApproved", "65");

        response = client.SendJSON(json, "http://localhost:9091/rest-api/requisitions/"+ id +"/approve", "PUT", "commTrack", dbWrapper.getAuthToken("commTrack"));
        client.SendJSON("", "http://localhost:9091/", "GET", "", "");

        assertEquals(response, "{\"error\":\"Please provide a valid username\"}");


    }

    @Test(groups = {"webservice"})
    public void testApproveRequisitionInvalidProduct() throws Exception {
        HttpClient client = new HttpClient();
        client.createContext();
        String response = utillity.submitReport();
        String id= utillity.getRequisitionIdFromResponse(response);

        String fileName = this.getClass().getClassLoader().getResource("FullJSON_Approve.txt").getFile();
        String json = utillity.readJSON(fileName);
        json = utillity.updateJSON(json, "requisitionId", id);
        json = utillity.updateJSON(json, "userId", "commTrack");
        json = utillity.updateJSON(json, "productCode", "P1000");
        json = utillity.updateJSON(json, "quantityApproved", "65");

        response = client.SendJSON(json, "http://localhost:9091/rest-api/requisitions/"+ id +"/approve", "PUT", "commTrack", dbWrapper.getAuthToken("commTrack"));
        client.SendJSON("", "http://localhost:9091/", "GET", "", "");

//        assertEquals(response, "{\"error\":\"Invalid data.\"}");


    }

    @Test(groups = {"webservice"})
    public void testApproveRequisitionInvalidRequisitionId() throws Exception {
        HttpClient client = new HttpClient();
        client.createContext();
        String response = utillity.submitReport();
        String id= "999999";
        String id2= utillity.getRequisitionIdFromResponse(response);


        String fileName = this.getClass().getClassLoader().getResource("FullJSON_Approve.txt").getFile();
        String json = utillity.readJSON(fileName);
        json = utillity.updateJSON(json, "requisitionId", id2);
        json = utillity.updateJSON(json, "userId", "commTrack");
        json = utillity.updateJSON(json, "productCode", "P10");
        json = utillity.updateJSON(json, "quantityApproved", "65");

        response = client.SendJSON(json, "http://localhost:9091/rest-api/requisitions/"+ id +"/approve", "PUT", "commTrack", dbWrapper.getAuthToken("commTrack"));
        client.SendJSON("", "http://localhost:9091/", "GET", "", "");

        assertEquals(response, "{\"error\":\"Requisition Not Found\"}");


    }

    @Test(groups = {"webservice"})
    public void testApproveRequisitionBlankQuantityApproved() throws Exception {
        HttpClient client = new HttpClient();
        client.createContext();
        String response = utillity.submitReport();
        String id= utillity.getRequisitionIdFromResponse(response);

        String fileName = this.getClass().getClassLoader().getResource("FullJSON_Approve.txt").getFile();
        String json = utillity.readJSON(fileName);
        json = utillity.updateJSON(json, "requisitionId", id);
        json = utillity.updateJSON(json, "userId", "commTrack1");
        json = utillity.updateJSON(json, "productCode", "P10");
        json = utillity.updateJSON(json, "quantityApproved", "");

        response = client.SendJSON(json, "http://localhost:9091/rest-api/requisitions/"+ id +"/approve", "PUT", "commTrack", dbWrapper.getAuthToken("commTrack"));
        client.SendJSON("", "http://localhost:9091/", "GET", "", "");

        assertEquals(response, "{\"error\":\"Invalid data.\"}");


    }

    @Test(groups = {"webservice"})
    public void testApproveRequisitionInValidVendor() throws Exception {
        HttpClient client = new HttpClient();
        client.createContext();
        String response = utillity.submitReport();
        String id= utillity.getRequisitionIdFromResponse(response);

        String fileName = this.getClass().getClassLoader().getResource("FullJSON_Approve.txt").getFile();
        String json = utillity.readJSON(fileName);
        json = utillity.updateJSON(json, "requisitionId", id);
        json = utillity.updateJSON(json, "userId", "commTrack100");
        json = utillity.updateJSON(json, "productCode", "P10");
        json = utillity.updateJSON(json, "quantityApproved", "65");

        response = client.SendJSON(json, "http://localhost:9091/rest-api/requisitions/"+ id +"/approve", "PUT", "commTrack1", dbWrapper.getAuthToken("commTrack"));
        client.SendJSON("", "http://localhost:9091/", "GET", "", "");

        //assertEquals(response, null);


    }

}

