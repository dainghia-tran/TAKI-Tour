package com.aws.takitour.notifications;

public class Data {
    private String user;
    private String title;
    private String body;
    private String receiveToken;
    public Data() {
    }

    public Data(String user, String title, String body, String receiveToken) {
        this.user = user;
        this.title = title;
        this.body = body;
        this.receiveToken = receiveToken;
    }

    public String getReceiveToken() {
        return receiveToken;
    }

    public void setReceiveToken(String receiveToken) {
        this.receiveToken = receiveToken;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
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
}
