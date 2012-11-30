package org.openlmis.core.domain;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class User {

    private String userName;
    private String password;
    private String role;
    private int facilityId;

    public User(String userName, String role) {
        this.userName = userName;
        this.role = role;
    }

}
