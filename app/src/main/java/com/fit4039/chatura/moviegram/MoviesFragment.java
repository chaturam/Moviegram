package com.fit4039.chatura.moviegram;
//https://github.com/daimajia/AndroidImageSlider
import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.daimajia.slider.library.Animations.DescriptionAnimation;
import com.daimajia.slider.library.SliderLayout;
import com.daimajia.slider.library.SliderTypes.BaseSliderView;
import com.daimajia.slider.library.SliderTypes.TextSliderView;
import com.daimajia.slider.library.Tricks.ViewPagerEx;
import com.fit4039.chatura.moviegram.model.Movie;
import com.fit4039.chatura.moviegram.model.Util;

import java.util.ArrayList;

/**
 * Created by hp on 5/7/2015.
 */

// handles movies list screen
public class MoviesFragment extends Fragment implements
        BaseSliderView.OnSliderClickListener,
        ViewPagerEx.OnPageChangeListener {

    View view;
    private SliderLayout mDemoSlider;
    ArrayList <Movie> moviesList;
    ProgressDialog dialog;
    ListView lvMovies;
    MoviesAdapter moviesAdapter;
    TextView tvHeader;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_movie, container, false);
        mDemoSlider = (SliderLayout)view.findViewById(R.id.slider);
        lvMovies = (ListView) view.findViewById(R.id.lvBoxOffice);

        // retrieving movie list passed by Main activity
        moviesList = (ArrayList<Movie>) getArguments().getSerializable("MOVIESLIST");

        lvMovies.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if(Util.isOnline(getActivity())){
                    // handling movie selection
                    int movieID = moviesList.get(position).getMovieID();
                    Intent intent = new Intent(getActivity(), MovieActivity.class);
                    intent.putExtra("MOVIE_ID", movieID);
                    getActivity().startActivity(intent);
                }else{
                    Util.showNoConnectionError(getActivity());
                }

            }
        });

        initComponents();
        return view;
    }

    // populating UI
    private void initComponents(){
        for(Movie movie : moviesList){
            if((movie.getPosters().getBackdrop() != null) && (!movie.getPosters().getBackdrop().equals(""))){
                TextSliderView textSliderView = new TextSliderView(getActivity());
                // initialize a SliderLayout

                textSliderView
                        .description(movie.getTitle())
                        .image(movie.getPosters().getBackdrop())
                        .setScaleType(BaseSliderView.ScaleType.Fit)
                        .setOnSliderClickListener(this);

                mDemoSlider.addSlider(textSliderView);
            }

            mDemoSlider.setPresetTransformer(SliderLayout.Transformer.Accordion);
            mDemoSlider.setPresetIndicator(SliderLayout.PresetIndicators.Center_Top);
            mDemoSlider.setCustomAnimation(new DescriptionAnimation());
            mDemoSlider.setDuration(5000);
            }

        //mDemoSlider.addOnPageChangeListener(this);

       moviesAdapter = new MoviesAdapter(getActivity(), moviesList);
        lvMovies.setAdapter(moviesAdapter);

    }

    @Override
    public void onPageScrolled(int i, float v, int i2) {

    }

    @Override
    public void onPageSelected(int i) {

    }

    @Override
    public void onPageScrollStateChanged(int i) {

    }

    @Override
    public void onSliderClick(BaseSliderView baseSliderView) {

    }

    @Override
    public void onStop() {
        super.onStop();
        mDemoSlider.stopAutoCycle();
    }

}
