package org.openlmis.report.service;

import lombok.NoArgsConstructor;
import org.apache.ibatis.session.RowBounds;
import org.openlmis.core.domain.ProcessingPeriod;
import org.openlmis.core.repository.GeographicZoneRepository;
import org.openlmis.report.mapper.FillRateMapper;
import org.openlmis.report.mapper.OrderFillRateReportMapper;
import org.openlmis.report.model.ReportData;
import org.openlmis.report.util.StringHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
@NoArgsConstructor
public class PushedProductReportDataProvider extends ReportDataProvider {
    @Autowired
    private OrderFillRateReportMapper mapper;
    @Autowired
    private GeographicZoneRepository geographicZoneRepository;
    private Map params;

    @Override
    protected List<? extends ReportData> getResultSetReportData(Map<String, String[]> params) {
        return getMainReportData(params, null, RowBounds.NO_ROW_OFFSET, RowBounds.NO_ROW_LIMIT);
    }

    @Override
    public List<? extends ReportData> getMainReportData(Map<String, String[]> filterCriteria, Map<String, String[]> sorter, int page, int pageSize) {
        RowBounds rowBounds = new RowBounds((page - 1) * pageSize, pageSize);
        return mapper.getPushedProducts(filterCriteria, rowBounds,this.getUserId());
    }

    @Override
    public HashMap<String, String> getAdditionalReportData(Map params) {
        this.params = params;
        HashMap<String, String> result = new HashMap<String, String>();

        // Interprate the different reporting parameters that were selected on the UI
        String period = (StringHelper.isBlank(params, "period")) ? "" : ((String[]) params.get("period"))[0];
        String facility = (StringHelper.isBlank(params, "facility")) ? "" : ((String[]) params.get("facility"))[0];
        String facilityType = (StringHelper.isBlank(params, "facilityType")) ? "" : ((String[]) params.get("facilityType"))[0];
        String program = (StringHelper.isBlank(params, "program")) ? "" : ((String[]) params.get("program"))[0];

        String productCategory = (StringHelper.isBlank(params, "productCategory")) ? "" : ((String[]) params.get("productCategory"))[0];
        String product = (StringHelper.isBlank(params, "product")) ? "" : ((String[]) params.get("product"))[0];
        String zone = (StringHelper.isBlank(params, "zone")) ? "" : ((String[]) params.get("zone"))[0];

        // compose the filter text as would be presented on the pdf reports.
        String header = "";
        if (program != "" && !program.endsWith("undefined")) {
            header += "Program: " + this.mapper.getProgram(Integer.parseInt(program)).get(0).getName();
        }
        ProcessingPeriod periodObject = this.mapper.getPeriodId(Integer.parseInt(period));

        if (period != "" && !period.endsWith("undefined")) {
            header += "\nPeriod : " + periodObject.getName() + " - " + periodObject.getStringYear();
        }
        if (zone != "" && !zone.endsWith("undefined")) {
            header += "\nGeographic Zone: " + this.geographicZoneRepository.getById(Integer.parseInt(zone)).getName();
        }

        if (facility != "" && !facility.endsWith("undefined")) {
            header += "\nFacility Name: " + this.mapper.getFacility(Integer.parseInt(facility)).get(0).getName();
        }

        result.put("REPORT_FILTER_PARAM_VALUES", header.toString());
        return result;
    }

}
