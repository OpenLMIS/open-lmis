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

import org.openlmis.equipment.domain.ColdChainEquipment;
import org.openlmis.equipment.domain.Equipment;
import org.openlmis.equipment.domain.EquipmentType;
import org.openlmis.equipment.service.EquipmentService;
import org.openlmis.equipment.service.EquipmentTypeService;
import org.openlmis.web.controller.BaseController;
import org.openlmis.web.response.OpenLmisResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping(value="/equipment/manage/")
public class EquipmentController extends BaseController {

  @Autowired
  private EquipmentService service;

    @Autowired
    EquipmentTypeService equipmentTypeService;

  @RequestMapping(method = RequestMethod.GET, value = "id")
  @PreAuthorize("@permissionEvaluator.hasPermission(principal,'MANAGE_EQUIPMENT_SETTINGS')")
  public ResponseEntity<OpenLmisResponse> getEquipmentById(@RequestParam("id") Long Id){

    return OpenLmisResponse.response("equipment", service.getById(Id));
  }

    @RequestMapping(method = RequestMethod.GET, value = "type-and-id")
    @PreAuthorize("@permissionEvaluator.hasPermission(principal,'MANAGE_EQUIPMENT_SETTINGS')")
    public ResponseEntity<OpenLmisResponse> getEquipmentByTypeAndId(@RequestParam("id") Long Id, @RequestParam("equipmentTypeId") Long equipmentTypeId){

        return OpenLmisResponse.response("equipment", service.getByTypeAndId(Id,equipmentTypeId));

    }

  @RequestMapping(method = RequestMethod.GET, value = "list")
  @PreAuthorize("@permissionEvaluator.hasPermission(principal,'MANAGE_EQUIPMENT_SETTINGS') or @permissionEvaluator.hasPermission(principal,'SERVICE_VENDOR_RIGHT')")
  public ResponseEntity<OpenLmisResponse> getList(@RequestParam("equipmentTypeId") Long equipmentTypeId){

      EquipmentType equipmentType=equipmentTypeService.getTypeById(equipmentTypeId);
      if(equipmentType.isColdChain())
      {
          return OpenLmisResponse.response("equipments", service.getAllCCE());
      }
        else{
          return OpenLmisResponse.response("equipments", service.getAllByType(equipmentTypeId));
      }
  }

  @RequestMapping(method = RequestMethod.GET, value = "list-by-type")
  @PreAuthorize("@permissionEvaluator.hasPermission(principal,'MANAGE_EQUIPMENT_SETTINGS') or @permissionEvaluator.hasPermission(principal,'SERVICE_VENDOR_RIGHT')")
  public ResponseEntity<OpenLmisResponse> getListByType(@RequestParam("equipmentTypeId") Long equipmentTypeId){
    return OpenLmisResponse.response("equipments", service.getAllByType(equipmentTypeId));
  }

  @RequestMapping(method = RequestMethod.GET, value = "typesByProgram/{programId}", headers = ACCEPT_JSON)
  @PreAuthorize("@permissionEvaluator.hasPermission(principal,'MANAGE_EQUIPMENT_SETTINGS')")
  public ResponseEntity<OpenLmisResponse> getTypesByProgram(@PathVariable(value="programId") Long programId){
    return OpenLmisResponse.response("equipment_types", service.getTypesByProgram(programId));
  }

  @RequestMapping(method = RequestMethod.POST, value = "save", headers = ACCEPT_JSON)
  @PreAuthorize("@permissionEvaluator.hasPermission(principal,'MANAGE_EQUIPMENT_SETTINGS')")
  @Transactional
  public ResponseEntity<OpenLmisResponse> save( @RequestBody Equipment equipment){
      ResponseEntity<OpenLmisResponse> response;
      EquipmentType equipmentType=equipmentTypeService.getTypeById(equipment.getEquipmentTypeId());
      equipment.setEquipmentType(equipmentType);
      ColdChainEquipment coldChainEquipment;
        try{
            if(equipment.getId()==null) {

                if(equipmentType.isColdChain()) {
                    service.saveEquipment(equipment);
                    coldChainEquipment = (ColdChainEquipment) equipment;
                    service.saveColdChainEquipment(coldChainEquipment);
                }
                else {
                    service.saveEquipment(equipment);
                }
            }
            else{
                if(equipmentType.isColdChain()) {
                    service.updateEquipment(equipment);
                    coldChainEquipment = (ColdChainEquipment) equipment;
                    service.updateColdChainEquipment(coldChainEquipment);
                }
                else {
                    service.updateEquipment(equipment);
                }
            }
       }catch(DuplicateKeyException exp){
          return OpenLmisResponse.error("Duplicate Code Exists in DB.", HttpStatus.BAD_REQUEST);
        }
      response = OpenLmisResponse.success(messageService.message("message.equipment.inventory.save"));
      response.getBody().addData("equipment", equipment);
      return response;
      }
}
