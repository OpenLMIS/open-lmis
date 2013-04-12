package org.openlmis.report.model.report;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.openlmis.core.domain.Facility;
import org.openlmis.report.model.ReportData;

import java.util.ArrayList;
import java.util.List;

/**
 *
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class FacilityReport implements ReportData {
    private String code;
    private String facilityName;
    private String facilityType;
    private boolean active;
    private String region;
    private String owner;
    private String gpsCoordinates;
    private String Email;
    private String phoneNumber;
    private String MSLMSDCode;
    private String fax;

    public FacilityReport(String code,String facilityName,String facilityType,boolean active){
        this.code  = code;
        this.facilityName = facilityName;
        this.facilityType = facilityType;
        this.active = active;
    }
}
