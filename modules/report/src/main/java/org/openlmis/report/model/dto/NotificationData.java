package org.openlmis.report.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * User: Issa
 * Date: 4/15/14
 * Time: 2:14 PM
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class NotificationData {
    private String facility;
    private Date rnrSubmittedDate;
}
