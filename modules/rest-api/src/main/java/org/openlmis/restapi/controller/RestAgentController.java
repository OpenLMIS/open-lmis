/*
 * This program is part of the OpenLMIS logistics management information system platform software.
 * Copyright © 2013 VillageReach
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *  
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 * You should have received a copy of the GNU Affero General Public License along with this program.  If not, see http://www.gnu.org/licenses.  For additional information contact info@OpenLMIS.org. 
 */


package org.openlmis.restapi.controller;

import lombok.NoArgsConstructor;
import org.openlmis.core.exception.DataException;
import org.openlmis.restapi.domain.Agent;
import org.openlmis.restapi.response.RestResponse;
import org.openlmis.restapi.service.RestAgentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import java.security.Principal;

import static org.openlmis.restapi.response.RestResponse.error;
import static org.openlmis.restapi.response.RestResponse.success;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.web.bind.annotation.RequestMethod.POST;
import static org.springframework.web.bind.annotation.RequestMethod.PUT;

/**
 * This controller is responsible for handling API endpoints to create/update a CHW/Virtual facility/Agent.
 */

@Controller
@NoArgsConstructor
public class RestAgentController extends BaseController {

  @Autowired
  private RestAgentService restAgentService;

  @RequestMapping(value = "/rest-api/agents", method = POST, headers = ACCEPT_JSON)
  public ResponseEntity<RestResponse> createCHW(@RequestBody Agent agent, Principal principal) {
    try {
      restAgentService.create(agent, loggedInUserId(principal));
      return success("message.success.agent.created");

    } catch (DataException e) {
      return error(e.getOpenLmisMessage(), BAD_REQUEST);
    }
  }

  @RequestMapping(value = "/rest-api/agents/{agentCode}", method = PUT, headers = ACCEPT_JSON)
  public ResponseEntity<RestResponse> updateCHW(@RequestBody Agent agent,
                                                @PathVariable String agentCode,
                                                Principal principal) {
    try {
      agent.setAgentCode(agentCode);
      restAgentService.update(agent, loggedInUserId(principal));
      return success("message.success.agent.updated");

    } catch (DataException e) {
      return error(e.getOpenLmisMessage(), BAD_REQUEST);
    }
  }
}
