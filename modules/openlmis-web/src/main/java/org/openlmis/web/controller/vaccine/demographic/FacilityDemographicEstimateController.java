/*
 * Electronic Logistics Management Information System (eLMIS) is a supply chain management system for health commodities in a developing country setting.
 *
 * Copyright (C) 2015  John Snow, Inc (JSI). This program was produced for the U.S. Agency for International Development (USAID). It was prepared under the USAID | DELIVER PROJECT, Task Order 4.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.openlmis.web.controller.vaccine.demographic;

import org.openlmis.core.web.OpenLmisResponse;
import org.openlmis.core.web.controller.BaseController;
import org.openlmis.vaccine.dto.DemographicEstimateForm;
import org.openlmis.vaccine.service.demographics.FacilityDemographicEstimateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;

import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.PUT;

@Controller
@RequestMapping(value = "/vaccine/demographic/estimate/")
public class FacilityDemographicEstimateController extends BaseController {

  @Autowired
  FacilityDemographicEstimateService service;

  @RequestMapping(value = "facilities", method = GET)
  @PreAuthorize("@permissionEvaluator.hasPermission(principal,'MANAGE_DEMOGRAPHIC_ESTIMATES')")
  public ResponseEntity<OpenLmisResponse> get( @RequestParam("year") Integer year, @RequestParam("programId") Long programId,  HttpServletRequest request){
    return OpenLmisResponse.response("estimates", service.getEstimateFor(loggedInUserId(request), programId, year));
  }

  @RequestMapping(value = "facilities", method = PUT)
  @PreAuthorize("@permissionEvaluator.hasPermission(principal,'MANAGE_DEMOGRAPHIC_ESTIMATES')")
  public ResponseEntity<OpenLmisResponse> save(@RequestBody DemographicEstimateForm form,  HttpServletRequest request){
    service.save(form);
    return OpenLmisResponse.response("estimates", form);
  }

}
