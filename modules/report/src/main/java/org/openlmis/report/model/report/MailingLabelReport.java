/*
 * Copyright © 2013 John Snow, Inc. (JSI). All Rights Reserved.
 *
 * The U.S. Agency for International Development (USAID) funded this section of the application development under the terms of the USAID | DELIVER PROJECT contract no. GPO-I-00-06-00007-00.
 */

package org.openlmis.report.model.report;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.openlmis.report.model.ReportData;

import javax.persistence.Column;

/**
 * A report data used to fill mailing labels report.
 * Mailing Labels Report is used to generates mailing labels displaying the contact person and address for each facility in the distribution system.
 * Logistics managers may use these labels when sending reports or correspon­dence to facilities in the distribution system
 */

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MailingLabelReport implements ReportData {

    @Column(name = "F.code")
    private String code;
    @Column(name = "f.name")
    private String facilityName;
    @Column(name = "F.typeid")
    private String facilityType;
    private boolean active;
    @Column(name = "GZ.name")
    private String region;
    @Column(name = "F.address1")
    private String address1;
    private String address2;
    @Column(name = "FO.code")
    private String owner;
    private String email;
    private String phoneNumber;
    private String mslOrmsdCode;
    private String fax;
    private String gpsCoordinates;
    @Column(name = " U.firstName")
    private String contact;

}
