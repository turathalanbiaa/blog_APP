package com.example.blog.controller.ui.profile;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
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
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.DialogFragment;

import com.android.volley.VolleyError;
import com.example.blog.R;
import com.example.blog.URLs;
import com.example.blog.controller.tools.SwipeDismissTouchListener;
import com.example.blog.controller.tools.TextValidator;
import com.example.blog.controller.tools.volley.FetchJson;
import com.example.blog.controller.tools.volley.IResult;
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
import java.util.HashMap;
import java.util.Map;

import static com.facebook.FacebookSdk.getApplicationContext;


public class EditProfileDialogFragment extends DialogFragment  {

    String userId,pic,name;
    EditText userName;
    Button chooseImg,save,cancel;
    ImageView profilePic;
    boolean nameCheck=true;

    private static final String TAG = "EditProfileDialogFragme";
    String updateProfileUrl;
    URLs baseUrl=new URLs();
    private static final int PERMISSION_REQUEST_CODE = 100;
    File imageFile=null;


    IResult mResultCallback = null;
    FetchJson mVolleyService;

    OnProfileDataPass dataPasser;
    ProgressDialog progressDialog;


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            dataPasser = (OnProfileDataPass) context;}
        catch (Exception e) {
            Log.e(TAG, "onAttach: "+e);
        }
    }

    public EditProfileDialogFragment(){}

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

       View view = inflater.inflate(R.layout.profile_edit, container,false);


        progressDialog=new ProgressDialog(getContext(),R.style.AlertDialogCustom);
        progressDialog.setCancelable(false);
        progressDialog.setTitle(R.string.wait);

       updateProfileUrl=baseUrl.getUrl(baseUrl.getUpdateProfile());

       userId= getArguments().getString("user_id");
       pic= getArguments().getString("pic");
       name=getArguments().getString("name");

       chooseImg=view.findViewById(R.id.getImgFromGallary);
       save=view.findViewById(R.id.save);
       cancel=view.findViewById(R.id.cancel);
       profilePic=view.findViewById(R.id.ph_profilePic);
       userName=view.findViewById(R.id.ph_name);

       userName.setText(name);
        if(pic == null || pic.equals("") || pic.equals("http://aqlam.turathalanbiaa.com/aqlam/image/000000.png") ||pic.equals("default"))
            Picasso.with(getApplicationContext()).load(R.drawable.default_profile_pic).into(profilePic);

        else
        Picasso.with(getContext()).load(pic).fit().into(profilePic);

        chooseImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                requestPermission();
                startActivityForResult(new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI), 1);

            }
        });

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });


        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(nameCheck){
                    save.setClickable(false);

                    name=userName.getText().toString();
                    if(imageFile !=null){
                        if (checkPermission())
                        {
                            updateWithImg(userId,name);
                        }

                         else
                            requestPermission();

                    }
                    else{
                        //
                        progressDialog.show();
                        updateNoImg(userId,name);
                    }



                }

            }
        });

        userName.addTextChangedListener(new TextValidator(userName) {
            @Override public void validate(TextView textView, String text) {

                if(text.isEmpty()){
                    userName.setError(getString(R.string.empty_field_error));
                    nameCheck=false;
                }
                else {
                    name=userName.getText().toString();
                    nameCheck=true;}

            }
        });



//        getDialog().setTitle("ProfileEditDialogFragment");
        getDialog().getWindow().setBackgroundDrawableResource(R.drawable.bg_round_corner);

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        Window window = getDialog().getWindow();

        if(window == null) return;
        WindowManager.LayoutParams params = window.getAttributes();
        params.width = (int)(getResources().getDisplayMetrics().widthPixels*0.90);
//        params.height = (int)(getResources().getDisplayMetrics().heightPixels*0.50);
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

    @Override
   public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);


        //Detects request codes
        if(requestCode==1 && resultCode == Activity.RESULT_OK) {
            Uri selectedImage = data.getData();
            Bitmap bitmap = null;
            try {
                bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), selectedImage);
                Picasso.with(getContext()).load(selectedImage).into(profilePic);
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
        Cursor cursor = getActivity().getContentResolver().query(uri, projection, null, null, null);
