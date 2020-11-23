package com.aws.takitour.model;

public class UserReview {
    private String userEmail;
    private int star;
    private String comment;

    public UserReview(String userEmail, int star, String comment) {
        this.userEmail = userEmail;
        this.star = star;
        this.comment = comment;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public int getStar() {
        return star;
    }

    public void setStar(int star) {
        this.star = star;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }
}
