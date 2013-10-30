/*
 * This program was produced for the U.S. Agency for International Development. It was prepared by the USAID | DELIVER PROJECT, Task Order 4. It is part of a project which utilizes code originally licensed under the terms of the Mozilla Public License (MPL) v2 and therefore is licensed under MPL v2 or later.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the Mozilla Public License as published by the Mozilla Foundation, either version 2 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the Mozilla Public License for more details.
 *
 * You should have received a copy of the Mozilla Public License along with this program. If not, see http://www.mozilla.org/MPL/
 */
package org.openlmis.report.builder;

import org.openlmis.report.model.filter.MailingLabelReportFilter;
import org.openlmis.report.model.report.MailingLabelReport;
import org.openlmis.report.model.report.StockImbalanceReport;
import org.openlmis.report.model.sorter.MailingLabelReportSorter;

import java.util.Map;

import static org.apache.ibatis.jdbc.SqlBuilder.*;
import static org.apache.ibatis.jdbc.SqlBuilder.ORDER_BY;
import static org.apache.ibatis.jdbc.SqlBuilder.SQL;

public class MailingLabelReportQueryBuilder {

    public static String SelectFilteredSortedPagedMailingLabelsSql(Map params){

        MailingLabelReportFilter filter  =(MailingLabelReportFilter)params.get("filterCriteria");
        MailingLabelReportSorter sorter = (MailingLabelReportSorter)params.get("SortCriteria");
        BEGIN();
        SELECT("F.id, F.code, F.name, F.active as active, F.address1, F.address2 , FT.name as facilityType, GZ.name as region, FO.code as owner, F.latitude::text ||',' ||  F.longitude::text  ||', ' || F.altitude::text gpsCoordinates, CASE WHEN U.officePhone IS NULL THEN '' ELSE U.officePhone || ' ,' END || CASE WHEN U.cellPhone IS NULL THEN '' ELSE U.cellPhone || ' ,' END || F.mainPhone as phoneNumber, U.email email, F.fax as fax, U.firstName || ' ' || U.lastName || ', ' || jobtitle contact ");
        FROM("facilities F");
        JOIN("facility_types FT on FT.id = F.typeid");
        LEFT_OUTER_JOIN("geographic_zones GZ on GZ.id = F.geographiczoneid");
        LEFT_OUTER_JOIN("facility_operators FO on FO.id = F.operatedbyid");
        LEFT_OUTER_JOIN("requisition_group_members ON f.id = requisition_group_members.facilityid");
        LEFT_OUTER_JOIN("requisition_groups ON requisition_groups.id = requisition_group_members.requisitiongroupid");
        LEFT_OUTER_JOIN("Users U on U.facilityId = F.id ");

        if(filter != null){
           if (filter.getFacilityTypeId() != 0) {
                WHERE("F.typeid = "+ filter.getFacilityTypeId());
            }
            if(filter.getRgroupId() != 0){
                WHERE("requisition_groups.id = "+ filter.getRgroupId());
            }
        }
        return SQL();
    }
}
