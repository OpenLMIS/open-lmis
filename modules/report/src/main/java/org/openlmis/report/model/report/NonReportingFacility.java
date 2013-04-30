package org.openlmis.report.model.report;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.openlmis.report.model.ReportData;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class NonReportingFacility implements ReportData {

    private String id;
    private String code;
    private String name;
    private String facilityType;
    private String location;
}
