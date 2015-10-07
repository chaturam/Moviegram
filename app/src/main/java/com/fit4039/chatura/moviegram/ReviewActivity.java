

package com.fit4039.chatura.moviegram;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.fit4039.chatura.moviegram.model.Review;
import com.fit4039.chatura.moviegram.model.Tweet;
import com.fit4039.chatura.moviegram.model.Util;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.github.mikephil.charting.utils.Highlight;
import com.google.gson.Gson;

import java.util.ArrayList;

/**
 * Created by hp on 6/3/2015.
 */

// handles review screen
public class ReviewActivity extends ActionBarActivity implements AsyncTaskListener, OnChartValueSelectedListener {
    ArrayList <Tweet> tweets;
    ArrayList <Tweet> results;
    ArrayList <Review> reviews;
    int movieID;
    String movieTitle;
    int movieYear;
    ReviewsAdapter reviewsAdapter;

    PieChart pieChart;
    TextView tvMovieTitle;
    ListView lvReviews;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_review);

        tvMovieTitle = (TextView) findViewById(R.id.tvReviewMovieTitle);
        lvReviews = (ListView) findViewById(R.id.lvReviewRT);

        // getting basic movie information from movie activity
        movieTitle = getIntent().getStringExtra("MovieTitle");
        movieID = getIntent().getIntExtra("MovieID", 0);
        movieYear = getIntent().getIntExtra("MovieYear", 0);
        String query = movieTitle + " movie";

        lvReviews.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // showing full review description suing dialog box
                Review review = reviews.get(position);
                String msg = review.getReviewText() + " ~ by " + review.getAuthor();
                AlertDialog.Builder builder1 = new AlertDialog.Builder(ReviewActivity.this);
                builder1.setTitle("Review for " + movieTitle);
                builder1.setMessage(msg);
                builder1.setCancelable(true);
                builder1.setNeutralButton(android.R.string.ok,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });

                AlertDialog alert11 = builder1.create();
                alert11.show();
            }
        });

        new TweetDownloader(ReviewActivity.this, this, TaskType.DOWNLOAD_TWEETS).execute(query);
    }

    @Override
    public void onTaskCompleted(String result, TaskType taskType) {
        // tweet download completed
        if(taskType == TaskType.DOWNLOAD_TWEETS) {
            tweets = new Util().decodeTweets(result);
            String jsonString = new Gson().toJson(tweets);
            jsonString = "{\"data\": " + jsonString + "}";

            String URL = "http://www.sentiment140.com/api/bulkClassifyJson?" + "appid=chaturamabotuwana@gmail.com";

            // uploading tweets for sentiment analysis
            new PostAsyncTask(ReviewActivity.this, this, TaskType.DOWNLOAD_SENTIMENT_RESULTS).execute(URL, jsonString);
        }else if(taskType == TaskType.DOWNLOAD_SENTIMENT_RESULTS) {
            // sentiment analysis completed
            results = new Util().decodePolarityResults(result);
            int pageLimit = 10;
            String URL = "http://api.rottentomatoes.com/api/public/v1.0/movies/" + movieID + "/reviews.json?apikey="
                    + Util.RTAPIKey + "&page_limit=" + pageLimit;
            // dowmloading rotten tomatoes reviews
            new GetAsyncTask(ReviewActivity.this, this, TaskType.DOWNLOAD_REVIEWS).execute(URL);

        }else if(taskType == TaskType.DOWNLOAD_REVIEWS){
            reviews = new Util().decodeReviews(result);
            initComponents();
        }
    }

    // populating the UI
    private void initComponents(){
        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("Reviews");
        actionBar.setDisplayHomeAsUpEnabled(true);

        pieChart = (PieChart) findViewById(R.id.chart1);
        loadPieChart();



        tvMovieTitle.setText(movieTitle);

        reviewsAdapter = new ReviewsAdapter(ReviewActivity.this, reviews);
        lvReviews.setAdapter(reviewsAdapter);
    }

    private void loadPieChart(){
        // calculating total votes
        double good = 0;
        double bad = 0;
        double neutral = 0;

        for(Tweet tweet : results){
            if(tweet.getPolarity() == 0){
                ++bad;
            }else if(tweet.getPolarity() == 2){
                ++neutral;
            }else if(tweet.getPolarity() == 4){
                ++good;
            }
        }

        pieChart.setHoleRadius(40f);
        pieChart.setDrawCenterText(true);
        pieChart.setDrawHoleEnabled(true);
        pieChart.setRotationAngle(0);
        pieChart.setCenterText("Reviews");
        pieChart.setDescription("");
        pieChart.setRotationEnabled(false);
        pieChart.setUsePercentValues(true);
        pieChart.setOnChartValueSelectedListener(this);
        pieChart.setTouchEnabled(false);
        setData(good, bad, neutral);
        pieChart.animateXY(1500, 1500);
    }

    // setting data for pie chart
    private void setData(double good, double bad, double neutral) {
        ArrayList<Entry> yVals1 = new ArrayList<Entry>();

        yVals1.add(new Entry((float) good, 0));
        yVals1.add(new Entry((float) bad, 0));
        yVals1.add(new Entry((float) neutral, 0));

        ArrayList<String> xVals = new ArrayList<String>();

        xVals.add("Good");
        xVals.add("Bad");
        xVals.add("Neutral");

        PieDataSet set1 = new PieDataSet(yVals1, "Reviews");
        set1.setSliceSpace(3f);

        ArrayList<Integer> colors = new ArrayList<Integer>();

        for (int c : ColorTemplate.VORDIPLOM_COLORS)
            colors.add(c);

        colors.remove(0);
        colors.remove(0);

        set1.setColors(colors);
        PieData data = new PieData(xVals, set1);
        pieChart.setData(data);
        pieChart.highlightValues(null);
        pieChart.invalidate();
    }

    @Override
    public void onValueSelected(Entry e, int dataSetIndex, Highlight h) {

    }

    @Override
    public void onNothingSelected() {

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
