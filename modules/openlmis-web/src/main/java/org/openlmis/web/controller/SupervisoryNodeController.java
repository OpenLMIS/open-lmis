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

import lombok.NoArgsConstructor;
import org.openlmis.core.domain.Pagination;
import org.openlmis.core.domain.SupervisoryNode;
import org.openlmis.core.service.SupervisoryNodeService;
import org.openlmis.web.response.OpenLmisResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

/**
 * This controller handles request to get list of supervisory nodes.
 */

@Controller
@NoArgsConstructor
public class SupervisoryNodeController extends BaseController {

  public static final String SUPERVISORY_NODES = "supervisoryNodes";
  public static final String SUPERVISORY_NODE = "supervisoryNode";

  @Autowired
  private SupervisoryNodeService supervisoryNodeService;

  @RequestMapping(value = "/supervisory-nodes", method = RequestMethod.GET)
  @PreAuthorize("@permissionEvaluator.hasPermission(principal,'MANAGE_USER')")
  public ResponseEntity<OpenLmisResponse> getAll() {
    return OpenLmisResponse.response(SUPERVISORY_NODES, supervisoryNodeService.getAll());
  }

  @RequestMapping(value = "/search-supervisory-nodes", method = RequestMethod.GET)
  @PreAuthorize("@permissionEvaluator.hasPermission(principal,'MANAGE_SUPERVISORY_NODE')")
  public ResponseEntity<OpenLmisResponse> searchSupervisoryNode(@RequestParam(value = "page",
    required = true,
    defaultValue = "1") Integer page, @RequestParam(required = true) String param, @RequestParam(required = true) Boolean parent) {
    ResponseEntity<OpenLmisResponse> response = OpenLmisResponse.response(SUPERVISORY_NODES, supervisoryNodeService.getSupervisoryNodesBy(page, param, parent));
    Pagination pagination = supervisoryNodeService.getPagination(page);
    pagination.setTotalRecords(supervisoryNodeService.getTotalSearchResultCount(param, parent));
    response.getBody().addData("pagination", pagination);
    return response;
  }

  @RequestMapping(value = "/supervisory-nodes/{id}", method = RequestMethod.GET)
  @PreAuthorize("@permissionEvaluator.hasPermission(principal,'MANAGE_SUPERVISORY_NODE')")
  public ResponseEntity<OpenLmisResponse> getById(@PathVariable(value = "id") Long id) {
    SupervisoryNode supervisoryNode = supervisoryNodeService.getById(id);
    return OpenLmisResponse.response(SUPERVISORY_NODE, supervisoryNode);
  }

  @RequestMapping(value = "/parent-supervisory-nodes", method = RequestMethod.GET)
  @PreAuthorize("@permissionEvaluator.hasPermission(principal,'MANAGE_SUPERVISORY_NODE')")
  public ResponseEntity<OpenLmisResponse> getFilteredNodes(@RequestParam(value = "searchParam") String param) {
    List<SupervisoryNode> filteredSupervisoryNodes = supervisoryNodeService.getFilteredSupervisoryNodesByName(param);
    return OpenLmisResponse.response(SUPERVISORY_NODES, filteredSupervisoryNodes);
  }
}
