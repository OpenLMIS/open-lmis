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

@Controller
@RequestMapping("/admin")
public class ProductController {

    private CSVParser csvParser;
    private ProductImportHandler handler;
    private String uploadDir;

    @Autowired
    public ProductController(@Value(value = "${upload.dir}") String uploadDir, CSVParser csvParser, ProductImportHandler handler) {
        this.uploadDir = uploadDir;
        this.csvParser = csvParser;
        this.handler = handler;
    }

    @RequestMapping(value = "/product/upload", method = RequestMethod.POST)
    public String upload(@RequestParam(value = "csvFile", required = true) MultipartFile multipartFile) {

        File uploadFile = new File(uploadDir + "productUpload.csv");
        try {
            multipartFile.transferTo(uploadFile);
            csvParser.process(uploadFile, Product.class, handler);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "{'msg':'uploaded successfully'}";
    }
}
