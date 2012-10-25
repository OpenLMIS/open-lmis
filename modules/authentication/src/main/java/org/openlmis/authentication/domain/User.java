package org.openlmis.authentication.domain;

public class User {


    private String userName;
    private String role;

    public User(){}

    public User(String userName, String role) {
        this.userName = userName;
        this.role = role;
    }

    public String getRole() {
        return role;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public void setRole(String role) {
        this.role = role;
    }

}
