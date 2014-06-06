package org.openlmis.report.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * User: Issa
 * Date: 3/17/14
 * Time: 1:49 PM
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class AlertSummary {
    private Long id;
    private String staticsValue;
    private String description;
    private Long supervisoryNodeId;
    private Long periodId;
    private String alertType;
    private String displaySection;
    private Boolean email;
    private Boolean sms;
    private String detailTable;
    private String smsMessageTemplate;
    private String emailMessageTemplate;
}
