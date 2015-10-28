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
package org.openlmis.web.controller;

import lombok.NoArgsConstructor;
import org.openlmis.core.service.StaticReferenceDataService;
import org.openlmis.core.web.OpenLmisResponse;
import org.openlmis.core.web.controller.BaseController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import static org.springframework.http.HttpStatus.OK;

/**
 * This controller handles endpoint to return page size used in pagination.
 */

@Controller
@NoArgsConstructor
public class StaticReferenceDataController extends BaseController {

  public static final String PAGE_SIZE = "pageSize";
  public static final String LINE_ITEMS_PER_PAGE = "line.items.per.page";
  public static final String KEY_TOGGLE_PREFIX = "toggle.";
  public static final String KEY = "key";

  @Autowired
  private StaticReferenceDataService service;

  @RequestMapping(value = "/reference-data/pageSize", method = RequestMethod.GET)
  public ResponseEntity<OpenLmisResponse> getPageSize() {
    OpenLmisResponse response = new OpenLmisResponse(PAGE_SIZE, service.getPropertyValue(LINE_ITEMS_PER_PAGE));
    return new ResponseEntity(response, OK);
  }

  @RequestMapping(value = "/reference-data/toggle/{key}", method = RequestMethod.GET)
  public ResponseEntity<OpenLmisResponse> getToggle(@PathVariable(value = "key") String key) {
    OpenLmisResponse response = new OpenLmisResponse(KEY, service.getBoolean(KEY_TOGGLE_PREFIX + key));
    return new ResponseEntity(response, OK);
  }
}
