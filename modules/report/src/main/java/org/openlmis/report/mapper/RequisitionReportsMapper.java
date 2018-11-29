/*
 * This program is part of the OpenLMIS logistics management information system platform software.
 * Copyright © 2013 VillageReach
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *  
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 * You should have received a copy of the GNU Affero General Public License along with this program.  If not, see http://www.gnu.org/licenses.  For additional information contact info@OpenLMIS.org. 
 */

package org.openlmis.report.mapper;

import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.SelectProvider;
import org.openlmis.report.builder.RequisitionReportsQueryBuilder;
import org.openlmis.report.model.dto.RequisitionDTO;
import org.openlmis.report.model.params.NonSubmittedRequisitionReportsParam;
import org.openlmis.report.model.params.RequisitionReportsParam;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * It maps the Rnr entity to corresponding representation in database.
 */

@Repository
public interface RequisitionReportsMapper {

    @SelectProvider(type = RequisitionReportsQueryBuilder.class, method = "getSubmittedResult")
    List<RequisitionDTO> getSubmittedRequisitionList(@Param("filterCriteria") RequisitionReportsParam filterCriteria);

    @Select("select -1 as id, fc.name facilityName, zone.name districtName, parent_zone.name provinceName, FALSE as emergency, p.name programName, '' as submittedUser, NULL as clientSubmittedTime, '' as requisitionStatus, NULL as webSubmittedTime, NULL as actualPeriodEnd, pp.startdate as schedulePeriodStart, pp.enddate schedulePeriodEnd\n" +
            " FROM facilities fc" +
            "  LEFT OUTER JOIN programs_supported ps on fc.id = ps.facilityid" +
            "  LEFT OUTER JOIN geographic_zones as zone on fc.geographiczoneid = zone.id" +
            "  LEFT OUTER JOIN geographic_zones as parent_zone on zone.parentid = parent_zone.id" +
            "  LEFT OUTER JOIN programs p on ps.programid = p.id" +
            "  CROSS JOIN processing_periods pp" +
            " WHERE (pp.startdate >= #{filterCriteria.startTime} AND pp.enddate <= #{filterCriteria.endTime})" +
            "  AND pp.id NOT in (SELECT periodid from requisitions WHERE facilityid = #{filterCriteria.facilityId} AND programid = #{filterCriteria.programId})" +
            "  AND (pp.startdate >= ps.reportstartdate)" +
            "  AND p.id = #{filterCriteria.programId}" +
            "  AND facilityid = #{filterCriteria.facilityId}" +
            "  AND ps.reportactive is TRUE;")
    List<RequisitionDTO> getUnSubmittedRequisitionList(@Param("filterCriteria")NonSubmittedRequisitionReportsParam filterCriteria);
}
