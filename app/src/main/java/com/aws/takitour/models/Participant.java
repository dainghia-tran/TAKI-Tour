package com.aws.takitour.models;

import java.io.Serializable;
import java.util.List;

public class Participant implements Serializable {
    private String longitude;
    private String latitude;
    private String name;
    private List<String> uploadImageLink;

    public Participant(){}

    public Participant(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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
