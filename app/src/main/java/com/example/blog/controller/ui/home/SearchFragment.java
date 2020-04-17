package com.example.blog.controller.ui.home;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Parcelable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.PopupMenu;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.example.blog.MainActivity;
import com.example.blog.R;
import com.example.blog.URLs;
import com.example.blog.controller.tools.ClickListenerInterface;
import com.example.blog.controller.tools.PaginationListener;
import com.example.blog.controller.tools.PopUpClass;
import com.example.blog.controller.tools.TextValidator;
import com.example.blog.controller.tools.volley.AppController;
import com.example.blog.controller.tools.volley.FetchJson;
import com.example.blog.controller.tools.volley.IResult;
import com.example.blog.controller.ui.comments.CommentsDialogFragment;
import com.example.blog.controller.ui.profile.ProfileActivity;
import com.example.blog.model.Posts;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


public class SearchFragment extends Fragment implements   ClickListenerInterface {

    LinearLayoutManager layoutManager;
    PostRecyclerAdapter adapter;
    RelativeLayout postsRelativeLayout;

    RecyclerView recyclerView;
    SwipeRefreshLayout swipeRefresh;
    EditText search;
    ImageButton clear;
//
    private final String KEY_RECYCLER_STATE = "recycler_state";
    private static Bundle mBundleRecyclerViewState;



    private static final int PAGE_START = 1;
    private boolean isLoading = false;
    private boolean isLastPage = false;
    private int TOTAL_PAGES = 3;
    private int currentPage = PAGE_START;
    URLs baseUrl=new URLs();
    private String TAG = "searchFragment";
    IResult mResultCallback = null;
    FetchJson mVolleyService;
    TextView errortxt;

    String incViewUrl;
//    JSONObject sendJson;


    ProgressBar loading;


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
//
        View root = inflater.inflate(R.layout.fragment_search, container, false);
//


       incViewUrl=baseUrl.getIncViewsUrl();


        FloatingActionButton fab = ((MainActivity) getActivity()).getFloatingActionButton();
        fab.show();

        loading=root.findViewById(R.id.progressBar4);

//        swipeRefresh=root.findViewById(R.id.swipeRefresh);
//        swipeRefresh.setOnRefreshListener(this);

        recyclerView = root.findViewById(R.id.posts_recycler_view);
        postsRelativeLayout = root.findViewById(R.id.postsRelativeLayout);
        layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);
        adapter = new PostRecyclerAdapter(getContext());

        //click listeners
        adapter.setClickListener(this);
        adapter.setCommentClickListener(this);
        adapter.setPicClickListener(this);
        adapter.setPostExpandClickListener(this);
        adapter.setProfileClickListener(this);
        adapter.setCatClickListener(this);

        recyclerView.setAdapter(adapter);




        //scroll to top
        Toolbar toolbar=getActivity().findViewById(R.id.toolbar);
        toolbar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                recyclerView.smoothScrollToPosition(0);
            }
        });

        //get posts from api

//        Parcelable state = layoutManager.onSaveInstanceState();
//        layoutManager.onRestoreInstanceState(state);

      //on search
