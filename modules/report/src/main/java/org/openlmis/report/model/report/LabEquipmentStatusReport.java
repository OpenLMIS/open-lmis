package org.openlmis.report.model.report;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.openlmis.report.model.ReportData;

import javax.persistence.Column;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LabEquipmentStatusReport  implements ReportData {

    @Column(name = "facility_name") private String facility_name;
    @Column(name = "equipment_name") private String equipment_name;
    @Column(name = "district") private String district;
    @Column(name = "model") private String model;
    @Column(name = "serialnumber") private String serialnumber;
    @Column(name = "test") private Integer test;
    @Column(name = "total_test") private Integer total_test;
    @Column(name = "daysoutofuse") private Integer daysoutofuse;
    @Column(name = "operational_status") private String operational_status;
}
