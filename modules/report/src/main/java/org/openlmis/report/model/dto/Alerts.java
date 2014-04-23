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
public class Alerts {
    private Long id;
    private String description;
    private Long supervisoryNodeId;
    private String category;
    private Boolean email;
    private Boolean sms;
}
