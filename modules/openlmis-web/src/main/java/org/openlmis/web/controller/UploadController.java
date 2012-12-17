package org.openlmis.web.controller;

import lombok.NoArgsConstructor;
import org.openlmis.web.handler.UploadHandlerFactory;
import org.openlmis.upload.exception.UploadException;
import org.openlmis.upload.model.ModelClass;
import org.openlmis.upload.parser.CSVParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.Map;

import static org.openlmis.authentication.web.UserAuthenticationSuccessHandler.USER;

@Controller
@NoArgsConstructor
@RequestMapping("/admin")
public class UploadController {

    @Autowired
    private CSVParser csvParser;
    @Resource
    private Map<String, Class> modelMap;
    @Autowired
    private UploadHandlerFactory uploadHandlerFactory;

    public UploadController(CSVParser csvParser, UploadHandlerFactory uploadHandlerFactory, Map<String, Class> modelMap) {
        this.csvParser = csvParser;
        this.uploadHandlerFactory = uploadHandlerFactory;
        this.modelMap = modelMap;
    }

    @RequestMapping(value = "/upload", method = RequestMethod.POST, headers = "Accept=application/json")
    public ResponseEntity<ModelMap> upload(@RequestParam(value = "csvFile", required = true) MultipartFile multipartFile,
                               @RequestParam(value = "model", required = true) String model, HttpServletRequest request) {

        ModelMap resultMap = new ModelMap();
        try {
            Class modelClass = modelMap.get(model);
            if (modelClass == null) {
                return errorResponse(resultMap, "Incorrect file");
            }
            if(multipartFile.isEmpty()){
                return errorResponse(resultMap, "File is empty");
            }
            if (!multipartFile.getOriginalFilename().endsWith(".csv")) {
                return errorResponse(resultMap, "Incorrect file format , Please upload " + model + " data as a \".csv\" file");
            }
            String modifiedBy = (String) request.getSession().getAttribute(USER);
            int recordsUploaded = csvParser.process(multipartFile.getInputStream(), new ModelClass(modelClass), uploadHandlerFactory.getHandler(model), modifiedBy);
            resultMap.addObject("message", "File upload success. Total " + model + " uploaded in the system : " + recordsUploaded);
        } catch (UploadException | IOException e) {
            return errorResponse(resultMap, e.getMessage());
        }
        return new ResponseEntity<ModelMap>(resultMap, HttpStatus.OK);
    }

    private ResponseEntity<ModelMap> errorResponse(ModelMap modelMap, String errorMessage) {
        modelMap.put("error", errorMessage);
        return new ResponseEntity<ModelMap>(modelMap, HttpStatus.BAD_REQUEST);
    }
}
