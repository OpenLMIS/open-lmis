package org.openlmis.authentication;

import org.openlmis.authentication.domain.User;

public class UserToken {

    private final String userName;
    private final String role;
    private final boolean authenticated;

    public UserToken(String userName, String role, boolean authenticated) {
        this.userName = userName;
        this.role = role;
        this.authenticated = authenticated;
    }

    public String getUserName() {
        return userName;
    }

    public String getRole() {
        return role;
    }

    public boolean isAuthenticated() {
        return authenticated;
    }
}
