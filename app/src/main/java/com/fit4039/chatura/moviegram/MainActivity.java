package com.fit4039.chatura.moviegram;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.ProgressDialog;
import android.app.SearchManager;
import android.content.Context;
import android.content.res.Configuration;
import android.os.AsyncTask;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SearchView;

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


//new comment
// this class sets up the navigation drawer and load appropriate movie list and pass it to fragment.
public class MainActivity extends ActionBarActivity implements AsyncTaskListener {
    private DrawerLayout mDrawerLayout;
    private ListView mDrawerList;
    private ActionBarDrawerToggle mDrawerToggle;

    ArrayList <Movie> moviesList;// selected movie list (box office, new releases etc)
    ProgressDialog dialog; // to display until data loading finishes
    Fragment fragment = null;
    int position; // selected nav draw index

    // nav drawer title
    private CharSequence mDrawerTitle;

    // used to store app title
    private CharSequence mTitle;


    private String[] navMenuTitles; // nav menu names list
    private ArrayList<NavDrawerItem> navDrawerItems; // stores nav draw icons
    private NavDrawerListAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mTitle = mDrawerTitle = getTitle();


        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerList = (ListView) findViewById(R.id.list_slidermenu);

        navMenuTitles = new String[6];
        navMenuTitles [0] = "Box Office";
        navMenuTitles [1] = "Now Showing";
        navMenuTitles [2] = "New Releases";
        navMenuTitles [3] = "Coming Soon";
        navMenuTitles [4] = "Bookmarks";
        navMenuTitles [5] = "About";

        navDrawerItems = new ArrayList<NavDrawerItem>();

        //adding drawables to the list
        navDrawerItems.add(new NavDrawerItem(navMenuTitles[0], R.drawable.box_office));
        navDrawerItems.add(new NavDrawerItem(navMenuTitles[1], R.drawable.now_showing));
        navDrawerItems.add(new NavDrawerItem(navMenuTitles[2], R.drawable.new_releases));
        navDrawerItems.add(new NavDrawerItem(navMenuTitles[3], R.drawable.coming_soon));
        navDrawerItems.add(new NavDrawerItem(navMenuTitles[4], R.drawable.bookmarks));
        navDrawerItems.add(new NavDrawerItem(navMenuTitles[5], R.drawable.about));

        mDrawerList.setOnItemClickListener(new SlideMenuClickListener());

        // setting the nav drawer list adapter
        adapter = new NavDrawerListAdapter(getApplicationContext(),
                navDrawerItems);
        mDrawerList.setAdapter(adapter);

