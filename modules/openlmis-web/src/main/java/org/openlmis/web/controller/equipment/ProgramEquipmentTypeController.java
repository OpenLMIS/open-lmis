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

import org.openlmis.core.exception.DataException;
import org.openlmis.equipment.domain.ProgramEquipmentType;
import org.openlmis.equipment.service.ProgramEquipmentTypeProductService;
import org.openlmis.equipment.service.ProgramEquipmentTypeService;
import org.openlmis.core.web.controller.BaseController;
import org.openlmis.core.web.OpenLmisResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;

@Controller
@RequestMapping(value = "/equipment/program-equipment/")
public class ProgramEquipmentTypeController extends BaseController {

  public static final String PROGRAM_EQUIPMENT = "programEquipment";
  public static final String PROGRAM_EQUIPMENTS = "programEquipments";

  @Autowired
  ProgramEquipmentTypeService programEquipmentTypeService;

  @Autowired
  ProgramEquipmentTypeProductService programEquipmentTypeProductService;

  @RequestMapping(value = "save", method = RequestMethod.POST, headers = ACCEPT_JSON)
  @PreAuthorize("@permissionEvaluator.hasPermission(principal,'MANAGE_EQUIPMENT_SETTINGS')")
  public ResponseEntity<OpenLmisResponse> save(@RequestBody ProgramEquipmentType programEquipmentType, HttpServletRequest request){
    ResponseEntity<OpenLmisResponse> successResponse;

    Long userId = loggedInUserId(request);
    Date date = new Date();

    if(programEquipmentType.getId() == null){
      programEquipmentType.setCreatedBy(userId);
      programEquipmentType.setCreatedDate(date);
      programEquipmentType.setEnableTestCount(false);
      programEquipmentType.setEnableTotalColumn(false);
      programEquipmentType.setDisplayOrder(0);
    }

    programEquipmentType.setModifiedBy(userId);
    programEquipmentType.setModifiedDate(date);

    try {
      programEquipmentTypeService.Save(programEquipmentType);
    }
    catch (DataException e){
      return OpenLmisResponse.error(e, HttpStatus.BAD_REQUEST);
    }

    successResponse = OpenLmisResponse.success("Program Equipment association successfully saved.");
    successResponse.getBody().addData(PROGRAM_EQUIPMENT, programEquipmentType);
    return successResponse;
  }

  @RequestMapping(value = "getByProgram/{programId}",method = RequestMethod.GET,headers = ACCEPT_JSON)
  @PreAuthorize("@permissionEvaluator.hasPermission(principal,'MANAGE_EQUIPMENT_SETTINGS')")
  public ResponseEntity<OpenLmisResponse> getProgramEquipmentByProgram(@PathVariable(value="programId") Long programId){
    return OpenLmisResponse.response(PROGRAM_EQUIPMENTS, programEquipmentTypeService.getByProgramId(programId));
  }

    @RequestMapping(value="remove/{programEquipmentId}")
    @PreAuthorize("@permissionEvaluator.hasPermission(principal,'MANAGE_EQUIPMENT_SETTINGS')")
    public ResponseEntity<OpenLmisResponse> remove(@PathVariable(value = "programEquipmentId") Long programEquipmentId){
        ResponseEntity<OpenLmisResponse> successResponse;

        try{
            //remove the program_equipment_products first
            programEquipmentTypeProductService.removeAllByEquipmentProducts(programEquipmentId);

            //then  go for the program_equipment
            programEquipmentTypeService.remove(programEquipmentId);
        }
        catch(DataException e){
            return OpenLmisResponse.error(e,HttpStatus.BAD_REQUEST);
        }

        successResponse = OpenLmisResponse.success("Program equipment successfully removed.");
        return successResponse;
    }
}
