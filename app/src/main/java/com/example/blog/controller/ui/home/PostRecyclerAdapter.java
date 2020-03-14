package com.example.blog.controller.ui.home;


import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.blog.controller.tools.ClickListenerInterface;
import com.example.blog.R;
import com.example.blog.controller.tools.TimeAgo;
import com.example.blog.model.Posts;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class PostRecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>  {


    private List<Posts> postsList;
    private LayoutInflater mInflater;

    private ClickListenerInterface mClickListener,mCommentClickListener,
            mPicClickListener,mPostExpandClickListener,mProfileClickListener,mCatClickListener;




    private static final int ITEM = 0;
    private static final int LOADING = 1;
    private Context context;


    private boolean isLoadingAdded = false;

    public PostRecyclerAdapter(Context context) {
        this.context = context;
        postsList = new ArrayList<>();
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
        View v1 = inflater.inflate(R.layout.post_single_item, parent, false);
        viewHolder = new PostVH(v1);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

         Posts posts =postsList.get(position);

        switch (getItemViewType(position)) {
            case ITEM:
                PostVH postVH = (PostVH) holder;

                //profile pic, get from user
                String profilePic=posts.getProfilePic();
                if (profilePic != null && !profilePic.equals("")) {
                Picasso.with( postVH.profilePic.getContext()).
                        load(profilePic).into( postVH.profilePic);}

                //name
                postVH.userName.setText(posts.getUsername());

                //background pic
//                Picasso.with( postVH.backgroundPic.getContext()).
//                        load(R.drawable.aqlamdefault).fit().centerCrop().into( postVH.backgroundPic);





                postVH.postTitle.setText(posts.getTitle());
                postVH.postDetails.setText(posts.getContent());


                TimeAgo timeago=new TimeAgo();

                String time= timeago.covertTimeToText(posts.getCreated_at());
                postVH.timeStamp.setText(time);

                postVH.catId.setText(""+posts.getCategory_id());
                postVH.viewCount.setText(""+posts.getViews());
                postVH.comments.setText(posts.getCommentsCount());

                postVH.catBtn.setText(posts.getCategory_name());


                String img = posts.getImage();


                if (img != null && !img.equals("")) {

                    postVH.postPic.setVisibility(View.VISIBLE);
                    Picasso.with( postVH.postPic.getContext()).load(img).fit().centerCrop().into( postVH.postPic);

                }
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
        TextView postTitle,postDetails,seeMore,postId,viewCount,comments,timeStamp,catId;
        ImageView postPic,profilePic,backgroundPic;
        LinearLayout contentll,catLL;
        Button catBtn;
        TextView userName;

        public PostVH(View itemView) {
            super(itemView);
            postPic = itemView.findViewById(R.id.post_pic);
            postTitle = itemView.findViewById(R.id.postTitle);
            postDetails = itemView.findViewById(R.id.postDetails);
//            postId = itemView.findViewById(R.id.psi_postId);
            seeMore = itemView.findViewById(R.id.seeMore);
            contentll = itemView.findViewById(R.id.contentLL);
            viewCount = itemView.findViewById(R.id.viewsCount);
            comments = itemView.findViewById(R.id.commentsCount);
            timeStamp=itemView.findViewById(R.id.time);

//            catLL=itemView.findViewById(R.id.post_catLL);

            catBtn=itemView.findViewById(R.id.catBtn);
            catId=itemView.findViewById(R.id.cat_Id);
//
            profilePic=itemView.findViewById(R.id.ph_profilePic);
            userName=itemView.findViewById(R.id.ph_name);
            backgroundPic=itemView.findViewById(R.id.profile_background);


            itemView.setOnClickListener(postClickListener);
            comments.setOnClickListener(commnetListener);
            postPic.setOnClickListener(picListener);

            profilePic.setOnClickListener(profileListener);
            userName.setOnClickListener(profileListener);

            contentll.setOnClickListener(postListener);

            catBtn.setOnClickListener(catListener);

        }
        View.OnClickListener postClickListener = new View.OnClickListener() {
            @Override
            public void onClick(android.view.View view) {
                if (mClickListener != null)mClickListener.onItemClick(view, getAdapterPosition());

            }
        };

        View.OnClickListener commnetListener = new View.OnClickListener() {
            @Override
            public void onClick(android.view.View view) {
                if (mCommentClickListener != null)mCommentClickListener.onCommentClick(view, getAdapterPosition());

            }
        };

        View.OnClickListener picListener = new View.OnClickListener() {
            @Override
            public void onClick(android.view.View view) {
                if (mPicClickListener != null)mPicClickListener.onPicClick(view, getAdapterPosition());

            }
        };
        View.OnClickListener postListener = new View.OnClickListener() {
            @Override
            public void onClick(android.view.View view) {
                if (mPostExpandClickListener != null)mPostExpandClickListener.onPostExpandClick(view, getAdapterPosition());

            }
        };
        View.OnClickListener profileListener = new View.OnClickListener() {
            @Override
            public void onClick(android.view.View view) {
                if (mProfileClickListener!= null)mProfileClickListener.onProfileClick(view, getAdapterPosition());

            }
        };
        View.OnClickListener catListener = new View.OnClickListener() {
            @Override
            public void onClick(android.view.View view) {
                if (mCatClickListener!= null)mCatClickListener.onCatClick(view, getAdapterPosition());

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
    //on comment click
    void setCommentClickListener(ClickListenerInterface commentClickListener) {
        this.mCommentClickListener = commentClickListener;
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
    void setProfileClickListener(ClickListenerInterface profileClickListener) {
        this.mProfileClickListener = profileClickListener;
    }

    //on category click
    void setCatClickListener(ClickListenerInterface catClickListener) {
        this.mCatClickListener = catClickListener;
    }


}