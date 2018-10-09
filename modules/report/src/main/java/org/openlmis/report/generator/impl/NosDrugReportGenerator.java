package org.openlmis.report.generator.impl;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.openlmis.core.utils.DateUtil;
import org.openlmis.report.generator.AbstractReportModelGenerator;
import org.openlmis.report.generator.StockOnHandStatus;
import org.openlmis.report.view.WorkbookCreator;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

@Component
public class NosDrugReportGenerator extends AbstractReportModelGenerator {

    private final static String CMM_ENTRIES_CUBE = "vw_cmm_entries";

    private final static String WEEKLY_NOS_SOH_CUBE = "vw_weekly_nos_soh";

    private final static String UNIQUE_SORTED_DATES = "unique_sorted_dates";

    private final static String STARTTIME_TO_ENDTIME = "starttime_to_endtime";

    @Override
    protected Map<String, Object> getCubeQueryResult(Map<Object, Object> paraMap) {
        String queryUriDrugs = getBaseFactUri(WEEKLY_NOS_SOH_CUBE);
        String queryStringDrugs = getQueryStringDrugs(paraMap);
        ResponseEntity responseEntityDrugs = super.cubesReportProxy.redirect(queryUriDrugs, queryStringDrugs);
        String bodyDrugs = responseEntityDrugs.getBody().toString();

        String queryUriCmmEntries = getBaseFactUri(CMM_ENTRIES_CUBE);
        String queryStringCmmEntries = getQueryStringCmmEntries(paraMap);
        ResponseEntity responseEntityCmmEntries = super.cubesReportProxy.redirect(queryUriCmmEntries, queryStringCmmEntries);
        String bodyCmmEntries = responseEntityCmmEntries.getBody().toString();

        Map<String, Object> cubeQueryResult = new HashMap<>();
        cubeQueryResult.put(WEEKLY_NOS_SOH_CUBE, jsonToListMap(bodyDrugs));
        cubeQueryResult.put(CMM_ENTRIES_CUBE, jsonToListMap(bodyCmmEntries));
        Set<String> set = getUniqSortedDates(cubeQueryResult);
        cubeQueryResult.put(UNIQUE_SORTED_DATES, set);

        String startTime = paraMap.get("startTime").toString();
        String endTime = paraMap.get("endTime").toString();

        cubeQueryResult.put(STARTTIME_TO_ENDTIME,
                DateUtil.transform(startTime, DateUtil.FORMAT_DATE_TIME_CUBE, DateUtil.FORMAT_DATE_DD_MM_YYYY)
                        + " - " + DateUtil.transform(endTime, DateUtil.FORMAT_DATE_TIME_CUBE, DateUtil.FORMAT_DATE_DD_MM_YYYY)
                );

        return cubeQueryResult;
    }

    @Override
    protected Object getReportHeaders(Map<Object, Object> paraMap, Map<String, Object> cubeQueryResult) {
        Map<String, String> headers = new LinkedHashMap<>();
        headers.put("drugCode", getMessage("report.header.drug.code"));
        headers.put("area", getMessage("report.header.area"));
        headers.put("subArea", getMessage("report.header.subarea"));
        headers.put("drugName", getMessage("report.header.drug.name"));
        headers.put("province", getMessage("report.header.province"));
        headers.put("district", getMessage("report.header.district"));
        headers.put("facility", getMessage("report.header.facility"));
        headers.put("cmmValue", getMessage("report.header.cmm"));
        headers.put("reportGeneratedFor", getMessage("report.header.generated.for"));

        Set<String> set = (Set<String>) cubeQueryResult.get(UNIQUE_SORTED_DATES);
        if (CollectionUtils.isNotEmpty(set)) {
            for (String date : set) {
                headers.put(date, DateUtil.getFormattedDate(DateUtil.parseDate(date, DateUtil.FORMAT_DATE),
                        DateUtil.FORMAT_DATE_TIME_DAY_MONTH_YEAR));
            }
        }

        headers.put("LatestStockStatus", getMessage("report.header.latest.stock.status"));

        return headers;
    }

    @Override
    protected Object getReportContent(Map<Object, Object> paraMap, Map<String, Object> cubeQueryResult) {
        return getNosDrugHash(cubeQueryResult);
    }

