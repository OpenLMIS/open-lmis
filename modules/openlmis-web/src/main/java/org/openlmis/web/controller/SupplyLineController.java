package org.openlmis.web.controller;

import org.openlmis.core.domain.Pagination;
import org.openlmis.core.domain.SupplyLine;
import org.openlmis.core.service.SupplyLineService;
import org.openlmis.web.response.OpenLmisResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

import static org.springframework.web.bind.annotation.RequestMethod.GET;

@Controller
@RequestMapping(value = "/supplyLines")
public class SupplyLineController extends BaseController {

  public static final String SUPPLY_LINES = "supplyLines";
  public static final String PAGINATION = "pagination";

  @Autowired
  private SupplyLineService service;

  @RequestMapping(value = "/search", method = GET, headers = ACCEPT_JSON)
  @PreAuthorize("@permissionEvaluator.hasPermission(principal,'MANAGE_SUPPLY_LINE')")
  public ResponseEntity<OpenLmisResponse> search(@RequestParam(value = "searchParam") String searchParam,
                                                 @RequestParam(value = "columnName") String columnName,
                                                 @RequestParam(value = "page", defaultValue = "1") Integer page,
                                                 @Value("${search.page.size}") String limit) {

    Pagination pagination = new Pagination(page, Integer.parseInt(limit));
    pagination.setTotalRecords(service.getTotalSearchResultCount(searchParam, columnName));
    List<SupplyLine> supplyLines = service.search(searchParam, columnName, pagination);
    ResponseEntity<OpenLmisResponse> response = OpenLmisResponse.response(SUPPLY_LINES, supplyLines);
    response.getBody().addData(PAGINATION, pagination);
    return response;
  }
}
