package org.openlmis.web.controller;

/**
 * User: mahmed
 * Date: 6/19/13
 */

import lombok.NoArgsConstructor;
import org.openlmis.core.domain.*;
import org.openlmis.core.exception.DataException;
import org.openlmis.core.repository.mapper.DosageUnitMapper;
import org.openlmis.core.repository.mapper.ProductFormMapper;
import org.openlmis.core.service.ProductGroupService;
import org.openlmis.core.service.ProductService;
import org.openlmis.core.service.ProgramProductService;
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

    public static final String PRODUCTS= "products";
    public static final String PRODUCT= "product";
    public static final String PRODUCTLIST= "productList";
    public static final String DOSAGEUNITS= "dosageUnits";
    public static final String PRODUCTCOST= "productCost";
    public static final String ALLPRODUCTCOST= "allProductCost";

    @Autowired
    private ProductService productService;

    @Autowired
    private ProductListDataProvider productListService;

    @Autowired
    private ReportLookupService reportLookupService;

    @Autowired
    private ProgramProductService programProductService;

    @Autowired
    private ProductGroupService productGroupService;


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

    @RequestMapping(value = "/productDetail/{id}", method = RequestMethod.GET, headers = ACCEPT_JSON)
    @PreAuthorize("@permissionEvaluator.hasPermission(principal,'MANAGE_PRODUCT')")
    public ResponseEntity<OpenLmisResponse> getProductDetails(@PathVariable("id") Long id){
      Product product = productListService.get(id);
      product.setProgramProducts(programProductService.getOptionsByProduct(product));
      return OpenLmisResponse.response("product", product);
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

    // mahmed - 07.11.2013  update
    @RequestMapping(value = "/updateProduct", method = RequestMethod.PUT, headers = ACCEPT_JSON)
    @PreAuthorize("@permissionEvaluator.hasPermission(principal,'MANAGE_PRODUCT')")
    public ResponseEntity<OpenLmisResponse> update( @RequestBody Product product,
                                                    HttpServletRequest request) {
        //product.setId(id);
        product.setModifiedBy(loggedInUserId(request));
        product.setModifiedDate(new Date());
        return saveProduct(product, false);
    }
    // create product
    @RequestMapping(value = "/createProduct", method = RequestMethod.POST ,  headers = ACCEPT_JSON)
    @PreAuthorize("@permissionEvaluator.hasPermission(principal,'MANAGE_PRODUCT')")
    public ResponseEntity<OpenLmisResponse> save(@RequestBody Product product, HttpServletRequest request) {
        product.setModifiedBy(loggedInUserId(request));
        product.setModifiedDate(new Date());
        return saveProduct(product, true);
    }

   // save/update
    private ResponseEntity<OpenLmisResponse> saveProduct(Product product, boolean createOperation) {
        try {
          setReferenceObjects(product);
          productService.save(product);

          for(org.openlmis.core.domain.ProgramProduct pp: product.getProgramProducts()){
              // set the product for each of the program products ... for the save functionalitiy to work
              pp.setProduct(product);
              programProductService.save(pp);
            }

            ResponseEntity<OpenLmisResponse> response = OpenLmisResponse.success("'" + product.getPrimaryName() + "' "+ (createOperation?"created":"updated") +" successfully");
            response.getBody().addData(PRODUCT, productListService.get(product.getId()));
            response.getBody().addData(PRODUCTLIST, productListService.getProductList());
            return response;
        } catch (DataException e) {
            return error(e, HttpStatus.BAD_REQUEST);
        }
    }
  // TODO: move this class to some other class
  // may be the service or the domain object itself.
  private void setReferenceObjects(Product product) {
    // prepare the reference data
    // set from reference information for the online form... that returns it using the id columns
    if(product.getForm() == null && product.getFormId() != null){
      product.setForm(new ProductForm());
      product.getForm().setId(product.getFormId());
    }

    if(product.getDosageUnit() == null && product.getDosageUnitId() != null){
      product.setDosageUnit(new DosageUnit());
      product.getDosageUnit().setId(product.getDosageUnitId());
    }

    if(product.getProductGroup() == null && product.getProductGroupId() != null){
      product.setProductGroup(new ProductGroup());
      product.getProductGroup().setId(product.getProductGroupId());
    }

    if(product.getCategory() == null && product.getCategoryId() != null){
      product.setCategory(new ProductCategory());
      product.getCategory().setId(product.getFormId());
    }
  }

}
