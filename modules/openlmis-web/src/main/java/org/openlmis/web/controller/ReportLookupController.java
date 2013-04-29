package org.openlmis.web.controller;

import lombok.NoArgsConstructor;
import org.openlmis.report.model.dto.Product;
import org.openlmis.report.model.dto.RequisitionGroup;
import org.openlmis.report.service.ReportLookupService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

import static org.springframework.web.bind.annotation.RequestMethod.GET;

/**

 */
@Controller
@NoArgsConstructor
@RequestMapping(value = "/reports")
public class ReportLookupController extends BaseController {

    public static final String USER_ID = "USER_ID";

    private ReportLookupService productReportService;

    @Autowired
    public ReportLookupController(ReportLookupService productReportService) {
        this.productReportService = productReportService;
    }


    @RequestMapping(value="/products", method = GET, headers = ACCEPT_JSON)
    public List<Product> getProducts(){
          return this.productReportService.getAllProducts();
    }

    @RequestMapping(value="/rgroups", method = GET, headers = ACCEPT_JSON)
    public List<RequisitionGroup> getRequisitionGroups(){
        return this.productReportService.getAllRequisitionGroups();
    }

}