        // enabling action bar app icon and behaving it as toggle button
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout,
                R.drawable.ic_drawer, // nav menu toggle icon
                R.string.app_name,
                R.string.app_name
        ) {
            public void onDrawerClosed(View view) {
                getSupportActionBar().setTitle(mTitle);
                // calling onPrepareOptionsMenu() to show action bar icons
                invalidateOptionsMenu();
            }

            public void onDrawerOpened(View drawerView) {
                getSupportActionBar().setTitle(mDrawerTitle);
                // calling onPrepareOptionsMenu() to hide action bar icons
                invalidateOptionsMenu();
            }
        };
        mDrawerLayout.setDrawerListener(mDrawerToggle);

        if (savedInstanceState == null) {
            // on first time display view for first nav item
            displayView(0);
        }
    }



    /**
     * Slide menu item click listener
     * */
    private class SlideMenuClickListener implements
            ListView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position,
                                long id) {
            // display view for selected nav drawer item
            displayView(position);
        }
    }

    private void displayView(int position) {
        // update the main content by replacing fragments
        // loading relevant movie list or view (bookmarks/abouts)
        String URL = "";
        switch (position) {
            case 0: //box office movies
                fragment = new MoviesFragment();
                this.position = position;
                URL = "http://api.rottentomatoes.com/api/public/v1.0/lists/movies/box_office.json?apikey=" + Util.RTAPIKey + "&limit=10";
                downloadMovies(URL);
                break;
            case 1: // now showing movies
                fragment = new MoviesFragment();
                this.position = position;
                URL = "http://api.rottentomatoes.com/api/public/v1.0/lists/movies/in_theaters.json?apikey=" + Util.RTAPIKey + "&limit=10";
                downloadMovies(URL);
                break;
            case 2: // new movies
                fragment = new MoviesFragment();
                this.position = position;
                URL = "http://api.rottentomatoes.com/api/public/v1.0/lists/movies/opening.json?apikey=" + Util.RTAPIKey + "&limit=10";
                downloadMovies(URL);
                break;
            case 3: // coming soon movies
                fragment = new MoviesFragment();
                this.position = position;
                URL = "http://api.rottentomatoes.com/api/public/v1.0/lists/movies/upcoming.json?apikey=" + Util.RTAPIKey + "&limit=10";
                downloadMovies(URL);
                break;
            case 4: // bookmarks
                fragment = new BookmarksFragment();
                if (fragment != null) {
                    FragmentManager fragmentManager = getFragmentManager();
                    fragmentManager.beginTransaction()
                            .replace(R.id.frame_container, fragment).commit();

                    // update selected item and title, then close the drawer
                    mDrawerList.setItemChecked(position, true);
                    mDrawerList.setSelection(position);
                    setTitle(navMenuTitles[position]);
                    mDrawerLayout.closeDrawer(mDrawerList);
                } else {
                    // error in creating fragment
                    Log.e("MainActivity", "Error in creating fragment");
                }
                break;
            case 5: // abouts page
                fragment = new AboutFragment();
                if (fragment != null) {
                    FragmentManager fragmentManager = getFragmentManager();
                    fragmentManager.beginTransaction()
                            .replace(R.id.frame_container, fragment).commit();

                    // update selected item and title, then close the drawer
                    mDrawerList.setItemChecked(position, true);
                    mDrawerList.setSelection(position);
                    setTitle(navMenuTitles[position]);
                    mDrawerLayout.closeDrawer(mDrawerList);
                } else {
                    // error in creating fragment
                    Log.e("MainActivity", "Error in creating fragment");
                }
                break;

            default:
                break;
        }

    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
        mDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mDrawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    public void setTitle(CharSequence title) {
        mTitle = title;
        getSupportActionBar().setTitle(mTitle);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);

        // Associate searchable configuration with the SearchView
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView = (SearchView) menu.findItem(R.id.action_search)
                .getActionView();
        searchView.setSearchableInfo(searchManager
                .getSearchableInfo(getComponentName()));

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // toggle nav drawer on selecting action bar app icon/title
        if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        // Handle action bar actions click
        switch (item.getItemId()) {

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void downloadMovies(String URL){
        // calling asyncTask
        new GetAsyncTask(this, this, TaskType.DOWNLOAD_MOVIES).execute(URL);
    }

    @Override
    public void onTaskCompleted(String result, TaskType taskType) {
        // basic movie information download complete
        if(taskType == TaskType.DOWNLOAD_MOVIES){
            moviesList = new Util().decodeMovies(result);
            dialog = ProgressDialog.show(this, "Loading", "Please wait...", true);
            // downloading posters/banners for each movie
            for(Movie movie : moviesList){
                // adding additional posters
                String title = null;
                try {
                    //encoding movie name
                    title = URLEncoder.encode(movie.getTitle(), "UTF-8");
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
                String year = String.valueOf(movie.getReleaseDate().getYear() + 1900);
                String URL = "http://api.themoviedb.org/3/search/movie?api_key=" + Util.TMDBAPIKey
                        + "&query=" + title
                        + "&year=" + year;

                // downloading posters/banners for each movie
                new DownloadImageLinkTask(this,this, TaskType.MOVIE_IMAGES, movie).execute(URL);

            }
        }

        if(taskType == TaskType.MOVIE_IMAGES){
            //poster/banner download complete
            boolean status = true; // checking all movies have finished downloading images
            for(Movie movie : moviesList){
                if(movie.getPosters().getBackdrop() == null){
                    status = false;
                }
            }

            if (status == true){
                dialog.dismiss();
                loadFragment();
            }
        }
    }

    // asyncTask for image downloading (not the actual image. just the image URL)
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

    private void loadFragment(){

        if (fragment != null) {
            // passing movie list as an argument to the fragment
            Bundle args = new Bundle();
            args.putSerializable("MOVIESLIST", moviesList);
            fragment.setArguments(args);
            FragmentManager fragmentManager = getFragmentManager();
            fragmentManager.beginTransaction()
                    .replace(R.id.frame_container, fragment).commit();

            // update selected item and title, then close the drawer
            mDrawerList.setItemChecked(position, true);
            mDrawerList.setSelection(position);
            setTitle(navMenuTitles[position]);
            mDrawerLayout.closeDrawer(mDrawerList);
        } else {
            // error in creating fragment
            Log.e("MainActivity", "Error in creating fragment");
        }
    }

}
