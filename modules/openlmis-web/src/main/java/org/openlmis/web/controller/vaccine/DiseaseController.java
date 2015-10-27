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
package org.openlmis.web.controller.vaccine;

import org.openlmis.core.exception.DataException;
import org.openlmis.vaccine.domain.VaccineDisease;
import org.openlmis.vaccine.service.DiseaseService;
import org.openlmis.core.web.controller.BaseController;
import org.openlmis.core.web.OpenLmisResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import static org.springframework.http.HttpStatus.BAD_REQUEST;

@Controller
@RequestMapping(value = "/vaccine/disease/")
public class DiseaseController extends BaseController {

  public static final String DISEASE = "disease";
  @Autowired
  private DiseaseService service;


  @RequestMapping(value="get/{id}")
  public ResponseEntity<OpenLmisResponse> get(@PathVariable Long id) {
    return OpenLmisResponse.response(DISEASE, service.getById(id));
  }

  @RequestMapping(value="all")
  public ResponseEntity<OpenLmisResponse> getAll() {
    return OpenLmisResponse.response("diseases", service.getAll());
  }

  @RequestMapping(value="save", headers = ACCEPT_JSON, method = RequestMethod.POST)
  @Transactional
  @PreAuthorize("@permissionEvaluator.hasPermission(principal,'MANAGE_VACCINE_DISEASE_LIST')")
  public ResponseEntity<OpenLmisResponse> save(@RequestBody VaccineDisease disease) {
    try {
      service.save(disease);
    } catch (DataException e) {
      return OpenLmisResponse.error(e, BAD_REQUEST);
    }
    return OpenLmisResponse.response(DISEASE, service.getById(disease.getId()));
  }


}
