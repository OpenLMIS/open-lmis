/*
 * Copyright © 2013 John Snow, Inc. (JSI). All Rights Reserved.
 *
 * The U.S. Agency for International Development (USAID) funded this section of the application development under the terms of the USAID | DELIVER PROJECT contract no. GPO-I-00-06-00007-00.
 */

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
   // private String contact;

    public FacilityReport(String code,String facilityName,String facilityType,boolean active){
        this.code  = code;
        this.facilityName = facilityName;
        this.facilityType = facilityType;
        this.active = active;
    }
}
