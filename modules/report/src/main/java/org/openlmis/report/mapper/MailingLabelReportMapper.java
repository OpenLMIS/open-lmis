package org.openlmis.report.mapper;

import org.apache.ibatis.annotations.*;
import org.apache.ibatis.mapping.ResultSetType;
import org.apache.ibatis.session.RowBounds;
import org.openlmis.report.model.FacilityReport;
import org.openlmis.report.model.MailingLabelReport;
import org.openlmis.report.model.ReportData;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: user
 * Date: 4/10/13
 * Time: 6:33 AM
 * To change this template use File | Settings | File Templates.
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
            @Result(column="phoneNumber", property="phoneNumber"),
            @Result(column="fax", property="fax")
    })
    public List<MailingLabelReport> SelectFilteredSortedPagedFacilities(
            @Param("filterCriteria") ReportData filterCriteria,
            @Param("SortCriteria") ReportData SortCriteria,
            @Param("RowBounds") RowBounds rowBounds
            //        @Param("page") int page,
            //        @Param("pageSize") int pageSize
    );

    @SelectProvider(type=MailingLabelReportQueryBuilder.class, method="SelectFilteredFacilitiesCountSql")
    //@Selcet("select count(*) from facilities")
    public Integer SelectFilteredFacilitiesCount(@Param("filterCriteria") ReportData filterCriteria);
}
