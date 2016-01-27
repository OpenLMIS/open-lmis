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
import org.openlmis.equipment.domain.Vendor;
import org.openlmis.equipment.service.VendorService;
import org.openlmis.core.web.controller.BaseController;
import org.openlmis.core.web.OpenLmisResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import static org.openlmis.core.web.OpenLmisResponse.error;
import static org.openlmis.core.web.OpenLmisResponse.success;
import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

@Controller
@RequestMapping(value = "/equipment/vendor/")
public class VendorController extends BaseController {

  public static final String VENDOR = "vendor";

  @Autowired
  private VendorService service;

  @RequestMapping(method = GET, value = "list")
  public ResponseEntity<OpenLmisResponse> getAll() {
    return OpenLmisResponse.response("vendors", service.getAll());
  }

  @RequestMapping(method = GET, value = "id")
  public ResponseEntity<OpenLmisResponse> getById(@RequestParam("id") Long id) {
    return OpenLmisResponse.response(VENDOR, service.getById(id));
  }


  @RequestMapping(value = "save", method = POST, headers = ACCEPT_JSON)
  public ResponseEntity<OpenLmisResponse> save(@RequestBody Vendor vendor) {
    try {
      service.save(vendor);
    } catch (DuplicateKeyException exp) {
      return OpenLmisResponse.error("error.equipment.vendor.code.duplicate", HttpStatus.BAD_REQUEST);
    }
    ResponseEntity<OpenLmisResponse> response = OpenLmisResponse.success("message.equipment.successfully.saved.vendor");
    response.getBody().addData(VENDOR, vendor);
    return response;
  }

  @RequestMapping(value = "delete/{id}", method = GET, headers = ACCEPT_JSON)
  public ResponseEntity<OpenLmisResponse> remove(@PathVariable(value = "id") Long vendorId) {

    try {
      service.removeVendor(vendorId);
    } catch (DataIntegrityViolationException ex) {
      return OpenLmisResponse.error("error.equipment.vendor.in.use", HttpStatus.BAD_REQUEST);
    } catch (DataException e) {
      return error(e, HttpStatus.BAD_REQUEST);
    }
    return success("message.equipment.vendor.removed.successfully");

  }
}
