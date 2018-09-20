package org.openlmis.report.generator;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class ReportModelGeneratorService {

    private static final String REPORT_TYPE = "reportType";


    private Map<String, AbstractReportModelGenerator> generators;


    @Autowired
    public void setGenerators(Map<String, AbstractReportModelGenerator> generators) {
        this.generators = generators;
    }

    private AbstractReportModelGenerator getReportGenerator(String reportType) {

        if (generators.containsKey(reportType)) {
            return generators.get(reportType);
        }
        throw new RuntimeException(String.format("report type is invalid. type value = %s", reportType));
    }

    public Map<String, Object> generateModel(Map<Object, Object> paraMap) {
        if (paraMap.containsKey(REPORT_TYPE)) {
            return getReportGenerator(paraMap.get(REPORT_TYPE).toString()).generate(paraMap);
        }
        throw new RuntimeException("report type not found in generator map.");
    }
}
