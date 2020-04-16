package com.example.blog.controller.ui.home;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.android.volley.VolleyError;
import com.example.blog.URLs;
import com.example.blog.controller.tools.volley.FetchJson;
import com.example.blog.controller.tools.volley.IResult;
import com.example.blog.controller.ui.profile.ProfileActivity;
import com.example.blog.R;
import com.example.blog.controller.tools.PopUpClass;
import com.example.blog.controller.tools.TimeAgo;
import com.example.blog.model.Posts;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class FullPostFragment extends Fragment {

    TextView postTitle,postDetails,postId,viewCount,comments,userName,created_at;
    ImageView postPic,profilePic,profileBackground;
    Button catBtn;
    Posts post=new Posts();
    String id;

    URLs baseUrl=new URLs();
    private String TAG = "fullPostFragment";
    IResult mResultCallback = null;
    FetchJson mVolleyService;

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

        //change font
        SharedPreferences settingsPrefs = getActivity().getSharedPreferences("settings", Activity.MODE_PRIVATE);
        Typeface typeface = ResourcesCompat.getFont(getContext(), R.font.myfont);
        if(settingsPrefs.getInt("font",1)==3)
            typeface = ResourcesCompat.getFont(getContext(), R.font.myfont3);
        else if (settingsPrefs.getInt("font",1)==2)
            typeface = ResourcesCompat.getFont(getContext(), R.font.myfont2);

        postTitle.setTypeface(typeface);
        postDetails.setTypeface(typeface);

        //change size
        postDetails.setTextSize(settingsPrefs.getFloat("size",16));
        postTitle.setTextSize(settingsPrefs.getFloat("size",16)+4);
        postDetails.invalidate();
        postTitle.invalidate();

        Log.e("TAG", "onCreateView: "+getArguments().getInt("notify"));
        if(getArguments().getInt("notify")==1 ){
            id=getArguments().getString("postId");
            Log.d("TAG", "onCreateView: "+"poooooost id gotten"+id);
            getPost(id);

        }
        else {
            post = getArguments().getParcelable("post");
            setPost();
        }
//else {
//    post = getArguments().getParcelable("post");
//
//
////        postId.setText(post_id);
//
//    postTitle.setText(post.getTitle());
//    postDetails.setText(post.getContent());
//    postId.setText("" + post.getId());
//    viewCount.setText("" + post.getViews());
//    comments.setText(post.getCommentsCount());
//    TimeAgo timeago = new TimeAgo();
//
//    String time = timeago.covertTimeToText(post.getCreated_at());
//    created_at.setText(time);
//
//
//    if (post.getProfilePic().equals("default")) {
//        Picasso.with(getContext()).
//                load(R.drawable.default_profile_pic).into(profilePic);
//    } else {
//        Picasso.with(getContext()).
//                load(post.getProfilePic()).into(profilePic);
//
//    }
//    catBtn.setText(post.getCategory_name());
//
//    //name
//    userName.setText(post.getUsername());
//
//
//    final String img = post.getImage();
//
//    if (img != null && !img.equals("") && !img.equals("aqlam-default.jpg")) {
//        postPic.setVisibility(View.VISIBLE);
//        postPic.getLayoutParams().height = 750;
//        Picasso.with(getContext()).load(img).fit().centerCrop().into(postPic);
//    }
//
//    postPic.setOnClickListener(new View.OnClickListener() {
//        @Override
//        public void onClick(View view) {
//            PopUpClass popUpClass = new PopUpClass();
//            popUpClass.showPopupWindow(view, img);
//
//        }
//    });
//
//
//    userName.setOnClickListener(profileListener);
//    profilePic.setOnClickListener(profileListener);
//
//    catBtn.setOnClickListener(new View.OnClickListener() {
//        @Override
//        public void onClick(View view) {
//
//            Bundle bundle = new Bundle();
//            bundle.putInt("catId", post.getCategory_id());
//            Navigation.findNavController(view).navigate(R.id.action_nav_post_to_nav_cat_posts, bundle);
////
//        }
//    });
//
//}

        return root;
    }

    private void setPost() {




//        postId.setText(post_id);

        postTitle.setText(post.getTitle());
        postDetails.setText(post.getContent());
        postId.setText("" + post.getId());
        viewCount.setText("" + post.getViews());
        comments.setText(post.getCommentsCount());
        TimeAgo timeago = new TimeAgo();

        String time = timeago.covertTimeToText(post.getCreated_at());
        created_at.setText(time);


        if (post.getProfilePic().equals("default")) {
            Picasso.with(getContext()).
                    load(R.drawable.default_profile_pic).into(profilePic);
        } else {
            Picasso.with(getContext()).
                    load(post.getProfilePic()).into(profilePic);

        }
        catBtn.setText(post.getCategory_name());

        //name
        userName.setText(post.getUsername());


        final String img = post.getImage();

        if (img != null && !img.equals("") && !img.equals("aqlam-default.jpg")) {
            postPic.setVisibility(View.VISIBLE);
            postPic.getLayoutParams().height = 750;
            Picasso.with(getContext()).load(img).fit().centerCrop().into(postPic);
        }

        postPic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PopUpClass popUpClass = new PopUpClass();
                popUpClass.showPopupWindow(view, img);

            }
        });


        userName.setOnClickListener(profileListener);
        profilePic.setOnClickListener(profileListener);

        catBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Bundle bundle = new Bundle();
                bundle.putInt("catId", post.getCategory_id());
                Navigation.findNavController(view).navigate(R.id.action_nav_post_to_nav_cat_posts, bundle);
