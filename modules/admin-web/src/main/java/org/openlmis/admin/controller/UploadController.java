package org.openlmis.admin.controller;

import lombok.NoArgsConstructor;
import org.openlmis.upload.RecordHandler;
import org.openlmis.upload.parser.CSVParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import javax.annotation.Resource;
import java.util.Map;

@Controller
@NoArgsConstructor
@RequestMapping("/resources/pages/admin")
public class UploadController {

    @Autowired
    private CSVParser csvParser;
    @Resource
    private Map<String, RecordHandler> uploadHandlerMap;
    @Resource
    private Map<String, Class> modelMap;


    public UploadController(CSVParser csvParser, Map<String, RecordHandler> uploadHandlerMap, Map<String, Class> modelMap) {
        this.csvParser = csvParser;
        this.uploadHandlerMap = uploadHandlerMap;
        this.modelMap = modelMap;
    }

    @RequestMapping(value = "/upload", method = RequestMethod.POST, headers = "Accept=application/json")
    public ModelAndView upload(@RequestParam(value = "csvFile", required = true) MultipartFile multipartFile,
                               @RequestParam(value = "model", required = true) String model) {

        ModelAndView modelAndView = new ModelAndView();


        try {
            Class modelClass = modelMap.get(model);
            if (modelClass == null) {
                return returnErrorModelAndView(modelAndView, "Incorrect file");
            }
            if (!multipartFile.getOriginalFilename().contains(".csv")) {
                return returnErrorModelAndView(modelAndView, "Incorrect file format , Please upload " + model + " data as a \".csv\" file");
            }
            int recordsUploaded = csvParser.process(multipartFile.getInputStream(), modelClass, uploadHandlerMap.get(model));
            modelAndView.addObject("message", "File upload success. Total " + model +" uploaded in the system : " + recordsUploaded);
        } catch (Exception e) {
            modelAndView.addObject("error", e.getMessage());
        }
        return modelAndView;
    }

    private ModelAndView returnErrorModelAndView(ModelAndView modelAndView, String errorMessage) {
        modelAndView.addObject("error", errorMessage);
        return modelAndView;
    }
}
