package org.openlmis.admin.controller;

import lombok.NoArgsConstructor;
import org.openlmis.core.handler.UploadHandlerFactory;
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
    public ModelAndView upload(@RequestParam(value = "csvFile", required = true) MultipartFile multipartFile,
                               @RequestParam(value = "model", required = true) String model) {

        ModelAndView modelAndView = new ModelAndView();
        try {
            Class modelClass = modelMap.get(model);
            if (modelClass == null) {
                return errorModelAndView(modelAndView, "Incorrect file");
            }
            if (!multipartFile.getOriginalFilename().contains(".csv")) {
                return errorModelAndView(modelAndView, "Incorrect file format , Please upload " + model + " data as a \".csv\" file");
            }
            int recordsUploaded = csvParser.process(multipartFile.getInputStream(), modelClass, uploadHandlerFactory.getHandler(model));
            modelAndView.addObject("message", "File upload success. Total " + model +" uploaded in the system : " + recordsUploaded);
        } catch (Exception e) {
            return errorModelAndView(modelAndView, e.getMessage());
        }
        return modelAndView;
    }

    private ModelAndView errorModelAndView(ModelAndView modelAndView, String errorMessage) {
        modelAndView.addObject("error", errorMessage);
        return modelAndView;
    }
}
