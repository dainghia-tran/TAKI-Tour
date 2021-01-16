package com.aws.takitour.notifications;

public class Data {
    private String tourId;
    private String sender;
    private String title;
    private String body;
    private String receiveToken;
    private int type;
    private String imageLink;

    public Data() {
    }

    public Data(String tourId, String sender, String title, String body, String receiveToken) {
        this.tourId = tourId;
        this.sender = sender;
        this.title = title;
        this.body = body;
        this.type = 0;
        this.receiveToken = receiveToken;
    }
    public Data(String tourId, String sender, String title, String body, String receiveToken, String imageLink) {
        this.tourId = tourId;
        this.sender = sender;
        this.title = title;
        this.body = body;
        this.type = 1;
        this.imageLink = imageLink;
        this.receiveToken = receiveToken;
    }

    public String getReceiveToken() {
        return receiveToken;
    }

    public void setReceiveToken(String receiveToken) {
        this.receiveToken = receiveToken;
    }

    public String getTourId() {
        return tourId;
    }

    public void setTourId(String tourId) {
        this.tourId = tourId;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getImageLink() {
        return imageLink;
    }

    public void setImageLink(String imageLink) {
        this.imageLink = imageLink;
    }
}
