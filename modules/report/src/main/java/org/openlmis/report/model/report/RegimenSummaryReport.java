package org.openlmis.report.model.report;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.openlmis.report.model.ReportData;

import javax.persistence.Column;


/**
 * Created with IntelliJ IDEA.
 * User: Hassan
 * Date: 11/25/13
 * Time: 6:06 PM
 * To change this template use File | Settings | File Templates.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class RegimenSummaryReport implements ReportData {

    @Column(name = "regimen")
    private String regimen;

    @Column(name="patientsontreatment")
    private Integer patientsontreatment;

   // @Column( name = "regimencategory")
   //private String regimencategory;

   @Column( name = "district")
    private String district;

    @Column(name= "patientstoinitiatetreatment")
    private Integer patientstoinitiatetreatment;

   @Column(name="patientsstoppedtreatment")
    private Integer patientsstoppedtreatment;

    private Double totalpatientsToInitiateTreatmentPercentage;
    private Double totalOnTreatmentPercentage;
    private Double stoppedTreatmentPercentage;


}
