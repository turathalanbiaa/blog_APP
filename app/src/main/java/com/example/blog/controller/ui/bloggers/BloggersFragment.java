package com.example.blog.controller.ui.bloggers;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Parcelable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.android.volley.VolleyError;
import com.example.blog.R;
import com.example.blog.controller.tools.TextValidator;
import com.example.blog.controller.ui.profile.ProfileActivity;
import com.example.blog.model.Users;
import com.example.blog.URLs;
import com.example.blog.controller.tools.ClickListenerInterface;
import com.example.blog.controller.tools.PaginationListener;
import com.example.blog.controller.tools.volley.FetchJson;
import com.example.blog.controller.tools.volley.IResult;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class BloggersFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener, ClickListenerInterface {
    LinearLayoutManager layoutManager;
    BloggersRecyclerAdapter adapter;
    RelativeLayout relativeLayout;

    RecyclerView recyclerView;
    SwipeRefreshLayout swipeRefresh;
    //
    private final String KEY_RECYCLER_STATE = "recycler_state";
    private static Bundle mBundleRecyclerViewState;



    private static final int PAGE_START = 1;
    private boolean isLoading = false;
    private boolean isLastPage = false;
    private int TOTAL_PAGES = 3;
    private int currentPage = PAGE_START;
    URLs baseUrl=new URLs();
    //    final String postRoute ="posttpagination";
//    final String viewsRoute="updateviews";
    private String TAG = "BloggersFragment";
    IResult mResultCallback = null;
    FetchJson mVolleyService;
    TextView errortxt;

    String firstPageUrl,incViewUrl;
    JSONObject sendJson;

    ProgressBar loading;
    EditText search;
    ImageButton clear;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
//
        View root = inflater.inflate(R.layout.fragment_bloggers, container, false);
//
        firstPageUrl=baseUrl.getUrl(baseUrl.getUsers());
        incViewUrl=baseUrl.getIncViewsUrl();


        Map<String,String> params=new HashMap<>();
        params.put("sortby","1");
//        params.put("cat","1");
        sendJson=new JSONObject(params);

        loading=root.findViewById(R.id.progressBar4);

        swipeRefresh=root.findViewById(R.id.swipeRefresh);
        swipeRefresh.setOnRefreshListener(this);

        recyclerView = root.findViewById(R.id.bloggers_recycler_view);
        relativeLayout = root.findViewById(R.id.bloggersRelativeLayout);
        layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);
        adapter = new BloggersRecyclerAdapter(getContext());

        //click listeners
        adapter.setClickListener(this);

        recyclerView.setAdapter(adapter);
        search=root.findViewById(R.id.search);
        clear =root.findViewById(R.id.clearBtn);
        clear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onRefresh();
                clear.setVisibility(View.INVISIBLE);
                hideKeyboard(getActivity());
                search.clearFocus();
            }
        });

        search.addTextChangedListener(new TextValidator(search) {
            @Override public void validate(TextView textView, String text) {

                if(! text.isEmpty()){
                    clear.setVisibility(View.VISIBLE);
                    searchForUsers(search.getText().toString());
                }
//
            }
        });



        //scroll to top
        Toolbar toolbar=getActivity().findViewById(R.id.toolbar);
        toolbar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                recyclerView.smoothScrollToPosition(0);
            }
        });


        loading.setVisibility(View.VISIBLE);
        loadFirstPage();

        /**
         * add scroll listener while user reach in bottom load more will call
         */
        recyclerView.addOnScrollListener(new PaginationListener(layoutManager) {
            protected void loadMoreItems() {
                isLoading = true;
//
                loadNextPage();

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



        errortxt=root.findViewById(R.id.bloggersErrorTextView);


        return root;
    }
    void hideKeyboard(Activity activity) {
        View view = activity.getCurrentFocus();
        InputMethodManager manager = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
        assert manager != null && view != null;
        manager.hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
    }

    private void searchForUsers(String searchData) {
        String url=baseUrl.getUrl(baseUrl.getSearchForUsers());
        Map<String,String> params=new HashMap<String, String>();
        params.put("data",searchData);
        JSONObject sendObj =new JSONObject(params);
        initVolleyCallback();
        mVolleyService =new FetchJson(mResultCallback,getContext());
        mVolleyService.postDataVolley("search",url,sendObj);
    }


    @Override
    public void onPause()
    {
        super.onPause();
        Log.d(TAG, "save: ");
        // save RecyclerView state
        mBundleRecyclerViewState = new Bundle();
        Parcelable listState = layoutManager.onSaveInstanceState();
        mBundleRecyclerViewState.putParcelable(KEY_RECYCLER_STATE, listState);
    }

    @Override
    public void onResume() {
        super.onResume();

        Log.d(TAG, "restore: ");

        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                if (mBundleRecyclerViewState != null){
                    Parcelable listState = mBundleRecyclerViewState.getParcelable(KEY_RECYCLER_STATE);
                    layoutManager.onRestoreInstanceState(listState);}

            }
        }, 500);
    }

    private void loadFirstPage() {
        Log.d(TAG, "loadFirstPage: ");
        initVolleyCallback();
        mVolleyService =new FetchJson(mResultCallback,getContext());
        mVolleyService.postDataVolley("GETCALL",firstPageUrl,sendJson);
//
    }

    private void loadNextPage() {
        Log.d(TAG, "loadNextPage: " + currentPage);

        String nextPageUrl=baseUrl.getNextPageUrl(baseUrl.getUsers(),currentPage);
        initVolleyCallback();
        mVolleyService =new FetchJson(mResultCallback,getContext());
        mVolleyService.postDataVolley("GETCALL",nextPageUrl,sendJson);
    }


    @Override
    public void onRefresh() {
        // itemCount = 0;
        search.setText("");
        currentPage = PAGE_START;
        isLastPage = false;
        adapter.clear();
        loadFirstPage();
    }

    void initVolleyCallback(){
        mResultCallback = new IResult() {
            @Override
            public void notifySuccess(String requestType,final JSONObject response) {
                Log.d(TAG, "Volley requester " + requestType);
                Log.d(TAG, "Volley JSON post" + response);
                errortxt.setVisibility(View.GONE);
                loading.setVisibility(View.GONE);

                if(requestType.equals("search")){
                    currentPage = PAGE_START;
                    isLastPage = false;
                    adapter.clear();
                    ArrayList<Users> postsList;
                    postsList = parsJsonObj(response,requestType);
                    adapter.addAll(postsList);

                }
                else {



//                Toast.makeText(getContext(),"//"+response,Toast.LENGTH_LONG).show();
                    currentPage += 1;
                    adapter.removeLoadingFooter();
                    isLoading = false;


                    ArrayList<Users> postsList;

                    postsList = parsJsonObj(response,requestType);
                    swipeRefresh.setRefreshing(false);
                    adapter.addAll(postsList);


                    if (currentPage <= TOTAL_PAGES) adapter.addLoadingFooter();
                    else isLastPage = true;
                    //
//                Toast.makeText(getContext(),"//"+currentPage,Toast.LENGTH_LONG).show();
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
                errortxt.setText(R.string.no_connection);
                loading.setVisibility(View.GONE);
//                Toast.makeText(getContext(),""+error,Toast.LENGTH_LONG).show();
                errortxt.setVisibility(View.VISIBLE);
                swipeRefresh.setRefreshing(false);

                adapter.removeLoadingFooter();
                isLoading = false;


            }
        };
    }
    //
    ArrayList<Users> parsJsonObj(JSONObject response, String requestType){

        ArrayList<Users> list=new ArrayList<>();
        try {

//
            //pages wont load if total page count is more than 11
            //increasing its value as the current page increases seems to work
            if(!requestType.equals("search")) {
                if (response.getInt("last_page") > 11) {
                    if (TOTAL_PAGES != response.getInt("last_page")) {
                        TOTAL_PAGES++;
                    }
                } else {
                    TOTAL_PAGES = response.getInt("last_page");
                }

                Log.d(TAG, "parsJsonObj: " + currentPage + "//tt " + TOTAL_PAGES);

            }

            JSONArray data=response.getJSONArray("data");
//           Toast.makeText(getContext(),""+data.length(),Toast.LENGTH_LONG).show();
            for(int i=0;i<data.length();i++){

                JSONObject obj=data.getJSONObject(i);

                String name=obj.getString("name");
                String id= obj.getString("id");
                String profilePic=obj.getString("picture");
                int points=obj.getInt("points");


                Users user=new Users();
                user.setId(id);
                user.setName(name);
                user.setPoints(points);
                if(profilePic == null || profilePic.equals("") ||profilePic.equals("http://aqlam.turathalanbiaa.com/aqlam/image/000000.png")||profilePic.equals("student.png")){
                    profilePic=baseUrl.getDefaultProfilePic();
                }

                user.setPicture(profilePic);

                list.add(user);
            }




//            adapter.notifyDataSetChanged();

        }catch (JSONException e) {
            e.printStackTrace();
//            Toast.makeText(getContext(),
//                    "Error: " + e.getMessage(),
//                    Toast.LENGTH_LONG).show();
        }

        return list;
    }

    @Override
    public void onItemClick(View view, int position) {

        Intent intent =new Intent(getContext(), ProfileActivity.class);
        Log.d(TAG, "onProfileClick: "+adapter.getItem(position).getId());

        intent.putExtra("user_id",adapter.getItem(position).getId());
        startActivity(intent);

    }

    @Override
    public void onPostExpandClick(View view, int position) {

    }

    @Override
    public void onPicClick(View view, int position) {

    }

    @Override
    public void onCommentClick(View view, int position) {


    }

    @Override
    public void onProfileClick(View view, int position) {

    }

    @Override
    public void onCatClick(View view, int position) {

    }

}