// YouTube player related code was taken from following link
// http://android-er.blogspot.com.au/2013/06/example-to-use-youtubeplayerfragment-of.html

package com.fit4039.chatura.moviegram;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.fit4039.chatura.moviegram.model.Bookmark;
import com.fit4039.chatura.moviegram.model.Movie;
import com.fit4039.chatura.moviegram.model.Role;
import com.fit4039.chatura.moviegram.model.Util;
import com.google.android.youtube.player.YouTubeBaseActivity;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerFragment;
import com.google.android.youtube.player.YouTubePlayerView;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

import io.realm.Realm;
import io.realm.RealmQuery;
import io.realm.RealmResults;


/**
 * Created by hp on 5/18/2015.
 */

// handles specific movie related functions
public class MovieActivity extends ActionBarActivity implements
        YouTubePlayer.OnInitializedListener, AsyncTaskListener {

    Movie selectedMovie;
    ArrayList <Movie> similarMovies;
    ProgressDialog dialog;
    Menu menu;
    boolean isBookmarked;
    int movieID;

    YouTubePlayer youTubePlayer;
    private YouTubePlayerFragment youTubePlayerFragment;
    private static final int RQS_ErrorDialog = 1;
    private MyPlayerStateChangeListener myPlayerStateChangeListener;
    private MyPlaybackEventListener myPlaybackEventListener;
    String log = "";

    ImageView ivBanner;
    TextView tvMovieTitle;
    ImageView ivPoster;
    TextView tvReleasedDate;
    TextView tvGenre;
    TextView tvRating;
    TextView tvRuntime;
    TextView tvScore;
    TextView tvDirector;
    TextView tvStudio;
    TextView tvSynopsis;
    RelativeLayout relReviews;
    RelativeLayout relCast;
    RelativeLayout relSimilarMovies;
    TextView tvSimilarTitle1;
    TextView tvSimilarTitle2;
    TextView tvSimilarTitle3;
    ImageView ivSimilarImage1;
    ImageView ivSimilarImage2;
    ImageView ivSimilarImage3;
    ImageButton ivReviews;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie);

        //getting the movie ID passed from the previous screen
        movieID = getIntent().getIntExtra("MOVIE_ID", 0);
        downloadMovie(movieID);


    }

    private void downloadMovie(int movieID){
        // downloading basic movie info
        String URL = "http://api.rottentomatoes.com/api/public/v1.0/movies/" + movieID + ".json?apikey=" + Util.RTAPIKey;
        new GetAsyncTask(this, this, TaskType.DOWNLOAD_MOVIE).execute(URL);
    }

    @Override
    public void onInitializationSuccess(YouTubePlayer.Provider provider, YouTubePlayer youTubePlayer, boolean b) {
        this.youTubePlayer = youTubePlayer;
        youTubePlayer.setPlayerStateChangeListener(myPlayerStateChangeListener);
        youTubePlayer.setPlaybackEventListener(myPlaybackEventListener);

        if (!b) {
            youTubePlayer.cueVideo(selectedMovie.getYoutubeID());
        }
    }

    @Override
    public void onInitializationFailure(YouTubePlayer.Provider provider, YouTubeInitializationResult youTubeInitializationResult) {
        if (youTubeInitializationResult.isUserRecoverableError()) {
            youTubeInitializationResult.getErrorDialog(this, RQS_ErrorDialog).show();
        } else {
            Toast.makeText(this,
                    "YouTubePlayer.onInitializationFailure(): " + youTubeInitializationResult.toString(),
                    Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onTaskCompleted(String result, TaskType taskType) {
        // basic movie information download complete
        if(taskType == TaskType.DOWNLOAD_MOVIE){
            dialog = ProgressDialog.show(this, "Loading", "Please wait...", true);
            selectedMovie = new Util().decodeMovie(result);
            String title = null;
            try {
                title = URLEncoder.encode(selectedMovie.getTitle(), "UTF-8");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            String year = String.valueOf(selectedMovie.getReleaseDate().getYear() + 1900);
            String URL = "http://api.themoviedb.org/3/search/movie?api_key=" + Util.TMDBAPIKey
                    + "&query=" + title
                    + "&year=" + year;
            // downloading movie poster/banner
            new DownloadImageLinkTask(this,this, TaskType.MOVIE_IMAGES, selectedMovie).execute(URL);
        }else if(taskType == TaskType.MOVIE_IMAGES){
            // movie images download complete
            // getting youtube video id for this movie
            String query = selectedMovie.getTitle() + " " + selectedMovie.getYear() + " " + "trailer";
            try{
                query = URLEncoder.encode(query, "UTF-8");
            }catch(Exception e){
                e.printStackTrace();
            }
            String youtubeURL = "https://www.googleapis.com/youtube/v3/search?part=snippet&q=" +
                    query + "&maxResults=1&key="+ Util.GoogleServerKey;
            new GetAsyncTask(this, this, TaskType.GET_YOUTUBE_ID).execute(youtubeURL);
        }else if(taskType == TaskType.GET_YOUTUBE_ID){
            // got youtube ID
            new Util().decodeYouTubeID(selectedMovie, result);
            // downloading similar movies
            String URL = "http://api.rottentomatoes.com/api/public/v1.0/movies/" + selectedMovie.getMovieID()
                    + "/similar.json?apikey=" + Util.RTAPIKey + "&limit=3";
            new GetAsyncTask(this, this, TaskType.DOWNLOAD_SIMILAR_MOVIES).execute(URL);
        }else if(taskType == TaskType.DOWNLOAD_SIMILAR_MOVIES){
            // similar movies download complete
            similarMovies = new Util().decodeMovies(result);
            // if similar movie count is less than 3, it is not displayed to user.
            if((similarMovies.size() > 2)){
                for(Movie movie : similarMovies){
                    String title = null;
                    try {
                        title = URLEncoder.encode(movie.getTitle(), "UTF-8");
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }
                    // getting posters for similar movies
                    String year = String.valueOf(movie.getReleaseDate().getYear() + 1900);
                    String URL = "http://api.themoviedb.org/3/search/movie?api_key=" + Util.TMDBAPIKey
                            + "&query=" + title
                            + "&year=" + year;
                    new DownloadImageLinkTask(this,this, TaskType.SIMILAR_MOVIE_IMAGES, movie).execute(URL);
                }
            }else{
                // no matching similar movies. So populating UI now
                dialog.dismiss();
                initComponents();
            }
        }
        else if(taskType == TaskType.SIMILAR_MOVIE_IMAGES){
            // got images for similar movies
            boolean status = true;
            for(Movie movie : similarMovies){
                if(movie.getPosters().getBackdrop() == null){
                    status = false;
                }
            }
            if (status == true){
                dialog.dismiss();
                initComponents();
            }
        }

    }

    // populating UI
    private void initComponents(){

        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle(selectedMovie.getTitle());
        actionBar.setDisplayHomeAsUpEnabled(true);

        ivBanner = (ImageView) findViewById(R.id.ivMovieBanner);
        tvMovieTitle = (TextView) findViewById(R.id.tvMovieTitle);
        ivPoster = (ImageView) findViewById(R.id.ivMoviePoster);
        tvReleasedDate = (TextView) findViewById(R.id.tvMovieReleasedDate);
        tvGenre = (TextView) findViewById(R.id.tvMovieGenre);
        tvRating = (TextView) findViewById(R.id.tvMovieRating);
        tvRuntime = (TextView) findViewById(R.id.tvMovieDuration);
        tvScore = (TextView) findViewById(R.id.tvMovieScore);
        tvDirector = (TextView) findViewById(R.id.tvMovieDirector);
        tvStudio = (TextView) findViewById(R.id.tvMovieStudio);
        tvSynopsis = (TextView) findViewById(R.id.tvMovieSynopsis);
        relSimilarMovies = (RelativeLayout) findViewById(R.id.relativeLayout6);
        tvSimilarTitle1 = (TextView) findViewById(R.id.tvMovieSimilar1);
        tvSimilarTitle2 = (TextView) findViewById(R.id.tvMovieSimilar2);
        tvSimilarTitle3 = (TextView) findViewById(R.id.tvMovieSimilar3);
        ivSimilarImage1 = (ImageView) findViewById(R.id.ivMovieSimilar1);
        ivSimilarImage2 = (ImageView) findViewById(R.id.ivMovieSimilar2);
        ivSimilarImage3 = (ImageView) findViewById(R.id.ivMovieSimilar3);
        ivReviews = (ImageButton) findViewById(R.id.btnMovieShowReviews);
        relCast = (RelativeLayout) findViewById(R.id.relativeLayout5);
        relReviews = (RelativeLayout) findViewById(R.id.relativeLayout4);

        // loading posters and banners asynchronously
        ImageLoader imageLoader = ImageLoader.getInstance();
        imageLoader.displayImage(selectedMovie.getPosters().getBackdrop(), ivBanner);
        imageLoader.displayImage(selectedMovie.getPosters().getProfile2(), ivPoster);


        tvMovieTitle.setText(selectedMovie.getTitle());
        SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy");
        tvReleasedDate.setText(sdf.format(selectedMovie.getReleaseDate()));

        // displaying multiple genres as a single line
        String genre = "";
        for(int i = 0; i < selectedMovie.getGenres().size(); ++i){
            genre = genre + selectedMovie.getGenres().get(i).getGenreType();
            if(i != (selectedMovie.getGenres().size() - 1)){
                genre = genre + " | ";
            }
        }
        tvGenre.setText(genre);
        tvRating.setText(selectedMovie.getRating());
        tvRuntime.setText(selectedMovie.getRuntime() + " mins");
        tvScore.setText(selectedMovie.getAudienceScore() + "/100");
        tvDirector.setText(selectedMovie.getDirector());
        tvStudio.setText(selectedMovie.getStudio());
        tvSynopsis.setText(selectedMovie.getSynopsis());

        // loading youtube video
        youTubePlayerFragment = (YouTubePlayerFragment)getFragmentManager()
                .findFragmentById(R.id.youtubeplayerfragment);
        youTubePlayerFragment.initialize(Util.GoogleAndroidKey, this);

        myPlayerStateChangeListener = new MyPlayerStateChangeListener();
        myPlaybackEventListener = new MyPlaybackEventListener();

        if(similarMovies.size() > 2){
            relSimilarMovies.setVisibility(View.VISIBLE);
            tvSimilarTitle1.setText(similarMovies.get(0).getTitle());
            tvSimilarTitle2.setText(similarMovies.get(1).getTitle());
            tvSimilarTitle3.setText(similarMovies.get(2).getTitle());
            imageLoader.displayImage(similarMovies.get(0).getPosters().getProfile2(), ivSimilarImage1);
            imageLoader.displayImage(similarMovies.get(1).getPosters().getProfile2(), ivSimilarImage2);
            imageLoader.displayImage(similarMovies.get(2).getPosters().getProfile2(), ivSimilarImage3);
        }else{
            // hiding UI component for similar movies since no similar movies were not found
            relSimilarMovies.setVisibility(View.INVISIBLE);
            RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) relSimilarMovies.getLayoutParams();
            params.height = 0;
        }

        relReviews.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // calling reviews screen
                Intent intent = new Intent(MovieActivity.this, ReviewActivity.class);
                intent.putExtra("MovieTitle", selectedMovie.getTitle());
                intent.putExtra("MovieID", selectedMovie.getMovieID());
                intent.putExtra("MovieYear", selectedMovie.getYear());
                startActivity(intent);
            }
        });

        relCast.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // calling casts screen
                Intent intent = new Intent(MovieActivity.this, CastActivity.class);
                intent.putExtra("MovieTitle", selectedMovie.getTitle());
                intent.putExtra("MovieID", selectedMovie.getMovieID());
                startActivity(intent);
            }
        });

        checkBookmarkedStatus();

    }

    // youtube player related operations
    private final class MyPlayerStateChangeListener implements YouTubePlayer.PlayerStateChangeListener {

        private void updateLog(String prompt){
            log +=  "MyPlayerStateChangeListener" + "\n" +
                    prompt + "\n\n=====";
            //Toast.makeText(getApplicationContext(), log, Toast.LENGTH_LONG).show();
        };

        @Override
        public void onAdStarted() {
            updateLog("onAdStarted()");
        }

        @Override
        public void onError(
                com.google.android.youtube.player.YouTubePlayer.ErrorReason arg0) {
            updateLog("onError(): " + arg0.toString());
        }

        @Override
        public void onLoaded(String arg0) {
            updateLog("onLoaded(): " + arg0);
        }

        @Override
        public void onLoading() {
            updateLog("onLoading()");
        }

        @Override
        public void onVideoEnded() {
            updateLog("onVideoEnded()");
        }

        @Override
        public void onVideoStarted() {
            updateLog("onVideoStarted()");
        }

    }

    private final class MyPlaybackEventListener implements YouTubePlayer.PlaybackEventListener {

        private void updateLog(String prompt){
            log +=  "MyPlaybackEventListener" + "\n-" +
                    prompt + "\n\n=====";
            //Toast.makeText(getApplicationContext(), log, Toast.LENGTH_LONG).show();
        };

        @Override
        public void onBuffering(boolean arg0) {
            updateLog("onBuffering(): " + String.valueOf(arg0));
        }

        @Override
        public void onPaused() {
            updateLog("onPaused()");
        }

        @Override
        public void onPlaying() {
            updateLog("onPlaying()");
        }

        @Override
        public void onSeekTo(int arg0) {
            updateLog("onSeekTo(): " + String.valueOf(arg0));
        }

        @Override
        public void onStopped() {
            updateLog("onStopped()");
        }

    }

    // downloading image links
    private class DownloadImageLinkTask extends AsyncTask<String, Void, String> implements Serializable {
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        this.menu = menu;
        getMenuInflater().inflate(R.menu.menu_movie, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();
        if (id == R.id.action_bookmark) {
            if(isBookmarked == false){
                bookmarkMovie();
            }else{
                removeBookmark();
            }
        }if(id == android.R.id.home){
            this.finish();
        }

        return super.onOptionsItemSelected(item);
    }

    // creating bookmark
    private void bookmarkMovie(){
        String posterFileName = selectedMovie.getMovieID()+".png";
        String imagePath = saveToInternalSorage(((BitmapDrawable) ivPoster.getDrawable()).getBitmap(), posterFileName);
        imagePath = imagePath + "/" + posterFileName;

        // adding bookmark to the database
        Realm realm = Realm.getInstance(this);
        realm.beginTransaction();
        Bookmark bookmark = realm.createObject(Bookmark.class); // new bookmark instance
        bookmark.setMovieID(selectedMovie.getMovieID());
        bookmark.setMovieTitle(selectedMovie.getTitle());
        bookmark.setGenre(tvGenre.getText().toString());
        bookmark.setPosterFileName(imagePath);
        realm.commitTransaction();

        Toast.makeText(this, selectedMovie.getTitle() + " saved to bookmarks", Toast.LENGTH_LONG).show();
        menu.getItem(0).setIcon(getResources().getDrawable(R.drawable.bookmark_delete)); // changing icon
        isBookmarked = true;
    }

    private void removeBookmark(){
        // search for book and remove it
        Realm realm = Realm.getInstance(this);
        realm.beginTransaction();
        Bookmark bookmark = realm.where(Bookmark.class).equalTo("movieID", selectedMovie.getMovieID()).findFirst();
        bookmark.removeFromRealm();
        realm.commitTransaction();

        Toast.makeText(this, selectedMovie.getTitle() + " removed from bookmarks", Toast.LENGTH_LONG).show();
        menu.getItem(0).setIcon(getResources().getDrawable(R.drawable.bookmark)); // changing icon
        isBookmarked = false;
    }

    private String saveToInternalSorage(Bitmap bitmapImage, String fileName){
        ContextWrapper cw = new ContextWrapper(getApplicationContext());
        // path to /data/data/yourapp/app_data/imageDir
        File directory = cw.getDir("MoviePosters", Context.MODE_PRIVATE);
        // Create imageDir
        File mypath=new File(directory,fileName);

        FileOutputStream fos = null;
        try {

            fos = new FileOutputStream(mypath);

            // Use the compress method on the BitMap object to write image to the OutputStream
            bitmapImage.compress(Bitmap.CompressFormat.PNG, 100, fos);
            fos.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        String s = directory.getAbsolutePath();
        return directory.getAbsolutePath();
    }

    // to check this movie is bookmarked before (prevents duplicate bookmarks)
    private void checkBookmarkedStatus(){
        Realm realm = Realm.getInstance(this);
        RealmQuery<Bookmark> query = realm.where(Bookmark.class);
        query.equalTo("movieID",movieID);

        RealmResults<Bookmark> result = query.findAll();
        if(result.size() > 0){
            isBookmarked = true;
            menu.getItem(0).setIcon(getResources().getDrawable(R.drawable.bookmark_delete));
        }else{
            isBookmarked = false;
            menu.getItem(0).setIcon(getResources().getDrawable(R.drawable.bookmark));
        }
    }
}
