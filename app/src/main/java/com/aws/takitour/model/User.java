package com.aws.takitour.model;

public class User {
    private String name;
    private int type;
    private String email;
    private String telephone;

    public User(String name, int type, String email, String telephone) {
        this.name = name;
        this.type = type;
        this.email = email;
        this.telephone = telephone;
    }

    public void setName(String name) {
        this.name = name;
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

    public String getName() {
        return name;
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
