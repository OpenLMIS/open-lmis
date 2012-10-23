package org.openlmis.authentication;

import org.openlmis.authentication.domain.User;

public class UserToken {

    private final User user;
    private final boolean authenticated;

    public UserToken(User user, boolean authenticated) {
        this.user = user;
        this.authenticated = authenticated;
    }

    public User getUser() {
        return user;
    }

    public boolean isAuthenticated() {
        return authenticated;
    }
}
