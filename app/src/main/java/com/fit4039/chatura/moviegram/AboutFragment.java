package com.fit4039.chatura.moviegram;

import android.app.Fragment;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.fit4039.chatura.moviegram.model.Abouts;
import com.fit4039.chatura.moviegram.model.Bookmark;

import java.util.ArrayList;

/**
 * Created by hp on 6/8/2015.
 */

// handles Abouts screen of the app
public class AboutFragment extends Fragment {
    ListView lvAbouts;
    View view;
    ArrayList<Abouts> abouts;
    AboutsAdapter aboutsAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_about, container, false);

        lvAbouts = (ListView) view.findViewById(R.id.lvAbout);
        genreateAboutData();
        aboutsAdapter = new AboutsAdapter(getActivity(), abouts);
        lvAbouts.setAdapter(aboutsAdapter);
        lvAbouts.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // starting the web browser with the link
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(abouts.get(position).getLink()));
                startActivity(browserIntent);
            }
        });

        return view;
    }

    // creating about objects with 3rd party library information
    private void genreateAboutData(){
        abouts = new ArrayList<Abouts>();

        Abouts abouts1 = new Abouts();
        abouts1.setName("Android View Animations");
        abouts1.setLink("https://github.com/daimajia/AndroidViewAnimations");
        abouts1.setDescription("Used in Splash screen for animations");
        abouts.add(abouts1);

        Abouts abouts2 = new Abouts();
        abouts2.setName("Android Image Slider");
        abouts2.setLink("https://github.com/daimajia/AndroidImageSlider");
        abouts2.setDescription("Used in Movies fragment to display multiple movie banners");
        abouts.add(abouts2);

        Abouts abouts3 = new Abouts();
        abouts3.setName("Universal Image Loader");
        abouts3.setLink("https://github.com/nostra13/Android-Universal-Image-Loader");
        abouts3.setDescription("Used in various occations to load images asynchronously");
        abouts.add(abouts3);

        Abouts abouts4 = new Abouts();
        abouts4.setName("YouTube Android Player API");
        abouts4.setLink("https://developers.google.com/youtube/android/player/");
        abouts4.setDescription("Used in Movie Activity to display movie trailer");
        abouts.add(abouts4);

        Abouts abouts5 = new Abouts();
        abouts5.setName("MP Android Chart");
        abouts5.setLink("https://github.com/PhilJay/MPAndroidChart");
        abouts5.setDescription("Used in Review Activity to generate the pie chart");
        abouts.add(abouts5);

        Abouts abouts6 = new Abouts();
        abouts6.setName("Rotten Tomatoes API");
        abouts6.setLink("http://developer.rottentomatoes.com/");
        abouts6.setDescription("Primary data source of Moviegram");
        abouts.add(abouts6);

        Abouts abouts7 = new Abouts();
        abouts7.setName("The Movie Database (TMDB)");
        abouts7.setLink("https://www.themoviedb.org/documentation/api");
        abouts7.setDescription("Provides posters/banners for movies");
        abouts.add(abouts7);

        Abouts abouts8 = new Abouts();
        abouts8.setName("Twitter API");
        abouts8.setLink("https://dev.twitter.com/rest/public");
        abouts8.setDescription("Provides tweets for sentiment analysis");
        abouts.add(abouts8);

        Abouts abouts9 = new Abouts();
        abouts9.setName("Sentiment 140");
        abouts9.setLink("http://www.sentiment140.com/");
        abouts9.setDescription("Provides setiment analysis for tweets");
        abouts.add(abouts9);

        Abouts abouts10 = new Abouts();
        abouts10.setName("Android Realm");
        abouts10.setLink("https://realm.io/docs/java/latest/");
        abouts10.setDescription("Used as local database to store bookmarks");
        abouts.add(abouts10);

        Abouts abouts11 = new Abouts();
        abouts11.setName("Google GSON");
        abouts11.setLink("https://code.google.com/p/google-gson/");
        abouts11.setDescription("To convert tweets to JSON objects");
        abouts.add(abouts11);
    }
}
