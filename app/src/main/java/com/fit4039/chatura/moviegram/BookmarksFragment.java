package com.fit4039.chatura.moviegram;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.fit4039.chatura.moviegram.model.Bookmark;
import com.fit4039.chatura.moviegram.model.Util;

import java.util.ArrayList;

import io.realm.Realm;
import io.realm.RealmQuery;
import io.realm.RealmResults;

/**
 * Created by hp on 6/5/2015.
 */

// handles bookmarks of the app
public class BookmarksFragment extends Fragment {
    ListView lvBookmarks;
    View view;
    ArrayList <Bookmark> bookmarks;
    BookmarksAdapter bookmarksAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_bookmarks, container, false);

        lvBookmarks = (ListView) view.findViewById(R.id.lvBookmarks);
        // retrieving stored bookmarks from database
        Realm realm = Realm.getInstance(getActivity());
        RealmQuery<Bookmark> query = realm.where(Bookmark.class);
        RealmResults<Bookmark> results = query.findAll();

        // adding bookmarks to an arraylist since direct conversion from RealmResults to ArrayList is not available
        bookmarks = new ArrayList<Bookmark>();
        for(Bookmark bookmark : results){
            bookmarks.add(bookmark);
        }

        bookmarksAdapter = new BookmarksAdapter(getActivity(), bookmarks);
        lvBookmarks.setAdapter(bookmarksAdapter);

        lvBookmarks.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if(Util.isOnline(getActivity())){// checks for internet availability
                    // loading the selected bookmark (movie)
                    int movieID = bookmarks.get(position).getMovieID();
                    Intent intent = new Intent(getActivity(), MovieActivity.class);
                    intent.putExtra("MOVIE_ID", movieID);
                    getActivity().startActivity(intent);
                }else{
                    Util.showNoConnectionError(getActivity());
                }

            }
        });

        return  view;
    }
}
