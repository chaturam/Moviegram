package com.fit4039.chatura.moviegram;

import android.app.Activity;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.TextView;

import com.fit4039.chatura.moviegram.model.Role;
import com.fit4039.chatura.moviegram.model.Util;

import org.w3c.dom.Text;

import java.util.ArrayList;

/**
 * Created by hp on 6/5/2015.
 */

//handles cast information of the movie
public class CastActivity extends ActionBarActivity implements AsyncTaskListener{
    TextView tvTitle;
    ListView lvCast;

    int movieID;
    String movieTitle;
    ArrayList<Role> cast;

    CastAdapter castAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cast);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("Cast and Crew");
        actionBar.setDisplayHomeAsUpEnabled(true);

        tvTitle = (TextView) findViewById(R.id.tvCastTitle);
        lvCast = (ListView) findViewById(R.id.lvCast);

        movieTitle = getIntent().getStringExtra("MovieTitle");
        movieID = getIntent().getIntExtra("MovieID", 0);

        tvTitle.setText("Cast of " + movieTitle);

        //calling asyncTask to download movie cast. Movie ID is given in the query string
        String URL = "http://api.rottentomatoes.com/api/public/v1.0/movies/" + movieID + "/cast.json?apikey=" + Util.RTAPIKey;
        new GetAsyncTask(CastActivity.this, this, TaskType.DOWNLOAD_CAST).execute(URL);

    }

    // after asyncTask completes its job it calls this method. Depending on the task type appropriate steps taken
    @Override
    public void onTaskCompleted(String result, TaskType taskType) {
        // cast information download completed. decoding the JSON result and filling it to listview
        if(taskType == TaskType.DOWNLOAD_CAST){
            cast = new Util().decodeCast(result);
            castAdapter = new CastAdapter(CastActivity.this, cast);
            lvCast.setAdapter(castAdapter);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if(id == android.R.id.home){
            this.finish();
        }
        return super.onOptionsItemSelected(item);
    }
}
