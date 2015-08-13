package org.openlmis.web.controller;


import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiOperation;
import org.openlmis.core.dto.StockAdjustmentReason;
import org.openlmis.core.service.StockManagementConfigService;
import org.openlmis.web.response.OpenLmisResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

import static org.springframework.web.bind.annotation.RequestMethod.GET;

@Controller
@Api(value = "Stock Cards", description = "Track the stock cards (stock on hand) at various facilities.")
@RequestMapping(value = "/api/v2/stockManagement/")
public class StockManagementConfigController extends BaseController {

  @Autowired
  StockManagementConfigService service;

  @RequestMapping(value = "adjustmentReasons", method = GET, headers = ACCEPT_JSON)
  @ApiOperation(value = "Get information about all stock management adjustment reasons from the system.",
      notes = "(This endpoint is not yet ready for use.)")
  public ResponseEntity getAdjustmentReasons(@RequestParam(value = "additive", required = false) Boolean additive,
                                             @RequestParam(value = "programId", required = false) Long programId)
  {
    List<StockAdjustmentReason> reasons = service.getAdjustmentReasons(additive, programId);

    if (reasons != null) {
      return OpenLmisResponse.response("adjustmentReasons", reasons);
    } else {
      return OpenLmisResponse.error("Adjustment reasons do not exist.", HttpStatus.NOT_FOUND);
    }
  }

}
