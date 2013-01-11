package org.openlmis.web.controller;

import lombok.NoArgsConstructor;
import org.openlmis.core.message.OpenLmisMessage;
import org.openlmis.upload.exception.UploadException;
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
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;

@Controller
@NoArgsConstructor
public class UploadController extends BaseController {

  @Autowired
  private CSVParser csvParser;
  @Resource
  private Map<String, UploadBean> uploadBeansMap;

  private static ResourceBundle resourceBundle = ResourceBundle.getBundle("messages", Locale.ENGLISH);


  String uploadPage = "redirect:/public/pages/admin/upload/index.html#/upload?";


  public UploadController(CSVParser csvParser, Map<String, UploadBean> uploadBeansMap) {
    this.csvParser = csvParser;
    this.uploadBeansMap = uploadBeansMap;
  }

  @RequestMapping(value = "/upload", method = RequestMethod.POST)
  @PreAuthorize("hasPermission('','UPLOADS')")
  public String upload(@RequestParam(value = "csvFile", required = true) MultipartFile csvFile,
                       @RequestParam(value = "model", required = true) String model,
                       HttpServletRequest request) {
    try {
      String error = validateFile(model, csvFile);
      if (error != null) {
        return errorPage(error, model);
      }

      String modifiedBy = loggedInUser(request);
      int recordsUploaded = csvParser.process(csvFile.getInputStream(),
        new ModelClass(uploadBeansMap.get(model).getImportableClass()),
        uploadBeansMap.get(model).getRecordHandler(), modifiedBy);
      return successPage(recordsUploaded, model);
    } catch (UploadException uploadException) {
      String messageForUploadException = getMessageForUploadException(uploadException);
      return errorPage(messageForUploadException, model);
    } catch (IOException e) {
      return errorPage(e.getMessage(), model);
    }
  }

  private String getMessageForUploadException(UploadException uploadException) {
    if(uploadException.getCode() == null) return  uploadException.getMessage();
    OpenLmisMessage message = new OpenLmisMessage(uploadException.getCode(), uploadException.getParams());
    return message.resolve(resourceBundle);
  }


  @RequestMapping(value = "/supported-uploads", method = RequestMethod.GET, headers = "Accept=application/json")
  @PreAuthorize("hasPermission('','UPLOADS')")
  public ResponseEntity<OpenLmisResponse> getSupportedUploads() {
    return OpenLmisResponse.response("supportedUploads", uploadBeansMap);
  }

  private String validateFile(String model, MultipartFile csvFile) {
    String error = null;
    if (!uploadBeansMap.containsKey(model)) {
      error = "Incorrect file";
    } else if (csvFile.isEmpty()) {
      error = "File is empty";
    } else if (!csvFile.getOriginalFilename().endsWith(".csv")) {
      error = "Incorrect file format , Please upload " + model + " data as a \".csv\" file";
    }
    return error;
  }

  private String successPage(int recordsUploaded, String model) {
    return uploadPage + "model=" + model + "&success=" + "File uploaded successfully. Total records uploaded: " + recordsUploaded;
  }

  private String errorPage(String error, String model) {
    return uploadPage + "model=" + model + "&error=" + error;
  }

}
