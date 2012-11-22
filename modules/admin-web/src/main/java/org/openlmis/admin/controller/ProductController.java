package org.openlmis.admin.controller;

import org.openlmis.core.domain.Product;
import org.openlmis.core.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.List;

@Controller
public class ProductController {

    private ProductService productService;

    @Autowired
    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @RequestMapping(value = "/logistics/products/{facilityCode}/{programCode}", method = RequestMethod.GET, headers = "Accept=application/json")
    public List<Product> getProductsByFacilityAndProgram(@PathVariable(value = "facilityCode") String facilityCode, @PathVariable(value = "programCode") String programCode) {
        return productService.getByFacilityAndProgram(facilityCode, programCode);
    }

}
