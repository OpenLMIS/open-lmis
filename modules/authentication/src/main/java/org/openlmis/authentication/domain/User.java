package org.openlmis.authentication.domain;

public class User {

    private String userName;
    private String role;

    public User(String userName, String role) {
        this.userName = userName;
        this.role = role;
    }

    public String getRole() {
        return role;
    }
}
