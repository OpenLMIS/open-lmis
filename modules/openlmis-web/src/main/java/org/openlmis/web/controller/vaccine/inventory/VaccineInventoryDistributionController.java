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
import org.openlmis.vaccine.domain.inventory.VaccineDistribution;
import org.openlmis.vaccine.service.inventory.VaccineInventoryDistributionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;

import java.util.Calendar;
import java.util.Date;

import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

@Controller
@RequestMapping(value = "/vaccine/inventory/distribution")
public class VaccineInventoryDistributionController extends BaseController {

    @Autowired
    VaccineInventoryDistributionService service;

    @RequestMapping(value = "save", method = POST, headers = ACCEPT_JSON)
    //TODO @PreAuthorize("@permissionEvaluator.hasPermission(principal,'MANAGE_PROGRAM_PRODUCT')")
    @Transactional
    public ResponseEntity<OpenLmisResponse> save(@RequestBody VaccineDistribution distribution, HttpServletRequest request) {
        Long userId = loggedInUserId(request);
        return OpenLmisResponse.response("distributionId", service.save(distribution, userId));
    }

    @RequestMapping(value = "get-distributed", method = GET, headers = ACCEPT_JSON)
    //TODO @PreAuthorize("@permissionEvaluator.hasPermission(principal,'MANAGE_PROGRAM_PRODUCT')")
    public ResponseEntity<OpenLmisResponse> getAll(HttpServletRequest request) {
        Long userId = loggedInUserId(request);
        return OpenLmisResponse.response("Distributions", service.getDistributedFacilitiesByPeriod(userId));
    }

    @RequestMapping(value = "supervised-facilities", method = GET, headers = ACCEPT_JSON)
    public ResponseEntity<OpenLmisResponse> getOneLevelSupervisedFacilities(HttpServletRequest request) {
        Long userId = loggedInUserId(request);
        return OpenLmisResponse.response("facilities", service.getFacilities(userId));
    }

}
