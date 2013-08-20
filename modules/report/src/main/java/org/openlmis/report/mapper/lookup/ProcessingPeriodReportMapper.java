/*
 * Copyright © 2013 John Snow, Inc. (JSI). All Rights Reserved.
 *
 * The U.S. Agency for International Development (USAID) funded this section of the application development under the terms of the USAID | DELIVER PROJECT contract no. GPO-I-00-06-00007-00.
 */

package org.openlmis.report.mapper.lookup;

import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.openlmis.report.model.dto.ProcessingPeriod;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

/**
 * e-lmis
 * Created by: Elias Muluneh
 * Date: 4/12/13
 * Time: 2:39 AM
 */
@Repository
public interface ProcessingPeriodReportMapper {

    @Select("SELECT id, name, startdate, enddate, description  " +
            "   FROM " +
            "       processing_periods")
    List<ProcessingPeriod> getAll();

    @Select("SELECT id, name, startdate, enddate, description  " +
            "   FROM processing_periods " +
            "WHERE startdate >= #{startDate, jdbcType=DATE, javaType=java.util.Date, mode=IN} and " +
            "enddate <= #{endDate, jdbcType=DATE, javaType=java.util.Date, mode=IN}")
    List<ProcessingPeriod> getFilteredPeriods(@Param("startDate") Date startDate, @Param("endDate") Date endDate);
}
