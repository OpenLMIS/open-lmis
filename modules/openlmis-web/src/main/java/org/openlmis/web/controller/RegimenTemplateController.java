/*
 * Copyright © 2013 VillageReach.  All Rights Reserved.  This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 *
 *  If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package org.openlmis.web.controller;

import org.openlmis.core.service.RegimenColumnService;
import org.openlmis.web.response.OpenLmisResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import static org.springframework.web.bind.annotation.RequestMethod.GET;

@Controller
public class RegimenTemplateController extends BaseController {

  @Autowired
  RegimenColumnService service;

  @RequestMapping(value = "/programId/{programId}/regimenTemplate", method = GET, headers = ACCEPT_JSON)
  @PreAuthorize("@permissionEvaluator.hasPermission(principal,'MANAGE_REGIMEN_TEMPLATE, VIEW_REQUISITION')")
  public ResponseEntity<OpenLmisResponse> getRegimenTemplate(@PathVariable("programId") Long programId) {
    return OpenLmisResponse.response("template", service.getRegimenTemplate(programId));
  }
}
