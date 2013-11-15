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
import org.openlmis.restapi.domain.Report;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.io.IOException;
import java.sql.SQLException;

import static com.thoughtworks.selenium.SeleneseTestBase.assertEquals;
import static com.thoughtworks.selenium.SeleneseTestNgHelper.assertTrue;


public class CommTrackTemplateTest extends JsonUtility {
  public static final String FULL_COMMTRACK_JSON_TXT_FILE_NAME = "CommTrackReportJson.txt";

  @BeforeMethod(groups = {"webservice"})
  public void setUp() throws Exception {
    super.setup();
    super.setupTestData(true);
    dbWrapper.insertVirtualFacility("V10","F10");
    dbWrapper.insertProcessingPeriod("current", "current period", "2013-01-30","2016-01-30", 1, "M");
    dbWrapper.insertRoleAssignmentForSupervisoryNode("700","store in-charge","N1");
  }

  @AfterMethod(groups = {"webservice"})
  public void tearDown() throws IOException, SQLException {
    dbWrapper.deleteData();
    dbWrapper.closeConnection();
  }

  @Test(groups = {"webservice"})
  public void testCommTrackSubmitReportValidRnR() throws Exception {
    HttpClient client = new HttpClient();
    client.createContext();

    Report reportFromJson = readObjectFromFile(FULL_COMMTRACK_JSON_TXT_FILE_NAME, Report.class);
    reportFromJson.setAgentCode("V10");
    reportFromJson.setProgramCode("HIV");

    ResponseEntity responseEntity =
      client.SendJSON(getJsonStringFor(reportFromJson),
        "http://localhost:9091/rest-api/requisitions.json",
        "POST",
        "commTrack",
        "Admin123");

    assertEquals(201, responseEntity.getStatus());
    assertTrue(responseEntity.getResponse().contains("{\"requisitionId\":"));
  }
}

