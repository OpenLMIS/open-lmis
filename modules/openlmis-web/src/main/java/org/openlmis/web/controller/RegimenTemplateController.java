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

import org.openlmis.core.web.controller.BaseController;
import org.openlmis.rnr.service.RegimenColumnService;
import org.openlmis.core.web.OpenLmisResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import static org.springframework.web.bind.annotation.RequestMethod.GET;

/**
 * This controller handles endpoint related to configure regimen template.
 */

@Controller
public class RegimenTemplateController extends BaseController {

  @Autowired
  private RegimenColumnService service;

  @RequestMapping(value = "/programId/{programId}/configureRegimenTemplate", method = GET, headers = ACCEPT_JSON)
  @PreAuthorize("@permissionEvaluator.hasPermission(principal,'MANAGE_REGIMEN_TEMPLATE, VIEW_REQUISITION')")
  public ResponseEntity<OpenLmisResponse> getProgramOrMasterRegimenTemplate(@PathVariable Long programId) {
    return OpenLmisResponse.response("template", service.getRegimenTemplateOrMasterTemplate(programId));
  }

  @RequestMapping(value = "/programId/{programId}/programRegimenTemplate", method = GET, headers = ACCEPT_JSON)
  @PreAuthorize("@permissionEvaluator.hasPermission(principal,'VIEW_REQUISITION')")
  public ResponseEntity<OpenLmisResponse> getProgramRegimenTemplate(@PathVariable Long programId) {
    return OpenLmisResponse.response("template", service.getRegimenTemplateByProgramId(programId));
  }
}
