package org.openlmis.report.generator.impl;

import org.openlmis.report.generator.AbstractReportModelGenerator;
import org.springframework.stereotype.Component;

import java.util.*;

@Component(value = "overStockProductReport")
public class OverStockProductReportGenerator extends AbstractReportModelGenerator {
    @Override
    protected Object getReportHeaders(Map<Object, Object> paraMap, Map<String, Object> cubeQueryResult) {
        Map<String, String> headers = new LinkedHashMap<>();
        headers.put("province", getMessage("report.header.province"));
        headers.put("district", getMessage("report.header.district"));
        headers.put("district", getMessage("report.header.district"));
        headers.put("facility", getMessage("report.header.facility"));
        headers.put("drugCode", getMessage("report.header.drug.code"));
        headers.put("drugName", getMessage("report.header.drug.name"));
        headers.put("lot", getMessage("report.header.lot"));
        headers.put("expiryDate", getMessage("report.header.expiry.date"));
        headers.put("soh", getMessage("report.header.stock.on.hand"));
        headers.put("cmm", getMessage("report.header.cmm"));
        headers.put("MoS", getMessage("report.header.MoS"));

        return headers;
    }

    @Override
    protected Object getReportContent(Map<Object, Object> paraMap, Map<String, Object> cubeQueryResult) {
        return null;
    }

    @Override
    protected List<Map<String, String>> getReportMergedRegions() {
        List<Map<String, String>> mergedRegions = new ArrayList<>();
        Map<String, String> mergedRegion  = new HashMap<>();
        mergedRegion.put("firstRow","1");
        mergedRegion.put("lastRow","2");
        mergedRegion.put("firstCol","3");
        mergedRegion.put("lastCol","4");
        mergedRegion.put("mergedValue","7");
        mergedRegions.add(mergedRegion);
        return null;
    }

    @Override
    protected Object reportDataForFrontEnd(Map<Object, Object> paraMap) {
        return null;
    }

}
