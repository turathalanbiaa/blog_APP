package com.example.blog.controller.ui.home;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.example.blog.controller.ui.profile.ProfileActivity;
import com.example.blog.R;
import com.example.blog.controller.tools.PopUpClass;
import com.example.blog.controller.tools.TimeAgo;
import com.example.blog.model.Posts;
import com.squareup.picasso.Picasso;

public class FullPostFragment extends Fragment {

    TextView postTitle,postDetails,postId,viewCount,comments,userName,created_at;
    ImageView postPic,profilePic,profileBackground;
    Button catBtn;
    Posts post;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
          View root = inflater.inflate(R.layout.full_post, container, false);
//

            postPic=root.findViewById(R.id.post_pic);
            postTitle=root.findViewById(R.id.postTitle);
            postDetails=root.findViewById(R.id.postDetails);
            postId=root.findViewById(R.id.psi_postId);
            viewCount=root.findViewById(R.id.viewsCount);
            comments=root.findViewById(R.id.commentsCount);
            created_at=root.findViewById(R.id.time);
            catBtn=root.findViewById(R.id.catBtn);

            profilePic=root.findViewById(R.id.ph_profilePic);
            userName=root.findViewById(R.id.ph_name);
            profileBackground=root.findViewById(R.id.profile_background);



        post=getArguments().getParcelable("post");


//        postId.setText(post_id);

        postTitle.setText(post.getTitle());
        postDetails.setText(post.getContent());
        postId.setText(""+post.getId());
        viewCount.setText(""+post.getViews());
        comments.setText(post.getCommentsCount());
        TimeAgo timeago=new TimeAgo();

        String time= timeago.covertTimeToText(post.getCreated_at());
        created_at.setText(time);
        Picasso.with(getContext()).
                load(post.getProfilePic()).into(profilePic);

        catBtn.setText(post.getCategory_name());

        //name
        userName.setText(post.getUsername());

        //background pic
//        Picasso.with(getContext()).
//                load(R.drawable.aqlamdefault).fit().centerCrop().into( profileBackground);




        final String img=post.getImage();
        if(img !=null && !img.equals("")) {
            postPic.setVisibility(View.VISIBLE);
            postPic.getLayoutParams().height=750;
            Picasso.with(getContext()).load(img).fit().centerCrop().into(postPic);
        }

        postPic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PopUpClass popUpClass = new PopUpClass();
                popUpClass.showPopupWindow(view,img);

            }
        });


        userName.setOnClickListener(profileListener);
        profilePic.setOnClickListener(profileListener);

        catBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Bundle bundle = new Bundle();
                bundle.putInt("catId",post.getCategory_id());
                Navigation.findNavController(view).navigate(R.id.action_nav_post_to_nav_cat_posts,bundle);
//
            }
        });


        return root;
    }

    View.OnClickListener profileListener = new View.OnClickListener() {
        @Override
        public void onClick(android.view.View view) {

            Intent intent =new Intent(getContext(), ProfileActivity.class);
            intent.putExtra("user_id",post.getUser_id());
            Log.d("tag", "onClick: "+post.getUser_id());
            startActivity(intent);

        }
    };
}