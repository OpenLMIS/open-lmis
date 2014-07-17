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
import org.openlmis.core.exception.DataException;
import org.openlmis.core.service.SupervisoryNodeService;
import org.openlmis.web.response.OpenLmisResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.openlmis.core.domain.SupervisoryNode;
import org.springframework.web.bind.annotation.RequestMethod;
import static org.openlmis.web.response.OpenLmisResponse.*;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import javax.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;

/**
 * This controller handles request to get list of supervisory nodes.
 */

@Controller
@NoArgsConstructor
public class SupervisoryNodeController extends BaseController {

  public static final String SUPERVISORY_NODES = "supervisoryNodes";

  @Autowired
  private SupervisoryNodeService supervisoryNodeService;

  @RequestMapping(value = "/supervisory-nodes", method = RequestMethod.GET)
  @PreAuthorize("@permissionEvaluator.hasPermission(principal,'MANAGE_USER, MANAGE_REQUISITION_GROUP')")
  public ResponseEntity<OpenLmisResponse> getAll() {
    return OpenLmisResponse.response(SUPERVISORY_NODES, supervisoryNodeService.getAll());
  }

  @RequestMapping(value="/supervisoryNode/getList",method= RequestMethod.GET, headers = ACCEPT_JSON)
  @PreAuthorize("@permissionEvaluator.hasPermission(principal,'MANAGE_SUPERVISORY_NODE')")
  public ResponseEntity<OpenLmisResponse> getSupervisoryNodeList(HttpServletRequest request){
      return OpenLmisResponse.response("supervisoryNodes", supervisoryNodeService.getCompleteList());
  }

  @RequestMapping(value="/supervisoryNode/insert.json",method=RequestMethod.POST,headers = ACCEPT_JSON)
  @PreAuthorize("@permissionEvaluator.hasPermission(principal,'MANAGE_SUPERVISORY_NODE')")
  public ResponseEntity<OpenLmisResponse> insert(@RequestBody SupervisoryNode supervisoryNode, HttpServletRequest request){
      ResponseEntity<OpenLmisResponse> successResponse;
      supervisoryNode.setModifiedBy(loggedInUserId(request));
      try {
          supervisoryNodeService.save_Ext(supervisoryNode);
      } catch (DataException e) {
          return error(e, HttpStatus.BAD_REQUEST);
      }
      successResponse = success(String.format("Supervisory node '%s' has been successfully saved", supervisoryNode.getName()));
      successResponse.getBody().addData("supervisoryNode", supervisoryNode);
      return successResponse;
  }


  @RequestMapping(value="/supervisoryNode/getDetails/{id}",method = RequestMethod.GET,headers = ACCEPT_JSON)
  @PreAuthorize("@permissionEvaluator.hasPermission(principal,'MANAGE_SUPERVISORY_NODE')")
  public ResponseEntity<OpenLmisResponse> getDetailsForSupervisoryNode(@PathVariable(value="id") Long id){
      return OpenLmisResponse.response("supervisoryNode", supervisoryNodeService.loadSupervisoryNodeById(id));
  }

  @RequestMapping(value="/supervisoryNode/remove/{id}",method = RequestMethod.GET,headers = ACCEPT_JSON)
  @PreAuthorize("@permissionEvaluator.hasPermission(principal,'MANAGE_SUPERVISORY_NODE')")
  public ResponseEntity<OpenLmisResponse> remove(@PathVariable(value="id") Long supervisoryNodeId, HttpServletRequest request){
      ResponseEntity<OpenLmisResponse> successResponse;
      try {
          supervisoryNodeService.removeSupervisoryNode(supervisoryNodeId);
      } catch (DataException e) {
          return error(e, HttpStatus.BAD_REQUEST);
      }
      successResponse = success(String.format("Supervisory node has been successfully removed"));
      return successResponse;
  }


}
