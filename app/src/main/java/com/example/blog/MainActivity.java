package com.example.blog;


import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import com.example.blog.controller.notification.FireBaseService;
//import com.example.blog.controller.notification.ForegroundService;
import com.example.blog.controller.notification.NotificationUtils;
import com.example.blog.controller.ui.category.CatDropDownFragment;
import com.example.blog.controller.ui.comments.CommentBarFragment;
import com.example.blog.controller.ui.profile.ProfileActivity;
import com.example.blog.controller.WritePostActivity;
import com.facebook.AccessToken;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.Profile;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.google.android.material.navigation.NavigationView;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.squareup.picasso.Picasso;

import androidx.drawerlayout.widget.DrawerLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.view.Menu;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

public class MainActivity extends AppCompatActivity implements CatDropDownFragment.OnDataPass, CommentBarFragment.OnCommentSent {

    private AppBarConfiguration mAppBarConfiguration;
    ImageView profilePic;
    TextView nameTextView,emailTextView;
    FloatingActionButton fab;
    public String fbId="";
    public Boolean actLoggedIn=false;
    SharedPreferences prefs;
   public boolean loggedOut;

    private FragmentInterface fragmentInterfaceListener;
    private CommentFragmentInterface comentFragmentInterfaceListener;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);


        //create channels
        NotificationUtils nu=new NotificationUtils(getApplicationContext());
        nu.createChannels();
        //firebase service
        Intent serviceIntent = new Intent(this, FireBaseService.class);
        startService(serviceIntent);


        setSupportActionBar(toolbar);
        loggedOut = AccessToken.getCurrentAccessToken() == null;

        prefs = getSharedPreferences("profile", Activity.MODE_PRIVATE);
        if(prefs.getString("user_id",null)!=null){
            actLoggedIn=true;
        }


       fab = findViewById(R.id.fab);


        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_home, R.id.nav_bloggers,
                R.id.nav_categories, R.id.nav_register, R.id.nav_login, R.id.nav_logout)
                .setDrawerLayout(drawer)
                .build();
        final NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);
//


        if (!loggedOut) {
//
             navigationView.getMenu().findItem(R.id.nav_login).setVisible(false);
            navigationView.getMenu().findItem(R.id.nav_logout).setVisible(true);
            //Using Graph API
            try {

                getUserProfile(AccessToken.getCurrentAccessToken());
            }catch (Exception e){}


        }
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//
                if(!loggedOut || actLoggedIn){
                    Intent intent =new Intent(getApplicationContext(), WritePostActivity.class);
                    startActivity(intent);
                }
                else{
                    navController.navigate(R.id.nav_login);
                }

            }
        });




        View headerView =  navigationView.inflateHeaderView(R.layout.nav_header_main);
        profilePic = headerView.findViewById(R.id.profilePic);
        nameTextView=headerView.findViewById(R.id.nameTextView);
        emailTextView=headerView.findViewById(R.id.emailTextView);

        profilePic.setOnClickListener(myProfileClickListener);
        nameTextView.setOnClickListener(myProfileClickListener);



        if(actLoggedIn){
            navigationView.getMenu().findItem(R.id.nav_login).setVisible(false);
            navigationView.getMenu().findItem(R.id.nav_logout).setVisible(true);

            String img=prefs.getString("profile_pic",null);
            if(img != null && !img.equals("")) {
                if (img.equals("default"))
                    Picasso.with(MainActivity.this).load(R.drawable.default_profile_pic).into(profilePic);
                else
                    Picasso.with(MainActivity.this).load(img).into(profilePic);
            }
            nameTextView.setText(prefs.getString("user_name","ضيف"));
        }
        else{
            nameTextView.setText("ضيف");
        }


        Intent intent=getIntent();
        int catId=intent.getIntExtra("catId",0);
        if(catId!=0){
            Bundle bundle = new Bundle();
            bundle.putInt("catId",catId);
            navController.navigate(R.id.nav_cat_posts,bundle);

        }
        if(intent.getExtras().getString("id",null)!=null){
            Bundle bundle = new Bundle();
            bundle.putInt("notify",1);
            bundle.putString("postId",intent.getExtras().getString("id"));
            navController.navigate(R.id.nav_full_post,bundle);
        }
        Bundle extras = getIntent().getExtras();
        int notify=extras.getInt("notify",0);

        if(notify==1){
            String postId=extras.getString("postId");
            if(!postId.equals("")){
                Bundle bundle = new Bundle();
                bundle.putInt("notify",1);
                bundle.putString("postId",postId);
                navController.navigate(R.id.nav_full_post,bundle);
            }
        }

