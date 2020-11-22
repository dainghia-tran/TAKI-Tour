package com.aws.takitour.model;

public class User {
    private String id;
    private int type;
    private String email;
    private String telephone;

    public User(String id, int type, String email, String telephone) {
        this.id = id;
        this.type = type;
        this.email = email;
        this.telephone = telephone;
    }

    public void setId(String id) {
        this.id = id;
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

    public String getId() {
        return id;
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
