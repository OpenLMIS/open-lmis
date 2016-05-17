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
import org.openlmis.report.model.dto.RequisitionDTO;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

/**
 * It maps the Rnr entity to corresponding representation in database.
 */

@Repository
public interface RequisitionReportsMapper {

    @Select({"SELECT req.id id, " +
            "fac.name facilityName, " +
            "req.emergency emergency, " +
            "pro.name programName, " +
            "us.username submittedUser, " +
            "req.clientsubmittedtime clientSubmittedTime, " +
            "req.status requisitionStatus, " +
            "req.modifieddate webSubmittedTime, " +
            "rp.periodenddate actualPeriodEnd, " +
            "pp.enddate schedulePeriodEnd " +
            "  FROM requisitions req" +
            "  left join facilities fac" +
            "      on req.facilityid = fac.id" +
            "  left join programs pro" +
            "      on req.programid = pro.id" +
            "  left join users us" +
            "      on req.modifiedby=us.id" +
            "  left join requisition_periods rp" +
            "      on req.id = rp.rnrid " +
            "  left join processing_periods pp " +
            "      on req.periodid = pp.id" +
            "  where ",
            "(req.modifiedDate >= #{startDate} "
                    + "AND req.modifiedDate <= #{endDate})"})
    List<RequisitionDTO> getRequisitionList(@Param("startDate") Date startTime,
                                            @Param("endDate") Date endTime);

}

