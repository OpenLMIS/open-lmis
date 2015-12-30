package org.openlmis.web.controller;

import org.openlmis.core.exception.DataException;
import org.openlmis.core.message.OpenLmisMessage;
import org.openlmis.core.web.OpenLmisResponse;
import org.openlmis.core.web.controller.BaseController;
import org.openlmis.db.service.DbService;
import org.openlmis.upload.RecordHandler;
import org.openlmis.upload.model.AuditFields;
import org.openlmis.upload.model.ModelClass;
import org.openlmis.upload.parser.CSVParser;
import org.openlmis.web.model.UploadBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
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
import static org.springframework.web.bind.annotation.RequestMethod.POST;

@Controller
public class UpdateProductsController extends BaseController {
    public static final String ERROR = "error";
    public static final String UPLOAD_FILE_SUCCESS = "upload.file.successful";
    public static final String SUCCESS = "success";

    @Autowired
    DbService dbService;

    @Autowired
    private CSVParser csvParser;

    @Autowired
    private HashMap<String, UploadBean> uploadBeansMap;

    @RequestMapping(value = "/update-products", method = POST)
    public ResponseEntity<OpenLmisResponse> updateProducts(MultipartFile csvFile, HttpServletRequest request) {
        try {
            Date currentTimestamp = dbService.getCurrentTimestamp();

            AuditFields auditFields = new AuditFields(loggedInUserId(request), currentTimestamp);
            ModelClass modelClass = new ModelClass(uploadBeansMap.get("updateProduct").getImportableClass());
            RecordHandler recordHandler = uploadBeansMap.get("updateProduct").getRecordHandler();

            int recordsToBeUploaded = csvParser.process(csvFile.getInputStream(), modelClass, recordHandler, auditFields);

            return successPage(recordsToBeUploaded);
        } catch (IOException e) {
            return errorResponse(new OpenLmisMessage(e.getMessage()));
        }catch (DataException e){
            return errorResponse(new OpenLmisMessage(messageService.message(e.getOpenLmisMessage())));
        }
    }

    private ResponseEntity<OpenLmisResponse> errorResponse(OpenLmisMessage errorMessage) {
        Map<String, String> responseMessages = new HashMap<>();
        String message = messageService.message(errorMessage);
        responseMessages.put(ERROR, message);
        return response(responseMessages, OK, TEXT_HTML_VALUE);
    }

    private ResponseEntity<OpenLmisResponse> successPage(int recordsProcessed) {
        Map<String, String> responseMessages = new HashMap<>();
        String message = messageService.message(UPLOAD_FILE_SUCCESS, recordsProcessed);
        responseMessages.put(SUCCESS, message);
        return response(responseMessages, OK, TEXT_HTML_VALUE);
    }
}
