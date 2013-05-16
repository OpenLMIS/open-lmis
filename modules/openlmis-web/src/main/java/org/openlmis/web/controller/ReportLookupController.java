package org.openlmis.web.controller;

import lombok.NoArgsConstructor;
import org.openlmis.report.model.dto.AdjustmentType;
import org.openlmis.report.model.dto.Product;
import org.openlmis.report.model.dto.ProductCategory;
import org.openlmis.report.model.dto.RequisitionGroup;
import org.openlmis.report.service.ReportLookupService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

import static org.springframework.web.bind.annotation.RequestMethod.GET;

/**

 */
@Controller
@NoArgsConstructor
@RequestMapping(value = "/reports")
public class ReportLookupController extends BaseController {

    public static final String USER_ID = "USER_ID";

    private ReportLookupService reportLookupService;

    @Autowired
    public ReportLookupController(ReportLookupService productReportService) {
        this.reportLookupService = productReportService;
    }


    @RequestMapping(value="/products", method = GET, headers = ACCEPT_JSON)
    public List<Product> getProducts(){
          return this.reportLookupService.getAllProducts();
    }

    @RequestMapping(value="/rgroups", method = GET, headers = ACCEPT_JSON)
    public List<RequisitionGroup> getRequisitionGroups(){
        return this.reportLookupService.getAllRequisitionGroups();
    }

    @RequestMapping(value="/reporting_groups_by_program_schedule", method = GET, headers = ACCEPT_JSON)
    public List<RequisitionGroup> getRequisitionGroupsByProgramSchedule(
            @RequestParam(value = "program", required = true, defaultValue = "1") int program,
            @RequestParam(value = "schedule", required = true, defaultValue = "10") int schedule
    ){
        return this.reportLookupService.getRequisitionGroupsByProgramAndSchedule(program,schedule);
    }


    @RequestMapping(value="/productCategories", method = GET, headers = ACCEPT_JSON)
    public List<ProductCategory> getProductCategories(){
        return this.reportLookupService.getAllProductCategories();
    }

    @RequestMapping(value="/adjustmentTypes", method = GET, headers = ACCEPT_JSON)
    public List<AdjustmentType> getAdjustmentTypes(){
        return this.reportLookupService.getAllAdjustmentTypes();
    }


}
