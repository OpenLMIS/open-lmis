package org.openlmis.web.controller;

import org.openlmis.core.domain.PriceSchedule;
import org.openlmis.core.service.PriceScheduleService;
import org.openlmis.web.response.OpenLmisResponse;
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
