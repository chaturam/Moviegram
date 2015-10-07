package com.fit4039.chatura.moviegram.model;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.google.gson.JsonArray;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.net.URLEncoder;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
//http://tab.bz/axovt
import io.realm.RealmList;

/**
 * Created by hp on 5/14/2015.
 */

// This is the utility class of the app. It contains frequently used functions and constants
public class Util {
    public static final String RTAPIKey = "rbz5pdeba8uwa7rgbqpcxuyh"; // rotten tomatoes key
    public static final String TMDBAPIKey = "7a5a9a9a623979d9c6240eb6114da380"; // the movie database key
    public static final String GoogleAndroidKey = "AIzaSyDvOsxRzuim4AVLVUP4esVwc14UEIRXY20"; // google android sey for youtube player
    public static final String GoogleServerKey = "AIzaSyBPkYKyjKJAXcJE4gmWPClw67J1gbbkeUk"; // google server sey for youtube search
    public static final String TwitterKey = "8K0yyrQBhMkMYYt75oFPrVrYk"; // twitter key
    public static final String TwitterSecret = "MLoBkBQdH0mNilCThnld02CtkjM5ZV8BgpHE6rcgvX5EIA0Ymo"; // twitter secret key


    // this method decodes a list movies (JSON) to an arraylist
    public ArrayList<Movie> decodeMovies(String input){
        ArrayList<Movie> movies = new ArrayList<Movie>();
        try {
            JSONObject jObj = new JSONObject(input);
            JSONArray jMovies = jObj.getJSONArray("movies");
            for(int i = 0; i < jMovies.length(); ++i){
                JSONObject jMovie = jMovies.getJSONObject(i);
                Movie movie = new Movie();
                movie.setMovieID(jMovie.getInt("id"));
                movie.setTitle(jMovie.getString("title"));
                movie.setYear(jMovie.getInt("year"));
                movie.setRating(jMovie.getString("mpaa_rating"));
                try {
                    //some movie objects do not have run time information. this try - catch handles it
                    movie.setRuntime(jMovie.getInt("runtime"));
                }catch (Exception e){
                    movie.setRuntime(90);// standard movie duration
                }

                JSONObject releaseDates = jMovie.getJSONObject("release_dates");
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                try {
                    Date releaseDate = sdf.parse(releaseDates.getString("theater"));
                    movie.setReleaseDate(releaseDate);
                } catch (ParseException e) {
                    e.printStackTrace();
                }

                JSONObject ratings = jMovie.getJSONObject("ratings");
                movie.setCriticsScore(ratings.getInt("critics_score"));
                movie.setAudienceScore(ratings.getInt("audience_score"));
                movie.setSynopsis(jMovie.getString("synopsis"));

                // this is only the URL of the image. Actual image loads at UI
                JSONObject jPosters = jMovie.getJSONObject("posters");
                Poster poster = new Poster();
                poster.setDetailed(jPosters.getString("detailed"));
                poster.setOriginal(jPosters.getString("original"));
                poster.setProfile(jPosters.getString("profile"));
                poster.setThumbnail(jPosters.getString("thumbnail"));
                movie.setPosters(poster);

                ArrayList <Role> cast = new ArrayList<Role>();
                JSONArray jCast = jMovie.getJSONArray("abridged_cast");
                for(int j = 0; j < jCast.length(); ++j){
                    JSONObject jRole = jCast.getJSONObject(j);
                    Role role = new Role();
                    role.setRealName(jRole.getString("name"));
                    cast.add(role);
                }
                movie.setCast(cast);

                movies.add(movie);
            }

        } catch (JSONException e) {
            e.printStackTrace();
            Log.d("movie", "error: " + e.toString());
        }
        return movies;
    }

