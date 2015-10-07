package com.fit4039.chatura.moviegram.model;

/**
 * Created by hp on 6/4/2015.
 */
//this class contains reviews of relevant movie

public class Review {
    private String author;
    private String reviewText;

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getReviewText() {
        return reviewText;
    }

    public void setReviewText(String reviewText) {
        this.reviewText = reviewText;
    }
}
