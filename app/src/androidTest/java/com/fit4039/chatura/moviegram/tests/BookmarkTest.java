package com.fit4039.chatura.moviegram.tests;

import android.test.ActivityInstrumentationTestCase2;
import android.test.suitebuilder.annotation.LargeTest;
import android.test.suitebuilder.annotation.SmallTest;

import com.fit4039.chatura.moviegram.MainActivity;
import com.fit4039.chatura.moviegram.MovieActivity;
import com.fit4039.chatura.moviegram.R;
import com.fit4039.chatura.moviegram.model.Bookmark;
import com.google.android.youtube.player.YouTubePlayerFragment;

import io.realm.Realm;
import io.realm.RealmQuery;
import io.realm.RealmResults;

/**
 * Created by hp on 6/7/2015.
 */
public class BookmarkTest extends ActivityInstrumentationTestCase2<MainActivity> {
    MainActivity activity;
    int movieID;

    public BookmarkTest(){
        super(MainActivity.class);
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        activity = getActivity();
        movieID = 1;
    }

   @LargeTest
    public void testBookmark(){
       //adding the bookmark
        Realm realm = Realm.getInstance(activity);
        realm.beginTransaction();
        Bookmark bookmark = realm.createObject(Bookmark.class);
        bookmark.setMovieID(movieID);
        bookmark.setMovieTitle("Test title");
        bookmark.setGenre("Test genre");
        bookmark.setPosterFileName("Test path");
        realm.commitTransaction();

        RealmQuery<Bookmark> query = realm.where(Bookmark.class);
        query.equalTo("movieID", movieID);

        RealmResults<Bookmark> result = query.findAll();
        assertTrue(result.size() > 0);


       //removing the bookmark
       realm.beginTransaction();
       bookmark = realm.where(Bookmark.class).equalTo("movieID", movieID).findFirst();
       bookmark.removeFromRealm();
       realm.commitTransaction();

       query = realm.where(Bookmark.class);
       query.equalTo("movieID", movieID);

       result = query.findAll();
       assertTrue(result.size() == 0);
    }

}
