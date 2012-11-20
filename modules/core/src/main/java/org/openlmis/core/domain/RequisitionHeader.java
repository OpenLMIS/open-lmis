package org.openlmis.core.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RequisitionHeader {

    String facilityName;
    String facilityCode;
    String facilityType;
    String facilityOperatedBy;
    int maximumStockLevel;
    float emergencyOrderPoint;
    GeographicZone zone;
    GeographicZone parentZone;

}