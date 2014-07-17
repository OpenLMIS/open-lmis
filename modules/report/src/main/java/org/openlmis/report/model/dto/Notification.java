package org.openlmis.report.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.openlmis.core.domain.User;

import java.util.List;

/**
 * User: Issa
 * Date: 4/15/14
 * Time: 2:14 PM
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Notification {
    private String emailMessage;
    private String smsMessage;
    private List<User> receivers;
    private List<String> notificationMethods;
}

