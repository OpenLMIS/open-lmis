package org.openlmis.report.generator;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.NoArgsConstructor;
import org.openlmis.core.service.MessageService;
import org.openlmis.report.view.CustomExcelTemplate;
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
    private MessageService messageService;// = MessageService.getRequestInstance();

    @Autowired
    protected CubesReportProxy cubesReportProxy;

    protected abstract Object getReportHeaders(Map<Object, Object> paraMap, Map<String, Object> cubeQueryResult);

    protected abstract Object getReportContent(Map<Object, Object> paraMap, Map<String, Object> cubeQueryResult);

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
        model.put(CustomExcelTemplate.getKEY_EXCEL_HEADERS(), reportHeaders);
        model.put(CustomExcelTemplate.getKEY_EXCEL_CONTENT(), reportContent);
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

}