    // this method decodes a single movie
    public Movie decodeMovie(String input){
        Movie movie = new Movie();

        try {
            JSONObject jMovie = new JSONObject(input);
            movie.setMovieID(jMovie.getInt("id"));
            movie.setTitle(jMovie.getString("title"));
            movie.setYear(jMovie.getInt("year"));
            movie.setRating(jMovie.getString("mpaa_rating"));
            movie.setRuntime(jMovie.getInt("runtime"));
            JSONObject releaseDates = jMovie.getJSONObject("release_dates");
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            try {
                Date releaseDate = sdf.parse(releaseDates.getString("theater"));
                movie.setReleaseDate(releaseDate);
            } catch (ParseException e) {
                e.printStackTrace();
            }

            JSONObject ratings = jMovie.getJSONObject("ratings");
            movie.setCriticsScore(ratings.getInt("critics_score"));
            movie.setAudienceScore(ratings.getInt("audience_score"));
            movie.setSynopsis(jMovie.getString("synopsis"));

            try{
                JSONArray jGenres = jMovie.getJSONArray("genres");
                ArrayList<Genre> genreList = new ArrayList<Genre>();
                for(int i = 0; i < jGenres.length(); ++i){
                    Genre genre = new Genre();
                    genre.setGenreType(jGenres.getString(i));
                    genreList.add(genre);
                }
                movie.setGenres(genreList);

                JSONObject jPosters = jMovie.getJSONObject("posters");
                Poster poster = new Poster();
                poster.setDetailed(jPosters.getString("detailed"));
                poster.setOriginal(jPosters.getString("original"));
                poster.setProfile(jPosters.getString("profile"));
                poster.setThumbnail(jPosters.getString("thumbnail"));
                movie.setPosters(poster);

                JSONArray directors = jMovie.getJSONArray("abridged_directors");
                JSONObject director = directors.getJSONObject(0);
                movie.setDirector(director.getString("name"));

                movie.setStudio(jMovie.getString("studio"));
            } catch (JSONException e) {

            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return  movie;
    }



    // this method decodes poster information
    public Movie decodePosters(Movie movie, String input){
        try {
            JSONObject jObj = new JSONObject(input);
            JSONArray results = jObj.getJSONArray("results");
            JSONObject result = results.getJSONObject(0);// taking the first object
            String backdropPath = "http://image.tmdb.org/t/p/w500" + result.getString("backdrop_path");
            String profilePath = "http://image.tmdb.org/t/p/w500" + result.getString("poster_path");

            movie.getPosters().setBackdrop(backdropPath);
            movie.getPosters().setProfile2(profilePath);

        } catch (Exception e) {
            //if any error occurs, values are set to empty instead of null
            movie.getPosters().setBackdrop("");
            movie.getPosters().setProfile2("");
            e.printStackTrace();
        }

        return  movie;// used for unit testing
    }

    // decoding the youtube ID
    public  Movie decodeYouTubeID(Movie movie, String input){
        try {
            JSONObject jObj = new JSONObject(input);
            JSONArray jItems = jObj.getJSONArray("items");
            JSONObject jItem = jItems.getJSONObject(0);
            JSONObject jID = jItem.getJSONObject("id");
            movie.setYoutubeID(jID.getString("videoId"));
        }catch (Exception e){
            e.printStackTrace();
        }

        return movie;
    }

    public ArrayList<Tweet> decodeTweets(String input){
        ArrayList <Tweet> tweets = new ArrayList<Tweet>();

        try {

            JSONObject jObj = new JSONObject(input);
            JSONArray jStatuses = jObj.getJSONArray("statuses");
            for(int i = 0; i < jStatuses.length(); ++i){
                JSONObject jStatus = jStatuses.getJSONObject(i);
                Tweet tweet = new Tweet();
                tweet.setId(jStatus.getLong("id"));
                tweet.setText(jStatus.getString("text"));
                tweets.add(tweet);
            }


        } catch (JSONException e) {
            e.printStackTrace();
        }

        return tweets;
    }

    public  ArrayList<Tweet> decodePolarityResults(String input){
        ArrayList <Tweet> tweets = new ArrayList<Tweet>();
        try {
            JSONObject jObj = new JSONObject(input);
            JSONArray jTweetResults = jObj.getJSONArray("data");
            for(int i = 0; i < jTweetResults.length(); ++i){
                JSONObject jResult = jTweetResults.getJSONObject(i);
                Tweet tweet = new Tweet();
                tweet.setId(jResult.getLong("id"));
                tweet.setText(jResult.getString("text"));
                tweet.setPolarity(jResult.getInt("polarity"));
                tweets.add(tweet);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return tweets;
    }

    public ArrayList <Review> decodeReviews(String input){
        ArrayList <Review> reviews = new ArrayList<Review>();

        try {
            JSONObject jObj = new JSONObject(input);
            JSONArray jReviews = jObj.getJSONArray("reviews");

            for(int i = 0; i < jReviews.length(); ++i){
                JSONObject jReview = jReviews.getJSONObject(i);
                Review review = new Review();
                review.setAuthor(jReview.getString("critic"));
                review.setReviewText(jReview.getString("quote"));
                reviews.add(review);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return  reviews;
    }

    public ArrayList <Role> decodeCast(String input){
        ArrayList <Role> cast = new ArrayList<Role>();

        try {
            JSONObject jObj = new JSONObject(input);
            JSONArray jCast = jObj.getJSONArray("cast");
            for(int i = 0; i < jCast.length(); ++i){
                JSONObject jRole = jCast.getJSONObject(i);
                Role role = new Role();

                role.setRealName(jRole.getString("name"));

                try{
                    //some object can contain empty arrays
                    JSONArray jCharacters = jRole.getJSONArray("characters");
                    String character = (String) jCharacters.get(0);
                    role.setCharacterName(character);
                    cast.add(role);
                }catch(Exception e){
                    e.printStackTrace();
                }

            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return  cast;
    }

    // network check function
    public static boolean isOnline(Context context) {
        ConnectivityManager cm =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        return cm.getActiveNetworkInfo() != null &&
                cm.getActiveNetworkInfo().isConnectedOrConnecting();
    }

    // network check fail error message
    public static void showNoConnectionError(Context context){
        Toast.makeText(context, "No internet connection!", Toast.LENGTH_LONG).show();
    }
}
