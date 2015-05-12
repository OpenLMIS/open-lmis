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

import org.openlmis.core.domain.Facility;
import org.openlmis.core.service.FacilityService;
import org.openlmis.core.service.ProgramService;
import org.openlmis.equipment.domain.EquipmentInventory;
import org.openlmis.equipment.service.EquipmentInventoryService;
import org.openlmis.web.controller.BaseController;
import org.openlmis.web.response.OpenLmisResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

import static org.openlmis.core.domain.RightName.AUTHORIZE_REQUISITION;
import static org.openlmis.core.domain.RightName.CREATE_REQUISITION;
import static org.openlmis.core.domain.RightName.MANAGE_EQUIPMENT_INVENTORY;

@Controller
@RequestMapping(value="/equipment/inventory/")
public class EquipmentInventoryController extends BaseController {

  @Autowired
  private EquipmentInventoryService service;

  @Autowired
  private FacilityService facilityService;

  @Autowired
  private ProgramService programService;

  @RequestMapping(value="list", method = RequestMethod.GET)
  public ResponseEntity<OpenLmisResponse> getInventory(@RequestParam("typeId") Long typeId,
                                                       @RequestParam("programId") Long programId,
                                                       @RequestParam("equipmentTypeId") Long equipmentTypeId,
                                                       HttpServletRequest request){
    Long userId = loggedInUserId(request);
    return OpenLmisResponse.response("inventory",service.getInventory(userId, typeId, programId, equipmentTypeId));
  }

  @RequestMapping(value="programs", method = RequestMethod.GET)
  public ResponseEntity<OpenLmisResponse> getPrograms(HttpServletRequest request){
    Long userId = loggedInUserId(request);
    return OpenLmisResponse.response("programs",programService.getProgramForSupervisedFacilities(userId, MANAGE_EQUIPMENT_INVENTORY));
  }

  @RequestMapping(value="facility/programs", method = RequestMethod.GET)
  public ResponseEntity<OpenLmisResponse> getProgramsForFacility(@RequestParam("facilityId") Long facilityId, HttpServletRequest request){
    Long userId = loggedInUserId(request);
    return OpenLmisResponse.response("programs",programService.getProgramsForUserByFacilityAndRights(facilityId, userId, MANAGE_EQUIPMENT_INVENTORY));
  }

  @RequestMapping(value="supervised/facilities", method = RequestMethod.GET)
  @PreAuthorize("@permissionEvaluator.hasPermission(principal,'MANAGE_EQUIPMENT_INVENTORY')")
  public ResponseEntity<ModelMap> getFacilities(@RequestParam("programId") Long programId,  HttpServletRequest request){
    ModelMap modelMap = new ModelMap();
    Long userId = loggedInUserId(request);
    List<Facility> facilities = facilityService.getUserSupervisedFacilities(userId, programId, MANAGE_EQUIPMENT_INVENTORY);
    modelMap.put("facilities", facilities);
    return new ResponseEntity<>(modelMap, HttpStatus.OK);
  }

  @RequestMapping(value="by-id", method = RequestMethod.GET)
  @PreAuthorize("@permissionEvaluator.hasPermission(principal,'MANAGE_EQUIPMENT_INVENTORY')")
  public ResponseEntity<OpenLmisResponse> getInventory(@RequestParam("id") Long id){
    return OpenLmisResponse.response("inventory", service.getInventoryById(id));
  }

  @RequestMapping(value="save", method = RequestMethod.POST)
  @PreAuthorize("@permissionEvaluator.hasPermission(principal,'MANAGE_EQUIPMENT_INVENTORY')")
  public ResponseEntity<OpenLmisResponse> save(@RequestBody EquipmentInventory inventory){
    ResponseEntity<OpenLmisResponse> response;
    service.save(inventory);
    response = OpenLmisResponse.success(messageService.message("message.equipment.inventory.saved"));
    response.getBody().addData("inventory", inventory);
    return response;
  }

  @RequestMapping(value="status/update", method = RequestMethod.POST)
  @PreAuthorize("@permissionEvaluator.hasPermission(principal,'MANAGE_EQUIPMENT_INVENTORY')")
  public ResponseEntity<OpenLmisResponse> updateStatus(@RequestBody EquipmentInventory inventory){
    ResponseEntity<OpenLmisResponse> response;
    service.updateStatus(inventory);
    response = OpenLmisResponse.success(messageService.message("message.equipment.inventory.saved"));
    response.getBody().addData("inventory", inventory);
    return response;
  }

}
