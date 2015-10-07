package com.fit4039.chatura.moviegram.model;

import java.io.Serializable;

import io.realm.RealmObject;

/**
 * Created by hp on 5/13/2015.
 */

// this class contains posters of relevant movie
public class Poster implements Serializable {
    private String thumbnail;
    private String profile;
    private String detailed;
    private String original;
    private String backdrop;
    private String profile2;

    public String getThumbnail() {
        return thumbnail;
    }

    public void setThumbnail(String thumbnail) {
        this.thumbnail = thumbnail;
    }

    public String getProfile() {
        return profile;
    }

    public void setProfile(String profile) {
        this.profile = profile;
    }

    public String getDetailed() {
        return detailed;
    }

    public void setDetailed(String detailed) {
        this.detailed = detailed;
    }

    public String getOriginal() {
        return original;
    }

    public void setOriginal(String original) {
        this.original = original;
    }

    public String getBackdrop() {
        return backdrop;
    }

    public void setBackdrop(String backdrop) {
        this.backdrop = backdrop;
    }

    public String getProfile2() {
        return profile2;
    }

    public void setProfile2(String profile2) {
        this.profile2 = profile2;
    }
}
