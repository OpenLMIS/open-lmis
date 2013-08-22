/*
 * Copyright © 2013 VillageReach.  All Rights Reserved.  This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 *
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
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

@Controller
@NoArgsConstructor
public class SupervisoryNodeController extends BaseController {

  public static final String SUPERVISORY_NODES = "supervisoryNodes";
  private SupervisoryNodeService supervisoryNodeService;

  @Autowired
  public SupervisoryNodeController(SupervisoryNodeService supervisoryNodeService) {
    this.supervisoryNodeService = supervisoryNodeService;
  }

  @RequestMapping(value = "/supervisory-nodes", method = RequestMethod.GET)
  @PreAuthorize("@permissionEvaluator.hasPermission(principal,'MANAGE_USER')")
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
