package com.fit4039.chatura.moviegram;

import android.content.Context;
import android.graphics.Typeface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.fit4039.chatura.moviegram.model.Review;
import com.fit4039.chatura.moviegram.model.Role;

import java.util.ArrayList;

/**
 * Created by hp on 6/5/2015.
 */

//adapter for cast page listview
public class CastAdapter extends ArrayAdapter<Role> {

    Context context;
    ArrayList<Role> cast;

    TextView tvRealName;
    TextView tvRole;

    public CastAdapter(Context context, ArrayList<Role> cast) {
        super(context, R.layout.item_cast, cast);
        this.context = context;
        this.cast = cast;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View row = inflater.inflate(R.layout.item_cast, parent, false);

        tvRealName = (TextView) row.findViewById(R.id.tvItemCastName);
        tvRole = (TextView) row.findViewById(R.id.tvItemCastRole);

        Role role = cast.get(position);
        tvRealName.setText(role.getRealName());
        tvRole.setText(role.getCharacterName());

        //setting a typeface (font)
        Typeface customFont = Typeface.createFromAsset(context.getAssets(), "fonts/OpenSans-Regular.ttf");
        tvRealName.setTypeface(customFont);
        tvRole.setTypeface(customFont);

        return row;
    }
}
