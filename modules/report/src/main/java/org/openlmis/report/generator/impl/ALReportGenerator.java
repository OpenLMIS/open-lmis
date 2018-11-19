package org.openlmis.report.generator.impl;

import org.openlmis.core.utils.DateUtil;
import org.openlmis.report.generator.AbstractReportModelGenerator;
import org.openlmis.rnr.domain.Rnr;
import org.openlmis.rnr.service.RequisitionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component(value = "alReport")
public class ALReportGenerator extends AbstractReportModelGenerator {

    private final static String KEY_QUERY_RESULT = "KEY_QUERY_RESULT";

    @Autowired
    private RequisitionService requisitionService;

    @Override
    protected Map<String, Object> getQueryResult(Map<Object, Object> paraMap) {

        Date start = DateUtil.parseDate(paraMap.get("startTime").toString());
        Date end = DateUtil.parseDate(paraMap.get("endTime").toString());
        List<Rnr> rnrs = new ArrayList<>();
        if (null != paraMap.get("provinceId") && null == paraMap.get("districtId")) {
            rnrs = requisitionService.alReportRequisitionsByZoneIdAndDate(
                    Integer.parseInt(paraMap.get("provinceId").toString()), start, end);
        }else if (null != paraMap.get("districtId") && null == paraMap.get("facilityId")) {
            rnrs = requisitionService.alReportRequisitionsByZoneIdAndDate(
                    Integer.parseInt(paraMap.get("districtId").toString()), start, end);
        } else if(null != paraMap.get("facilityId")) {
            rnrs = requisitionService.alReportRequisitionsByFacilityId(
                    Integer.parseInt(paraMap.get("facilityId").toString()), start, end);
        } else {
            rnrs = requisitionService.alReportRequisitionsByStartAndEndDate(start, end);
        }
        Map<String, Object> result = new HashMap<>();
        result.put(KEY_QUERY_RESULT, rnrs);
        return result;
    }

    @Override
    protected Object getReportHeaders(Map<Object, Object> paraMap, Map<String, Object> queryResult) {
        return null;
    }

    @Override
    protected Object getReportContent(Map<Object, Object> paraMap, Map<String, Object> queryResult) {
        return null;
    }

    @Override
    protected List<Map<String, String>> getReportMergedRegions(Map<Object, Object> paraMap, Map<String, Object> queryResult) {
        return null;
    }

    @Override
    protected Object reportDataForFrontEnd(Map<Object, Object> paraMap) {
        return getQueryResult(paraMap).get(KEY_QUERY_RESULT);
    }
}
