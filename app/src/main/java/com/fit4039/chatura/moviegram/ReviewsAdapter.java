package com.fit4039.chatura.moviegram;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.fit4039.chatura.moviegram.model.Review;

import java.util.ArrayList;

/**
 * Created by hp on 6/4/2015.
 */

// handles review page list view
public class ReviewsAdapter extends ArrayAdapter <Review> {
    Context context;
    ArrayList<Review> reviewsList;

    TextView tvReviewText;
    TextView tvAuthor;

    public ReviewsAdapter(Context context, ArrayList<Review> reviewsList) {
        super(context, R.layout.item_review, reviewsList);
        this.context = context;
        this.reviewsList = reviewsList;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View row = inflater.inflate(R.layout.item_review, parent, false);

        tvReviewText = (TextView) row.findViewById(R.id.tvReviewText);
        tvAuthor = (TextView) row.findViewById(R.id.tvReviewAuthor);

        Typeface customFont = Typeface.createFromAsset(context.getAssets(), "fonts/OpenSans-Regular.ttf");
        tvReviewText.setTypeface(customFont);

        Review review = reviewsList.get(position);
        tvReviewText.setText(review.getReviewText());
        tvAuthor.setText(review.getAuthor());

        return row;
    }
}
