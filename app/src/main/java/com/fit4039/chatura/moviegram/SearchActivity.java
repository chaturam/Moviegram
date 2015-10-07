package com.fit4039.chatura.moviegram;

import android.app.ProgressDialog;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.fit4039.chatura.moviegram.model.Movie;
import com.fit4039.chatura.moviegram.model.Util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;

/**
 * Created by peercoreit on 18/05/15.
 */

// handles search activity operations
public class SearchActivity extends ActionBarActivity implements AsyncTaskListener {

    ArrayList<Movie> movies;
    ProgressDialog dialog;
    ListView lvMovies;
    MoviesAdapter moviesAdapter;
    TextView tvSearchText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activty_search);

        lvMovies = (ListView) findViewById(R.id.lvSearchResult);
        tvSearchText = (TextView) findViewById(R.id.tvSearchText);
        ActionBar actionBar = getSupportActionBar();


        // Enabling Back navigation on Action Bar icon
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle("Search Results");
        handleIntent(getIntent());

        lvMovies.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                int movieID = movies.get(position).getMovieID();
                Intent intent = new Intent(SearchActivity.this, MovieActivity.class);
                intent.putExtra("MOVIE_ID", movieID);
                startActivity(intent);
            }
        });


    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        handleIntent(intent);
    }

    // retrieving search term
    private void handleIntent(Intent intent) {
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            String query = intent.getStringExtra(SearchManager.QUERY);
            tvSearchText.setText(query);
            downloadMovies(query);


        }

    }

    // downloading basic movie information for matching results
    private void downloadMovies(String query){
        try {
            query = URLEncoder.encode(query, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        String URL = "http://api.rottentomatoes.com/api/public/v1.0/movies.json?apikey=" + Util.RTAPIKey + "&q=" + query +"&page_limit=10&page=1";
        new GetAsyncTask(this, this, TaskType.DOWNLOAD_MOVIES).execute(URL);
    }

    @Override
    public void onTaskCompleted(String result, TaskType taskType) {
        // basic information download complete
        if(taskType == TaskType.DOWNLOAD_MOVIES){
            movies = new Util().decodeMovies(result);
            dialog = ProgressDialog.show(this, "Loading", "Please wait...", true);
            for(Movie movie : movies){
                // adding additional posters
                String title = null;
                try {
                    title = URLEncoder.encode(movie.getTitle(), "UTF-8");
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
                String year = String.valueOf(movie.getReleaseDate().getYear() + 1900);
                String URL = "http://api.themoviedb.org/3/search/movie?api_key=" + Util.TMDBAPIKey
                        + "&query=" + title
                        + "&year=" + year;

                // downloading poster link for each movie
                new DownloadImageLinkTask(this,this, TaskType.MOVIE_IMAGES, movie).execute(URL);

            }
        }
        if(taskType == TaskType.MOVIE_IMAGES){
            boolean status = true;
            for(Movie movie : movies){
                if(movie.getPosters().getBackdrop() == null){
                    status = false;
                }
            }

            if (status == true){
                // populating listview
                dialog.dismiss();
                moviesAdapter = new MoviesAdapter(this, movies);
                lvMovies.setAdapter(moviesAdapter);
            }
        }
    }

    // image link download
    private class DownloadImageLinkTask extends AsyncTask<String, Void, String> {
        Movie movie;
        String outputStr;
        AsyncTaskListener taskListener;
        TaskType taskType;
        Context context;
        public DownloadImageLinkTask(Context context, AsyncTaskListener taskListener, TaskType taskType, Movie movie){
            this.context = context;
            this.movie = movie;
            this.taskListener = taskListener;
            this.taskType = taskType;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

        }

        @Override
        protected String doInBackground(String... params) {
            String urlString = params[0];
            try {

                URL url = new URL(urlString);
                HttpURLConnection conn
                        = (HttpURLConnection) url.openConnection();
                conn.setConnectTimeout(5000);
                if (conn.getResponseCode() != 200) {
                    throw new IOException(conn.getResponseMessage());
                }

                // Buffer the result into a string
                BufferedReader rd = new BufferedReader(
                        new InputStreamReader(conn.getInputStream()));
                StringBuilder sb = new StringBuilder();
                String line;
                while ((line = rd.readLine()) != null) {
                    sb.append(line);
                }
                rd.close();
                conn.disconnect();
                String jsonStr = sb.toString();

                outputStr = jsonStr;
            } catch (SocketTimeoutException e) {
                outputStr = "timeout";
            }catch(Exception e){
                e.printStackTrace();
                outputStr = "error";
            }

            return outputStr;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            new Util().decodePosters(movie, s);
            taskListener.onTaskCompleted(s, taskType);
        }
    }
}
