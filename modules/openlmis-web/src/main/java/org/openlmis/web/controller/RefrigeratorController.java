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

import org.openlmis.core.exception.DataException;
import org.openlmis.core.domain.Refrigerator;
import org.openlmis.core.service.RefrigeratorService;
import org.openlmis.web.response.OpenLmisResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

import static org.openlmis.web.response.OpenLmisResponse.error;
import static org.openlmis.web.response.OpenLmisResponse.response;
import static org.springframework.web.bind.annotation.RequestMethod.GET;

@Controller
public class RefrigeratorController extends BaseController {

  public static final String REFRIGERATORS = "refrigerators";

  @Autowired
  RefrigeratorService refrigeratorService;

  @RequestMapping(value = "/deliveryZone/{deliveryZoneId}/program/{programId}/refrigerators", method = GET, headers = ACCEPT_JSON)
  public ResponseEntity<OpenLmisResponse> getRefrigeratorsForADeliveryZoneAndProgram(@PathVariable(value = "deliveryZoneId") Long deliveryZoneId, @PathVariable(value = "programId") Long programId) {
    try {
      ResponseEntity<OpenLmisResponse> response;
      List<Refrigerator> refrigerators = refrigeratorService.getRefrigeratorsForADeliveryZoneAndProgram(deliveryZoneId, programId);
      response = response(REFRIGERATORS, refrigerators);
      return response;
    } catch (DataException dataException) {
      return error(UNEXPECTED_EXCEPTION, HttpStatus.BAD_REQUEST);
    }
  }


}
