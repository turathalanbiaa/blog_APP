package com.example.blog.controller.ui.category;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.VolleyError;
import com.example.blog.R;
import com.example.blog.model.Categories;
import com.example.blog.URLs;
import com.example.blog.controller.tools.volley.FetchJson;
import com.example.blog.controller.tools.volley.IResult;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;


public class CategoriesFragment extends Fragment implements CategoriesRecyclerViewAdapter.ItemClickListener {



    private String TAG = "commentsFragment";
    IResult mResultCallback = null;
    FetchJson mVolleyService;
   CategoriesRecyclerViewAdapter adapter;
    RelativeLayout catRelativeLayout;
    ArrayList<Categories>catList=new ArrayList<>();
    URLs baseUrl=new URLs();
    ProgressBar loading;
    TextView errorText;


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_categories, container,false);
        errorText= view.findViewById(R.id.ErrorTextView);
        loading=view.findViewById(R.id.progressBar5);


        if(catList.isEmpty())
        loadCat();

        RecyclerView recyclerView = view.findViewById(R.id.categories_recycler_view);
        catRelativeLayout = view.findViewById(R.id.catRelativeLayout);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        adapter= new CategoriesRecyclerViewAdapter( getContext(),catList);
        adapter.setClickListener(this);
        recyclerView.setAdapter(adapter);


        return view;
    }

    private void loadCat() {
        loading.setVisibility(View.VISIBLE);
        errorText.setVisibility(View.GONE);
     catList=new ArrayList<>();
        initVolleyCallback();
        String url=baseUrl.getCategoriesUrl();
        mVolleyService =new FetchJson(mResultCallback,getContext());
        mVolleyService.getArrayDataVolley("GETCALL",url);
//
    }

    @Override
    public void onItemClick(View view, int position) {
//        Toast.makeText(getContext(), "pos: " + position+" id: "+ catList.get(position).getId(), Toast.LENGTH_SHORT).show();


        Bundle bundle = new Bundle();
        bundle.putInt("catId",catList.get(position).getId());
        Navigation.findNavController(view).navigate(R.id.action_nav_categories_to_nav_cat_posts,bundle);
//

    }



    void initVolleyCallback(){
        mResultCallback = new IResult() {
            @Override
            public void notifySuccess(String requestType,JSONObject response) {
                Log.d(TAG, "Volley requester " + requestType);
                Log.d(TAG, "Volley JSON post" + response);
//                Toast.makeText(getContext(),"//"+response,Toast.LENGTH_LONG).show();

            }
            @Override
            public void notifySuccessJsonArray(String requestType,JSONArray response) {
                Log.d(TAG, "Volley requester " + requestType);
                Log.d(TAG, "Volley JSON post" + response);

                loading.setVisibility(View.GONE);
                parsJsonArray(response);

//                Toast.makeText(getContext(),"//"+response,Toast.LENGTH_LONG).show();

            }

            @Override
            public void notifyError(String requestType, VolleyError error) {
                Log.d(TAG, "Volley requester " + requestType);
                Log.d(TAG, "Volley JSON post" + error);
                errorText.setText(R.string.no_connection);
                errorText.setVisibility(View.VISIBLE);

                loading.setVisibility(View.GONE);
            }
        };
    }

    void parsJsonArray(JSONArray response){



        try {

            for (int i = 0; i < response.length(); i++) {

                JSONObject obj = (JSONObject) response
                        .get(i);

               int id= obj.getInt("id");
                String name = obj.getString("name");
                name=name.replaceAll("\n","");
//
        Categories cat=new Categories(id,name);
        catList.add(cat);

            }

            adapter.notifyDataSetChanged();

        }catch (JSONException e) {
            e.printStackTrace();
//            Toast.makeText(getContext(),
//                    "Error: " + e.getMessage(),
//                    Toast.LENGTH_LONG).show();
        }

    }

}