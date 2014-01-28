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

import lombok.NoArgsConstructor;
import org.openlmis.core.service.StaticReferenceDataService;
import org.openlmis.web.response.OpenLmisResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import static org.springframework.http.HttpStatus.OK;


@Controller
@NoArgsConstructor
public class StaticReferenceDataController extends BaseController {

  public static final String PAGE_SIZE = "pageSize";
  public static final String LINE_ITEMS_PER_PAGE = "line.items.per.page";

  @Autowired
  StaticReferenceDataService service;

  @RequestMapping(value = "/reference-data/pageSize", method = RequestMethod.GET)
  public ResponseEntity<OpenLmisResponse> getPageSize() {
    OpenLmisResponse response = new OpenLmisResponse(PAGE_SIZE, service.getPropertyValue(LINE_ITEMS_PER_PAGE));
    return new ResponseEntity(response, OK);
  }

}
