package org.openlmis.web.controller;

/**
 * User: mahmed
 * Date: 6/19/13
 */

import lombok.NoArgsConstructor;
import org.openlmis.core.domain.Facility;
import org.openlmis.core.exception.DataException;
import org.openlmis.core.repository.mapper.DosageUnitMapper;
import org.openlmis.core.repository.mapper.ProductFormMapper;
import org.openlmis.core.service.ProductServiceExtension;
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

import javax.servlet.http.HttpServletRequest;
import java.util.Date;

import static org.openlmis.web.response.OpenLmisResponse.error;


@Controller
@NoArgsConstructor
public class ProductController extends BaseController {

    public static final String PRODUCTS= "products";
    public static final String PRODUCT= "product";
    public static final String PRODUCTLIST= "productList";
    @Autowired
    private ProductServiceExtension productServiceExt;


    // supply line list for view
    @RequestMapping(value = "/productslist", method = RequestMethod.GET, headers = "Accept=application/json")
    @PreAuthorize("@permissionEvaluator.hasPermission(principal,'MANAGE_PRODUCT')")
    public ResponseEntity<OpenLmisResponse> getList() {
        return OpenLmisResponse.response(PRODUCTLIST, productServiceExt.getProductsList());
    }


}
