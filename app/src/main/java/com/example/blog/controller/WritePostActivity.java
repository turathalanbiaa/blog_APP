package com.example.blog.controller;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.LinearLayoutCompat;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.android.volley.VolleyError;
import com.example.blog.controller.ui.category.CatDropDownFragment;
import com.example.blog.MainActivity;
import com.example.blog.R;
import com.example.blog.controller.tools.TextValidator;
import com.example.blog.URLs;
import com.example.blog.controller.tools.volley.FetchJson;
import com.example.blog.controller.tools.volley.IResult;
import com.facebook.AccessToken;
import com.facebook.Profile;
import com.google.gson.JsonObject;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;
import com.koushikdutta.ion.ProgressCallback;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class WritePostActivity extends AppCompatActivity implements CatDropDownFragment.OnDataPass {

    private static String TAG = WritePostActivity.class.getSimpleName();

    EditText title,content;
    int cat_Id=1;

    IResult mResultCallback = null;
    FetchJson mVolleyService;

    String sendPostUrl;
    URLs baseUrl=new URLs();
    String userID="0";
    Boolean checkTitle=false, checkContent=false;

    private static final int PERMISSION_REQUEST_CODE = 100;
    ImageView uploadImg;
    File imageFile=null;

    FrameLayout imgFrame;
    ImageButton cancelPic;
    boolean updatePost=false;
    int postId;
    String updatePostUrl;
    ProgressDialog mProgressDialog;
    Button sendPost;
    EditText tag;
    List<String> tagList;
    int c=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.create_post);
        sendPost=findViewById(R.id.sendPostBtn);

        tag=findViewById(R.id.tagText);
        title=findViewById(R.id.titleEditTxt);
        content=findViewById(R.id.detailsEditTxt);
        imgFrame=findViewById(R.id.imgFrame);
        cancelPic=findViewById(R.id.cancelPic);

        Bundle bundle=getIntent().getBundleExtra("post");

        if(bundle != null){
            title.setText(bundle.getString("title"));
            content.setText(bundle.getString("content"));
            cat_Id=bundle.getInt("cat_id");
            postId=bundle.getInt("post_id");
            updatePost=true;
            checkTitle=true;
            checkContent=true;

        }



        final boolean loggedOut = AccessToken.getCurrentAccessToken() == null;
        SharedPreferences prefs = getSharedPreferences("profile", Activity.MODE_PRIVATE);

        if(!loggedOut){
           userID=Profile.getCurrentProfile().getId();
        }
        else if(prefs.getString("user_id",null)!=null){
            userID=prefs.getString("user_id",null);
        }
        else{
            Toast.makeText(getApplicationContext(),R.string.must_login_to_post,Toast.LENGTH_SHORT).show();
            finish();
        }

        uploadImg=findViewById(R.id.uploadImg);
        Button openGallary=findViewById(R.id.getImgFromGallary);
//        if(updatePost)
//            openGallary.setVisibility(View.GONE);

        openGallary.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                requestPermission();
                startActivityForResult(new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.INTERNAL_CONTENT_URI), 1);
            }
        });



        sendPostUrl=baseUrl.getSendPostUrl();
        updatePostUrl=baseUrl.getUrl(baseUrl.getUpdatePost());


       sendPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                tag.clearFocus();
                getTags();

                if(checkPostData()) {
                    buttonInactive();
                   if(imageFile != null) {
                        if (checkPermission()) {
                            uploadPostWithImg(userID, title.getText().toString(), content.getText().toString(), cat_Id,getTags());


                        }else
                            requestPermission();
                    }
                   else if(imageFile == null)
                       if(updatePost)
                           sendUpdatedPost(postId,title.getText().toString(), content.getText().toString(),cat_Id,getTags());
                       else
                       sendPostToDb(userID, title.getText().toString(), content.getText().toString(), cat_Id,getTags());


                }
                else {
                    if(title.getText().toString().isEmpty())
                        title.setError(getString(R.string.empty_field_error));
                    else content.setError(getString(R.string.empty_field_error));
                }
            }
        });