//        loading.setVisibility(View.VISIBLE);
//        loadFirstPage();

        search=root.findViewById(R.id.search);
        clear =root.findViewById(R.id.clearBtn);
        clear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                clear.setVisibility(View.INVISIBLE);
                hideKeyboard(getActivity());
                search.clearFocus();
                search.setText("");
                currentPage = PAGE_START;
                isLastPage = false;
                adapter.clear();
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

        /**
         * add scroll listener while user reach in bottom load more will call
         */
        recyclerView.addOnScrollListener(new PaginationListener(layoutManager) {
            protected void loadMoreItems() {
                 isLoading = true;

                        loadNextPage(search.getText().toString());

//
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



//    @Override
//    public void onPause()
//    {
//        super.onPause();
//        Log.d(TAG, "save: ");
//        // save RecyclerView state
//        mBundleRecyclerViewState = new Bundle();
//        Parcelable listState = layoutManager.onSaveInstanceState();
//        mBundleRecyclerViewState.putParcelable(KEY_RECYCLER_STATE, listState);
//    }
//
//    @Override
//    public void onResume() {
//        super.onResume();
//
//        Log.d(TAG, "restore: ");
//
//        new Handler().postDelayed(new Runnable() {
//
//            @Override
//            public void run() {
//                if (mBundleRecyclerViewState != null){
//                Parcelable listState = mBundleRecyclerViewState.getParcelable(KEY_RECYCLER_STATE);
//               layoutManager.onRestoreInstanceState(listState);}
//
//            }
//        }, 1000);
//    }
void hideKeyboard(Activity activity) {
    View view = activity.getCurrentFocus();
    InputMethodManager manager = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
    assert manager != null && view != null;
    manager.hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
}

    private void searchForUsers(String searchData) {
        String url=baseUrl.getUrl(baseUrl.getSearchPost());
        Map<String,String> params=new HashMap<String, String>();
        params.put("data",searchData);
        JSONObject sendObj =new JSONObject(params);
        initVolleyCallback();
        mVolleyService =new FetchJson(mResultCallback,getContext());
        mVolleyService.postDataVolley("search",url,sendObj);
    }




    private void loadNextPage(String searchData) {
        Log.d(TAG, "loadNextPage: " + currentPage);

        String nextPageUrl=baseUrl.getNextPageUrl(baseUrl.getSearchPost(),currentPage);
        Map<String,String> params=new HashMap<String, String>();
        params.put("data",searchData);
        JSONObject sendObj =new JSONObject(params);
        initVolleyCallback();
        mVolleyService =new FetchJson(mResultCallback,getContext());
        mVolleyService.postDataVolley("next",nextPageUrl,sendObj);
    }

//
//    @Override
//    public void onRefresh() {
//
//        currentPage = PAGE_START;
//        isLastPage = false;
//        adapter.clear();
//
//    }




    void initVolleyCallback(){
        mResultCallback = new IResult() {
            @Override
            public void notifySuccess(String requestType,final JSONObject response) {
                Log.d(TAG, "Volley requester " + requestType);
                Log.d(TAG, "Volley JSON post" + response);
                errortxt.setVisibility(View.GONE);
                loading.setVisibility(View.GONE);
//                Toast.makeText(getContext(),"//"+response,Toast.LENGTH_LONG).show();
                currentPage += 1;
                adapter.removeLoadingFooter();
                isLoading = false;


                ArrayList<Posts> postsList;

                postsList= parsJsonObj(response);
//                swipeRefresh.setRefreshing(false);
                adapter.addAll(postsList);



                if (currentPage <= TOTAL_PAGES) adapter.addLoadingFooter();
                else isLastPage = true;
                //
//                Toast.makeText(getContext(),"//"+currentPage,Toast.LENGTH_LONG).show();


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
                errortxt.setText(R.string.no_connection);
                loading.setVisibility(View.GONE);
//                Toast.makeText(getContext(),""+error,Toast.LENGTH_LONG).show();
                errortxt.setVisibility(View.VISIBLE);
//                swipeRefresh.setRefreshing(false);

                adapter.removeLoadingFooter();
                isLoading = false;


            }
        };
    }
    //
    ArrayList<Posts> parsJsonObj(JSONObject response){

        ArrayList<Posts> postsList=new ArrayList<>();
        try {
//            JSONObject response=res.getJSONObject("data");

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
                    Toast.makeText(getContext(),R.string.no_posts,Toast.LENGTH_SHORT).show();
                }catch (Exception e){
                    Log.e(TAG, "notifyError: ",e );
                }
            }

           JSONArray data=response.getJSONArray("data");
//           Toast.makeText(getContext(),""+data.length(),Toast.LENGTH_LONG).show();
           for(int i=0;i<data.length();i++){

               JSONObject obj=data.getJSONObject(i);
               int id=obj.getInt("id");
               String title=obj.getString("title");

               String content=obj.getString("content");

//               String content = new String(obj.getString("content").getBytes(StandardCharsets.ISO_8859_1), Charset.forName("UTF-8"));
//               new String(obj.getString("content").getBytes("ISO-8859-1"), "UTF-8");

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

               Posts post=new Posts();
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

                postsList.add(post);
            }




//            adapter.notifyDataSetChanged();

        }catch (JSONException e) {
            e.printStackTrace();
            Log.d(TAG, "parsJsonObj: "+e.getMessage());
        }

        return postsList;
    }

    @Override
    public void onItemClick(View view, int position) {

//        //incViews if post hass been approved
//        if(adapter.getItem(position).getStatus() == 1)
//        updateViews(incViewUrl,adapter.getItem(position).getId());

        if( ((MainActivity)getActivity()).actLoggedIn  ||  !((MainActivity)getActivity()).loggedOut)
        {
            SharedPreferences prefs = getActivity().getSharedPreferences("profile", Activity.MODE_PRIVATE);
            if(prefs.getString("user_id","").equals(adapter.getItem(position).getUser_id()) || ((MainActivity)getActivity()).fbId.equals(adapter.getItem(position).getUser_id())){

            }
            else
                updateViews(incViewUrl,adapter.getItem(position).getId());

        }
        else
            updateViews(incViewUrl,adapter.getItem(position).getId());


        Bundle bundle = new Bundle();

//        bundle.putInt("post",adapter.getItem(position).getId());
        bundle.putParcelable("post",adapter.getItem(position));

        Navigation.findNavController(view).navigate(R.id.action_nav_home_to_nav_post,bundle);
//
    }

    @Override
    public void onPostExpandClick(View view, int position) {
        TextView postDetails=view.findViewById(R.id.postDetails);
        TextView seeMore = view.findViewById(R.id.seeMore);
//        Log.d("line count", ""+postDetails.getLineCount());
                    if (postDetails.getMaxLines() == 3) {
                        postDetails.setMaxLines(1000);
                        seeMore.setVisibility(View.GONE);

                        if( ((MainActivity)getActivity()).actLoggedIn  ||  !((MainActivity)getActivity()).loggedOut)
                        {
                            SharedPreferences prefs = getActivity().getSharedPreferences("profile", Activity.MODE_PRIVATE);
                            if(prefs.getString("user_id","").equals(adapter.getItem(position).getUser_id()) || ((MainActivity)getActivity()).fbId.equals(adapter.getItem(position).getUser_id())){

                            }
                            else
                                updateViews(incViewUrl,adapter.getItem(position).getId());

                        }
                        else
                            updateViews(incViewUrl,adapter.getItem(position).getId());


                    } else {
                        postDetails.setMaxLines(3);
                        seeMore.setVisibility(View.VISIBLE);

                    }
    }

    private void updateViews(String url, int postId) {

        Map<String, String> params = new HashMap<>();
        params.put("id", String.valueOf(postId));



        JsonObjectRequest req = new JsonObjectRequest(Request.Method.POST,
               url, new JSONObject(params), new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {
                Log.d(TAG, response.toString());

                try {


//                    Toast.makeText(getApplicationContext(),
//                            " "+  response.getString("message"), Toast.LENGTH_LONG).show();
//
////

                } catch (Exception e) {
                    e.printStackTrace();
                    Log.d(TAG, "onErrorResponse: "+e.getMessage());

                }


            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d(TAG, "Error: " + error.getMessage());


            }

        });

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(req);

    }


    @Override
    public void onPicClick(View view, int position) {
        PopUpClass popUpClass = new PopUpClass();
        popUpClass.showPopupWindow(view,adapter.getItem(position).getImage());

    }

    @Override
    public void onCommentClick(View view, int position) {

        final int postid= adapter.getItem(position).getId();

        Bundle bundle = new Bundle();
        bundle.putInt("postId",postid);


        CommentsDialogFragment cf=new CommentsDialogFragment();
        cf.setArguments(bundle);
        FragmentTransaction ft =  getChildFragmentManager().beginTransaction();
        Fragment prev =  getChildFragmentManager().findFragmentByTag("dialog");
        if (prev != null) {
            ft.remove(prev);
        }
        ft.addToBackStack(null);


        cf.show(ft, "dialog");
    }

    @Override
    public void onProfileClick(View view, int position) {
        Intent intent =new Intent(getContext(), ProfileActivity.class);
        Log.d(TAG, "onProfileClick: "+adapter.getItem(position).getUser_id());


        intent.putExtra("user_id",adapter.getItem(position).getUser_id());
        startActivity(intent);
    }

    @Override
    public void onCatClick(View view, int position) {
        Bundle bundle = new Bundle();
        bundle.putInt("catId",adapter.getItem(position).getCategory_id());
        Navigation.findNavController(view).navigate(R.id.action_nav_home_to_nav_cat_posts,bundle);
//
    }


}