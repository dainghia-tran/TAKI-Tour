package com.aws.takitour.model;

import java.util.List;

public class Tour {
    private String name;
    private String id;
    private String description;
    private String coverImage;
    private List<Participant> participants;
    private String tourGuide;
    private String date;
    private String host;
    private String price;
    private int length;
    private boolean isAvailable;
    private float overallRating;
    private List<UserReview> userReviewList;

    public Tour(String name, String id, String description, String coverImage, String tourGuide, String date, String host, String price, int length, boolean isAvailable) {
        this.name = name;
        this.id = id;
        this.description = description;
        this.coverImage = coverImage;
        this.tourGuide = tourGuide;
        this.date = date;
        this.host = host;
        this.price = price;
        this.length = length;
        this.isAvailable = isAvailable;
    }

    public void addUserReview(UserReview newUserReview){
        this.userReviewList.add(newUserReview);
    }

    public void addParticipant(Participant newParticipant){
        this.participants.add(newParticipant);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCoverImage() {
        return coverImage;
    }

    public void setCoverImage(String coverImage) {
        this.coverImage = coverImage;
    }

    public List<Participant> getParticipants() {
        return participants;
    }

    public void setParticipants(List<Participant> participants) {
        this.participants = participants;
    }

    public String getTourGuide() {
        return tourGuide;
    }

    public void setTourGuide(String tourGuide) {
        this.tourGuide = tourGuide;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public int getLength() {
        return length;
    }

    public void setLength(int length) {
        this.length = length;
    }

    public boolean isAvailable() {
        return isAvailable;
    }

    public void setAvailable(boolean available) {
        isAvailable = available;
    }

    public float getOverallRating() {
        return overallRating;
    }

    public void setOverallRating(float overallRating) {
        this.overallRating = overallRating;
    }

    public List<UserReview> getUserReviewList() {
        return userReviewList;
    }

    public void setUserReviewList(List<UserReview> userReviewList) {
        this.userReviewList = userReviewList;
    }
}
