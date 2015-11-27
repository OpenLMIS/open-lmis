/*
 *
 * Electronic Logistics Management Information System (eLMIS) is a supply chain management system for health commodities in a developing country setting.
 *
 *  Copyright (C) 2015  John Snow, Inc (JSI). This program was produced for the U.S. Agency for International Development (USAID). It was prepared under the USAID | DELIVER PROJECT, Task Order 4.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 *
 *    You should have received a copy of the GNU Affero General Public License along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 */

package org.openlmis.vaccine.repository.mapper.reports.builder.helpers;/*
 * This program was produced for the U.S. Agency for International Development. It was prepared by the USAID | DELIVER PROJECT, Task Order 4. It is part of a project which utilizes code originally licensed under the terms of the Mozilla Public License (MPL) v2 and therefore is licensed under MPL v2 or later.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the Mozilla Public License as published by the Mozilla Foundation, either version 2 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the Mozilla Public License for more details.
 *
 * You should have received a copy of the Mozilla Public License along with this program. If not, see http://www.mozilla.org/MPL/
 */

public class PerformanceByDropOutRateHelper {
    private PerformanceByDropOutRateHelper(){

    }
    /*


    String period_start_date;
    String period_end_date;
    Long period_id;
    Long product_id;
    Long program_id;
    Long geographic_zone_id;
    Long facility_id;
     */
    public static String isFilteredPeriodStartDate(String field) {
        return String.format("%s >= to_date(#{filterCriteria.period_start_date}::text, 'YYYY-MM-DD')", field);
    }
    public static String isFilteredPeriodEndDate(String field) {
        return String.format("%s <=to_date(#{filterCriteria.period_end_date}::text, 'YYYY-MM-DD')", field);
    }
    public static String isFilteredPeriodId(String field) {
        return String.format("%s = #{filterCriteria.period_id}::INT ", field);
    }
    public static String isFilteredProductId(String field) {
        return String.format("%s = #{filterCriteria.product_id}::INT ", field);
    }
    public static String isFilteredProgramId(String field) {
        return String.format("%s = #{filterCriteria.program_id}::INT ", field);
    }

    public static String isFilteredGeographicZoneId(String parent, String region, String district) {
        return String.format("(%1s = #{filterCriteria.geographic_zone_id}::INT or  %2s = #{filterCriteria.geographic_zone_id}::INT or  %3s = #{filterCriteria.geographic_zone_id}::INT " +
                "  or  0 = #{filterCriteria.geographic_zone_id}::INT) ", parent,region,district);
    }
    public static String isFilteredFacilityId(String field) {
        return String.format("%s =#{filterCriteria.facility_id}::INT ", field);
    }
}