//        FirebaseInstanceId.getInstance().getInstanceId()
//                .addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
//                    @Override
//                    public void onComplete(@NonNull Task<InstanceIdResult> task) {
//                        if (!task.isSuccessful()) {
//                            Log.w("TAG", "getInstanceId failed", task.getException());
//                            return;
//                        }
//
//                        // Get new Instance ID token
//                        String token = task.getResult().getToken();
//
//                        // Log and toast
////                        String msg = getString(R.string.msg_token_fmt, token);
//                        Log.d("TAG", token);
////                        Toast.makeText(MainActivity.this, msg, Toast.LENGTH_SHORT).show();
//                    }
//                });

//
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }


    private void getUserProfile(AccessToken currentAccessToken) {
        GraphRequest request = GraphRequest.newMeRequest(
                currentAccessToken, new GraphRequest.GraphJSONObjectCallback() {
                    @Override
                    public void onCompleted(JSONObject object, GraphResponse response) {
                        Log.d("TAG", ""+response);
                        if(response.getError() == null)
                        try {
                            String first_name = object.getString("first_name");
                            String last_name = object.getString("last_name");
                            String email = object.getString("email");
                            String id = object.getString("id");
                            String image_url = "https://graph.facebook.com/" + id + "/picture?type=normal";
//                            Toast.makeText(getApplicationContext(),first_name+" "+email+" , "+id,Toast.LENGTH_LONG).show();

                            nameTextView.setText(first_name +" "+last_name);
                            fbId=id;
//                            emailTextView.setText(email);
//                            txtUsername.setText("First Name: " + first_name + "\nLast Name: " + last_name);
//                            txtEmail.setText(email);
                            Picasso.with(MainActivity.this).load(image_url).into(profilePic);
//
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                });

        Bundle parameters = new Bundle();
        parameters.putString("fields", "first_name,last_name,email,id");
        request.setParameters(parameters);
        request.executeAsync();


    }


    //go to my profile
    View.OnClickListener myProfileClickListener = new View.OnClickListener() {
        @Override
        public void onClick(android.view.View view) {
            //go to profile
            if(!loggedOut) {
                Intent intent = new Intent(getApplicationContext(), ProfileActivity.class);
                intent.putExtra("user_id", Profile.getCurrentProfile().getId());
                startActivity(intent);
//                navController.navigate(R.id.nav_profile);
            }
            else if(actLoggedIn){
                Intent intent = new Intent(getApplicationContext(), ProfileActivity.class);
                intent.putExtra("user_id",prefs.getString("user_id",null));
                startActivity(intent);

            }
        }
    };

    public FloatingActionButton getFloatingActionButton() {
        return fab;
    }

    //cat drop down

    @Override
    public void onDataPass(int data) {

        fragmentInterfaceListener.sendData(data);
    }

    public interface FragmentInterface{
        void sendData(int data);
    }
    public void setOnDataListener(FragmentInterface fragmentInterface){
        fragmentInterfaceListener=fragmentInterface;
    }

    //comment sent refesh
    @Override
    public void onCommentSent(int data) {

        comentFragmentInterfaceListener.sendRefresh(data);
    }

    public interface CommentFragmentInterface{
        void sendRefresh(int data);
    }
    public void setOnCommentListener(CommentFragmentInterface mComentFragmentInterface){
       comentFragmentInterfaceListener=mComentFragmentInterface;
    }

}