//


        title.addTextChangedListener(new TextValidator(title) {
            @Override public void validate(TextView textView, String text) {

               if(text.isEmpty()){
                    title.setError("field required");
                    checkTitle=false;
                }
                else checkTitle=true;

            }
        });
        content.addTextChangedListener(new TextValidator(content) {
            @Override public void validate(TextView textView, String text) {

                if(text.isEmpty()){
                    content.setError("field required");
                    checkContent=false;
                }
                else checkContent=true;

            }
        });

        cancelPic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                imageFile=null;

                imgFrame.setVisibility(View.GONE);
            }
        });


        final LinearLayout tagLL=findViewById(R.id.tagsLayout);
        tagList=new ArrayList<>();

        tag.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if(!b){
                    if(!tag.getText().toString().equals("") && !tag.getText().toString().equals(" "))
                    {
                        tag.setText(tag.getText().toString()+" ");
                    }
                }
            }
        });
        tag.addTextChangedListener(new TextValidator(tag) {
            @Override public void validate(TextView textView, String text) {

//                if(tagList.size()>0) {
//                    tag.setHint("");
//
//                }
                if(!text.equals(" ")&& !text.equals("")) {


                    if (text.endsWith(" ")) {
                        LayoutInflater vi = (LayoutInflater) getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                        final View v = vi.inflate(R.layout.tag, null);
                        LinearLayoutCompat.LayoutParams lparams = new LinearLayoutCompat.LayoutParams(
                                LinearLayoutCompat.LayoutParams.WRAP_CONTENT, LinearLayoutCompat.LayoutParams.WRAP_CONTENT);
                        v.setLayoutParams(lparams);
                        tagLL.addView(v);
                        TextView tagName = v.findViewById(R.id.tagName);
                        TextView tagId = v.findViewById(R.id.tagId);
                        LinearLayout tagBody=v.findViewById(R.id.tagBody);

                        tagId.setText(""+c++);
                        tagName.setText(text.replace(" ", ""));

                        tagList.add(tagName.getText().toString());
//                        Toast.makeText(getApplicationContext(),"/"+tagList.size(),Toast.LENGTH_SHORT).show();

                        tag.setText("");

                        tagBody.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                tagLL.removeView(v);
                                TextView id =view.findViewById(R.id.tagId);
                                tagList.set(Integer.parseInt(id.getText().toString()),"");
//                                c--;
//                                Toast.makeText(getApplicationContext(),"/"+id.getText(),Toast.LENGTH_SHORT).show();
                                for(int i=0;i<tagList.size();i++) {
                                    Log.d(TAG, "onClick: "+tagList.toString());
//                                    Toast.makeText(getApplicationContext(), "/" + tagList.get(i), Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    }
                }

            }
        });

    }


    String getTags() {
        String tagsStr="";
        if(tagList.size()>0) {
            for (int i = 0; i < tagList.size(); i++) {
                if (!tagList.get(i).equals(""))
                    tagsStr += tagList.get(i) + ",";

            }
        }
        return tagsStr;
    }

    private void buttonInactive() {

        sendPost.setClickable(false);
    }
    private void buttonActive() {

        sendPost.setClickable(true);
    }

    private void sendUpdatedPost(int postId, String title, String content, int cat_id,String tags) {
        Map<String,String> params=new HashMap<String, String>();
        params.put("id",""+postId);
        params.put("title",title);
        params.put("content",content);
        params.put("category_id",""+cat_id);
//        if(!tags.equals(""))
        params.put("tags",tags);

        JSONObject sendObj =new JSONObject(params);
        initVolleyCallback();
        mVolleyService =new FetchJson(mResultCallback,getApplicationContext());
        mVolleyService.postDataVolley("updatePost",updatePostUrl,sendObj);
    }


    public void sendPostToDb(String userId, String title,String content,int cat_Id,String tags){
        Log.d(TAG, "sendPostToDb: "+userId+"/"+title+"/"+content+"/"+cat_Id);

        Map<String,String> params=new HashMap<String, String>();
        params.put("user_id",userId);
        params.put("title",title);
        params.put("content",content);
        params.put("category_id",""+cat_Id);
        if(!tags.equals(""))
        params.put("tags",tags);
        JSONObject sendObj =new JSONObject(params);
        initVolleyCallback();
        mVolleyService =new FetchJson(mResultCallback,getApplicationContext());
        mVolleyService.postDataVolley("send",sendPostUrl,sendObj);
//

    }
    void initVolleyCallback(){
        mResultCallback = new IResult() {
            @Override
            public void notifySuccess(String requestType,final JSONObject response) {
                Log.d(TAG, "Volley requester " + requestType);
                Log.d(TAG, "Volley JSON post" + response);

                buttonActive();
                if(requestType.equals("updatePost")){
                    finish();
                }
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);
                finish();


//                Toast.makeText(getApplicationContext(),"//"+response,Toast.LENGTH_LONG).show();


            }
            @Override
            public void notifySuccessJsonArray(String requestType, JSONArray response) {
                Log.d(TAG, "Volley requester " + requestType);
                Log.d(TAG, "Volley JSON post" + response);

            }

            @Override
            public void notifyError(String requestType, VolleyError error) {
                Log.d(TAG, "Volley requester " + requestType);
                Log.d(TAG, "Volley JSON post" + error);


                buttonActive();
//                Toast.makeText(getApplicationContext(),"hm"+error,Toast.LENGTH_LONG).show();



            }
        };
    }

    Boolean checkPostData(){

        if(checkTitle && checkContent)
            return true;
        else  return false;

    }
    //from cat dropdown fragment
    @Override
    public void onDataPass(int data) {
        Log.d("LOG","cat id " + data);
        cat_Id=data;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);


        //Detects request codes
        if(requestCode==1 && resultCode == Activity.RESULT_OK) {
            Uri selectedImage = data.getData();
            Bitmap bitmap = null;
            try {
                bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), selectedImage);
                imgFrame.setVisibility(View.VISIBLE);
                Picasso.with(WritePostActivity.this).load(selectedImage).into(uploadImg);
                imageFile = new File(getImgPath(selectedImage));
                Log.d(TAG, "onActivityResult: "+imageFile);


            } catch (FileNotFoundException e) {

                e.printStackTrace();
            } catch (IOException e) {

                e.printStackTrace();
            }
        }
    }
    public String getImgPath(Uri uri) {
        String[] projection = { MediaStore.Images.Media.DATA };
        Cursor cursor = getContentResolver().query(uri, projection, null, null, null);
//        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        int columnIndex = cursor.getColumnIndex(projection[0]);
        String filePath = cursor.getString(columnIndex);
        cursor.close();

        return filePath;
    }

    void uploadPostWithImg(String userId, final String title, String content, int cat_Id,String tags){


        int llPadding = 30;
        LinearLayout ll = new LinearLayout(this);
        ll.setOrientation(LinearLayout.HORIZONTAL);
        ll.setPadding(llPadding, llPadding, llPadding, llPadding);
        ll.setGravity(Gravity.CENTER);
        LinearLayout.LayoutParams llParam = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        llParam.gravity = Gravity.CENTER;
        ll.setLayoutParams(llParam);

        ProgressBar progressBar = new ProgressBar(this);
        progressBar.setIndeterminate(true);
        progressBar.setPadding(0, 0, llPadding, 0);
        progressBar.setLayoutParams(llParam);

        llParam = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        llParam.gravity = Gravity.CENTER;
        final TextView tvText = new TextView(this);

        tvText.setTextColor(Color.parseColor("#000000"));
        tvText.setTextSize(20);
        tvText.setLayoutParams(llParam);

        ll.addView(progressBar);
        ll.addView(tvText);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(false);
        builder.setView(ll);

        final AlertDialog dialog = builder.create();
        dialog.show();
        Window window = dialog.getWindow();
        if (window != null) {
            WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
            layoutParams.copyFrom(dialog.getWindow().getAttributes());
            layoutParams.width = LinearLayout.LayoutParams.WRAP_CONTENT;
            layoutParams.height = LinearLayout.LayoutParams.WRAP_CONTENT;
            dialog.getWindow().setAttributes(layoutParams);
        }




        //"https://api.imgur.com/3/upload"
        if(updatePost){
            Ion.with(getApplicationContext())
                    .load(updatePostUrl)
                    .uploadProgressHandler(new ProgressCallback() {
                        @Override
                        public void onProgress(long uploaded, long total) {
                            tvText.setText((getResources().getString(R.string.loading)+" "+uploaded*100/total+"%"));
//                        Toast.makeText(getApplicationContext(), uploaded+"/"+total,Toast.LENGTH_SHORT).show();
                            Log.d(TAG, "onProgress: "+uploaded+"/"+total);
                        }
                    })

                    .setTimeout(60 * 60 * 1000)
                    .setMultipartFile("image", "file", imageFile)
                    .setMultipartParameter("id",""+postId)
                    .setMultipartParameter("title",title)
                    .setMultipartParameter("content",content)
                    .setMultipartParameter("category_id",""+cat_Id)
                    .setMultipartParameter("tags",tags)

                    .asJsonObject()
                    // run a callback on completion
                    .setCallback(new FutureCallback<JsonObject>() {
                        @Override
                        public void onCompleted(Exception e, JsonObject result) {
//                        mProgressDialog.dismiss();
                            dialog.dismiss();


                            Log.d(TAG, "onCompleted: "+result);
                            buttonActive();
//
                            if (e != null) {
                                Log.e(TAG, "error/ "+e );
                                Toast.makeText(getApplicationContext(), R.string.upload_failed, Toast.LENGTH_LONG).show();
                                return;
                            }
                            Toast.makeText(getApplicationContext(), R.string.upload_complete, Toast.LENGTH_LONG).show();
                            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                            startActivity(intent);
                            finish();
                        }
                    });
        }
        else {
            Ion.with(getApplicationContext())
                    .load(sendPostUrl)
                    .uploadProgressHandler(new ProgressCallback() {
                        @Override
                        public void onProgress(long uploaded, long total) {
                            tvText.setText((getResources().getString(R.string.loading) + " " + uploaded * 100 / total + "%"));
//                        Toast.makeText(getApplicationContext(), uploaded+"/"+total,Toast.LENGTH_SHORT).show();
                            Log.d(TAG, "onProgress: " + uploaded + "/" + total);
                        }
                    })

                    .setTimeout(60 * 60 * 1000)
                    .setMultipartFile("input_img", "file", imageFile)
                    .setMultipartParameter("user_id", userId)
                    .setMultipartParameter("title", title)
                    .setMultipartParameter("content", content)
                    .setMultipartParameter("category_id", "" + cat_Id)
                    .setMultipartParameter("tags",tags)
                    .asJsonObject()
                    // run a callback on completion
                    .setCallback(new FutureCallback<JsonObject>() {
                        @Override
                        public void onCompleted(Exception e, JsonObject result) {
//                        mProgressDialog.dismiss();
                            dialog.dismiss();


                            Log.d(TAG, "onCompleted: " + result);
                            buttonActive();
//
                            if (e != null) {
                                Log.e(TAG, "error/ " + e);
                                Toast.makeText(getApplicationContext(), R.string.upload_failed, Toast.LENGTH_LONG).show();
                                return;
                            }
                            Toast.makeText(getApplicationContext(), R.string.upload_complete, Toast.LENGTH_LONG).show();
                            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                            startActivity(intent);
                            finish();
                        }
                    });
        }

    }

    private boolean checkPermission() {
        int result = ContextCompat.checkSelfPermission(WritePostActivity.this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (result == PackageManager.PERMISSION_GRANTED) {
            return true;
        } else {
            return false;
        }
    }

    private void requestPermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(WritePostActivity.this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            Toast.makeText(getApplicationContext(), "Write External Storage permission allows us to save files. Please allow this permission in App Settings.", Toast.LENGTH_LONG).show();
        } else {
            ActivityCompat.requestPermissions(WritePostActivity.this, new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE}, PERMISSION_REQUEST_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_REQUEST_CODE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Log.e("value", "Permission Granted, Now you can use local drive .");
            } else {
                Log.e("value", "Permission Denied, You cannot use local drive .");
            }
            break;
        }
    }




}
