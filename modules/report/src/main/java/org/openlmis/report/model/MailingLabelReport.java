package org.openlmis.report.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * A report data used to fill mailing labels report.
 * Mailing Labels Report is used to generates mailing labels displaying the contact person and address for each facility in the distribution system.
 * Logistics managers may use these labels when sending reports or correspon­dence to facilities in the distribution system
 */

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MailingLabelReport implements ReportData  {

    private String code;
    private String facilityName;
    private String facilityType;
    private boolean active;
    private String region;
    private String owner;
    private String email;
    private String phoneNumber;
    private String mslOrmsdCode;
    private String fax;
    private String gpsCoordinates;

}
