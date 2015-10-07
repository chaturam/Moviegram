package com.fit4039.chatura.moviegram.model;

/**
 * Created by hp on 6/3/2015.
 */

// this class contains tweet information
public class Tweet {

    private long id;
    private String text;
    private String topic = "movies"; // for accurate sentiment analysis
    private int polarity;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public int getPolarity() {
        return polarity;
    }

    public void setPolarity(int polarity) {
        this.polarity = polarity;
    }
}
