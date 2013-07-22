package org.openlmis.web.controller;

/**
 * User: mahmed
 * Date: 6/19/13
 */

import lombok.NoArgsConstructor;
import org.openlmis.core.domain.Facility;
import org.openlmis.core.domain.Product;
import org.openlmis.core.exception.DataException;
import org.openlmis.core.repository.mapper.DosageUnitMapper;
import org.openlmis.core.repository.mapper.ProductFormMapper;
import org.openlmis.core.service.ProductService;
import org.openlmis.web.response.OpenLmisResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.openlmis.report.service.ReportLookupService;
import org.openlmis.report.model.dto.ProgramProductPriceList;
import org.openlmis.report.service.ProgramProductPriceListDataProvider;
import org.openlmis.report.service.ProductListDataProvider;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;

import static org.openlmis.web.response.OpenLmisResponse.error;


@Controller
@NoArgsConstructor
public class ProductController extends BaseController {

    public static final String PRODUCTS= "Products";
    public static final String PRODUCT= "Product";
    public static final String PRODUCTLIST= "productList";
    public static final String DOSAGEUNITS= "dosageUnits";
    public static final String PRODUCTCOST= "productCost";
    public static final String ALLPRODUCTCOST= "allProductCost";

    @Autowired
    private ProductService productService;

    @Autowired
    private ProductListDataProvider productListService;

    @Autowired
    private ProgramProductPriceListDataProvider programPriceService;

    @Autowired
    private ReportLookupService reportLookupService;

    @Autowired
    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    // supply line list for view
    @RequestMapping(value = "/productslist", method = RequestMethod.GET, headers = "Accept=application/json")
    @PreAuthorize("@permissionEvaluator.hasPermission(principal,'MANAGE_PRODUCT')")
    public ResponseEntity<OpenLmisResponse> getProductsList() {
        return OpenLmisResponse.response(PRODUCTLIST, productListService.getProductList());
    }


    @RequestMapping(value = "/programs/{productId}/productcost", method = RequestMethod.GET, headers = ACCEPT_JSON)
    @PreAuthorize("@permissionEvaluator.hasPermission(principal,'MANAGE_PRODUCT')")
    public ResponseEntity<OpenLmisResponse> getByProductId(@PathVariable("productId") Long productId) {
        return OpenLmisResponse.response(PRODUCTCOST, programPriceService.getByProductId(productId));
    }

    // All product cost
    @RequestMapping(value = "/allproductcost", method = RequestMethod.GET, headers = ACCEPT_JSON)
    @PreAuthorize("@permissionEvaluator.hasPermission(principal,'MANAGE_PRODUCT')")
    public ResponseEntity<OpenLmisResponse> getAllPrices() {
        return OpenLmisResponse.response(ALLPRODUCTCOST, programPriceService.getAllPrices());
    }


    // mahmed - 07.11.2013  delete
    @RequestMapping(value = "/removeProduct/{id}", method = RequestMethod.GET, headers = ACCEPT_JSON)
    @PreAuthorize("@permissionEvaluator.hasPermission(principal,'MANAGE_PRODUCT')")
    public ResponseEntity<OpenLmisResponse> delete(@PathVariable("id") Long id, HttpServletRequest request) {
        try{
            productListService.deleteById(id);
            ResponseEntity<OpenLmisResponse> response = OpenLmisResponse.success("Product deactivated successfully");
            response.getBody().addData(PRODUCTLIST, productListService.getProductList());
            return response;
        }
        catch (DataException e) {
           return error(e, HttpStatus.BAD_REQUEST);
        }
    }

    // mahmed - 07.11.2013  delete
    @RequestMapping(value = "/restoreProduct/{id}", method = RequestMethod.GET, headers = ACCEPT_JSON)
    @PreAuthorize("@permissionEvaluator.hasPermission(principal,'MANAGE_PRODUCT')")
    public ResponseEntity<OpenLmisResponse> restore(@PathVariable("id") Long id, HttpServletRequest request) {
        try{
            productListService.restoreById(id);
            ResponseEntity<OpenLmisResponse> response = OpenLmisResponse.success("Product restored successfully");
            response.getBody().addData(PRODUCTLIST, productListService.getProductList());
            return response;
        }
        catch (DataException e) {
            return error(e, HttpStatus.BAD_REQUEST);
        }
    }

      // create product
    @RequestMapping(value = "/createProduct", method = RequestMethod.POST ,  headers = ACCEPT_JSON)
    @PreAuthorize("@permissionEvaluator.hasPermission(principal,'MANAGE_PRODUCT')")
    public ResponseEntity<OpenLmisResponse> save(@RequestBody Product product, HttpServletRequest request) {
       // set default values for some columns
       // this is a querk until all fields have UI fields

        product.setDosesPerDispensingUnit(1);
        product.setModifiedBy(loggedInUserId(request));
        product.setCreatedBy(loggedInUserId(request));
        product.setCreatedDate(new Date());
        product.setModifiedDate(new Date());
        return saveProduct(product, true);
    }

   // save/update
    private ResponseEntity<OpenLmisResponse> saveProduct(Product product, boolean createOperation) {
        try {
            productService.save(product);
            ResponseEntity<OpenLmisResponse> response = OpenLmisResponse.success("'" + product.getPrimaryName() + "' "+ (createOperation?"created":"updated") +" successfully");
            response.getBody().addData(PRODUCT, productListService.get(product.getId()));
            response.getBody().addData(PRODUCTLIST, productListService.getProductList());
            return response;
        } catch (DataException e) {
            return error(e, HttpStatus.BAD_REQUEST);
        }
    }

}
