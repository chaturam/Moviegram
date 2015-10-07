package com.fit4039.chatura.moviegram;

import android.content.Context;
import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.fit4039.chatura.moviegram.model.Abouts;
import com.fit4039.chatura.moviegram.model.Bookmark;

import java.util.ArrayList;

/**
 * Created by hp on 6/8/2015.
 */

//adapter for abouts page listview
public class AboutsAdapter extends ArrayAdapter<Abouts> {

    Context context;
    ArrayList <Abouts> abouts;

    TextView tvName;
    TextView tvLink;
    TextView tvDescription;

    public AboutsAdapter(Context context, ArrayList<Abouts> abouts) {
        super(context, R.layout.item_about, abouts);
        this.context = context;
        this.abouts = abouts;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View row = inflater.inflate(R.layout.item_about, parent, false);

        tvName = (TextView) row.findViewById(R.id.tvAboutName);
        tvLink = (TextView) row.findViewById(R.id.tvAboutLink);
        tvDescription = (TextView) row.findViewById(R.id.tvAboutDesc);

        //getting currnt about object
        Abouts about = abouts.get(position);
        tvName.setText(about.getName());
        tvLink.setText(about.getLink());
        tvDescription.setText(about.getDescription());

        // adding underline to link textview to look it like a hyper link
        tvLink.setPaintFlags(tvLink.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);

        return row;
    }
}
