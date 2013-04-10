package org.openlmis.report.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Created with IntelliJ IDEA.
 * User: user
 * Date: 4/11/13
 * Time: 12:29 AM
 * To change this template use File | Settings | File Templates.
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
    private String GPSCoordinates;
    private String Email;
    private String phoneNumber;
    private String MSLMSDCode;
    private String fax;

}
