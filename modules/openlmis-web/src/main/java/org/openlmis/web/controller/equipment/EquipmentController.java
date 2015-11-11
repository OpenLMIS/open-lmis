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

import org.openlmis.core.domain.Pagination;
import org.openlmis.core.exception.DataException;
import org.openlmis.equipment.domain.ColdChainEquipment;
import org.openlmis.equipment.domain.Equipment;
import org.openlmis.equipment.domain.EquipmentType;
import org.openlmis.equipment.service.EquipmentService;
import org.openlmis.equipment.service.EquipmentTypeService;
import org.openlmis.core.web.controller.BaseController;
import org.openlmis.core.web.OpenLmisResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

import static java.lang.Integer.parseInt;

@Controller
@RequestMapping(value="/equipment/manage/")
public class EquipmentController extends BaseController {

  public static final String EQUIPMENT = "equipment";
  public static final String EQUIPMENTS = "equipments";
  @Autowired
  private EquipmentService service;

    @Autowired
    EquipmentTypeService equipmentTypeService;

  @RequestMapping(method = RequestMethod.GET, value = "id")
  @PreAuthorize("@permissionEvaluator.hasPermission(principal,'MANAGE_EQUIPMENT_SETTINGS')")
  public ResponseEntity<OpenLmisResponse> getEquipmentById(@RequestParam("id") Long id){

    return OpenLmisResponse.response(EQUIPMENT, service.getById(id));
  }

    @RequestMapping(method = RequestMethod.GET, value = "type-and-id")
    @PreAuthorize("@permissionEvaluator.hasPermission(principal,'MANAGE_EQUIPMENT_SETTINGS')")
    public ResponseEntity<OpenLmisResponse> getEquipmentByTypeAndId(@RequestParam("id") Long id, @RequestParam("equipmentTypeId") Long equipmentTypeId){

        return OpenLmisResponse.response(EQUIPMENT, service.getByTypeAndId(id,equipmentTypeId));

    }

  @RequestMapping(method = RequestMethod.GET, value = "list")
  @PreAuthorize("@permissionEvaluator.hasPermission(principal,'MANAGE_EQUIPMENT_SETTINGS') or @permissionEvaluator.hasPermission(principal,'SERVICE_VENDOR_RIGHT')")
  public ResponseEntity<OpenLmisResponse> getList(@RequestParam("equipmentTypeId") Long equipmentTypeId,
                                                  @RequestParam(value = "page", defaultValue = "1") Integer page,
                                                  @Value("${search.page.size}") String limit
                                                  ){

      Pagination pagination = new Pagination(page, parseInt(limit));
      EquipmentType equipmentType=equipmentTypeService.getTypeById(equipmentTypeId);
      if(equipmentType.isColdChain())
      {
          List<ColdChainEquipment> equipments=service.getAllCCE(equipmentTypeId,pagination);
          pagination.setTotalRecords(service.getCCECountByType(equipmentTypeId));
          ResponseEntity<OpenLmisResponse> response = OpenLmisResponse.response(EQUIPMENTS,equipments);
          response.getBody().addData("pagination", pagination);
          return response;
      }
        else{
          List<Equipment> equipments=service.getByType(equipmentTypeId, pagination);
          pagination.setTotalRecords(service.getEquipmentsCountByType(equipmentTypeId));
          ResponseEntity<OpenLmisResponse> response = OpenLmisResponse.response(EQUIPMENTS,equipments);
          response.getBody().addData("pagination", pagination);
          return response;
      }
  }

  @RequestMapping(method = RequestMethod.GET, value = "list-by-type")
  @PreAuthorize("@permissionEvaluator.hasPermission(principal,'MANAGE_EQUIPMENT_SETTINGS')" +
      " or @permissionEvaluator.hasPermission(principal,'MANAGE_EQUIPMENT_INVENTORY')" +
      " or @permissionEvaluator.hasPermission(principal,'SERVICE_VENDOR_RIGHT')")
  public ResponseEntity<OpenLmisResponse> getListByType(@RequestParam("equipmentTypeId") Long equipmentTypeId){
    return OpenLmisResponse.response(EQUIPMENTS, service.getAllByType(equipmentTypeId));
  }

  @RequestMapping(method = RequestMethod.GET, value = "typesByProgram/{programId}", headers = ACCEPT_JSON)
  @PreAuthorize("@permissionEvaluator.hasPermission(principal,'MANAGE_EQUIPMENT_SETTINGS')" +
      " or @permissionEvaluator.hasPermission(principal,'MANAGE_EQUIPMENT_INVENTORY')")
  public ResponseEntity<OpenLmisResponse> getTypesByProgram(@PathVariable(value="programId") Long programId){
    return OpenLmisResponse.response("equipment_types", service.getTypesByProgram(programId));
  }

  @RequestMapping(method = RequestMethod.POST, value = "save", headers = ACCEPT_JSON)
  @PreAuthorize("@permissionEvaluator.hasPermission(principal,'MANAGE_EQUIPMENT_SETTINGS')")
  @Transactional
  public ResponseEntity<OpenLmisResponse> save( @RequestBody Equipment equipment, HttpServletRequest request){
      ResponseEntity<OpenLmisResponse> response;
      Long userId = loggedInUserId(request);
      equipment.setCreatedBy(userId);
      equipment.setModifiedBy(userId);
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
      response = OpenLmisResponse.success(messageService.message("message.equipment.list.saved"));
      response.getBody().addData(EQUIPMENT, equipment);
      return response;
      }

    @RequestMapping(value="remove/{equipmentTypeId}/{id}")
    @PreAuthorize("@permissionEvaluator.hasPermission(principal,'MANAGE_EQUIPMENT_SETTINGS')")
    @Transactional
    public ResponseEntity<OpenLmisResponse> remove(@PathVariable(value = "equipmentTypeId") Long equipmentTypeId,@PathVariable(value = "id") Long id){
        ResponseEntity<OpenLmisResponse> successResponse;
        EquipmentType equipmentType=equipmentTypeService.getTypeById(equipmentTypeId);
        try{
            if(equipmentType.isColdChain()) {
                //remove Cold Chain first
                service.removeCCE(id);
                //then  remove equipment
                service.removeEquipment(id);
            }
            else {
                service.removeEquipment(id);
            }
        }
        catch(DataException e){
            return OpenLmisResponse.error(e,HttpStatus.BAD_REQUEST);
        }

        successResponse = OpenLmisResponse.success(messageService.message("message.equipment.list.removed"));
        return successResponse;
    }
}
