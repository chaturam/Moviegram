package com.fit4039.chatura.moviegram;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.support.v7.internal.view.menu.MenuView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.fit4039.chatura.moviegram.model.Movie;
import com.fit4039.chatura.moviegram.model.Role;
import com.nostra13.universalimageloader.cache.memory.impl.WeakMemoryCache;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;

import java.io.InputStream;
import java.util.ArrayList;

import io.realm.RealmList;

/**
 * Created by hp on 5/17/2015.
 */
//adapter for movies page listview
public class MoviesAdapter extends ArrayAdapter<Movie> {
    Context context;
    ArrayList<Movie> moviesList;

    ImageView ivThumbnail;
    TextView tvTitle;
    TextView tvYear;
    TextView tvRating;
    TextView tvScore;
    TextView tvCast;
    ImageView ivRating;
    public MoviesAdapter(Context context, ArrayList<Movie> moviesList){
        super(context, R.layout.item_movie, moviesList);
        this.context = context;
        this.moviesList = moviesList;

        // UNIVERSAL IMAGE LOADER SETUP
        DisplayImageOptions defaultOptions = new DisplayImageOptions.Builder()
                .cacheOnDisc(true).cacheInMemory(true)
                .imageScaleType(ImageScaleType.EXACTLY)
                .displayer(new FadeInBitmapDisplayer(300)).build();

        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(
                 context)
                .defaultDisplayImageOptions(defaultOptions)
                .memoryCache(new WeakMemoryCache())
                .discCacheSize(100 * 1024 * 1024).build();

        ImageLoader.getInstance().init(config);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View row = inflater.inflate(R.layout.item_movie, parent, false);
        ivThumbnail = (ImageView) row.findViewById(R.id.ivItemMovieThumbnail);
        tvTitle = (TextView) row.findViewById(R.id.tvItemMovieTitle);
        tvYear = (TextView) row.findViewById(R.id.tvItemMovieYear);
        tvRating = (TextView) row.findViewById(R.id.tvItemMovieRating);
        tvScore = (TextView) row.findViewById(R.id.tvItemMovieScore);
        tvCast = (TextView) row.findViewById(R.id.tvItemMovieCast);
        ivRating = (ImageView) row.findViewById(R.id.ivRating);

        Typeface customFont = Typeface.createFromAsset(context.getAssets(), "fonts/OpenSans-Regular.ttf");
        tvTitle.setTypeface(customFont);

        Movie movie = moviesList.get(position);

        ImageLoader imageLoader = ImageLoader.getInstance();
        imageLoader.displayImage(movie.getPosters().getProfile2(), ivThumbnail); // loading image
        tvTitle.setText(movie.getTitle());
        String year = String.valueOf(movie.getReleaseDate().getYear() + 1900);
        tvYear.setText("(" + year + ")");
        tvRating.setText(movie.getRating());
        tvScore.setText(movie.getAudienceScore() + "/100");

        //arraging names of the cast as a single string
        String cast = "";
        for(Role role : movie.getCast()){
            cast = cast + role.getRealName() + ", ";
        }
        tvCast.setText(cast);


        // applying "star rating" based on audience score
        int rating = movie.getAudienceScore();
        if((rating >= 0) && (rating < 20)){
            ivRating.setImageDrawable(context.getResources().getDrawable(R.drawable.rating_1));
        }else if((rating >= 20) && (rating < 40)){
            ivRating.setImageDrawable(context.getResources().getDrawable(R.drawable.rating_2));
        }else if((rating >= 40) && (rating < 60)){
            ivRating.setImageDrawable(context.getResources().getDrawable(R.drawable.rating_3));
        }else if((rating >= 60) && (rating < 80)){
            ivRating.setImageDrawable(context.getResources().getDrawable(R.drawable.rating_4));
        }else if((rating >= 80) && (rating <= 100)){
            ivRating.setImageDrawable(context.getResources().getDrawable(R.drawable.rating_5));
        }


        return  row;
    }
}
