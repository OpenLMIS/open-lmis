package org.openlmis.web.controller.equipment;
/*
 * This program was produced for the U.S. Agency for International Development. It was prepared by the USAID | DELIVER PROJECT, Task Order 4. It is part of a project which utilizes code originally licensed under the terms of the Mozilla Public License (MPL) v2 and therefore is licensed under MPL v2 or later.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the Mozilla Public License as published by the Mozilla Foundation, either version 2 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the Mozilla Public License for more details.
 *
 * You should have received a copy of the Mozilla Public License along with this program. If not, see http://www.mozilla.org/MPL/
 */

import org.openlmis.core.exception.DataException;
import org.openlmis.equipment.domain.ProgramEquipmentProduct;
import org.openlmis.equipment.service.ProgramEquipmentProductService;
import org.openlmis.web.controller.BaseController;
import org.openlmis.web.response.OpenLmisResponse;
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

@RequestMapping(value="/program-equipment-product/")
@Controller
public class ProgramEquipmentProductController extends BaseController{

  @Autowired
  ProgramEquipmentProductService programEquipmentProductService;

  @RequestMapping(value="getByProgramEquipment/{programEquipmentId}",headers = ACCEPT_JSON,method = RequestMethod.GET)
  @PreAuthorize("@permissionEvaluator.hasPermission(principal,'MANAGE_EQUIPMENT_SETTINGS')")
  public ResponseEntity<OpenLmisResponse> getByProgramEquipmentId(@PathVariable(value="programEquipmentId") Long programEquipmentId){
    return OpenLmisResponse.response("programEquipmentProducts",programEquipmentProductService.getByProgramEquipmentId(programEquipmentId));
  }

  @RequestMapping(value = "save", method = RequestMethod.POST, headers = ACCEPT_JSON)
  @PreAuthorize("@permissionEvaluator.hasPermission(principal,'MANAGE_EQUIPMENT_SETTINGS')")
  public ResponseEntity<OpenLmisResponse> save(@RequestBody ProgramEquipmentProduct programEquipmentProduct, HttpServletRequest request){
    Date date = new Date();
    Long userId = loggedInUserId(request);
    ResponseEntity<OpenLmisResponse> successResponse;

    if(programEquipmentProduct.getId() == null){
      programEquipmentProduct.setCreatedDate(date);
      programEquipmentProduct.setCreatedBy(userId);
    }
    programEquipmentProduct.setModifiedDate(date);
    programEquipmentProduct.setModifiedBy(userId);

    try{
      programEquipmentProductService.Save(programEquipmentProduct);
    }
    catch (DataException e){
      return OpenLmisResponse.error(e, HttpStatus.BAD_REQUEST);
    }

    successResponse = OpenLmisResponse.success("Association between program, equipment, and product successfully saved.");
    successResponse.getBody().addData("programEquipmentProduct",programEquipmentProduct);
    return successResponse;
  }

  @RequestMapping(value="remove/{programEquipmentId}")
  @PreAuthorize("@permissionEvaluator.hasPermission(principal,'MANAGE_EQUIPMENT_SETTINGS')")
  public ResponseEntity<OpenLmisResponse> remove(@PathVariable(value = "programEquipmentId") Long programEquipmentId){
    ResponseEntity<OpenLmisResponse> successResponse;

    try{
      programEquipmentProductService.remove(programEquipmentId);
    }
    catch(DataException e){
      return OpenLmisResponse.error(e,HttpStatus.BAD_REQUEST);
    }

    successResponse = OpenLmisResponse.success("Product successfully removed.");
    return successResponse;
  }

}