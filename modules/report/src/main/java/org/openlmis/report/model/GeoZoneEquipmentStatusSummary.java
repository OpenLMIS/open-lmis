package org.openlmis.report.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GeoZoneEquipmentStatusSummary {

    private int status_id;
    private String equipment_status;
    private int total;
}
