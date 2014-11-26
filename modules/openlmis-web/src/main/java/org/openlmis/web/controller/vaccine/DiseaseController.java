/*
 * This program was produced for the U.S. Agency for International Development. It was prepared by the USAID | DELIVER PROJECT, Task Order 4. It is part of a project which utilizes code originally licensed under the terms of the Mozilla Public License (MPL) v2 and therefore is licensed under MPL v2 or later.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the Mozilla Public License as published by the Mozilla Foundation, either version 2 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the Mozilla Public License for more details.
 *
 * You should have received a copy of the Mozilla Public License along with this program. If not, see http://www.mozilla.org/MPL/
 */

package org.openlmis.web.controller.vaccine;

import org.openlmis.vaccine.domain.VaccineDisease;
import org.openlmis.vaccine.service.DiseaseService;
import org.openlmis.web.controller.BaseController;
import org.openlmis.web.response.OpenLmisResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping(value = "/vaccine/disease/")
public class DiseaseController extends BaseController {

  @Autowired
  private DiseaseService service;


  @RequestMapping(value="get/{id}")
  public ResponseEntity<OpenLmisResponse> get(@PathVariable Long id) {
    return OpenLmisResponse.response("disease", service.getById(id));
  }

  @RequestMapping(value="all")
  public ResponseEntity<OpenLmisResponse> getAll() {
    return OpenLmisResponse.response("diseases", service.getAll());
  }

  @RequestMapping(value="save")
  public ResponseEntity<OpenLmisResponse> save(@RequestBody VaccineDisease disease) {
    if(disease.getId() == null){
      service.insert(disease);
    }
    else{
      service.update(disease);
    }
    return OpenLmisResponse.response("status", "success");
  }


}
