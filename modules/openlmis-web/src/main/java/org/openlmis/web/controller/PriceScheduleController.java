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

import org.openlmis.core.domain.PriceSchedule;
import org.openlmis.core.service.PriceScheduleService;
import org.openlmis.core.web.OpenLmisResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import javax.servlet.http.HttpServletRequest;

import java.util.List;

import static org.springframework.web.bind.annotation.RequestMethod.GET;

/**
 * A front end for the price schedule service
 */
@Controller
public class PriceScheduleController {

    @Autowired
    PriceScheduleService priceScheduleService;

    public static final String PRICE_SCHEDULE_CATEGORIES = "priceScheduleCategories";

    @RequestMapping(value = "/priceScheduleCategories", method = GET)
    public ResponseEntity<OpenLmisResponse> getPriceScheduleCategory(HttpServletRequest request) {

        List<PriceSchedule> categories =  priceScheduleService.getAllPriceSchedules();
        return OpenLmisResponse.response(PRICE_SCHEDULE_CATEGORIES, categories);
    }
}
