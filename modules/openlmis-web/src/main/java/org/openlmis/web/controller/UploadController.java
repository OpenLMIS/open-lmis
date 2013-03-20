package org.openlmis.web.controller;

import lombok.NoArgsConstructor;
import org.openlmis.core.exception.DataException;
import org.openlmis.db.service.DbService;
import org.openlmis.upload.exception.UploadException;
import org.openlmis.upload.model.AuditFields;
import org.openlmis.upload.model.ModelClass;
import org.openlmis.upload.parser.CSVParser;
import org.openlmis.web.model.UploadBean;
import org.openlmis.web.response.OpenLmisResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.Date;
import java.util.Map;
import java.util.ResourceBundle;

@Controller
@NoArgsConstructor
public class UploadController extends BaseController {

  @Autowired
  private CSVParser csvParser;
  @Autowired
  DbService dbService;
  @Resource
  private Map<String, UploadBean> uploadBeansMap;

  private static ResourceBundle resourceBundle = ResourceBundle.getBundle("messages");


  String uploadPage = "redirect:/public/pages/admin/upload/index.html#/upload?";


  public UploadController(CSVParser csvParser, Map<String, UploadBean> uploadBeansMap, DbService dbService) {
    this.csvParser = csvParser;
    this.uploadBeansMap = uploadBeansMap;
    this.dbService = dbService;
  }

  @RequestMapping(value = "/upload", method = RequestMethod.POST)
  @PreAuthorize("@permissionEvaluator.hasPermission(principal,'UPLOADS')")
  public String upload(@RequestParam(value = "csvFile", required = true) MultipartFile csvFile,
                       @RequestParam(value = "model", required = true) String model,
                       HttpServletRequest request) {
    try {
      String error = validateFile(model, csvFile);
      if (error != null) {
        return errorPage(error, model);
      }

      int initialRecordCount = dbService.getCount(uploadBeansMap.get(model).getTableName());
      Date currentTimestamp= dbService.getCurrentTimestamp();

      int recordsToBeUploaded = csvParser.process(csvFile.getInputStream(), new ModelClass(uploadBeansMap.get(model).getImportableClass()),
        uploadBeansMap.get(model).getRecordHandler(), new AuditFields(loggedInUserId(request), currentTimestamp));

      return redirectToSuccessRoute(model, initialRecordCount, recordsToBeUploaded);

    } catch (DataException dataException) {
      return errorPage(dataException.getOpenLmisMessage().resolve(resourceBundle), model);
    } catch (UploadException | IOException e) {
      return errorPage(e.getMessage(), model);
    }
  }

  private String redirectToSuccessRoute(String model, int initialRecordCount, int recordsToBeUploaded) {
    int finalRecordCount = dbService.getCount(uploadBeansMap.get(model).getTableName());
    int recordsCreated = finalRecordCount - initialRecordCount;

    return successPage(model, recordsCreated, recordsToBeUploaded - recordsCreated);
  }

  @RequestMapping(value = "/supported-uploads", method = RequestMethod.GET, headers = "Accept=application/json")
  @PreAuthorize("@permissionEvaluator.hasPermission(principal,'UPLOADS')")
  public ResponseEntity<OpenLmisResponse> getSupportedUploads() {
    return OpenLmisResponse.response("supportedUploads", uploadBeansMap);
  }

  private String validateFile(String model, MultipartFile csvFile) {
    String error = null;
    if (model.isEmpty()) {
      error = "Please select the Upload type";
    } else if (!uploadBeansMap.containsKey(model)) {
      error = "Incorrect file";
    } else if (csvFile.isEmpty()) {
      error = "File is empty";
    } else if (!csvFile.getOriginalFilename().endsWith(".csv")) {
      error = "Incorrect file format , Please upload " + model + " data as a \".csv\" file";
    }
    return error;
  }

  private String successPage(String model, int recordsCreated, int recordsUpdated) {
    return uploadPage + "model=" + model + "&success=" + "File uploaded successfully. " +
      "'Number of records created: " + recordsCreated + "', 'Number of records updated : " + recordsUpdated + "'";
  }

  private String errorPage(String error, String model) {
    return uploadPage + "model=" + model + "&error=" + error;
  }

}
