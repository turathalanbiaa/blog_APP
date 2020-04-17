package com.example.blog.controller.ui.bookmarks;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.blog.R;
import com.example.blog.controller.tools.MyDBHandler;
import com.example.blog.model.Posts;

import java.util.ArrayList;

public class BookmarksFragment extends Fragment implements BookmarksRecyclerViewAdapter.ItemClickListener {


    RelativeLayout catRelativeLayout;
    ArrayList<Posts> postsList=new ArrayList<>();
    BookmarksRecyclerViewAdapter adapter;
    String TAG="bookmarksFragment";
    MyDBHandler dbHandler;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_bookmarks, container,false);

        dbHandler = new MyDBHandler(getActivity(), null, null, 1);


        int c=(int)dbHandler.getProfilesCount();

        Log.d(TAG, "onCreateView: "+c);
        postsList.addAll(dbHandler.loadHandler());

        RecyclerView recyclerView = view.findViewById(R.id.posts_recycler_view);
        catRelativeLayout = view.findViewById(R.id.layout);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        adapter= new BookmarksRecyclerViewAdapter( getContext(),postsList);
        adapter.setClickListener(this);
        recyclerView.setAdapter(adapter);



        return view;
    }

    @Override
    public void onItemClick(View view, int position) {
        ImageButton bookmark=view.findViewById(R.id.bookmark);
        Drawable drawable = getResources().getDrawable(R.drawable.ic_bookmark_border_black_24dp);
        bookmark.setBackground(drawable);

        //remove
        Log.d(TAG, "onItemClick: "+postsList.get(position).getId());
        dbHandler.deleteHandler(postsList.get(position).getId());
        adapter.remove(postsList.get(position));
    }
}