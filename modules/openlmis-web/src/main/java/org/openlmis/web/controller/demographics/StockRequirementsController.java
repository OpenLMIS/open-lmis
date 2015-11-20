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
import org.openlmis.demographics.dto.StockRequirements;
import org.openlmis.demographics.service.StockRequirementsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

import static org.springframework.web.bind.annotation.RequestMethod.GET;


@Controller
public class StockRequirementsController extends BaseController
{
    @Autowired
    private StockRequirementsService stockRequirementsService;

    /**
     * @deprecated
     * Because the calculations used to determine and return stock-related info are VIMS specific, this endpoint may be considered deprecated.
     */
    @Deprecated
    @ApiOperation
    (
        value = "Returns the Minimum Stock, Maximum Stock, and re-order levels, along with ancillary data, for the active products at the specified facility's program. <p />"

        ,notes = "<p> " +
                 "All of the calculations done by this class rely on an ISA, which may come from two different places. If an ISA that has been configured for a specific FacilityProgramProduct (for instance, via the \"Programs Supported\" section of the Facility tab in the UI) will take precedent. If no such ISA exists, then the general ISA associated with a ProgramProduct (for instance, via \"Administration\" -> \"Configure\" -> \"Program Product ISA\" in the UI) will be used.\n" +
                 "<br>" +
                 "Every ISA requires a population value as a coefficient, and is configured to retrieve this population from one of the following possible sources:\n" +
                 "<br>" +
                 " * The facility catchment population\n" +
                 "<br>" +
                 " * A demographic estimate. If an ISA's population is specified to come from a demographic estimate, it will try looking it up for a specific program. If the user has specified no such value, the DemographicEstimateService will do its best to determine a reasonable value based on the facility catchment population. This value, however, is not likely to equal the facility catchment population. \n" +
                 "<br> " +
                 "Note that the above two options potentially rely on a facility catchment population. If no such value is specified in an instance wherein it is found to be necessary, it will not be possible to return any of the stock-related values reliant on an ISA. This erroneous configuration results in a falsey (specifically, either 0 or null) population value being returned. \n" +
                 "<br>" +
                 "Please also note that a third population-source is used when dealing with an RVS or CVS. In such cases, the total population of all of its child facilities is used. " +
                 "<br><br> " +
                 "Also, note that inconsistent population values are necessarily returned if inconsistent population-sources are specified. This happens, for instance, if a user specifies that “Facility Catchment” be used for a ProgramProduct ISA, yet “Children Under 2” be used for a specific DVS. The population associated with an RVS serving the DVS would in part be comprised of the DVS’ population. Rather than using the DVS’ “Children Under 2” population, however, it would use its “Facility Catchment” population. This is by design. Only inconsistent configuration, however, will yield this behavior. " +
                 "<br><br> " +
                 "Finally, please note that the FacilityDemography page relies on a geoZone being associated with the relevant SDP. Therefore, without a geoZone, an SDP cannot be assigned a demography estimate. An SDP without a geoZone is thus automatically treated as though it Facility Catchment Population should be used for the sake of all ISA calculations. " +
                 "<br><br> " +
                 "Noteworthy values returned by this endpoint include: " +
                 "<br><br> " +
                 "<b>facilityId, facilityCode, and productId:</b> Returned as a convenience for the developer." +
                 "<br><br> " +
                 "<b>population:</b> Usually either a facility catchment, a user-specified demography value, or the total of all child-facility populations. See above documentation for details. " +
                 "<br><br> " +
                 "<b>isaCoefficients:</b> The ISA Coefficients specified by the user at the facility-level. If such values don't exist, the ISA Coefficients set at the more general program-product level are returned. " +
                 "<br><br> " +
                 "<b>minMonthsOfStock, maxMonthsOfStock, and eop:</b> Values set by the user, potentially via the Facility Approved Products page (Administration -> Manage -> Facility Approved Products). Note that eop stands for 'Emergency Order Point.' " +
                 "<br><br> " +
                 "<b>isaValue:</b> The result of applying the ISA formula to the isaCoefficients. " +
                 "<br><br> " +
                 "<b>MinimumStock:</b>  This equals isaValue * minMonthsOfStock " +
                 "<br><br> " +
                 "<b>MaximumStock:</b>  This equals isaValue * maxMonthsOfStock " +
                 "<br><br> " +
                 "<b>ReorderLevel:</b>  This equals isaValue * eop " +
                 "</p> "
    )
    @RequestMapping(value = "/rest-api/facility/{facilityId}/program/{programId}/stockRequirements", method = GET, headers = ACCEPT_JSON)
    public ResponseEntity getStockRequirements(@PathVariable Long facilityId, @PathVariable Long programId)
    {
        List<StockRequirements> stockRequirements = stockRequirementsService.getStockRequirements(facilityId, programId);
       if(stockRequirements == null) {
           return OpenLmisResponse.error("Stock requirements not found. Please ensure that the specified facility exists and is properly configured." , HttpStatus.NOT_FOUND);
       }

        String JSON =  StockRequirements.getJSONArray(stockRequirements);
        return OpenLmisResponse.response(JSON);
    }
}
