/*
 * Electronic Logistics Management Information System (eLMIS) is a supply chain management system for health commodities in a developing country setting.
 *
 * Copyright (C) 2015 Clinton Health Access Initiative (CHAI). This program was produced for the U.S. Agency for International Development (USAID). It was prepared under the USAID | DELIVER PROJECT, Task Order 4.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.openlmis.web.controller.vaccine.inventory;


import org.openlmis.core.web.OpenLmisResponse;
import org.openlmis.core.web.controller.BaseController;
import org.openlmis.vaccine.domain.inventory.VaccineInventoryProductConfiguration;
import org.openlmis.vaccine.service.inventory.VaccineInventoryConfigurationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

import static org.openlmis.core.web.OpenLmisResponse.response;
import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.PUT;

@Controller
@RequestMapping(value = "/vaccine/inventory/configuration")
public class VaccineInventoryConfigurationController extends BaseController {

    @Autowired
    VaccineInventoryConfigurationService service;

    @RequestMapping(value = "save", method = PUT, headers = ACCEPT_JSON)
    //TODO @PreAuthorize("@permissionEvaluator.hasPermission(principal,'MANAGE_PROGRAM_PRODUCT')")
    public ResponseEntity<OpenLmisResponse> save(@RequestBody List<VaccineInventoryProductConfiguration> configurations) {
        service.save(configurations);
        return OpenLmisResponse.response("Configurations", service.getAll());
    }

    @RequestMapping(value = "getAll", method = GET, headers = ACCEPT_JSON)
    //TODO @PreAuthorize("@permissionEvaluator.hasPermission(principal,'MANAGE_PROGRAM_PRODUCT')")
    public ResponseEntity<OpenLmisResponse> getAll() {
        return OpenLmisResponse.response("Configurations", service.getAll());
    }

}
