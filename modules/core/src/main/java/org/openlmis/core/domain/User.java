package org.openlmis.core.domain;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class User {

    private int id;
    private String userName;
    private String password;
    private String role;
    private Integer facilityId;

    public User(String userName, String password) {
        this.userName = userName;
        this.password = password;
    }

    @Deprecated
    public User(String userName, String password, String role) {
        this.userName = userName;
        this.role = role;
        this.password = password;
    }
}
