package com.fit4039.chatura.moviegram.model;

import android.graphics.Bitmap;

import io.realm.RealmObject;

/**
 * Created by hp on 6/5/2015.
 */

// this class is for bookmarks. it extends RealObject so it can be saved in Realm database
public class Bookmark extends RealmObject {
    private int movieID;
    private String movieTitle;
    private String genre;

    public String getPosterFileName() {
        return posterFileName;
    }

    public void setPosterFileName(String posterFileName) {
        this.posterFileName = posterFileName;
    }

    private String posterFileName;

    public int getMovieID() {
        return movieID;
    }

    public void setMovieID(int movieID) {
        this.movieID = movieID;
    }

    public String getMovieTitle() {
        return movieTitle;
    }

    public void setMovieTitle(String movieTitle) {
        this.movieTitle = movieTitle;
    }

    public String getGenre() {
        return genre;
    }

    public void setGenre(String genre) {
        this.genre = genre;
    }

}
