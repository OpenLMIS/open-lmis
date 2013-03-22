/*
 * Copyright © 2013 VillageReach.  All Rights Reserved.  This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 *
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package org.openlmis.web.controller;

import lombok.NoArgsConstructor;
import org.openlmis.core.service.SupervisoryNodeService;
import org.openlmis.web.response.OpenLmisResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
@NoArgsConstructor
public class SupervisoryNodeController {

  public static final String SUPERVISORY_NODES = "supervisoryNodes";
  private SupervisoryNodeService supervisoryNodeService;

  @Autowired
  public SupervisoryNodeController(SupervisoryNodeService supervisoryNodeService) {
    this.supervisoryNodeService = supervisoryNodeService;
  }

  @RequestMapping(value = "/supervisory-nodes", method = RequestMethod.GET)
  @PreAuthorize("@permissionEvaluator.hasPermission(principal,'MANAGE_USERS')")
  public ResponseEntity<OpenLmisResponse> getAll() {
    return OpenLmisResponse.response(SUPERVISORY_NODES, supervisoryNodeService.getAll());
  }
}
