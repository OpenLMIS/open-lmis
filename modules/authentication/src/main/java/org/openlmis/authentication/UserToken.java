package org.openlmis.authentication;

import lombok.Data;

@Data
public class UserToken {

    private final String userName;
    private final Integer userId;
    private final boolean authenticated;


    public UserToken(String userName, Integer userId, boolean authenticated) {
        this.userName = userName;
        this.userId = userId;
        this.authenticated = authenticated;
    }
}
