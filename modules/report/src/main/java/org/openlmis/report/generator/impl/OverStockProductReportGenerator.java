package org.openlmis.report.generator.impl;

import org.apache.commons.lang.StringUtils;
import org.openlmis.core.utils.DateUtil;
import org.openlmis.report.generator.AbstractReportModelGenerator;
import org.openlmis.report.model.dto.LotInfo;
import org.openlmis.report.model.dto.OverStockProductDto;
import org.openlmis.report.model.params.OverStockReportParam;
import org.openlmis.report.service.SimpleTableService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.text.DecimalFormat;
import java.util.*;

@Component(value = "overStockProductReport")
public class OverStockProductReportGenerator extends AbstractReportModelGenerator {

    @Autowired
    private SimpleTableService simpleTableService;

    private final static String KEY_QUERY_RESULT = "KEY_QUERY_RESULT";

    private final static String CMM_COLUMN = "8";

    private final static String MOS_COLUMN = "9";

    @Override
    protected Map<String, Object> getQueryResult(Map<Object, Object> paraMap) {
        OverStockReportParam filterCriteria = new OverStockReportParam();
        filterCriteria.setEndTime(DateUtil.parseDate(paraMap.get("endTime").toString()));
        filterCriteria.setProvinceId(Integer.parseInt(paraMap.get("provinceId").toString()));
        filterCriteria.setDistrictId(Integer.parseInt(paraMap.get("districtId").toString()));
        filterCriteria.setFacilityId(Integer.parseInt(paraMap.get("facilityId").toString()));
        List<OverStockProductDto> overStockProductDtoList = simpleTableService.getOverStockProductReport(filterCriteria);

        Map<String, Object> result = new HashMap<>();
        result.put(KEY_QUERY_RESULT, overStockProductDtoList);
        return result;
    }

    @Override
    protected Object getReportHeaders(Map<Object, Object> paraMap, Map<String, Object> cubeQueryResult) {
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
        if(null != paraMap.get("districtId")){
            headers.put("cmm", getMessage("report.header.cmm"));
            headers.put("MoS", getMessage("report.header.MoS"));
        }
        return headers;
    }

    @Override
    protected Object getReportContent(Map<Object, Object> paraMap, Map<String, Object> queryResult) {

        List<OverStockProductDto> overStockProductDtoList = (List<OverStockProductDto>) queryResult.get(KEY_QUERY_RESULT);
        List<Map<String, String>> content = new ArrayList<>();
        for (OverStockProductDto dto : overStockProductDtoList) {
            for (LotInfo lotinfo : dto.getLotList()) {
                Map<String, String> rowMap = new HashMap<>();
                rowMap.put("province", dto.getProvinceName());
                rowMap.put("district", dto.getDistrictName());
                rowMap.put("drugCode", dto.getProductCode());
                rowMap.put("drugName", dto.getProductName());
                rowMap.put("lot", lotinfo.getLotNumber());
                rowMap.put("expiryDate", DateUtil.formatDate(lotinfo.getExpiryDate()));
                rowMap.put("soh", lotinfo.getStockOnHandOfLot().toString());
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

        if(null == paraMap.get("districtId") || StringUtils.isBlank(paraMap.get("districtId").toString())){
            return null;
        }

        List<OverStockProductDto> overStockProductDtoList = (List<OverStockProductDto>) queryResult.get(KEY_QUERY_RESULT);
        List<Map<String, String>> mergedRegions = new ArrayList<>();
        int index = 0;
        for (OverStockProductDto dto : overStockProductDtoList) {
            int cmmSpan = dto.getLotList().size();
            mergedRegions.add(createMergedRegion(String.valueOf(index + 1), String.valueOf(index + cmmSpan), CMM_COLUMN, CMM_COLUMN, getFormatDoubleValue(dto.getCmm())));
            mergedRegions.add(createMergedRegion(String.valueOf(index + 1), String.valueOf(index + cmmSpan), MOS_COLUMN, MOS_COLUMN, getFormatDoubleValue(dto.getMos())));
            index = index + cmmSpan;
        }

        return mergedRegions;
    }


    private Map<String, String> createMergedRegion(String firstRow, String lastRow, String firstCol, String lastCol, String value) {
        Map<String, String> regionMap = new HashMap<>();
        regionMap.put("firstRow", firstRow);
        regionMap.put("lastRow", lastRow);
        regionMap.put("firstCol", firstCol);
        regionMap.put("lastCol", lastCol);
        regionMap.put("mergedValue", value);
        return regionMap;
    }

    private String getFormatDoubleValue(Double d) {
        DecimalFormat formatter = new DecimalFormat("0.##");
        return null != d ? formatter.format(d) : "0";
    }


    @Override
    protected Object reportDataForFrontEnd(Map<Object, Object> paraMap) {
        return null;
    }
}
