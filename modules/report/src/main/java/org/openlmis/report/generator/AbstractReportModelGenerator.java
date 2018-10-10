package org.openlmis.report.generator;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.openlmis.core.service.MessageService;
import org.openlmis.report.view.WorkbookCreator;
import org.openlmis.web.controller.cubesreports.CubesReportProxy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
@NoArgsConstructor
public abstract class AbstractReportModelGenerator {

    @Autowired
    private MessageService messageService;

    @Autowired
    protected CubesReportProxy cubesReportProxy;

    protected abstract Object getReportHeaders(Map<Object, Object> paraMap, Map<String, Object> cubeQueryResult);

    protected abstract Object getReportContent(Map<Object, Object> paraMap, Map<String, Object> cubeQueryResult);

    protected abstract List<Map<String, String>> getReportMergedRegions();

    protected Object getReportLegenda(Map<Object, Object> paraMap, Map<String, Object> cubeQueryResult, Map<String, Object> model) {
        return null;
    }

    protected Map<String, Object> getCubeQueryResult(Map<Object, Object> paraMap) {
        return null;
    }

    public Map<String, Object> generate(Map<Object, Object> paraMap) {

        Map<String, Object> cubeQueryResult = getCubeQueryResult(paraMap);

        Object reportHeaders = getReportHeaders(paraMap, cubeQueryResult);
        if (null == reportHeaders) {
            throw new RuntimeException("report headers return null!");
        }
        Object reportContent = getReportContent(paraMap, cubeQueryResult);
        if (null == reportContent) {
            throw new RuntimeException("report content return null!");
        }
        Map<String, Object> model = new HashMap<>();
        model.put(WorkbookCreator.getKEY_EXCEL_HEADERS(), reportHeaders);
        model.put(WorkbookCreator.getKEY_EXCEL_CONTENT(), reportContent);
        Object reportLegenda = getReportLegenda(paraMap, cubeQueryResult, model);
        if (null != reportLegenda) {
            model.put(WorkbookCreator.getKEY_EXCEL_LEGENDA(), reportLegenda);
        }

        List<Map<String, String>> reportMergedRegion = getReportMergedRegions();
        if(null != reportMergedRegion){
            model.put(WorkbookCreator.getKEY_EXCEL_MERGE(),reportMergedRegion);
        }

        return model;
    }

    protected String getBaseFactUri(String cubeModelName) {
        return String.format("/cube/%s/facts", cubeModelName);
    }

    protected String getMessage(String key) {
        return messageService.allMessages().get(key);
    }

    protected List<Map<String, String>> jsonToListMap(String json) {
        ObjectMapper mapper = new ObjectMapper();
        try {
            return mapper.readValue(json, new TypeReference<ArrayList<Map<String, String>>>(){});
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    protected String mapToQueryString(Map<String, Object> cutsParams) {
        StringBuilder sb = new StringBuilder();
        for (Map.Entry<String, Object> entry : cutsParams.entrySet()) {
            sb.append(entry.getKey()).append(":");
            if (entry.getValue() instanceof String) {
                sb.append(entry.getValue());
            } else if (entry.getValue() instanceof List) {
                for (String value : (List<String>) entry.getValue()) {
                    sb.append(value).append(";");
                }
                if (sb.toString().endsWith(";")) {
                    sb.deleteCharAt(sb.length() - 1);
                }
            }
            sb.append("|");
        }
        if (sb.toString().endsWith("|")) {
            sb.deleteCharAt(sb.length() - 1);
        }

        return sb.toString();
    }

    protected boolean isOneDistrict(String province, String district) {
        return StringUtils.isNotEmpty(province) && StringUtils.isNotEmpty(district);
    }

    protected boolean isOneProvince(String province, String district) {
        return StringUtils.isNotEmpty(province) && StringUtils.isEmpty(district);
    }

    protected boolean isAllProvinces(String province, String district) {
        return StringUtils.isEmpty(province) && StringUtils.isEmpty(district);
    }

    protected String getLocationHierarchy(String province, String district) {
        if (isOneDistrict(province, district)) {
            return province + "," + district;
        } else if (isOneProvince(province, district)) {
            return province;
        }
        return null;
    }

    protected String getDistrict(Map<Object, Object> paraMap) {
        return get(paraMap, "district", "code");
    }

    protected String getProvince(Map<Object, Object> paraMap) {
        return get(paraMap, "province", "code");
    }

    protected String get(Map<Object, Object> paraMap, String key1, String key2) {
        if (!paraMap.containsKey(key1)) {
            return null;
        }
        Map<String, String> map = (Map<String, String>) paraMap.get(key1);
        return map.get(key2);
    }
}
