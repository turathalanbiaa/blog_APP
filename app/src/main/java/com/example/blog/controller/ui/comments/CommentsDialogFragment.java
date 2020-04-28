package com.example.blog.controller.ui.comments;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.blog.R;
import com.example.blog.controller.tools.SwipeDismissTouchListener;
import com.example.blog.model.Comments;
import com.facebook.AccessToken;

import java.util.ArrayList;


public class CommentsDialogFragment extends DialogFragment  {



    public CommentsDialogFragment(){}

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

       View view = inflater.inflate(R.layout.dialog_comments_fragment, container,false);
        SharedPreferences prefs = getActivity().getSharedPreferences("profile", Activity.MODE_PRIVATE);
        final boolean loggedOut = AccessToken.getCurrentAccessToken() == null;
//

        CommentsFragment commentsFragment=new CommentsFragment();
        CommentBarFragment commentBar=new CommentBarFragment();
        int postId= getArguments().getInt("postId");

        Bundle bundle=new Bundle();
        bundle.putInt("postId",postId);
        commentBar.setArguments(bundle);
        commentsFragment.setArguments(bundle);

       getChildFragmentManager().beginTransaction().replace(R.id.dialogFrame,commentsFragment,"dialogFrame").commit();
        if(prefs.getString("user_id",null)!=null || !loggedOut)
            getChildFragmentManager().beginTransaction().replace(R.id.dialogCommentBarFrame, commentBar, "commentBarFrame").commit();

        getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
//        getDialog().setTitle("CommentsDialogFragment");
        getDialog().getWindow().setBackgroundDrawableResource(R.drawable.bg_round_corner);

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        Window window = getDialog().getWindow();

        if(window == null) return;
        WindowManager.LayoutParams params = window.getAttributes();
        params.width = (int)(getResources().getDisplayMetrics().widthPixels*0.75);
        params.height = (int)(getResources().getDisplayMetrics().heightPixels*0.75);
        window.setAttributes(params);
        window.getDecorView().setOnTouchListener(new SwipeDismissTouchListener(window.getDecorView(), null, new SwipeDismissTouchListener.DismissCallbacks() {
            @Override
            public boolean canDismiss(Object token) {
                return true;
            }

            @Override
            public void onDismiss(View view, Object token) {
                dismiss();
            }
        }));
    }
}