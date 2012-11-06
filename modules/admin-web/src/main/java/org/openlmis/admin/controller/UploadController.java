package org.openlmis.admin.controller;

import org.openlmis.core.domain.Product;
import org.openlmis.core.handler.ProductImportHandler;
import org.openlmis.upload.parser.CSVParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;

@Controller
@RequestMapping("resources/pages/admin")
public class UploadController {

    private CSVParser csvParser;
    private ProductImportHandler handler;
    HashMap<String, Class> modelMap = new HashMap<String, Class>();

    @Autowired
    public UploadController(CSVParser csvParser, ProductImportHandler handler) {
        this.csvParser = csvParser;
        this.handler = handler;
        modelMap.put("product", Product.class);
    }

    @RequestMapping(value = "/{model}/upload", method = RequestMethod.POST)
    public String upload(@RequestParam(value = "csvFile", required = true) MultipartFile multipartFile,
                         @PathVariable String model) {

        try {
            csvParser.process(multipartFile.getInputStream(), modelMap.get(model), handler);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "{'msg':'uploaded successfully'}";
    }
}
