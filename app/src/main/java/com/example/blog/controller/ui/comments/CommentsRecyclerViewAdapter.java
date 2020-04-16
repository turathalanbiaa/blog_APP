package com.example.blog.controller.ui.comments;


import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.res.ResourcesCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.blog.R;
import com.example.blog.controller.tools.TimeAgo;
import com.example.blog.model.Comments;

import java.util.ArrayList;
import java.util.List;

public class CommentsRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>  {


    private List<Comments> commentsList;
    private LayoutInflater mInflater;


    private static final int ITEM = 0;
    private static final int LOADING = 1;
    private Context context;


    private boolean isLoadingAdded = false;
    private ClickListenerInterface mClickListener,mNameClickListener;

    public CommentsRecyclerViewAdapter(Context context) {
        this.context = context;
        commentsList = new ArrayList<>();
    }

    public List<Comments> getComments() {
        return commentsList;
    }

    public void setComments(List<Comments> comments) {
        this.commentsList = comments;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder viewHolder = null;
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());

        switch (viewType) {
            case ITEM:
                viewHolder = getViewHolder(parent, inflater);
                break;
            case LOADING:
                View v2 = inflater.inflate(R.layout.item_loading, parent, false);
                viewHolder = new LoadingVH(v2);
                break;
        }
        return viewHolder;
    }

    @NonNull
    private RecyclerView.ViewHolder getViewHolder(ViewGroup parent, LayoutInflater inflater) {
        RecyclerView.ViewHolder viewHolder;
        View v1 = inflater.inflate(R.layout.comment_single_item, parent, false);
        viewHolder = new PostVH(v1);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

       Comments comments = commentsList.get(position);

        switch (getItemViewType(position)) {
            case ITEM:
                PostVH postVH = (PostVH) holder;

                postVH.comment.setText(comments.getContent());

                TimeAgo timeago=new TimeAgo();
                String fTime="";
                fTime= timeago.covertTimeToText(comments.getCreated_at());
                postVH.time.setText(fTime);

                //get name from user_id
                postVH.name.setText(comments.getUsername());


                break;
            case LOADING:
//                Do nothing
                break;
        }

    }



    @Override
    public int getItemCount() {
        return commentsList == null ? 0 : commentsList.size();
    }

    @Override
    public int getItemViewType(int position) {
        return (position == commentsList.size() - 1 && isLoadingAdded) ? LOADING : ITEM;
    }

    /*
   Helpers
   _________________________________________________________________________________________________
    */

    public void add(Comments mc) {
        commentsList.add(mc);
        notifyItemInserted(commentsList.size() - 1);
    }

    public void addAll(List<Comments> mcList) {
        for (Comments mc : mcList) {
            add(mc);
        }
    }

    public void remove(Comments c) {
        int position = commentsList.indexOf(c);
        if (position > -1) {
            commentsList.remove(position);
            notifyItemRemoved(position);
        }
    }

    public void clear() {
        isLoadingAdded = false;
        while (getItemCount() > 0) {
            remove(getItem(0));
        }
    }

    public boolean isEmpty() {
        return getItemCount() == 0;
    }


    public void addLoadingFooter() {
        isLoadingAdded = true;
        add(new Comments());
    }

    public void removeLoadingFooter() {
        isLoadingAdded = false;

        if(commentsList.size()!=0) {
            int position = commentsList.size() - 1;
            Comments item = getItem(position);

            if (item != null) {
                commentsList.remove(position);
                notifyItemRemoved(position);
            }
        }
    }

    public Comments getItem(int position) {
        return commentsList.get(position);
    }


   /*
   View Holders
   _________________________________________________________________________________________________
    */

    /**
     * Main list's content ViewHolder
     */
    protected class PostVH extends RecyclerView.ViewHolder {
        TextView comment,userId,postId,name,time;


        public PostVH(View itemView) {
            super(itemView);
            comment = itemView.findViewById(R.id.comment);
            name=itemView.findViewById(R.id.commenterName);
            time= itemView.findViewById(R.id.commentPostTime);

            userId=itemView.findViewById(R.id.userId);
            postId=itemView.findViewById(R.id.postId);

            itemView.setOnClickListener(clickListener);
            name.setOnClickListener(nameClickListener);

            //change font
            SharedPreferences settingsPrefs = context.getSharedPreferences("settings", Activity.MODE_PRIVATE);
            Typeface typeface = ResourcesCompat.getFont(context, R.font.myfont);
            if(settingsPrefs.getInt("font",1)==3)
                typeface = ResourcesCompat.getFont(context, R.font.myfont3);
            else if (settingsPrefs.getInt("font",1)==2)
                typeface = ResourcesCompat.getFont(context, R.font.myfont2);

            comment.setTypeface(typeface);

            //change size
            comment.setTextSize(settingsPrefs.getFloat("size",16));
            comment.invalidate();

        }
        View.OnClickListener clickListener = new View.OnClickListener() {
            @Override
            public void onClick(android.view.View view) {
                if (mClickListener != null)mClickListener.onItemClick(view, getAdapterPosition());

            }
        };

        View.OnClickListener nameClickListener = new View.OnClickListener() {
            @Override
            public void onClick(android.view.View view) {
                if (mNameClickListener != null)mNameClickListener.onNameClick(view, getAdapterPosition());

            }
        };

    }


    protected class LoadingVH extends RecyclerView.ViewHolder {

        public LoadingVH(View itemView) {
            super(itemView);
        }
    }


    public interface ClickListenerInterface {
        void onItemClick(View view, int position);
        void onNameClick(View view, int position);
    }

    void setClickListener(ClickListenerInterface itemClickListener) {
        this.mClickListener = itemClickListener;
    }


    void setNameClickListener(ClickListenerInterface nameClickListener) {
        this.mNameClickListener = nameClickListener;
    }


}