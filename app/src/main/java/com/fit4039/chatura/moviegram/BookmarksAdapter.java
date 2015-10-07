package com.fit4039.chatura.moviegram;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.fit4039.chatura.moviegram.model.Bookmark;
import com.fit4039.chatura.moviegram.model.Review;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;

/**
 * Created by hp on 6/5/2015.
 */

//adapter for bookmarks page listview
public class BookmarksAdapter extends ArrayAdapter<Bookmark> {

    Context context;
    ArrayList <Bookmark> bookmarks;

    ImageView ivPoster;
    TextView tvTitle;
    TextView tvGenre;

    public BookmarksAdapter(Context context, ArrayList<Bookmark> bookmarks) {
        super(context, R.layout.item_bookmark, bookmarks);
        this.context = context;
        this.bookmarks = bookmarks;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View row = inflater.inflate(R.layout.item_bookmark, parent, false);

        ivPoster = (ImageView) row.findViewById(R.id.ivBMPoster);
        tvTitle = (TextView) row.findViewById(R.id.tvBMTitle);
        tvGenre = (TextView) row.findViewById(R.id.tvBMGenre);

        Bookmark bookmark = bookmarks.get(position);
        tvTitle.setText(bookmark.getMovieTitle());
        tvGenre.setText(bookmark.getGenre());
        String s = bookmark.getPosterFileName();
        loadImageFromStorage(bookmark.getPosterFileName(), ivPoster);

        return row;
    }

    // images for bookmarks are stored in local storage when the bookmarks are created.
    // this method retrieves it
    private void loadImageFromStorage(String path, ImageView img)
    {

        try {
            File f=new File(path);
            Bitmap b = BitmapFactory.decodeStream(new FileInputStream(f));
            img.setImageBitmap(b);
        }
        catch (FileNotFoundException e)
        {
            e.printStackTrace();
        }

    }
}
