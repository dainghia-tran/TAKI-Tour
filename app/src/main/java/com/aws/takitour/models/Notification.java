package com.aws.takitour.models;

import java.io.Serializable;

public class Notification implements Serializable {
    private String user;
    private String title;
    private String body;
    public Notification() {
    }

    public Notification(String user, String title, String body) {
        this.user = user;
        this.title = title;
        this.body = body;
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
