package org.openlmis.report.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GeoZoneEquipmentStatus {

    private int facility_id;

    private int total_partially_operational;

    private int total_not_operational;

    private int total_fully_operational;
    
    private String facility_code;
    
    private String facility_name;
    
    private String facility_type;
    
    private String disrict;
    
    private String zone;
    
    private String equipment_type;

    private Float latitude;

    private Float longitude;

    private String serial_number;

    private String equipment_name;

    private String equipment_status;
}
