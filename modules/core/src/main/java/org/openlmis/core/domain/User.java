package org.openlmis.core.domain;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class User {
    private Integer id;
    private String userName;
    private String password;
    private String firstName;
    private String lastName;
    private String employeeId;
    private String jobTitle;
    private String primaryNotificationMethod;
    private String officePhone;
    private String cellPhone;
    private String email;
    private Integer supervisorId;

    private Integer facilityId;
}
