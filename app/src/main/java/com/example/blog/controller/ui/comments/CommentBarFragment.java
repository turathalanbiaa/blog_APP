package com.example.blog.controller.ui.comments;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.graphics.drawable.DrawableCompat;
import androidx.fragment.app.Fragment;

import com.android.volley.VolleyError;
import com.example.blog.MainActivity;
import com.example.blog.R;
import com.example.blog.URLs;
import com.example.blog.controller.tools.volley.FetchJson;
import com.example.blog.controller.tools.volley.IResult;
import com.facebook.AccessToken;
import com.facebook.Profile;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import static android.content.Context.INPUT_METHOD_SERVICE;

public class CommentBarFragment extends Fragment {

    private static String TAG = "commentBar";
    IResult mResultCallback = null;
    FetchJson mVolleyService;


    String sendPostUrl;
    URLs baseUrl=new URLs();

    EditText comment;
    String userID="0";
    int postId;
    boolean actLoggedIn=false;
    OnCommentSent dataPasser;
    ImageButton sendComment;


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
        dataPasser = (OnCommentSent) context;}
        catch (Exception e) {
            Log.e(TAG, "onAttach: "+e);
        }
    }

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.comment_bar, container, false);
        SharedPreferences prefs = getActivity().getSharedPreferences("profile", Activity.MODE_PRIVATE);
        final boolean loggedOut = AccessToken.getCurrentAccessToken() == null;
//
        if(!loggedOut){
            userID= Profile.getCurrentProfile().getId();
        }

        else if(prefs.getString("user_id",null)!=null){
            actLoggedIn=true;
            userID=prefs.getString("user_id",null);
        }

        sendPostUrl=baseUrl.getUrl(baseUrl.getSendComment());
        postId=getArguments().getInt("postId");
        sendComment=root.findViewById(R.id.sendComment);

        comment=root.findViewById(R.id.commentEditText);

        sendComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(comment.getText().toString().isEmpty()) {
                    comment.setError(getString(R.string.empty_field_error));
                }
                else
                    sendCommentToDb(postId, userID, comment.getText().toString());
            }
        });

        return root;
    }

    private void sendCommentToDb(int postId,String userID, String content) {
        Log.d(TAG, "sendCommentToDb: "+postId+"/"+userID+"/"+content);
        Map<String,String> params=new HashMap<>();
        params.put("user_id",userID);
        params.put("post_id",""+postId);
        params.put("content",content);
        JSONObject sendObj =new JSONObject(params);
        initVolleyCallback();
        mVolleyService =new FetchJson(mResultCallback,getContext());
        mVolleyService.postDataVolley("GETCALL",sendPostUrl,sendObj);
        buttonInactive();

    }

    private void buttonInactive() {
        Drawable drawable = getResources().getDrawable(R.drawable.ic_menu_send_gray);
        sendComment.setBackground(drawable);
        sendComment.setClickable(false);
    }
    private void buttonActive() {
        Drawable drawable = getResources().getDrawable(R.drawable.ic_menu_send);
        sendComment.setBackground(drawable);
        sendComment.setClickable(true);
    }


    void initVolleyCallback(){
        mResultCallback = new IResult() {
            @Override
            public void notifySuccess(String requestType,final JSONObject response) {
                Log.d(TAG, "Volley requester " + requestType);
                Log.d(TAG, "Volley JSON post" + response);
                try  {
                    InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(getView().getWindowToken(), 0);
                } catch (Exception e) {

                }
                buttonActive();
                comment.setText("");
                comment.clearFocus();
                try {
                    Toast.makeText(getContext(),R.string.comment_sent,Toast.LENGTH_LONG).show();

                }catch (Exception e){
                    Log.e(TAG, "notifyError: ",e );
                }

//              if(getActivity().getLocalClassName().equalsIgnoreCase("MainActivity"))
                passData(1);


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
                try  {
                    InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(getView().getWindowToken(), 0);
                } catch (Exception e) {

                }

                buttonActive();
                try {
                    Toast.makeText(getContext(),R.string.comment_not_sent,Toast.LENGTH_LONG).show();

                }catch (Exception e){
                    Log.e(TAG, "notifyError: ",e );
                }



            }
        };
    }

    public interface OnCommentSent {
        public void onCommentSent(int data);
    }
    public void passData(int data) {
        Log.e(TAG, "passData: "+data );
        dataPasser.onCommentSent(data);
    }
}