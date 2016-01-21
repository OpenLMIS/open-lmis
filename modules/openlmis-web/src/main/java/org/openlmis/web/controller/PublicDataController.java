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
package org.openlmis.web.controller;

import lombok.NoArgsConstructor;
import org.openlmis.core.repository.PublicDataRepository;
import org.openlmis.core.web.OpenLmisResponse;
import org.openlmis.core.web.controller.BaseController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;

import static org.openlmis.core.web.OpenLmisResponse.response;
import static org.springframework.web.bind.annotation.RequestMethod.GET;

/**
 * This class is an entry point for the publicly available (unsecured) data access
 */
@Controller
@NoArgsConstructor
public class PublicDataController  extends BaseController {

    @Autowired
    PublicDataRepository publicDataRepository;

    private static final String JSON_LIST = "list";


    @RequestMapping(value = "/public-data/regions", method = GET)
    public ResponseEntity<OpenLmisResponse> getProgramProductsByProgram(HttpServletRequest request) {
        return response(JSON_LIST, publicDataRepository.getPublicDataRegions());
    }

    @RequestMapping(value = "/public-data/districts", method = GET)
    public ResponseEntity<OpenLmisResponse> getPublicDataDistricts(HttpServletRequest request) {
        return response(JSON_LIST, publicDataRepository.getPublicDataDistricts());
    }

    @RequestMapping(value = "/public-data/facilities", method = GET)
    public ResponseEntity<OpenLmisResponse> getPublicDataFacilities(HttpServletRequest request) {
        return response(JSON_LIST, publicDataRepository.getPublicDataHealthFacilities());
    }

    @RequestMapping(value = "/public-data/arv-products", method = GET)
    public ResponseEntity<OpenLmisResponse> getPublicDataProductsARV(HttpServletRequest request) {
        return response(JSON_LIST, publicDataRepository.getPublicDataProductsARV());
    }

    @RequestMapping(value = "/public-data/ils-products", method = GET)
    public ResponseEntity<OpenLmisResponse> getPublicDataProductsILS(HttpServletRequest request) {
        return response(JSON_LIST, publicDataRepository.getPublicDataProductsILS());
    }

    @RequestMapping(value = "/public-data/labdist-products", method = GET)
    public ResponseEntity<OpenLmisResponse> getPublicDataProductsLabDist(HttpServletRequest request) {
        return response(JSON_LIST, publicDataRepository.getPublicDataProductsLabDist());
    }

    @RequestMapping(value = "/public-data/labnational-products", method = GET)
    public ResponseEntity<OpenLmisResponse> getPublicDataProductsLabNational(HttpServletRequest request) {
        return response(JSON_LIST, publicDataRepository.getPublicDataProductsLabNational());
    }

    @RequestMapping(value = "/public-data/labregional-products", method = GET)
    public ResponseEntity<OpenLmisResponse> getPublicDataProductsLabReg(HttpServletRequest request) {
        return response(JSON_LIST, publicDataRepository.getPublicDataProductsLabReg());
    }

    @RequestMapping(value = "/public-data/labzone-products", method = GET)
    public ResponseEntity<OpenLmisResponse> getPublicDataProductsLabZone(HttpServletRequest request) {
        return response(JSON_LIST, publicDataRepository.getPublicDataProductsLabZone());
    }

    @RequestMapping(value = "/public-data/tb-products", method = GET)
    public ResponseEntity<OpenLmisResponse> getPublicDataProductsTB(HttpServletRequest request) {
        return response(JSON_LIST, publicDataRepository.getPublicDataProductsTB());
    }
}
