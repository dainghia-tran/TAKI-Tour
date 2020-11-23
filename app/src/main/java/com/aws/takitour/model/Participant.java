package com.aws.takitour.model;

import java.util.List;

public class Participant {
    private String userEmail;
    private String longitude;
    private String latitude;
    private List<String> uploadImageLink;

    public Participant(String userEmail) {
        this.userEmail = userEmail;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public List<String> getUploadImageLink() {
        return uploadImageLink;
    }

    public void setUploadImageLink(List<String> uploadImageLink) {
        this.uploadImageLink = uploadImageLink;
    }
}
