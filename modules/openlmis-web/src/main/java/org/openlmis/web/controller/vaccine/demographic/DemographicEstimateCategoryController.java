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

import org.openlmis.vaccine.domain.demographics.DemographicEstimateCategory;
import org.openlmis.vaccine.service.demographics.DemographicEstimateCategoryService;
import org.openlmis.web.controller.BaseController;
import org.openlmis.web.response.OpenLmisResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import static java.util.Arrays.asList;

@Controller
@RequestMapping(value = "/vaccine/demographic/estimate/")
public class DemographicEstimateCategoryController extends BaseController{

  @Autowired
  DemographicEstimateCategoryService service;

  @RequestMapping("categories")
  public ResponseEntity<OpenLmisResponse> getAll(){
    return OpenLmisResponse.response("estimate_categories", service.getAll());
  }

  @RequestMapping("category/{id}")
  public ResponseEntity<OpenLmisResponse> getById(@PathVariable Long id){
    return OpenLmisResponse.response("estimate_category", service.getById(id));
  }

  @RequestMapping("category/save")
  public ResponseEntity<OpenLmisResponse> save(@RequestBody DemographicEstimateCategory category){
    service.save(asList(category));
    return OpenLmisResponse.response("estimate_category", category);
  }

}