//        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        int columnIndex = cursor.getColumnIndex(projection[0]);
        String filePath = cursor.getString(columnIndex);
        cursor.close();

        return filePath;
    }



    void updateWithImg(String userId, final String name){


        int llPadding = 30;
        LinearLayout ll = new LinearLayout(getContext());
        ll.setOrientation(LinearLayout.HORIZONTAL);
        ll.setPadding(llPadding, llPadding, llPadding, llPadding);
        ll.setGravity(Gravity.CENTER);
        LinearLayout.LayoutParams llParam = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        llParam.gravity = Gravity.CENTER;
        ll.setLayoutParams(llParam);

        ProgressBar progressBar = new ProgressBar(getContext());
        progressBar.setIndeterminate(true);
        progressBar.setPadding(0, 0, llPadding, 0);
        progressBar.setLayoutParams(llParam);

        llParam = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        llParam.gravity = Gravity.CENTER;
        final TextView tvText = new TextView(getContext());

        tvText.setTextColor(Color.parseColor("#000000"));
        tvText.setTextSize(20);
        tvText.setLayoutParams(llParam);

        ll.addView(progressBar);
        ll.addView(tvText);

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext(),R.style.AlertDialogCustom);
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


        Ion.with(getContext())
                .load(updateProfileUrl)
//
//
                .uploadProgressHandler(new ProgressCallback() {
                    @Override
                    public void onProgress(long uploaded, long total) {

//
                        tvText.setText((getResources().getString(R.string.loading)+" "+uploaded*100/total+"%"));

//                        Toast.makeText(getApplicationContext(), uploaded+"/"+total,Toast.LENGTH_SHORT).show();

                        Log.d(TAG, "onProgress: "+uploaded+"/"+total);


                    }
                })

                .setTimeout(60 * 60 * 1000)
                .setMultipartFile("image", "file", imageFile)
                .setMultipartParameter("id",userId)
                .setMultipartParameter("name",name)
                .asJsonObject()
                // run a callback on completion
                .setCallback(new FutureCallback<JsonObject>() {
                    @Override
                    public void onCompleted(Exception e, JsonObject result) {
//                        mProgressDialog.dismiss();
                        dialog.dismiss();

                        save.setClickable(true);
                        progressDialog.dismiss();
                        Log.d(TAG, "onCompleted: "+result);
//
                        if (e != null) {
                            Log.e(TAG, "error/ "+e );
                            try {
                                Toast.makeText(getContext(), R.string.upload_failed, Toast.LENGTH_LONG).show();
                            }catch (Exception e1){
                                Log.e(TAG, "notifyError: ",e1 );
                            }
                            return;
                        }
                        passData(1);
                        dismiss();
                        try {
                            Toast.makeText(getContext(), R.string.upload_complete, Toast.LENGTH_LONG).show();
                        }catch (Exception e1){
                            Log.e(TAG, "notifyError: ",e1 );
                        }
                        //
                    }
                });

    }

    public void updateNoImg(String userId, String name){

        Log.d(TAG, "updateNoImg: "+userId);
        Map<String,String> params=new HashMap<String, String>();
        params.put("id",userId);
        params.put("name",name);
        JSONObject sendObj =new JSONObject(params);
        initVolleyCallback();
        mVolleyService =new FetchJson(mResultCallback,getContext());
        mVolleyService.postDataVolley("send",updateProfileUrl,sendObj);
//

    }
    void initVolleyCallback(){
        mResultCallback = new IResult() {
            @Override
            public void notifySuccess(String requestType,final JSONObject response) {
                Log.d(TAG, "Volley requester " + requestType);
                Log.d(TAG, "Volley JSON post" + response);

                save.setClickable(true);
                progressDialog.dismiss();
                passData(1);
                dismiss();


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

                save.setClickable(true);
                progressDialog.dismiss();

                try {
                    Toast.makeText(getApplicationContext(),R.string.no_connection,Toast.LENGTH_LONG).show();

                }catch (Exception e){}
//                Toast.makeText(getApplicationContext(),"hm"+error,Toast.LENGTH_LONG).show();



            }
        };
    }

    private boolean checkPermission() {
        int result = ContextCompat.checkSelfPermission(getActivity(), android.Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (result == PackageManager.PERMISSION_GRANTED) {
            return true;
        } else {
            return false;
        }
    }

    private void requestPermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(), android.Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
//            Toast.makeText(getContext(), "Write External Storage permission allows us to save files. Please allow this permission in App Settings.", Toast.LENGTH_LONG).show();
        } else {
            ActivityCompat.requestPermissions(getActivity(), new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE}, PERMISSION_REQUEST_CODE);
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

    public interface OnProfileDataPass {
        public void onDataPass(int data);
    }
    public void passData(int data) {
        dataPasser.onDataPass(data);
    }


}