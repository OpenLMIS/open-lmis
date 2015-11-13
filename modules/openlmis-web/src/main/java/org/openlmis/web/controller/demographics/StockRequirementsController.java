/*
 * This program is part of the OpenLMIS logistics management information system platform software.
 * Copyright © 2013 VillageReach
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *  
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 * You should have received a copy of the GNU Affero General Public License along with this program.  If not, see http://www.gnu.org/licenses.  For additional information contact info@OpenLMIS.org. 
 */

package org.openlmis.web.controller.demographics;

import com.wordnik.swagger.annotations.ApiOperation;
import org.openlmis.core.web.OpenLmisResponse;
import org.openlmis.core.web.controller.BaseController;
import org.openlmis.demographics.domain.StockRequirements;
import org.openlmis.demographics.service.StockRequirementsService;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.beans.factory.annotation.Autowired;

import static org.springframework.web.bind.annotation.RequestMethod.GET;

@Controller
public class StockRequirementsController extends BaseController
{
    @Autowired
    private StockRequirementsService stockRequirementsService;

    @ApiOperation
    (
        value = "Returns the Minimum Stock, Maximum Stock, and re-order levels, along with ancillary data, for the active products at the specified facility's program.",
        notes = "The values returned by this endpoint may be categorized as follows: <p />" +
                "<b>facilityId and productId:</b> Returned as a convenience for the developer. <p />" +
                "<b>population:</b> The 'catchment population' associated with the relevant facility. In the future, this value will come from alternate sources as well. <p />" +
                "<b>isaCoefficients:</b> The ISA Coefficients specified by the user at the facility-level. If such values don't exist, the ISA Coefficients set at the more general program-product level are returned. <p />" +
                "<b>minMonthsOfStock, maxMonthsOfStock, and eop:</b> Values set by the user, potentially via the Facility Approved Products page. Note that eop stands for 'Emergency Order Point.' <p />" +
                "<b>isaValue:</b> The result of applying the ISA formula to the isaCoefficients. <p />" +
                "<b>MinimumStock:</b>  This equals isaValue * minMonthsOfStock <p />" +
                "<b>MaximumStock:</b>  This equals isaValue * maxMonthsOfStock <p />" +
                "<b>ReorderLevel:</b>  This equals isaValue * eop <p />"
    )
    @RequestMapping(value = "/rest-api/facility/{facilityId}/program/{programId}/stockRequirements", method = GET, headers = ACCEPT_JSON)
    public ResponseEntity<Object> getStockRequirements(@PathVariable Long facilityId, @PathVariable Long programId)
    {
        String JSON =  StockRequirements.getJSONArray(stockRequirementsService.getStockRequirements(facilityId, programId));
        return OpenLmisResponse.response(JSON);
    }
}
