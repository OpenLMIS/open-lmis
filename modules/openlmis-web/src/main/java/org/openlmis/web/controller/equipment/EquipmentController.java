/*
 * This program was produced for the U.S. Agency for International Development. It was prepared by the USAID | DELIVER PROJECT, Task Order 4. It is part of a project which utilizes code originally licensed under the terms of the Mozilla Public License (MPL) v2 and therefore is licensed under MPL v2 or later.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the Mozilla Public License as published by the Mozilla Foundation, either version 2 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the Mozilla Public License for more details.
 *
 * You should have received a copy of the Mozilla Public License along with this program. If not, see http://www.mozilla.org/MPL/
 */

package org.openlmis.web.controller.equipment;

import org.openlmis.equipment.domain.Equipment;
import org.openlmis.equipment.domain.EquipmentType;
import org.openlmis.equipment.service.EquipmentService;
import org.openlmis.web.controller.BaseController;
import org.openlmis.web.response.OpenLmisResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping(value="/equipment/manage/")
public class EquipmentController extends BaseController {

  @Autowired
  private EquipmentService service;

  @RequestMapping(method = RequestMethod.GET, value = "id")
  public ResponseEntity<OpenLmisResponse> getEquipmentById(@RequestParam("id") Long Id){
    return OpenLmisResponse.response("equipment", service.getById(Id));
  }

  @RequestMapping(method = RequestMethod.GET, value = "list")
  public ResponseEntity<OpenLmisResponse> getList(){
    return OpenLmisResponse.response("equipments", service.getAll());
  }

  @RequestMapping(method = RequestMethod.POST, value = "save", headers = ACCEPT_JSON)
  public ResponseEntity<OpenLmisResponse> save( @RequestBody Equipment equipment){
    equipment.setEquipmentType(new EquipmentType());
    equipment.getEquipmentType().setId(equipment.getEquipmentTypeId());
    service.save(equipment);
    return OpenLmisResponse.response("equipment", equipment);
  }
}
