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
package org.openlmis.web.controller.equipment;

import org.openlmis.equipment.domain.EquipmentType;
import org.openlmis.equipment.repository.EquipmentOperationalStatusRepository;
import org.openlmis.equipment.service.EquipmentTypeService;
import org.openlmis.core.web.controller.BaseController;
import org.openlmis.core.web.OpenLmisResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;

import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

@Controller
@RequestMapping(value="/equipment/type/")
public class EquipmentTypeController extends BaseController {

  public static final String EQUIPMENT_TYPE = "equipment_type";
  public static final String STATUS = "status";
  public static final String EQUIPMENT_TYPES = "equipment_types";
  @Autowired
  private EquipmentTypeService service;

  @Autowired
  private EquipmentOperationalStatusRepository statusRepository;

  @RequestMapping(method = GET, value = "list")
  public ResponseEntity<OpenLmisResponse> getAll(){
    return  OpenLmisResponse.response(EQUIPMENT_TYPES, service.getAll());
  }

  @RequestMapping(method = GET, value = "id")
  @PreAuthorize("@permissionEvaluator.hasPermission(principal,'MANAGE_EQUIPMENT_SETTINGS')" +
      " or @permissionEvaluator.hasPermission(principal,'MANAGE_EQUIPMENT_INVENTORY')")
  public ResponseEntity<OpenLmisResponse> getById( @RequestParam("id") Long id){
    return  OpenLmisResponse.response(EQUIPMENT_TYPE, service.getTypeById(id));
  }

  @RequestMapping(method = GET, value = "operational-status")
  public ResponseEntity<OpenLmisResponse> getAllStatuses( ){
    return  OpenLmisResponse.response(STATUS, statusRepository.getAll());
  }

  @RequestMapping(value = "save", method = POST, headers = ACCEPT_JSON)
  @PreAuthorize("@permissionEvaluator.hasPermission(principal,'MANAGE_EQUIPMENT_SETTINGS')")
  public ResponseEntity<OpenLmisResponse> save(@RequestBody EquipmentType type, HttpServletRequest request){
    try {
      Long userId = loggedInUserId(request);
      type.setCreatedBy(userId);
      type.setModifiedBy(userId);
      service.save(type);
    }catch(DuplicateKeyException exp){
      return OpenLmisResponse.error("Duplicate Code Exists in DB.", HttpStatus.BAD_REQUEST);
    }
    return OpenLmisResponse.response(STATUS,"success");
  }

}
