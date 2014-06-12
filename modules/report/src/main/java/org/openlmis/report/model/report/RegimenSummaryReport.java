package org.openlmis.report.model.report;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.openlmis.report.model.ReportData;

import javax.persistence.Column;
@Data
@AllArgsConstructor
@NoArgsConstructor
public class RegimenSummaryReport implements ReportData {

    @Column(name = "regimen")
    private String regimen;

    @Column(name="patientsontreatment")
    private Integer patientsontreatment;

    @Column( name = "rgroupid")
    private String rgroupid;
    private String rgroup;
    private String district;
    private String facilityname;

    @Column(name= "patientstoinitiatetreatment")
    private Integer patientstoinitiatetreatment;

   @Column(name="patientsstoppedtreatment")
    private Integer patientsstoppedtreatment;

    private Double totalpatientsToInitiateTreatmentPercentage;
    private Double totalOnTreatmentPercentage;
    private Double stoppedTreatmentPercentage;


}
