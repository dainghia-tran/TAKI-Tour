package com.aws.takitour.model;

import java.io.Serializable;
import java.util.List;

public class User implements Serializable{
    private String name;
    private int type;
    private String email;
    private String telephone;
    private List<String> tourList;
    private String profileImage;

    public User(String name, int type, String email, String telephone, String profileImage) {
        this.name = name;
        this.type = type;
        this.email = email;
        this.telephone = telephone;
        this.profileImage = profileImage;
    }

    public String getProfileImage() {
        return profileImage;
    }

    public void setProfileImage(String profileImage) {
        this.profileImage = profileImage;
    }

    public void addTour(String tourCode){
        this.tourList.add(tourCode);
    }

    public List<String> getTourList() {
        return tourList;
    }

    public void setTourList(List<String> tourList) {
        this.tourList = tourList;
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
