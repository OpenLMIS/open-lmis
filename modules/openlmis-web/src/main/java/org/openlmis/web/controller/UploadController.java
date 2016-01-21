/*
 * This program is part of the OpenLMIS logistics management information system platform software.
 * Copyright © 2013 VillageReach
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *  
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 * You should have received a copy of the GNU Affero General Public License along with this program.  If not, see http://www.gnu.org/licenses.  For additional information contact info@OpenLMIS.org. 
 */

package org.openlmis.web.controller;

import org.openlmis.core.exception.DataException;
import org.openlmis.core.message.OpenLmisMessage;
import org.openlmis.core.web.controller.BaseController;
import org.openlmis.db.service.DbService;
import org.openlmis.upload.RecordHandler;
import org.openlmis.upload.exception.UploadException;
import org.openlmis.upload.model.AuditFields;
import org.openlmis.upload.model.ModelClass;
import org.openlmis.upload.parser.CSVParser;
import org.openlmis.web.model.UploadBean;
import org.openlmis.core.web.OpenLmisResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static org.openlmis.core.web.OpenLmisResponse.response;
import static org.springframework.http.HttpStatus.OK;
import static org.springframework.http.MediaType.TEXT_HTML_VALUE;
import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

/**
 * This controller handles endpoints to related to uploads.
 */

@Controller
public class UploadController extends BaseController {

  public static final String SELECT_UPLOAD_TYPE = "upload.select.type";
  public static final String INCORRECT_FILE = "upload.incorrect.file";
  public static final String FILE_IS_EMPTY = "upload.file.empty";
  public static final String INCORRECT_FILE_FORMAT = "upload.incorrect.file.format";
  public static final String UPLOAD_FILE_SUCCESS = "upload.file.successful";
  public static final String SUCCESS = "success";
  public static final String ERROR = "error";
  public static final String SUPPORTED_UPLOADS = "supportedUploads";

  @Autowired
  private CSVParser csvParser;

  @Autowired
  private DbService dbService;

  @Autowired
  private HashMap<String, UploadBean> uploadBeansMap;

  @RequestMapping(value = "/upload", method = POST)
  @PreAuthorize("@permissionEvaluator.hasPermission(principal,'UPLOADS')")
  public ResponseEntity<OpenLmisResponse> upload(MultipartFile csvFile, String model, HttpServletRequest request) {
    try {
      OpenLmisMessage errorMessage = validateFile(model, csvFile);
      if (errorMessage != null) {
        return errorResponse(errorMessage);
      }

      Date currentTimestamp = dbService.getCurrentTimestamp();

      RecordHandler recordHandler = uploadBeansMap.get(model).getRecordHandler();
      ModelClass modelClass = new ModelClass(uploadBeansMap.get(model).getImportableClass());
      AuditFields auditFields = new AuditFields(loggedInUserId(request), currentTimestamp);

      int recordsToBeUploaded = csvParser.process(csvFile.getInputStream(), modelClass, recordHandler, auditFields);

      return successPage(recordsToBeUploaded);
    } catch (DataException dataException) {
      return errorResponse(dataException.getOpenLmisMessage());
    } catch (UploadException e) {
      return errorResponse(new OpenLmisMessage(messageService.message(e.getCode(), (Object[])e.getParams())));
    } catch (IOException e) {
      return errorResponse(new OpenLmisMessage(e.getMessage()));
    }
  }

  @RequestMapping(value = "/supported-uploads", method = GET, headers = ACCEPT_JSON)
  @PreAuthorize("@permissionEvaluator.hasPermission(principal,'UPLOADS')")
  public ResponseEntity<OpenLmisResponse> getSupportedUploads() {
    // this is a hack to make the new version of jackson to work
    // fasterxml jackson does currenly was failing to serialize
    HashMap<String, UploadBean> beanDefinitions = new HashMap<>();
    for(String key :uploadBeansMap.keySet()){
      UploadBean proxy = uploadBeansMap.get(key);
      UploadBean bean = new UploadBean();
      bean.setDisplayName(proxy.getDisplayName());
      beanDefinitions.put(key,bean);
    }

    return response(SUPPORTED_UPLOADS, beanDefinitions);
  }

  private OpenLmisMessage validateFile(String model, MultipartFile csvFile) {
    OpenLmisMessage errorMessage = null;
    if (model.isEmpty()) {
      errorMessage = new OpenLmisMessage(SELECT_UPLOAD_TYPE);
    } else if (!uploadBeansMap.containsKey(model)) {
      errorMessage = new OpenLmisMessage(INCORRECT_FILE);
    } else if (csvFile == null || csvFile.isEmpty()) {
      errorMessage = new OpenLmisMessage(FILE_IS_EMPTY);
    } else if (!csvFile.getOriginalFilename().endsWith(".csv")) {
      errorMessage = new OpenLmisMessage(messageService.message(INCORRECT_FILE_FORMAT, messageService.message(uploadBeansMap.get(model).getDisplayName())));
    }
    return errorMessage;
  }

  private ResponseEntity<OpenLmisResponse> successPage(int recordsProcessed) {
    Map<String, String> responseMessages = new HashMap<>();
    String message = messageService.message(UPLOAD_FILE_SUCCESS, recordsProcessed);
    responseMessages.put(SUCCESS, message);
    return response(responseMessages, OK, TEXT_HTML_VALUE);
  }

  private ResponseEntity<OpenLmisResponse> errorResponse(OpenLmisMessage errorMessage) {
    Map<String, String> responseMessages = new HashMap<>();
    String message = messageService.message(errorMessage);
    responseMessages.put(ERROR, message);
    return response(responseMessages, OK, TEXT_HTML_VALUE);
  }

}
