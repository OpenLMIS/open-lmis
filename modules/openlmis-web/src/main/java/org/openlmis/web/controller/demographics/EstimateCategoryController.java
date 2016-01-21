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
package org.openlmis.web.controller.demographics;

import org.openlmis.demographics.domain.EstimateCategory;
import org.openlmis.demographics.service.EstimateCategoryService;
import org.openlmis.core.web.controller.BaseController;
import org.openlmis.core.web.OpenLmisResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import static java.util.Arrays.asList;

@Controller
@RequestMapping(value = "/demographic/estimate/")
public class EstimateCategoryController extends BaseController {

  public static final String ESTIMATE_CATEGORIES = "estimate_categories";

  public static final String ESTIMATE_CATEGORY = "estimate_category";

  @Autowired
  EstimateCategoryService service;

  @RequestMapping(value = "categories", method = RequestMethod.GET, headers = ACCEPT_JSON)
  public ResponseEntity<OpenLmisResponse> getAll() {
    return OpenLmisResponse.response(ESTIMATE_CATEGORIES, service.getAll());
  }

  @RequestMapping(value = "category/{id}", method = RequestMethod.GET, headers = ACCEPT_JSON)
  public ResponseEntity<OpenLmisResponse> getById(@PathVariable Long id) {
    return OpenLmisResponse.response(ESTIMATE_CATEGORY, service.getById(id));
  }

  @RequestMapping(value = "category/save", method = RequestMethod.PUT, headers = ACCEPT_JSON)
  public ResponseEntity<OpenLmisResponse> save(@RequestBody EstimateCategory category) {
    service.save(asList(category));
    return OpenLmisResponse.response(ESTIMATE_CATEGORY, category);
  }

}