    @Override
    protected Object getReportLegenda(Map<Object, Object> paraMap, Map<String, Object> cubeQueryResult,
                                      Map<String, Object> model) {
        Map<String,String> content = (Map<String,String>)model.get(WorkbookCreator.getKEY_EXCEL_HEADERS());
        List<List<Map<String, Object>>> legenda = new ArrayList<>();
        int count = content.size();

        //legenda text
        List<Map<String, Object>> legendaText = new ArrayList<>();
        legendaText.add(createCellWithIndex("legenda", count));
        legenda.add(legendaText);

        legenda.add(createStockRowList(StockOnHandStatus.STOCK_OUT, count));
        legenda.add(createStockRowList(StockOnHandStatus.REGULAR_STOCK, count));
        legenda.add(createStockRowList(StockOnHandStatus.LOW_STOCK, count));
        legenda.add(createStockRowList(StockOnHandStatus.OVER_STOCK, count));

        return legenda;
    }

    private List<Map<String, Object>> createStockRowList(StockOnHandStatus stockOnHandStatus, int count) {
        List<Map<String, Object>> row = new ArrayList<>();
        Map<String, Object> map = createCellWithIndex("", count);
        Map<String, Object> styleMap = new HashMap<>();
        styleMap.put("color", stockOnHandStatus.getColorIndex());
        map.put("style", styleMap);
        row.add(map);

        row.add(createCellWithIndex(getMessage(stockOnHandStatus.getMessageKey()), count + 1));
        return row;
    }

    private Map<String, Object> createCellWithIndex(String value, int cellIndex) {
        Map<String, Object> map = new HashMap<>();
        map.put("value", value);
        map.put("cellIndex", cellIndex);
        return map;
    }

    private String getQueryStringCmmEntries(Map<Object, Object> paraMap) {
        String endTime = paraMap.get("endTime").toString();
        String startDatePeriod = DateUtil.formatDateWithStartDayOfPeriod(endTime);
        String endDatePeriod = DateUtil.formatDateWithEndDayOfPeriod(endTime);
        List<String> drugs = validateDrugs(paraMap) ? (List<String>) paraMap.get("selectedDrugs")
                : (List<String>) paraMap.get("allNosDrugs");
        Map<String, Object> cutsParams = new HashMap<>();
        cutsParams.put("product", drugs);
        cutsParams.put("periodbegin", startDatePeriod);
        cutsParams.put("periodend", endDatePeriod);

        String location = getLocationHierarchy(getProvince(paraMap), getDistrict(paraMap));
        if (StringUtils.isNotEmpty(location)) {
            cutsParams.put("location", location);
        }
        String cutStr = "?cut=" + mapToQueryString(cutsParams);
        return cutStr;
    }

    private boolean validateDrugs(Map<Object, Object> paraMap) {
        if (null != paraMap.get("selectedDrugs")) {
            List<String> drugs = (List<String>) paraMap.get("selectedDrugs");
            if (drugs.size() > 0) {
                return true;
            }
        }
        return false;
    }


    private String getQueryStringDrugs(Map<Object, Object> paraMap) {
        String startTime = paraMap.get("startTime").toString();
        String endTime = paraMap.get("endTime").toString();
        List<String> selectedDrugs = validateDrugs(paraMap) ? (List<String>) paraMap.get("selectedDrugs") : null;
        String province = getProvince(paraMap);
        String district = getDistrict(paraMap);

        Map<String, Object> cutsParams = generateCutsParams("cutDate",
                startTime, endTime, null, selectedDrugs, province, district);
        String cutStr = "?cut=" + mapToQueryString(cutsParams);
        String fieldsStr = prepareDrugsParams();
        return cutStr + "&" + fieldsStr;
    }

    private String prepareDrugsParams() {
        /*var params = [{
            name: 'fields',
                    value: ['location.province_name', 'location.district_name', 'facility.facility_code',
                    'facility.facility_name', 'drug.drug_name', 'drug.drug_code', 'date', 'soh',
                    'area.area_name', 'area.sub_area_name']
        }];*/
        List<String> fields = new ArrayList<>();
        fields.add("location.province_name");
        fields.add("location.district_name");
        fields.add("facility.facility_code");
        fields.add("facility.facility_name");
        fields.add("drug.drug_name");
        fields.add("drug.drug_code");
        fields.add("date");
        fields.add("soh");
        fields.add("area.area_name");
        fields.add("area.sub_area_name");
        return "fields=" + StringUtils.join(fields, ",");
    }

