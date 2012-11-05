package org.openlmis.admin.controller;

import org.openlmis.core.domain.Product;
import org.openlmis.core.handler.ProductImportHandler;
import org.openlmis.upload.parser.CSVParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.util.HashMap;

@Controller
@RequestMapping("/admin")
public class UploadController {

    private CSVParser csvParser;
    private ProductImportHandler handler;
    private String uploadDir;
    HashMap<String,Class> modelMap=new HashMap<String, Class>();

    @Autowired
    public UploadController(@Value(value = "${upload.dir}") String uploadDir, CSVParser csvParser, ProductImportHandler handler) {
        this.uploadDir = uploadDir;
        this.csvParser = csvParser;
        this.handler = handler;
        modelMap.put("product",Product.class);
    }

    @RequestMapping(value = "/product/upload", method = RequestMethod.POST)
    public String upload(@RequestParam(value = "csvFile", required = true) MultipartFile multipartFile,
                         @RequestParam(value = "model", required = true) String model) {

        File uploadFile = new File(uploadDir + "productUpload.csv");
        try {
            multipartFile.transferTo(uploadFile);
            csvParser.process(uploadFile, modelMap.get(model), handler);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "{'msg':'uploaded successfully'}";
    }
}
