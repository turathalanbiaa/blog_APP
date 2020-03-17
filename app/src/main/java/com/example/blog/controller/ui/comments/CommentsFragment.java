package com.example.blog.controller.ui.comments;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.VolleyError;
import com.example.blog.MainActivity;
import com.example.blog.R;
import com.example.blog.URLs;
import com.example.blog.controller.ui.profile.ProfileActivity;
import com.example.blog.model.Comments;
import com.example.blog.controller.tools.PaginationListener;
import com.example.blog.controller.tools.volley.FetchJson;
import com.example.blog.controller.tools.volley.IResult;
import com.facebook.AccessToken;
import com.facebook.Profile;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


public class CommentsFragment extends Fragment implements CommentsRecyclerViewAdapter.ClickListenerInterface,MainActivity.CommentFragmentInterface ,ProfileActivity.CommentFragmentInterface{


    RecyclerView recyclerView;
//    SwipeRefreshLayout swipeRefresh;
    CommentsRecyclerViewAdapter adapter;
    LinearLayout commentsLinearLayout;
    LinearLayoutManager layoutManager;

    private String TAG = "commentsFragment";
    IResult mResultCallback = null;
    FetchJson mVolleyService;
    private static final int PAGE_START = 1;
    private boolean isLoading = false;
    private boolean isLastPage = false;
    private int TOTAL_PAGES = 3;
    private int currentPage = PAGE_START;

    //
    URLs baseUrl=new URLs();
    String firstPageUrl;
    String route="commentpagination";

    Map<String,String> params=new HashMap<>();
    JSONObject sendJson;
    String deleteCommentUrl;
    int deletedItemPosition;


    public CommentsFragment(){}

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
       View root = inflater.inflate(R.layout.fragment_comments, container,false);


       if(getActivity().getLocalClassName().equalsIgnoreCase("MainActivity"))
        ((MainActivity) getActivity()).setOnCommentListener(this);
       //profile act
       else
           ((ProfileActivity) getActivity()).setOnCommentListener(this);

        int postId;


        if(getArguments()!=null) {
            postId = getArguments().getInt("postId");
            params.put("post_id",""+postId);
            sendJson=new JSONObject(params);

        }


        firstPageUrl=baseUrl.getUrl(route);
        deleteCommentUrl=baseUrl.getUrl(baseUrl.getDeleteComment());


//

//        swipeRefresh=root.findViewById(R.id.swipeRefresh);
        recyclerView = root.findViewById(R.id.comments_recycler_view);
        commentsLinearLayout = root.findViewById(R.id.commentsLinearLayout);
        layoutManager=new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);
        adapter= new CommentsRecyclerViewAdapter(getContext());

        adapter.setClickListener(this);
        adapter.setNameClickListener(this);
        recyclerView.setAdapter(adapter);

        recyclerView.addOnScrollListener(new PaginationListener(layoutManager) {
            protected void loadMoreItems() {
                isLoading = true;
                currentPage += 1;

                // mocking network delay for API call
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        loadNextPage();

                    }
                }, 1000);
            }

            @Override
            public int getTotalPageCount() {
                return TOTAL_PAGES;
            }

            @Override
            public boolean isLastPage() {
                return isLastPage;
            }

            @Override
            public boolean isLoading() {
                return isLoading;
            }
        });


