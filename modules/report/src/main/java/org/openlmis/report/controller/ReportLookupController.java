package org.openlmis.report.controller;

import lombok.NoArgsConstructor;
import org.openlmis.core.service.FacilityService;
import org.openlmis.report.model.dto.AdjustmentType;
import org.openlmis.report.model.dto.Product;
import org.openlmis.report.model.dto.Schedule;
import org.openlmis.report.model.dto.Program;
import org.openlmis.report.model.dto.ProductCategory;
import org.openlmis.report.model.dto.RequisitionGroup;
import org.openlmis.report.service.ReportLookupService;
import org.openlmis.report.response.OpenLmisResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

import static org.springframework.web.bind.annotation.RequestMethod.GET;

/**

 */
@Controller
@NoArgsConstructor
@RequestMapping(value = "/reports")
public class ReportLookupController extends BaseController {

    public static final String USER_ID = "USER_ID";

    public static final String OPEN_LMIS_OPERATION_YEARS = "years";
    public static final String OPEN_LMIS_OPERATION_MONTHS = "months";

    @Autowired
    private ReportLookupService reportLookupService;
    @Autowired
    private FacilityService facilityService;


    @RequestMapping(value="/programs", method = GET, headers = BaseController.ACCEPT_JSON)
    public ResponseEntity<OpenLmisResponse> getPrograms(){
        return OpenLmisResponse.response( "programs", this.reportLookupService.getAllPrograms() );
    }

    @RequestMapping(value="/schedules", method = GET, headers = BaseController.ACCEPT_JSON)
    public ResponseEntity<OpenLmisResponse> getSchedules(){
        return OpenLmisResponse.response( "schedules", this.reportLookupService.getAllSchedules() ) ;
    }

    @RequestMapping(value="/facilityTypes", method = GET, headers = BaseController.ACCEPT_JSON)
    public ResponseEntity<OpenLmisResponse> getFacilityTypes(){
        return OpenLmisResponse.response( "facilityTypes", this.reportLookupService.getFacilityTypes() ) ;
    }


    @RequestMapping(value="/products", method = GET, headers = BaseController.ACCEPT_JSON)
    public List<Product> getProducts(){
          return this.reportLookupService.getAllProducts();
    }

    @RequestMapping(value="/rgroups", method = GET, headers = BaseController.ACCEPT_JSON)
    public List<RequisitionGroup> getRequisitionGroups(){
        return this.reportLookupService.getAllRequisitionGroups();
    }

    @RequestMapping(value="/reporting_groups_by_program_schedule", method = GET, headers = BaseController.ACCEPT_JSON)
    public List<RequisitionGroup> getRequisitionGroupsByProgramSchedule(
            @RequestParam(value = "program", required = true, defaultValue = "1") int program,
            @RequestParam(value = "schedule", required = true, defaultValue = "10") int schedule
    ){
        return this.reportLookupService.getRequisitionGroupsByProgramAndSchedule(program,schedule);
    }


    @RequestMapping(value="/productCategories", method = GET, headers = BaseController.ACCEPT_JSON)
    public List<ProductCategory> getProductCategories(){
        return this.reportLookupService.getAllProductCategories();
    }

    @RequestMapping(value="/adjustmentTypes", method = GET, headers = BaseController.ACCEPT_JSON)
    public List<AdjustmentType> getAdjustmentTypes(){
        return this.reportLookupService.getAllAdjustmentTypes();
    }

    @RequestMapping(value = "/operationYears", method = GET, headers = BaseController.ACCEPT_JSON)
    public Map getOperationYears() {
        MultiValueMap operationPeriods = new LinkedMultiValueMap<>();
        operationPeriods.put(OPEN_LMIS_OPERATION_YEARS,reportLookupService.getOperationYears());

        return operationPeriods;
    }

    @RequestMapping(value = "/months", method = GET, headers = BaseController.ACCEPT_JSON)
    public Map getAllMonths() {
        MultiValueMap months = new LinkedMultiValueMap<>();
        months.put(OPEN_LMIS_OPERATION_MONTHS,reportLookupService.getAllMonths());

        return months;
    }

    @RequestMapping(value = "/geographicZones", method = GET, headers = BaseController.ACCEPT_JSON)
    public ResponseEntity<OpenLmisResponse> getAllGeographicZones(HttpServletRequest request) {
        return OpenLmisResponse.response("zones", reportLookupService.getAllZones());
    }

}
