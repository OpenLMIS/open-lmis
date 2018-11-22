package org.openlmis.report.generator.impl;

import lombok.Getter;
import lombok.Setter;
import org.openlmis.core.utils.DateUtil;
import org.openlmis.report.generator.AbstractReportModelGenerator;
import org.openlmis.rnr.domain.RegimenLineItem;
import org.openlmis.rnr.domain.Rnr;
import org.openlmis.rnr.service.RequisitionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Component(value = "alReport")
public class ALReportGenerator extends AbstractReportModelGenerator {

    private final static String KEY_QUERY_RESULT = "KEY_QUERY_RESULT";

    private final static String KEY_NO_DATA = "KEY_NO_DATA";

    private final static String[] ITEMS = {"Consultas AL US/APE Malaria 1x6",
            "Consultas AL STOCK Malaria 1x6",
            "Consultas AL US/APE Malaria 2x6",
            "Consultas AL STOCK Malaria 2x6",
            "Consultas AL US/APE Malaria 3x6",
            "Consultas AL STOCK Malaria 3x6",
            "Consultas AL US/APE Malaria 4x6",
            "Consultas AL STOCK Malaria 4x6"};

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
        } else if (null != paraMap.get("districtId") && null == paraMap.get("facilityId")) {
            rnrs = requisitionService.alReportRequisitionsByZoneIdAndDate(
                    Integer.parseInt(paraMap.get("districtId").toString()), start, end);
        } else if (null != paraMap.get("facilityId")) {
            rnrs = requisitionService.alReportRequisitionsByFacilityId(
                    Integer.parseInt(paraMap.get("facilityId").toString()), start, end);
        } else {
            rnrs = requisitionService.alReportRequisitionsByStartAndEndDate(start, end);
        }
        Map<String, Object> result = new HashMap<>();
        Map<String, AlRegimenStat> map = stat(rnrs);
        if (map.size() == 0) {
            paraMap.put(KEY_NO_DATA, true);
        }
        result.put(KEY_QUERY_RESULT, map);
        return result;
    }

    private Map<String, AlRegimenStat> stat(List<Rnr> rnrs) {
        Map<String, AlRegimenStat> map = new HashMap<>();
        for (Rnr rnr : rnrs) {
            List<RegimenLineItem> list = rnr.getRegimenLineItems();
            for (RegimenLineItem regimenLineItem : list) {
                if (!map.containsKey(regimenLineItem.getName())) {
                    AlRegimenStat stat = new AlRegimenStat();
                    map.put(regimenLineItem.getName(), stat);
                }
                AlRegimenStat alRegimenStat = map.get(regimenLineItem.getName());
                if (null != regimenLineItem.getChw()) {
                    alRegimenStat.setChw(alRegimenStat.getChw() + regimenLineItem.getChw());
                }
                if (null != regimenLineItem.getHf()) {
                    alRegimenStat.setHf(alRegimenStat.getHf() + regimenLineItem.getHf());
                }
            }
        }
        return map;
    }

    @Override
    protected Object getReportHeaders(Map<Object, Object> paraMap, Map<String, Object> queryResult) {
        Map<String, String> headers = new LinkedHashMap<>();
        headers.put("blank", "");
        headers.put("6x1 treatments", getMessage("report.header.al.treatments"));
        headers.put("6x1 stock", getMessage("report.header.al.exist.stock"));
        headers.put("6x2 treatments", getMessage("report.header.al.treatments"));
        headers.put("6x2 stock", getMessage("report.header.al.exist.stock"));
        headers.put("6x3 treatments", getMessage("report.header.al.treatments"));
        headers.put("6x3 stock", getMessage("report.header.al.exist.stock"));
        headers.put("6x4 treatments", getMessage("report.header.al.treatments"));
        headers.put("6x4 stock", getMessage("report.header.al.exist.stock"));
        return headers;
    }

    @Override
    protected Object getReportContent(Map<Object, Object> paraMap, Map<String, Object> queryResult) {
        Map<String, AlRegimenStat> map = (Map<String, AlRegimenStat>) queryResult.get(KEY_QUERY_RESULT);
        List<List<String>> list = initReportContentList();
        for (String item : ITEMS) {
            fillReportContent(list, map.get(item));
        }

        return list;
    }

    private void fillReportContent(List<List<String>> list, AlRegimenStat alRegimenStat) {
        if (null != alRegimenStat) {
            list.get(0).add(String.valueOf(alRegimenStat.getHf()));
            list.get(1).add(String.valueOf(alRegimenStat.getChw()));
            list.get(2).add(String.valueOf(alRegimenStat.getTotal()));
        } else {
            list.get(0).add(String.valueOf(""));
            list.get(1).add(String.valueOf(""));
            list.get(2).add(String.valueOf(""));
        }
    }

    private List<List<String>> initReportContentList() {
        List<List<String>> list = new ArrayList<>();
        list.add(new ArrayList<String>());
        list.add(new ArrayList<String>());
        list.add(new ArrayList<String>());
        list.get(0).add("HF");
        list.get(1).add("CHW");
        list.get(2).add("TOTAL");
        return list;
    }

    @Override
    protected List<Map<String, String>> getReportMergedRegions(Map<Object, Object> paraMap, Map<String, Object> queryResult) {

        List<Map<String, String>> mergedRegions = new ArrayList<>();
        mergedRegions.add(createMergedRegion("2", "3", "0", "0", ""));
        for (int i = 1; i < 9; i += 2) {
            mergedRegions.add(createMergedRegion("2",
                    "2", String.valueOf(i), String.valueOf(i + 1), ""));
        }
        return mergedRegions;
    }

    @Override
    protected Object getReportTitle(Map<Object, Object> paraMap) {
        List<List<String>> title = new ArrayList<>();

        title.add(regionTitle(paraMap));
        title.add(generationDate(paraMap));
        title.add(title());
        return title;
    }

    private List<String> regionTitle(Map<Object, Object> paraMap) {
        List<String> list = new ArrayList<>();
        list.add(getMessage("report.header.province"));
        list.add(null != paraMap.get("province") ? paraMap.get("province").toString() : "");
        list.add(getMessage("report.header.district"));
        list.add(null != paraMap.get("district") ? paraMap.get("district").toString() : "");
        list.add(getMessage("report.header.facility"));
        list.add(null != paraMap.get("facility") ? paraMap.get("facility").toString() : "");
        return list;
    }

    private List<String> title() {
        List<String> list = new ArrayList<>();
        list.add("");
        for (int i = 1; i < 5; ++i) {
            list.add("6x" + i);
            list.add("6x" + i);
        }
        return list;
    }

    private List<String> generationDate(Map<Object, Object> paraMap) {
        List<String> generationDate = new ArrayList<>();
        generationDate.add(getMessage("report.header.generated.for"));

        generationDate.add(DateUtil.transform(paraMap.get("startTime").toString(), DateUtil.FORMAT_DATE_TIME, "dd/MM/yyyy") + "-" +
                DateUtil.transform(paraMap.get("endTime").toString(), DateUtil.FORMAT_DATE_TIME, "dd/MM/yyyy"));
        return generationDate;
    }

    @Override
    protected Object reportDataForFrontEnd(Map<Object, Object> paraMap) {
        Map<String, Object> map = generate(paraMap);
        if (paraMap.containsKey(KEY_NO_DATA)) {
            return new ArrayList<>();
        }
        return map.get("KEY_EXCEL_CONTENT");
    }

    @Setter
    @Getter
    class AlRegimenStat {

        private int hf;
        private int chw;

        public int getTotal() {
            return hf + chw;
        }
    }
}