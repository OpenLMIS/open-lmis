package org.openlmis.report.model.params;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.openlmis.report.model.ReportParameter;

@Data
@AllArgsConstructor
@EqualsAndHashCode(callSuper=false)
@NoArgsConstructor
public class ReplacementPlanReportParam{

    private Long programId;
    private String facilityLevel;
    private String facilityIds;
    private String status;
    private String plannedYear;
    private String facility;
    private Boolean disaggregated;




}
