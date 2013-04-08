/*
 * Copyright © 2013 VillageReach. All Rights Reserved. This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 *
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package org.openlmis.web.controller.commtrack;

import lombok.NoArgsConstructor;
import org.openlmis.commtrack.domain.CommtrackRequisition;
import org.openlmis.commtrack.service.CommtrackService;
import org.openlmis.web.controller.BaseController;
import org.openlmis.web.response.OpenLmisResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import static org.springframework.web.bind.annotation.RequestMethod.POST;

@Controller
@NoArgsConstructor
public class CommtrackController extends BaseController {

  @Autowired
  private CommtrackService commtrackService;

  @RequestMapping(value = "/commtrack/requisitions", method = POST, headers = ACCEPT_JSON)
  public ResponseEntity submitRequisition(CommtrackRequisition requisition) {
    return OpenLmisResponse.response("R&R", commtrackService.submitRequisition(requisition));
  }
}
