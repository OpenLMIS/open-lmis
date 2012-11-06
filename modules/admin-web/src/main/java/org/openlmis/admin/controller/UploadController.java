package org.openlmis.admin.controller;

import org.openlmis.core.domain.Product;
import org.openlmis.core.handler.ProductImportHandler;
import org.openlmis.upload.parser.CSVParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import java.util.HashMap;
import java.util.Map;

@Controller
@RequestMapping("/resources/pages/admin")
public class UploadController {

    private CSVParser csvParser;
    private ProductImportHandler handler;
    private final Map<String, Class[]> modelMap = new HashMap<String, Class[]>();

    @Autowired
    public UploadController(CSVParser csvParser, ProductImportHandler handler) {
        this.csvParser = csvParser;
        this.handler = handler;
        modelMap.put("product", new Class[]{Product.class, ProductImportHandler.class});
    }

    @RequestMapping(value = "/upload", method = RequestMethod.POST, headers = "Accept=application/json")
    public ModelAndView upload(@RequestParam(value = "csvFile", required = true) MultipartFile multipartFile,
                               @RequestParam(value = "model", required = true) String model) {

        ModelAndView modelAndView = new ModelAndView();

        try {
            Class[] modelAndHandler = modelMap.get(model);
            if (modelAndHandler == null) {
                modelAndView.addObject("error", "Incorrect file");
            } else {
                csvParser.process(multipartFile.getInputStream(), modelAndHandler[0], handler);
                modelAndView.addObject("message", "uploaded successfully");
            }
        } catch (Exception e) {
            modelAndView.addObject("error", e.getMessage());
        }
        return modelAndView;
    }
}
