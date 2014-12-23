package org.openlmis.report.model.dto;

import lombok.Data;

@Data
public class ReportingStatus {
    private Integer total;
    private Integer reporting;
    private Integer nonReporting;
}
