package com.aws.takitour.model;

public class User {
    private int type;
    private String email;
    private String telephone;

    public User(int type, String email, String telephone) {
        this.type = type;
        this.email = email;
        this.telephone = telephone;
    }

    public void setType(int type) {
        this.type = type;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setTelephone(String telephone) {
        this.telephone = telephone;
    }

    public int getType() {
        return type;
    }

    public String getEmail() {
        return email;
    }

    public String getTelephone() {
        return telephone;
    }
}
