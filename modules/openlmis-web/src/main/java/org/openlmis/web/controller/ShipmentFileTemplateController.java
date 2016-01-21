/*
 * This program is part of the OpenLMIS logistics management information system platform software.
 * Copyright © 2013 VillageReach
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *  
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 * You should have received a copy of the GNU Affero General Public License along with this program.  If not, see http://www.gnu.org/licenses.  For additional information contact info@OpenLMIS.org. 
 */

package org.openlmis.web.controller;

import org.openlmis.core.domain.EDIFileTemplate;
import org.openlmis.core.web.controller.BaseController;
import org.openlmis.shipment.service.ShipmentFileTemplateService;
import org.openlmis.core.web.OpenLmisResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;

import static java.util.Arrays.asList;
import static org.openlmis.core.web.OpenLmisResponse.response;
import static org.openlmis.core.web.OpenLmisResponse.success;
import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

/**
 * This controller handles endpoint to list, create shipment file template
 */

@Controller
public class ShipmentFileTemplateController extends BaseController {

  @Autowired
  private ShipmentFileTemplateService service;

  @RequestMapping(value = "/shipment-file-template", method = GET)
  @PreAuthorize("@permissionEvaluator.hasPermission(principal,'SYSTEM_SETTINGS')")
  public ResponseEntity<OpenLmisResponse> get() {
    return response("shipment_template", service.get());
  }

  @RequestMapping(value = "/shipment-file-template", method = POST, headers = ACCEPT_JSON)
  @PreAuthorize("@permissionEvaluator.hasPermission(principal,'SYSTEM_SETTINGS')")
  public ResponseEntity<OpenLmisResponse> update(@RequestBody EDIFileTemplate shipmentFileTemplate,
                                                 HttpServletRequest request) {
    shipmentFileTemplate.validateAndSetModifiedBy(loggedInUserId(request), asList("productCode", "orderId", "quantityShipped"));
    service.update(shipmentFileTemplate);

    return success("shipment.file.configuration.success");
  }
}
