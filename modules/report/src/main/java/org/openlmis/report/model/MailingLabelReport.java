package org.openlmis.report.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * A report data used to fill mailing labels report.
 * Mailing Labels Report is used to generates mailing labels displaying the contact person and address for each facility in the distribution system.
 * Logistics managers may use these labels when sending reports or correspon­dence to facilities in the distribution system
 */
public class MailingLabelReport extends FacilityReport {

    public MailingLabelReport(String code, String facilityName, String facilityType, boolean active, String ownership, Double latitude, Double longitude, Double altitude, String email, String phoneNumber, String mslCode, String msdCode) {
        super(code, facilityName, facilityType, active, ownership, latitude, longitude, altitude, email, phoneNumber, mslCode, msdCode);
    }
}
