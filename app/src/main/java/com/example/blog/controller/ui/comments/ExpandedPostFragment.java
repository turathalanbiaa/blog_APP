package com.example.blog.controller.ui.comments;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ScrollView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.example.blog.MainActivity;
import com.example.blog.R;
import com.example.blog.controller.ui.home.FullPostFragment;
import com.example.blog.model.Posts;
import com.example.blog.controller.ui.comments.CommentBarFragment;
import com.example.blog.controller.ui.comments.CommentsFragment;
import com.facebook.AccessToken;
import com.facebook.Profile;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class ExpandedPostFragment extends Fragment {

    String id;
    boolean notification=false;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
         View root = inflater.inflate(R.layout.expanded_post, container, false);
//
        SharedPreferences prefs = getActivity().getSharedPreferences("profile", Activity.MODE_PRIVATE);
        final boolean loggedOut = AccessToken.getCurrentAccessToken() == null;
//

        FloatingActionButton fab = ((MainActivity) getActivity()).getFloatingActionButton();
        fab.hide();

        Posts post=new Posts();

        if(getArguments().getInt("notify")==1 ){
            id=getArguments().getString("postId");
            notification=true;

        }
        if(getArguments()!=null)
            post=getArguments().getParcelable("post");

        FullPostFragment postFragment=new FullPostFragment();
        CommentsFragment commentsFragment = new CommentsFragment();
        CommentBarFragment commentBar=new CommentBarFragment();

        //

        Bundle bundle=new Bundle();
        if(notification){
            bundle.putInt("notify",1);
            bundle.putString("postId",id);

        }
        else
            bundle.putParcelable("post",post);

        postFragment.setArguments(bundle);
//        commentsFragment.setArguments(bundle);

        Bundle bundle2=new Bundle();
        if(notification)
            bundle2.putInt("postId",Integer.parseInt(id));
        else
            bundle2.putInt("postId",post.getId());
        commentBar.setArguments(bundle2);
        commentsFragment.setArguments(bundle2);


        FragmentManager fm = getFragmentManager();
        fm.beginTransaction().replace(R.id.fragment1, postFragment, "fragmentone").commit();
        fm.beginTransaction().replace(R.id.fragment2, commentsFragment, "fragmenttwo").commit();
        if(prefs.getString("user_id",null)!=null || !loggedOut)
        getChildFragmentManager().beginTransaction().replace(R.id.commentBarFrame, commentBar, "commentBarFrame").commit();



        final ScrollView scrollView=root.findViewById(R.id.scrollView_post);
//        scrollView.fullScroll(ScrollView.FOCUS_UP);
        Toolbar toolbar=getActivity().findViewById(R.id.toolbar);
        toolbar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               scrollView.fullScroll(ScrollView.FOCUS_UP);

            }
        });

        return root;
    }
}