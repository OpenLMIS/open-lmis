package org.openlmis.report.model.dto;

import com.fasterxml.jackson.annotation.JsonValue;
import lombok.*;
import org.openlmis.core.utils.DateUtil;

import java.util.Date;

@Getter
@Setter
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
public class RequisitionDTO {
    private Long id;
    private String programName;
    private RequisitionType type;
    private boolean emergency;
    private String facilityName;
    private String submittedUser;
    private String webSubmittedTimeString;
    private String clientSubmittedTimeString;
    private Date actualPeriodEnd;
    private Date schedulePeriodEnd;

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
        type = emergency ? RequisitionType.EMERGENCY_TYPE : RequisitionType.NORMAL_TYPE;
    }

    public enum RequisitionType {
        NORMAL_TYPE("Normal"),
        EMERGENCY_TYPE("Emergency");

        @JsonValue
        public String getType() {
            return type;
        }

        private String type;

        RequisitionType(String type) {
            this.type = type;
        }

    }

}