    protected Map<String, Object> generateCutsParams(String timeDimension, String startTime, String endTime, String facilityCode,
                                                     List<String> selectedDrugs, String province, String district) {
        Map<String, Object> cutsParams = new HashMap<>();

        if (StringUtils.isNotEmpty(timeDimension) && (StringUtils.isNotEmpty(startTime) || StringUtils.isNotEmpty(endTime))) {
            StringBuilder timeSpan = new StringBuilder();
            if (StringUtils.isNotEmpty(startTime)) {
                timeSpan.append(startTime);
            }
            timeSpan.append("-");
            if (StringUtils.isNotEmpty(endTime)) {
                timeSpan.append(endTime);
            }
            cutsParams.put(timeDimension, timeSpan.toString());
        }

        if (StringUtils.isNotEmpty(facilityCode)) {
            cutsParams.put("facility", facilityCode);
        }

        if (CollectionUtils.isNotEmpty(selectedDrugs)) {
            cutsParams.put("drug", selectedDrugs);
        }

        if (isOneDistrict(province, district)) {
            List<String> values = new ArrayList<>();
            values.add(province + "," + district);
            cutsParams.put("location", values);

        } else if (isOneProvince(province, district)) {
            List<String> values = new ArrayList<>();
            values.add(province);
            cutsParams.put("location", values);
        }

        return cutsParams;
    }

    private Set<String> getUniqSortedDates(Map<String, Object> cubeQueryResult) {
        List<Map<String, String>> drugs = (List<Map<String, String>>) cubeQueryResult.get(WEEKLY_NOS_SOH_CUBE);
        if (CollectionUtils.isNotEmpty(drugs)) {
            Set<String> set = new TreeSet<>(new Comparator<String>() {
                @Override
                public int compare(String o1, String o2) {
                    if (DateUtil.parseDate(o1, DateUtil.FORMAT_DATE).getTime() >
                            DateUtil.parseDate(o2, DateUtil.FORMAT_DATE).getTime()) {
                        return 1;
                    }
                    return -1;
                }
            });
            for (Map<String, String> map : drugs) {
                String date = map.get("date");
                set.add(date);
            }

            return set;
        }
        return new HashSet<>();
    }

    protected Map<String, List<Map<String, String>>> groupByKey(List<Map<String, String>> listMap, String... keyList) {
        Map<String, List<Map<String, String>>> result = new LinkedHashMap<>();

        for (Map<String, String> map : listMap) {
            String key = getCombinationKey(map, keyList);
            if (result.containsKey(key)) {
                result.get(key).add(map);
            } else {
                List<Map<String, String>> list = new ArrayList<>();
                list.add(map);
                result.put(key, list);
            }
        }

        return result;
    }

    protected String getCombinationKey(Map<String, String> map, String... keyList) {
        StringBuilder sb = new StringBuilder();
        for (String key : keyList) {
            sb.append(map.get(key));
        }
        return sb.toString();
    }

