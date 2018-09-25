package org.openlmis.report.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ExportRequest {

    private LinkedHashMap<String,String> reportHeaders;

    private List<Map<String, Object>> reportContent;

    private List<String> reportTitles;
}
