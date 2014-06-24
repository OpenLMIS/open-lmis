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

import org.openlmis.core.exception.DataException;
import org.openlmis.equipment.domain.VendorUser;
import org.openlmis.equipment.service.VendorUserService;
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

import static org.openlmis.web.response.OpenLmisResponse.error;
import static org.openlmis.web.response.OpenLmisResponse.success;

@Controller
@RequestMapping(value = "/equipment/vendor-user/")
public class VendorUserController extends BaseController {

  @Autowired
  private VendorUserService vendorUserService;

  @RequestMapping(value = "getAllUsersForVendor/{vendorId}", method = RequestMethod.GET, headers = ACCEPT_JSON)
  @PreAuthorize("@permissionEvaluator.hasPermission(principal,'MANAGE_EQUIPMENT_SETTINGS')")
  public ResponseEntity<OpenLmisResponse> getAllUsersForVendor(@PathVariable(value = "vendorId") Long vendorId) {
    return OpenLmisResponse.response("users", vendorUserService.getAllUsersForVendor(vendorId));
  }

  @RequestMapping(value = "getAllUsersAvailableForVendor", method = RequestMethod.GET, headers = ACCEPT_JSON)
  @PreAuthorize("@permissionEvaluator.hasPermission(principal,'MANAGE_EQUIPMENT_SETTINGS')")
  public ResponseEntity<OpenLmisResponse> getAllUsersAvailableForVendor() {
    return OpenLmisResponse.response("users", vendorUserService.getAllUsersAvailableForVendor());
  }

  @RequestMapping(value="saveNewUserForVendor", method = RequestMethod.POST,headers = ACCEPT_JSON)
  @PreAuthorize("@permissionEvaluator.hasPermission(principal,'MANAGE_EQUIPMENT_SETTINGS')")
  public ResponseEntity<OpenLmisResponse> saveVendorUsers(@RequestBody VendorUser vendorUser, HttpServletRequest request) {
    ResponseEntity<OpenLmisResponse> successResponse;
    vendorUser.setModifiedBy(loggedInUserId(request));
    try {
      vendorUserService.save(vendorUser);
    } catch (DataException e) {
      return OpenLmisResponse.error(e, HttpStatus.BAD_REQUEST);
    }
    successResponse = OpenLmisResponse.success("User successfully associated with vendor.");
    successResponse.getBody().addData("vendorUser", vendorUser);
    return successResponse;
  }

  @RequestMapping(value="remove/{vendorId}/{userId}", method = RequestMethod.GET, headers = ACCEPT_JSON)
  @PreAuthorize("@permissionEvaluator.hasPermission(principal,'MANAGE_EQUIPMENT_SETTINGS')")
  public ResponseEntity<OpenLmisResponse> remove (@PathVariable(value="vendorId") Long vendorId, @PathVariable(value="userId") Long userId){
    ResponseEntity<OpenLmisResponse> successResponse;
    try {
      vendorUserService.removeVendorUserAssociation(vendorId,userId);
    } catch (DataException e) {
      return error(e, HttpStatus.BAD_REQUEST);
    }
    successResponse = success(String.format("Vendor and user association has been successfully removed"));
    return successResponse;
  }

}