package org.openlmis.report.model.dto;

import lombok.*;
import org.openlmis.core.utils.DateUtil;

import java.util.Date;

@Getter
@Setter
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
public class RequisitionDTO {
    public static final String NORMAL_TYPE = "Normal";
    public static final String EMERGENCY_TYPE = "Emergency";

    private Long id;
    private String programName;
    private String type;
    private boolean emergency;
    private String facilityName;
    private String submittedUser;
    private String webSubmittedTimeString;
    private String clientSubmittedTimeString;

    private Date webSubmittedTime;
    private Date clientSubmittedTime;

    private String requisitionStatus;

    public String getClientSubmittedTimeString() {

        return DateUtil.formatDate(clientSubmittedTime);
    }

    public String getWebSubmittedTimeString() {
        return DateUtil.formatDate(webSubmittedTime);
    }

    public void assignType() {
        type = emergency ? EMERGENCY_TYPE : NORMAL_TYPE;
    }

}
