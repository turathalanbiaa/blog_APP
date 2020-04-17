package com.example.blog.controller.ui.bookmarks;


import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.core.content.res.ResourcesCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.blog.R;
import com.example.blog.model.Categories;
import com.example.blog.model.Posts;

import java.util.List;

public class BookmarksRecyclerViewAdapter extends RecyclerView.Adapter<BookmarksRecyclerViewAdapter.ViewHolder> {


    private List<Posts> postsList;
    private LayoutInflater mInflater;

    private ItemClickListener mClickListener;
    Context context;

    // data is passed into the constructor
//    MyRecyclerViewAdapter(Context context, List<String> data) {
//
//        this.mInflater = LayoutInflater.from(context);
//        this.mData = data;
//    }
    BookmarksRecyclerViewAdapter(Context context, List<Posts> itemData) {

        this.mInflater = LayoutInflater.from(context);
        this.postsList = itemData;
        this.context=context;
    }

    // inflates the row layout from xml when needed
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.bookmark_post_single_item, parent, false);

        return new ViewHolder(view);
    }

    // binds the data to the TextView in each row

//    @Override
//    public void onBindViewHolder(ViewHolder holder, int position) {
//        String id = mData.get(position);
//
//        holder.itemId.setText(id);
//    }
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        Posts posts= postsList.get(position);
       // holder.itemId.setText(item.getId());
        holder.postTitle.setText(posts.getTitle());
        holder.postDetails.setText(posts.getContent());
        holder.username.setText(posts.getUsername());



    }

    // total number of rows
    @Override
    public int getItemCount() {
        return postsList.size();
    }


    // stores and recycles views as they are scrolled off screen
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView postTitle,postDetails,seeMore,username;

        ImageButton bookmark;

        ViewHolder(View itemView) {
            super(itemView);

            postTitle = itemView.findViewById(R.id.postTitle);
            postDetails = itemView.findViewById(R.id.postDetails);
            seeMore = itemView.findViewById(R.id.seeMore);
            username = itemView.findViewById(R.id.ph_name);

            bookmark=itemView.findViewById(R.id.bookmark);

            Drawable drawable = context.getResources().getDrawable(R.drawable.ic_bookmark_black_24dp);

            bookmark.setBackground(drawable);
            bookmark.setOnClickListener(this);

            seeMore.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (postDetails.getMaxLines() == 3) {
                        postDetails.setMaxLines(1000);
                        seeMore.setVisibility(View.GONE);

                    } else {
                        postDetails.setMaxLines(3);
                        seeMore.setVisibility(View.VISIBLE);

                    }
                }
            });

            //change font
            SharedPreferences settingsPrefs = context.getSharedPreferences("settings", Activity.MODE_PRIVATE);
            Typeface typeface = ResourcesCompat.getFont(context, R.font.myfont);
            if(settingsPrefs.getInt("font",1)==3)
                typeface = ResourcesCompat.getFont(context, R.font.myfont3);
            else if (settingsPrefs.getInt("font",1)==2)
                typeface = ResourcesCompat.getFont(context, R.font.myfont2);

            postTitle.setTypeface(typeface);
            postDetails.setTypeface(typeface);

            //change size
            postDetails.setTextSize(settingsPrefs.getFloat("size",16));
            postTitle.setTextSize(settingsPrefs.getFloat("size",16)+6);
            postDetails.invalidate();
            postTitle.invalidate();
        }


        @Override
        public void onClick(View view) {
            if (mClickListener != null) mClickListener.onItemClick(view, getAdapterPosition());
        }
    }

    // convenience method for getting data at click position
//    String getItem(int id) {
//        return mData.get(id);
//    }

    // allows clicks events to be caught
    void setClickListener(ItemClickListener itemClickListener) {
        this.mClickListener = itemClickListener;
    }

    // parent activity will implement this method to respond to click events
    public interface ItemClickListener {
        void onItemClick(View view, int position);
    }
    public void remove(Posts p) {
        int position = postsList.indexOf(p);
        if (position > -1) {
            postsList.remove(position);
            notifyItemRemoved(position);
        }
    }

}