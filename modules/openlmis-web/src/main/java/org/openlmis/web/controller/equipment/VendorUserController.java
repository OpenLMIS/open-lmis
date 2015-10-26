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
import org.openlmis.equipment.domain.VendorUser;
import org.openlmis.equipment.service.VendorUserService;
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

import static org.openlmis.core.web.OpenLmisResponse.error;
import static org.openlmis.core.web.OpenLmisResponse.success;

@Controller
@RequestMapping(value = "/equipment/vendor-user/")
public class VendorUserController extends BaseController {

  public static final String VENDOR_USER = "vendorUser";
  public static final String USERS = "users";

  @Autowired
  private VendorUserService vendorUserService;

  @RequestMapping(value = "getAllUsersForVendor/{vendorId}", method = RequestMethod.GET, headers = ACCEPT_JSON)
  @PreAuthorize("@permissionEvaluator.hasPermission(principal,'MANAGE_EQUIPMENT_SETTINGS')")
  public ResponseEntity<OpenLmisResponse> getAllUsersForVendor(@PathVariable(value = "vendorId") Long vendorId) {
    return OpenLmisResponse.response(USERS, vendorUserService.getAllUsersForVendor(vendorId));
  }

  @RequestMapping(value = "getAllUsersAvailableForVendor", method = RequestMethod.GET, headers = ACCEPT_JSON)
  @PreAuthorize("@permissionEvaluator.hasPermission(principal,'MANAGE_EQUIPMENT_SETTINGS')")
  public ResponseEntity<OpenLmisResponse> getAllUsersAvailableForVendor() {
    return OpenLmisResponse.response(USERS, vendorUserService.getAllUsersAvailableForVendor());
  }

  @RequestMapping(value="saveNewUserForVendor", method = RequestMethod.POST,headers = ACCEPT_JSON)
  @PreAuthorize("@permissionEvaluator.hasPermission(principal,'MANAGE_EQUIPMENT_SETTINGS')")
  public ResponseEntity<OpenLmisResponse> saveVendorUsers(@RequestBody VendorUser vendorUser, HttpServletRequest request) {

    vendorUser.setModifiedBy(loggedInUserId(request));
    try {
      vendorUserService.save(vendorUser);
    } catch (DataException e) {
      return OpenLmisResponse.error(e, HttpStatus.BAD_REQUEST);
    }
    ResponseEntity<OpenLmisResponse>  successResponse = OpenLmisResponse.success( messageService.message("message.equipment.vendor.associated.with.user"));
    successResponse.getBody().addData(VENDOR_USER, vendorUser);
    return successResponse;
  }

  @RequestMapping(value="remove/{vendorId}/{userId}", method = RequestMethod.GET, headers = ACCEPT_JSON)
  @PreAuthorize("@permissionEvaluator.hasPermission(principal,'MANAGE_EQUIPMENT_SETTINGS')")
  public ResponseEntity<OpenLmisResponse> remove (@PathVariable(value="vendorId") Long vendorId, @PathVariable(value="userId") Long userId){

    try {
      vendorUserService.removeVendorUserAssociation(vendorId,userId);
    } catch (DataException e) {
      return error(e, HttpStatus.BAD_REQUEST);
    }
    return  OpenLmisResponse.success("message.equipment.vendor.removed.associated.user");
  }

}
