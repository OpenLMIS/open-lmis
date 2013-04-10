package org.openlmis.report.dataprovider;

import lombok.NoArgsConstructor;
import org.apache.ibatis.session.RowBounds;
import org.openlmis.core.service.FacilityService;
import org.openlmis.report.mapper.FacilityReportMapper;
import org.openlmis.report.mapper.MailingLabelReportMapper;
import org.openlmis.report.model.ReportData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: user
 * Date: 4/10/13
 * Time: 6:02 AM
 * To change this template use File | Settings | File Templates.
 */
@Component
@NoArgsConstructor
public class MailingLabelReportDataProvider extends ReportDataProvider {


    private FacilityService facilityService;
    private MailingLabelReportMapper mailingLabelReportMapper;

    @Autowired
    public MailingLabelReportDataProvider(FacilityService facilityService, MailingLabelReportMapper mailingLabelReportMapper) {
        this.facilityService = facilityService;
        this.mailingLabelReportMapper = mailingLabelReportMapper;
    }

    @Override
    protected List<? extends ReportData> getBeanCollectionReportData(ReportData filterCriteria) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    protected List<? extends ReportData> getResultSetReportData(ReportData filterCriteria) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public List<? extends ReportData> getReportDataByFilterCriteriaAndPagingAndSorting(ReportData filterCriteria, ReportData SortCriteria, int page, int pageSize) {
        RowBounds rowBounds = new RowBounds((page-1)*pageSize,pageSize);
        return mailingLabelReportMapper.SelectFilteredSortedPagedFacilities(filterCriteria,SortCriteria,rowBounds);
    }

    @Override
    public int getReportDataCountByFilterCriteria(ReportData mailingLabelReportFilter) {
        return (int)mailingLabelReportMapper.SelectFilteredFacilitiesCount(mailingLabelReportFilter);
    }
}
