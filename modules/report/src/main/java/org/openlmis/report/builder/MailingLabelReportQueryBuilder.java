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
package org.openlmis.report.builder;

import org.openlmis.report.model.params.MailingLabelReportParam;

import java.util.Map;

import static org.apache.ibatis.jdbc.SqlBuilder.*;
import static org.openlmis.report.builder.helpers.RequisitionPredicateHelper.facilityTypeIsFilteredBy;
import static org.openlmis.report.builder.helpers.RequisitionPredicateHelper.geoZoneIsFilteredBy;

public class MailingLabelReportQueryBuilder {

    public static String getQuery(Map params){

        MailingLabelReportParam filter  =(MailingLabelReportParam)params.get("filterCriteria");

        BEGIN();
        SELECT("distinct F.id, F.code, F.name, F.name As facilityName, F.active as active, F.address1, F.address2 , FT.name as facilityType, GZ.district_name as region, FO.code as owner, F.latitude::text ||',' ||  F.longitude::text  ||', ' || F.altitude::text gpsCoordinates, CASE WHEN U.officePhone IS NULL THEN '' ELSE U.officePhone || ' ,' END || CASE WHEN U.cellPhone IS NULL THEN '' ELSE U.cellPhone || ' ,' END || F.mainPhone as phoneNumber, U.email email, F.fax as fax, U.firstName || ' ' || U.lastName || ', ' || jobtitle contact ");
        FROM("facilities F");
        JOIN("facility_types FT on FT.id = F.typeid");
        LEFT_OUTER_JOIN("programs_supported ps on ps.facilityid = F.id");
        LEFT_OUTER_JOIN("vw_districts GZ on GZ.district_id = F.geographiczoneid");
        LEFT_OUTER_JOIN("facility_operators FO on FO.id = F.operatedbyid");
        LEFT_OUTER_JOIN("requisition_group_members ON f.id = requisition_group_members.facilityid");
        LEFT_OUTER_JOIN("requisition_groups ON requisition_groups.id = requisition_group_members.requisitiongroupid");
        LEFT_OUTER_JOIN("Users U on U.facilityId = F.id ");

        if(filter != null){
           if (filter.getFacilityType() != 0) {
                WHERE( facilityTypeIsFilteredBy( "F.typeid"));
            }
            if(filter.getZone() != 0){
              WHERE(geoZoneIsFilteredBy("GZ"));
            }
            if (filter.getStatus() != null) {
                WHERE("F.active = " + filter.getStatus().toString());
            }
        }

        return SQL();
    }
}
