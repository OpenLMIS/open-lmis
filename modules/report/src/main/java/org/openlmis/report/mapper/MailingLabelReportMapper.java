/*
 * Copyright © 2013 John Snow, Inc. (JSI). All Rights Reserved.
 *
 * The U.S. Agency for International Development (USAID) funded this section of the application development under the terms of the USAID | DELIVER PROJECT contract no. GPO-I-00-06-00007-00.
 */

package org.openlmis.report.mapper;

import org.apache.ibatis.annotations.*;
import org.apache.ibatis.mapping.ResultSetType;
import org.apache.ibatis.session.RowBounds;
import org.openlmis.report.builder.MailingLabelReportQueryBuilder;
import org.openlmis.report.model.report.MailingLabelReport;
import org.openlmis.report.model.ReportData;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * User: user
 * Date: 4/10/13
 * Time: 6:33 AM
 */
@Repository
public interface MailingLabelReportMapper {

    @SelectProvider(type=MailingLabelReportQueryBuilder.class, method="SelectFilteredSortedPagedMailingLabelsSql")
    @Options(resultSetType = ResultSetType.SCROLL_SENSITIVE, fetchSize=10,timeout=0,useCache=true,flushCache=true)
    //@Select("SELECT id, code, name FROM facilities")
    //@RowBounds(getLimit = 10,getOffset = 10)
    @Results(value = {
            @Result(column="code", property="code"),
            @Result(column="name", property="facilityName"),
            @Result(column="active", property="active"),
            @Result(column="facilityType", property="facilityType"),
            @Result(column="region", property="region"),
            @Result(column="owner", property="owner"),
/*            @Result(column ="longitude", property = "longitude"),
            @Result(column ="latitude", property = "latitude"),
            @Result(column ="altitude", property = "altitude"),*/
            @Result(column = "gpsCoordinates", property = "gpsCoordinates"),
            @Result(column="phoneNumber", property="phoneNumber"),
            @Result(column="fax", property="fax")
    })
    public List<MailingLabelReport> SelectFilteredSortedPagedFacilities(
            @Param("filterCriteria") ReportData filterCriteria,
            @Param("SortCriteria") ReportData SortCriteria,
            @Param("RowBounds") RowBounds rowBounds
    );
}