    private List<Map<String, Object>> getNosDrugHash(Map<String, Object> cubeQueryResult) {

        List<Map<String, String>> drugs = (List<Map<String, String>>) cubeQueryResult.get(WEEKLY_NOS_SOH_CUBE);
        Map<String, List<Map<String, String>>> nosDrugs = groupByKey(drugs, "drug.drug_code", "facility.facility_code");

        Map<String, Map<String, String>> nosDrugHash = new LinkedHashMap<>();
        Set<String> set = (Set<String>) cubeQueryResult.get(UNIQUE_SORTED_DATES);

        for (Map.Entry<String, List<Map<String, String>>> entry : nosDrugs.entrySet()) {
            List<Map<String, String>> nosDrugInFacility = entry.getValue();
            Map<String, String> first = nosDrugInFacility.get(0);

            Map<String, String> newNosDrug = new HashMap<>();
            newNosDrug.put("drugCode", first.get("drug.drug_code"));
            newNosDrug.put("area", first.get("area.area_name"));
            newNosDrug.put("subArea", first.get("area.sub_area_name"));
            newNosDrug.put("drugName", first.get("drug.drug_name"));
            newNosDrug.put("province", first.get("location.province_name"));
            newNosDrug.put("district", first.get("location.district_name"));
            newNosDrug.put("facility", first.get("facility.facility_name"));
            newNosDrug.put("reportGeneratedFor", cubeQueryResult.get(STARTTIME_TO_ENDTIME).toString());

            for (String date : set) {
                newNosDrug.put(date, "N/A");
                for (Map<String, String> nosDrug : nosDrugInFacility) {
                    if (StringUtils.equalsIgnoreCase(nosDrug.get("date"), date)) {
                        newNosDrug.put(date, nosDrug.get("soh"));
                    }
                }
            }

            nosDrugHash.put(first.get("drug.drug_code") + "@" + first.get("facility.facility_code"), newNosDrug);
        }

        List<Map<String, String>> cmmEntries = (List<Map<String, String>>) cubeQueryResult.get(CMM_ENTRIES_CUBE);

        for (Map<String, String> cmmEntry : cmmEntries) {
            if (StringUtils.isNotEmpty(cmmEntry.get("cmm"))
                    && nosDrugHash.containsKey(cmmEntry.get("product") + "@" + cmmEntry.get("facilityCode"))) {
                nosDrugHash.get(cmmEntry.get("product") + "@" + cmmEntry.get("facilityCode")).put("cmmValue", cmmEntry.get("cmm"));
            }
        }

        List<Map<String, Object>> result = new ArrayList<>();
        Set<String> checkSet = new HashSet<>(set);
        for (Map.Entry<String, Map<String, String>> entry : nosDrugHash.entrySet()) {
            long cmm = getCmmValue(entry.getValue());
            Map<String, Object> obj = new HashMap<>();
            for (Map.Entry<String, String> kv : entry.getValue().entrySet()) {
                if (checkSet.contains(kv.getKey())) {
                    Map<String, Object> tmpValue = new HashMap<>();
                    tmpValue.put("value", kv.getValue());
                    Map<String, Object> styleMap = new HashMap<>();
                    styleMap.put("color", stockOnHandStatus(cmm,
                            NumberUtils.toLong(kv.getValue())).getColorIndex());
                    tmpValue.put("style", styleMap);
                    obj.put(kv.getKey(), tmpValue);
                } else {
                    obj.put(kv.getKey(), kv.getValue());
                }
            }
            String latestDate = set.toArray()[set.size() - 1].toString();
            obj.put("LatestStockStatus", getMessage(stockOnHandStatus(cmm,
                    NumberUtils.toLong(entry.getValue().get(latestDate))).getMessageKey()));

            result.add(obj);
        }

        return result;
    }



    /*
        $scope.cmmStatus = function (entry) {
        var cmm = entry.cmm;
        var soh = entry.soh;
        if (soh === 0) {
            return CMM_STATUS.stockOut;
        }
        if (cmm == -1) {
            return CMM_STATUS.regularStock;
        }

        if (soh < 0.05 * cmm) {//low stock
            return CMM_STATUS.lowStock;
        }
        else if (soh > 2 * cmm) {//over stock
            return CMM_STATUS.overStock;
        } else {
            return CMM_STATUS.regularStock;
        }
    };
     */

    private long getCmmValue(Map<String, String> row) {
        return StringUtils.isNotEmpty(row.get("cmmValue")) ? NumberUtils.toLong(row.get("cmmValue")): -1;
    }

    private StockOnHandStatus stockOnHandStatus(long cmm, long soh) {
        if (0 == soh) {
            return StockOnHandStatus.STOCK_OUT;
        }
        if (cmm == -1) {
            return StockOnHandStatus.REGULAR_STOCK;
        }

        if (soh < 0.05 * cmm) {
            return StockOnHandStatus.LOW_STOCK;
        } else if (soh > 2 * cmm) {
            return StockOnHandStatus.OVER_STOCK;
        }
        return StockOnHandStatus.REGULAR_STOCK;
    }
}
