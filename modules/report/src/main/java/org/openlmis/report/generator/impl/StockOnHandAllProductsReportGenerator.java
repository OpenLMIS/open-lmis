package org.openlmis.report.generator.impl;

import org.openlmis.core.utils.DateUtil;
import org.openlmis.report.model.dto.LotInfo;
import org.openlmis.report.model.dto.StockProductDto;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Component(value = "stockOnHandAll")
public class StockOnHandAllProductsReportGenerator extends StockOnHandForSingleProductReportGenerator {

    @Override
    protected Object getReportHeaders(Map<Object, Object> paraMap, Map<String, Object> queryResult) {
        Map<String, String> headers = new LinkedHashMap<>();
        headers.put("province", getMessage("report.header.province"));
        headers.put("district", getMessage("report.header.district"));
        headers.put("facility", getMessage("report.header.facility"));
        headers.put("drugName", getMessage("report.header.drug.name"));
        headers.put("lot", getMessage("report.header.lot"));
        headers.put("sohOfLot", getMessage("report.header.sohoflot"));
        headers.put("totalSoh", getMessage("report.header.sohoflot.total"));
        headers.put("status", getMessage("report.header.status"));
        headers.put("expiryDate", getMessage("report.soonest.expiry.date"));
        headers.put("MoS", getMessage("report.estimated.consumption.month"));
        headers.put("cmm", getMessage("report.header.cmm"));
        return headers;
    }

    @Override
    protected Object getReportContent(Map<Object, Object> paraMap, Map<String, Object> queryResult) {
        List<StockProductDto> stockProductDtoList = (List<StockProductDto>) queryResult.get(KEY_QUERY_RESULT);
        List<Map<String, Object>> content = new ArrayList<>();
        for (StockProductDto dto : stockProductDtoList) {
            Date date = earliestExpiryDate(dto.getLotList());
            for (LotInfo lotinfo : dto.getLotList()) {
                Map<String, Object> rowMap = new HashMap<>();
                rowMap.put("province", dto.getProvinceName());
                rowMap.put("district", dto.getDistrictName());
                rowMap.put("facility", dto.getFacilityName());
                rowMap.put("drugName", dto.getProductName());
                rowMap.put("lot", lotinfo.getLotNumber());
                rowMap.put("sohOfLot", lotinfo.getStockOnHandOfLot());
                rowMap.put("totalSoh", dto.getSumStockOnHand());
                rowMap.put("status", getMessage(dto.getStockOnHandStatus().getMessageKey()));

                Map<String, Object> tmpValue = new HashMap<>();
                tmpValue.put("value", DateUtil.formatDate(date));
                Map<String, Object> styleMap = new HashMap<>();
                styleMap.put("dataPattern", DateUtil.FORMAT_DATE_TIME);
                styleMap.put("excelDataPattern", "d/m/yy");
                tmpValue.put("style", styleMap);
                tmpValue.put("dataType", "date");
                rowMap.put("expiryDate", tmpValue);

                rowMap.put("MoS", getFormatDoubleValue(dto.getMos()));
                rowMap.put("cmm", getFormatDoubleValue(dto.getCmm()));

                content.add(rowMap);
            }
        }

        return content;
    }

    @Override
    protected List<Map<String, String>> getReportMergedRegions(Map<Object, Object> paraMap,
                                                               Map<String, Object> queryResult) {
        List<StockProductDto> stockProductDtoList = (List<StockProductDto>) queryResult.get(KEY_QUERY_RESULT);
        List<Map<String, String>> mergedRegions = new ArrayList<>();
        int index = 1;
        for (StockProductDto dto : stockProductDtoList) {
            int cmmSpan = dto.getLotList().size();
            mergedRegions.add(createMergedRegion(String.valueOf(index + 1), String.valueOf(index + cmmSpan),
                    "6", "6", dto.getSumStockOnHand().toString()));
            mergedRegions.add(createMergedRegion(String.valueOf(index + 1), String.valueOf(index + cmmSpan),
                    "7", "7", getMessage(dto.getStockOnHandStatus().getMessageKey())));
            mergedRegions.add(createMergedRegion(String.valueOf(index + 1), String.valueOf(index + cmmSpan),
                    "8", "8", earliestExpiryDate(dto.getLotList()).toString()));
            mergedRegions.add(createMergedRegion(String.valueOf(index + 1), String.valueOf(index + cmmSpan),
                    "9", "9", getFormatDoubleValue(dto.getMos())));
            mergedRegions.add(createMergedRegion(String.valueOf(index + 1), String.valueOf(index + cmmSpan),
                    "10", "10", getFormatDoubleValue(dto.getCmm())));
            index = index + cmmSpan;
        }
        return mergedRegions;
    }
}
