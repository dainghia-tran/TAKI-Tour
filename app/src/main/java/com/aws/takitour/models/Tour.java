package com.aws.takitour.models;

import java.io.Serializable;
import java.util.List;

public class Tour implements Serializable {
    private String name;
    private String id;
    private String description;
    private List<String> coverImage;
    private List<Participant> participants;
    private String tourGuide;
    private String startDate;
    private String host;
    private String price;
    private String endDate;
    private boolean isAvailable;
    private float overallRating;
    private List<UserReview> userReviewList;

    public Tour(String name, String id, String description, List<String> coverImage, String tourGuide, String startDate, String host, String price, String endDate, boolean isAvailable) {
        this.name = name;
        this.id = id;
        this.description = description;
        this.coverImage = coverImage;
        this.tourGuide = tourGuide;
        this.startDate = startDate;
        this.host = host;
        this.price = price;
        this.endDate = endDate;
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

    public List<String> getCoverImage() {
        return coverImage;
    }

    public void setCoverImage(List<String> coverImage) {
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

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
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

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
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
