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

import org.codehaus.jackson.map.ObjectMapper;
import org.openlmis.UiUtils.HttpClient;
import org.openlmis.UiUtils.ResponseEntity;
import org.openlmis.UiUtils.TestCaseHelper;
import org.openlmis.restapi.domain.Report;

import java.io.File;
import java.io.IOException;
import java.io.StringWriter;


public class JsonUtility extends TestCaseHelper{
    public static final String FULL_JSON_TXT_FILE_NAME = "ReportFullJson.txt";
    public static final String FULL_JSON_APPROVE_TXT_FILE_NAME = "ReportJsonApprove.txt";

 public static <T> T readObjectFromFile(String fullJsonTxtFileName, Class<T> clazz) throws IOException {
    String classPathFile = JsonUtility.class.getClassLoader().getResource(fullJsonTxtFileName).getFile();
    ObjectMapper mapper = new ObjectMapper();
    return mapper.readValue(new File(classPathFile), clazz);
 }

  public static String getJsonStringFor(Object object) throws IOException {
    ObjectMapper objectMapper = new ObjectMapper();
    StringWriter writer = new StringWriter();
    objectMapper.writeValue(writer, object);
    return writer.toString();
  }
    public static String submitReport() throws Exception {
        HttpClient client = new HttpClient();
        client.createContext();

        Report reportFromJson = readObjectFromFile(FULL_JSON_TXT_FILE_NAME, Report.class);
        reportFromJson.setFacilityId(dbWrapper.getFacilityID("F10"));
        reportFromJson.setPeriodId(dbWrapper.getPeriodID("Period2"));
        reportFromJson.setProgramId(dbWrapper.getProgramID("HIV"));

        ResponseEntity responseEntity = client.SendJSON(getJsonStringFor(reportFromJson),
                "http://localhost:9091/rest-api/requisitions.json",
                "POST",
                "commTrack",
                "Admin123");

        client.SendJSON("", "http://localhost:9091/", "GET", "", "");

        return responseEntity.getResponse();
    }


    public static Long getRequisitionIdFromResponse(String response) {
        return Long.parseLong(response.substring(response.lastIndexOf(":") + 1, response.lastIndexOf("}")));
    }

    public static String createApproveRequisition() throws Exception {
        HttpClient client = new HttpClient();
        client.createContext();

        Report reportFromJson = readObjectFromFile(FULL_JSON_TXT_FILE_NAME, Report.class);
        reportFromJson.setFacilityId(dbWrapper.getFacilityID("F10"));
        reportFromJson.setPeriodId(dbWrapper.getPeriodID("Period2"));
        reportFromJson.setProgramId(dbWrapper.getProgramID("HIV"));

        ResponseEntity responseEntity = client.SendJSON(getJsonStringFor(reportFromJson),
                "http://localhost:9091/rest-api/requisitions.json",
                "POST",
                "commTrack",
                "Admin123");

        client.SendJSON("", "http://localhost:9091/", "GET", "", "");
        Long id = getRequisitionIdFromResponse(responseEntity.getResponse());

        reportFromJson = readObjectFromFile(FULL_JSON_APPROVE_TXT_FILE_NAME, Report.class);
        reportFromJson.setUserId("commTrack1");
        reportFromJson.setRequisitionId(id);
        reportFromJson.getProducts().get(0).setProductCode("P10");
        reportFromJson.getProducts().get(0).setQuantityApproved(65);

        responseEntity = client.SendJSON(getJsonStringFor(reportFromJson),
                "http://localhost:9091/rest-api/requisitions/" + id + "/approve",
                "PUT",
                "commTrack",
                "Admin123");
        return responseEntity.getResponse();
    }

    public static void approveRequisition(Long id, int quantityApproved) throws Exception {

        HttpClient client = new HttpClient();
        client.createContext();

        Report reportFromJson = readObjectFromFile(FULL_JSON_APPROVE_TXT_FILE_NAME, Report.class);
        reportFromJson.setUserId("commTrack1");
        reportFromJson.setRequisitionId(id);
        reportFromJson.getProducts().get(0).setProductCode("P10");
        reportFromJson.getProducts().get(0).setQuantityApproved(quantityApproved);

        client.SendJSON(getJsonStringFor(reportFromJson),
                "http://localhost:9091/rest-api/requisitions/" + id + "/approve",
                "PUT",
                "commTrack",
                "Admin123");
    }
}

