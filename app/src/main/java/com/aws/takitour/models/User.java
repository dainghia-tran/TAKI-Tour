package com.aws.takitour.models;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class User implements Serializable{
    private String name;
    private int type;
    private String email;
    private String telephone;
    private List<String> tourList;
    private String profileImage;
    private String description;

    public User(){}

    public User(String name, int type, String email, String telephone, String profileImage, String description) {
        this.name = name;
        this.type = type;
        this.email = email;
        this.telephone = telephone;
        this.tourList = new ArrayList<>();
        this.profileImage = profileImage;
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
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
