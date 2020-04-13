package com.example.blog.controller.ui.bloggers;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.blog.R;
import com.example.blog.model.Users;
import com.example.blog.controller.tools.ClickListenerInterface;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class BloggersRecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>  {


    private List<Users> usersList;
    private LayoutInflater mInflater;

    private ClickListenerInterface mClickListener;




    private static final int ITEM = 0;
    private static final int LOADING = 1;
    private Context context;


    private boolean isLoadingAdded = false;

    public BloggersRecyclerAdapter(Context context) {
        this.context = context;
        usersList = new ArrayList<>();
    }

    public List<Users> getPosts() {
        return usersList;
    }

    public void setPosts(List<Users> users) {
        this.usersList = users;
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
        View v1 = inflater.inflate(R.layout.profile_card, parent, false);
        viewHolder = new PostVH(v1);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

         Users users = usersList.get(position);

        switch (getItemViewType(position)) {
            case ITEM:
                PostVH postVH = (PostVH) holder;

                //profile pic, get from user
                if(users.getPicture().equals("default")){
                    Picasso.with(postVH.profilePic.getContext()).
                            load(R.drawable.default_profile_pic).into(postVH.profilePic);
                }
                else {
                    Picasso.with(postVH.profilePic.getContext()).
                            load(users.getPicture()).into(postVH.profilePic);

                }
                //name
                postVH.userName.setText(users.getName());


                postVH.points.setText("النقاط: "+users.getPoints());

//                //background pic
//                Picasso.with( postVH.backgroundPic.getContext()).
//                        load(R.drawable.aqlamdefault).fit().centerCrop().into( postVH.backgroundPic);





                break;
            case LOADING:
//                Do nothing
                break;
        }

    }



    @Override
    public int getItemCount() {
        return usersList == null ? 0 : usersList.size();
    }

    @Override
    public int getItemViewType(int position) {
        return (position == usersList.size() - 1 && isLoadingAdded) ? LOADING : ITEM;
    }

    /*
   Helpers
   _________________________________________________________________________________________________
    */

    public void add(Users mc) {
        usersList.add(mc);
        notifyItemInserted(usersList.size() - 1);
    }

    public void addAll(List<Users> mcList) {
        for (Users mc : mcList) {
            add(mc);
        }
    }

    public void remove(Users u) {
        int position = usersList.indexOf(u);
        if (position > -1) {
            usersList.remove(position);
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
        add(new Users());
    }

    public void removeLoadingFooter() {
        isLoadingAdded = false;

        if(usersList.size()!=0) {
            int position = usersList.size() - 1;
            Users item = getItem(position);

            if (item != null) {
                usersList.remove(position);
                notifyItemRemoved(position);
            }
        }
    }

    public Users getItem(int position) {
        return usersList.get(position);
    }


   /*
   View Holders
   _________________________________________________________________________________________________
    */

    /**
     * Main list's content ViewHolder
     */
    protected class PostVH extends RecyclerView.ViewHolder {
        ImageView profilePic,backgroundPic;
        TextView userName,userId,points;

        public PostVH(View itemView) {
            super(itemView);

//
            points=itemView.findViewById(R.id.points);
            profilePic=itemView.findViewById(R.id.ph_profilePic);
            userName=itemView.findViewById(R.id.ph_name);
//            backgroundPic=itemView.findViewById(R.id.profile_background);



            itemView.setOnClickListener(postClickListener);


        }
        View.OnClickListener postClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mClickListener != null)mClickListener.onItemClick(view, getAdapterPosition());

            }
        };


    }


    protected class LoadingVH extends RecyclerView.ViewHolder {

        public LoadingVH(View itemView) {
            super(itemView);
        }
    }


    void setClickListener(ClickListenerInterface itemClickListener) {
        this.mClickListener = itemClickListener;
    }



}