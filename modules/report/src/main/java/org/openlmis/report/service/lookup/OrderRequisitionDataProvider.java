package org.openlmis.report.service.lookup;

import lombok.NoArgsConstructor;
import org.apache.ibatis.session.RowBounds;
import org.openlmis.core.service.ConfigurationSettingService;
import org.openlmis.core.service.ProcessingPeriodService;
import org.openlmis.core.service.ProgramService;
import org.openlmis.report.mapper.lookup.OrderRequisitionMapper;
import org.openlmis.report.model.ReportData;
import org.openlmis.report.model.params.OrderRequisitionParam;
import org.openlmis.report.service.ReportDataProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
@NoArgsConstructor
public class OrderRequisitionDataProvider extends ReportDataProvider {
    @Autowired
    OrderRequisitionMapper mapper;

    private OrderRequisitionParam requisitionParam = null;

    @Autowired
    private ProgramService programService;

    @Autowired
    private ProcessingPeriodService processingPeriodService;
    @Autowired
    ConfigurationSettingService configurationService;


    @Autowired
    public OrderRequisitionDataProvider(OrderRequisitionMapper mapper) {
        this.mapper = mapper;
    }


    @Override
    protected List<? extends ReportData> getResultSetReportData(Map<String, String[]> filterCriteria) {
        return getMainReportData(filterCriteria, null, RowBounds.NO_ROW_OFFSET, RowBounds.NO_ROW_LIMIT);
    }

    @Override
    public List<? extends ReportData> getMainReportData(Map<String, String[]> filterCriteria, Map<String, String[]> SortCriteria, int page, int pageSize) {
        RowBounds rowBounds = new RowBounds((page - 1) * pageSize, pageSize);
        return mapper.getReport(getReportFilterData(filterCriteria),SortCriteria, rowBounds);
    }

    public OrderRequisitionParam getReportFilterData(Map<String, String[]> filterCriteria) {

        if (filterCriteria != null) {
            requisitionParam = new OrderRequisitionParam();
            requisitionParam.setId((long) (filterCriteria.get("id") == null ? 0 : Long.parseLong(filterCriteria.get("id")[0])));
        }
        return requisitionParam;
    }

    @Override
    public String getFilterSummary(Map<String, String[]> params) {
        return getReportFilterData(params).toString();

    }
    @Override
    public HashMap<String, String> getAdditionalReportData(Map params) {
        HashMap<String, String> result = new HashMap<String, String>();
        result.put("CUSTOM_REPORT_TITLE", configurationService.getConfigurationStringValue("VACCINE_ORDER_REQUISITION_TITLE"));
         Date date = new Date();
        result.put("CURRENT_DATE", new SimpleDateFormat("dd-MM-yy hh:mm a").format(date));

        return result;

    }



}
