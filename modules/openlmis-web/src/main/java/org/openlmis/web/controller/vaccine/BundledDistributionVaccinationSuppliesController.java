/*
 * This program was produced for the U.S. Agency for International Development. It was prepared by the USAID | DELIVER PROJECT, Task Order 4. It is part of a project which utilizes code originally licensed under the terms of the Mozilla Public License (MPL) v2 and therefore is licensed under MPL v2 or later.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the Mozilla Public License as published by the Mozilla Foundation, either version 2 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the Mozilla Public License for more details.
 *
 * You should have received a copy of the Mozilla Public License along with this program. If not, see http://www.mozilla.org/MPL/
 */
package org.openlmis.web.controller.vaccine;

import org.openlmis.core.web.OpenLmisResponse;
import org.openlmis.core.web.controller.BaseController;
import org.openlmis.vaccine.service.reports.BundledDistributionVaccinationSuppliesService;
import org.openlmis.vaccine.service.reports.VaccineReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
@RequestMapping(value = "/vaccine/report/")
public class BundledDistributionVaccinationSuppliesController extends BaseController {

    public static final String BUNDLED_DISTRIBUTION_VACCINATION_SUPPLIES = "bundledDistributionVaccinationSupplies";

    @Autowired
    BundledDistributionVaccinationSuppliesService service;

    @RequestMapping(value = "view-bundled-distribution-vaccination-supplies/{year}/{productId}", method = RequestMethod.GET, headers = "Accept=application/json")

    public ResponseEntity<OpenLmisResponse> getViewPeriods(@PathVariable Long year, @PathVariable Long productId) {
        return OpenLmisResponse.response(BUNDLED_DISTRIBUTION_VACCINATION_SUPPLIES, service.getBundledDistributionVaccinationSupplies(year, productId));
    }


}
