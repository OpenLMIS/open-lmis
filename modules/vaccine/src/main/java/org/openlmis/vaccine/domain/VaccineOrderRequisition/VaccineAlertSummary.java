package org.openlmis.vaccine.domain.VaccineOrderRequisition;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class VaccineAlertSummary {
    private Long id;
    private String staticsValue;
    private String description;
    private Long supervisoryNodeId;
    private Long periodId;
    private Long programId;
    private Long productId;
    private String zone;
    private String alertType;
    private String displaySection;
    private Boolean email;
    private Boolean sms;
    private String detailTable;
    private String smsMessageTemplate;
    private String emailMessageTemplate;

}
