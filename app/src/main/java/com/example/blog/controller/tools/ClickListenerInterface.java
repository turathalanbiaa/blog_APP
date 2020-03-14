package com.example.blog.controller.tools;

import android.view.View;




public interface ClickListenerInterface {
    void onItemClick(View view, int position);
    void onCommentClick(View view, int position);
    void onPicClick(View view, int position);
    void onPostExpandClick(View view, int position);
    void onProfileClick(View view, int position);
    void onCatClick(View view, int position);
}



