package com.fit4039.chatura.moviegram.model;

import java.io.Serializable;

import io.realm.RealmObject;

/**
 * Created by hp on 5/14/2015.
 */

// this class is for movie genre information
public class Genre implements Serializable {
    private String genreType;

    public String getGenreType() {
        return genreType;
    }

    public void setGenreType(String genreType) {
        this.genreType = genreType;
    }
}
