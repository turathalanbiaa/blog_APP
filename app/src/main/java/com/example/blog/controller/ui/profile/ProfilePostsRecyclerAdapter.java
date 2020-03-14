package com.example.blog.controller.ui.profile;


import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.blog.R;
import com.example.blog.controller.tools.ClickListenerInterface;
import com.example.blog.controller.tools.TimeAgo;
import com.example.blog.model.Posts;
import com.facebook.AccessToken;
import com.facebook.Profile;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class ProfilePostsRecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>  {


    private boolean myProfile=false;
    private List<Posts> postsList;
    private LayoutInflater mInflater;

    private ClickListenerInterface mClickListener,meditClickListener,
            mPicClickListener,mPostExpandClickListener, mDeleteClickListener;




    private static final int ITEM = 0;
    private static final int LOADING = 1;
    private Context context;


    private boolean isLoadingAdded = false;

    public ProfilePostsRecyclerAdapter(Context context, boolean myProfile) {
        this.context = context;
        postsList = new ArrayList<>();
        this.myProfile=myProfile;
    }

    public List<Posts> getPosts() {
        return postsList;
    }

    public void setPosts(List<Posts> posts) {
        this.postsList = posts;
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
        View v1 = inflater.inflate(R.layout.profile_post_single_item, parent, false);
        viewHolder = new PostVH(v1);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

         Posts posts =postsList.get(position);

        switch (getItemViewType(position)) {
            case ITEM:
                PostVH postVH = (PostVH) holder;




                postVH.postTitle.setText(posts.getTitle());
                postVH.postDetails.setText(posts.getContent());
                TimeAgo timeago=new TimeAgo();

                String time= timeago.covertTimeToText(posts.getCreated_at());
                postVH.timeStamp.setText(time);

//                postVH.catId.setText(""+posts.getCategory_id());
                postVH.viewCount.setText(""+posts.getViews());
                postVH.commentsCount.setText(posts.getCommentsCount());

                postVH.catBtn.setText(posts.getCategory_name());


                String img = posts.getImage();


                if (img != null && !img.equals("")) {

                    postVH.postPic.setVisibility(View.VISIBLE);
                    Picasso.with( postVH.postPic.getContext()).load(img).fit().centerCrop().into( postVH.postPic);

                }

                if(myProfile)
                    if (posts.getStatus() ==0)
                        postVH.edit.setVisibility(View.VISIBLE);

                break;
            case LOADING:
//                Do nothing
                break;
        }

    }



    @Override
    public int getItemCount() {
        return postsList == null ? 0 : postsList.size();
    }

    @Override
    public int getItemViewType(int position) {
        return (position == postsList.size() - 1 && isLoadingAdded) ? LOADING : ITEM;
    }

    /*
   Helpers
   _________________________________________________________________________________________________
    */

    public void add(Posts mc) {
        postsList.add(mc);
        notifyItemInserted(postsList.size() - 1);
    }

    public void addAll(List<Posts> mcList) {
        for (Posts mc : mcList) {
            add(mc);
        }
    }

    public void remove(Posts p) {
        int position = postsList.indexOf(p);
        if (position > -1) {
            postsList.remove(position);
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
        add(new Posts());
    }

    public void removeLoadingFooter() {
        isLoadingAdded = false;

        if(postsList.size()!=0) {
            int position = postsList.size() - 1;
            Posts item = getItem(position);

            if (item != null) {
                postsList.remove(position);
                notifyItemRemoved(position);
            }
        }
    }

    public Posts getItem(int position) {
        return postsList.get(position);
    }


   /*
   View Holders
   _________________________________________________________________________________________________
    */

    /**
     * Main list's content ViewHolder
     */
    protected class PostVH extends RecyclerView.ViewHolder {
        TextView postTitle,postDetails,seeMore,postId,viewCount,timeStamp,catId,commentsCount;
        ImageView postPic;
        LinearLayout contentll,catLL;
        Button catBtn;
        TextView delete,edit;

        public PostVH(View itemView) {
            super(itemView);
            postPic = itemView.findViewById(R.id.post_pic);
            postTitle = itemView.findViewById(R.id.postTitle);
            postDetails = itemView.findViewById(R.id.postDetails);
//            postId = itemView.findViewById(R.id.psi_postId);
            seeMore = itemView.findViewById(R.id.seeMore);
            contentll = itemView.findViewById(R.id.contentLL);
            viewCount = itemView.findViewById(R.id.viewsCount);
            delete = itemView.findViewById(R.id.deletePost);
            edit=itemView.findViewById(R.id.editPost);
            timeStamp=itemView.findViewById(R.id.time);
            commentsCount = itemView.findViewById(R.id.commentsCount);

//            catLL=itemView.findViewById(R.id.post_catLL);

            catBtn=itemView.findViewById(R.id.catBtn);
            catId=itemView.findViewById(R.id.cat_Id);
//


            itemView.setOnClickListener(postClickListener);
            postPic.setOnClickListener(picListener);
            edit.setOnClickListener(editClickListener);
            delete.setOnClickListener(deleteClickListener);
            contentll.setOnClickListener(postListener);
//


            if(myProfile) {
                delete.setVisibility(View.VISIBLE);

            }

//


        }
        View.OnClickListener postClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mClickListener != null)mClickListener.onItemClick(view, getAdapterPosition());

            }
        };

        View.OnClickListener deleteClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mDeleteClickListener != null) mDeleteClickListener.onProfileClick(view, getAdapterPosition());

            }
        };

        View.OnClickListener editClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (meditClickListener != null) meditClickListener.onCommentClick(view, getAdapterPosition());

            }
        };


        View.OnClickListener picListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mPicClickListener != null)mPicClickListener.onPicClick(view, getAdapterPosition());

            }
        };
        View.OnClickListener postListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mPostExpandClickListener != null)mPostExpandClickListener.onPostExpandClick(view, getAdapterPosition());

            }
        };

    }


    protected class LoadingVH extends RecyclerView.ViewHolder {

        public LoadingVH(View itemView) {
            super(itemView);
        }
    }

    //post click
    void setClickListener(ClickListenerInterface itemClickListener) {
        this.mClickListener = itemClickListener;
    }
    //edit
    void setCommentClickListener(ClickListenerInterface editClickListener) {
        this.meditClickListener = editClickListener;
    }

    //on pic click
    void setPicClickListener(ClickListenerInterface picClickListener) {
        this.mPicClickListener = picClickListener;
    }

    //on text expand
    void setPostExpandClickListener(ClickListenerInterface postExpandClickListener) {
        this.mPostExpandClickListener = postExpandClickListener;
    }

    //on profile click
    //delete
    void setProfileClickListener(ClickListenerInterface profileClickListener) {
        this.mDeleteClickListener = profileClickListener;
    }


}