package org.openlmis.restapi.controller;

import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiOperation;
import com.wordnik.swagger.annotations.ApiResponse;
import com.wordnik.swagger.annotations.ApiResponses;
import org.openlmis.core.domain.ProductCategory;
import org.openlmis.restapi.domain.ReportTypeDTO;
import org.openlmis.restapi.response.RestResponse;
import org.openlmis.restapi.service.RestReportTypeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;


@Api(value = "Report Types", description = "Opreate Report Types")
@Controller
@RequestMapping("/rest-api/report-types")
public class ReportTypeController extends BaseController {

    public static final String REPORT_TYPES = "report-types";

    private RestReportTypeService restReportTypeService;

    @Autowired
    public ReportTypeController(RestReportTypeService restReportTypeService) {
        this.restReportTypeService = restReportTypeService;
    }

    @ApiOperation(value = "Report Types", notes = "Returns a list of report types", response = ReportTypeDTO.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successful request", response = ProductCategory.class),
            @ApiResponse(code = 500, message = "Internal server error")}
    )
    @RequestMapping(method = RequestMethod.GET, headers = ACCEPT_JSON)
    public ResponseEntity getAllReportTypes() {
        return RestResponse.response(REPORT_TYPES, restReportTypeService.getAllReportType());
    }


}
