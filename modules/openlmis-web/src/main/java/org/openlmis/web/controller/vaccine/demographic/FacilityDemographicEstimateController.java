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

import org.openlmis.vaccine.dto.DemographicEstimateForm;
import org.openlmis.vaccine.service.demographics.FacilityDemographicEstimateService;
import org.openlmis.web.controller.BaseController;
import org.openlmis.web.response.OpenLmisResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;

@Controller
@RequestMapping(value = "/vaccine/demographic/estimate/facility/")
public class FacilityDemographicEstimateController extends BaseController {

  @Autowired
  FacilityDemographicEstimateService service;

  @RequestMapping("get")
  public ResponseEntity<OpenLmisResponse> get( @RequestParam("year") Integer year, @RequestParam("programId") Long programId,  HttpServletRequest request){
    return OpenLmisResponse.response("estimates", service.getEstimateFor(loggedInUserId(request), programId, year));
  }

  @RequestMapping("save")
  public ResponseEntity<OpenLmisResponse> save(@RequestBody DemographicEstimateForm form,  HttpServletRequest request){
    service.save(form);
    return OpenLmisResponse.response("estimates", form);
  }

}
