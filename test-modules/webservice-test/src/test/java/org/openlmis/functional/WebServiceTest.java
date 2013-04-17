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

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.openlmis.UiUtils.TestCaseHelper;

import static com.thoughtworks.selenium.SeleneseTestNgHelper.*;
import static java.lang.System.getProperty;


public class WebServiceTest extends TestCaseHelper  {

  @BeforeMethod(groups = {"webservice"})
  public void setUp() throws Exception {
      WebDriver driver=new FirefoxDriver();
      driver.get("http://localhost:9091");
      super.setup();
  }


  @Test(groups = {"webservice"})
  public void testSubmitReport() throws Exception {
      setupData();
      HttpClient client = new HttpClient();
      client.createContext();
      String fileName=this.getClass().getClassLoader().getResource("DummyJSON.txt").getFile();
      String response= client.SendJSON(fileName, "http://localhost:9091/rest-api/requisitions.json", "POST","commTrack",dbWrapper.getAuthToken("commTrack"));
      client.SendJSON("", "http://localhost:9091/", "GET", "", "");

      assertEquals(response, "{\"error\":\"User doesn't have access to Program & Facility.\"}");
  }

  public void setupData() throws IOException ,SQLException {
      dbWrapper.insertVendor("commTrack");
      List<String> rightsList = new ArrayList<String>();
      rightsList.add("CREATE_REQUISITION");
      rightsList.add("VIEW_REQUISITION");
      rightsList.add("AUTHORIZE_REQUISITION");
      setupTestDataToInitiateRnR("HIV", "commTrack", "700", "commTrack", rightsList);

  }
    }

