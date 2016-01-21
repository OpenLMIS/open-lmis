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
import org.openlmis.core.exception.DataException;
import org.openlmis.core.service.SupervisoryNodeService;
import org.openlmis.core.web.OpenLmisResponse;
import org.openlmis.core.web.controller.BaseController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.web.bind.annotation.RequestMethod.*;

/**
 * This controller handle endpoints to list, search, create, update supervisory nodes.
 */

@Controller
@NoArgsConstructor
public class SupervisoryNodeController extends BaseController {

  public static final String SUPERVISORY_NODES = "supervisoryNodes";

  @Autowired
  private SupervisoryNodeService supervisoryNodeService;

  @RequestMapping(value = "/supervisory-nodes", method = GET)
  @PreAuthorize("@permissionEvaluator.hasPermission(principal,'MANAGE_USER')")
  public ResponseEntity<OpenLmisResponse> getAll() {
    return OpenLmisResponse.response(SUPERVISORY_NODES, supervisoryNodeService.getAll());
  }

  @RequestMapping(value = "/supervisory-nodes/list", method = GET)
  public ResponseEntity<OpenLmisResponse> getAllReadOnly() {
    return OpenLmisResponse.response(SUPERVISORY_NODES, supervisoryNodeService.getAll());
  }

  @RequestMapping(value = "/paged-search-supervisory-nodes", method = GET)
  @PreAuthorize("@permissionEvaluator.hasPermission(principal,'MANAGE_SUPERVISORY_NODE')")
  public ResponseEntity<OpenLmisResponse> searchSupervisoryNode(@RequestParam(value = "page", required = true, defaultValue = "1") Integer page,
                                                                @RequestParam(required = true) String param,
                                                                @RequestParam(required = true) Boolean parent) {
    ResponseEntity<OpenLmisResponse> response = OpenLmisResponse.response(SUPERVISORY_NODES,
      supervisoryNodeService.getSupervisoryNodesBy(page, param, parent));
    Pagination pagination = supervisoryNodeService.getPagination(page);
    pagination.setTotalRecords(supervisoryNodeService.getTotalSearchResultCount(param, parent));
    response.getBody().addData("pagination", pagination);
    return response;
  }

  @RequestMapping(value = "/supervisory-nodes/{id}", method = GET)
  @PreAuthorize("@permissionEvaluator.hasPermission(principal,'MANAGE_SUPERVISORY_NODE')")
  public SupervisoryNode getById(@PathVariable(value = "id") Long id) {
    return supervisoryNodeService.getSupervisoryNode(id);
  }

  @RequestMapping(value = "/search-supervisory-nodes", method = GET, headers = ACCEPT_JSON)
  @PreAuthorize("@permissionEvaluator.hasPermission(principal,'MANAGE_SUPERVISORY_NODE, MANAGE_REQUISITION_GROUP')")
  public List<SupervisoryNode> getFilteredNodes(@RequestParam(value = "searchParam") String param) {
    return supervisoryNodeService.getFilteredSupervisoryNodesByName(param);
  }

  @RequestMapping(value = "/supervisory-nodes", method = POST)
  @PreAuthorize("@permissionEvaluator.hasPermission(principal,'MANAGE_SUPERVISORY_NODE')")
  public ResponseEntity<OpenLmisResponse> insert(@RequestBody SupervisoryNode supervisoryNode,
                                                 HttpServletRequest request) {
    ResponseEntity<OpenLmisResponse> response;
    Long userId = loggedInUserId(request);
    supervisoryNode.setCreatedBy(userId);
    supervisoryNode.setModifiedBy(userId);
    try {
      supervisoryNodeService.save(supervisoryNode);
    } catch (DataException de) {
      response = OpenLmisResponse.error(de, BAD_REQUEST);
      return response;
    }
    response = OpenLmisResponse.success(
      messageService.message("message.supervisory.node.created.success", supervisoryNode.getName()));
    response.getBody().addData("supervisoryNodeId", supervisoryNode.getId());
    return response;
  }

  @RequestMapping(value = "/supervisory-nodes/{id}", method = PUT, headers = ACCEPT_JSON)
  @PreAuthorize("@permissionEvaluator.hasPermission(principal,'MANAGE_SUPERVISORY_NODE')")
  public ResponseEntity<OpenLmisResponse> update(@RequestBody SupervisoryNode supervisoryNode,
                                                 @PathVariable(value = "id") Long supervisoryNodeId,
                                                 HttpServletRequest request) {
    ResponseEntity<OpenLmisResponse> response;
    Long userId = loggedInUserId(request);
    supervisoryNode.setModifiedBy(userId);
    supervisoryNode.setId(supervisoryNodeId);
    try {
      supervisoryNodeService.save(supervisoryNode);
    } catch (DataException de) {
      response = OpenLmisResponse.error(de, BAD_REQUEST);
      return response;
    }
    response = OpenLmisResponse.success(
      messageService.message("message.supervisory.node.updated.success", supervisoryNode.getName()));
    response.getBody().addData("supervisoryNodeId", supervisoryNode.getId());
    return response;
  }

  @RequestMapping(value = "/topLevelSupervisoryNodes", method = GET, headers = ACCEPT_JSON)
  @PreAuthorize("@permissionEvaluator.hasPermission(principal,'MANAGE_SUPPLY_LINE')")
  public List<SupervisoryNode> searchTopLevelSupervisoryNodesByName(@RequestParam(value = "searchParam") String param) {
    return supervisoryNodeService.searchTopLevelSupervisoryNodesByName(param);
  }
}
