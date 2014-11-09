package org.openlmis.report.model.dto;

import lombok.Data;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Data
public class MessageCollection {

    private List<MessageDto> messages;
    private String reportKey;
    private String subject;
    private String outputOption;
    private Map<String, String> reportParams;

    public Map<String, String[]> getReportParams(){

        Map<String, String[]> modifiedParamMap = new HashMap<String, String[]>();

        /** Reformat the report filter param */
        for(Map.Entry<String, String> obj : reportParams.entrySet()){
            modifiedParamMap.put(obj.getKey().toString(), new String[]{obj.getValue().toString()});
        }

        return modifiedParamMap;
    }
}
