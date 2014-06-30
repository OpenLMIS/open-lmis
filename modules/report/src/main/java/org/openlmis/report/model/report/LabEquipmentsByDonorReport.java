package org.openlmis.report.model.report;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.openlmis.report.model.ReportData;

import javax.persistence.Column;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LabEquipmentsByDonorReport implements ReportData {

    @Column(name = "facilityname")
    private String facilityname;
    @Column(name = "equipment_name")
    private String equipment_name;
    @Column(name = "district")
    private String district;
    @Column(name = "model")
    private String model;
    @Column(name = "donor")
    private String donor;
    @Column(name="hasservicecontract")
    private String hasservicecontract;
    @Column(name = "sourceoffund")
    private String sourceoffund;
    @Column(name = "yearofinstallation")
    private String yearofinstallation;
    @Column(name = "servicecontractenddate")
    private String servicecontractenddate;
    @Column(name = "isactive")
    private String isactive;
    @Column(name = "datedecommissioned")
    private String datedecommissioned;
    @Column(name = "replacementrecommended")
    private String replacementrecommended;


}