//        TextView refreshComments=root.findViewById(R.id.comment_refresh);
//        refreshComments.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//               refresh();
//            }
//        });



        loadFirstPage();

        return root;
    }

    private void refresh() {
        currentPage = PAGE_START;
        isLastPage = false;
        adapter.clear();
        loadFirstPage();
    }

    @Override
    public void onResume() {
        super.onResume();

    }


    private void loadFirstPage() {



        initVolleyCallback();
        mVolleyService =new FetchJson(mResultCallback,getContext());

        mVolleyService.postDataVolley("GETCALL",firstPageUrl,sendJson);
//        mVolleyService.getDataVolley("GETCALL",firstPageUrl);


    }

    private void loadNextPage() {
        Log.d(TAG, "loadNextPage: " + currentPage);

      String nextPageUrl=baseUrl.getNextPageUrl(route,currentPage);

//      initVolleyCallback();
        mVolleyService =new FetchJson(mResultCallback,getContext());
        mVolleyService.postDataVolley("GETCALL",nextPageUrl,sendJson);
//
    }




    void initVolleyCallback(){
        mResultCallback = new IResult() {
            @Override
            public void notifySuccess(String requestType,final JSONObject response) {
                Log.d(TAG, "Volley requester " + requestType);
                Log.d(TAG, "Volley JSON post" + response);
//                Toast.makeText(getContext(),"//"+response,Toast.LENGTH_LONG).show();
                if(requestType.equals("delete")){
                    adapter.remove(adapter.getItem(deletedItemPosition));
                    try {
                        Toast.makeText(getContext(),R.string.delete_complete,Toast.LENGTH_LONG).show();

                    }catch (Exception e){
                        Log.e(TAG, "notifyError: ",e );
                    }
                    }
                else {
                    adapter.removeLoadingFooter();
                    isLoading = false;
                    ArrayList<Comments> commentsList;
                    commentsList = parsJsonObj(response);
                    adapter.addAll(commentsList);
                    if (currentPage <= TOTAL_PAGES) adapter.addLoadingFooter();
                    else isLastPage = true;

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

               if(requestType.equals("delete")){
                   try {
                       Toast.makeText(getContext(), R.string.delete_failed, Toast.LENGTH_LONG).show();
                   }catch (Exception e){
                       Log.e(TAG, "notifyError: ",e );
                   }
                }
                else {
                   adapter.removeLoadingFooter();
                   isLoading = false;

               }

            }
        };
    }
    //
    ArrayList<Comments> parsJsonObj(JSONObject response){

        ArrayList<Comments> commentsList=new ArrayList<>();
        try {

            //pages wont load if total page count is more than 11
            //increasing its value as the current page increases seems to work
            if(response.getInt("last_page")>11) {
                if (TOTAL_PAGES != response.getInt("last_page")) {
                    TOTAL_PAGES++;
                }
            }
            else {TOTAL_PAGES=response.getInt("last_page");}

            if(TOTAL_PAGES==0){
                try {
                    Toast.makeText(getContext(),R.string.no_comments,Toast.LENGTH_SHORT).show();
                }catch (Exception e){
                    Log.e(TAG, "notifyError: ",e );
                }
            }


            JSONArray data=response.getJSONArray("data");
            for(int i=0;i<data.length();i++){

                JSONObject obj=data.getJSONObject(i);
                int id=obj.getInt("id");
                String content=obj.getString("content");
                String user_id=obj.getString("user_id");
                String created_at=obj.getString("created_at");
                JSONObject user=obj.getJSONObject("user");
                String name=user.getString("name");


                Comments comments=new Comments();
                comments.setContent(content);
                comments.setId(id);
                comments.setUser_id(user_id);
                comments.setCreated_at(created_at);
                comments.setUsername(name);

                commentsList.add(comments);
            }



        }catch (JSONException e) {
            e.printStackTrace();
            Log.d(TAG, "parsJsonObj: "+e.getMessage());
        }

        return commentsList;
    }



    @Override
    public void onItemClick(View view, final int position) {
        //delete listener
        if(myComment(position)) {
            DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    switch (which) {
                        case DialogInterface.BUTTON_POSITIVE:
                            //Yes button clicked
                        deletedItemPosition=deleteComment(adapter.getItem(position).getId(),position);
                            break;

                        case DialogInterface.BUTTON_NEGATIVE:
                            //No button clicked
                            dialog.dismiss();
                            break;
                    }
                }
            };

            AlertDialog.Builder builder = new AlertDialog.Builder(getContext(),R.style.AlertDialogCustom);
            builder.setMessage(R.string.delete).setPositiveButton(R.string.yes, dialogClickListener)
                    .setNegativeButton(R.string.no, dialogClickListener).show();
        }

    }

    boolean myComment(int position){
       boolean myComment=false;
       String myId="";
        final boolean loggedOut = AccessToken.getCurrentAccessToken() == null;
        if(!loggedOut){
            myId= Profile.getCurrentProfile().getId();
        }
        boolean actLoggedIn=false;

        SharedPreferences prefs = getActivity().getSharedPreferences("profile", Activity.MODE_PRIVATE);
        if(prefs.getString("user_id",null)!=null){
            myId=prefs.getString("user_id",null);
            actLoggedIn=true;
        }
        if(!loggedOut || actLoggedIn)
        {
            if(myId.equals(adapter.getItem(position).getUser_id()))
                myComment=true;
        }

       return myComment;
    }

    public int deleteComment(int id,int position) {
        Log.d(TAG, "deletePost: ");

        Map<String,String> params=new HashMap<>();
        params.put("id",""+id);
        JSONObject deleteJson=new JSONObject(params);

        initVolleyCallback();
        mVolleyService =new FetchJson(mResultCallback,getContext());
        mVolleyService.postDataVolley("delete",deleteCommentUrl,deleteJson);

        return position;
    }

    @Override
    public void onNameClick(View view, int position) {
        Intent intent =new Intent(getContext(), ProfileActivity.class);
        intent.putExtra("user_id",adapter.getItem(position).getUser_id());
        startActivity(intent);
    }

    @Override
    public void sendRefresh(int data) {
        if(data==1){
            refresh();
        }
    }
}