//
            }
        });
    }

    void getPost(String id) {
        Log.d(TAG, "loadPost ");
        Map<String,String> params=new HashMap<>();
        params.put("id",id);
        JSONObject jsonObject=new JSONObject(params);
        initVolleyCallback();
        mVolleyService =new FetchJson(mResultCallback,getContext());
        mVolleyService.postDataVolley("GETPOST",baseUrl.getUrl(baseUrl.getPostById()),jsonObject);
//
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

    void initVolleyCallback(){
        mResultCallback = new IResult() {
            @Override
            public void notifySuccess(String requestType,final JSONObject response) {
                Log.d(TAG, "Volley requester " + requestType);
                Log.d(TAG, "Volley JSON post" + response);
               parsJsonObj(response);
               //

            }
            @Override
            public void notifySuccessJsonArray(String requestType, JSONArray response) {
                Log.d(TAG, "Volley requester " + requestType);
                Log.d(TAG, "Volley JSON post" + response);


//                Toast.makeText(getContext(),"//"+response,Toast.LENGTH_LONG).show();

            }

            @Override
            public void notifyError(String requestType, VolleyError error) {
                Log.d(TAG, "Volley requester " + requestType);
                Log.d(TAG, "Volley JSON post " + error);


            }
        };
    }
    //
    void parsJsonObj(JSONObject obj){


        try {




                int id=obj.getInt("id");
                String title=obj.getString("title");

                String content=obj.getString("content");

//
                String created_at=obj.getString("created_at");
                String image=obj.getString("image");
                int views=obj.getInt("views");
                int rate=obj.getInt("rate");
                int status=obj.getInt("status");
                int cat_id=obj.getInt("category_id");
                String userId=obj.getString("user_id");
                String commentsCount=obj.getString("cmd_count");

                if(image != null && !image.equals("") && !image.equals("aqlam-default.jpg"))
                    image=baseUrl.getImagePath()+image;


                post.setId(id);
                post.setImage(image);
                post.setTitle(title);
                post.setCreated_at(created_at);
                post.setViews(views);
                post.setRate(rate);
                post.setCategory_id(cat_id);
                post.setStatus(status);
                post.setContent(content);
                post.setUser_id(userId);
                post.setCommentsCount(commentsCount);

                JSONObject user=obj.getJSONObject("user");
                String userName=user.getString("name");
                String profilePic=user.getString("picture");

                if(profilePic == null || profilePic.equals("") ||profilePic.equals("http://aqlam.turathalanbiaa.com/aqlam/image/000000.png")||profilePic.equals("student.png")){
//                   profilePic=baseUrl.getDefaultProfilePic();
                    profilePic="default";
                }


                JSONObject cat=obj.getJSONObject("cat");
                String catName=cat.getString("name");

                //get username and profile pic
                post.setUsername(userName);
//               profilePic=baseUrl.getImagePath(profilePic);

                //database image path is outdated
                post.setProfilePic(profilePic);
                //get cat name
                post.setCategory_name(catName);

            setPost();

        }catch (JSONException e) {
            e.printStackTrace();
            Log.d(TAG, "parsJsonObj: "+e.getMessage());
        }

    }
}