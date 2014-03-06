/*
 * This program is part of the OpenLMIS logistics management information system platform software.
 *
 * Copyright © 2013 VillageReach
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *
 *
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License along with this program.  If not, see http://www.gnu.org/licenses.  For additional information contact info@OpenLMIS.org.
 */

package org.openlmis.restapi.controller;

import org.openlmis.core.dto.FacilityFeedDTO;
import org.openlmis.core.exception.DataException;
import org.openlmis.restapi.response.RestResponse;
import org.openlmis.restapi.service.RestFacilityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.web.bind.annotation.RequestMethod.GET;

/**
 * This controller is responsible for handling API endpoint to get facility details.
 */

@Controller
public class RestFacilityController extends BaseController {

  public static final String FACILITY = "facility";
  @Autowired
  RestFacilityService restFacilityService;

  @RequestMapping(value = "/rest-api/facilities/{facilityCode}", method = GET, headers = ACCEPT_JSON)
  public ResponseEntity<RestResponse> getFacilityByCode(@PathVariable String facilityCode) {
    FacilityFeedDTO facilityFeedDTO;
    try {
      facilityFeedDTO = restFacilityService.getFacilityByCode(facilityCode);
    } catch (DataException e) {
      return RestResponse.error(e.getOpenLmisMessage(), BAD_REQUEST);
    }
    return RestResponse.response(FACILITY, facilityFeedDTO);
  }
}
