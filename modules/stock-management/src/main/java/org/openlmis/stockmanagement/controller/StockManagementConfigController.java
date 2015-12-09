package org.openlmis.stockmanagement.controller;


import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.StringUtils;
import org.openlmis.core.domain.StockAdjustmentReason;
import org.openlmis.core.service.StockAdjustmentReasonService;
import org.openlmis.core.web.controller.BaseController;
import org.openlmis.core.web.OpenLmisResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

import static org.springframework.web.bind.annotation.RequestMethod.GET;

@Controller
@Api(value = "Stock Cards", description = "Track the stock cards (stock on hand) at various facilities.")
@RequestMapping(value = "/api/v2/stockManagement/")
public class StockManagementConfigController extends BaseController {

  @Autowired
  StockAdjustmentReasonService service;

  @Transactional
  @RequestMapping(value = "adjustmentReasons", method = GET, headers = ACCEPT_JSON)
  @ApiOperation(value = "Get information about all stock adjustment reasons from the system.",
      notes = "Gets stock adjustment reasons from the system. Can be specified by program, or returns a default list " +
              "if program is not specified. Additive boolean can be specified to only get positive or negative reasons, " +
              "otherwise return all." +
              "<p>Request parameters:" +
              "<ul>" +
              "<li><strong>programId</strong> (Integer, optional, no default) - program for program-specific " +
              "adjustment reasons. If not specified, get default reasons.</li>" +
              "<li><strong>additive</strong> (Boolean, optional, no default) - in order to get additive, or " +
              "non-additive reasons. If not specified, get both.</li>" +
              "<li><strong>category</strong> (String, optional, DEFAULT category) - a category parameter will " +
              "get only those reasons that belong to the given category.</li>" +
              "</ul>")
  public ResponseEntity getAdjustmentReasons(@RequestParam(value = "additive", required = false) Boolean additive,
                                             @RequestParam(value = "programId", required = false) Long programId,
                                             @RequestParam(value = "category", required = false) String categoryStr)
  {
    StockAdjustmentReason.Category category = StockAdjustmentReason.Category.parse(categoryStr);
    if(false == StringUtils.isBlank(categoryStr) && null == category)
      return OpenLmisResponse.error("Category not found", HttpStatus.BAD_REQUEST);

    List<StockAdjustmentReason> reasons = service.getAdjustmentReasons(additive, programId, category);

    if (reasons != null) {
      return OpenLmisResponse.response("adjustmentReasons", reasons);
    } else {
      return OpenLmisResponse.error("Adjustment reasons do not exist.", HttpStatus.NOT_FOUND);
    }
  }
}
