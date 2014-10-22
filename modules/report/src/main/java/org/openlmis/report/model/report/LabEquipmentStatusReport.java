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

    //@Column(name = "facility_name")
    private String facilityName;
   // @Column(name = "equipment_name")
    private String equipmentName;
   // @Column(name = "disrict")
    private String district;
    //@Column(name = "equipment_model")
    private String model;
   // @Column(name = "serial_number")
    private String serialNumber;
   // @Column(name = "equipment_status")
    private String operationalStatus;
    //@Column(name = "equipment_type")
    private String equipmentType;
   // @Column(name = "facility_code")
    private String facilityCode;
   // @Column(name = "facility_type")
    private String facilityType;
   // @Column(name = "zone")
    private String zone;

    //@Column(name = "hasservicecontract")
    private String serviceContract;
   // @Column(name = "contract.name")
    private String vendorName;
   // @Column(name = "contract.contractid")
    private String contractId;

}
