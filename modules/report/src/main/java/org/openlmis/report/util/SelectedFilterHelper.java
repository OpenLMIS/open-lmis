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

package org.openlmis.report.util;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.openlmis.core.domain.Facility;
import org.openlmis.core.domain.GeographicZone;
import org.openlmis.core.domain.ProcessingPeriod;
import org.openlmis.core.domain.Product;
import org.openlmis.core.repository.FacilityRepository;
import org.openlmis.core.repository.GeographicZoneRepository;
import org.openlmis.core.repository.ProcessingPeriodRepository;
import org.openlmis.core.repository.ProductRepository;
import org.openlmis.core.service.ProgramService;
import org.openlmis.core.service.SupervisoryNodeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;


//TODO:  Oh Lord - please re-write this class
@Component
@NoArgsConstructor
@Data
public class SelectedFilterHelper {

    @Autowired
    private ProcessingPeriodRepository periodService;

    @Autowired
    private ProgramService programService;

    @Autowired
    private ProductRepository productService;

    @Autowired
    private GeographicZoneRepository geoZoneRepsotory;

    @Autowired
    private FacilityRepository facilityRepository;

    @Autowired
    private SupervisoryNodeService supervisoryNodeService;

    public String getProgramPeriodGeoZone(Map<String, String[]> params) {
        String filterSummary = "";

        String program = StringHelper.isBlank(params, "program") ? "0" : params.get("program")[0];
        String period = StringHelper.isBlank(params, "period") ? "0" : params.get("period")[0];
        String zone = StringHelper.isBlank(params, "zone") ? "0" : params.get("zone")[0];
        String userId = StringHelper.isBlank(params, "userId") ? "0" : params.get("userId")[0];
        // these filters are essential for all reports and these lines should be fairly re-used.

        ProcessingPeriod periodObject = periodService.getById(Long.parseLong(period));
        GeographicZone zoneObject = geoZoneRepsotory.getById(Long.parseLong(zone));
        if (program != null) {
            if(program.equals("0")){
                filterSummary = "Program: All Programs";
            }else {
                filterSummary = "Program: " + programService.getById(Long.parseLong(program)).getName();
            }
        }
        if (periodObject != null) {
            filterSummary += "\nPeriod: " + periodObject.getName() + ", " + periodObject.getStringYear();
        }
        if (zoneObject == null) {
            // Lets determine the user's supervisory node is either National or not
            Long totalSNods = supervisoryNodeService.getTotalUnassignedSupervisoryNodeOfUserBy(Long.parseLong(userId), Long.parseLong(program));

            if (totalSNods == 0)
                filterSummary += "\nGeographic Zone: National";
            else
                filterSummary += "\nGeographic Zone: All Zones";

        } else {
            filterSummary += "\nGeographic Zone: " + zoneObject.getName();
        }

        return filterSummary;
    }

    public String getProgramGeoZoneFacility(Map<String, String[]> params) {

        String program = StringHelper.isBlank(params, "program") ? "0" : params.get("program")[0];
        String zone = StringHelper.isBlank(params, "zone") ? "0" : params.get("zone")[0];
        String facility = StringHelper.isBlank(params, "facility") ? "0" : params.get("facility")[0];

        String filterSummary = "Program: " + ((program.equals("0"))?"":programService.getById(Long.parseLong(program)).getName());
        GeographicZone zoneObject = geoZoneRepsotory.getById(Long.parseLong(zone));
        Facility facilityObject = facilityRepository.getById(Long.parseLong(facility));

        if (zoneObject == null) {
            filterSummary += "\nGeographic Zone: National";
        } else {
            filterSummary += "\nGeographic Zone: " + zoneObject.getName();
        }

        if (facilityObject == null) {
            filterSummary += "\nFacility: All Facilities";
        } else {
            filterSummary += "\nFacility: " + facilityObject.getName();
        }

        return filterSummary;
    }

    public String getSelectedFilterString(Map<String, String[]> params) {
        String filterSummary = "";

        String product = params.get("product")[0];
        String program = params.get("program")[0];
        String period = params.get("period")[0];
        // these filters are essential for all reports and these lines should be fairly re-used.

        filterSummary = "Program: " + programService.getById(Long.parseLong(program)).getName();
        filterSummary += "\nPeriod: " + periodService.getById(Long.parseLong(period)).getName();

        if (product.isEmpty()) {
            filterSummary += "\nProduct: All Products";
        } else if (product.equalsIgnoreCase("0")) {
            filterSummary += "\nProduct: Indicator / Tracer Commodities";
        } else {
            Product productObject = productService.getById(Long.parseLong(product));
            if (productObject != null) {
                filterSummary += "Product: " + productObject.getFullName();
            }
        }

        return filterSummary;
    }


}
