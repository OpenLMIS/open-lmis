/*
 * This program was produced for the U.S. Agency for International Development. It was prepared by the USAID | DELIVER PROJECT, Task Order 4. It is part of a project which utilizes code originally licensed under the terms of the Mozilla Public License (MPL) v2 and therefore is licensed under MPL v2 or later.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the Mozilla Public License as published by the Mozilla Foundation, either version 2 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the Mozilla Public License for more details.
 *
 * You should have received a copy of the Mozilla Public License along with this program. If not, see http://www.mozilla.org/MPL/
 */

package org.openlmis.web.controller.equipment;

import org.openlmis.equipment.service.ColdChainStatusDataService;
import org.openlmis.web.controller.BaseController;
import org.openlmis.web.response.OpenLmisResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import static org.springframework.web.bind.annotation.RequestMethod.GET;

@Controller
@RequestMapping(value="/equipment/cold-chain/")
public class ColdChainStatusDataController extends BaseController {


  @Autowired
  ColdChainStatusDataService service;

  @RequestMapping(method = GET, value = "designations")
  public ResponseEntity<OpenLmisResponse> getDesignations( ){
    return OpenLmisResponse.response("designations",service.getAllDesignations());
  }

  @RequestMapping(method = GET, value = "pqsStatus")
  public ResponseEntity<OpenLmisResponse> getPqsStatus( ){
    return OpenLmisResponse.response("pqs_status",service.getAllPqsStatus());
  }

}
