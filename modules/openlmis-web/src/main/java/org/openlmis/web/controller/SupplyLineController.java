/*
 * This program is part of the OpenLMIS logistics management information system platform software.
 * Copyright © 2013 VillageReach
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *  
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 * You should have received a copy of the GNU Affero General Public License along with this program.  If not, see http://www.gnu.org/licenses.  For additional information contact info@OpenLMIS.org. 
 */

package org.openlmis.web.controller;

import org.openlmis.core.domain.Pagination;
import org.openlmis.core.domain.SupplyLine;
import org.openlmis.core.exception.DataException;
import org.openlmis.core.service.SupplyLineService;
import org.openlmis.core.web.OpenLmisResponse;
import org.openlmis.core.web.controller.BaseController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

import static java.lang.Integer.parseInt;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.web.bind.annotation.RequestMethod.*;

/**
 * This controller handles endpoint to list, search, create, update supervisory nodes.
 */
@Controller
@RequestMapping(value = "/supplyLines")
public class SupplyLineController extends BaseController {

  public static final String SUPPLY_LINES = "supplyLines";
  public static final String PAGINATION = "pagination";
  public static final String SUPPLYLINES = "supplylines";
  public static final String SUPPLY_LINE_ID = "supplyLineId";

  @Autowired
  private SupplyLineService service;

  @RequestMapping(value = "/search", method = GET, headers = ACCEPT_JSON)
  @PreAuthorize("@permissionEvaluator.hasPermission(principal,'MANAGE_SUPPLY_LINE')")
  public ResponseEntity<OpenLmisResponse> search(@RequestParam(value = "searchParam") String searchParam,
                                                 @RequestParam(value = "column") String column,
                                                 @RequestParam(value = "page", defaultValue = "1") Integer page,
                                                 @Value("${search.page.size}") String limit) {

    Pagination pagination = new Pagination(page, parseInt(limit));
    pagination.setTotalRecords(service.getTotalSearchResultCount(searchParam, column));
    List<SupplyLine> supplyLines = service.search(searchParam, column, pagination);
    ResponseEntity<OpenLmisResponse> response = OpenLmisResponse.response(SUPPLY_LINES, supplyLines);
    response.getBody().addData(PAGINATION, pagination);
    return response;
  }

  @RequestMapping(method = POST, headers = ACCEPT_JSON)
  @PreAuthorize("@permissionEvaluator.hasPermission(principal,'MANAGE_SUPPLY_LINE')")
  public ResponseEntity<OpenLmisResponse> insert(@RequestBody SupplyLine supplyLine,
                                                 HttpServletRequest request) {
    ResponseEntity<OpenLmisResponse> response;
    Long userId = loggedInUserId(request);
    supplyLine.setCreatedBy(userId);
    supplyLine.setModifiedBy(userId);
    try {
      service.save(supplyLine);
    } catch (DataException de) {
      response = OpenLmisResponse.error(de, BAD_REQUEST);
      return response;
    }
    response = OpenLmisResponse.success(messageService.message("message.supply.line.created.success"));
    response.getBody().addData(SUPPLY_LINE_ID, supplyLine.getId());
    return response;
  }

  @RequestMapping(value = "/{id}", method = PUT, headers = ACCEPT_JSON)
  @PreAuthorize("@permissionEvaluator.hasPermission(principal,'MANAGE_SUPPLY_LINE')")
  public ResponseEntity<OpenLmisResponse> update(@RequestBody SupplyLine supplyLine,
                                                 @PathVariable(value = "id") Long supervisoryNodeId,
                                                 HttpServletRequest request) {
    ResponseEntity<OpenLmisResponse> response;
    Long userId = loggedInUserId(request);
    supplyLine.setModifiedBy(userId);
    supplyLine.setId(supervisoryNodeId);
    try {
      service.save(supplyLine);
    } catch (DataException de) {
      response = OpenLmisResponse.error(de, BAD_REQUEST);
      return response;
    }
    response = OpenLmisResponse.success(messageService.message("message.supply.line.updated.success"));
    response.getBody().addData(SUPPLY_LINE_ID, supplyLine.getId());
    return response;
  }

  @RequestMapping(value = "/{id}", method = GET)
  @PreAuthorize("@permissionEvaluator.hasPermission(principal,'MANAGE_SUPPLY_LINE')")
  public SupplyLine getById(@PathVariable(value = "id") Long id) {
    return service.getById(id);
  }

  @RequestMapping(value = "/supplying-depots.json", method = GET, headers = ACCEPT_JSON)
  public ResponseEntity<OpenLmisResponse> getSupplyingDepots(HttpServletRequest request){

    return OpenLmisResponse.response(SUPPLYLINES, service.getSupplyingFacilities(loggedInUserId(request)));
  }
}
