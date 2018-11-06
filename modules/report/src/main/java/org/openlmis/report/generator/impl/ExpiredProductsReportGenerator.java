package org.openlmis.report.generator.impl;

import org.apache.commons.lang.StringUtils;
import org.openlmis.core.utils.DateUtil;
import org.openlmis.report.generator.AbstractReportModelGenerator;
import org.openlmis.report.model.dto.LotInfo;
import org.openlmis.report.model.dto.StockProductDto;
import org.openlmis.report.model.params.StockReportParam;
import org.openlmis.report.service.SimpleTableService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Component(value = "expiredProductsReport")
public class ExpiredProductsReportGenerator extends AbstractReportModelGenerator {

    private final static String KEY_QUERY_RESULT = "KEY_QUERY_RESULT";

    private final static String FACILITY_PRICE_COLUMN = "8";

    private final static String DISTRICT_PRICE_COLUMN = "7";

    private final static String CMM_COLUMN = "9";

    private final static String MOS_COLUMN = "10";

    @Autowired
    private SimpleTableService simpleTableService;

    @Override
    protected Object getReportTitle(Map<Object, Object> paraMap) {
        List<List<String>> title = new ArrayList<>();
        if(null != paraMap.get("endTime")) {
            List<String> generationDate = new ArrayList<>();
            generationDate.add(getMessage("report.header.generated.for"));

            generationDate.add(formatDate(paraMap.get("endTime").toString()));

            title.add(generationDate);
        }
        return title;
    }

    private String formatDate(String endTime) {
        Date date = new SimpleDateFormat("yyyy-mm-dd").parse(endTime, new ParsePosition(0));
        String result = new SimpleDateFormat("dd/mm/yyyy").format(date.getTime());
        return result;
    }

    @Override
    protected Object getReportHeaders(Map<Object, Object> paraMap, Map<String, Object> queryResult) {
        Map<String, String> headers = new LinkedHashMap<>();
        headers.put("province", getMessage("report.header.province"));
        headers.put("district", getMessage("report.header.district"));
        if(null != paraMap.get("districtId")){
            headers.put("facility", getMessage("report.header.facility"));
        }
        headers.put("drugCode", getMessage("report.header.drug.code"));
        headers.put("drugName", getMessage("report.header.drug.name"));
        headers.put("lot", getMessage("report.header.lot"));
        headers.put("expiryDate", getMessage("report.header.expiry.date"));
        headers.put("soh", getMessage("report.header.stock.on.hand"));
        headers.put("price", getMessage("report.header.price"));
        if(null != paraMap.get("districtId")){
            headers.put("cmm", getMessage("report.header.cmm"));
            headers.put("MoS", getMessage("report.header.MoS"));
        }
        return headers;
    }

    @Override
    protected Object getReportContent(Map<Object, Object> paraMap, Map<String, Object> queryResult) {
        List<StockProductDto> stockProductDtoList = (List<StockProductDto>) queryResult.get(KEY_QUERY_RESULT);
        List<Map<String, Object>> content = new ArrayList<>();
        for (StockProductDto dto : stockProductDtoList) {
            for (LotInfo lotinfo : dto.getLotList()) {
                Map<String, Object> rowMap = new HashMap<>();
                rowMap.put("province", dto.getProvinceName());
                rowMap.put("district", dto.getDistrictName());
                rowMap.put("drugCode", dto.getProductCode());
                rowMap.put("drugName", dto.getProductName());
                rowMap.put("lot", lotinfo.getLotNumber());

                Map<String, Object> tmpValue = new HashMap<>();
                tmpValue.put("value", DateUtil.formatDate(lotinfo.getExpiryDate()));
                Map<String, Object> styleMap = new HashMap<>();
                styleMap.put("dataPattern", DateUtil.FORMAT_DATE_TIME);
                styleMap.put("excelDataPattern", "d/m/yy");
                tmpValue.put("style", styleMap);
                tmpValue.put("dataType", "date");
                rowMap.put("expiryDate", tmpValue);

                rowMap.put("soh", lotinfo.getStockOnHandOfLot().toString());
                rowMap.put("price", dto.getPrice());
                if(null != paraMap.get("districtId")){
                    rowMap.put("facility", dto.getFacilityName());
                    rowMap.put("cmm", getFormatDoubleValue(dto.getCmm()));
                    rowMap.put("MoS", getFormatDoubleValue(dto.getMos()));
                }
                content.add(rowMap);
            }
        }

        return content;
    }

    @Override
    protected List<Map<String, String>> getReportMergedRegions(Map<Object, Object> paraMap, Map<String, Object> queryResult) {
        List<StockProductDto> stockProductDtoList = (List<StockProductDto>) queryResult.get(KEY_QUERY_RESULT);
        Map<String, String> mergedColumns = getMergedColumns(paraMap);

        return getMergedRegions(stockProductDtoList, mergedColumns);
    }

    private Map<String, String> getMergedColumns(Map<Object, Object> paraMap) {
        Map<String, String> mergedColumns = new HashMap<>();
        if(null == paraMap.get("districtId") || StringUtils.isBlank(paraMap.get("districtId").toString())) {
            mergedColumns.put("DISTRICT_PRICE_COLUMN", DISTRICT_PRICE_COLUMN);
        } else {
            mergedColumns.put("FACILITY_PRICE_COLUMN", FACILITY_PRICE_COLUMN);
            mergedColumns.put("CMM_COLUMN", CMM_COLUMN);
            mergedColumns.put("MOS_COLUMN", MOS_COLUMN);
        }

        return mergedColumns;
    }

    private List<Map<String, String>> getMergedRegions(List<StockProductDto> stockProductDtoList, Map<String, String> mergedColumns) {
        List<Map<String, String>> mergedRegions = new ArrayList<>();
        int index = 1;

        for (StockProductDto dto : stockProductDtoList) {
            int cmmSpan = dto.getLotList().size();
            for(Map.Entry<String, String> entry : mergedColumns.entrySet()) {
                mergedRegions.add(createMergedRegion(String.valueOf(index + 1), String.valueOf(index + cmmSpan), entry.getValue(), entry.getValue(), getFormatDoubleValue(dto.getCmm())));
            }
            index = index + cmmSpan;
        }

        return mergedRegions;
    }

    @Override
    protected Map<String, Object> getQueryResult(Map<Object, Object> paraMap) {
        StockReportParam filterCriteria = new StockReportParam();
        filterCriteria.setValue(paraMap);
        filterCriteria.setFilterCondition(new StockReportParam.FilterCondition() {
            @Override
            public String getCondition() {
                return "lots.expirationdate <= #{filterCriteria.endTime}";
            }
        });
        List<StockProductDto> stockProductDtoList = simpleTableService.getStockProductData(filterCriteria);

        Map<String, Object> result = new HashMap<>();
        result.put(KEY_QUERY_RESULT, stockProductDtoList);
        return result;
    }

    @Override
    protected Object reportDataForFrontEnd(Map<Object, Object> paraMap) {
        return getQueryResult(paraMap).get(KEY_QUERY_RESULT);
    }
}