package org.openlmis.authentication;

import lombok.Data;

@Data
public class UserToken {

    private final String userName;
    private final String role;
    private final boolean authenticated;


    public UserToken(String userName, String role, boolean authenticated) {
        this.userName = userName;
        this.role = role;
        this.authenticated = authenticated;
    }
}
