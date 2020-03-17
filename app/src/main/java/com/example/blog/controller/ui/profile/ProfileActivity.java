package com.example.blog.controller.ui.profile;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.example.blog.MainActivity;
import com.example.blog.R;
import com.example.blog.URLs;
import com.example.blog.controller.tools.volley.FetchJson;
import com.example.blog.controller.tools.volley.IResult;
import com.example.blog.controller.ui.comments.CommentBarFragment;
import com.example.blog.controller.ui.home.HomeFragment;
import com.example.blog.model.Categories;
import com.facebook.AccessToken;
import com.facebook.Profile;
import com.google.android.material.tabs.TabLayout;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class ProfileActivity extends AppCompatActivity implements EditProfileDialogFragment.OnProfileDataPass, CommentBarFragment.OnCommentSent {

    ImageView profilePic,background;
    TextView name;
    String userId,userName,imgStr,myUserId;
   String myFbId;
    String SHARD_PERFNAME="profile";
    URLs baseUrl=new URLs();
    String TAG="profile";
    IResult mResultCallback = null;
    FetchJson mVolleyService;
    int points;
    boolean myProfile=false;
    private CommentFragmentInterface comentFragmentInterfaceListener;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);


        final NestedScrollView scrollView=findViewById(R.id.scrollView);
        Toolbar toolbar=findViewById(R.id.toolbar);
        toolbar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                scrollView.fullScroll(ScrollView.FOCUS_UP);
            }
        });

//        background=findViewById(R.id.profile_background);
        profilePic=findViewById(R.id.ph_profilePic);
//        background.getLayoutParams().height=350;


//        Picasso.with(this).load(R.drawable.aqlamdefault).fit().into(background);

        Intent intent = getIntent();
        userId= intent.getStringExtra("user_id");



        final boolean fbLoggedOut = AccessToken.getCurrentAccessToken() == null;
//
        if(!fbLoggedOut)
            myFbId=Profile.getCurrentProfile().getId();

        SharedPreferences prefs = getSharedPreferences(SHARD_PERFNAME, Activity.MODE_PRIVATE);
       myUserId= prefs.getString("user_id",null);

        //make api call, send userId get user info

        //check if its my profile
        if(userId.equals(myUserId)|| userId.equals(myFbId)){
            //my profile
            //show additional options
            myProfile=true;


        }

//

        ProfilePostsFragment postsFragment=new ProfilePostsFragment();
        Bundle bundle=new Bundle();
        bundle.putBoolean("my_profile",myProfile);
        bundle.putString("user_id",userId);
        postsFragment.setArguments(bundle);
        getSupportFragmentManager().beginTransaction().replace(R.id.posts_frame, postsFragment, "postFragment").commit();


        ImageButton edit=findViewById(R.id.editProfile);
        if(myProfile)
            edit.setVisibility(View.VISIBLE);

        edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bundle bundle = new Bundle();
                bundle.putString("user_id",userId);
                bundle.putString("name",userName);
                bundle.putString("pic",imgStr);

                EditProfileDialogFragment cf=new EditProfileDialogFragment();
                cf.setArguments(bundle);
                FragmentTransaction ft =  getSupportFragmentManager().beginTransaction();
                Fragment prev =  getSupportFragmentManager().findFragmentByTag("dialog");
                if (prev != null) {
                    ft.remove(prev);
                }
                ft.addToBackStack(null);


                cf.show(ft, "dialog");
            }
        });

//
        name=findViewById(R.id.ph_name);


        //load profile info from db
       loadProfile();
//

        //add my posts fragment




    }
    void loadProfile(){

        String url=baseUrl.getUrl(baseUrl.getProfileInfo());
        initVolleyCallback();
        mVolleyService =new FetchJson(mResultCallback,getApplicationContext());
        Map<String,String> params=new HashMap<>();
        params.put("id",userId);
        JSONObject sendJson=new JSONObject(params);
        mVolleyService.postDataVolley("GETCALL",url,sendJson);
    }

    void initVolleyCallback(){
        mResultCallback = new IResult() {
            @Override
            public void notifySuccess(String requestType, JSONObject response) {
                Log.d(TAG, "Volley requester " + requestType);
                Log.d(TAG, "Volley JSON post" + response);
//                Toast.makeText(getContext(),"//"+response,Toast.LENGTH_LONG).show();
               if(parsJson(response)){
                   name.setText(userName);
                   if(imgStr == null || imgStr.equals("") || imgStr.equals("http://aqlam.turathalanbiaa.com/aqlam/image/000000.png")){
                       imgStr="https://alkafeelblog.edu.turathalanbiaa.com/aqlam/image/000000.png";
                   }
                     Picasso.with(getApplicationContext()).load(imgStr).fit().into(profilePic);


               }


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
                Log.d(TAG, "Volley JSON post" + error);
                loadProfile();
            }
        };
    }

    boolean parsJson(JSONObject response){

        boolean success=false;

        try {




           userName = response.getString("name");
           imgStr = response.getString("picture");
           points=response.getInt("points");
           success=true;


        }catch (JSONException e) {

            success=false;
        }

        return success;
    }

    @Override
    public void onDataPass(int data) {
       loadProfile();
    }



    //comment sent refesh
    @Override
    public void onCommentSent(int data) {

        comentFragmentInterfaceListener.sendRefresh(data);
    }

    public interface CommentFragmentInterface{
        void sendRefresh(int data);
    }
    public void setOnCommentListener(ProfileActivity.CommentFragmentInterface mComentFragmentInterface){
        comentFragmentInterfaceListener=mComentFragmentInterface;
    }


}
